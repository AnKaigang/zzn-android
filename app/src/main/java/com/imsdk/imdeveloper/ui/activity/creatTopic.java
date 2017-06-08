package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.model.ImageItem;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.CustomConstants;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.ImageDisplayer;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.IntentConstants;
import com.imsdk.imdeveloper.util.ImagePublishAdapter;
import com.imsdk.imdeveloper.util.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import imsdk.data.IMMyself;
import imsdk.data.community.IMCommunity;

public class creatTopic extends Activity implements View.OnClickListener {


    private ArrayList<Bitmap> mBitmap = new ArrayList<Bitmap>();
    private Button mSubmitButton;
    private EditText mTitleEdit;
    private EditText mDateEdit;
    private EditText mAdressEdit;
    Calendar calendar;
    private LoadingDialog mLoading;

    private String mTitle;
    private String mDate;
    private String mAdress;


    private GridView mGridView;
    private ImagePublishAdapter mAdapter;
    public static List<com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.model.ImageItem> mDataList = new ArrayList<com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.model.ImageItem>();
    //拍照
    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";
    //选择图片
    public static final int SELECT_PICTURE = 0x000001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_creat_topic);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        calendar = Calendar.getInstance();
        mSubmitButton = (Button) findViewById(R.id.date_submit);
        mTitleEdit = (EditText) findViewById(R.id.createtopic_title);
        mDateEdit = (EditText) findViewById(R.id.createtopic_date);
        mAdressEdit = (EditText) findViewById(R.id.createtopic_address);
        mGridView= (GridView) findViewById(R.id.gridview);

//        mLeftImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        mLeftTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        mRightTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        mSubmitButton.setOnClickListener(this);
        mDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mDataList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(creatTopic.this, mGridView);
                } else {
                    Intent intent = new Intent(creatTopic.this,
                            ImageZoomActivity.class);
                    intent.putExtra("image_list",
                            (Serializable) mDataList);
                    intent.putExtra("current_img_position", position);
                    startActivity(intent);
                }
            }
        });
    }
    public class PopupWindows extends PopupWindow
    {

        public PopupWindows(Context mContext, View parent)
        {

            View view = View.inflate(mContext, R.layout.item_popupwindow, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    takePhoto();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intent = new Intent(creatTopic.this,
                            ImageBucketChooseActivity.class);
                    intent.putExtra("can_add_image_size",
                            getAvailableSize());
                    startActivityForResult(intent, SELECT_PICTURE);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    dismiss();
                }
            });

        }
    }
    private int getAvailableSize()
    {
        int availSize = 3 - mDataList.size();
        if (availSize >= 0)
        {
            return availSize;
        }
        return 0;
    }
    public void takePhoto()
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists())
        {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        }
        else
        {
            if (vFile.exists())
            {
                vFile.delete();
            }
        }
        path = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (mDataList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(path))
                {
                    ImageItem item = new ImageItem();
                    item.sourcePath = path;
                    mDataList.add(item);
                }
                break;
            case SELECT_PICTURE:
            {
                if(data == null)return;
                List<ImageItem> incomingDataList = (List<ImageItem>) data.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
                if (incomingDataList != null)
                {
                    mDataList.addAll(incomingDataList);
                }
            }
            break;
        }
    }
    private int getDataSize()
    {
        return mDataList == null ? 0 : mDataList.size();
    }


    private boolean valite() {
        mTitle = mTitleEdit.getText().toString();
        mDate = mDateEdit.getText().toString();
        mAdress = mAdressEdit.getText().toString();
        if (mTitle.equals("")) {
            UICommon.showTips(creatTopic.this, R.drawable.tips_error, "标题为空");
            return false;
        } else if (mDate.equals("")) {
            UICommon.showTips(creatTopic.this, R.drawable.tips_error, "时间为空");
            return false;
        } else if (mAdress.equals("")) {
            UICommon.showTips(creatTopic.this, R.drawable.tips_error, "地点为空");
            return false;
        } else if (mDataList.isEmpty()) {
            UICommon.showTips(creatTopic.this, R.drawable.tips_error, "图片为空");
            return false;
        }
        if(mDataList.size() > 0){
            for(int i = 0 ; i < mDataList.size(); i++){
                Bitmap img = BitmapFactory.decodeFile(mDataList.get(i).sourcePath);
                try {
                    //压缩
                    img = ImageDisplayer.getInstance(creatTopic.this).compressImg(mDataList.get(i).sourcePath, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(img != null){
                    mBitmap.add(img);
                }
            }
        }
        return true;
    }

    private void showDateDialog() {
        DatePickerDialog date_dialog = new DatePickerDialog(creatTopic.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateEdit.setText(+year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        date_dialog.show();
    }

    public void clearData() {
        mTitleEdit.setText(" ");
        mAdressEdit.setText(" ");
        mDateEdit.setText(" ");
        mDataList.clear();
        notifyDataChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyDataChanged();
    }
    private void notifyDataChanged()
    {
        mAdapter.notifyDataSetChanged();

        if(getDataSize()==CustomConstants.MAX_IMAGE_SIZE)
        {
            mGridView.setVisibility(View.GONE);
        }else
        {
            mGridView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_submit:
                if (valite()) {
                    mLoading = new LoadingDialog(creatTopic.this, "正在上传...");
                    mLoading.setCancelable(true);
                    mLoading.show();
                    IMCommunity.createTopic(mTitle, mDate + mAdress, mBitmap,
                            new IMMyself.OnActionResultListener() {

                                @Override
                                public void onSuccess(Object result) {

                                    clearData();
                                    mLoading.dismiss();
                                    UICommon.showTips(creatTopic.this, R.drawable.tips_smile, "创建成功:" + result);
                                    finish();
                                }

                                @Override
                                public void onFailure(String error) {
                                    mLoading.dismiss();
                                    UICommon.showTips(creatTopic.this, R.drawable.tips_smile, "创建失败:" + error);
                                }
                            });
                }
                break;
            default:
                break;
        }
    }
}
