package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.imsdk.imdeveloper.R;

public class CommunityGetTopicListActivity extends Activity {


    private boolean mIsOver = false;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_get_topic_list);

    }
}