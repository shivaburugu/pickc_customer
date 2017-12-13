package com.prts.pickccustomer.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import com.prts.pickccustomer.FCM.MyFirebaseInstanceIDService;
import com.prts.pickccustomer.FCM.MyFirebaseMessagingService;
import com.prts.pickccustomer.Payment.Payment;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.navgation_drawer.NavDrawerMainActivity;
import com.prts.pickccustomer.services.BookingInfo;
import com.prts.pickccustomer.ui.fragments.BookingFragment;
import com.prts.pickccustomer.ui.fragments.TruckCateogeoriesFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.prts.pickccustomer.ui.InvoiceActivity.BOOKING_NO;

public class HomeActivity extends NavDrawerMainActivity implements
        LocationListener, Constants, OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {


    @BindView(R.id.pinIV)
    ImageView pinIV;

    @BindView(R.id.from_location_imageVIew)
    ImageView zoomFromLocationIV ;

    @BindView(R.id.to_location_imageVIew)
    ImageView zoomToLocationIV;

    @BindView(R.id.location_LLL)
    public LinearLayout locationLLL;
    @BindView(R.id.map_linear)
    LinearLayout mapLL;
    @BindView(R.id.views_linear)
    LinearLayout viewsLinearFragentsLL;

    private static final int FROM_PLACE_PICKER_REQUEST = 1;
    private static final int TO_PLACE_PICKER_REQUEST = 2;
    private static final int REQUEST_LOATION = 25689;
    private static final long INTERVAL_TIME = 10000;
    private static final float INTERVAL_DISTANCE = 10;
    private static final String GETTING_LOACATION_STATIC_TEXT = "Getting location";
    private static final int MAX_PADDING = 15;
    private static final int MIN_PADDING = 1;
    private static final String UN_SELECTED_COLOR = "#e8ffffff";
    public static final String ANNOUNCEMENTS_SHARED_PREF = "Announcements";
    public static final String ANNOUNCEMENT_SHARED_PREF_KEY = "announcenment";
    public GoogleMap map;
    public   static  int selectedVehicleid;
    public   static int selectedtypeid;
    static String TAG = "HomeActivity";


    public BlinkBookings blinkBookings;
    private Activity activity;
    @BindView(R.id.progressBar_activity_center)
    public ProgressBar progressDialog;
    @BindView(R.id.progressBar_for_time)
    public ProgressBar progressBarForTime;
    @BindView(R.id.textView_for_time)

    public PickCCustomTextVIew textView_for_time;
    @BindView(R.id.textView_for_time_2)


    public  PickCCustomTextVIew tv_forTime_2 ;


    public String currentSelection = FROM;
    @BindView(R.id.lock_from_location_imageVIew)
    public ImageView lockFromIV;
    @BindView(R.id.lock_to_location_imageVIew)
    ImageView lockToIV;

    @BindView(R.id.from_location_relativeLayout)
    public RelativeLayout fromLocationRL;
    @BindView(R.id.to_location_relativeLayout)
    RelativeLayout toLocationRL;
    @BindView(R.id.setLocationMarkertext)
    PickCCustomTextVIew setLocationMarkertext;
    public ArrayList<Double> latittudesAL = new ArrayList<>();
    public ArrayList<Double> longittudesAL = new ArrayList<>();
    private BookingInfo bookingInfo = new BookingInfo();
    private boolean mIsClicked = false;
    private boolean mtoIsClicked = false;

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String currentAddress = DEFAULT_LOCATION;
    public static String toAddress = DEFAULT_LOCATION;
    public static double distance = 0;

    public static final String FROM = "from";
    public static final String TO = "to";

    public static LatLng fromLatLng = null;
    public  static LatLng toLatLng = null;
    MapFragment mapFragment;
    public TruckCateogeoriesFragment truckCateogeoriesFragment;
    public BookingFragment bookingFragment;
    @BindView(R.id.from_lcation_textView)
    public PickCCustomEditText fromLocationTV;
    @BindView(R.id.to_lcation_textView)
    public PickCCustomEditText toLocationTV;

    public static AVLoadingIndicatorView indicatorView;

    private static final String[] INDICATORS=new String[]{
            "BallPulseIndicator"};

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
                gpsEnabled = false;
                return;
            }
        } else {
            Toast.makeText(HomeActivity.this, "Please give permission to access locations", Toast.LENGTH_SHORT).show();
        }
        //1253
        //finish();
        //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
    }

    private boolean gpsEnabled = false;
    public  static boolean isFromLocationLocked = false;
    public  static  boolean isToLocationLocked = false;


    @Override
    protected void onResume() {
        super.onResume();
        mIsClicked = false;
        mtoIsClicked = false;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            gpsEnabled = false;
            return;
        } else {
            if (gpsEnabled) {
                finish();
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                return;
            }
        }
        if (MyFirebaseMessagingService.isCancelled.equals(MyFirebaseMessagingService.BOOKING_CANCELLED_BY_DRIVER)) {
            finish();
            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            MyFirebaseMessagingService.isCancelled = MyFirebaseMessagingService.NOT_CANCELLED;
            return;
        }
    }

    Snackbar snackbar;
   DatabaseReference mdatabaserefernce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_your_truck);
        mdatabaserefernce = FirebaseDatabase.getInstance().getReference();

        indicatorView= (AVLoadingIndicatorView) findViewById(R.id.indicator);
        indicatorView.setIndicator(INDICATORS[0]);


        ButterKnife.bind(this);
        try {
            MyFirebaseInstanceIDService.postDeviceIdToServer(getApplicationContext(),
                    CredentialsSharedPref.getMobileNO(getApplicationContext()),
                    FirebaseInstanceId.getInstance().getToken());
        } catch (Exception e) {

        }
        navDrawerOnCreate();

        progressBarForTime.setVisibility(View.GONE);
        progressBarForTime.setBackgroundColor(Color.TRANSPARENT);
        textView_for_time.setVisibility(View.GONE);
        tv_forTime_2.setVisibility(View.GONE);
        progressDialog.setVisibility(View.VISIBLE);

        snackbar = Snackbar.make(progressDialog, "Getting your location. Please wait...", Snackbar.LENGTH_INDEFINITE);
        View snackView = snackbar.getView();
        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.appThemeYellow));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(0, 0, 0, 250);
        snackView.setLayoutParams(params);
        snackbar.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    createActivity();
                } catch (IllegalStateException ise) {
                    Log.d(TAG, "run: HomActivity long live problem occured");
                    finish();
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                    ise.printStackTrace();
                }
            }
        }, 1200);
    }



//    @Override
//    protected void onStart() {
//        super.onStart();
//        mdatabaserefernce.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String driverId = CredentialsSharedPref.getDriverId(HomeActivity.this).toLowerCase();
//                Log.d(TAG, "onDataChange: "+driverId);
//                for (DataSnapshot driver : dataSnapshot.getChildren()) {
//
//
//                    if (driverId != null && bookingFragment.showDriverMarker) {
//                        DriverFcmUpdates dfu = new DriverFcmUpdates();
//
////                        dfu.setDriverId(driver.child("dr170800007").getValue(DriverFcmUpdates.class).getDriverId());
////                        dfu.setVehicletype(driver.child("dr170800007").getValue(DriverFcmUpdates.class).getVehicletype());
//
//                        dfu.setLatitude(driver.child(driverId).getValue(DriverFcmUpdates.class).getLatitude());
//                        dfu.setLongitude(driver.child(driverId).getValue(DriverFcmUpdates.class).getLongitude());
//                        double latitude = Double.parseDouble(dfu.getLatitude());
//                        double longitude = Double.parseDouble(dfu.getLongitude());
//                        bookingFragment.CreateMarker(latitude, longitude);
////                    DriverFcmUpdates dfu = driver.getValue(DriverFcmUpdates.class);
////                    String  driverID = dfu.getDriverId();
////                    Toast.makeText(activity, " Driver Id is"+driverID, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(activity, "Driver id is " + dfu.getDriverId() + dfu.getVehicletype(),Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void createActivity() {

