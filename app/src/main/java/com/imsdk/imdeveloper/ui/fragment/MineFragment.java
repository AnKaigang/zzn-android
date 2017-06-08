package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnInitializedListener;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import imsdk.data.mainphoto.IMMyselfMainPhoto;
import imsdk.data.nickname.IMSDKNickname;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.ui.activity.AroundActivity;
import com.imsdk.imdeveloper.ui.activity.JiaowuActivity;
import com.imsdk.imdeveloper.ui.activity.LoginActivity;
import com.imsdk.imdeveloper.ui.activity.MyProfileActivity;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.ui.view.SettingSwitchButton;
import com.imsdk.imdeveloper.util.CommonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * 我的
 */
public class MineFragment extends Fragment implements OnClickListener {
    // data
    private SharedPreferences mSharedPreferences;

    // ui
    private RelativeLayout mModifyUserInfoLayout;
    private RelativeLayout mJiaowuLayout;
    private RelativeLayout mNearByLayout;
    private Button mLogoutBtn;

    private RoundedCornerImageView mMainPhotoImageView;
    private TextView mUserNameTextView;
    private TextView mRegionTextView;

    private SettingSwitchButton mSoundSwitchBtn;
    private SettingSwitchButton mVibrateSwitchBtn;
    private RelativeLayout mKefuBtn;

    private void updateUserNameRegion() {

        if (CommonUtil.isNull(IMSDKNickname.get())) {
            mUserNameTextView.setText(IMMyself.getCustomUserID());
        } else {
            mUserNameTextView.setText(IMSDKNickname.get());
        }

        String customUserInfo = IMMyselfCustomUserInfo.get();
        String[] array = customUserInfo.split("\n");

        if (array.length == 3) {
            mRegionTextView.setText(array[1]);
        } else {
            mRegionTextView.setText("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        mModifyUserInfoLayout = (RelativeLayout) view
                .findViewById(R.id.mine_modify_userinfo_layout);
        mModifyUserInfoLayout.setOnClickListener(this);
        mJiaowuLayout = (RelativeLayout) view
                .findViewById(R.id.layout_jiaowu);
        mJiaowuLayout.setEnabled(true);
        mJiaowuLayout.setOnClickListener(this);

        mNearByLayout = (RelativeLayout) view.findViewById(R.id.layout_fujin);
        mNearByLayout.setEnabled(true);
        mNearByLayout.setOnClickListener(this);
        mLogoutBtn = (Button) view.findViewById(R.id.mine_logout_btn);
        mLogoutBtn.setOnClickListener(this);

        mMainPhotoImageView = (RoundedCornerImageView) view
                .findViewById(R.id.mine_mainphoto_imageview);
        mMainPhotoImageView.setRoundness(8);//圆角

        mUserNameTextView = (TextView) view.findViewById(R.id.mine_name_textview);
        mRegionTextView = (TextView) view.findViewById(R.id.mine_location_textview);

        if (IMMyselfCustomUserInfo.isInitialized()) {
            updateUserNameRegion();
        } else {
            mUserNameTextView.setText("");

            IMMyselfCustomUserInfo
                    .setOnInitializedListener(new OnInitializedListener() {
                        @Override
                        public void onInitialized() {
                            updateUserNameRegion();
                        }
                    });
        }

        if (IMMyselfMainPhoto.isInitialized()) {
            Bitmap bitmap = IMMyselfMainPhoto.get();

            if (bitmap != null) {
                mMainPhotoImageView.setImageBitmap(bitmap);
            } else {
                mMainPhotoImageView.setImageResource(R.drawable.news_head_man);
            }
        } else {
            IMMyselfMainPhoto.setOnInitializedListener(new OnInitializedListener() {
                @Override
                public void onInitialized() {
                    Bitmap bitmap = IMMyselfMainPhoto.get();

                    if (bitmap != null) {
                        mMainPhotoImageView.setImageBitmap(bitmap);
                    } else {
                        mMainPhotoImageView.setImageResource(R.drawable.news_head_man);
                    }
                }
            });
        }

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        IMConfiguration.sSoundNotice = mSharedPreferences.getBoolean("soundNotice",
                true);
        IMConfiguration.sVibrateNotice = mSharedPreferences.getBoolean("vibrateNotice",
                true);


        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.mine_modify_userinfo_layout:
                if (IMMyselfMainPhoto.isInitialized()) {
                    intent = new Intent(getActivity(), MyProfileActivity.class);
                    startActivity(intent);
                } else {
                    if (getActivity() == null) {
                        return;
                    }

                    Toast.makeText(getActivity(), "正在初始化", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.mine_logout_btn:
                IMMyself.logout();
                AsyncHttpClient client=new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                requestParams.add("username",IMMyself.getCustomUserID());
                client.post(IMConfiguration.logout, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("CustomUserID", IMMyself.getCustomUserID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                });
                break;
            case R.id.layout_fujin:


                Intent intent_radar = new Intent(getActivity(), AroundActivity.class);
                //intent.putExtra("CustomUserID", IMMyself.getCustomUserID());
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent_radar);
                //getActivity().finish();
                break;
            case R.id.layout_jiaowu: {
                Intent intent2 = new Intent(getActivity(), JiaowuActivity.class);
                startActivity(intent2);
            }
            break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (IMMyselfMainPhoto.isInitialized()) {
            Bitmap bitmap = IMMyselfMainPhoto.get();

            if (bitmap != null) {
                mMainPhotoImageView.setImageBitmap(bitmap);
            } else {
                mMainPhotoImageView.setImageResource(R.drawable.news_head_man);
            }
        }
    }
}
