package exun.cli.in.brinjal.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.fragment.Home;
import exun.cli.in.brinjal.activity.fragment.QRScan;
import exun.cli.in.brinjal.activity.fragment.Store;
import exun.cli.in.brinjal.adapter.FragmentDrawer;
import exun.cli.in.brinjal.helper.AppConstants;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    //Defining Variables for nav Drawer
    private Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    String subCatTitle = "Brinjal";
    int subCatId  = 1;

    // variables for Double Back to Exit
    boolean doubleBackToExitPressedOnce;
    private int select = 0;
    public String qrResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nav menu
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_bookmark)
            Toast.makeText(MainActivity.this,"No active bookmarks yet!",Toast.LENGTH_SHORT).show();

        return true;

    }

    public void displayView(int pos) {
        Fragment fragment = null;
        String url = AppConstants.URL_STORE_LIST + subCatId;
        select = pos;
        switch (pos) {
            case 0:
                fragment = new Home();
                subCatTitle = "Home";
                break;
            case 1:
                fragment = new Store();
                break;
            case 2:
                fragment = new QRScan();
                subCatTitle = "Scan Code";
                break;
            default:
                break;
        }

        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();

            if (getSupportActionBar().isShowing()){
                if (pos != 2) {
                    // set the toolbar title
                    getSupportActionBar().setTitle(subCatTitle);
                } else {
                    getSupportActionBar().hide();
                }
            } else {
                if (pos != 2) {
                    // set the toolbar title
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle(subCatTitle);
                }
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
}
