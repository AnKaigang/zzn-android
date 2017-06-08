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
 * Created by Administrator on 2015/12/19.
 */
public class httpPush extends AsyncTask<String,Boolean,String> {

    String result="";
    Activity activity;

    public httpPush(Activity a){
        activity=a;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.equals("ok")) {
            UICommon.showTips(activity, R.drawable.tips_smile,"推送成功");
            activity.finish();
        } else {
            UICommon.showTips(activity, R.drawable.tips_smile,result);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"pushCommunityInform.html?communityid="+ URLEncoder.encode(params[0].toString(), "utf-8")+
                                                                                          "&title="+ URLEncoder.encode(params[1].toString(), "utf-8");
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
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("user", params[0].toString());
//            jsonObject.put("passwd", params[1].toString());
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
//            user = jsonObject.getString("user");
//            passwd = jsonObject.getString("passwd");
//            IMMyself.setCustomUserID(jsonObject.getString("user"));
//            IMMyself.setPassword(jsonObject.getString("passwd"));

        } catch (IOException e) {
            e.printStackTrace();
            result = "服务器异常";
        }
        return result;
    }
}
