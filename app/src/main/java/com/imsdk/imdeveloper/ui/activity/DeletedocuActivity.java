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
import com.imsdk.imdeveloper.http.httpDeleteFile;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import imsdk.data.IMMyself;

public class DeletedocuActivity extends Activity implements View.OnClickListener {

    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mDelButton;
    private Button mRight_titleBar;
    private String mGroupId;
    private String mGroupName;
    private ClearEditText mClearEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_deletedocu);

        findViewById();
        setListener();
        initView();

    }

    private void setListener(){
        mTitleBarLogo.setText("删除文件");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
        mDelButton.setOnClickListener(this);
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
                clubListDao.createFile();
                if(!mClearEditText.getText().toString().equals("")) {
                    JSONObject js=clubListDao.queryFileIsExistsByFileName(mClearEditText.getText().toString());
                    if(js==null){
                        UICommon.showTips(DeletedocuActivity.this, R.drawable.tips_error, "文件不存在");
                    }else {
                        try {
                            new httpDeleteFile(DeletedocuActivity.this).execute(js.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    UICommon.showTips(DeletedocuActivity.this,R.drawable.tips_error,"不可为空");
                }
                break;
            default:

                break;

        }
    }
}
