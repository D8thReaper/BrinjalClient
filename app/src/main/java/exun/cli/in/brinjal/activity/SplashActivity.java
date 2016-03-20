package exun.cli.in.brinjal.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.adapter.AppController;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SQLiteHandler;
import exun.cli.in.brinjal.helper.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static String TAG = "Splash";

    private ProgressDialog pDialog;
    Context context = this;
    private SessionManager session;
    private SQLiteHandler db;

    // Checking network state
    int networkState = 0;

    // Test
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);

//        Intent i = new Intent(SplashActivity.this,MainActivity.class);
//        startActivity(i);
//        finish();
        new CheckInternet().execute();
    }

    public static int hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0)
                    return 1;
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
                return 2;
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return 0;
    }

    public static boolean isNetworkAvailable(Context a) {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;
        try {

            ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("wifi"))
                    if (ni.isConnected())
                        hasConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("mobile"))
                    if (ni.isConnected())
                        hasConnectedMobile = true;
            }
            return hasConnectedWifi || hasConnectedMobile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private class CheckInternet extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            networkState = hasInternetAccess(context);

            return 1;
        }

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Loading ...");
            showDlg();

        }

        protected void onPostExecute(Integer result) {

            if (networkState == 1) {

                Log.d(TAG, session.isCategories() + "");

                if (!session.isCategories()) {
                    getCategories(AppConstants.URL_CATEGORIES);
                    getSubCategories(AppConstants.URL_SUB_CATEGORIES);
                } else {

                    Intent intent = new Intent(context, GrabLocation.class);
                    startActivity(intent);
                    finish();
                }


            } else if (networkState == 2) {
                showAlert();
            } else {
                showAlert();
            }
        }
    }

    private void showAlert() {
        new AlertDialog.Builder(context)
                .setTitle("No connection!")
                .setMessage("Unable to check internet connection!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        finish();
                    }
                })
                .show();
    }

    private void getSubCategories(String url) {

        // Creating volley request obj
        StringRequest subCatRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Sub Categories: " + response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {



                                // Now store the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "Splash " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0) {

                                    hideDialog();

                                    for (int k = 0; k < data.length(); k++) {

                                        JSONObject obj = data.getJSONObject(k);

                                        int id = obj.getInt("id");
                                        int pid = obj.getInt("category_id");
                                        String name = obj.getString("name");
                                        db.addSubCategory(id, name, pid);
                                    }

                                    updateUI();
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                hideDialog();
                                db.delete(AppConstants.TABLE_SUB_CATEGORIES);
                                showAlert();
                                Log.d(TAG, "Json error: " + errorMsg);
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            hideDialog();
                            db.delete(AppConstants.TABLE_SUB_CATEGORIES);
                            showAlert();
                            Log.d("Splash", "Json error: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                hideDialog();
                db.delete(AppConstants.TABLE_SUB_CATEGORIES);
                showAlert();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(subCatRequest);
    }

    private void updateUI() {
        session.setCategories(true);
        Intent intent = new Intent(context, GrabLocation.class);
        startActivity(intent);
        finish();
    }

    private void getCategories(String url) {

        // Creating volley request obj
        StringRequest catRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Categories: " + response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now store the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "Splash " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0) {

                                    for (int k = 0; k < data.length(); k++) {

                                        JSONObject obj = data.getJSONObject(k);

                                        int id = obj.getInt("id");
                                        String name = obj.getString("name");
                                        db.addCategories(id, name);
                                    }
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                hideDialog();
                                db.delete(AppConstants.TABLE_CATEGORIES);
                                showAlert();
                                Log.d(TAG, "Json error: " + errorMsg);
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            hideDialog();
                            db.delete(AppConstants.TABLE_CATEGORIES);
                            showAlert();
                            Log.d("Splash", "Json error: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                hideDialog();
                db.delete(AppConstants.TABLE_CATEGORIES);
                showAlert();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(catRequest);
    }

    private void showDlg() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
