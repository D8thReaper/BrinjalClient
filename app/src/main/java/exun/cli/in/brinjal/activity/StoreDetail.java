package exun.cli.in.brinjal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.activity.fragment.storeDetailsFragments.DealsFragment;
import exun.cli.in.brinjal.activity.fragment.storeDetailsFragments.DescFragment;
import exun.cli.in.brinjal.activity.fragment.storeDetailsFragments.TimingsFragment;
import exun.cli.in.brinjal.adapter.TabPagerAdapter;
import exun.cli.in.brinjal.helper.AppConstants;

public class StoreDetail extends AppCompatActivity {

    public static String sLocation;
    private Toolbar toolbar;
    private ImageView imageView;
    private CollapsingToolbarLayout collapsingToolbar;
    private TabPagerAdapter tabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    public String title;
    public static String url;
    public static String urlCoupons;
    public static int isCoupon = 0;
    private String TAG  = "StoreDetail";
    public static int Sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);


        initialize();
        setToolbar();


        mViewPager= (ViewPager) findViewById(R.id.htab_viewpager);
        tabPagerAdapter=new TabPagerAdapter(getSupportFragmentManager());
        setupViewPager();
        mViewPager.setAdapter(tabPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        imageView= (ImageView) findViewById(R.id.htab_header);

        collapsingToolbar=(CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.background_material);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {

                int vibrantColor = palette.getVibrantColor(R.color.colorPrimary);
                int vibrantDarkColor = palette.getDarkVibrantColor(R.color.colorPrimaryDark);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(vibrantDarkColor);
            }
        });


        mTabLayout= (TabLayout) findViewById(R.id.htab_tabs);
        mTabLayout.setTabsFromPagerAdapter(tabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        Log.d(TAG,"Tab 1");
                        break;
                    case 1:
                        Log.d(TAG,"Tab 2");
                        break;
                    case 2:
                        Log.d(TAG,"Tab 3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initialize() {
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        Sid = bundle.getInt("id");
        url = bundle.getString("url");
        urlCoupons = AppConstants.URL_COUPONS + bundle.getInt("id");
        isCoupon = bundle.getInt("isCoupons");
        sLocation = bundle.getString("location");
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager() {
        tabPagerAdapter.addFrag(new DescFragment(), "Description");
        tabPagerAdapter.addFrag(new TimingsFragment(), "Timings");
        tabPagerAdapter.addFrag(new DealsFragment(), "Deals");
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.htab_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
