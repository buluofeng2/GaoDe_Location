package com.sfx.caretestmap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 多端定位：
 *      单次定位
 *      连续定位
 */
public class LocationManager extends AppCompatActivity {

    private Button btClientSingle;
    private TextView tvResultSingle;

    private AMapLocationClient locationClientSingle = null;

    private int continueCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);

        btClientSingle = (Button)findViewById(R.id.bt_startClient1);
        tvResultSingle = (TextView)findViewById(R.id.tv_result1);
        btClientSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSingleLocation();

            }
        });

    }

//    // 单次定位
//    public void singleLocation(View view) {
//        if (btClientSingle.getText().equals(
//                getResources().getString(R.string.startLocation))) {
//            startSingleLocation();
//            btClientSingle.setText(R.string.stopLocation);
//            tvResultSingle.setText("正在定位...");
//        } else {
//            stopSingleLocation();
//            btClientSingle.setText(R.string.startLocation);
//        }
//    }


    /**
     * 启动单次客户端定位
     */
    void startSingleLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }

        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        //使用单次定位
        locationClientOption.setOnceLocation(true);
        // 地址信息
        locationClientOption.setNeedAddress(true);
        locationClientOption.setLocationCacheEnable(false);
        locationClientSingle.setLocationOption(locationClientOption);
        locationClientSingle.setLocationListener(locationSingleListener);
        locationClientSingle.startLocation();

    }

    /**
     * 停止单次客户端
     */
    void stopSingleLocation(){
        if(null != locationClientSingle){
            locationClientSingle.stopLocation();
        }
    }



    /**
     * 单次客户端的定位监听
     */
    AMapLocationListener locationSingleListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {

            long callBackTime = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            StringBuffer info = new StringBuffer();
            sb.append("单次定位完成\n");
            sb.append("回调时间: " + Utils.formatUTC(callBackTime, null) + "\n");
            if(null == location){
                sb.append("定位失败：location is null!!!!!!!");

            } else {
                sb.append(Utils.getLocationStr(location));
                double homeLongitude=location.getLongitude();   //经    度
                double homeLatitude=location.getLatitude();     //纬    度

                info.append(homeLongitude);
                info.append(",");
                info.append(homeLatitude);
                saveHomeInfo(info.toString());
            }
            Toast.makeText(LocationManager.this,info.toString(),Toast.LENGTH_SHORT).show();
            tvResultSingle.setText(sb.toString());
        }
    };
    public void saveHomeInfo(String inputText){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try
        {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if(writer!=null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSingleLocation();
        if(null != locationClientSingle){
            locationClientSingle.onDestroy();
            locationClientSingle = null;
        }
    }
}
