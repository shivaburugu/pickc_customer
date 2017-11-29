package com.prts.pickccustomer.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.FCM.MyFirebaseInstanceIDService;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.credentials.LoginActivity;
import com.prts.pickccustomer.ui.credentials.SignUpActivity;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends Activity implements Constants {


    private static final String TAG = "SplashActivity";
    Typeface limeLightFontFace;

    PickCCustomTextVIew texthead;

    public static boolean enableAnimation = true;

    PickCCustomTextVIew errorTV;
    LinearLayout linearLayout;
    ImageView imageView;

    TranslateAnimation animation;
    TranslateAnimation animation1;

    @BindView(R.id.tvsignup)
    PickCCustomTextVIew tvsignup;
    @BindView(R.id.tvlogin)
    PickCCustomTextVIew tvlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        errorTV = (PickCCustomTextVIew)findViewById(R.id.error_textView_splash);
        imageView = (ImageView) findViewById(R.id.splash_iv);
        TextView appNameTV = (TextView)findViewById(R.id.title_appName_textView);
        TextView appNameTV3 = (TextView)findViewById(R.id.title_appName_textView3);
        limeLightFontFace = Typeface.createFromAsset(getAssets(), "fonts/limelight.ttf");
        appNameTV.setTypeface(limeLightFontFace);
        appNameTV3.setTypeface(limeLightFontFace);
        animation = new TranslateAnimation(0.0f, 0.0f,
                0.0f, -1000.0f);
      /* TranslateAnimation animation = new TranslateAnimation(500.0f, 800.0f,
                0.0f, 0.0f);  */       //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(1500);  // animation duration
        animation.setRepeatCount(0);  // animation repeat count
        animation.setRepeatMode(1);   // repeat animation (left to right, right to left )
//      animation.setFillAfter(true);




        texthead = (PickCCustomTextVIew) findViewById(R.id.splash_text_id);

        linearLayout = (LinearLayout)findViewById(R.id.title_ll);
        animation1 = new TranslateAnimation(0.0f, 00.0f,
                0.0f, 1000.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation1.setDuration(1500);  // animation duration
        animation1.setRepeatCount(0);  // animation repeat count
        animation1.setRepeatMode(1);   // repeat animation (left to right, right to left )
//      animation.setFillAfter(true);



        /*face1 = Typeface.createFromAsset(getAssets(), "fonts/AHRONBD.TTF");

        texthead.setTypeface(face1);*/

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!haveNetworkConnection(SplashActivity.this)){
                    displayErrorMessage("Please Connect to the internet and Try Again");
                    buildAlertMessageNoInternet();
                    return;
                }
            }
        },3000);*/
        //verifyToken();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!haveNetworkConnection(SplashActivity.this)){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    displayErrorMessage("Please Connect to the internet and Try Again");
                    buildAlertMessageNoInternet();
                    return;

            }
        },1000);
        }else {
            verifyToken();
        }
    }

    @OnClick(R.id.tvsignup)
    public void signUpOnClick(View view) {
        startActivity(new Intent(SplashActivity.this,SignUpActivity.class));
        finish();
    }

    @OnClick(R.id.tvlogin)
    public void loginOnClick(View view) {

        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        finish();
    }


    private void verifyToken() {

        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS+ISMOBILEEXIST;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: 7896 "+response);

                ((LinearLayout)findViewById(R.id.signup_linear)).setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyFirebaseInstanceIDService.postDeviceIdToServer(getApplicationContext(),
                                CredentialsSharedPref.getMobileNO(getApplicationContext()),
                                MyFirebaseInstanceIDService.getdeviceId(SplashActivity.this));
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                        finish();
                    }
                },2000);
                if (enableAnimation) {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            imageView.startAnimation(animation);  // start animation
                            //texthead.startAnimation(animation1);  // start animation
                            linearLayout.startAnimation(animation1);
                        }
                    },1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if ((error+"").contains("TimeoutError")){

                    ((LinearLayout)findViewById(R.id.server_error_linear)).setVisibility(View.VISIBLE);
                    ((LinearLayout)findViewById(R.id.signup_linear)).setVisibility(View.GONE);
                    displayErrorMessage("Server busy\nPlease try again after some time");
                    return;
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ((LinearLayout)findViewById(R.id.server_error_linear)).setVisibility(View.GONE);
                        ((LinearLayout)findViewById(R.id.signup_linear)).setVisibility(View.VISIBLE);
                    }
                },1000);
                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: 7896 "+error);
                //TODO: handle failure
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(SplashActivity.this);
            }
        };


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    private void displayErrorMessage(String message) {
        ((LinearLayout)findViewById(R.id.server_error_linear)).setVisibility(View.VISIBLE);
        errorTV.setText(message);
    }

    @Override
    protected void onDestroy() {
        enableAnimation = true;
        super.onDestroy();
    }

    public void refreshServerOnClick(View view) {
        if (!haveNetworkConnection(SplashActivity.this)){
            displayErrorMessage("Please Connect to the internet and Try Again");
            buildAlertMessageNoInternet();
            return;
        }
        ((LinearLayout)findViewById(R.id.server_error_linear)).setVisibility(View.GONE);
        verifyToken();
    }
    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NO internet connection");
        builder.setMessage("Wifi & Data both are disabled. Please enable any one")
                .setCancelable(false)
                .setPositiveButton("Enable Wifi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int id) {
                       /* WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(true);*/

                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshServerOnClick(new View(SplashActivity.this));
                    }
                })
                .setNegativeButton("Enable Data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }
    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
