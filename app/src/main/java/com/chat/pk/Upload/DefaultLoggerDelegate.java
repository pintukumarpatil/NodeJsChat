package com.chat.pk.Upload;

import android.util.Log;


/**
 * Created by //pintu kumar patil 9977638049 india on 5/12/15.
 */
public class DefaultLoggerDelegate implements Logger.LoggerDelegate {

    private static final String TAG = "UploadService";

    @Override
    public void error(String tag, String message) {
        Log.e(TAG, tag + " - " + message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        Log.e(TAG, tag + " - " + message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        Log.d(TAG, tag + " - " + message);
    }

    @Override
    public void info(String tag, String message) {
        Log.i(TAG, tag + " - " + message);
    }
}
