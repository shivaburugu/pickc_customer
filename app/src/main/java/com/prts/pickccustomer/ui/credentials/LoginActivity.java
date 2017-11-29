package com.prts.pickccustomer.ui.credentials;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.prts.pickccustomer.FCM.MyFirebaseInstanceIDService;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.HomeActivity;
import com.prts.pickccustomer.ui.SplashActivity;
import com.prts.pickccustomer.ui.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

/**
 * Created by Uday on 05-05-2016.
 */
public class LoginActivity extends BaseActivity implements TextWatcher, Constants {
    private static final String TAG = "LoginActivity";

    PickCCustomEditText mobileNoET, passwordET;
    PickCCustomTextVIew errorTV, login_TV;
    Button forgotPwdBtn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    Handler handler;

    private void initialize() {
        mobileNoET = (PickCCustomEditText) findViewById(R.id.mobile_txt_et);
        passwordET = (PickCCustomEditText) findViewById(R.id.password_txt_et);

        errorTV = (PickCCustomTextVIew) findViewById(R.id.error_text_tv);
        login_TV = (PickCCustomTextVIew) findViewById(R.id.login_TV);

        PickCCustomTextVIew helpTextVIew = (PickCCustomTextVIew) findViewById(R.id.helpTV);
        helpTextVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String completeUrl = WEB_API_ADDRESS + "Help/menu";
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra("url", completeUrl);
                startActivity(intent);
            }
        });
        forgotPwdBtn = (Button) findViewById(R.id.forgot_pwd_button);
        forgotPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        mobileNoET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);

        login_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobileNo = mobileNoET.getText().toString();
                String password = passwordET.getText().toString();
                Log.d(TAG, password + " onBookingConfirmed: " + mobileNo);

                if (mobileNo.isEmpty()) {
                    mobileNoET.setError("Please enter mobile no");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (mobileNo.length() < 10) {
                    mobileNoET.setError("Please enter 10 digit mobile no.");
                    displayErrorMessage("Enter 10 digit mobile number");
                    return;
                }
                if (password.isEmpty()) {
                    passwordET.setError("Please enter password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                verifyLoginDetailsInServer(mobileNo,password);
            }
        });

    }

    private void displayErrorMessage(@NonNull final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTV.setVisibility(View.VISIBLE);
                errorTV.setText(message);
            }
        });

    }

    boolean disableOnBackPressed = false;

    private void verifyLoginDetailsInServer(@NonNull final String mobileNo, @NonNull String password) {

        progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Verifying your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = WEB_API_ADDRESS + LOG_IN_API_CALL;

        FormBody.Builder builder = new FormBody.Builder();

        builder.add(MOBILE_NO_JSON_KEY, mobileNoET.getText().toString().trim());
        builder.add(PASSWORD_JSON_KEY, passwordET.getText().toString().trim());


        okhttp3.Request request = new okhttp3.Request
                .Builder()
                .url(url)
                .post(builder.build())
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {


                String string_res=response.body().string();
                Log.e("LOGIN",string_res);
                if(string_res.contains("OTP not Verified...!"))
                {

                }


                validateLogin(string_res);

            }
        });


        //oldApi();
    }

    private void validateLogin(String string_res) {

        progressDialog.dismiss();
        try {
           JSONObject jsonObject=new JSONObject(string_res);



            if(jsonObject.has(TOKEN_JSON_KEY)) {
                Log.d(TAG, "validateLogin: "+jsonObject.getString(TOKEN_JSON_KEY));
                CredentialsSharedPref.setAuthToken(LoginActivity.this, jsonObject.getString(TOKEN_JSON_KEY));
                CredentialsSharedPref.setMobileNumber(LoginActivity.this,
                        mobileNoET.getText().toString().trim());
                getUserInfoFromServer(mobileNoET.getText().toString().trim());
                MyFirebaseInstanceIDService.postDeviceIdToServer(getApplicationContext(),
                        CredentialsSharedPref.getMobileNO(getApplicationContext()),
                        FirebaseInstanceId.getInstance().getToken());
            }else
            {
                if(jsonObject.has(MESSAGE_ERROR))
                displayErrorMessage(jsonObject.getString(MESSAGE_ERROR));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void oldApi(final String mobileNo) {
        String url = WEB_API_ADDRESS + LOG_IN_API_CALL;
        JSONObject obj = new JSONObject();
        try {

            obj.put(MOBILE_NO_JSON_KEY, mobileNoET.getText().toString().trim());
            obj.put(PASSWORD_JSON_KEY, passwordET.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "postResponse2: " + obj);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        //hideProgressDialog();
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            String token = response.getString(TOKEN_JSON_KEY);
                            Log.d(TAG, "onResponse: 123456 token " + token);
                            Log.d(TAG, "onResponse: 123456 token mobileno " + mobileNo);
                            //login success
                            CredentialsSharedPref.setAuthToken(LoginActivity.this, token);
                            CredentialsSharedPref.setMobileNumber(LoginActivity.this, mobileNo);
                            getUserInfoFromServer(mobileNo);
                            MyFirebaseInstanceIDService
                                    .postDeviceIdToServer(getApplicationContext(),
                                            CredentialsSharedPref.getMobileNO(getApplicationContext()), MyFirebaseInstanceIDService.getdeviceId(LoginActivity.this));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //hideProgressDialog();
                        progressDialog.dismiss();
                        Log.d(TAG, "onErrorResponse:123456 " + error);
                        if ((error + "").contains("TimeoutError")) {

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        if ((error + "").contains("token")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: 123456 ");
                                }
                            }, 3000);
                        }
                        if ((error + "").contains("ServerError")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: 123456 ");
                                    displayErrorMessage("Mobile number or password is incorrect");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AppDialogTheme1);
                                    builder.setTitle("Alert !");
                                    /*PickCCustomTextVIew textVIew = new PickCCustomTextVIew(LoginActivity.this);
                                    textVIew.setText("Mobile number or password is incorrect");
                                    textVIew.setTextColor(Color.WHITE);
                                    builder.setView(textVIew);*/
                                    builder.setCancelable(false);
                                    builder.setMessage("Mobile number or password is incorrect");
                                    builder.setNegativeButton("Ok", null);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }, 1000);
                        } else {
                            displayErrorMessage("Please check your internet connection");
                        }
                        //hideProgressDialog();
                    }
                });

        queue.add(jsObjRequest);
    }

    private void getUserInfoFromServer(@NonNull final String mobileNo) {
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS + CUSTOMER_INFO_API_CALL + mobileNo;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 " + response);
                displayErrorMessage("Logged In Successfully. Please wait");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String name = response.getString(NAME_JSON_KEY);
                        CredentialsSharedPref.setName(LoginActivity.this, name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse:78945 " + error);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(LoginActivity.this);
            }
        };


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    /*private void isMobileNoExistsInServerIfNotRegister(@NonNull final String mobileNo,
                                                       @NonNull final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Verifying your credentials");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = "http://www.pickcargo.in/api/master/customer/list";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> mobileNosAL = new ArrayList<>();
                Log.d(TAG, "onResponse: "+response);
                displayErrorMessage(response.toString());
                for (int i = 0; i<response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String mobileNo = jsonObject.getString(TestingActivity.MOBILE_NO_JSON_KEY);
                        mobileNosAL.add(mobileNo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
                if (mobileNosAL.contains(mobileNo)){
                    //login success
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }else {
                    displayErrorMessage("Invalid Mobile No. or Password");
                    //post to server
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: "+error);
                //TODO: handle failure
            }
        });


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }*/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        errorTV.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onBackPressed() {

            SplashActivity.enableAnimation = false;
            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
            super.onBackPressed();
    }

    private void verifyMobileNoInServer(@NonNull final String mobileNo) {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Verifying your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = WEB_API_ADDRESS + VERIFY_MOBILE_NO_API_CALL + mobileNo;

        Log.d(TAG, "postResponse2: verifyMobileNoinServer " + url);
        RequestQueue queue = Volley.newRequestQueue(this);
        Request<Boolean> booleanRequest = new Request<Boolean>(Request.Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d(TAG, "onErrorResponse:  verifyMobileNoinServer 123 " + error);
            }
        }) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {

                progressDialog.dismiss();
                Log.d(TAG, "parseNetworkResponse: verifyMobileNoinServer 123 " + response.toString());
                String jsonString = null;
                try {
                    jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));


                    verifyLoginDetailsInServer(mobileNo, passwordET.getText().toString().trim());


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "parseNetworkResponse: " + jsonString);
                if (jsonString != null) {

                    return null;
                }
                displayErrorMessage("Server busy. Please try again after some time");
                return null;
            }

            @Override
            protected void deliverResponse(Boolean response) {
                progressDialog.dismiss();
                disableOnBackPressed = false;
                Log.d(TAG, "deliverResponse: verifyMobileNoinServer 123 " + response);
            }
        };
        queue.add(booleanRequest);
    }

    public void isMobileNoExists(String booleanValue, @NonNull final String mobileNo, @NonNull final String name,
                                 @NonNull final String emailId, @NonNull final String password) {
        switch (booleanValue) {
            case TRUE:

                break;
            case FALSE:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        displayErrorMessage("Please enter registered mobile number");
                    }
                });
                break;
            default:

                break;
        }
    }

    public void loginOnClick(View view) {

    }
}
