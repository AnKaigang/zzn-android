package com.imsdk.imdeveloper.ui.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> dataList;

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    class ViewHolder {

        TextView tvName;
        CheckBox cb;
    }

    public MyAdapter(Context context, List<Map<String, String>> list) {
        // TODO Auto-generated constructor stub
        this.dataList = list;
        this.context = context;
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < dataList.size(); i++) {
            getIsSelected().put(i, false);
        }
    }
    public void setDataList(){
        initDate();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position).get("name");
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        ViewHolder holder;
        Map<String,String> map = dataList.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_department, null);
            holder = new ViewHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.deleteID);
            holder.tvName = (TextView) convertView
                    .findViewById(R.id.departmentName);
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(map.get("name"));
        // 监听checkBox并根据原来的状态来设置新的状态
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelected.put(position, isChecked);
                System.out.print(isChecked);
            }
        });
        // 根据isSelected来设置checkbox的选中状况
        if(isSelected.size()!=0) {
            boolean b = isSelected.get(position);
            holder.cb.setChecked(b);
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyAdapter.isSelected = isSelected;
    }
}
