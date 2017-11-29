package com.prts.pickccustomer.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.prts.pickccustomer.BuildConfig;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;

/**
 * Created by Uday on 14-11-2016.
 */

public class AboutActivity extends AppCompatActivity {
    Typeface limeLightFontFace;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PickCCustomTextVIew textVIew = (PickCCustomTextVIew)findViewById(R.id.versionTV);
        TextView titleTv = (TextView)findViewById(R.id.title_appName_textView);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        limeLightFontFace = Typeface.createFromAsset(getAssets(), "fonts/limelight.ttf");
//        textVIew.setText(versionCode + "("+versionName+")");
        titleTv.setTypeface(limeLightFontFace);
    }
}
