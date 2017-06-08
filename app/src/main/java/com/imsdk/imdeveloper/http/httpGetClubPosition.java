package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;

import com.baidu.android.pushservice.PushManager;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import imsdk.data.IMMyself;

/**
 * Created by Administrator on 2015/12/23.
 */
public class httpGetClubPosition extends AsyncTask<String, String, String> {
    String result = "";
    Activity activity;

    public httpGetClubPosition(Activity a) {
        activity = a;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"getRlation.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = null;
            try {
                url = new URL(path);
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
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                    JSONArray jsonArray = new JSONArray(result);
                    ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
                    clubDao.createIdentify();
                    if(jsonArray!=null&&jsonArray.length()>0) {
                        clubDao.deleteIdentify();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject js = (JSONObject) jsonArray.get(i);
                        if (!clubDao.queryIdentifyIsExitsById(js.getString("id"))) {
                            clubDao.insertIdentify(js.getString("id"), js.getString("communityid"), js.getString("position"));
                        }
                        String cluName=clubDao.queryClubNameByClubId(js.getString("communityid"));
                        Cursor cursor = clubDao.queryIdentify();
                        List<String> list =new ArrayList<String>();
                        list.add(cluName);
                        if(js.getString("department")!=null&&!"".equals(js.getString("department"))){
                            list.add(js.getString("department"));
                        }
                        PushManager.setTags(activity, list);
                    }
                } else {
                    result = "服务器异常";
                }

            } catch (IOException e) {
                result = "服务器异常";
                e.printStackTrace();
            } catch (JSONException e) {
                result = "服务器异常";
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
