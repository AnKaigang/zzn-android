package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.CommunityData;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.http.httpModifyClubInfo;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import imsdk.data.IMMyself;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import imsdk.data.mainphoto.IMMyselfMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.nickname.IMSDKNickname;

public class PushDetailActivity extends Activity {
    private String title;
    private String content;
    private String informId;
    private TextView mTitle;
    private TextView mContent;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_detail);
        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        informId=getIntent().getStringExtra("id");
        mTitle= (TextView)findViewById(R.id.pushDetailTitle);
        mContent=(TextView)findViewById(R.id.pushDetailContent);
        mButton= (Button) findViewById(R.id.btn_surePush);
        mTitle.setText(title);
        mContent.setText(content);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client=new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                requestParams.add("informId", String.valueOf(informId));
                requestParams.add("username", String.valueOf(IMMyself.getCustomUserID()));
                client.post(PushDetailActivity.this, IMConfiguration.updateInformStatus, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int j, Header[] headers, byte[] bytes) {
                        if("ok".equals(new String(bytes))) {
                            UICommon.showTips(PushDetailActivity.this, R.drawable.tips_smile, "成功");
                        }else {
                            UICommon.showTips(PushDetailActivity.this, R.drawable.tips_error, "服务器异常");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        UICommon.showTips(PushDetailActivity.this, R.drawable.tips_error, "网络异常");
                    }
                });
            }
        });
}

}
