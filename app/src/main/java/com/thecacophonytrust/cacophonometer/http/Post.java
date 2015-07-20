package com.thecacophonytrust.cacophonometer.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.thecacophonytrust.cacophonometer.enums.PostErrorType;

import android.os.AsyncTask;
import android.util.Log;

public class Post extends AsyncTask<PostDataObject, Integer, PostResponse>{
	/**
	 * This class deals with all post requests. It uses a HttpPostDataObject to get the fields and files to sends and returns the response.
	 */
	private static final String LOG_TAG = "Post.java";
	private static final String LINE_END = "\r\n";
	private static final String TWO_HYPHENS = "--";
	
	private String response;				//Response of server
	private PostErrorType errorType;				//Error Type, if there was one
	private int responseCode;				//response code from server
	
	private PostResponse postResponse = null;
	
	protected PostResponse doInBackground(PostDataObject... httpPostDataObjects){
		if (httpPostDataObjects.length != 1){
			errorType = PostErrorType.INVALID_NUMBER_OF_POST_DATA_OBJECTS;
			Log.e(LOG_TAG, errorType.toString());
			return new PostResponse(null, errorType, 0);
		} else {
			PostDataObject pdo = httpPostDataObjects[0];
			Log.d(LOG_TAG, "Starting post: " + pdo);
			startPost(pdo.getPostFields(), pdo.getPostFiles(), pdo.getURL());
			checkServerResponse();
			postResponse = new PostResponse(response, errorType, responseCode);
			return postResponse;
		}
		
	}
	
	protected void onProgressUpdate(Integer... progress){
		
	}

	protected void onPostExecute(Integer result){
	}
	
	private void checkServerResponse(){
		if (response == null)
			errorType = PostErrorType.NO_RESPONSE_FOUND;
		if (responseCode != 200){
			Log.d(LOG_TAG, "Invalid response code of: "+ responseCode);
			errorType = PostErrorType.INVALID_RESPONSE_CODE;
		}
			
	}
	
	
	private boolean startPost(List<PostField> postFields, List<PostFile> postFiles, URL url){
		Log.d(LOG_TAG, "Starting post");
		Log.d(LOG_TAG, "URL: " + url);
		String boundary = Long.toHexString(System.currentTimeMillis());
		try {
			URLConnection urlConn = url.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);

			urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			//urlConn.getOutputStream();

			DataOutputStream outputStream = new DataOutputStream (urlConn.getOutputStream ());
			for (int i = 0; postFields != null && i < postFields.size(); i++) {
				Log.d(LOG_TAG, "Adding post field.");
				String key = postFields.get(i).getKey();
				String value = postFields.get(i).getValue();
				outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\""
								+ key + "\"" + LINE_END);
				outputStream
						.writeBytes("Content-Type: text/plain; charset=UTF-8"
								+ LINE_END);
				outputStream.writeBytes(LINE_END);
				outputStream.writeBytes(value + LINE_END);
			}
			
			for (int i = 0; postFiles != null && i < postFiles.size(); i++) {
				Log.d(LOG_TAG, "Adding post file.");
				File f = postFiles.get(i).getFile();
				if (!f.exists())
					Log.e(LOG_TAG, "Recording not found \""+ f.getAbsolutePath() +"\"");
				outputStream.writeBytes(TWO_HYPHENS + boundary + LINE_END);
				outputStream.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\"file.txt\""+ LINE_END); //TODO change file name etc...
				//outputStream.writeBytes("Content-Type: text/plain; charset=UTF-8"+ LINE_END);
				outputStream.writeBytes(LINE_END);
				FileInputStream fileInputStream = new FileInputStream(f);
				int bytesRead, bytesAvailable, bufferSize;
				int maxBufferSize = 1 * 1024 * 1024;
				bytesAvailable = fileInputStream.available();
		        bufferSize = Math.min(bytesAvailable, maxBufferSize);
		        byte[] buffer;
		        buffer = new byte[bufferSize];
		        // read file and write it into form...
		        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		        while (bytesRead > 0) {

		        	outputStream.write(buffer, 0, bufferSize);
		            bytesAvailable = fileInputStream.available();
		            bufferSize = Math.min(bytesAvailable, maxBufferSize);
		            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		        }

			}

			outputStream.writeBytes(LINE_END + TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_END);
			outputStream.flush();
			outputStream.close();
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
			responseCode = ((HttpURLConnection) urlConn).getResponseCode();
			Log.d(LOG_TAG, "Response code: " + responseCode);
			response = "";
			String line;
			try {
				while ((line = inputStream.readLine()) != null){
					response += line;
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, e.toString());
			}
			errorType = PostErrorType.NO_ERROR;
			
			return true;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.toString());
			errorType = PostErrorType.COULD_NOT_CONNECT_TO_SERVER;
			postResponse = new PostResponse(null, errorType, 0);
			return false;
		}
	}
}
