package com.learnateso.learn_ateso.ui.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.models.WorkBook;
import com.learnateso.learn_ateso.ui.fragments.AudioComparisonExerciseFragment;
import com.learnateso.learn_ateso.ui.fragments.PictureQuizExerciseFragment;
import com.learnateso.learn_ateso.ui.fragments.QuizExerciseFragment;
import com.learnateso.learn_ateso.ui.fragments.SentenceConstFragment;
import com.learnateso.learn_ateso.ui.fragments.TypeTranslationFragment;
import com.learnateso.learn_ateso.ui.fragments.VoiceRecordingExerciseFragment;
import com.learnateso.learn_ateso.ui.fragments.WordMatchingFragment;
import com.learnateso.learn_ateso.ui.viewmodels.WorkBookViewModel;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

/**
 * Created by Emo on 3/31/2017.
 */

public class WorkBookActivity extends AppCompatActivity implements
        QuizExerciseFragment.OnOptionSelectedListener, TypeTranslationFragment.TypeTranslationListener,
        SentenceConstFragment.SentenceConstListener, WordMatchingFragment.WordMatchingListener,
        AudioComparisonExerciseFragment.AudioComparisonCallback, VoiceRecordingExerciseFragment.VoiceRecordingCallback,
        PictureQuizExerciseFragment.PicQuizCallback {

    public static WorkBookActivity instance;
    private static final String TAG = WorkBookActivity.class.getSimpleName();
    private Cursor cursor;
    private int max,min=1;
    private WorkBookViewModel workBookViewModel;
    private int categoryId,sectionId;
    //private ProgressBar progressBar;
    private FrameLayout exercise_frame_layout;
    private int counter = 0, progress = 0;
    private TextView progressCount, quiz_prep_tv;
    private String categoryName, sectionName;
    private ImageView quiz_prep_iv;
    private MediaPlayer audioplayer = null;
    private static int userFinalScore = 0, directHitCount = 0;
    private AdView mAdView;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.exercise_view_flipper);
            instance = this;
            //Toolbar toolbar = findViewById(R.id.toolbar);
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

        //progressBar = findViewById(R.id.workbook_progress);
        progressCount = findViewById(R.id.progress_count);
        quiz_prep_iv = findViewById(R.id.quiz_preps_iv);
        quiz_prep_tv = findViewById(R.id.quiz_content_prep_text);
        exercise_frame_layout = findViewById(R.id.fragment_container);

        workBookViewModel = ViewModelProviders.of
                (this).get(WorkBookViewModel.class);


        //async to do stuff in background
        new loadCategories().execute();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
        }

    }//closing onCreate

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
/*
    private void setProgressValue(final int progress){
        //st the progress
        progressBar.setProgress(progress);
        //thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                setProgressValue(progress);
            }
        });
        thread.start();
    }
*/
    public static WorkBookActivity getInstance() {
        return instance;
    }

    //async task to get stuff from db
    private class loadCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            Intent intent = getIntent();
            sectionId = intent.getIntExtra("sectionId", 1);
            categoryId = intent.getIntExtra("categoryId", 1);
            categoryName = intent.getStringExtra("categoryName");
            sectionName = intent.getStringExtra("sectionName");

            Log.d(TAG, "sectionID = " + sectionId + " *** categoryID = " + categoryId);

            cursor = workBookViewModel.getSingleLesson(sectionId, categoryId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                //set the name of this fragment in the toolbar
                (WorkBookActivity.this).getSupportActionBar().setTitle(sectionName);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            max = cursor.getCount();
            Log.e(TAG, "Number of rows for this exercise = "+max);
            //make the image and text vies visible informing the user that the
            //quiz content is still under preparation
            if (max < 1){
                quiz_prep_iv.setVisibility(View.VISIBLE);
                quiz_prep_tv.setVisibility(View.VISIBLE);
                exercise_frame_layout.setVisibility(View.GONE);
            }else{
                runExercise();
            }
        }
    }

    //move to the next counter position checking which exercise
    //to present next
    public void runExercise(){
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
            displayMetaInfo(counter);

        }
    }

    private void displayMetaInfo(int position) {
        progressCount.setText((position + 1) + " / " + cursor.getCount());
    }

    //method to check which exercise to present depending on the name from the db row
    private void checkNextExercise(Cursor cursor){
        String exerciseName = cursor.getString(cursor.getColumnIndex("exercise_name"));
        switch (exerciseName){
            case "word_quiz":
                quizExercise(cursor);
                break;
            case "sentence_construction":
                sentenceConstructionExercise(cursor);
                break;
            case "word_matching":
                wordMatchingExercise(cursor);
                break;
            case "picture_quiz":
                picQuizExercise(cursor);
                break;
            case "audio_comparison":
                atesoAudioComparisonExercise(cursor);
                break;
            case "voice_recording":
                VoiceRecordingExercise(cursor);
                break;
        }

    }

