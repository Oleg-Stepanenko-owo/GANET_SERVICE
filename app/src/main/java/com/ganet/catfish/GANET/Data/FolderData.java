package com.ganet.catfish.GANET.Data;

import java.util.Vector;

/**
 * Created by oleg on 28.07.2016.
 */

/*
     1C	684B3102   0376	  0F	  01		0102		03		00		440065007000650063006800650020		59
           PACK   | ID |       |Folder|		       | pack/ALL |     	| FOLDER NAME
                               |number|
     <GA:183131684B31020376 0F02 02 02 01 0046006F006C00640065007200310031    49>
     <GA:183131684B31020376 0F02 02 02 11 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF    8B>
     <GA:183131684B31020376 0F01 01 12 00 0046006F006C0064006500720031FFFF    23>
     <GA:183131684B31020376 0F01 01 12 10 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF    98>
 */

public class FolderData {

    private int allPack;
    public boolean[] pk;
    public String[] pkS;

    public int folderID;
    public int parentID;
    public int folderLevel;
    public boolean isSelect;
    public int trackCount;
    public int trackCalcFrom;

    public Vector<Integer> subFoldersId;


    public FolderData() {
        allPack = -1;
    }
    /**
     *
     * @param allPk
     */
    public FolderData(int allPk) {
        pk = new boolean[4];
        pkS = new String[4];
        for( int a = 0; a < 4; a++ ){
            pk[a] = false;
            pkS[a] = "";
        }

        allPack = allPk;

        if( allPack == 2)       { pk[3] = true; }
        else if( allPack == 1 ) { pk[3] = true; pk[2] = true; }
        else if( allPack == 0 ) { pk[3] = true; pk[2] = true; pk[1] = true; }
    }

    /**
     *
     * @param currPk
     * @param folderName
     */
    public void updateData(int currPk, String folderName ) {
        pkS[currPk] = folderName;
        pk[currPk] = true;
    }


    public boolean isComplete() {
        return (pk[0] && pk[1] && pk[2] && pk[3]);
    }

    public String getName(){
        return (pkS[0] + pkS[1] + pkS[2] + pkS[3]);
    }

    public void setSubFolderCount( final int count ) {
        subFoldersId = new Vector<Integer>(count);
    }
}
