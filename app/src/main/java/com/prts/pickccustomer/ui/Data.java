package com.prts.pickccustomer.ui;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Uday on 25-08-2016.
 */
public class Data {
    private static Data data;
    private LatLng pickUpLocationLatlng;
    private LatLng dropLocationLatlng;
    private double distance;
    private int selectedVehicleGroup;
    private int selectedVehicleType;
    private boolean isBookingConfirmed;
    public static boolean createDataInstance(){
        if (data!=null) {
            data = new Data();
            return true;
        }
        return false;
    }
    public static Data getDataInstance(){
        return data;
    }

}
