package com.ganet.catfish.GANET.Data;

import android.util.Log;

import com.ganet.catfish.GANET.MainGanetPKG;
import com.ganet.catfish.GANET.ParserGANET;

/**
 * Created by oleg on 25.07.2016.
 * current play active track
 */
public class ActiveTrack {

    public int diskID;
    public int playMin;
    public int playSec;
    public int folderId;
    public int trackId;
    private String albomeName;
    private String artistName;
    private String trackName;
    private String actFileName;
    private String actArtistName;

    private GeNetPkgText albomPkg;
    private GeNetPkgText trackNamePkg;
    private GeNetPkgText fileNamePkg;
    private GeNetPkgText artistNamePkg;

    public static final String TAG = "GaNetService";

    public ActiveTrack() {
        diskID = -1;
        playMin = 0;
        playSec = 0;
        folderId = -1;
        trackId = -1;
        albomeName = "";
        artistName = "";
        trackName = "";
        actFileName = "";
        actArtistName = "";

        albomPkg = new GeNetPkgText();
        trackNamePkg = new GeNetPkgText();
        fileNamePkg = new GeNetPkgText();
        artistNamePkg = new GeNetPkgText();
    }

    /**
     20 684B310203   	00     	F3    	60   	FF  F020    0F 03 0F    46    FFFFFFFF  2130 0F 03 0F 19002030200101  C5
     20 684B310203   	00     	F3    	60   	FF  F003    0F 01 0F    10    FFFFFFFF  2130 0F 03 0F 18002030200101  6F
     20	684B310203		00		F5		60		FF	F235	0F 00 0F	02	  FFFFFFFF	2130 0F 00 0F 03002030200000  7C
     20	684B310203		00		F5		60		FF	F236	0F 00 0F	02	  FFFFFFFF	2130 0F 00 0F 03002030200000  7D
     20	684B310203		00		F3		60		FF	F000	0F 02 0F    43	  FFFFFFFF	2130 0F	03 0F 19002030200101  A1
        Track pack |     play | DISK |        	  | TIME  |  folder | Track|
                                                    FFFF - isert
                                                    F000 - start play
     <GA:183131684B31020300 F360FFF0000F010F01FFFFFFFF21300F130F03002030200104  5B>
     <GA:183131684B31020300 F360FFF0090F010F01FFFFFFFF21300F130F03002030200104  64>
     <GA:183131684B31020300 F360FFF1150F010F01FFFFFFFF21300F130F03002030200104  71>
     *
     * @param data
     */
    public void updateActiveTrackInfo( String data, MainGanetPKG.eExCommand extCommand ) {
        int textPos = 1;
        int currPack = 0;

        String valueCom;
        //DISK INFO --------------------
        if( extCommand == MainGanetPKG.eExCommand.ePLAY ||
                extCommand == MainGanetPKG.eExCommand.eBeforeTrackPlay) {
            valueCom = data.substring(textPos, (textPos += 1));
            diskID = Integer.valueOf(valueCom);

            valueCom = data.substring(textPos += 4, textPos += 2);
            valueCom = valueCom.replace("F", "0");
            playMin = Integer.valueOf(valueCom);

            valueCom = data.substring(textPos, textPos += 2);
            valueCom = valueCom.replace("F", "0");
            playSec = Integer.valueOf(valueCom);

            valueCom = data.substring(textPos += 2, textPos += 2);
            valueCom = valueCom.replace("F", "0");
            folderId = Integer.valueOf(valueCom);

            valueCom = data.substring(textPos += 2, textPos += 2);
            valueCom = valueCom.replace("F", "0");
            if( trackId != Integer.valueOf(valueCom) ){
                albomPkg.reset();
                trackNamePkg.reset();
                fileNamePkg.reset();
                artistNamePkg.reset();
            }
            trackId = Integer.valueOf(valueCom);
        } else  if( extCommand == MainGanetPKG.eExCommand.eACTALBOMENAME ) {
            boolean isAscii = false;
            textPos = 1;

            valueCom = data.substring( textPos, (textPos +=1) );
            isAscii = (Integer.valueOf(valueCom).intValue() == 4 ? true: false) ;

            valueCom = data.substring( textPos, (textPos +=1) );
            currPack = Integer.valueOf(valueCom).intValue();

            valueCom = data.substring( textPos, (textPos +=1) );
            albomPkg.setAllPack( Integer.valueOf(valueCom).intValue() );

            valueCom = data.substring( textPos, data.length() );
            updateAlbome(valueCom, currPack, isAscii);
        } else  if( extCommand == MainGanetPKG.eExCommand.eACTTRACKNAME1 ) {
            boolean isAscii = false;
            textPos = 1;

            valueCom = data.substring( textPos, (textPos +=1) );
            isAscii = (Integer.valueOf(valueCom).intValue() == 4 ? true : false) ;

            valueCom = data.substring( textPos, (textPos +=1) );
            currPack = Integer.valueOf(valueCom).intValue();

            valueCom = data.substring( textPos, (textPos +=1) );
            trackNamePkg.setAllPack( Integer.valueOf(valueCom).intValue() );

            valueCom = data.substring( textPos, data.length() );
            updateTrackName(valueCom, currPack, isAscii);
        } else  if( extCommand == MainGanetPKG.eExCommand.eACTFILENAME ) {
            boolean isAscii = true;
            textPos = 1;
            valueCom = data.substring( textPos, (textPos +=1) );
            isAscii = (Integer.valueOf(valueCom).intValue() == 2 ? false : true) ;

            valueCom = data.substring( textPos, (textPos +=1) );
            currPack = Integer.valueOf(valueCom).intValue();

            valueCom = data.substring( textPos, (textPos +=1) );
            fileNamePkg.setAllPack( Integer.valueOf(valueCom).intValue() );

            valueCom = data.substring( textPos, data.length() );
            updateActFileName(valueCom, currPack, isAscii);
        } else  if( extCommand == MainGanetPKG.eExCommand.eACTARTISTNAME ) {
            boolean isAscii = false;
            textPos = 1;
            valueCom = data.substring( textPos, (textPos +=1) );
            isAscii = (Integer.valueOf(valueCom).intValue() == 4 ? true : false) ;

            valueCom = data.substring( textPos, (textPos +=1) );
            currPack = Integer.valueOf(valueCom).intValue();

            valueCom = data.substring( textPos, (textPos +=1) );
            artistNamePkg.setAllPack( Integer.valueOf(valueCom).intValue() );

            valueCom = data.substring( textPos, data.length() );
            updateActArtistName(valueCom, currPack, isAscii);
        } else if ( extCommand == MainGanetPKG.eExCommand.eLOAD ) {
            clear();
            valueCom = data.substring(textPos, (textPos += 1));
            diskID = Integer.valueOf(valueCom);
        }
    }

