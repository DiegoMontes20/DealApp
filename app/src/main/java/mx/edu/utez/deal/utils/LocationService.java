package mx.edu.utez.deal.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {

    public static Location loc = null;
    private LocationManager locationManager = null;
    Location myLocation;

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            myLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {

            loc=location;
//                System.out.println("Latitud " + location.getLatitude());
//                System.out.println("Longitud " + location.getLongitude());

            myLocation.set(location);
        }
    }

    LocationListener[] mLocationListener = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        iniLocalLocation();

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0,
                mLocationListener[1]
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 0,
                mLocationListener[0]
        );
    }

    private void iniLocalLocation(){
        if(locationManager == null){
            locationManager =(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager !=null){
            for (int i=0; i<mLocationListener.length;i++){
                locationManager.removeUpdates(mLocationListener[i]);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
