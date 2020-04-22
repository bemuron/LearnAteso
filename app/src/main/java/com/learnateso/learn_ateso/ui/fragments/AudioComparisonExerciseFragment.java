package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;
import com.learnateso.learn_ateso.ui.activities.CategorySectionsActivity;

/**
 * Created by BE on 2/25/2018.
 */

public class AudioComparisonExerciseFragment extends Fragment {
    private View view;
    AudioComparisonCallback mCallback;
    private TextView instructionstv, eng_word;
    private String englishWord, audio1,audio2, audio;
    private MediaPlayer audioplayer = null;
    private ImageView audio1_imageview, audio2_imageview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.audio_comparison_exercise, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            englishWord = bundle.getString("EngWord");
            audio1 = bundle.getString("audioCompare1");
            audio2 = bundle.getString("audioCompare2");
        }

        SetUpViewWidgets(view);
        audioComparisonExercise();
        return view;
    }

    // Container Activity must implement this interface
    public interface AudioComparisonCallback {
        public void audioComparisonData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (AudioComparisonExerciseFragment.AudioComparisonCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AudioComparisonCallback");
        }
    }

    private void SetUpViewWidgets(View view){
        instructionstv = view.findViewById(R.id.comparison_instructions);
        eng_word = view.findViewById(R.id.comparison_eng_word);
        audio1_imageview = view.findViewById(R.id.image_audio1);
        audio2_imageview = view.findViewById(R.id.image_audio2);

    }

    private void audioComparisonExercise(){
        eng_word.setText(englishWord);

        audio1_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAudio();
            }
        });

        audio2_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAudio();
            }
        });
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
}
