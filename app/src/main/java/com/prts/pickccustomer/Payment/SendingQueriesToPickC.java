package com.prts.pickccustomer.Payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by LOGICON on 20-06-2017.
 */

public class SendingQueriesToPickC extends AppCompatActivity implements TextWatcher,Constants {
    EditText queryBox;
    PickCCustomEditText mobile_et,name_et,email_et,subj_et;
    private static final String TAG = "SendingQueriesToPickc";
    String name,email,mobileNo,subject,query;
    Button send_queries;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_queries);
        send_queries = (Button) findViewById(R.id.send_queries);
        String mobileno = CredentialsSharedPref.getMobileNO(SendingQueriesToPickC.this);
        mobile_et = (PickCCustomEditText) findViewById(R.id.mobile_et);
        mobile_et.setText(mobileno);

        send_queries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToPickCargo();
            }
        });

    }
    public void sendEmailToPickCargo() {
        queryBox = (EditText) findViewById(R.id.queryBox);
        name_et = (PickCCustomEditText) findViewById(R.id.name_et);
        email_et = (PickCCustomEditText) findViewById(R.id.email_et);
        subj_et = (PickCCustomEditText) findViewById(R.id.subj_et);



        mobile_et.addTextChangedListener(this);
        subj_et.addTextChangedListener(this);
        email_et.addTextChangedListener(this);
        name_et.addTextChangedListener(this);
         name = name_et.getText().toString();
         email = email_et.getText().toString();
       mobileNo = mobile_et.getText().toString();
       subject = subj_et.getText().toString();

        if (name.isEmpty()) {
            name_et.setError("Please enter Name");
            displayErrorMessage("Please enter valid details");
            return;
        }
        if (email.isEmpty()) {
            email_et.setError("Please enter Email");
            displayErrorMessage("Please enter valid details");
            return;
        }

        if (!email.isEmpty()) {
            if (!isValidEmail(email)) {
                email_et.setError("Please enter correct email id");
                displayErrorMessage("Please enter valid details");
                return;
            }
            if (mobileNo.isEmpty()){
                mobile_et.setError("Please enter mobile no");
                displayErrorMessage("Please enter valid details");
                return;
            }
            if (mobileNo.length()<10){
                mobile_et.setError("Please enter 10 digit mobile no.");
                displayErrorMessage("Enter 10 digit mobile number");
                return;
            }
            if (subject.isEmpty()){
                subj_et.setError("Please enter subject");
                displayErrorMessage("Please enter valid details");
                return;
            }



             query = queryBox.getText().toString().trim();
            if (query.isEmpty()){
                queryBox.setError("Please enter query");
                displayErrorMessage("Please enter valid details");
                return;
            }


            Log.d(TAG, "sendEmailToPickCargo: Query Message : " + query);
            sendQueryToserver();
            finish();
        }
    }

    private void sendQueryToserver() {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Sending Email");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//        disableOnBackPressed = true;
//         name,email,mobileNo,subject,query;

//        Name,
//                Email,Mobile,Message,Subject
        Log.d(TAG, "sendQueryToserver: "+name+email+mobileNo+query+subject);
        final JSONObject obj = new JSONObject();
        try {

            obj.put("Name", name);
            obj.put("Email", email);
            obj.put("Mobile", mobileNo);
            obj.put("Message", query);
            obj.put("Subject", subject);
            obj.put("Type", "ContactUs");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = WEB_API_ADDRESS+"api/master/customer/SendMessageToPickC";
//        String url = "http://192.168.0.108/PickCApi/api/master/customer/SendMessageToPickC";

//      String url = "http://192.168.0.108/PickCApi/api/master/customer/Pay/"+bookingNumber+"/"+driverId;



        RequestQueue queue = Volley.newRequestQueue(SendingQueriesToPickC.this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.d(TAG, "onResponse: postDeviceIdToServer "+response);
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Log.d(TAG, "onErrorResponse:12345 CashPayment "+error);
                        if ((error+"").contains("Mail Sent Successfully!")){
                            progressDialog.dismiss();
                            Toast.makeText(SendingQueriesToPickC.this, "Sent Message To Pick-C mail", Toast.LENGTH_SHORT).show();
                        }

                        if ((error+"").contains("TimeoutError")){
                            progressDialog.dismiss();

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        //hideProgressDialog();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(SendingQueriesToPickC.this);
            }
        };
        queue.add(jsObjRequest);
    }


    public final static boolean isValidEmail(String target) {
        if (target == null || target.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    private static void displayErrorMessage(String message) {
        Log.d(TAG, "displayErrorMessage: "+message);
    }
}
