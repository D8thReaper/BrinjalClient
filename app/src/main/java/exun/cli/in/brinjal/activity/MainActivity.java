package exun.cli.in.brinjal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.fragment.Home;
import exun.cli.in.brinjal.activity.fragment.QRScan;
import exun.cli.in.brinjal.activity.fragment.SearchFragment;
import exun.cli.in.brinjal.adapter.FragmentDrawer;
import exun.cli.in.brinjal.helper.AppConstants;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    final String TAG = "MainActivity";

    //Defining Variables for nav Drawer
    private FragmentDrawer drawerFragment;
    String subCatTitle = "Brinjal";
    int subCatId  = 1;
    RelativeLayout toolBar;
    ImageView nav;

    // variables for Double Back to Exit
    boolean doubleBackToExitPressedOnce;
    private int select = 0;
    public String qrResult = null;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        nav = (ImageView) findViewById(R.id.imageView6);
        toolBar = (RelativeLayout) findViewById(R.id.toolbar);


        if (savedInstanceState!=null) {
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().getFragment(savedInstanceState, "drawerFragment");
        }
        else {
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        }

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerFragment.setDrawerListener(MainActivity.this);

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        displayView(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "drawerFragment", drawerFragment);
    }

    public void displayView(int pos) {
        Fragment fragment = null;
        Intent intent;
        String url = AppConstants.URL_STORE_LIST + subCatId;
        select = pos;
        switch (pos) {
            case 0:
                fragment = new Home();
                break;
            case 1:
                intent = new Intent(MainActivity.this,Store.class);
                intent.putExtra("url", url);
                intent.putExtra("title",subCatTitle);
                startActivity(intent);
                break;
            case 2:
                fragment = new QRScan();
                break;
            case 3:
                intent = new Intent(MainActivity.this,UserDetails.class);
                startActivity(intent);
                break;
            case 4:
                fragment = new SearchFragment();
                Log.d("displayview", "Kiddan?");
                break;
            default:
                break;
        }

        if (fragment != null && (pos == 2 || pos == 0 || pos == 4)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();

            if (pos == 0){
                if (toolBar.getVisibility() == View.GONE)
                    toolBar.setVisibility(View.VISIBLE);
            }
            else {
                if (toolBar.getVisibility() == View.VISIBLE)
                    toolBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int subId, String title, int selection) {
        subCatId = subId;
        subCatTitle = title;
        displayView(selection);
    }

    @Override
    public void onBackPressed() {
        if (select == 0){
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        else {
            displayView(0);
        }
    }

    public void setSubCatDetails(int subCatId, String subCatTitle){
        this.subCatId = subCatId;
        this.subCatTitle = subCatTitle;
    }
}
