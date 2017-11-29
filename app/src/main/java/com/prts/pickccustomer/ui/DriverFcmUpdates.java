package com.prts.pickccustomer.ui;

import android.location.Location;

/**
 * Created by LOGICON on 28-08-2017.
 */

public class DriverFcmUpdates {
    public DriverFcmUpdates(){}
    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Location getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location driverLocation) {
        this.driverLocation = driverLocation;
    }

    Location driverLocation;

    String driverId;
    String latitude;
    String longitude;


    public String getVehicletype() {
        return vehicletype;
    }

    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    String vehicletype;
    String vehicleCategory;
    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    float bearing;

    public DriverFcmUpdates(String driverId, String latitude, String longitude, String vehicletype, String vehicleCategory, float bearing) {
        this.driverId = driverId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vehicletype = vehicletype;
        this.vehicleCategory = vehicleCategory;
        this.bearing = bearing;

    }
}
