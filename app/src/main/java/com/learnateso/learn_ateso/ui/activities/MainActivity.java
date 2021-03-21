package com.learnateso.learn_ateso.ui.activities;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.data.database.LADbProvider;
import com.learnateso.learn_ateso.data.database.PhrasesDao;
import com.learnateso.learn_ateso.helpers.SessionManager;
import com.learnateso.learn_ateso.ui.fragments.CategoriesFragment;
import com.learnateso.learn_ateso.ui.fragments.EventsFragment;
import com.learnateso.learn_ateso.ui.fragments.HeritageFragment;
import com.learnateso.learn_ateso.ui.fragments.ProVersionFragment;
import com.learnateso.learn_ateso.ui.fragments.ShopFragment;
import com.learnateso.learn_ateso.ui.fragments.ExploreFragment;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = Main2Activity.class.getSimpleName();
    private MainActivityViewModel mainActivityViewModel;

    private static final String TAG_FRAGMENT_HOME = "tag_frag_home";

    private static final String TAG_FRAGMENT_HERITAGE = "tag_frag_heritage";

    private static final String TAG_FRAGMENT_EVENTS = "tag_frag_events";

    private static final String TAG_FRAGMENT_EXPLORE = "tag_frag_explore";
    private AdView mAdView;
    private ListView mListView;
    private boolean isAtesoQuiz;
    private SimpleCursorAdapter words;

    private TextView mTextMessage;

    private BottomNavigationView bottomNavigationView;

    /*
    Maintains a list of Fragments for BottomNavigationView
     */
    private List<Fragment> fragments = new ArrayList<>(4);

    public static final int NEW_CATEGORY_ACTIVITY_REQUEST_CODE = 1;

    private SessionManager session;

    private DrawerLayout mDrawerLayout;

    private Toolbar toolbar;

    private String name, email;

    private Random rand;
    private SearchView searchView;
    private SearchManager searchManager;
    private String query;

    int max, min = 1, randomPhraseID;
    //prepare the share link
    private String app_link = "https://play.google.com/store/apps/details?id=com.learnateso.learn_ateso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // session manager
        session = new SessionManager(getApplicationContext());

        /*if (!session.isLoggedIn()) {
            logoutUser();
        }*/

        mListView = findViewById(R.id.list);

        setupToolbar();

        handleIntent(getIntent());

        //async to do stuff in background
        new getUserDetailsAsync().execute();

        setupToolbar();

        setupNavigationDrawer();

        setUpBottomNavigation();

        //get intent from which this activity is called
        Intent intent = getIntent();
        isAtesoQuiz = intent.getBooleanExtra("Ateso_Quiz", false);

        //CategoriesFragment categoriesFragment = findOrCreateViewFragment();

        //setupViewFragment(categoriesFragment);

        buildFragmentList();

        //set the 0th Fragment to be displayed by default
        switchFragment(0, TAG_FRAGMENT_HOME);


    }// cloe oncreate

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

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        setIntent(intent);
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            Intent wordIntent = new Intent(this, PhraseListActivity.class);
            wordIntent.setData(intent.getData());
            startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    /**
     * Searches the phrases and displays results for the given query.
     * //@param query The search query
     */

    private void showResults(String query) {

        //async task to get results from the db in background
        new getSearchResultsAsyncTask().execute(query);
    }

    private class getWordMatchesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(final Void... params) {
            searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            //searchView.setIconifiedByDefault(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
    }

    //async task to get stuff from db
    private class getUserDetailsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // Fetching user details from SQLite
            HashMap<String, String> user = mainActivityViewModel.getUserDetails();

            name = user.get("name");
            email = user.get("email");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getBaseContext(), name + " Logged In", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == NEW_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                Category category = new Category(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
                mCategoriesViewModel.insert(category);
            } else {
                Toast.makeText(
                        getApplicationContext(),"Not saved",
                        Toast.LENGTH_LONG).show();
            }
        }
    */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //method to setup bottom navigation
    private void setUpBottomNavigation() {
        bottomNavigationView = findViewById(R.id.navigation);
        setUpNavigationContent(bottomNavigationView);
    }

    private void setUpNavigationContent(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                switchFragment(0, TAG_FRAGMENT_HOME);
                                //mTextMessage.setText(R.string.title_home);
                                return true;
                            case R.id.navigation_explore_teso:
                                switchFragment(1, TAG_FRAGMENT_EXPLORE);
                                //mTextMessage.setText(R.string.title_home);
                                return true;
                            case R.id.navigation_teso_heritage:
                                switchFragment(2, TAG_FRAGMENT_HERITAGE);
                                //mTextMessage.setText(R.string.title_dashboard);
                                return true;
                            case R.id.navigation_events:
                                switchFragment(3, TAG_FRAGMENT_EVENTS);
                                //mTextMessage.setText(R.string.title_notifications);
                                return true;
                        }
                        return false;
                    }
                });
    }

    //method to switch correctly between the bottom navigation fragments
    private void switchFragment(int pos, String tag) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragments.get(pos), tag)
                .commit();
    }

    //method to build list of fragments
    private void buildFragmentList() {
        CategoriesFragment homeFragment = buildCategoriesFragment("Learn Ateso");

        ExploreFragment exploreFragment = buildUserExploreFragment("Explore Teso");

        HeritageFragment heritageFragment = buildHeritageFragment("Our Heritage");

        EventsFragment eventsFragment = buildEventsFragment("Upcoming Events");

        fragments.add(homeFragment);
        fragments.add(exploreFragment);
        fragments.add(heritageFragment);
        fragments.add(eventsFragment);
    }

    //build the fragment
    private CategoriesFragment buildCategoriesFragment(String title) {
        CategoriesFragment fragment = new CategoriesFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fragment_name", title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private ExploreFragment buildUserExploreFragment(String title) {
        ExploreFragment fragment = new ExploreFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fragment_name", title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private HeritageFragment buildHeritageFragment(String title) {
        HeritageFragment fragment = new HeritageFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fragment_name", title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private EventsFragment buildEventsFragment(String title) {
        EventsFragment fragment = new EventsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fragment_name", title);

        fragment.setArguments(bundle);

        return fragment;
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            int id = 9;

            Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
            intent.putExtra("item-ID", id);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //async task to get word matches from the db in background
        new getWordMatchesAsyncTask().execute();
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        return true;
    }

    private class getSearchResultsAsyncTask extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(final String... params) {
            query = params[0];
            return getContentResolver().query(LADbProvider.CONTENT_URI,
                    null, null, new String[]{params[0]}, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null) {
                // There are no results
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Search returned cursor null");
            } else {
                // Display the number of results
                int count = cursor.getCount();
                String countString = getResources().getQuantityString(R.plurals.search_results,
                        count, new Object[] {count, query});
                //mTextView.setText(countString);
                Log.e(TAG, "Search returned cursor size"+cursor.getCount());
                Toast.makeText(MainActivity.this, countString + " results found", Toast.LENGTH_LONG).show();

                // Specify the columns we want to display in the result
                String[] from = new String[] {PhrasesDao.KEY_TRANSLATION,
                        PhrasesDao.KEY_ATESO_PHRASE };

                // Specify the corresponding layout elements where we want the columns to go
                int[] to = new int[] {android.R.id.text1,
                        android.R.id.text2 };

                // Create a simple cursor adapter for the definitions and apply them to the ListView
                words = new SimpleCursorAdapter(MainActivity.this,
                        R.layout.result_two, cursor, from, to, 0);
                mListView.setAdapter(words);

                // Define the on-click listener for the list items
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Build the Intent used to open WordActivity with a specific word Uri
                        Intent wordIntent = new Intent(getApplicationContext(), PhraseListActivity.class);
                        Uri data = Uri.withAppendedPath(LADbProvider.CONTENT_URI,
                                String.valueOf(id));
                        wordIntent.setData(data);
                        startActivity(wordIntent);
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search){
            return true;
        }else if (id == R.id.action_random_phrase){
            generateRandomPhrase();
        }else if (id == R.id.action_favorite){
            Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
            intent.putExtra("favourites", true);
            startActivity(intent);
        }else if (id == R.id.action_about_ateso){
            Intent intent = new Intent(MainActivity.this, AboutAtesoActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_feedback){
            //feedback
            sendFeedback(MainActivity.this);
        }else if (id == R.id.action_share){
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            String shareBody = "\nI am learning to speak Ateso using the" +
                    "Learn Ateso app." +
                    "\n Download the Learn Ateso app from the Google Playstore." +
                    "\n "+app_link;
            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.setType("text/plain");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
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
                            Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
                            intent.putExtra("favourites", true);
                            startActivity(intent);
                            return true;
                        } else if (id == R.id.randomWord) {
                            generateRandomPhrase();
                        }
                        else if (id == R.id.about_ateso) {
                            //AboutAteso.Show(Main2Activity.this);
                            Intent intent = new Intent(MainActivity.this, AboutAtesoActivity.class);
                            startActivity(intent);

                        } else if (id == R.id.nav_share) {
                            Intent sharingIntent = new Intent();
                            sharingIntent.setAction(Intent.ACTION_SEND);
                            String shareBody = "\nI am learning to speak Ateso using the" +
                                    "Learn Ateso app." +
                                    "\n Download the Learn Ateso app from the Google Playstore." +
                                    "\n "+app_link;
                            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        } else if (id == R.id.feedback) {
                            //feedback
                            sendFeedback(MainActivity.this);
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
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@emtechint.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from Learn Ateso app user");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
   /* private void logoutUser() {
        session.setLogin(false);

        mainActivityViewModel.delete();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }*/
}
