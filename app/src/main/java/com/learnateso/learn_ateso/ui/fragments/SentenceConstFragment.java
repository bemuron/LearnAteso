package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;
import com.learnateso.learn_ateso.utilities.MyKeyboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Emo on 4/7/2017.
 */

public class SentenceConstFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SentenceConstFragment.class.getSimpleName();
    private EditText textInput;
    SentenceConstListener sentenceConstCallback;
    private Button continu, buttonDelete, buttonAudio;
    private TextView textToTranslate;
    private MyKeyboard keyboard;
    private String phrase, phraseAns,hint, audio;
    private LinearLayout parentLayout;
    private MediaPlayer audioplayer = null;
    private String [] wordsArray;
    private int directHitCount=0, finalScore = 0;
    SparseArray<String> keyValues = new SparseArray<>();
    // Our communication link to the EditText
    private InputConnection inputConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sentence_const_exercise,container,false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            phrase = bundle.getString("phrase");
            phraseAns = bundle.getString("phraseAns");
            audio = bundle.getString("audio");
        }

        setUpViewWidgets(view);
        configKeyboard();
        sentenceConstructionExercise();
        initKeyboard();
        return view;
    }

    // Container Activity must implement this interface
    public interface SentenceConstListener {
        public void sentenceConstData(int directHit, int score);
    }

    @Override
    public void onAttach(Context context) { //Try Context context as the parameter. It is not deprecated
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            sentenceConstCallback = (SentenceConstListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SentenceConstListener");
        }
    }

    private void setUpViewWidgets(View view){
        textToTranslate = view.findViewById(R.id.eng_ateso_phrase);
        textToTranslate.setText(phrase);
        textToTranslate.setTypeface(null, Typeface.BOLD);

        continu = view.findViewById(R.id.continu);
        continu.setVisibility(View.INVISIBLE);

        textInput = view.findViewById(R.id.editText2);
        textInput.setTextSize(20);

        buttonAudio = view.findViewById(R.id.audio_btn);
        parentLayout = view.findViewById(R.id.kb_parent_layout);
        keyboard = view.findViewById(R.id.keyboard);
    }

    private void initKeyboard() {

        wordsArray = phraseAns.split(" ");

        List<String> stringList = Arrays.asList(wordsArray);
        Collections.shuffle(stringList);
        wordsArray = stringList.toArray(new String[stringList.size()]);

        //instatiate the layouts/rows for the words
        LinearLayout row1,row2,rowDel;
        row1 = new LinearLayout(getActivity());
        row2 = new LinearLayout(getActivity());
        rowDel = new LinearLayout(getActivity());

            for (int j = 0; j < wordsArray.length; j++) {
                Button button = new Button(getActivity());
                //a maximum of 3 words per row
                if (j <= 3 ) {
                    row1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row1.setOrientation(LinearLayout.HORIZONTAL);

                    button.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    button.setText(wordsArray[j]);
                    button.setId((j + 1));
                    keyValues.put(button.getId(), wordsArray[j]);
                    button.setOnClickListener(this);
                    row1.addView(button);
                }else if (j >=4 && j< 7){
                    row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    row2.setOrientation(LinearLayout.HORIZONTAL);

                    button.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    button.setText(wordsArray[j]);
                    button.setId((j + 1));
                    keyValues.put(button.getId(), wordsArray[j]);
                    button.setOnClickListener(this);
                    row2.addView(button);
                }
            }

            rowDel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rowDel.setOrientation(LinearLayout.HORIZONTAL);

            buttonDelete = new Button(getActivity());
            buttonDelete.setId(wordsArray.length + 1);
            buttonDelete.setText("<=");
            buttonDelete.setOnClickListener(this);
            rowDel.addView(buttonDelete);
            parentLayout.addView(row1);
        parentLayout.addView(row2);
        parentLayout.addView(rowDel);
    }

    //configuring the keyboard
    private void configKeyboard(){
        // prevent system keyboard from appearing when EditText is tapped
        textInput.setRawInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setTextIsSelectable(true);

        // pass the InputConnection from the EditText to the keyboard
        inputConnection = textInput.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(inputConnection);

    }

    private void sentenceConstructionExercise() {

        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check what the user has entered
                checkAnswer();

            }
        });

        //handle the click on the audio button
        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play the audio
                setUpAudio();

            }
        });
    }

    //check if the user has input the right answer
    //check if its a direct hit i.e the correct answer on the first try
    private void checkAnswer(){
        directHitCount++;
        String content = textInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            textInput.setError("come on, you can do this...");
        } else if (directHitCount == 1 && content.equals(phraseAns)){
            //award the user the points
            awardScore(directHitCount);
            //send data to WorkBook
            sentenceConstCallback.sentenceConstData(directHitCount, finalScore);
        } else if (content.equals(phraseAns)){
            //award the user the points
            awardScore(directHitCount);
            //send data to WorkBook
            sentenceConstCallback.sentenceConstData(0, finalScore);
        }else{
            textInput.setError("I know you can do this, try again");
        }
    }

    @Override
    public void onClick(View v) {
        //make continue button visible
        continu.setVisibility(View.VISIBLE);
        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == buttonDelete.getId()) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            //display clicked button text on edit text field
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value+" ", 1);
        }
    }

    //method to handle the audio
    //set up the audio
    private void setUpAudio(){
        int audioId = getResources().getIdentifier(audio,
                "raw", WorkBookActivity.getInstance().getPackageName());

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
