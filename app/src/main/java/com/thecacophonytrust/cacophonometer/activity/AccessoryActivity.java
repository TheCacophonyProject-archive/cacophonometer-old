package com.thecacophonytrust.cacophonometer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.thecacophonytrust.cacophonometer.R;
import com.thecacophonytrust.cacophonometer.util.Logger;
import com.thecacophonytrust.cacophonometer.videoRecording.VideoCaptureService;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AccessoryActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Accessory.java";

    private static AccessoryActivity aa = null;

    private UsbAccessory usbAccessory;
    private UsbManager usbManager;
    private ParcelFileDescriptor parcelFileDescriptor;
    private FileInputStream inputStream;
    private OutputStream outputStream;

    private static final byte START_MOTION_RECORDING = 0x02;
    private static final byte CHARGING = 0x03;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessory);
        toast("Accessory Attached.");
        Intent intent = getIntent();
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            if (aa != null) aa.finish();
            aa = this;
            if (openAccessory())
                readMessage();
        } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
            if (VideoCaptureService.isMotionRecording())
                stopMotionRecording();
            closeAccessory();
        }

    }

    /**
     * Opens the accessory and generates the input and output streams.
     */
    private boolean openAccessory() {
        Logger.d(LOG_TAG, "Opening accessory.");
        parcelFileDescriptor = usbManager.openAccessory(usbAccessory);
        if (parcelFileDescriptor != null) {
            FileDescriptor fd = parcelFileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            toast("Opened accessory.");
            return true;
        } else {
            toast("Failed to open accessory.");
            return false;
        }
    }

    @Override
    public void onResume() {
        toast("Resuming ");
        super.onResume();
    }

    private void readMessage() {
        byte[] readBuffer = new byte[1];
        if(inputStream != null) {
            try {
                inputStream.read(readBuffer);

                toast("Res: " + Byte.toString(readBuffer[0]));
                if (readBuffer[0] == START_MOTION_RECORDING) {
                    Intent i = new Intent(getApplicationContext(), VideoCaptureService.class);
                    VideoCaptureService.setRule(-1);
                    startService(i);
                    makeDetachReceiver();
                }
            } catch (IOException e) {
                toast("Error with reading message from accessory.");
                Log.e(LOG_TAG, "Read failed", e);
            }
        }
    }

    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void closeAccessory() {
        try {
            if (parcelFileDescriptor != null)
                parcelFileDescriptor.close();
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Failed in closing accessory.");
            Logger.exception(LOG_TAG, e);
        } finally {
            parcelFileDescriptor = null;
            usbAccessory = null;
        }
    }

    private void makeDetachReceiver(){
        IntentFilter intentFilter = new IntentFilter(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(usbDisconnect, intentFilter);
    }

    private void stopMotionRecording() {
        toast("Stopping motion recording.");
        VideoCaptureService.stopMotionRecording();
    }

    private final BroadcastReceiver usbDisconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopMotionRecording();
            closeAccessory();
        }
    };
}