//for 6.0 permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        //getSupportActionBar().hide();
        activity = HomeActivity.this;
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOATION);
            return;
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            gpsEnabled = false;
            return;
        }

        //1253
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TruckCateogeoriesFragment fragment = new TruckCateogeoriesFragment();
        fragmentTransaction.add(R.id.gridLayout_fragment, fragment);
        BookingFragment fragment2 = new BookingFragment();
        fragmentTransaction.add(R.id.booknow_linear_fragment, fragment2);
        fragmentTransaction.commit();



        lockFromIV.setImageResource(R.drawable.unlock);
        lockToIV.setImageResource(R.drawable.unlock);
        lockFromIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFromLocationLocked) {
                    return;
                }
                enableLockFromIV();
                showMarkerOnMap(fromLatLng, false);
            }
        });
        lockToIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isToLocationLocked) {
                    return;
                }
                enableLockToIV();
                showMarkerOnMapForToLocation(toLatLng, false);

            }
        });

        fromLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromLocationLocked) {
                    return;
                }

                if (fromLatLng != null && !isFromLocationLocked) {
                    switch (currentSelection) {
                        case FROM:

                            break;
                        case TO:
                            fromLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                            toLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                            fromLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                            toLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                            currentSelection = FROM;
                            pinIV.setImageResource(R.drawable.source2);
                            // showSelectedAddressOnMap(fromLatLng, false);
                            showMarkerOnMap(fromLatLng, false);
                            return;
                    }
                }
                try {
                    if (!mIsClicked) {
                        mIsClicked = true;
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(activity);

                        startActivityForResult(intent, FROM_PLACE_PICKER_REQUEST);
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        toLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(activity), TO_PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }*/
                if (isToLocationLocked) {
                    return;
                }
                if (toLatLng != null && !isToLocationLocked) {
                    switch (currentSelection) {
                        case FROM:
                            toLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                            fromLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                            toLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                            fromLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                            currentSelection = TO;
                            pinIV.setImageResource(R.drawable.destination2);
                            //showSelectedAddressOnMapForToLocation(toLatLng, false);
                            showMarkerOnMapForToLocation(toLatLng, false);
                            return;
                        case TO:
                            break;
                    }
                }
                try {
                    if (!mtoIsClicked) {
                        mtoIsClicked = true;
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                        .build(activity);
                        startActivityForResult(intent, TO_PLACE_PICKER_REQUEST);
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        pinIV.setImageResource(R.drawable.source2);
        zoomFromLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null && fromLatLng != null) {
                    fromLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                    toLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                    fromLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                    toLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                    currentSelection = FROM;
                    pinIV.setImageResource(R.drawable.source2);
                    // showSelectedAddressOnMap(fromLatLng, false);
                    showMarkerOnMap(fromLatLng, false);
                }
            }
        });

        zoomToLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null && toLatLng != null) {
                    toLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                    fromLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                    toLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                    fromLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                    currentSelection = TO;
                    pinIV.setImageResource(R.drawable.destination2);
                    //showSelectedAddressOnMapForToLocation(toLatLng, false);
                    showMarkerOnMapForToLocation(toLatLng, false);
                }
            }
        });
        Log.d(TAG, "onCreate: progressDialog " + progressDialog);
        if (!haveNetworkConnection(activity)) {
            Toast.makeText(activity, "Please connect to the internet to proceed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL_TIME, INTERVAL_DISTANCE, HomeActivity.this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_TIME, INTERVAL_DISTANCE, HomeActivity.this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //truckCateogeoriesFragment = (TruckCateogeoriesFragment) getSupportFragmentManager().findFragmentById(R.id.gridLayout_fragment);
        //bookingFragment = (BookingFragment) getSupportFragmentManager().findFragmentById(R.id.booknow_linear_fragment);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.source);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        sourceIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.destination);
        Bitmap b1 = bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 100, 100, false);
        destinationIcon = BitmapDescriptorFactory.fromBitmap(smallMarker1);
        /*map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();*/

        try {
            blinkBookings = (BlinkBookings) bookingFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.getVisibility() == View.VISIBLE) {
                    progressDialog.setVisibility(View.GONE);
                }
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
                checkCustomerIsInTrip();
            }
        }, 2000);


    }



    private void checkCustomerIsAlreadyBooked() {

        String url = WEB_API_ADDRESS ;
        Log.d(TAG, "checkCustomerIsInTrip: checkCustomerIsInTrip url " + url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 checkCustomerIsInTripcheckCustomerIsInTrip " + response);
                // displayErrorMessage("Logged In Successfully. Please wait");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject isConfirmBookingInfoJsonObject = response;
                        Log.d(TAG, "onResponse: jsonObject checkCustomerIsInTrip " + isConfirmBookingInfoJsonObject);

                        boolean isInTrip = isConfirmBookingInfoJsonObject.getBoolean(IS_IN_TRIP_JSON_KEY);
                        String bookingNo = isConfirmBookingInfoJsonObject.getString(BOOKING_NO_SMALL_JSON_KEY);
                        if (isInTrip) {
                            Toast.makeText(HomeActivity.this, "You are already in trip", Toast.LENGTH_SHORT).show();
                            getBookingsInfoFromServer(bookingNo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, error.networkResponse + " checkCustomerIsInTrip error.networkResponse onErrorResponse:78945 " + error);

                if ((error + "").contains("TimeoutError")) {
                    displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    checkCustomerIsInTrip();
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(HomeActivity.this);
            }
        };


        Volley.newRequestQueue(HomeActivity.this).add(jsonArrayRequest);

    }




    private void checkCustomerIsInTrip() {

      String url = WEB_API_ADDRESS + CUSTOMER_IS_IN_TRIP_API_CALL;
//       String url = "http://192.168.0.126/PickCApi/" + CUSTOMER_IS_IN_TRIP_API_CALL;
        Log.d(TAG, "checkCustomerIsInTrip: checkCustomerIsInTrip url " + url);
        OkHttpClient mOkHttpClient;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        FormBody.Builder builder = new FormBody.Builder();



        okhttp3.Request request = new okhttp3.Request
                .Builder()
                .url(url)

                .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(activity))
                .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(activity))
                .get()
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "onResponse: "+res);
                try{
                    final JSONObject jsonObject = new JSONObject(res);
                    final boolean isInTrip = jsonObject.getBoolean(IS_IN_TRIP_JSON_KEY);
                    final String bookingNo = jsonObject.getString(BOOKING_NO_SMALL_JSON_KEY);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isInTrip) {
                                Toast.makeText(HomeActivity.this, "You are already in trip", Toast.LENGTH_SHORT).show();
                                getBookingsInfoFromServer(bookingNo);
                            }
                            if(!isInTrip)
                            {
                                checkCustomerHasPendingPayment();
                            }
                        }
                    });



                }catch (JSONException ae)
                {
                    ae.printStackTrace();
                }
            }

            private void checkCustomerHasPendingPayment() {
                String url = WEB_API_ADDRESS + "api/master/customer/CustomerPaymentsIsPaidCheck" ;
//           String url = "http://192.168.0.126/PickCApi/" + CUSTOMER_IS_IN_TRIP_API_CALL;
                Log.d(TAG, "checkCustomerHasPendingPayment: "+url);
                OkHttpClient mOkHttpClient;
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                mOkHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build();

                FormBody.Builder builder = new FormBody.Builder();



                okhttp3.Request request = new okhttp3.Request
                        .Builder()
                        .url(url)

                        .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(activity))
                        .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(activity))
                        .get()
                        .build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onFailure: "+e.toString());
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: "+res);
                        try {
                            final JSONObject jsonObject = new JSONObject(res);
                            final String status = jsonObject.getString("Status");
                            final String bookingNo = jsonObject.getString("result");
                            if (status.equalsIgnoreCase("Success")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(activity,Payment.class);
                                      intent.putExtra(BOOKING_NO,bookingNo);
                                        startActivity(intent);

                                    }
                                });
                            }
                        }
                        catch (JSONException ae ){
                            ae.printStackTrace();
                        }


                    }
                });


            }
        });

