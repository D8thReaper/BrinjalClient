package exun.cli.in.brinjal.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import exun.cli.in.brinjal.adapter.FragmentDrawerFilter;
import exun.cli.in.brinjal.adapter.RVStoreAdapter;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SessionManager;
import exun.cli.in.brinjal.model.StoreList;

/**
 * Created by n00b on 3/6/2016.
 */
public class Store extends AppCompatActivity implements SearchView.OnQueryTextListener,FragmentDrawerFilter.FragmentFilterDrawerListener{

    // Log tag
    private static final String TAG ="Store";

    // blogs json url
    private static String url;
    private ProgressDialog pDialog;
    private List<StoreList> storeList = new ArrayList<StoreList>();
    private List<StoreList> searchStoreList = new ArrayList<StoreList>();
    String title;
    //New
    private RecyclerView mRecyclerView;
    private RVStoreAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Search from the current list

    Bundle bundle;
    private SessionManager session ;
    private Toolbar toolbar;
    private FragmentDrawerFilter drawerFragment;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store);

        // Show dialog
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Get the URL from the bundle
        bundle = getIntent().getExtras();
        if (bundle!=null){
            url = bundle.getString("url");
            title = bundle.getString("title");
        }

        setToolbar();

        drawerFragment = (FragmentDrawerFilter)
                getSupportFragmentManager().findFragmentById(R.id.fragment_filter_drawer);
        drawerFragment.setUp(R.id.fragment_filter_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerFragment.setDrawerListener(Store.this);

        // Session manager
        session = new SessionManager(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.store_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVStoreAdapter(this,storeList,session.getLat(),session.getLongi());
        mRecyclerView.setAdapter(mAdapter);

        fetchStoreData();

    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchStoreData() {

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
                                        store.setTags(obj.getString("tags"));
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
                                Toast.makeText(Store.this,
                                        "No data!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG,errorMsg);
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(Store.this,
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
                Toast.makeText(Store.this,
                        "Connection failed! :(", Toast.LENGTH_SHORT).show();
                hidePDialog();
            }

        });

        searchStoreList = storeList;

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(storeReq);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RVStoreAdapter) mAdapter).setOnItemClickListener(new RVStoreAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                StoreList sl = storeList.get(position);
                int sID, isDeals;
                String sTitle, storeURL = AppConstants.URL_STORES;

                sID = sl.getId();
                storeURL = storeURL + sID;
                sTitle = sl.getTitle();
                isDeals = sl.getIsDeals();

                Intent i = new Intent(Store.this, StoreDetail.class);
                i.putExtra("url", storeURL);
                i.putExtra("title", sTitle);
                i.putExtra("id", sID);
                i.putExtra("isCoupons", isDeals);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_list, menu);

        final MenuItem item = menu.findItem(R.id.action_search_store);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search " + getSupportActionBar().getTitle() + "...");
        searchView.setOnQueryTextListener(Store.this);


        return true;
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
            Log.d("SEarch by query",text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private List<StoreList> filterByDis(List<StoreList> models, int dis) {

        final List<StoreList> filteredModelList = new ArrayList<>();
        for (StoreList model : models) {
            final int disInM = (int) model.getDisInM();
            Log.d("SEarch by dis",disInM + "");
            if (dis*1000 > disInM) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private List<StoreList> filterByTags(List<StoreList> models, List<String> tags) {

        final List<StoreList> filteredModelList = new ArrayList<>();
        for (StoreList model : models) {
            final String text = model.getTags().toLowerCase();
            Log.d("SEarch by query",text);
            for (int i =0 ; i<tags.size();i++) {
                String query = tags.get(i);
                if (text.contains(query)) {
                    filteredModelList.add(model);
                    Log.d("Selected", i + "");
                    break;
                }
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
        searchStoreList = new ArrayList<>(storeList);
        final List<StoreList> filteredModelList = filter(searchStoreList, newText);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVStoreAdapter(this,filteredModelList,session.getLat(),session.getLongi());
        mRecyclerView.setAdapter(mAdapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        } else if (menuItem != null && menuItem.getItemId() == R.id.action_filter) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDrawerItemSelected(View view, int position, List<String> appliedFilteredList, int maxDistance) {
        if (position == 0){
            if (maxDistance!=0 && !appliedFilteredList.isEmpty()){
                Log.d("By Dis + list", "No Null");
                List<StoreList> filteredModelList = filterByDis(storeList, maxDistance);
                filteredModelList = filterByTags(filteredModelList, appliedFilteredList);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new RVStoreAdapter(this,filteredModelList,session.getLat(),session.getLongi());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            else if (maxDistance==0){
                Log.d("By list","Null dist");
                List<StoreList> filteredModelList = filterByTags(storeList, appliedFilteredList);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new RVStoreAdapter(this,filteredModelList,session.getLat(),session.getLongi());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            } else if (appliedFilteredList.isEmpty()){
                Log.d("By Dis","Null list");
                List<StoreList> filteredModelList = filterByDis(storeList, maxDistance);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new RVStoreAdapter(this,filteredModelList,session.getLat(),session.getLongi());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
        else if (position == 1){
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new RVStoreAdapter(this,storeList,session.getLat(),session.getLongi());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}