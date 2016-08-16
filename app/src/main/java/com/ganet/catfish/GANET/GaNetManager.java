package com.ganet.catfish.GANET;

import com.ganet.catfish.GANET.Data.ActiveTrack;
import com.ganet.catfish.GANET.Data.DevTime;
import com.ganet.catfish.GANET.Data.Folder;
import com.ganet.catfish.GANET.Data.RadioAction;
import com.ganet.catfish.GANET.Data.Track;
import com.ganet.catfish.GANET.Data.Volume;
import com.ganet.catfish.ganet_service.GaNetService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oleg on 25.07.2016.
 */
public class GaNetManager {
    private GaNetService gaService;
    public int currentDiskId;
    public int currentLevel;

    public Folder mFolder;
    public Map<Integer, Track> mTrack;
    public DevTime mDevTime;
    public ActiveTrack mActiveTrack;
    public Volume mVol;
    public RadioAction mRadio;

    public ParserGANET mParser;

    public GaNetManager(GaNetService gaService) {
        this.gaService = gaService;
        mActiveTrack = new ActiveTrack();
        mTrack = new HashMap<Integer, Track>();
        mDevTime = new DevTime();
        mFolder = new Folder();
        mVol = new Volume();
        mRadio = new RadioAction();
        mParser = new ParserGANET( this );
    }

    public ParserGANET getParser() {
        return mParser;
    }

    public void invalidate( ParserGANET.eParse activeParseID ) {
        gaService.invalidate( activeParseID );
    }

    public String getTrackById( int trackID ){
        String returnVal = "";
        if( mTrack.containsKey(Integer.valueOf(trackID)) ){
            returnVal = mTrack.get(Integer.valueOf(trackID)).getName();
        }
        return returnVal;
    }

}
