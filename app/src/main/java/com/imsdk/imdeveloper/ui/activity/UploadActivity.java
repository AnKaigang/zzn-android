package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.CommunityEvent;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.http.httpClubCreatlist;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import imsdk.data.IMMyself;

public class UploadActivity extends Activity implements OnClickListener {

    String mGroupName;
    String mGroupId;
    Calendar calendar;
    EditText et_date;
    EditText mTitleEditText;
    String mTitle;
    Button mSubmitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shangchuan);
        mGroupId=getIntent().getStringExtra("groupId");
        mGroupName=getIntent().getStringExtra("groupName");
        mTitleEditText= (EditText) findViewById(R.id.title);
        et_date = (EditText)findViewById(R.id.task_date);
        calendar = Calendar.getInstance();
        mSubmitBtn= (Button) findViewById(R.id.submit_task);
        mSubmitBtn.setEnabled(true);
        mSubmitBtn.setOnClickListener(this);
        et_date.setOnClickListener(this);
        ((ImageButton) findViewById(R.id.imbasetitlebar_back))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.task_date:
                showDateDialog();
                break;
            case R.id.submit_task:
                ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                JSONObject js=clubDao.queryClubInfo(mGroupName);
                if(js!=null){
                    try {
                        String clubId=js.getString("id");
                        String content=mTitleEditText.getText().toString();
                        String date=et_date.getText().toString();
                        CommunityEvent communityEvent=new CommunityEvent();
                        communityEvent.setCommunityid(clubId);
                        communityEvent.setDate(date);
                        communityEvent.setEventcontent(content);
                        String s=new Gson().toJson(communityEvent);
                        //new httpClubCreatlist(UploadActivity.this).execute(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showDateDialog()
    {
        DatePickerDialog date_dialog=new DatePickerDialog(UploadActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                et_date.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        date_dialog.show();
    }


}
