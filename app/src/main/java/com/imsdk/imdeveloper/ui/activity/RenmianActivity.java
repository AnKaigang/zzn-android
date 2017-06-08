package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.Rlation;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpAddClub;
import com.imsdk.imdeveloper.ui.a1common.UICommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import imsdk.data.IMMyself;

public class RenmianActivity extends Activity implements View.OnClickListener {


    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;
    private Button mRenMianButton;
    private RadioGroup mRadioGroup;
    public String mGroupId;
    public String mGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renmian);

        findViewById();
        setListener();
        initView();
    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mRenMianButton = (Button) findViewById(R.id.renmian_btn);
        mGroupId = getIntent().getStringExtra("groupId");
        mGroupName = getIntent().getStringExtra("groupName");
        mRadioGroup = (RadioGroup) findViewById(R.id.renmian_Radiogroup);

        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button) findViewById(R.id.imbasetitlebar_right);
    }

    private void setListener() {
        mTitleBarLogo.setText("任免职位");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    private void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.renmian_btn:
                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                clubListDao.createIdName();
                String userid = clubListDao.queryIdNameByUserName(IMMyself.getCustomUserID());
                if (userid == null) {
                    UICommon.showTips(RenmianActivity.this, R.drawable.tips_error, "失败");
                } else {
                    ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                    clubDao.createClubInfo();
                    JSONObject jsonObject = clubDao.queryClubInfo(mGroupName);
                    if (jsonObject == null) {
                        UICommon.showTips(RenmianActivity.this, R.drawable.tips_error, "失败");
                    } else {
                        String position;
                        if (mRadioGroup.getCheckedRadioButtonId() == R.id.chairman_btn) {
                            position = "主席";
                        } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.minister_btn) {
                            position = "部长";
                        } else {
                            position = "部员";
                        }
                        Rlation relation = new Rlation();
                        try {
                            relation.setCommunityid(jsonObject.getString("id"));
                            relation.setPosition(position);
                            relation.setUserid(userid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String s= new Gson().toJson(relation);
                        new httpAddClub(RenmianActivity.this).execute(s);
                    }
                }
                break;
            default:

                break;
        }
    }
}
