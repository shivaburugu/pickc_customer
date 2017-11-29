package com.prts.pickccustomer.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.prts.pickccustomer.R;

import static com.prts.pickccustomer.constants.Constants.VEHICLE_TYPE_CLOSED;
import static com.prts.pickccustomer.constants.Constants.VEHICLE_TYPE_OPEN;
import static com.prts.pickccustomer.constants.Constants.WEB_API_ADDRESS;

/**
 * Created by LOGICON3 on 4/25/2017.
 */

public class ZoomPicture extends AppCompatActivity {
    ImageView iv,closeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Window window = this.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getSupportActionBar().hide();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        setContentView(R.layout.custom_fullimage_dialog);
        this.setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        int selectedTruckID = intent.getIntExtra("selectedTruckID", 0);
        int selectedvehicleTypeID = intent.getIntExtra("selectedvehicleTypeID", 0);


        String url = getTruckSelectedIconForRateCard(selectedTruckID, selectedvehicleTypeID);
//        rateCardIV.setImageResource(IsConfirmBookingInfo.selectedTruckicon);


        iv = (ImageView)findViewById(R.id.fullimage);
        closeView = (ImageView) findViewById(R.id.close_rate_card_imageVIew);
//        iv.setImageResource(R.drawable.very_large_image);
        Picasso.with(ZoomPicture.this).load(url).into(iv);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    public String getTruckSelectedIconForRateCard(int vehicleGroupId, int vehicletypeId) {
        switch (vehicleGroupId) {
            case 1000:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/128-Mini-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/128-Mini-closed.png";
                }
                return WEB_API_ADDRESS+"Images/128-Mini-open.png";
            case 1001:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/128-Small-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/128-Small-closed.png";
                }
                return WEB_API_ADDRESS+"Images/128-Small-open.png";
            case 1002:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/128-Small-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/128-Small-closed.png";
                }
                return WEB_API_ADDRESS+"Images/128-Small-open.png";
            case 1003:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/128-Large-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/128-Large-closed.png";
                }
                return WEB_API_ADDRESS+"Images/128-Large-open.png";
        }
        return WEB_API_ADDRESS+"Images/128-Mini-open.png";
    }
}
