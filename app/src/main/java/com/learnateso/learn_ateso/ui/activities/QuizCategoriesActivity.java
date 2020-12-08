package com.learnateso.learn_ateso.ui.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.CategoriesFragment;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;

import java.util.Random;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

public class QuizCategoriesActivity extends AppCompatActivity {
    private static final String TAG = QuizCategoriesActivity.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar toolbar;
    private Random rand;
    private boolean isAtesoQuiz;
    private int max;
    private int randomPhraseID;
    private int min = 1;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_categories);
        setupActionBar();

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

        //get intent from which this activity is called
        Intent intent = getIntent();
        isAtesoQuiz = intent.getBooleanExtra("Ateso_Quiz", false);

        CategoriesFragment categoriesFragment = findOrCreateViewFragment();

        setupViewFragment(categoriesFragment);
    }// closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
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
}
