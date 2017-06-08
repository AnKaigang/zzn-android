package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.ui.a1common.UICommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import imsdk.data.IMMyself;

/**
 * Created by Administrator on 2015/12/23.
 */
public class httpGetRelation  extends AsyncTask<String,Void,String> {

    String result="";
    Activity activity;
    String mGroupId;
    public httpGetRelation(Activity a,String mGroupId){
        activity=a;
        this.mGroupId=mGroupId;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"getAllRlation.html?communityid=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
            if (httpCoon.getResponseCode() == 200) {
                InputStream is = httpCoon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String line="";
                while ((line=br.readLine())!=null){
                    result+=line;
                }
                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                clubListDao.createRelation();
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                    if(!clubListDao.queryRelationIsExists(jsonObject.getString("id"))){
                        clubListDao.insertRelation(jsonObject.getString("id"),
                                jsonObject.getString("userid"),
                                jsonObject.getString("position"));
                    }
                }
            } else {
                result = "服务器异常";
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = "服务器异常";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("服务器异常")) {
            UICommon.showTips(activity, R.drawable.tips_error, result);
        } else {
           // activity.handler
        }

    }
}