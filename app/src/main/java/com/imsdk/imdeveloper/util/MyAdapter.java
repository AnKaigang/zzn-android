package com.imsdk.imdeveloper.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.PushCountActivity;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MyAdapter extends BaseAdapter{
    Bitmap[] bitmaps;
    String[] names;
    PushCountActivity pushCountActivity;
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setData(Bitmap[] bitmaps,String[] names,PushCountActivity pushCountActivity){
        this.bitmaps=bitmaps;
        this.names=names;
        this.pushCountActivity=pushCountActivity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout=View.inflate(pushCountActivity, R.layout.item_headerandname, null);
        ImageView face = (ImageView)layout.findViewById(R.id.pushHeader);
        TextView name =(TextView)layout.findViewById(R.id.pushName);
        face.setImageBitmap(bitmaps[position]);
        name.setText(names[position]);
        return layout;
    }
}
