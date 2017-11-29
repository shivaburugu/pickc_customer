package com.prts.pickccustomer.ui.history;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.AlertDialogActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class BookingHistoryActivity extends AppCompatActivity implements Constants {

    private static final String TAG = "BookingHistoryACt";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_booking_histoy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.arrow_up_float);
        activity = BookingHistoryActivity.this;
        initialize();
    }

    RecyclerView recyclerView;
    TextView nobookingsTV;
    LinearLayout bookingHistoryLayout;

    public void initialize() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        nobookingsTV = (TextView)findViewById(R.id.no_bookings);
        bookingHistoryLayout = (LinearLayout)findViewById(R.id.bookingHistoryLayout);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        downloadBookingHistoryBasedOnMobileNo(CredentialsSharedPref.getMobileNO(BookingHistoryActivity.this));
        //updateRecyclerView(activity,historyDataArrayList);
    }

    BookingHistoryListAdapter adapter;

    private void updateRecyclerView(Activity activity, ArrayList<HistoryData> historyDatas) {
        adapter = new BookingHistoryListAdapter(activity, historyDatas);
        recyclerView.setAdapter(adapter);
    }

    ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();

    public void downloadBookingHistoryBasedOnMobileNo(String mobileNO) {
        final ProgressDialog progressDialog = new ProgressDialog(this, 0);
        progressDialog.setTitle("Loading your data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//     String url = "http://192.168.0.122/PickCApi/api/Operation/booking/BookingHistoryListbyCustomerMobileNo/"+mobileNO;

//        api/Operation/booking/BookingHistoryListbyCustomerMobileNo/8341037724
        String url = WEB_API_ADDRESS + "api/Operation/booking/BookingHistoryListbyCustomerMobileNo/" + mobileNO;

//        String url = api/operation/booking/list
        Log.d(TAG, "downloadBookingHistoryBasedOnMobileNo: url " + url);


        OkHttpClient mOkHttpClient;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

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
                Log.d(TAG, "onFailure: " + e.toString());
                progressDialog.dismiss();
                if (e.toString().contains("TimeoutError")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
//                            displayErrorMessage("Server busy. Please try again after some time");
                            AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                                    "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                                    new AlertDialogActivity.OnPositiveBtnClickListener() {
                                        @Override
                                        public void onPositiveBtnClick() {
                                            downloadBookingHistoryBasedOnMobileNo(CredentialsSharedPref.getMobileNO(BookingHistoryActivity.this));

                                            // callBookNowAPI(vehicleGroup, vehicleType, vehicleGroupDescId, isBookingLater, loadingUnloadingStatus);
                                        }
                                    }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                        @Override
                                        public void onNegativeBtnClick() {

                                        }
                                    });
                            return;

                        }
                    });

                }

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                historyDataArrayList.clear();
                Log.d(TAG, "onResponse: " + res);
                progressDialog.dismiss();

                try {

                    JSONArray array = new JSONArray(res);
                    if (array.length() == 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Booking History is Empty", Toast.LENGTH_SHORT).show();
//                                nobookingsTV.setVisibility(View.VISIBLE);
                                bookingHistoryLayout.setVisibility(View.VISIBLE);

                            }
                        });
                    }

                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jsonObject = array.getJSONObject(i);
                            Log.d(TAG, "onResponse: jsonObject 6547 " + jsonObject);
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
                            String driverRating = jsonObject.getString("DriverRating");
                            String tripAmount = jsonObject.getString("TripAmount");

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
                            data.setAvgDriverRating(driverRating);
                            data.setTripAmount("₹ " + tripAmount);
                            historyDataArrayList.add(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException ae) {
                    ae.printStackTrace();
                }

//
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerView(activity, historyDataArrayList);
                        Log.d(TAG, "onResponse: updated");

                        progressDialog.dismiss();
                    }
                });
//                 updateRecyclerView(activity,historyDataArrayList);
//                Log.d(TAG, "onResponse: updated");
//
//                progressDialog.dismiss();

            }
        });


