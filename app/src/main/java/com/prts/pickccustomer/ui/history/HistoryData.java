package com.prts.pickccustomer.ui.history;

/**
 * Created by Uday on 28-08-2016.
 */
public class HistoryData {

    private String bookingNo;
    private String bookingDate;
    //private String mobileNO_CusID;
    private String requiredDate;

    public String getTripAmount() {
        return tripAmount;
    }

    public void setTripAmount(String tripAmount) {
        this.tripAmount = tripAmount;
    }

    private String tripAmount;

    private String fromLocation;
    private String toLocation;
    private String cargoDesc;

    private int vehicleType;
    private int vehicleGroup;
    private String remarks;

    private boolean isConfirmed;
    private String confirmDate;
    private String driverId;
    private String vehicleNo;

    private boolean isCanceled;
    private String canceledTime;
    private String cancelRemarks;

    private boolean isCompleted;
    private String completedTime;

    public String getAvgDriverRating() {
        return avgDriverRating;
    }

    public void setAvgDriverRating(String avgDriverRating) {
        this.avgDriverRating = avgDriverRating;
    }

    private String avgDriverRating;

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

    public String getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(String requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getCargoDesc() {
        return cargoDesc;
    }

    public void setCargoDesc(String cargoDesc) {
        this.cargoDesc = cargoDesc;
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

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getCanceledTime() {
        return canceledTime;
    }

    public void setCanceledTime(String canceledTime) {
        this.canceledTime = canceledTime;
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
}
