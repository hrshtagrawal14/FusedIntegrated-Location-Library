package com.littlefluffytoys.FusedLocation;

import android.location.Location;
import com.google.android.gms.common.api.Status;

public interface LocationInterface {

	abstract void locationUpdate(Location location);

	abstract void isLocationOn(int status);
	abstract  void locationSetting(Status status);

}
