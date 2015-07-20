package com.thecacophonytrust.cacophonometer.http;


public class PostField {
	private String key = null;
	private String value = null;
	
	public PostField(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getKey(){
		return key;
	}

	public boolean isValid(){
		//TODO check that the field is valid
		return true;
	}
}
