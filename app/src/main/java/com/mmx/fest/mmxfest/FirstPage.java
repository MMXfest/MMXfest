package com.mmx.fest.mmxfest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.provider.Browser;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;


public class FirstPage extends BaseDemoActivity {

    String bookmark = null;
    ImageView addButton;
    private String mNextPageToken;
    private boolean mHasMore;
    final String ROOT_NAME = "SDCard BackUp MMXFest";
    final String PREFS_NAME = "MMXFEST";
    final String PREFS_ROOT_NAME = "MMXFEST_ROOT_FOLDER_ID";
    //    private static DriveId sFolderId = DriveId.decodeFromString("DriveId:0B2EEtIjPUdX6MERsWlYxN3J6RU0");
    String driveId;
    DriveId DRIVE_ID;
    private File file;
    private List<String> myList;
    private ListView mResultsListView;
    SharedPreferences prefs;


    LinearLayout contactsLayout;
    LinearLayout imageLayout;
    LinearLayout musicLayout;
    LinearLayout videoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        addButton = (ImageView) findViewById(R.id.imageAddButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstPage.this, NextPage.class);
                startActivity(intent);
            }
        });


        contactsLayout = (LinearLayout) findViewById(R.id.contactsLayout);

        contactsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, Contacts.class);
                startActivity(intent);
            }
        });


        imageLayout = (LinearLayout) findViewById(R.id.imagesLayout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, Gallery.class);
                startActivity(intent);
            }
        });


        musicLayout = (LinearLayout) findViewById(R.id.musicLayout);
        musicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, Music.class);
                startActivity(intent);
            }
        });


        videoLayout = (LinearLayout) findViewById(R.id.videosLayout);
        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPage.this, VideoStoredInSDCard.class);
                startActivity(intent);
            }
        });

        getBookmark();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookmark != null)
            generateNoteOnSD("MMXBookmark", bookmark);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(FirstPage.this, SettingPage.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getBookmark() {
        String[] proj = new String[]{Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL};
        String sel = Browser.BookmarkColumns.BOOKMARK + " = 1"; // 0 = history, 1 = bookmark
        Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI, proj, sel, null, null);
        this.startManagingCursor(mCur);
        mCur.moveToFirst();

        String title = "";
        String url = "";

        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {

                title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));
                url = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));
                // Do something with title and url
                bookmark = url + ",";
                Log.e("title and url", title + "::" + url);
                mCur.moveToNext();
            }
        }

    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(new CustomCallback<DriveApi.DriveContentsResult>(gpxfile) {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    MetadataChangeSet changeSet;
                    changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getFile().getName())
                            .build();
                    OutputStream outputStream = result.getDriveContents().getOutputStream();
                    byte[] bFile = new byte[(int) getFile().length()];
                    FileInputStream fileInputStream = null;

                    try {
                        //convert file into array of bytes
                        fileInputStream = new FileInputStream(getFile());
                        fileInputStream.read(bFile);
                        fileInputStream.close();

                        for (int i = 0; i < bFile.length; i++) {
                            outputStream.write((char) bFile[i]);
                        }

                        System.out.println("Done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Drive.DriveApi.getAppFolder(getGoogleApiClient()).createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void openFolder(final File file, String str, DriveId working_drive_id) {

        Log.i("DriveActivity", "OPEN FOLDER");
        Log.i("DriveActivity", str + file.getName());
        if (file.listFiles() != null) {
            DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), working_drive_id);
            folder.listChildren(getGoogleApiClient()).setResultCallback(
                    new CustomMetadataBuffer(file.listFiles(), folder) {
                        @Override
                        public void onResult(DriveApi.MetadataBufferResult result) {
                            Log.i("DriveActivity", "CustomMetadataBuffer");
                            if (!result.getStatus().isSuccess()) {
                                showMessage("Error while trying to get Metadata");
                                return;
                            }
                            boolean rootExists = false;
                            int whichItem = 0;
                            File[] files = getMetaFiles();
                            HashMap<String, DriveId> set = new HashMap<String, DriveId>();
                            int count = result.getMetadataBuffer().getCount();
                            for (int i = 0; i < count; i++) {
                                set.put(result.getMetadataBuffer().get(i).getTitle(),
                                        result.getMetadataBuffer().get(i).getDriveId());
                            }
                            for (int i = 0; i < files.length; i++) {
                                Log.i("DriveActivity", "Metadata: " + files[i]);
                                DriveId tempFile = set.get(files[i].getName());
                                if (tempFile == null && files[i].isDirectory()) {
                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(files[i].getName()).build();

                                    getFolder().createFolder(getGoogleApiClient(), changeSet).setResultCallback(
                                            new CustomResultCallback<DriveFolder.DriveFolderResult>(files[i]) {
                                                @Override
                                                public void onResult(DriveFolder.DriveFolderResult result) {
                                                    Log.i("DriveActivity", "FOLDER CREATED");
                                                    if (!result.getStatus().isSuccess()) {
                                                        showMessage("Problem while trying to create a folder");
                                                        return;
                                                    }
                                                    Log.i("DriveActivity", "Folder " + result.getDriveFolder() + " " + getFile().getName());
                                                    showMessage("Folder succesfully created" + getFile().getName());
                                                    openFolder(getFile(), "", result.getDriveFolder().getDriveId());

                                                }
                                            }
                                    );
                                } else if (files[i].isFile()) {

                                    Drive.DriveApi.newDriveContents(getGoogleApiClient())
                                            .setResultCallback(
                                                    new CustomCallbackContent<DriveApi.DriveContentsResult>(files[i], getFolder()) {
                                                        @Override
                                                        public void onResult(DriveApi.DriveContentsResult result) {
                                                            if (!result.getStatus().isSuccess()) {
                                                                showMessage("Error while trying to create new file contents");
                                                                return;
                                                            }

                                                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                                    .setTitle(getFile().getName())
                                                                    .build();
                                                            OutputStream outputStream = result.getDriveContents().getOutputStream();
                                                            byte[] bFile = new byte[(int) getFile().length()];
                                                            FileInputStream fileInputStream = null;

                                                            try {
                                                                //convert file into array of bytes
                                                                fileInputStream = new FileInputStream(getFile());
                                                                fileInputStream.read(bFile);
                                                                fileInputStream.close();

                                                                for (int i = 0; i < bFile.length; i++) {
                                                                    outputStream.write((char) bFile[i]);
                                                                }

                                                                System.out.println("Done");
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            getFolder().createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                                                                    .setResultCallback(fileCallback);

                                                        }
                                                    }
                                            );

                                } else {
                                    openFolder(files[i], "", tempFile);
                                }
                            }

                        }
                    }

            );
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(file.getName())
                            .build();
                    Drive.DriveApi.getAppFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
                }
            };
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file in App Folder: "
                            + result.getDriveFile().getDriveId());
                }
            };


    @Override
    public void onConnected(Bundle connectionHint) {
        super.onCreate(connectionHint);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        driveId = prefs.getString(PREFS_ROOT_NAME, null);
        if (driveId == null) {

            Log.i("DriveActivity", "drive Id NULL");
            Drive.DriveApi.getRootFolder(getGoogleApiClient()).listChildren(getGoogleApiClient())
                    .setResultCallback(checkRootCallback);
        } else {
            Log.i("DriveActivity", "drive Id NOT NULL");
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(PREFS_ROOT_NAME, driveId);
            editor.commit();
            String root_sd = Environment.getExternalStorageDirectory().toString();
            file = new File("/storage/sdcard1");
            DRIVE_ID = DriveId.decodeFromString(driveId);
            openFolder(file, "", DRIVE_ID);
            //DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), DriveId.decodeFromString(driveId));
        }

        Log.i("DriveActivity", "OnConnected");

    }

    ResultCallback<DriveApi.MetadataBufferResult> checkRootCallback = new ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result) {
            Log.i("DriveActivity", "checkRootCallback");
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to get Metadata");
                return;
            }
            boolean rootExists = false;
            int whichItem = 0;
            int count = result.getMetadataBuffer().getCount();
            for (int i = 0; i < count; i++) {
                Log.i("DriveActivity", "Metadata: " + result.getMetadataBuffer().get(i).getTitle());
                if (result.getMetadataBuffer().get(i).getTitle().equals(ROOT_NAME)) {
                    rootExists = true;
                    whichItem = i;
                    break;
                }
            }
            if (rootExists) {
                DRIVE_ID = result.getMetadataBuffer().get(whichItem).getDriveId();
                driveId = DRIVE_ID.encodeToString();
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(PREFS_ROOT_NAME, driveId);
                editor.commit();
                String root_sd = Environment.getExternalStorageDirectory().toString();
                file = new File("/storage/sdcard1");
                openFolder(file, "", DRIVE_ID);
                Log.i("DriveActivity", "ROOT EXSTS");


            } else {
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(ROOT_NAME).build();
                Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(getGoogleApiClient(), changeSet)
                        .setResultCallback(rootCreated);
                Log.i("DriveActivity", "ROOT EXSTS");

            }
        }
    };

        ResultCallback<DriveFolder.DriveFolderResult> rootCreated = new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult result) {
                Log.i("DriveActivity", "ROOT CREATED");

                if (!result.getStatus().isSuccess()) {
                    showMessage("Error while trying to create the folder");
                    return;
                }
                showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
                DRIVE_ID = result.getDriveFolder().getDriveId();
                driveId = DRIVE_ID.encodeToString();
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(PREFS_ROOT_NAME, driveId);
                editor.commit();

                String root_sd = Environment.getExternalStorageDirectory().toString();
                file = new File("/storage/sdcard1");
                openFolder(file, "", DRIVE_ID);
                Log.i("DriveActivity", "ROOT EXSTS");
            }
        };

}

