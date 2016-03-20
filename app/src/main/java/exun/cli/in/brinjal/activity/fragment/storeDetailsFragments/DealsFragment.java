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
import android.widget.TextView;
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
import exun.cli.in.brinjal.adapter.RVCouponsAdapter;
import exun.cli.in.brinjal.model.CouponsList;

public class DealsFragment extends Fragment {

    String TAG = "Deals";

    String url = StoreDetail.urlCoupons;
    private ProgressDialog pDialog;
    TextView noDeal;
    int isCoupon = StoreDetail.isCoupon;

    // coupons items
    private List<CouponsList> couponsList = new ArrayList<CouponsList>();

    //Lists
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public DealsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);

        Log.d(TAG, url + "  " + isCoupon);

        noDeal = (TextView) rootView.findViewById(R.id.noActiveDeal);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.coupon_rv);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RVCouponsAdapter(couponsList);
        mRecyclerView.setAdapter(mAdapter);

        if (isCoupon == 1)
            showDeals();
        else {
            mRecyclerView.setVisibility(View.GONE);
            noDeal.setVisibility(View.VISIBLE);
        }

        return rootView;
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

    void showDeals(){

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        StringRequest couponReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Coupons: " + response);
                        hidePDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now store the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "Coupons " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0){

                                    for (int k = 0; k< data.length();k++){

                                        JSONObject obj = data.getJSONObject(k);
                                        CouponsList coupon = new CouponsList();
                                        coupon.setTitle(obj.getString("title"));
                                        coupon.setDescription(obj.getString("description"));
                                        coupon.setConditions(obj.getString("conditions"));

                                        // adding blog to blogs array
                                        couponsList.add(coupon);
                                    }
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Log.d(TAG,errorMsg);
                            }
                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Log.d(TAG,"Json error: " + e.getMessage());
                            mRecyclerView.setVisibility(View.GONE);
                            noDeal.setVisibility(View.VISIBLE);
                        }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(couponReq);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RVCouponsAdapter) mAdapter).setOnItemClickListener(new RVCouponsAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getActivity(), "Mah lyf!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}