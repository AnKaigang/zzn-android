package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.Rlation;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
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
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import imsdk.data.IMMyself;
import imsdk.data.group.IMMyselfGroup;

public class InviteActivity extends Activity implements View.OnClickListener {

    EditText et_invite;
    Button invite_btn;

    public String mGroupId;
    public String mGroupName;
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;
    private RadioGroup mRadioGroup;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        findViewById();
        setListener();
        initView();


    }

    private void initView() {


    }

    private void setListener() {
        mTitleBarLogo.setText("邀请好友");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
        invite_btn.setOnClickListener(this);
    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);


        mGroupId = getIntent().getStringExtra("groupId");
        mGroupName = getIntent().getStringExtra("groupName");
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button) findViewById(R.id.imbasetitlebar_right);

        mRadioGroup = (RadioGroup) findViewById(R.id.chooseIdentify);
        et_invite = (EditText) findViewById(R.id.invite_user_edit);
        invite_btn = (Button) findViewById(R.id.invite_btn);
        //invite_btn.setEnabled(true);
    }
    public static Map getValue(Object thisObj)
    {
        Map map = new HashMap();
        Class c;
        try
        {
            c = Class.forName(thisObj.getClass().getName());
            Method[] m = c.getMethods();
            for (int i = 0; i < m.length; i++)
            {
                String method = m[i].getName();
                if (method.startsWith("get"))
                {
                    try{
                        Object value = m[i].invoke(thisObj);
                        if (value != null)
                        {
                            String key=method.substring(3);
                            key=key.substring(0,1).toUpperCase()+key.substring(1);
                            map.put(method, value);
                        }
                    }catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("error:"+method);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
        return map;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;

            case R.id.invite_btn:
                mDialog=new LoadingDialog(this,"正在发送");
                mDialog.setCancelable(true);
                mDialog.show();
                if(et_invite.getText().toString().equals("")){
                    mDialog.dismiss();
                    UICommon.showTips(InviteActivity.this,R.drawable.tips_error,"不可为空");
                    return;
                }
                IMMyselfGroup.addMember(et_invite.getText().toString(), mGroupId,
                        new IMMyself.OnActionListener() {
                            @Override
                            public void onSuccess() {
                                mDialog.dismiss();
                                UICommon.showTips(InviteActivity.this, R.drawable.tips_smile, "成功");
                                ClubListDao clubListDao = new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                                clubListDao.createIdName();
                                new AsyncTask<String, Void, String>() {
                                    @Override
                                    protected void onPostExecute(String s) {
                                        super.onPostExecute(s);
                                        if (s.equals("服务器异常")) {
                                            UICommon.showTips(InviteActivity.this, R.drawable.tips_error, s);
                                        } else {
                                            String userid = s;
                                            ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
                                            clubDao.createClubInfo();
                                            JSONObject jsonObject = clubDao.queryClubInfo(mGroupName);
                                            if (jsonObject == null) {
                                                UICommon.showTips(InviteActivity.this, R.drawable.tips_error, "失败");
                                            } else {
                                                String position;
                                                if (mRadioGroup.getCheckedRadioButtonId() == R.id.chairman_btn) {
                                                    position = "主席";
                                                } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.minister_btn) {
                                                    position = "部长";
                                                } else {
                                                    position = "部员";
                                                }
                                                Rlation relation = new Rlation();
                                                try {
                                                    relation.setCommunityid(jsonObject.getString("id"));
                                                    relation.setPosition(position);
                                                    relation.setUserid(userid);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Map map = getValue(relation);
                                                String json = new Gson().toJson(relation);
                                                new httpAddClub(InviteActivity.this).execute(json);
                                            }
                                        }
                                    }

                                    @Override
                                    protected String doInBackground(String... params) {
                                        String path = null;
                                        String result = "";
                                        try {
                                            path = IMConfiguration.ip + "getUseridByName.html?username=" + URLEncoder.encode(params[0].toString(), "utf-8");

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
                                }.execute(et_invite.getText().toString());
                            }

                            @Override
                            public void onFailure(String error) {
                                mDialog.dismiss();
                                UICommon.showTips(InviteActivity.this, R.drawable.tips_error, error);
                            }
                        });
                break;
            default:

                break;
        }
    }
}
