package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import android.widget.Toast;
import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.R;
import org.json.JSONObject;

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

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.EnterLay);
        EditText number = (EditText) findViewById(R.id.LayEnter_txt_number);
        EditText pass = (EditText) findViewById(R.id.LayEnter_txt_pass);
    sPref = getSharedPreferences(preference_location,MODE_PRIVATE);

        number.setText(sPref.getString(preference_user_login,""));


	}



//    Скрипт должен называться android_login
//    Приложение отправляет запрос на вход в виде
//    {
//        number - мобильный телефон в качестве значения.
//        pass - пароль
//    }
//

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

//    Сервер опринимает данные и в ответ отправляет отчет в виде
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
//    }
    private Boolean parse_responce(JSONObject jo)
    {
        try {
          String resp =  jo.get("response").toString();



            if(resp.equals("ok"))
            {
                //TODO реализовать запись в файлики
                String id = jo.get("user_id").toString();
                String username = jo.get("username").toString();
                String userlogin = jo.get("userlogin").toString();

                sPref = getSharedPreferences("my_pref",MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();

                ed.putString(preference_user_id ,id);
                
                ed.putString(preference_user_name,username);
                ed.putString(preference_user_login,userlogin);
                ed.commit();
                return true;
            }
            if(resp.equals("denied"))
            {
            //TODO возможнотутприйдется занулять данныепользователя!

                return false;
                //Toast.makeText(this, "Неверная комбинация логин/пароль!",Toast.LENGTH_LONG);
            }
        }
        catch (Exception e)
        {}
        return false;

    }


    public void ent_buttonClick(View v)
	{
        EditText number = (EditText) findViewById(R.id.LayEnter_txt_number);
        EditText pass = (EditText) findViewById(R.id.LayEnter_txt_pass);



        // some validation

        JSONObject jo =login_to_server(pass.getText().toString(),number.getText().toString());

       if( parse_responce(jo) == false)
       {
           Toast.makeText(this, "Неверная комбинация логин/пароль!",Toast.LENGTH_SHORT);
           return;
       }


		Intent intent = new Intent(this, ChoiceLayout.class);
		try
		{

            sPref = getSharedPreferences(preference_location, MODE_PRIVATE);

            // имя грузим из файлика

            intent.putExtra("user_name",sPref.getString(preference_user_name,""));

			startActivity(intent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
