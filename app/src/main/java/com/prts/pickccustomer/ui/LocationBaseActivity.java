package com.prts.pickccustomer.ui;

import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by LOGICON on 09-07-2017.
 */

public abstract class LocationBaseActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String TAG = "LocationActivity";
    static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1200;
    static final int REQUEST_CHECK_SETTINGS = 0x1;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1253;

    GoogleApiAvailability mGoogleApiAvailability;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLocation = new Location("");
    LocationSettingsRequest mLocationSettingsRequest;
    int isAvailable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        isAvailable = mGoogleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if (isAvailable == ConnectionResult.SUCCESS) {
            locationEnable();

        } else if (mGoogleApiAvailability.isUserResolvableError(isAvailable)) {

            mGoogleApiAvailability.getErrorDialog(this, isAvailable,
                    REQUEST_GOOGLE_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                            isAvailable = mGoogleApiAvailability.
                                    isGooglePlayServicesAvailable(getApplicationContext());

                        }
                    });

        } else {

            mGoogleApiAvailability.showErrorDialogFragment(this, isAvailable,
                    REQUEST_GOOGLE_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {


                            isAvailable = mGoogleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());

                        }
                    });

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        checkLocationPermission();

    }


    private void locationEnable() {


        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    private void checkLocationPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            // getLastKnownLocation();
            checkLocationSettings();

        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {


                    Toast.makeText(this, "Please enable location", Toast.LENGTH_SHORT).show();

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //getLastKnownLocation();
                    checkLocationSettings();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    checkLocationPermission();


                }

            }
            break;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);


    }


    private void getLastKnownLocation() {

        try {
//            Intent intent = new Intent(this, LocationService.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startService(intent);

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                Log.e("OLD LOC", mLocation.getProvider());

            }
            LocationServices.FusedLocationApi.
                    requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                            //  mRequestingLocationUpdates = true;
                            // setButtonsEnabledState();
                        }
                    });

        } catch (SecurityException se) {

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Connection failed: ConnectionResult.getErrorCode() = " +
                +connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {


        Log.e("NEW LOC", location.getProvider());
        mLocation = location;
        locationChanged(location);

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {


        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e(TAG, "All location settings are satisfied.");

                getLastKnownLocation();

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setFastestInterval(1000);

    }

    public abstract void locationChanged(Location c);


}
