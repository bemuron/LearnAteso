package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;
import com.learnateso.learn_ateso.ui.activities.CategorySectionsActivity;

import java.util.ArrayList;

/**
 * Created by Emo on 4/3/2017.
 */

public class QuizExerciseFragment extends Fragment implements
        RadioGroup.OnCheckedChangeListener {
    private static final String TAG = QuizExerciseFragment.class.getSimpleName();
    OnOptionSelectedListener mCallback;
    private Button continu;
    private TextView phrase, hint;
    private String opt_Selected;
    private MediaPlayer audioplayer = null;
    private RadioButton opt_1;
    private ImageView audioImage;
    private RadioGroup radgrp;
    private int max, counter = 0, score, oSelectedCount=0, directHit=0, finalScore = 0;
    private String atesoPhrase, hintText, opt1, opt2, opt3, opt4, ans, audio;
    private ArrayList<String> radioOptions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quiz_exercise,container,false);

        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        radioOptions = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            atesoPhrase = bundle.getString("Ateso_Phrase");
            hintText = bundle.getString("Hint");
            audio = bundle.getString("audio");
            opt1 = bundle.getString("Option_1");
            opt2 = bundle.getString("Option_2");
            opt3 = bundle.getString("Option_3");
            opt4 = bundle.getString("Option_4");
            ans = bundle.getString("Answer");
            //add radio options to array
            radioOptions.add(opt1);
            radioOptions.add(opt2);
            radioOptions.add(opt3);
            radioOptions.add(opt4);
        }

        //Report that this fragment would like to participate in
        // populating the options menu by receiving a call to
        setHasOptionsMenu(true);

        setUpWidgets(view);
        quizExercise();
        return view;
    }

    // Container Activity must implement this interface
    public interface OnOptionSelectedListener {
        public void quizDataCallback(int directHit, int score);
    }

    @Override
    public void onAttach(Context activity) { //Try Context context as the parameter. It is not deprecated
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnOptionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOptionSelectedListener");
        }
    }

    //initialise the views
    private void setUpWidgets(View view){
        phrase = view.findViewById(R.id.wordTextView);
        audioImage = (ImageView) view.findViewById(R.id.audId);
        continu = view.findViewById(R.id.continu);
        continu.setVisibility(View.INVISIBLE);
        radgrp = (RadioGroup) view.findViewById(R.id.radiogroup);
        hint = view.findViewById(R.id.hintId);
    }

    private void quizExercise() {
        //if (c.moveToPosition(counter)) {
            score = 10;

            //play audio when audio image view is clicked
        audioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAudio();
            }
        });

            phrase.setText(atesoPhrase);
            phrase.setTypeface(null, Typeface.BOLD);

            //hint.setHint(c.getString(3));
            hint.setText(hintText);
            hint.setTextColor(Color.parseColor("#33802B"));

            radgrp.setOnCheckedChangeListener(this);
            radgrp.removeAllViews();
            int i;
            for (i = 0; i < radioOptions.size(); i++) {
                opt_1 = new RadioButton(getActivity());
                opt_1.setGravity(Gravity.CENTER);
                opt_1.setButtonDrawable(android.R.color.transparent);
                opt_1.setBackgroundResource(R.drawable.button_background);
                opt_1.setText(radioOptions.get(i));
                opt_1.setPadding(5, 15, 5, 15);
                opt_1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                opt_1.setTextSize(23);
                radgrp.addView(opt_1);
            }

            //continu.setVisibility(View.INVISIBLE);
            continu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(oSelectedCount==1 && opt_Selected.equals(ans)){
                        //send data to WorkBook
                        mCallback.quizDataCallback(oSelectedCount, finalScore);
                        //Toast.makeText(getActivity(), "dhcount" + oSelectedCount + "fs= "+finalScore, Toast.LENGTH_LONG).show();
                    }else if(opt_Selected.equals(ans)) {
                        //oSelectedCount = 0;
                        //send data to WorkBook
                        mCallback.quizDataCallback(0, finalScore);
                        //Toast.makeText(getActivity(), "dhcount" + oSelectedCount + "fs= "+finalScore, Toast.LENGTH_LONG).show();
                    }else{
                        //send data to WorkBook
                        mCallback.quizDataCallback(0, finalScore);
                    }
                }
            });
      // }
    }

    @Override
    public void onCheckedChanged(RadioGroup radiogrp, int i) {
        int radioButtonId = radiogrp.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radiogrp.findViewById(radioButtonId);
        //counting the number of times a person selects the correct ans on first try
        oSelectedCount++;
        opt_Selected = (String) radioButton.getText();
        if(oSelectedCount==1 && opt_Selected.equals(ans)){
            //make the continue button visible
            continu.setVisibility(View.VISIBLE);
            //underline the text
            radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            //disable the radiobutton
            radioButton.setEnabled(false);
            //award points to user for correct ans
            awardScore(oSelectedCount);
            //set background of a check_mark
            radioButton.setBackgroundResource(R.drawable.button_background1);
            //make the text bold and change the color
            radioButton.setTypeface(null, Typeface.BOLD);
            radioButton.setTextColor(Color.parseColor("#5FCC00"));

        }else if(opt_Selected.equals(ans)) {
            //current_score = score;
            //final_score = current_score + previous_score;
            continu.setVisibility(View.VISIBLE);
            //disable the radiobutton
            radioButton.setEnabled(false);
            //award points to user for correct ans
            awardScore(oSelectedCount);
            //set background of a check_mark
            radioButton.setBackgroundResource(R.drawable.button_background1);
            //making the text underlined
            radioButton.setPaintFlags(radioButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            radioButton.setTypeface(null, Typeface.BOLD);
            radioButton.setTextColor(Color.parseColor("#5FCC00"));
            Log.e(TAG, "Not a Direct Hit");
            //Toast.makeText(getActivity(), "Not a Direct Hit", Toast.LENGTH_SHORT).show();
        }else{
            //set color of wrong selected answer to red
            radioButton.setTextColor(Color.parseColor("#CC0000"));
            //disable the radiobutton
            radioButton.setEnabled(false);
        }
    }

    //set up the audio
    private void setUpAudio(){
       int audioId = getResources().getIdentifier(audio,
                "raw", WorkBookActivity.getInstance().getPackageName());
        //audioplayer.reset();
        if (audioplayer != null){
            if (audioplayer.isPlaying()||audioplayer.isLooping()) {
                audioplayer.stop();
            }
            audioplayer.release();
            audioplayer = null;
        }
        audioplayer = MediaPlayer.create(CategorySectionsActivity.getInstance().getApplicationContext(), audioId);
        //play audio
        audioplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    //method to award the score
    private void awardScore(int optionsSelectedCount){
        if (optionsSelectedCount == 1){
            finalScore = 5;
        }else if(optionsSelectedCount == 2){
            finalScore = 3;
        }else if(optionsSelectedCount == 3){
            finalScore = 2;
        }else if(optionsSelectedCount == 4){
            finalScore = 1;
        }else{
            finalScore = 1;
        }
        /*
        if (finalScore == 0){
            finalScore = 10;
        }else if (finalScore == 10){
            finalScore = 10;
        }
        */
    }
}
