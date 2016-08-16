package com.ganet.catfish.ganet_service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ganet.catfish.GANET.GaNetManager;
import com.ganet.catfish.GANET.ParserGANET;
import com.ganet.catfish.GANET.ReadFromFile;

import java.lang.ref.WeakReference;
import java.util.Set;

public class GaNetService extends Service {

    public static final String TAG = "GaNetService";

    public static final String SERVICESTART = "com.ganet.catfish.ganet_service.start";
    public static final String SERVICESTATUS_REQ = "com.ganet.catfish.ganet_service.status_req";
    public static final String SERVICESTATUS_RES = "com.ganet.catfish.ganet_service.status_res";
    public static final String READFILE = "com.ganet.catfish.ganet_service.readfile";
    public static final String DISKID = "com.ganet.catfish.ganet_service.diskid";
    public static final String ACTIVETR = "com.ganet.catfish.ganet_service.activetr";
    public static final String TRACKINFO = "com.ganet.catfish.ganet_service.track";
    public static final String FOLDERINFO = "com.ganet.catfish.ganet_service.folderinfo";
    public static final String TIMEINFO = "com.ganet.catfish.ganet_service.timeinfo";
    public static final String EJECTDISK = "com.ganet.catfish.ganet_service.ejectdisk";
    public static final String INSERTTRACK = "com.ganet.catfish.ganet_service.inserttrack";
    public static final String VOLUMEINFO = "com.ganet.catfish.ganet_service.volumeinfo";
    public static final String RADIOINFO = "com.ganet.catfish.ganet_service.radio";
    public static final String STARTCDINFO_REQ = "com.ganet.catfish.ganet_service.startsdreq";
    public static final String STARTCDINFO_RES = "com.ganet.catfish.ganet_service.startsdres";

    private MyHandler mHandler;
    private UsbCom usbService;
    private GaNetManager mGANET;
    private ReadFromFile readFileObj;
    private int currActiveTrack;
    private int currActiveDisk;

    boolean isStart = false;
    BroadcastReceiver br;


