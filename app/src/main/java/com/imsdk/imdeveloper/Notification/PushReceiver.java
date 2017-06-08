package com.imsdk.imdeveloper.Notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.PushDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 */
public class PushReceiver extends PushMessageReceiver{
    public PushReceiver() {
        super();
    }

    @Override
    public void onBind(Context context, int i, String s, String s1, String s2, String s3) {

    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onCheckBindState(Context context, int i, String s, boolean b) {
        super.onCheckBindState(context, i, s, b);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        String str="";
        for(String s1:list){
            str+=s1;
        }
        UICommon.showTips(context, R.drawable.tips_smile,str);
    }

    @Override
    public void onMessage(Context context, String s, String s1) {

    }

    @Override
    public void onNotificationClicked(Context context, String title, String content, String customContentString) {
        Intent dialogIntent = new Intent(context, PushDetailActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            JSONObject jsonObject=new JSONObject(customContentString);
            dialogIntent.putExtra("id", jsonObject.getString("id"));
            dialogIntent.putExtra("title", title);
            dialogIntent.putExtra("content", content);

        } catch (JSONException e) {
        }
        context.getApplicationContext().startActivity(dialogIntent); //startActivity(dialogIntent);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
