package com.example.taksitm.layout;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.R;
import com.example.taksitm.Validation;

public class MainActivity extends Activity
{
	String[] data = { "one", "two", "three", "four", "five" };

	Boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		flag = false;

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (Validation.isOnline(this) == false)
		{

			Toast.makeText(this, "����������� ����������� � ����������", Toast.LENGTH_SHORT).show();
			return;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)

	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void btn_RequestPass(View v)
	{
		// ��������
		// �������� ����� ��������,
		// � ������� � json ������
		// ���� number!

		if (Validation.isOnline(this) == false)
		{

			Toast.makeText(this, "����������� ����������� � ����������", Toast.LENGTH_LONG).show();
			return;
		}

		EditText et = (EditText) findViewById(R.id.txt_telephone_number_main);

		String valSrt = et.getText().toString();
		if (Validation.isNull(valSrt))
		{
			Toast.makeText(this, "������� �����", Toast.LENGTH_SHORT).show();
			et.requestFocus();
			return;
		}

		// ��������� �����

		if (et.getText().length() < 9)
		{

			Toast.makeText(this, "������������ �����", Toast.LENGTH_SHORT).show();
			et.requestFocusFromTouch();
			return;
		}

		try
		{

			/*
				JSONObject jo = new JSONObject();
				jo.put("number", et.getText().toString());
				
				HttpResponse res = JSONWorker._SendJson(jo, "http://taxi.br-studio.com.ua/android/httppost.php");
				
				jo = JSONWorker._ReceiveJsonResponse(res);
			*/
			My_AsyncTask_Worker d = new My_AsyncTask_Worker();
			JSONObject jo = new JSONObject();
			jo.put("number", et.getText().toString());

			d.execute(jo, "http://taxi-tm.ru/index/");
			jo = d.get();

			String response = jo.get("response").toString();
			String reason = jo.get("reason").toString();

			if (response.equals("ok"))
			{
				// save.reason
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void btn_Register(View v)
	{

		//
		if (flag)
		{

		}

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

}
