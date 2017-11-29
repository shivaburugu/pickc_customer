package com.prts.pickccustomer.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.prts.pickccustomer.Payment.SendingQueriesToPickC;
import com.prts.pickccustomer.Payment.UserRatingBarActivity;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.constants.PickCCustomEditText;
import com.prts.pickccustomer.constants.PickcCircleImageView;
import com.prts.pickccustomer.constants.ProgressDialog;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.map.GMapV2Direction;
import com.prts.pickccustomer.map.GMapV2DirectionAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class InvoiceActivity extends AppCompatActivity implements Constants {

    private static final String TAG = "InvoiceActivity";
    public static final String BOOKING_NO = "bookingNo";
    public static final String DRIVERID = "driver";
    boolean disableOnBackPressed = false;

    String[] faresDesc = {"Base fare", "Distance fare", "Ride time fare"};
    String[] fares = {"51", "40.8", "21"};
    String[] taxesDesc = {"Service tax", "Swatch bharath cess", "Krishi kalyan cess"};
    String[] taxes = {"9.08", "0.32", "0.32"};
    TextView driverNameTV, travelTimeFareForMinTV, waitingNightTimeChargesTV, otherChargesTotalTV,totalBillAmountTV;
    TextView distanceFareforTenKmsTV, loadingAndUnloadingChargesTV, totalFareTV,swachBharathTV,krishiKalyanCessTV;
    TextView baseFareFor4KmTV,driverIdWithName, totalBilledAmountTV,crnNumberTV,otherChargesTV,serviceTaxTV,totalTripKilometersTV,totalTripTimeTV;
    TextView sourceTimeTV,vehicleTypeTV, sourceAddressTV, destinationTimeTV, destinationAddressTV,dteOfInvoiceTV,customerNameTV;
    TextView baseFareInKms,distanceForKms,textView3,driverIdWithoutName,vehicleCategoryTV,loadingUnloadTV;
    ImageView driverProfileIV, truckIV,iv_map;

    String driverID;
    Button rateDriverBut,contactUsBut,emailInvoiceBut;
     String bookingNo;
    String sendEmailForCustomer;
    String emailID;
    Double fromLatitudes,fromLongitudes,toLatitudes,toLongitudes;

    LatLng sourceLatLng, destinationLatLng;
    ListView fareListView, taxListView;
    ImageView imagestar;
    PickcCircleImageView imageviewthank,imageViewthankyou;
    GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_activity_invoice);

       getSupportActionBar().setDisplayHomeAsUpEnabled(false);


//        getSupportActionBar().setTitle("Sat, Dec 17,08:30 pm");


        intialize();
        Intent intent = getIntent();
         bookingNo = intent.getStringExtra(BOOKING_NO);

        sendEmailForCustomer = intent.getStringExtra("SendInvoice");

        Log.d(TAG, "onCreate: "+sendEmailForCustomer);



        rateDriverBut = (Button)findViewById(R.id.rateDriverBut);
        contactUsBut =(Button)findViewById(R.id.contactUsBut);
        emailInvoiceBut = (Button)findViewById(R.id.emailInvoiceBut);
        imageviewthank = (PickcCircleImageView)findViewById(R.id.thankyou_imageview);
        imageViewthankyou = (PickcCircleImageView)findViewById(R.id.thank_imageview);
        imagestar = (ImageView)findViewById(R.id.imagestar);

        emailInvoiceBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber =CredentialsSharedPref.getMobileNO(getApplicationContext());
                getUserInfoFromServer(mobileNumber);
//                emailInvoice();
            }
        });
        contactUsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SendingQueriesToPickC.class));
            }
        });

        rateDriverBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.d(TAG, "onClick: From Invouice to Driver Rating Activity"+ bookingNo);
                Intent driverRating = new Intent(getApplicationContext(),UserRatingBarActivity.class);
                driverRating.putExtra(DRIVERID,driverID);

                startActivity(driverRating);

            }
        });

        downloadInvoice(bookingNo);

