package com.imsdk.imdeveloper.ui.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.util.ImageDisplayer;

import java.util.List;


public class CommentListAdapter extends BaseAdapter{

	private List<CommentItem> mDataList;
	private Context mContext;

	public CommentListAdapter(Context context, List<CommentItem> commentItems)
	{
		this.mContext = context;
		this.mDataList = commentItems;
	}
	
	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder mHolder;
		if (convertView == null)
		{
			convertView = View.inflate(mContext, R.layout.item_review,
					null);
			mHolder = new ViewHolder();
			mHolder.headView = (RoundedCornerImageView) convertView.findViewById(R.id.date_headerImage);
			mHolder.userNameTv = (TextView) convertView.findViewById(R.id.date_headerName);
			mHolder.contentTv = (TextView) convertView.findViewById(R.id.reviewTv);
			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		final CommentItem item = mDataList.get(position);
		
		//头像
		if(item.getCommCreator().getHeadPicture() != null){
			ImageDisplayer.getInstance(mContext).displayBmp(mHolder.headView, item.getCommCreator().getHeadPicture());
		}

		//用户名
		mHolder.userNameTv.setText((item.getCommCreator().getNickname() == null ? 
				item.getCommCreator().getCustomUserID():item.getCommCreator().getNickname()));
		mHolder.contentTv.setText(item.getCommContent());
		
		return convertView;
	}

	class ViewHolder
	{
		public RoundedCornerImageView headView;
		public TextView userNameTv;
		public TextView topictimeTv;
		public TextView contentTv;
		public TextView likeTv;
	}
	
}
