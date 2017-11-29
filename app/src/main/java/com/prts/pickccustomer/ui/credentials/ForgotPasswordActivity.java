package com.prts.pickccustomer.ui.credentials;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ForgotPasswordActivity extends AppCompatActivity implements Constants {
    private static final String TAG = "ForgotPasswordAct";
    PickCCustomTextVIew continueTV,errorTV;
    EditText editText;
    String enteredMobNo;
    private Handler handler;
    boolean disableOnBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        errorTV = (PickCCustomTextVIew)findViewById(R.id.error_text_tv);
        editText = (EditText) findViewById(R.id.eneteredMobNoET);
        continueTV = (PickCCustomTextVIew) findViewById(R.id.continueTV);
//        disableContinueTV();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

//                    enableContinueTV();
                } else {
//                    disableContinueTV();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler();
    }

//    private void disableContinueTV() {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                continueTV.setEnabled(false);
//                continueTV.setTextColor(Color.GRAY);
//            }
//        });
//
//    }

//    private void enableContinueTV() {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                continueTV.setEnabled(true);
//                continueTV.setTextColor(getResources().getColor(R.color.appThemeYellow));
//            }
//        });
//
//    }

    public void continueOnClick(View view) {
        view.setEnabled(false);
        Log.d(TAG, "continueOnClick: ");


        enteredMobNo = editText.getText().toString();
        Log.d(TAG, "continueOnClick: " + enteredMobNo);
//        disableContinueTV();

        verifyMobileNoInServerIfNotPost(enteredMobNo,view);

    }

    private boolean otpIsValid() {

        return true;
    }


    private void verifyMobileNoInServerIfNotPost(@NonNull final String mobileNo, View v) {
        final View s =v;


//       String url = WEB_API_ADDRESS + VERIFY_MOBILE_NO_API_CALL+mobileNo;
//       String url = "http://192.168.0.106/PickCApi/"+VERIFY_MOBILE_NO_API_CALL+mobileNo;
//        Log.d(TAG, "postResponse2: verifyMobileNoinServer " + url);
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Verifying your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        String url = WEB_API_ADDRESS+VERIFY_MOBILE_NO_API_CALL+mobileNo;

        Log.d(TAG, "postResponse2: verifyMobileNoinServer "+url);
        RequestQueue queue = Volley.newRequestQueue(this);
        Request<Boolean> booleanRequest = new Request<Boolean>(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                s.setEnabled(true);
                disableOnBackPressed = false;
                Log.d(TAG, "onErrorResponse:  verifyMobileNoinServer 123 "+error);
            }
        }) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {


                handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
                disableOnBackPressed = false;
                Log.d(TAG, "parseNetworkResponse: verifyMobileNoinServer 123 "+response.toString());
                String jsonString = null;
                progressDialog.dismiss();
//                s.setEnabled(true);
                try {
                    jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "parseNetworkResponse: "+jsonString);
                if (jsonString != null){
                    (ForgotPasswordActivity.this).isMobileNoExists(jsonString,mobileNo);
                    return null;
                }
                displayErrorMessage("Server busy. Please try again after some time");
                return null;

            }

            @Override
            protected void deliverResponse(Boolean response) {
                progressDialog.dismiss();
//                s.setEnabled(true);
                disableOnBackPressed = false;
                Log.d(TAG, "deliverResponse: verifyMobileNoinServer 123 "+response);
            }
        };
        queue.add(booleanRequest);

    }
    public void isMobileNoExists(String booleanValue, @NonNull final String mobileNo) {
        switch (booleanValue){
            case TRUE:
                //show alert dialog

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        generateOtp(mobileNo);
                    }
                });
                break;
            case FALSE:
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        createAlertDialogIfNotMobNoExists();
//
                    }
                });
                break;
            default:

                break;
        }
    }

    private void createOTPdialog() {
        final Dialog dialog = new Dialog(ForgotPasswordActivity.this, R.style.AppDialogTheme1);
        dialog.setTitle("OTP");
        dialog.setContentView(R.layout.otp_dialog_layout);
        dialog.show();
        final EditText enteredOTP_ET = (EditText) dialog.findViewById(R.id.editText_otp);
        TextInputLayout textInputLayout = (TextInputLayout) dialog.findViewById(R.id.editHint);
        textInputLayout.setHint("Kindly enter OTP to reset your pswd");
        enteredOTP_ET.setText("");
        TextView submitBtn = (TextView) dialog.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBookingConfirmed: forgot password");
                String enteredOTP = enteredOTP_ET.getText().toString();
                if (otpIsValid()) {
                    dialog.dismiss();
                    //finish();
                }
            }
        });

    }
    private void generateOtp(String mobileNo){
        {
       final String mobileNu = mobileNo;
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
            progressDialog.setTitle("Generating OTP");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            disableOnBackPressed = true;
//            String url = WEB_API_ADDRESS+GET_OTP_FORGOT_PASSWORD_API_CALL+mobileNo;
            String url = WEB_API_ADDRESS+"api/master/customer/forgotpassword/"+mobileNo;

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);

                            disableOnBackPressed = false;
                            Log.d(TAG, "onResponse: " + response);
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            disableOnBackPressed = false;
//                            progressDialog.dismiss();
                            Log.d(TAG, "onErrorResponse:12345 "+error);

                            if ((error+"").contains("OTP Generated")) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent intent = new Intent(getApplicationContext(), OtpForgetPasswordActivity.class);
                                        intent.putExtra("mobileNo", enteredMobNo);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);

                            }
                            progressDialog.dismiss();
                        }
                    });
            queue.add(jsObjRequest);
        }
    }
    private void displayErrorMessage(@NonNull String message){
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(message);
    }

    private void createAlertDialogIfNotMobNoExists(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this,R.style.AppDialogTheme1);
        builder.setTitle("Exists !");
        builder.setMessage("This mobile no. is not registered.");
        builder.setCancelable(false);
        builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                mobileNoET.setFocusable(true);
            }
        });
//        builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
//                finish();
//            }
//        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

