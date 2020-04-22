package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.learnateso.learn_ateso.helpers.CheckableImageButton;
import com.learnateso.learn_ateso.ui.adapters.PicQuizGridAdapter;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;
import com.learnateso.learn_ateso.models.PicQuiz;

import java.util.ArrayList;

/**
 * Created by BE on 2/26/2018.
 */

public class PictureQuizExerciseFragment extends Fragment implements
        PicQuizGridAdapter.PicQuizGridAdapterOnItemClickHandler {

    private PicQuizGridAdapter picQuizGridAdapter;
    PicQuizCallback mCallback;

    private GridView gridView;

    private String ateso_word,eng_word,quiz_pic1,quiz_pic2,quiz_pic3,quiz_pic4,
            quiz_audio1,quiz_audio2,quiz_audio3,quiz_audio4, id, ans;

    private ArrayList<PicQuiz> quizArrayList; //= new ArrayList<PicQuiz>();

    private Button btn_continue;

    private int score, directHitCount=0, finalScore = 0;

    private MediaPlayer audioplayer = null;

    private TextView ateso_word_tv;

    public PictureQuizExerciseFragment(){

    }

    @Override
    public void onResume() {
        super.onResume();
        //mCategoriesViewModel.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.picture_quiz_exercise, container, false);

        SetUpViewWidgets(rootView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ateso_word = bundle.getString("AtesoWord");
            eng_word = bundle.getString("EngWord");
            ans = bundle.getString("Answer");
            id = bundle.getString("ID");
            quiz_audio1 = bundle.getString("pic_audio1");
            quiz_audio2 = bundle.getString("pic_audio2");
            quiz_audio3 = bundle.getString("pic_audio3");
            quiz_audio4 = bundle.getString("pic_audio4");
            quiz_pic1 = bundle.getString("pic_name1");
            quiz_pic2 = bundle.getString("pic_name2");
            quiz_pic3 = bundle.getString("pic_name3");
            quiz_pic4 = bundle.getString("pic_name4");
        }

        //display the Ateso phrase
        ateso_word_tv.setText(ateso_word);

        setUpQuizGridArray();
        setupQuizGridAdapter();
        picQuizExercise();
        return rootView;
    }

    // Container Activity must implement this interface
    public interface PicQuizCallback {
        public void picQuizData(int directHit, int score);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (PictureQuizExerciseFragment.PicQuizCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PicQuizCallback");
        }
    }

    private void setUpQuizGridArray(){
        quizArrayList = new ArrayList<PicQuiz>();

        PicQuiz picQuiz1 = new PicQuiz();
        picQuiz1.setId("1");
        picQuiz1.setmPicImageName(quiz_pic1);
        picQuiz1.setmPicQuizItemName(quiz_pic1);
        picQuiz1.setmAudioName(quiz_audio1);

        PicQuiz picQuiz2 = new PicQuiz();
        picQuiz2.setId("2");
        picQuiz2.setmPicImageName(quiz_pic2);
        picQuiz2.setmPicQuizItemName(quiz_pic2);
        picQuiz1.setmAudioName(quiz_audio2);

        PicQuiz picQuiz3 = new PicQuiz();
        picQuiz3.setId("3");
        picQuiz3.setmPicImageName(quiz_pic3);
        picQuiz3.setmPicQuizItemName(quiz_pic3);
        picQuiz1.setmAudioName(quiz_audio3);

        PicQuiz picQuiz4 = new PicQuiz();
        picQuiz4.setId("4");
        picQuiz4.setmPicImageName(quiz_pic4);
        picQuiz4.setmPicQuizItemName(quiz_pic4);
        picQuiz1.setmAudioName(quiz_audio4);

        quizArrayList.add(picQuiz1);
        quizArrayList.add(picQuiz2);
        quizArrayList.add(picQuiz3);
        quizArrayList.add(picQuiz4);
    }

    private void SetUpViewWidgets(View view){
        ateso_word_tv = view.findViewById(R.id.pic_quiz_phrase);
        gridView = view.findViewById(R.id.pic_quiz_gridview);
        btn_continue = view.findViewById(R.id.button_continue);
        //make the continue button invisible
        btn_continue.setVisibility(View.INVISIBLE);

    }

    private void setupQuizGridAdapter(){
        picQuizGridAdapter = new PicQuizGridAdapter(quizArrayList,getActivity(),this);

        //picQuizGridAdapter = new PicQuizGridAdapter(
          //      new ArrayList<PicQuiz>(0),getActivity(),this);

        gridView.setAdapter(picQuizGridAdapter);
        //onGridItemClickListener(gridView);
    }

    /**
     * This method is for responding to clicks from our grid.
     *
     * @param quizItemName name of pic selected
     */
    @Override
    public void onItemClick(String quizItemName, CheckableImageButton button) {

        //play the audio for the selected option
        checkAnsForAudio(quizItemName);
        //check the answer the user has selected
        checkAnswer(quizItemName, button);
    }

    private void picQuizExercise(){
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send data to WorkBook
                if (directHitCount == 1) {
                    mCallback.picQuizData(directHitCount, finalScore);
                }else{
                    mCallback.picQuizData(0, finalScore);
                }
            }
        });
    }

    //method to check the user's answer
    private void checkAnswer(String selectedOption, CheckableImageButton button){
        directHitCount++;
        if (directHitCount == 1 && selectedOption.equals(ans)){
            //award the user the points
            awardScore();
            btn_continue.setVisibility(View.VISIBLE);
        }else if (selectedOption.equals(ans)){
            //award the user the points
            awardScore();
            btn_continue.setVisibility(View.VISIBLE);
        }else{
            //award the user the points
            awardScore();
            Toast.makeText(getActivity(), "Sorry, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    //check and play the right audio
    private void checkAnsForAudio(String selectedOption){
        if (selectedOption.equals(quiz_pic1)){
            playAudio(quiz_audio1);
        }else if (selectedOption.equals(quiz_pic2)){
            playAudio(quiz_audio2);
        }else if (selectedOption.equals(quiz_pic3)){
            playAudio(quiz_audio3);
        }else if (selectedOption.equals(quiz_pic4)){

            playAudio(quiz_audio4);
        }
    }

    //play the audio
    private void playAudio(String audioName){
        int audioId = WorkBookActivity.getInstance().getResources().getIdentifier(audioName,
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

    //method to award the score
    private void awardScore(){
        if (finalScore == 0){
            finalScore = 10;
        }else if (finalScore == 10){
            finalScore = 10;
        }
    }
}
