package com.example.taksitm.layout;

import com.example.taksitm.R;
import com.example.taksitm.R.layout;
import com.example.taksitm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HistoryLayout extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_layout, menu);
		return true;
	}

}
