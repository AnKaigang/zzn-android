package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpGetEventList;
import com.imsdk.imdeveloper.ui.view.TimeLineView;
import com.imsdk.imdeveloper.ui.view.TimeViewInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsdk.data.IMMyself;

public class TaskListActivity extends Activity implements View.OnClickListener {

    List<Map<String, Object>> list;
    public String mGroupId;
    public String mGroupName;
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;

    public TimeLineView timeLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_list);

        findViewById();
        setListener();
        initView();
        ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
        clubDao.createClubInfo();
        JSONObject jsonObject=clubDao.queryClubInfo(mGroupName);
        if(jsonObject!=null) {
            try {
                new httpGetEventList(TaskListActivity.this,mGroupId).execute(jsonObject.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void initView() {
        ArrayList<TimeViewInfo> infos = new ArrayList<TimeViewInfo>();
        ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
        clubListDao.createEvent();
        clubListDao.deleteEvent();
        timeLineView.setTimeInfos(infos);
    }

    public TimeViewInfo createTimeInfo(boolean first, boolean last,
                                        String date, String title, String summary) {
        TimeViewInfo info = new TimeViewInfo();

        View leftMain = createLeftView(date);
        info.leftView = leftMain;

        boolean showNode = false;
        if (date == null || date.equals("")) {
            showNode = false;
        } else {
            showNode = true;
        }
        View middleMain = createMiddleView(showNode, first, last);
        info.middleView = middleMain;

        View right = createRightView(title, summary);
        info.rightView = right;

        return info;
    }

    public View createRightView(String titleStr, String summaryStr) {
        View rightView = LayoutInflater.from(this).inflate(
                R.layout.time_line_right, null);
        TextView title = (TextView) rightView.findViewById(R.id.title);
        title.setText(titleStr);
        TextView summary = (TextView) rightView.findViewById(R.id.summary);
        summary.setText(summaryStr);
        return timeLineView.createRightView(this, rightView);
    }

    public View createMiddleView(boolean showNode, boolean first, boolean last) {
        View middleView = LayoutInflater.from(this).inflate(
                R.layout.time_line_middle, null);
        if (!showNode)
            middleView.findViewById(R.id.node).setVisibility(View.INVISIBLE);
        if (first)
            middleView.findViewById(R.id.top).setVisibility(View.INVISIBLE);
        if (last)
            middleView.findViewById(R.id.bottom).setVisibility(View.INVISIBLE);

        return timeLineView.createMiddleView(this, middleView);
    }

    public View createLeftView(String content) {
        View leftView = LayoutInflater.from(this).inflate(
                R.layout.time_line_left, null);
        TextView date = (TextView) leftView.findViewById(R.id.date);
        date.setText(content);
        return timeLineView.createLeftView(this, leftView);
    }

    private void setListener() {
        mTitleBarLogo.setText("活动列表");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("管理");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    private void findViewById() {
        Intent i=getIntent();
        mGroupId=i.getStringExtra("groupId");
        mGroupName=i.getStringExtra("groupName");

        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button) findViewById(R.id.imbasetitlebar_right);
        timeLineView = (TimeLineView) findViewById(R.id.timeLineView);
        timeLineView.setWeight(4, 2, 24);
        timeLineView.addLeftRule(RelativeLayout.ALIGN_PARENT_LEFT);
        timeLineView.addLeftRule(RelativeLayout.CENTER_VERTICAL);
        timeLineView.addMiddleRule(RelativeLayout.CENTER_VERTICAL);
        timeLineView.addMiddleRule(RelativeLayout.ALIGN_PARENT_TOP);
        timeLineView.addMiddleRule(RelativeLayout.CENTER_HORIZONTAL);

        timeLineView.addRightRule(RelativeLayout.ALIGN_PARENT_LEFT);
        timeLineView.addRightRule(RelativeLayout.CENTER_VERTICAL);

       /* String position=getIntent().getStringExtra("position");
        if(position==null||position.equals("部员")){
            mRight_titleBar.setVisibility(View.GONE);
            mRight_titleBar.setEnabled(false);
        }*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.imbasetitlebar_right:
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);
                View v = LayoutInflater.from(TaskListActivity.this).inflate(
                        R.layout.dialog_task, null);
                LinearLayout uploadLayout = (LinearLayout) v.findViewById(R.id.layout_upload);
                LinearLayout delLayout = (LinearLayout) v.findViewById(R.id.layout_del);

                builder.setView(v);
                final AlertDialog dialog = builder.create();

                dialog.show();
                uploadLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(TaskListActivity.this, AddClubList.class);
                        intent.putExtra("groupId",mGroupId);
                        intent.putExtra("groupName",mGroupName);
                        startActivity(intent);
                    }
                });

                delLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent = new Intent(TaskListActivity.this, DelActivity.class);
                        intent.putExtra("groupId",mGroupId);
                        intent.putExtra("groupName",mGroupName);
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}
