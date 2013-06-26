package com.example.taksitm.layout;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.taksitm.R;

import android.os.Bundle;
import android.app.Activity;

public class ConfirmLayout extends Activity
{
    TextView from,to,comment,city,service,number,price;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_layout);
        from = (TextView) findViewById(R.id.LayConfirm_from);
        to = (TextView) findViewById(R.id.LayConfirm_to);
        comment = (TextView) findViewById(R.id.LayConfirm_comment);
        city = (TextView) findViewById(R.id.LayConfirm_city);
        service = (TextView) findViewById(R.id.LayConfirm_service);
        number= (TextView) findViewById(R.id.LayConfirm_number);
        price = (TextView) findViewById(R.id.LayConfirm_price);


        try
        {
            Intent i = getIntent();

            from.setText(i.getStringExtra("inception"));
            city.setText(i.getStringExtra("city"));
            service.setText(i.getStringExtra("service"));

            comment.setText(i.getStringExtra("comment"));
            number.setText(i.getStringExtra("number"));

            to.setText(i.getStringExtra("destination"));
            price.setText(i.getStringExtra("price"));




        }
        catch (Exception e)
        {
            Log.d("onload", e.getMessage());
        }
	}


    public void accept_order(View v)
    {
        setResult(2);
        finish();
    }
    public void edit_order(View v)
    {
       setResult(1);
        finish();
    }
    public void denied_order(View v)
    {
       setResult(0);
        finish();

    }


}
