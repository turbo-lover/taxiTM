package com.example.taksitm.layout;


import android.os.Debug;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.My_Preferences_Worker;
import com.example.taksitm.R;
import com.example.taksitm.R.layout;
import com.example.taksitm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.example.taksitm.composite_history;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryLayout extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);



        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        JSONObject jo = new JSONObject();

        //получаем id user

        My_Preferences_Worker pw = new My_Preferences_Worker(this);



        try{

            String usr_id=  pw.get_user_id();

            if(usr_id.length() != 0)
            {

                jo.put("user_id",usr_id);
                // jo.put("user_id",""+1);


            }

            worker.execute(jo,"http://taxi-tm.ru/index/android_get_history");

            jo = worker.get();


        }
        catch (Exception e)
        {

        }

        set_history(jo);
    }

    private void set_history(JSONObject jo)
    {
        LinearLayout ll = (LinearLayout)findViewById(R.id.LayHistory_content);
        JSONArray ja =new JSONArray();
        try
        {
           ja = jo.getJSONArray("history");


        }
        catch (JSONException je)
        {
            Log.d("history array", je.getMessage());
        }

        try
        {

            for (int i=0;i<ja.length() ;i++)
            {
                composite_history ch = new composite_history(this);

                JSONObject jObj = ja.getJSONObject(i);
                String dest = jObj.getString("to");
                for (String s : dest.split("/"))
                {
                    if(s.equals("  ") == false)
                        ch.add_destination(s);
                } ;

                ch.add_header("Дата поездки "+jObj.getString("date"));
                ch.add_from(jObj.getString("from"));

                ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            ll.addView(ch);
            }
        }
        catch (Exception e)
        {
            Log.d("history", e.getMessage());
        }
    }

}
