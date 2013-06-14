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
    //телефон
    final private String preference_user_login = "user_login";
    final private String preference_location = "my_pref";
    final private String preference_user_address = "user_address";
    final private String preference_user_address_house = "user_address_house";
    final private String preference_user_address_corpus = "user_address_corpus";

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

    public void set_Number(String number)
    {
         ContextWrapper cw = new ContextWrapper(context);
         sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
         Editor ed = sPref.edit();

         ed.putString(preference_user_login ,number);
         ed.commit();
    }

    public void set_user_address(String address)
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_user_address ,address);
        ed.commit();
    }

    public void set_user_address_house(String address_house)
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_user_address_house ,address_house);
        ed.commit();
    }

    public void set_user_address_corpus(String address_corpus)
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_user_address_corpus ,address_corpus);
        ed.commit();
    }

    public void set_value(String key,String value)
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location,Context.MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(key ,value);
        ed.commit();
    }

    /*функции получения*/

    public String get_UID()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_uid,"");
    }

    public String get_user_number()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_login,"");
    }

    public String get_user_id()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_id,"");
    }

    public String get_user_address()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_address,"");
    }

    public String get_user_address_house()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_address_house,"");
    }

    public String get_user_address_corpus()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_address_corpus,"");
    }


    public String get_user_name()
    {
        ContextWrapper cw = new ContextWrapper(context);
        sPref = cw.getSharedPreferences(preference_location, Context.MODE_PRIVATE);

        return sPref.getString(preference_user_name,"");
    }
}
