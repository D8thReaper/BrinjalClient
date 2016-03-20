package exun.cli.in.brinjal.activity.fragment.storeDetailsFragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.*;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.StoreDetail;
import exun.cli.in.brinjal.adapter.AppController;
import exun.cli.in.brinjal.helper.SQLiteHandler;
import exun.cli.in.brinjal.model.StoreList;

public class DescFragment extends Fragment {


    TextView location, description,bookmark;
    ImageView call,mail,addBookmark,review,directions;
    private ProgressDialog pDialog;
    String sLocation = StoreDetail.sLocation;
    String url = StoreDetail.url;
    private int storeID = StoreDetail.Sid;
    private String TAG = "Description";
    StoreList store = new StoreList();
    String subtitle = "Store";
    Button submitFeed,submitReview;
    EditText textFeedBack,textReview;
    LinearLayout llReview;

    //Bluemix Mobile backend which will send push notifications
    private String BluemixMobileBackendApplication_ROUTE = "http://brinjal-watson.mybluemix.net";
    private String BluemixMobileBackendApplication_App_GUID= "914f7b3d-3de3-45d0-a70a-45f6b31d4c0f";

    //Application which will receive the feedback submitted from this Android application.
    //If running in a hybrid environment where feedback is stored on-premise, the Secure Gateway API would go here.
    //For this demo application, we are going to talk directly to the feedback manager application.
    private String FeedbackApplicationRoute = "http://watson-brinjal.au-syd.mybluemix.net";

    //User Details
    private String YourName;
    private int id;
    private SQLiteHandler db;

    public DescFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_desc, container, false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        YourName = db.getUserDetails().get("name");
        id = Integer.parseInt(db.getUserDetails().get("_id"));


        try {
            //Initialize the Core SDK
            BMSClient.getInstance().initialize(getActivity().getApplicationContext(), BluemixMobileBackendApplication_ROUTE, BluemixMobileBackendApplication_App_GUID);
        } catch (MalformedURLException e) {
            System.out.println("ERROR : Initialize the Core SDK");
            e.printStackTrace();
        }

        initialize(rootView);

        sLocation = "Address of store\nLine 2\nLine 3";
        location.setText(sLocation);

        getDescription();

        setClickListners();

