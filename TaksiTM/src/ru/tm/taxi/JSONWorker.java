package ru.tm.taxi;


import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONWorker
{
	/**
	 * ко
	 * @param jso
	 *            - Обьект содержащий отправляемые данные.
	 * @param uri
	 *            - Адресс сервера.
	 * @return Возвращаем ответ сервера, для обработки.
	 * 
	 */
	public static HttpResponse _SendJson(JSONObject jso, String uri) throws Exception
	{
        HttpResponse httpResponse = null;
        try
        {
            HttpParams httpParams = new BasicHttpParams();
            HttpClient client = new DefaultHttpClient(httpParams);
            int timeout = 5000;// кол-во милисекунд
            HttpConnectionParams.setConnectionTimeout(client.getParams(), timeout); // Timeout
                                                                                    // Limit
            HttpPost post = new HttpPost(uri);

            post.setEntity(new ByteArrayEntity(jso.toString().getBytes("UTF8")));
            post.setHeader("json", jso.toString());





                httpResponse = client.execute(post);

		}
		catch (ClientProtocolException e)
		{
            Log.d("_sendJSON_CLIENT", e.getMessage() + "__________" + e.toString());
			e.printStackTrace();

		}
		catch (IOException e)
		{
            Log.d("_sendJSON_IO", e.getMessage() + "__________" + e.toString());
			e.printStackTrace();
		}

		catch (Exception e)
		{
            Log.d("_sendJSON", e.getMessage() + "__________" + e.toString());
			e.printStackTrace();

		}

		return httpResponse;

	}

	/**
	 * 
	 * @param resp
	 *            - Получаемый ответ от сервера
	 * @return возвращаем строку возможно содержащую ok или denied;
	 */
	public static JSONObject _ReceiveJsonResponse(HttpResponse resp)
	{

		JSONObject obj = null;
		try
		{
			String jsonString = _inputStreamToString(resp.getEntity().getContent());
			obj = new JSONObject(jsonString);

		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return obj;
	}

	private static String _inputStreamToString(InputStream is)
	{
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try
		{
			do
			{
				rLine = rd.readLine();
				answer.append(rLine);

			} while (rLine != null);

		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		return answer.toString();
	}

}
