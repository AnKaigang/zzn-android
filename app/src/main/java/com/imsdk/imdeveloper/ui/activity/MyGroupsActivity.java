package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnInitializedListener;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMMyselfGroup.OnGroupEventsListener;
import imsdk.data.group.IMSDKGroup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.http.httpGetClubId;
import com.imsdk.imdeveloper.http.httpGetClubPosition;

public class MyGroupsActivity extends Activity implements View.OnClickListener {
	// ui
	private LayoutInflater mInflater;
	private TextView mTitleBarRightView;
	private GroupsListAdapter mAdapter;
	private TextView mEmptyDataShow;
	private ListView mListView;
	private ProgressBar mProgressBar;
	
	private List<String> mGroupsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchandmakegroup);
		
		findViewById();
		setListener();
		init();
	}
	
	public void findViewById(){
		mInflater = LayoutInflater.from(MyGroupsActivity.this);
		
		mTitleBarRightView = (TextView) findViewById(R.id.right);
		
		mEmptyDataShow = (TextView) findViewById(R.id.text_noinfo);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		mListView = (ListView) findViewById(R.id.grouplist);
	}
	
	public void setListener(){
		mTitleBarRightView.setOnClickListener(this);
		((TextView) findViewById(R.id.left)).setOnClickListener(this);
		((ImageView) findViewById(R.id.titlebar_logo)).setOnClickListener(this);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(MyGroupsActivity.this,
						IMGroupChatActivity.class);

				intent.putExtra("groupID", (String) mGroupsList
						.get(position));
				startActivity(intent);
			}
		});

		IMMyselfGroup.setOnGroupEventsListener(new OnGroupEventsListener() {
			@Override
			public void onRemovedFromGroup(String groupID, String customUserID,
					long actionServerTime) {
			}

			@Override
			public void onInitialized() {
			}

			@Override
			public void onGroupNameUpdated(String newGroupName, String groupID,
					long actionServerTime) {
			}

			@Override
			public void onGroupMemberUpdated(ArrayList membersList, String groupID,
					long actionServerTime) {
			}

			@Override
			public void onGroupDeletedByUser(String groupID, String customUserID,
					long actionServerTime) {
			}

			@Override
			public void onCustomGroupInfoUpdated(String newGroupInfo, String groupID,
					long actionSeverTime) {
			}

			@Override
			public void onAddedToGroup(String groupID, long actionServerTime) {
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		
		mTitleBarRightView.setText("添加");


		new httpGetClubId().execute(IMMyself.getCustomUserID());
		new httpGetClubPosition(MyGroupsActivity.this).execute(IMMyself.getCustomUserID());

		mGroupsList = IMMyselfGroup.getMyGroupsList();
		
		if(mGroupsList == null){
			mGroupsList = new ArrayList<String>();//为空时，要赋值
			Toast.makeText(MyGroupsActivity.this, "无社团信息", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mAdapter = new GroupsListAdapter();
		mListView.setEmptyView(mEmptyDataShow);
		mListView.setAdapter(mAdapter);
		
		//判断群组模块是否初始化成功
		if(!IMMyselfGroup.isInitialized()){
			
			Toast.makeText(MyGroupsActivity.this, "重新社团模块初始化", Toast.LENGTH_SHORT).show();
			
			IMMyselfGroup.setOnInitializedListener(new OnInitializedListener() {
				
				@Override
				public void onInitialized() {
					Toast.makeText(MyGroupsActivity.this, "社团模块初始化成功", Toast.LENGTH_SHORT).show();
				}
			});
		}
		Log.d("imsdk", "====mGroupsList:"+mGroupsList);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			this.finish();
			break;
		case R.id.titlebar_logo:
			this.finish();
			break;
		case R.id.right:

			mTitleBarRightView.setEnabled(false);
			AlertDialog.Builder builder = new AlertDialog.Builder(MyGroupsActivity.this);
			View view = LayoutInflater.from(MyGroupsActivity.this).inflate(
					R.layout.dialog_club, null);
			LinearLayout createLayout = (LinearLayout) view.findViewById(R.id.layout_create);
			builder.setView(view);
			final AlertDialog dialog = builder.create();

			dialog.show();
			createLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					Intent intent = new Intent(MyGroupsActivity.this, CreateClubActivity.class);
					startActivity(intent);
				}
			});

			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (IMMyselfGroup.isInitialized()) {
			mProgressBar.setVisibility(View.GONE);
		} else {
			IMMyselfGroup.setOnInitializedListener(new OnInitializedListener() {
				@Override
				public void onInitialized() {
					mProgressBar.setVisibility(View.GONE);
				}
			});
		}
	}
	private class GroupsListAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mGroupsList.size();
		}

		@Override
		public Object getItem(int position) {
			return mGroupsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String groupID = (String) mGroupsList.get(position);
			IMGroupInfo groupInfo = IMSDKGroup.getGroupInfo(groupID);
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group, parent, false);
				holder.mGroupName = (TextView) convertView.findViewById(R.id.groupName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mGroupName.setText(groupInfo.getGroupName());
			return convertView;
		}

		private final class ViewHolder {
			TextView mGroupName;
		}
	}
}
