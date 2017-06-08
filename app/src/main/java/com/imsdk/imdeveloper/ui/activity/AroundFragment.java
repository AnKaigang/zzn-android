package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;
import com.imsdk.imdeveloper.ui.view.sortlistview.PinyinComparator;
import com.imsdk.imdeveloper.ui.view.sortlistview.SideBar;
import com.imsdk.imdeveloper.ui.view.sortlistview.SortAdapter;

import java.util.Collections;
import java.util.List;

import imsdk.data.IMMyself;
import imsdk.data.IMSDK;
import imsdk.data.relations.IMMyselfRelations;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AroundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AroundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AroundFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> mSortCustomUserIDsList;

    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator mPinyinComparator;

    // ui
    private SortAdapter mAdapter;
    private ListView mListView;
    public ClearEditText mClearEditText;
    private SideBar mSideBar;
    private TextView mDialog;
    private TextView mEmptyTextView;
    private LinearLayout mLoadingLayout;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AroundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AroundFragment newInstance(String param1, String param2) {
        AroundFragment fragment = new AroundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AroundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_around, container, false);

        initViews(view);


        if (IMMyselfRelations.isInitialized()) {
            initDatas();

            IMMyselfRelations
                    .setOnFriendsListDataChangedListener(new IMSDK.OnDataChangedListener() {
                        @Override
                        public void onDataChanged() {
                            initDatas();
                        }
                    });
        } else {
            // 设置初始化事件监听
            IMMyselfRelations.setOnInitializedListener(new IMMyself.OnInitializedListener() {
                @Override
                public void onInitialized() {
                    initDatas();

                    IMMyselfRelations
                            .setOnFriendsListDataChangedListener(new IMSDK.OnDataChangedListener() {
                                @Override
                                public void onDataChanged() {
                                    initDatas();
                                }
                            });
                }
            });
        }
        return view;
    }
    public void initDatas() {
        mSortCustomUserIDsList = IMMyselfRelations.getFriendsList();

        // 根据a-z进行排序源数据
        Collections.sort(mSortCustomUserIDsList, mPinyinComparator);

        mAdapter = new SortAdapter(getActivity(), mSortCustomUserIDsList);
        mListView.setAdapter(mAdapter);


        if (mSortCustomUserIDsList.size() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mSideBar.setVisibility(View.GONE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
            mSideBar.setVisibility(View.VISIBLE);
        }

        // 根据输入框输入值的改变来过滤搜索

        mSideBar.setTextView(mDialog);


        mLoadingLayout.setVisibility(View.GONE);
    }

    private void initViews(View view) {
        mPinyinComparator = new PinyinComparator();

        mListView = (ListView) view.findViewById(R.id.contacts_listview);
        mClearEditText = (ClearEditText) view.findViewById(R.id.contacts_edittext);
        mSideBar = (SideBar) view.findViewById(R.id.contacts_sidebar);
        mDialog = (TextView) view.findViewById(R.id.contacts_dialog);
        mEmptyTextView = (TextView) view.findViewById(R.id.contacts_emtpy_textview);
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.contacts_loading_layout);

        //群聊+商家
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_contacts_list_head, null);

        mListView.addHeaderView(v);
        LinearLayout groupLayout = (LinearLayout)v.findViewById(R.id.listhead_group);
        groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MyGroupsActivity.class);
                startActivity(intent);
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