//        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse:12 checkCustomerIsInTripcheckCustomerIsInTrip " + response);
//                // displayErrorMessage("Logged In Successfully. Please wait");
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject isConfirmBookingInfoJsonObject = response;
//                        Log.d(TAG, "onResponse: jsonObject checkCustomerIsInTrip " + isConfirmBookingInfoJsonObject);
//
//                        boolean isInTrip = isConfirmBookingInfoJsonObject.getBoolean(IS_IN_TRIP_JSON_KEY);
//                        String bookingNo = isConfirmBookingInfoJsonObject.getString(BOOKING_NO_SMALL_JSON_KEY);
//                        if (isInTrip) {
//                            Toast.makeText(HomeActivity.this, "You are already in trip", Toast.LENGTH_SHORT).show();
//                            getBookingsInfoFromServer(bookingNo);
//                        }
//                        boolean calledConfirmBooking = CredentialsSharedPref.getCallBookNowAPI(getApplicationContext());
//
//                        if(calledConfirmBooking)
//
//                        {
////                           int selectedVehicleGroupID = CredentialsSharedPref.getSelectedVehicleGroupID(activity);
////                           int  selectedVehicleTypeID = CredentialsSharedPref.getSelectedVehicleTypeID(activity);
////                           String  selectedTruckWeightDesc = CredentialsSharedPref.getSelectedTruckWeightDesc(activity);
////                            boolean isBookingLater = CredentialsSharedPref.getIsBookingLater(activity);
////                            int loadingUnloadingStatus =  CredentialsSharedPref.getLoadingUnloadingStatus(activity);
//////                            CredentialsSharedPref.setToLat(activity, ((HomeActivity) activity).toLatLng.latitude);
//////                            CredentialsSharedPref.setToLong(activity, ((HomeActivity) activity).toLatLng.longitude);
////                            HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
////
////                            bookingFragment.disableviewsforCrashBooking();
////
////
////                            bookingFragment.callBookNowAPI(selectedVehicleGroupID,
////                                    selectedVehicleTypeID, selectedTruckWeightDesc,
////                                    isBookingLater, loadingUnloadingStatus);
////                            CredentialsSharedPref.setCallBookNowAPI(activity,true);
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //TODO: handle success
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.d(TAG, error.networkResponse + " checkCustomerIsInTrip error.networkResponse onErrorResponse:78945 " + error);
//
//                if ((error + "").contains("TimeoutError")) {
//                    displayErrorMessage("Server busy. Please try again after some time");
//                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                            new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//                                    checkCustomerIsInTrip();
//                                }
//                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
//                    return;
//                }
//                //TODO: handle failure
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(HomeActivity.this);
//            }
//        };
//
//
//        Volley.newRequestQueue(HomeActivity.this).add(jsonArrayRequest);

    }

    public void enableLockToIV() {
        isToLocationLocked = false;
        lockToIV.setImageResource(R.drawable.unlock);
        pinIV.setVisibility(View.VISIBLE);
        setLocationMarkertext.setVisibility(View.VISIBLE);

        toLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
        fromLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
        toLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
        fromLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
        currentSelection = TO;
        pinIV.setImageResource(R.drawable.destination2);
    }

    public void enableLockFromIV() {
        lockFromIV.setImageResource(R.drawable.unlock);
        isFromLocationLocked = false;
        pinIV.setVisibility(View.VISIBLE);
        setLocationMarkertext.setVisibility(View.VISIBLE);

        fromLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
        toLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
        fromLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
        toLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
        currentSelection = FROM;
        pinIV.setImageResource(R.drawable.source2);
    }

    BitmapDescriptor sourceIcon;
    BitmapDescriptor destinationIcon;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                // String toastMsg = String.format("Place: %s", place.getName());
                // Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                fromLatLng = place.getLatLng();

               /* if (toLatLng != null) {
                    showBothFromTOLocationOnMapThroughMarkers
                            (fromLatLng, toLatLng);
                    return;
                }*/
                bookingFragment.speakOut("Pickup location is selected");
                if (toLatLng == null) {
                    bookingFragment.speakOut("Please select drop location");
                }
                fromLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                toLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                fromLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                toLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                currentSelection = FROM;
                showSelectedAddressOnMap(fromLatLng, true);
                showMarkerOnMap(fromLatLng, true);
            }
        }
        if (requestCode == TO_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                toLatLng = place.getLatLng();
               /* if (fromLatLng != null) {
                    showBothFromTOLocationOnMapThroughMarkers
                            (fromLatLng, toLatLng);
                    return;
                }*/
                Log.d(TAG, "onActivityResult: blinkBookingLayout " + blinkBookings);
                /*if (blinkBookings != null) {
                    blinkBookings.blinkBookingLayout();
                }*/
                if (toLatLng != null) {
                    bookingFragment.speakOut("Now you can proceed for booking");
                }
                bookingFragment.speakOut("Please select loading unloading options and vehicle type");
                toLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                fromLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                toLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                fromLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                currentSelection = TO;
                showSelectedAddressOnMapForToLocation(toLatLng, true);
                showMarkerOnMapForToLocation(toLatLng, true);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: location " + location);
        if (location != null) {
            /*fromLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            showMarkerOnMap(fromLatLng,true);*/
            if (bookingFragment != null &&
                    bookingFragment.isDriverMoving) {

                bookingFragment.retrieveDriverCurrentLatLng(CredentialsSharedPref.getDriverId(HomeActivity.this));
            }
        }
        //showMarkerOnMap(location);
    }

    MarkerOptions mp;
    Marker fromMarker;

    private void showSelectedAddressOnMap(@NonNull LatLng location, boolean callGetAdreessASync) {
        pinIV.setImageResource(R.drawable.source2);
        String fromAddress = "" + location.latitude + ", " + location.longitude;
        if (callGetAdreessASync) {
            GetStringFromLocationAsyncWithoutMarkers async = new GetStringFromLocationAsyncWithoutMarkers(location.latitude, location.longitude, FROM);
            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (currentAddress.isEmpty()) {
            currentAddress = fromAddress;
        }
        if (fromLocationTV.getText().toString().isEmpty()) {
            fromLocationTV.setText(currentAddress);
        }
        return;


    }

    private Marker showDriverMarkOnMap(@NonNull LatLng location) {
        if (truckIcon == null) {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.open_truck_symbol_marker);
            largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);
            truckIcon = BitmapDescriptorFactory.fromBitmap(largeIcon);
        }
        pinIV.setImageResource(R.drawable.source2);
        map.clear();
        map.getUiSettings().setZoomGesturesEnabled(false);

        mp = new MarkerOptions();

        mp.position(location);
        //mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Log.d("Tag", "showMarkerOnMap: " + location.latitude + "\t" + location.longitude);
        /*String fromAddress = getCompleteAddressString(this, location.latitude, location.longitude, FROM);
        mp.title(fromAddress);*/
        String fromAddress = "" + location.latitude + ", " + location.longitude;
        if (currentAddress.isEmpty()) {
            currentAddress = fromAddress;
        }
        mp.title(currentAddress);
        mp.icon(truckIcon);
        if (fromLocationTV.getText().toString().isEmpty()) {
            fromLocationTV.setText(currentAddress);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.latitude, location.longitude), MAP_CAMERA_ZOOM_ANIMATION));
        if (fromMarker != null && fromMarker.isVisible()) {
            fromMarker.remove();
        }
        if (isFromLocationLocked && isToLocationLocked) {
            fromMarker = map.addMarker(mp);
        }
        return fromMarker;
    }

    private Marker showMarkerOnMap(@NonNull LatLng location, boolean callGetAdreessASync) {

        pinIV.setImageResource(R.drawable.source2);
        map.clear();
        mp = new MarkerOptions();

        mp.position(location);
        //mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Log.d("Tag", "showMarkerOnMap: " + location.latitude + "\t" + location.longitude);
        /*String fromAddress = getCompleteAddressString(this, location.latitude, location.longitude, FROM);
        mp.title(fromAddress);*/
        String fromAddress = "" + location.latitude + ", " + location.longitude;
        if (callGetAdreessASync) {
            GetStringFromLocationAsync async = new GetStringFromLocationAsync(location.latitude, location.longitude, FROM);
            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (currentAddress.isEmpty()) {
            currentAddress = fromAddress;
        }
        mp.title(currentAddress);
        mp.icon(sourceIcon);
        if (fromLocationTV.getText().toString().isEmpty()) {
            fromLocationTV.setText(currentAddress);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.latitude, location.longitude), MAP_CAMERA_ZOOM_ANIMATION));
        if (fromMarker != null && fromMarker.isVisible()) {
            fromMarker.remove();
        }
        if (isFromLocationLocked && isToLocationLocked) {
            fromMarker = map.addMarker(mp);
        }
        return fromMarker;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "").setIcon(R.drawable.announcements_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
       /* SharedPreferences sharedPreferences = getSharedPreferences(ANNOUNCEMENTS_SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ANNOUNCEMENT_SHARED_PREF_KEY,true);
        editor.apply();*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                SharedPreferences sharedPreferences = getSharedPreferences(ANNOUNCEMENTS_SHARED_PREF, MODE_PRIVATE);
                boolean announcementStatus = sharedPreferences.getBoolean(ANNOUNCEMENT_SHARED_PREF_KEY, true);
                if (announcementStatus) {
                    item.setIcon(R.drawable.announcements_off);
                } else {
                    item.setIcon(R.drawable.announcements_on);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ANNOUNCEMENT_SHARED_PREF_KEY, !announcementStatus);
                editor.apply();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void displayErrorMessage(String message) {
        Log.d(TAG, "displayErrorMessage: " + message);
    }

    public String getCompleteAddressString(Context context,
                                           double LATITUDE, double LONGITUDE, String fromOrTo) {
        Log.d(TAG, "getCompleteAddressString: FROMorTO " + fromOrTo);
        Log.d(TAG, LATITUDE + " getCompleteAddressString: double LATITUDE, double LONGITUDE " + LONGITUDE);
        String strAdd = "";
        String area = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder
                    .getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                area = returnedAddress.getSubLocality() + "";
                Log.d(TAG, "getCompleteAddressString:11 getAdminArea " + returnedAddress.getAdminArea());
                Log.d(TAG, "getCompleteAddressString:11 getSubAdminArea " + returnedAddress.getSubAdminArea());
                Log.d(TAG, "getCompleteAddressString:11 getCountryCode " + returnedAddress.getCountryCode());
                Log.d(TAG, "getCompleteAddressString:11 getCountryName " + returnedAddress.getCountryName());
                Log.d(TAG, "getCompleteAddressString:11 getPostalCode " + returnedAddress.getPostalCode());
                Log.d(TAG, "getCompleteAddressString:11 getFeatureName " + returnedAddress.getFeatureName());
                Log.d(TAG, "getCompleteAddressString:11 getLocality " + returnedAddress.getLocality());
                Log.d(TAG, "getCompleteAddressString:11 getSubLocality " + returnedAddress.getSubLocality());
                Log.d(TAG, "getCompleteAddressString:11 getPhone " + returnedAddress.getPhone());
                Log.d(TAG, "getCompleteAddressString:11 getPremises " + returnedAddress.getPremises());
                Log.d(TAG, "getCompleteAddressString:11 getThoroughfare " + returnedAddress.getThoroughfare());
                Log.d(TAG, "getCompleteAddressString:11 getSubThoroughfare " + returnedAddress.getSubThoroughfare());
                Log.d(TAG, "getCompleteAddressString:11 getUrl " + returnedAddress.getUrl());
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            ", ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Loction address",
                        "" + strReturnedAddress.toString());
            } else {
                Log.w("Loction address", "No Address returned!");
            }
        } catch (IOException e) {
            area = LATITUDE + ", " + LONGITUDE;
            e.printStackTrace();
            Log.w("Loction address", "Cannot get Address!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Loction address", "Cannot get Address!");
        }
        Log.d(TAG, area + " area getCompleteAddressString: currentAddress " + currentAddress);

        if (fromOrTo.equals(FROM)) {
            if (area != null && !area.isEmpty()) {
                currentAddress = area;
            } else {
                currentAddress = strAdd;
                area = LATITUDE + ", " + LONGITUDE;
            }
            if (strAdd.isEmpty()) {
                fromLocationTV.setText(area);
            } else {
                fromLocationTV.setText(strAdd);
            }
            if (currentAddress.isEmpty()) {
                currentAddress = area;
            }
        }
        if (fromOrTo.equals(TO)) {
            if (area != null && !area.isEmpty()) {
                toAddress = area;
            } else {
                toAddress = strAdd;
                area = LATITUDE + ", " + LONGITUDE;
            }
            if (strAdd.isEmpty()) {
                toLocationTV.setText(area);
            } else {
                toLocationTV.setText(strAdd);
            }
            if (toAddress.isEmpty()) {
                toAddress = area;
            }
        }
        return strAdd;
    }

    MarkerOptions markerForToLocation;

    @Override
    public boolean onMyLocationButtonClick() {
        Location myLocation = map.getMyLocation();
        Log.d(TAG, "onMyLocationButtonClick: " + myLocation);
        if (myLocation != null) {
            fromLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            showMarkerOnMap(fromLatLng, true);
            if (truckCateogeoriesFragment != null) {
                getNearByTrucksBasedOnSelection(truckCateogeoriesFragment.
                        selectedvehicleTypeID, truckCateogeoriesFragment.selectedTruckID);
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: " + googleMap);
        currentSelection = FROM;
        toLatLng = null;
        toLocationTV.setText("");
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        map = googleMap;
        map.getUiSettings().setZoomGesturesEnabled(false);

        map.clear();
        map.getUiSettings().setZoomGesturesEnabled(false);
        if (truckCateogeoriesFragment != null) {
            getNearByTrucksBasedOnSelection(truckCateogeoriesFragment.selectedvehicleTypeID, truckCateogeoriesFragment.selectedTruckID);
        }
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (isFromLocationLocked && isToLocationLocked) {
                    return;
                }
                Log.e(TAG, "onCameraMoveStarted: 123897");
                locationLLL.setVisibility(View.GONE);
                viewsLinearFragentsLL.setVisibility(View.GONE);
                switch (currentSelection) {
                    case FROM:
                        if (!isFromLocationLocked) {
                            fromLocationTV.setText(GETTING_LOACATION_STATIC_TEXT);
                        }
                        break;
                    case TO:
                        if (!isToLocationLocked) {
                            toLocationTV.setText(GETTING_LOACATION_STATIC_TEXT);
                        }
                        break;
                }
            }
        });
      /*  map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d(TAG, "onCameraMove: 123897");
                Log.i("centerLat",map.getCameraPosition().target.latitude+"");

                Log.i("centerLong",map.getCameraPosition().target.longitude+"");
            }

        });*/
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                /*if (isFromLocationLocked && isToLocationLocked){
                    return;
                }*/
                if (bookingFragment != null) {
                    if (bookingFragment.book_now_textView.getVisibility() == View.VISIBLE ||
                            bookingFragment.bookLaterTV.getVisibility() == View.VISIBLE) {
                        locationLLL.setVisibility(View.VISIBLE);
                    }
                } else {
                    locationLLL.setVisibility(View.VISIBLE);
                }
                viewsLinearFragentsLL.setVisibility(View.VISIBLE);
                //Log.d(TAG, "onCameraIdle: 123897");
                if (!fromLocationTV.getText().toString().equals(GETTING_LOACATION_STATIC_TEXT) &&
                        !toLocationTV.getText().toString().equals(GETTING_LOACATION_STATIC_TEXT)) {
                    //Log.d(TAG, "onCameraIdle: no getting location");
                    return;
                }
                //Log.d(TAG, "onCameraIdle: getting location");
                //Log.i("centerLat", map.getCameraPosition().target.latitude + "");

                Log.i("centerLong", map.getCameraPosition().target.longitude + "");


                switch (currentSelection) {
                    case FROM:
                        if (!isFromLocationLocked) {
                            fromLatLng = map.getCameraPosition().target;
                            if (truckCateogeoriesFragment != null) {
                                getNearByTrucksBasedOnSelection(truckCateogeoriesFragment.selectedvehicleTypeID, truckCateogeoriesFragment.selectedTruckID);
                            }
                            showSelectedAddressOnMap(fromLatLng, true);
                        }
                        break;
                    case TO:
                        if (!isToLocationLocked) {
                            toLatLng = map.getCameraPosition().target;
                            showSelectedAddressOnMapForToLocation(toLatLng, true);
                        }
                        break;
                }
            }
        });
        map.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                Log.d(TAG, "onCameraMoveCanceled: 123897");
            }
        });
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);
        View mapView = mapFragment.getView();
        Log.d(TAG, mapView.findViewById(1) + " onMapReady: 651131 25 " + mapView);
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            Log.d(TAG, "onMapReady: 651131 32");
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        //markerForToLocation = new MarkerOptions();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);/*new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location myLocation = map.getMyLocation();
                Log.d(TAG, "onMyLocationButtonClick: " + myLocation);
                if (myLocation != null) {
                    fromLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    showMarkerOnMap(fromLatLng, true);
                }
                return true;
            }
        });*/
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null && fromLatLng == null) {
            fromLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
           /* finish();
            startActivity(new Intent(HomeActivity.this,HomeActivity.class));
            return;*/
        }
        onMyLocationButtonClick();
        if (fromLatLng == null) {
            final Handler getLastKnownLocationHandler = new Handler();
            getLastKnownLocationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (fromLatLng != null) {
                        showMarkerOnMap(fromLatLng, true);
                        return;
                    }
                    Log.d(TAG, "run: Retreving Location please wait");
                    //Toast.makeText(HomeActivity.this, "Retreving Location please wait", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    final Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        if (fromLatLng == null) {
                            fromLatLng =
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude());
                        }
                    } else {
                        Location myLocation = map.getMyLocation();
                        Log.d(TAG, "onMyLocationButtonClick: " + myLocation);
                        if (myLocation != null) {
                            fromLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        }
                        getLastKnownLocationHandler.postDelayed(this, 100);
                    }

                }
            }, 100);
            return;
        } else {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //lm.removeUpdates(HomeActivity.this);
        }
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                /*Location toLocation = lastKnownLocation;
                toLocation.setLatitude(latLng.latitude);
                toLocation.setLongitude(latLng.longitude);*/
                /*toLatLng = latLng;
                showBothFromTOLocationOnMapThroughMarkers
                        (fromLatLng,toLatLng);*/
            }
        });
        showMarkerOnMap(fromLatLng, true);
        //showBothFromTOLocationOnMapThroughMarkers(lastKnownLocation,null);
    }

    private void showSelectedAddressOnMapForToLocation(@NonNull LatLng location, boolean callGetAdreessASync) {


        pinIV.setImageResource(R.drawable.destination2);
        // markerForToLocation.position(new LatLng(location.latitude, location.longitude));

        Log.d(TAG, "showMarkerOnMapForToLocation: " + location.latitude + "\t" + location.longitude);
        //currentAddress = ("lat: " + location.latitude + ", long: " + location.longitude);
        //markerForToLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        //markerForToLocation.title(getCompleteAddressString(this, location.latitude, location.longitude,TO));

        String to_address = "" + location.latitude + ", " + location.longitude;
        if (callGetAdreessASync) {
            GetStringFromLocationAsyncWithoutMarkers async = new GetStringFromLocationAsyncWithoutMarkers(location.latitude, location.longitude, TO);
            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (toAddress.isEmpty()) {
            toAddress = to_address;
        }
        /*markerForToLocation.title(toAddress);
        markerForToLocation.icon(destinationIcon);*/
        if (toLocationTV.getText().toString().isEmpty()) {
            toLocationTV.setText(toAddress);
        }
        return;
    }

    public Marker showMarkerOnMapForToLocation(@NonNull LatLng location, boolean callGetAdreessASync) {


        pinIV.setImageResource(R.drawable.destination2);
        //map.clear();
        markerForToLocation = new MarkerOptions();
        markerForToLocation.position(new LatLng(location.latitude, location.longitude));

        Log.e(TAG, "showMarkerOnMapForToLocation: " + location.latitude + "\t" + location.longitude);
        //currentAddress = ("lat: " + location.latitude + ", long: " + location.longitude);
        //markerForToLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        //markerForToLocation.title(getCompleteAddressString(this, location.latitude, location.longitude,TO));
        String to_address = "" + location.latitude + ", " + location.longitude;
        if (callGetAdreessASync) {
            GetStringFromLocationAsync async = new
                    GetStringFromLocationAsync(location.latitude, location.longitude, TO);
            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (toAddress.isEmpty()) {
            toAddress = to_address;
        }
        markerForToLocation.title(toAddress);
        markerForToLocation.icon(destinationIcon);
        if (toLocationTV.getText().toString().isEmpty()) {
            toLocationTV.setText(toAddress);
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.latitude, location.longitude), MAP_CAMERA_ZOOM_ANIMATION));
        if (isFromLocationLocked && isToLocationLocked) {
            return map.addMarker(markerForToLocation);
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showBothFromTOLocationOnMapThroughMarkers(final LatLng fromLocation, final LatLng toLocation) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mapLL.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.location_LLL);
        map.clear();
        map.getUiSettings().setZoomGesturesEnabled(false);

        Log.d(TAG, fromLocation + " showBothFromTOLocationOnMapThroughMarkers: " + toLocation);
        ArrayList<Marker> markersAL = new ArrayList<>();
        if (fromLocation != null) {
            markersAL.add(showMarkerOnMap(fromLocation, true));
        }
        if (isFromLocationLocked && isToLocationLocked) {
            if (toLocation != null) {
                markersAL.add(showMarkerOnMapForToLocation(toLocation, true));
                distance = CalculationByDistance(markersAL.get(0).getPosition(), markersAL.get(1).getPosition());
            }
        }
        try {
            //distance = CalculationByDistance(markersAL.get(0).getPosition(),markersAL.get(0).getPosition());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markersAL) {

                builder.include(marker.getPosition());
            }
            Log.d(TAG, "showBothFromTOLocationOnMapThroughMarkers: markersAL.size() " + markersAL.size());
            Log.d(TAG, "showBothFromTOLocationOnMapThroughMarkers: markersAL " + markersAL);
            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            try {
                map.animateCamera(cu);
            } catch (Exception e) {
                Log.d(TAG, "showBothFromTOLocationOnMapThroughMarkers: " + e);
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final View mapView = mapFragment.getView();
        if (mapView.getViewTreeObserver().isAlive() && toLocation != null) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    LatLngBounds.Builder bld = new LatLngBounds.Builder();

                    bld.include(fromLocation);
                    bld.include(toLocation);
                    LatLngBounds bounds = bld.build();
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    /// Gopinath Added the rule of relative layout
                    params.addRule(RelativeLayout.RIGHT_OF, 0);
                  //  params.removeRule(RelativeLayout.BELOW);
                }
            });
        }

        try {

            /// Gopinath Added the rule of relative layout
            params.addRule(RelativeLayout.RIGHT_OF, 0);
           // params.removeRule(RelativeLayout.BELOW);
        }catch (Exception e)
        {

        }

    }

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        Log.d(TAG, StartP + " CalculationByDistance: " + EndP);
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "CalculationByDistance " + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        //return Radius * c;
        return kmInDec;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme1);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int id) {
                        gpsEnabled = true;
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        buildAlertMessageNoGps();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static LatLng getLocationFromString(String address)
            throws JSONException {

        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(
                    "http://maps.google.com/maps/api/geocode/json?address="
                            + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        return new LatLng(lat, lng);
    }

    public List<Address> getStringFromLocation(double lat, double lng)
            throws ClientProtocolException, IOException, JSONException {


        String address = String
                .format(Locale.ENGLISH,
                        "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                                + Locale.getDefault().getCountry(), lat, lng);
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        List<Address> retList = null;

        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());

        retList = new ArrayList<Address>();
        String area = "";
        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
            JSONArray results = jsonObject.getJSONArray("results");
            Log.e(TAG, "getStringFromLocation: ******--------------****************************");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String indiStr = result.getString("formatted_address");
                Address addr = new Address(Locale.getDefault());
                addr.setAddressLine(0, indiStr);
                retList.add(addr);
                Log.e(TAG, "getStringFromLocation: result " + result);
                Log.e(TAG, indiStr + " indiStr getStringFromLocation: i " + i + " " + addr);
                Log.e(TAG, "getStringFromLocation: ------------------------------------------------");
            }
            Log.e(TAG, "getStringFromLocation: ######################################################");
        }
        Log.e(TAG, retList + "\ngetStringFromLocation: " + retList.toString());

        return retList;
    }


    @OnClick(R.id.setLocationMarkertext)
    public void setLocationOnClick(View view) {

        switch (currentSelection) {
            case FROM:
                if (fromLatLng == null || isFromLocationLocked) {
                    return;
                }
                isFromLocationLocked = true;
                lockFromIV.setImageResource(R.drawable.lock);
                if (!isToLocationLocked) {
                    if (toLatLng == null) {
                        toLatLng = fromLatLng;
                    }
                    toLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                    fromLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                    toLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                    fromLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                    currentSelection = TO;
                    pinIV.setImageResource(R.drawable.destination2);
                    showMarkerOnMapForToLocation(toLatLng, true);
                } else {
                    setLocationMarkertext.setVisibility(View.GONE);
                    pinIV.setVisibility(View.GONE);
                    showBothMarkersOnMap();
                }
                break;
            case TO:
                if (toLatLng == null) {
                    //Toast.makeText(activity, "Please select drop Location", Toast.LENGTH_SHORT).show();
                    return;
                }
                isToLocationLocked = true;
                lockToIV.setImageResource(R.drawable.lock);
                if (!isFromLocationLocked) {
                    if (fromLatLng == null) {
                        fromLatLng = toLatLng;
                    }
                    fromLocationTV.setPadding(MAX_PADDING, MAX_PADDING, MAX_PADDING, MAX_PADDING);
                    toLocationTV.setPadding(MIN_PADDING, MIN_PADDING, MIN_PADDING, MIN_PADDING);
                    fromLocationRL.setBackgroundResource(R.drawable.selected_location_rounded_corner_map);
                    toLocationRL.setBackgroundColor(Color.parseColor(UN_SELECTED_COLOR));
                    currentSelection = FROM;
                    pinIV.setImageResource(R.drawable.source2);
                    showMarkerOnMap(fromLatLng, true);
                } else {
                    setLocationMarkertext.setVisibility(View.GONE);
                    pinIV.setVisibility(View.GONE);
                    showBothMarkersOnMap();
                }

                break;
        }
    }

    public void showBothMarkersIncludingDriverOnMap(LatLng driverLatLng) {
        GoogleMap googleMap = ((HomeActivity) activity).map;
        googleMap.clear();
        Log.e(TAG, fromLatLng + " fromLatLng showBothMarkersIncludingDriverOnMap: driverLatLng " + driverLatLng);
        Marker marker1 = showDriverMarkOnMap(driverLatLng);
        Log.d(TAG, "showBothMarkersIncludingDriverOnMap: 12 marker1 " + marker1);
        if (marker1 == null) {
            marker1 = showMarkerOnMap(fromLatLng, false);
        }
        Log.d(TAG, "showBothMarkersIncludingDriverOnMap: 23 marker1 " + marker1);
        Marker[] markers = {marker1, showMarkerOnMapForToLocation(toLatLng, false)};
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);

    }

    public void showBothMarkersOnMap() {
        GoogleMap googleMap = ((HomeActivity) activity).map;
        googleMap.clear();

        Marker[] markers = {showMarkerOnMap(fromLatLng, false), showMarkerOnMapForToLocation(toLatLng, false)};
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);

    }

    public class GetStringFromLocationAsync extends AsyncTask {

        double lat, longi;
        String fromOrTo;
        List<Address> addressList = new ArrayList<>();

        GetStringFromLocationAsync(double lat, double longi, String fromOrTo) {
            this.lat = lat;
            this.longi = longi;
            this.fromOrTo = fromOrTo;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                addressList = getStringFromLocation(lat, longi);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            StringBuilder stringBuilder = new StringBuilder("");
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                Log.e(TAG, "onPostExecute " + address.getAddressLine(0));
                Log.e(TAG, "onPostExecute:  " + address.getMaxAddressLineIndex());
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    Log.d(TAG, "onPostExecute: i " + i + " " + address.getAddressLine(i));
                    stringBuilder.append(address.getAddressLine(i)).append(", ");
                }
                if (fromOrTo.equals(FROM)) {
                    fromLocationTV.setText(stringBuilder.toString());
                    currentAddress = stringBuilder.toString();
                    showMarkerOnMap(new LatLng(lat, longi), false);
                }
                if (fromOrTo.equals(TO)) {
                    toLocationTV.setText(stringBuilder.toString());
                    toAddress = stringBuilder.toString();
                    showMarkerOnMapForToLocation(new LatLng(lat, longi), false);
                }
                Log.e(TAG, stringBuilder + "onPostExecute: currentAddress " + currentAddress);
            }
        }
    }

    public class GetStringFromLocationAsyncWithoutMarkers extends AsyncTask {

        double lat, longi;
        String fromOrTo;
        List<Address> addressList = new ArrayList<>();

        GetStringFromLocationAsyncWithoutMarkers(double lat, double longi, String fromOrTo) {
            this.lat = lat;
            this.longi = longi;
            this.fromOrTo = fromOrTo;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                addressList = geocoder.getFromLocation(lat, longi, 1);
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    IpAddress.e("err", address + " " + city + " " + state + " " + country + " " + postalCode + " " + knownName);
                }

                //  addressList = getStringFromLocation(lat, longi);
                IpAddress.e("err", addressList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                StringBuilder stringBuilder = new StringBuilder("");


                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    Log.e(TAG, "onPostExecute: 41315113 " + address.getAddressLine(0));
                    Log.e(TAG, "onPostExecute: getMaxAddressLineIndex " + address.getMaxAddressLineIndex());
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        Log.d(TAG, "onPostExecute: i " + i + " " + address.getAddressLine(i));
                        stringBuilder.append(address.getAddressLine(i)).append(", ");
                    }
                    if (fromOrTo.equals(FROM)) {
                        fromLocationTV.setText(stringBuilder.toString());
                        currentAddress = stringBuilder.toString();
                        //showMarkerOnMap(new LatLng(lat, longi), false);
                    }
                    if (fromOrTo.equals(TO)) {
                        toLocationTV.setText(stringBuilder.toString());
                        toAddress = stringBuilder.toString();
                        // showMarkerOnMapForToLocation(new LatLng(lat, longi), false);
                    }
                    Log.d(TAG, stringBuilder + " stringBuilder  onPostExecute: currentAddress " + currentAddress);
                }
            }catch (Exception ae)
            {
                ae.printStackTrace();
            }
        }

    }

    public static String onBackPressStatus = FINISH_ACTIVITY;

    @Override
    public void onBackPressed() {
        if (progressDialog.getVisibility() == View.VISIBLE) {
            return;
        }
        Log.d(TAG, "onBackPressed: onBackPressStatus " + onBackPressStatus);
        switch (onBackPressStatus) {
            case FINISH_ACTIVITY:
                super.onBackPressed();
                break;
            case ENABLE_BOOKING:
                /*if (bookingFragment != null) {
                    bookingFragment.enableBooking();
                }
                activity.setTitle(getResources().getString(R.string.home_activity_title));
                onMapReady(map);*/

                finish();
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                break;
            case DISABLE_ON_BACK_PRESS:

                break;
        }
    }

    public  void getNearByTrucksBasedOnSelection(int selectedVehicleType, final int selectedVehicleGroup) {


        if (fromLatLng != null) {
//          String url = "http://192.168.0.107/PickCApi/" + NEAR_BY_TRUCKS_BASED_ON_SELECTION_API_CALL;

          String url = WEB_API_ADDRESS + NEAR_BY_TRUCKS_BASED_ON_SELECTION_API_CALL;
            Log.e(TAG, " getNearByTrucksBasedOnSelection: url for getting Near by Trucks" + url);

            OkHttpClient mOkHttpClient;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            FormBody.Builder builder = new FormBody.Builder();

            builder.add(LATITUDE, String.valueOf(fromLatLng.latitude));
            builder.add(LONGITUDE, String.valueOf(fromLatLng.longitude));
            builder.add(VEHICLE_TYPE_JSON_KEY, String.valueOf(selectedVehicleType));
            builder.add(VEHICLE_GROUP_JSON_KEY, String.valueOf(selectedVehicleGroup));

            okhttp3.Request request = new okhttp3.Request
                    .Builder()
                    .url(url)

                    .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(activity))
                    .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(activity))
                    .post(builder.build())
                    .build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    String res = response.body().string();
                    Log.d(TAG, "onResponse: "+res);
                    try {
                        final JSONArray arr = new JSONArray(res);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                latittudesAL.clear();
                                longittudesAL.clear();
                            }
                        });
                        if (arr.length() == 0) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        map.clear();
                                        map.getUiSettings().setZoomGesturesEnabled(false);

                                    }catch (Exception ae)
                                    {}
                                    if (truckCateogeoriesFragment != null) {
                                        truckCateogeoriesFragment.setETAToSelectedTextView(NO_TRUCKS, selectedVehicleGroup);
                                    }
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < arr.length(); i++) {
                                        try {
                                            JSONObject jsonObject = arr.getJSONObject(i);
                                            Log.e(TAG, "onResponse: getNearByTrucksBasedOnSelection jsonObject " + jsonObject);
                                            double lat = jsonObject.getDouble(LATITUDE_BOOKING_JSON_KEY);
                                            double longi = jsonObject.getDouble(LONGITUDE_BOOKING_JSON_KEY);
                                            latittudesAL.add(lat);
                                            longittudesAL.add(longi);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    showNearByTrucksonMap(selectedVehicleGroup);

                                }
                            });
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }


                }
            });