        return rootView;
    }

    private void initialize(View rootView) {
        location = (TextView) rootView.findViewById(R.id.store_detail_location);
        description = (TextView) rootView.findViewById(R.id.store_detail_desc);
        bookmark = (TextView) rootView.findViewById(R.id.store_detail_bookmarks);
        call = (ImageView) rootView.findViewById(R.id.store_detail_call);
        mail = (ImageView) rootView.findViewById(R.id.store_detail_email);
        addBookmark = (ImageView) rootView.findViewById(R.id.store_detail_add_bookmark);
        review = (ImageView) rootView.findViewById(R.id.store_detail_review);
        directions = (ImageView) rootView.findViewById(R.id.store_detail_btn_direct);
        submitFeed = (Button) rootView.findViewById(R.id.submitFeed);
        textFeedBack = (EditText) rootView.findViewById(R.id.editTextFeed);
        textReview = (EditText) rootView.findViewById(R.id.textReview);
        submitReview = (Button) rootView.findViewById(R.id.btnSubmitReview);
        llReview = (LinearLayout) rootView.findViewById(R.id.layoutReview);
    }

    private void setClickListners() {

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ store.getContact()));
                startActivity(intent);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",store.getEmail(), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        addBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Bleh!", Toast.LENGTH_SHORT).show();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Yo!", Toast.LENGTH_SHORT).show();
                llReview.setVisibility(View.VISIBLE);
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(store.getLati(), store.getLongi());
            }
        });

        textFeedBack.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    submitFeed.callOnClick();
                    handled = true;
                }
                return handled;
            }
        });

        submitFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textFeedBack.toString().length() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Thank you!")
                            .setMessage("We received your feedback, and will get back to you shortly.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        new CallAPITask().execute(textFeedBack.getText().toString());
                                    } catch (Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }

            }
        });

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textReview.toString().length()>0){
                    ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_02_11);
                    service.setUsernameAndPassword("4f9c178b-fe0d-43da-ad27-f4cc07dfe7cc", "x0KXRWomPndZ");

                    String text =
                            "A word is dead when it is said, some say. Emily Dickinson";

                    // Call the service and get the tone
                    ToneAnalysis tone = service.getTone(text);
                    Log.d(TAG,tone + "");
                }
            }
        });
    }

    public void showMap(double PLACE_LATITUDE, double PLACE_LONGITUDE) {

        boolean installedMaps = false;

        // CHECK IF GOOGLE MAPS IS INSTALLED
        PackageManager pkManager = getActivity().getPackageManager();
        try {
            @SuppressWarnings("unused")
            PackageInfo pkInfo = pkManager.getPackageInfo("com.google.android.apps.maps", 0);
            installedMaps = true;
        } catch (Exception e) {
            e.printStackTrace();
            installedMaps = false;
        }

        // SHOW THE MAP USING CO-ORDINATES FROM THE CHECKIN
        if (installedMaps == true) {
            String geoCode = "geo:0,0?q=" + PLACE_LATITUDE + ","
                    + PLACE_LONGITUDE;
            Intent sendLocationToMap = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(geoCode));
            startActivity(sendLocationToMap);
        } else if (installedMaps == false) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());

            // SET THE ICON
            alertDialogBuilder.setIcon(R.drawable.ic_map);

            // SET THE TITLE
            alertDialogBuilder.setTitle("Google Maps Not Found");

            // SET THE MESSAGE
            alertDialogBuilder
                    .setMessage("Maps application couldn't be found. This feature requires Google Maps!")
                    .setCancelable(false)
                    .setNeutralButton("Got It",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                }
                            });

            // CREATE THE ALERT DIALOG
            AlertDialog alertDialog = alertDialogBuilder.create();

            // SHOW THE ALERT DIALOG
            alertDialog.show();
        }
    }

    private void getDescription() {
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        StringRequest storeReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "stores: " + response);
                        hidePDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now store the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "stores " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0){

                                    for (int k = 0; k< data.length();k++){

                                        JSONObject obj = data.getJSONObject(k);
                                        store.setContact(obj.getInt("phone"));
                                        store.setEmail(obj.getString("email"));
                                        store.setLongi(obj.getDouble("longitude"));
                                        store.setLati(obj.getDouble("latitude"));
                                        description.setText(obj.getString("description"));
                                        String imageUrl = (obj.getString("thumb"));
                                        store.setImage(imageUrl);
                                        store.setRating(obj.getInt("rating"));
                                        String review = obj.getInt("rating") + " people bookmarked this place!";
                                        bookmark.setText(review);
                                        subtitle = obj.getString("subtitle");
                                        ((StoreDetail)getActivity()).getSupportActionBar().setSubtitle(subtitle);
                                    }
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "No data! :(", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Connection failed! :(", Toast.LENGTH_LONG).show();
                            Log.d("Events","Json error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        "Connection failed! :(", Toast.LENGTH_LONG).show();
                hidePDialog();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(storeReq);
    }

    // Uses AsyncTask to create a task away from the main UI thread.
    private class CallAPITask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                callAPI(params[0]);
                return "Done!";
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPost " + result);


        }

        public void callAPI(String feedback) throws Exception {
            String feedbackAPIURL = Uri.parse(FeedbackApplicationRoute).buildUpon().appendPath("submitFeedback").toString();
            URL url = new URL(feedbackAPIURL);
            JSONObject payloadjson = new JSONObject();
            payloadjson.put("user", YourName);
            payloadjson.put("feedback", feedback);
            payloadjson.put("id",id);
            payloadjson.put("storeId",storeID);
            Log.d(TAG, "feedback " + feedback);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(payloadjson.toString().getBytes());
            os.flush();

            StringBuilder sb = new StringBuilder();
            int HttpResult = conn.getResponseCode();
            Log.d(TAG, String.valueOf(HttpResult));
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    Log.d(TAG, line + "\n");
                }

                br.close();

                Log.d(TAG, "" + sb.toString());

            } else {
                Log.d(TAG, conn.getResponseMessage());
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
