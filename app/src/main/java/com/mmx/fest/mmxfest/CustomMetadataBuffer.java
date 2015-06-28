package com.mmx.fest.mmxfest;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;

import java.io.File;
import java.util.List;

/**
 * Created by asus on 28-06-2015.
 */
abstract class CustomMetadataBuffer implements ResultCallback<DriveApi.MetadataBufferResult> {
    private File[] list;
    private DriveFolder folder;
    CustomMetadataBuffer(File[] list,DriveFolder folder)
    {
        this.list=list;
        this.folder=folder;
    }

    public File[] getMetaFiles() {
        return list;
    }
    public DriveFolder getFolder() {
        return folder;
    }
}
