package com.mmx.fest.mmxfest;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;

import java.io.File;

/**
 * Created by asus on 28-06-2015.
 */
abstract class CustomCallback <D> implements ResultCallback<DriveApi.DriveContentsResult> {
    private File file=null;
    CustomCallback(File file)
    {this.file=file;}
    public void setFile(File file){this.file=file;};
    public File getFile(){ return  file;};
}
