package ru.tm.taxi;

import android.util.Log;
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
		JSONObject Response = new JSONObject();
		try
		{

			Response = JSONWorker._ReceiveJsonResponse(JSONWorker._SendJson((JSONObject) params[0], (String) params[1]));

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
            Log.d("doInbackground",e.getMessage() + "__________"+ e.toString());
            e.printStackTrace();
		}
		return Response;
	}

}
