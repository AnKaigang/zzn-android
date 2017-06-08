package com.imsdk.imdeveloper.http;

import android.os.AsyncTask;
import android.widget.EditText;

import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubListDao;

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
 * Created by Administrator on 2015/12/24.
 */
public class httpGetIdName extends AsyncTask<String, Integer, String> {
    EditText edit;
    boolean isRight;
    String user = null;
    String passwd = null;
    String result="";
    String line = "";
String mGroupId;
    public httpGetIdName(String mGroupId) {
        super();
        this.mGroupId=mGroupId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip+"getUsersInCommunity.html?communityid=" + URLEncoder.encode(params[0].toString(), "utf-8");
            URL url = new URL(path);
            HttpURLConnection httpCoon = (HttpURLConnection) url.openConnection();

            httpCoon.setRequestProperty("Content_Type", "application/x-www-form-url");
            httpCoon.setRequestProperty("Charset", "utf-8");
            httpCoon.setReadTimeout(10000);
            httpCoon.setConnectTimeout(10000);


            httpCoon.connect();
//            bw.flush();
//            bw.close();
            if (httpCoon.getResponseCode() == 200) {
                InputStream is = httpCoon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line=br.readLine())!=null){
                    result+=line;
                }
                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                clubListDao.createIdName();
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    if(!clubListDao.queryIdNameIsExistsByUserId(jsonObject.getString("id"))){
                        clubListDao.insertIdName(jsonObject.getString("id"),
                                jsonObject.getString("username"));
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
        if (!s.equals("服务器异常")) {
            //LoginActivity.login(loginActivity, user, passwd, 1);
            //loginActivity.updateStatus(0,user);
        } else {
            //dialog.dismiss();
        }
    }
}

