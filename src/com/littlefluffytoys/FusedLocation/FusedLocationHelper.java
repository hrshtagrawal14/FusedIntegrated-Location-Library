package com.littlefluffytoys.FusedLocation;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class FusedLocationHelper implements LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private Context context;
    private LocationInterface locationInterface;
    private boolean mRequestingLocationUpdates = false;
    private FusedLocationInfo fusedlocationinfo;
    private CallActivityInterface callactivityinterface;
    private boolean littleflurryflag = false;

    public void createLocationRequest(int priority) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(priority);
    }

    public FusedLocationHelper(Activity activity) {
        this.context = (Activity) activity;
        // isLocationOn(askForLocationService,priority,true);

    }

    public FusedLocationHelper(Context context,
                               FusedLocationInfo fusedlocationinfo) {
        this.context = context;
        this.fusedlocationinfo = fusedlocationinfo;
        // isLocationOn(askForLocationService,priority,true);

    }

    public void isLocationOn(final boolean askForLocationService, int priority,
                             final boolean isStartPeriodicUpdate) {

        Log.e("FusedLocationHelper", "isLocationOn");
        createLocationRequest(priority);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        if (!isGoogleApiConnected())
            mGoogleApiClient.connect();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                result.getLocationSettingsStates();
                int flag = 0;
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        if (isGoogleApiConnected() && !isLocationUpdateOn()
                                && isStartPeriodicUpdate)
                            startLocationUpdates();
                        flag = 1;
                        locationInterface = (LocationInterface) fusedlocationinfo;
                        locationInterface.locationSetting(status);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (isGoogleApiConnected() && !isLocationUpdateOn()
                                    && isStartPeriodicUpdate)
                                startLocationUpdates();
                            locationInterface = (LocationInterface) fusedlocationinfo;
                            locationInterface.locationSetting(status);
                            flag = 2;
                        } else if (askForLocationService) {
                            locationInterface = (LocationInterface) fusedlocationinfo;
                        /*
						 * locationInterface = (LocationInterface)
						 * fusedlocationinfo;
						 * locationInterface.locationSetting(status);
						 */
                            locationInterface.locationSetting(status);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no
                        // way to fix the
                        // settings so we won't show the dialog.
                        littleflurryflag = true;
                        if (isGoogleApiConnected() && !isLocationUpdateOn()
                                && isStartPeriodicUpdate)
                            startLocationUpdates();
                        locationInterface = (LocationInterface) fusedlocationinfo;
                        locationInterface.locationSetting(status);
                        flag = 2;
                        break;
                }
                locationInterface = (LocationInterface) fusedlocationinfo;
                locationInterface.isLocationOn(flag);
            }
        });
    }

    public boolean isLocationUpdateOn() {
        Log.d(TAG,
                "mRequestingLocationUpdates ..."
                        + String.valueOf(mRequestingLocationUpdates));
        return mRequestingLocationUpdates;
    }

    public void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        // if(isGoogleApiConnected())
        try {
            if (isGoogleApiConnected() && !littleflurryflag) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
                littleflurryflag = true;
                Log.d(TAG, "Location update stopped .......................");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public void startLocationUpdates() {
        mRequestingLocationUpdates = true;
        if (isGoogleApiConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        Log.d(TAG, "Location update started ..............: ");
    }

    public Location newLocation() {
        return mCurrentLocation;
    }

    public void disconnectGoogleApi() {
        if (isLocationUpdateOn()) {
            stopLocationUpdates();
        }
        if (isGoogleApiConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isGoogleApiConnected() {
        try {
            return mGoogleApiClient.isConnected();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }

    }

    public Location lastKnownLocation() {
        return LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }


    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onConnected - isConnected ...............: "
                + mGoogleApiClient.isConnected());
    }

    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Log.d(TAG,
                "Firing onLocationChanged..............................................");
        if (location != null
                && mCurrentLocation != null
                && context.getClass().getSimpleName()
                .equalsIgnoreCase("AllActivity")
                && (mCurrentLocation.getAccuracy() - location.getAccuracy() >= 50)) {
            // Todo
            // ((AllActivity) context).onAccuracyChanged();
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        String lat = String.valueOf(mCurrentLocation.getLatitude());
        String lng = String.valueOf(mCurrentLocation.getLongitude());
        Log.d("fused location", "At Time: " + mLastUpdateTime + "\n"
                + "Latitude: " + lat + "\n" + "Longitude: " + lng + "\n"
                + "Accuracy: " + mCurrentLocation.getAccuracy() + "\n"
                + "Provider: " + mCurrentLocation.getProvider());
        locationInterface = (LocationInterface) fusedlocationinfo;
        locationInterface.locationUpdate(location);
        if (location == null) {
            location = new Location("default");
            location.setLatitude(0.0);
            location.setLongitude(0.0);
        }
        if (age_ms(location) < 120) {
            locationInterface = (LocationInterface) fusedlocationinfo;
            locationInterface.locationUpdate(location);
        } else
            Log.e("Fused Location", "old location");
    }

    public long age_ms(Location last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return age_ms_api_17(last);
        return age_ms_api_pre_17(last);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private long age_ms_api_17(Location last) {
        return (SystemClock.elapsedRealtimeNanos() - last
                .getElapsedRealtimeNanos()) / 1000000000;
    }

    private long age_ms_api_pre_17(Location last) {
        return (System.currentTimeMillis() - last.getTime()) / 1000;
    }

}
