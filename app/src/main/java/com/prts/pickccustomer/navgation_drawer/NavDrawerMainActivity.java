package com.prts.pickccustomer.navgation_drawer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.prts.pickccustomer.R;
import com.prts.pickccustomer.constants.Constants;
import com.prts.pickccustomer.credentials.CredentialsSharedPref;
import com.prts.pickccustomer.ui.AboutActivity;
import com.prts.pickccustomer.ui.EmergencyActivity;
import com.prts.pickccustomer.ui.ProfileActivity;
import com.prts.pickccustomer.ui.RateCardActivity;
import com.prts.pickccustomer.ui.WebViewActivity;
import com.prts.pickccustomer.ui.credentials.LoginActivity;
import com.prts.pickccustomer.ui.history.BookingHistoryActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavDrawerMainActivity extends AppCompatActivity implements Constants {

    private static final String TAG = "NavDrawerAct";

    int mPosition = -1;
    String mTitle = "";
    Toolbar toolbar;
    TextView toolbartitle;

    // Array of strings storing country names
    public static String[] mCountries;


    // Array of strings to initial counts
    String[] mCount = new String[]{
            "", "", "", "", "",
            "", "", "", "", ""};

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FrameLayout frameLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mDrawer, mDrawerListLayout;
    private List<HashMap<String, String>> mList;
    private SimpleAdapter mAdapter;
    final private String COUNTRY = "country";
    final private String FLAG = "flag";
    final private String COUNT = "count";


    @Override
    public void setContentView(int layoutResID) {

        LinearLayout fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.nav_activity_main, null);
        frameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_frame);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(fullLayout);

        //Your drawer content...

    }

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] mFlags = new int[]{
            R.drawable.truck_icon,
            R.drawable.ic_nav_my_rides,
            R.drawable.ic_nav_rate_card,

            R.drawable.ic_nav_emergency_contact,
            R.drawable.ic_nav_support,
            R.drawable.about,
            R.drawable.logout
    };
    boolean isNavDrawerOpened = false;

    protected void navDrawerOnCreate() {
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity_main);*/


        // Getting an array of country names
        mCountries = new String[]{"Book your truck",
                "Booking history",
                "Rate card",

                "Emergency contacts",
                "Help",
                "About",
                "Log out"};

        // Title of the activity
        mTitle = (String) getTitle();


        // Getting a reference to the drawer listview
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Getting a reference to the sidebar drawer ( Title + ListView )
        mDrawer = (LinearLayout) findViewById(R.id.main_parent_view);
        mDrawerListLayout = (LinearLayout) findViewById(R.id.drawer);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        toolbartitle = (TextView) findViewById(R.id.titletool);



        // Each row in the list stores country name, count and flag
        mList = new ArrayList<HashMap<String, String>>();


        for (int i = 0; i < mCountries.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(COUNTRY, mCountries[i]);
            //hm.put(COUNT, mCount[i]);
            hm.put(FLAG, Integer.toString(mFlags[i]));
            mList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {FLAG, COUNTRY, COUNT};

        // Ids of views in listview_layout
        int[] to = {R.id.flag, R.id.country, R.id.count};

        // Instantiating an adapter to store each items
        // R.layout.nav_drawer_layout defines the layout of each item
        mAdapter = new SimpleAdapter(this, mList, R.layout.nav_drawer_layout, from, to);

        // Getting reference to DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Creating a ToggleButton for NavigationDrawer with drawer event listener
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                highlightSelectedCountry();
                supportInvalidateOptionsMenu();
                isNavDrawerOpened = false;
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle("More Apps From Us");
                supportInvalidateOptionsMenu();
             initialize();
                isNavDrawerOpened = true;
            }
        };

        // Setting event listener for the drawer
        Log.d("TAG", mDrawerToggle + " NavDrawerOnCreate: " + mDrawerLayout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // ItemClick event handler for the drawer items
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, final View arg1, int position,
                                    long arg3) {

                // Increment hit count of the drawer list item
                //incrementHitCount(position);
                showFragment(position);
                if (position < 5) { // Show fragment for countries : 0 to 4
                    // showFragment(position);
                } else { // Show message box for countries : 5 to 9
                    //Toast.makeText(getApplicationContext(), mCountries[position], Toast.LENGTH_LONG).show();
                }
                arg1.setBackgroundResource(R.color.appThemeYellow);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        arg1.setBackgroundResource(android.R.color.transparent);
                    }
                },500);

                // Closing the drawer
                mDrawerLayout.closeDrawer(mDrawerListLayout);
            }
        });


        // Enabling Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting the adapter to the listView

        mDrawerList.setAdapter(mAdapter);
