package com.prts.pickccustomer.ui.credentials;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.ui.SplashActivity;
import com.prts.pickccustomer.ui.WebViewActivityForSignUp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uday on 05-05-2016.
 */
public class SignUpActivity extends AppCompatActivity implements TextWatcher,Constants{

    private static final String TAG = "SignUpActivity";
    PickCCustomEditText nameET, mobileNoET, eMailIDET, passwordET, rePasswordET;
    PickCCustomTextVIew errorTV;
    PickCCustomTextVIew signUpBtnTV;
    String name,mobileNo,emailID,password,rePassword;
    boolean disableOnBackPressed = false;
    public static boolean checkedCheckBox = false;
    public static boolean mobileNumberExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
         initialize();

    }

    private void initialize(){
        nameET = (PickCCustomEditText)findViewById(R.id.name_txt_et);
        mobileNoET = (PickCCustomEditText)findViewById(R.id.mobile_number_txt_et);
        eMailIDET = (PickCCustomEditText)findViewById(R.id.email_txt_et);
        passwordET = (PickCCustomEditText)findViewById(R.id.password_txt_et);
        rePasswordET = (PickCCustomEditText)findViewById(R.id.re_enter_password_txt_et);

        errorTV = (PickCCustomTextVIew)findViewById(R.id.error_text_tv);
        signUpBtnTV = (PickCCustomTextVIew) findViewById(R.id.signUpTV);

        nameET.addTextChangedListener(this);
        mobileNoET.addTextChangedListener(this);
        eMailIDET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
        rePasswordET.addTextChangedListener(this);



        signUpBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                 name = nameET.getText().toString();
                 mobileNo = mobileNoET.getText().toString();
                 emailID = eMailIDET.getText().toString();
                 password = passwordET.getText().toString();
                 rePassword = rePasswordET.getText().toString();

                if (name.isEmpty()){
                    nameET.setError("Please enter name");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (name.startsWith(" ")){
                    nameET.setError("Starts with Spaces not Allowed");
                    displayErrorMessage("Starts with Spaces not Allowed");
                    return;
                }
                if (name.contains(" ")){
                    int maxIndex = name.length()-1;
                    int spaceIndex = name.indexOf(" ");
                    while (spaceIndex < maxIndex){
                        if ((name.charAt(spaceIndex+1)+"").equals(" ")){
                            nameET.setError("Multiple Spaces not Allowed");
                            displayErrorMessage("Multiple Spaces not Allowed");
                            return;
                        }else {
                            spaceIndex = name.indexOf(" ",spaceIndex+1);
                        }
                    }
                }
                if (name.endsWith(" ")){
                    nameET.setError("Ends with Spaces not Allowed");
                    displayErrorMessage("Ends with Spaces not Allowed");
                    return;
                }
                if (mobileNo.isEmpty()){
                    mobileNoET.setError("Please enter mobile no");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (mobileNo.length()<10){
                    mobileNoET.setError("Please enter 10 digit mobile no");
                    displayErrorMessage("Enter 10 digit mobile number");
                    return;
                }
                if (!emailID.isEmpty()){
                    if (!isValidEmail(emailID)){
                        eMailIDET.setError("Please enter correct email id");
                        displayErrorMessage("Please enter valid details");
                        return;
                    }
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



                    verifyMobileNoInServerIfNotPost(mobileNo,name,emailID,password);
                    handler = new Handler();


                    // Show alert dialog


                //postRegistrationDetailsToServer(mobileNo,name,emailID,password);
               // validateMobileNumberIfNotExistsInServer_RegisterUserCredentials(mobileNo,name,emailID,password);

            }
        });
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    Handler handler;
    private void displayErrorMessage(@NonNull String message){
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(message);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        errorTV.setVisibility(View.GONE);
        String mobNo = mobileNoET.getText().toString();
        if (!mobNo.isEmpty()) {
            if ("0123456".contains(mobNo.charAt(0) + "")) {
                mobileNoET.setText("");
                mobileNoET.setError("Please enter valid mobile no.");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void verifyMobileNoInServerIfNotPost(@NonNull final String mobileNo, @NonNull final String name,
                                                 @NonNull final String emailId, @NonNull final String password){


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

                disableOnBackPressed = false;
                Log.d(TAG, "onErrorResponse:  verifyMobileNoinServer 123 "+error);
            }
        }) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {


                disableOnBackPressed = false;
                Log.d(TAG, "parseNetworkResponse: verifyMobileNoinServer 123 "+response.toString());
                String jsonString = null;
                try {
                    jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "parseNetworkResponse: "+jsonString);
                if (jsonString != null){
                   (SignUpActivity.this).isMobileNoExists(jsonString,mobileNo,name,emailId,password);
                    return null;
                }
                displayErrorMessage("Server busy. Please try again after some time");
                progressDialog.dismiss();

                return null;

            }

            @Override
            protected void deliverResponse(Boolean response) {
                progressDialog.dismiss();

                disableOnBackPressed = false;
                Log.d(TAG, "deliverResponse: verifyMobileNoinServer 123 "+response);
            }
        };
        queue.add(booleanRequest);
    }


    private void postRegistrationDetailsToServer(@NonNull String mobileNo, @NonNull String name,
                                                 @NonNull String emailId, @NonNull String password){

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Registering your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        String url = WEB_API_ADDRESS+CUSTOMER_SAVE_API_CALL;
        JSONObject obj = new JSONObject();
        try {


            obj.put(MOBILE_NO_JSON_KEY, mobileNo);
            obj.put(NAME_JSON_KEY, name);
            obj.put(MAIL_ID_JSON_KEY, emailId);
            obj.put(PASSWORD_JSON_KEY, password);
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

                        disableOnBackPressed = false;
                        Log.d(TAG, "onResponse: "+response);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(!checkedCheckBox ) {
                                    String completeUrl = "http://www.pickcargo.in/mobile/terms.html";
                                    Intent signIntent = new Intent(getApplicationContext(), WebViewActivityForSignUp.class);
                                    signIntent.putExtra(WebViewActivityForSignUp.URL, completeUrl);

                                    startActivity(signIntent);
                                }


                            }
                        },1000);
                        progressDialog.dismiss();
                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disableOnBackPressed = false;
                        progressDialog.dismiss();
                        Log.d(TAG, "onErrorResponse:12345 "+error);
                        if ((error+"").contains("SAVED SUCSSFULLY")){



                            displayErrorMessage("OTP will be generated");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(!checkedCheckBox ) {
                                        String completeUrl = "http://www.pickcargo.in/mobile/terms.html";
                                         Intent signIntent = new Intent(getApplicationContext(), WebViewActivityForSignUp.class);
                                        signIntent.putExtra(WebViewActivityForSignUp.URL, completeUrl);

                                        startActivity(signIntent);
                                    }


//                                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
//                                    finish();
                                }
                            },3000);
                        }else {
                            displayErrorMessage("Please check your internet connection");
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        progressDialog.dismiss();
                        //hideProgressDialog();
                    }
                });
        queue.add(jsObjRequest);
    }

    private void validateMobileNumberIfNotExistsInServer_RegisterUserCredentials
            (@NonNull final String mobileNo, @NonNull final String name,
             @NonNull final String emailId, @NonNull final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Verifying your mobile no");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS+"api/master/customer/list";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> mobileNosAL = new ArrayList<>();
                Log.d(TAG, "onResponse:12 "+response);
                displayErrorMessage(response.toString());
                for (int i = 0; i<response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String mobileNo = jsonObject.getString(MOBILE_NO_JSON_KEY);
                        mobileNosAL.add(mobileNo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                disableOnBackPressed = false;
                progressDialog.dismiss();
                if (mobileNosAL.contains(mobileNo)){
                    displayErrorMessage("Entered mobile No. already registered");
                }else {
                    //post to server
                    postRegistrationDetailsToServer(mobileNo,name,emailId,password);
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                disableOnBackPressed = false;
                progressDialog.dismiss();
                displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse:78945 "+error);
                //TODO: handle failure
            }
        });


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    @Override
    public void onBackPressed() {
        if (disableOnBackPressed){

        }else {
            SplashActivity.enableAnimation = false;
            startActivity(new Intent(SignUpActivity.this,SplashActivity.class));
            super.onBackPressed();
        }
    }
    public final static boolean isValidEmail(String target) {
        if (target == null || target.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }




    public void isMobileNoExists(String booleanValue, @NonNull final String mobileNo, @NonNull final String name,
                                 @NonNull final String emailId, @NonNull final String password) {
        switch (booleanValue){
            case TRUE:

                mobileNumberExists = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        createAlertDialogIfMobNoExists();
                    }
                });
                break;
            case FALSE:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        postRegistrationDetailsToServer(mobileNo,name,emailId,password);


                    }
                });
                break;
            default:

                break;
        }
    }


    private void createAlertDialogIfMobNoExists(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this,R.style.AppDialogTheme1);
        builder.setTitle("Exists !");
        builder.setMessage("This mobile no. is already registered.");
        builder.setCancelable(false);
        builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mobileNoET.setFocusable(true);
            }
        });
        builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("GettingBack").equals("gotBack"))

        {
            final Dialog dialog = new Dialog(SignUpActivity.this,R.style.AppDialogTheme1);
            dialog.setTitle("OTP");
            dialog.setContentView(R.layout.otp_dialog_layout);
            dialog.show();
            final EditText enteredOTP_ET = (EditText)dialog.findViewById(R.id.editText_otp);
            TextView submitBtn = (TextView) dialog.findViewById(R.id.submitBtn);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String enteredOTP = enteredOTP_ET.getText().toString();
                    if (enteredOTP.isEmpty()){
                        displayErrorMessage("Please enter OTP");
                        return;
                    }
                    else
                        {
//                            postRegistrationOtpToServerStringRequest(mobileNo,enteredOTP);

                        postRegistrationOtpToServer(mobileNo,enteredOTP);
                    }


//                    if (otpIsValid()){
////                        postRegistrationDetailsToServer(mobileNo,name,emailID,password);
//                        dialog.dismiss();
//                    }
                }
            });
        }

    }
    private void postRegistrationOtpToServer(@NonNull String mobileNo, @NonNull String enteredOTP)
                                                 {

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Validating OTP");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
//        String url = WEB_API_ADDRESS+VERIFY_OTP_NO_API_CALL+mobileNo+"/"+enteredOTP;
        String url = WEB_API_ADDRESS+VERIFY_OTP_NO_API_CALL+mobileNo+"/"+enteredOTP;


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        progressDialog.dismiss();
                        disableOnBackPressed = false;
                        Log.d(TAG, "onResponse: " + response);
                        //goto login Activity





//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
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
                        if ((error+"").contains("OTP Verified...!")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    displayErrorMessage("Registration Completed");
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }, 3000);
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        progressDialog.dismiss();
                        //hideProgressDialog();
                    }
                });
        queue.add(jsObjRequest);
    }
    private void postRegistrationOtpToServerStringRequest(@NonNull String mobileNo, @NonNull String enteredOTP) {
        String tag_string_req = "string_req";
        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Validating OTP");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = WEB_API_ADDRESS+VERIFY_OTP_NO_API_CALL+mobileNo+"/"+enteredOTP;
        StringRequest strReq = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                displayErrorMessage("Registration Completed");


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    }
                }, 3000);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

// Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        queue.add(strReq);
    }

}
