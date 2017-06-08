package com.imsdk.imdeveloper.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.imsdk.imdeveloper.app.IMConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/12/14.
 */
public class userDao {
    public static SQLiteDatabase sqLiteDatabase;


    public userDao() {
        File destDir = new File(IMConfiguration.path);
        if (!destDir.exists()) {
            destDir.mkdirs();
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(IMConfiguration.path+"user.db", null);
            sqLiteDatabase.execSQL("create table user(id varchar(12) primary key,passwd varchar(255))");
        } else {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(IMConfiguration.path+"user.db", null);
        }
    }

    public void createUser() {
        sqLiteDatabase.execSQL("create table if not exists user(id varchar(12) primary key,passwd varchar(255))");
    }
    public void deleteALl() {
        sqLiteDatabase.execSQL("delete from user");
    }
    public boolean insert(String id, String passwd) {
        try {
            sqLiteDatabase.execSQL("insert into user values(?,?)", new String[]{id, passwd});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public JSONObject query() {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user", null);
        JSONObject jsonObject = new JSONObject();
        cursor.moveToLast();
        try {
            if (cursor.getCount() > 0) {

                jsonObject.put("user", cursor.getString(0));
                jsonObject.put("passwd", cursor.getString(1));
            } else {
                jsonObject = null;
            }
            cursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean queryUser(String id) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user where id=?", new String[]{id});
        if (cursor.getCount() != 0) {
            return false;
        } else {
            return true;
        }
    }

    public void delete(String user) {
        sqLiteDatabase.execSQL("delete from user where id=?", new String[]{user});
    }

    public void destroy() {
        sqLiteDatabase.close();
    }
}
