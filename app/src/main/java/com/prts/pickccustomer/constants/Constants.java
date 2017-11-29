package com.prts.pickccustomer.constants;

/**
 * Created by Uday on 18-08-2016.
 */
public interface Constants {

    String SUPPORT_URL = "http://ragsarma-001-site6.htempurl.com/Help/mobilehelp";

    String ISMOBILEEXIST="api/master/customer/list";
//    String SUPPORT_URL = "http://ragsarma-001-site6.htempurl.com/Help/mobilehelp";

    String BULLET = "\u2022";
    String TRUE = "true";
    String FALSE = "false";
    //String WEB_API_ADDRESS = "http://10.0.0.15/PickCApi/";
    //String WEB_API_ADDRESS = "http://www.pickcargo.in/";

    //PRODUCATION
   String WEB_API_ADDRESS = "http://ragsarma-001-site13.htempurl.com/";
    //TESTING
    //String WEB_API_ADDRESS="http://localhost:51896/";


//   String WEB_API_ADDRESS = "http://192.168.0.121/PickCApi/";
    String DEVICE_ID_API_CALL = "api/master/customer/deviceid";
    String MOBILE_NO_DEVICE_ID_JSON_KEY = "mobileNo";
    String DEVICE_ID_DEVICE_ID_JSON_KEY = "deviceId";
    //1253 get trucks lat long by
    String NEAR_BY_TRUCKS_BASED_ON_SELECTION_API_CALL = "api/operation/driveractivity/user";
    String VEHICLE_TYPE_JSON_KEY = "vehicleType";
    String VEHICLE_GROUP_JSON_KEY = "vehicleGroup";

    String UPDATE_BOOKING_INFO_API_CALL = "api/operation/driveractivity/user";
    String UPDATE_USER_PROFILE_API_CALL = "api/master/customer/";
    String UPDATE_PROFILE_PASSWORD_API_CALL = "api/master/customer/changepassword/";
    String VERIFY_MOBILE_NO_API_CALL = "api/master/customer/check/";
    String VERIFY_OTP_NO_API_CALL = "api/master/customer/verifyotp/";
    String GET_OTP_FORGOT_PASSWORD_API_CALL = "api/master/customer/forgotpassword/";
    String VERIFY_OTP_FORGOT_PASSWORD_API_CALL = "api/master/customer/forgotpassword";



    String CUSTOMER_INFO_API_CALL = "api/master/customer/";
    String CUSTOMER_SAVE_API_CALL = "api/master/customer/save";
    String LOG_IN_API_CALL = "api/master/customer/login";
    String LOG_OUT_API_CALL = "api/master/customer/logout";

    String MOBILE_NO_JSON_KEY = "MobileNo";
    String NAME_JSON_KEY = "Name";
    String MAIL_ID_JSON_KEY = "EmailID";
    String PASSWORD_JSON_KEY = "Password";
    String NEW_PASSWORD_JSON_KEY = "NewPassword";
    String TOKEN_JSON_KEY = "token";
    String MESSAGE_ERROR="Message";

    // Todo Vehicle group   ***************** Start *********************
    String VEHICLE_GROUP_LIST = "api/master/vehiclegroup/list";
    String VEHICLE_GROUP_LOOK_UP_ID_JSON_KEY = "LookupID";
    String VEHICLE_GROUP_LOOK_UP_DESCRIPTION_JSON_KEY = "LookupDescription";
    // Todo Vehicle group   ***************** end *********************


    // Todo Vehicle type   ***************** Start *********************
    String VEHICLE_TYPE_LIST = "api/master/vehicletype/list";

    String VEHICLE_TYPE_LOOK_UP_ID_JSON_KEY = "LookupID";
    String VEHICLE_TYPE_LOOK_UP_DESCRIPTION_JSON_KEY = "LookupDescription";
    // Todo Vehicle type   ***************** end *********************


    // Todo Rate Card   ***************** Start *********************
    String RATE_CARD_LIST = "api/master/ratecard/list";
    String RATE_CARD_VALUES = "api/master/ratecard/";
    String CARGO_TYPE_LIST_API_CALL = "api/master/cargotype/list";
    String LOADING_UNLOADING_LIST_API_CALL = "api/master/loadingunloading/list";

