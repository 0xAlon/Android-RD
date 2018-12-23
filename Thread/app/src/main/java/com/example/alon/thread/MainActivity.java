package com.example.alon.thread;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Thread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a thread and start it running
        CustomThread thread = new CustomThread();
        thread.start();

        // Create a thread and start it running
        Thread runnable = new Thread(new CustomRunnable());
        runnable.start();

        Log.d(TAG, "MainActivity");
    }

    public class CustomThread extends Thread {

        @Override
        public void run() {
            Log.d(TAG, "CustomThread");
        }
    }

    /*
        The other way to create a thread is to declare a class that implements the Runnable interface.
        That class then implements the run method.
        An instance of the class can then be allocated, passed as an argument when creating Thread, and started.
     */
    public class CustomRunnable implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "CustomRunnable");
        }
    }
}
