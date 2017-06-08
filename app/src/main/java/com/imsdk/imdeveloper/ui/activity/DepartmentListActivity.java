package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
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

public class DepartmentListActivity extends Activity {

    private ListView mListview;
    private SimpleAdapter sim;
    private Button mAddBtn;
    private TextView mDelete;
    private TextView mLeft;
    int type=-1;
    LoadingDialog mDialog;
    List<Map<String, String>> list;
    MyAdapter myAdapter;
    String mGroupName;
    String mGroupId;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        mListview= (ListView) findViewById(R.id.departmentList);
        mAddBtn= (Button) findViewById(R.id.btn_addDept);
        mDelete= (TextView) findViewById(R.id.deptDelete);
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        mDialog=new LoadingDialog(this,"正在努力");
        mListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDialog.show();
        getData();
        myAdapter=new MyAdapter(getApplicationContext(),list);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123421: {
                        myAdapter.setDataList();
                        myAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        };
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=2;
                showGroupNameOrInfoDialog("部门名称","","");
            }
        });
        mLeft=(TextView) findViewById(R.id.left);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client=new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                String id="";
                HashMap<Integer,Boolean> se= MyAdapter.getIsSelected();
                for(int i=0;i<list.size();i++){
                    boolean b=se.get(i);
                    if(b){
                        id=id+list.get(i).get("id")+",";
                    }
                }
                if(id.length()==0){
                    UICommon.showTips(DepartmentListActivity.this,R.drawable.tips_error,"请选择要删除的部门");
                    return;
                }
                id=id.substring(0,id.length()-1);
                System.out.println(id);
                requestParams.add("id", id);
                client.post(DepartmentListActivity.this, IMConfiguration.deleteDept, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int j, Header[] headers, byte[] bytes) {
                        mDialog.dismiss();
                        if ("ok".equals(new String(bytes))) {
                            UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_smile, "成功");
                        } else {
                            UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_smile, "服务器异常");
                        }
                        getData();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        mDialog.dismiss();
                        UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_error, "网络异常");
                    }
                });
            }
        });
        mListview.setAdapter(myAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String nid = (String) list.get(position).get("id");
                String name=(String) list.get(position).get("name");
                System.out.println(nid + name);
                type = 1;
                showGroupNameOrInfoDialog("部门名称", name, nid);
            }
        });
    }
    private List<Map<String,String>> getData() {
        list=new ArrayList<Map<String, String>>();
        AsyncHttpClient client=new AsyncHttpClient();

        RequestParams requestParams=new RequestParams();
        requestParams.add("community", mGroupName);
        client.post(DepartmentListActivity.this, IMConfiguration.departList, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int j, Header[] headers, byte[] bytes) {
                mDialog.dismiss();
                JSONArray jsonArray = null;
                try {
                    list.clear();
                    jsonArray = new JSONArray(new String(bytes));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String, String> map = new HashMap<String, String>();
                        JSONObject js = jsonArray.getJSONObject(i);
                        map.put("name", js.getString("deptname"));
                        map.put("id", js.getString("id"));
                        list.add(map);
                    }
                    Message message = new Message();
                    message.what = 123421;
                    DepartmentListActivity.this.handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mDialog.dismiss();
            }
        });
        return list;
    }
    private void showGroupNameOrInfoDialog(final String updateName, final String name,final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DepartmentListActivity.this);
        View view = LayoutInflater.from(DepartmentListActivity.this).inflate(
                R.layout.dialog_nickname, null);

        final TextView titleTV = (TextView) view.findViewById(R.id.dialog_title);
        final EditText editET = (EditText) view.findViewById(R.id.dialog_edittext);
        Button cancleBtn = (Button) view.findViewById(R.id.dialog_cancle);
        Button sureBtn = (Button) view.findViewById(R.id.dialog_sure);

        titleTV.setText(updateName);
        editET.setHint("请输入" + updateName);
        editET.setText(name);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.show();

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                AsyncHttpClient client=new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                requestParams.add("id", id);
                requestParams.add("deptName", editET.getText().toString());
                requestParams.add("community",mGroupName);
                String url="";
                if(type==1){
                    url=IMConfiguration.updateDept;
                }else if(type==2){
                    url=IMConfiguration.addDept;
                }
                client.post(DepartmentListActivity.this, url, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int j, Header[] headers, byte[] bytes) {
                            mDialog.dismiss();
                            if("ok".equals(new String(bytes))) {
                                UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_smile, "成功");
                            }else{
                                UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_smile, "服务器异常");
                            }
                            getData();
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            mDialog.dismiss();
                            UICommon.showTips(DepartmentListActivity.this, R.drawable.tips_error, "网络异常");
                        }
                    });
                dialog.dismiss();
            }
        });
    }
}
