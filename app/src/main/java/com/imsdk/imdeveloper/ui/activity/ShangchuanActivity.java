package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.imsdk.imdeveloper.R;

public class ShangchuanActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shangchuan);
    }
}
