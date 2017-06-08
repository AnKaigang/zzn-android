package com.imsdk.imdeveloper.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.imsdk.imdeveloper.app.IMConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2015/12/25.
 */
public class ClubListDao {
    public  static SQLiteDatabase clubListSqLiteDatabase;
    public ClubListDao(String stuIdGroup) {

        File destDir = new File(IMConfiguration.path+stuIdGroup);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        clubListSqLiteDatabase = clubListSqLiteDatabase.openOrCreateDatabase(IMConfiguration.path+stuIdGroup + "/clubList.db", null);
    }
    public void createIdName(){
        clubListSqLiteDatabase.execSQL("create table if not exists idName(userid varchar(36) primary key,username varchar(36))");
    }
    public boolean insertIdName(String id,String name) {
        try {
            clubListSqLiteDatabase.execSQL("insert into idName values(?,?)", new String[]{id,name});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public JSONArray queryIdNameAll() {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from idName", null);
        JSONArray jsonArray=new JSONArray();
        while (cursor.moveToNext()){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("id",cursor.getString(0));
                jsonObject.put("date",cursor.getString(1));
                jsonObject.put("content",cursor.getString(2));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    public String queryIdNameByUserId(String id) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from idName where userid=?", new String[]{id});
        JSONObject jsonObject=new JSONObject();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getString(1);
        } else {
            cursor.close();
            return null;
        }
    }
    public String queryIdNameByUserName(String name) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from idName where userName=?", new String[]{name});
        JSONObject jsonObject=new JSONObject();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getString(0);
        } else {
            cursor.close();
            return null;
        }
    }
    public boolean queryIdNameIsExistsByUserId(String id) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from idName where userid=?", new String[]{id});
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }
    
    
    public void createRelation(){
        clubListSqLiteDatabase.execSQL("create table if not exists relation(id varchar(36) primary key,userId varchar(20),position varchar(100))");
    }
    public boolean insertRelation(String id, String uerid, String position) {
        clubListSqLiteDatabase.execSQL("insert into relation values(?,?,?)", new String[]{id, uerid, position});
        return true;
    }
    /*public JSONObject queryAllRelation() {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from relation", null);
        JSONObject jsonObject = new JSONObject();
        cursor.moveToLast();
        try {
            if (cursor.getCount() > 0) {

                jsonObject.put("user", cursor.getString(0));
                jsonObject.put("passwd", cursor.getString(1));
            } else {
                jsonObject = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cursor.close();
        return jsonObject;
    }*/
    public boolean queryRelationIsExists(String id) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from relation where userId=?", new String[]{id});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public String queryRelationByUserName(String userId) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from relation where userId=?", new String[]{userId});
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return cursor.getString(2);
        } else {
            return "";
        }
    }
    public void deleteRelation() {
        clubListSqLiteDatabase.execSQL("delete from relation");
    }
    public JSONArray queryRelationByPosition(String position) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from relation where position=?",new String[]{position});
        JSONArray jsonArray=new JSONArray();
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()){
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("id", cursor.getString(0));
                    jsonObject.put("userid", cursor.getString(1));
                    jsonObject.put("position",cursor.getString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        } else {
            jsonArray=null;
        }
        return jsonArray;
    }
    
    
    public void createEvent(){
        clubListSqLiteDatabase.execSQL("create table if not exists event(id varchar(36) primary key,date varchar(20),content varchar(500),title varchar(500))");
    }
    public boolean insertEvent(String id, String date,String content,String title) {
        try {
            clubListSqLiteDatabase.execSQL("insert into event values(?,?,?,?)", new String[]{id, date,content,title});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public JSONArray queryAllEvent() {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from event", null);
        JSONArray jsonArray=new JSONArray();
        while (cursor.moveToNext()){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("id",cursor.getString(0));
                jsonObject.put("date",cursor.getString(1));
                jsonObject.put("content",cursor.getString(2));
                jsonObject.put("title",cursor.getString(3));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    public JSONObject queryEventIsExistsByContent(String name) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from event where content=?", new String[]{name});
        JSONObject jsonObject=new JSONObject();
        if (cursor.getCount() > 0) {
            cursor.moveToLast();

            try {
                jsonObject.put("date", cursor.getString(1));
                jsonObject.put("id", cursor.getString(0));
                jsonObject.put("content", cursor.getString(2));
                jsonObject.put("title",cursor.getString(3));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            cursor.close();
            jsonObject=null;
        }
        return jsonObject;
    }
    public boolean queryEventIsExistsById(String name) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from event where id=?", new String[]{name});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }


    public void createFile(){
        clubListSqLiteDatabase.execSQL("create table if not exists file(id varchar(36) primary key,date varchar(20),fileName varchar(500))");
    }
    public boolean insertFile(String id, String date,String content) {
        try {
            clubListSqLiteDatabase.execSQL("insert into file values(?,?,?)", new String[]{id, date,content});
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public JSONArray queryAllFile() {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from file order by date", null);
        JSONArray jsonArray=new JSONArray();
        while (cursor.moveToNext()){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("id",cursor.getString(0));
                jsonObject.put("date",cursor.getString(1));
                jsonObject.put("content",cursor.getString(2));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    public JSONObject queryFileIsExistsByFileName(String name) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from file where fileName=?", new String[]{name});
        JSONObject jsonObject=new JSONObject();
        if (cursor.getCount() > 0) {
            cursor.moveToLast();

            try {
                jsonObject.put("date", cursor.getString(1));
                jsonObject.put("id", cursor.getString(0));
                jsonObject.put("fileName", cursor.getString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            cursor.close();
            jsonObject=null;
        }
        return jsonObject;
    }
    public boolean queryFileIsExistsById(String id) {
        Cursor cursor = clubListSqLiteDatabase.rawQuery("select * from file where id=?", new String[]{id});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }


    public void destroy(){
        if(clubListSqLiteDatabase!=null) {
            clubListSqLiteDatabase.close();
        }
    }


    public void deleteEvent() {
        clubListSqLiteDatabase.execSQL("delete from event");
    }
}
