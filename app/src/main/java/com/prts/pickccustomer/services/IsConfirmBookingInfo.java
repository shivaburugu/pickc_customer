package com.prts.pickccustomer.services;

import com.prts.pickccustomer.R;

import java.io.Serializable;

/**
 * Created by Uday on 12-10-2016.
 */
public class IsConfirmBookingInfo implements Serializable {

    private String bookingNo;

    private String customerId;
    private boolean isConfirmed;

    private String driverId;
    private String vehicleNo;


    private String driverName;
    private String driverMobNo;
    private String driverImageUrl;

    private double driverLatitude;
    private double driverLongitude;

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    private int vehicleType;

    public static String vehicleDesc = "1.5 Ton Open truck";
    public static int selectedTruckicon = R.drawable.open_selected_1_5_ton;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    private String otp;

    public IsConfirmBookingInfo(String bookingNo, String customerId, boolean isConfirmed, String driverId, String vehicleNo, String driverName, String driverMobNo, String driverImageUrl, double driverLatitude, double driverLongitude , String otp, int vehicleType) {
        this.bookingNo = bookingNo;
        this.customerId = customerId;
        this.isConfirmed = isConfirmed;
        this.driverId = driverId;
        this.vehicleNo = vehicleNo;
        this.driverName = driverName;
        this.driverMobNo = driverMobNo;
        this.driverImageUrl = driverImageUrl;
        this.driverLatitude = driverLatitude;
        this.driverLongitude = driverLongitude;
        this.otp = otp;
        this.vehicleType = vehicleType;
    }

    public IsConfirmBookingInfo() {

    }

    public void updateIsConfirmBookingInfo(String bookingNo, String customerId, boolean isConfirmed, String driverId, String vehicleNo, String driverName, String driverMobNo, String driverImageUrl, double driverLatitude, double driverLongitude, String otp , int vehicleType) {
        this.bookingNo = bookingNo;
        this.customerId = customerId;
        this.isConfirmed = isConfirmed;
        this.driverId = driverId;
        this.vehicleNo = vehicleNo;
        this.driverName = driverName;
        this.driverMobNo = driverMobNo;
        this.driverImageUrl = driverImageUrl;
        this.driverLatitude = driverLatitude;
        this.driverLongitude = driverLongitude;
        this.otp = otp;
        this.vehicleType = vehicleType;
    }

    public String getDriverMobNo() {
        return driverMobNo;
    }

    public void setDriverMobNo(String driverMobNo) {
        this.driverMobNo = driverMobNo;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverImageUrl() {
        return driverImageUrl;
    }

    public void setDriverImageUrl(String driverImageUrl) {
        this.driverImageUrl = driverImageUrl;
    }

    public double getDriverLatitude() {
        return driverLatitude;
    }

    public void setDriverLatitude(double driverLatitude) {
        this.driverLatitude = driverLatitude;
    }

    public double getDriverLongitude() {
        return driverLongitude;
    }

    public void setDriverLongitude(double driverLongitude) {
        this.driverLongitude = driverLongitude;
    }
}
