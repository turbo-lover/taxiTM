package com.example.taksitm.layout;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.My_Preferences_Worker;
import com.example.taksitm.R;
import com.example.taksitm.R.layout;
import com.example.taksitm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import com.example.taksitm.Validation;
import com.example.taksitm.composite_history;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryLayout extends Activity
{
    private NetworkStateReceiver mNetSateReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);

        mNetSateReceiver = new NetworkStateReceiver();
        //подключения слушателя события включения интернета
        registerReceiver( mNetSateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION ) );

        set_history(get_history());
    }



    private class NetworkStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            LinearLayout ll = (LinearLayout)findViewById(R.id.LayHistory_content);

                clear_history();
                set_history(get_history());

        }
    }

    private void clear_history() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.LayHistory_content);


        ll.removeAllViewsInLayout();
    }


    JSONObject get_history()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        JSONObject jo = new JSONObject();

        //получаем id user

        My_Preferences_Worker pw = new My_Preferences_Worker(this);



        try{

            String usr_id=  pw.get_user_id();

            if(usr_id.length() != 0)
            {
                //TODO изменить
                jo.put("user_id",usr_id);
                //jo.put("user_id",""+55);


            }
            if (Validation.isOnline(this) == false)
            {
                Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();
                return null;
            }
            worker.execute(jo,"http://taxi-tm.ru/index/android_get_history");

            jo = worker.get();



        }
        catch (Exception e)
        {

        }
        return jo;
    }

    private void set_history(JSONObject jo)
    {
        LinearLayout ll = (LinearLayout)findViewById(R.id.LayHistory_content);
        JSONArray ja =new JSONArray();
        try
        {
           ja = jo.getJSONArray("history");


        }
        catch (Exception e)
        {
            //TODO установить текст белый цвет и побольше его сделать
            TextView empty_textView = new TextView(this);
            empty_textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            empty_textView.setText("История пуста!");
            ll.addView(empty_textView);
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
