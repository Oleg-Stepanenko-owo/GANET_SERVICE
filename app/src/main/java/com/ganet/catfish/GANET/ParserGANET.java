package com.ganet.catfish.GANET;

import android.util.Log;

import com.ganet.catfish.GANET.Data.ActiveTrack;
import com.ganet.catfish.GANET.Data.DevTime;
import com.ganet.catfish.GANET.Data.Folder;
import com.ganet.catfish.GANET.Data.RadioAction;
import com.ganet.catfish.GANET.Data.Track;
import com.ganet.catfish.GANET.Data.Volume;

import java.util.Map;
import java.util.Vector;

/**
 * Created by oleg on 25.07.2016.
 */
public class ParserGANET {
    public static final String TAG = "GaNetService";

    static public enum eParse{
        eActiveTr,
        eTr,
        eFolder,
        eTime,
        eEjectDisk,
        eInsertTrack,
        eVolume,
        eRadio,
        eNone
    }
    private GaNetManager mGaNET;

    private String receivedLine;
    private ActiveTrack mActiveTrack;
    private Folder mFolder;
    private Map<Integer, Track> mTrack;
    private DevTime mDevTime;
    private Volume mVol;
    private RadioAction mRadio;

    private eParse activeParseID;

    private String srcDev, dstDev;
    private String commandDev;
    private String exCommand;
    private String dataDev;
    private int activeDiskID;

    private String buffer;
    private Vector<String> vGANETCommand;
    private boolean startAdd;

    /**
     *
     * @param mGaNET
     */
    ParserGANET( GaNetManager mGaNET ){
        this.mGaNET = mGaNET;
//            ActiveTrack activeTrack, Folder folder, DevTime devTime, Map<Integer, Track> track, Volume mVol, RadioAction mRadio) {
        vGANETCommand = new Vector<String>();
        mTrack = mGaNET.mTrack;
        mDevTime = mGaNET.mDevTime;
        mActiveTrack = mGaNET.mActiveTrack;
        mFolder = mGaNET.mFolder;
        this.mVol = mGaNET.mVol;
        this.mRadio = mGaNET.mRadio;
        buffer = "";
    }

