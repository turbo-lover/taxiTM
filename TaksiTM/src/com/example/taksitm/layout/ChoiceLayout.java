package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taksitm.R;

public class ChoiceLayout extends Activity
{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_layout);
	}

	public void to_Order_lay(View v)
	{
		Intent intent = new Intent(this, OrderLayout.class);
		startActivity(intent);

	}

	public void to_History(View v)
	{
		Intent intent = new Intent(this, OrderLayout.class);
		startActivity(intent);

	}

	public void to_Settings(View v)
	{

	}

}