//       mAdapter.notifyDataSetChanged();

        initialize();
    }

    private  void initialize() {
        TextView userMobileNOTV = (TextView) findViewById(R.id.user_mobileNo_TV);
        TextView userNameTV = (TextView) findViewById(R.id.user_name_TV);
        userMobileNOTV.setText(CredentialsSharedPref.getMobileNO(NavDrawerMainActivity.this));
        userNameTV.setText(CredentialsSharedPref.getName(NavDrawerMainActivity.this));

        LinearLayout userProfileLL = (LinearLayout)findViewById(R.id.userProfileLL);
        userProfileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(NavDrawerMainActivity.this, ProfileActivity.class));


            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}*/

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLayout);
        int menuSize = menu.size();
        for (int i = 0; i < menuSize; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setVisible(!drawerOpen);
        }
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
			case R.id.action_websearch:
				// create intent to perform web search for this planet
				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, toolbartitle.getText());
				// catch event that there's no activity to handle intent
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivity(intent);
				} else {
					Toast.makeText(this, "app_not_available", Toast.LENGTH_LONG).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
*/


/*	public void incrementHitCount(int position){
		HashMap<String, String> item = mList.get(position);
		String count = item.get(COUNT);
		item.remove(COUNT);
		if(count.equals("")){
			count = "  1  ";
		}else{
			int cnt = Integer.parseInt(count.trim());
			cnt ++;
			count = "  " + cnt + "  ";
		}
		item.put(COUNT, count);
		mAdapter.notifyDataSetChanged();
	}*/

    public void showFragment(int position) {
        getSupportActionBar().setTitle(R.string.app_name);
        Fragment fragment = null;
        switch (position) {
            case 0:

                break;
            case 1:
                startActivity(new Intent(NavDrawerMainActivity.this, BookingHistoryActivity.class));
                //finish();

                /*String completeUrl2 = "http://ragsarma-001-site6.htempurl.com/bill/mobile";
                Intent intent2 = new Intent(NavDrawerMainActivity.this,WebViewActivity.class);
                intent2.putExtra("url",completeUrl2);
                startActivity(intent2);*/
                return;
            case 2:
               /* if (TruckCateogeoriesFragment.rateCardDialog != null) {
                    TruckCateogeoriesFragment.rateCardDialog.show();
                }*/

                startActivity(new Intent(NavDrawerMainActivity.this, RateCardActivity.class));
                return;
            case 3:
                startActivity(new Intent(getApplicationContext(), EmergencyActivity.class));
                break;
            case 4:
                String completeUrl1 = SUPPORT_URL;
                Intent intent = new Intent(NavDrawerMainActivity.this,WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL,completeUrl1);

                startActivity(intent);
                break;
            case 5:
                startActivity(new Intent(NavDrawerMainActivity.this, AboutActivity.class));
                //startActivity(new Intent(NavDrawerMainActivity.this, SupportMainActivity.class));
                break;
            case 6:
                logOutCall();
                startActivity(new Intent(NavDrawerMainActivity.this, LoginActivity.class));
                finish();
                break;
            case 7:

                return;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
        }
        if (fragment != null) {
            // Getting reference to the FragmentManager
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Creating a fragment transaction
            FragmentTransaction ft = fragmentManager.beginTransaction();
            frameLayout.setVisibility(View.VISIBLE);
            // Adding a fragment to the fragment transaction
            ft.replace(R.id.content_frame, fragment);
            // Committing the transaction
            ft.commit();
        }
    }

    private void logOutCall() {

        String url = WEB_API_ADDRESS + LOG_OUT_API_CALL;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse:466161 " + response);
                Toast.makeText(NavDrawerMainActivity.this, "Logged out Successfully", Toast.LENGTH_SHORT).show();

                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse:466161 " + error);
                if (error.toString().contains("USER LOGGEDOUT SUCCESSFULLY")) {
                    CredentialsSharedPref.clearAuthToken(NavDrawerMainActivity.this);
                    CredentialsSharedPref.clearMobileNumber(NavDrawerMainActivity.this);
                    Toast.makeText(NavDrawerMainActivity.this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((error + "").contains("TimeoutError")) {

                    displayErrorMessage("Server busy. Please try again after some time");
                    return;
                }
                displayErrorMessage("Please check your\n Internet Connection");

                //TODO: handle failure
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return CredentialsSharedPref.getHeaders(NavDrawerMainActivity.this);
            }
        };


        Volley.newRequestQueue(this).add(jsonArrayRequest);

        // Show response on activity
        //resultTV.setText( text  );

    }

    private void displayErrorMessage(String message) {
        Toast.makeText(NavDrawerMainActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    boolean isMoreAppsFragmentShowing = false;

    // Highlight the selected country : 0 to 4
    public void highlightSelectedCountry() {
        int selectedItem = mDrawerList.getCheckedItemPosition();

        if (selectedItem > 4)
            mDrawerList.setItemChecked(mPosition, true);
        else
            mPosition = selectedItem;

		/*if(mPosition!=-1)
			getSupportActionBar().setTitle(mCountries[mPosition]);*/
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpened) {
            mDrawerLayout.closeDrawer(mDrawerListLayout);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initialize();
    }
}