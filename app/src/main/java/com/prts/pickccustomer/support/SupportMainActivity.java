package com.prts.pickccustomer.support;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.prts.pickccustomer.R;

public class SupportMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "SupportMainAct";
    String[] browseTopics = {"Using PICK C", "My account", "Referral",
            "Safety", "Other cargo services",
            "I did not receive an OTP to reset my password.", "Forgot password."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter());
        listView.setOnItemClickListener(this);
        setListViewHeightBasedOnChildren(listView);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: " + browseTopics[position]);
        Intent tellUsActivityIntent = new Intent(SupportMainActivity.this,TesllUsAboutItSupportActivity.class);
        switch (position) {
            case 0:
                startActivity(new Intent(SupportMainActivity.this, UsingPickCActivity.class));
                //startActivity(new Intent(SupportMainActivity.this, SupportActivityCreatePickCAccount.class));
                return;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                tellUsActivityIntent.putExtra("pos",0);
                startActivity(tellUsActivityIntent);
                return;
            case 6:

                break;
            case 7:

                break;
            default:
                break;
        }
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return browseTopics.length;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_support_main_list_sub_layout, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.textView_browseTopics);
            textView.setText(browseTopics[position]);
            Log.d(TAG, "getView: " + browseTopics[position]);
            return view;
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + 10) + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
