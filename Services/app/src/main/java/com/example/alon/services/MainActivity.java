package com.example.alon.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private final String TAG = "MainActivity";
    private MyService myService;
    boolean mIsConnected;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, MyService.class);
        startService(intent);

        /*
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(10000);
                    Log.d(TAG, "MyService Time-out 10000ms"); // Debug
                    stopService(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        */
    }

    @Override
    public void onStart() {
        super.onStart();
        //Intent intent = new Intent(this, MyService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(this /* ServiceConnection */);
        myService = null;
        mIsConnected = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mIsConnected = true;
        MyService.MyBinder binder = (MyService.MyBinder) service;
        myService = binder.getService();

        Log.d(TAG, myService.succeed()); // Debug
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mIsConnected = false;
        myService = null;
    }
}
