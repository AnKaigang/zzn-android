package com.imsdk.imdeveloper.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.customview.multiphotopicker.model.ImageItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/20.
 */
public class ImagePublishAdapter extends BaseAdapter
{
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private Context mContext;

    public ImagePublishAdapter(Context context, List<ImageItem> dataList)
    {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public int getCount()
    {
        // 多返回一个用于展示添加图标
        if (mDataList == null)
        {
            return 1;
        }
        else if (mDataList.size() == 9)
        {
            return 9;
        }
        else
        {
            return mDataList.size() + 1;
        }
    }

    public Object getItem(int position)
    {
        if (mDataList != null
                && mDataList.size() == 9)
        {
            return mDataList.get(position);
        }

        else if (mDataList == null || position - 1 < 0
                || position > mDataList.size())
        {
            return null;
        }
        else
        {
            return mDataList.get(position - 1);
        }
    }

    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
        convertView = View.inflate(mContext, R.layout.item_publish, null);
        ImageView imageIv = (ImageView) convertView
                .findViewById(R.id.item_grid_image);

        if (isShowAddItem(position))
        {
            imageIv.setImageResource(R.drawable.btn_add_pic);
            imageIv.setBackgroundResource(R.color.white);
        }
        else
        {
            final ImageItem item = mDataList.get(position);
            ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
                    item.thumbnailPath, item.sourcePath);
        }

        return convertView;
    }

    private boolean isShowAddItem(int position)
    {
        int size = mDataList == null ? 0 : mDataList.size();
        return position == size;
    }

}
