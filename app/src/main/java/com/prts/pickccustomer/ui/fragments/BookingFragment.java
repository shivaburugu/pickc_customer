package com.prts.pickccustomer.ui.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.prts.pickccustomer.ui.AVLoadingIndicatorView;
import com.squareup.picasso.Picasso;

import com.prts.pickccustomer.FCM.MyFirebaseMessagingService;
import com.prts.pickccustomer.Payment.Payment;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.constants.RegularMethods;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.map.GMapV2Direction;
import com.prts.pickccustomer.map.GMapV2DirectionAsyncTask;
import com.prts.pickccustomer.map.LatLngInterpolator;
import com.prts.pickccustomer.map.MarkerAnimation;
import com.prts.pickccustomer.services.IsConfirmBookingInfo;
import com.prts.pickccustomer.ui.AlertDialogActivity;
import com.prts.pickccustomer.ui.BlinkBookings;
import com.prts.pickccustomer.ui.DriverFcmUpdates;
import com.prts.pickccustomer.ui.HomeActivity;
import com.prts.pickccustomer.ui.history.HistoryData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.Context.MODE_PRIVATE;
import static com.prts.pickccustomer.R.id.vehicleType;
import static com.prts.pickccustomer.ui.HomeActivity.ANNOUNCEMENTS_SHARED_PREF;
import static com.prts.pickccustomer.ui.HomeActivity.ANNOUNCEMENT_SHARED_PREF_KEY;
import static com.prts.pickccustomer.ui.HomeActivity.fromLatLng;
import static com.prts.pickccustomer.ui.HomeActivity.toLatLng;
import static com.prts.pickccustomer.ui.InvoiceActivity.BOOKING_NO;

/**
 * Created by Uday on 18-08-2016.
 */
public class BookingFragment extends Fragment implements Constants, BlinkBookings, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TextToSpeech.OnInitListener {

    private static final String TAG = "BookingFragment";
    ProgressBar progressDialog;
    private static int UPDATE_INTERVAL = 3000; // 3 sec
    private static int FATEST_INTERVAL = 3000; // 5 sec
    private static int DISPLACEMENT = 10;

    private String bookingRequiredDate = "";
    private boolean isBookingLater = false;
    TextToSpeech tts;
    private BitmapDescriptor destinationIcon;
    private BitmapDescriptor sourceIcon;
    public boolean isDriverMoving = false;
    HomeActivity activity;
    JsonObjectRequest jsObjRequest;
    private String pinMode;
    boolean canCancel;
//    DatabaseReference mdatabaserefence;
    ValueEventListener mlistener;
     public boolean showDriverMarker = false;
    public boolean updateEta=false;


