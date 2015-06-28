package com.mmx.fest.mmxfest;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Bifortis on 27-06-2015.
 */

public class asyncLoader  extends AsyncTaskLoader {

Context context;
    public asyncLoader(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public List loadInBackground() {

        ContactsFetcher fetcher=new ContactsFetcher(context);
        List<ContactsFetcher.CustomContact> contactList=fetcher.fetchContacts();

        return contactList;
    }
}