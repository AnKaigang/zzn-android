package com.imsdk.imdeveloper.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TimeLineView extends FrameLayout {

	private Context mContext;
	private int leftWeight;
	private int middleWeight;
	private int rightWeight;
	private LinearLayout mainView;
	private ArrayList<Integer> leftRules = new ArrayList<Integer>();
	private ArrayList<Integer> middleRules = new ArrayList<Integer>();
	private ArrayList<Integer> rightRules = new ArrayList<Integer>();

	public TimeLineView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public TimeLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public TimeLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mainView = new LinearLayout(mContext);
		mainView.setOrientation(LinearLayout.VERTICAL);
	}

	public void setTimeInfos(ArrayList<TimeViewInfo> infos) {
		int size = infos.size();
		for (int i = 0; i < size; i++) {
			TimeViewInfo info = infos.get(i);
			addView(info);
		}
		this.addView(mainView);
	}

	private void addView(TimeViewInfo timeInfo) {
		LinearLayout item = new LinearLayout(mContext);
		addLeftView(timeInfo, item);
		addMiddleView(timeInfo, item);
		addRightView(timeInfo, item);
		mainView.addView(item);
	}

	public void setWeight(int left, int middle, int right) {
		leftWeight = left;
		middleWeight = middle;
		rightWeight = right;
	}

	public void addLeftRule(int rule) {
		leftRules.add(Integer.valueOf(rule));
	}

	public void addMiddleRule(int rule) {
		middleRules.add(Integer.valueOf(rule));
	}

	public void addRightRule(int rule) {
		rightRules.add(Integer.valueOf(rule));
	}

	public View createLeftView(Context context, View orignalView) {
		RelativeLayout main = new RelativeLayout(context);
		if (orignalView != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			for (Integer rule : leftRules)
				params.addRule(rule.intValue());
			main.addView(orignalView, params);
		}
		return main;
	}

	public View createMiddleView(Context context, View orignalView) {
		RelativeLayout middleMain = new RelativeLayout(context);
		if (orignalView != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			for (Integer rule : middleRules)
				params.addRule(rule.intValue());
			middleMain.addView(orignalView, params);
		}
		return middleMain;
	}

	public View createRightView(Context context, View orignalView) {
		RelativeLayout main = new RelativeLayout(context);
		if (orignalView != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			for (Integer rule : rightRules)
				params.addRule(rule.intValue());
			main.addView(orignalView, params);
		}
		return main;
	}

	private void addLeftView(TimeViewInfo timeInfo, LinearLayout item) {
		View view = timeInfo.leftView;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		params.weight = leftWeight;
		params.gravity = Gravity.CENTER_VERTICAL;
		item.addView(view, params);
	}

	private void addMiddleView(TimeViewInfo timeInfo, LinearLayout item) {
		View view = timeInfo.middleView;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT);
		params.weight = middleWeight;
		params.height= ViewGroup.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.CENTER_VERTICAL;
		item.addView(view, params);
	}

	private void addRightView(TimeViewInfo timeInfo, LinearLayout item) {
		View view = timeInfo.rightView;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		params.weight = rightWeight;
		params.gravity = Gravity.CENTER_VERTICAL;
		item.addView(view, params);
	}

}