   /* public AVLoadingIndicatorView indicatorView;
    private static final String[] INDICATORS=new String[]{
            "BallPulseIndicator"};*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_booking_layout, container, false);
//        mdatabaserefence = FirebaseDatabase.getInstance().getReference();
        activity.bookingFragment = BookingFragment.this;
        initialize(view);
        tts = new TextToSpeech(activity, this);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.source);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        sourceIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);


        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.destination);
        Bitmap b1 = bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 100, 100, false);
        destinationIcon = BitmapDescriptorFactory.fromBitmap(smallMarker1);


        return view;
    }

    public PickCCustomTextVIew book_now_textView, bookLaterTV, bookingCancelTV, book_confirm_textView;
    LinearLayout bookingLL;

    private void initialize(final View view) {

       // indicatorView= (AVLoadingIndicatorView) view.findViewById(R.id.indicator);

        book_now_textView = (PickCCustomTextVIew) view.findViewById(R.id.book_now_textView);
        bookLaterTV = (PickCCustomTextVIew) view.findViewById(R.id.book_later_textView);
        bookingCancelTV = (PickCCustomTextVIew) view.findViewById(R.id.book_cancel_textView);
        book_confirm_textView = (PickCCustomTextVIew) view.findViewById(R.id.book_confirm_textView);
        bookingLL = (LinearLayout) view.findViewById(R.id.booknow_linear);

        enableBooking();
        final TruckCateogeoriesFragment truckCateogeoriesFragment = (TruckCateogeoriesFragment) getFragmentManager().findFragmentById(R.id.gridLayout_fragment);
        book_now_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //blink(bookingLL,3);.
                if(!(HomeActivity.isFromLocationLocked && HomeActivity.isToLocationLocked))
                {
                    Toast.makeText(activity, "Please Lock both Locations", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!areLocationsSelected()) {
                    return;
                }
                if(!(HomeActivity.isFromLocationLocked && HomeActivity.isToLocationLocked))
                {
                    Toast.makeText(activity, "Please Lock both Locations", Toast.LENGTH_SHORT).show();
                    return;
                }
//                ((HomeActivity)activity).showBothFromTOLocationOnMapThroughMarkers(((HomeActivity)activity).fromLatLng,((HomeActivity)activity).toLatLng);

                bookNowConfirmationforTrucks(truckCateogeoriesFragment.selectedvehicleTypeID, truckCateogeoriesFragment.selectedTruckID);
//                createDrivingRoute(((HomeActivity) activity).fromLatLng,
//                        ((HomeActivity) activity).toLatLng,false);
//                enableConfirmBooking(false);
//
//
//               /* indicatorView.setVisibility(View.GONE);
//                indicatorView.setIndicator(INDICATORS[0]);*/
//
//                progressDialog =activity.progressDialog;
//                double fromlatitude = fromLatLng.latitude;
//                double fromlongitude = fromLatLng.longitude;
//                double toLatitude = toLatLng.latitude;
//                double toLongitude = toLatLng.longitude;
//                CredentialsSharedPref.setFromoLat(activity,fromlatitude);
//                CredentialsSharedPref.setFromLong(activity,fromlongitude);
//                CredentialsSharedPref.setToLat(activity,toLatitude);
//                CredentialsSharedPref.setToLong(activity,toLongitude);
//
//
//
//                downloadCargoMaterialTypes();


            }
        });
        bookLaterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areLocationsSelected()) {
                    return;
                }
                //((HomeActivity)activity).showBothFromTOLocationOnMapThroughMarkers(((HomeActivity)activity).fromLatLng,((HomeActivity)activity).toLatLng);
                createDrivingRoute(((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,false);
                enableConfirmBooking(true);
                progressDialog = ((HomeActivity) activity).progressDialog;
                showDatePickerDialogWithCurrentDate();
                //Toast.makeText(activity, "Waiting for update", Toast.LENGTH_SHORT).show();
            }
        });
        bookingCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBookingInfo(CredentialsSharedPref.getBookingNO(activity));
            }
        });
        book_confirm_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (!areLocationsSelected()) {
                    enableBooking();
                    return;
                }
                if (isBookingLater) {
                    enableBooking();
                } else {
                    enableCancelBooking();
                }
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.removeUpdates((HomeActivity) getActivity());
                HomeActivity.selectedtypeid = truckCateogeoriesFragment.selectedvehicleTypeID;
                HomeActivity.selectedVehicleid = truckCateogeoriesFragment.selectedTruckID;
                final int selectedVehicleGroupID = truckCateogeoriesFragment.selectedTruckID;
                final int selectedVehicleTypeID = truckCateogeoriesFragment.selectedvehicleTypeID;
                final int loadingUnloadingStatus = truckCateogeoriesFragment.loadingIVSelectedStatus;
                final String selectedTruckWeightDesc = truckCateogeoriesFragment.vehicleGroupDescriptionsAL.get(
                        truckCateogeoriesFragment.vehicleGroupIdsAL.indexOf(selectedVehicleGroupID));
                Log.e(TAG, selectedVehicleGroupID + " initialize: callBookNowAPI " + selectedTruckWeightDesc);
                Log.e(TAG, selectedVehicleTypeID + " initialize: callBookNowAPI " + selectedTruckWeightDesc);
                //Log.d(TAG, ((HomeActivity) activity).progressDialog + " initialize: progressDialog " + progressDialog);

                CredentialsSharedPref.setSelectedVehicleGroupID(activity,selectedVehicleGroupID);
                CredentialsSharedPref.setSelectedVehicleTypeID(activity,selectedVehicleTypeID);
                CredentialsSharedPref.setSelectedTruckWeightDesc(activity,selectedTruckWeightDesc);
                CredentialsSharedPref.setIsBookingLater(activity,isBookingLater);
                CredentialsSharedPref.setLoadingUnloadingStatus(activity,loadingUnloadingStatus);



                callBookNowAPI(selectedVehicleGroupID,
                        selectedVehicleTypeID, selectedTruckWeightDesc,
                        isBookingLater, loadingUnloadingStatus);
                CredentialsSharedPref.setCallBookNowAPI(activity,true);

            }
        });

        MyFirebaseMessagingService.setOnBookingConfirmedListener(new MyFirebaseMessagingService.OnBookingConfirmedListener() {
            @Override
            public void onBookingConfirmed(String bodyOfFCM, String bookingNo) {
                Log.d(TAG, bodyOfFCM + " bodyOfFCM onBookingConfirmed: MyFirebaseMsgService Booking COnfirmed bookingNo " + bookingNo);
                /*displayErrorMessage("Your Booking confirmed by driver");
                showSnackBar(bookingCancelTV, "Your Booking confirmed by driver");
                enableAcceptedBooking();*/
                isDriverMoving = true;

                showDriverMarker =true;
                startCheckingIsConfirmed(bookingNo);


            }
        });
        MyFirebaseMessagingService.setOnTripStartedListener(new MyFirebaseMessagingService.OnTripStartedListener() {
            @Override
            public void onTripStart(String bodyOfFCM, String bookingNo) {
                Log.d(TAG, bodyOfFCM + " bodyOfFCM onTripStart: MyFirebaseMsgService Booking onTripStart bookingNo " + bookingNo);
                //displayErrorMessage("Your Booking confirmed by driver");
                showSnackBar(bookingCancelTV, "Trip started by driver");

                showDriverMarker =true;
                updateEta=true;
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,true);
                //mdatabaserefence.addValueEventListener(mlistener);


                startTrip();
            }
        });
        MyFirebaseMessagingService.setOnTripEndedListener(new MyFirebaseMessagingService.OnTripEndedListener() {

            @Override
            public void onTripEnd(String bodyOfFCM, String bookingNo) {
                Log.d(TAG, bodyOfFCM + " bodyOfFCM onTripEnd: MyFirebaseMsgService Booking onTripEnd bookingNo " + bookingNo);
                //displayErrorMessage("Your Booking confirmed by driver");
                showSnackBar(bookingCancelTV, "Trip completed by driver");
                isDriverMoving = false;
                showDriverMarker =false;
                updateEta=false;
//                mdatabaserefence.removeEventListener(mlistener);
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,false);
               /* enableBooking();
                ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);*/
            }

            @Override
            public void onTripEndOk(String bookingNo) {
                activity.finish();
//                startActivity(new Intent(activity, HomeActivity.class));
                //String bookingNO = bookingData.getBookingNo();
//                Intent intent = new Intent(activity,InvoiceActivity.class);
                showDriverMarker =false;
                updateEta=false;
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,false);
                Intent intent = new Intent(activity,Payment.class);
                intent.putExtra(BOOKING_NO,CredentialsSharedPref.getBookingNO(getActivity()));
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,false);
                activity.startActivity(intent);
            }
        });

        MyFirebaseMessagingService.setOnDriverReachedPickUpLocationListener(new MyFirebaseMessagingService.OnDriverReachedPickUpLocationListener() {
            @Override
            public void onDriverReachedPickUpLocation(String bodyOfFCM, String bookingNo) {

                Log.d(TAG, bodyOfFCM + " bodyOfFCM onDriverReachedPickUpLocation: MyFirebaseMsgService onDriverReachedPickUpLocation bookingNo " + bookingNo);
                //displayErrorMessage("Your Booking confirmed by driver");
                showSnackBar(bookingCancelTV, "Driver reached pick up location");
                enableDriverReachedPickUP();
//                DriverDetailsFragment.pickTVAR


                speakOut("Driver Arrived");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                driverDetailsFragment = DriverDetailsFragment.getInstanceOfDriverDetailsFragment(newBookingInfo, BookingFragment.this, false, true);
                fragmentTransaction.replace(R.id.driverDetailsFragment, driverDetailsFragment);
                Log.d(TAG, "enableAcceptedBooking: fragment attached");
                fragmentTransaction.commitAllowingStateLoss();
                updateEta=false;
//
                //mdatabaserefence.removeEventListener(mlistener);
                showDriverMarker =true;
                isDriverMoving = false;
            }
        });

        MyFirebaseMessagingService.setOnDriverAbouttotReachPickUPLocationListener(new MyFirebaseMessagingService.OnDriverAbouttotReachPickUPLocationListener() {
            @Override
            public void onDriverAbouttotReachPickUPLocationListener(String bodyOfFCM, String bookingNo) {
                showDriverMarker =true;
                updateEta=true;
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,true);
                Toast.makeText(activity, "Driver is about to reach Pick up location", Toast.LENGTH_SHORT).show();

//                mdatabaserefence.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "onDataChange: 24121");
//                        first = true;
//                        mlistener.onDataChange(dataSnapshot);
//                        mdatabaserefence.addValueEventListener(mlistener);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d(TAG, "onCancelled: 24121");
//
//                    }
//                });

            }
        });



        MyFirebaseMessagingService.setOnDriverReachedDropLocationListener(new MyFirebaseMessagingService.OnDriverReachedDropLocationListener() {
            @Override
            public void onDriverReachedDropLocation(String bodyOfFCM, String bookingNo) {

                Log.d(TAG, bodyOfFCM + " bodyOfFCM onDriverReachedDropLocation: MyFirebaseMsgService onDriverReachedDropLocation bookingNo " + bookingNo);
                //displayErrorMessage("Your Booking confirmed by driver");
                showSnackBar(bookingCancelTV, "Driver reached delivery location");
                isDriverMoving = false;
                showDriverMarker =false;
                canCancel = true;
                updateEta=false;

                showDriverMarker = true;
                if (mapAsync != null && mapAsync.getStatus() == AsyncTask.Status.RUNNING){
                    mapAsync.cancel(true);
                    mapAsync = null;
                }
                retrieveDriverCurrentLatLngDriverReachedPickup(CredentialsSharedPref.getDriverId(activity));
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,false);

//          mdatabaserefence.removeEventListener(mlistener);

//                retrieveDriverCurrentLatLngDriverReachedPickup(CredentialsSharedPref.getDriverId(activity));
//                if (mapAsync != null && mapAsync.getStatus() == AsyncTask.Status.RUNNING){
//                    mapAsync.cancel(true);
//                    mapAsync = null;
//                }

            }
        });
    }

    public void startTrip() {
        enableTripStarted();
        isDriverMoving = true;
//       createDrivingRoute(((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,true);
    }

    private void enableDriverReachedPickUP() {
        if (newBookingInfo != null) {
//            retrieveDriverCurrentLatLngDriverReachedPickup(CredentialsSharedPref.getDriverId(activity));
        }



//      enableDriverLocationChange();

    }

    public void retrieveDriverCurrentLatLngDriverReachedPickup(final String driverId) {

        String url = WEB_API_ADDRESS + DRIVER_CURRENT_LAT_LNG_API_CALL + driverId;
        /*JSONObject obj = new JSONObject();
        try {
            obj.put(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY, bookingNo);
            obj.put(REMARKS_CANCEL_JSON_KEY, cancelRemarks);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/


        Log.d(TAG, "retrieveDriverCurrentLatLng: retrieveDriverCurrentLatLng url "+url);
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

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(res);
                    double currentLat = jsonObject.getDouble(CURRENT_LAT_JSON_KEY);
                    double currentLong = jsonObject.getDouble(CURRENT_LONG_JSON_KEY);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            createDrivingRoute(((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,false);
                        }
                    });

                }catch (JSONException ae){
                    ae.printStackTrace();
                }

            }
        });
//        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse: retrieveDriverCurrentLatLng response" + response);
//
//                try {
//                    JSONObject jsonObject = response;
//                    double currentLat = jsonObject.getDouble(CURRENT_LAT_JSON_KEY);
//                    double currentLong = jsonObject.getDouble(CURRENT_LONG_JSON_KEY);
//
//
//                    createDrivingRoute(((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,false);
////                    createmarkersforbothLocations();
//                    //Todo use booking info from here
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                //displayErrorMessage("Please check your internet connection");
//                Log.d(TAG, "onErrorResponse: retrieveDriverCurrentLatLng 7896 " + error);
//                //TODO: handle failure
//
//                if ((error + "").contains("TimeoutError")) {
//                    /*displayErrorMessage("Server busy. Please try again after some time");
//                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                            new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//                                    retrieveDriverCurrentLatLng(driverId);
//
//                                }
//                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });*/
//                    return;
//                }
//                if ((error + "").contains("DELETED")) {
//                    Log.d(TAG, "onErrorResponse: retrieveDriverCurrentLatLng success");
//                }
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(activity);
//            }
//        };
//        Volley.newRequestQueue(activity).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }
    public void createmarkersforbothLocations()
    {
        MarkerOptions markerForToLocation;
        markerForToLocation = new MarkerOptions();

        MarkerOptions markerForFromLocation;
        markerForFromLocation = new MarkerOptions();

        BitmapDescriptor destinationIcon;

        Bitmap largesIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.destination2);



        largesIcon = Bitmap.createScaledBitmap(largesIcon, 50, 50, false);
        destinationIcon = BitmapDescriptorFactory.fromBitmap(largesIcon);
        markerForToLocation.position(activity.toLatLng)
                .anchor(0.5f, 0.5f)
                .icon(destinationIcon);


        BitmapDescriptor sourceIcon;

        Bitmap smallIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.source2);
        smallIcon = Bitmap.createScaledBitmap(smallIcon, 50, 50, false);
        sourceIcon = BitmapDescriptorFactory.fromBitmap(smallIcon);

        markerForFromLocation.position(activity.fromLatLng)
                .anchor(0.5f, 0.5f)
                .icon(sourceIcon);


        GoogleMap googleMap = ((HomeActivity) activity).map;
        googleMap.clear();

        Marker marker1 = googleMap.addMarker(markerForToLocation);
        Marker marker2 = googleMap.addMarker(markerForToLocation);

        Marker[] markers = {marker1, marker2};
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);


    }

    private void enableDriverLocationChange() {
        if (newBookingInfo != null){
            retrieveDriverCurrentLatLng(newBookingInfo.getDriverId());
        }
    }

    public void retrieveDriverCurrentLatLng(final String driverId) {

        String url = WEB_API_ADDRESS + DRIVER_CURRENT_LAT_LNG_API_CALL + driverId;
        /*JSONObject obj = new JSONObject();
        try {
            obj.put(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY, bookingNo);
            obj.put(REMARKS_CANCEL_JSON_KEY, cancelRemarks);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Log.d(TAG, "retrieveDriverCurrentLatLng: retrieveDriverCurrentLatLng url "+url);
        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: retrieveDriverCurrentLatLng response" + response);

                try {
                    JSONObject jsonObject = response;
                    double currentLat = jsonObject.getDouble(CURRENT_LAT_JSON_KEY);
                    double currentLong = jsonObject.getDouble(CURRENT_LONG_JSON_KEY);
                    createDrivingRoute(new LatLng(currentLat,currentLong), ((HomeActivity) activity).toLatLng,true);
                    //Todo use booking info from here

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: retrieveDriverCurrentLatLng 7896 " + error);
                //TODO: handle failure

                if ((error + "").contains("TimeoutError")) {
                    /*displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    retrieveDriverCurrentLatLng(driverId);

                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });*/
                    return;
                }
                if ((error + "").contains("DELETED")) {
                    Log.d(TAG, "onErrorResponse: retrieveDriverCurrentLatLng success");
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };
        Volley.newRequestQueue(activity).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }
    private void bookingInfoApiCall(final String bookingNo) {

        String url = WEB_API_ADDRESS + BOOKING_INFO_API_CALL_BASED_ON_BOOKING_NO + bookingNo;
        /*JSONObject obj = new JSONObject();
        try {
            obj.put(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY, bookingNo);
            obj.put(REMARKS_CANCEL_JSON_KEY, cancelRemarks);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Log.d(TAG, "bookingInfoApiCall: bookingInfoApiCall url "+url);
        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: bookingInfoApiCall response" + response);

                try {
                    JSONObject jsonObject = response;

                    String bookingNo = jsonObject.getString(BOOKING_NO_BOOKING_JSON_KEY);
                    String bookingDate = jsonObject.getString(BOOKING_DATE_BOOKING_JSON_KEY);
                    //String mobileNO_CusID = jsonObject.getString(CUSTOMER_ID_BOOKING_JSON_KEY);
                    String requiredDate = jsonObject.getString(REQUIRED_DATE_BOOKING_JSON_KEY);

                    String fromLocation = jsonObject.getString(LOCATION_FROM_BOOKING_JSON_KEY);
                    String toLocation = jsonObject.getString(LOCATION_TO_BOOKING_JSON_KEY);
                    String cargoDesc = jsonObject.getString(CARGO_DESC_BOOKING_JSON_KEY);

                    int vehicleType = jsonObject.getInt(VEHICLE_TYPE_BOOKING_JSON_KEY);
                    int vehicleGroup = jsonObject.getInt(VEHICLE_GROUP_BOOKING_JSON_KEY);
                    String remarks = jsonObject.getString(REMARKS_BOOKING_JSON_KEY);

                    boolean isConfirmed = jsonObject.getBoolean(IS_CONFIRM_BOOKING_JSON_KEY);
                    String confirmDate = jsonObject.getString(CONFIRM_DATE_BOOKING_JSON_KEY);
                    String driverId = jsonObject.getString(DRIVER_ID_BOOKING_JSON_KEY);
                    String vehicleNo = jsonObject.getString(VEHICLE_NO_BOOKING_JSON_KEY);

                    boolean isCanceled = jsonObject.getBoolean(IS_CANCEL_BOOKING_JSON_KEY);
                    String canceledTime = jsonObject.getString(CANCEL_TIME_BOOKING_JSON_KEY);
                    String cancelRemarks = jsonObject.getString(CANCEL_REMARKS_BOOKING_JSON_KEY);

                    boolean isCompleted = jsonObject.getBoolean(IS_COMPLETE_BOOKING_JSON_KEY);
                    String completedTime = jsonObject.getString(COMPLETE_TIME_BOOKING_JSON_KEY);


                    HistoryData data = new HistoryData();

                    data.setBookingNo(bookingNo);
                    data.setBookingDate(bookingDate);
                    data.setRequiredDate(requiredDate);
                    data.setFromLocation(fromLocation);
                    data.setToLocation(toLocation);
                    data.setCargoDesc(cargoDesc);

                    data.setVehicleType(vehicleType);
                    data.setVehicleGroup(vehicleGroup);
                    data.setRemarks(remarks);
                    data.setConfirmed(isConfirmed);
                    data.setConfirmDate(confirmDate);
                    data.setDriverId(driverId);

                    data.setVehicleNo(vehicleNo);
                    data.setCanceled(isCanceled);
                    data.setCanceledTime(canceledTime);
                    data.setCancelRemarks(cancelRemarks);
                    data.setCompleted(isCompleted);
                    data.setCompletedTime(completedTime);

                    //Todo use booking info from here

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: bookingInfoApiCall 7896 " + error);
                //TODO: handle failure

                if ((error + "").contains("TimeoutError")) {
                    displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                 public void onPositiveBtnClick() {
                                    bookingInfoApiCall(bookingNo);

                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
                if ((error + "").contains("DELETED")) {
                    Log.d(TAG, "onErrorResponse: bookingInfoApiCall success");
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };
        Volley.newRequestQueue(activity).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }

    private boolean areLocationsSelected() {
        if ((((HomeActivity) activity).fromLatLng == null && ((HomeActivity) activity).toLatLng == null)) {
            displayErrorMessage("Please select Source and destination");
            return false;
        }
        if (((HomeActivity) activity).fromLatLng == null) {
            displayErrorMessage("Please select Source location");
            return false;
        }
        if (((HomeActivity) activity).toLatLng == null) {
            displayErrorMessage("Please select destination location");
            return false;
        }
        if (!lockBothLocations()) {
            displayErrorMessage("Unable to lock locations. Please try again");
            return false;
        }

        return true;
    }

    private void showDatePickerDialogWithCurrentDate() {

        Calendar calendar = Calendar.getInstance();

        int Year = calendar.get(Calendar.YEAR);
        int Month = calendar.get(Calendar.MONTH);
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, 2);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(BookingFragment.this, Year, Month, Day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.setMaxDate(calendar);
        datePickerDialog.setAccentColor(Color.parseColor("#009688"));
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show(activity.getFragmentManager(), "DatePickerDialog");

    }

    private void showTimePickerDialogWithCurrentTime() {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        /*int Day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE,2);*/

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(BookingFragment.this, hour, minute, false);
        timePickerDialog.setThemeDark(false);
        timePickerDialog.setAccentColor(Color.parseColor("#009688"));
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show(activity.getFragmentManager(), "DatePickerDialog");

    }

    public DriverDetailsFragment driverDetailsFragment;

    public void enableBooking() {
        HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.VISIBLE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.GONE);
        }
        book_now_textView.setVisibility(View.VISIBLE);
        bookLaterTV.setVisibility(View.VISIBLE);
        bookingCancelTV.setVisibility(View.GONE);
        book_confirm_textView.setVisibility(View.GONE);
        if (((HomeActivity) activity).locationLLL != null) {
            ((HomeActivity) activity).locationLLL.setVisibility(View.VISIBLE);
        }
        enableLockImages(((HomeActivity) activity));

        if (driverDetailsFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(driverDetailsFragment);
            Log.d(TAG, "enableBooking: fragment removed");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void enableLockImages(HomeActivity activity) {
        activity.enableLockToIV();
        activity.enableLockFromIV();
    }

    private void enableAcceptedBooking(IsConfirmBookingInfo newBookingInfo) {

        stopProgressTime(HomeActivity.indicatorView, ((HomeActivity)
                getActivity()).textView_for_time, ((HomeActivity) getActivity()).tv_forTime_2);

        HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.GONE);
        }
        book_now_textView.setVisibility(View.GONE);
        bookLaterTV.setVisibility(View.GONE);
        //Todo
        bookingCancelTV.setVisibility(View.GONE);
        book_confirm_textView.setVisibility(View.GONE);

        lockBothLocations();
        speakOut("Your pickup is arriving");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        driverDetailsFragment = DriverDetailsFragment.getInstanceOfDriverDetailsFragment(newBookingInfo, BookingFragment.this, false,false);
        fragmentTransaction.replace(R.id.driverDetailsFragment, driverDetailsFragment);
        Log.d(TAG, "enableAcceptedBooking: fragment attached");
        fragmentTransaction.commitAllowingStateLoss();
        // fragmentTransaction.commit();

        updateDriverLatitudeAndLongitude();
       startLocationUpdates();
        showDriverMarker = true;
