package exun.cli.in.brinjal.adapter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
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
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.model.Filter;

/**
 * Created by n00b on 3/6/2016.
 */
public class FragmentDrawerFilter extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private static final String url = AppConstants.URL_TAGS;
    private ProgressDialog pDialog;
    private List<Filter> filterList = new ArrayList<Filter>();
    public List<String> appliedFilters = new ArrayList<String>();
    private ListView listView;
    private FilterListAdapter adapter;
    LinearLayout apply, clear;

    private DrawerLayout mDrawerLayout;
    private View containerView;
    private FragmentFilterDrawerListener drawerListener;
    private int i = 0,filteredMaxDistance = 0;
    RadioGroup radioGroup;

    public FragmentDrawerFilter(){}

    public void setDrawerListener(FragmentFilterDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_filter_nav, container, false);

        listView = (ListView) layout.findViewById(R.id.lvFilterTags);
        adapter = new FilterListAdapter(getActivity(), filterList);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

        apply = (LinearLayout) layout.findViewById(R.id.applyFilters);
        clear = (LinearLayout) layout.findViewById(R.id.clearFiltersDistance);

        radioGroup = (RadioGroup) layout.findViewById(R.id.distance_filter_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.dis1:
                        filteredMaxDistance = 1;
                        break;
                    case R.id.dis5:
                        filteredMaxDistance = 5;
                        break;
                    case R.id.dis10:
                        filteredMaxDistance = 10;
                        break;
                    default:
                        filteredMaxDistance = 0;
                        break;
                }
            }
        });

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        populateFilterList();

        setClickListeners();
        return layout;
    }

    private void setClickListeners() {

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"Applied!",Toast.LENGTH_SHORT).show();
                for (i = 0; i< adapter.getCount();i++){
                    if (filterList.get(i).getIsChecked()) {
                        appliedFilters.add(filterList.get(i).getTag().toLowerCase());
                        Log.d("Filters",filterList.get(i).getTag());
                    }
                }

                drawerListener.onDrawerItemSelected(v,0,appliedFilters,filteredMaxDistance);

                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                adapter.uncheckAllChildrenCascade(listView);
                filteredMaxDistance = 0;

                drawerListener.onDrawerItemSelected(v,1,null,filteredMaxDistance);
                radioGroup.clearCheck();

                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });
    }

    private void populateFilterList() {

        StringRequest filterReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Filter: " + response);
                        hidePDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");

                            // Check for error node in json
                            if (status.equals("success")) {

                                // Now filter the user in SQLite
                                String message = jObj.getString("message");

                                Log.d(TAG, "Filter " + message);

                                JSONArray data = jObj.getJSONArray("data");

                                if (data.length() > 0){

                                    for (int k = 0; k< data.length();k++){

                                        JSONObject obj = data.getJSONObject(k);
                                        Filter filter = new Filter();
                                        filter.setTag(obj.getString("title"));
                                        filter.setValue(obj.getInt("value"));

                                        // adding movie to movies array
                                        filterList.add(filter);
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
                            Log.d(TAG,"Json error: " + e.getMessage());
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(filterReq);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;


    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }



    public interface FragmentFilterDrawerListener {
        public void onDrawerItemSelected(View view, int position, List<String> filteredList,int maxDistance);
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

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}