package com.example.user.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by user on 01-04-2018.
 */

public class Locator {

    private static final String TAG = "Locator";
    private static final int MY_PERM_REQUEST_CODE = 1111;

    private MainActivity owner;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity mainActivity) {
        Log.d(TAG, "Locator: In Constructor");
        owner = mainActivity;

        boolean havePermission = checkPermission();
        if(havePermission) {
            setUpLocationManager();
            determineLocation();    //findLocation();
        }
    }

    //Initializes the location manager object
    public void setUpLocationManager()
    {
        //Means the location manager is already setup
        if(locationManager != null)
            return;

        //If the permission is not granted then no need to proceed further to setup the location manger, just return
        if(!checkPermission())
            return;

        //Get the system's Location Manager
        //The locationManger object gets set here. LOCATION_SERVICE is the existing variable.
        locationManager = (LocationManager) owner.getSystemService(LOCATION_SERVICE);

        //If want continuous location updates then add the locationListener object and specify the methods here
        //Define a listener that responds toi the location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //called when a new location is found by the network location provider
                owner.doLocationWork(location.getLatitude(), location.getLongitude());
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        };

        //Register the listener with Location Manager to receive the GPS location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    //Checks if the app has the permission to access the device location
    private boolean checkPermission()
    {
        //If the permission is not yet granted then ask the user for the permission
        if(ContextCompat.checkSelfPermission(owner,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //This will ask user for the permission
            ActivityCompat.requestPermissions(owner, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERM_REQUEST_CODE);
            Log.d(TAG, "checkPermission: Waiting for the ACCESS_FINE_LOCATION permission");
            return false;
        }
        else
        {
            //If the permission is already granted
            Log.d(TAG, "checkPermission: ACCESS_FINE_LOCATION permission granted for this app");
            return true;
        }
    }

    //This is findLocation() and latitude and longitude is found here
    public void determineLocation()
    {
        if(!checkPermission())
            return;

        if(locationManager == null)
            setUpLocationManager();

        Location loc;
        //1. get the location from network
        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(loc != null)
        {
            Toast.makeText(owner, "Location was fetched using NETWORK provider", Toast.LENGTH_SHORT).show();
            owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
            return;
        }
        else
        {
            //2. if null found from network provider
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if(loc != null)
            {
                Toast.makeText(owner, "Location was fetched using PASSIVE provider", Toast.LENGTH_SHORT).show();
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
                return;
            }
            else
            {
                //3. if null found from passive provider
                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(loc != null)
                {
                    Toast.makeText(owner, "Location was fetched using GPS", Toast.LENGTH_SHORT).show();
                    owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
                    return;
                }
                else
                {
                    //if got no location at all
                    owner.noLocationAvailable();
                    return;
                }
            }
        }
    }
}
