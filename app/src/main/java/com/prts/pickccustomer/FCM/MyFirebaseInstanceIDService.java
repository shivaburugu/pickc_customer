package com.prts.pickccustomer.FCM;

/**
 * Created by Uday on 10-11-2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements Constants {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String DEVICE_ID_SHARED_PREF_KEY = "device_id";
    private static final String CREDNTIALS_SHARED_PREF_NAME = "shared_pref";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        setDeviceId(getApplicationContext(),refreshedToken);
        postDeviceIdToServer(getApplicationContext(), CredentialsSharedPref.getMobileNO(getApplicationContext()),refreshedToken);
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }


    public static void setDeviceId(Context context, @NonNull String deviceId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID_SHARED_PREF_KEY,deviceId);
        editor.commit();
    }

    public static String getdeviceId(Context context){
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(CREDNTIALS_SHARED_PREF_NAME,context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString(DEVICE_ID_SHARED_PREF_KEY, null);
        Log.d(TAG, "getdeviceId: deviceId "+deviceId);
        return deviceId;
    }


    public static void postDeviceIdToServer(final Context context, @NonNull String mobileNo, @NonNull String deviceId){

        String url = WEB_API_ADDRESS+DEVICE_ID_API_CALL;
        JSONObject obj = new JSONObject();
        try {
            obj.put(MOBILE_NO_DEVICE_ID_JSON_KEY, mobileNo);
            obj.put(DEVICE_ID_DEVICE_ID_JSON_KEY, deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "postDeviceIdToServer: "+obj);
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.d(TAG, "onResponse: postDeviceIdToServer "+response);
                        //goto login Activity

                        //hideProgressDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d(TAG, "onErrorResponse:12345 postDeviceIdToServer "+error);
                        if ((error+"").contains("SAVED SUCSSFULLY")){

                        }else {
                            displayErrorMessage("Please check your internet connection");
                        }

                        if ((error+"").contains("TimeoutError")){

                            displayErrorMessage("Server busy. Please try again after some time");
                            return;
                        }
                        //hideProgressDialog();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(context);
            }
        };
        queue.add(jsObjRequest);
    }

    private static void displayErrorMessage(String message) {
        Log.d(TAG, "displayErrorMessage: "+message);
    }
}