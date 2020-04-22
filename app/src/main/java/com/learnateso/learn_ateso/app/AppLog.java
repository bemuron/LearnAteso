package com.learnateso.learn_ateso.app;

/**
 * Created by BE on 2/26/2018.
 */

import android.util.Log;

public class AppLog {
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }

    public static int errorlogString(String message){
        return Log.e(APP_TAG,message);
    }
}
