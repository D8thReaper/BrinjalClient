package exun.cli.in.brinjal.activity.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.adapter.AppController;
import exun.cli.in.brinjal.adapter.RVStoreAdapter;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SessionManager;
import exun.cli.in.brinjal.model.StoreList;

/**
 * Created by n00b on 3/6/2016.
 */
public class Store extends Fragment implements SearchView.OnQueryTextListener{

    // Log tag
    private static final String TAG ="Store";

    // blogs json url
    private static String url;
    private ProgressDialog pDialog;
    private List<StoreList> storeList = new ArrayList<StoreList>();
    private List<StoreList> searchStoreList = new ArrayList<StoreList>();
    //New
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Search from the current list

    Bundle bundle;
    private SessionManager session ;

    public Store(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store, container, false);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        bundle = getArguments();
        url = bundle.getString("url");

        // Session manager
        session = new SessionManager(getActivity().getApplicationContext());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.store_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVStoreAdapter(getActivity(),storeList,session.getLat(),session.getLongi());
        mRecyclerView.setAdapter(mAdapter);

        // Creating volley request obj
        StringRequest storeReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Store: " + response);
                        hidePDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now store the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "Store " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0){

                                    for (int k = 0; k< data.length();k++){

                                        JSONObject obj = data.getJSONObject(k);
                                        StoreList store = new StoreList();
                                        store.setTitle(obj.getString("title"));
                                        store.setId(obj.getInt("id"));
                                        store.setLocality(obj.getString("longitude"));
                                        store.setLongi(obj.getDouble("longitude"));
                                        store.setLati(obj.getDouble("latitude"));
                                        store.setIsDeals(obj.getInt("deals"));

                                        // adding blog to blogs array
                                        storeList.add(store);
                                    }
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getActivity(),
                                        "No data!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG,errorMsg);
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Connection failed! :(", Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"Json error: " + e.getMessage());
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        "Connection failed! :(", Toast.LENGTH_SHORT).show();
                hidePDialog();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(storeReq);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RVStoreAdapter) mAdapter).setOnItemClickListener(new RVStoreAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                StoreList sl = storeList.get(position);
                int sID,isDeals;
                String sTitle,storeURL = AppConstants.URL_STORES;

                sID = sl.getId();
                storeURL = storeURL + sID;
                sTitle = sl.getTitle();
                isDeals = sl.getIsDeals();

                Toast.makeText(getActivity(),"Holaa",Toast.LENGTH_SHORT).show();

//                Intent i = new Intent(getActivity(),StoreDetail.class);
//                i.putExtra("url",storeURL);
//                i.putExtra("title",sTitle);
//                i.putExtra("id",sID);
//                i.putExtra("isCoupons",isDeals);
//                startActivity(i);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_store_list,menu);
        final MenuItem item = menu.findItem(R.id.action_search_store);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search " + ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle() + "...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setOnQueryTextListener(Store.this);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
        hideDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private List<StoreList> filter(List<StoreList> models, String query) {
        query = query.toLowerCase();

        final List<StoreList> filteredModelList = new ArrayList<>();
        for (StoreList model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG,newText);
        searchStoreList = storeList;
        final List<StoreList> filteredModelList = filter(searchStoreList, newText);
        ((RVStoreAdapter) mAdapter).animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }
}