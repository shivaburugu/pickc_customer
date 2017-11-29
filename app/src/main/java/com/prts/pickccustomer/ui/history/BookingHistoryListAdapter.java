package com.prts.pickccustomer.ui.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.InvoiceActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.prts.pickccustomer.ui.InvoiceActivity.BOOKING_NO;

/**
 * Created by Uday on 28-08-2016.
 */
public class BookingHistoryListAdapter extends RecyclerView.Adapter<BookingHistoryListAdapter.MyViewHolder> implements Constants{
    private static final String TAG = "bookHisAct 6547";
    Activity activity;
    ArrayList<HistoryData> historyDataArrayList;
    String vehicleGroupText = "1 Ton";
    String vehicleTypeText = "Open";

    BookingHistoryListAdapter(Activity activity, ArrayList<HistoryData> historyDataArrayList){
        this.activity = activity;
        this.historyDataArrayList = historyDataArrayList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_booking_history_list_sub_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == (getItemCount()-1)){
//            holder.divider.setVisibility(View.GONE);
        }else {
           // holder.divider.setVisibility(View.VISIBLE);
        }

        final HistoryData bookingData = historyDataArrayList.get(position);
        Log.d(TAG, "onBindViewHolder: position "+position);

        // Todo date and Time
        holder.dateAndTimeTV.setText(bookingData.getBookingDate());
        Log.d(TAG, "onBindViewHolder: " + bookingData.getBookingDate());

        // Todo booking status
        if (bookingData.isCompleted()){
            Log.d(TAG, position+" onBindViewHolder: isCompleted "+bookingData.isCompleted());
            bindStatusToTV(STATUS_COMPLETED,holder.statusTV, holder.completedStatusIV);
        }else if (bookingData.isCanceled()){
            Log.d(TAG, position+" onBindViewHolder: isCanceled "+bookingData.isCanceled());
            bindStatusToTV(STATUS_CANCELLED,holder.statusTV, holder.completedStatusIV);
        }else if (bookingData.isConfirmed()){
            Log.d(TAG, position+" onBindViewHolder: isConfirmed "+bookingData.isConfirmed());
            bindStatusToTV(STATUS_CONFIRMED,holder.statusTV, holder.completedStatusIV);
        }else {
            bindStatusToTV(STATUS_NOT_SPECIFIED,holder.statusTV, holder.completedStatusIV);
            // Todo date and Time
            long currentTimeInmilliseconds = Calendar.getInstance().getTimeInMillis();
            long requiredBookingDateAndTimeInLiiseconds = getDateAndTimeInMilliSeconds(bookingData.getRequiredDate());
            if (requiredBookingDateAndTimeInLiiseconds <=  currentTimeInmilliseconds){
                holder.dateAndTimeTV.setText(bookingData.getBookingDate());
                holder.cancelBookingTV.setVisibility(View.GONE);
            }else {
                Log.d(TAG, position+" onBindViewHolder: isPending "+bookingData.getRequiredDate());
                bindStatusToTV(STATUS_PENDING,holder.statusTV, holder.completedStatusIV);
                holder.dateAndTimeTV.setText(bookingData.getRequiredDate());
                holder.cancelBookingTV.setVisibility(View.VISIBLE);

            }
        }

        holder.dateAndTimeTV.setText(changeDateAndTimeFormat(holder.dateAndTimeTV.getText().toString()));

        holder.ratingBar.setRating(Float.parseFloat(bookingData.getAvgDriverRating()));
        //Todo vehicle Description
        //holder.vehicleDesTV.setText(bookingData.getCargoDesc());

        //Todo bookingNo
        holder.bookingNoTV.setText(bookingData.getBookingNo());
        Context context =  holder.driverIV.getContext();
        String driverId = bookingData.getDriverId();
//        Picasso.with(context).load("http://ragsarma-001-site13.htempurl.com/DriverImages/"+driverId+".png").into(holder.driverIV);
        holder.tripAmountTV.setText(bookingData.getTripAmount());
        //Todo from location
        holder.fromLocationTV.setText(bookingData.getFromLocation());

