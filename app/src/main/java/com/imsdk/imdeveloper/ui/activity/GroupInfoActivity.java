package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.nickname.IMSDKNickname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.CommunityData;
import com.imsdk.imdeveloper.db.ClubDao;
import com.imsdk.imdeveloper.db.ClubListDao;
import com.imsdk.imdeveloper.http.httpGetIdName;
import com.imsdk.imdeveloper.http.httpModifyClubInfo;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.gridview.GroupInfoAdapter;
import com.imsdk.imdeveloper.util.CommonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

public class GroupInfoActivity extends Activity implements OnClickListener {

    private TextView mTitleTV;
    private List<HashMap<String, Object>> list = null;
    private GroupInfoAdapter mAdapter = null;
    private String groupID;
    private IMGroupInfo groupInfo;
    //private TextView mGroupCreatorTV;
    private TextView mGroupInfoTV;
    private TextView mGroupNameTV;
    private RelativeLayout mGroupNameLayout;
    private RelativeLayout mGroupPushLayout;
    private RelativeLayout mGroupPartyLayout;
    private RelativeLayout mGroupMemberLayout;
    private RelativeLayout mGroupDocumentLayout;
    private RelativeLayout mGroupManageLayout;
    private RelativeLayout mGroupInfoLayout;
    private RelativeLayout mGroupDepartmentLayout;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_groupinfo);

        findViewById();

        setListener();
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void findViewById() {

        mTitleTV = ((TextView) this.findViewById(R.id.imbasetitlebar_title));
        mGroupInfoTV = (TextView) this.findViewById(R.id.groupinfo_info_textview);
        mGroupNameTV = (TextView) this.findViewById(R.id.groupinfo_name_textview);
        mGroupNameLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_name_layout);
        mGroupPushLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_push_layout);
        mGroupDocumentLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_document_layout);
        mGroupPartyLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_party_layout);
        mGroupManageLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_manage_layout);
        mGroupMemberLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_member_layout);
        mGroupInfoLayout = (RelativeLayout) this.findViewById(R.id.groupinfo_info_layout);
        mGroupDepartmentLayout= (RelativeLayout) this.findViewById(R.id.groupinfo_department_layout);
    }

    public void setListener() {
        //返回
        ((ImageButton) findViewById(R.id.imbasetitlebar_back))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        mGroupNameLayout.setOnClickListener(this);
        mGroupInfoLayout.setOnClickListener(this);
        mGroupPushLayout.setOnClickListener(this);
        mGroupDocumentLayout.setOnClickListener(this);
        mGroupPartyLayout.setOnClickListener(this);
        mGroupMemberLayout.setOnClickListener(this);
        mGroupManageLayout.setOnClickListener(this);
        mGroupDepartmentLayout.setOnClickListener(this);
    }

    public void init() {
        //获取groupID
        groupID = getIntent().getStringExtra("groupID");

        //获取群信息
        groupInfo = IMSDKGroup.getGroupInfo(groupID);
        ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
        clubDao.createClubInfo();
        JSONObject jsonObject = clubDao.queryClubInfo(groupInfo.getGroupName());
        if(jsonObject!=null) {
            try {
                new httpGetIdName(groupID).execute(jsonObject.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject js =clubDao.queryClubInfo(groupInfo.getGroupName());
        if(js!=null){
            clubDao.createIdentify();
            try {
                position=clubDao.queryIdentifyClubPosition(js.getString("id"));
                if(position==null||"部员".equals(position)){
                    mGroupNameLayout.setEnabled(false);
                    mGroupInfoLayout.setEnabled(false);
                    mGroupDepartmentLayout.setVisibility(View.GONE);
                    mGroupManageLayout.setVisibility(View.GONE);
                }else {
                    mGroupNameLayout.setEnabled(true);
                    mGroupInfoLayout.setEnabled(true);
                    mGroupDepartmentLayout.setVisibility(View.VISIBLE);
                    mGroupManageLayout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //获取群成员
        final ArrayList<String> groupMemberCustomUserIDsList = groupInfo.getMemberList();

        //title
        //mTitleTV.setText("群消息（"+groupMemberCustomUserIDsList.size()+"）");
        mTitleTV.setText("功能");
        //群创建者
        //mGroupCreatorTV.setText(groupInfo.getOwnerCustomUserID());
        //群信息
        mGroupInfoTV.setText(groupInfo.getCustomGroupInfo());
        //群名称
        mGroupNameTV.setText(groupInfo.getGroupName());

        list = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < (groupMemberCustomUserIDsList.size() + 2); i++) {
            final HashMap<String, Object> map = new HashMap<String, Object>();

            if (i == groupMemberCustomUserIDsList.size()) {
                map.put("text", "jia");
                map.put("img", R.drawable.jia);
            } else if (i == (groupMemberCustomUserIDsList.size() + 1)) {
                map.put("text", "jian");
                map.put("img", R.drawable.jian);
            } else {
                String nickname = IMSDKNickname.get(groupMemberCustomUserIDsList.get(i));
                Bitmap bitmap = IMSDKMainPhoto.get(groupMemberCustomUserIDsList.get(i));
                final String customUserID = groupMemberCustomUserIDsList.get(i);
                if (nickname == null) {
                    map.put("text", customUserID);//为空时默认为cid
                } else {
                    map.put("text", nickname);
                }
                map.put("img", bitmap);
                map.put("cid", customUserID);

                if (bitmap == null || nickname == null) {
                    IMSDKMainPhoto.request(customUserID, 20,
                            new OnBitmapRequestProgressListener() {
                                @Override
                                public void onSuccess(Bitmap bitmap, byte[] buffer) {
                                    if (bitmap != null) {
                                        map.put("img", bitmap);
                                    }
                                    // 头像更新后，昵称也会同步更新
                                    String nickname_new = IMSDKNickname.get(customUserID);
                                    if (!CommonUtil.isNull(nickname_new)) {
                                        map.put("text", nickname_new);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onProgress(double arg0) {
                                }

                                @Override
                                public void onFailure(String arg0) {
                                }
                            });
                }

            }
            list.add(map);
        }
        mAdapter = new GroupInfoAdapter(GroupInfoActivity.this, list, removeMemberListener);

    }

    private View.OnClickListener removeMemberListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            IMMyselfGroup.removeMember((String) v.getTag(), groupID, new IMMyself.OnActionListener() {
                @Override
                public void onSuccess() {
                    // 移除成功回调
                    UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "移除成功");
                    init();
                }

                @Override
                public void onFailure(String error) {
                    // 移除失败回调
                    UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "移除失败：" + error);
                }
            });

        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.groupinfo_name_layout: {

                if (!groupInfo.getOwnerCustomUserID().equals(IMMyself.getCustomUserID())) {
                    UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "您不是管理员，不能修改社团名称");
                    return;
                }
                //修改群名称
                showGroupNameOrInfoDialog("社团名称",mGroupNameTV.getText().toString());

            }
            break;
            case R.id.groupinfo_info_layout: {
                if (!groupInfo.getOwnerCustomUserID().equals(IMMyself.getCustomUserID())) {
                    UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "您不是官~，不能修改群信息");
                    return;
                }
                //修改群信息
                showGroupNameOrInfoDialog("社团信息",mGroupInfoTV.getText().toString());
            }
            break;
            case R.id.groupinfo_push_layout: {

               // PushManager.listTags(GroupInfoActivity.this);
                intent = new Intent(GroupInfoActivity.this, PushListActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                startActivity(intent);
            }
            break;
            case R.id.groupinfo_member_layout: {
                intent = new Intent(GroupInfoActivity.this, MemberOfClubActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                startActivity(intent);
            }
            break;
            case R.id.groupinfo_party_layout: {
                intent = new Intent(GroupInfoActivity.this, TaskListActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                intent.putExtra("position", position);
                startActivity(intent);
            }
            break;
            case R.id.groupinfo_document_layout: {
                intent = new Intent(GroupInfoActivity.this, DocumentActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                intent.putExtra("position", position);
                startActivity(intent);
            }
            break;
            case R.id.groupinfo_department_layout: {
                intent = new Intent(GroupInfoActivity.this, DepartmentListActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                intent.putExtra("position", position);
                startActivity(intent);
            }
            break;
            case R.id.groupinfo_manage_layout: {
                intent = new Intent(GroupInfoActivity.this, MemberOfClubActivity.class);
                intent.putExtra("groupId", groupID);
                intent.putExtra("groupName", groupInfo.getGroupName());
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }


    private void showGroupNameOrInfoDialog(final String updateName,final String oldName) {
        AlertDialog.Builder builder = new Builder(GroupInfoActivity.this);
        View view = LayoutInflater.from(GroupInfoActivity.this).inflate(
                R.layout.dialog_nickname, null);


        final TextView titleTV = (TextView) view.findViewById(R.id.dialog_title);
        final EditText editET = (EditText) view.findViewById(R.id.dialog_edittext);
        Button cancleBtn = (Button) view.findViewById(R.id.dialog_cancle);
        Button sureBtn = (Button) view.findViewById(R.id.dialog_sure);

        titleTV.setText(updateName);
        editET.setHint("请输入" + updateName);
        editET.setText(oldName);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.show();

        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        sureBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubDao clubDao = new ClubDao(IMMyself.getCustomUserID());
                clubDao.createClubInfo();
                JSONObject jsonObject = clubDao.queryClubInfo(groupInfo.getGroupName());
                if(jsonObject!=null) {
                    CommunityData community = new CommunityData();
                    try {
                        community.setId(jsonObject.getString("id"));
                        community.setCommunityname(jsonObject.getString("name"));
                        community.setCommunityintro(jsonObject.getString("intro"));
                        community.setType(jsonObject.getString("type"));

                        String name = editET.getText().toString();
                        if (updateName.equals("社团名称")) {
                            community.setCommunityname(name);
                            String j=new Gson().toJson(community);
                            new httpModifyClubInfo(GroupInfoActivity.this).execute(j);
                            updateGroupName(name, null, updateName);
                        } else {
                            community.setCommunityintro(name);
                            String j=new Gson().toJson(community);
                            new httpModifyClubInfo(GroupInfoActivity.this).execute(j);
                            updateGroupName(null, name, updateName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 更新群名称或者群信息
     *
     * @param groupName
     * @param info
     * @param showName
     */
    public void updateGroupName(final String groupName, final String info, final String showName) {
        if (groupName != null) {
            groupInfo.setGroupName(groupName);
        }
        if (info != null) {
            groupInfo.setCustomGroupInfo(info);
        }
        groupInfo.commitGroupInfo(new IMMyself.OnActionListener() {

            @Override
            public void onSuccess() {
                if (groupName != null) {
                    mGroupNameTV.setText(groupName);
                }
                if (info != null) {
                    mGroupInfoTV.setText(info);
                }
                UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "修改" + showName + "成功");
            }

            @Override
            public void onFailure(String error) {
                UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "修改" + showName + "失败：" + error);
            }
        });
    }

    /**
     * 群添加成员页面返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;
        switch (requestCode) {
            case 10: {
                String cid = data.getStringExtra("CustomUserID");
                IMMyselfGroup.addMember(cid, groupID, new IMMyself.OnActionListener() {

                    @Override
                    public void onSuccess() {
                        // 添加成功回调
                        UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "添加群成员成功");
                        init();
                    }

                    @Override
                    public void onFailure(String error) {
                        // 添加失败回调
                        UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "添加群成员失败：" + error);
                    }
                });
            }
            break;

            default:
                break;
        }

    }


}
