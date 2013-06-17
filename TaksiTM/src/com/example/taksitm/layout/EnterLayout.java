package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import com.example.taksitm.*;

import org.json.JSONObject;

import static android.widget.Toast.makeText;

public class EnterLayout extends Activity
{


    private SharedPreferences sPref;
    final private String preference_user_id = "user_id";
    final private String preference_user_name = "user_name";
    final private String preference_user_login = "user_login";
    final private String preference_location = "my_pref";

    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_layout);

        //TODO добавить пароль в pass
        EditText number = (EditText) findViewById(R.id.LayEnter_txt_number);
        number.addTextChangedListener(new MaskWatcher());
        EditText pass = (EditText) findViewById(R.id.LayEnter_txt_pass);
        sPref = getSharedPreferences(preference_location,MODE_PRIVATE);

        number.setText(sPref.getString(preference_user_login,""));


	}




    JSONObject login_to_server(String pass,String login)
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        JSONObject jo = new JSONObject();
       if(login.length() >= 10)
       {
           if (login.length() != 0)
           {

               try
               {
                  jo.put("number", login);
                  jo.put("pass",pass);

                   worker.execute(jo, "http://taxi-tm.ru/index/android_login");

                   jo =worker.get();
                   
                    return jo;
               }
               catch(Exception e)
               {
                   Log.d("login_to_server", e.getMessage());
               }
           }
       }
        return jo;
    }

/*/    Сервер опринимает данные и в ответ отправляет отчет в виде
//    {
//        response - со значением вида ok / denied:
//        * ok – если номер и пароль правильный
//            * denied – выдается в случае неправильного логина или пароля
//        reason -  со значением вида
//        * wrong_login – номер некорректен/ такой номер не существует.
//        если response ok
//            * user_id -  id пользователя
//            * username - имя пользователся
//            * userlogin - логин/номер телефона
//    }*/
    private Boolean parse_responce(JSONObject jo)
    {
        My_Preferences_Worker pref_work = new My_Preferences_Worker(this);

        try {
          String resp =  jo.get("response").toString();



            if(resp.equals("ok"))
            {
                //TODO реализовать запись в файлики
                String id = jo.get("user_id").toString();
                String username = jo.get("username").toString();
                String userlogin = jo.get("userlogin").toString();
                String user_address = jo.getString("user_address");
                String user_address_house = jo.getString("user_address_house");
                String user_address_corpus = jo.getString("user_address_corpus");

                sPref = getSharedPreferences(preference_location,MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();

                ed.putString(preference_user_id ,id);
                ed.putString(preference_user_name,username);
                ed.putString(preference_user_login,userlogin);
                
                pref_work.set_user_address(user_address);
                pref_work.set_user_address_corpus(user_address_corpus);
                pref_work.set_user_address_house(user_address_house);


                ed.commit();
                return true;
            }
            if(resp.equals("denied"))
            {
            //TODO возможно тут прийдется занулять данные пользователя!

                return false;
                //Toast.makeText(this, "Неверная комбинация логин/пароль!",Toast.LENGTH_LONG);
            }
        }
        catch (Exception e)
        {}
        return false;

    }

//обработка нажатия конопки входа
    public void ent_buttonClick(View v)
	{
        EditText number = (EditText) findViewById(R.id.LayEnter_txt_number);
        EditText pass = (EditText) findViewById(R.id.LayEnter_txt_pass);

        // some validation
        if(Validation.isOnline(this)==false)
        {
            makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();

            return;
        }

        StringBuilder sb = new StringBuilder();

        for (char c : number.getText().toString().toCharArray())
        {
                if(Character.isDigit(c))
                    sb.append(c);
        };

        String password = pass.getText().toString();
        String login = sb.toString();

///////////////////////////////////////////////////////////////////////////////
        if(Validation.isNull(password)  == true)
        {
            makeText(this, R.string.notify_enter_pass,Toast.LENGTH_SHORT).show();
            return;
        }
        if (Validation.isNull(login) == true)
        {
            makeText(this, R.string.notify_enter_number,Toast.LENGTH_SHORT).show();
            return;
        }
////////////////////////////////////////////////////////////////////////////

        JSONObject jo = login_to_server(password, login);

        //в parse_responce  происходит запись значений из ответа в файлы
       if( parse_responce(jo) == false)
       {
           makeText(this, R.string.notify_pass_or_login_wrong, Toast.LENGTH_SHORT).show();

           return;
       }


		Intent intent = new Intent(this, ChoiceLayout.class);
		try
		{

            sPref = getSharedPreferences(preference_location, MODE_PRIVATE);

            // имя грузим из файлика

            intent.putExtra("user_name",sPref.getString(preference_user_name,""));


			startActivity(intent);

            finish();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
