package com.prts.pickccustomer.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomTextVIew;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.services.IsConfirmBookingInfo;
import com.prts.pickccustomer.ui.AlertDialogActivity;
import com.prts.pickccustomer.ui.BlinkBookings;
import com.prts.pickccustomer.ui.HomeActivity;
import com.prts.pickccustomer.ui.RideEstimateActivity;
import com.prts.pickccustomer.ui.ZoomPicture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Uday on 18-08-2016.
 */
public class TruckCateogeoriesFragment extends Fragment implements Constants, View.OnClickListener {

    private static final float UN_SELECTED_FLOAT_VALUE = 0.3f;
    private static final float SELECTED_FLOAT_VALUE = 1.0f;
    PopupWindow popupWindow;
    private int truckIconFromDrawble = R.drawable.vehicle_open1;
    private int truckSelectedIconFromDrawble = R.drawable.vehicle_open1_selected;
    private int imageViewId = 101;
    private int textViewId = 1001;
    private int vehicleTypeTextViewId = 10001;
    public int etaTextViewId = 10101;

    private static final int LOADING_SELECTED = 1370;
    private static final int UN_LOADING_SELECTED = 1371;
    private static final int ALL_SELECTED = 1372;
    private static final int NONE_SELECTED = 1373;
    public static int loadingIVSelectedStatus = NONE_SELECTED;
    public int selectedvehiclTypeID = 1000 ;
    static int[] vehicleTypeIds = {VEHICLE_TYPE_OPEN, VEHICLE_TYPE_CLOSED};
    public static int selectedvehicleTypeID = vehicleTypeIds[0];

    ArrayList<Integer> vehicleGroupIdsAL = new ArrayList<>();
    ArrayList<String> vehicleGroupDescriptionsAL = new ArrayList<>();

    static  int[] vehicleGroupIds = {1000, 1001, 1002, 1003};
    String[] vehicleGroupDescriptions = {"Mini", "Small", "Medium", "Large"};
    public static  int selectedTruckID = vehicleGroupIds[0];
    ArrayList<Integer> vehicleTypeIdsAL = new ArrayList<>();
    ArrayList<String> vehicleTypeDescriptionsAL = new ArrayList<>();


    String[] vehicleTypeDescriptions = {"Open", "Closed"};

    public static Dialog rateCardDialog;
    private static final String TAG = "TruckFragment";
    Activity activity;
    GridLayout gridLayout;
    // LinearLayout rateCardLinearLayout;
    ImageView rateCardIV,displayImage,closingZoom;
    PickCCustomTextVIew baseFareTV, baseKM_TV, rideTimeFareTV, truckDescRateTV, finalDescRaCardTV;
    //PickCCustomTextVIew baseFareDescTV, baseKM_DescTV, rideTimeFareDescTV, estimateFareTV;
    LinearLayout topLL, confirmBookingRateCardLL;
    PickCCustomTextVIew rateCardTV, rideEstimateTV;

    private BlinkBookings blinkBookings;
    MapAsync mapAsync;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_truck_cateogeories, container, false);
        activity = getActivity();
        ((HomeActivity)activity).truckCateogeoriesFragment = TruckCateogeoriesFragment.this;
        topLL = (LinearLayout) view.findViewById(R.id.truck_categeories_top_linearLayout);
        confirmBookingRateCardLL = (LinearLayout) view.findViewById(R.id.confirm_booking_rateCardLayout);
        if (!HomeActivity.haveNetworkConnection(activity)) {
            Toast.makeText(activity, "Please connect to the internet to proceed", Toast.LENGTH_SHORT).show();
            activity.finish();
            return view;
        }
        initialize(view);
        downloadTruckCategories_VehicleGroups();
        downloadTruckTypes_VehicleTypes();
