package com.learnateso.learn_ateso.ui.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.helpers.SessionManager;
import com.learnateso.learn_ateso.ui.fragments.CategoriesFragment;
import com.learnateso.learn_ateso.ui.fragments.ProVersionFragment;
import com.learnateso.learn_ateso.ui.fragments.ShopFragment;
import com.learnateso.learn_ateso.ui.fragments.UserProfileFragment;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    private MainActivityViewModel mainActivityViewModel;

    private static final String TAG_FRAGMENT_HOME = "tag_frag_home";

    private static final String TAG_FRAGMENT_PROFILE = "tag_frag_profile";

    private static final String TAG_FRAGMENT_SHOP = "tag_frag_shop";

    private static final String TAG_FRAGMENT_PRO = "tag_frag_pro";

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

    int max, min = 1, randomPhraseID;
    //prepare the share link
    private String app_link = "https://play.google.com/store/apps/details?id=com.learnateso.learn_ateso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = ViewModelProviders.of
                (this).get(MainActivityViewModel.class);

        // session manager
        session = new SessionManager(getApplicationContext());

        /*if (!session.isLoggedIn()) {
            logoutUser();
        }*/

        //async to do stuff in background
        new getUserDetailsAsync().execute();

        setupToolbar();

        setupNavigationDrawer();

        setUpBottomNavigation();

        buildFragmentList();

        //set the 0th Fragment to be displayed by default
        switchFragment(0, TAG_FRAGMENT_HOME);


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
                            case R.id.navigation_dashboard:
                                switchFragment(1, TAG_FRAGMENT_PROFILE);
                                //mTextMessage.setText(R.string.title_dashboard);
                                return true;
                            case R.id.navigation_notifications:
                                switchFragment(2, TAG_FRAGMENT_SHOP);
                                //mTextMessage.setText(R.string.title_notifications);
                                return true;
                            case R.id.pro_version:
                                switchFragment(3, TAG_FRAGMENT_PRO);
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
        CategoriesFragment homeFragment = buildCategoriesFragment("Home");

        UserProfileFragment profileFragment = buildUserProfileFragment("Profile");

        ShopFragment shopFragment = buildShopFragment("Shop");

        ProVersionFragment proFragment = buildProFragment("Pro");

        fragments.add(homeFragment);
        fragments.add(profileFragment);
        fragments.add(shopFragment);
        fragments.add(proFragment);
    }

    //build the fragment
    private CategoriesFragment buildCategoriesFragment(String title) {
        CategoriesFragment fragment = new CategoriesFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CategoriesFragment.ARG_TITLE, title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private UserProfileFragment buildUserProfileFragment(String title) {
        UserProfileFragment fragment = new UserProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CategoriesFragment.ARG_TITLE, title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private ShopFragment buildShopFragment(String title) {
        ShopFragment fragment = new ShopFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CategoriesFragment.ARG_TITLE, title);

        fragment.setArguments(bundle);

        return fragment;
    }

    //build the fragment
    private ProVersionFragment buildProFragment(String title) {
        ProVersionFragment fragment = new ProVersionFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CategoriesFragment.ARG_TITLE, title);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_ateso) {
            AboutAteso.Show(MainActivity.this);
        } else if (id == R.id.action_favorite) {
            //Intent intent = new Intent(this, FavouritesActivity.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Handle navigation view item clicks here.
                        int id = item.getItemId();

                        if (id == R.id.favs) {
                            //Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
                            //startActivity(intent);
                            return true;
                        } else if (id == R.id.randomWord) {
                            randomPhraseID = rand.nextInt((max - min) + 1) + min;
                            Intent intent = new Intent(MainActivity.this, PhraseListActivity.class);
                            intent.putExtra("P-ID", randomPhraseID);
                            startActivity(intent);
                        } else if (id == R.id.about_ateso) {
                            AboutAteso.Show(MainActivity.this);
                        } else if (id == R.id.nav_share) {
                            Intent sharingIntent = new Intent();
                            sharingIntent.setAction(Intent.ACTION_SEND);
                            String shareBody = "\n I am learning to speak Ateso using the" +
                                    "Learn Ateso app." +
                                    "\n "+app_link;
                            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
