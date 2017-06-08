package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.http.HttpGetCheckCode;
import com.imsdk.imdeveloper.http.httpRegister;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.util.T;

public class RegisterActivity extends Activity implements OnClickListener {
    // data
    private String mCustomUserID;
    private String mPassword;
    private String mRepassword;
    private String mPhoneNumber;
    private String checkCode;
    //    int iTemp=0;
    // ui
    private EditText mUserEditText; // 帐号编辑框
    private EditText mPasswordEditText; // 密码编辑框
    private EditText mRePasswordEditText;
    private EditText mPhoneEditText;
    public ImageView imageView;
    private EditText mCheckCodeEditText;

    private HttpGetCheckCode httpGetCheckCode=new HttpGetCheckCode(this);
    private Button mRegisterBtn;
    private Button mBackBtn;
    public LoadingDialog mDig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使得音量键控制媒体声音
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_yonghu_zhuce);


        mUserEditText = (EditText) findViewById(R.id.register_user_edit);
        mPasswordEditText = (EditText) findViewById(R.id.register_passwd_edit);
        mRePasswordEditText = (EditText) findViewById(R.id.re_register_passwd_edit);
        mPhoneEditText = (EditText) findViewById(R.id.phone_number);
        mCheckCodeEditText= (EditText) findViewById(R.id.checkCode);
        imageView= (ImageView) findViewById(R.id.checkCodeImage);


        mRegisterBtn = (Button) findViewById(R.id.register_register_btn);
        mRegisterBtn.setOnClickListener(this);
        mBackBtn = (Button) findViewById(R.id.btn_back);
        imageView.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

        httpGetCheckCode.execute("","","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_register_btn:
                reigster();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.checkCodeImage:
            {
                new HttpGetCheckCode(this).execute("");
            }
                break;
            default:
                break;
        }
    }

    public void reigster() {
        mCustomUserID = mUserEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();
        mRepassword = mRePasswordEditText.getText().toString();
        mPhoneNumber = mPhoneEditText.getText().toString();
        checkCode=mCheckCodeEditText.getText().toString();
        // 判断帐号和密码
        if ("".equals(mCustomUserID) || "".equals(mPassword) || "".equals(mRepassword)) {
            UICommon.showTips(RegisterActivity.this, R.drawable.tips_warning, "帐号或密码不能为空，\n请输入后再注册！");
        } else if (!mPassword.equals(mRepassword)) {
            UICommon.showTips(RegisterActivity.this, R.drawable.tips_warning, "两次输入的密码不一致!");
        } else {
            //mRegisterBtn.setEnabled(false);
            // 注册
//			iTemp++;
//			String text=null;
//			String.format(text,iTemp);
//			mUserEditText.setText("1" + text);

            mDig=new LoadingDialog(this,"正在注册");
            mDig.show();
            new httpRegister(this).execute(mCustomUserID, mPhoneNumber, mPassword,checkCode);
        }
    }

    public static void registerThread(final RegisterActivity registerActivity, final String CustomUserID, final String Password) {

        IMMyself.setCustomUserID(CustomUserID);
        IMMyself.setPassword(Password);

        IMMyself.register(5, new OnActionListener() {
            @Override
            public void onSuccess() {
                registerActivity.mDig.dismiss();
                //mRegisterBtn.setEnabled(true);
                T.show(registerActivity, "注册成功");
                LoginActivity.loginNo(registerActivity,CustomUserID,Password,0,0);

                //Intent intent = new Intent();

//				intent.putExtra("username", mCustomUserID);
//				intent.putExtra("password", mPassword);
//				setResult(Activity.RESULT_OK, intent);
                registerActivity.finish();

            }

            @Override
            public void onFailure(String error) {
                //mRegisterBtn.setEnabled(true);
                registerActivity.mDig.dismiss();
                if (error.equals("Timeout")) {
                    error = "注册超时";
                }
                if (error.equals("9")) {
                    error = "注册帐号已存在";
                }

                T.show(registerActivity, "注册失败:" + error);
            }
        });
    }
}
