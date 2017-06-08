package com.imsdk.imdeveloper.http;

import android.os.AsyncTask;

import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;

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
public class httpGetClubId extends AsyncTask<String ,String,String>{

   String result="";
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"getCommunityList.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
//            httpCoon.setRequestMethod("GET");
//            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
//            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
            if (httpCoon.getResponseCode() == 200) {
                InputStream is = httpCoon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String line="";
                while((line = br.readLine())!=null) {
                    result += line;
                }
                JSONArray jsonArray=new JSONArray(result);
                ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                clubDao.createClubInfo();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject js= (JSONObject) jsonArray.get(i);
                    if(!clubDao.queryClubClubInfoExists(js.getString("id"))){
                        clubDao.insertClubInfo(js.getString("id"),js.getString("communityname"),
                                           js.getString("communityintro"),js.getString("type"));
                    }
                }
            } else {
                result = "服务器异常";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  result;
    }
}
