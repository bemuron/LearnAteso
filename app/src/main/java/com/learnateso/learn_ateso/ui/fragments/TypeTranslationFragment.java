package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.learnateso.learn_ateso.R;

/**
 * Created by Emo on 4/3/2017.
 */

public class TypeTranslationFragment extends Fragment {
    View view;
    EditText textInput;
    TypeTranslationListener typeTranslationCallback;
    Button continu;
    TextView textToTranslate, hint;
    String atesoPhrase;
    Cursor c;
    RadioButton opt_1;
    RadioGroup radgrp;
    int max, counter = 0, score, previousPosition, currentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.type_translation_exercise,container,false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            atesoPhrase = bundle.getString("AtesoPhrase");
        }

        typeTranslationExercise();
        return view;
    }
    // Container Activity must implement this interface
    public interface TypeTranslationListener {
        public void typeTranslationData();
    }

    @Override
    public void onAttach(Context context) { //Try Context context as the parameter. It is not deprecated
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            typeTranslationCallback = (TypeTranslationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TypeTranslationListener");
        }
    }

    private void typeTranslationExercise() {
        //type translation stuff
        //if (c.moveToPosition(currentPosition)) {
            textToTranslate = (TextView) view.findViewById(R.id.eng_ateso_phrase);
            textToTranslate.setText(atesoPhrase);
            textToTranslate.setTypeface(null, Typeface.BOLD);
        //}
        continu = (Button) view.findViewById(R.id.continu);
        //continu.setVisibility(View.INVISIBLE);
        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //previous_score = final_score;
                //oSelectedCount =0;
                //counter++;
                //continu.setVisibility(View.INVISIBLE);
                //send data to WorkBook
                typeTranslationCallback.typeTranslationData();
            }
        });
    }
}
