package com.imsdk.imdeveloper.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.adapter.ImageBucketAdapter;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.model.ImageBucket;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.CustomConstants;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.ImageFetcher;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.utils.IntentConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 选择相册
 * 
 */

public class ImageBucketChooseActivity extends Activity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_image_bucket_choose);

		mHelper = ImageFetcher.getInstance(ImageBucketChooseActivity.this);
		initData();
		initView();
	}

	private void initData()
	{
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView()
	{
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		TextView titleTv  = (TextView) findViewById(R.id.mid);
		titleTv.setText("相册");
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);

				startActivityForResult(intent, 200);
			}
		});

		//取消
		TextView cancelTv = (TextView) findViewById(R.id.right);
		cancelTv.setText("取消");
		cancelTv.setVisibility(View.VISIBLE);
		cancelTv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		//返回
		((ImageButton)this.findViewById(R.id.left)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void selectOne(int position)
	{
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			if (i == position) mDataList.get(i).selected = true;
			else
			{
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 200){
			setResult(creatTopic.SELECT_PICTURE, data);
			finish();
		}
		
	}

}
