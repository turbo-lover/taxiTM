package com.example.taksitm.DataBase;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class SQLiteDatabase
{

	private final int sqliteHandle;
	private final String openedFileName;

	private final Map<String, SQLitePreparedStatement> preparedMap;
	private boolean isOpen = false;

	public int getSQLiteHandle()
	{
		return sqliteHandle;
	}

	/**
	 * @throws SQLiteException
	 */
	public SQLiteDatabase(String fileName) throws SQLiteException
	{
		openedFileName = fileName;
		sqliteHandle = opendb(fileName);
		isOpen = true;

		preparedMap = new HashMap<String, SQLitePreparedStatement>();
	}

	public boolean tableExists(String tableName) throws SQLiteException
	{
		checkOpened();
		String s = "SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;";

		return executeInt(s, tableName) != null;
	}

	public void execute(String sql, Object... args) throws SQLiteException
	{
		checkOpened();
		SQLiteCursor cursor = query(sql, args);
		try
		{
			cursor.next();
		}
		finally
		{
			cursor.dispose();
		}
	}

	public Integer executeInt(String sql, Object... args) throws SQLiteException
	{
		checkOpened();
		SQLiteCursor cursor = query(sql, args);
		try
		{
			if (!cursor.next())
			{
				return null;
			}
			return cursor.intValue(0);
		}
		finally
		{
			cursor.dispose();
		}
	}

	public int executeIntOrThrow(String sql, Object... args) throws SQLiteException, SQLiteNoRowException
	{
		checkOpened();
		Integer val = executeInt(sql, args);
		if (val != null)
		{
			return val.intValue();
		}

		throw new SQLiteNoRowException();
	}

	public String executeString(String sql, Object... args) throws SQLiteException
	{
		checkOpened();
		SQLiteCursor cursor = query(sql, args);
		try
		{
			if (!cursor.next())
			{
				return null;
			}
			return cursor.stringValue(0);
		}
		finally
		{
			cursor.dispose();
		}
	}

	public SQLiteCursor query(String sql, Object... args) throws SQLiteException
	{
		checkOpened();
		SQLitePreparedStatement stmt = preparedMap.get(sql);

		if (stmt == null)
		{
			stmt = new SQLitePreparedStatement(this, sql, false);
			preparedMap.put(sql, stmt);
		}

		return stmt.query(args);
	}

	public SQLiteCursor queryFinalized(String sql, Object... args) throws SQLiteException
	{
		checkOpened();
		return new SQLitePreparedStatement(this, sql, true).query(args);
	}

	public void close()
	{
		if (isOpen)
		{
			try
			{
				for (SQLitePreparedStatement stmt : preparedMap.values())
				{
					stmt.finalizeQuery();
				}

				closedb(sqliteHandle);
			}
			catch (SQLiteException e)
			{
				Log.e("sqlite", e.getMessage(), e);
			}
			isOpen = false;
		}
	}

	void checkOpened() throws SQLiteException
	{
		if (!isOpen)
		{
			throw new SQLiteException("Database closed");
		}
	}

	public void finalize()
	{
		close();
	}

	static
	{
		/*
		 * Этот вызов нужен для прогрузки класса ByteBuffer, если этого не
		 * сделать будет ошибка при чтении BLOB данных с базы данных. see
		 * http://
		 * groups.google.com/group/android-developers/browse_thread/thread
		 * /c5a6574a0c01759e for details
		 */
		ByteBuffer.allocate(0);
	}

	native int opendb(String fileName) throws SQLiteException;

	native void closedb(int sqliteHandle) throws SQLiteException;
}
