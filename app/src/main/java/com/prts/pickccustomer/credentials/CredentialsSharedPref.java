package com.prts.pickccustomer.credentials;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Uday on 15-08-2016.
 */
public class CredentialsSharedPref {

    private static final String CREDNTIALS_SHARED_PREF_NAME = "CRED_SHARE_PREF";
    private static final String AUTH_TOKEN_SHARED_PREF_KEY = "Auth_token_key";
    private static final String MOBILE_NO_SHARED_PREF_KEY = "Mobile_No_key";

    private static final String AUTH_TOKEN_HEADER_KEY = "AUTH_TOKEN";
    private static final String MOBILE_NO_HEADER_KEY = "MOBILENO";
    private static final String TAG = "CredentialsSharedPref";


    private static final String BOOKING_NO_SHARED_PREF_KEY = "booking_no";
    private static final String TRIP_ID_SHARED_PREF_KEY = "trip_id";

    private static final String NAME_SHARED_PREF_KEY = "name";
    private static final String MAIL_ID_SHARED_PREF_KEY = "email";


    private static final String DRIVER_ID_SHARED_PREF_KEY = "driver_id";
    private static final String TO_LAT_SHARED_PREF_KEY = "to_lat";
    private static final String TO_LONG_SHARED_PREF_KEY = "to_long";
    private static final String PASSWORD_SHARED_PREF_KEY = "password";

    public static String getAuthToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(AUTH_TOKEN_SHARED_PREF_KEY,null);
    }
    public static String getMobileNO(Context context){
        if (context == null){
            return null;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(MOBILE_NO_SHARED_PREF_KEY,null);
    }
    public static void setAuthToken(Context context, @NonNull String authToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_SHARED_PREF_KEY,authToken);
        editor.commit();
    }
    public static void setMobileNumber(Context context, @NonNull String mobileNo){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_NO_SHARED_PREF_KEY,mobileNo);
        editor.commit();
    }
    public static void clearAuthToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_SHARED_PREF_KEY,null);
        editor.commit();
    }
    public static void clearMobileNumber(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_NO_SHARED_PREF_KEY,null);
        editor.commit();
    }
    public static Map<String, String> getHeaders(Context context) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AUTH_TOKEN_HEADER_KEY, getAuthToken(context));
        params.put(MOBILE_NO_HEADER_KEY, getMobileNO(context));
        Log.e(TAG, "getHeaders: "+params);
        return params;
    }

    public static void setBookingNo(Context context, @NonNull String bookingNO){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BOOKING_NO_SHARED_PREF_KEY,bookingNO);
        editor.commit();
    }
    public static String getBookingNO(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(BOOKING_NO_SHARED_PREF_KEY,null);
    }

    public static void setTripID(Context context, @NonNull String tripID){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TRIP_ID_SHARED_PREF_KEY,tripID);
        editor.commit();
    }
    public static String getTripID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(TRIP_ID_SHARED_PREF_KEY,null);
    }

    public static void setName(Context context, @NonNull String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_SHARED_PREF_KEY,name);
        editor.commit();
    }
    public static String getName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME_SHARED_PREF_KEY,null);
    }

    public static void setEmailId(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MAIL_ID_SHARED_PREF_KEY,email);
        editor.commit();
    }
    public static String getEmailId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(MAIL_ID_SHARED_PREF_KEY,null);
    }
    public static void setDriverId(Context context, @NonNull String driverId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DRIVER_ID_SHARED_PREF_KEY,driverId);
        editor.commit();
    }
    public static String getDriverId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(DRIVER_ID_SHARED_PREF_KEY,null);
    }

    public static void setToLat(Context context, @NonNull double toLat){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TO_LAT_SHARED_PREF_KEY,toLat+"");
        editor.commit();
    }
    public static double getToLat(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        String toLat = sharedPreferences.getString(TO_LAT_SHARED_PREF_KEY,null);
        if (toLat!= null) {
            return Double.parseDouble(toLat);
        }else {
            return 0;
        }
    }

    public static void setToLong(Context context, @NonNull double toLong){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TO_LONG_SHARED_PREF_KEY,toLong+"");
        editor.commit();
    }
    public static double getToLong(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        String toLong = sharedPreferences.getString(TO_LONG_SHARED_PREF_KEY,null);
        if (toLong!= null) {
            return Double.parseDouble(toLong);
        }else {
            return 0;
        }
    }
    public static void setPassword(Context context, @NonNull String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD_SHARED_PREF_KEY,password);
        editor.commit();
    }
    public static String getPassword(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_SHARED_PREF_KEY,null);
    }


    public static void setSelectedVehicleGroupID(Context context, @NonNull int SelectedVehicleGroupID){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SelectedVehicleGroupID",SelectedVehicleGroupID);
        editor.commit();
    }
    public static int getSelectedVehicleGroupID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt("SelectedVehicleGroupID",0);
    }

    public static void setSelectedVehicleTypeID(Context context, @NonNull int SelectedVehicleTypeID){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SelectedVehicleTypeID",SelectedVehicleTypeID);
        editor.commit();
    }
    public static int getSelectedVehicleTypeID(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt("SelectedVehicleTypeID",0);
    }
    public static void setSelectedTruckWeightDesc(Context context, @NonNull String selectedTruckWeightDesc){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SelectedTruckWeightDesc",selectedTruckWeightDesc);
        editor.commit();
    }
    public static String getSelectedTruckWeightDesc(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString("SelectedTruckWeightDesc",null);
    }

    public static void setIsBookingLater(Context context, @NonNull boolean isBookingLater){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IsBookingLater",isBookingLater);
        editor.commit();
    }
    public static boolean getIsBookingLater(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("IsBookingLater",false);
    }
    public static void setLoadingUnloadingStatus(Context context, @NonNull int SelectedVehicleTypeID){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("LoadingUnloadingStatus",SelectedVehicleTypeID);
        editor.commit();
    }
    public static int getLoadingUnloadingStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt("LoadingUnloadingStatus",0);
    }
    public static void setCallBookNowAPI(Context context, @NonNull boolean callBookNowAPI){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("CallBookNowAPI",callBookNowAPI);
        editor.commit();
    }
    public static boolean getCallBookNowAPI(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("CallBookNowAPI",false);
    }
    public static void setFromoLat(Context context, @NonNull double fromLat){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FROMLAT",fromLat+"");
        editor.commit();
    }
    public static double getFromoLat(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        String fromlat = sharedPreferences.getString("FROMLAT",null);
        if (fromlat!= null) {
            return Double.parseDouble(fromlat);
        }else {
            return 0;
        }
    }

    public static void setFromLong(Context context, @NonNull double fromLong){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FROMLNG",fromLong+"");
        editor.commit();
    }
    public static double getFromLong(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME, context.MODE_PRIVATE);
        String fromlng = sharedPreferences.getString("FROMLNG", null);
        if (fromlng != null) {
            return Double.parseDouble(fromlng);
        } else {
            return 0;
        }
    }

    public static void setVehicleType(Context context, @NonNull int vehicleType){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("VEHCILETYPE",vehicleType);
        editor.commit();
    }
    public static int getVehicleType(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getInt("VEHCILETYPE",1300);
    }



    public static void setShowingLiveUpdateMarker(Context context, @NonNull boolean show){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d(TAG, "getShowingLiveUpdateMarker: "+ show);
        editor.putBoolean("ShowingLiveUpdateMarker",show);
        editor.commit();
    }
    public static boolean getShowingLiveUpdateMarker(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);

        return sharedPreferences.getBoolean("ShowingLiveUpdateMarker",false);
    }


}




