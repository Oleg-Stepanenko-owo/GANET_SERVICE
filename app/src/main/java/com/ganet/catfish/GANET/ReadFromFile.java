package com.ganet.catfish.GANET;

import android.os.Environment;

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

    public ReadFromFile(String fileName, GaNetManager manager) {
        fName = fileName;
        gaNetManager = manager;
    }

    public void startRead( ParserGANET parserObj ) {
        mParser = parserObj;
        this.start();
    }

    public void getLine( ) {
        synchronized (this) {
            mParser.parseLine(returnLineVal);
        }
    }

    @Override
    public void run() {
        File file = new File( fName );
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader( new FileReader(file) );
            while ((returnLineVal = br.readLine()) != null) {
                getLine();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }
}
