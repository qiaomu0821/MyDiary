package com.example.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "diaries";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String MODE = "mode";
    public MyDatabaseHelper(Context context) {
        super(context, "diaries", null, 1);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT NOT NULL, "
                + AUTHOR + " TEXT NOT NULL, "
                + CONTENT + " TEXT NOT NULL, "
                + TIME + " TEXT NOT NULL, "
                + MODE + " INTEGER DEFAULT 1)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
