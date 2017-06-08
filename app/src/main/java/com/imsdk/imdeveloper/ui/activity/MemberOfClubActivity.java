package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
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

import imsdk.data.IMMyself;

public class MemberOfClubActivity extends Activity implements View.OnClickListener {

    ListView listView1;
    MySimAdapter sim_adp1;
    List<Map<String, Object>> list;
    private String mGroupId;
    private String mGroupName;

    public ClubListDao mClubListDao;
    private TextView mLeft_titleBar;
    private TextView mTitleBarLogo;
    private TextView mRight_titleBar;
    public  Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_of_club);
        list = new ArrayList<Map<String, Object>>();

        mClubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
        mClubListDao.createIdName();
        mClubListDao.createRelation();
        findViewById();
        setListener();
        initView();
        ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
        clubDao.createClubInfo();
        JSONObject js=clubDao.queryClubInfo(mGroupName);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123451: {
                        list=getData1();
                        sim_adp1.notifyDataSetChanged();
                    }
                    break;
                    case 123452: {
                        sim_adp1.notifyDataSetChanged();
                    }
                    break;
                }
            }
        };
        if(js!=null) {
           try {
                getData(js.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void findViewById() {
        mLeft_titleBar = (TextView) findViewById(R.id.left);

        Intent i = getIntent();
        mGroupId = i.getStringExtra("groupId");
        mGroupName = i.getStringExtra("groupName");
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (TextView) findViewById(R.id.right);

        listView1 = (ListView) findViewById(R.id.memberlist);
    }

    public void setListener() {
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    public void initView() {
        sim_adp1 = new MySimAdapter(MemberOfClubActivity.this, getData1(),
                R.layout.member_club_items, new String[]{"name","label"}, new int[]{R.id.name_club,R.id.name_label},mGroupName,mGroupId);
        listView1.setAdapter(sim_adp1);
    }

    private List<Map<String, Object>> getData1() {

        JSONArray jsonArray = mClubListDao.queryRelationByPosition("主席");
        if(jsonArray!=null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = mClubListDao.queryIdNameByUserId(jsonObject.getString("userid"));
                    String position = mClubListDao.queryIdNameByUserId(jsonObject.getString("position"));
                    map.put("name", name);
                    map.put("label", "主席");
                    list.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        jsonArray = mClubListDao.queryRelationByPosition("部长");
        if(jsonArray!=null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = mClubListDao.queryIdNameByUserId(jsonObject.getString("userid"));
                    String position = mClubListDao.queryIdNameByUserId(jsonObject.getString("position"));
                    map.put("name", name);
                    map.put("label", "部长");
                    list.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        jsonArray = mClubListDao.queryRelationByPosition("部员");
        if(jsonArray!=null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = mClubListDao.queryIdNameByUserId(jsonObject.getString("userid"));
                    map1.put("name", name);
                    map1.put("label", "部员");
                    list.add(map1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    public void getData(String id){
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();
        requestParams.add("communityid", id);
        client.post(MemberOfClubActivity.this, IMConfiguration.getRelation, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int j, Header[] headers, byte[] bytes) {

                JSONArray jsonArray = null;
                try {
                    list.clear();
                    jsonArray = new JSONArray(new String(bytes));
                    ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                    clubListDao.createRelation();
                    clubListDao.deleteRelation();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                        if(!clubListDao.queryRelationIsExists(jsonObject.getString("id"))){
                            clubListDao.insertRelation(jsonObject.getString("id"),
                                    jsonObject.getString("userid"),
                                    jsonObject.getString("position"));
                        }
                    }
                    Message message=new Message();
                    message.what=123451;
                    MemberOfClubActivity.this.handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                finish();
                break;

            case R.id.right:
                Intent i=new Intent(MemberOfClubActivity.this,AddContactActivity.class);
                i.putExtra("type","1");
                i.putExtra("mGroupName",mGroupName);
                i.putExtra("mGroupId",mGroupId);
                this.startActivity(i);
                break;
        }
    }
}