//        mapAsync = new MapAsync();
//        mapAsync.execute(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private void openRideEstimateActivity(Activity activity, LatLng fromLatLng, LatLng toLatLng, String fromAddress, String toAddress) {
        /*if (fromAddress.equals(DEFAULT_LOCATION) || toAddress.equals(DEFAULT_LOCATION)) {
            if (fromAddress.equals(DEFAULT_LOCATION) && toAddress.equals(DEFAULT_LOCATION)) {
                displayErrorMessage("Please select Source and destination");
                return;
            }
            if (fromAddress.equals(DEFAULT_LOCATION)) {
                displayErrorMessage("Please select Source location");
                return;
            }
            if (toAddress.equals(DEFAULT_LOCATION)) {
                displayErrorMessage("Please select destination location");
                return;
            }
            //
        }*/
        Intent intent = RideEstimateActivity.setDataAndCreateIntent(activity, fromLatLng, toLatLng,
                ((HomeActivity) activity).fromLocationTV.getText().toString(), ((HomeActivity) activity).toLocationTV.getText().toString());
        startActivity(intent);
    }

    ImageView loadingIV;
    TextView loadingTV, unloadingTV;

    private void initialize(View view) {
//         rateCardLinearLayout = (LinearLayout) view.findViewById(R.id.rate_card_linearLayout);
        rateCardTV = (PickCCustomTextVIew) view.findViewById(R.id.rateCardTV_confirmBookingLayout);
        rideEstimateTV = (PickCCustomTextVIew) view.findViewById(R.id.rideEstimateTV_confirmBookingLayout);
        rateCardTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCardDialog.show();
            }
        });
        rideEstimateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRideEstimateActivity(activity, ((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,
                        HomeActivity.currentAddress, HomeActivity.toAddress);
            }
        });
        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);
        /*LinearLayout.LayoutParams gridLayoutParam =
                (LinearLayout.LayoutParams) gridLayout.getLayoutParams();*/
        rateCardDialog = new Dialog(activity);
        rateCardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateCardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //rateCardDialog.setTitle("Rate Card");
        //rateCardDialog.setCanceledOnTouchOutside(false);
        rateCardDialog.setContentView(R.layout.final_rate_card_dialog_layout_new);

        loadingIV = (ImageView) view.findViewById(R.id.loadingImageView);
        loadingTV = (TextView) view.findViewById(R.id.loadingTV_2);
        unloadingTV = (TextView) view.findViewById(R.id.unlodingTV_2);
        loadingIV.setImageResource(getLoadingIconBasedOnSelection(loadingIVSelectedStatus, true));
        loadingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*modifyLoadingIvSelectedStatus();
                loadingIV.setImageResource(getLoadingIconBasedOnSelection());*/

                popupWindow = showLoadingAssistancePopUpWindow();
                popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 3) - (v.getHeight() / 2));
            }
        });
        rateCardIV = (ImageView) rateCardDialog.findViewById(R.id.rateCardImageView);
        displayImage = (ImageView) rateCardDialog.findViewById(R.id.close_rate_card_imageVIew);


        baseFareTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.min_base_fare_textView);
        //baseFareDescTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.min_base_fare_desc_textView);
        baseKM_TV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.per_km_fare_textView);
        //baseKM_DescTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.per_km_fare_desc_textView);
        rideTimeFareTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.ride_time_fare_textView);
        truckDescRateTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.truck_desc_rateCardDialog);
        finalDescRaCardTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.finalDescRateCardTV);
        //rideTimeFareDescTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.ride_time_fare_desc_textView);
        //estimateFareTV = (PickCCustomTextVIew) rateCardDialog.findViewById(R.id.estimate_fare_textView);
        final ImageView closeRateCardDialogIV = (ImageView) rateCardDialog.findViewById(R.id.close_rate_card_imageVIew);
        closeRateCardDialogIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCardDialog.dismiss();
            }
        });
        /*estimateFareTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openRideEstimateActivity(activity, ((HomeActivity) activity).fromLatLng, ((HomeActivity) activity).toLatLng,
                        HomeActivity.currentAddress, HomeActivity.toAddress);
                // Toast.makeText(activity, "Waiting for update", Toast.LENGTH_SHORT).show();
            }
        });*/
        setUpGridLayout();

    }

    /*private void modifyLoadingIvSelectedStatus() {
        switch (loadingIVSelectedStatus){
            case NONE_SELECTED:
                loadingIVSelectedStatus = LOADING_SELECTED;
                showSnackBar(loadingIV, "You are selected Loading");
                break;
            case LOADING_SELECTED:
                loadingIVSelectedStatus = UN_LOADING_SELECTED;
                showSnackBar(loadingIV, "You are selected Un-loading");
                break;
            case UN_LOADING_SELECTED:
                loadingIVSelectedStatus = ALL_SELECTED;f
                showSnackBar(loadingIV, "You are selected Loading & Un-loading");
                break;
            case ALL_SELECTED:
                loadingIVSelectedStatus = NONE_SELECTED;
                showSnackBar(loadingIV, "Un selected");
                break;
        }
        Log.d(TAG, "modifyLoadingIvSelectedStatus: loadingIVSelectedStatus "+loadingIVSelectedStatus);
    }*/

   /* private int getLoadingIconBasedOnSelection() {
        Log.d(TAG, selectedvehicleTypeID+" selectedvehicleTypeID getLoadingIconBasedOnSelection: loadingIVSelectedStatus "+loadingIVSelectedStatus);
        switch (loadingIVSelectedStatus) {
            case NONE_SELECTED:
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.none_selected_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.none_selected_closed;
                }
                break;
            case LOADING_SELECTED:
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.loading_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.loading_closed;
                }
                break;
            case UN_LOADING_SELECTED:
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.unloading_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.unloading_closed;
                }
                break;
            case ALL_SELECTED:
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.all_selected_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.all_selected_closed;
                }
                break;
        }
        return R.drawable.loading_open;
    }*/

    private int getLoadingIconBasedOnSelection(int loadingIVSelectedStatus, boolean canChangeText) {
        Log.d(TAG, selectedvehicleTypeID+" selectedvehicleTypeID getLoadingIconBasedOnSelection: loadingIVSelectedStatus "+loadingIVSelectedStatus);
        switch (loadingIVSelectedStatus) {
            case NONE_SELECTED:
//                ((HomeActivity)getActivity()).bookingFragment.speakOut("Loading/UnLoading option Not Selected");
                if (canChangeText) {
                loadingTV.setAlpha(UN_SELECTED_FLOAT_VALUE);
                unloadingTV.setAlpha(UN_SELECTED_FLOAT_VALUE);

                    /*loadingTV.setVisibility(View.INVISIBLE);
                    unloadingTV.setVisibility(View.INVISIBLE);*/
                    Log.d(TAG, "getLoadingIconBasedOnSelection: NONE_SELECTED selectedvehicleTypeID" + selectedvehicleTypeID);
                }
                    switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.none_selected_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.none_selected_closed;
                }
                break;
            case LOADING_SELECTED:
//                ((HomeActivity)getActivity()).bookingFragment.speakOut("Loading Selected");
                if (canChangeText) {
                loadingTV.setAlpha(SELECTED_FLOAT_VALUE);
                unloadingTV.setAlpha(UN_SELECTED_FLOAT_VALUE);
                    /*loadingTV.setVisibility(View.VISIBLE);
                    unloadingTV.setVisibility(View.INVISIBLE);*/
                    Log.d(TAG, "getLoadingIconBasedOnSelection: LOADING_SELECTED selectedvehicleTypeID" + selectedvehicleTypeID);
                }
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.loading_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.loading_closed;
                }
                break;
            case UN_LOADING_SELECTED:
//                ((HomeActivity)getActivity()).bookingFragment.speakOut("UnLoading Selected");
                if (canChangeText) {
                loadingTV.setAlpha(UN_SELECTED_FLOAT_VALUE);
                unloadingTV.setAlpha(SELECTED_FLOAT_VALUE);
                   /* loadingTV.setVisibility(View.INVISIBLE);
                    unloadingTV.setVisibility(View.VISIBLE);*/
                    Log.d(TAG, "getLoadingIconBasedOnSelection: UN_LOADING_SELECTED selectedvehicleTypeID" + selectedvehicleTypeID);
                }
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.unloading_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.unloading_closed;
                }
                break;
            case ALL_SELECTED:
