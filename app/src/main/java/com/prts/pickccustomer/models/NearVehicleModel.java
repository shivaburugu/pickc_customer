package com.prts.pickccustomer.models;

/**
 * Created by LOGICON on 22-07-2017.
 */

public class NearVehicleModel {

    long vehicleGroup;
    long vehicleType;
    double longitude;
    double latitude;

    public long getVehicleGroup() {
        return vehicleGroup;
    }

    public void setVehicleGroup(long vehicleGroup) {
        this.vehicleGroup = vehicleGroup;
    }

    public long getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(long vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
