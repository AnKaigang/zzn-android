package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.util.MyLocationListener;
import com.imsdk.imdeveloper.util.MyRadarSearchListener;


public class AroundActivity extends Activity {
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener;
    MapView mMapView = null;
    private TextView mLeft;
    private AroundFragment.OnFragmentInteractionListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_around);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mLeft=(TextView) findViewById(R.id.left);
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RadarSearchManager mManager = RadarSearchManager.getInstance();
        RadarSearchListener listener= new MyRadarSearchListener(mMapView);
        mManager.addNearbyInfoListener(listener);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        myListener= new MyLocationListener(this,mManager,mMapView);
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        mMapView.getMap().setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
}