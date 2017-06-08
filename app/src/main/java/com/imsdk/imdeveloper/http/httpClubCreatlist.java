package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.AddClubList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/14.
 */
public class httpClubCreatlist extends AsyncTask<String, Integer, Boolean> {

    AddClubList activity=null;
    String result="";
    Boolean isRight;

    public httpClubCreatlist(AddClubList a) {
        super();
        activity = a;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            URL url = new URL(IMConfiguration.ip+"addEvent.html?event="+ URLEncoder.encode(params[0].toString(), "utf-8"));

            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
            httpCoon.setRequestMethod("POST");
            httpCoon.setDoOutput(true);
            httpCoon.setDoInput(true);
            httpCoon.setUseCaches(false);
            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
            if (httpCoon.getResponseCode() == 200) {
                InputStream is = httpCoon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);


                result = br.readLine();
            } else {
                result = "服务器异常";
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = "服务器异常";
            isRight = false;
            return isRight;
        }
        if (result.toString().equals("服务器异常")) {
            isRight = false;
            return isRight;
        } else {
            isRight = true;
            return isRight;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        activity.mDialog.dismiss();
        if (aBoolean) {
            UICommon.showTips(activity,R.drawable.tips_smile,"加入活动成功");
            activity.finish();
        } else {
            UICommon.showTips(activity,R.drawable.tips_error,"加入活动失败");
        }
    }
}
