package com.learnateso.learn_ateso.ui.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.CategoriesFragment;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = Main2Activity.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Random rand;
    private boolean isAtesoQuiz;
    private int max;
    private int randomPhraseID;
    private int min = 1;
    private AdView mAdView;

    private BottomNavigationView bottomNavigationView;

    /*
    Maintains a list of Fragments for BottomNavigationView
     */
    private List<CategoriesFragment> fragments = new ArrayList<>(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //initialise the ads
        MobileAds.initialize(this, "ca-app-pub-3075330085087679~4136422493");

        mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            private void showToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                Log.e(TAG, "Ad loaded");
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //showToast(String.format("Ad failed to load with error code %d.", errorCode));
                Log.e(TAG, "Failed to load ad "+errorCode);
            }

            @Override
            public void onAdOpened() {
                //showToast("Ad opened.");
                Log.e(TAG, "Ad opened");
            }

            @Override
            public void onAdClosed() {
                Log.e(TAG, "Ad closed");
            }

            @Override
            public void onAdLeftApplication() {
                Log.e(TAG, "Ad left application");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        rand = new Random();

        mainActivityViewModel = ViewModelProviders.of
                (this).get(MainActivityViewModel.class);

        setupToolbar();

        setupNavigationDrawer();

        //get intent from which this activity is called
        Intent intent = getIntent();
        isAtesoQuiz = intent.getBooleanExtra("Ateso_Quiz", false);

        CategoriesFragment categoriesFragment = findOrCreateViewFragment();

        setupViewFragment(categoriesFragment);
        //max = mainActivityViewModel.NumberOfPhrases();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void setupViewFragment(CategoriesFragment categoriesFragment) {

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.categoriesContentFrame, categoriesFragment)
                .commit();
    }

    @NonNull
    private CategoriesFragment findOrCreateViewFragment() {

        CategoriesFragment categoriesFragment = (CategoriesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.categoriesContentFrame);

        if (categoriesFragment == null) {
            categoriesFragment = CategoriesFragment.newInstance(isAtesoQuiz);
        }
        return categoriesFragment;
    }

    //generate a random phrase id
    private void generateRandomPhrase(){
        //max = mainActivityViewModel.NumberOfPhrases();
        //Log.e(TAG, "phrase count = "+ mainActivityViewModel.NumberOfPhrases());
        try {
            Log.e(TAG, "max count = "+ max);
            //Log.e(TAG, "phrase count in try block = "+ mainActivityViewModel.NumberOfPhrases());
            //max = mainActivityViewModel.NumberOfPhrases();
            randomPhraseID = rand.nextInt((196 - min) + 1) + min;
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Intent intent = new Intent(Main2Activity.this, PhraseListActivity.class);
        intent.putExtra("P-ID", randomPhraseID);
        startActivity(intent);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Handle navigation view item clicks here.
                        int id = item.getItemId();

                        if (id == R.id.favs) {
                            Intent intent = new Intent(Main2Activity.this, PhraseListActivity.class);
                            intent.putExtra("favourites", true);
                            startActivity(intent);
                            return true;
                        } else if (id == R.id.randomWord) {
                           generateRandomPhrase();
                        }
                        else if (id == R.id.about_ateso) {
                            //AboutAteso.Show(Main2Activity.this);
                            Intent intent = new Intent(Main2Activity.this, AboutAtesoActivity.class);
                            startActivity(intent);

                        } else if (id == R.id.nav_share) {
                            Intent sharingIntent = new Intent();
                            sharingIntent.setAction(Intent.ACTION_SEND);
                            String shareBody = "\nI am learning to speak Ateso using the" +
                                    "Learn Ateso app." +
                                    "\nDownload the Learn Ateso app from the Google Playstore.";
                            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        } else if (id == R.id.feedback) {
                            //feedback
                            sendFeedback(Main2Activity.this);
                        }

                        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        //drawer.closeDrawer(GravityCompat.START);
                        // Close the navigation drawer when an item is selected.
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    private static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@emtechint.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Learn Ateso app user");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }



}
