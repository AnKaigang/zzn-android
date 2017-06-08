package com.imsdk.imdeveloper.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.ReviewActivity;
import com.imsdk.imdeveloper.ui.fragment.AroundFragment;
import com.imsdk.imdeveloper.ui.view.DateImageView;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.ui.view.TitlePopup;

import java.util.List;

import imsdk.data.IMMyself;
import imsdk.data.community.CMTopicInfo;
import imsdk.data.community.IMCommunity;

public class FriendsListAdapter extends BaseAdapter{

	private TitlePopup.OnItemAdapterOnClickListener mOnCommentOptListener;
	private List<CMTopicInfo> mDataList;
	private Context mContext;
	//private OnItemAdapterOnClickListener mOnCommentOptListener;

	public FriendsListAdapter(Context context, List<CMTopicInfo> friendsItems)
	{
		this.mContext = context;
		this.mDataList = friendsItems;
		//this.mOnCommentOptListener = onCommentOptListener;
		
	}

	public FriendsListAdapter(Context context, List<CMTopicInfo> friendsItems, TitlePopup.OnItemAdapterOnClickListener onCommentOptListener)
	{
		this.mContext = context;
		this.mDataList = friendsItems;
		this.mOnCommentOptListener = onCommentOptListener;

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder mHolder;
		if (convertView == null)
		{
			convertView = View.inflate(mContext, R.layout.yuewan_item,
					null);
			mHolder = new ViewHolder();
			mHolder.titleTv = (TextView)convertView.findViewById(R.id.title);
			mHolder.addressTv = (TextView)convertView.findViewById(R.id.address1);
			mHolder.zanTv = (TextView)convertView.findViewById(R.id.zan);
			mHolder.reviewTv = (TextView)convertView.findViewById(R.id.review);
			mHolder.headView = (RoundedCornerImageView) convertView.findViewById(R.id.date_headerImage);
			mHolder.userNameTv = (TextView) convertView.findViewById(R.id.date_headerName);
			mHolder.likesTv = (TextView) convertView.findViewById(R.id.date_praise);
			mHolder.commentsTv = (TextView) convertView.findViewById(R.id.date_commente);
			mHolder.picIv = (ImageView)convertView.findViewById(R.id.pic);
			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		final CMTopicInfo item = mDataList.get(position);
		
		//点赞、评论按钮
//		final TitlePopup mTitlePopup = new TitlePopup(mContext, CommonUtils.dip2px(mContext, 165), CommonUtils.dip2px(
//				mContext, 40));
//		mTitlePopup
//				.addAction(new ActionItem(mContext, "赞", R.drawable.circle_praise));
//		mTitlePopup.addAction(new ActionItem(mContext, "评论",
//				R.drawable.circle_comment));
//		mTitlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
//
//			@Override
//			public void onItemClick(ActionItem actionItem, int pos) {
//
//				mOnCommentOptListener.onItemClick(actionItem, pos, position);
//			}
//		});


		//话题图片

		if (item.getTopicImages() != null && item.getTopicImages().length > 0)
		{
			ImageDisplayer.getInstance(mContext).displayBmp(mHolder.picIv, item.getTopicImages()[0]);
			
		}

		//头像
		if(item.getTopicCreator().getHeadPicture() != null){
			ImageDisplayer.getInstance(mContext).displayBmp(mHolder.headView, item.getTopicCreator().getHeadPicture());
		}

		//用户名
		mHolder.userNameTv.setText((item.getTopicCreator().getNickname() == null ||
				item.getTopicCreator().getNickname().length() == 0 ?
				item.getTopicCreator().getCustomUserID() : item.getTopicCreator().getNickname()));
		//mHolder.topictimeTv.setText(CommonUtils.getTimeBylong(item.getTopicTime()*1000)+"");

		mHolder.likesTv.setText(item.getPraiseNums()+"");
		mHolder.commentsTv.setText(item.getCommentNums()+"");

		mHolder.titleTv.setText(item.getTopicTitle());
		mHolder.addressTv.setText(item.getTopicContent());

		//点击事件
//		mHolder.likesTv.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, MainActivity.class);
//				intent.putExtra("ntype", 1);
//				intent.putExtra("communityId", item.getTopicId());
//				mContext.startActivity(intent);
//			}
//		});
//		mHolder.commentsTv.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Intent intent = new Intent(mContext,MainActivity.class);
//				intent.putExtra("topicId", item.getTopicId());
//				mContext.startActivity(intent);
//			}
//		});
		mHolder.zanTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IMCommunity.likeTopic(mDataList.get(position).getTopicId(), new IMMyself.OnActionListener() {

					@Override
					public void onSuccess() {
						Integer i;

						i = Integer.parseInt(((mHolder.likesTv.getText()).toString())) + 1;

						mHolder.likesTv.setText(String.valueOf(i));

						String message  = "[约玩]\n" +
								item.getTopicTitle() + "\n" +
								item.getTopicContent() + "\n" +
								"约约约~~~";
						String userID = item.getTopicCreator().getCustomUserID();
						IMMyself.sendText(message , userID, (message.length() / 400 + 1) * 5, new IMMyself.OnActionListener() {
							@Override
							public void onSuccess() {
								Toast.makeText(AroundFragment.mactivity, "发送成功", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onFailure(String error) {
								Toast.makeText(AroundFragment.mactivity, "发送失败 -- " + error, Toast.LENGTH_SHORT).show();
							}
						});
						//刷新
					}

					@Override
					public void onFailure(String error) {
						Toast.makeText(AroundFragment.mactivity, "服务器超时:" + error, Toast.LENGTH_SHORT).show();
					}
				});

			}
		});

		mHolder.reviewTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent  = new Intent(AroundFragment.mactivity, ReviewActivity.class);
				intent.putExtra("topicId", mDataList.get(position).getTopicId());
				AroundFragment.mactivity.startActivity(intent);
			}
		});
		return convertView;
	}
	

	public class ViewHolder
	{
		public RoundedCornerImageView headView;
		public TextView userNameTv;
		public TextView topictimeTv;
		public TextView titleTv;
		public TextView addressTv;
		public TextView zanTv;
		public TextView reviewTv;
		public TextView contentTv;
		public DateImageView contentView;
		public TextView likesTv;
		public TextView commentsTv;
		public ImageView picIv;
	}
	
}
