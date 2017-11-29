package com.prts.pickccustomer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.fragments.TruckCateogeoriesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class RideEstimateActivity extends AppCompatActivity implements Constants{

    private static final String TAG = "RideEstimateAct";
    EditText fromLocationTV, toLocationTV;
    static LatLng fromLatLng, toLatLng;
    static String fromAddress,toAddress ,tripDistance,tripDuration,tripAmount;
    private static final int FROM_PLACE_PICKER_REQUEST = 11;
    private static final int TO_PLACE_PICKER_REQUEST = 22;
    private double distance;
    public TruckCateogeoriesFragment truckCateogeoriesFragment;
    private double minDistance, maxDistance;
    private double minTravelTime, maxTravelTime;
    private String minFare, maxFare;
    private int loadingValue;
    public static final String estimateTravelTimeText = "Approximate travel time: ";
    public static final String estimateTravelDistanceText = "Approximate travel distance: ";
    PickCCustomTextVIew  estTravelTimeTV,LoadingStatus,estDistance;

    TextView minEstFareTV,maxEstFareTV;
    int truckID;
    int selectedVehicleGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            Window window = this.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getSupportActionBar().hide();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        //getSupportActionBar().hide();
//        ImageView closeIV = (ImageView)findViewById(R.id.closeIV);
//        closeIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setContentView(R.layout.final_activity_ride_estimate);

        LoadingStatus = (PickCCustomTextVIew) findViewById(R.id.LoadingStatus);
        loadingValue = TruckCateogeoriesFragment.loadingIVSelectedStatus;
        loadingStatusconfirming(loadingValue);
//        truckCateogeoriesFragment.selectedTruckID;
//        final int selectedVehicleGroupID = truckCateogeoriesFragment.selectedTruckID;
//        final int selectedVehicleTypeID = truckCateogeoriesFragment.selectedvehicleTypeID;
        selectedVehicleGroupID = TruckCateogeoriesFragment.selectedvehicleTypeID;

//                HomeActivity.selectedVehicleid;
        truckID = TruckCateogeoriesFragment.selectedTruckID;

        //int[] vehicleGroupIds = {1000, 1001, 1002, 1003};int VEHICLE_TYPE_OPEN = 1300;
//        int VEHICLE_TYPE_CLOSED = 1301;
//         truckID = TruckCateogeoriesFragment.selectedvehicleTypeID;

//      LoadingStatus.setText(TruckCateogeoriesFragment.loadingIVSelectedStatus);

        minEstFareTV = (TextView) findViewById(R.id.minEstimateFareTV);
        maxEstFareTV = (TextView) findViewById(R.id.maxEstimateFareTV);

        estTravelTimeTV = (PickCCustomTextVIew) findViewById(R.id.estimateTravelTimeTV);
        estDistance= (PickCCustomTextVIew) findViewById(R.id.estimateTravelDistanceTV);

        fromLocationTV = (EditText) findViewById(R.id.from_lcation_textView);
        toLocationTV = (EditText) findViewById(R.id.to_lcation_textView);

        if (fromAddress != null && fromAddress.length()>0){
            fromLocationTV.setText(fromAddress);
        }if (toAddress != null && toAddress.length()>0){
            toLocationTV.setText(toAddress);
        }
        getDistanceTimeofTripEstimate();
        calculateDistance();
        fromLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(RideEstimateActivity.this), FROM_PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        toLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(RideEstimateActivity.this), TO_PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void calculateDistance(){
        if (fromLatLng != null && toLatLng != null){
            System.out.println(fromLatLng +" + " +toLatLng);
            distance = HomeActivity.CalculationByDistance(fromLatLng,toLatLng);

            Log.d(TAG, "calculateDistance:1 "+distance);
            distance = roundTwoDecimals(distance);
            Log.d(TAG, "calculateDistance:2 "+distance);
            calcMinAndMaxDistances(distance);
            calcMinAndMaxTravelTimes(distance);
//            getMinAndMaxFares(minDistance,minTravelTime,maxDistance,maxTravelTime);
        }
        Log.d(TAG, "calculateDistance: if not executed");
    }
    private void calcMinAndMaxDistances(double actualDistance){
        double twentyPercentDistance = get20Percent(actualDistance);

        minDistance = actualDistance - twentyPercentDistance;
        maxDistance = actualDistance + twentyPercentDistance;
        Log.d(TAG, minDistance+" calcMinAndMaxDistances: "+maxDistance);

    }
    private void calcMinAndMaxTravelTimes(double totalDistance){

        minTravelTime = totalDistance * 2;//distance * avg time in minutes
        maxTravelTime = totalDistance * 3.5;
    }
    private double get20Percent(double distance20Percent){
        distance20Percent = (distance20Percent*20)/100;
        Log.d(TAG, "get20Percent: "+distance20Percent);
        return distance20Percent;
    }
    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
    public static Intent setDataAndCreateIntent(Activity activity, LatLng fromLatLng, LatLng toLatLng, String fromAddress, String toAddress){
        RideEstimateActivity.fromLatLng = fromLatLng;
        RideEstimateActivity.toLatLng = toLatLng;
        RideEstimateActivity.fromAddress = fromAddress;
        RideEstimateActivity.toAddress = toAddress;

        return new Intent(activity,RideEstimateActivity.class);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                fromLatLng = place.getLatLng();
                fromAddress = String.valueOf(place.getAddress());
                fromLocationTV.setText(fromAddress+"");
            }
        }
        if (requestCode == TO_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                toLatLng = place.getLatLng();
                toAddress = String.valueOf(place.getAddress());
                toLocationTV.setText(toAddress+"");
            }
        }
        calculateDistance();

    }
