package com.imsdk.imdeveloper.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpGetFileList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsdk.data.IMMyself;

public class DocumentActivity extends FragmentActivity implements View.OnClickListener {

    ListView listView;
    public List<Map<String, Object>> list;
    public SimpleAdapter sim_adp;

    public String mGroupId;
    public String mGroupName;
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_document);
        findViewById();
        setListener();
        initView();

    }

    private void setListener() {
        mTitleBarLogo.setText("文件列表");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("操作");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button) findViewById(R.id.imbasetitlebar_right);
        String position=getIntent().getStringExtra("position");
      /*  if(position==null||position.equals("部员")){
            mRight_titleBar.setEnabled(false);
            mRight_titleBar.setVisibility(View.GONE);
        }*/
    }

    private void initView() {

        Intent i = getIntent();
        mGroupId = i.getStringExtra("groupId");
        mGroupName = i.getStringExtra("groupName");
        listView = (ListView) findViewById(R.id.documentlist);
        sim_adp = new SimpleAdapter(DocumentActivity.this, getData(), R.layout.item_document,
                new String[]{"name"}, new int[]{R.id.document_name});
        listView.setAdapter(sim_adp);
        ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
        clubDao.createClubInfo();
        JSONObject js = clubDao.queryClubInfo(mGroupName);
        if(js!=null) {
            try {
                new httpGetFileList(DocumentActivity.this,mGroupId).execute(js.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id_file = (String) list.get(position).get("id");
                String url;
                url= IMConfiguration.ip+"download.html?fileid="+id_file;
                Intent intent = new Intent(DocumentActivity.this, webview.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
        clubListDao.createFile();
        JSONArray jsonArray = clubListDao.queryAllFile();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject js = (JSONObject) jsonArray.get(i);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", js.getString("name"));
                    map.put("id", js.getString("id"));
                    list.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
//        map.put("name", "联络单");
//        Map<String, Object> map2 = new HashMap<String, Object>();
//        map2.put("name", "活动策划");
//        Map<String, Object> map3 = new HashMap<String, Object>();
//        map3.put("name", "干部选拔");
//        Map<String, Object> map4 = new HashMap<String, Object>();
//        map4.put("name", "社团简介");
//        list.add(map);
//        list.add(map2);
//        list.add(map3);
//        list.add(map4);


        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.imbasetitlebar_right:
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                View v = LayoutInflater.from(DocumentActivity.this).inflate(
                        R.layout.dialog_document, null);
                LinearLayout shuangchuanLayout = (LinearLayout) v.findViewById(R.id.layout_shangchuan);
                LinearLayout deleteLayout = (LinearLayout) v.findViewById(R.id.layout_deletedocu);
                builder.setView(v);
                final AlertDialog dialog = builder.create();

                dialog.show();
                shuangchuanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        Intent intent = new Intent(DocumentActivity.this, ShangchuanActivity.class);
//                        startActivity(intent);
                        ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
                        JSONObject jsonObject = clubDao.queryClubInfo(mGroupName);
                        if (jsonObject != null) {
                            String url;
                            try {
                                url= IMConfiguration.ip+"upload.html?communityid=" + jsonObject.getString("id").toString();

                                Intent intent = new Intent(DocumentActivity.this, webview.class);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                deleteLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(DocumentActivity.this, DeletedocuActivity.class);
                        intent.putExtra("groupId", mGroupId);
                        intent.putExtra("groupName", mGroupName);
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}
