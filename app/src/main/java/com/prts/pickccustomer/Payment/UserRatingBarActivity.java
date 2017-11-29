package com.prts.pickccustomer.Payment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * Created by LOGICON on 08-06-2017.
 */

public class UserRatingBarActivity extends AppCompatActivity implements Constants {
    RatingBar ratingBar;
    private static final String TAG = "UserRatingBarActivity";
    TextView amountTV, truckTypeTv, fromlocationTv, toLocationTv, avgratingTv, driverNam;
    public static final String DRIVERID = "driver";
    public static String driverId;
    Button submit;
    String rating;
    int intRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_activity_users_ratings);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        amountTV = (TextView) findViewById(R.id.amountTextView);
        truckTypeTv = (TextView) findViewById(R.id.truckTypeTv);
        fromlocationTv = (TextView) findViewById(R.id.from_location_address_TV);
        toLocationTv = (TextView) findViewById(R.id.dest_location_address_TV);
        driverNam = (TextView) findViewById(R.id.driverNam);
        submit = (Button) findViewById(R.id.submit);
        String bookingNumber = CredentialsSharedPref.getBookingNO(getApplicationContext());
        Intent frintent = getIntent();


        driverId = frintent.getStringExtra(DRIVERID);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ratings = (int) ratingBar.getRating();
                if (ratings == 0) {
                    Toast.makeText(UserRatingBarActivity.this, "Rate the Driver", Toast.LENGTH_SHORT).show();

                } else if (ratings <= 3) {
                    createCancelRemarksDialogAndShow(rating);
                    rating = "" + ratingBar.getRating();

//                    intRating = (int) Math.round(ratingBar.getRating());
//                    Log.d(TAG, "onClick: Checking the rating given to the driver by the customer" + rating);
//                    sendRatingtoServer();
                } else {
                    rating = "" + ratingBar.getRating();

                    intRating = (int) Math.round(ratingBar.getRating());
                    Log.d(TAG, "onClick: Checking the rating given to the driver by the customer" + rating);
                    sendRatingtoServer();
                }

            }
        });

        getDetailsForUser(bookingNumber);
        retrieveDriverRating(driverId);


    }

    public void getDetailsForUser(String bkNumber) {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Verifying your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = WEB_API_ADDRESS + "api/master/customer/TripInvoice/" + bkNumber;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);


                        Log.d(TAG, "onResponse: " + response);
                        try {
                            String amount = response.getString("TotalBillAmount");
                            String locationForm = response.getString("LocationFrom");
                            String locationto = response.getString("LocationTo");
                            String vehicleType = response.getString("VehicleType");
                            String driverName = response.getString("DriverName");
                            String category = response.getString("VehicleGroup");
                            Log.d(TAG, "onResponse: ");
                            driverNam.setText("Driver Name : " + driverName);
                            amountTV.setText("₹ " + amount);
                            fromlocationTv.setText(locationForm);
                            toLocationTv.setText(locationto);

                            truckTypeTv.setText(category + " - " + vehicleType + " Truck");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse:12345 " + error);


                        if ((error + "").contains("TimeoutError")) {
                            Toast.makeText(UserRatingBarActivity.this, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();

//                           displayErrorMessage("Server busy. Please try again after some time");

                        }
                        progressDialog.dismiss();
                        //hideProgressDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(UserRatingBarActivity.this);
            }
        };
        queue.add(jsObjRequest);

    }

    public void sendRatingtoServer() {
//        String url = WEB_API_ADDRESS+"api/master/customer/DriverRating";
        String url = WEB_API_ADDRESS + "api/master/customer/DriverRating";
        JSONObject jsonObject = new JSONObject();
        final String bookingNumber = CredentialsSharedPref.getBookingNO(getApplicationContext());
        Log.d(TAG, "sendRatingtoServer: Objects sending to the server" + bookingNumber + driverId + intRating);
        try {
            jsonObject.put("BookingNo", bookingNumber);
            jsonObject.put("DriverID", driverId);
            jsonObject.put("Rating", intRating);
            jsonObject.put("Remarks", cancelRemarks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "sendRatingtoServer: " + jsonObject);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: " + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse:12345 " + error);

                        if ((error + "").contains("ParseError")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 1000);


                            String jsonString = null;
                            try {
                                if ((error + "").contains(TRUE))
                                    jsonString = TRUE;
                                if ((error + "").contains(FALSE))
                                    jsonString = FALSE;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "parseNetworkResponse: " + jsonString);
                            if (jsonString != null) {

                                switch (jsonString) {
                                    case TRUE:
                                        //show alert dialog
                                        Toast.makeText(UserRatingBarActivity.this, "Rating Completed Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                                        Intent invoice = new Intent(getApplicationContext(), WebViewActivityforInvoice.class);
////                                    startActivity(new Intent(getApplicationContext(), WebViewActivityforInvoice));
//                                        invoice.putExtra(WebViewActivityforInvoice.URL, "http://pickcargo.in/Help/TRIPINVOICE");
//                                        invoice.putExtra(BOOKING_NO, bookingNumber);
//                                        startActivity(invoice);
                                        break;
                                    case FALSE:

                                        break;
                                    default:

                                        break;
                                }
                                return;
                            }
                            //hideProgressDialog();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(UserRatingBarActivity.this);
            }
        };
        queue.add(jsObjRequest);

    }

    //    private void displayErrorMessage(String message) {
//        Toast.makeText(UserRatingBarActivity.this, message, Toast.LENGTH_SHORT).show();
//
//    }
    public void retrieveDriverRating(final String driverId) {

        String url = WEB_API_ADDRESS + "api/master/customer/AvgDriverRating/" + driverId;


        Log.d(TAG, "retrieveDriverCurrentLatLng: retrieveDriverCurrentLatLng url " + url);
        JsonObjectRequest stringRequestRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: retrieveDriverCurrentLatLng response" + response);

                try {
                    JSONObject jsonObject = response;
                    String rating = jsonObject.getString("Rating");
                    avgratingTv = (TextView) findViewById(R.id.avgratingTv);
                    avgratingTv.setText("Driver Rating : " + rating);

                } catch (Exception e) {
                    e.printStackTrace();
                    avgratingTv.setText("Driver Rating : " + "Zero");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

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
                return CredentialsSharedPref.getHeaders(getApplicationContext());
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequestRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }

    private void createCancelRemarksDialogAndShow(String rating) {
        final Dialog dialog = new Dialog(UserRatingBarActivity.this);
        dialog.setContentView(R.layout.driver_less_rating);
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
                    Toast.makeText(UserRatingBarActivity.this, "Please select a reason to cancel", Toast.LENGTH_SHORT).show();
                    return;
                }
                intRating = (int) Math.round(ratingBar.getRating());
//                Log.d(TAG, "onClick: Checking the rating given to the driver by the customer" + rating);
                dialog.dismiss();
                sendRatingtoServer();
//                cancelBooking(bookingNo, cancelRemarks);
//                enableBooking();
//                ((HomeActivity) activity).onMapReady(((HomeActivity) activity).map);
//                stopProgressTime(((HomeActivity) getActivity()).progressDialog, ((HomeActivity) getActivity()).tv_forTime, ((HomeActivity) getActivity()).tv_forTime_2);

            }
        });
//        PickCCustomTextVIew doNotCancelBtn = (PickCCustomTextVIew) dialog.findViewById(R.id.donotCancelTV);
//        doNotCancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

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
    final String[] CANCEL_REMARKS = {"Driver is late", "Driver attitude was not good", "Driver was drunk", "Driver behaviour was not good"};
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
}

