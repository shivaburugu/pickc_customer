package com.prts.pickccustomer.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CheckBookingsService extends IntentService implements Constants {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CHECK_BOOKINGS_INFO = "com.uday.pickcdrivermodule.services.action.check.bookings.info";

    // TODO: Rename parameters
    private static final String TAG = "ChkBookingsService_TAG";
    public static final String BOOKINGS_INFO_AL_KEY = "bookings_info_al_key";

    Handler handler;
    public CheckBookingsService() {
        super("CheckBookingsService");
    }

    // TODO: Customize helper method
    public static void startActionCheckBookings(Context context) {
        Intent intent = new Intent(context, CheckBookingsService.class);
        intent.setAction(ACTION_CHECK_BOOKINGS_INFO);
        context.startService(intent);
    }
    public static void stopActonCheckBookings(Context context){
        Intent intent = new Intent(context, CheckBookingsService.class);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: getBookingsInfoFromServer intent123 "+intent);
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_CHECK_BOOKINGS_INFO)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    }
    private void getBookingsInfoFromServer() {
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS+UPDATE_BOOKING_INFO_API_CALL;
        GPSTracker gps = new GPSTracker (this);
        final double latitude = gps.getLatitude();
        final double longitude= gps.getLongitude();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(LATITUDE,latitude);
            jsonObject.put(LONGITUDE,longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse:12 getBookingsInfoFromServer "+response);
               // displayErrorMessage("Logged In Successfully. Please wait");
                for (int i = 0; i<response.length(); i++) {
                    try {
                        JSONObject bookingInfoJsonObject = response.getJSONObject(i);
                        Log.d(TAG, "onResponse: jsonObject "+bookingInfoJsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, error.networkResponse.statusCode+" error.networkResponse.statusCode onErrorResponse:78945 "+error);

                //TODO: handle failure
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(CheckBookingsService.this);
            }
        };


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }
}
