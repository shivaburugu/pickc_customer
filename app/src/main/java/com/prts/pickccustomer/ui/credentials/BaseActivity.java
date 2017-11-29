package com.prts.pickccustomer.ui.credentials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by LOGICON on 15-07-2017.
 */

public class BaseActivity extends AppCompatActivity {


    OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }
}
