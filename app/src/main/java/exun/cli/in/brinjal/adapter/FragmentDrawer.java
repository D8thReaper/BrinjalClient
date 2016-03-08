package exun.cli.in.brinjal.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.helper.SQLiteHandler;

/**
 * Created by n00b on 3/6/2016.
 */
public class FragmentDrawer extends Fragment implements ExpandableListView.OnChildClickListener {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private ExpandableListView expandableListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private int lastExpandedPosition = -1;
    private SQLiteHandler db;
    Cursor mGroupsCursor;
    CursorTreeAdapter mAdapter;
    private FragmentDrawerListener drawerListener;
    private View containerView;
    TextView userName, userEmail;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_nav, container, false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        userName = (TextView) layout.findViewById(R.id.username);
        userEmail = (TextView) layout.findViewById(R.id.email);

        String name = db.getUserDetails().get("name");

        userName.setText(name);
        userEmail.setText(db.getUserDetails().get("email"));


        mGroupsCursor = db.fetchGroup();
        getActivity().startManagingCursor(mGroupsCursor);
        mGroupsCursor.moveToFirst();

        expandableListView = (ExpandableListView) layout.findViewById(R.id.left_drawer);

        mAdapter = new NavAdapter(getActivity(), mGroupsCursor,
                R.layout.nav_group_item,                     // Your row layout for a group
                new String[] { "name" },                      // Field(s) to use from group cursor
                new int[] { R.id.lblListHeader },                 // Widget ids to put group data into
                R.layout.nav_child_item,                 // Your row layout for a child
                new String[] { "name"},  // Field(s) to use from child cursors
                new int[] { R.id.lblListItem});          // Widget ids to put child data into

        expandableListView.setAdapter(mAdapter);                         // set the list adapter.

        expandableListView.setOnChildClickListener(this);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int subId, String title, int selection);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {

        Cursor pointerChildCursor,pointerParentCursor;
        pointerParentCursor = db.fetchGroup();
        getActivity().startManagingCursor(pointerParentCursor);
        pointerParentCursor.moveToPosition(groupPosition);

        pointerChildCursor = db.fetchChildren(pointerParentCursor.getInt(0));
        getActivity().startManagingCursor(pointerChildCursor);
        pointerChildCursor.moveToPosition(childPosition);
        int subId = pointerChildCursor.getInt(0);
        String title = pointerChildCursor.getString(1);


        drawerListener.onDrawerItemSelected(v, subId,title,1);
        mDrawerLayout.closeDrawer(containerView);
        return false;
    }

    public class NavAdapter extends SimpleCursorTreeAdapter {

        public NavAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            Cursor childCursor = db.fetchChildren(groupCursor.getInt(groupCursor.getColumnIndex("_id")));
            getActivity().startManagingCursor(childCursor);
            childCursor.moveToFirst();
            return childCursor;
        }
    }
}