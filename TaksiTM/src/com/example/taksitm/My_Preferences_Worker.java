package com.example.taksitm;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
/**
 * Created by turbo_lover on 27.05.13.
 */
public class My_Preferences_Worker
{
    final private  String preference_uid = "uid";
    final private String preference_user_id = "user_id";
    final private String preference_user_name = "user_name";
    final private String preference_user_login = "user_login";
    final private String preference_location = "my_pref";
    private SharedPreferences sPref;
    private Context context;

    public My_Preferences_Worker(Context in)
    {
        context = in;
    }

    public void set_UID(String uid)
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_uid ,uid);
        ed.commit();
    }

    public void set_Number(String number,)
    {
         ContextWrapper cw = new ContextWrapper(context);
         sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
         Editor ed = sPref.edit();

         ed.putString(preference_user_login ,number);
         ed.commit();
    }

}
