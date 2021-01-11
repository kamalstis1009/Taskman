package com.subra.taskman.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.subra.taskman.R;

public class MyLocationReceiver extends BroadcastReceiver {

    /*
    Help Link:
    ===========
    https://stackoverflow.com/questions/47216479/android-check-if-user-turned-off-location
    */

    private static final String TAG = "MyLocationReceiver";
    private Snackbar mSnackBar;
    private LocationListener mListener;

    public interface LocationListener {
        void onLocation(boolean isEnabled);
    }

    public MyLocationReceiver(Context context) {
        this.mSnackBar = Snackbar.make(((Activity)context).findViewById(android.R.id.content), context.getString(R.string.msg_location_unavailable), Snackbar.LENGTH_INDEFINITE);
    }

    public MyLocationReceiver(Context context, LocationListener listener){
        this.mListener = listener;
        this.mSnackBar = Snackbar.make(((Activity)context).findViewById(android.R.id.content), context.getString(R.string.msg_location_unavailable), Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (gpsEnabled && networkEnabled) {
                    if (mSnackBar != null) {
                        mSnackBar.dismiss();
                    }
                    Log.d(TAG, "GPS is enabled");
                    mListener.onLocation(true);
                } else {
                    if (mSnackBar != null) {
                        mSnackBar.show();
                    }
                    Log.d(TAG, "GPS is disabled");
                    if (mListener != null) {
                        mListener.onLocation(false);
                    }
                }
            }
        }
    }

}
