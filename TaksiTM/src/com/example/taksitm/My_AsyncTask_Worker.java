package com.example.taksitm;

import org.json.JSONObject;

import android.os.AsyncTask;

public class My_AsyncTask_Worker extends AsyncTask<Object, Void, JSONObject>
{
	public My_AsyncTask_Worker()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	protected JSONObject doInBackground(Object... params)
	{
		JSONObject Response = null;
		try
		{

			Response = JSONWorker._ReceiveJsonResponse(JSONWorker._SendJson((JSONObject) params[0], (String) params[1]));

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response;
	}

}