//            JSONObject obj = new JSONObject();
//            try {
//                obj.put(LATITUDE, fromLatLng.latitude);
//                obj.put(LONGITUDE, fromLatLng.longitude);
//                obj.put(VEHICLE_TYPE_JSON_KEY, selectedVehicleType);
//                obj.put(VEHICLE_GROUP_JSON_KEY, selectedVehicleGroup);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.e(TAG, "getNearByTrucksBasedOnSelection: obj " + obj);
//
//                 RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
//
//
//            JsonRequest<JSONArray> arrayJsonRequest = new JsonRequest<JSONArray>
//                    (Request.Method.POST, url, obj.toString(), new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            Log.e(TAG, "onResponse: getNearByTrucksBasedOnSelection response " + response);
//                            latittudesAL.clear();
//                            longittudesAL.clear();
//                            if (response.length() == 0) {
//                                try {
//                                    map.clear();
//                                }catch (Exception ae)
//                                {}
//                                if (truckCateogeoriesFragment != null) {
//                                    truckCateogeoriesFragment.setETAToSelectedTextView(NO_TRUCKS, selectedVehicleGroup);
//                                }
//                                // Toast.makeText(activity, "No near by trucks for your selection", Toast.LENGTH_SHORT).show();
//                            } else {
//                                for (int i = 0; i < response.length(); i++) {
//                                    try {
//                                        JSONObject jsonObject = response.getJSONObject(i);
//                                        Log.e(TAG, "onResponse: getNearByTrucksBasedOnSelection jsonObject " + jsonObject);
//                                        double lat = jsonObject.getDouble(LATITUDE_BOOKING_JSON_KEY);
//                                        double longi = jsonObject.getDouble(LONGITUDE_BOOKING_JSON_KEY);
//                                        latittudesAL.add(lat);
//                                        longittudesAL.add(longi);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                showNearByTrucksonMap(selectedVehicleGroup);
//                            }
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                            error.printStackTrace();
//                            Log.d(TAG, "onErrorResponse:1234567 getNearByTrucksBasedOnSelection error " + error);
//
//                            if ((error + "").contains("TimeoutError")) {
//
//                                displayErrorMessage("Server busy. Please try again after some time");
//                                return;
//                            }
//                        }
//                    }) {
//                @Override
//                protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
//                    Log.d(TAG, "parseNetworkResponse: getNearByTrucksBasedOnSelection response " + response);
//                    try {
//                        String jsonString = new String(response.data,
//                                HttpHeaderParser
//                                        .parseCharset(response.headers));
//                        Log.d(TAG, "parseNetworkResponse: getNearByTrucksBasedOnSelection jsonString " + jsonString);
//                        return Response.success(new JSONArray(jsonString),
//                                HttpHeaderParser
//                                        .parseCacheHeaders(response));
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                        return Response.error(new ParseError(e));
//                    } catch (JSONException je) {
//                        je.printStackTrace();
//                        return Response.error(new ParseError(je));
//                    }
//
//
//                    // return null;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    return CredentialsSharedPref.getHeaders(HomeActivity.this);
//                }
//            };
//            queue.add(arrayJsonRequest);
        }
    }

    BitmapDescriptor truckIcon;

    private void showNearByTrucksonMap(int selectedVehicleGroup) {
        try {
            map.clear();
            map.getUiSettings().setZoomGesturesEnabled(false);

        }catch (Exception ae)
        {

        }
        for (int i = 0; i < latittudesAL.size(); i++) {
            double lat = latittudesAL.get(i);
            double longi = longittudesAL.get(i);
            LatLng location = new LatLng(lat, longi);
            MarkerOptions mp = new MarkerOptions();
            mp.position(location);
            Log.d(TAG, "showNearByTrucksonMap: " + truckCateogeoriesFragment);
            if (truckCateogeoriesFragment != null) {

                if (truckCateogeoriesFragment.selectedvehicleTypeID == VEHICLE_TYPE_CLOSED) {
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.closed_truck_symbol_marker);
                    largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);
                    truckIcon = BitmapDescriptorFactory.fromBitmap(largeIcon);
                    mp.icon(truckIcon);
                } else {
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.open_truck_symbol_marker);
                    largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);
                    truckIcon = BitmapDescriptorFactory.fromBitmap(largeIcon);
                    mp.icon(truckIcon);
                }
            } else {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.open_truck_symbol_marker);
                largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);
                truckIcon = BitmapDescriptorFactory.fromBitmap(largeIcon);
                mp.icon(truckIcon);
            }

            //mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Log.d("Tag", "showNearByTrucksonMap: " + location.latitude + "\t" + location.longitude);
            //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), MAP_CAMERA_ZOOM_ANIMATION));
            /*if (fromMarker != null && fromMarker.isVisible()) {
                fromMarker.remove();
            }*/
            map.addMarker(mp);
            getTravelTimeBetweenTwoLocations(fromLatLng.latitude + "," + fromLatLng.longitude, lat + "," + longi, selectedVehicleGroup, true);
        }
        return;


    }

    public void getTravelTimeBetweenTwoLocations(String fromLatLong, String toLatLong, final int selectedVehicleGroup, final boolean isForTrucks) {
        String API_KEY = "AIzaSyDZzsUIdzyATngclxURzHTX9ijd_GlpuV0";
        String API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + fromLatLong + "&destinations=" + toLatLong + "&key=" + API_KEY;

        final JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.POST, API_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: getTravelTimeBetweenTwoLocations response" + response);
                try {
                    JSONArray rowsArray = response.getJSONArray("rows");
                    for (int i = 0; i < rowsArray.length(); i++) {
                        JSONObject row = rowsArray.getJSONObject(i);
                        JSONArray elementsArray = row.getJSONArray("elements");
                        for (int j = 0; j < elementsArray.length(); j++) {
                            JSONObject element = elementsArray.getJSONObject(j);
                            JSONObject distanceObject = element.getJSONObject("distance");
                            String distance = distanceObject.getString("text");
                            JSONObject durationObject = element.getJSONObject("duration");
                            String duration = durationObject.getString("text");
                            Log.d(TAG, distance + " distance onResponse: getTravelTimeBetweenTwoLocations duration " + duration);

                            if (isForTrucks) {

                                if (truckCateogeoriesFragment != null) {
                                    truckCateogeoriesFragment.setETAToSelectedTextView(duration, selectedVehicleGroup);
                                } else {

                                }
                            } else {
                                if (bookingFragment != null && bookingFragment.driverDetailsFragment != null) {
                                    bookingFragment.driverDetailsFragment.setETAtime(duration);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: getTravelTimeBetweenTwoLocations 7896 " + error);
                //TODO: handle failure

                if ((error + "").contains("Timeo                                                                                                                                                                                                                                                                                                    utError")) {
                    displayErrorMessage("Server busy. Please try again after some time");
                  /*  AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    cancelBooking(CredentialsSharedPref.getBookingNO(activity), cancelRemarks);

                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;*/
                }
                if ((error + "").contains("DELETED")) {
                    Log.d(TAG, "onErrorResponse: getTravelTimeBetweenTwoLocations success");
                }

            }
        });
        Volley.newRequestQueue(activity).add(stringRequestRequest);
    }

    private void getBookingsInfoFromServer(final String bookingNO) {

        String url = WEB_API_ADDRESS + BOOKING_INFO_API_CALL_BASED_ON_BOOKING_NO + bookingNO;
        Log.d(TAG, "getBookingsInfoFromServer: url " + url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 getBookingsInfoFromServer " + response);
                // displayErrorMessage("Logged In Successfully. Please wait");

                    try {
                        JSONObject bookingInfoJsonObject = response;
                        Log.d(TAG, "onResponse: jsonObject " + bookingInfoJsonObject);
                        bookingInfo.updateBookingInfo(
                                bookingInfoJsonObject.getString(BOOKING_NO_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(BOOKING_DATE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CUSTOMER_ID_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(REQUIRED_DATE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(LOCATION_FROM_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(LOCATION_TO_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CARGO_DESC_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getInt(VEHICLE_TYPE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getInt(VEHICLE_GROUP_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(REMARKS_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getBoolean(IS_CONFIRM_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CONFIRM_DATE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(DRIVER_ID_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(VEHICLE_NO_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getBoolean(IS_CANCEL_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CANCEL_TIME_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CANCEL_REMARKS_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getBoolean(IS_COMPLETE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(COMPLETE_TIME_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(PAY_LOAD_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getString(CARGO_TYPE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getDouble(LATITUDE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getDouble(LONGITUDE_BOOKING_JSON_KEY),
                                bookingInfoJsonObject.getDouble(TO_LATITUDE),
                                bookingInfoJsonObject.getDouble(TO_LONGITUDE),
                                bookingInfoJsonObject.getString(RECEIVER_MOBILE_NO)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                fromLatLng = new LatLng(bookingInfo.getLatitude(), bookingInfo.getLongitude());
                toLatLng = new LatLng(bookingInfo.getToLatitude(), bookingInfo.getToLongitude());

                if (truckCateogeoriesFragment != null) {
                    truckCateogeoriesFragment.selectedvehicleTypeID = bookingInfo.getVehicleType();
                    truckCateogeoriesFragment.selectedTruckID = bookingInfo.getVehicleGroup();
                    //truckCateogeoriesFragment.loadingIVSelectedStatus = bookingInfo.getLo
                }
                if (bookingFragment != null) {
                    bookingFragment.lockBothLocations();
                    bookingFragment.startTrip();
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, error.networkResponse + " error.networkResponse onErrorResponse:789415 " + error);

                if ((error + "").contains("TimeoutError")) {
                    //Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    AlertDialogActivity.showAlertDialogActivity(HomeActivity.this, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    getBookingsInfoFromServer(bookingNO);
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }

                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(HomeActivity.this);
            }
        };


        Volley.newRequestQueue(HomeActivity.this).add(jsonArrayRequest);

    }

}
