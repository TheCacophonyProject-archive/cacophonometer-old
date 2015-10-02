package com.thecacophonytrust.cacophonometer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.recording.RecordingUploadManager;
import com.thecacophonytrust.cacophonometer.rules.Rule;
import com.thecacophonytrust.cacophonometer.rules.RulesArray;
import com.thecacophonytrust.cacophonometer.util.Update;

public class RulesMenuActivity extends ActionBarActivity {

	private static final String LOG_TAG = "RulesMenuActivity.java";
	
	private List<Rule> ruleList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules_menu);
		updateRules();
	}

	@Override
	public void onResume() {
		updateRules();
		Log.d(LOG_TAG, "Resuming main activity");
		RecordingUploadManager.update();
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

	private void openRule(int ruleId) {
		Intent intent = new Intent(this, NewRuleActivity.class);
		intent.putExtra("ruleId", ruleId);
		startActivity(intent);
	}

	public void updateRules() {
		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayList<String> list = new ArrayList<>();
		Map<String, Rule> ruleMap = RulesArray.getRules();
		ruleList = new ArrayList<>();
		
		for (String key : ruleMap.keySet()) {
			list.add(ruleMap.get(key).toString());
			ruleList.add(ruleMap.get(key));
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(LOG_TAG, "Getting rule at postition " + position);
				startRuleActivity(position);
			}
		});
	}

	public void startRuleActivity(int position) {
		Log.d(LOG_TAG, ruleList.toString());
		Rule r = ruleList.get(position);
		Log.d(LOG_TAG, "Rule: " + r.toString());
		Intent i = new Intent(this, ViewRuleActivity.class);
		i.setAction(r.getName());
		startActivity(i);
	}

	public void newRule(View v) {
		openRule(-1);
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
