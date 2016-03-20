package exun.cli.in.brinjal.activity.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.MainActivity;
import exun.cli.in.brinjal.activity.StoreDetail;
import exun.cli.in.brinjal.adapter.AppController;
import exun.cli.in.brinjal.contentProvider.Categories;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SQLiteHandler;

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView textView;
    EditText searchBar;
    SimpleCursorAdapter mAdapter;
    ListView lvSearch;
    private SQLiteHandler db;
    private String TAG = "SearchFragment";
    private ProgressDialog pDialog;
    private ImageView btnRetry, back, searchNow;
    String code;
    CardView cvSearch;
    private int isCode;
    private Cursor childCursor, parentCusor, pcCursor, cursors[];
    private boolean hasData;
    private MergeCursor mergeCursorChild,mergeCursorParent;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search, container, false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        initialize(rootView);

        setClickListeners();

        /** Creating a loader for populating listview from sqlite database */
        /** This statement, invokes the method onCreatedLoader() */
        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
//                            textView.setVisibility(View.GONE);
//                        }
//                    });
                    mAdapter.getFilter().filter(s.toString());
                } else {
                    isCode = 0;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            textView.setVisibility(View.GONE);
                            cvSearch.setVisibility(View.VISIBLE);
                        }
                    });
                    getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
                }
            }
        });

        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(final CharSequence constraint) {
                String where;

                if (constraint.toString().startsWith("#")) {
                    isCode = 1;
                    Log.d(TAG, "#Done");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            cvSearch.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Enter code: #BXXXX");
                        }
                    });
                } else {
                    int parentID = -1;
                    hasData = false;
                    isCode = 2;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            cvSearch.setVisibility(View.VISIBLE);
                        }
                    });
                    where = "name LIKE '" + constraint.toString() + "%' or name LIKE '% " + constraint.toString() + "%'";

                    childCursor = db.fetchChildren(where);
                    if (childCursor != null && childCursor.getCount() > 0) {
                        hasData = true;
                        parentID = childCursor.getInt(childCursor.getColumnIndexOrThrow("parent_id"));
                        mergeCursorParent = new MergeCursor(new Cursor[]{childCursor});
                        Log.d(TAG,"Child mil gaya!");
                    }

                    parentCusor = db.fetchGroupByName(where);
                    if (parentCusor != null && parentCusor.getCount() > 0) {
                        int id = parentCusor.getInt(parentCusor.getColumnIndex("_id"));
                        Log.d(TAG,parentID + " " +id);
                        if (parentID== -1 || parentID!=id) {
                            hasData = true;
                            final String name = parentCusor.getString(parentCusor.getColumnIndex("name"));

                            String[] columnNames = {"_id", "name"};
                            MatrixCursor matrixCursor = new MatrixCursor(columnNames);
                            matrixCursor.addRow(new String[]{"-1", "in Category " + name + ": "});


                            where = "parent_id = '" + id + "'";
                            pcCursor = db.fetchChildren(where);

                            mergeCursorParent = new MergeCursor(new Cursor[]{mergeCursorChild, matrixCursor, pcCursor});
                            Log.d(TAG,"Bapu mil gaya!");
                        }
                        Log.d(TAG,"Child and bapu mil gaya!");

                    }

                    if (!hasData) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("No local data!");
                                cvSearch.setVisibility(View.GONE);
                                Log.d(TAG,"No data mila!");
                            }
                        });
                        return null;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                textView.setVisibility(View.GONE);
                            }
                        });
                        Log.d(TAG,"Sab Mila!");
                        return mergeCursorParent;
                    }


//                    if (childCursor != null && childCursor.getCount() > 0) {
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
//
//                            }
//                        });
//                        return childCursor;
//
//
//                    } else {
//
//                        where = "name LIKE '" + constraint.toString() + "%' or name LIKE '% " + constraint.toString() + "%'";
//                        Cursor parentCursor = db.fetchGroupByName(where);
//
//                        if (parentCursor != null && parentCursor.getCount() > 0) {
//
//                            final String name = "in " + parentCursor.getString(parentCursor.getColumnIndex("name"));
//
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
//
//                                }
//                            });
//
//                            int id = parentCursor.getInt(parentCursor.getColumnIndex("_id"));
//                            where = "parent_id = '" + id + "'";
//                            parentCursor.close();
//                            return db.fetchChildren(where);
//
//                        } else {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
//
//                                }
//                            });
//                            return parentCursor;
//                        }
//                    }
                }
                return null;
            }
        });

        return rootView;
    }

    private void setClickListeners() {
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetry.setVisibility(View.GONE);
                launchRequest(code);
            }
        });

        mAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.list_row_search,
                null,
                new String[]{"name", "_id"},
                new int[]{R.id.tvSearchItem}, 0);

        lvSearch.setAdapter(mAdapter);

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) lvSearch.getItemAtPosition(position);
                int sId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String sName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                ((MainActivity) getActivity()).setSubCatDetails(sId, sName);
                ((MainActivity) getActivity()).displayView(1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).displayView(0);
            }
        });

        searchNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCode != 0) {
                    if (isCode == 1)
                        launchRequest(searchBar.getText().toString());
                    else
                        doOnlineQuery(searchBar.getText().toString());
                }
            }
        });
    }

    private void doOnlineQuery(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void initialize(View rootView) {
        textView = (TextView) rootView.findViewById(R.id.textView7);
        btnRetry = (ImageView) rootView.findViewById(R.id.btnRetry);
        searchBar = (EditText) rootView.findViewById(R.id.toolbar);
        cvSearch = (CardView) rootView.findViewById(R.id.cvSearch);
        back = (ImageView) rootView.findViewById(R.id.searchBack);
        searchNow = (ImageView) rootView.findViewById(R.id.searchImageButton);
        searchBar.requestFocus();

        lvSearch = (ListView) rootView.findViewById(R.id.lvSearch);
    }

    private void launchRequest(final String code) {

        String tag_string_req = "req_register";

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Checking ...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConstants.URL_CODE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Code Response: " + response.toString());
                pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        int Sid = jObj.getInt("placeid");

                        if (Sid == 0) {
                            showError("Code not found!", 0);
                        } else {
                            // Launch camera activity
                            Intent intent = new Intent(getActivity(), StoreDetail.class);
                            intent.putExtra("id", Sid);
                            String storeURL = AppConstants.URL_STORES + Sid;
                            intent.putExtra("url", storeURL);
                            intent.putExtra("title", "Store");
                            intent.putExtra("isCoupons", 1);
                            startActivity(intent);
                        }

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        showError("Connection failed!", 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", code);

                return params;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showError(String s, int retry) {
        textView.setText(s);
        if (retry == 1)
            btnRetry.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            ((MainActivity) getActivity()).displayView(0);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Categories.CONTENT_URI_SUB_CATEGORIES_ALL;
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }
}
