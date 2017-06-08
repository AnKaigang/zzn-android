package com.imsdk.imdeveloper.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/12/25.
 */
public class NotifyDao {
    String path = "/sdcard/iyanda";
    SQLiteDatabase sqLiteDatabase;

    public NotifyDao() {
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/sdcard/iyanda/notify.db", null);
            sqLiteDatabase.execSQL("create table notify(id varchar(12) primary key,title varchar(255))");
        } else {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/sdcard/iyanda/notify.db", null);
        }
    }

    public void createNotify() {
        sqLiteDatabase.execSQL("create table if not exists notify(id varchar(12) primary key,title varchar(255))");
    }

    public boolean insert(String id, String title) {
        try {
            sqLiteDatabase.execSQL("insert into notify values(?,?)", new String[]{id, title});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public JSONArray query() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from notify", null);
        JSONArray jsonArray = new JSONArray();
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("title", cursor.getString(1));
                    jsonArray.put(jsonObject);
                }
            } else {
                jsonArray = null;
            }
            cursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public boolean queryNotifyExists(String id) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from notify where id=?", new String[]{id});
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public void delete(String user) {
        sqLiteDatabase.execSQL("delete from notify where id=?", new String[]{user});
    }

    public void destroy() {
        sqLiteDatabase.close();
    }

    public void deleteAll() {
        sqLiteDatabase.execSQL("delete from notify");
    }
}
