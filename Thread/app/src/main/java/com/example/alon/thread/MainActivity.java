package com.example.alon.thread;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Thread";
    private Executor mainThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a thread and start it running
        CustomThread thread = new CustomThread();
        thread.start();

        /*
            The Runnable interface should be implemented by any class whose instances are intended to be executed by a thread.
            The class must define a method of no arguments called run.
         */
            // Create a thread and start it running
        Thread runnableThread = new Thread(new CustomRunnable());
        runnableThread.start();

        Executor executor = new anExecutor(); // Object that executes submitted Runnable tasks
        executor.execute(new CustomRunnable());
        executor.execute(new CustomRunnable2());

        // Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available.
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Log.d(TAG, String.valueOf(threadPoolExecutor.getPoolSize()));
        threadPoolExecutor.execute(new CustomRunnable());
        Log.d(TAG, String.valueOf(threadPoolExecutor.getPoolSize()));

    }

    class anExecutor implements Executor {
        public void execute(Runnable task) {
            task.run();
        }
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

    public class CustomRunnable2 implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "CustomRunnable2");
        }
    }
}
