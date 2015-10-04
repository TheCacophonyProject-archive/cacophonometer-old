package com.thecacophonytrust.cacophonometer.recording;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class PlayRecording {

	/**
	 * This class deals with playing the recording on the device.
	 */

	final static String LOG_TAG = "PlayRecording.java";
	
	static MediaPlayer mp = new MediaPlayer();
	
	private static RecordingDataObject playingRDO;


	/**
	 * Starts playing a recording.
	 * The recording that is played is given by the RecordingDataObject that is passed to the method.
	 * The recording can be stopped by calling PlayRecording.stop().
	 * If it is called to play a recording that is already playing then it will stop playing it.
	 * If is is called to play a recording while already playing a recording,
	 * previous recording will stop playing and new one will start playing.
	 * A toast is also displayed saying what it is doing.
	 *
	 * @param rdo RecordingDataObject that has data for the recording.
	 * @param context context to use for displaying a toast on play status.
	 * @return true if recording was found and started playing.
	 */
	public static boolean play(RecordingDataObject rdo, Context context){
        /*
		File recording = new File(rdo.getRecordingFilePath());
		
		if (!recording.exists() || recording.isDirectory()){
			Log.d(LOG_TAG, "Error with file: " + recording.getPath());
			if (!recording.exists() || recording.isDirectory())
				return false;
		}
		
		if (mp.isPlaying()){
			//Stopping playing recording
			mp.stop();
			//Break out of method if was playing this recording playing
			Toast.makeText(context, "Stopping playing recording", Toast.LENGTH_SHORT).show();
			if (rdo == playingRDO){
				playingRDO = null;
				return true;
			}
		}
		try{
			mp = new MediaPlayer();
			mp.setDataSource(recording.getPath());
			mp.prepare();
			mp.start();
			playingRDO = rdo;
			Toast.makeText(context, "Playing recording", Toast.LENGTH_SHORT).show();
			
		} catch (Exception e){
			Log.e(LOG_TAG, e.toString());
			return false;
		}
		*/
		return true;
	}

	/**
	 * Stops the device from playing a recording
	 */
	public static void stop(){
		if (isPlaying())
			mp.stop();
	}

	/**
	 * Returns if the device is playing a recording.
	 * @return true if playing recording, false if not.
	 */
	public static boolean isPlaying(){
		return mp.isPlaying();
	}
}
