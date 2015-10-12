package com.littlefluffytoys.FusedLocation;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FusedLocationInfo implements LocationInterface {
    private static final long serialVersionUID = 1L;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    public String lastProvider;
    private String Tag = "FusedLocationInfo";
    private int status;
    private FusedLocationHelper fusedLocation;
    static boolean isTag = false;
    private int isTagPopupShown = 0;
    private AlertDialog alertTag;
    private LocationInfo latestInfo;
    private Location location;
    private Context context;
    private boolean askForLocation;
    private CallActivityInterface callinterface;
    private Status mstatus;
    public static int location_accuracy = 0;
    boolean littleFlurryStatus = false;
    boolean sendStatus = true;

    // private boolean askforlocation = true;

    public FusedLocationInfo(Context context, final boolean askforlocation) {
        // TODO Auto-generated constructor stub
        this.context = context;
        fusedLocation = new FusedLocationHelper(context, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (location_accuracy != 0) {
                    fusedLocation.isLocationOn(askforlocation,
                            location_accuracy, true);
                } else {
                    fusedLocation.isLocationOn(askforlocation,
                            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, true);
                }
            }
        }).start();


    }

    public void locationUpdate(Location location) {
        // TODO Auto-generated method stub
        setLocation(location);


    }

    public void locationSetting(Status status) {
        this.mstatus = status;
        Log.d(Tag, "SendStatus1" + sendStatus + "");
        Log.e(Tag,"status"+mstatus.getStatusCode() + LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE);
            if (sendStatus && mstatus.getStatusCode()!=LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                sendStatus = false;

                callinterface = (CallActivityInterface) context;
                callinterface.locationSetting(mstatus, this.status);
            }

        // TODO Auto-generated method stub
        /*
         * try { //status.startResolutionForResult(this,
		 * REQUEST_CHECK_SETTINGS); } catch (SendIntentException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
    }

    public void isLocationOn(int status) {
        // TODO Auto-generated method stub
        Log.d("flag", status + "");
        Log.d(Tag, "IsLocationOn");
        if (status == 1) {

            // home_crownit.setText("Near Me");
            if (fusedLocation.isGoogleApiConnected()
                    && !fusedLocation.isLocationUpdateOn())
                fusedLocation.startLocationUpdates();
            if (sendStatus) {
                Log.d(Tag, "SendStatus2" + sendStatus + "");
                sendStatus = false;
                callinterface = (CallActivityInterface) context;
                callinterface.locationSetting(mstatus, 1);

            }
        } else if (status == 2) {
            sendStatus = true;

            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                isTag = false;
                isTagPopupShown = 0;
                if (alertTag != null) {
                    alertTag.cancel();
                }


                callinterface = (CallActivityInterface) context;
                callinterface.createAlertDialog();


                /*
                 * showAlertDialogEnableLocation("Alert",
				 * "To get tagged, Please enable Location Services.");
				 */
            } else {
                if (locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    if (sendStatus) {
                        Log.d(Tag, "SendStatus3" + sendStatus + "");
                        sendStatus = false;

                        callinterface = (CallActivityInterface) context;
                        callinterface.locationSetting(mstatus, 1);
                    }
                /*
				 * showAlertDialogEnableLocation("Alert",
				 * "To find nearby outlets, Please enable Location Services.");
				 */
                }
            }

            LocationLibrary.showDebugOutput(true);
            LocationLibrary.initialiseLibrary(context, "com.example.sample");

            latestInfo = new LocationInfo(context);
            if (latestInfo != null) {
                location = new Location("default");
                location.setLatitude(latestInfo.lastLat);
                location.setLongitude(latestInfo.lastLong);
                location.setAccuracy(latestInfo.lastAccuracy);
                location.setProvider(latestInfo.lastProvider);

                Log.d("littlefluffy",
                        "At Time: "
                                + String.valueOf(latestInfo.lastLocationUpdateTimestamp)
                                + "\n" + "Latitude: "
                                + String.valueOf(latestInfo.lastLat) + "\n"
                                + "Longitude: "
                                + String.valueOf(latestInfo.lastLong) + "\n"
                                + "Accuracy: "
                                + String.valueOf(latestInfo.lastAccuracy)
                                + "\n" + "Provider: "
                                + String.valueOf(latestInfo.lastProvider));

                if (isTag) {
                    isTag = false;
                    // continueTagProcess();
                }
            }
        }
    }

    public void setLocation(Location mlocation) {
        Log.d("lat", mlocation.getLatitude() + "");
        String x = String.valueOf(mlocation.getLatitude());
        String y = String.valueOf(mlocation.getLatitude());

        try {
            this.location = mlocation;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if (locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER) && sendStatus) {
            Log.d(Tag, "SendStatus4" + sendStatus + "");
            sendStatus = false;
            callinterface = (CallActivityInterface) context;
            callinterface.locationSetting(mstatus, 1);


        } else {

        }

        callinterface = (CallActivityInterface) context;
        callinterface.islocationChanged(location);

    }

    public Location getLocation() {
        if (location != null) {

            return this.location;
        }
        return null;
    }

    public Status getStatus() {

        return mstatus;

    }

	/*
	 * private void showAlertDialogEnableLocation(String title, String message)
	 * {
	 * 
	 * AlertDialog.Builder builder_locationCheck = new AlertDialog.Builder(
	 * context); // builder.setTitle(title); builder_locationCheck
	 * .setMessage(message) .setNegativeButton("Enable", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub startActivity(new Intent(
	 * android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); } })
	 * 
	 * .setPositiveButton("Close", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub dialog.dismiss(); sendToServer();
	 * askForLocation = false; } });
	 * 
	 * alertDialog_checkLocation = builder_locationCheck.create();
	 * alertDialog_checkLocation.show(); }
	 */


    public String getCityName(double lat, double lon, int flag) throws IOException {
        try {

            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                if (flag == 2)
                    return addresses.get(0).getAddressLine(1) + "," + addresses.get(0).getLocality();
                else
                    return addresses.get(0).getLocality();

            } else {
                return null;
            }

        } catch (Exception exc) {
            exc.printStackTrace();

            return null;
        }

    }

    public void stopLocationUpdates() {
        fusedLocation.stopLocationUpdates();
    }

    public void startLocationUpdates() {
        fusedLocation.startLocationUpdates();
    }

}
