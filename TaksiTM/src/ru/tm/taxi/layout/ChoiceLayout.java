package ru.tm.taxi.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import ru.tm.taxi.My_Preferences_Worker;
import ru.tm.taxi.R;

public class ChoiceLayout extends Activity
{
    @Override
    protected void onResume()
    {
        super.onResume();

        tw_username.setText(preferences_worker.get_user_name());
    }
    TextView tw_username;
    My_Preferences_Worker preferences_worker;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_layout);

        preferences_worker = new My_Preferences_Worker(this);

        Intent intent = getIntent();

        String u_n = intent.getStringExtra("user_name");

        preferences_worker.set_value("username",u_n);
        tw_username = (TextView) findViewById(R.id.LayChoise_user_name);

        tw_username.setText(u_n);
	}

	public void to_Order_lay(View v)
	{
		Intent intent = new Intent(this, OrderLayout.class);
        intent.putExtra("previous",ChoiceLayout.class.toString());
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
        Intent intent = new Intent(this,EnterLayout.class);
        startActivity(intent);
       finish();

    }

}
