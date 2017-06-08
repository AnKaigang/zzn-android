package com.imsdk.imdeveloper.http;

import android.app.Activity;
import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.ui.a1common.UICommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/18.
 */
public class httpCreatClub extends AsyncTask<String, String, String> {

    Activity activity = null;
    String result="";

    public httpCreatClub(Activity a) {
        activity = a;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"createCommunity.html?"
                    + "communityname=" + URLEncoder.encode(params[0].toString(), "utf-8")
                    + "&communityIntro=" + URLEncoder.encode(params[1].toString(), "utf-8")
                    + "&type=" + URLEncoder.encode(params[2].toString(), "utf-8")
                    + "&username=" + URLEncoder.encode(params[3].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
//            httpCoon.setRequestMethod("GET");
//            URL url = new URL("http://211.81.248.105:8080/Fangker/loginServlet");
//
//            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
//            httpCoon.setRequestMethod("POST");
//            httpCoon.setDoOutput(true);
//            httpCoon.setDoInput(true);
//            httpCoon.setUseCaches(false);
            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
//            OutputStream os = httpCoon.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
//            BufferedWriter bw = new BufferedWriter(osw);
//            bw.write("user=" + jsonObject.getString("user"));
//            bw.write("passwd=" +jsonObject.getString("passwd"));
//            bw.flush();
//            bw.close();
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
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.equals("ok")) {
            UICommon.showTips(activity, R.drawable.tips_smile, "创建社团成功");
        } else {
            if (result.equals("exists")) {
                UICommon.showTips(activity, R.drawable.tips_error, "社团已存在");
            } else {
                UICommon.showTips(activity, R.drawable.tips_error, result);
            }
        }
    }
}
