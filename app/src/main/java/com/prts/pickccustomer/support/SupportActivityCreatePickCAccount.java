package com.prts.pickccustomer.support;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.prts.pickccustomer.R;

public class SupportActivityCreatePickCAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_create_pick_caccount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
    }

    private static final int REQUEST_CALL_PHONE = 2554;
    private void initialize() {
        TextView callUsTV = (TextView) findViewById(R.id.call_us_TV);
        callUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeACall();
            }
        });
        TextView writeToUsTV = (TextView)findViewById(R.id.write_us_TV);
        writeToUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivityCreatePickCAccount.this, WriteToUsSupportActivity.class));
            }
        });
    }

    private void makeACall() {
        //for 6.0 permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(SupportActivityCreatePickCAccount.this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(SupportActivityCreatePickCAccount.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:1234567890"));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeACall();
        } else {
            Toast.makeText(SupportActivityCreatePickCAccount.this, "Please give permission to call", Toast.LENGTH_SHORT).show();
        }
    }
}
