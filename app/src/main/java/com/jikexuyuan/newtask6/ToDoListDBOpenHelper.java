package com.jikexuyuan.newtask6;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sparrow on 16-10-7.
 */

public class ToDoListDBOpenHelper extends SQLiteOpenHelper {
    public static final String KEY_ID = "_id";
    public static final String KEY_TODO_TIME_COLUMN = "TODO_TIME_COLUMN";
    public static final String KEY_TODO_THINGS_COLUMN = "TODO_THINGS_COLUMN";

    private static final String DATABASE_NAME = "todoList.db";
    public static final String DATABASE_TABLE = "Items";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE
            + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TODO_TIME_COLUMN + " INTEGER NOT NULL,"
            + KEY_TODO_THINGS_COLUMN + " TEXT NOT NULL);";

    public ToDoListDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
