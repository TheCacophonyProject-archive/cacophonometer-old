package com.thecacophonytrust.cacophonometer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.resources.VideoRules;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoRulesMenuActivity extends AppCompatActivity {

    private static final String LOG_TAG = "VideoRulesMenuActivity.java";

    private List<Integer> ruleKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_rules_menu);
        updateRules();
    }

    @Override
    public void onResume() {
        updateRules();
        Logger.d(LOG_TAG, "Resuming Video Rules menu activity");
        Update.now();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rules_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newRule() {
        Intent intent = new Intent(this, NewVideoRuleActivity.class);
        startActivity(intent);
    }

    public void updateRules() {
        ListView lv = (ListView) findViewById(R.id.listView1);
        ArrayList<String> list = new ArrayList<>();
        Map<Integer, VideoRules.DataObject> ruleMap = VideoRules.getRuleDOMap();
        ruleKeyList = new ArrayList<>();

        for (int key : ruleMap.keySet()) {
            list.add(ruleMap.get(key).getName());
            ruleKeyList.add(key);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.d(LOG_TAG, "Getting rule at position " + position);
                startRuleActivity(position);
            }
        });
    }

    public void startRuleActivity(int position) {
        Logger.d(LOG_TAG, ruleKeyList.toString());
        int ruleKey = ruleKeyList.get(position);
        Intent i = new Intent(this, ViewVideoRuleActivity.class);
        i.putExtra("RULE_KEY", ruleKey);
        //i.setAction(ruleKey);
        startActivity(i);
    }

    public void newRule(View v) {
        newRule();
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
