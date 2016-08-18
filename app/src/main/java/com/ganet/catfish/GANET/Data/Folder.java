package com.ganet.catfish.GANET.Data;

import com.ganet.catfish.GANET.Data.FolderData;
import com.ganet.catfish.GANET.ParserGANET;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oleg on 25.07.2016.
 * folder info
 */
public class Folder {
    public int folderId;

    public Map<Integer, FolderData> mFoldersData;

    public Folder() {
        mFoldersData = new HashMap<Integer, FolderData>();
    }

    /**
     *
     1C	684B3102   0376	  0F	  01		0102		03		00		440065007000650063006800650020		59
           PACK   | ID |       |Folder|		       | pack/ALL |     	| FOLDER NAME
                               |number|
     <GA:183131684B31020376 0F02 02 02 01 0046006F006C00640065007200310031    49>
     <GA:183131684B31020376 0F02 02 02 11 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF    8B>
     <GA:183131684B31020376 0F01 01 12 00 0046006F006C0064006500720031FFFF    23>
     <GA:183131684B31020376 0F01 01 12 10 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF    98>
               684B31020376 0F04 03 02 01 0046006F006C00640065007200310031    4C
     * @param exCommand
     * @param data
     */

    public void updateFolderInfo( String exCommand, String data ) {
        int textPos = 2;
        String valueCom;
        int folderLevel;
        boolean isSelect;

        if( exCommand.equals("0376") ) // get name
        {
            valueCom = data.substring( textPos, (textPos +=2) );
            folderId = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, (textPos +=2) );
            folderLevel = Integer.valueOf(valueCom);

            valueCom = data.substring( (textPos), (++textPos) );
            isSelect = false;
            if (Integer.valueOf(valueCom).intValue() == 1 ) isSelect = true;

            valueCom = data.substring( textPos += 1, textPos +=1 );
            int currPack = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, (textPos +=1) );
            int allPack = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos, data.length() );
            String currFolderName = ParserGANET.getString( valueCom, false, true );
//                    updateNameFolder(valueCom);

//            folderDataMarge( currPack, allPack, folderId, currFolderName, isSelect );
            updateFolderData(folderId, folderLevel, isSelect, currPack, allPack, currFolderName );

        } else if ( exCommand.equals("030B") ) { //Folder param
//183131	684B3102	030B	0F	06	03	0F	05	0F	15	0F	04	00		62		// in sub folder #6 disk #3  (4 trakss)
            valueCom = data.substring( textPos, (textPos +=2) );
            folderId = Integer.valueOf(valueCom);
            // if( 0 == folderId )return;

            FolderData currentFolderData;
            if( mFoldersData.containsKey( folderId ) ) currentFolderData = mFoldersData.get(folderId);
            else currentFolderData = new FolderData( folderId );

            valueCom = data.substring( textPos, (textPos +=2) );
            currentFolderData.folderLevel = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos +=2, textPos +=2 );
            valueCom = valueCom.replace("FF","0");
            currentFolderData.parentID = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos +=2, textPos +=2 );
            valueCom = valueCom.replace("FF","0");
            currentFolderData.trackCalcFrom = Integer.valueOf(valueCom);

            valueCom = data.substring( textPos +=2, textPos +=2 );
            valueCom = valueCom.replace("FF","0");
            currentFolderData.trackCount = Integer.valueOf(valueCom);

            if( !mFoldersData.containsKey( folderId ) )mFoldersData.put( folderId, currentFolderData );

        } else if ( exCommand.equals("030D") ) { //Sub folder info
//183131	684B3102	030D	0F	05	0F	01	00	00	0F	06	0F	FF	0F	FF	0F	FF0FFF0FFF0FFF0FFF	00	A9  // in Folder id5   disk #3
            valueCom = data.substring( textPos, (textPos +=2) );
            folderId = Integer.valueOf(valueCom);
            // if( 0 == folderId )return;

            FolderData currentFolderData;
            if( mFoldersData.containsKey( folderId ) ) currentFolderData = mFoldersData.get(folderId);
            else currentFolderData = new FolderData(folderId);

            valueCom = data.substring( textPos +=2, textPos +=2 );
            valueCom = valueCom.replace("FF","0");
            int vElementCount = Integer.valueOf(valueCom).intValue();
            currentFolderData.setSubFolderCount( vElementCount );

            textPos +=6;

            for( int a = 0; a < vElementCount; a++ ) {
                valueCom = data.substring( textPos, textPos += 2 );
                currentFolderData.subFoldersId.add(Integer.valueOf(valueCom).intValue());
                textPos += 2;
            }

            if( !mFoldersData.containsKey( folderId ) ) mFoldersData.put( folderId, currentFolderData );
        }
    }

    private void updateFolderData(int folderId, int folderLevel, boolean isSelect, int currPack, int allPack, String currFolderName) {
        if( mFoldersData.containsKey( Integer.valueOf(folderId) ) ) {
            FolderData tempFolderData = mFoldersData.get( Integer.valueOf(folderId) );
            tempFolderData.updateData( currPack, currFolderName );
            tempFolderData.folderLevel = folderLevel;
            tempFolderData.isSelect = isSelect;
        } else {
            FolderData newFolderData = new FolderData(folderId);
            newFolderData.setFolderAllPkg(allPack);
            newFolderData.updateData( currPack, currFolderName );
            newFolderData.folderLevel = folderLevel;
            newFolderData.isSelect = isSelect;
            mFoldersData.put( folderId, newFolderData );
        }

    }

//    /**
//     * updateNameFolder
//     * @param text
//     */
//    private String updateNameFolder( String text ) {
//        String trackTextTmp = "";
//        for( int a = 2; a < text.length(); a +=2 ) {
//            String strTmp = text.substring( a, a+=2 );
//            if( !strTmp.equals("FF") ) {
//                int tmpInt = Integer.parseInt( strTmp, 16 );
//                trackTextTmp += Character.toString((char)tmpInt);
//            }
//        }
//        return trackTextTmp;
//    }

    /**
     * resetFolders
     */
    public void resetFolders() {
        mFoldersData.clear();
    }

    /**
     * getName
     * @return
     */
    public String getNameByID( int keyID ) {
        String returnVal = "< >";
        if( mFoldersData.containsKey( Integer.valueOf( keyID ) ) ){
            FolderData tempFolderData = mFoldersData.get( Integer.valueOf(keyID) );
            if(tempFolderData.isComplete()) returnVal = tempFolderData.getName();
        }
        return returnVal;
    }

}