    //--------------------- USB SERIAL ------------------------------------------------------
    /*
    * Notifications from UsbService will be received here.
    */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbCom.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbCom.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbCom.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbCom.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbCom.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbCom.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbCom.ACTION_NO_USB);
        filter.addAction(UsbCom.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbCom.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbCom.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    public void invalidate(ParserGANET.eParse activeParseID) {
        Intent in;

        switch ( activeParseID ){
            case eActiveTr:
                currActiveDisk = mGANET.mActiveTrack.diskID;
                currActiveTrack = mGANET.mActiveTrack.trackId;

                in = new Intent(ACTIVETR);
                in.putExtra("diskID", mGANET.mActiveTrack.diskID);
                in.putExtra("playTrackTime", mGANET.mActiveTrack.getPlayTime());
                in.putExtra("folderId", mGANET.mActiveTrack.folderId);
                in.putExtra("trackId", mGANET.mActiveTrack.trackId);
                in.putExtra("playTrackName", !mGANET.mActiveTrack.getTrackName().isEmpty() ?
                        mGANET.mActiveTrack.getTrackName() : mGANET.getTrackById(mGANET.mActiveTrack.trackId) );
                in.putExtra("playAlbome", mGANET.mActiveTrack.getAlbomeName() );

                Log.d(TAG, "Disk#" + String.valueOf(mGANET.mActiveTrack.diskID)
                        + "; Track#" + String.valueOf(mGANET.mActiveTrack.trackId )
                        + " " + mGANET.mActiveTrack.getTrackName()
                        + "; Time: " + mGANET.mActiveTrack.getPlayTime() );
                sendBroadcast(in);
                break;
            case eTr:
                break;
            case eFolder:
                break;
            case eTime:
                in = new Intent(TIMEINFO);
                in.putExtra("Time", mGANET.mDevTime.getTime());
                Log.d( TAG, "Time:" + mGANET.mDevTime.getTime() );
                sendBroadcast(in);
                break;
            case eEjectDisk:
                break;
            case eInsertTrack:
                Log.d( TAG, "Start Play track" + mGANET.mActiveTrack.trackId );
                updateDiskInfo();
                break;
            case eVolume:
                break;
            case eRadio:
                in = new Intent(RADIOINFO);
                in.putExtra( "radioCommand", mGANET.mRadio.mCurrRAction.ordinal() );
                in.putExtra( "radioType", mGANET.mRadio.mRadioType.ordinal() );
                in.putExtra( "radioFr", mGANET.mRadio.mFrequency );
                in.putExtra( "radioStoreId", mGANET.mRadio.mStoreID );
                in.putExtra( "radioQuality", mGANET.mRadio.mRQuality );
                sendBroadcast(in);
                break;
            case eNone:
                break;
        }
    }

    private void updateDiskInfo() {
        if( currActiveDisk != mGANET.mActiveTrack.diskID ){
            mGANET.mTrack.clear();
            mGANET.mActiveTrack.clear();
            currActiveDisk = mGANET.mActiveTrack.diskID;
            currActiveTrack = mGANET.mActiveTrack.trackId;
        } else if ( currActiveTrack != mGANET.mActiveTrack.trackId ){
            mGANET.mActiveTrack.clear();
            currActiveTrack = mGANET.mActiveTrack.trackId;
        }
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        static int iPK = 0;
        private final WeakReference<GaNetService> mGaNetService;

        public MyHandler(GaNetService activity) {
            mGaNetService = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbCom.MESSAGE_FROM_SERIAL_PORT:
                    final String data = (String) msg.obj;
                    // mActivity.get().vtComLog.append(String.valueOf(iPK++) + ":" + data);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            mGaNetService.get().mFileLog.writeFile( data );
//                            mGaNetService.get().mGANET.mParser.parseLine(data);
                        }
                    }).start();

                    break;
                case UsbCom.CTS_CHANGE:
                    Toast.makeText(mGaNetService.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbCom.DSR_CHANGE:
                    Toast.makeText(mGaNetService.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbCom.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbCom.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
//------------------------------------------------------------------------------------------------

    public GaNetService() {
        currActiveTrack = 0;
        currActiveDisk = 0;
        mHandler = new MyHandler(this);
    }

    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();

        filter.addAction(SERVICESTART);
        filter.addAction(SERVICESTATUS_REQ);
        filter.addAction(READFILE);
        filter.addAction(STARTCDINFO_REQ);

        setFilters();  // Start listening notifications from UsbService

        br = new BroadcastReceiver() {
            // действия при получении сообщений
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent in;
                switch (intent.getAction()) {
                    case SERVICESTART:
                        if(!isStart) {
                            startUSBList();
                        }
                        break;
                    case SERVICESTATUS_REQ:
                        in = new Intent(SERVICESTATUS_RES);
                        in.putExtra("Start", isStart);
                        in.putExtra("Data", isStart ? "Service start." : "Service stop." );
                        sendBroadcast(in);
                        break;
                    case READFILE:
                        readFileObj = new ReadFromFile(intent.getStringExtra("StartRead"), mGANET );
                        readFileObj.startRead( mGANET.getParser() );
                        break;
                    case STARTCDINFO_REQ:
                        in = new Intent(STARTCDINFO_RES);
                        in.putExtra("playTrackTime", mGANET.mActiveTrack.getPlayTime() );
                        in.putExtra("diskID", mGANET.mActiveTrack.diskID );
                        in.putExtra("trackId", mGANET.mActiveTrack.trackId );
                        in.putExtra("playTrackName", mGANET.getTrackById(mGANET.mActiveTrack.trackId) );
                        in.putExtra("playArtist", "");
                        sendBroadcast(in);
                        break;
                }
            }
        };

        registerReceiver(br, filter);
        startUSBList();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private void startUSBList() {
        if( !isStart ) {
            isStart = true;
            startService(UsbCom.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
            mGANET = new GaNetManager( this );
        }

        Intent in = new Intent(SERVICESTATUS_RES);
        in.putExtra("Start", isStart);
        in.putExtra("Data", isStart ? "Service start." : "Service stop." );
        sendBroadcast(in);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
