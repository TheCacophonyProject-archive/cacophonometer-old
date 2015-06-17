package com.thecacophonytrust.cacophonometer.http;

public class PostFile {
	private String path = null;
	private String name = null;
	private String type = null;
	private String hash = null;
	private int size = -1;
	
	public PostFile(String path){
		this.path = path;
		//TODO set all other values
	}
	
	public String getPath() {
		return path;
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
	public int getSize() {
		return size;
	}
}
