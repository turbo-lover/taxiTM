package ru.tm.taxi.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.tm.taxi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.makeText;

public class MainActivity extends Activity implements TextWatcher,OnClickListener
{
	String[] mContacts = {};

    SharedPreferences sPref;
    final private  String preference_uid = "uid";
    final private String preference_user_id = "user_id";
    final private String preference_user_name = "user_name";
    final private String preference_user_login = "user_login";

    // Корпус
    EditText _number_corp ;
// Номер дома
            EditText _number_home ;
    // Имя
    EditText _nameTextInput ;
// Проверочный код из смс
            EditText _passTextInput ;
    // номер телефона
    EditText _number ;
// ввод улицы
            AutoCompleteTextView mAutoComplete ;
    // Номер города
    Spinner sp ;

            My_Preferences_Worker preferences_worker;
    private boolean FLAG = true;

    @Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.relative_main);

        Initialize_vars();

        if (preferences_worker.get_user_id().length()!= 0)
        {

            Intent i = new Intent(this, EnterLayout.class);

            try
            {
                startActivity(i);
                return;

            }
            catch (Exception e)
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        spinner_load_cities();
	}

    private void Initialize_vars() {

        // Корпус
       _number_corp =(EditText) findViewById(R.id.LayMain_txt_corp);
    // Номер дома
       _number_home =(EditText) findViewById(R.id.LayMain_txt_home);
// Имя
        _nameTextInput = (EditText) findViewById(R.id.txt_enter_name);
// Проверочный код из смс
        _passTextInput =(EditText) findViewById(R.id.txt_confirm_pass);
// номер телефона
        _number = (EditText) findViewById(R.id.LayMain_phone);
// ввод улицы
        mAutoComplete =(AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
// Номер города
        sp =(Spinner) findViewById(R.id.spinner_city);

                preferences_worker =new My_Preferences_Worker(this);

        FLAG=true;

        EditText et = (EditText) findViewById(R.id.LayMain_phone);
        et.addTextChangedListener(
                new MaskWatcher()
        );


        //Toast.makeText(this,sPref.getString(preference_user_login,""),Toast.LENGTH_LONG);


        AutoCompleteTextView mAutoComplete = (AutoCompleteTextView) findViewById(R.id.LayMain_txt_street);
        mAutoComplete.addTextChangedListener(this);

        Spinner spr = (Spinner) findViewById(R.id.spinner_city);

        spr.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motion)
            {
                if(motion.getAction() == MotionEvent.ACTION_DOWN)
                {
                        spinner_load_cities();

                }
                return false;
            }
        });
    }

    public void spinner_load_cities()
    {
        if (Validation.isOnline(this) == false)
        {
             Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();

            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.spinner_city);

//        if(spr.getCount()==0)
//        {
            try
            {
                // получаем список городов от сервера
                JSONArray jsonArrayCity = getJsonArraCity();


                List<String> list = get_cities(jsonArrayCity);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // !  наполняем список городов
                spr.setAdapter(dataAdapter);

                List<String> id_list = new ArrayList<String>();



                for(int i=0; i<jsonArrayCity.length(); i++)
                {
                    JSONObject id = jsonArrayCity.getJSONObject(i);
                    id_list.add(id.getString("id"));
                }

                spr.setTag(id_list);

            }
            catch (Exception je)
            {
                Log.d("onClick", je.getMessage());
                // je.printStackTrace();
            }
        //}
    }


    private JSONArray getJsonArraCity()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();

        try {
            worker.execute(new JSONObject().put("get_city","lol"), "http://taxi-tm.ru/index/android_get_city");
            ja = worker.get().getJSONArray("city");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (Exception e){

        }

        return ja;

    }


    private List<String> get_cities(JSONArray ja)
    {

        List<String> list= new ArrayList<String>();
        try
        {

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

        if(FLAG == false)
        {

            return;
        }

        FLAG = false;
		if (Validation.isOnline(this) == false)
		{
            FLAG = true;
			Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_LONG).show();
			return;
		}

		// Поле ввода телефона
		EditText _number;
        _number = (EditText) findViewById(R.id.LayMain_phone);

        StringBuilder sb = new StringBuilder();

        for (char c : _number.getText().toString().toCharArray())
        {
            if(Character.isDigit(c))
                sb.append(c);
        };
        String valSrt = sb.toString();

		if (Validation.isNull(valSrt) == true)
		{
			Toast.makeText(this, R.string.notify_enter_number, Toast.LENGTH_SHORT).show();
			_number.requestFocus();
            FLAG = true;
			return;
		}


        //проверка на валидность 14 = ок
		if (valSrt.length() == 14)
		{
            FLAG = true;
			Toast.makeText(this, R.string.notify_incorrect_number, Toast.LENGTH_SHORT).show();
			_number.requestFocusFromTouch();
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

                 Toast.makeText(this, R.string.notify_wait_for_sms, Toast.LENGTH_SHORT).show();

                return;
			}
            else
            {
                if(reason.equals("wrong_number"))
                {
                    Toast.makeText(this, R.string.notify_incorrect_number, Toast.LENGTH_SHORT).show();
                    FLAG = true;
                }
                if(reason.equals("exists"))
                {
                    Toast.makeText(this, R.string.notify_number_exists, Toast.LENGTH_SHORT).show();
                    FLAG = true;
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




    //достаем номер из маски
        StringBuilder sb = new StringBuilder();

        for (char c : _number.getText().toString().toCharArray())
        {
            if(Character.isDigit(c))
                sb.append(c);
        };

    //получаем доступ к сохраннем настройкам
        sPref = getSharedPreferences("my_pref",MODE_PRIVATE);
        ArrayList<String> city_id_list= (ArrayList<String>) sp.getTag();

        //получаем информацию с полей
        String number = sb.toString();
        String userName =_nameTextInput.getText().toString();
        String city = ""+city_id_list.get( sp.getSelectedItemPosition());
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

                makeText(this, "Вы успешно зарегистрированны!", Toast.LENGTH_SHORT).show();
                preferences_worker.set_Number(number);
                preferences_worker.set_value("city",city);

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
                    makeText(this, "Не верный код из смс!", Toast.LENGTH_SHORT);
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

        if(s.length() == 2)
        {
            if (Validation.isOnline(this) == false)
            {
                Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();

                return;
            }
            JSONObject jo = new JSONObject();
            Spinner sp = (Spinner) findViewById(R.id.spinner_city);
            try
            {
                ArrayList<String> city_id_list= (ArrayList<String>) sp.getTag();


                String city = ""+city_id_list.get( sp.getSelectedItemPosition());
                jo.put("chars",s.toString());
                jo.put("city_id",city );

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
                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
                mAutoComplete.setAdapter(arrAd);

                mAutoComplete.showDropDown();
            }
            catch(Exception e)
            {

            }

        }
	}

    @Override
    public void onClick(View view) {

    }
}
