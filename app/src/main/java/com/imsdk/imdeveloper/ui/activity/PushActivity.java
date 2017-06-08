package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.http.httpPush;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PushActivity extends Activity implements View.OnClickListener {

    Spinner spinner;
    List<Map<String, Object>> list;
    SimpleAdapter sim_adp;
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private TextView mPushMessage;
    private TextView mTitle;
    private Button mSurePush;
    private String mGroupId;
    private String mGroupName;
    LoadingDialog mDialog;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_push);
        mDialog=new LoadingDialog(PushActivity.this,"正在推送");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123451: {
                        sim_adp.notifyDataSetChanged();
                    }
                    break;
                }
            }
        };
        findViewById();
        setListener();
        initView();

    }

    private void initView() {

        spinner = (Spinner) findViewById(R.id.spinner);
        sim_adp = new SimpleAdapter(PushActivity.this, getData(), R.layout.item_spinner,
                new String[]{"department"}, new int[]{R.id.department});
        spinner.setAdapter(sim_adp);
    }

    public void setListener() {
        mSurePush.setOnClickListener(this);
        mTitleBarLogo.setText("推送通知");
        mLeft_titleBar.setOnClickListener(this);
    }

    public void findViewById() {
        mTitle= (TextView) findViewById(R.id.pushTitle);
        mPushMessage= (TextView) findViewById(R.id.pushMessage);
        mSurePush= (Button) findViewById(R.id.surePush);
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
    }

    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        AsyncHttpClient client=new AsyncHttpClient();

        RequestParams requestParams=new RequestParams();
        requestParams.add("community", mGroupName);
        client.post(PushActivity.this, IMConfiguration.departList, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int j, Header[] headers, byte[] bytes) {
                JSONArray jsonArray = null;
                try {
                    list.clear();
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("department", "全体成员");
                    map1.put("id", -1);
                    list.add(map1);
                    jsonArray = new JSONArray(new String(bytes));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject js = jsonArray.getJSONObject(i);
                        map.put("department", js.getString("deptname"));
                        map.put("id", js.getString("id"));
                        list.add(map);
                    }
                    Message message = new Message();
                    message.what = 123451;
                    PushActivity.this.handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.surePush:
                String message=mPushMessage.getText().toString();
                String title=mTitle.getText().toString();
                String receiver= ((Map<String,Object>)spinner.getSelectedItem()).get("department").toString();
                if(!"".equals(message)&&!"".equals(title)) {
                    AsyncHttpClient client= new AsyncHttpClient();
                    RequestParams requestParams=new RequestParams();
                    requestParams.add("community",mGroupName);
                    if(("全体成员").equals(receiver)){
                        requestParams.add("type","2");
                    }else{
                        requestParams.add("type","1");
                    }
                    requestParams.add("dept",receiver);
                    requestParams.add("title",title);
                    requestParams.add("content",message);
                    mDialog.show();
                    client.post(PushActivity.this, IMConfiguration.pushInform, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            mDialog.dismiss();
                            String result=new String(bytes);
                            if("ok".equals(result)){
                                UICommon.showTips(PushActivity.this, R.drawable.tips_smile, "推送成功");
                            }else{
                                UICommon.showTips(PushActivity.this, R.drawable.tips_error, "推送失败");
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            mDialog.dismiss();
                            UICommon.showTips(PushActivity.this, R.drawable.tips_error, "网络异常");
                        }
                    });
                }else {
                    UICommon.showTips(PushActivity.this, R.drawable.tips_error, "不可为空");
                }
                break;
        }
    }
}
