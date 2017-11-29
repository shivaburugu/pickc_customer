package com.prts.pickccustomer.Payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.InvoiceActivity;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LOGICON3 on 5/2/2017.
 */

public class CashPayment extends AppCompatActivity implements Constants {
    PickCCustomTextVIew cashPayment,bookingNumberTv;
    TextView amountTv;
    private static final String TAG = "CashPayment";
    public static final String BOOKING_NO = "bookingNo";
    public static final String AMOUNT = "amount";
    public static final String DRIVERID = "driver";
    public static String bookingNumber;
    public static String driverId;
    public static String amountToPay;
    public static Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_cash_payment);
        context = getApplicationContext();
        Intent frintent = getIntent();
         bookingNumber = frintent.getStringExtra(BOOKING_NO);
        amountToPay = frintent.getStringExtra(AMOUNT);
        driverId = frintent.getStringExtra(DRIVERID);
        cashPayment = (PickCCustomTextVIew)findViewById(R.id.pay_button);
        amountTv = (TextView)findViewById(R.id.amount_pays);
        bookingNumberTv = (PickCCustomTextVIew)findViewById(R.id.booking_num);
        amountTv.setText("₹ "+amountToPay);
        bookingNumberTv.setText("CRN "+bookingNumber);
        Log.d(TAG, "onCreate: Payment "+amountToPay+driverId);

        cashPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent usrating = new Intent(getApplicationContext(),InvoiceActivity.class);
                usrating.putExtra(DRIVERID,driverId);
                usrating.putExtra(BOOKING_NO,bookingNumber);
                usrating.putExtra("SendInvoice", "yes");
                startActivity(usrating);

//              startActivity(new Intent(getApplicationContext(), UserRatingBarActivity.class));

//               startActivity(new Intent(getApplicationContext(), CashPaymentInvoice.class));
           payConfirmationToDriver();
            }
        });

    }
    public static void payConfirmationToDriver(){
        Log.d(TAG, "onCreate: Payment "+"Amount jklsfhhfjkghhjbhgsbsdsdjnbg :"+amountToPay+driverId+bookingNumber );


      String url = WEB_API_ADDRESS+"api/master/customer/Pay/"+bookingNumber+"/"+driverId;
//      String url = "http://192.168.0.108/PickCApi/api/master/customer/Pay/"+bookingNumber+"/"+driverId;



        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.d(TAG, "onResponse: postDeviceIdToServer "+response);

                        //goto login Activity

                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(TAG, "onErrorResponse:12345 CashPayment "+error);
                        if ((error+"").contains("SAVED SUCSSFULLY")){

                        }else {
                            displayErrorMessage("Please check your internet connection");
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        //hideProgressDialog();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(context);
            }
        };
        queue.add(jsObjRequest);
    }

    private static void displayErrorMessage(String message) {
        Log.d(TAG, "displayErrorMessage: "+message);
    }

    @Override
    public void onBackPressed() {

    }
}
