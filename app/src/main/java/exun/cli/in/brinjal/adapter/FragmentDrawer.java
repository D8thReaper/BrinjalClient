package exun.cli.in.brinjal.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import java.util.HashMap;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.MainActivity;
import exun.cli.in.brinjal.contentProvider.Categories;
import exun.cli.in.brinjal.helper.SQLiteHandler;

/**
 * Created by n00b on 3/6/2016.
 */
public class FragmentDrawer extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private ListView lvCats, lvSubCats;
    private DrawerLayout mDrawerLayout;
    private SQLiteHandler db;
    SimpleCursorAdapter mAdapterCats,madapterSubCats;
    RelativeLayout llSubCats;
    private FragmentDrawerListener drawerListener;
    private View containerView;
    ImageView back;
    TextView userName, userEmail;


    public String[] childProjections = {"name","parent_id","_id"};
    public String[] groupProjections = {"name","_id"};
    private ImageView editUser;

    public FragmentDrawer() {
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_nav, container, false);

        setUpAdapters();

        lvCats = (ListView) layout.findViewById(R.id.lvCategories);
        lvSubCats = (ListView) layout.findViewById(R.id.lvSubCategories);
        llSubCats = (RelativeLayout) layout.findViewById(R.id.lvSubCatContainer);
        back = (ImageView) layout.findViewById(R.id.backNavSC);userName = (TextView) layout.findViewById(R.id.username);
        userEmail = (TextView) layout.findViewById(R.id.email);
        editUser = (ImageView) layout.findViewById(R.id.editUser);

        lvCats.setAdapter(mAdapterCats);
        lvSubCats.setAdapter(madapterSubCats);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        Loader loader = getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }

        setOnClickListeners();

        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        String name = db.getUserDetails().get("name");

        userName.setText(name);
        userEmail.setText(db.getUserDetails().get("email"));

        return layout;
    }

    private void setOnClickListeners() {

        final FragmentDrawer flg = this;

        lvCats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvCats.setVisibility(View.GONE);
                llSubCats.setVisibility(View.VISIBLE);
                Cursor cursor = (Cursor) lvCats.getItemAtPosition(position);

                int groupId = (cursor.getInt(cursor.getColumnIndex("_id")));
                Log.d(TAG, "getChildrenCursor() for groupId " + groupId);

                Loader loader = getLoaderManager().getLoader(groupId);
                if ( loader != null && !loader.isReset() ) {
                    getLoaderManager().restartLoader(groupId, null, flg);
                } else {
                    getLoaderManager().initLoader(groupId, null, flg);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCats.setVisibility(View.VISIBLE);
                llSubCats.setVisibility(View.GONE);
            }
        });

        lvSubCats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) lvSubCats.getItemAtPosition(position);

                int subCatID = cursor.getInt(cursor.getColumnIndex("_id"));
                String subCatNam = cursor.getString(cursor.getColumnIndex("name"));

                mDrawerLayout.closeDrawers();
                drawerListener.onDrawerItemSelected(view, subCatID, subCatNam, 1);
            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerListener.onDrawerItemSelected(v, 0, "User Profile", 3);
            }
        });
    }

    private void setUpAdapters() {
        mAdapterCats = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.nav_group_item,
                null,
                groupProjections,
                new int[]{R.id.lblListHeader}, 0);

        madapterSubCats = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.nav_child_item,
                null,
                childProjections,
                new int[]{R.id.lblListItem}, 0);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        Log.d(TAG, "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        if (id!= -1){
            Uri uri = Categories.CONTENT_URI_SUB_CATEGORIES;
            String selection = "parent_id = '" + id + "'";
            cl = new CursorLoader(getActivity(),
                    uri,
                    childProjections,
                    selection,
                    null,
                    null);
        } else {
            // group cursor
            Uri groupsUri = Categories.CONTENT_URI_CATEGORIES;
            cl = new CursorLoader(getActivity(), groupsUri,
                    groupProjections, null, null, null);
        }
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        Log.d(TAG, "onLoadFinished() for loader_id " + id);
        if (id != -1) {
            // child cursor
            madapterSubCats.swapCursor(data);
        } else {
            // group cursor
            mAdapterCats.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // is about to be closed.
        int id = loader.getId();
        Log.d(TAG, "onLoaderReset() for loader_id " + id);
        if (id != -1) {
            // child cursor
            madapterSubCats.swapCursor(null);
        } else {
            mAdapterCats.swapCursor(null);
        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int subId, String title, int selection);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}