package com.example.taksitm.layout;


import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.taksitm.R;
import com.example.taksitm.R.layout;
import com.example.taksitm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.example.taksitm.composite_history;

public class HistoryLayout extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_layout);

        LinearLayout ll = (LinearLayout)findViewById(R.id.LayHistory_content);

        try
        {
            for (int i=10;i>0;i--)
            {
            composite_history ch = new composite_history(this);

            ch.add_destination("Шлюшная 3");
            ch.add_destination("Шлюшная 5");
            ch.add_header("девушка делает ой, омлет");

            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            ll.addView(ch);
            }
        }
        catch (Exception e)
        {
            Log.d("history",e.getMessage());
        }
	}

}
