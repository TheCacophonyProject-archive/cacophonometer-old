package com.thecacophonytrust.cacophonometer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.resources.AudioRules;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.util.Update;

public class AudioRulesMenuActivity extends AppCompatActivity {

	private static final String LOG_TAG = "AudioRulesMenuActivity.java";
	
	private List<Integer> ruleKeyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules_menu);
		updateRules();
	}

	@Override
	public void onResume() {
		updateRules();
		Logger.d(LOG_TAG, "Resuming main activity");
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
		Intent intent = new Intent(this, NewAudioRuleActivity.class);
		startActivity(intent);
	}

	public void updateRules() {
		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayList<String> list = new ArrayList<>();
		Map<Integer, AudioRules.DataObject> ruleMap = AudioRules.getRuleDOMap();
		ruleKeyList = new ArrayList<>();
		
		for (int key : ruleMap.keySet()) {
			list.add(ruleMap.get(key).getName());
			ruleKeyList.add(key);
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Logger.d(LOG_TAG, "Getting rule at position " + position);
				startRuleActivity(position);
			}
		});
	}

	public void startRuleActivity(int position) {
		Logger.d(LOG_TAG, ruleKeyList.toString());
		int ruleKey = ruleKeyList.get(position);
		//Logger.d(LOG_TAG, "AudioRules.DataObject: " + r.toString());
		Intent i = new Intent(this, ViewAudioRuleActivity.class);
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
