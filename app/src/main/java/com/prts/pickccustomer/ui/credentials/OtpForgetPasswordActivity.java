package com.prts.pickccustomer.ui.credentials;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.prts.pickccustomer.constants.Constants.VERIFY_OTP_FORGOT_PASSWORD_API_CALL;
import static com.prts.pickccustomer.constants.Constants.WEB_API_ADDRESS;

/**
 * Created by LOGICON3 on 4/26/2017.
 */

public class OtpForgetPasswordActivity extends AppCompatActivity {
    PickCCustomEditText  otpET, passwordET, rePasswordET;
    PickCCustomTextVIew errorTV;
    String password,otp, rePassword;
    boolean disableOnBackPressed = false;
    PickCCustomTextVIew signUpBtnTV;
    String mobileNo;
    private static final String TAG = "otpForgotPasswd";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_details);
        Intent intent = getIntent();
         mobileNo = intent.getStringExtra("mobileNo");
        initialize();
    }
    private void initialize()
    {

        otpET = (PickCCustomEditText)findViewById(R.id.mobile_number_txt_et);

        passwordET = (PickCCustomEditText)findViewById(R.id.password_txt_et);
        rePasswordET = (PickCCustomEditText)findViewById(R.id.re_enter_password_txt_et);

        errorTV = (PickCCustomTextVIew)findViewById(R.id.error_text_tv);

        signUpBtnTV = (PickCCustomTextVIew) findViewById(R.id.signUpTV);
        signUpBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwordET.getText().toString();
                rePassword = rePasswordET.getText().toString();
                otp = otpET.getText().toString();
                if (otp.isEmpty()){
                    displayErrorMessage("Please enter OTP");
                    return;
                }
                if (password.isEmpty()){
                    passwordET.setError("Please enter password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (password.length()<8){
                    passwordET.setError("Enter password again");
                    displayErrorMessage("Password must be more than 8 characters");
                    return;
                }
                if (password.length()>20){
                    passwordET.setError("Enter password again");
                    displayErrorMessage("Password must be below 20 characters");
                    return;
                }
                if (!isValidPassword(password)){
                    passwordET.setError("Enter strong password");
                    displayErrorMessage("Password must contain 1 Uppercase, 1 Lowercase, 1 Special character and 1 Number");
                    return;
                }
                if (rePassword.isEmpty()){
                    rePasswordET.setError("Please re-enter password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (!password.equals(rePassword)){
                    rePasswordET.setError("password and re-enter password are not matched");
                    displayErrorMessage("password and re-enter password are not matched");
                    return;
                }
//                verifyOtp();
                postRegistrationOtpToServer();

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                errorTV.setVisibility(View.GONE);

            }
        });


    }

    private void verifyOtp() {
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Validating OTP");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
       String url = WEB_API_ADDRESS;
//        String url = "http://192.168.0.103/PickCApi/"+"api/master/customer/verifyotp/"+mobileNo+otp;


//        Log.d(TAG, "postResponse2: "+obj);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        progressDialog.dismiss();
                        disableOnBackPressed = false;
                        Log.d(TAG, "onResponse: " + response);
                        //goto login Activity


//                        displayErrorMessage("Registration Completed");
//
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                                finish();
//                            }
//                        }, 3000);
                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disableOnBackPressed = false;

                        Log.d(TAG, "onErrorResponse:12345 "+error);
                        System.out.println("I got Return response of error  " + error);

                        if ((error+"").contains("OTP Verified...!")){
//                            postRegistrationOtpToServer();
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    displayErrorMessage("Updated Password");
//                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                                    finish();
//                                }
//                            }, 3000);
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        //hideProgressDialog();
                        progressDialog.dismiss();
                    }
                });
        queue.add(jsObjRequest);
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    private void displayErrorMessage(@NonNull String message){
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(message);
    }
    private void postRegistrationOtpToServer()
    {

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Validating OTP");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        String url = WEB_API_ADDRESS+VERIFY_OTP_FORGOT_PASSWORD_API_CALL;
//        String url = "http://192.168.0.120/PickCApi/"+VERIFY_OTP_FORGOT_PASSWORD_API_CALL;

        JSONObject obj = new JSONObject();
        try {
           /* obj.put("id", "1");
            obj.put("name", "myname");*/

            obj.put("MobileNo", mobileNo);
            obj.put("NewPassword", password);
            obj.put("OTP", otp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "postResponse2: "+obj);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        progressDialog.dismiss();
                        disableOnBackPressed = false;
                        Log.d(TAG, "onResponse: " + response);
                        //goto login Activity


                        displayErrorMessage("Updated Password");
                        CredentialsSharedPref.setPassword(getApplicationContext(),password);
//
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                                finish();
//                            }
//                        }, 3000);
                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disableOnBackPressed = false;
                        progressDialog.dismiss();
                        Log.d(TAG, "onErrorResponse:12345 "+error);
                        System.out.println("I got Return response of error  " + error);

                        if ((error+"").contains("Password updated...!")){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    displayErrorMessage("Updated Password");
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            }, 3000);
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        //hideProgressDialog();
                    }
                });
        queue.add(jsObjRequest);
    }

}
