package alon.activityrecognition;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> , LocationListener {


    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private TextView mDetectedActivityTextView;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private int Confidence = 80;
    public int Interval = 5 * 1000;
    public int FastestInterval = 3 * 1000;
    private String CurrentActivity = "";

    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));

        mDetectedActivityTextView = (TextView) findViewById(R.id.detected_activities_textview);
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Interval);
        mLocationRequest.setFastestInterval(FastestInterval);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public String getDetectedActivity(int detectedActivityType) {
        Resources resources = this.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public void requestActivityUpdates(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "GoogleApiClient not yet connected", Toast.LENGTH_SHORT).show();
        } else {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, getActivityDetectionPendingIntent()).setResultCallback(this);
        }
    }

    public void removeActivityUpdates(View view) {
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent()).setResultCallback(this);
        mDetectedActivityTextView.setText(null);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivitiesIntentService.class);

        return PendingIntent.getService(this, 10 * 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(TAG, "Successfully added activity detection.");

        } else {
            Log.e(TAG, "Error: " + status.getStatusMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.STRING_ACTION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        Toast.makeText(this, "Updated: " + mLastUpdateTime, Toast.LENGTH_SHORT).show();
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(Constants.STRING_EXTRA);
            String activityString = "";
            for(DetectedActivity activity: detectedActivities){
                activityString +=  "Activity: " + getDetectedActivity(activity.getType()) + ", Confidence: " + activity.getConfidence() + "%\n";

                if (getDetectedActivity(activity.getType()).equals("Still") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("Still")){
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  MainActivity.this);
                    CurrentActivity = "Still";
                }
                else if(getDetectedActivity(activity.getType()).equals("Walking") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("Walking")){
                    CreteLocationUpdate(1000 * 60, 1000 * 50);
                    CurrentActivity = "Walking";
                }
                else if(getDetectedActivity(activity.getType()).equals("Running") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("Running")){
                    CreteLocationUpdate(1000 * 50, 1000 * 45);
                    CurrentActivity = "Running";
                }
                else if(getDetectedActivity(activity.getType()).equals("On a bicycle") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("On a bicycle")){
                    CreteLocationUpdate(1000 * 40, 1000 * 30);
                    CurrentActivity = "On a bicycle";
                }
                else if(getDetectedActivity(activity.getType()).equals("In a vehicle") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("In a vehicle")){
                    CreteLocationUpdate(1000 * 30, 1000 * 20);
                    CurrentActivity = "In a vehicle";
                }
                else if(getDetectedActivity(activity.getType()).equals("Unknown") && activity.getConfidence() >= Confidence && !CurrentActivity.equals("Unknown")){
                    CreteLocationUpdate(1000 * 90, 1000 * 90);
                    CurrentActivity = "Unknown";
                }
            }
            mDetectedActivityTextView.setText(activityString);
        }
    }

    private void CreteLocationUpdate(int Interval,int FastestInterval){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Interval);
        mLocationRequest.setFastestInterval(FastestInterval);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
    }
}


