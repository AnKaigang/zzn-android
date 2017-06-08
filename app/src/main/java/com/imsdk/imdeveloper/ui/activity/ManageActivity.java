package com.imsdk.imdeveloper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;

public class ManageActivity extends Activity implements OnClickListener{

    LinearLayout inviteLayout;
    LinearLayout renmianLayout;
    LinearLayout deleteLayout;

    public String mGroupId;
    public String mGroupName;
    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        findViewById();
        setListener();
        initView();



    }

    private void setListener() {
        mTitleBarLogo.setText("管理社团");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);

        inviteLayout.setOnClickListener(this);

        renmianLayout.setOnClickListener(this);

        deleteLayout.setOnClickListener(this);
    }

    private void initView() {

    }

    private void findViewById() {
        Intent i=getIntent();
        mGroupId=i.getStringExtra("groupId");
        mGroupName=i.getStringExtra("groupName");

        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button)findViewById(R.id.imbasetitlebar_right);

        inviteLayout = (LinearLayout) findViewById(R.id.layout_invite);
        renmianLayout = (LinearLayout) findViewById(R.id.layout_appoint);
        deleteLayout = (LinearLayout) findViewById(R.id.layout_delete);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.layout_invite:
                intent = new Intent(ManageActivity.this, InviteActivity.class);
                intent.putExtra("groupId",mGroupId);
                intent.putExtra("groupName",mGroupName);
                startActivity(intent);
                break;
            case R.id.layout_appoint:
                intent = new Intent(ManageActivity.this, RenmianActivity.class);
                intent.putExtra("groupId",mGroupId);
                intent.putExtra("groupName",mGroupName);
                startActivity(intent);
                break;

            case R.id.layout_delete:
                intent = new Intent(ManageActivity.this, DeleteActivity.class);
                intent.putExtra("groupId",mGroupId);
                intent.putExtra("groupName",mGroupName);
                startActivity(intent);
                break;

        }
    }
}
