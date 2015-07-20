package com.thecacophonytrust.cacophonometer.http;

import android.util.Log;

import java.io.File;

public class PostFile {

	private static final String LOG_TAG = "PostFile.java";

	private File file = null;
	private String name = null;
	private String type = null;
	private String hash = null;
	private long length = -1;
	
	public PostFile(File file){
		if (file.exists()){
			this.file = file;
			name = file.getName();
			type = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
			length = file.length();
		} else {
			Log.e(LOG_TAG, "File not found at: " + file.getAbsolutePath());
		}
	}
	
	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getHash() {
		return hash;
	}

	public long getLength() {
		return length;
	}
}
