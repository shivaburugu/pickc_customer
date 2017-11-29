package com.prts.pickccustomer.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.FCM.MyFirebaseMessagingService;
import com.prts.pickccustomer.FCM.MyFirebaseMessagingService.OnDriverReachedPickUpLocationListener;
import com.squareup.picasso.Picasso;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.services.IsConfirmBookingInfo;
import com.prts.pickccustomer.ui.HomeActivity;
import com.prts.pickccustomer.ui.WebViewActivity;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Uday on 03-11-2016.
 */
public class DriverDetailsFragment extends Fragment implements Constants{

    private static final String CONFIRM_BOOKING_INFO_BUNDLE_KEY = "confirm_bundle_key";
    private static final String IS_TRIP_STARTED_BOOKING_INFO_BUNDLE_KEY = "is_trip_started";
    private static final String TAG = "DriverDetailsFragment";
    ImageView driverIV;
    PickCCustomTextVIew driverNameTV, vehicleDescTV, bookingVehicleNosTV,otpTV;
    public PickCCustomTextVIew eTA_Time_TV;
    LinearLayout callDriverLL, supportLL, cancelLL, topLL;
    Activity activity;
    TextView ratingTextTV;
    IsConfirmBookingInfo confirmBookingInfo;
    static BookingFragment bookingFragment;
    RatingBar rtBar;
    boolean isTripStarted;

  boolean driverReached ;
    TextView pickTVAR;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null){
            confirmBookingInfo = (IsConfirmBookingInfo) bundle.getSerializable(CONFIRM_BOOKING_INFO_BUNDLE_KEY);
            driverReached = bundle.getBoolean("ISTRIPREACHED");
            isTripStarted = bundle.getBoolean(IS_TRIP_STARTED_BOOKING_INFO_BUNDLE_KEY);
        }
        return inflater.inflate(R.layout.final_driver_details_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        initialize(view);
        setConfirmBookingInfoData();
        String driverId = confirmBookingInfo.getDriverId();
        retrieveDriverRating(driverId);
    }

    private void setConfirmBookingInfoData() {
        if (confirmBookingInfo != null){
            driverNameTV.setText(confirmBookingInfo.getDriverName());
            vehicleDescTV.setText(IsConfirmBookingInfo.vehicleDesc);
            otpTV.setText("OTP: "+confirmBookingInfo.getOtp());
            bookingVehicleNosTV.setText("Veh No. "+ confirmBookingInfo.getVehicleNo() + "/" + confirmBookingInfo.getBookingNo() );
            HomeActivity activity = ((HomeActivity)getActivity());
            setETAtime("calculating...");
//            String driverId = confirmBookingInfo.getDriverId();
//            retrieveDriverRating(driverId);
            activity.getTravelTimeBetweenTwoLocations(activity.fromLatLng.latitude+","+activity.fromLatLng.longitude, confirmBookingInfo.getDriverLatitude()+","+confirmBookingInfo.getDriverLongitude(), 0, false);
        }
    }

    private void initialize(View view) {
        driverIV = (ImageView)view.findViewById(R.id.driver_imageView);

        rtBar= (RatingBar) view.findViewById(R.id.ratingBar2);

        driverNameTV = (PickCCustomTextVIew)view.findViewById(R.id.driverNameTV);
        vehicleDescTV = (PickCCustomTextVIew)view.findViewById(R.id.vehicleDescTV);
        bookingVehicleNosTV = (PickCCustomTextVIew)view.findViewById(R.id.bookingNoTV);
        ratingTextTV = (TextView)view.findViewById(R.id.ratingText);
        eTA_Time_TV = (PickCCustomTextVIew)view.findViewById(R.id.eta_time_TV);
        otpTV = (PickCCustomTextVIew)view.findViewById(R.id.otpTV);
        callDriverLL = (LinearLayout)view.findViewById(R.id.callDriverLL);
        supportLL = (LinearLayout)view.findViewById(R.id.supportLL);
        cancelLL = (LinearLayout)view.findViewById(R.id.cancelBookingLL);

        topLL = (LinearLayout)view.findViewById(R.id.topLL_driver);
        pickTVAR = (TextView)view.findViewById(R.id.pickTVAR);
        if(driverReached){
            pickTVAR.setText("Pick up Arrived");
            setETAtime("0");
        }

        if (isTripStarted){
            topLL.setVisibility(View.GONE);
            cancelLL.setVisibility(View.GONE);
        }
        callDriverLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String driverNumber = "123456789";
                if (confirmBookingInfo != null) {
                   driverNumber = confirmBookingInfo.getDriverMobNo();
                }
                Uri number = Uri.parse("tel:"+driverNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        supportLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String completeUrl = SUPPORT_URL;
                Intent intent = new Intent(activity,WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL,completeUrl);
                startActivity(intent);

            }
        });
        cancelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingFragment.cancelBookingInfo(CredentialsSharedPref.getBookingNO(activity));
                CredentialsSharedPref.setShowingLiveUpdateMarker(activity,false);
                if (bookingFragment.mapAsync != null ){
                    bookingFragment.canCancel = true;
                    bookingFragment .mapAsync = null;
                }

//                bookingFragment.mdatabaserefence.removeEventListener(bookingFragment.mlistener);
            }
        });
    }

    public static DriverDetailsFragment getInstanceOfDriverDetailsFragment(IsConfirmBookingInfo confirmBookingInfo,BookingFragment bookingFragment, boolean isTripStarted, boolean driverReached) {
        DriverDetailsFragment driverDetailsFragment = new DriverDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_BOOKING_INFO_BUNDLE_KEY, confirmBookingInfo);
        bundle.putBoolean(IS_TRIP_STARTED_BOOKING_INFO_BUNDLE_KEY, isTripStarted);
        bundle.putBoolean("ISTRIPREACHED", driverReached);

        driverDetailsFragment.setArguments(bundle);
        DriverDetailsFragment.bookingFragment = bookingFragment;
        return driverDetailsFragment;
    }

    public void setETAtime(String duration){

        eTA_Time_TV.setText("ETA: "+duration);
    }
    public static void setText() {

    }
    public void retrieveDriverRating(final String driverId) {
        String png = ".png";
        String urlImage ="http://ragsarma-001-site13.htempurl.com/DriverImages/"+driverId+png;
        Log.d(TAG, "retrieveDriverRating: Image "+urlImage);
//        String urlImage ="http://ragsarma-001-site13.htempurl.com/DriverImages/dr170800008.png";
//       Picasso.with(activity).load(urlImage).into(driverIV);

        String url = WEB_API_ADDRESS+"api/master/customer/AvgDriverRating/"+driverId;



        CredentialsSharedPref.setShowingLiveUpdateMarker(activity,true);
//        bookingFragment.mdatabaserefence.addValueEventListener(bookingFragment.mlistener);
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
                    String rating = jsonObject.getString("Rating");
//                    Picasso.with(getContext()).load("http://ragsarma-001-site13.htempurl.com/DriverImages/"+driverId+".png").into(driverIV);
                    ratingTextTV.setText(rating);
                    rtBar.setRating(Float.parseFloat(rating));
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

}
