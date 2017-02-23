package com.ganet.catfish.GANET;

import android.os.Environment;
import android.util.Log;

import com.ganet.catfish.ganet_service.GaNetService;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;

/**
 * Created by oleg on 31.07.2016.
 */
public class LogToFile  {
    private String FILENAME = "myLOG";
    private final GaNetService gaService;
    private boolean redyToWrite = false;
    public LogToFile( GaNetService gaService ) {

        this.gaService = gaService;

//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
            redyToWrite = true;
            FILENAME = "myLOG.txt";
//        }

    }

    public void writeFile(String data) {
        if( gaService.ismLogWrite() && redyToWrite ){
            try {
                // отрываем поток для записи
                String currTime = String.valueOf(System.currentTimeMillis()) + ":";
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( gaService.openFileOutput(FILENAME, gaService.MODE_APPEND)));
                //CharSequence ch = data.subSequence(0, data.length());
                bw.write( currTime + data );
                bw.close();
//            Log.d(LOG_TAG, "Файл записан");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