    public void parseLine( String lineTmp ) {
        extractGaNetLine( lineTmp );
        for( int a = 0; a < vGANETCommand.size(); a++ ) {
            String line = vGANETCommand.get(a);
            activeParseID = eParse.eNone;
            // <GA:183 100 600D01000140 1517 0000CD> - TIME
            if( (line.indexOf("<GA:") != -1) && (line.indexOf(">") != -1) ){
                int chPos = 4;
                srcDev = line.substring( chPos, chPos += 3 );
                dstDev = line.substring( chPos, chPos += 3 );

                // TIME
                if( line.indexOf("600D01000140", chPos) != -1 ) {
                    commandDev = line.substring( chPos, chPos += 12 );
                    dataDev = line.substring( chPos, chPos += 4 );
                    mDevTime.setDevTime( dataDev );
                    activeParseID = eParse.eTime;
                }
                // CD\DVD DISK
                else if( (line.indexOf("684B3102") != -1) && dstDev.equals("131") )
                {
                    commandDev = line.substring( chPos, chPos += 8 );
                    exCommand = line.substring( chPos, chPos += 4 );
                    int endPos = (line.length() - 3); //without 2last symbols (CRS)
                    dataDev = line.substring( chPos, endPos );

                    MainGanetPKG.eExCommand parseExCommand = getExCommand(exCommand);
                    if( parseExCommand == MainGanetPKG.eExCommand.eINFO ) {
                        Track tempParseTrack = new Track();
                        tempParseTrack.updateTrackInfo(dataDev, parseExCommand);

                        if( mTrack.containsKey(tempParseTrack.getTrackId()) ) {
                            Track margeParseTrack = mTrack.get(tempParseTrack.getTrackId());
                            margeParseTrack.trackMarge(tempParseTrack);
                            mTrack.put(tempParseTrack.getTrackId(), margeParseTrack);
                        } else mTrack.put(tempParseTrack.getTrackId(), tempParseTrack );
                        activeParseID = eParse.eTr;
                    } else if( parseExCommand == MainGanetPKG.eExCommand.ePLAY ) {
                        mActiveTrack.updateActiveTrackInfo( dataDev, parseExCommand );
                        activeParseID = eParse.eActiveTr;
                    } else if ( parseExCommand == MainGanetPKG.eExCommand.eFOLDER ){
                        mFolder.updateFolderInfo( exCommand, dataDev );
                        activeParseID = eParse.eFolder;
                    } else if( parseExCommand == MainGanetPKG.eExCommand.eEjected ){
                        mTrack.clear();
                        mFolder.clearAll();
                        activeParseID = eParse.eEjectDisk;
                    } else if( parseExCommand == MainGanetPKG.eExCommand.eBeforeTrackPlay) {
                        mActiveTrack.updateActiveTrackInfo( dataDev, parseExCommand );
                        activeParseID = eParse.eInsertTrack;
                    } else if ( parseExCommand == MainGanetPKG.eExCommand.eACTALBOMENAME ) {
                        mActiveTrack.updateActiveTrackInfo(dataDev, parseExCommand);
                        activeParseID = eParse.eActiveTr;
                    } else if ( parseExCommand == MainGanetPKG.eExCommand.eACTTRACKNAME1 ) {
                        mActiveTrack.updateActiveTrackInfo(dataDev, parseExCommand);
                        activeParseID = eParse.eActiveTr;
                    } else if ( parseExCommand == MainGanetPKG.eExCommand.eACTFILENAME ) {
                        mActiveTrack.updateActiveTrackInfo(dataDev, parseExCommand);
                        activeParseID = eParse.eActiveTr;
                    } else if ( parseExCommand == MainGanetPKG.eExCommand.eACTARTISTNAME ) {
                        mActiveTrack.updateActiveTrackInfo(dataDev, parseExCommand);
                        activeParseID = eParse.eActiveTr;
                    }
                }
                // VOLUME
                else if ( (line.indexOf("680231020200") != -1) && dstDev.equals("131") )
                {
                    commandDev = line.substring( chPos, chPos += 12 );
                    dataDev = line.substring( chPos, chPos += 2 );
                    dataDev = dataDev.replace( "FF", "00" );

                    mVol.setVol( Integer.valueOf(dataDev, 16).intValue() );
                    activeParseID = eParse.eVolume;
                }
                // FM-AM RADIO
                else if( (line.indexOf("68073102") != -1) && dstDev.equals("131")  )
                {
                    commandDev = line.substring( chPos, chPos += 8 );
                    exCommand = line.substring( chPos, chPos += 4 );
                    int endPos = (line.length() - 3); //without 2last symbols (CRS)
                    dataDev = line.substring( chPos, endPos );

                    mRadio.mCurrRAction = mRadio.getCommand(exCommand);
                    if ( RadioAction.eRadioCommand.eChange != mRadio.mCurrRAction &&
                            RadioAction.eRadioCommand.eNone != mRadio.mCurrRAction ) {
                    /*
183131	68073102	0100	01	01	1010	FF50	1B 	- Store1	FM1  101.1 FM  (play)
183131	68073102	0100	03	01	1018	FF40	15	- 101.8 FM  Store3
                     */
                        chPos = 0;
                        String tempData = dataDev.substring( chPos, chPos += 2 );
                        mRadio.mStoreID = Integer.valueOf(tempData).intValue();

                        tempData = dataDev.substring( chPos, chPos += 2 );
                        mRadio.setRadioType( tempData );

                        tempData = dataDev.substring( chPos, chPos += 4 );
                        mRadio.setFrequency( tempData );

                        tempData = dataDev.substring( chPos += 2, ++chPos );
                        tempData = tempData.replace("F", "0");
                        mRadio.mRQuality = Integer.valueOf(tempData).intValue();
                        activeParseID = eParse.eRadio;
                    }
                }
            }
            this.mGaNET.invalidate( activeParseID );
        }
        vGANETCommand.clear();
    }

