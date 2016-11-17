package com.abhinav.awarenessdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.List;

/**
 * Created by abhinav.sharma on 11/7/2016.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int LOCATION_REQ_CODE = 27;
    private TextView tvWeather, tvPlaces, tvLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpUI();
        GoogleApiClient client = setUpGoogleClient(this);
        initiateSnapshots(client);
    }

    private void setUpUI() {
        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvPlaces = (TextView) findViewById(R.id.tv_places);
        tvLocation = (TextView) findViewById(R.id.tv_location);

    }

    private void initiateSnapshots(GoogleApiClient client) {
        detectUserActivity(client);
        detectHeadfonesState(client);
        detectLocation(client);
        detectPlaces(client);
        detectWeather(client);
    }

    private void detectWeather(GoogleApiClient googleApiClient) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermissions();
            return;
        }
        Awareness.SnapshotApi.getWeather(googleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
            @Override
            public void onResult(@NonNull WeatherResult weatherResult) {
                if (weatherResult.getStatus().isSuccess()) {
                    Log.d(TAG, "onResult: Weather ");
                    Log.d(TAG, "" + weatherResult.getWeather().toString());
                    String weathers = "Temp - " + weatherResult.getWeather().getTemperature(2) + "\n";
                    weathers += "Humidity - " + weatherResult.getWeather().getHumidity() + "\n";
                    weathers += "Dew - " + weatherResult.getWeather().getDewPoint(2);

                    tvWeather.setText(weathers);
                }
            }
        });
    }

    private void detectPlaces(GoogleApiClient googleApiClient) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestLocationPermissions();
            return;
        }
        Awareness.SnapshotApi.getPlaces(googleApiClient).setResultCallback(new ResultCallback<PlacesResult>() {
            @Override
            public void onResult(@NonNull PlacesResult placesResult) {
                Log.d(TAG, "onResult: ");
                List<PlaceLikelihood> likelihoods = placesResult.getPlaceLikelihoods();
                if (likelihoods == null) {
                    tvPlaces.setText("No Data Found :(");
                    return;
                }
                String s = "";
                for (PlaceLikelihood p : likelihoods) {
                    s += ("# Places API # Likelihood of the place is " + p.getLikelihood());
                    Place place = p.getPlace();
                    s += ("# Places API # Id " + place.getId());
                    s += ("# Places API # Name " + place.getName());
                    s += ("# Places API # Address " + place.getAddress());
                    s += ("# Places API # Attributions " + place.getAttributions());
                    s += ("# Places API # Place Locale " + place.getLocale());
                    s += ("# Places API # Place Price Level " + place.getPriceLevel());
                    s += ("# Places API # Place Rating " + place.getRating());
                    if (place.getPlaceTypes() == null) {
                        return;
                    }
                    for (Integer i : place.getPlaceTypes()) {
                        s += ("    ## Place Type : " + i);
                    }
                }
                tvPlaces.setText(s);
            }
        });
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
    }

    private void detectLocation(GoogleApiClient googleApiClient) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            requestLocationPermissions();
            return;
        }
        Awareness.SnapshotApi.getLocation(googleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                Log.d(TAG, "onResult: " + locationResult.getLocation().toString());
                String s = "";
                if (!locationResult.getStatus().isSuccess()) {
                    return;
                }

                s += "Provider - " + locationResult.getLocation().getProvider() + "\n";
                s += "Latitude - " + locationResult.getLocation().getLatitude() + "\n";
                s += "Longitude - " + locationResult.getLocation().getLongitude() + "\n";
                s += "Altitude - " + locationResult.getLocation().getAltitude() + "\n";
                s += "Accuracy - " + locationResult.getLocation().getAccuracy() + "\n";
                String temp = (locationResult.getLocation().getExtras()!=null)?
                        locationResult.getLocation().getExtras().toString() : "No Data in Extras :(";
                s += "Extras - " + temp + "\n";

                tvLocation.setText(s);

            }
        });
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
