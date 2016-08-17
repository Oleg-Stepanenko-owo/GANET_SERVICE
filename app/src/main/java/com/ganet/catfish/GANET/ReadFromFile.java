package com.ganet.catfish.GANET;

import android.content.Intent;
import android.os.Environment;

import com.ganet.catfish.ganet_service.GaNetService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by oleg on 25.07.2016.
 * this class need for emulate GaNet from stored logs in file.
 */
public class ReadFromFile extends Thread {
    private String fName;
    private String returnLineVal;
    private ParserGANET mParser;
    private GaNetManager gaNetManager;
    private boolean isActive;

    public ReadFromFile(String fileName, GaNetManager manager) {
        fName = fileName;
        gaNetManager = manager;
    }

    public void startRead( ParserGANET parserObj ) {
        mParser = parserObj;
        isActive = true;
        this.start();
    }

    public void getLine( ) {
        // synchronized (this) {
            mParser.parseLine(returnLineVal);
        // }
    }

    @Override
    public void run() {
        File file = new File( fName );
        StringBuilder text = new StringBuilder();
        long readedSize = 0;

        try {
            BufferedReader br = new BufferedReader( new FileReader(file) );
            while (isActive && ((returnLineVal = br.readLine()) != null)) {
                readedSize += returnLineVal.length();
                getLine();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                gaNetManager.updateReading( file.length(), readedSize);
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    public void stopRead() {
        isActive = false;
        this.interrupt();
    }
}
