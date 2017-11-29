package com.prts.pickccustomer.support;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prts.pickccustomer.R;


public class CallUsSupportFragment extends Fragment {
    public CallUsSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call_us_support, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view);
    }

    private static final int REQUEST_CALL_PHONE = 2554;
    private void initialize(View view) {
        TextView callUsTV = (TextView) view.findViewById(R.id.call_us_TV);
        callUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeACall();
            }
        });
    }

    private void makeACall() {
        //for 6.0 permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(getActivity(),
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
            Toast.makeText(getActivity(), "Please give permission to call", Toast.LENGTH_SHORT).show();
        }
    }
}
