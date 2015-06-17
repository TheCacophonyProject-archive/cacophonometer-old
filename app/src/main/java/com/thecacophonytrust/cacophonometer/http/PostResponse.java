package com.thecacophonytrust.cacophonometer.http;

import com.thecacophonytrust.cacophonometer.enums.PostErrorType;
import com.thecacophonytrust.cacophonometer.enums.ResponseType;


public class PostResponse {
	
	@Override
	public String toString() {
		return "PostResponse [response=" + response + ", errorType=" + errorType + ", responseCode=" + responseCode
				+ "]";
	}

	/**
	 * Object that is passed back after a post
	 */
	private ResponseType response;				//Response of server
	private PostErrorType errorType;				//Error Type, if there was one
	private int responseCode;				//response code from server
	
	PostResponse(String response, PostErrorType errorType, int responseCode){
		this.response = ResponseType.fromString(response);
		this.errorType = errorType;
		this.responseCode = responseCode;
	}
	
	public ResponseType getResponse() {
		return response;
	}

	public PostErrorType getErrorType() {
		return errorType;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
}
