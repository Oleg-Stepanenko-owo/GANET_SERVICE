package com.ganet.catfish.GANET.Data;

import android.util.Log;

/**
 * Created by OStepanenko on 03.03.2017.
 */

//Ping active device
//        183131	780D3103	024B	    0000	09	-Active CD ping
//        183131	780D3103	0207	    0001	C6	-Active FM1 ping
//        183131	780D3103	0207	    0002	C7	-Active FM2 ping
//        183131	780D3103	0207	    0011	D6	-Active AM ping
//        SrcDst	Command	    CommandID	Data    CRC


public class PingDev {
    public static final String TAG = "GaNetService";

    private eActiveDevPing mCurrentActiveDev;

    static public enum eActiveDevPing
    {
        eNone,
        eCD,
        eFM1,
        eFM2,
        eAM
    }

    public PingDev() {
        mCurrentActiveDev = eActiveDevPing.eNone;
    }

    public eActiveDevPing getActiveDevPing() {
        return mCurrentActiveDev;
    }

    public void setActiveDevPing ( eActiveDevPing val ){
        Log.d(TAG, "Ping:" + val );
        mCurrentActiveDev = val;
    }
}