//                ((HomeActivity)getActivity()).bookingFragment.speakOut("Loading and Unloading Selected");
                if (canChangeText) {
                loadingTV.setAlpha(SELECTED_FLOAT_VALUE);
                unloadingTV.setAlpha(SELECTED_FLOAT_VALUE);
                    /*loadingTV.setVisibility(View.VISIBLE);
                    unloadingTV.setVisibility(View.VISIBLE);*/
                    Log.d(TAG, "getLoadingIconBasedOnSelection: ALL_SELECTED selectedvehicleTypeID" + selectedvehicleTypeID);
                }
                switch (selectedvehicleTypeID) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.all_selected_open;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.all_selected_closed;
                }
                break;
        }
        return R.drawable.loading_open;
    }

    private int defaultLoadingIcon() {
        return R.drawable.loading_open;
    }

    private void displayErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private void setUpGridLayout() {

        vehicleGroupIdsAL.clear();
        vehicleGroupDescriptionsAL.clear();
        vehicleTypeTextViewsAL.clear();
        vehicleTypeImageViewsAL.clear();
        etaTextViewsAL.clear();
        if (vehicleGroupIds.length > 0) {
            gridLayout.removeAllViews();
        }
        for (int i = 0; i < vehicleGroupIds.length; i++) {
            vehicleGroupIdsAL.add(vehicleGroupIds[i]);
            vehicleGroupDescriptionsAL.add(vehicleGroupDescriptions[i]);
            createAndAddViewForGidLayout(gridLayout,
                    vehicleGroupIdsAL.get(i),
                    vehicleGroupDescriptionsAL.get(i));
        }
        for (int i = 0; i < vehicleTypeIds.length; i++) {
            vehicleTypeIdsAL.add(vehicleTypeIds[i]);
            vehicleTypeDescriptionsAL.add(vehicleTypeDescriptions[i]);
        }

        for (PickCCustomTextVIew textView : vehicleTypeTextViewsAL) {
            textView.setVisibility(View.GONE);
        }
        //vehicleTypeTextViewsAL.get(0).setVisibility(View.VISIBLE);
        Log.d(TAG, "setUpGridLayout: " + vehicleGroupIdsAL);
        Log.d(TAG, "setUpGridLayout: " + vehicleGroupDescriptionsAL);
        selectATruck(vehicleGroupIds[0] + imageViewId);

        downloadLoadingUnloadingList();
        for (int id : vehicleGroupIds){
            ((HomeActivity) activity).getNearByTrucksBasedOnSelection(selectedvehicleTypeID, id);
        }
    }

//    public int selectedTruckID = vehicleGroupIds[0];


    private void selectATruck(int imageViewId) {
        if (!(truckCategeoryIdsAL.size() > 0)) {
            Log.e(TAG, "setRateCatrdDataToTextViews: nodata");
            downloadRateCardForAllTrucks();
        }
        for (int i = 0; i < vehicleGroupIds.length; i++) {
            int id = imageViewId;
            RelativeLayout relativeLayout = (RelativeLayout) gridLayout.getChildAt(i);
            Log.d(TAG, gridLayout.getChildCount() + " 5151 selectATruck: " + relativeLayout);
            int availableId = (vehicleGroupIdsAL.get(i) + imageViewId);

            ImageView availableImageView = (ImageView) relativeLayout.getChildAt(0);

            Log.d(TAG, id + " selectATruck: " + availableImageView.getId());


            if (id == availableImageView.getId()  ) {
                //Todo truck selected
                selectedTruckID = relativeLayout.getId();
                availableImageView.setImageResource(getTruckSelectedIcon((id - this.imageViewId), selectedvehicleTypeID));
                Log.d(TAG, "selectATruck: selected Id " + selectedTruckID);
                /*availableTextView.setTextColor(getResources().getColor(R.color.appThemeTextColorDark));
                availableTextView.setBackgroundColor(getResources().getColor(R.color.appThemeBgColorLight));*/
                setRateCardDataToTextViews();
            } else {
                availableImageView.setImageResource(getTruckIcon(vehicleGroupIds[i]));
                /*availableTextView.setTextColor(getResources().getColor(R.color.appThemeTextColorLight));
                availableTextView.setBackgroundColor(getResources().getColor(R.color.appThemeBgColorDark));*/
            }
        }
        ((HomeActivity) activity).getNearByTrucksBasedOnSelection(selectedvehicleTypeID, selectedTruckID);
    }

    public static double baseFareAmt, rideTimeFareAmtPerMinute, perKmFareAmt;
    public static double baseKMs;
    public static double baseTimeInMinutes = 60;

    private void setRateCardDataToTextViews() {
        if (!(truckCategeoryIdsAL.size() > 0)) {
            Log.e(TAG, "setRateCatrdDataToTextViews: nodata");
            return;
        }
        int position = truckCategeoryIdsAL.indexOf(selectedTruckID + selectedvehicleTypeID);

        baseFareAmt = baseFaresAL.get(position);
        perKmFareAmt = distanceFaresAL.get(position);
        baseKMs = baseKMsAL.get(position);
        rideTimeFareAmtPerMinute = rideTimeFaresAL.get(position);

        String url = getTruckSelectedIconForRateCard(selectedTruckID, selectedvehicleTypeID);
        Picasso.with(activity).
                load(url).into(rateCardIV);

        rateCardIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity,ZoomPicture.class);
                intent.putExtra("selectedTruckID",
                        selectedTruckID);
                intent.putExtra("selectedvehicleTypeID",
                        selectedvehicleTypeID);

                startActivity(intent);
            }
        });
        rateCardValues(selectedTruckID,selectedvehicleTypeID);
//        baseFareTV.setText(rupee + " " + ((Double) baseFareAmt).intValue() +
//                "  for first " + ((Double) baseKMs).intValue() + " Km");
//        //baseFareDescTV.setText("min fare");
//        baseKM_TV.setText(rupee + " " + ((Double) perKmFareAmt).intValue() + " / Km" +
//                "  after " + ((Double) baseKMs).intValue() + " Km");
//        //baseKM_DescTV.setText("After " + ((Double) baseKMs).intValue() + " Km");
//        rideTimeFareTV.setText(rupee + " " + ((Double) rideTimeFareAmtPerMinute).intValue() + " /min");
//        +
//                "\nafter " + (int) baseTimeInMinutes + " mins of trip start");
        //rideTimeFareDescTV.setText("After " + (int) baseTimeInMinutes + " mins of trip start");
        /*Log.d(TAG, selectedTruckID +" selectedTruckID setRateCardDataToTextViews: selectedvehicleTypeID "+selectedvehicleTypeID);
        Log.d(TAG, vehicleGroupIdsAL+" vehicleGroupIdsAL setRateCardDataToTextViews: vehicleTypeIdsAL "+vehicleTypeIdsAL);*/

        String vehicleGroupDesc = vehicleGroupDescriptionsAL.get(vehicleGroupIdsAL.indexOf(selectedTruckID));
        String vehicleTypeDesc = vehicleTypeDescriptionsAL.get(vehicleTypeIdsAL.indexOf(selectedvehicleTypeID));

        Log.d(TAG, vehicleGroupDesc + " vehicleGroupDesc setRateCardDataToTextViews: vehicleTypeDesc " + vehicleTypeDesc);
        truckDescRateTV.setText("Truck Type : "+" "+vehicleGroupDesc + " - " + vehicleTypeDesc + " Truck");
        IsConfirmBookingInfo.vehicleDesc = vehicleGroupDesc + " - " + vehicleTypeDesc + " truck";
