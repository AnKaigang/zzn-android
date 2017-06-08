package com.imsdk.imdeveloper.util;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.activity.AroundActivity;
import com.imsdk.imdeveloper.ui.activity.ProfileActivity;
import com.imsdk.imdeveloper.ui.activity.WelcomeActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import imsdk.data.IMMyself;

/**
 * Created by Administrator on 2016/9/23.
 */
public class MyLocationListener implements BDLocationListener {
    RadarSearchManager mManager;
    boolean isReceive=false;
    boolean isFirst=true;
    MapView mapView;
    AroundActivity aroundActivity;
    BaiduMap baiduMapmap;
    public MyLocationListener(AroundActivity aroundActivity, RadarSearchManager mManager,MapView map){
        this.mManager=mManager;
        this.mapView=map;
        this.aroundActivity=aroundActivity;
        this.baiduMapmap=map.getMap();
    }
    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
            isReceive=true;
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
            isReceive=true;
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            isReceive=true;
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            isReceive=true;
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            isReceive=true;
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            isReceive=true;
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息


        if(isReceive){
            if(isFirst){
                Log.i("BaiduLocationApiDem", sb.toString());
                System.out.print("安凯刚"+sb.toString());
                isFirst=false;
                RadarUploadInfo info = new RadarUploadInfo();
                info.comments = "";
                final LatLng pt=new LatLng(location.getLatitude(), location.getLongitude());
                info.pt = pt;

                // 开启定位图层
                baiduMapmap.setMyLocationEnabled(true);
// 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(pt,20);
                baiduMapmap.animateMapStatus(u);
                //mManager.uploadInfoRequest(info);

                //RadarNearbySearchOption option1 = new RadarNearbySearchOption().centerPt(pt).pageNum(0).radius(5000);
//发起查询请求
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams requestParams=new RequestParams();
                requestParams.add("username", IMMyself.getCustomUserID());
                requestParams.add("x",String.valueOf(pt.latitude));
                requestParams.add("y",String.valueOf(pt.longitude));
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka);
                OverlayOptions option = new MarkerOptions()
                        .position(pt)
                        .icon(bitmap).title(IMMyself.getCustomUserID());
                Marker marker = (Marker) (mapView.getMap().addOverlay(option));
                client.post(IMConfiguration.uploadLocation, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int j, Header[] headers,byte[] bytes) {
                        try {
                            JSONArray jsonArray=new JSONArray(new String(bytes));
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                double x=jsonObject.getDouble("latitude");
                                double y=jsonObject.getDouble("longitude");
                                BitmapDescriptor bitmap = BitmapDescriptorFactory
                                        .fromResource(R.drawable.bianjiziliao_dingwei);
                                OverlayOptions option = new MarkerOptions()
                                        .position(new LatLng(x,y))
                                        .icon(bitmap).title(jsonObject.getString("userid"));
                                final Marker markert = (Marker) (mapView.getMap().addOverlay(option));
                                baiduMapmap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        if(marker.getPosition().equals(markert.getPosition())) {
                                            Intent intent = new Intent(aroundActivity, ProfileActivity.class);
                                            intent.putExtra("CustomUserID", marker.getTitle());
                                            aroundActivity.startActivity(intent);
                                        }
                                        return true;
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            UICommon.showTips(aroundActivity, R.drawable.tips_error, "请求失败");
                    }
                });
            }
        }else{
            Log.i("BaiduLocationApiDem", sb.toString());
            System.out.print("安凯刚"+sb.toString());
            RadarUploadInfo info = new RadarUploadInfo();
            info.comments = "";
            LatLng pt=new LatLng(location.getLatitude(), location.getLongitude());
            info.pt = pt;

            // 开启定位图层
            baiduMapmap.setMyLocationEnabled(true);
// 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(pt);
            baiduMapmap.animateMapStatus(u);
            mManager.uploadInfoRequest(info);
//构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(pt)
                    .icon(bitmap);
//在地图上添加Marker，并显示
            mapView.getMap().addOverlay(option);
        }
    }
}
