package com.prts.pickccustomer.ui;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Srinath on 20/07/17.
 */

public class BaseFragment extends Fragment {

    OkHttpClient mOkHttpClient;
    ConnectivityManager connectivityManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        mOkHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }
}
