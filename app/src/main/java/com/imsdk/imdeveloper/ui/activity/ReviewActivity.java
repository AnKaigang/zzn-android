package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsdk.data.community.CMCommInfo;
import imsdk.data.community.CMTopicCommInfo;
import imsdk.data.community.IMCommunity;

public class ReviewActivity extends Activity implements View.OnClickListener{

    private ImageButton mLeft_titleBar;
    private TextView mTitleBarLogo;
    private Button mRight_titleBar;
    private CommentListAdapter mAdapter;


    private List<CommentItem> commentItems = new ArrayList<CommentItem>();
    private long topicId;

    ListView listView;
    List<Map<String, Object>> list;
    SimpleAdapter sim_adp;
    List<Long> topicIdList = new ArrayList<Long>();
    List<CMTopicCommInfo> topicCommInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        findViewById();
        setListener();
        initData();
        initView();
    }

    private void initData() {
        topicId = getIntent().getLongExtra("topicId", 0);

        topicIdList.add(topicId);

        IMCommunity.getBatchCommentList(1 ,20, topicIdList, new IMCommunity.OnGetBatchCommInfoListListener() {

            @Override
            public void onSuccess(List list) {
                // 很多topicId的评论，因为只传入了一个topicId，所以list只有一项
                //topicCommInfoList得到的是对应某一个topicId的评论，包含每条评论，开始时间，jie'shu'shi'j
                topicCommInfoList = (List<CMTopicCommInfo>)list;

                if(topicCommInfoList == null || topicCommInfoList.size() == 0){
                    Toast.makeText(ReviewActivity.this, "无评论", Toast.LENGTH_SHORT).show();
                    Log.e("1", "没有传进去东西");
                }else{
                    CMTopicCommInfo myCMCommInfo = topicCommInfoList.get(0);
                    Log.i("1","1");
                    List<CMCommInfo> myCMCommInfo1 =  myCMCommInfo.getComminfoList();
                    Log.i("1","1");
                    for(int i = 0 ; i < myCMCommInfo1.size(); i++){


                        CMCommInfo commInfo = myCMCommInfo1.get(i);

                        CommentItem uitem = new CommentItem();
                        uitem.setCommInfo(commInfo);
                        commentItems.add(uitem);


                    }

                    Log.i("1", "1");
                    //topicCommInfoList.clear();

                    listView = (ListView)findViewById(R.id.reviewlist);
                    mAdapter = new CommentListAdapter(ReviewActivity.this, commentItems);
                    listView.setAdapter(mAdapter);

                }

            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {

                Toast.makeText(ReviewActivity.this, "获取评论失败:"+errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setListener(){
        mTitleBarLogo.setText("");
        mRight_titleBar.setVisibility(View.VISIBLE);
        mRight_titleBar.setText("发表评论");
        mRight_titleBar.setOnClickListener(this);
        mLeft_titleBar.setOnClickListener(this);
    }

    private void findViewById() {
        mLeft_titleBar = (ImageButton) findViewById(R.id.imbasetitlebar_back);

        mTitleBarLogo = (TextView) findViewById(R.id.imbasetitlebar_title);
        mRight_titleBar = (Button)findViewById(R.id.imbasetitlebar_right);

    }

    private void initView() {



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imbasetitlebar_back:
                finish();
                break;
            case R.id.imbasetitlebar_right:

                Intent intent = new Intent(ReviewActivity.this, AddReviewActivity.class);
                intent.putExtra("topicId", topicId);
                startActivity(intent);

//                IMCommunity.createComment(
//                        Long.parseLong(mTopicIdET.getText().toString()),
//                        mContentET.getText().toString(),
//                        new IMMyself.OnActionResultListener() {
//
//                            @Override
//                            public void onSuccess(Object result) {
//
//                                Toast.makeText(ReviewActivity.this, "创建成功:" + result, Toast.LENGTH_SHORT).show();
//                                if (topicId != 0) {
//                                    //有话题id时创建返回
//                                    Intent in = new Intent();
//                                    in.putExtra("topicId", topicId);
//                                    setResult(200, in);
//                                    finish();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(String error) {
//
//                                Toast.makeText(ReviewActivity.this, "创建失败:" + error, Toast.LENGTH_SHORT).show();
//                            }
//                        });
                break;
        }
    }

    private List<Map<String, Object>> getData() {
        list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("review", "真棒");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("review", "哈哈哈");
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("review", "+1");
        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("review", "+1");
        list.add(map);
        list.add(map2);
        list.add(map3);
        list.add(map4);



        return list;
    }
}
