package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMSDK;
import imsdk.data.mainphoto.IMSDKMainPhoto;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.userDao;
import com.imsdk.imdeveloper.http.httpLogin;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.TipsToast;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {
    private SharedPreferences mySharedPreferences;



    private userDao userdao = new userDao();
    private EditText mUserNameEditText; // 帐号编辑框
    private EditText mPasswordEditText; // 密码编辑框

    private httpLogin httplogin;

    private ImageView mImageView;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    private static LoadingDialog mDialog;

    private long mExitTime;

    private final static int SUCCESS = 0;
    private final static int FAILURE = -1;

    private static TipsToast mTipsToast;

    public LoginActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使得音量键控制媒体声音
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //layout
        setContentView(R.layout.activity_login);

        mDialog = new LoadingDialog(this, "正在登录...");
        mDialog.setCancelable(true);
        initView();
        initListener();
    }


    private void initView() {

        mUserNameEditText = (EditText) findViewById(R.id.login_user_name_edittext);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_edittext);

        mLoginBtn = (Button) findViewById(R.id.login_login_btn);
        mRegisterBtn = (Button) findViewById(R.id.login_register_btn);

        mImageView = (ImageView) findViewById(R.id.login_imageview);

        //设置默认用户名
        mUserNameEditText.addTextChangedListener(mTextWatcher);
        JSONObject js = userdao.query();
        try {
            if (js != null) {
                mUserNameEditText.setText(js.getString("user"));
                mPasswordEditText.setText(js.getString("passwd"));
            } else {
                mUserNameEditText.setHint("学号");
                mPasswordEditText.setHint("密码");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initListener() {
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
    }


    public void updateStatus(int status, String user) {
        switch (status) {
            case SUCCESS: {
                mDialog.dismiss();
                //UICommon.showTips(LoginActivity.this, R.drawable.tips_smile, "登录成功");

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                intent.putExtra("userName", user);
                startActivity(intent);
                //缓存用户名

                LoginActivity.this.finish();
            }
            break;
            case FAILURE:
                mDialog.dismiss();
                mLoginBtn.setEnabled(true);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                final String username=mUserNameEditText.getText().toString();
                final String password=mPasswordEditText.getText().toString();
                if(username==null||username==""){
                    UICommon.showTips(this,R.drawable.tips_error,"学号为空！");
                    return;
                }
                if(!IMMyself.setCustomUserID(username)){
                    UICommon.showTips(this,R.drawable.tips_error,"学生不存在");
                    return;
                }
                mDialog.show();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                requestParams.add("username",username);
                requestParams.add("password", password);
                client.post(IMConfiguration.login, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers,byte[] bytes) {
                        try {
                            JSONObject jsonObject=new JSONObject(new String(bytes));
                            LoginActivity.loginNo(LoginActivity.this, username, password, 0,1);
                            userDao userdao =new userDao();
                            if(userdao.query()!=null&&!userdao.query().getString("user").equals(jsonObject.getString("username"))){
                                userdao.deleteALl();
                                userdao.insert(jsonObject.getString("username"),jsonObject.getString("password"));
                            }else{
                                userdao.insert(jsonObject.getString("username"),jsonObject.getString("password"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        if(bytes!=null&&"null".equals(new String(bytes))) {
                            mDialog.dismiss();
                            UICommon.showTips(LoginActivity.this, R.drawable.tips_error, "该用户不存在");
                        }else{
                            UICommon.showTips(LoginActivity.this, R.drawable.tips_error, "网络异常");
                        }
                    }
                });
                break;
            case R.id.login_register_btn: {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            default:
                break;
        }
    }

    public void login(final Activity activity, final String user, String passwd, final int state) {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        intent.putExtra("userName", user);
        startActivity(intent);
        //缓存用户名

        LoginActivity.this.finish();
        boolean result = IMMyself.setCustomUserID(user);
    }
    public static void loginNo(final Activity activity, final String user, String passwd, final int state,final int type) {

        boolean result = IMMyself.setCustomUserID(user);

        if (!result) {
            UICommon.showTips(activity,R.drawable.tips_error,"学号好像不咋对");
            return ;
        }

        result = IMMyself.setPassword(passwd);

        if (!result) {
            UICommon.showTips(activity,R.drawable.tips_error,"哎，再看下密码吧");
            return ;
        }
        IMMyself.login(false, 5, new OnActionListener() {
            @Override
            public void onSuccess() {
                if(type==1) {
                    mDialog.dismiss();
                }
                Intent mainIntent = new Intent(activity,MainActivity.class);
                mainIntent.putExtra("username", user);
                activity.startActivity(mainIntent);
                activity.finish();
            }

            @Override
            public void onFailure(String error)
            {
                if(type==1) {
                    mDialog.dismiss();
                }
                if (state==0) {
                    if (error.equals("Timeout")) {
                        error = "登录超时";
                    } else if (error.equals("Wrong Password")) {
                        error = "密码错误";
                    }else if (error.equals("CustomUserID don't Exist")) {
                        error = "学生未注册";
                    }
                    UICommon.showTips(activity, R.drawable.tips_error, error);
                    if(type==0) {
                        Intent mainIntent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(mainIntent);
                        activity.finish();
                    }
                }
            }
        });
    }

    private void login() {

        boolean result = IMMyself.setCustomUserID(mUserNameEditText.getText()
                .toString());

        if (!result) {
            UICommon.showTips(LoginActivity.this, R.drawable.tips_warning, IMSDK.getLastError());
            mDialog.dismiss();
            return;
        }

        result = IMMyself.setPassword(mPasswordEditText.getText().toString());

        if (!result) {
            showTips(R.drawable.tips_warning, IMSDK.getLastError());
            mDialog.dismiss();
            return;
        }
        IMMyself.login(false, 5, new OnActionListener() {
            @Override
            public void onSuccess() {
                UICommon.showTips(LoginActivity.this, R.drawable.tips_smile, "登录成功");
                updateStatus(SUCCESS, mUserNameEditText.getText().toString());
            }

            @Override
            public void onFailure(String error) {
                if (error.equals("Timeout")) {
                    error = "登录超时";
                } else if (error.equals("Wrong Password")) {
                    error = "密码错误";
                }

                updateStatus(FAILURE, mUserNameEditText.getText().toString());
                UICommon.showTips(LoginActivity.this, R.drawable.tips_error, error);
            }
        });
    }


    public void showTips(int iconResId, String tips) {
        if (mTipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mTipsToast.cancel();
            }
        } else {
            mTipsToast = TipsToast.makeText(getApplication().getBaseContext(), tips,
                    TipsToast.LENGTH_SHORT);
        }

        mTipsToast.show();
        mTipsToast.setIcon(iconResId);
        mTipsToast.setText(tips);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    LoginActivity.this.finish();
                }
                break;
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Uri uri = IMSDKMainPhoto.getLocalUri(s.toString());

            if (uri != null) {
                IMApplication.sImageLoader.displayImage(uri.toString(), mImageView,
                        IMApplication.sDisplayImageOptions);
            } else {
                mImageView.setImageResource(R.drawable.icon);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                UICommon.showTips(LoginActivity.this, R.drawable.tips_smile, "再按一次返回桌面");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userdao.destroy();
    }
}