//        showBothMarkersOnMap(new LatLng(newBookingInfo.getDriverLatitude(), newBookingInfo.getDriverLongitude()),
//                ((HomeActivity) activity).fromLatLng, FROM_ICON);
    }
    public void disableviewsforCrashBooking()
    {
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.GONE);
        }
        book_now_textView.setVisibility(View.GONE);
        bookLaterTV.setVisibility(View.GONE);
        //Todo
        bookingCancelTV.setVisibility(View.VISIBLE);
        book_confirm_textView.setVisibility(View.GONE);

//        lockBothLocations();
    }

    private void updateDriverLatitudeAndLongitude() {


        String driverId = CredentialsSharedPref.getDriverId(activity);

        String url = WEB_API_ADDRESS+"api/master/customer/DriverMonitorInCustomer/"+driverId;

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
                try{
                    JSONObject jsonObject = new JSONObject(res);
                    final double currentLat = jsonObject.getDouble("CurrentLat");
                    final double currentLong = jsonObject.getDouble("CurrentLong");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //CreateMarker(currentLat,currentLong);
                            Location location = new Location("");
                            location.setLatitude(currentLat);
                            location.setLongitude(currentLong);
                            onTruckLocationUpdated(location);
                            activity.getTravelTimeBetweenTwoLocations(activity.fromLatLng.latitude + "," + activity.fromLatLng.longitude, currentLat + "," + currentLong, 0, false);



                        }
                    });



                }
                catch (JSONException ex)
                {
                    ex.printStackTrace();
                }

            }
        });


//        RequestQueue queue = Volley.newRequestQueue(activity);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log.d(TAG, "onResponse: postDeviceIdToServer "+response);
//                        try {
//                            JSONObject jsonObject = response;
//                            double currentLat = jsonObject.getDouble("CurrentLat");
//                            double currentLong = jsonObject.getDouble("CurrentLong");
////                          CreateMarker(currentLat,currentLong);
////                            LatLng latlong = new LatLng(currentLat,currentLong);
////                           animateMarker(latlong, mk);
//
//                            addMarker(currentLat,currentLong);
//                            //Todo use booking info from here
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Log.d(TAG, "onErrorResponse:12345 CashPayment "+error);
//
//
////                        if ((error+"").contains("TimeoutError")){
////
////                            displayErrorMessage("Server busy. Please try again after some time");
////                            return;
////                        }
//
//                    }
//                }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(activity);
//            }
//        };
//        queue.add(jsObjRequest);





    }
    boolean first = false;
    @Override
    public void onResume() {
        super.onResume();


//        mlistener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String driverId = null;
//
//                boolean showingDriverMarkerLive = CredentialsSharedPref.getShowingLiveUpdateMarker(activity);
//                if (showingDriverMarkerLive) {
//                    driverId = CredentialsSharedPref.getDriverId(activity).toLowerCase();
//                }
//
//                for (DataSnapshot driver : dataSnapshot.getChildren()) {
//                    if (showingDriverMarkerLive) {
//                        if (!driverId.isEmpty() && driverId != null) {
//                            DriverFcmUpdates dfu = new DriverFcmUpdates();
//
//                            dfu.setLatitude(driver.child(driverId).getValue(DriverFcmUpdates.class).getLatitude());
//                            dfu.setLongitude(driver.child(driverId).getValue(DriverFcmUpdates.class).getLongitude());
//
//                            dfu.setBearing(driver.child(driverId).getValue(DriverFcmUpdates.class).getBearing());
//
//                            try {
//                                 final double latitude = Double.parseDouble(dfu.getLatitude());
//                                final  double longitude = Double.parseDouble(dfu.getLongitude());
//                               final float bearing = dfu.getBearing();
//                                Log.d(TAG, "onDataChange: "+ bearing +" "+ latitude + longitude);
//
//                                Handler handler;
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        if (first) {
////                                            first = false;
////                                            CreateMarker(latitude, longitude, bearing);
////                                        }else {
////                                            if (mk != null){
////                                                MarkerAnimation.animateMarkerToICS(mk, new LatLng(latitude, longitude), new com.prts.pickccustomer.map.LatLngInterpolator.Spherical());
////                                            }
////                                        }
//
//                                        addMarker(latitude, longitude, bearing);
//                                    }
//                                }, 100);
//
//                            }
//                            catch (Exception ae)
//                            {
//                                ae.printStackTrace();
//                            }
//
//
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        if (CredentialsSharedPref.getShowingLiveUpdateMarker(activity) && mlistener != null) {
//            String driverId = null;
//            try {
//                driverId = CredentialsSharedPref.getDriverId(activity);
//            } catch (Exception ae) {
//                ae.printStackTrace();
//            }
////           if(driverId!=null && !driverId.isEmpty()) {
////                mdatabaserefence.addValueEventListener(mlistener);
////           }
//        }
    }




    @Override
    public void onPause() {
        super.onPause();
//        showDriverMarker=false;
        if (mlistener != null) {
//            mdatabaserefence.removeEventListener(mlistener);
        }

    }
//   private int markerCount =0 ;
//    Marker mk = null;
//    public void addMarker( double lat, double lon) {
//        GoogleMap googlesMap = ((HomeActivity) activity).map;
//        int vehicleType = CredentialsSharedPref.getVehicleType(activity);
//        if (markerCount == 1) {
//            animateMarker(lat,lon, mk);
//        } else if (markerCount == 0) {
//            //Set Custom BitMap for Pointer
//            int height = 100;
//            int width = 100;
//
//            try {
//                BitmapDrawable bitmapdraw;
//
//                if (vehicleType == 1300) {
//
//                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.open_truck_symbol_marker);
//
//                } else {
//                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.closed_truck_symbol_marker);
//                }
////            if (vehicleType == 1300) {
////            BitmapDrawable bitmapdraw = (BitmapDrawable) activity.getResources().getDrawable(R.drawable.open_truck_symbol_marker);
//                Bitmap b = bitmapdraw.getBitmap();
//                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//
//
//                LatLng latlong = new LatLng(lat, lon);
//                mk = googlesMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
//                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
//                        .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
//                googlesMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
//            }catch (Exception ae)
//            {
//                ae.printStackTrace();
//            }
//            //Set Marker Count to 1 after first marker is created
//            markerCount = 1;
//
//            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                return;
//            }
//            //mMap.setMyLocationEnabled(true);
//            startLocationUpdates();
//        }
//    }


    private Marker truckMarker;
    LatLngInterpolator latLngInterpolator = null;

    private void onTruckLocationUpdated(Location location) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        GoogleMap googleMap = ((HomeActivity) activity).map;

        if(latLngInterpolator == null) {
            latLngInterpolator = new LatLngInterpolator.LinearFixed();
        }

        if (googleMap != null) {
            if (truckMarker == null) {
                Log.d("Marker", "truckMarker is null inside onLocationChanged ");
                truckMarker = googleMap
                        .addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(),
                                        location.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.open_truck_symbol_marker)).flat(true)
                                .anchor(.5f, .5f));
            } else {
                Log.d("Marker", "truckMarker is not null inside onLocationChanged ");
                animateMarkerLocationToGBP(truckMarker, locationTOLatLng(location),
                        latLngInterpolator);
            }
        }
    }

    private void animateMarkerLocationToGBP(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        final float durationInMs = 1500;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                try{
                    if(marker != null){
                        marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                    }
                }catch(Exception e){
                    Log.e(TAG,"Exception while changing the marker position with animation : "+e.toString());
                }

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public static LatLng locationTOLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }



    Marker mk = null;
    public void CreateMarker(double latitude, double longitude)
    {

        GoogleMap googleMap = ((HomeActivity) activity).map;
//       googleMap.clear();
        int vehicleType = CredentialsSharedPref.getVehicleType(activity);
        LatLng latlong = new LatLng(latitude, longitude);
        if(pinMode=="initialized") {
            animateMarker(latlong, mk);
        }

        int height = 80;
        int width = 45;
        try {
            BitmapDrawable bitmapdraw;

            if (vehicleType == 1300) {

                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.open_truck_symbol_marker);

            } else {
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.closed_truck_symbol_marker);
            }
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            mk = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
//            mk.setRotation(90);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
        }
        catch ( Exception ae)
        {
            ae.printStackTrace();
        }
        pinMode="initialized";

//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
//        startLocationUpdates();
//        Marker marker2 = googleMap.addMarker(createMarker(latitude, longitude, VEHICLE_TYPE_ICON));

//        Marker[] markers = {marker1};
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : markers) {
//            builder.include(marker.getPosition());
//        }
//        LatLngBounds bounds = builder.build();
//
//        int padding = 20; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        googleMap.moveCamera(cu);
//        googleMap.animateCamera(cu);


//        animateMarker(mLastLocation,mk);
    }
    public static void animateMarker(final LatLng destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            marker.remove();
            final LatLng endPosition = destination;

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(100); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);

                        Location loc = null;
                        loc.setLatitude(destination.latitude);
                        loc.setLatitude(destination.longitude);
//                        float brngs = (float)angleFromCoordinate(startPosition.latitude,startPosition.longitude,destination.latitude,destination.longitude);
//                        marker.setRotation(computeRotation(v, startRotation, bearing));

                        rotateMarker(marker,startRotation,loc.getBearing() );
                    } catch (Exception ex) {
                        // I don't care atm..


                        Log.d(TAG, "onAnimationUpdate: "+ex.toString());
                    }
                }
            });

            valueAnimator.start();
        }
    }
    public static void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = st;
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }




//    Marker mk = null;
//    public void CreateMarker(double latitude, double longitude,float bearing)
//    {
//
//        GoogleMap googleMap = ((HomeActivity) activity).map;
////       googleMap.clear();
//        int vehicleType = CredentialsSharedPref.getVehicleType(activity);
//        LatLng latlong = new LatLng(latitude, longitude);
//         if(pinMode=="initialized") {
//             animateMarker(latlong, mk,bearing);
//        }
//
//        int height = 80;
//        int width = 45;
//        try {
//            BitmapDrawable bitmapdraw;
//
//            if (vehicleType == 1300) {
//
//                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.open_truck_symbol_marker);
//
//            } else {
//                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.closed_truck_symbol_marker);
//            }
//            Bitmap b = bitmapdraw.getBitmap();
//            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//
//            mk = googleMap.addMarker(new MarkerOptions().rotation(bearing).position(new LatLng(latitude, longitude)).flat(true)
//                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
////            mk.setRotation(90);
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
//        }
//        catch ( Exception ae)
//        {
//            ae.printStackTrace();
//        }
//        pinMode="initialized";
//
////        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
////        startLocationUpdates();
////        Marker marker2 = googleMap.addMarker(createMarker(latitude, longitude, VEHICLE_TYPE_ICON));
//
////        Marker[] markers = {marker1};
////        LatLngBounds.Builder builder = new LatLngBounds.Builder();
////        for (Marker marker : markers) {
////            builder.include(marker.getPosition());
////        }
////        LatLngBounds bounds = builder.build();
////
////        int padding = 20; // offset from edges of the map in pixels
////        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
////        googleMap.moveCamera(cu);
////        googleMap.animateCamera(cu);
//
//
////        animateMarker(mLastLocation,mk);
//    }

    MapAsync mapAsync;
    protected void startLocationUpdates() {
        mapAsync = new MapAsync();
        mapAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);


    }

//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }



    private void enableTripStarted() {
        HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.GONE);
        }
        book_now_textView.setVisibility(View.GONE);
        bookLaterTV.setVisibility(View.GONE);
        bookingCancelTV.setVisibility(View.GONE);
        book_confirm_textView.setVisibility(View.GONE);
        ((HomeActivity) activity).locationLLL.setVisibility(View.GONE);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        driverDetailsFragment = DriverDetailsFragment.getInstanceOfDriverDetailsFragment(newBookingInfo, BookingFragment.this, true, false);
        fragmentTransaction.replace(R.id.driverDetailsFragment, driverDetailsFragment);
        Log.d(TAG, "enableAcceptedBooking: fragment attached");
        fragmentTransaction.commitAllowingStateLoss();
        // fragmentTransaction.commit();
//        showBothMarkersOnMap(new LatLng(newBookingInfo.getDriverLatitude(), newBookingInfo.getDriverLongitude()),
//                ((HomeActivity) activity).toLatLng, TO_ICON);
    }

    private void enableCancelBooking() {
        HomeActivity.onBackPressStatus = DISABLE_ON_BACK_PRESS;
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.GONE);
        }
        book_now_textView.setVisibility(View.GONE);
        bookLaterTV.setVisibility(View.GONE);
        //Todo
        bookingCancelTV.setVisibility(View.VISIBLE);
        book_confirm_textView.setVisibility(View.GONE);

        if (driverDetailsFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(driverDetailsFragment);
            Log.d(TAG, "enableCancelBooking: fragment removed");
            fragmentTransaction.commitAllowingStateLoss();
        }

    }

    private void enableConfirmBooking(boolean isBookingLater) {
        this.isBookingLater = isBookingLater;

        HomeActivity.onBackPressStatus = ENABLE_BOOKING;
        if (((HomeActivity) activity).truckCateogeoriesFragment.topLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
        }
        if (((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL != null) {
            ((HomeActivity) activity).truckCateogeoriesFragment.confirmBookingRateCardLL.setVisibility(View.VISIBLE);
        }
        book_now_textView.setVisibility(View.GONE);
        bookLaterTV.setVisibility(View.GONE);
        bookingCancelTV.setVisibility(View.GONE);
        book_confirm_textView.setVisibility(View.VISIBLE);

        if (driverDetailsFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(driverDetailsFragment);
            Log.d(TAG, "enableConfirmBooking: fragment removed");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         activity = (HomeActivity)context;
    }

    public boolean lockBothLocations() {

        String current = activity.currentSelection;
        activity.currentSelection = HomeActivity.FROM;
        activity.setLocationOnClick(null);
        activity.currentSelection = HomeActivity.TO;
        activity.setLocationOnClick(null);
        activity.currentSelection = current;

        activity.locationLLL.setVisibility(View.GONE);
        return areLocationsLocked(activity);

    }

    private boolean areLocationsLocked(HomeActivity activity) {
        if (activity.isFromLocationLocked && activity.isToLocationLocked) {
            return true;
        }
        return false;
    }

    public void cancelBookingInfo(String bookingNo) {
        createCancelRemarksDialogAndShow(bookingNo);

    }

    private void cancelBooking(String bookingNo, final String cancelRemarks) {
        String url = WEB_API_ADDRESS + BOOKING_CANCEL_API_CALL;
//        String url =  "http://192.168.0.111/PickCApi/" +  BOOKING_CANCEL_API_CALL;

        OkHttpClient mOkHttpClient;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        FormBody.Builder builder = new FormBody.Builder();



        builder.add(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY, bookingNo);
        builder.add(REMARKS_CANCEL_JSON_KEY, cancelRemarks);

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
                String fai = e.getMessage();
                Log.d(TAG, "onFailure: "+fai);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "onResponse: "+res);

            }
        });

//        JSONObject obj = new JSONObject();
//        try {
//            obj.put(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY, bookingNo);
//            obj.put(REMARKS_CANCEL_JSON_KEY, cancelRemarks);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse: cancelBooking response" + response);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                //displayErrorMessage("Please check your internet connection");
//                Log.d(TAG, "onErrorResponse: cancelBooking 7896 " + error);
//                //TODO: handle failure
//
//                if ((error + "").contains("TimeoutError")) {
//                    displayErrorMessage("Server busy. Please try again after some time");
//                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                            new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//                                    cancelBooking(CredentialsSharedPref.getBookingNO(activity), cancelRemarks);
//
//                                }
//                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
//                    return;
//                }
//                if ((error + "").contains("DELETED")) {
//                    Log.d(TAG, "onErrorResponse: cancelBooking success");
//                }
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(activity);
//            }
//        };
//        Volley.newRequestQueue(activity).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }

    //1253 confirm booking
    public  void callBookNowAPI(final int vehicleGroup, final int vehicleType,
                                @NonNull final String vehicleGroupDescId,
                                final boolean isBookingLater, final int loadingUnloadingStatus) {

        //1253
        showProgress();
        String time = RegularMethods.getCurrentTime();
        final String currentDate = RegularMethods.getCurrentDate() + " " + time;
        Log.e(TAG, "callBookNowAPI: date " + currentDate);

        //1253
        String url = WEB_API_ADDRESS + BOOKING_API_CALL;
//    String url = "http://192.168.0.107/PickCApi/"+BOOKING_API_CALL;

        double fromLAT = CredentialsSharedPref.getFromoLat(activity);
        double fromLNG = CredentialsSharedPref.getFromLong(activity);
        double toLAT = CredentialsSharedPref.getToLat(activity);
        double toLNG = CredentialsSharedPref.getToLong(activity);
        int loadingUnloadingsts = CredentialsSharedPref.getLoadingUnloadingStatus(activity);



        OkHttpClient mOkHttpClient;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        FormBody.Builder builder = new FormBody.Builder();


        builder.add(BOOKING_NO_BOOKING_JSON_KEY, "");
        builder.add(BOOKING_DATE_BOOKING_JSON_KEY, currentDate);
        builder.add(CUSTOMER_ID_BOOKING_JSON_KEY, CredentialsSharedPref.getMobileNO(activity));

        if (isBookingLater) {
            builder.add(REQUIRED_DATE_BOOKING_JSON_KEY, bookingRequiredDate);
        } else {
            builder.add(REQUIRED_DATE_BOOKING_JSON_KEY, currentDate);
        }

        builder.add(LOCATION_FROM_BOOKING_JSON_KEY, HomeActivity.currentAddress);
        builder.add(LOCATION_TO_BOOKING_JSON_KEY, HomeActivity.toAddress);
        builder.add(CARGO_DESC_BOOKING_JSON_KEY, cargoDescription);

        builder.add(VEHICLE_TYPE_BOOKING_JSON_KEY, String.valueOf(vehicleType));
        builder.add(VEHICLE_GROUP_BOOKING_JSON_KEY, String.valueOf(vehicleGroup));
        builder.add(REMARKS_BOOKING_JSON_KEY, "Remarks will be here");

        builder.add(IS_CONFIRM_BOOKING_JSON_KEY, String.valueOf(false));
        builder.add(CONFIRM_DATE_BOOKING_JSON_KEY, currentDate);
        builder.add(DRIVER_ID_BOOKING_JSON_KEY, "");
        builder.add(VEHICLE_NO_BOOKING_JSON_KEY, "");

        builder.add(IS_CANCEL_BOOKING_JSON_KEY, String.valueOf(false));
        builder.add(CANCEL_TIME_BOOKING_JSON_KEY, currentDate);
        builder.add(CANCEL_REMARKS_BOOKING_JSON_KEY, cancelRemarks);

        builder.add(IS_COMPLETE_BOOKING_JSON_KEY, String.valueOf(false));
        builder.add(COMPLETE_TIME_BOOKING_JSON_KEY, currentDate);




        builder.add(LATITUDE, String.valueOf(fromLAT));
        builder.add(LONGITUDE, String.valueOf(fromLNG));
//
        builder.add(TO_LATITUDE, String.valueOf(toLAT));
        builder.add(TO_LONGITUDE, String.valueOf(toLNG));
        builder.add(RECEIVER_MOBILE_NO, deliveryPersonMobileNo);

        builder.add(LOADING_UNLOADING_JSON_KEY, String.valueOf((loadingUnloadingsts)));
//        String token = String.valueOf(CredentialsSharedPref.getAuthToken(getActivity()));
//        String mobile = String.valueOf(CredentialsSharedPref.getAuthToken(getActivity()));

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

                Log.d(TAG, "onFailure: Booking failure" + e.toString());
                CredentialsSharedPref.setCallBookNowAPI(activity,false);


                if (e.toString().contains("TimeoutError")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            displayErrorMessage("Server busy. Please try again after some time");
                            AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                                    "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                                    new AlertDialogActivity.OnPositiveBtnClickListener() {
                                        @Override
                                        public void onPositiveBtnClick() {


//                                             callBookNowAPI(vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
                                        }
                                    }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                        @Override
                                        public void onNegativeBtnClick() {
                                            enableBooking();
                                            ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
                                        }
                                    });
                            return;

                        }
                    });

        }

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                Log.d(TAG, "onResponse: "+response);

                 String bookingNo = "";
                String string_res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(string_res);
                    String status = jsonObject.getString("Status");
                    if (status.equalsIgnoreCase("Booking Cancelled by System")) {
                        Toast.makeText(getActivity(), "Trucks are Offline", Toast.LENGTH_SHORT).show();

                        enableBooking();
                        ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
                    }

                } catch (Exception ae) {

                }
                Log.e("LOGIN", string_res);

//                hideProgress();

                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(string_res);
                    bookingNo = jsonObject.getString(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY);
                    Log.d(TAG, "onResponse: bookingNo " + bookingNo);
                    Log.d(TAG, "onResponse: " + response);
//                    CredentialsSharedPref.setCallBookNowAPI(activity,false);
                    CredentialsSharedPref.setBookingNo(activity, bookingNo);
//                    CredentialsSharedPref.setToLat(activity, ((HomeActivity) activity).toLatLng.latitude);
//                    CredentialsSharedPref.setToLong(activity, ((HomeActivity) activity).toLatLng.longitude);
                    startProg(bookingNo);


                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableBooking();
                            ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
                        }
                    });

                }
            }



            private void startProg(String bookingNo) {
                if (bookingNo != null) {

                    if (!isBookingLater) {
                        startCheckingIsConfirmed(bookingNo);
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                startProgressTime(HomeActivity.indicatorView, ((HomeActivity) getActivity()).tv_forTime_2, ((HomeActivity) getActivity()).textView_for_time, vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);

                            }
                        });

