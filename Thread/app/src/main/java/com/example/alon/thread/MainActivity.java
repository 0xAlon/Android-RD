package com.example.alon.thread;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Thread";
    public Handler handler;
    public ProgressBar progressBar;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        // Create a thread and start it running
        CustomThread thread = new CustomThread();
        thread.start();

        /*
            The Runnable interface should be implemented by any class whose instances are intended to be executed by a thread.
            The class must define a method of no arguments called run.
         */
        // Create a thread and start it running
        Thread runnableThread;
        runnableThread = new Thread(new CustomRunnable());
        runnableThread.start();

        Executor executor = new anExecutor(); // Object that executes submitted Runnable tasks
        executor.execute(new CustomRunnable());
        executor.execute(new CustomRunnable2());

        // Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available.
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Log.d(TAG, String.valueOf(threadPoolExecutor.getPoolSize()));
        threadPoolExecutor.execute(new CustomRunnable());
        Log.d(TAG, String.valueOf(threadPoolExecutor.getPoolSize()));


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setProgress(msg.arg1);
                if (msg.arg1 == 99) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Succeed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };

        runnableThread = new Thread(new CustomRunnable3());
        runnableThread.start();

        //CustomThread1 thread1 = new CustomThread1();
        //thread1.start();

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

    public class CustomRunnable3 implements Runnable {
        @Override
        public void run() {

            for (int i = 0; i < 100; i++) {

                Message msg = Message.obtain(); // Creates an new Message instance
                msg.arg1 = i;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class CustomThread1 extends Thread {

        @SuppressLint("HandlerLeak")
        public void run() {
            Looper.prepare();

            handler = new Handler() {
                public void handleMessage(Message msg) {
                    Log.d(TAG, String.valueOf(msg.arg1));

                    Message msg1 = Message.obtain(); // Creates an new Message instance
                    msg1.arg1 = msg.arg1;
                    handler.sendMessage(msg1);
                }
            };

            Looper.loop();
        }
    }
}
