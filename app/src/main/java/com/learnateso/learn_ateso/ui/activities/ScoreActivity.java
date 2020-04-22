package com.learnateso.learn_ateso.ui.activities;


        import android.content.Intent;
        import android.os.Bundle;
        import androidx.appcompat.app.ActionBar;
        import androidx.appcompat.app.AppCompatActivity;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.ads.AdListener;
        import com.google.android.gms.ads.AdRequest;
        import com.google.android.gms.ads.AdView;
        import com.google.android.gms.ads.InterstitialAd;
        import com.google.android.gms.ads.MobileAds;
        import com.learnateso.learn_ateso.R;

/**
 * Created by BE on 3/24/2018.
 */

public class ScoreActivity extends AppCompatActivity {
    private static final String TAG = ScoreActivity.class.getSimpleName();
    private TextView points,directHits;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setupActionBar();

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

        points = (TextView)findViewById(R.id.score_points);
        directHits = (TextView)findViewById(R.id.num_dhits);
        TextView catSect = findViewById(R.id.category_section);

        //Intent score_intent = getIntent();
        Bundle bundle_score = getIntent().getExtras();
        if(bundle_score != null) {
            //fetch value from key-value pair and make it visible on textView
            int num_direct_hits = bundle_score.getInt("user_direct_hits");
            int user_score = bundle_score.getInt("user_score");
            String section = bundle_score.getString("sectionName");
            String category = bundle_score.getString("categoryName");
            //section.setTextColor(Color.parseColor("#5FCC00"));


            points.setText(String.valueOf(user_score));
            directHits.setText(String.valueOf(num_direct_hits));
            catSect.setText("Category: "+ category +" | Section: "+ section );
            //catSect.setTextColor(Color.parseColor("#5FCC00"));
/*
                getCurrentFocus().startAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.vertical));
                                */

        }

        Button continu = (Button) findViewById(R.id.continu);
        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();

                    Intent intent = new Intent(ScoreActivity.this, Main2Activity.class);
                    intent.putExtra("Ateso_Quiz", true);
                    startActivity(intent);
                    ScoreActivity.this.finish();
                }else{*/
                    Intent intent = new Intent(ScoreActivity.this, Main2Activity.class);
                    intent.putExtra("Ateso_Quiz", true);
                    startActivity(intent);
                    ScoreActivity.this.finish();
               // }
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        /*
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, QuizCategoriesActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

