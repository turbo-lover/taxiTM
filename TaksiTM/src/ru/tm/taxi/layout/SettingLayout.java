package ru.tm.taxi.layout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.tm.taxi.My_AsyncTask_Worker;
import ru.tm.taxi.My_Preferences_Worker;
import ru.tm.taxi.R;
import ru.tm.taxi.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Turbo on 09.06.13.
 */
public class SettingLayout extends Activity implements TextWatcher
{  AutoCompleteTextView to ;
   EditText to_house,to_corp,username;
    My_Preferences_Worker pref;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        initialize_component();
    }

    private void initialize_component()
    {

        pref = new My_Preferences_Worker(this);
        to = (AutoCompleteTextView) findViewById(R.id.sett_to);
        to_corp = (EditText) findViewById(R.id.sett_to_corp);
        to_house = (EditText) findViewById(R.id.sett_to_numb);
        username = (EditText) findViewById(R.id.setting_name);


        to.setText(pref.get_user_address());
        to_house.setText(pref.get_user_address_house());
        to_corp.setText(pref.get_user_address_corpus());
        username.setText(pref.get_user_name());

        to.addTextChangedListener(this);
    }

    public void change_pass(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://taxi-tm.ru"));

        startActivity(intent);
    }

    //изменение имени и адресса
    public void change_params(View v)
    {
        My_AsyncTask_Worker worker =new My_AsyncTask_Worker();

        JSONObject obj = new JSONObject();

        try
        {
            obj.put("user_id",pref.get_user_id());
//            if(pref.get_user_address().equals(to.getText().toString())==true)
//            {
//                Toast.makeText(this,R.string.notify_same_address);
//            }
            obj.put("home_address",to.getText().toString());
            obj.put("home_address_corpus",to_corp.getText().toString());
            obj.put("home_address_house",to_house.getText().toString());

            if(Validation.isNull(username.getText().toString()))
            {
                Toast.makeText(this,"Имя не может быть пустым!",Toast.LENGTH_SHORT).show();
                return;
            }
            obj.put("username", username.getText().toString());
            String temp_name = username.getText().toString();

            worker.execute(obj,"http://taxi-tm.ru/index/android_change_user_info");

            obj= worker.get();

            parse_change_params_response(obj,temp_name);
        }
        catch (Exception ex)
        {

        }
    }

    private Boolean parse_change_params_response(JSONObject responce, String temp_name)
    {
        try
        {
            String success = responce.getString("success");

           if(success.equals("denied") == true)
           {
               String reason = responce.getString("reason");

               Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();

               return false;

           }

            if(success.equals("ok") == true)
            {
                //String reason = responce.getString("reason");

                Toast.makeText(this, "Изменения были проведены успешно", Toast.LENGTH_SHORT).show();
                pref.set_value("user_name",temp_name);
                return true;

            }

            return true;
        }
        catch (Exception ex)
        {

        }
        return true;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int st, int i2, int i3)
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        if(s.length() == 2)
        {
            JSONObject jo = new JSONObject();
            Spinner sp = (Spinner) findViewById(R.id.spinner_city);
            try
            {
                jo.put("chars",s.toString());


                My_Preferences_Worker pf = new My_Preferences_Worker(this);


               //TODO поправить когда будетвозможность
                jo.put("city_id",pf.get_value("city") );

                worker.execute(jo, "http://taxi-tm.ru/index/android_get_street");
                // TODO вынести все в отдельную функцию
                jo = worker.get();

                JSONArray arr =  jo.getJSONArray("street");

                List<String> list = new ArrayList<String>();

                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject c = arr.getJSONObject(i);
                    list.add(c.get("title").toString());
                }




                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
               to.setAdapter(arrAd);

                to.showDropDown();


            }
            catch(Exception e)
            {

            }

        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}