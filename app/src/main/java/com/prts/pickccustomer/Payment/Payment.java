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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.Payment.CCAvenuepayment.CcAvenueWebViewActivity;
import com.prts.pickccustomer.Payment.CcAvenueUtility.AvenuesParams;
import com.prts.pickccustomer.Payment.CcAvenueUtility.ServiceUtility;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.prts.pickccustomer.constants.Constants.WEB_API_ADDRESS;

/**
 * Created by LOGICON3 on 4/28/2017.
 */

public class Payment extends AppCompatActivity {
    PickCCustomTextVIew cash;
    PickCCustomTextVIew online;
    PickCCustomTextVIew bookingNumberTV;
    static TextView amountTV;
    public static final String BOOKING_NO = "bookingNo";
    public static final String AMOUNT = "amount";
    public static final String DRIVERID = "driver";
    private static final String TAG = "Payment";
    public  static String bookingNumber;
    public static String amountToPay;
    public static String driverId;
    public static Context context;
    String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_payment_selection);
        cash = (PickCCustomTextVIew)findViewById(R.id.cash_button);
        online = (PickCCustomTextVIew)findViewById(R.id.online_button);
        bookingNumberTV = (PickCCustomTextVIew)findViewById(R.id.b_number);
        amountTV = (TextView)findViewById(R.id.amount);
        context = getApplicationContext();
        Intent intent = getIntent();
         bookingNumber = intent.getStringExtra(BOOKING_NO);
        bookingNumberTV.setText("CRN: "+bookingNumber);
        getAmount();
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer randomNum = ServiceUtility.randInt(0, 9999999);
                orderId = randomNum.toString();
                Intent intent = new Intent(getApplicationContext(), CcAvenueWebViewActivity.class);
                intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amountToPay).toString().trim());
                intent.putExtra(AvenuesParams.BOOKING_NUBMER, ServiceUtility.chkNull(bookingNumber).toString().trim());
                intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
                startActivity(intent);
            }
        });


        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent casInt = new Intent(getApplicationContext(),CashPayment.class);
                casInt.putExtra(BOOKING_NO, CredentialsSharedPref.getBookingNO(getApplicationContext()));
                casInt.putExtra(AMOUNT, amountToPay);
                casInt.putExtra(DRIVERID, driverId);

                Log.d(TAG, "onClick: Payment "+amountToPay+driverId);
                startActivity(casInt);
//                startActivity(new Intent(getApplicationContext(), CashPayment.class));
            }
        });
    }
    public static void getAmount(){




        String url =  WEB_API_ADDRESS+"api/master/customer/BillDetails/"+bookingNumber;
//
//        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d(TAG, "onResponse: "+response);
                displayErrorMessage(response.toString());
                for (int i = 0; i<response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        amountToPay = jsonObject.getString("TotalAmount");
                        driverId= jsonObject.getString("DriverID");
                        amountTV.setText("₹ "+amountToPay);
                        Log.d(TAG, "onResponse: amount to pay : "+amountToPay +"  "+ driverId);

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


                Log.d(TAG, "onErrorResponse: "+error);
                //TODO: handle failure
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(context);
            }
        };
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    private static void displayErrorMessage(String message) {
        Log.d(TAG, "displayErrorMessage: "+message);
    }
    @Override
    public void onBackPressed() {

    }
}