//    private void getMinAndMaxFares(double minDistance, double minTravelTime, double maxDistance, double maxTravelTime){
////        getDistanceTimeofTripEstimate();
//        minFare = getEstimatedFare(minDistance,minTravelTime);
//        maxFare = getEstimatedFare(maxDistance,maxTravelTime);
//
//        Log.d("MAX and MIn",minFare+""+maxFare);
//
//        minEstFareTV.setText(rupee+" "+minFare);
//        maxEstFareTV.setText(rupee+" "+maxFare);
//
//        double estimatedTravelDistance = (minDistance+maxDistance)/2;
//
//        double estimatedTravelTime = (minTravelTime+maxTravelTime)/2;
//
//        estTravelTimeTV.setText("*"+" "+estimateTravelTimeText+estimatedTravelTime+" min");
//        estDistance.setText("*"+" "+estimateTravelDistanceText+estimatedTravelDistance+" km");
//    }

    private double getEstimatedFare(double distance, double minutes){
        getRateCardData();
        double totalAmt = baseFareAmt;
        if (distance > baseKMs){
            double extraDistance = distance - baseKMs;
            totalAmt += (extraDistance*perKmFareAmt);
        }
        if (minutes > baseTimeInMinutes){
            double extraMinutes = minutes - baseTimeInMinutes;
            totalAmt += extraMinutes*rideTimeFareAmtPerMinute;
        }
        return totalAmt;
    }
    private double baseFareAmt, rideTimeFareAmtPerMinute, perKmFareAmt;
    private double baseKMs;
    private double baseTimeInMinutes;

    private void getRateCardData(){
        baseFareAmt =  TruckCateogeoriesFragment.baseFareAmt;
        rideTimeFareAmtPerMinute =  TruckCateogeoriesFragment.rideTimeFareAmtPerMinute;
        perKmFareAmt =  TruckCateogeoriesFragment.perKmFareAmt;
        baseKMs =  TruckCateogeoriesFragment.baseKMs;
        baseTimeInMinutes =  TruckCateogeoriesFragment.baseTimeInMinutes;

    }
    private void getDistanceTimeofTripEstimate() {
        String fromlatl= String.valueOf(fromLatLng);
        String tolatl= String.valueOf(toLatLng);

        JSONObject obj = new JSONObject();
        int length = String.valueOf(HomeActivity.fromLatLng).length();
        String fromLocation = String.valueOf(HomeActivity.fromLatLng).substring(10,length-1);
        int lengths = String.valueOf(HomeActivity.toLatLng).length();
        String toLocation  = String.valueOf(HomeActivity.toLatLng).substring(10,lengths-1);
        try {
            obj.put("VehicleType", selectedVehicleGroupID);
            obj.put("VehicleGroup", truckID);
            obj.put("frmLatLong", fromLocation);
            obj.put("toLatLong", toLocation);
            obj.put("LdUdCharges", loadingValue);

        } catch (JSONException e) {
            e.printStackTrace();
        }
  String url = WEB_API_ADDRESS+"api/master/customer/tripestimate";
        Log.d(TAG, "getDistanceTimeofTripEstimate: "+ obj);
//       String url = "http://192.168.0.124/PickCApi/api/master/customer/tripestimate";
//        Log.d(TAG, "getDistanceTimeofTripEstimate: "+ fromLatLng + toLatLng);
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Updating Trip Estimate");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "getDistanceTimeofTripEstimate: " + obj);
        Log.d(TAG, "getDistanceTimeofTripEstimate: " + url);

        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: cancelBooking response" + response);


                JSONObject jObj = response;
                try {

                    JSONArray info = jObj.getJSONArray("tripEstimateForCustomer");
                    for (int i = 0; i < info.length(); i++) {
                        JSONObject c = info.getJSONObject(i);
                        minFare = c.getString("TotalTripEstimateminValue");
                        maxFare = c.getString("TotalTripEstimatemaxValue");
                        tripDistance = c.getString("ApproximateDistanceKM");
                        tripDuration = c.getString("ApproximateTime");

                        estDistance.setText("*" + " " + estimateTravelDistanceText + tripDistance + " km");
                        estTravelTimeTV.setText("*" + " " + estimateTravelTimeText + tripDuration + " min");
                        minEstFareTV.setText(rupee + " " + minFare);
                        maxEstFareTV.setText(rupee + " " + maxFare);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();



                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                progressDialog.dismiss();

                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: cancelBooking 7896 " + error);
                //TODO: handle failure

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return CredentialsSharedPref.getHeaders(RideEstimateActivity.this);
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }
    private void loadingStatusconfirming(int loadingCode)
    {

        switch (loadingCode)
        {
            case 1370:
                LoadingStatus.setText(" Selected: Loading Services");
                break;
            case 1371:
                LoadingStatus.setText(" Selected: Un Loading Services");
                break;
            case 1372:
                LoadingStatus.setText(" Selected: Loading & Un Loading Services");
                break;
            case 1373:
                LoadingStatus.setText(" Selected: None");
                break;

        }
    }


}
