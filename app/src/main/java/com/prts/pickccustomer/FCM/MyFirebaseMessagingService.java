package com.prts.pickccustomer.FCM;

/**
 * Created by Uday on 10-11-2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.prts.pickccustomer.Payment.Payment;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.ui.AlertDialogActivity;
import com.prts.pickccustomer.ui.HomeActivity;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static final String BOOKING_CONFIRMED = "Booking Confirmed";
    public static final String BOOKING_FAILED = "Booking Failed";
    public static final String BOOKING_CANCELLED_BY_DRIVER = "Booking Cancelled by driver";
    public static final String TRIP_STARTED = "Trip Started";
    public static final String TRIP_END = "Trip End";
    public static final String DRIVER_REACHED_PICK_UP_LOCATION = "Driver reached pickup location";
    public static final String DRIVER_REACHED_DROP_LOCATION = "Driver reached destination location";
    public static final String INVOICE_GENERATED = "Invoice Generated";
    private static final String BODY_KEY = "body";
    private static final String BOOKING_NO_KEY = "bookingNo";
    private static final String GENERATE_INVOICE = "DriverpaymentReceived";
    private static final String ABOUTTOREACHPICKUPLOCATION= "Driver is Started to reach pickup location";

    Handler handler;

    public static final String NOT_CANCELLED = "not cancelled";
    public static String isCancelled = NOT_CANCELLED;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        try {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "onMessageReceived: remoteMessage " + remoteMessage);
            Log.d(TAG, "onMessageReceived: remoteMessage.getData() " + remoteMessage.getData());
        } catch (Exception e) {

            Log.d(TAG, "onMessageReceived: remoteMessage " + remoteMessage);
            Log.d(TAG, "onMessageReceived: remoteMessage.getData() " + remoteMessage.getData());
            e.printStackTrace();
        }
        //Calling method to generate notification
        // sendNotification(remoteMessage.getNotification().getBody());
        final String bodyMessage = remoteMessage.getData().get(BODY_KEY);
        final String bookingNo = remoteMessage.getData().get(BOOKING_NO_KEY);
        Log.d(TAG, bookingNo + " bookingNo onMessageReceived: bodyMessage " + bodyMessage);
        handler.post(new Runnable() {
            @Override
            public void run() {
                verifyBodyOftheMessage(bodyMessage, bookingNo);

            }
        });
    }

    private void verifyBodyOftheMessage(String bodyOfFCM, final String bookingNo) {
        Log.d(TAG, "verifyBodyOftheMessage: bodyOfFCM " + bodyOfFCM);
        switch (bodyOfFCM) {
            case BOOKING_CONFIRMED:
                if (onBookingConfirmedListener != null) {
                    onBookingConfirmedListener.onBookingConfirmed(bodyOfFCM, bookingNo);
                }
                break;
            case BOOKING_FAILED:

                break;
            case BOOKING_CANCELLED_BY_DRIVER:
                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "CANCELLED",
                        "Your booking with "+bookingNo+" is Cancelled by driver. Sorry for the inconvenience. Please try again", "OK", null,
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                isCancelled = BOOKING_CANCELLED_BY_DRIVER;
                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
                            }
                        }, null);
                break;
            case TRIP_STARTED:

                if (onTripStartedListener != null) {
                    onTripStartedListener.onTripStart(bodyOfFCM, bookingNo);
                }
                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Trip Started",
                        bookingNo+"Trip started and the vehicle is on the way to delivery location.", "OK", null,
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
                            }
                        }, null);
                break;
            case TRIP_END:

                if (onTripEndedListener != null) {
                    onTripEndedListener.onTripEnd(bodyOfFCM, bookingNo);
                }

                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Trip Completed",
                        "Trip completed successfully. Please proceed for payment options.", "OK", null,
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                if (onTripEndedListener != null) {
                                    onTripEndedListener.onTripEndOk(bookingNo);
                                }
//                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
                               new Intent(MyFirebaseMessagingService.this, Payment.class);
                            }
                        }, null);

                break;
            case DRIVER_REACHED_PICK_UP_LOCATION:
                if (onDriverReachedPickUpLocationListener != null) {
                    onDriverReachedPickUpLocationListener.onDriverReachedPickUpLocation(bodyOfFCM, bookingNo);
                }
                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Pick up location",
                        "Driver has reached pick up location", "OK", null,
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
                            }
                        }, null);
                break;
            case DRIVER_REACHED_DROP_LOCATION:
                if (onDriverReachedDropLocationListener != null) {
                    onDriverReachedDropLocationListener.onDriverReachedDropLocation(bodyOfFCM, bookingNo);
                }
                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Delivery location",
                        "Driver reached delivery location", "OK", null,
                        new AlertDialogActivity.OnPositiveBtnClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
                            }
                        }, null);
                break;
            case INVOICE_GENERATED:

                break;
            case ABOUTTOREACHPICKUPLOCATION:

                if (onDriverAbouttotReachPickUPLocationListener != null) {
                    onDriverAbouttotReachPickUPLocationListener.onDriverAbouttotReachPickUPLocationListener(bodyOfFCM, bookingNo);
                }
                break;

            case GENERATE_INVOICE:
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//                alertDialogBuilder.setMessage("Rate the Driver");
//                        alertDialogBuilder.setPositiveButton("yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface arg0, int arg1) {
//                                        new Intent(MyFirebaseMessagingService.this, UserRatingBarActivity.class);
//
//                                    }
//                                });
//                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Driver Received Payment",
//                        "Please Rate the Driver.", "OK", null,
//                        new AlertDialogActivity.OnPositiveBtnClickListener() {
//                            @Override
//                            public void onPositiveBtnClick() {
//                                new Intent(MyFirebaseMessagingService.this, UserRatingBarActivity.class);
//                            }
//                        }, null);
//                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Driver Received Payment",
//                        "Please Rate the Driver.", "OK", null,
//                        new AlertDialogActivity.OnPositiveBtnClickListener() {
//                            @Override
//                            public void onPositiveBtnClick() {
//
//                                new Intent(MyFirebaseMessagingService.this, UserRatingBarActivity.class);
//
////
//
//                            }
//                        }, null);


//                AlertDialogActivity.showAlertDialogActivity(MyFirebaseMessagingService.this, "Payment Done Successfully",
//                        "Trip completed successfully with " + bookingNo +". Please proceed for payment options.", "OK", null,
//                        new AlertDialogActivity.OnPositiveBtnClickListener() {
//                            @Override
//                            public void onPositiveBtnClick() {
//                                if (onTripEndedListener != null) {
//                                    onTripEndedListener.onTripEndOk(bookingNo);
//                                }
////                                new Intent(MyFirebaseMessagingService.this, HomeActivity.class);
//                                new Intent(MyFirebaseMessagingService.this, Payment.class);
//                            }
//                        }, null);
                Toast.makeText(this, "Payment Done Successfully", Toast.LENGTH_LONG).show();
                break;
            default:

                break;
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }



    // todo on about to reach pickuplocation by driver
    static OnDriverAbouttotReachPickUPLocationListener onDriverAbouttotReachPickUPLocationListener;

    public static void setOnDriverAbouttotReachPickUPLocationListener(OnDriverAbouttotReachPickUPLocationListener onDriverAbouttotReachPickUPLocationListener) {
        MyFirebaseMessagingService.onDriverAbouttotReachPickUPLocationListener = onDriverAbouttotReachPickUPLocationListener;
//        throw new RuntimeException("Stub!");
    }

    public interface OnDriverAbouttotReachPickUPLocationListener {
        void onDriverAbouttotReachPickUPLocationListener(String bodyOfFCM, String bookingNo);
    }


    // todo on booking confirmed
    static OnBookingConfirmedListener onBookingConfirmedListener;
    public static void setOnBookingConfirmedListener(OnBookingConfirmedListener onBookingConfirmedListener) {
        MyFirebaseMessagingService.onBookingConfirmedListener = onBookingConfirmedListener;
//        throw new RuntimeException("Stub!");
    }
    public interface OnBookingConfirmedListener {
        void onBookingConfirmed(String bodyOfFCM, String bookingNo);
    }




    // todo on trip started
    static OnTripStartedListener onTripStartedListener;
    public static void setOnTripStartedListener(OnTripStartedListener onTripStartedListener) {
        MyFirebaseMessagingService.onTripStartedListener = onTripStartedListener;
//        throw new RuntimeException("Stub!");
    }
    public interface OnTripStartedListener {
        void onTripStart(String bodyOfFCM, String bookingNo);
    }
    // todo on trip ended
    static OnTripEndedListener onTripEndedListener;
    public static void setOnTripEndedListener(OnTripEndedListener onTripEndedListener) {
        MyFirebaseMessagingService.onTripEndedListener = onTripEndedListener;
//        throw new RuntimeException("Stub!");
    }
    public interface OnTripEndedListener {
        void onTripEnd(String bodyOfFCM, String bookingNo);
        void onTripEndOk(String bookingNo);
    }
    // todo on driver reached pick up location
    static OnDriverReachedPickUpLocationListener onDriverReachedPickUpLocationListener;
    public static void setOnDriverReachedPickUpLocationListener(OnDriverReachedPickUpLocationListener onDriverReachedPickUpLocationListener) {
        MyFirebaseMessagingService.onDriverReachedPickUpLocationListener = onDriverReachedPickUpLocationListener;
//        throw new RuntimeException("Stub!");
    }
    public interface OnDriverReachedPickUpLocationListener {
        void onDriverReachedPickUpLocation(String bodyOfFCM, String bookingNo);
    }
    // todo on driver reached drop location
    static OnDriverReachedDropLocationListener onDriverReachedDropLocationListener;
    public static void setOnDriverReachedDropLocationListener(OnDriverReachedDropLocationListener onDriverReachedDropLocationListener) {
        MyFirebaseMessagingService.onDriverReachedDropLocationListener = onDriverReachedDropLocationListener;
//        throw new RuntimeException("Stub!");
    }
    public interface OnDriverReachedDropLocationListener {
        void onDriverReachedDropLocation(String bodyOfFCM, String bookingNo);
    }
}