//quiz exercise callback
    @Override
    public void quizDataCallback(int dhcount, int score) {
        counter++;
        //setProgressValue(counter);
        handleScore(dhcount, score);
        displayMetaInfo(counter);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }else if (cursor.isAfterLast()){
            //play final audio
            playAudio();
            //launch the summary activity
            launchSumaryActivity();
            //Toast.makeText(this, "ce"+"finalscore = "+userFinalScore+" dh= "+directHitCount, Toast.LENGTH_SHORT).show();
        }
    }

    //type translation exercise callback
    @Override
    public void typeTranslationData() {
        counter++;
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }
    }

    //sentence construction exercise callback
    @Override
    public void sentenceConstData(int dhcount, int score) {
        counter++;
        displayMetaInfo(counter);
        handleScore(dhcount, score);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }else if (cursor.isAfterLast()){
            //play final audio
            playAudio();
            //launch the summary activity
            launchSumaryActivity();
            //Toast.makeText(this, "ce"+"finalscore = "+userFinalScore+" dh= "+directHitCount, Toast.LENGTH_SHORT).show();
        }
    }

    //word matching exercise callback
    @Override
    public void wordMatchingData() {
        counter++;
        displayMetaInfo(counter);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }
    }

    //audio comparison exercise callback
    @Override
    public void audioComparisonData() {
        counter++;
        displayMetaInfo(counter);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }
    }

    //voice recording exercise callback
    @Override
    public void voiceRecordingData() {
        counter++;
        displayMetaInfo(counter);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }else if (cursor.isAfterLast()){
            //play final audio
            playAudio();
            //launch the summary activity
            launchSumaryActivity();
            //Toast.makeText(this, "ce"+"finalscore = "+userFinalScore+" dh= "+directHitCount, Toast.LENGTH_SHORT).show();
        }
    }

    //picture quiz exercise callback
    @Override
    public void picQuizData(int dhcount, int score) {
        counter++;
        displayMetaInfo(counter);
        handleScore(dhcount, score);
        if (cursor.moveToPosition(counter)) {
            checkNextExercise(cursor);
        }else if (cursor.isAfterLast()){
            //play final audio
            playAudio();
            //launch the summary activity
            launchSumaryActivity();
            //Toast.makeText(this, "ce"+"finalscore = "+userFinalScore+" dh= "+directHitCount, Toast.LENGTH_SHORT).show();
        }
    }

    //Sentence Construction exercise
    public void sentenceConstructionExercise(Cursor cursor){
        String phrase = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_SENT_CONST_PHRASE));
        String ans = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_SENT_CONST_ANS));
        String hint = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_HINT));
        String atesoWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD));
        String engWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENGLISH_WORD));
        String audio = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_AUDIO));

        //create a new quiz fragment to be put in the container(framelayout)
        SentenceConstFragment sentenceConstFragment = new SentenceConstFragment();
        Bundle args = new Bundle();
        args.putString("phrase", phrase);
        args.putString("phraseAns", ans);
        args.putString("aWord", atesoWord);
        args.putString("EngWord", engWord);
        args.putString("Hint", hint);
        args.putString("audio", audio);
        sentenceConstFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sentenceConstFragment).commit();
    }

    //Quiz exercise
    public void quizExercise(Cursor cursor){
        String aPhrase = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD));
        String hint = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_HINT));
        String audio = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_AUDIO));
        String option1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_OPTION_1));
        String option2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_OPTION_2));
        String option3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_OPTION_3));
        String option4 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_OPTION_4));
        String answer = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_QUIZ_ANSWER));
        //create a new quiz fragment to be put in the container(framelayout)
        QuizExerciseFragment quizExerciseFragment = new QuizExerciseFragment();
        Bundle args = new Bundle();
        args.putString("Ateso_Phrase", aPhrase);
        args.putString("Hint", hint);
        args.putString("audio", audio);
        args.putString("Option_1", option1);
        args.putString("Option_2", option2);
        args.putString("Option_3", option3);
        args.putString("Option_4", option4);
        args.putString("Answer", answer);
        quizExerciseFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, quizExerciseFragment).commit();
    }

    //word matching exercise fragment
    private void wordMatchingExercise(Cursor cursor){
        String aWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD));
        String aWordMatch1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH1));
        String aWordMatch2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH2));
        String aWordMatch3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH3));
        String aWordMatchAudio1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH_AUDIO1));
        String aWordMatchAudio2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH_AUDIO2));
        String aWordMatchAudio3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD_MATCH_AUDIO3));
        String engWordMatch1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENG_WORD_MATCH1));
        String engWordMatch2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENG_WORD_MATCH2));
        String engWordMatch3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENG_WORD_MATCH3));
        String engWordMatch4 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENG_WORD_MATCH4));
        String engWordMatch5 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENG_WORD_MATCH5));

        //launch the fragment
        WordMatchingFragment wordMatchingFragment = new WordMatchingFragment();
        Bundle args = new Bundle();
        args.putString("AtesoWord", aWord);
        args.putString("aWordMatch1", aWordMatch1);
        args.putString("aWordMatch2", aWordMatch2);
        args.putString("aWordMatch3", aWordMatch3);
        args.putString("aWordMatchAudio1", aWordMatchAudio1);
        args.putString("aWordMatchAudio2", aWordMatchAudio2);
        args.putString("aWordMatchAudio3", aWordMatchAudio3);
        args.putString("engWordMatch1", engWordMatch1);
        args.putString("engWordMatch2", engWordMatch2);
        args.putString("engWordMatch3", engWordMatch3);
        args.putString("engWordMatch4", engWordMatch4);
        args.putString("engWordMatch5", engWordMatch5);
        wordMatchingFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, wordMatchingFragment);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    //Picture Quiz exercise
    public void picQuizExercise(Cursor cursor){
        String aWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD));
        String engWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENGLISH_WORD));
        String answer = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_QUIZ_ANSWER));
        String id = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_EXERCISE_ID));
        String picName1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_NAME_1));
        String picName2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_NAME_2));
        String picName3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_NAME_3));
        String picName4 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_NAME_4));
        String picAudio1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_AUDIO1));
        String picAudio2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_AUDIO2));
        String picAudio3 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_AUDIO3));
        String picAudio4 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_PIC_AUDIO4));
        //create a new quiz fragment to be put in the container(framelayout)
        PictureQuizExerciseFragment pictureQuizExerciseFragment = new PictureQuizExerciseFragment();
        Bundle args = new Bundle();
        args.putString("AtesoWord", aWord);
        args.putString("EngWord", engWord);
        args.putString("Answer", answer);
        args.putString("ID", id);
        args.putString("pic_audio1", picAudio1);
        args.putString("pic_audio2", picAudio2);
        args.putString("pic_audio3", picAudio3);
        args.putString("pic_audio4", picAudio4);
        args.putString("pic_name1", picName1);
        args.putString("pic_name2", picName2);
        args.putString("pic_name3", picName3);
        args.putString("pic_name4", picName4);
        pictureQuizExerciseFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, pictureQuizExerciseFragment).commit();
    }

    //Ateso Audio comparison exercise
    public void atesoAudioComparisonExercise(Cursor cursor){
        String aWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENGLISH_WORD));
        //String audio = c.getString(3);
        String audioCompare1 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_COMPARISON_AUDIO1));
        String audioCompare2 = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_COMPARISON_AUDIO2));
        //create a new quiz fragment to be put in the container(framelayout)
        AudioComparisonExerciseFragment audioComparisonExerciseFragment = new AudioComparisonExerciseFragment();
        Bundle args = new Bundle();
        args.putString("EngWord", aWord);
        //args.putString("audio", audio);
        args.putString("audioCompare1", audioCompare1);
        args.putString("audioCompare2", audioCompare2);
        audioComparisonExerciseFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, audioComparisonExerciseFragment).commit();
    }

    //Ateso Voice Recording exercise
    public void VoiceRecordingExercise(Cursor cursor){
        String aWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_WORD));
        String engWord = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ENGLISH_WORD));
        String aWordAudio = cursor.getString(cursor.getColumnIndex(WorkBook.KEY_ATESO_AUDIO));
        //create a new quiz fragment to be put in the container(framelayout)
        VoiceRecordingExerciseFragment voiceRecordingExerciseFragment = new VoiceRecordingExerciseFragment();
        Bundle args = new Bundle();
        args.putString("AtesoWord", aWord);
        args.putString("EngWord", engWord);
        args.putString("AtesoWordAudio", aWordAudio);
        voiceRecordingExerciseFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, voiceRecordingExerciseFragment).commit();
    }

    //launch the score activity to give user summary about the exercise
    private void launchSumaryActivity(){
        Bundle bundle_score = new Bundle();
        bundle_score.putString("sectionName", sectionName);
        bundle_score.putString("categoryName", categoryName);
        bundle_score.putInt("user_direct_hits",directHitCount);
        bundle_score.putInt("user_score", userFinalScore);
        Intent score_intent = new Intent(WorkBookActivity.this, ScoreActivity.class);
        score_intent.putExtras(bundle_score);
        startActivity(score_intent);
        //set back scores to zero
        directHitCount = 0;
        userFinalScore = 0;
        this.finish();
    }

    //play the audio
    private void playAudio(){
        int audioId = WorkBookActivity.getInstance().getResources().getIdentifier("emali",
                "raw", WorkBookActivity.getInstance().getPackageName());

        //audioplayer.reset();
        if (audioplayer != null){
            if (audioplayer.isPlaying()||audioplayer.isLooping()) {
                audioplayer.stop();
            }
            audioplayer.release();
            audioplayer = null;
        }
        audioplayer = MediaPlayer.create(WorkBookActivity.getInstance().getApplicationContext(), audioId);
        //play audio
        audioplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    private void handleScore(int dh, int fs){
        directHitCount = directHitCount + dh;
        userFinalScore = userFinalScore + fs;
    }

}
