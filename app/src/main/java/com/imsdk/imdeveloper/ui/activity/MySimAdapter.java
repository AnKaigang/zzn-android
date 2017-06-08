package com.imsdk.imdeveloper.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.Rlation;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpAddClub;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imsdk.data.IMMyself;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.relations.IMMyselfRelations;

/**
 * Created by Administrator on 2016/9/19.
 */
public class MySimAdapter extends SimpleAdapter {
    List<Map<String, Object>> dataList;
    Context appContext;
    String mGroupName;
    String mGroupId;
    public MySimAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to,String mGroupName,String mGroupId) {
        super(context, data, resource, from, to);
        this.dataList=data;
        this.appContext=context;
        this.mGroupName=mGroupName;
        this.mGroupId=mGroupId;
    }
    class ViewHolder {
        Button operater;
        Button delete;
    }
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = LayoutInflater.from(appContext).inflate(R.layout.member_club_items,null);
        Button operater = (Button) view.findViewById(R.id.operater);
        Button delete = (Button) view.findViewById(R.id.delete);
        TextView name = (TextView) view.findViewById(R.id.name_club);
        TextView label= (TextView) view.findViewById(R.id.name_label);
        RoundedCornerImageView imageView = (RoundedCornerImageView) view.findViewById(R.id.pHeader);
        if(dataList.size()>0) {
            name.setText(dataList.get(position).get("name").toString());
            label.setText(dataList.get(position).get("label").toString());
            getHeadPhoto(dataList.get(position).get("name").toString(), imageView);
        }
        ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
        String userId= clubListDao.queryIdNameByUserName(IMMyself.getCustomUserID());
        String pos= clubListDao.queryRelationByUserName(userId);
        if(!"主席".equals(pos)){
            if(dataList.get(position).get("label").toString().equals("主席")){
                operater.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        }
         operater.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 showGroupNameOrInfoDialog(mGroupName,"",dataList.get(position).get("name").toString());
             }
         });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(appContext).setMessage("确定删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Rlation relation = new Rlation();
                                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                                clubListDao.createIdName();
                                String userid = clubListDao.queryIdNameByUserName(dataList.get(position).get("name").toString());
                                if (userid == null) {
                                    UICommon.showTips(appContext, R.drawable.tips_error, "失败");
                                } else {
                                    ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                                    clubDao.createClubInfo();
                                    JSONObject jsonObject = clubDao.queryClubInfo(mGroupName);
                                    if (jsonObject == null) {
                                        UICommon.showTips(appContext, R.drawable.tips_error, "失败");
                                    } else {
                                        try {
                                            relation.setCommunityid(jsonObject.getString("id"));
                                            relation.setUserid(userid);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                String s= new Gson().toJson(relation);
                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams requestParams = new RequestParams();
                                requestParams.add("rlation", s);
                                client.post(appContext, IMConfiguration.deleteRelation, requestParams, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int j, Header[] headers, byte[] bytes) {
                                        if ("ok".equals(new String(bytes))) {

                                            IMMyselfGroup.removeMember(dataList.get(position).get("name").toString(),mGroupId , new IMMyself.OnActionListener() {
                                                @Override
                                                public void onSuccess() {
                                                    UICommon.showTips(appContext, R.drawable.tips_smile, "成功");
                                                }

                                                @Override
                                                public void onFailure(String error) {
                                                    UICommon.showTips(appContext, R.drawable.tips_smile, "失败");
                                                }
                                            });
                                        } else {
                                            UICommon.showTips(appContext, R.drawable.tips_smile, "服务器异常");
                                        }
                                        getData();
                                    }

                                    @Override
                                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                        //mDialog.dismiss();
                                        UICommon.showTips(appContext, R.drawable.tips_error, "网络异常");
                                    }
                                });
                            }
                        }).setNegativeButton("取消", null).create().show();
            }
        });
        return view;
    }

    public void showGroupNameOrInfoDialog(final String community, final String dept,final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
        View view = LayoutInflater.from(appContext).inflate(
                R.layout.dialog_memberoperater, null);

        final Spinner department = (Spinner) view.findViewById(R.id.dialog_department);
        Button cancleBtn = (Button) view.findViewById(R.id.dialog_cancle);
        Button sureBtn = (Button) view.findViewById(R.id.dialog_sure);
        final RadioGroup mRadioGroup= (RadioGroup) view.findViewById(R.id.chooseIdentify);
        SimpleAdapter sim_adp = new SimpleAdapter(appContext, getData(), R.layout.item_spinner,
                new String[]{"department"}, new int[]{R.id.department});
        builder.setView(view);
        department.setAdapter(sim_adp);
        final AlertDialog dialog = builder.create();

        dialog.show();

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rlation relation = new Rlation();
                ClubListDao clubListDao=new ClubListDao(IMMyself.getCustomUserID()+"/"+mGroupId);
                clubListDao.createIdName();
                String userid = clubListDao.queryIdNameByUserName(name);
                if (userid == null) {
                    UICommon.showTips(appContext, R.drawable.tips_error, "失败");
                } else {
                    ClubDao clubDao=new ClubDao(IMMyself.getCustomUserID());
                    clubDao.createClubInfo();
                    JSONObject jsonObject = clubDao.queryClubInfo(community);
                    if (jsonObject == null) {
                        UICommon.showTips(appContext, R.drawable.tips_error, "失败");
                    } else {
                        String position;
                        if (mRadioGroup.getCheckedRadioButtonId() == R.id.chairman_btn) {
                               position = "主席";
                        } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.minister_btn) {
                              position = "部长";
                        } else {
                              position = "部员";
                        }
                        try {
                            relation.setCommunityid(jsonObject.getString("id"));
                            relation.setPosition(position);
                            relation.setUserid(userid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String s= new Gson().toJson(relation);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams requestParams = new RequestParams();
                requestParams.add("rlation", s);
                client.post(appContext, IMConfiguration.updateRelation, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int j, Header[] headers, byte[] bytes) {
                        if ("ok".equals(new String(bytes))) {
                            UICommon.showTips(appContext, R.drawable.tips_smile, "成功");
                        } else {
                            UICommon.showTips(appContext, R.drawable.tips_smile, "服务器异常");
                        }
                        getData();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        //mDialog.dismiss();
                        UICommon.showTips(appContext, R.drawable.tips_error, "网络异常");
                    }
                });
                dialog.dismiss();
            }
        });
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1=new HashMap<String, Object>();
        map1.put("department","体育部");
        Map<String, Object> map2=new HashMap<String, Object>();
        map2.put("department","文艺部");
        list.add(map1);
        list.add(map2);
        return list;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public ViewBinder getViewBinder() {
        return super.getViewBinder();
    }

    @Override
    public void setViewBinder(ViewBinder viewBinder) {
        super.setViewBinder(viewBinder);
    }

    @Override
    public void setViewImage(ImageView v, int value) {
        super.setViewImage(v, value);
    }

    @Override
    public void setViewImage(ImageView v, String value) {
        super.setViewImage(v, value);
    }

    @Override
    public void setViewText(TextView v, String text) {
        super.setViewText(v, text);
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
    private void getHeadPhoto(String userID,final RoundedCornerImageView image) {
        IMSDKMainPhoto.request(userID, 30,
                new IMSDKMainPhoto.OnBitmapRequestProgressListener() {
                    @Override
                    public void onSuccess(Bitmap mainPhoto, byte[] buffer) {
                        if (mainPhoto != null) {
                            image.setImageBitmap(mainPhoto);
                        }
                    }

                    @Override
                    public void onProgress(double progress) {

                    }

                    @Override
                    public void onFailure(String error) {
                    }
                });
    }
}
