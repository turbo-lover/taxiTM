#include <jni.h>

#include "sqlite3.h"
#include <math.h>
#include "android_sqlite.h"

#define DEG2RAD(degrees) (degrees * 0.01745327)
static void distanceFunc(sqlite3_context *context, int argc, sqlite3_value **argv)
{
	// проверить, что аргументы нормальны
	if (argc != 4 || sqlite3_value_type(argv[0]) == SQLITE_NULL || sqlite3_value_type(argv[1]) == SQLITE_NULL || sqlite3_value_type(argv[2]) == SQLITE_NULL || sqlite3_value_type(argv[3]) == SQLITE_NULL) {
		sqlite3_result_null(context);
		return;
	}
	// берем все аргументы
	double itemLat = sqlite3_value_double(argv[0]);
	double itemLong = sqlite3_value_double(argv[1]);
	double userLat = sqlite3_value_double(argv[2]);
	double userLong = sqlite3_value_double(argv[3]);
	
	double itemLatRad = DEG2RAD(itemLat);
	double userLatRad = DEG2RAD(userLat);
	// тут используется сферический закон
	// 6378.1 примерный радиус Земли в километрах
	sqlite3_result_double(context, acos(sin(itemLatRad) * sin(userLatRad) + cos(itemLatRad) * cos(userLatRad) * cos(DEG2RAD(itemLong) - DEG2RAD(userLong))) * 6378.1);
}

void Java_biz_sneg_sqlite_SQLiteDatabase_closedb(JNIEnv* env, jobject object, int sqliteHandle) {
	sqlite3* handle = (sqlite3*) sqliteHandle;

	int err = sqlite3_close(handle);

	if (SQLITE_OK != err) {
		throw_sqlite3_exception(env, handle, err);
	}
}

int Java_biz_sneg_sqlite_SQLiteDatabase_opendb(JNIEnv* env, jobject object, jstring fileName) {
    char const * fileNameStr = (*env)->GetStringUTFChars(env, fileName, 0);

    sqlite3 * handle = 0;
    //LOGI("Open");
	//LOGI(fileNameStr);
    int err = sqlite3_open_v2(fileNameStr, &handle, SQLITE_OPEN_READWRITE | SQLITE_OPEN_PRIVATECACHE, 0);
    if (SQLITE_OK != err) {
    	//(*env)->SetIntField(env, object, offset_sqliteHandle, (int) handle);
    	throw_sqlite3_exception(env, handle, err);
    }
	sqlite3_create_function(handle, "geodistance", 4, SQLITE_UTF8, NULL, &distanceFunc, NULL, NULL);
    if (fileNameStr != 0) (*env)->ReleaseStringUTFChars(env, fileName, fileNameStr);

    return (int)handle;
}