        //Todo to location
        holder.toLocationTV.setText(bookingData.getToLocation());

        //Todo vehicleImageView //Todo vehicle Description
        bindVehicleImageToIVandCargoDescTV(holder.vehicleDesTV, bookingData.getVehicleGroup(), bookingData.getVehicleType(), holder.vehicleIV);

        holder.cancelBookingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelBooking(bookingData.getBookingNo());
                ((BookingHistoryActivity)activity).downloadBookingHistoryBasedOnMobileNo(CredentialsSharedPref.getMobileNO(activity));
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String completeUrl = "http://ragsarma-001-site6.htempurl.com/bill/mobile";
                Intent intent = new Intent(activity,WebViewActivity.class);
                intent.putExtra("url",completeUrl);
                activity.startActivity(intent);*/

                if (!bookingData.isCanceled()){
                    String bookingNO = bookingData.getBookingNo();
                    Intent intent = new Intent(activity,InvoiceActivity.class);
//                    intent.putExtra(WebViewActivityforInvoice.URL,"http://pickcargo.in/Help/tripinvoice");
                    intent.putExtra(BOOKING_NO,bookingNO);
                    intent.putExtra("SendInvoice", "no");
                    activity.startActivity(intent);
                    Log.d(TAG, "onClick: bookingData.isCompleted() getBookingNo "+bookingData.getBookingNo());
                }
                Log.d(TAG, "onClick: bookingData.isNotCompleted() getBookingNo "+bookingData.getBookingNo());
            }
        });
    }

    private void bindVehicleImageToIVandCargoDescTV(TextView vehicleDescTV, int vehicleGroup, int vehicleType, ImageView vehicleImageView){
        vehicleImageView.setImageResource(getTruckSelectedIcon(vehicleGroup, vehicleType));
        vehicleDescTV.setText(vehicleGroupText + " " + vehicleTypeText+" Truck");

    }

    public int getTruckSelectedIcon(int vehicleGroupId, int vehicleTypeId) {
        switch (vehicleGroupId) {
            case 1000:
                vehicleGroupText = "700kgs - Mini -";
                switch (vehicleTypeId) {
                    case VEHICLE_TYPE_OPEN:
                        vehicleTypeText = "Open";
                        return R.drawable.open_0_75_ton_selected;
                    case VEHICLE_TYPE_CLOSED:
                        vehicleTypeText = "Closed";
                        return R.drawable.closed_0_75_ton_selected;
                }
                return R.drawable.open_0_75_ton_selected;
            case 1001:
                vehicleGroupText = "1030kgs - Small -";
                switch (vehicleTypeId) {
                    case VEHICLE_TYPE_OPEN:
                        vehicleTypeText = "Open";
                        return R.drawable.open_selected_1_ton;
                    case VEHICLE_TYPE_CLOSED:
                        vehicleTypeText = "Closed";
                        return R.drawable.closed_selected_1_ton;
                }
                return R.drawable.open_selected_1_ton;
            case 1002:
                vehicleGroupText = "1250kgs - Medium -";
                switch (vehicleTypeId) {
                    case VEHICLE_TYPE_OPEN:
                        vehicleTypeText = "Open";
                        return R.drawable.open_selected_1_5_ton;
                    case VEHICLE_TYPE_CLOSED:
                        vehicleTypeText = "Closed";
                        return R.drawable.closed_selected_1_5_ton;
                }
                return R.drawable.open_selected_1_5_ton;
            case 1003:
                vehicleGroupText = "2700kgs - Large -";
                switch (vehicleTypeId) {
                    case VEHICLE_TYPE_OPEN:
                        vehicleTypeText = "Open";
                        return R.drawable.open_2_ton_selected;
                    case VEHICLE_TYPE_CLOSED:
                        vehicleTypeText = "Closed";
                        return R.drawable.closed_2_ton_selected;
                }
                return R.drawable.open_2_ton_selected;
        }
        return R.drawable.open_selected_1_ton;
    }
    private String changeDateAndTimeFormat(String dateAndTime){

        dateAndTime = dateAndTime.replace("T"," ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm a yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("      EEE, MMM dd, yyyy HH:mm a");
        try {
            Date mDate = sdf.parse(dateAndTime);
            dateAndTime = sdf1.format(mDate);
            System.out.println(dateAndTime+" Date in milli :: ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateAndTime;
    }
    private long getDateAndTimeInMilliSeconds(String dateAndTime){
        dateAndTime = dateAndTime.replace("T"," ");
        long timeInMilliseconds = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse(dateAndTime);
            timeInMilliseconds = mDate.getTime();
            System.out.println(dateAndTime+" Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
    private void bindStatusToTV(String status, TextView statusTextView, ImageView completedStatusIV){
        Log.d(TAG, "bindStatusToTV: "+status);
        statusTextView.setVisibility(View.GONE);
        completedStatusIV.setImageResource(R.drawable.paid_stamp);
        switch (status){
            case STATUS_COMPLETED:
                statusTextView.setText(STATUS_COMPLETED);
                statusTextView.setTextColor(activity.getResources().getColor(R.color.colorCompletedStatus));
                break;
            case STATUS_CANCELLED:
                statusTextView.setText(STATUS_CANCELLED);
                completedStatusIV.setImageResource(R.drawable.cancelled_stamp);
                statusTextView.setTextColor(activity.getResources().getColor(R.color.colorCancelledStatus));
                break;
            case STATUS_CONFIRMED:
                statusTextView.setText(STATUS_CONFIRMED);
                statusTextView.setTextColor(activity.getResources().getColor(R.color.colorConfirmedStatus));
                break;
            case STATUS_PENDING:
                statusTextView.setText(STATUS_PENDING);
                statusTextView.setTextColor(activity.getResources().getColor(R.color.colorPendingStatus));
                break;
            default:
                statusTextView.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return historyDataArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        View divider;
        ImageView vehicleIV, driverIV, completedStatusIV;
        RatingBar ratingBar;
        TextView statusTV, dateAndTimeTV, vehicleDesTV, bookingNoTV,fromLocationTV,toLocationTV, fareTV,tripAmountTV;
        TextView cancelBookingTV;
        public MyViewHolder(View itemView) {
            super(itemView);
            divider = (View)itemView.findViewById(R.id.view_line_divider);

            vehicleIV = (ImageView)itemView.findViewById(R.id.vehicle_image_IV);
            driverIV = (ImageView)itemView.findViewById(R.id.driverIconIV);


            completedStatusIV = (ImageView)itemView.findViewById(R.id.completedStatusIV);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar2);

            statusTV = (TextView)itemView.findViewById(R.id.booking_status_TV);
            dateAndTimeTV = (TextView)itemView.findViewById(R.id.booking_date_time_TV);
            vehicleDesTV = (TextView)itemView.findViewById(R.id.vehicle_group_type_TV);
            bookingNoTV = (TextView)itemView.findViewById(R.id.booking_no_TV);
            fromLocationTV = (TextView)itemView.findViewById(R.id.from_location_address_TV);
            toLocationTV = (TextView)itemView.findViewById(R.id.to_location_address_TV);
            fareTV = (TextView)itemView.findViewById(R.id.total_fare_TV);
            cancelBookingTV = (TextView)itemView.findViewById(R.id.cancel_booking_TV);
            tripAmountTV = (TextView)itemView.findViewById(R.id.tripAmountTV);

        }
    }
    /*private void cancelBooking(String bookingNo) {
        String url = WEB_API_ADDRESS+BOOKING_CANCEL_API_CALL+bookingNo;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: 7896 cancelBooking "+response);
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //displayErrorMessage("Please check your internet connection");
                Log.d(TAG, "onErrorResponse: cancelBooking 7896 "+error);
                //TODO: handle failure
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };
        Volley.newRequestQueue(activity).add(jsonArrayRequest);
        // Show response on activity
        //resultTV.setText( text  );

    }*/
}
