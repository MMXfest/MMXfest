package com.mmx.fest.mmxfest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bifortis on 27-06-2015.
 */
public class ContactsFetcher {

    Context context;
    private List<CustomContact> customContactList;

    public ContactsFetcher(Context context) {
        super();
        this.context = context;
        customContactList = new ArrayList<CustomContact>();

    }

    /*
     * Function to fetch local contacts from the device phonebook.
     */
    public List<CustomContact> fetchContacts() {

        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                                Cursor pCur = cr.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                        null,
                                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                        + " = ?", new String[]{id}, null);
                                while (pCur.moveToNext())
                                {
                                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        phoneNo = phoneNo.replaceAll("[^0-9\\+]+", "");
                                        phoneNo = phoneNo.replaceAll("-", "");
                                        CustomContact customContact = new CustomContact(name,
                                                phoneNo);
                                        customContactList.add(customContact);
                                }
                                pCur.close();
                    }
                }
            }
            cur.close();
        } catch (Exception e1) {
            Log.e("Inside  Fetching", e1.toString());
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < customContactList.size(); i++) {
            list.add(customContactList.get(i).Phonenumber);
        }
        JSONArray jsArray = new JSONArray(list);

        //26-06-2015
        Log.e("Contacts in The Phone :", jsArray.toString());
        //End 26-06-2015

        return customContactList;


    }


    public class CustomContact {
        public String ContactName;
        public String Phonenumber;

        public CustomContact(String ContactName, String Phonenumber) {
            this.ContactName = ContactName;
            this.Phonenumber = Phonenumber;
        }
    }
}