//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d(TAG, "onResponse: downloadBookingHistoryBasedOnMobileNo " + response);
//                Log.d(TAG, "onResponse: ");
//                historyDataArrayList.clear();
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//
//                        Log.d(TAG, "onResponse: jsonObject 6547 "+jsonObject);
//                        String bookingNo = jsonObject.getString(BOOKING_NO_BOOKING_JSON_KEY);
//                        String bookingDate = jsonObject.getString(BOOKING_DATE_BOOKING_JSON_KEY);
//                        //String mobileNO_CusID = jsonObject.getString(CUSTOMER_ID_BOOKING_JSON_KEY);
//                        String requiredDate = jsonObject.getString(REQUIRED_DATE_BOOKING_JSON_KEY);
//
//                        String fromLocation = jsonObject.getString(LOCATION_FROM_BOOKING_JSON_KEY);
//                        String toLocation = jsonObject.getString(LOCATION_TO_BOOKING_JSON_KEY);
//                        String cargoDesc = jsonObject.getString(CARGO_DESC_BOOKING_JSON_KEY);
//
//                        int vehicleType = jsonObject.getInt(VEHICLE_TYPE_BOOKING_JSON_KEY);
//                        int vehicleGroup = jsonObject.getInt(VEHICLE_GROUP_BOOKING_JSON_KEY);
//                        String remarks = jsonObject.getString(REMARKS_BOOKING_JSON_KEY);
//
//                        boolean isConfirmed = jsonObject.getBoolean(IS_CONFIRM_BOOKING_JSON_KEY);
//                        String confirmDate = jsonObject.getString(CONFIRM_DATE_BOOKING_JSON_KEY);
//                        String driverId = jsonObject.getString(DRIVER_ID_BOOKING_JSON_KEY);
//                        String vehicleNo = jsonObject.getString(VEHICLE_NO_BOOKING_JSON_KEY);
//
//                        boolean isCanceled = jsonObject.getBoolean(IS_CANCEL_BOOKING_JSON_KEY);
//                        String canceledTime = jsonObject.getString(CANCEL_TIME_BOOKING_JSON_KEY);
//                        String cancelRemarks = jsonObject.getString(CANCEL_REMARKS_BOOKING_JSON_KEY);
//
//                        boolean isCompleted = jsonObject.getBoolean(IS_COMPLETE_BOOKING_JSON_KEY);
//                        String completedTime = jsonObject.getString(COMPLETE_TIME_BOOKING_JSON_KEY);
//                        String driverRating = jsonObject.getString("DriverRating");
//                        String tripAmount = jsonObject.getString("TripAmount");
//
//                        HistoryData data = new HistoryData();
//
//                        data.setBookingNo(bookingNo);
//                        data.setBookingDate(bookingDate);
//                        data.setRequiredDate(requiredDate);
//                        data.setFromLocation(fromLocation);
//                        data.setToLocation(toLocation);
//                        data.setCargoDesc(cargoDesc);
//
//                        data.setVehicleType(vehicleType);
//                        data.setVehicleGroup(vehicleGroup);
//                        data.setRemarks(remarks);
//                        data.setConfirmed(isConfirmed);
//                        data.setConfirmDate(confirmDate);
//                        data.setDriverId(driverId);
//
//
//                        data.setVehicleNo(vehicleNo);
//                        data.setCanceled(isCanceled);
//                        data.setCanceledTime(canceledTime);
//                        data.setCancelRemarks(cancelRemarks);
//                        data.setCompleted(isCompleted);
//                        data.setCompletedTime(completedTime);
//                        data.setAvgDriverRating(driverRating);
//                        data.setTripAmount("₹ "+tripAmount);
//                        historyDataArrayList.add(data);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                updateRecyclerView(activity,historyDataArrayList);
//                Log.d(TAG, "onResponse: updated");
//
//                progressDialog.dismiss();
//                //adapter.notify
//                //TODO: handle success
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.d("Booking History", "Error");
//                progressDialog.dismiss();
//                if ((error + "").contains("TimeoutError")) {
//                  //  Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
//                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                            new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//                                    downloadBookingHistoryBasedOnMobileNo(CredentialsSharedPref.getMobileNO(BookingHistoryActivity.this));
//                                }
//                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
//                    return;
//                }
//                Toast.makeText(activity, "Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "onErrorResponse: downloadBookingHistoryBasedOnMobileNo " + error);
//                Log.d(TAG, "onErrorResponse: ");
//                Log.d(TAG, error.getMessage()+" getMessage onErrorResponse: downloadBookingHistoryBasedOnMobileNo getLocalizedMessage "+error.getLocalizedMessage());
//                //TODO: handle failure
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = CredentialsSharedPref.getHeaders(activity);
//                Log.d(TAG, "getHeaders: "+map);
//                return map;
//            }
//        };
//        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//
//        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void retrieveDriverRating(final String driverId) {
//        Picasso.with(getContext()).load("http://ragsarma-001-site13.htempurl.com/DriverImages/"+driverId+".png").into(driverIV);

        String url = WEB_API_ADDRESS + "api/master/customer/AvgDriverRating/" + driverId;


        Log.d(TAG, "retrieveDriverCurrentLatLng: retrieveDriverCurrentLatLng url " + url);
        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: retrieveDriverCurrentLatLng response" + response);

                try {
                    JSONObject jsonObject = response;
                    HistoryData data = new HistoryData();

//                    String rating = jsonObject.getString("Rating");
//                    data.setAvgDriverRating(rating);
//                    Picasso.with(getContext()).load("http://ragsarma-001-site13.htempurl.com/DriverImages/"+driverId+".png").into(driverIV);
//                    ratingTextTV.setText(rating);
//                    rtBar.setRating(Float.parseFloat(rating));
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
