package com.imsdk.imdeveloper.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.ui.activity.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/12/23.
 */
public class HttpGetCheckCode extends AsyncTask<String ,String,Bitmap>{

    String result="";
    Bitmap bitmap = null;
    BufferedInputStream bis = null;
    RegisterActivity re=null;

    public HttpGetCheckCode(RegisterActivity registerActivity)
    {
        re=registerActivity;
    }
    @Override
    protected void onPostExecute(Bitmap s) {
        super.onPostExecute(s);
        re.imageView.setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            String path = IMConfiguration.ip + "getCheckCode.html";
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
                bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
