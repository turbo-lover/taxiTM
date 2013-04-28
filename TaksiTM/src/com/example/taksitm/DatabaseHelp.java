package com.example.taksitm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelp extends SQLiteOpenHelper
{

	// ����������� ��������� ���� � ���� ������ ����������

	private static String DB_PATH = "/data/data/com.example.taksitm/databases/";

	private static String DB_NAME = "street.sqlite";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * 13 ����������� 14 ��������� � ��������� ������ �� ���������� �������� ���
	 * ������� � �������� ���������� 15
	 * 
	 * @param context
	 * 
	 */
	public DatabaseHelp(Context context)
	{

		super(context, DB_NAME, null, 1);

		this.myContext = context;

	}

	/**
	 * C������ ������ ���� ������ � �������������� �� ����� ����������� �����
	 * 
	 * */
	public void createDataBase() throws IOException
	{

		boolean dbExist = checkDataBase();

		if (dbExist)
		{

			// ������ �� ������ - ���� ��� ����

		}
		else
		{

			// ������� ���� ����� ������� ������ ����, ����� ��� �����
			// ������������

			this.getReadableDatabase();

			try
			{

				copyDataBase();

			}
			catch (IOException e)
			{

				throw new Error("Error copying database");

			}

		}

	}

	/**
	 * ���������, ���������� �� ��� ��� ����, ����� �� ���������� ������ ��� ���
	 * ������� ����������
	 * 
	 * @return true ���� ����������, false ���� �� ����������
	 */
	private boolean checkDataBase()
	{

		SQLiteDatabase checkDB = null;

		try
		{

			String myPath = DB_PATH + DB_NAME;

			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}
		catch (SQLiteException e)
		{

			// ���� ��� �� ����������

		}

		if (checkDB != null)
		{

			checkDB.close();

		}

		return checkDB != null ? true : false;

	}

	/**
	 * �������� ���� �� ����� assets ������� ��������� ��������� �� �����������
	 * ����� ����������� ������ ������.
	 * */
	private void copyDataBase() throws IOException
	{

		// ��������� ��������� �� ��� �������� �����

		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// ���� �� ����� ��������� ��

		String outFileName = DB_PATH + DB_NAME;

		// ��������� ������ ���� ������ ��� ��������� �����

		OutputStream myOutput = new FileOutputStream(outFileName);

		// ���������� ����� �� ��������� ����� � ���������

		byte[] buffer = new byte[1024];
		int length;

		while ((length = myInput.read(buffer)) > 0)
		{

			myOutput.write(buffer, 0, length);

		}

		// ��������� ������

		myOutput.flush();

		myOutput.close();

		myInput.close();

	}

	public void openDataBase() throws SQLException
	{

		// ��������� ��

		String myPath = DB_PATH + DB_NAME;

		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close()
	{

		if (myDataBase != null)

			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

	public Cursor getStreetName(String str)
	{

		return myDataBase.rawQuery("select street from street where street like '%"
				+ str + "%' AND like;", null);
	}
	// ����� ����� �������� ��������������� ������ ��� ������� � ���������
	// ������ �� ��

	// �� ������ ���������� ������� ����� "return myDataBase.query(....)", ���
	// �������� �� �������������

	// � �������� ��������� ��� ����� view

}
