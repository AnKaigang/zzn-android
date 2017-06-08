package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.http.httpCreatClub;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsdk.data.IMMyself;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;

public class CreateClubActivity extends Activity {

    EditText et_name;
    TextView et_view;
    TextView et_nameNull;
    TextView et_infoNull;
    EditText et_message;
    Spinner spinner;
    SimpleAdapter sim_adp;
    Button button;
    List<Map<String, Object>> list;

    String mCommunityName = null;
    String mCommunityInfo = null;
    String mCommunityType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);
        spinner = (Spinner) findViewById(R.id.spinner);
        et_view=(TextView)findViewById(R.id.errorInfo);
        et_nameNull=(TextView)findViewById(R.id.errorNUllName);
        et_infoNull=(TextView)findViewById(R.id.errorNUllInfo);
        sim_adp = new SimpleAdapter(CreateClubActivity.this, getData(), R.layout.item_spinner,
                new String[]{"department"}, new int[]{R.id.department});
        spinner.setAdapter(sim_adp);
        et_name = (EditText) findViewById(R.id.club_name_edit);
        et_message = (EditText) findViewById(R.id.club_message_edit);
        button = (Button) findViewById(R.id.register_club_btn);
        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus&&!"".equals(et_name.getText().toString())){
                    AsyncHttpClient client=new AsyncHttpClient();
                    RequestParams requestParams=new RequestParams();
                    requestParams.add("communityName", et_name.getText().toString());
                    client.get(IMConfiguration.validateCommunityName,requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String result=new String(bytes);
                            if(!"1".equals(result)){
                                et_view.setVisibility(View.VISIBLE);
                            }else{
                                et_view.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommunityName = et_name.getText().toString();
                mCommunityInfo = et_message.getText().toString();
                mCommunityType = spinner.getSelectedItem().toString();
                mCommunityType=mCommunityType.substring(12,17);
                if (valivate()) {
                    if (IMMyselfGroup.isInitialized()) {
                        IMMyselfGroup.createGroup(mCommunityName, new IMMyself.OnActionResultListener() {

                            @Override
                            public void onSuccess(Object result) {
                                if (!(result instanceof String)) {
                                    return;
                                }
                                // 成功创建的群都会返回唯一的groupID，类型为String
                                String groupID = (String) result;

                                // 2. 获取指定groupID群的群名称和自定义资料
                                IMGroupInfo group = IMSDKGroup.getGroupInfo(groupID);
                                group.setCustomGroupInfo(mCommunityInfo+"("+mCommunityType+")");
                                group.commitGroupInfo(new IMMyself.OnActionListener() {
                                    @Override
                                    public void onSuccess() {
                                        new httpCreatClub(CreateClubActivity.this).execute(mCommunityName, mCommunityInfo, mCommunityType, IMMyself.getCustomUserID());
                                        List<String> list=new ArrayList<String>();
                                        list.add(mCommunityName);
                                        list.add("主席");
                                        PushManager.setTags(CreateClubActivity.this, list);

                                        AsyncHttpClient client=new AsyncHttpClient();
                                        RequestParams requestParams=new RequestParams();
                                        requestParams.add("communityname", mCommunityName);
                                        requestParams.add("communityIntro", mCommunityInfo);
                                        requestParams.add("type", mCommunityType);
                                        requestParams.add("username", IMMyself.getCustomUserID());
                                        client.get(IMConfiguration.createCommunity, requestParams, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                                String result = new String(bytes);
                                                if (!"1".equals(result)) {
                                                    et_view.setVisibility(View.VISIBLE);
                                                } else {
                                                    et_view.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        UICommon.showTips(CreateClubActivity.this,R.drawable.tips_error,"创建失败");
                                    }
                                });
                            }

                            @Override

                            public void onFailure(String error) {
                                // 创建群失败
                                //new httpCreatClub(CreateClubActivity.this).execute(mCommunityName, mCommunityInfo, mCommunityType);
                                UICommon.showTips(CreateClubActivity.this, R.drawable.tips_error, "创建社团失败");
                            }
                        });
                        //new httpCreatClub(CreateClubActivity.this).execute(mCommunityName, mCommunityInfo, mCommunityType);
                        finish();

                    }
                    else {
                        UICommon.showTips(CreateClubActivity.this,R.drawable.tips_error,"爱萌不可用");
                    }
                }
            }
        });

    }

    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("department", "公益服务类");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("department", "理论学习类");
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("department", "科学技术类");
        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("department", "文艺体育类");
        Map<String, Object> map5 = new HashMap<String, Object>();
        map5.put("department", "兴趣爱好类");
        Map<String, Object> map6 = new HashMap<String, Object>();
        map6.put("department", "其他社团类");
        list.add(map);
        list.add(map2);
        list.add(map3);
        list.add(map4);
        list.add(map5);
        list.add(map6);

        return list;
    }

    private boolean valivate() {
        if(et_view.getVisibility()==View.VISIBLE){
            return false;
        }
        if (mCommunityName == null||"".equals(mCommunityName)) {
            et_nameNull.setVisibility(View.VISIBLE);
            return false;
        } else{
            et_nameNull.setVisibility(View.GONE);
        }
        if (mCommunityInfo == null||"".equals(mCommunityInfo)) {
            et_infoNull.setVisibility(View.VISIBLE);
            return false;
        }else{
            et_infoNull.setVisibility(View.GONE);
        }
        return true;
    }
}
