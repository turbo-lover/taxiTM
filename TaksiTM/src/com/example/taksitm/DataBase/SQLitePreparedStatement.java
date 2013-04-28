package com.example.taksitm.DataBase;

import java.nio.ByteBuffer;

import android.util.Log;

public class SQLitePreparedStatement
{
	private SQLiteDatabase database;

	private boolean isFinalized = false;
	private int sqliteStatementHandle;

	private String querySql;
	private int queryArgsCount;
	private boolean finalizeAfterQuery = false;

	public int getStatementHandle()
	{
		return sqliteStatementHandle;
	}

	/**
	 * @throws SQLiteException
	 */
	public SQLitePreparedStatement(SQLiteDatabase db, String sql,
			boolean finalize) throws SQLiteException
	{
		database = db;

		querySql = sql;
		finalizeAfterQuery = finalize;

		sqliteStatementHandle = prepare(database.getSQLiteHandle(), sql);
	}

	/**
	 * @throws SQLiteException
	 */
	public SQLiteCursor query(Object[] args) throws SQLiteException
	{
		if (args == null || args.length != queryArgsCount)
		{
			throw new IllegalArgumentException();
		}

		checkFinalized();

		String[] names = reset(sqliteStatementHandle);

		int i = 1;// SQLite args indexed from 1
		for (Object obj : args)
		{
			if (obj == null)
			{
				bindNull(sqliteStatementHandle, i);
			}
			else if (obj instanceof Integer)
			{
				bindInt(sqliteStatementHandle, i, ((Integer) obj).intValue());
			}
			else if (obj instanceof Double)
			{
				bindDouble(sqliteStatementHandle, i, ((Double) obj).doubleValue());
			}
			else if (obj instanceof String)
			{
				bindString(sqliteStatementHandle, i, (String) obj);
			}
			else if (obj instanceof ByteBuffer)
			{
				ByteBuffer buf = (ByteBuffer) obj;
				if (!buf.isDirect())
				{
					throw new IllegalArgumentException("Only direct ByteBuffers are supported");
				}
				bindByteBuffer(sqliteStatementHandle, i, (ByteBuffer) obj);
			}
			else
			{
				throw new IllegalArgumentException();
			}
			i++;
		}

		return new SQLiteCursor(this, names);
	}

	/**
	 * @throws SQLiteException
	 */
	public SQLiteCursor requery() throws SQLiteException
	{
		checkFinalized();

		String[] names = reset(sqliteStatementHandle);

		return new SQLiteCursor(this, names);
	}

	public void dispose()
	{
		if (finalizeAfterQuery)
		{
			finalizeQuery();
		}
	}

	void checkFinalized() throws SQLiteException
	{
		if (isFinalized)
		{
			throw new SQLiteException("Prepared query finalized");
		}
	}

	public void finalizeQuery()
	{
		try
		{
			isFinalized = true;
			finalize(sqliteStatementHandle);
		}
		catch (SQLiteException e)
		{
			Log.e("sqlite", e.getMessage(), e);
		}
	}

	native void bindByteBuffer(int statementHandle, int index, ByteBuffer value) throws SQLiteException;

	native void bindString(int statementHandle, int index, String value) throws SQLiteException;

	native void bindInt(int statementHandle, int index, int value) throws SQLiteException;

	native void bindDouble(int statementHandle, int index, double value) throws SQLiteException;

	native void bindNull(int statementHandle, int index) throws SQLiteException;

	native String[] reset(int statementHandle) throws SQLiteException;

	native int prepare(int sqliteHandle, String sql) throws SQLiteException;

	native void finalize(int statementHandle) throws SQLiteException;

}