//        drawMarker(sourceLatLng,destinationLatLng);
    }



    public void emailInvoice(String emailID){
//       String url = "http://192.168.0.108/PickCApi/api/master/customer/SendInvoiceMail/"+bookingNo+"/"+emailID+"/";
//        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
//        progressDialog.setTitle("Sending Email...");
//        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        disableOnBackPressed = true;

     String url = WEB_API_ADDRESS+"api/master/customer/SendInvoiceMail/"+bookingNo+"/"+emailID+"/";


        OkHttpClient mOkHttpClient;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();




        FormBody.Builder builder = new FormBody.Builder();


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)

                .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(InvoiceActivity.this))
                .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(InvoiceActivity.this))

                .get()
                .build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IpAddress.e("latlong",e.toString());
//                progressDialog.dismiss();

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String res = response.body().string();
                IpAddress.e("latlong", res);
                if(res.contains("Invoice Is Sent To Your Mail!")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InvoiceActivity.this, "Sent Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
//                progressDialog.dismiss();
            }
        });
    }


//        RequestQueue queue = Volley.newRequestQueue(InvoiceActivity.this);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,url,null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        System.out.println(response);
//                        Log.d(TAG, "onResponse: postDeviceIdToServer "+response);
//                        Toast.makeText(InvoiceActivity.this, "Sent Successfully", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                        disableOnBackPressed = false;
//                        //goto login Activity
//
//                        //hideProgressDialog();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                        Log.d(TAG, "onErrorResponse:12345 CashPayment "+error);
//                        progressDialog.dismiss();
//                        disableOnBackPressed = false;
//
//                        if ((error+"").contains("Invoice Is Sent To Your Mail!")){
//                            Toast.makeText(InvoiceActivity.this, "Invoice Is Sent To Your Mail!", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                        //hideProgressDialog();
//                    }
//                }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(getApplicationContext());
//            }
//        };
//        queue.add(jsObjRequest);
//    }


    private void intialize() {
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        driverProfileIV = (ImageView)findViewById(R.id.driverProfileIV);
        driverNameTV = (TextView)findViewById(R.id.driverNameTV);


//        truckIV = (ImageView)findViewById(R.id.truckIV);
//        truckDescriptionTV = (TextView)findViewById(R.id.truckDescriptionTV);
//
//        distanceTV = (TextView)findViewById(R.id.distanceTV);
//        durationTV = (TextView)findViewById(R.id.durationTV);
//        totalFareTV = (TextView)findViewById(R.id.totalFareTV);
//
//        fareListView = (ListView)findViewById(R.id.fareListView);
//        taxListView = (ListView)findViewById(R.id.taxListView);
//
//        totalBaseFareTV = (TextView)findViewById(R.id.totalBaseFareTV);
//        totalBillTV = (TextView)findViewById(R.id.totalBillTV);

        sourceTimeTV = (TextView)findViewById(R.id.sourceTimeTV);
        sourceAddressTV = (TextView)findViewById(R.id.sourceAddressTV);
        destinationTimeTV = (TextView)findViewById(R.id.destinationTimeTV);
        destinationAddressTV = (TextView)findViewById(R.id.destinationAddressTV);


//        setAllFields();
//        updateFaresListView();
//        setListViewHeightBasedOnChildren(fareListView);
//        updateTaxesListView();
//        setListViewHeightBasedOnChildren(taxListView);
    }

    private void setAllFields() {
        driverProfileIV.setImageResource(R.drawable.driver_dummy_100x100);
        driverNameTV.setText("");

        truckIV.setImageResource(R.drawable.open_selected_1_ton);

    }

    private void updateTaxesListView() {
        CustomListAdapter adapter = new CustomListAdapter(taxesDesc, taxes);
        taxListView.setAdapter(adapter);
    }
    private void updateFaresListView() {
        CustomListAdapter adapter = new CustomListAdapter(faresDesc, fares);
        fareListView.setAdapter(adapter);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
////        drawMarker(sourceLatLng,destinationLatLng);
//
////        drawMarker(sourceLatLng,destinationLatLng);
//


//    }
    public void goToLocation(double lat,double lng){}

    class CustomListAdapter extends BaseAdapter {
        String[] fareDescriptions;
        String[] fares;

        public CustomListAdapter(String[] fareDescriptions, String[] fares) {
            this.fareDescriptions = fareDescriptions;
            this.fares = fares;
        }

        @Override
        public int getCount() {
            return fareDescriptions.length;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fare_tax_list_sub_layout,parent,false);
            TextView fareDescTV = (TextView)view.findViewById(R.id.fare_descTV);
            TextView fareTV = (TextView)view.findViewById(R.id.fareTV);

            fareDescTV.setText(fareDescriptions[position]);
            fareTV.setText(rupee+" "+fares[position]);
            return view;
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void downloadInvoice(final String bookingNo) {
//        String url = "http://192.168.0.102/PickCApi/api/master/customer/TripInvoice/"+"BK170600006";

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppDialogTheme1);
        progressDialog.setTitle("Generating Your Invoice");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        disableOnBackPressed = true;

//        String url = "http://192.168.0.108/PickCApi/api/master/customer/TripInvoice/"+bookingNo;
        String url = WEB_API_ADDRESS+"api/master/customer/TripInvoice/"+bookingNo;
        Log.d(TAG, "downloadInvoice: downloadInvoice url "+url);
        JsonObjectRequest jsonobjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:12 downloadInvoice"+response);


                try {
//                  emailID = response.getString("EmailID");
                    String locationForm =response.getString("LocationFrom");
                    String locationto =response.getString("LocationTo");
                    String vehicleType =response.getString("VehicleType");
                     String invoiceNo =response.getString("InvoiceNo");
                    String bookingsNum =response.getString("BookingNo");
                    String invoiceDate =response.getString("InvoiceDate");
                    String customerMobileNo =response.getString("CustomerMobileNo");
                    String customerName =response.getString("CustomerName");
                    String tripID =response.getString("TripID");
                    String baseKM =response.getString("BaseKM");
                    String baseFare =response.getString("BaseFare");
                    String distanceKM =response.getString("DistanceKM");
                    String distanceFare =response.getString("DistanceFare");
                    String travelTime =response.getString("TravelTime");
                    String travelTimeFare =response.getString("TravelTimeFare");
                    String loadingUnLoadingCharges =response.getString("LoadingUnLoadingCharges");
                    String waitingCharges =response.getString("WaitingCharges");
                    String otherCharges =response.getString("OtherCharges");
                    String totalDistanceKm =response.getString("TotalDistanceKm");
                     driverID =response.getString("DriverID");

                    String driverName =response.getString("DriverName");
                    String startTime =response.getString("StartTime");
                    String endTime=response.getString("EndTime");

                    String totalAmount =response.getString("TotalAmount");

                    String gstTax =response.getString("GSTTax");

                    String paymentType=response.getString("PaymentType");
                    String totalBillAmount =response.getString("TotalBillAmount");
                    String totalFare =response.getString("TotalFare");
                   String vehicleGroup =response.getString("VehicleGroup");
                    String vehicleNo=response.getString("VehicleNo");
                    String vehicleMaker =response.getString("VehicleMaker");
//                    int loadingUnloadingtext = Integer.parseInt(response.getString("LoadingUnloading"));
                    int loadingUnloadingtext = response.getInt("LoadingUnloading");

                    setLoadingUnloadingTextview(loadingUnloadingtext);


//                    String vehicleCategory =response.getString("VehicleMaker");
                    dteOfInvoiceTV = (TextView)findViewById(R.id.dteOfInvoice);
                    dteOfInvoiceTV.setText(invoiceDate);
                    customerNameTV = (TextView)findViewById(R.id.customerName);
                    customerNameTV.setText(" "+customerName);
                    crnNumberTV = (TextView)findViewById(R.id.crnNumber);
                    crnNumberTV.setText(" "+bookingsNum);
                    totalBilledAmountTV = (TextView)findViewById(R.id.totalBilledAmountTV);
                    totalBilledAmountTV.setText("₹ "+totalBillAmount);
                    baseFareInKms = (TextView)findViewById(R.id.baseFareInKms);
                    baseFareInKms.setText("Base Fare for "+baseKM+" km");
                    baseFareFor4KmTV = (TextView)findViewById(R.id.baseFareFor4KmTV);
                    baseFareFor4KmTV.setText("₹ "+baseFare);
                    distanceFareforTenKmsTV = (TextView)findViewById(R.id.distanceFareforTenKmsTV);
                    distanceFareforTenKmsTV.setText("₹ "+distanceFare);
                    distanceForKms = (TextView)findViewById(R.id.distanceForKms);
                    distanceForKms.setText("Distance Fare for "+distanceKM+" km");
                    textView3 = (TextView)findViewById(R.id.textView3);
                    textView3.setText("Travel Time Fare for "+travelTime+" Mins");
                    travelTimeFareForMinTV = (TextView)findViewById(R.id.travelTimeFareForMinTV);
                    travelTimeFareForMinTV.setText("₹ "+travelTimeFare);
                    totalFareTV = (TextView)findViewById(R.id.totalFareTV);
                    totalFareTV.setText("₹ "+totalFare);
                    loadingAndUnloadingChargesTV = (TextView)findViewById(R.id.loadingAndUnloadingChargesTV);
                    loadingAndUnloadingChargesTV.setText("₹ "+loadingUnLoadingCharges);
                    waitingNightTimeChargesTV = (TextView)findViewById(R.id.waitingNightTimeChargesTV);
                    waitingNightTimeChargesTV.setText("₹ "+waitingCharges);
                    otherChargesTV = (TextView)findViewById(R.id.otherChargesTV);
                    otherChargesTV.setText("₹ "+otherCharges);
                    otherChargesTotalTV = (TextView)findViewById(R.id.otherChargesTotalTV);
                    otherChargesTotalTV.setText("₹ "+totalAmount);
                    serviceTaxTV = (TextView)findViewById(R.id.serviceTaxTV);
                    serviceTaxTV.setText("₹ "+gstTax);

                    totalBillAmountTV = (TextView)findViewById(R.id.totalBillAmountTV);
                    totalBillAmountTV.setText("₹ "+totalBillAmount);
                    totalTripKilometersTV = (TextView)findViewById(R.id.totalTripKilometersTV);
                    totalTripKilometersTV.setText(totalDistanceKm);
                    totalTripTimeTV = (TextView)findViewById(R.id.totalTripTimeTV);
                    totalTripTimeTV.setText(travelTime);
                    driverIdWithName = (TextView)findViewById(R.id.driverIdWithName);
                    driverIdWithName.setText(driverName);
                    driverIdWithoutName = (TextView)findViewById(R.id.driverIdWithoutName);
                    driverIdWithoutName.setText(driverID);
                    vehicleTypeTV = (TextView)findViewById(R.id.vehicleTypeTV);
                    vehicleTypeTV.setText(vehicleType);
                    sourceAddressTV.setText(locationForm);
                    destinationAddressTV.setText(locationto);
                    sourceTimeTV.setText(startTime);
                    destinationTimeTV.setText(endTime);
                    vehicleCategoryTV = (TextView)findViewById(R.id.vehicleCategoryTV);
                    vehicleCategoryTV.setText(vehicleGroup);


                     double fromLatitude =response.getDouble("FromLatitute");
                    double fromLongitude =response.getDouble("FromLongitude");
                    double toLatitude =response.getDouble("ToLatitute");
                    double toLongitude =response.getDouble("ToLongitude");
                    iv_map = (ImageView)findViewById(R.id.iv_map);

                    sourceLatLng = new LatLng(fromLatitude, fromLongitude);
                    destinationLatLng = new LatLng(toLatitude, toLongitude);

                    String addres1 = null;
                    String cityName1 = null;
                    String stateName1 = null;
                    String cityName2 = null;
                    String stateName2 = null;
                    String addres2 = null;
                    String getPostalCode1 =null;
                    String getPostalCode2 = null;

                    try {
                        Geocoder geocoder = new Geocoder(InvoiceActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(fromLatitude, fromLongitude, 1);

                        addres1 = addresses.get(0).getSubLocality();
                         cityName1 = addresses.get(0).getLocality();
                         stateName1 = addresses.get(0).getAdminArea();
                        getPostalCode1 = addresses.get(0).getPostalCode();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Geocoder geocoder = new Geocoder(InvoiceActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(toLatitude, toLongitude, 1);

                        addres2 = addresses.get(0).getSubLocality();
                         cityName2 = addresses.get(0).getLocality();
                         stateName2 = addresses.get(0).getAdminArea();
                         getPostalCode2 = addresses.get(0).getPostalCode();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                     String STATIC_MAP_API_ENDPOINT = "http://maps.googleapis.com/maps/api/staticmap?size=640x400&path=";

//                    icon:http://tinyurl.com/2ftvtt6|
//                    String urlone= "http://ragsarma-001-site13.htempurl.com/Images/source2.png";

                    String marker_me = "color:0x006400|label:|"+addres1+cityName1+stateName1+getPostalCode1;
                    String marker_dest = "color:0xFF0000|label:|"+addres2+cityName2+stateName2+getPostalCode2;
                    String path = null;
                    try {
                        marker_me = URLEncoder.encode(marker_me, "UTF-8");
                        marker_dest = URLEncoder.encode(marker_dest, "UTF-8");

                     path= "weight:0|color:blue|geodesic:false|"+addres1+"|"+addres2;
                    path = URLEncoder.encode(path, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

//                    STATIC_MAP_API_ENDPOINT = STATIC_MAP_API_ENDPOINT + path + "&markers= icon:http://ragsarma-001-site13.htempurl.com/Images/source2.png" +  "&markers=" + marker_dest;
                    STATIC_MAP_API_ENDPOINT = STATIC_MAP_API_ENDPOINT + path + "&markers=" + marker_me + "&markers=" + marker_dest;

//                    iv_map.setScaleType(ImageView.ScaleType.FIT_XY);

                    Picasso.with(InvoiceActivity.this).load(STATIC_MAP_API_ENDPOINT).into(iv_map);
                    sendEmaitoCustomer();

//


////                    drawMarker(sourceLatLng,destinationLatLng);
//                    String url = getMapsApiDirectionsUrl(sourceLatLng, destinationLatLng);
//                    ReadTask downloadTask = new ReadTask();
//                    // Start downloading json data from Google Directions API
//                    downloadTask.execute(url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                disableOnBackPressed = false;
                progressDialog.dismiss();
//              createDrivingRoute(sourceLatLng,destinationLatLng,false);
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, error.networkResponse + " downloadInvoice error.networkResponse onErrorResponse:78945 " + error);
                progressDialog.dismiss();
                if ((error + "").contains("TimeoutError")) {
                   // displayErrorMessage("Server busy. Please try again after some time");
                    AlertDialogActivity.showAlertDialogActivity(InvoiceActivity.this, "Server Busy",
                            "Unable to process now. Do you want to retry...", "Retry", "Cancel",
                            new AlertDialogActivity.OnPositiveBtnClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    downloadInvoice(bookingNo);
                                }
                            }, new AlertDialogActivity.OnNegativeBrnClickListener() {
                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                    return;
                }
                //TODO: handle failure
                disableOnBackPressed = false;

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(InvoiceActivity.this);
            }
        };


        Volley.newRequestQueue(InvoiceActivity.this).add(jsonobjRequest);

    }

    public void sendEmaitoCustomer() {
        Log.d(TAG, "sendEmaitoCustomer: ");
//        String mobileNumber = CredentialsSharedPref.getMobileNO(InvoiceActivity.this);
//        getUseremal(mobileNumber);


            if (sendEmailForCustomer.contains("no")) {

                rateDriverBut.setVisibility(View.GONE);
                imagestar.setVisibility(View.GONE);

        }
        if (sendEmailForCustomer.contains("yes")){
            final String mobilenm = CredentialsSharedPref.getMobileNO(InvoiceActivity.this);
            getUseremal(mobilenm);
        }


    }
//    private Marker showMarkerOnMap(@NonNull LatLng location, boolean callGetAdreessASync) {
//
//        pinIV.setImageResource(R.drawable.source2);
//        map.clear();
//        mp = new MarkerOptions();
//
//        mp.position(location);
//        //mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        Log.d("Tag", "showMarkerOnMap: " + location.latitude + "\t" + location.longitude);
//        /*String fromAddress = getCompleteAddressString(this, location.latitude, location.longitude, FROM);
//        mp.title(fromAddress);*/
//        String fromAddress = "" + location.latitude + ", " + location.longitude;
//        if (callGetAdreessASync) {
//            HomeActivity.GetStringFromLocationAsync async = new HomeActivity.GetStringFromLocationAsync(location.latitude, location.longitude, FROM);
//            async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//        if (currentAddress.isEmpty()) {
//            currentAddress = fromAddress;
//        }
//        mp.title(currentAddress);
//        mp.icon(sourceIcon);
//        if (fromLocationTV.getText().toString().isEmpty()) {
//            fromLocationTV.setText(currentAddress);
//        }
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(location.latitude, location.longitude), MAP_CAMERA_ZOOM_ANIMATION));
//        if (fromMarker != null && fromMarker.isVisible()) {
//            fromMarker.remove();
//        }
//        if (isFromLocationLocked && isToLocationLocked) {
//            fromMarker = map.addMarker(mp);
//        }
//        return fromMarker;
//
//
//    }
public void drawMarker(LatLng source_point, LatLng destination_point) {

    // Creating an instance of MarkerOptions
    MarkerOptions markerOptions1 = new MarkerOptions();
//    markerOptions1.title("Marker");
//    markerOptions1.snippet("Marker Yo Yo");
    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    markerOptions1.position(source_point);

    MarkerOptions markerOptions2 = new MarkerOptions();
//    markerOptions2.title("Marker2");
//    markerOptions2.snippet("Marker Xo Xo");
    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    markerOptions2.position(destination_point);

    // Adding marker on the Google Map

    mGoogleMap.addMarker(markerOptions1);
    mGoogleMap.addMarker(markerOptions2);
//    String url = getMapsApiDirectionsUrl(sourceLatLng, destinationLatLng);
//    ReadTask downloadTask = new ReadTask();
//    // Start downloading json data from Google Directions API
//    downloadTask.execute(url);
}

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (disableOnBackPressed){
            if (sendEmailForCustomer.contains("no")){
                super.onBackPressed();
            }
            Toast.makeText(this, "Please Rate the driver", Toast.LENGTH_SHORT).show();
        }else {
            if (sendEmailForCustomer.contains("no")){
                super.onBackPressed();
            }else {
                Toast.makeText(this, "Please Rate the driver", Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected void createDrivingRoute(LatLng sourcePosition, final LatLng destPosition, boolean canShowDriverLocation) {

        String mode = GMapV2Direction.MODE_DRIVING;

        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions()
                            .width(10)
                            .color(Color.BLUE);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = ((InvoiceActivity) getApplicationContext()).mGoogleMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                    boolean hasPoints = false;
                    Double maxLat = null, minLat = null, minLon = null, maxLon = null;

                    if (polylin != null && polylin.getPoints() != null) {
                        List<LatLng> pts = polylin.getPoints();
                        for (LatLng coordinate : pts) {
                            // Find out the maximum and minimum latitudes & longitudes
                            // Latitude
                            maxLat = maxLat != null ? Math.max(coordinate.latitude, maxLat) : coordinate.latitude;
                            minLat = minLat != null ? Math.min(coordinate.latitude, minLat) : coordinate.latitude;

                            // Longitude
                            maxLon = maxLon != null ? Math.max(coordinate.longitude, maxLon) : coordinate.longitude;
                            minLon = minLon != null ? Math.min(coordinate.longitude, minLon) : coordinate.longitude;

                            hasPoints = true;
                        }
                    }

                    if (hasPoints) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        builder.include(new LatLng(maxLat, maxLon));
                        builder.include(new LatLng(minLat, minLon));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
//                        ((HomeActivity) activity).map.addMarker()

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, mode).execute();

    }
    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;

    }
    private class ReadTask extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }
    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line ="";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();


            }
            catch (Exception e) {
                Log.d("Exception  url", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }
    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i=0 ; i < jRoutes.length() ; i ++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String,String>>();
                    for(int j = 0 ; j < jLegs.length() ; j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for(int k = 0 ; k < jSteps.length() ; k ++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for(int l = 0 ; l < list.size() ; l ++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }}
    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            mGoogleMap.addPolyline(polyLineOptions);

        }}

    private void getUseremal(@NonNull final String mobileNo) {


        disableOnBackPressed = true;
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS + CUSTOMER_INFO_API_CALL + mobileNo;

        OkHttpClient mOkHttpClient = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)

                .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(InvoiceActivity.this))
                .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(InvoiceActivity.this))

                .get()
                .build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IpAddress.e("latlong",e.toString());


            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String res = response.body().string();
                IpAddress.e("latlong", res);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject Jobject = new JSONObject(res);
                            String email = Jobject.getString(MAIL_ID_JSON_KEY);
