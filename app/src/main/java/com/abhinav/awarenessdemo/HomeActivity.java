package com.abhinav.awarenessdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by abhinav.sharma on 11/7/2016.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GoogleApiClient client = setUpGoogleClient(this);
        initiateSnapshots(client);
    }

    private void initiateSnapshots(GoogleApiClient client) {
        detectUserActivity(client);
        detectHeadfonesState(client);
    }

    private void detectHeadfonesState(GoogleApiClient googleApiClient) {
        Awareness.SnapshotApi.getHeadphoneState(googleApiClient).setResultCallback(new ResultCallbacks<HeadphoneStateResult>() {
            @Override
            public void onSuccess(@NonNull HeadphoneStateResult headphoneStateResult) {
                /*This callback is just providing the current state snapshot of the device*/

                Log.d(TAG, "onSuccess: ");
                Log.d(TAG, "Headphone state: " + headphoneStateResult.getHeadphoneState().toString());
                int state = headphoneStateResult.getHeadphoneState().getState();

                String str = (state == 1) ? "Yo Man play some music" : "Headphones unplugged";
                notifyUser(str);
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e(TAG, "onFailure: " + status.toString());
            }
        });
    }

    private void notifyUser(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void detectUserActivity(GoogleApiClient googleApiClient) {
        Awareness.SnapshotApi.getDetectedActivity(googleApiClient).setResultCallback(new ResultCallbacks<DetectedActivityResult>() {
            @Override
            public void onSuccess(@NonNull DetectedActivityResult detectedActivityResult) {
                Log.d(TAG, "onSuccess: The probable Activities are : ");
                ActivityRecognitionResult activityRecognitionResult = detectedActivityResult.getActivityRecognitionResult();
                List<DetectedActivity> detectedActivities = activityRecognitionResult.getProbableActivities();
                for (DetectedActivity a : detectedActivities) {
                    System.out.println(" ## " + a.toString());
                }

                Log.d(TAG, "The Most probable activity for user is : " + activityRecognitionResult.getMostProbableActivity().toString());
                Log.d(TAG, "Elapsed Time is : " + activityRecognitionResult.getElapsedRealtimeMillis());

            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e(TAG, "onFailure: " + status.toString());
            }
        });
    }

    private GoogleApiClient setUpGoogleClient(Context context) {
        GoogleApiClient client = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        client.connect();
        return client;
    }
}
