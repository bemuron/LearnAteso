package com.learnateso.learn_ateso.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

public class CategorySectionsActivity extends AppCompatActivity {
    private static final String TAG = CategorySectionsActivity.class.getSimpleName();
    public static CategorySectionsActivity instance;
    private String catname;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_sections);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setupActionBar();
        instance = this;

        RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
                .setMaxAdContentRating(MAX_AD_CONTENT_RATING_PG)
                .build();

        MobileAds.setRequestConfiguration(requestConfiguration);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        //setup and initialize the interstitial ads
        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.live_interstitial_ad_unit_id));

        //request for the ad
        adRequest = new AdRequest.Builder().build();
        //load it into the object
        mInterstitialAd.loadAd(adRequest);

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            startActivityAfterAd("notes");
        }

        //setUpInterstitialAd();

        mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            private void showToast(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                //showToast("Ad loaded.");
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                //showToast(String.format("Ad failed to load with error code %d.", errorCode));
                String error =
                        String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());

                Log.e(TAG, "onAdFailedToLoad() with error: "+error);
            }

            @Override
            public void onAdOpened() {
                //showToast("Ad opened.");
            }

            @Override
            public void onAdClosed() {
                //showToast("Ad closed.");
            }

            @Override
            public void onAdLeftApplication() {
                //showToast("Ad left application.");
            }
        });
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

    //show the ad
    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.e(TAG,"Ad did not load");
        }
    }


    //set up the interstitial ad
    private void startActivityAfterAd(String activityName){

        mInterstitialAd.setAdListener(
                new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        Log.i(TAG,"onAdLoaded()");
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Log.e(TAG,"onAdFailedToLoad() with error: " + error);
                    }

                    @Override
                    public void onAdClosed() {
                        Log.e(TAG,"Interstitial Ad closed");
                        /*if (activityName.equals("favs")){
                            Intent intent = new Intent(CategorySectionsActivity.this, FavouritesActivity.class);
                            startActivity(intent);
                        }else if (activityName.equals("notes")){
                            Intent intent = new Intent(CategorySectionsActivity.this, NotesListActivity.class);
                            startActivity(intent);
                        }*/
                    }
                });
    }

}
