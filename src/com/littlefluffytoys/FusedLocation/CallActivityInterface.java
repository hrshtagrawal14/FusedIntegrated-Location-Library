package com.littlefluffytoys.FusedLocation;

import android.location.Location;

import com.google.android.gms.common.api.Status;

public interface CallActivityInterface {
	abstract void islocationChanged(Location location);

	abstract void locationSetting(Status status,int locationOn);



	abstract void createAlertDialog();

}
