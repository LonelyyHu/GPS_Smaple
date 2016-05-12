package com.example.lonelyy.gps_smaple.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lonelyy on 2016/5/12.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gps_sample.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE_SQL =
            "CREATE TABLE " +
                    "gps_records (id INTEGER PRIMARY KEY,  trip_id INTEGER,  lon NUMERIC, lat NUMERIC, " +
                    "created_time TIMESTAMP default CURRENT_TIMESTAMP);";
    public static final String DROP_TABLE_SQL =
            "DROP TABLE IF EXISTS gps_records";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.println(Log.ERROR, "DBHelper-test-run", "DBHelper-test-run");
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onDropTable(db);
        onCreate(db);
    }

    public void onDropTable(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_SQL);
    }
}
