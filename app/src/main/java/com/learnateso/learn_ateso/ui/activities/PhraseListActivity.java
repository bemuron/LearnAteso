package com.learnateso.learn_ateso.ui.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.data.database.PhrasesDao;
import com.learnateso.learn_ateso.models.Phrase;
import com.learnateso.learn_ateso.ui.adapters.PhrasesExpandableListAdapter;
import com.learnateso.learn_ateso.ui.viewmodels.MainActivityViewModel;
import com.learnateso.learn_ateso.ui.viewmodels.PhraseListViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

/**
 * Created by Emo on 10/17/2016.
 */
public class PhraseListActivity extends AppCompatActivity implements PhrasesExpandableListAdapter.PhrasesExpandableListAdapterListener {
    private static final String TAG = PhraseListActivity.class.getSimpleName();
    private PhraseListViewModel phraseListViewModel;
    private int categoryId,sectionId;
    private List<Phrase> phraseList;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<Phrase>> expandableListDetail;
    private ExpandableListView exlv;
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListDataPump expandableListDataPump;
    private String catname,sectname;
    private String audioname,ePhrase,aPhrase;
    private int audioId,phraseID, favouriteValue;
    private Parcelable state;
    MediaPlayer audioplayer = null;
    private int favouritePhrasePosition, rowid;
    ImageView notFav, inFavs;
    private boolean getFavourites;
    private String categoryName, sectionName;
    private Cursor c,cursor;
    Random rand;
    private AdView mAdView;
    //prepare the share link
    private String app_link = "https://play.google.com/store/apps/details?id=com.learnateso.learn_ateso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
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
        categoryName = intent.getStringExtra("categoryName");
        sectionName = intent.getStringExtra("sectionName");
        try {
            //set the name of this fragment in the toolbar
            (this).getSupportActionBar().setTitle(sectionName);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        phraseListViewModel = ViewModelProviders.of
                (this).get(PhraseListViewModel.class);

        exlv = findViewById(R.id.phrase_header_list);
        expandableListDataPump = new ExpandableListDataPump();


        sectionId = intent.getIntExtra("sectionId", 0);
        categoryId = intent.getIntExtra("categoryId", 0);
        phraseID = intent.getIntExtra("P-ID", 0);
        getFavourites = intent.getBooleanExtra("favourites", false);

        //if the user has clicked on random phrase
        if (phraseID > 0){
            try {
                //set the name of this fragment in the toolbar
                (this).getSupportActionBar().setTitle("Random Phrase");
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            phraseListViewModel.getRandomPhraseList(phraseID).observe(this,
                    new Observer<List<Phrase>>() {
                        @Override
                        public void onChanged(@Nullable final List<Phrase> phrases) {
                            // Update the cached copy of the categories in the adapter.
                            phraseList = phrases;
                            displayPhraseList(phraseList);

                        }
                    });
        }
        //user clicked on favourites
        else  if (getFavourites){
            //set the name on the action bar to favourites
            try {
                //set the name of this fragment in the toolbar
                (this).getSupportActionBar().setTitle("Favourites");
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            phraseListViewModel.getFavouritePhrases().observe(this,
                    new Observer<List<Phrase>>() {
                        @Override
                        public void onChanged(@Nullable final List<Phrase> phrases) {
                            // Update the cached copy of the categories in the adapter.
                            phraseList = phrases;
                            if (phraseList.size() > 0){
                                try{
                                    displayPhraseList(phraseList);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e(TAG, e.getMessage());
                                }
                            }else{
                                Toast.makeText(PhraseListActivity.this,
                                        "No favourite phrases added yet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else if(sectionId != 0 && categoryId != 0) {
        /*
        user has clicked on a specific section
        an observer for the LiveData returned by getPhrasesInSection.
        The onChanged() method fires when the observed data changes and the activity is
        in the foreground.
        */
            phraseListViewModel.getPhrasesInSection(sectionId, categoryId).observe(this,
                    new Observer<List<Phrase>>() {
                        @Override
                        public void onChanged(@Nullable final List<Phrase> phrases) {
                            // Update the cached copy of the categories in the adapter.
                            phraseList = phrases;
                            displayPhraseList(phraseList);
                        }
                    });
        }else {//user is coming from a search query
            //async task to get and load data
            new getPhraseAsyncTask(phraseListViewModel).execute();
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }// closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //get the phrase the user clicked on from the search results
    private class getPhraseAsyncTask extends AsyncTask<Void, Void, Void> {

        private PhraseListViewModel phraseListViewModel;

        getPhraseAsyncTask(PhraseListViewModel viewModel){
            phraseListViewModel = viewModel;
        }

        @Override
        protected Void doInBackground(Void... params){

            Uri uri = getIntent().getData();
            Log.e(TAG,"Search URI = "+uri);
            cursor = getContentResolver().query(uri, null, null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (cursor != null) {
            cursor.moveToFirst();

            //get the section Id of the phrase clicked on
            int sectionIdIndex = cursor.getColumnIndexOrThrow("section_id");
            int sectionId = cursor.getInt(sectionIdIndex);

            //async task
            //use the section id to get the section name
            new getSectionNameAsyncTask(phraseListViewModel).execute(sectionId);
            Log.e(TAG,"Section ID of phrase clicked = "+sectionId);

            //get the category id of the phrase clicked
            int categoryIdIndex = cursor.getColumnIndexOrThrow("category_id");
            int categoryId = cursor.getInt(categoryIdIndex);
            Log.e(TAG,"Category ID of phrase clicked = "+categoryId);

            //get the row Id of the phrase clicked on
            int rowIdIndex = cursor.getColumnIndexOrThrow("rowid");
            rowid = cursor.getInt(rowIdIndex);
            Log.e(TAG,"Row ID of phrase clicked = "+rowid);

            //get these set of phrases
            phraseListViewModel.getPhrasesInSection(sectionId, categoryId).observe(PhraseListActivity.this,
                    new Observer<List<Phrase>>() {
                        @Override
                        public void onChanged(@Nullable final List<Phrase> phrases) {
                            // Update the cached copy of the categories in the adapter.
                            phraseList = phrases;
                            displayPhraseList(phraseList);

                            //scroll to the position of that phrase that was clicked from the search
                            //expand it
                            //play the audio too
                            int len = expandableListAdapter.getGroupCount();
                            for (int i = 0; i < len; i++) {
                                if (rowid == expandableListDetail.get(
                                        expandableListTitle.get(i)).get(0).getRowId()) {
                                    exlv.smoothScrollToPositionFromTop(i,20);
                                    exlv.expandGroup(i);

                                    //play the audio
                                    audioname = expandableListDetail.get(
                                            expandableListTitle.get(i)).get(0).getAtesoAudio().toLowerCase(Locale.US);
                                    audioId = getResources().getIdentifier(audioname,
                                            "raw", getPackageName());
                                    //audioplayer.reset();
                                    if (audioplayer != null){
                                        if (audioplayer.isPlaying()||audioplayer.isLooping()) {
                                            audioplayer.stop();
                                        }
                                        audioplayer.release();
                                        audioplayer = null;
                                    }
                                    audioplayer = MediaPlayer.create(getApplicationContext(), audioId);
                                    //play audio
                                    audioplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                        }
                                    });
                                    //get out of the loop
                                    break;
                                }
                            }
                        }
                    });
            } else {
                Toast.makeText(PhraseListActivity.this, "Sorry couldn't load phrases", Toast.LENGTH_LONG).show();
                Log.e(TAG,"Cursor is null = "+cursor);
                cursor.close();
            }
        }
    }

    //get the section name of  phrase the user clicked on from the search results
    private class getSectionNameAsyncTask extends AsyncTask<Integer, Void, String> {

        private PhraseListViewModel phraseListViewModel;

        getSectionNameAsyncTask(PhraseListViewModel viewModel){
            phraseListViewModel = viewModel;
        }

        @Override
        protected String doInBackground(final Integer... params){
            sectname = phraseListViewModel.getSectionName(params[0]);
            Log.e(TAG, "Section id = "+params[0]);
            return sectname;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Section name = "+sectname);
            try {
                //set the name of this fragment in the toolbar
                PhraseListActivity.this.getSupportActionBar().setTitle(sectname);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /*@Override
    public void onPause(){
        //save the expandable list view state(includes the scroll position) as a Parcelable
        state = exlv.onSaveInstanceState();
        super.onPause();
    }*/

    public void displayPhraseList(final List<Phrase> phraseList){

        //Log.i(TAG, "PhraseDataList size = "+phraseDataList.size());
        //Log.i(TAG, "ExpandableListDetail size = "+expandableListDetail.size());
        expandableListDetail = expandableListDataPump.getData(phraseList);
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new PhrasesExpandableListAdapter(this, expandableListTitle, expandableListDetail, this);
        //save the expandable list view state(includes the scroll position) as a Parcelable
        state = exlv.onSaveInstanceState();
        exlv.setAdapter(expandableListAdapter);
        //restore previous state(includes selected item, index and scroll position)
        exlv.onRestoreInstanceState(state);

        //click listener for the headers/english phrases
        exlv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition,long id) {
                Log.e(TAG, "On Group Click group position = "+groupPosition);
                Log.e(TAG, "OnGroupClick group ID = "+expandableListAdapter.getGroupId(groupPosition));
                Log.e(TAG, "On Group Click group position = "+phraseList);

                audioname = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(0).getAtesoAudio().toLowerCase(Locale.US);
                audioId = getResources().getIdentifier(audioname,
                        "raw", getPackageName());
                //audioplayer.reset();
                if (audioplayer != null){
                    if (audioplayer.isPlaying()||audioplayer.isLooping()) {
                        audioplayer.stop();
                    }
                    audioplayer.release();
                    audioplayer = null;
                }
                audioplayer = MediaPlayer.create(getApplicationContext(), audioId);
                //play audio
                audioplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                //cursor.requery();

                return false;
            }
        });

        exlv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                int len = expandableListAdapter.getGroupCount();
                Log.e(TAG, "OnGroupExpand group position parent "+groupPosition);

                    for (int i = 0; i < len; i++) {
                        if (i != groupPosition) {
                            exlv.collapseGroup(i);
                        }
                    }

                //Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition)+" Expanded",
                  //      Toast.LENGTH_SHORT).show();
            }
        });

        //click listener for the child clicks
        exlv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                /*Toast.makeText(getApplicationContext(), expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(childPosition).getAtesoAudio(),
                        Toast.LENGTH_SHORT).show();*/


                // play ateso phrase again when a person clicks on child
                audioname = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(childPosition).getAtesoAudio().toLowerCase(Locale.US);
                audioId = getResources().getIdentifier(audioname,
                        "raw", getPackageName());
                //audioplayer.reset();
                if (audioplayer != null){
                    if (audioplayer.isPlaying()||audioplayer.isLooping()) {
                        audioplayer.stop();
                    }
                    audioplayer.release();
                    audioplayer = null;
                }
                audioplayer = MediaPlayer.create(getApplicationContext(), audioId);
                //play audio
                audioplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                return false;
            }
        });

        exlv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(),phraseList.get(groupPosition)+" Collapsed",
                  //      Toast.LENGTH_SHORT).show();
            }
        });

    }//closing display phrases

    //add a phrase to favourites when the icon is clicked
    @Override
    public void onIconImportantClicked(Phrase phrase, int position) {
        int isFavourite = phrase.getIsFavourite();
        if (isFavourite == 1) {
            Log.e(TAG, "removing from favourites");
            phraseListViewModel.removeFavouritePhrase(phrase.getRowId());
            Log.e(TAG, "group position child "+position);
            /*imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp));
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_selected));*/
        } else {
            Log.e(TAG, "adding to favourites");
            phraseListViewModel.addFavouritePhrase(phrase.getRowId());
            Log.e(TAG, "group position child "+position);
            /*imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_black_24dp));
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_normal));*/
        }
        favouritePhrasePosition = position;
        //restore previous state(includes selected item, index and scroll position)
        //exlv.setSelection(position);
        //exlv.expandGroup(position);
        //exlv.smoothScrollToPosition(position);
        //exlv.setSelection(position);

    }

    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {
        Context context;
        public MyExpandableListAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom,
                                       int[] groupTo, int childLayout, String[] childrenFrom, int[] childrenTo) {
                super(context, cursor, groupLayout, groupFrom, groupTo,
                        childLayout, childrenFrom, childrenTo);
            this.context = context;
        }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        //Cursor childCursor = dbhelper.getPhraseChildren(pid);
        System.out.println(phraseID);

        return groupCursor;
    }
        //method to handle clicks on child items
        public View getChildView(final int groupPosition,
                                 final int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            View rowView = super.getChildView(groupPosition, childPosition,
                    isLastChild, convertView, parent);

            //share imageview and click listener
            ImageView share = (ImageView) rowView.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    shareIt(ePhrase,aPhrase);
                    //Toast.makeText(getApplicationContext(), ePhrase + aPhrase,
                      //      Toast.LENGTH_SHORT).show();
                }
            });

            //2 image views of the favourites star and the click listeners
            notFav = rowView.findViewById(R.id.fav_grey);
            inFavs = rowView.findViewById(R.id.fav_yellow);

            updateStar(favouriteValue);
            //state=0;

            notFav.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    inFavs.setVisibility(View.VISIBLE);
                    notFav.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), favouriteValue,
                            Toast.LENGTH_SHORT).show();

                    //updateStar(favouriteValue);
                    Toast.makeText(getApplicationContext(), "Added to favs",
                            Toast.LENGTH_SHORT).show();
                    //c1.close();
                }
            });
            inFavs.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    inFavs.setVisibility(View.INVISIBLE);
                    notFav.setVisibility(View.VISIBLE);

                    //dbhelper.deleteFavourite(phraseID);
                    //cursor.requery();
                   // updateStar(favouriteValue);

                    Toast.makeText(getApplicationContext(), "Deleted from favs",
                            Toast.LENGTH_SHORT).show();
                }
            });

            return rowView;
        }
    }

    public void updateStar(int value){
        if(value == 1){
            inFavs.setVisibility(View.VISIBLE);
            notFav.setVisibility(View.INVISIBLE);
        } else if (value == 0) {
            inFavs.setVisibility(View.INVISIBLE);
            notFav.setVisibility(View.VISIBLE);
        }
    }
