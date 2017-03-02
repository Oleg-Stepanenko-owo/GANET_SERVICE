package com.ganet.catfish.GANET.Data;

import android.util.Log;

import com.ganet.catfish.GANET.MainGanetPKG;
import com.ganet.catfish.GANET.ParserGANET;

/**
 * Created by oleg on 25.07.2016.
 */
public class Track {
    public static final String TAG = "GaNetService";
//---------------------------------------------------
    private int folderId;
    private int subFolderId;
    private int trackId;
    public boolean selectedTrack;

    private GeNetPkgText infoPkg;

//-----------------------------------------------------

    public Track() {
        infoPkg = new GeNetPkgText();
    }

    /*
   need to parse active track info.
   Track NAME --------------------------------------------------------------------------------------------
< d 183 131
1E   684B3102	0377    0F 02	01 0F      30         02     		03       00300036005F004F0061007300690073   92
1E   684B3102	0377    0F 02	01 0F      30         02     		13       005F004C00690076006500200046006F   A2
1E   684B3102	0377    0F 03	01 0F      45         02     		33       00540068006500200057006F0072006C   F9
   Track pack | info |	folder|subfolder|track number|02-not select|pack/ALL|         Track name             |
                                                     12-select
    */

    /**
     * updateTrackInfo
     * @param data
     */
    public void updateTrackInfo( String data, MainGanetPKG.eExCommand extCommand ) {
        int textPos = 2;
        String valueCom;

//        Log.d( TAG, "updateTrackInfo. data[" + data + "]; extCommand: " + extCommand );

        if( extCommand == MainGanetPKG.eExCommand.eINFO ) {
            valueCom = data.substring( textPos, (textPos +=2) );
            folderId = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, (textPos +=2) );
            subFolderId = Integer.valueOf(valueCom);

            valueCom = data.substring( (textPos+=2), (textPos +=2) );
            trackId = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, (textPos +=2) );
            if( valueCom.getBytes()[0] == '0' ) selectedTrack = false;
            else selectedTrack = true;

            valueCom = data.substring( textPos, (textPos +=1) );
            int currPack = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, (textPos +=1) );
            infoPkg.setAllPack( Integer.valueOf(valueCom) );

            valueCom = data.substring( textPos, data.length() );
//            Log.d( TAG, "updateTrackInfo(updateNameTrack). valueCom[" + valueCom + "]; currPack: " + currPack );
            updateNameTrack(valueCom, currPack, false);
        }
    }

    /**
     * updateNameTrack
     * @param text
     */
    private void updateNameTrack( String text, int currPack, boolean isASCII ) {
        String trackTextTmp = ParserGANET.getString( text, isASCII, true );
        infoPkg.updateInfo( trackTextTmp, currPack );
    }

    /**
     * getTrackId
     * @return
     */
    public Integer getTrackId(){
        return Integer.valueOf( trackId );
    }

    /**
     * trackMarge
     * @param tempParseTrack
     */
    public void trackMarge(Track tempParseTrack) {
        if( trackId == tempParseTrack.getTrackId().intValue() )
        {
            if( tempParseTrack.infoPkg.p0 && !infoPkg.p0 ) {
                infoPkg.p0 = true;
                infoPkg.pS0 = tempParseTrack.infoPkg.pS0;
            }
            if( tempParseTrack.infoPkg.p1 && !infoPkg.p1 ) {
                infoPkg.p1 = true;
                infoPkg.pS1 = tempParseTrack.infoPkg.pS1;
            }
            if( tempParseTrack.infoPkg.p2 && !infoPkg.p2 ) {
                infoPkg.p2 = true;
                infoPkg.pS2 = tempParseTrack.infoPkg.pS2;
            }
            if( tempParseTrack.infoPkg.p3 && !infoPkg.p3 ) {
                infoPkg.p3 = true;
                infoPkg.pS3 = tempParseTrack.infoPkg.pS3;
            }
        }
    }

    /**
     * getName
     * @return
     */
    public String getName() {
        String returnVal = infoPkg.getText();
        Log.d( TAG, "TrackInfoName: " + infoPkg.getText() );
        return returnVal;
    }

    public int getFolderId() {
        return folderId;
    }
}
