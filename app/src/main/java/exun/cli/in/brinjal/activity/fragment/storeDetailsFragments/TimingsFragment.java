package exun.cli.in.brinjal.activity.fragment.storeDetailsFragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import exun.cli.in.brinjal.activity.StoreDetail;
import exun.cli.in.brinjal.adapter.AppController;
import exun.cli.in.brinjal.adapter.RVTimingAdapter;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.model.TimingsList;

public class TimingsFragment extends Fragment {

    // Log tag
    private static final String TAG ="Timings";

    // Timings json url
    private static final String url = AppConstants.URL_TIMINGS + StoreDetail.Sid;
    private ProgressDialog pDialog;
    private List<TimingsList> timingList = new ArrayList<TimingsList>();

    //Lists
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public String[] daysOfWeek = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};


    public TimingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timings,container,false);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.timings_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVTimingAdapter(timingList);
        mRecyclerView.setAdapter(mAdapter);

        // Creating volley request obj
        StringRequest timingReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "timings: " + response);
                        hidePDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now timing the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "timings " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0){

                                    for (int k = 0; k< data.length();k++){

                                        JSONObject obj = data.getJSONObject(k);
                                        TimingsList time = new TimingsList();
                                        time.setDay(daysOfWeek[k]);
                                        time.setIsOpen(obj.getBoolean("open"));
                                        time.setStartTime(obj.getString("start_time"));
                                        time.setEndTime(obj.getString("end_time"));
                                        time.setBreakStart(obj.getString("break_start"));
                                        time.setBreakEnd(obj.getString("break_end"));

                                        // adding timing to timings array
                                        timingList.add(time);
                                    }
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                showDefault();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            showDefault();
                            Log.d(TAG, "Json error: " + e.getMessage());
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
                        "Connection failed! :(", Toast.LENGTH_LONG).show();
                showDefault();
                hidePDialog();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(timingReq);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void showDefault() {

        for (int i = 0;i<7;i++){
            TimingsList time = new TimingsList();
            time.setDay(daysOfWeek[i]);
            if (i==5 || i==6)
                time.setIsOpen(false);
            else
                time.setIsOpen(true);
            time.setStartTime("9:00");
            time.setEndTime("7:00");
            time.setBreakStart("12:00");
            time.setBreakEnd("1:30");

            // adding timing to timings array
            timingList.add(time);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RVTimingAdapter) mAdapter).setOnItemClickListener(new RVTimingAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
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