package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpDeleteClubEve;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import imsdk.data.IMMyself;

public class DelActivity extends Activity implements View.OnClickListener{


    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;
    private Button mDelButton;
    private ClearEditText mClearEditText;
    private String mGroupId;
    private String mGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_del);

        findViewById();
        setListener();
        initView();


    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        Intent i=getIntent();
        mGroupId=i.getStringExtra("groupId");
        mGroupName=i.getStringExtra("groupName");

        mClearEditText= (ClearEditText) findViewById(R.id.delete_edittext);
        mDelButton= (Button) findViewById(R.id.delete_btn);
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button)findViewById(R.id.imbasetitlebar_right);

    }
    private void setListener() {
        mTitleBarLogo.setText("删除活动");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
        mDelButton.setOnClickListener(this);
    }
    private void initView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.delete_btn:
                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                clubListDao.createEvent();
                if(!mClearEditText.getText().toString().equals("")) {
                    JSONObject js=clubListDao.queryEventIsExistsByContent(mClearEditText.getText().toString());
                    if(js==null){
                        UICommon.showTips(DelActivity.this,R.drawable.tips_error,"活动不存在");
                    }else {
                        try {
                            new httpDeleteClubEve(DelActivity.this).execute(js.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    UICommon.showTips(DelActivity.this,R.drawable.tips_error,"不可为空");
                }
                break;
            default:

                break;
        }
    }
}
