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

    private GeNetPkgText folderName;

    private int folderID;
    public int parentID;
    public int folderLevel;
    public boolean isSelect;
    public int trackCount;
    public int trackCalcFrom;

    public Vector<Integer> subFoldersId;


    public FolderData( int folderID ) {
        folderName = new GeNetPkgText();
        this.folderID = folderID;
        subFoldersId = new Vector<Integer>();
    }
    /**
     *
     * @param allPk
     */
    public void setFolderAllPkg(int allPk) {
        folderName.setAllPack( allPk );
    }

    public void updateData(int currPk, String folderName ) {
        this.folderName.updateInfo( folderName, currPk );
    }

    public boolean isComplete() {
        return folderName.isReady();
    }

    public String getName() {
        return folderName.getText();
    }

    public int getFolderID() {
        return folderID;
    }
}