//                        startProgressTime(HomeActivity.indicatorView, ((HomeActivity) getActivity()).tv_forTime_2, ((HomeActivity) getActivity()).textView_for_time, vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
//                        startProgressTime(((HomeActivity) getActivity()).progressBarForTime, ((HomeActivity) getActivity()).tv_forTime_2, ((HomeActivity) getActivity()).textView_for_time, vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);

                    } else {
                        enableBooking();
                        ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
                    }
                    if (isBookingLater) {
                        speakOut("Truck scheduled successfully");
                        AlertDialogActivity.showAlertDialogActivity(getActivity(), "Truck Booked",
                                "Truck scheduled with " + bookingNo + " at " + bookingRequiredDate + ".", "OK", null,
                                new AlertDialogActivity.OnPositiveBtnClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        new Intent(getActivity(), HomeActivity.class);
                                    }
                                }, null);
                        displayErrorMessage("Truck scheduled successfully.\nDriver will arrive at " + bookingRequiredDate);
                    } else {
                        speakOut("Truck booked successfully");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Truck booked successfully. Waiting for driver confirmation", Toast.LENGTH_SHORT).show();
                            }
                        });

//                    displayErrorMessage("Truck booked successfully.\nWaiting for driver confirmation");
                    }
//                    Log.e(TAG, "onResponse: callBookNowAPI " + response);

                    //hideProgressDialog();
                }
            }





        });

        this.isBookingLater = false;
    }




