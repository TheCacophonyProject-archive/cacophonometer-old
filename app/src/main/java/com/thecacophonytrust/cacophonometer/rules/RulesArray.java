package com.thecacophonytrust.cacophonometer.rules;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.rules.Rule;

public class RulesArray {

	/**
	 * Organises and holds all the rules in an array.
	 */

	private static final String LOG_TAG = "Rules.java";
	
	static Map<String, Rule> ruleMap = new HashMap<String, Rule>();

	/**
	 * Adds a rule to be added to teh rule array.
	 * @param rule to be added.
	 */
	static public void addRule(Rule rule){
		if (ruleMap.containsKey(rule.getName())){
			if (rule.equal(ruleMap.get(rule.getName())))
				Log.e(LOG_TAG, "Trying to add the same rule twice.");
			else {
				Log.e(LOG_TAG, String.format("Rule allready exists under name '%s'", rule.getName()));
				//TODO rename rule and.
			}
		} else {
			ruleMap.put(rule.getName(), rule);
			Log.d(LOG_TAG, "adding rule, name: " + rule.getName());
		}
	}

	/**
	 * Removes rule from the rule array.
	 * @param rule to be removed.
	 * @return true if rule was removed, false if not.
	 */
	static public boolean removeRule(Rule rule){
		if (ruleMap.containsKey(rule.getName())){
			ruleMap.remove(rule.getName());
			Log.d(LOG_TAG, "removing rule: " + rule.getName());
			Log.d(LOG_TAG, "ruleList:" + ruleMap.toString());
			return true;
		}
		else 
			return false;
	}

	/**
	 * Gets the array of rules.
	 * @return array of rules.
	 */
	static public Map<String, Rule> getRules(){
		return ruleMap;
	}

	/**
	 * Goes through rules and finds what rule is next for recording, done by time of recording.
	 * @return next rule to be recorded.
	 */
	static public Rule getNextRule(){
		Log.d(LOG_TAG, "Finding next Rule");
		if (ruleMap.size() == 0) return null;
		Rule nextRule = null;
		for (String ruleName : ruleMap.keySet()){
			if (nextRule == null){
				nextRule = ruleMap.get(ruleName);
			} else if (nextRule.nextRecordingTime().after(ruleMap.get(ruleName).nextRecordingTime()))
				nextRule = ruleMap.get(ruleName);
		}
		return nextRule;
	}

	/**
	 * Logs (data) all the rules that are loaded in the rules array.
	 */
	public static void printRules(){
		String result = "Rules, total: " + ruleMap.size() + ", list of rules:\n";
		for (String r : ruleMap.keySet()){
			result += String.format(" [%s]\n", ruleMap.get(r));
		}
		Log.d(LOG_TAG, result);
	}

	/**
	 * Returns the rule that has the matching name.
	 * @param name of rule.
	 * @return rule that has the given name.
	 */
	public static Rule getByName(String name) {
		Rule r = ruleMap.get(name);
		if (r == null){
			Log.e(LOG_TAG, "No rule found by the name '"+name+"'");
			return null;
		} else
			return r;
	}
}
