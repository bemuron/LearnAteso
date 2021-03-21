package com.learnateso.learn_ateso.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.CategoriesFragment;
import com.learnateso.learn_ateso.ui.fragments.EventsFragment;
import com.learnateso.learn_ateso.ui.fragments.HeritageFragment;
import com.learnateso.learn_ateso.ui.fragments.ProverbsFragment;
import com.learnateso.learn_ateso.ui.fragments.StoriesFragment;
import com.learnateso.learn_ateso.ui.fragments.VisionFragment;

public class ExploreCategoriesActivity extends AppCompatActivity {
    private int categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_categories);

        //get data passed in to activity
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id",0);
        categoryName = intent.getStringExtra("category_name");


        // Check that the activity is using the layout version with
        // the explore_fragment_container FrameLayout
        if (findViewById(R.id.explore_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
        }

        //show the right category fragment based on the id
        switch (categoryId){
            case 1:
                StoriesFragment storiesFragment = findOrCreateViewFragment();
                setupViewFragment(storiesFragment);
                break;

            case 2:
                ProverbsFragment proverbsFragment = findOrCreateProverbsFragment();
                setupViewFragment(proverbsFragment);
                break;

            case 3:
                HeritageFragment heritageFragment = findOrCreateHeritageFragment();
                setupViewFragment(heritageFragment);
                break;

            case 4:
                EventsFragment eventsFragment = findOrCreateEventsFragment();
                setupViewFragment(eventsFragment);
                break;

            case 5:
                VisionFragment visionFragment = findOrCreateVisionFragment();
                setupViewFragment(visionFragment);
                break;
        }
    }

    //set up the stories fragment for display
    private void setupViewFragment(StoriesFragment storiesFragment) {

        // Add the fragment to the 'explore_fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, storiesFragment)
                .commit();
    }

    @NonNull
    private StoriesFragment findOrCreateViewFragment() {

        StoriesFragment storiesFragment = (StoriesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_fragment_container);

        if (storiesFragment == null) {
            storiesFragment = StoriesFragment.newInstance(categoryName);
        }
        return storiesFragment;
    }

    //set up the proverbs fragment for display
    private void setupViewFragment(ProverbsFragment proverbsFragment) {

        // Add the fragment to the 'explore_fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, proverbsFragment)
                .commit();
    }

    @NonNull
    private ProverbsFragment findOrCreateProverbsFragment() {

        ProverbsFragment proverbsFragment = (ProverbsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_fragment_container);

        if (proverbsFragment == null) {
            proverbsFragment = ProverbsFragment.newInstance(categoryName);
        }
        return proverbsFragment;
    }

    //set up the heritage fragment for display
    private void setupViewFragment(HeritageFragment heritageFragment) {

        // Add the fragment to the 'explore_fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, heritageFragment)
                .commit();
    }

    @NonNull
    private HeritageFragment findOrCreateHeritageFragment() {

        HeritageFragment heritageFragment = (HeritageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_fragment_container);

        if (heritageFragment == null) {
            heritageFragment = HeritageFragment.newInstance(categoryName);
        }
        return heritageFragment;
    }

    //set up the events fragment for display
    private void setupViewFragment(EventsFragment eventsFragment) {

        // Add the fragment to the 'explore_fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, eventsFragment)
                .commit();
    }

    @NonNull
    private EventsFragment findOrCreateEventsFragment() {

        EventsFragment eventsFragment = (EventsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_fragment_container);

        if (eventsFragment == null) {
            eventsFragment = EventsFragment.newInstance(categoryName);
        }
        return eventsFragment;
    }

    //set up the vison fragment for display
    private void setupViewFragment(VisionFragment visionFragment) {

        // Add the fragment to the 'explore_fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, visionFragment)
                .commit();
    }

    @NonNull
    private VisionFragment findOrCreateVisionFragment() {

        VisionFragment visionFragment = (VisionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.explore_fragment_container);

        if (visionFragment == null) {
            visionFragment = VisionFragment.newInstance(categoryName);
        }
        return visionFragment;
    }
}
