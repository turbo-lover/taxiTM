package com.example.taksitm.layout;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MotionEvent;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.R;
import com.example.taksitm.Validation;

public class MainActivity extends Activity implements TextWatcher,OnClickListener
{
	String[] mContacts = {};

    SharedPreferences sPref;
    final private  String preference_uid = "uid";
    final private String preference_user_id = "user_id";
    final private String preference_user_name = "user_name";
    final private String preference_user_login = "user_login";

    @Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        sPref = getSharedPreferences("my_pref",MODE_PRIVATE);

        //Toast.makeText(this,sPref.getString(preference_user_login,""),Toast.LENGTH_LONG);

        if (sPref.getString(preference_user_login,"").length()!= 0  )
        {
            
            Intent i = new Intent(this, EnterLayout.class);

            try
            {
                startActivity(i);
            }
            catch (Exception e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }


        AutoCompleteTextView mAutoComplete = (AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
        mAutoComplete.addTextChangedListener(this);

        Spinner spr = (Spinner) findViewById(R.id.spinner_city);

        spr.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motion)
            {
                Spinner spr = (Spinner) findViewById(R.id.spinner_city);
                if(motion.getAction() == MotionEvent.ACTION_DOWN)
                {
                        spinner_load_cities();

                }
                return false;
            }
        });


		if (Validation.isOnline(this) == false)
		{
			//Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
			return;
		}




       // spr.setOnClickListener(onClickSpinner);


        spinner_load_cities();

	}

    public void spinner_load_cities()
    {
        if (Validation.isOnline(this) == false)
        {
            //Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.spinner_city);

        if(spr.getCount()==0)
        {
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

	public void btn_RequestPass(View v)
	{

		if (Validation.isOnline(this) == false)
		{
			//Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
			return;
		}

		// Поле ввода телефона
		EditText _numberTextInput;
        _numberTextInput = (EditText) findViewById(R.id.txt_telephone_number_main);
        String valSrt = _numberTextInput.getText().toString();

		if (Validation.isNull(valSrt) == true)
		{
			Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
			_numberTextInput.requestFocus();
			return;
		}


        //проверка на валидность
		if (_numberTextInput.getText().length() < 9)
		{

			Toast.makeText(this, "Некорректный номер", Toast.LENGTH_SHORT).show();
			_numberTextInput.requestFocusFromTouch();
			return;
		}

		try
		{
			My_AsyncTask_Worker d = new My_AsyncTask_Worker();
			JSONObject jo = new JSONObject();

            //вносим номер в обьект для отпраки
			jo.put("number", valSrt);

            //передаем данные АПИ
			d.execute(jo, "http://taxi-tm.ru/index/android_registration");
			jo = d.get();

			String response = jo.get("response").toString();
			String reason = jo.get("reason").toString();

            // если все хорошо запоминаем uid
			if (response.equals("ok"))
			{
                saveUid(reason);

				Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
                // save.reason
                return;
			}
            else
            {
                if(reason.equals("wrong_number"))
                {
                    Toast.makeText(this, "Номер некоректен", Toast.LENGTH_SHORT).show();
                }
                if(reason.equals("exists"))
                {
                    Toast.makeText(this, "Номер существует", Toast.LENGTH_SHORT).show();
                }

            }

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

    //TODO сохранение в конфигурацию
    private void saveUid(String str)
    {
        sPref = getSharedPreferences("my_pref",MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_uid ,str);
        ed.commit();
    }
    private void saveNumber(String str)
    {
        sPref = getSharedPreferences("my_pref",MODE_PRIVATE);
        Editor ed = sPref.edit();

        ed.putString(preference_user_login ,str);
        ed.commit();
    }

    public void btn_Register(View v)
	{
        //TODO 1) проверить была ли выпонена предыдущая функция, если нет выполнить и продолжить

    //получаем все текстовые поля
        // Корпус
        EditText _number_corp = (EditText) findViewById(R.id.LayMain_txt_corp);
        // Номер дома
        EditText _number_home = (EditText) findViewById(R.id.LayMain_txt_home);
        // Имя
        EditText _nameTextInput = (EditText) findViewById(R.id.txt_enter_name);
        // Проверочный код из смс
        EditText _passTextInput = (EditText) findViewById(R.id.txt_confirm_pass);
        // номер телефона
        EditText _numberTextInput = (EditText) findViewById(R.id.txt_telephone_number_main);
        // ввод улицы
        AutoCompleteTextView mAutoComplete = (AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
        // Номер города
        Spinner sp = (Spinner) findViewById(R.id.spinner_city);

    //получаем доступ к сохраннем настройкам
        sPref = getSharedPreferences("my_pref",MODE_PRIVATE);

    //получаем информацию с полей
        String number = _numberTextInput.getText().toString();
        String userName =_nameTextInput.getText().toString();
        String city = ""+sp.getSelectedItemPosition()+1;
        String street = mAutoComplete.getText().toString();
        String street_number = _number_home.getText().toString();
        String street_corp = _number_corp.getText().toString();
        String pass =_passTextInput.getText().toString();
        String uid  = sPref.getString(preference_uid,"");

        //валидация поля
        My_AsyncTask_Worker d = new My_AsyncTask_Worker();
        JSONObject json = new JSONObject();

        try
        {   //после валидации заносим всю пижню в джейсон
            json.put("number",number);
            json.put("username",userName);
            json.put("city",city);
            json.put("street",street);
            json.put("street_corpus",street_corp);
            json.put("street_number",street_number);
            json.put("pass", pass);
            json.put("uid",uid);

            //посылаем на сервак запрос
            d.execute(json, "http://taxi-tm.ru/index/android_end_registration");

            json = d.get();

            String response = json.get("response").toString();
            String reason = json.get("reason").toString();

           // обработка удачной регистрации
            if (response.equals("ok"))
            {

                Toast.makeText(this, "Вы успешно зарегистрированны!", Toast.LENGTH_SHORT).show();
                saveNumber(number);


                Intent i = new Intent(this, EnterLayout.class);

                try
                {
                    startActivity(i);
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }

                return;
            }
           //если что то пошло не так!
            else
            {
                if(response.equals("code"))
                {
                    Toast.makeText(this, "Не верный код из смс!",Toast.LENGTH_SHORT);
                }

            }
        }
        catch(Exception e)
        {}
	}

	public void btn_toEnterActivity(View v)
	{
		Intent i = new Intent(this, EnterLayout.class);

		try
		{
			startActivity(i);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}


	@Override
	public void afterTextChanged(Editable s)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{

        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        if(s.length() >= 2)
        {
            JSONObject jo = new JSONObject();
            Spinner sp = (Spinner) findViewById(R.id.spinner_city);
            try
            {
                jo.put("chars",s.toString());
                jo.put("city_id",sp.getSelectedItemPosition()+1 );

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



			    AutoCompleteTextView mAutoComplete = (AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list);
                mAutoComplete.setAdapter(arrAd);

                mAutoComplete.showDropDown();


            }
            catch(Exception e)
            {

            }

        }


//		/*try
//		{
//			mContacts = _getStreets(s.toString()).toArray(new String[0]).clone();
//			AutoCompleteTextView mAutoComplete = (AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
//			mAutoComplete.showDropDown();
//
//			ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mContacts);
//
//			mAutoComplete.setAdapter(arrAd);
//			if (s.toString().length() >= 2)
//				mAutoComplete.showDropDown();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			Log.wtf("after text change", e.toString());
//		}*/

	}

    @Override
    public void onClick(View view) {

    }
}
