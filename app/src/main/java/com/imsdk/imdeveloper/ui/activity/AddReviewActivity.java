package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;

import imsdk.data.IMMyself;
import imsdk.data.community.IMCommunity;

public class AddReviewActivity extends Activity implements View.OnClickListener{

    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        findViewById();
        setListener();
        initView();
    }

    private void initView() {

    }

    public void setListener(){
        mTitleBarLogo.setText("评论");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("完成");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    public void findViewById(){
        editText = (EditText)findViewById(R.id.review_message);
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);
        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button)findViewById(R.id.imbasetitlebar_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.imbasetitlebar_right:
                IMCommunity.createComment(
                        getIntent().getLongExtra("topicId",0),
                        editText.getText().toString(),
                        new IMMyself.OnActionResultListener() {

                            @Override
                            public void onSuccess(Object result) {

                                Toast.makeText(AddReviewActivity.this, "创建成功:" + result, Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {

                                Toast.makeText(AddReviewActivity.this, "创建失败:" + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

        }
    }
}
