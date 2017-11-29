package com.prts.pickccustomer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity implements TextWatcher, Constants {

    private static final String TAG = "ChangePasswordAct";
    PickCCustomEditText oldPasswordET, newPasswordET, newReEnteredPasswordET;
    PickCCustomTextVIew continueTV;
    PickCCustomTextVIew errorTV;
    private boolean disableOnBackPressed;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handler = new Handler();
        oldPasswordET = (PickCCustomEditText) findViewById(R.id.old_password_txt_et);
        newPasswordET = (PickCCustomEditText) findViewById(R.id.new_password_txt_et);
        newReEnteredPasswordET = (PickCCustomEditText) findViewById(R.id.new_re_enter_password_txt_et);

        continueTV = (PickCCustomTextVIew) findViewById(R.id.continueTV);
        errorTV = (PickCCustomTextVIew) findViewById(R.id.error_text_tv);

        oldPasswordET.addTextChangedListener(this);
        newPasswordET.addTextChangedListener(this);
        newReEnteredPasswordET.addTextChangedListener(this);

        continueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = oldPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String newRePassword = newReEnteredPasswordET.getText().toString();

                if (oldPassword.isEmpty()) {
                    oldPasswordET.setError("Please enter old password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (newPassword.isEmpty()) {
                    newPasswordET.setError("Please enter new password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (newPassword.length() < 8) {
                    newPasswordET.setError("Please enter new password");
                    displayErrorMessage("Password must be more than 6 characters");
                    return;
                }
                if (newPassword.length() > 20) {
                    newPasswordET.setError("Please enter new password");
                    displayErrorMessage("Password must be below 20 characters");
                    return;
                }
                if (!isValidPassword(newPassword)){
                    newPasswordET.setError("Enter strong password");
                    displayErrorMessage("Password must contain 1 Uppercase, 1 Lowercase, 1 Special character and 1 Number");
                    return;
                }
                if (newRePassword.isEmpty()) {
                    newReEnteredPasswordET.setError("Please re-enter new password");
                    displayErrorMessage("Please enter valid details");
                    return;
                }
                if (!newPassword.equals(newRePassword)) {
                    newReEnteredPasswordET.setError("New password and re-enter password are not matched");
                    displayErrorMessage("password and re-enter password are not matched");
                    return;
                }

                //
                updatePasswordInServer(oldPassword, newPassword);
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
    private void displayErrorMessage(@NonNull String message) {
        Log.d(TAG, "displayErrorMessage: " + message);
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

    private void updatePasswordInServer(@NonNull String oldPassword,
                                        @NonNull final String newPassword) {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppDialogTheme1);
        progressDialog.setTitle("Updating your password");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;
        String mobileNo = CredentialsSharedPref.getMobileNO(ChangePasswordActivity.this);
       String url = WEB_API_ADDRESS + UPDATE_PROFILE_PASSWORD_API_CALL + mobileNo;
//        String url = "http://192.168.0.120/PickCApi/api/master/customer/changepassword/"+mobileNo;
        final JSONObject obj = new JSONObject();
        try {
           /* obj.put("id", "1");
            obj.put("name", "myname");*/

            obj.put(MOBILE_NO_JSON_KEY, mobileNo);
            obj.put(PASSWORD_JSON_KEY, oldPassword);
            obj.put(NEW_PASSWORD_JSON_KEY, newPassword);
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
                        Log.d(TAG, "onResponse: "+response);
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        disableOnBackPressed = false;
                        //hideProgressDialog();

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
                        if ((error + "").contains("ParseError")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 1000);

                            progressDialog.dismiss();
                            disableOnBackPressed = false;
                            Log.d(TAG, "parseNetworkResponse: verifyMobileNoinServer 123 " + error.toString());
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
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                CredentialsSharedPref.setPassword(getApplicationContext(),newPassword);
                                                showSnackBar(continueTV, "Password changed successfully");
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finish();
                                                    }
                                                }, 1000);
                                            }
                                        });
                                        break;
                                    case FALSE:
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AppDialogTheme1);
                                                builder.setTitle("Failure");
                                                builder.setCancelable(false);
                                                builder.setMessage("Wrong password. Please check your current password and try again");
                                                builder.setNegativeButton("OK", null);
                                                AlertDialog alertDialog = builder.create();
                                                alertDialog.show();
                                            }
                                        });
                                        break;
                                    default:

                                        break;
                                }
                                return;
                            }
                            displayErrorMessage("Server busy. Please try again after some time");
                        } else {
                            displayErrorMessage("Please check your internet connection");
                        }
                        //hideProgressDialog();
                        progressDialog.dismiss();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(ChangePasswordActivity.this);
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

    private void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.appThemeYellow));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        snackView.setLayoutParams(params);
        snackbar.show();
    }
}
