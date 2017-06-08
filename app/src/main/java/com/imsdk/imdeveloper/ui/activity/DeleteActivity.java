package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpAddClub;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.util.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import imsdk.data.IMMyself;
import imsdk.data.group.IMMyselfGroup;

public class DeleteActivity extends Activity implements View.OnClickListener{
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private EditText mDeleteEditText;
    private Button mRight_titleBar;
    private Button mDeleteButton;
    private String mGroupId;
    private String mGroupName;
    private LoadingDialog mDialoag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        findViewById();
        setListener();
        initView();
    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mDeleteEditText= (EditText) findViewById(R.id.delete_edittext);
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        mDeleteButton= (Button) findViewById(R.id.delete_btn);
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button)findViewById(R.id.imbasetitlebar_right);
    }
    private void setListener() {
        mTitleBarLogo.setText("删除成员");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
    }
    private void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.delete_btn:
                mDialoag=new LoadingDialog(this,"正在删除");
                mDialoag.setCancelable(true);
                mDialoag.show();
                final String userId=mDeleteEditText.getText().toString();
                if(!userId.equals("")) {
                    IMMyselfGroup.removeMember(userId, mGroupId,
                            new IMMyself.OnActionListener() {
                                @Override
                                public void onSuccess() {
                                    mDialoag.dismiss();
                                    UICommon.showTips(DeleteActivity.this,R.drawable.tips_smile,"成功");
                                    ClubListDao clubListDao = new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                                    clubListDao.createIdName();
                                    new AsyncTask<String, Void, String>() {
                                        @Override
                                        protected void onPostExecute(String s) {
                                            super.onPostExecute(s);
                                            if (s.equals("服务器异常")) {

                                            } else {
                                                String userid = s;
                                                ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
                                                clubDao.createClubInfo();
                                                JSONObject jsonObject = clubDao.queryClubInfo(mGroupName);
                                                if (jsonObject == null) {
                                                    UICommon.showTips(DeleteActivity.this, R.drawable.tips_error, "失败");
                                                } else {
                                                    try {
                                                        new httpAddClub(DeleteActivity.this).execute(jsonObject.getString("id"),userid);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        protected String doInBackground(String... params) {
                                            String path = null;
                                            String result = "";
                                            try {
                                                path = "http://211.81.248.108:8080/iyanda_yrb/getUseridByName.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");

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


                                                    result = br.readLine();
                                                } else {
                                                    result = "服务器异常";
                                                }
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                                result = "服务器异常";
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                                result = "服务器异常";
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                result = "服务器异常";
                                            }
                                            return result;
                                        }
                                    }.execute(userId);
                                }

                                @Override
                                public void onFailure(String error) {
                                    mDialoag.dismiss();
                                    UICommon.showTips(DeleteActivity.this, R.drawable.tips_error, error);
                                }
                            });
                }else {
                    mDialoag.dismiss();
                    UICommon.showTips(DeleteActivity.this,R.drawable.tips_error,"不可为空");
                }
                break;
            default:

                break;
        }
    }
}
