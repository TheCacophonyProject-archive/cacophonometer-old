package com.thecacophonytrust.cacophonometer.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.thecacophonytrust.cacophonometer.http.PostField;
import com.thecacophonytrust.cacophonometer.http.PostFile;

public class PostDataObject {
	
	/**
	 * This object holds the required data for a HttpPost.
	 */
	private static final String LOG_TAG = "HttpPostDataObject.java";
	
	private List<PostField> postFields;
	private List<PostFile> postFiles;
	private URL url;
	
	public PostDataObject(){
		postFields = new ArrayList<>();
		postFiles = new ArrayList<>();
		Log.d(LOG_TAG, "PostDataObject created.");
	}
	
	public void setPostFields(List<PostField> postFileds){
		this.postFields = postFileds;
	}
	
	public void setPostFiles(List<PostFile> postFiles){
		this.postFiles = postFiles;
	}

	public void addPostFile(PostFile postFile){
		postFiles.add(postFile);
	}

	public void addPostField(PostField postField){
		postFields.add(postField);
	}
	
	public List<PostField> getPostFields(){
		return postFields;
	}
	
	public List<PostFile> getPostFiles(){
		return postFiles;
	}
	
	/**
	 * Takes a string and makes URL from it, if it throws a
	 * MalformedURLException when making the URL the method returns false
	 * 
	 * @param url
	 * @return
	 */
	public boolean setURL(String url){
		try {
			this.url = new URL(url);
			Log.d(LOG_TAG, "HttpPostDataObject created, url: '"+url+"'");
			return true;
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.toString());
			return false;
		}
	}
	
	public URL getURL(){
		return url;
	}

	@Override
	public String toString() {
		return "PostDataObject [postFields=" + postFields + ", postFiles=" + postFiles + ", url=" + url + "]";
	}
}
