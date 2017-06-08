package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.NotifyDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiaowuActivity extends Activity {

    ListView listView;
    List<Map<String, Object>> list;
    SimpleAdapter sim_adp;
    static JSONArray jsonArray;
    private TextView mLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiaowu);
        listView = (ListView) findViewById(R.id.jiaowulist);
        Intent i = getIntent();
        //String temp = i.getStringExtra("fileList");
        // jsonArray = new JSONArray(temp);

        sim_adp = new SimpleAdapter(JiaowuActivity.this, getData(), R.layout.item_jiaowu,
                new String[]{"name"}, new int[]{R.id.jiaowu_name});
        listView.setAdapter(sim_adp);
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
                String nid= (String) list.get(position).get("id");
                String url;
                url= IMConfiguration.ip+"getJWCContent.html?id="+nid;
                Intent intent = new Intent(JiaowuActivity.this, webview.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        NotifyDao notifyDao=new NotifyDao();
        notifyDao.createNotify();
        JSONArray jsonArray=notifyDao.query();
        if(jsonArray!=null){
            for(int i=0;i<jsonArray.length();i++){
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    JSONObject js=jsonArray.getJSONObject(i);
                    map.put("name", js.getString("title")) ;
                    map.put("id", js.getString("id")) ;
                    list.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDao.destroy();
//        for (int i = 0; i < jA.length(); i++) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("name", "高数考试时间");
//            map.put("time", "2015-9-1");
//            list.add(map);
//        }
        return list;
    }
}

