package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import com.example.taksitm.My_Preferences_Worker;
import com.example.taksitm.R;

public class ChoiceLayout extends Activity
{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_layout);

        Intent intent = getIntent();

       String u_n = intent.getStringExtra("user_name");

        TextView tw_username = (TextView) findViewById(R.id.LayChoise_user_name);

        tw_username.setText(u_n);
	}

	public void to_Order_lay(View v)
	{
		Intent intent = new Intent(this, OrderLayout.class);
        intent.putExtra("previous",ChoiceLayout.class);
		startActivity(intent);

	}

	public void to_History(View v)
	{
		Intent intent = new Intent(this, HistoryLayout.class);
		startActivity(intent);

	}

	public void to_Settings(View v)
	{
        Intent i = new Intent(this,SettingLayout.class);

        startActivity(i);
	}

    // TODO удаление данных пользователя
    public void log_out(View v)
    {
       finish();

    }

}
