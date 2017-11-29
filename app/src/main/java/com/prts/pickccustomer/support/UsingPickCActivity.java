package com.prts.pickccustomer.support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.prts.pickccustomer.R;

public class UsingPickCActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "UsingPickCAct";
    String[] browseTopics = {"PICK C App", "Getting Started", "Cargo Fares"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_using_pick_c);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter());
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: " + browseTopics[position]);
        switch (position) {
            case 0:
                //startActivity(new Intent(SupportMainActivity.this, SupportActivityCreatePickCAccount.class));
                return;
            case 1:

                break;
            case 2:

                break;
            case 3:

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
}
