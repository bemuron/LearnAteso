package com.learnateso.learn_ateso.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.learnateso.learn_ateso.app.AppLog;
import com.learnateso.learn_ateso.R;
import com.learnateso.learn_ateso.ui.activities.WorkBookActivity;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by BE on 2/25/2018.
 */

public class VoiceRecordingExerciseFragment extends Fragment {
    private View view;
    VoiceRecordingCallback mCallback;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FILE_EXT_AMR = ".amr";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private TextView recordTimeText;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Timer timer;

    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP,
            MediaRecorder.OutputFormat.AMR_NB};
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP,
            AUDIO_RECORDER_FILE_EXT_AMR };

    private TextView instructionstv, eng_word, ateso_word;
    private String englishWord,atesoWord, atesoWordAudio,filepath, AudioSavePathInDevice = null;
    private File file;
    private MediaPlayer audioplayer = null, recordedAudio = null;
    private ImageView ateso_audio_icon;
    private Button btn_continue, voice_record_icon, delete_audio_icon, play_audio_icon;
    private boolean isActionDown = false, isPrepared, isRecording;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.voice_recording_exercise, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {

            englishWord = bundle.getString("EngWord");
            atesoWord = bundle.getString("AtesoWord");
            atesoWordAudio = bundle.getString("AtesoWordAudio");
        }

        SetUpViewWidgets(view);

        //audio storage
        AudioSavePathInDevice = Environment.getExternalStorageDirectory().
                getAbsolutePath() + AUDIO_RECORDER_FOLDER + "/" + atesoWord + "_user_recorded " + AUDIO_RECORDER_FILE_EXT_AMR;

        //getFilename();

        voiceRecordExercise();
        return view;
    }

    // Container Activity must implement this interface
    public interface VoiceRecordingCallback {
        public void voiceRecordingData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (VoiceRecordingExerciseFragment.VoiceRecordingCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement VoiceRecordingCallback");
        }
    }

    private void SetUpViewWidgets(View view){
        instructionstv = view.findViewById(R.id.voice_recording_instruction);
        ateso_word = view.findViewById(R.id.ateso_word);
        eng_word = view.findViewById(R.id.eng_translation);
        ateso_audio_icon = view.findViewById(R.id.audio_icon);
        voice_record_icon = view.findViewById(R.id.voice_record_icon);
        delete_audio_icon = view.findViewById(R.id.delete_recorded_icon);
        play_audio_icon = view.findViewById(R.id.play_recorded_icon);
        btn_continue = view.findViewById(R.id.button_continue);
        recordTimeText = view.findViewById(R.id.timer);

        //play_audio_icon.setEnabled(false);
        //delete_audio_icon.setEnabled(false);

    }

    private void voiceRecordExercise(){
        eng_word.setText(englishWord);
        ateso_word.setText(atesoWord);

        ateso_audio_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpAudio();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.voiceRecordingData();
            }
        });

        //recordBtnClickListener();
        recordAudio();
        playRecordedAudio();
        deleteRecordedAudio();
    }

    private void recordBtnClickListener(){
        voice_record_icon.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){

                isActionDown = true;
                try{
                    if (isActionDown){
                        initRecorder();
                        if(isActionDown){
                            prepareRecorder();
                        }
                    }
                    if (isPrepared && isActionDown){
                        startRecording();
                        isRecording = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AppLog.errorlogString("OnLongClickError: "+ e.toString());
                }

                return true;
            }
        });

    }

    private void recordAudio(){
        voice_record_icon.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        initRecorder();
                        try{
                            prepareRecorder();
                            startRecording();
                            AppLog.logString("start recording");
                        }catch (Exception e){
                            e.printStackTrace();
                            AppLog.errorlogString("ActionDownError: "+ e.toString());
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        try{
                            stopRecording();
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        play_audio_icon.setEnabled(true);
                        delete_audio_icon.setEnabled(true);
                        break;
                }

                return false;
            }
        });

    }

    //set up the audio
    private void setUpAudio(){
        int audioId = getResources().getIdentifier(atesoWordAudio,
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

    //play recorded audio
    private void setUpRecordedAudio(){
        if (recordedAudio != null){
            if (recordedAudio.isPlaying()||recordedAudio.isLooping()) {
                recordedAudio.stop();
            }
            recordedAudio.release();
            recordedAudio = null;
        }
        recordedAudio = new MediaPlayer();
        try {
            recordedAudio.setDataSource(getFilename());
            recordedAudio.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
        recordedAudio.start();

    }


    //get filename where audio will be recorded
    private String getFilename(){
        filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }

        //AUDIO_RECORDER_FILE_EXT_MP4
        //file_exts[currentFormat]
        return (file.getAbsolutePath() + "/" + atesoWord + "_user_recorded " + AUDIO_RECORDER_FILE_EXT_AMR);
    }

    //initialise the recorder
    private void initRecorder(){
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //MediaRecorder.OutputFormat.MPEG_4
        //recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());

        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
    }

    //prepare the recorder
    private void prepareRecorder(){
        try {
            recorder.prepare();
        }catch (IllegalStateException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //start recording
    private void startRecording(){

        try {
            recorder.start();
            startTime = SystemClock.uptimeMillis();
            timer = new Timer();
            MyTimerTask myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, 1000, 1000);
            vibrate();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    //stop recording
    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
            //recorder.reset();
            recorder.release();
            //recorder = null;
            initRecorder();

            if (timer != null) {
                timer.cancel();
            }
            /*
            if (recordTimeText.getText().toString().equals("00:00")) {
                return;
            }
            */
            //recordTimeText.setText("00:00");
            vibrate();
        }
    }

    //method to play the recorded audio if available
    private void playRecordedAudio(){
        play_audio_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //voice_record_icon.setEnabled(false);
                //delete_audio_icon.setEnabled(false);
                if (getFilename() == null){
                    Toast.makeText(getActivity(), "Tap and hold to record audio", Toast.LENGTH_SHORT).show();
                }else if (getFilename() != null){
                    setUpRecordedAudio();
                }
            }
        });
    }

    //method to delete the recorded audio if available
    private void deleteRecordedAudio(){
        delete_audio_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //voice_record_icon.setEnabled(true);
                //play_audio_icon.setEnabled(false);
                File file = new File(getFilename());
                    try {
                        //if (file.exists()){
                            if (file.delete())
                            Toast.makeText(getActivity(), "Audio Deleted", Toast.LENGTH_SHORT).show();
                        recordTimeText.setText("00:00");
                        //}
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }
        });
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    //method to handle the vibration
    private void vibrate() {
        // TODO Auto-generated method stub
        try {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(Locale.US,
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }

}
