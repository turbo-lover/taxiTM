package com.example.taksitm.DataBase;

import java.nio.ByteBuffer;
import java.util.HashMap;

import android.util.Log;

public class SQLiteCursor
{

	public static final int FIELD_TYPE_INT = 1;
	public static final int FIELD_TYPE_FLOAT = 2;
	public static final int FIELD_TYPE_STRING = 3;
	public static final int FIELD_TYPE_BYTEBUFFER = 4;
	public static final int FIELD_TYPE_NULL = 5;

	SQLitePreparedStatement preparedStatement;
	boolean inRow = false;
	HashMap<String, Integer> columnNames = new HashMap<String, Integer>();

	public SQLiteCursor(SQLitePreparedStatement stmt, String[] names)
	{
		preparedStatement = stmt;

		for (int i = 0; i < names.length; i++)
		{
			columnNames.put(names[i], i);
		}
	}

	public Iterable<String> fields()
	{
		return columnNames.keySet();
	}

	public int columnIndex(String column) throws SQLiteException
	{
		Integer colIndex = columnNames.get(column);
		if (colIndex == null)
		{
			throw new SQLiteException();
		}

		return colIndex.intValue();
	}

	public boolean isNull(String column) throws SQLiteException
	{
		return isNull(columnIndex(column));
	}

	public int intValue(String column) throws SQLiteException
	{
		return intValue(columnIndex(column));
	}

	public String stringValue(String column) throws SQLiteException
	{
		return stringValue(columnIndex(column));
	}

	public double doubleValue(String column) throws SQLiteException
	{
		return doubleValue(columnIndex(column));
	}

	public ByteBuffer byteBufferValue(String column) throws SQLiteException
	{
		return byteBufferValue(columnIndex(column));
	}

	public int getTypeOf(String column) throws SQLiteException
	{
		return getTypeOf(columnIndex(column));
	}

	public Object objectValue(String column) throws SQLiteException
	{
		return objectValue(columnIndex(column));
	}

	public boolean isNull(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnIsNull(preparedStatement.getStatementHandle(), columnIndex) == 1;
	}

	public int intValue(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnIntValue(preparedStatement.getStatementHandle(), columnIndex);
	}

	public double doubleValue(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnDoubleValue(preparedStatement.getStatementHandle(), columnIndex);
	}

	public String stringValue(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnStringValue(preparedStatement.getStatementHandle(), columnIndex);
	}

	public String columnName(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnName(preparedStatement.getStatementHandle(), columnIndex);
	}

	/**
	 * Возвращаемый ByteBuffer доступен только до
	 * перемещения курсора или повторного
	 * вызова запроса
	 * 
	 * @param columnIndex
	 * @return
	 * @throws SQLiteException
	 */
	public ByteBuffer byteBufferValue(int columnIndex) throws SQLiteException
	{
		checkRow();

		ByteBuffer buf = columnByteBufferValue(preparedStatement.getStatementHandle(), columnIndex);
		if (buf != null)
		{
			return buf.asReadOnlyBuffer();
		}
		return null;
	}

	public int getTypeOf(int columnIndex) throws SQLiteException
	{
		checkRow();

		return columnType(preparedStatement.getStatementHandle(), columnIndex);
	}

	public Object objectValue(int columnIndex) throws SQLiteException
	{
		checkRow();

		int type = columnType(preparedStatement.getStatementHandle(), columnIndex);
		switch (type) {
		case FIELD_TYPE_INT:
			return intValue(columnIndex);
		case FIELD_TYPE_BYTEBUFFER:
			return byteBufferValue(columnIndex);
		case FIELD_TYPE_FLOAT:
			return doubleValue(columnIndex);
		case FIELD_TYPE_STRING:
			return stringValue(columnIndex);
		}
		return null;
	}

	public boolean next()
	{
		int res = step(preparedStatement.getStatementHandle());
		if (res == -1)
			Log.d("mmmmmm", "res =  -1 ");
		inRow = ((res) == 0);
		return inRow;
	}

	public SQLiteCursor reset() throws SQLiteException
	{
		return preparedStatement.requery();
	}

	public int getStatementHandle()
	{
		return preparedStatement.getStatementHandle();
	}

	public int count()
	{
		return columnCount(preparedStatement.getStatementHandle());
	}

	public void dispose()
	{
		preparedStatement.dispose();
	}

	void checkRow() throws SQLiteException
	{
		if (!inRow)
		{
			throw new SQLiteException("You must call next before");
		}
	}

	native int columnCount(int statementHandle);

	native int columnType(int statementHandle, int columnIndex);

	native int columnIsNull(int statementHandle, int columnIndex);

	native int columnIntValue(int statementHandle, int columnIndex);

	native double columnDoubleValue(int statementHandle, int columnIndex);

	native String columnStringValue(int statementHandle, int columnIndex);

	native ByteBuffer columnByteBufferValue(int statementHandle, int columnIndex);

	native String columnName(int statementHandle, int columnIndex);

	native int step(int statementHandle);
}
