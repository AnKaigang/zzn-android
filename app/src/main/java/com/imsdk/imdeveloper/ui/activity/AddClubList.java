package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.CommunityEvent;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.http.httpClubCreatlist;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.datePicker;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import imsdk.data.IMMyself;

public class AddClubList extends Activity {

    EditText mTitleEdit;
    EditText mMessageEdit;
    public EditText mDateEdit;
    Calendar calendar;
    Button submit;
    String mGroupName;
    String mGroupId;
    String mTitle;
    String mMessage;
    String mDate;
    public LoadingDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club_list);
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        calendar = Calendar.getInstance();
        mTitleEdit= (EditText) findViewById(R.id.title);
        mMessageEdit= (EditText) findViewById(R.id.message);
        mDateEdit= (EditText) findViewById(R.id.date);
        submit= (Button) findViewById(R.id.submit_activity);
        /*new datePicker(AddClubList.this);*/
        mDateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDateDialog();
                }
            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle=mTitleEdit.getText().toString();
                mDate=mDateEdit.getText().toString();
                mMessage=mMessageEdit.getText().toString();
                if(valite())
                {
                    ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                    JSONObject js=clubDao.queryClubInfo(mGroupName);
                    if(js!=null){
                        try {
                            String clubId=js.getString("id");
                            String content=mMessage;
                            String date=mDate;
                            String title=mTitle;
                            CommunityEvent communityEvent=new CommunityEvent();
                            communityEvent.setCommunityid(clubId);
                            communityEvent.setDate(date);
                            communityEvent.setEventcontent(content);
                            communityEvent.setTitle(title);
                            String s=new Gson().toJson(communityEvent);
                            mDialog=new LoadingDialog(AddClubList.this,"正在添加");
                            mDialog.show();
                            send(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    private boolean valite()
    {
        if (mTitle == null) {
            UICommon.showTips(this, R.drawable.tips_error, "主题为空不好吧");
            return false;
        } else if (mDate == null) {
            UICommon.showTips(this, R.drawable.tips_error, "日期为空不好吧");
            return false;
        }else if(mMessage==null){
            UICommon.showTips(this, R.drawable.tips_error, "内容为空不好吧");
            return false;
        }
        return true;
    }
    private void send(String s){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams=new RequestParams();
        requestParams.add("event", s);

        client.post(IMConfiguration.addEvent, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String s = new String(bytes);
                mDialog.dismiss();
                if (s != null && "ok".equals(s)) {
                    UICommon.showTips(AddClubList.this, R.drawable.tips_smile, "加入活动成功");
                } else {
                    UICommon.showTips(AddClubList.this, R.drawable.tips_error, "加入活动失败");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mDialog.dismiss();
                UICommon.showTips(AddClubList.this, R.drawable.tips_error, "网络异常");
            }
        });
    }
    private void showDateDialog() {
        DatePickerDialog date_dialog = new DatePickerDialog(AddClubList.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateEdit.setText(+year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        date_dialog.show();
    }
}
