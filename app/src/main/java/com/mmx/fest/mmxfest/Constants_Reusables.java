package com.mmx.fest.mmxfest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by M1031787 on 6/27/2015.
 */
public class Constants_Reusables {

    public  static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences(
                "com.mmx.app", Context.MODE_PRIVATE);
    }
}
