package com.learnateso.learn_ateso.ui.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.fragments.SectionsFragment;

public class CategorySectionsActivity extends AppCompatActivity {

    public static CategorySectionsActivity instance;
    private String catname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_sections);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setupActionBar();
        instance = this;

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