//        final JSONObject obj = new JSONObject();
//        try {
//            obj.put(BOOKING_NO_BOOKING_JSON_KEY, "");
//            obj.put(BOOKING_DATE_BOOKING_JSON_KEY, currentDate);
//            obj.put(CUSTOMER_ID_BOOKING_JSON_KEY, CredentialsSharedPref.getMobileNO(activity));
//            if (isBookingLater) {
//                obj.put(REQUIRED_DATE_BOOKING_JSON_KEY, bookingRequiredDate);
//            } else {
//                obj.put(REQUIRED_DATE_BOOKING_JSON_KEY, currentDate);
//            }
//
//            obj.put(LOCATION_FROM_BOOKING_JSON_KEY, HomeActivity.currentAddress);
//            obj.put(LOCATION_TO_BOOKING_JSON_KEY, HomeActivity.toAddress);
//            obj.put(CARGO_DESC_BOOKING_JSON_KEY, cargoDescription);
//
//            obj.put(VEHICLE_TYPE_BOOKING_JSON_KEY, vehicleType);
//            obj.put(VEHICLE_GROUP_BOOKING_JSON_KEY, vehicleGroup);
//            obj.put(REMARKS_BOOKING_JSON_KEY, "Remarks will be here");
//
//            obj.put(IS_CONFIRM_BOOKING_JSON_KEY, false);
//            obj.put(CONFIRM_DATE_BOOKING_JSON_KEY, currentDate);
//            obj.put(DRIVER_ID_BOOKING_JSON_KEY, "");
//            obj.put(VEHICLE_NO_BOOKING_JSON_KEY, "");
//
//            obj.put(IS_CANCEL_BOOKING_JSON_KEY, false);
//            obj.put(CANCEL_TIME_BOOKING_JSON_KEY, currentDate);
//            obj.put(CANCEL_REMARKS_BOOKING_JSON_KEY, cancelRemarks);
//
//            obj.put(IS_COMPLETE_BOOKING_JSON_KEY, false);
//            obj.put(COMPLETE_TIME_BOOKING_JSON_KEY, currentDate);
//
//            obj.put(LATITUDE, ((HomeActivity) activity).fromLatLng.latitude);
//            obj.put(LONGITUDE, ((HomeActivity) activity).fromLatLng.longitude);
//
//            obj.put(TO_LATITUDE, ((HomeActivity) activity).toLatLng.latitude);
//            obj.put(TO_LONGITUDE, ((HomeActivity) activity).toLatLng.longitude);
//            obj.put(RECEIVER_MOBILE_NO, deliveryPersonMobileNo);
//            obj.put(LOADING_UNLOADING_JSON_KEY, loadingUnloadingStatus);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.e(TAG, "callBookNowAPI: " + obj.toString());
//
//         jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        //1253
//                        hideProgress();
//                        String bookingNo = "";
//                        Log.e(TAG, "onResponse: " + response);
//                        try {
//                            String status = response.getString("Status");
//                            if ((status + "").contains("Booking Cancelled by System is Failed")) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        enableBooking();
//                                        ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                                        displayErrorMessage("Unable to delete");
//                                    }
//                                }, 3000);
//                                return;
//                            }
//                        }
//                        catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                        try {
//                            String status = response.getString("Status");
//                            if ((status + "").contains("Booking Cancelled by System")) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        enableBooking();
//                                        ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                                        displayErrorMessage("No drivers are available");
//                                    }
//                                }, 3000);
//                                return;
//                            }
//                        }
//                        catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                        try {
//
//                            bookingNo = response.getString(BOOKING_NO_RESPONSE_BOOKING_JSON_KEY);
//                            Log.d(TAG, "onResponse: bookingNo " + bookingNo);
//                            CredentialsSharedPref.setBookingNo(activity, bookingNo);
//                            CredentialsSharedPref.setToLat(activity,((HomeActivity) activity).toLatLng.latitude);
//                            CredentialsSharedPref.setToLong(activity,((HomeActivity) activity).toLatLng.longitude);
//
//                            if (!isBookingLater) {
//                                speakOut("Truck booked successfully");
//                                displayErrorMessage("Truck booked successfully.\nWaiting for driver confirmation");
//                                startCheckingIsConfirmed(bookingNo);
//
//                                startProgressTime(HomeActivity.indicatorView, ((HomeActivity) getActivity()).tv_forTime_2, ((HomeActivity) getActivity()).textView_for_time, vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
//
//                            }else {
//                                enableBooking();
//                                ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getActivity(), "Trucks are Busy", Toast.LENGTH_SHORT).show();
//                            enableBooking();
//                            ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                        }
//                        if (isBookingLater){
//                            speakOut("Truck scheduled successfully");
//                            AlertDialogActivity.showAlertDialogActivity(getActivity(), "Truck Booked",
//                                    "Truck scheduled with "+bookingNo+" at "+bookingRequiredDate+".", "OK", null,
//                                    new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                        @Override
//                                        public void onPositiveBtnClick() {
//                                            new Intent(getActivity(), HomeActivity.class);
//                                        }
//                                    }, null);
//                            displayErrorMessage("Truck scheduled successfully.\nDriver will arrive at "+bookingRequiredDate);
//                        }else {
////                            speakOut("Truck booked successfully");
////                            displayErrorMessage("Truck booked successfully.\nWaiting for driver confirmation");
//                        }
//                        Log.e(TAG, "onResponse: callBookNowAPI " + response);
//
//                        //hideProgressDialog();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(final VolleyError error) {
//                        hideProgress();
//                        onError(error);
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(activity);
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(activity);
//       queue.add(jsObjRequest);
//        this.isBookingLater = false;
//    }
//
//    public void onError(VolleyError error) {
//
//        if (error.toString().contains("TimeoutError")) {
//            displayErrorMessage("Server busy. Please try again after some time");
//            AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                    "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                    new AlertDialogActivity.OnPositiveBtnClickListener() {
//                        @Override
//                        public void onPositiveBtnClick() {
//
//                            RequestQueue queue = Volley.newRequestQueue(activity);
//                            queue.add(jsObjRequest);
//                           // callBookNowAPI(vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
//                        }
//                    }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                        @Override
//                        public void onNegativeBtnClick() {
//                            enableBooking();
//                            ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                        }
//                    });
//            return;
//        }
//        Log.e(TAG, "onErrorResponse: callBookNowAPI " + error);
//        if ((error + "").contains("SAVED SUCSSFULLY")) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    displayErrorMessage("Trip Saved Successfully");
//                }
//            }, 3000);
//            return;
//        }
//        if ((error + "").contains("Booking Cancelled by System")) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    displayErrorMessage("Drivers are offline");
//                }
//            }, 3000);
//            return;
//        }
//        if ((error + "").contains("Booking Cancelled by System is Failed")) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    displayErrorMessage("Unable to delete");
//                }
//            }, 3000);
//            return;
//        }
//        displayErrorMessage("Please check your internet connection");
//
//        //hideProgressDialog();
//        AlertDialogActivity.showAlertDialogActivity(activity, "Something went wrong",
//                "Unable to process. Please check your internet connection. Do you want to retry...", "Retry", "Cancel",
//                new AlertDialogActivity.OnPositiveBtnClickListener() {
//                    @Override
//                    public void onPositiveBtnClick() {
//
//                        RequestQueue queue = Volley.newRequestQueue(activity);
//                        queue.add(jsObjRequest);
//                       // callBookNowAPI(vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
//                    }
//                }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                    @Override
//                    public void onNegativeBtnClick() {
//                        enableBooking();
//                        ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                    }
//                });
//        return;
//    }

    private void startCheckingIsConfirmed(String bookingNO) {
        getIsConfirmBookingsInfoFromServer(bookingNO);
//        handler = new Handler();
    }

    IsConfirmBookingInfo newBookingInfo = new IsConfirmBookingInfo();
    Handler handler;

    private void getIsConfirmBookingsInfoFromServer(final String bookingNO) {

        Log.d(TAG, "isMobileNoExistsInServer: getIsConfirmBookingsInfoFromServer");
      String url = WEB_API_ADDRESS + BOOKING_INFO_IS_CONFIRM_API_CALL_BASED_ON_BOOKING_NO + bookingNO;
//        String url = "http://192.168.0.107/PickCApi/"+BOOKING_INFO_IS_CONFIRM_API_CALL_BASED_ON_BOOKING_NO + bookingNO;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 getIsConfirmBookingsInfoFromServer " + response);
                // displayErrorMessage("Logged In Successfully. Please wait");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        newBookingInfo = new IsConfirmBookingInfo();
                        JSONObject isConfirmBookingInfoJsonObject = response;
                        Log.d(TAG, "onResponse: jsonObject getIsConfirmBookingsInfoFromServer " + isConfirmBookingInfoJsonObject);
                        newBookingInfo.updateIsConfirmBookingInfo(
                                bookingNO,
                                CredentialsSharedPref.getMobileNO(getActivity()),
                                isConfirmBookingInfoJsonObject.getBoolean(BOOKING_IS_CONFIRM_BOOKING_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getString(DRIVER_ID_IS_CONFIRM_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getString(VEHICLE_NO_IS_CONFIRM_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getString(DRIVER_NAME_IS_CONFIRM_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getString(DRIVER_MOB_NO_IS_CONFIRM_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getString(DRIVER_IMAGE_IS_CONFIRM_JSON_KEY),
                                isConfirmBookingInfoJsonObject.getDouble(LATITUDE),
                                isConfirmBookingInfoJsonObject.getDouble(LONGITUDE),
                                isConfirmBookingInfoJsonObject.getString("OTP"),
                                isConfirmBookingInfoJsonObject.getInt("VehicleType")

                        );
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CredentialsSharedPref.setDriverId(activity,newBookingInfo.getDriverId());
                                CredentialsSharedPref.setVehicleType(activity,newBookingInfo.getVehicleType());
                                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,true);
//                                enableDriverLocationChange();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!newBookingInfo.isConfirmed()) {

                            //getIsConfirmBookingsInfoFromServer(bookingNO);
                        } else {
                            handler=new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //displayErrorMessage("Your Booking confirmed by driver");
                                    if (driverDetailsFragment != null && driverDetailsFragment.eTA_Time_TV != null) {
                                        showSnackBar(bookingCancelTV, "Your Pick up arriving. " + driverDetailsFragment.eTA_Time_TV.getText().toString());
                                    }
                                    enableAcceptedBooking(newBookingInfo);
                                }
                            });
                        }
                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!newBookingInfo.isConfirmed()) {
//
//                            //getIsConfirmBookingsInfoFromServer(bookingNO);
//                        } else {
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //displayErrorMessage("Your Booking confirmed by driver");
//                                    if (driverDetailsFragment != null && driverDetailsFragment.eTA_Time_TV != null) {
//                                        showSnackBar(bookingCancelTV, "Your Pick up arriving. " + driverDetailsFragment.eTA_Time_TV.getText().toString());
//                                    }
//                                    enableAcceptedBooking(newBookingInfo);
//                                }
//                            });
//                        }
//                    }
//                }).start();
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, error.networkResponse + " getIsConfirmBookingsInfoFromServer error.networkResponse onErrorResponse:78945 " + error);

                if ((error + "").contains("TimeoutError")) {
                    displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    getIsConfirmBookingsInfoFromServer(bookingNO);
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
                return CredentialsSharedPref.getHeaders(getActivity());
            }
        };


        Volley.newRequestQueue(getActivity()).add(jsonArrayRequest);

    }

    private void displayErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
       // indicatorView.setVisibility(View.VISIBLE);
      // progressDialog.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
       // indicatorView.setVisibility(View.GONE);
       // progressDialog.setVisibility(View.GONE);
    }

    protected  void createDrivingRoute(LatLng sourcePosition, final LatLng destPosition, boolean canShowDriverLocation) {

        String mode = GMapV2Direction.MODE_DRIVING;

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions()
                            .width(10)
                            .color(Color.BLUE);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = ((HomeActivity) activity).map.addPolyline(rectLine);
                    md.getDurationText(doc);
                    boolean hasPoints = false;
                    Double maxLat = null, minLat = null, minLon = null, maxLon = null;

                    if (polylin != null && polylin.getPoints() != null) {
                        List<LatLng> pts = polylin.getPoints();
                        for (LatLng coordinate : pts) {
                            // Find out the maximum and minimum latitudes & longitudes
                            // Latitude
                            maxLat = maxLat != null ? Math.max(coordinate.latitude, maxLat) : coordinate.latitude;
                            minLat = minLat != null ? Math.min(coordinate.latitude, minLat) : coordinate.latitude;

                            // Longitude
                            maxLon = maxLon != null ? Math.max(coordinate.longitude, maxLon) : coordinate.longitude;
                            minLon = minLon != null ? Math.min(coordinate.longitude, minLon) : coordinate.longitude;

                            hasPoints = true;
                        }
                    }

                    if (hasPoints) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        builder.include(new LatLng(maxLat, maxLon));
                        builder.include(new LatLng(minLat, minLon));
                        ((HomeActivity) activity).map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
//                        ((HomeActivity) activity).map.addMarker()

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, mode).execute();
        if (canShowDriverLocation) {
            ((HomeActivity) activity).showBothMarkersIncludingDriverOnMap(sourcePosition);
//            ((HomeActivity) activity).showBothMarkersOnMap();
        } else {
            ((HomeActivity) activity).showBothFromTOLocationOnMapThroughMarkers(sourcePosition, destPosition);
//            ((HomeActivity) activity).showBothMarkersOnMap();
        }
    }

    int selectedYear;
    int selectedMonthOfYear;
    int selectedDayOfMonth;

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        showTimePickerDialogWithCurrentTime();
        selectedYear = year;
        selectedMonthOfYear = monthOfYear;
        selectedDayOfMonth = dayOfMonth;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay + timeSeperator + minute;
        Log.d(TAG, "onTimeSet: bookLater " + time);

        String date = selectedDayOfMonth + dateSeperator + selectedMonthOfYear + dateSeperator + selectedYear;
        Log.d(TAG, "onDateSet: bookLater " + date);
        //************************************************
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        long currentDateAndTimeInMilliSeconds = calendar.getTimeInMillis();
        calendar.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth,
                hourOfDay, minute, 0);
        long selectedDateAndTimeMilliSeconds = calendar.getTimeInMillis();
        //**************************************************
        if (selectedDateAndTimeMilliSeconds <= currentDateAndTimeInMilliSeconds) {
            showTimePickerDialogWithCurrentTime();
            Toast.makeText(activity, "Selected Time is less than current time", Toast.LENGTH_LONG).show();
            return;
        }
        Date date1 = new Date(selectedYear - 1900, selectedMonthOfYear, selectedDayOfMonth);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String cdate = formatter.format(date1);
        bookingRequiredDate = cdate + " " + time;
        //activity.setTitle(bookingRequiredDate);
        downloadCargoMaterialTypes();
        //enableConfirmBooking(true);
        Log.d(TAG, "onTimeSet: " + bookingRequiredDate);
    }

    int count = 0;

    public void blink(final View view, final int noOfTimes) {
        speakOut("Now you can proceed for booking");
        count = 0;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                if (count < noOfTimes) {
                    final int timeToBlink = 300;    //in millis seconds
                    handler.postDelayed(this, timeToBlink);
                    count++;
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }, 1);
    }

    @Override
    public void blinkBookingLayout() {
        Log.d(TAG, "blinkBookingLayout: ");
        blink(bookingLL, 6);
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit: status " + status);
    }

    public void speakOut(String message) {
        Log.d(TAG, "speakOut: message " + message);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(ANNOUNCEMENTS_SHARED_PREF, MODE_PRIVATE);
        boolean announcementStatus = sharedPreferences.getBoolean(ANNOUNCEMENT_SHARED_PREF_KEY, true);
        if (!announcementStatus) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(message);
        } else {
            ttsUnder20(message);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private String cargoDescription = "";
    ArrayList<String> cargoTypesDesAL = new ArrayList<>();
    ArrayList<String> urlPicsCargo = new ArrayList<>();
    ArrayList<Integer> cargoTypesIdsAL = new ArrayList<>();
    ArrayList<Integer> cargoTypesSelectedStatusAL = new ArrayList<>();
    boolean[] SELECTED_POS_OF_CARGO_TYPES = new boolean[1];

    private void createAndShowCargoMaterialDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle("Select Cargo Type");
        dialog.setContentView(R.layout.final_cargo_material_type_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        speakOut("Select a cargo type and Enter weight in kilograms");
        final PickCCustomEditText weightET = (PickCCustomEditText) dialog.findViewById(R.id.weightEditText);
        ImageView closeIv = (ImageView)dialog.findViewById(R.id.closeIV);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                enableBooking();
            }
        });
        weightET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
               /* if (!hasFocus){
                    weightET.setFocusable(true);
                }else {
                    weightET.setFocusable(false);
                }*/
            }
        });
        weightET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyBoard(v);
            }
        });
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargoDescription = "";
                for (int i = 0; i < SELECTED_POS_OF_CARGO_TYPES.length; i++) {
                    if (SELECTED_POS_OF_CARGO_TYPES[i]) {
                        if (cargoDescription.isEmpty()) {
                            cargoDescription = cargoTypesDesAL.get(i);
                        } else {
                            cargoDescription += ", " + cargoTypesDesAL.get(i);
                        }
                    }
                }
                Log.d(TAG, "onBookingConfirmed: okbtn cargoTypesSelectedStatusAL " + cargoTypesSelectedStatusAL);
                if (cargoDescription.isEmpty()) {
                    showSnackBar(v, "Please select at least one");
                    return;
                }
                if (!weightET.getText().toString().isEmpty()) {

                    double weight = Double.parseDouble(weightET.getText().toString());

                    if (weight != 0) {
                        final int selectedVehicleGroupID = ((HomeActivity) getActivity()).truckCateogeoriesFragment.selectedTruckID;
                        switch (selectedVehicleGroupID) {
//                            case 1000:
//                                if (weight > 0.75) {
//                                    showSnackBar(weightET, "Weight shouldn't be greater than 700 kgs");
//                                    return;
//                                }
//                                break;
//                            case 1001:
//                                if (weight > 1) {
//                                    showSnackBar(weightET, "Weight shouldn't be greater than 1030 kgs");
//                                    return;
//                                }
//                                break;
//                            case 1002:
//                                if (weight > 1.5) {
//                                    showSnackBar(weightET, "Weight shouldn't be greater than 1250 kgs");
//                                    return;
//                                }
//                                break;
//                            case 1003:
//                                if (weight > 2) {
//                                    showSnackBar(weightET, "Weight shouldn't be greater than 2700 kgs");
//                                    return;
//                                }
//                                break;
                            case 1000:
                                if (weight > 700 ) {
                                    showSnackBar(weightET, "Weight shouldn't be greater than 700 kgs");
                                    return;
                                }
                                break;
                            case 1001:
                                if (weight > 1030) {
                                    showSnackBar(weightET, "Weight shouldn't be greater than 1030 kgs");
                                    return;
                                }
                                break;
                            case 1002:
                                if (weight > 1250) {
                                    showSnackBar(weightET, "Weight shouldn't be greater than 1250 kgs");
                                    return;
                                }
                                break;
                            case 1003:
                                if (weight > 2700) {
                                    showSnackBar(weightET, "Weight shouldn't be greater than 2700 kgs");
                                    return;
                                }
                                break;
                        }
                        cargoDescription = "Total " + weight + " tons with " + cargoDescription + " materials.";
                    }
                }
                dialog.dismiss();
                createAndShowDeliveryPersonMobileNumberDialog();
            }
        });

        ListView cargoTypeListView = (ListView) dialog.findViewById(R.id.listView);
        SELECTED_POS_OF_CARGO_TYPES = new boolean[cargoTypesDesAL.size()];
        final CargoTypeAdapter typeAdapter = new CargoTypeAdapter(cargoTypesDesAL);
        cargoTypeListView.setAdapter(typeAdapter);
        cargoTypeListView.setItemsCanFocus(false);
        cargoTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cargoTypeListView.setAdapter(new CargoWeightAdapter(cargoWeightsDesAL));
                weightET.setFocusable(false);
                SELECTED_POS_OF_CARGO_TYPES[position] = !SELECTED_POS_OF_CARGO_TYPES[position];
                typeAdapter.notifyDataSetChanged();
                //Object object = parent.getItemAtPosition(position);
                Log.d(TAG, "onItemClick: cargoTypeListView position" + position);
            }
        });
    }

    private void showKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private String deliveryPersonMobileNo = "";

    //1253
    private void createAndShowDeliveryPersonMobileNumberDialog() {
        final Dialog dialog = new Dialog(activity);
        //dialog.setTitle("Select Cargo Type");
        dialog.setContentView(R.layout.final_cargo_delivery_person_mobile_number_dialog_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        speakOut("Select use my mobile number or enter an alternative number");

        final PickCCustomEditText mobileNoET = (PickCCustomEditText) dialog.findViewById(R.id.mobileNoEditText);
        mobileNoET.setText(CredentialsSharedPref.getMobileNO(getActivity()));
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkBox);
        selectSameMoileNoCheckBox(checkBox, mobileNoET);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: b " + b);
                if (b) {
                    selectSameMoileNoCheckBox(checkBox, mobileNoET);
                } else {
                    unSelectSameMoileNoCheckBox(checkBox, mobileNoET);
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    deliveryPersonMobileNo = CredentialsSharedPref.getMobileNO(activity);
                } else {
                    String mobileNo = mobileNoET.getText().toString();
                    if (mobileNo.isEmpty() || mobileNo.length() < 10) {
                        showSnackBar(v, "Please enter valid 10 digit mobile No.");
                        return;
                    }
                    deliveryPersonMobileNo = mobileNo;
                }
                HomeActivity.onBackPressStatus = ENABLE_BOOKING;
                dialog.dismiss();
                speakOut("Click Confirm booking");
            }
        });
    }

    private void selectSameMoileNoCheckBox(CheckBox checkBox, PickCCustomEditText mobileNoET) {
        checkBox.setChecked(true);
        mobileNoET.setEnabled(false);
        Log.d(TAG, "selectSameMoileNoCheckBox: ");
    }

    private void unSelectSameMoileNoCheckBox(CheckBox checkBox, PickCCustomEditText mobileNoET) {
        checkBox.setChecked(false);
        mobileNoET.setEnabled(true);
        mobileNoET.requestFocus();
        Log.d(TAG, "unSelectSameMoileNoCheckBox: ");
    }

    private void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.appThemeYellow));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        snackView.setLayoutParams(params);
        snackbar.show();
    }
    /*class CargoWeightAdapter extends BaseAdapter{
        ArrayList<String> cargoWeightsDesAL;

        public CargoWeightAdapter(ArrayList<String> cargoWeightsDesAL) {
            this.cargoWeightsDesAL = cargoWeightsDesAL;
        }

        @Override
        public int getCount() {
            return cargoWeightsDesAL.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        int selectedPosition = 0;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
             convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cargo_material_weight_sub_layout, parent, false);
            }
            final RadioButton weightRadioButton = (RadioButton)convertView.findViewById(R.id.radioButton);
            weightRadioButton.setText(cargoWeightsDesAL.get(position));
            weightRadioButton.setChecked(position == selectedPosition);
            weightRadioButton.setTag(position);
            //weightRadioButton.setChecked(cargoWeightsSelectedStatusAL.get(position));
            weightRadioButton.setOnBookingConfirmedListener(new View.OnBookingConfirmedListener() {
                @Override
                public void onBookingConfirmed(View v) {

                    selectedPosition = (Integer)v.getTag();
                 *//*   Log.d(TAG, selectedPosition+" onCheckedChanged: "+isChecked);
                    boolean b = cargoWeightsSelectedStatusAL.get(selectedPosition);
                    cargoWeightsSelectedStatusAL.remove(selectedPosition);
                    cargoWeightsSelectedStatusAL.add(selectedPosition,true);*//*
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
*/

    class CargoTypeAdapter extends BaseAdapter {
        ArrayList<String> cargoTypesDesAL;

        public CargoTypeAdapter(ArrayList<String> cargoTypesDesAL) {
            this.cargoTypesDesAL = cargoTypesDesAL;
        }

        @Override
        public int getCount() {
            return cargoTypesDesAL.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {



                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_cargo_material_type_sub_layout, parent, false);
                CheckBox typeRadioButton = (CheckBox) view.findViewById(R.id.checkBox);
                ImageView imageview = (ImageView) view.findViewById(R.id.image_id);
                String url = urlPicsCargo.get(position) ;

                if (url.length()!=0) {
                    Picasso.with(getContext()).load(urlPicsCargo.get(position)).into(imageview);
                }
                typeRadioButton.setText(cargoTypesDesAL.get(position));
                typeRadioButton.setTag(cargoTypesIdsAL.get(position));

                typeRadioButton.setChecked(SELECTED_POS_OF_CARGO_TYPES[position]);
                if (SELECTED_POS_OF_CARGO_TYPES[position]) {
                    typeRadioButton.setBackgroundColor(getResources().getColor(R.color.appThemeYellow));
                } else {
                    typeRadioButton.setBackgroundColor(getResources().getColor(R.color.appThemeBgColorDark));
                }


            /*//typeRadioButton.setChecked(false);
            Log.d(TAG, "getView: cargoTypesSelectedStatusAL " + cargoTypesSelectedStatusAL);
            typeRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer id = (Integer) v.getTag();
                    if (cargoTypesSelectedStatusAL.contains(id)) {
                        cargoTypesSelectedStatusAL.remove(id);
                    } else {
                        cargoTypesSelectedStatusAL.add(id);
                    }
                    Log.d(TAG, "onBookingConfirmed: cargoTypesSelectedStatusAL " + cargoTypesSelectedStatusAL);
                }
            });*/
                return view;

            }

    }

    boolean disableOnBackPressed = false;

    private void downloadCargoMaterialTypes() {

        final ProgressDialog progressDialog = new ProgressDialog(activity, R.style.AppDialogTheme1);
        progressDialog.setTitle("Loading cargo types");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HomeActivity.onBackPressStatus = DISABLE_ON_BACK_PRESS;
        String url = WEB_API_ADDRESS + CARGO_TYPE_LIST_API_CALL;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                clearAllCargoALs();
                //hideProgressDialog();
                progressDialog.dismiss();
                HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
//                ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
                Log.d(TAG, "onResponse: " + response);
                for (int i = 0; i < response.length(); i++) {
                        try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int cargoTypeId = jsonObject.getInt(CARGO_TYPE_ID_JSON_KEY);
                        String cargoTypeDes = jsonObject.getString(CARGO_TYPE_DES_JSON_KEY);
                            String urlsImagesCargo = jsonObject.getString("Image");
                        cargoTypesIdsAL.add(cargoTypeId);
                        cargoTypesDesAL.add(cargoTypeDes);
                           urlPicsCargo.add(urlsImagesCargo);

                       /* double baseKM = jsonObject.getDouble(BASE_KM_JSON_KEY);
                        double distanceFare = jsonObject.getDouble(DISTANCE_FARE_JSON_KEY);
                        double waitingFare = jsonObject.getDouble(WAITING_FARE_JSON_KEY);
                        double rideTimeFare = jsonObject.getDouble(RIDE_TIME_FARE_JSON_KEY);
                        double cancellationFee = jsonObject.getDouble(CANCELLATION_FEE_JSON_KEY);

                        truckCategeoryIdsAL.add(cateogeoryId);
                        baseFaresAL.add(baseFare);
                        baseKMsAL.add(baseKM);
                        distanceFaresAL.add(distanceFare);
                        waitingFaresAL.add(waitingFare);
                        rideTimeFaresAL.add(rideTimeFare);
                        cancellationFeeAL.add(cancellationFee);*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                createAndShowCargoMaterialDialog();

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideProgressDialog();
                progressDialog.dismiss();
                HomeActivity.onBackPressStatus = FINISH_ACTIVITY;
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    //Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    downloadCargoMaterialTypes();
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }

                // Toast.makeText(activity, "Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:Home8465 " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };


        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    private void clearAllCargoALs() {
        // cargoWeightsDesAL.clear();
        cargoTypesDesAL.clear();
        cargoTypesIdsAL.clear();
        cargoTypesSelectedStatusAL.clear();
    }

    @Override
    public void onDestroyView() {
        //Close the Text to Speech Library
        if (tts != null) {

            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroyView();
    }

    public static final String FROM_ICON = "from_icon";
    public static final String TO_ICON = "to_icon";
    public static final String VEHICLE_TYPE_ICON = "vehicle_icon";

    private MarkerOptions createMarker(double latitude, double longitude, String iconType) {

        BitmapDescriptor icon = sourceIcon;
        switch (iconType) {
            case FROM_ICON:
                icon = sourceIcon;
                break;
            case TO_ICON:
                icon = destinationIcon;
                break;
            case VEHICLE_TYPE_ICON:
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.open_truck_symbol_marker);
                largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);

                if (((HomeActivity) activity).truckCateogeoriesFragment.selectedvehicleTypeID == VEHICLE_TYPE_CLOSED) {
                    largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.closed_truck_symbol_marker);
                    largeIcon = Bitmap.createScaledBitmap(largeIcon, 100, 100, false);
                }
                icon = BitmapDescriptorFactory.fromBitmap(largeIcon);
                break;
        }

        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .icon(icon);
    }

    public void showBothMarkersOnMap(LatLng vehicleLatLong, LatLng toLatLong, String toLatLongType) {
        GoogleMap googleMap = ((HomeActivity) activity).map;
        googleMap.clear();

        Marker marker1 = googleMap.addMarker(createMarker(vehicleLatLong.latitude, vehicleLatLong.longitude, VEHICLE_TYPE_ICON));
        Marker marker2 = googleMap.addMarker(createMarker(toLatLong.latitude, toLatLong.longitude, toLatLongType));

        Marker[] markers = {marker1, marker2};
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);

    }

    private void createCancelRemarksDialogAndShow(final String bookingNo) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.final_trip_cancel_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        PickCCustomTextVIew cancelBtn = (PickCCustomTextVIew) dialog.findViewById(R.id.cancelBookingTV);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRemarks = "";
                for (int i = 0; i < SELECTED_POSITIONS_OF_CANCEL_REMARKS.length; i++) {
                    if (SELECTED_POSITIONS_OF_CANCEL_REMARKS[i]) {
                        if (cancelRemarks.isEmpty()) {
                            cancelRemarks = CANCEL_REMARKS[i];
                        } else {
                            cancelRemarks += ", " + CANCEL_REMARKS[i];
                        }
                    }
                }
                if (cancelRemarks.isEmpty()) {
                    Toast.makeText(activity, "Please select a reason to cancel", Toast.LENGTH_SHORT).show();
                    return;
                }
                cancelBooking(bookingNo, cancelRemarks);
                enableBooking();
                ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);

                stopProgressTime(HomeActivity.indicatorView,
                        ((HomeActivity) getActivity()).textView_for_time, ((HomeActivity) getActivity()).tv_forTime_2);

                dialog.dismiss();
            }
        });
        PickCCustomTextVIew doNotCancelBtn = (PickCCustomTextVIew) dialog.findViewById(R.id.donotCancelTV);
        doNotCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final ListView cancelRemarksListView = (ListView) dialog.findViewById(R.id.listViewCancelDialog);
        SELECTED_POSITIONS_OF_CANCEL_REMARKS = new boolean[CANCEL_REMARKS.length];
        final CancelRemarksAdapter cancelRemarksAdapter = new CancelRemarksAdapter();
        cancelRemarksListView.setAdapter(cancelRemarksAdapter);
        cancelRemarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (SELECTED_POSITIONS_OF_CANCEL_REMARKS[pos]) {

                } else {

                }
                SELECTED_POSITIONS_OF_CANCEL_REMARKS[pos] = !SELECTED_POSITIONS_OF_CANCEL_REMARKS[pos];
                boolean b = SELECTED_POSITIONS_OF_CANCEL_REMARKS[pos];
                for (int i = 0; i < SELECTED_POSITIONS_OF_CANCEL_REMARKS.length; i++) {
                    SELECTED_POSITIONS_OF_CANCEL_REMARKS[i] = false;
                }
                SELECTED_POSITIONS_OF_CANCEL_REMARKS[pos] = b;
                cancelRemarksAdapter.notifyDataSetChanged();

            }
        });

    }

    String cancelRemarks = "";
    final String[] CANCEL_REMARKS = {"Driver is late", "Changed my mind", "Booked another truck", "Driver denied duty"};
    boolean[] SELECTED_POSITIONS_OF_CANCEL_REMARKS = new boolean[CANCEL_REMARKS.length];

    private class CancelRemarksAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return CANCEL_REMARKS.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cancel_remarks_sub_layout, viewGroup, false);
            PickCCustomTextVIew cancelRemarksTV = (PickCCustomTextVIew) view1.findViewById(R.id.cancel_remarks_tv);
            LinearLayout cancelLL = (LinearLayout) view1.findViewById(R.id.cancel_remarks_ll);
            if (SELECTED_POSITIONS_OF_CANCEL_REMARKS[position]) {
                cancelLL.setBackgroundColor(getResources().getColor(R.color.appThemeYellow));
            } else {
                cancelLL.setBackgroundColor(getResources().getColor(R.color.appThemeBgColorDark));
            }
            cancelRemarksTV.setText(CANCEL_REMARKS[position]);
            return view1;
        }
    }

    MyCountDownTimer myCountDownTimer;

    public void startProgressTime(AVLoadingIndicatorView progressBar, PickCCustomTextVIew tv_forTime_2, PickCCustomTextVIew tv_forTime, int vehicleGroup, int vehicleType, String vehicleGroupDescId, boolean isBookingLater, int loadingUnloadingStatus) {
        int repeatTimes = 2;
        int totalTimeSec = 60;
        long totalTime = totalTimeSec * 1000;
        // todo
        /*tv_forTime.setVisibility(View.VISIBLE);*/
        tv_forTime.setVisibility(View.GONE);
        tv_forTime.setText(totalTimeSec + " s");
        // todo
        /*tv_forTime_2.setVisibility(View.VISIBLE);*/
        tv_forTime_2.setVisibility(View.GONE);
        tv_forTime_2.setText("1/2");

        myCountDownTimer = new MyCountDownTimer(totalTime, 1000, progressBar, repeatTimes, tv_forTime_2, totalTime, tv_forTime, vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
        myCountDownTimer.start();
        myCountDownTimer.isRunning = true;
        Animation an = new RotateAnimation(0.0f, 0.0f, 0f, 0f);
        an.setFillAfter(true);
        // todo

        HomeActivity.indicatorView.setVisibility(View.VISIBLE);

      /*  progressBar.setVisibility(View.VISIBLE);
        //progressBar.setBackgroundResource(R.drawable.alert_dialog_bg);
        progressBar.setProgress(100);
        progressBar.startAnimation(an);*/
    }

    public void stopProgressTime(final AVLoadingIndicatorView progressBar, final PickCCustomTextVIew tvForTime, PickCCustomTextVIew tv_forTime_2) {
        if (myCountDownTimer == null){
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myCountDownTimer != null && myCountDownTimer.isRunning) {
                    myCountDownTimer.cancel();
                }

            }
        }, 100);

        Log.d(TAG, "stopProgressTime: 12345689");
        myCountDownTimer.isRunning = false;
       // progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        progressBar.setBackgroundColor(Color.TRANSPARENT);
        tvForTime.setText("");
        tvForTime.setVisibility(View.GONE);
        tv_forTime_2.setText("");
        tv_forTime_2.setVisibility(View.GONE);

        myCountDownTimer = null;
        Log.d(TAG, "stopProgressTime: 123456892");
    }

    public class MyCountDownTimer extends CountDownTimer {
        AVLoadingIndicatorView progressBar;
        //ProgressBar progressBar;
        boolean isRunning = false;
        long totalTimeInMillis;
        PickCCustomTextVIew tvForTime;
        int vehicleGroup;
        int vehicleType;
        String vehicleGroupDescId;
        boolean isBookingLater;
        int loadingUnloadingStatus;
        PickCCustomTextVIew tv_forTime_2;
        int repeatTimes;
        int count = 1;


        public MyCountDownTimer(long millisInFuture, long countDownInterval, AVLoadingIndicatorView progressBa, int repeatTimes, PickCCustomTextVIew tv_forTime_2, long totalTimeInMillis, PickCCustomTextVIew tvForTime, int vehicleGroup, int vehicleType, String vehicleGroupDescId, boolean isBookingLater, int loadingUnloadingStatus) {
            super(repeatTimes*millisInFuture, countDownInterval);
            this.progressBar = progressBar;
            this.totalTimeInMillis = totalTimeInMillis;
            this.tvForTime = tvForTime;
            this.tv_forTime_2 = tv_forTime_2;
            this.vehicleGroup = vehicleGroup;
            this.vehicleType = vehicleType;
            this.vehicleGroupDescId = vehicleGroupDescId;
            this.isBookingLater = isBookingLater;
            this.loadingUnloadingStatus = loadingUnloadingStatus;
            this.repeatTimes = repeatTimes;
        }

        /*public MyCountDownTimer(long millisInFuture, long countDownInterval, ProgressBar progressBar, PickCCustomTextVIew tv_forTime, int vehicleGroup, int vehicleType, String vehicleGroupDescId, boolean isBookingLater, int loadingUnloadingStatus) {
            super(millisInFuture, countDownInterval);
            this.progressBar = progressBar;
            this.totalTimeInMillis = millisInFuture;
            this.tvForTime = tv_forTime;

        }*/

        @Override
        public void onTick(long millisUntilFinished) {
            //textCounter.setText(String.valueOf(millisUntilFinished));
            if (isRunning) {
                int currentMillis = (int) (millisUntilFinished - ((repeatTimes-count)*totalTimeInMillis));
                int progress = (int) (((double) currentMillis / (double) totalTimeInMillis) * (double) 100);
                Log.d(TAG, "onTick: progress " + progress);
              //  progressBar.setProgress(progress);
                if (currentMillis <= 0){
                    count++;
                }
                Log.d(TAG, currentMillis+" currentMillis onTick: repeatTimes "+ repeatTimes);
                tv_forTime_2.setText(count+"/"+repeatTimes);
                tvForTime.setText(((int) currentMillis/ 1000) + " s");
            }
            else {

                Log.d(TAG, "stopProgressTime: 12345689 3");
               // progressBar.setProgress(0);
//                progressBar.setVisibility(View.GONE);
               // progressBar.setBackgroundColor(Color.TRANSPARENT);

                Log.d(TAG, "stopProgressTime: 12345689 4");
            }
        }

        @Override
        public void onFinish() {
            tvForTime.setText("0 s");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   // progressBar.setProgress(0);

//                     progressBar = (AVLoadingIndicatorView) getView().findViewById(R.i)
//                    progressBar.setVisibility(View.GONE);
//                    progressBar.smoothToHide();
//                    HomeActivity.indicatorView.smoothToHide();
                    HomeActivity.indicatorView.hide();
                    CredentialsSharedPref.setCallBookNowAPI(activity,false);

                  //  progressBar.setBackgroundColor(Color.TRANSPARENT);
                    tvForTime.setText("");
                    tv_forTime_2.setVisibility(View.GONE);
                    tvForTime.setVisibility(View.GONE);
                }
            }, 100);
            if (isRunning) {
                isRunning = false;
                AlertDialogActivity.showAlertDialogActivity(activity, "Trucks are not avialable",
                        "Currently trucks are not available at your pick up location. Due to high demand.  Do you want to retry...", "Retry", "Cancel",
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                Log.d(TAG, "onPositiveBtnClick: progress");
                                callBookNowAPI(vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
                            }
                        }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                            @Override
                            public void onNegativeBtnClick() {
                                speakOut("Your booking is cancelled");
                                Log.d(TAG, "onNegativeBtnClick: progress");
                                cancelBooking(CredentialsSharedPref.getBookingNO(activity), "Time limit Exceeded");
                                enableBooking();
                                ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
                            }
                        });
                speakOut("Trucks not found Please select cancel or retry");
            }
            //textCounter.setText("Finished");
        }

    }
