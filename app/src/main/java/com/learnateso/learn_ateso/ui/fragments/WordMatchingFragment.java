package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.learnateso.learn_ateso.helpers.FingerLineDraw;
import com.learnateso.learn_ateso.R;

/**
 * Created by Emo on 4/7/2017.
 */

public class WordMatchingFragment extends Fragment {
    private View view;
    FingerLineDraw fingerLineDraw;
    WordMatchingListener wordMatchingCallback;
    private ImageView imageView;
    private Bitmap bitmap;
    Button continu;
    TextView textToTranslate, hint;
    String atesoPhrase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fingerLineDraw = new FingerLineDraw(getActivity(),null);

        Display currentDisplay = getActivity().getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();

        view = inflater.inflate(R.layout.word_matching_exercise, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            atesoPhrase = bundle.getString("AtesoPhrase");
        }

        wordMatchingExercise();
        return view;
    }

    // Container Activity must implement this interface
    public interface WordMatchingListener {
        public void wordMatchingData();
    }

    @Override
    public void onAttach(Context activity) { //Try Context context as the parameter. It is not deprecated
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            wordMatchingCallback = (WordMatchingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WordMatchingListener");
        }
    }

    private void wordMatchingExercise() {
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
                //send data to WorkBookActivity
                wordMatchingCallback.wordMatchingData();
            }
        });
    }
}
