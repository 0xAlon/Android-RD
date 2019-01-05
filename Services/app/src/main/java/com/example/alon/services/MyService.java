package com.example.alon.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    boolean mAllowRebind; // indicates whether onRebind should be used
    MyBinder mBinder = new MyBinder(); // interface for clients that bind
    private final String TAG = "MyService";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        // The service is being created
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // The service is starting, due to a call to startService()
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // A client is binding to the service
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // The service is no longer used and is being destroyed
    }

    public class MyBinder extends Binder {
        MyService getService() {
            // Return this instance of MyBinder so clients can call public methods
            return MyService.this;
        }
    }

    public String succeed() {
        return "succeed";
    }

}
