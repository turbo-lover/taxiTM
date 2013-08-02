package ru.tm.taxi;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Validation
{
	public static Boolean isNull(String str)
	{
		if (str.length() == 0)
			return true;
		return false;
	}

	public static Boolean isOnline(Context cntPack)
	{
		ConnectivityManager cm = (ConnectivityManager) cntPack.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();


		if (nInfo != null && nInfo.isConnected())
		    //connection is good
		    return true;
		else
        {

			return false;
        }
	}

}
