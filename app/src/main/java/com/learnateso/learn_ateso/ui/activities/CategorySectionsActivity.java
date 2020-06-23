package com.learnateso.learn_ateso.ui.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.SectionsFragment;

public class CategorySectionsActivity extends AppCompatActivity {
    private static final String TAG = CategorySectionsActivity.class.getSimpleName();
    public static CategorySectionsActivity instance;
    private String catname;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_sections);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setupActionBar();
        instance = this;

        //initialise the ads
        MobileAds.initialize(this, "ca-app-pub-3075330085087679~4136422493");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3075330085087679/1767119183");

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            private void showToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                Log.e(TAG, "Interstitial Ad loaded");
                mInterstitialAd.show();
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

                //mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLeftApplication() {
                Log.e(TAG, "Ad left application");
            }
        });

        //setupActionBar();

        SectionsFragment sectionsFragment = findOrCreateViewFragment();

        setupViewFragment(sectionsFragment);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static CategorySectionsActivity getInstance() {
        return instance;
    }

    private void setupViewFragment(SectionsFragment sectionsFragment) {

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sectionsContentFrame, sectionsFragment)
                .commit();
    }

    @NonNull
    private SectionsFragment findOrCreateViewFragment() {
        //get intent from which this activity is called and the id of category selected
        //Bundle bundle = getArguments();
        int categoryId = getIntent().getIntExtra("category_id", 1);
        String categoryName = getIntent().getStringExtra("category_name");
        boolean isAtesoQuiz = getIntent().getBooleanExtra("Ateso_Quiz", false);

        SectionsFragment sectionsFragment = (SectionsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.sectionsContentFrame);

        if (sectionsFragment == null) {
            sectionsFragment = SectionsFragment.newInstance(categoryId, categoryName, isAtesoQuiz);
        }
        return sectionsFragment;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

}