//    public static void animateMarker(final double latitude, final double longitude, final Marker marker) {
//        if (marker != null) {
//            final LatLng startPosition = marker.getPosition();
//            final LatLng endPosition = new LatLng(latitude, longitude);
//
//            final float startRotation = marker.getRotation();
//
//            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//            valueAnimator.setDuration(200); // duration 1 second
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    try {
//                        float v = animation.getAnimatedFraction();
//                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
//                        marker.setPosition(newPosition);
//                        Location loc = null;
//                        loc.setLatitude(latitude);
//                        loc.setLatitude(longitude);
//                        marker.setRotation(computeRotation(v, startRotation, loc.getBearing()));
//                    } catch (Exception ex) {
//                        // I don't care atm..
//                        ex.printStackTrace();
//                    }
//                }
//            });
//
//            valueAnimator.start();
//        }
//    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements com.prts.pickccustomer.ui.fragments.BookingFragment.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

//    public static void animateMarker(final LatLng destination, final Marker marker, final float bearing) {
//        if (marker != null) {
//            final LatLng startPosition = marker.getPosition();
//            marker.remove();
//            final LatLng endPosition = destination;
//
//            final float startRotation = marker.getRotation();
//
//            final LatLngInterpolator latLngInterpolator = new BookingFragment.LatLngInterpolator.LinearFixed();
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//            valueAnimator.setDuration(100); // duration 1 second
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    try {
//                        float v = animation.getAnimatedFraction();
//                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
//                        marker.setPosition(newPosition);
//
////                        Location loc = null;
////                        loc.setLatitude(destination.latitude);
////                        loc.setLatitude(destination.longitude);
////                        float brngs = (float)angleFromCoordinate(startPosition.latitude,startPosition.longitude,destination.latitude,destination.longitude);
////                        marker.setRotation(computeRotation(v, startRotation, bearing));
//
//                        rotateMarker(marker,startRotation,bearing );
//                    } catch (Exception ex) {
//                        // I don't care atm..
//
//
//                        Log.d(TAG, "onAnimationUpdate: "+ex.toString());
//                    }
//                }
//            });
//
//            valueAnimator.start();
//        }
//    }
//
//    public static void rotateMarker(final Marker marker, final float toRotation, final float st) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        final float startRotation = st;
//        final long duration = 1555;
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//
//                float rot = t * toRotation + (1 - t) * startRotation;
//
//                marker.setRotation(-rot > 180 ? rot / 2 : rot);
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });
//    }
//
//
//
//    private static float computeRotation(float fraction, float start, float end) {
//        float normalizeEnd = end - start; // rotate start to 0
//        float normalizedEndAbs = (normalizeEnd + 360) % 360;
//
//        float direction = (normalizedEndAbs > 360) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
//        float rotation;
//        if (direction > 0) {
//            rotation = normalizedEndAbs;
//        } else {
//            rotation = normalizedEndAbs - 360;
//        }
//
//        float result = fraction * rotation + start;
//        return (result + 360) % 360;
//    }
//    private interface LatLngInterpolator {
//        LatLng interpolate(float fraction, LatLng a, LatLng b);
//
//        class LinearFixed implements BookingFragment.LatLngInterpolator {
//            @Override
//            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
//                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
//                double lngDelta = b.longitude - a.longitude;
//                // Take the shortest path across the 180th meridian.
//                if (Math.abs(lngDelta) > 180) {
//                    lngDelta -= Math.signum(lngDelta) * 360;
//                }
//                double lng = lngDelta * fraction + a.longitude;
//                return new LatLng(lat, lng);
//            }
//        }
//    }
//    private static  double angleFromCoordinate(double lat1, double long1, double lat2,
//                                       double long2) {
//
//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        brng = (brng + 360) % 360;
//        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise
//
//        return brng;
//    }

    class MapAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {

            while (true){
                try {
                    if (canCancel){
                        break;
                    }
                    Thread.sleep(10 * 1000);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            canCancel = false;
        }

        @Override
        protected void onPostExecute(Object o) {
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            //Map UI update
            updateDriverLatitudeAndLongitude();

        }
    }

    public void bookNowConfirmationforTrucks(int selectedVehicleType, final int selectedVehicleGroup) {


        if (fromLatLng != null) {
            String url = WEB_API_ADDRESS + NEAR_BY_TRUCKS_BASED_ON_SELECTION_API_CALL;
//            String url = "http://192.168.0.107/PickCApi/" + NEAR_BY_TRUCKS_BASED_ON_SELECTION_API_CALL;


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
                    try {
                        JSONArray arr = new JSONArray(res);
                        if (arr.length() == 0) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Currently Trucks are Busy", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    createDrivingRoute(((HomeActivity) activity).fromLatLng,
                                            ((HomeActivity) activity).toLatLng, false);
                                    enableConfirmBooking(false);
                                    ((HomeActivity) activity).locationLLL.setVisibility(View.GONE);
//                                ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);


               /* indicatorView.setVisibility(View.GONE);
                indicatorView.setIndicator(INDICATORS[0]);*/

                                    progressDialog = activity.progressDialog;
                                    double fromlatitude = fromLatLng.latitude;
                                    double fromlongitude = fromLatLng.longitude;
                                    double toLatitude = toLatLng.latitude;
                                    double toLongitude = toLatLng.longitude;
                                    CredentialsSharedPref.setFromoLat(activity, fromlatitude);
                                    CredentialsSharedPref.setFromLong(activity, fromlongitude);
                                    CredentialsSharedPref.setToLat(activity, toLatitude);
                                    CredentialsSharedPref.setToLong(activity, toLongitude);


                                    downloadCargoMaterialTypes();

                                }
                            });
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }


                }
            });


