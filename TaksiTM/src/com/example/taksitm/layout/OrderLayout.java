package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.R;
import com.example.taksitm.Validation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderLayout extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_layout);

        if(Validation.isOnline(this))
        {
            return;
        }

        spinner_load_cities();

	}

    public void spinner_load_cities()
    {
        if (Validation.isOnline(this) == false)
        {
            //Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);


            try
            {
                // получаем список городов от сервера
                List<String> list = get_cities();

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // !  наполняем список городов
                spr.setAdapter(dataAdapter);
            }
            catch (Exception je)
            {
                Log.d("onClick", je.getMessage());
                // je.printStackTrace();
            }

    }

    private List<String> get_cities()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();
        List<String> list= new ArrayList<String>();
        try
        {
            worker.execute(new JSONObject().put("get_city","lol"), "http://taxi-tm.ru/index/android_get_city");
            ja = worker.get().getJSONArray("city");
            for(int i = 0; i < ja.length(); i++)
            {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("title").toString());
            }
        }
        catch(Exception e)
        {
            Log.d("get_city method",e.getMessage());
            return list;
        }
        return list;
    }

    public void add_adr(View v)
    {

    }



	public void OrderButton_click(View v)
	{
		// some validation

		try
		{
			Intent i = new Intent(this, ConfirmLayout.class);

			startActivity(i);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
