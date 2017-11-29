package com.prts.pickccustomer.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ProfileActivity extends AppCompatActivity implements TextWatcher, Constants {

    private static final String TAG = "ProfileActivity";
    PickCCustomEditText nameET, eMailIDET;
            PickCCustomTextVIew passwordTV, mobileNoTV;
    PickCCustomTextVIew errorTV;
    PickCCustomTextVIew updateProfileTV;
    boolean disableOnBackPressed = false;
    String name,emailID,mobileNo;

    View nameDummyView, emailDummyView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserInfoFromServer(CredentialsSharedPref.getMobileNO(ProfileActivity.this));
        initialize();
    }

    private void initialize() {
        nameET = (PickCCustomEditText) findViewById(R.id.name_txt_et);
        mobileNoTV = (PickCCustomTextVIew) findViewById(R.id.mobile_number_txt_et);
        eMailIDET = (PickCCustomEditText) findViewById(R.id.email_txt_et);
        passwordTV = (PickCCustomTextVIew) findViewById(R.id.password_txt_et);
        passwordTV.setTextSize(16);

        nameDummyView = findViewById(R.id.dummyView_name);
        emailDummyView = findViewById(R.id.dummyView_email);
        nameDummyView.setVisibility(View.VISIBLE);
        emailDummyView.setVisibility(View.VISIBLE);

        nameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                nameDummyView.setVisibility((hasFocus)? View.GONE : View.VISIBLE);
            }
        });
        eMailIDET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                emailDummyView.setVisibility((hasFocus)? View.GONE : View.VISIBLE);
            }
        });
        mobileNoTV.setText(CredentialsSharedPref.getMobileNO(ProfileActivity.this));
        nameET.setText(CredentialsSharedPref.getName(ProfileActivity.this));
        eMailIDET.setText(CredentialsSharedPref.getEmailId(ProfileActivity.this));

        errorTV = (PickCCustomTextVIew) findViewById(R.id.error_text_tv);
        updateProfileTV = (PickCCustomTextVIew) findViewById(R.id.updateProfileTV);

        nameET.addTextChangedListener(this);
        mobileNoTV.addTextChangedListener(this);
        eMailIDET.addTextChangedListener(this);
        passwordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });


        updateProfileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();
                mobileNo = mobileNoTV.getText().toString();
                emailID = eMailIDET.getText().toString();

                if (name.isEmpty()) {
                    nameET.setError("Please enter name");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (name.startsWith(" ")) {
                    nameET.setError("Starts with Spaces not Allowed");
                    displayErrorMessage("Starts with Spaces not Allowed");
                    return;
                }
                if (name.contains(" ")) {
                    int maxIndex = name.length() - 1;
                    int spaceIndex = name.indexOf(" ");
                    while (spaceIndex < maxIndex) {
                        if ((name.charAt(spaceIndex + 1) + "").equals(" ")) {
                            nameET.setError("Multiple Spaces not Allowed");
                            displayErrorMessage("Multiple Spaces not Allowed");
                            return;
                        } else {
                            spaceIndex = name.indexOf(" ", spaceIndex + 1);
                        }
                    }
                }
                if (name.endsWith(" ")) {
                    nameET.setError("Ends with Spaces not Allowed");
                    displayErrorMessage("Ends with Spaces not Allowed");
                    return;
                }
                if (!emailID.isEmpty()) {
                    if (!isValidEmail(emailID)) {
                        eMailIDET.setError("Please enter correct email id");
                        displayErrorMessage("Please enter valid details");
                        return;
                    }
                }
//                try {
//                    String storedPassword = CredentialsSharedPref.getPassword(getApplicationContext());
//                    if (storedPassword.isEmpty()) {
//                        startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
//                        return;
//                    }
//                }catch (NullPointerException ae )
//                {
//                    startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
//                    return;
//                }

                final Dialog dialogsCall = new Dialog(ProfileActivity.this);
                dialogsCall.setTitle("Select Cargo Type");
                dialogsCall.setContentView(R.layout.activity_enter_your_password);
                dialogsCall.setCancelable(false);
                dialogsCall.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogsCall.show();
                ImageView closeIv = (ImageView) dialogsCall.findViewById(R.id.closeIV);
                closeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogsCall.dismiss();
                    }
                });
                PickCCustomTextVIew submit = (PickCCustomTextVIew) dialogsCall.findViewById(R.id.submitTV);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PickCCustomEditText old_password_txt_et = (PickCCustomEditText) dialogsCall.findViewById(R.id.old_password_txt_et);
                        String password = old_password_txt_et.getText().toString();
                        if (password.isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                        }

                        if (!password.isEmpty()) {
                            validatePassowrd(password, mobileNo, name, emailID);

//                            updateUserDetailsInServer(mobileNo, name, emailID);
//                            updateUserDetailsInServer(CredentialsSharedPref.getDriverID(getApplicationContext()), name);
                            dialogsCall.dismiss();

                        }
                    }
                });
            }
                public void validatePassowrd(final String passwor, String mobilenu, String profileName, String emailid )

                {

                    String url = WEB_API_ADDRESS+"api/master/customer/checkCustomerPassword/"+mobilenu+"/"+passwor;

                    OkHttpClient mOkHttpClient;
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

                    FormBody.Builder builder = new FormBody.Builder();


                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)

                            .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(ProfileActivity.this))
                            .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(ProfileActivity.this))

                            .get()
                            .build();


                    mOkHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            IpAddress.e("latlong",e.toString());
