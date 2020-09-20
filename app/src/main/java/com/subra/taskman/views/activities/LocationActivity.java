package com.subra.taskman.views.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;
import com.subra.taskman.R;
import com.subra.taskman.services.MyLocationReceiver;
import com.subra.taskman.services.MyNetworkReceiver;
import com.subra.taskman.session.SharedPefManager;
import com.subra.taskman.utils.GpsUtility;
import com.subra.taskman.utils.Utility;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 99;

    private ProgressDialog mProgress;
    private MyNetworkReceiver mNetworkReceiver;
    private MyLocationReceiver mLocationReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        /*if (SharedPefManager.getInstance(this).isLoggedIn()) {
            SharedPefManager.getInstance(this).setLoggedIn(true);
            startActivity(new Intent(LocationActivity.this, HomeActivity.class));
            finish();
        }*/

        mNetworkReceiver = new MyNetworkReceiver(this);
        mLocationReceiver = new MyLocationReceiver(this);

        //-----------------------------------------------| GPS/Location
        LocationManager mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mManager != null) {
            checkGpsEnabled(mManager);
        }

        requestPermissions();

        //-----------------------------------------------| Location Update
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        ((Button) findViewById(R.id.set_location)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = Utility.getInstance().showProgressDialog(LocationActivity.this, getResources().getString( R.string.progress), true);
                if (mLocation != null) {
                    //location.setLocation("23.751428,90.394264");
                    SharedPefManager.getInstance(LocationActivity.this).saveDeviceLocation(mLocation.getLatitude() + "," + mLocation.getLongitude());
                    startActivity(new Intent(LocationActivity.this, HomeActivity.class));
                } else {
                    Snackbar.make(LocationActivity.this.findViewById(android.R.id.content), "Please wait for a seconds to active location", Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)); //After Oreo version this code must be used
            registerReceiver(mLocationReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));  //After Oreo version this code must be used
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (mFusedLocationClient != null && mLocationRequest != null && mLocationCallback != null) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver); //After Oreo version this code must be used
            unregisterReceiver(mLocationReceiver); //After Oreo version this code must be used
            if (mFusedLocationClient != null && mLocationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //====================================| Explain why the app needs the request permissions
    //https://developers.google.com/maps/documentation/android-sdk/location
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION); //if there is no permission allowed then, display permission request dialog
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //For allow button
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
            } else {
                //For denied button
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                requestPermissions();
            }
        }
    }

    //===============================================| Trigger new location updates at interval
    private void startLocationUpdates() {
        mProgress = Utility.getInstance().showProgressDialog(this, getResources().getString( R.string.progress), false);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Utility.getInstance().dismissProgressDialog(mProgress);
                        mLocation = location;
                    }
                }
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); //long UPDATE_INTERVAL = 10 * 1000;  /* 5 secs */
        mLocationRequest.setFastestInterval(1000); //long FASTEST_INTERVAL = 2000; /* 2 sec */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
    }

    //===============================================| GPS/Location
    private void checkGpsEnabled(LocationManager manager) {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && GpsUtility.hasGPSDevice(this)) {
            GpsUtility.displayLocationSettingsRequest(new GpsUtility.GpsOnListenerCallBack() {
                @Override
                public void gpsResultCode(int resultCode) {
                    //REQUEST_CHECK_SETTINGS = resultCode;
                }
            }, this);
        } else {
            //checkPermissions();
        }
    }
}
