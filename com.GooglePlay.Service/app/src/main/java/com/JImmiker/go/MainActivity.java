package com.JImmiker.go;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener
{
    private boolean getService = false;     //是否已開啟定位服務
    private final String TAG = "BootUpService";

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.KILL_BACKGROUND_PROCESSES, Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET}; // 选择你需要申请的权限
            for (int i = 0; i < permissions.length; i++) {
                int state = ContextCompat.checkSelfPermission(this, permissions[i]);
                if (state != PackageManager.PERMISSION_GRANTED) { // 判断权限的状态
                    ActivityCompat.requestPermissions(this, permissions, 200); // 申请权限
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity running!!");
        checkPermission();
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Intent mainActivityIntent = new Intent(this, Service1.class);
            startService(mainActivityIntent);
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
        }
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 200) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 用户点的拒绝，仍未拥有权限
                    Toast.makeText(this, "要求權限", Toast.LENGTH_SHORT).show();
                    // 可以选择添加如下代码在系统设置中打开该应用的设置页面
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    return;
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
