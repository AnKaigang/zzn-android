package com.imsdk.imdeveloper.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.imsdk.imdeveloper.app.IMConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/12/25.
 */
public class ClubDao {
    public static SQLiteDatabase clubSqLiteDatabase;
    public ClubDao(String stuId){

        File destDir = new File(IMConfiguration.path + stuId);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        clubSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(IMConfiguration.path + stuId + "/club.db", null);
    }
    public void createIdentify(){
        clubSqLiteDatabase.execSQL("create table if not exists identify(id varchar(36) primary key,clubId varchar(50),position varchar(20))");
    }


    public void createClubInfo(){
        clubSqLiteDatabase.execSQL("create table if not exists clubInformation(clubId varchar(36) primary key,clubName varchar(20),clubIntro varchar(500),clubType varchar(50))");
    }
    public boolean insertIdentify(String id, String name,String position) {
        try {
            clubSqLiteDatabase.execSQL("insert into identify values(?,?,?)", new String[]{id, name,position});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public void deleteIdentify() {
        clubSqLiteDatabase.execSQL("delete from identify");
    }
    public Cursor queryIdentify() {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select * from identify", null);
        return cursor;
    }
    public boolean queryIdentifyIsExitsById(String id) {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select * from identify where id=?", new String[]{id});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
    public String queryIdentifyClubPosition(String clubid) {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select * from identify where clubId=?", new String[]{clubid});
        String result="";
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()){
                result= cursor.getString(2);
            }
        } else {
            cursor.close();
            result=null;
        }
        return result;
    }


    public JSONObject queryClubInfo(String name) {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select * from clubInformation where clubName=?", new String[]{name});
        JSONObject jsonObject = new JSONObject();
        cursor.moveToLast();
        try {
            if (cursor.getCount()>0) {
                jsonObject.put("id", cursor.getString(0));
                jsonObject.put("name", cursor.getString(1));
                jsonObject.put("intro", cursor.getString(2));
                jsonObject.put("type", cursor.getString(3));
            } else {
                jsonObject = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cursor.close();
        return jsonObject;
    }
    public String queryClubNameByClubId(String communityid) {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select clubName from clubInformation where clubId=?", new String[]{communityid});
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getString(0);
        } else {
            cursor.close();
            return null;
        }
    }
    public boolean queryClubClubInfoExists(String id) {
        Cursor cursor = clubSqLiteDatabase.rawQuery("select * from clubInformation where clubId=?", new String[]{id});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
    public boolean insertClubInfo(String id, String name,String intro,String type) {
        try {
            clubSqLiteDatabase.execSQL("insert into clubInformation values(?,?,?,?)", new String[]{id, name,intro,type});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public void destroy(){
        if(clubSqLiteDatabase!=null) {
            clubSqLiteDatabase.close();
        }
    }



}