    String TRUCK_CATEGEORY_ID_JSON_KEY = "Category";
    String TRUCK_VEHICLE_TYPE_ID_JSON_KEY = "VehicleType";
    String BASE_FARE_JSON_KEY = "BaseFare";
    String BASE_KM_JSON_KEY = "BaseKM";
    String DISTANCE_FARE_JSON_KEY = "DistanceFare";
    String WAITING_FARE_JSON_KEY = "WaitingFare";
    String RIDE_TIME_FARE_JSON_KEY = "RideTimeFare";
    String CANCELLATION_FEE_JSON_KEY = "CancellationFee";


    String CARGO_TYPE_ID_JSON_KEY = "LookupID";
    String CARGO_TYPE_DES_JSON_KEY = "LookupDescription";
    // Todo Rate Card   ***************** End *********************


    // Todo Book_Now   ***************** Start *********************

    String BOOKING_API_CALL = "api/operation/booking/save";


    String BOOKING_NO_BOOKING_JSON_KEY = "BookingNo";
    String BOOKING_DATE_BOOKING_JSON_KEY = "BookingDate";
    String CUSTOMER_ID_BOOKING_JSON_KEY = "CustomerID";
    String REQUIRED_DATE_BOOKING_JSON_KEY = "RequiredDate";
    String LOCATION_FROM_BOOKING_JSON_KEY = "LocationFrom";
    String LOCATION_TO_BOOKING_JSON_KEY = "LocationTo";
    String CARGO_DESC_BOOKING_JSON_KEY = "CargoDescription";

    String VEHICLE_GROUP_BOOKING_JSON_KEY = "VehicleGroup";
    String VEHICLE_TYPE_BOOKING_JSON_KEY = "VehicleType";
    String REMARKS_BOOKING_JSON_KEY = "Remarks";
    String IS_CONFIRM_BOOKING_JSON_KEY = "IsConfirm";
    String CONFIRM_DATE_BOOKING_JSON_KEY = "ConfirmDate";
    String DRIVER_ID_BOOKING_JSON_KEY = "DriverID";
    String VEHICLE_NO_BOOKING_JSON_KEY = "VehicleNo";
    String IS_CANCEL_BOOKING_JSON_KEY = "IsCancel";
    String CANCEL_TIME_BOOKING_JSON_KEY = "CancelTime";
    String CANCEL_REMARKS_BOOKING_JSON_KEY = "CancelRemarks";
    String IS_COMPLETE_BOOKING_JSON_KEY = "IsComplete";
    String COMPLETE_TIME_BOOKING_JSON_KEY = "CompleteTime";


    String BOOKING_IS_CONFIRM_BOOKING_JSON_KEY = "isConfirm";
    String DRIVER_ID_IS_CONFIRM_JSON_KEY = "driverId";
    String VEHICLE_NO_IS_CONFIRM_JSON_KEY = "vehicleNo";
    String DRIVER_NAME_IS_CONFIRM_JSON_KEY = "driverName";
    String DRIVER_MOB_NO_IS_CONFIRM_JSON_KEY = "MobileNo";
    String DRIVER_IMAGE_IS_CONFIRM_JSON_KEY = "driverImage";


    String PAY_LOAD_BOOKING_JSON_KEY = "PayLoad";
    String CARGO_TYPE_BOOKING_JSON_KEY = "CargoType";
    String LATITUDE_BOOKING_JSON_KEY = "Latitude";
    String LONGITUDE_BOOKING_JSON_KEY = "Longitude";

    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";

    String TO_LATITUDE = "ToLatitude";
    String TO_LONGITUDE = "ToLongitude";

    String RECEIVER_MOBILE_NO = "ReceiverMobileNo";
    String LOADING_UNLOADING_JSON_KEY = "LoadingUnLoading";


    String BOOKING_NO_RESPONSE_BOOKING_JSON_KEY = "bookingNo";
    String REMARKS_CANCEL_JSON_KEY = "remarks";
    // Todo Book_Now   ***************** End *********************

