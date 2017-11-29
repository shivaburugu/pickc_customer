package com.prts.pickccustomer.constants;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Uday on 19-08-2016.
 */
public class RegularMethods {

    private static final String TAG = "RegularMethods";

    public static String getCurrentDateAndTime(){

        String dateAndTime = getCurrentDate()+" "+getCurrentTime();
        Log.d(TAG, "getCurrentDateAndTime: "+dateAndTime);
        return dateAndTime;
    }
    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = df.format(c.getTime());

        Log.d(TAG, "getCurrentDate: "+currentDate);
        return currentDate;
    }
    public static String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        // DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentTime = df.format(c.getTime());
        currentTime = currentTime.replaceAll(".","");
        Log.d(TAG, "getCurrentTime: "+currentTime);
        return currentTime;
    }

}
