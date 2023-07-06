package com.example.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseCrud {
    SQLiteOpenHelper dbHandler;  //数据库处理器
    SQLiteDatabase db; //定义数据库

    //取出数据库的数据
    private static final String[] columns = {
            MyDatabaseHelper.ID,
            MyDatabaseHelper.TITLE,
            MyDatabaseHelper.AUTHOR,
            MyDatabaseHelper.CONTENT,
            MyDatabaseHelper.TIME,
            MyDatabaseHelper.MODE
    };

    //构造方法
    public MyDatabaseCrud(Context context) {
        //实例化SQLiteOpenHelper
        dbHandler = new MyDatabaseHelper(context);
    }

    //对数据库进行写入功能
    public void open() {
        db = dbHandler.getWritableDatabase();
    }

    //关闭数据库
    public void close() {
        dbHandler.close();
    }


    //添加笔记,把note加入到database里面
    public Diary addNote(Diary diary) {
        //添加一个笔记到数据库
        //专门处理数据的一个类，相当于一个内容值
        if(diary.getAuthor().isEmpty()) {
            diary.setAuthor("GKQ");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.TITLE, diary.getTitle());
        contentValues.put(MyDatabaseHelper.AUTHOR, diary.getAuthor());
        contentValues.put(MyDatabaseHelper.CONTENT, diary.getContent());
        contentValues.put(MyDatabaseHelper.TIME, diary.getTime());
        contentValues.put(MyDatabaseHelper.MODE, diary.getTog());
        long insertId = db.insert(MyDatabaseHelper.TABLE_NAME, null, contentValues);
        diary.setId(insertId);
        return diary;
    }

    //通过id获取Note数据
    public Diary getDiary(long id) {
        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, columns, MyDatabaseHelper.ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Diary note = new Diary(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5));
        return note;
    }

    //通过id获取Note数据
    public List<Diary> getAllNotes() {
        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, columns, null, null, null, null, MyDatabaseHelper.TIME + " DESC");
        List<Diary> list = new ArrayList<>();
        Diary diary = null;
        if (cursor.moveToFirst()) {
            do {
                diary = new Diary();
                diary.setId(cursor.getLong(cursor.getColumnIndex(MyDatabaseHelper.ID)));
                diary.setTitle(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.TITLE)));
                diary.setAuthor(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.AUTHOR)));
                diary.setContent(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.CONTENT)));
                diary.setTime(cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.TIME)));
                diary.setTog(cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.MODE)));
                list.add(diary);
            } while (cursor.moveToNext());
        }
        cursor.close(); // 关闭Cursor对象
        return list;
    }

    //修改笔记
    public int updateDiary(Diary diary) {
        if(diary.getAuthor().isEmpty()) {
            diary.setAuthor("GKQ");
        }
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.TITLE, diary.getTitle());
        values.put(MyDatabaseHelper.AUTHOR, diary.getAuthor());
        values.put(MyDatabaseHelper.CONTENT, diary.getContent());
        values.put(MyDatabaseHelper.TIME, diary.getTime());
        values.put(MyDatabaseHelper.MODE, diary.getTog());
        return db.update(MyDatabaseHelper.TABLE_NAME, values,
                MyDatabaseHelper.ID + "=?", new String[]{String.valueOf(diary.getId())});
    }

    //删除笔记
    public void removeNote(Diary diary) {
        db.delete(MyDatabaseHelper.TABLE_NAME, MyDatabaseHelper.ID + "=" + diary.getId(), null);
    }

}
