package com.george.assignment3cameragalleryapp;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


/**
 * Created by George on 2016-01-29.
 */
public class gps extends Service implements LocationListener {
    private final Context context;

    boolean booGpsEnab = false;
    boolean booNetEnab = false;
    boolean boogetLoc = false;

    Location location;

    double latitude;
    double longitude;

    private static final long minDistChange = 10;
    private static final long minTimeUpd = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public gps(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            booGpsEnab = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            booNetEnab = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!booGpsEnab && !booNetEnab) {

            } else {
                this.boogetLoc = true;

                if (booNetEnab) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            minTimeUpd,
                            minDistChange, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                if(booGpsEnab) {
                    if(location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                minTimeUpd,
                                minDistChange, this);

                        if(locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }


    @Override
    public void onLocationChanged(Location arg0) {
    }

    @Override
    public void onProviderDisabled(String arg0) {
    }

    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
