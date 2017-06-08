package com.imsdk.imdeveloper.http;

import android.os.AsyncTask;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.userDao;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.RegisterActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/15.
 */
public class httpRegister extends AsyncTask<String, Integer, String> {

    RegisterActivity registerActivity;
    String result="";
    String line = "";
    String user="";
    String passwd="";

    public httpRegister(RegisterActivity r) {
        registerActivity = r;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
            String path = IMConfiguration.ip+"regist.html?username=" + URLEncoder.encode(params[0].toString())
                    + "&tel=" + URLEncoder.encode(params[1].toString())
                    + "&password=" + URLEncoder.encode(params[2].toString())
                    + "&checkcode=" + URLEncoder.encode(params[3].toString());
            url = new URL(path);


            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();
            httpCoon.setReadTimeout(20000);
            httpCoon.setConnectTimeout(20000);
            httpCoon.setRequestMethod("GET");
//            httpCoon.setDoOutput(true);
//            httpCoon.setDoInput(true);
//            httpCoon.setUseCaches(false);
            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");


            httpCoon.connect();
//            OutputStream os = httpCoon.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
//            BufferedWriter bw = new BufferedWriter(osw);

            user = params[0].toString();
            passwd = params[2].toString();
//            bw.write("user=" + jsonObject.getString("user"));
//            bw.write("passwd=" + jsonObject.getString("passwd"));
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = "服务器异常1";
        } catch (ProtocolException e) {
            e.printStackTrace();
            result = "服务器异常2";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = "服务器异常3";
        } catch (IOException e) {
            e.printStackTrace();
            result = "服务器异常4";
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.equals("ok")) {
            //String sucess = "注册成功";
            //T.show(registerActivity, sucess);
            userDao uD=new userDao();
            uD.createUser();
            uD.deleteALl();
            uD.insert(user, passwd);
            uD.destroy();
            RegisterActivity.registerThread(registerActivity, user, passwd);
//            Intent i = new Intent(registerActivity,MainActivity.class);
//            try {
//                i.putExtra("username", jsonObject.getString("user"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            registerActivity.startActivity(i);

            //registerActivity.finish();
        } else {
            registerActivity.mDig.dismiss();
            if("exist".equals(s)){
                UICommon.showTips(registerActivity, R.drawable.tips_error, "用户已存在");
            }else if("loginerror".equals(s)){
                UICommon.showTips(registerActivity, R.drawable.tips_error, "请输入学号及教务系统密码");
            }else {
                UICommon.showTips(registerActivity, R.drawable.tips_error, s);
            }


        }
    }
}