//                progressDialog.dismiss();

                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            String res = response.body().string();
                            IpAddress.e("latlong", res);
                            if (res.contains("SAVED SUCSSFULLY")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        updateUserDetailsInServer(passwor, mobileNo, name, emailID);
                                    }
                                });
                            }
                            if (res.contains("Failed")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(ProfileActivity.this, "Please Enter Correct Password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }



//                progressDialog.dismiss();
                        }
                    });





                }


//                updateUserDetailsInServer(mobileNo, name, emailID);


                // Show alert dialog

                //postRegistrationDetailsToServer(mobileNo,name,emailID,password);
                // validateMobileNumberIfNotExistsInServer_RegisterUserCredentials(mobileNo,name,emailID,password);


        });
    }

    Handler handler;

    private void displayErrorMessage(@NonNull String message) {
        errorTV.setVisibility(View.VISIBLE);
        errorTV.setText(message);
    }

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

    private void updateUserDetailsInServer(@NonNull final String password, @NonNull final String mobileNo, @NonNull final String name,
                                           @NonNull final String emailId) {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Registering your details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
//        String password = CredentialsSharedPref.getPassword(getApplicationContext());

        String url = WEB_API_ADDRESS + UPDATE_USER_PROFILE_API_CALL + mobileNo;

        JSONObject obj = new JSONObject();
        try {
           /* obj.put("id", "1");
            obj.put("name", "myname");*/

            obj.put(MOBILE_NO_JSON_KEY, mobileNo);
            obj.put(NAME_JSON_KEY, name);
            obj.put(MAIL_ID_JSON_KEY, emailId);
            obj.put(PASSWORD_JSON_KEY, password);
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
                        progressDialog.dismiss();
                        disableOnBackPressed = false;
                        Log.d(TAG, "onResponse: " + response);
                        //goto login Activity
                        displayErrorMessage("Updated successfully");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                                finish();
                            }
                        }, 1000);
                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disableOnBackPressed = false;
                        progressDialog.dismiss();
                        Log.d(TAG, "onErrorResponse:12345 " + error);
                        if ((error + "").contains("SAVED SUCSSFULLY")) {
                            displayErrorMessage("Updated successfully");
                            CredentialsSharedPref.setName(ProfileActivity.this,name);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);
                        } else {
                            displayErrorMessage("Please check your internet connection");
                        }

                        if ((error + "").contains("TimeoutError")) {

                            displayErrorMessage("Server busy. Please try again after some time");
                            AlertDialogActivity.showAlertDialogActivity(ProfileActivity.this, "Server Busy",
                                    "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                                    new AlertDialogActivity.OnPositiveBtnClickListener() {
                                        @Override
                                        public void onPositiveBtnClick() {
                                            updateUserDetailsInServer(password,mobileNo, name, emailId);
                                        }
                                    }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                        @Override
                                        public void onNegativeBtnClick() {

                                        }
                                    });
                            return;
                        }
                        //hideProgressDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(ProfileActivity.this);
            }
        };
        queue.add(jsObjRequest);
    }

    @Override
    public void onBackPressed() {
        if (disableOnBackPressed) {

        } else {
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

    private void getUserInfoFromServer(@NonNull final String mobileNo) {
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS + CUSTOMER_INFO_API_CALL + mobileNo;

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 " + response);
                displayErrorMessage("Logged In Successfully. Please wait");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        String name = response.getString(NAME_JSON_KEY);
                        String email = response.getString(MAIL_ID_JSON_KEY);
                        CredentialsSharedPref.setName(ProfileActivity.this, name);
                        CredentialsSharedPref.setEmailId(ProfileActivity.this, email);
                        nameET.setText(CredentialsSharedPref.getName(ProfileActivity.this));

                        eMailIDET.setText(CredentialsSharedPref.getEmailId(ProfileActivity.this));
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
                Log.d(TAG, "onErrorResponse:78945 " + error);
                //TODO: handle failure

                if ((error + "").contains("TimeoutError")) {
                    displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(ProfileActivity.this, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    getUserInfoFromServer(CredentialsSharedPref.getMobileNO(ProfileActivity.this));
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(ProfileActivity.this);
            }
        };


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    public void onCLickEtIv(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (view.getId()){
            case R.id.nameET_IV:
                nameET.requestFocus();
                nameET.setFocusableInTouchMode(true);
                imm.showSoftInput(nameET, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.email_et_iv:
                eMailIDET.requestFocus();
                eMailIDET.setFocusableInTouchMode(true);
                imm.showSoftInput(eMailIDET, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.password_et_iv:
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
                break;
        }
    }

    public void dummyOnClickLL(View view) {
        Log.d(TAG, "dummyOnClickLL: dummy click");
    }
}