    String BOOKING_HISTORY_LIST_API_CALL_BASED_ON_MOBILE_NO = "api/operation/booking/list/";
    String DRIVER_CURRENT_LAT_LNG_API_CALL = "api/operation/booking/drivergeoposition/";

    String BOOKING_INFO_API_CALL_BASED_ON_BOOKING_NO = "api/operation/booking/";

    String CUSTOMER_IS_IN_TRIP_API_CALL = "api/operation/trip/customer/isintrip";
    String INVOICE_API_CALL = "api/operation/invoice/";

    String IS_IN_TRIP_JSON_KEY = "isintrip";
    String BOOKING_NO_SMALL_JSON_KEY = "bookingno";
    String BOOKING_INFO_IS_CONFIRM_API_CALL_BASED_ON_BOOKING_NO = "api/operation/booking/isconfirm/";

    // Todo TRIP   ***************** Start *********************
    String TRIP_API_CALL = "api/operation/trip/save";

    String TRIP_DATE_TRIP_JSON_KEY = "TripDate";
    String CUSTOMER_MOBILE_TRIP_JSON_KEY = "CustomerMobile";
    String DRIVER_ID_TRIP_JSON_KEY = "DriverID";
    String DRIVER_VEHICLE_NO_TRIP_JSON_KEY = "VehicleNo";
    String VEHICLE_GROUP_TRIP_JSON_KEY = "VehicleGroup";
    String VEHICLE_TYPE_TRIP_JSON_KEY = "VehicleType";
    String LOCATION_FROM_TRIP_JSON_KEY = "LocationFrom";
    String LOCATION_TO_TRIP_JSON_KEY = "LocationTo";
    String DISTANCE_TRIP_JSON_KEY = "Distance";
    String START_TIME_TRIP_JSON_KEY = "StartTime";
    String END_TIME_TRIP_JSON_KEY = "EndTime";
    String TRIP_DURATION_MINUTES_TRIP_JSON_KEY = "TripMinutes";
    String WAITING_MINUTES_TRIP_JSON_KEY = "WaitingMinutes";
    String TOTAL_TRUCK_WEIGHT_TRIP_JSON_KEY = "TotalWeight";
    String CARGO_DESCRIPTION_TRIP_JSON_KEY = "CargoDescription";
    String REMARKS_TRIP_JSON_KEY = "Remarks";


    String CURRENT_LAT_JSON_KEY = "CurrentLat";
    String CURRENT_LONG_JSON_KEY = "CurrentLong";

    //-----------------

    String TRIP_ID_JSON_KEY = "tripID";
    // Todo TRIP   ***************** end *********************

    String TRIP_CANCEL_API_CALL = "api/operation/trip/";
    String BOOKING_CANCEL_API_CALL = "api/operation/booking/delete";


    String DEFAULT_LOCATION = "NoLocation";
    float MAP_CAMERA_ZOOM_ANIMATION = 18;
    String rupee = "₹";
    String FINISH_ACTIVITY = "finish";
    String ENABLE_BOOKING = "enable_booking";
    String DISABLE_ON_BACK_PRESS = "disable_back_press";

    String STATUS_CONFIRMED = "CONFIRMED";
    String STATUS_CANCELLED = "CANCELLED";
    String STATUS_COMPLETED = "COMPLETED";
    String STATUS_PENDING = "PENDING";
    String STATUS_NOT_SPECIFIED = "NO_STATUS";

    String dateSeperator = "/";
    String timeSeperator = ":";


    int VEHICLE_TYPE_OPEN = 1300;
    int VEHICLE_TYPE_CLOSED = 1301;

   // String CUSTOM_FONT = "fonts/angelina.TTF";
    //String CUSTOM_FONT = "fonts/angelina.TTF";
    //String CUSTOM_FONT = "fonts/angelina.TTF";
    String CUSTOM_FONT = "fonts/rosemary_roman.ttf";
    //String CUSTOM_FONT = "fonts/data_latin.ttf";


    String NO_TRUCKS = "no trucks";
    String DISTANCE_TIME_TRIP_ESTIMATE = "api/operation/booking/tripestimate";
}