/*
** instead of cursor.requery(), this async task can be used to create an
* updated cursor reflecting all the changes done to the db. It should be
* called the same way the first one above is done.
    private class checkFavourite extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            c1 = dbhelper.getPhraseChildren(phraseID);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            c1.moveToFirst();
            favouriteValue = cursor.getString(6);
            updateStar(favouriteValue);
        }
    }
*/
    //code for sharing the words with other apps
    public void shareIt(String eWord, String aWord){
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        String shareBody = "Hey, I learnt a new phrase in Ateso:" +
        "\n" +aWord+ " means " + eWord+"." +
        "\n "+app_link;
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.setType("text/plain");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public class ExpandableListDataPump{
        public LinkedHashMap<String, List<Phrase>> getData(List<Phrase> phraseList){
            LinkedHashMap<String, List<Phrase>> expandableListDetail = new LinkedHashMap<>();
            Map<String, List<Phrase>> expandableListDetails = new TreeMap<>(expandableListDetail);

            List<Phrase> phraseDataList;
            Phrase phrase;

            for (int i=0; i < phraseList.size(); i++){
                phraseDataList = new ArrayList<>();
                phrase = new Phrase();
                //if (i == phraseList.get(i).getPhraseId()){
                phrase.setIsFavourite(phraseList.get(i).getIsFavourite());
                phrase.setPhraseCategoryId(phraseList.get(i).getPhraseCategoryId());
                phrase.setPhraseSectionId(phraseList.get(i).getPhraseSectionId());
                phrase.setTranslation(phraseList.get(i).getTranslation());
                phrase.setAtesoAudio(phraseList.get(i).getAtesoAudio());
                phrase.setPhrasePic(phraseList.get(i).getPhrasePic());
                phrase.setRowId(phraseList.get(i).getRowId());
                phrase.setAtesoPhrase(phraseList.get(i).getAtesoPhrase());

                //engPhrase = phraseList.get(i).getTranslation();

                phraseDataList.add(phrase);
                expandableListDetail.put(phraseList.get(i).getTranslation(), phraseDataList);
                Log.e(TAG, "PhraseDataList size inside loop= "+phraseDataList.size());
                Log.e(TAG, "expandableListDetail size inside loop = "+expandableListDetail.size());
                //}
            }
            return expandableListDetail;
        }
    }
}