package com.mmx.fest.mmxfest;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Contacts extends Activity implements LoaderManager.LoaderCallbacks {

    //ArrayList for contact name and contact number
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

    //key value pair for contact used in ArrayList
    HashMap<String, String> map = new HashMap<String, String>();

    //ListView instance for creating ListView
    ListView contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        contacts=(ListView)findViewById(R.id.listContacts);

        //Loading Loader for fetching contacts from phone
        getLoaderManager().initLoader(0, null, this).forceLoad();
        }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new asyncLoader(Contacts.this);
    }



    @Override
    public void onLoadFinished(Loader loader, Object data) {
        List<ContactsFetcher.CustomContact> contactList=(List<ContactsFetcher.CustomContact>)data;

        for (int i = 0; i < contactList.size(); i++) {
            map = new HashMap<String, String>();
            //putting ContactName and ContactNumber into HashMap
            map.put("name", contactList.get(i).ContactName);
            map.put("number", contactList.get(i).Phonenumber);

            //Adding Hash value to Array List
            mylist.add(map);

            //Adapter to show Contacts Details into ListView
            SimpleAdapter adapter = new SimpleAdapter(this, mylist, R.layout.contact_list_row,
                    new String[]{"name", "number"}, new int[]
                    {R.id.textContactName, R.id.textContactNumber});

            contacts.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
