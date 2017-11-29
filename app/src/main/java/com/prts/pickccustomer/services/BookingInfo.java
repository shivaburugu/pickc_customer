package com.prts.pickccustomer.services;

import java.io.Serializable;

/**
 * Created by Uday on 12-10-2016.
 */
public class BookingInfo implements Serializable {

    private String bookingNo;
    private String bookingDate;

    private String customerId;
    private String requiredDate;
    private String locationFrom;
    private String locationTo;

    private String cargoDescription;
    private int vehicleType;
    private int vehicleGroup;
    private String remarks;

    private boolean isConfirmed;
    private String confirmDate;

    private String driverId;
    private String vehicleNo;

    private boolean isCancelled;
    private String cancelledTime;
    private String cancelRemarks;

    private boolean isCompleted;
    private String completedTime;

    private String payLoad;
    private String cargoType;

    private double latitude;
    private double longitude;


    private double toLatitude;
    private double toLongitude;
    private String receiverMobileNo;

    public BookingInfo(String bookingNo, String bookingDate, String customerId, String requiredDate, String locationFrom, String locationTo, String cargoDescription, int vehicleType, int vehicleGroup, String remarks, boolean isConfirmed, String confirmDate, String driverId, String vehicleNo, boolean isCancelled, String cancelledTime, String cancelRemarks, boolean isCompleted, String completedTime, String payLoad, String cargoType, double latitude, double longitude, double toLatitude, double toLongitude, String receiverMobileNo) {
        this.bookingNo = bookingNo;
        this.bookingDate = bookingDate;
        this.customerId = customerId;
        this.requiredDate = requiredDate;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.cargoDescription = cargoDescription;
        this.vehicleType = vehicleType;
        this.vehicleGroup = vehicleGroup;
        this.remarks = remarks;
        this.isConfirmed = isConfirmed;
        this.confirmDate = confirmDate;
        this.driverId = driverId;
        this.vehicleNo = vehicleNo;
        this.isCancelled = isCancelled;
        this.cancelledTime = cancelledTime;
        this.cancelRemarks = cancelRemarks;
        this.isCompleted = isCompleted;
        this.completedTime = completedTime;
        this.payLoad = payLoad;
        this.cargoType = cargoType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.receiverMobileNo = receiverMobileNo;
    }

    public BookingInfo() {

    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(String requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public String getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getVehicleGroup() {
        return vehicleGroup;
    }

    public void setVehicleGroup(int vehicleGroup) {
        this.vehicleGroup = vehicleGroup;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
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

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getCancelledTime() {
        return cancelledTime;
    }

    public void setCancelledTime(String cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    public String getCancelRemarks() {
        return cancelRemarks;
    }

    public void setCancelRemarks(String cancelRemarks) {
        this.cancelRemarks = cancelRemarks;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(String completedTime) {
        this.completedTime = completedTime;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(double toLatitude) {
        this.toLatitude = toLatitude;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(double toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getReceiverMobileNo() {
        return receiverMobileNo;
    }

    public void setReceiverMobileNo(String receiverMobileNo) {
        this.receiverMobileNo = receiverMobileNo;
    }

    public void updateBookingInfo(String bookingNo, String bookingDate, String customerId, String requiredDate, String locationFrom, String locationTo, String cargoDescription, int vehicleType, int vehicleGroup, String remarks, boolean isConfirmed, String confirmDate, String driverId, String vehicleNo, boolean isCancelled, String cancelledTime, String cancelRemarks, boolean isCompleted, String completedTime, String payLoad, String cargoType, double latitude, double longitude, double toLatitude, double toLongitude, String receiverMobileNo) {
        this.bookingNo = bookingNo;
        this.bookingDate = bookingDate;
        this.customerId = customerId;
        this.requiredDate = requiredDate;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.cargoDescription = cargoDescription;
        this.vehicleType = vehicleType;
        this.vehicleGroup = vehicleGroup;
        this.remarks = remarks;
        this.isConfirmed = isConfirmed;
        this.confirmDate = confirmDate;
        this.driverId = driverId;
        this.vehicleNo = vehicleNo;
        this.isCancelled = isCancelled;
        this.cancelledTime = cancelledTime;
        this.cancelRemarks = cancelRemarks;
        this.isCompleted = isCompleted;
        this.completedTime = completedTime;
        this.payLoad = payLoad;
        this.cargoType = cargoType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.receiverMobileNo = receiverMobileNo;
    }

}