//            Log.e(TAG, " getNearByTrucksBasedOnSelection: url for getting Near by Trucks" + url);
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
//            RequestQueue queue = Volley.newRequestQueue(activity);
//
//            JsonRequest<JSONArray> arrayJsonRequest = new JsonRequest<JSONArray>
//                    (Request.Method.POST, url, obj.toString(), new Response.Listener<JSONArray>() {
//                        @Override
//                        public void onResponse(JSONArray response) {
//                            Log.e(TAG, "onResponse: getNearByTrucksBasedOnSelection response " + response);
//
//                            if (response.length() == 0) {
//                                try {
//                                    Toast.makeText(activity, "Currently Trucks are Busy", Toast.LENGTH_SHORT).show();
//                                }catch (Exception ae)
//                                {}
////                                if (truckCateogeoriesFragment != null) {
////                                    truckCateogeoriesFragment.setETAToSelectedTextView(NO_TRUCKS, selectedVehicleGroup);
////                                }
//                                // Toast.makeText(activity, "No near by trucks for your selection", Toast.LENGTH_SHORT).show();
//                            } else {
////                                ((HomeActivity)activity).showBothFromTOLocationOnMapThroughMarkers(((HomeActivity)activity).fromLatLng,((HomeActivity)activity).toLatLng);
//
//                                createDrivingRoute(((HomeActivity) activity).fromLatLng,
//                                        ((HomeActivity) activity).toLatLng,false);
//                                enableConfirmBooking(false);
//                                ((HomeActivity) activity).locationLLL.setVisibility(View.GONE);
////                                ((HomeActivity) activity).truckCateogeoriesFragment.topLL.setVisibility(View.GONE);
//
//
//               /* indicatorView.setVisibility(View.GONE);
//                indicatorView.setIndicator(INDICATORS[0]);*/
//
//                                progressDialog =activity.progressDialog;
//                                double fromlatitude = fromLatLng.latitude;
//                                double fromlongitude = fromLatLng.longitude;
//                                double toLatitude = toLatLng.latitude;
//                                double toLongitude = toLatLng.longitude;
//                                CredentialsSharedPref.setFromoLat(activity,fromlatitude);
//                                CredentialsSharedPref.setFromLong(activity,fromlongitude);
//                                CredentialsSharedPref.setToLat(activity,toLatitude);
//                                CredentialsSharedPref.setToLong(activity,toLongitude);
//
//
//
//                                downloadCargoMaterialTypes();
//
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
//                    return CredentialsSharedPref.getHeaders(activity);
//                }
//            };
//            queue.add(arrayJsonRequest);
//        }
        }

    }
}
