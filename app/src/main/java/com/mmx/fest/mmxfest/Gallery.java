package com.mmx.fest.mmxfest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class Gallery extends Activity {
    ImageAdapter myImageAdapter;
    Cursor cc = null;

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        ArrayList<String> itemList = new ArrayList<String>();

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public String getURI(int postion){
            return itemList.get(postion);
        }

        void add(String path){
            itemList.add(path);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300,300));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            if(!itemList.get(position).contains("http")) {
                Picasso.with(mContext).load(new File(itemList.get(position))).resize(300, 300).into(imageView);
            }//Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 300, 300);
            else {
                Picasso.with(mContext).load(itemList.get(position)).resize(300, 300).into(imageView);
            }
            //imageView.setImageBitmap(bm);
            //decodeSampledBitmapFromUri(itemList.get(position), 300, 300, imageView);

            return imageView;
        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);

            return bm;
        }
        //delete if not working
        public void decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight,ImageView imageView) {
            Log.v("Decode hemanth", "imagevide " + imageView + "rew" + reqWidth + "reh " + reqHeight);
            Picasso.with(mContext)
                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg").resize(reqHeight,reqWidth)
                    .into(imageView);
        }

        public int calculateInSampleSize(

                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }

            return inSampleSize;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_gallery);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        myImageAdapter = new ImageAdapter(this);
        gridview.setAdapter(myImageAdapter);


        cc = getRequiredCursor();
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/test/";

        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
        if(cc!=null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        cc.moveToFirst();
                        for (int i = 0; i < cc.getCount(); i++) {
                            cc.moveToPosition(i);
                            myImageAdapter.add(cc.getString(1));
                            //  Log.v("bhkkk", cc.getString(1));
                        }
                    } catch (Exception e) {

                    }
                }


            }.start();
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //do the view code here
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(((ImageAdapter) gridview.getAdapter()).getURI(position)),"image/*");
                startActivity(intent);
                //  Log.v("hemanth", position+"");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
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

            Intent settingPage = new Intent(this,SettingPage.class);
            startActivity(settingPage);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private Cursor getRequiredCursor(){
        return this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);
    }
}
