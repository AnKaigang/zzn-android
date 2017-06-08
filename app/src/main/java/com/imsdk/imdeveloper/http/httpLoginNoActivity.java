package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.LoginActivity;
import com.imsdk.imdeveloper.ui.activity.MainActivity;

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
 * Created by Administrator on 2015/12/14.
 */
public class httpLoginNoActivity extends AsyncTask<String, Integer, Boolean> {
    Activity welcomeActivity;
    JSONObject jsonObject;

    public httpLoginNoActivity(Activity w) {
        super();
        welcomeActivity = w;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    boolean isRight;
    String result="";
    String line = "";

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"login.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
            httpCoon.setRequestMethod("GET");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);
//            httpCoon.setDoOutput(true);
//            httpCoon.setDoInput(true);
//            httpCoon.setUseCaches(false);
          //  httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
           // httpCoon.setRequestProperty("Charset", "utf-8");


            httpCoon.connect();
//            OutputStream os = httpCoon.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
//            BufferedWriter bw = new BufferedWriter(osw);
            jsonObject = new JSONObject();
            jsonObject.put("user", params[0].toString());
            jsonObject.put("passwd", params[1].toString());
//            bw.write("user=" + jsonObject.getString("user"));
//            bw.write("passwd=" +jsonObject.getString("passwd"));
//            bw.flush();
//            bw.close();
            if (httpCoon.getResponseCode() == 200) {
                InputStream is = httpCoon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);

                while((line = br.readLine())!=null) {
                    result += line;
                }
//
//                JSONArray jsonArray=new JSONArray(result);
//                UserIndentifyDao userIndentifyDao=new UserIndentifyDao(jsonObject.getString("user"));
//                for(int i=0;i<jsonArray.length();i++){
//                    JSONObject js= (JSONObject) jsonArray.get(i);
//                    if(!userIndentifyDao.queryClub(js.getString("communityid"))){
//                        userIndentifyDao.insert(js.getString("communityid"),js.getString("position"));
//                    }
//                }
            } else {
                result = "服务器异常";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result="服务器超时";
            isRight = false;
            return isRight;
        } catch (JSONException e) {
            e.printStackTrace();
            result="服务器超时";
            isRight = false;
            return isRight;
        }
        if (result.equals("服务器异常")) {
            isRight = false;
            return isRight;
        } else {
            isRight = false;
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            Intent mainIntent = new Intent(welcomeActivity,
                    MainActivity.class);
            try {
                LoginActivity.loginNo(welcomeActivity, jsonObject.getString("user"), jsonObject.getString("passwd"),0,0);
                IMMyself.setCustomUserID(jsonObject.getString("user"));
                IMMyself.setPassword(jsonObject.getString("passwd"));
                mainIntent.putExtra("username", jsonObject.getString("user"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            welcomeActivity.startActivity(mainIntent);
           // IMMyself.login();
            welcomeActivity.finish();
        } else {
            Intent mainIntent = new Intent().setClass(welcomeActivity,
                    LoginActivity.class);
            UICommon.showTips(welcomeActivity, R.drawable.tips_error, result.toString());
            welcomeActivity.startActivity(mainIntent);
            welcomeActivity.finish();
        }
    }
}