    private void extractGaNetLine( String lineTmp ) {
        boolean startAdd = false;
//        boolean stopAdd = false;

        buffer += lineTmp;
        String retVal = "";
        try{
            for( int a = 0; a < buffer.length(); a++ ) {
                if( !startAdd && (buffer.charAt(a)  == '<') ) {
                    startAdd = true;
//                stopAdd = false;
                    retVal = String.valueOf( buffer.charAt(a) );
                } else if ( startAdd && (buffer.charAt(a)  == '>') ) {
                    startAdd = false;
//                stopAdd = true;
                    retVal += String.valueOf( buffer.charAt(a) );
                    if( checkGANET_PKG( retVal ) ) vGANETCommand.add( retVal );
                    else {
                        Log.e( TAG, "ERROR: " + retVal );
                    }
                    buffer = buffer.substring( a+1, buffer.length() );
                    a = 0;
                } else if (  startAdd ) {
                    retVal += String.valueOf( buffer.charAt(a) );
                }
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            startAdd = false;
            retVal = "";
            buffer = "";
            Log.e( TAG, e.getMessage() );
        }

//        return retVal;
    }

    private boolean checkGANET_PKG(String retVal) {
        if ( retVal.indexOf("<", 1) != -1 ) return false;
        if ( retVal.indexOf(">", 0 ) != (retVal.length()-1) ) return false;
        return true;
    }

    static public String getString( String data, boolean isASCII, boolean isZerroFirst ) {
        String returnText = "";
        for( int a = 0; a < data.length();  ) {
            if( !isASCII ) {
                String str1 = data.substring( a, a += 2 );
                String str2 = data.substring( a, a += 2 );
                if( !str1.equals("FF") && !str2.equals("FF") ) {
                    returnText += isZerroFirst ? UnicodeToString( str1 + str2) : UnicodeToString( str2 + str1 );
                }
            } else {
                String str1 = data.substring( a, a += 2 );
                if( str1.equals("FF") )break;
                byte aaa = Byte.parseByte(str1, 16);
                returnText += String.valueOf((char)aaa);
            }
        }
        return returnText;
    }

    private static String UnicodeToString( String Hex ) {
        String enUnicode = null;
        String deUnicode = null;
        try{
            for (int i = 0; i < Hex.length(); i++) {
                if (enUnicode == null)
                    enUnicode = String.valueOf(Hex.charAt(i));
                else
                    enUnicode = enUnicode + Hex.charAt(i);

                if (i % 4 == 3) {
                    if (enUnicode != null) {
                        if (deUnicode == null)
                            deUnicode = String.valueOf((char) Integer.valueOf(enUnicode, 16).intValue());
                        else
                            deUnicode += String.valueOf((char) Integer.valueOf(enUnicode, 16).intValue());
                    }
                    enUnicode = null;
                }
            }
        } catch (NumberFormatException e) {
            deUnicode = "---- ERROR ----";
        }

        return deUnicode;
    }
    /**
     * 183131684B31 0202 00F360FFFFFF0F 01 0F 01 FFFFFFFF21300F130F0300203020010468
     * @param dataDev
     * @return
     */
    private int getActiveDisk(String dataDev) {
        int textPos = 3;
        String valueCom = dataDev.substring( textPos, ++textPos );
        return Integer.valueOf(valueCom).intValue();
    }

    private MainGanetPKG.eExCommand getExCommand(String exCommand) {
        switch( exCommand ) {
            case "0377":
                return MainGanetPKG.eExCommand.eINFO;
            case "0300":
                return MainGanetPKG.eExCommand.ePLAY;
            case "0376":
            case "030B":
            case "030D":
                return MainGanetPKG.eExCommand.eFOLDER;
            case "1A00": //start Eject Disk
                return MainGanetPKG.eExCommand.eStartEject;
            case "1B00": //end Eject Disk
                return MainGanetPKG.eExCommand.eEjected;
            case "0200": //preparing before play track
                return MainGanetPKG.eExCommand.eBeforeTrackPlay;
            case "0372":
                return MainGanetPKG.eExCommand.eACTTRACKNAME1;
            case "0373":
                return MainGanetPKG.eExCommand.eACTALBOMENAME;
            case "0371":
                return MainGanetPKG.eExCommand.eACTFILENAME;
            case "0370":
                return MainGanetPKG.eExCommand.eACTFOLDERNAMSE;
            case "0374":
                return MainGanetPKG.eExCommand.eACTARTISTNAME;
        }
        return MainGanetPKG.eExCommand.eNONE;
    }


    public eParse getActiveParseID(){
        return activeParseID;
    }

    public int getActiveDiskID() { return activeDiskID; }

}
