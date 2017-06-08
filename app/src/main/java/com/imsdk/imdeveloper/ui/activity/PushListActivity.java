package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.NotifyDao;
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

public class PushListActivity extends Activity {

    ListView listView;
    List<Map<String, Object>> list;
    SimpleAdapter sim_adp;
    LoadingDialog mDialog;
    private String mGroupId;
    private String mGroupName;
    private TextView mRight;
    private TextView mLeft;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_list);
        listView = (ListView) findViewById(R.id.informList);
        mRight = (TextView) findViewById(R.id.right);
        Intent i = getIntent();
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        mDialog=new LoadingDialog(PushListActivity.this,"正在加载");
        mDialog.show();
        sim_adp = new SimpleAdapter(PushListActivity.this, getData(), R.layout.item_pushlist,
                new String[]{"name"}, new int[]{R.id.push_name});
        listView.setAdapter(sim_adp);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123321: {
                        sim_adp.notifyDataSetChanged();
                    }
                    break;
                }
            }
        };
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PushListActivity.this,PushActivity.class);
                i.putExtra("groupName",mGroupName);
                i.putExtra("groupId",mGroupId);
                startActivity(i);
                finish();
            }
        });
        mLeft=(TextView) findViewById(R.id.left);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nid = (String) list.get(position).get("id");
                String title = (String) list.get(position).get("title");
                String content = (String) list.get(position).get("content");
                Intent intent = new Intent(PushListActivity.this, PushCountActivity.class);
                intent.putExtra("id", nid);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                startActivity(intent);
            }
        });
    }
    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();
        requestParams.add("community", mGroupName);
        client.post(PushListActivity.this, IMConfiguration.pushList, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int j, Header[] headers, byte[] bytes) {
                mDialog.dismiss();
                JSONArray jsonArray = null;
                try {
                        list.clear();
                        jsonArray = new JSONArray(new String(bytes));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            JSONObject js = jsonArray.getJSONObject(i);
                            map.put("name", js.getString("title"));
                            map.put("id", js.getString("id"));
                            map.put("title",js.getString("title"));
                            map.put("content",js.getString("content"));
                            list.add(map);
                        }
                    Message message=new Message();
                    message.what=123321;
                    PushListActivity.this.handler.sendMessage(message);
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
}
