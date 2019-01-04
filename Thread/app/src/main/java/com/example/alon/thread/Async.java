package com.example.alon.thread;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class Async extends AsyncTask<Integer, Integer, String> {

    private final String TAG = "Async";

    @SuppressLint("StaticFieldLeak")
    private TextView textView;

    public Async(TextView textView) {
        this.textView = textView;
    }

    /*
        This method contains the code which needs to be executed in background.
        In this method we can send results multiple times to the UI thread by publishProgress() method.
     */
    @Override
    protected String doInBackground(Integer... integers) {
        for (Integer integer : integers) {
            Log.d(TAG, integer.toString());
            publishProgress(integer); // Call onProgressUpdate
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(integers.length);
    }

    // This method contains the code which is executed before the background processing starts.
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute"); // Debug
        textView.setText("0");

    }

    /*
        This method receives progress updates from doInBackground method,
         which is published via publishProgress method, and this method can use this progress update to update the UI thread
     */
    @SuppressLint("SetTextI18n")
    protected void onProgressUpdate(Integer... progress) {
        textView.setText(progress[0].toString());
    }

    // This method is called after doInBackground method completes processing. Result from doInBackground is passed to this method.
    protected void onPostExecute(String result) {
        Log.d(TAG, "valueOf integers = " + result);
    }

}
