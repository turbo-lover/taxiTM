package com.example.taksitm.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taksitm.R;

public class EnterLayout extends Activity
{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.enter_layout);

	}

	public void ent_buttonClick(View v)
	{
		// some validation

		Intent intent = new Intent(this, ChoiceLayout.class);
		try
		{

			startActivity(intent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