//                            createAndShowDeliveryPersonMobileNumberDialog(email);

                           emailInvoice(email);
                        }catch (JSONException e)
                        {
                            e.printStackTrace();}

                    }
                });



//                JsonObject jsonObject =new JsonObject(res);

//                Gson gson = new Gson();
//                ProfileforCustomer profile = gson.fromJson(response.toString(), ProfileforCustomer.class);
//                if (profile != null) {
//
////                    String email = String.valueOf(jsonObject.get(MAIL_ID_JSON_KEY));
//                    String email = profile.getEmailID();
////                        CredentialsSharedPref.setEmailId(InvoiceActivity.this,email);
//                   emailInvoice(email);


            }

        });}


    private void getUserInfoFromServer(@NonNull final String mobileNo) {


        disableOnBackPressed = true;
        Log.d(TAG, "isMobileNoExistsInServer: ");
        String url = WEB_API_ADDRESS + CUSTOMER_INFO_API_CALL + mobileNo;

        OkHttpClient mOkHttpClient = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)

                .addHeader("AUTH_TOKEN", CredentialsSharedPref.getAuthToken(InvoiceActivity.this))
                .addHeader("MOBILENO", CredentialsSharedPref.getMobileNO(InvoiceActivity.this))

                .get()
                .build();


        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IpAddress.e("latlong",e.toString());


            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String res = response.body().string();
                IpAddress.e("latlong", res);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            JSONObject Jobject = new JSONObject(res);
                            String email = Jobject.getString(MAIL_ID_JSON_KEY);
                            createAndShowDeliveryPersonMobileNumberDialog(email);

