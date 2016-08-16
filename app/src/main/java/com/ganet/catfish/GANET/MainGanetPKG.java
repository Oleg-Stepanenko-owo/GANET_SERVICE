package com.ganet.catfish.GANET;

/**
 * Created by oleg on 25.07.2016.
 */
public class MainGanetPKG {
     static public enum eCommands {
        eTIME,      // 600D01000140
        eTRACK,     // 684B3102
        eVALUE      // 680231020200
    };

    static public enum eExCommand {
        ePLAY,          //0300
        eBeforeTrackPlay,    //0200
        eLOAD,          //1000
        eSTOP,          //2200
        eStartEject,    //1A00
        eEjected,       //1B00
        eERROR,         //0400
        eFOLDER,        //030B
        eFINISH,        //F200
        eINFO,          //0377
        eACTFOLDERNAMSE,//0370
        eACTFILENAME,   //0371
        eACTTRACKNAME1, //0372
        eACTALBOMENAME, //0373
        eACTARTISTNAME, //0374
        eFOLDERNAME,    //0376
        eACTTRACKNAME2, //0378
        eNONE
    };

    private String src, dst;
    private eCommands command;
    private eExCommand exCommand;
    private String data;
    private String crs;

    MainGanetPKG() {
        exCommand = eExCommand.eNONE;
    }

    public boolean getExCommandFromData() {

        return true;
    };

}
