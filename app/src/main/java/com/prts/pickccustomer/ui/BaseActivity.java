package com.prts.pickccustomer.ui;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.prts.pickccustomer.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Srinath on 20/07/17.
 */

public class BaseActivity extends AppCompatActivity {


    OkHttpClient mOkHttpClient;
    public ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        mOkHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


    }


    public boolean hasNetwork() {

        boolean flag = false;
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            flag = (networkInfo != null && networkInfo.isAvailable() && (
                    networkInfo.isConnectedOrConnecting() || networkInfo.isConnected()));


            if (flag) {
                return flag;
            } else {
                Toast.makeText(this, getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }

        return flag;


    }

}