//                            emailInvoice(email);
                        }catch (JSONException e)
                        {
                            e.printStackTrace();}

                    }
                });



//                JsonObject jsonObject =new JsonObject(res);

//                Gson gson = new Gson();
//                ProfileforCustomer profile = gson.fromJson(response.toString(), ProfileforCustomer.class);
//                if (profile != null) {
//
////                    String email = String.valueOf(jsonObject.get(MAIL_ID_JSON_KEY));
//                    String email = profile.getEmailID();
////                        CredentialsSharedPref.setEmailId(InvoiceActivity.this,email);
//                   emailInvoice(email);


                }

        });




//        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse:12 " + response);
//
//                    try {
//
//                        String email = response.getString(MAIL_ID_JSON_KEY);
////                        CredentialsSharedPref.setEmailId(InvoiceActivity.this,email);
//                        emailInvoice(email);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//
//
//
//
//                //TODO: handle success
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.d(TAG, "onErrorResponse:78945 " + error);
//                //TODO: handle failure
//
//
//                disableOnBackPressed = false;
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return CredentialsSharedPref.getHeaders(InvoiceActivity.this);
//            }
//        };
//
//
//        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

//    class ProfileforCustomer
//    {
////        {"MobileNo":"8341037724","Name":"Praneeth","EmailID":"vr_praneeth@yahoo.com"}
//
//        public String getMobileNo() {
//            return MobileNo;
//        }
//
//        public void setMobileNo(String mobileNo) {
//            MobileNo = mobileNo;
//        }
//
//        public String getName() {
//            return Name;
//        }
//
//        public void setName(String name) {
//            Name = name;
//        }
//
//        public String getEmailID() {
//            return EmailID;
//        }
//
//        public void setEmailID(String emailID) {
//            EmailID = emailID;
//        }
//
//        String MobileNo;
//        String Name;
//        String EmailID;
//
//    }
    private void setLoadingUnloadingTextview(int loadingCode)
    {
        loadingUnloadTV = (TextView) findViewById(R.id.loadingUnloadTV);

        switch (loadingCode)
        {
            case 1370:
                loadingUnloadTV.setText("Loading Charges");
                break;
            case 1371:
                loadingUnloadTV.setText("UnLoading Charges");
                break;
            case 1372:
                loadingUnloadTV.setText("Loading & UnLoading Charges");
                break;
            case 1373:
                loadingUnloadTV.setText("Loading & UnLoading Charges");
                break;

        }
    }
    private String deliveryemail = "";
    private void createAndShowDeliveryPersonMobileNumberDialog(final String email) {
        final Dialog dialog = new Dialog(InvoiceActivity.this);
        //dialog.setTitle("Select Cargo Type");
        dialog.setContentView(R.layout.final_cargo_delivery_person_email_dialog_layout);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();


        final PickCCustomEditText mobileNoET = (PickCCustomEditText) dialog.findViewById(R.id.mobileNoEditText);
        mobileNoET.setText(email);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkBox);
        selectSameMoileNoCheckBox(checkBox, mobileNoET);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: b " + b);
                if (b) {
                    selectSameMoileNoCheckBox(checkBox, mobileNoET);
                } else {
                    unSelectSameMoileNoCheckBox(checkBox, mobileNoET);
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    deliveryemail = email;
                } else {
                    String emailIdentered = mobileNoET.getText().toString();
                    if (email.isEmpty() ) {
//                        showSnackBar(v, "Please enter valid 10 digit mobile No.");
                        return;
                    }
                    deliveryemail = emailIdentered;
                }
                emailInvoice(deliveryemail);

                dialog.dismiss();

            }
        });
    }

    private void selectSameMoileNoCheckBox(CheckBox checkBox, PickCCustomEditText mobileNoET) {
        checkBox.setChecked(true);
        mobileNoET.setEnabled(false);
        Log.d(TAG, "selectSameMoileNoCheckBox: ");
    }

    private void unSelectSameMoileNoCheckBox(CheckBox checkBox, PickCCustomEditText mobileNoET) {
        checkBox.setChecked(false);
        mobileNoET.setEnabled(true);
        mobileNoET.requestFocus();
        Log.d(TAG, "unSelectSameMoileNoCheckBox: ");
    }

}
