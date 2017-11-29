package com.prts.pickccustomer.support;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.prts.pickccustomer.R;
import com.prts.pickccustomer.support.fragments.IDidntReceiveOTPFragment;

public class TesllUsAboutItSupportActivity extends AppCompatActivity {

    private static final String TAG = "TellUsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesll_us_about_it_support);
        int pos = getIntent().getIntExtra("pos",0);
        displayView(pos);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Log.d(TAG, "displayView: position "+position);
        switch (position){
            case 0:
                fragment = new IDidntReceiveOTPFragment();
                break;
            default:
                break;
        }

        if (fragment != null){
            // Getting reference to the FragmentManager
            FragmentManager fragmentManager  = getSupportFragmentManager();
            // Creating a fragment transaction
            FragmentTransaction ft = fragmentManager.beginTransaction();
            // Adding a fragment to the fragment transaction
            ft.replace(R.id.frame_container_tell_us_activity, fragment);
            // Committing the transaction
            ft.commit();
        }
    }


}
