package com.imsdk.imdeveloper.http;

import android.os.AsyncTask;
import android.widget.EditText;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.LoginActivity;
import com.imsdk.imdeveloper.util.LoadingDialog;

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
public class httpLogin extends AsyncTask<String, Integer, Boolean> {
    EditText edit;
    LoadingDialog dialog;
    LoginActivity loginActivity;
    boolean isRight;
    String user = "";
    String passwd = "";
    String result="";
    String line = "";

    public httpLogin(LoadingDialog et, LoginActivity login) {
        super();
        dialog = et;
        loginActivity = login;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"login.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();

            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
//            OutputStream os = httpCoon.getOutputStream();
//            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
//            BufferedWriter bw = new BufferedWriter(osw);
            JSONObject jsonObject = new JSONObject();
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


                result = br.readLine();
            } else {
                result = "服务器异常";
            }
            user = jsonObject.getString("user");
            passwd = jsonObject.getString("passwd");
            IMMyself.setCustomUserID(jsonObject.getString("user"));
            IMMyself.setPassword(jsonObject.getString("passwd"));

        } catch (IOException e) {
            e.printStackTrace();
            result = "服务器异常";
            isRight = false;
            return isRight;
        } catch (JSONException e) {
            e.printStackTrace();
            result = "服务器异常";
            isRight = false;
            return isRight;
        }
        if (result.equals("服务器异常")) {
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
        if (aBoolean) {
            loginActivity.login(loginActivity, user, passwd, 1);
            //loginActivity.updateStatus(0,user);
        } else {
            UICommon.showTips(loginActivity,R.drawable.tips_error,result);
            dialog.dismiss();
        }
    }
}
