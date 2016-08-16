package com.ganet.catfish.GANET.Data;

import com.ganet.catfish.GANET.MainGanetPKG;
import com.ganet.catfish.GANET.ParserGANET;
import com.ganet.catfish.ganet_service.GaNetService;

/**
 * Created by oleg on 13.08.2016.
 */
public class GeNetPkgText {
    private int allPack;
//    public int currPack;
    public boolean p0, p1, p2, p3;
    public String pS0, pS1, pS2, pS3;

    void GeNetPkgText() {
        allPack = 0;
//        currPack = 0;
        p0 = false;
        p1 = p0;
        p2 = p0;
        p3 = p0;
        pS0 = "";
        pS1 = "";
        pS2 = "";
        pS3 = "";
    }

    void setAllPack( int all ) {
        allPack = all;
        if( allPack == 2)       { p3 = true; }
        else if( allPack == 1 ) { p3 = true; p2 = true; }
        else if( allPack == 0 ) { p3 = true; p2 = true; p1 = true; }
    }

    public int getAllPack() {
        return allPack;
    }

    public void reset() {
        p0 = false;
        p1 = p0;
        p2 = p0;
        p2 = p0;
        pS0 = "";
        pS1 = "";
        pS2 = "";
        pS3 = "";
    }

    public boolean isReady() {
        return ( p0 && p1 && p2 && p3 );
    }

    public void updateInfo( String text, int currPack ) {
        if( currPack == 0 )         { pS0 = text; p0 = true; }
        else if( currPack == 1 )    { pS1 = text; p1 = true; }
        else if( currPack == 2 )    { pS2 = text; p2 = true; }
        else if( currPack == 3 )    { pS3 = text; p3 = true; }
    }

    public String getText() {
        String returnText = "";

        if( p0 && !pS0.isEmpty() ) returnText = pS0;
        else return returnText;

        if( p1 && !pS1.isEmpty() ) returnText += pS1;
        else return returnText;

        if( p2 && !pS2.isEmpty() ) returnText += pS2;
        else return returnText;

        if( p3 && !pS3.isEmpty() ) returnText += pS3;
        else return returnText;

        return returnText;
    }
}
