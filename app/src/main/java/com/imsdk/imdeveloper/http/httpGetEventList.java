package com.imsdk.imdeveloper.http;

import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.TaskListActivity;
import com.imsdk.imdeveloper.ui.view.TimeViewInfo;

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
import java.util.ArrayList;

import imsdk.data.IMMyself;

/**
 * Created by Administrator on 2015/12/23.
 */
public class httpGetEventList extends AsyncTask<String,String,String> {


    String result="";
    TaskListActivity activity;
    String mGroupId;
    public httpGetEventList(TaskListActivity a,String mGroupId){
        activity=a;
        this.mGroupId=mGroupId;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"getEvents.html?communityid=" + URLEncoder.encode(params[0].toString(), "utf-8");
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
                clubListDao.createEvent();
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                    if(!clubListDao.queryEventIsExistsById(jsonObject.getString("id"))){
                        clubListDao.insertEvent(jsonObject.getString("id"),
                                jsonObject.getString("date"),
                                jsonObject.getString("eventcontent"),jsonObject.getString("title"));
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
            ArrayList<TimeViewInfo> infos = new ArrayList<TimeViewInfo>();

            ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
            clubListDao.createEvent();
            JSONArray jsonArray = clubListDao.queryAllEvent();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject js = (JSONObject) jsonArray.get(i);
                        infos.add(activity.createTimeInfo(false, false, js.getString("date"),
                                js.getString("title"), js.getString("content")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            activity.timeLineView.removeAllViews();
            activity.timeLineView.setTimeInfos(infos);
        }

    }
}