//        finalDescRaCardTV.setText("* Toll / Parking / Other charges shall be paid by the customer \n"
//              + "* Night time charges is applicable from 10PM to 5AM \n" +
////                BULLET + " Parking charges should be paid by the customer \n" +
////                BULLET + " wating for API update" +
//                " \n");
//        finalDescRaCardTV.setText(Html.fromHtml(getString(R.string.rateCardCautions)));

    }

    private void rateCardValues(int truckid, int selectedvehicle) {

        String url = WEB_API_ADDRESS+RATE_CARD_VALUES+"/"+selectedvehicle+"/"+truckid;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                clearAllRateCardALs();
                Log.d(TAG, "onResponse:downloadRateCardForAllTrucks " + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int cateogeoryIdAndTypeId = jsonObject.getInt(TRUCK_CATEGEORY_ID_JSON_KEY) +
                                jsonObject.getInt(TRUCK_VEHICLE_TYPE_ID_JSON_KEY);
                        double baseFare = jsonObject.getDouble(BASE_FARE_JSON_KEY);
                        double baseKM = jsonObject.getDouble(BASE_KM_JSON_KEY);
                        double distanceFare = jsonObject.getDouble(DISTANCE_FARE_JSON_KEY);
                        double waitingFare = jsonObject.getDouble(WAITING_FARE_JSON_KEY);
                        double rideTimeFare = jsonObject.getDouble(RIDE_TIME_FARE_JSON_KEY);
                        double cancellationFee = jsonObject.getDouble(CANCELLATION_FEE_JSON_KEY);


                        baseFareTV.setText(rupee + " " + ((Double) baseFare).intValue() +
                                "  for first " + ((Double) baseKM).intValue() + " Km");
                        //baseFareDescTV.setText("min fare");
                        baseKM_TV.setText(rupee + " " + ((Double) distanceFare).intValue() + " / Km" +
                                "  after " + ((Double) baseKMs).intValue() + " Km");
                        //baseKM_DescTV.setText("After " + ((Double) baseKMs).intValue() + " Km");
                        rideTimeFareTV.setText(rupee + " " + ((Double) rideTimeFare).intValue() + " /min");



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setRateCardDataToTextViews();

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    //Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
//                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
//                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
//                            new AlertDialogActivity.OnPositiveBtnClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//                                    downloadRateCardForAllTrucks();
//                                }
//                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
                    return;
                }
                //Toast.makeText(activity, "Unable to downloadRateCardForAllTrucks Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:downloadRateCardForAllTrucks " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };


        Volley.newRequestQueue(activity).add(jsonArrayRequest);
    }

    public ArrayList<PickCCustomTextVIew> etaTextViewsAL = new ArrayList<>();
    ArrayList<PickCCustomTextVIew> vehicleTypeTextViewsAL = new ArrayList<>();
    ArrayList<ImageView> vehicleTypeImageViewsAL = new ArrayList<>();


    private void createAndAddViewForGidLayout(GridLayout gridLayout, int id, String description) {

        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(getTruckIcon(id));
        imageView.setId(id + imageViewId);
        imageView.setPadding(5, 5, 5, 5);
        vehicleTypeImageViewsAL.add(imageView);

        PickCCustomTextVIew textView = new PickCCustomTextVIew(activity);
        textView.setText(description);
        textView.setId(id + textViewId);
        textView.setTextSize(10);
        textView.setTextColor(getResources().getColor(R.color.appThemeTextColorLight));
        textView.setPadding(2, 2, 2, 2);
        Log.d(TAG, "createAndAddViewForGidLayout: " + (id + textViewId));


        PickCCustomTextVIew etaTextView = new PickCCustomTextVIew(activity);
        etaTextView.setText(getTruckStatusTime(id,selectedvehicleTypeID));

        etaTextView.setId(id + etaTextViewId);
        etaTextView.setPadding(5, 5, 5, 5);
        etaTextView.setTextSize(10);
        etaTextView.setTextColor(getResources().getColor(R.color.appThemeTextColorLight));
        etaTextViewsAL.add(etaTextView);


        PickCCustomTextVIew vehicleTypeTextView = new PickCCustomTextVIew(activity);
        vehicleTypeTextView.setText("Open");
        vehicleTypeTextView.setId(id + vehicleTypeTextViewId);
        vehicleTypeTextView.setPadding(5, 5, 5, 5);
        vehicleTypeTextView.setTextSize(10);
        vehicleTypeTextView.setTextColor(getResources().getColor(R.color.appThemeTextColorLight));
        vehicleTypeTextViewsAL.add(vehicleTypeTextView);

        RelativeLayout relativeLayout = new RelativeLayout(activity);
        relativeLayout.addView(imageView);
        relativeLayout.addView(textView);
        relativeLayout.addView(vehicleTypeTextView);

        relativeLayout.addView(etaTextView);

        relativeLayout.setId(id);
        relativeLayout.setTag(id);

        relativeLayout.setOnClickListener(this);

        RelativeLayout.LayoutParams imageViewLayoutParam =
                (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        RelativeLayout.LayoutParams textViewLayoutParam =
                (RelativeLayout.LayoutParams) textView.getLayoutParams();
        RelativeLayout.LayoutParams vehicleTypeTextViewLayoutParam =
                (RelativeLayout.LayoutParams) vehicleTypeTextView.getLayoutParams();
        RelativeLayout.LayoutParams etaTextViewLayoutParam =
                (RelativeLayout.LayoutParams) etaTextView.getLayoutParams();



        textViewLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        textViewLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        vehicleTypeTextViewLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        vehicleTypeTextViewLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewLayoutParam.addRule(RelativeLayout.BELOW, id + textViewId);
        imageViewLayoutParam.addRule(RelativeLayout.ABOVE, id + etaTextViewId);
        etaTextViewLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        etaTextViewLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // imageViewLayoutParam.addRule(RelativeLayout.BELOW, id + vehicleTypeTextViewId);


        gridLayout.addView(relativeLayout);
        GridLayout.LayoutParams relativeLayoutParam =
                (GridLayout.LayoutParams) relativeLayout.getLayoutParams();
        relativeLayoutParam.width = (int) getResources().getDimension(R.dimen.grid_relative_layout_width);

        relativeLayoutParam.setGravity(GridLayout.LayoutParams.MATCH_PARENT);
        Log.d(TAG, gridLayout.getHeight() + " createAndAddViewForGidLayout: " + gridLayout.getWidth());
    }

    public int getTruckIcon(int vehicleGroupId) {
        switch (vehicleGroupId) {
            case 1000:
                return R.drawable.open_0_75_ton;
            case 1001:
                return R.drawable.open_1_ton;
            case 1002:
                return R.drawable.open_1_5_ton;

            case 1003:
                return R.drawable.open_2_ton;
        }
        Log.d(TAG, "getTruckIcon: default");
        return truckIconFromDrawble;
    }

    private String getTruckStatusTime(int vehicleGroupId, int vehicletypeId) {
        switch (vehicleGroupId) {
            case 1000:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return NO_TRUCKS;
                    case VEHICLE_TYPE_CLOSED:
                        return NO_TRUCKS;
                }
                return "No trucks";
            case 1001:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return NO_TRUCKS;
                    case VEHICLE_TYPE_CLOSED:
                        return NO_TRUCKS;
                }
                return "No trucks";
            case 1002:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return NO_TRUCKS;
                    case VEHICLE_TYPE_CLOSED:
                        return NO_TRUCKS;
                }
                return "No trucks";
            case 1003:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return NO_TRUCKS;
                    case VEHICLE_TYPE_CLOSED:
                        return NO_TRUCKS;
                }
                return NO_TRUCKS;
        }
        return "No trucks";
    }

    public int getTruckSelectedIcon(int vehicleGroupId, int vehicletypeId) {
        switch (vehicleGroupId) {
            case 1000:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.open_0_75_ton_selected;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.closed_0_75_ton_selected;
                }
                return R.drawable.open_0_75_ton_selected;
            case 1001:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.open_selected_1_ton;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.closed_selected_1_ton;
                }
                return R.drawable.open_selected_1_ton;
            case 1002:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.open_selected_1_5_ton;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.closed_selected_1_5_ton;
                }
                return R.drawable.open_selected_1_5_ton;
            case 1003:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return R.drawable.open_2_ton_selected;
                    case VEHICLE_TYPE_CLOSED:
                        return R.drawable.closed_2_ton_selected;
                }
                return R.drawable.open_2_ton_selected;
        }
        return truckSelectedIconFromDrawble;
    }
    public String getTruckSelectedIconForRateCard(int vehicleGroupId, int vehicletypeId) {
        switch (vehicleGroupId) {
            case 1000:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/64-Mini-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/64-Mini-closed.png";
                }
                return WEB_API_ADDRESS+"Images/64-Mini-open.png";
            case 1001:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/64-Small-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/64-Small-closed.png";
                }
                return WEB_API_ADDRESS+"Images/64-Small-open.png";
            case 1002:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/64-Small-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/64-Small-closed.png";
                }
                return WEB_API_ADDRESS+"Images/64-Small-open.png";
            case 1003:
                switch (vehicletypeId) {
                    case VEHICLE_TYPE_OPEN:
                        return WEB_API_ADDRESS+"Images/64-Large-open.png";
                    case VEHICLE_TYPE_CLOSED:
                        return WEB_API_ADDRESS+"Images/64-Large-closed.png";
                }
                return WEB_API_ADDRESS+"Images/64-Large-open.png";
        }
        return WEB_API_ADDRESS+"Images/64-Mini-open.png";
    }


    private void downloadTruckCategories_VehicleGroups() {

        String url = WEB_API_ADDRESS + VEHICLE_GROUP_LIST;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int[] vehicleGroupIds = new int[response.length()];
                String[] vehicleGroupDescriptions = new String[response.length()];
                Log.d(TAG, "onResponse: " + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int vehicleGroupId = jsonObject.getInt(VEHICLE_GROUP_LOOK_UP_ID_JSON_KEY);
                        String vehicleGroupDescription = jsonObject.getString(VEHICLE_GROUP_LOOK_UP_DESCRIPTION_JSON_KEY);
                        vehicleGroupIds[i] = vehicleGroupId;
                        vehicleGroupDescriptions[i] = vehicleGroupDescription;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                TruckCateogeoriesFragment.this.vehicleGroupIds = vehicleGroupIds;
                TruckCateogeoriesFragment.this.vehicleGroupDescriptions = vehicleGroupDescriptions;
                setUpGridLayout();

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:Home8465689 " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };

        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    private void downloadTruckTypes_VehicleTypes() {

        String url = WEB_API_ADDRESS + VEHICLE_TYPE_LIST;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int[] vehicleTypeIds = new int[response.length()];
                String[] vehicleTypeDescriptions = new String[response.length()];
                Log.d(TAG, "onResponse: VehicleTypes " + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        int vehicleTypeId = jsonObject.getInt(VEHICLE_TYPE_LOOK_UP_ID_JSON_KEY);
                        String vehicleTypeDescription = jsonObject.getString(VEHICLE_TYPE_LOOK_UP_DESCRIPTION_JSON_KEY);
                        vehicleTypeIds[i] = vehicleTypeId;
                        vehicleTypeDescriptions[i] = vehicleTypeDescription;
                        Log.d(TAG, vehicleTypeId + " onResponse: VehicleTypes 25 " + vehicleTypeDescription);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                TruckCateogeoriesFragment.this.vehicleTypeIds = vehicleTypeIds;
                TruckCateogeoriesFragment.this.vehicleTypeDescriptions = vehicleTypeDescriptions;

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:Home84655623 " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };


        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    ArrayList<Integer> truckCategeoryIdsAL = new ArrayList<>();
    ArrayList<Double> baseFaresAL = new ArrayList<>();
    ArrayList<Double> baseKMsAL = new ArrayList<>();
    ArrayList<Double> distanceFaresAL = new ArrayList<>();
    ArrayList<Double> waitingFaresAL = new ArrayList<>();
    ArrayList<Double> rideTimeFaresAL = new ArrayList<>();
    ArrayList<Double> cancellationFeeAL = new ArrayList<>();


    private void clearAllRateCardALs() {
        truckCategeoryIdsAL.clear();
        baseFaresAL.clear();
        baseKMsAL.clear();
        distanceFaresAL.clear();
        waitingFaresAL.clear();
        rideTimeFaresAL.clear();
        cancellationFeeAL.clear();
    }

    private void downloadRateCardForAllTrucks() {

        String url = WEB_API_ADDRESS + RATE_CARD_LIST;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                clearAllRateCardALs();
                Log.d(TAG, "onResponse:downloadRateCardForAllTrucks " + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int cateogeoryIdAndTypeId = jsonObject.getInt(TRUCK_CATEGEORY_ID_JSON_KEY) +
                                jsonObject.getInt(TRUCK_VEHICLE_TYPE_ID_JSON_KEY);
                        double baseFare = jsonObject.getDouble(BASE_FARE_JSON_KEY);
                        double baseKM = jsonObject.getDouble(BASE_KM_JSON_KEY);
                        double distanceFare = jsonObject.getDouble(DISTANCE_FARE_JSON_KEY);
                        double waitingFare = jsonObject.getDouble(WAITING_FARE_JSON_KEY);
                        double rideTimeFare = jsonObject.getDouble(RIDE_TIME_FARE_JSON_KEY);
                        double cancellationFee = jsonObject.getDouble(CANCELLATION_FEE_JSON_KEY);

                        truckCategeoryIdsAL.add(cateogeoryIdAndTypeId);
                        baseFaresAL.add(baseFare);
                        baseKMsAL.add(baseKM);
                        distanceFaresAL.add(distanceFare);
                        waitingFaresAL.add(waitingFare);
                        rideTimeFaresAL.add(rideTimeFare);
                        cancellationFeeAL.add(cancellationFee);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setRateCardDataToTextViews();

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    //Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    downloadRateCardForAllTrucks();
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
                //Toast.makeText(activity, "Unable to downloadRateCardForAllTrucks Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:downloadRateCardForAllTrucks " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };


        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onBookingConfirmed: " + v.getTag());

        //onGridLayoutItemClick(v.getId());
        onGridLayoutItemClickToShowPopUpWindow((Integer) v.getTag(), v);

    }

    private void onGridLayoutItemClickToShowPopUpWindow(int itemId, View v) {

        vehicleTypeIdsAL.clear();
        vehicleTypeDescriptionsAL.clear();
        if (vehicleTypeIds.length <= 0) {
            Toast.makeText(activity, "Retriving data\nPlease wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < vehicleTypeIds.length; i++) {

            vehicleTypeIdsAL.add(vehicleTypeIds[i]);
            vehicleTypeDescriptionsAL.add(vehicleTypeDescriptions[i]);
        }
        Log.d(TAG, "onGridLayoutItemClickToShowPopUpWindow: vehicleTypeIdsAL " + vehicleTypeIdsAL);
        Log.d(TAG, "onGridLayoutItemClickToShowPopUpWindow: vehicleTypeDescriptionsAL " + vehicleTypeDescriptionsAL);
        selectATruck(itemId+ imageViewId);

        popupWindow = vehicleTypesPopUpWindow(vehicleTypeDescriptionsAL, v);
        Log.d(TAG, itemId + " itemId onGridLayoutItemClickToShowPopUpWindow: vehicleGroupIds[(vehicleGroupIds.length-1)] " + vehicleGroupIds[(vehicleGroupIds.length - 1)]);
        /*if (itemId == vehicleGroupIds[(vehicleGroupIds.length - 1)]) {
            popupWindow.showAsDropDown(v, 0, 0);
        } else {
        }*/
        switch (selectedTruckID) {
            case 1000:

                //Old Code
//                popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 3));

                popupWindow.showAtLocation(v, Gravity.CENTER, Gravity.CENTER,150);
                //GOpinath
               // popupWindow.showAsDropDown(v, 0,3,2);
                break;
            case 1001:
                popupWindow.showAtLocation(v, Gravity.CENTER, Gravity.CENTER,150);

               // popupWindow.showAsDropDown(v, 0,0,0);

//                popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 3));
                break;
            case 1002:
                popupWindow.showAtLocation(v, Gravity.CENTER, Gravity.CENTER,150);

//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int height = displayMetrics.heightPixels;
//                int width = displayMetrics.widthPixels;
//
//                String widthString= String.valueOf(width);
//                Log.d("Width",""+width);
//
//                if (widthString.equals("1080")){
//                    popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 3));
//                }else{
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    popupWindow.showAsDropDown(v, 0, 0, Gravity.TOP);
//                } else {
//                    popupWindow.showAsDropDown(v, v.getWidth() - popupWindow.getWidth(), 0);
//                }
//                }


               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    popupWindow.showAsDropDown(v, 0, 0, Gravity.TOP);
                } else {
                    popupWindow.showAsDropDown(v, v.getWidth() - popupWindow.getWidth(), 0);
                }*/
                break;
            case 1003:
                popupWindow.showAtLocation(v, Gravity.CENTER, Gravity.CENTER,150);
//                DisplayMetrics displayMetrics1 = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics1);
//                int height1 = displayMetrics1.heightPixels;
//                int width1 = displayMetrics1.widthPixels;
//
//                String widthString1= String.valueOf(width1);
//                Log.d("Width",""+width1);
//
//                if (widthString1.equals("1080")){
//                    popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 5));
//
////                    popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 4));
//                }else{
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        popupWindow.showAsDropDown(v, 0, 0, Gravity.TOP);
//                    } else {
//                        popupWindow.showAsDropDown(v, v.getWidth() - popupWindow.getWidth(), 0);
//                    }
//                }
//
//                if (widthString1.equals("720")){
//
//                    popupWindow.showAsDropDown(v, 0, 0 ,0);
//                }
//                else {
//                    popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 4));
//                }

                break;

//                vehicleTypesListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
//                        R.layout.header_pop_up_list_item_very_large, null), null, false);


        }
//        popupWindow.showAsDropDown(v, 0, -(v.getHeight() * 2) - (v.getHeight() / 3));

    }

    boolean rateCardLinearLayoutIsShowing = false;
    int count = 0;

/*    private void onGridLayoutItemClick(int itemID) {
        if (selectedTruckID == itemID) {
            count++;
        }
        selectATruck(itemID + textViewId);
        count++;
        Log.d(TAG, "onGridLayoutItemClick: count " + count);
        if (count == 2) {
            if (!rateCardLinearLayoutIsShowing) {
                rateCardLinearLayout.setVisibility(View.VISIBLE);
            } else {
                rateCardLinearLayout.setVisibility(View.GONE);
            }
            rateCardLinearLayoutIsShowing = !rateCardLinearLayoutIsShowing;
        }
        if (count == 1) {
            if (rateCardLinearLayoutIsShowing) {
                rateCardLinearLayout.setVisibility(View.VISIBLE);
            } else {
                rateCardLinearLayout.setVisibility(View.GONE);
            }
        }
        count = 0;
    }*/


//    public int selectedvehicleTypeID = vehicleTypeIds[0];
    boolean popUpItemNotSelected = true;
    boolean loadingStatus[] = {false, false};
    private PopupWindow showLoadingAssistancePopUpWindow() {
        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(activity);
        // the drop down list is a list view
        final ListView assistanceListView = new ListView(activity);
        //ArrayAdapter vehicleTypesAdapter = new ArrayAdapter<String>(activity, R.layout.pop_up_list_item, vehicleTypesAL);
        final CustomPopUpListViewAdapterAssistance assistanceAdapter = new CustomPopUpListViewAdapterAssistance();
        // set our adapter and pass our pop up window contents
        assistanceListView.setAdapter(assistanceAdapter);

        assistanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                position -= listView.getHeaderViewsCount();
                loadingStatus[position] = !loadingStatus[position];
                assistanceAdapter.notifyDataSetChanged();

                if (loadingStatus[0] && loadingStatus[1]){
                    loadingIVSelectedStatus = ALL_SELECTED;
                    ((HomeActivity)getActivity()).bookingFragment.speakOut("Loading/UnLoading Selected");
                }if (!loadingStatus[0] && loadingStatus[1]){
                    loadingIVSelectedStatus = UN_LOADING_SELECTED;
                    ((HomeActivity)getActivity()).bookingFragment.speakOut("UnLoading Selected");
                }if (loadingStatus[0] && !loadingStatus[1]){
                    loadingIVSelectedStatus = LOADING_SELECTED;
                    ((HomeActivity)getActivity()).bookingFragment.speakOut("Loading Selected");
                }if (!loadingStatus[0] && !loadingStatus[1]){
                    loadingIVSelectedStatus = NONE_SELECTED;

                }

                loadingIV.setImageResource(getLoadingIconBasedOnSelection(loadingIVSelectedStatus, true));
            }
        });
        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(400);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appThemeBgColorDark)));

        // set the list view as pop up window content
        popupWindow.setContentView(assistanceListView);

        return popupWindow;

    }

    public PopupWindow vehicleTypesPopUpWindow(final ArrayList<String> vehicleTypesAL, final View view2) {
        popUpItemNotSelected = true;
        final int alPosition = vehicleGroupIdsAL.indexOf(selectedTruckID);
        for (PickCCustomTextVIew textView : vehicleTypeTextViewsAL) {
            textView.setVisibility(View.GONE);
        }
        for (ImageView imageView : vehicleTypeImageViewsAL) {
            imageView.setImageResource(getTruckIcon(imageView.getId() - this.imageViewId));
        }
        vehicleTypeTextViewsAL.get(alPosition).setText(vehicleTypesAL.get(0));
        //vehicleTypeTextViewsAL.get(alPosition).setVisibility(View.VISIBLE);
        vehicleTypeImageViewsAL.get(alPosition).setImageResource(getTruckSelectedIcon((vehicleTypeImageViewsAL.get(alPosition).getId()) - this.imageViewId, selectedvehicleTypeID));
        selectedvehicleTypeID = vehicleTypeIds[0];
        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(activity);
        // the drop down list is a list view
        final ListView vehicleTypesListView = new ListView(activity);
//          TextView tv = new TextView(getActivity());
//
//        tv.setTextColor(Color.BLACK);
//        ViewGroup viewGroup;
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_up_list_item,viewGroup,false;
//
//        TextView textView = (TextView) view.findViewById(R.id.text1_TV);
       switch (selectedTruckID) {
           case 1000:


               vehicleTypesListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                       R.layout.header_pop_up_list_item, null), null, false);
               break;
           case 1001:



               vehicleTypesListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                       R.layout.header_pop_up_list_item_medium, null), null, false);
               break;
           case 1002:

               vehicleTypesListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                       R.layout.header_pop_up_list_item_large, null), null, false);
               break;
           case 1003:

               vehicleTypesListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                       R.layout.header_pop_up_list_item_very_large, null), null, false);
               break;
       }

        //ArrayAdapter vehicleTypesAdapter = new ArrayAdapter<String>(activity, R.layout.pop_up_list_item, vehicleTypesAL);
        CustomPopUpListViewAdapter vehicleTypesAdapter = new CustomPopUpListViewAdapter(vehicleTypesAL);
        // set our adapter and pass our pop up window contents
        vehicleTypesListView.setAdapter(vehicleTypesAdapter);

        // set the item click listener
        vehicleTypesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= vehicleTypesListView.getHeaderViewsCount();
                popupWindow.dismiss();
                popUpItemNotSelected = false;
                // rateCardDialog.show();

                selectedvehicleTypeID = vehicleTypeIds[position];
                if (!(truckCategeoryIdsAL.size() > 0)) {
                    Log.e(TAG, "setRateCatrdDataToTextViews: nodata");
                    downloadRateCardForAllTrucks();
                }
                setRateCardDataToTextViews();

                String selectedVehicleGroupDes = vehicleGroupDescriptionsAL.get(alPosition);
                String selectedVehicleTypeDes = vehicleTypesAL.get(position);
                int selectedImage = getTruckSelectedIcon(selectedTruckID, selectedvehicleTypeID);
                if (position == 0) {
                    selectedImage = getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_OPEN);
                }
                else
                {
                    selectedImage = getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_CLOSED);
                }
                for (PickCCustomTextVIew textView : vehicleTypeTextViewsAL) {
                    textView.setVisibility(View.GONE);
                }
                vehicleTypeTextViewsAL.get(alPosition).setText(selectedVehicleTypeDes);
                //vehicleTypeTextViewsAL.get(alPosition).setVisibility(View.VISIBLE);

                for (ImageView imageView : vehicleTypeImageViewsAL) {
                    imageView.setImageResource(getTruckIcon(imageView.getId() - TruckCateogeoriesFragment.this.imageViewId));
                }
                vehicleTypeImageViewsAL.get(alPosition).setImageResource(selectedImage);
                showSnackBar(view2, selectedVehicleGroupDes + " " + vehicleTypesAL.get(position) + " truck selected");
                blinkBookings = ((HomeActivity) activity).blinkBookings;
                Log.d(TAG, ((HomeActivity) getActivity()).toLatLng +
                        " onItemClick: blinkBookings " + blinkBookings);
                /*if (((HomeActivity) getActivity()).toLatLng != null && blinkBookings != null) {
                    blinkBookings.blinkBookingLayout();
                }*/

                if (((HomeActivity) getActivity()).toLatLng != null){
                    ((HomeActivity)getActivity()).bookingFragment.speakOut("Now you can proceed for booking");
                }
                if (loadingIVSelectedStatus == NONE_SELECTED){
                    ((HomeActivity) getActivity()).bookingFragment.speakOut(selectedVehicleGroupDes + " " + vehicleTypesAL.get(position) + " truck is selected");
                }else {
                    ((HomeActivity) getActivity()).bookingFragment.speakOut(selectedVehicleGroupDes + " " + vehicleTypesAL.get(position) + " truck with " + getLoadingDes() + " is selected");
                }
                loadingIV.setImageResource(getLoadingIconBasedOnSelection(loadingIVSelectedStatus, true));
                ((HomeActivity) activity).getNearByTrucksBasedOnSelection(selectedvehicleTypeID, selectedTruckID);
            }
        });
        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(300);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.appThemeBgColorDark)));

        // set the list view as pop up window content
        popupWindow.setContentView(vehicleTypesListView);


      /*  popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (popUpItemNotSelected){
                            popupWindow.showAsDropDown(view2, 0, -550);
                            Toast.makeText(activity, "Please select type of truck", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onDismiss:34121 "+popUpItemNotSelected);
                            return;
                        }
                        popUpItemNotSelected = (!popUpItemNotSelected);
                        //Toast.makeText(activity, "popup dismissed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "popup onDismiss:3511 "+popUpItemNotSelected);
                    }
                },100);
            }
        });*/
        return popupWindow;
    }

    private String getLoadingDes() {
        String s = "";
        switch (loadingIVSelectedStatus){
            case LOADING_SELECTED:
                s = "loading";
                break;
            case UN_LOADING_SELECTED:

                s = "unloading";
                break;
            case ALL_SELECTED:

                s = "loading and unloading";
                break;
            case NONE_SELECTED:
                s = "";
                break;
            default:
                s= "";
                break;
        }
        return s;
    }

    private void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        TextView tv = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.appThemeYellow));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        snackView.setLayoutParams(params);
        snackbar.show();
    }

    public class CustomPopUpListViewAdapter extends BaseAdapter {


        ArrayList<String> vehicleTypesAL;
        int[] images = {getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_OPEN),
                getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_CLOSED)};

        CustomPopUpListViewAdapter(ArrayList<String> vehicleTypesAL) {
            this.vehicleTypesAL = vehicleTypesAL;
        }

        @Override
        public int getCount() {
            return vehicleTypesAL.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_up_list_item, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.text1_TV);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_Image);

            textView.setText(vehicleTypesAL.get(position));
             imageView.setImageResource(images[position]);



            return view;
        }
    }


    class CustomPopUpListViewAdapterAssistance extends BaseAdapter {
        int[] images = {getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_OPEN), getTruckSelectedIcon(selectedTruckID, VEHICLE_TYPE_CLOSED)};

        CustomPopUpListViewAdapterAssistance() {
        }

        @Override
        public int getCount() {
            return loadingStatus.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_up_list_item_assistane, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.text1_TV);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_Image);

            switch (position) {
                case 0:
                    textView.setText("Loading");
                    imageView.setImageResource(getLoadingIconBasedOnSelection(LOADING_SELECTED, false));
                    break;
                case 1:
                    textView.setText("Un loading");
                    imageView.setImageResource(getLoadingIconBasedOnSelection(UN_LOADING_SELECTED, false));
                    break;
            }
            if (loadingStatus[position]){
                textView.setBackgroundColor(getResources().getColor(R.color.appThemeYellow));
                textView.setTextColor(getResources().getColor(R.color.appThemeBgColorLight));
            }else {
                textView.setBackgroundColor(getResources().getColor(R.color.appThemeBgColorLight));
                textView.setTextColor(getResources().getColor(R.color.appThemeYellow));
            }
            return view;
        }
    }

    private void downloadLoadingUnloadingList() {

        String url = WEB_API_ADDRESS + LOADING_UNLOADING_LIST_API_CALL;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                clearAllRateCardALs();
                Log.d(TAG, "onResponse:downloadLoadingUnloadingList " + response);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if ((error + "").contains("TimeoutError")) {
                    //Toast.makeText(activity, "Server busy. Please try again after some time", Toast.LENGTH_SHORT).show();
                    AlertDialogActivity.showAlertDialogActivity(activity, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    downloadLoadingUnloadingList();
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
                //Toast.makeText(activity, "Unable to downloadRateCardForAllTrucks Please check your\n Internet Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onErrorResponse:downloadLoadingUnloadingList " + error);
                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(activity);
            }
        };


        Volley.newRequestQueue(activity).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }
    public void setETAToSelectedTextView(String duration, int selectedVehicleGroup){
        int tag = etaTextViewId + selectedVehicleGroup;
        for (TextView tv : etaTextViewsAL){
            if (tv.getId() == tag){

                tv.setText(duration);
                break;
            }/*else {
                tv.setText("No trucks");
            }*/
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        canCancel = true;
//        if (mapAsync != null && mapAsync.getStatus() == AsyncTask.Status.RUNNING){
//            mapAsync.cancel(true);
//            mapAsync = null;
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        canCancel = true;
        if (mapAsync != null && mapAsync.getStatus() == AsyncTask.Status.RUNNING){
            mapAsync.cancel(true);
            mapAsync = null;
        }
    }

    boolean canCancel;
    class MapAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {

            while (true){
                try {
                    if (canCancel){
                        break;
                    }
                    Thread.sleep(10 * 1000);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            canCancel = false;
        }

        @Override
        protected void onPostExecute(Object o) {
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            //Map UI update
//            getNearBy();

            ((HomeActivity)activity).getNearByTrucksBasedOnSelection(selectedvehicleTypeID, selectedTruckID);
        }

    }





}
