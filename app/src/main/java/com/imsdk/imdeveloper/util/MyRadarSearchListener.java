package com.imsdk.imdeveloper.util;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.imsdk.imdeveloper.R;

/**
 * Created by Administrator on 2016/9/23.
 */
public class MyRadarSearchListener implements RadarSearchListener {
    MapView mapView;
    public MyRadarSearchListener(MapView mapView){
        this.mapView=mapView;
    }
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
        System.out.println("查找成功");
        for(int i=0;i<radarNearbyResult.infoList.size();i++){
            RadarNearbyInfo radarNearbyInfo=radarNearbyResult.infoList.get(i);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.bianjiziliao_dingwei);
            OverlayOptions option = new MarkerOptions()
                    .position(radarNearbyInfo.pt)
                    .icon(bitmap).title(radarNearbyInfo.comments);
            mapView.getMap().addOverlay(option);
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }
}
