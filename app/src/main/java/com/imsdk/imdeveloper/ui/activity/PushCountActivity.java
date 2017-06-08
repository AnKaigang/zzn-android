package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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

import imsdk.data.IMMyself;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import imsdk.data.mainphoto.IMMyselfMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.nickname.IMSDKNickname;

public class PushCountActivity extends Activity {
    String id;
    private LoadingDialog mDialog;
    private List<Map<String,Object>> list1;
    private List<Map<String,Object>> list2;
    private TextView mTitle;
    Bitmap bitmap;
    private TextView mContent;
    private TextView mCount;
    private GridView listView1;
    private GridView listView2;
    private SimpleAdapter sim1;
    private SimpleAdapter sim2;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_count);
        IMSDKNickname.setOnInitializedListener(new IMMyself.OnInitializedListener() {
            @Override
            public void onInitialized() {
                // 昵称模块已初始化
            }
        });
        IMMyselfMainPhoto.setOnInitializedListener(new IMMyself.OnInitializedListener() {
            @Override
            public void onInitialized() {
                // 头像模块已初始化
            }
        });
        mTitle= (TextView) findViewById(R.id.pushCountTitle);
        mContent= (TextView) findViewById(R.id.pushCountContent);
        mCount= (TextView) findViewById(R.id.pushCountCount);
        listView1= (GridView) findViewById(R.id.pushCountArrived);
        listView2= (GridView) findViewById(R.id.pushCountNoArrived);
        mDialog=new LoadingDialog(PushCountActivity.this,"正在加载");
        mTitle.setText(getIntent().getStringExtra("title"));
        mContent.setText(getIntent().getStringExtra("content"));
        list1=new ArrayList<Map<String, Object>>();
        list2=new ArrayList<Map<String, Object>>();

        sim1=new SimpleAdapter(PushCountActivity.this,list1,R.layout.item_headerandname,new String[]{"header","name"},new int[]{R.id.pushHeader,R.id.pushName});
        sim2=new SimpleAdapter(PushCountActivity.this,list2,R.layout.item_headerandname,new String[]{"header","name"},new int[]{R.id.pushHeader,R.id.pushName});
        sim2.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ImageView imageView = (ImageView) view;
                    Bitmap bmp = (Bitmap) data;
                    imageView.setImageBitmap(bmp);
                    return true;
                }
                return false;
            }
        });
        sim1.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if((view instanceof ImageView) && (data instanceof Bitmap)) {
                    ImageView imageView = (ImageView) view;
                    Bitmap bmp = (Bitmap) data;
                    imageView.setImageBitmap(bmp);
                    return true;
                }
                return false;
            }
        });
        listView1.setAdapter(sim1);
        listView2.setAdapter(sim2);
        mDialog.show();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123123:{
                        for(int i=0;i<list1.size();i++){
                            getHeadPhoto(0,i,list1.get(i).get("name").toString());
                        }
                        for(int i=0;i<list2.size();i++){
                            getHeadPhoto(0,i,list2.get(i).get("name").toString());
                        }
                        mCount.setText(list1.size()+"/"+(list2.size()+list1.size()));
                    }
                    break;
                }
            }
        };
        id=getIntent().getStringExtra("id");
        getData();
    }
    public void getData(){
        if(id==null||"".equals(id)){
            UICommon.showTips(PushCountActivity.this,R.drawable.tips_error,"数据丢失");
        }else{
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams requestParams=new RequestParams();
            requestParams.add("id", String.valueOf(id));
            boolean isInitialized = IMMyselfCustomUserInfo.isInitialized();
            boolean isInitialized1 = IMMyselfMainPhoto.isInitialized();
            if(isInitialized&&isInitialized1) {
                client.post(PushCountActivity.this, IMConfiguration.pushCount, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int j, Header[] headers, byte[] bytes) {
                        mDialog.dismiss();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(new String(bytes));
                            list1.clear();
                            list2.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                JSONObject js = jsonArray.getJSONObject(i);
                                Bitmap b=IMSDKMainPhoto.get(js.getString("userid"),30,30);
                                if(b==null) {
                                    b = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                                }
                                String name=IMSDKNickname.get(js.getString("userid"));
                                if(IMSDKNickname.get(js.getString("userid"))==null){
                                    name=js.getString("userid");
                                }
                                if ("1".equals(js.getString("isarrived"))) {
                                    map.put("header", b);
                                    map.put("name", name);
                                    list1.add(map);
                                } else {
                                    map.put("header", b);
                                    map.put("name", name);
                                    list2.add(map);
                                }
                            }
                            Message message = new Message();
                            message.what = 123123;
                            PushCountActivity.this.handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mDialog.dismiss();
                            UICommon.showTips(PushCountActivity.this, R.drawable.tips_error, "网络异常");
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        mDialog.dismiss();
                        UICommon.showTips(PushCountActivity.this, R.drawable.tips_error, "网络异常");
                    }
                });
                return;
            }else{
                getData();
            }
        }
    }
    private void getHeadPhoto(final int type, final int index,String userID) {
        IMSDKMainPhoto.request(userID, 30,
                new IMSDKMainPhoto.OnBitmapRequestProgressListener() {
                    @Override
                    public void onSuccess(Bitmap mainPhoto, byte[] buffer) {
                        mDialog.dismiss();
                        if (mainPhoto != null) {
                            if(type==0){
                                if(list1.size()>0){
                                    list1.get(index).put("header",mainPhoto);
                                    sim1.notifyDataSetChanged();
                                }

                            }else{
                                if(list2.size()>0) {
                                    list2.get(index).put("header", mainPhoto);
                                    sim2.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onProgress(double progress) {

                    }

                    @Override
                    public void onFailure(String error) {
                        mDialog.dismiss();
                    }
                });
    }
}