    private void updateActArtistName(String valueCom, int currPack, boolean isAscii) {
        String artistTextTmp = ParserGANET.getString( valueCom, isAscii, false );
        artistNamePkg.updateInfo(artistTextTmp, currPack);
        artistName = artistNamePkg.getText();

        Log.d(TAG,"IS ready:" + artistNamePkg.isReady() + "; ARTIST: " + artistName );
    }

    private void updateActFileName(String valueCom, int currPack, boolean isAscii) {
        String fileTextTmp = ParserGANET.getString( valueCom, isAscii, true );
        fileNamePkg.updateInfo(fileTextTmp, currPack);
        actFileName = fileNamePkg.getText();

        Log.d(TAG,"Is ready:" + fileNamePkg.isReady() + "; Active file name: " + actFileName );
    }

    /**
     *
     * @param valueCom
     */
    private void updateTrackName(String valueCom, int currPack, boolean isASCII) {
        String trackTextTmp = ParserGANET.getString( valueCom, isASCII, false );
        trackNamePkg.updateInfo(trackTextTmp, currPack);
        trackName = trackNamePkg.getText();
        Log.d(TAG, "Is ready:" + trackNamePkg.isReady() + "; Active track name: " + trackName );
    }

    /**
     *
     * @param valueCom
     */
    private void updateAlbome(String valueCom, int currPack, boolean isASCII) {
        String trackTextTmp = ParserGANET.getString( valueCom, isASCII, false );
        albomPkg.updateInfo( trackTextTmp, currPack );

        albomeName =  albomPkg.getText();
        Log.d(TAG, "Is ready:" + albomPkg.isReady() + "; Active albome name: " + albomeName );
    }

    /**
     *
     * @return
     */
    public String getPlayTime() {
        String returnSTR = "00:00";
            String sMin = String.format( "%02d" , playMin);
            String sSec = String.format( "%02d" , playSec);
            returnSTR = ( sMin + ":" + sSec );
        return returnSTR;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbomeName() {
        return albomeName;
    }

    public void clear() {
        artistName = "";
        albomeName = "";
        trackName = "";
        fileNamePkg.reset();
        artistNamePkg.reset();
        trackNamePkg.reset();
        albomPkg.reset();
        folderId = -1;
        playMin = 0;
        playSec = 0;
        diskID = -1;
        trackId = -1;
        actFileName = "";
        actArtistName = "";
    }
}
