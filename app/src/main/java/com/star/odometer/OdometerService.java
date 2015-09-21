package com.star.odometer;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class OdometerService extends Service {

    private final IBinder mBinder = new OdometerBinder();

    private static double sDistanceInMeters;
    private static Location sLastLocation;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (sLastLocation == null) {
                    sLastLocation = location;
                } else {
                    sDistanceInMeters += location.distanceTo(sLastLocation);
                    sLastLocation = location;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000, 10, mLocationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }

    public class OdometerBinder extends Binder {
        public OdometerService getOdometerService() {
            return OdometerService.this;
        }
    }

    public double getKiloMeters() {
        return sDistanceInMeters / 1000;
    }
}
