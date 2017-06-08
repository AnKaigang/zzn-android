package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.community.CMTopicInfo;
import imsdk.data.community.IMCommunity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.CommunityGetTopicListActivity;
import com.imsdk.imdeveloper.ui.activity.customview.PullToRefreshListView;
import com.imsdk.imdeveloper.util.FriendsListAdapter;

/**
 * 周边
 */
public class AroundFragment extends Fragment {
    // ui
    private FriendsListAdapter mAdapter;
    private List<Map<String, Object>> list;

    private ListView mListView;
    private LinearLayout mLoadingLayout;
    private TextView mEmptyData;
    private List<CMTopicInfo> mFriendsItems = new ArrayList<CMTopicInfo>();
    private boolean mIsOver;



    public static Activity mactivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mactivity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_around, container, false);

        initData();
        findViewById(view);


        setListener();
        initView();

        mListView=(ListView)view.findViewById(R.id.around_listview);
        //listView = (ListView)view.findViewById(R.id.yuewan_listview);

        return view;

    }

    private void initData() {
        IMCommunity.getNewAllTopicList(20, new IMCommunity.OnGetDynamicTopicsListener() {

            @Override
            public void onSuccess(List list, boolean isOver) {
                List<CMTopicInfo> topicInfoList = list;
                mIsOver = isOver;
                if (topicInfoList == null || topicInfoList.size() == 0) {

                } else {
                    addToFriendItem(topicInfoList);
                }
               mAdapter = new FriendsListAdapter(getActivity().getBaseContext(), mFriendsItems);
                mListView.setAdapter(mAdapter);

                mLoadingLayout.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {

                Toast.makeText(mactivity, "获取话题失败:" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListener(){
//        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
//            // 下拉Pull Down
//            @Override
//            public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(getActivity(),
//                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
//                                | DateUtils.FORMAT_SHOW_DATE
//                                | DateUtils.FORMAT_ABBREV_ALL);
//                // 显示最后更新的时间
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
//                        "最后加载时间:" + label);
//                refreshView.getLoadingLayoutProxy().setRefreshingLabel("数据加载中...");
//                refreshView.getLoadingLayoutProxy().setPullLabel("准备加载最新");
//                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
//                IMCommunity.getNewAllTopicList(20, new IMCommunity.OnGetDynamicTopicsListener() {
//
//                    @Override
//                    public void onSuccess(List list, boolean isOver) {
////                        List<CMTopicInfo> topicInfoList = list;
////                        mIsOver = isOver;
////
////                        if (topicInfoList == null || topicInfoList.size() == 0) {
////                            Toast.makeText(getParentFragment().getActivity(), "没有话题信息", Toast.LENGTH_SHORT).show();
////                        } else {
////                            addToFriendItem(topicInfoList);
////                            mAdapter.notifyDataSetChanged();
////                        }
//                        List<CMTopicInfo> topicInfoList = list;
//                        mIsOver = isOver;
//                        if (topicInfoList == null || topicInfoList.size() == 0) {
//
//                        } else {
//                            addToFriendItem(topicInfoList);
//                            mAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(int errorCode, String errorMsg) {
//
//                        Toast.makeText(getParentFragment().getActivity(), "获取话题失败:" + errorMsg, Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

            //			 上拉Pulling Up
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(getActivity(),
//                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
//                                | DateUtils.FORMAT_SHOW_DATE
//                                | DateUtils.FORMAT_ABBREV_ALL);
//
//                // 以前框架是不需要设置的，不知道是不是lib包不同的问题
//                refreshView.getLoadingLayoutProxy().setRefreshingLabel("数据加载中...");
//                refreshView.getLoadingLayoutProxy().setPullLabel("准备加载更多");
//                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
//                        "最后加载时间:" + label);
//            }
//        });
//    }

    private void findViewById(View view) {
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.around_loading);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mEmptyData = (TextView) view.findViewById(R.id.around_empty);
        mEmptyData.setVisibility(View.GONE);

        //mListView.setMode(PorterDuff.Mode.PULL_FROM_START);

    }

    private void initView() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getAroundUsers();

        // 支持上下拉

    }

    public void addToFriendItem(List<CMTopicInfo> list) {
        for (int iTemp = 0; iTemp < list.size(); iTemp++) {
            mFriendsItems.add(list.get(iTemp));
        }
    }


}
