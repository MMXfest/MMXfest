package com.mmx.fest.mmxfest;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveFolder;

import java.io.File;

/**
 * Created by asus on 28-06-2015.
 */
abstract class CustomResultCallback<D> implements ResultCallback <DriveFolder.DriveFolderResult>{

    private File file=null;
    CustomResultCallback(File file)
    {this.file=file;}
    public void setFile(File file){this.file=file;};
    public File getFile(){ return  file;};
}
