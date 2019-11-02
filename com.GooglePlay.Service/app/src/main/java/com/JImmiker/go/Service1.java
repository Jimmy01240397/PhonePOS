package com.JImmiker.go;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import java.io.*;
import java.net.*;

public class Service1 extends Service implements LocationListener {
    private final String TAG = "BootUpService";
    private boolean running = true;
    private LocationManager lms;
    private Location location;

    private String best;
    private double lat=25.0402555, lng=121.512377;

    private String bestProvider = LocationManager.GPS_PROVIDER;
    private boolean getService = true;     //是否已開啟定位服務
    private Socket client;
    private boolean link = false;

    public Service1() {
        Log.i(TAG, "Service22 running!!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service33 running!!");
        running = true;
        locationServiceInitial(false);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service running!!");
        running = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    client = new Socket("59.127.53.197", 5555);
                    link = true;
                    Log.i(TAG, "Client go!!");
                }
                catch (Exception e)
                {
                    link = false;
                    Log.i(TAG, e.toString());
                }
                while (running) {
                    try {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {

                        }
                        locationServiceInitial(true);
                    } catch (Exception e) {
                        Log.i(TAG, e.toString());
                    }
                }
                Log.i(TAG, "Service111 stop!!");
            }
        }.start();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void locationServiceInitial(boolean on) {

        lms = (LocationManager) getSystemService(LOCATION_SERVICE); //取得系統定位服務


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        /*//做法一,由程式判斷用GPS_provider
           if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
               location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);  //使用GPS定位座標
         }
         else if ( lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
         { location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //使用GPS定位座標
         }
         else {}*/


        /* 做法二,由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者

        if(getService) {
            lms.requestLocationUpdates(bestProvider, 1000, 1, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }


        @SuppressLint("MissingPermission") Location location = lms.getLastKnownLocation(bestProvider);*/

        updateStat();

        if(on) {
            getLocation(location);
        }
    }

    public double[] updateStat(){
        lms =(LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        best = lms.getBestProvider(criteria, true);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new double[2];
        }

//Hero 2.1 開啟gps
        if(android.os.Build.MODEL.equals("HTC Hero")){
            if(lms.getLastKnownLocation("gps") != null){
                lms.requestLocationUpdates(best, 2000, 0, this);
                location = lms.getLastKnownLocation(best);
                lat=location.getLatitude();
                lng=location.getLongitude();
            }else if (lms.getLastKnownLocation("network") != null){
                lms.requestLocationUpdates("gps", 2000, 0, this);
                location = lms.getLastKnownLocation(best);
                lat=location.getLatitude();
                lng=location.getLongitude();
            }else{
                lms.requestLocationUpdates(best, 2000, 0, this);
                lat=25.0402555;
                lng=121.512377;
            }
        }

//MileStone 開啟gps
        else{
            if(lms.getLastKnownLocation("gps") != null){
                lms.requestLocationUpdates(best, 2000, 0, this);
                location = lms.getLastKnownLocation(best);
                lat=location.getLatitude();
                lng=location.getLongitude();
            }else if (lms.getLastKnownLocation("network") != null){
                short i=0;
                while(location  == null){
                    lms.requestLocationUpdates("gps", 2000, 0, this);
                    location = lms.getLastKnownLocation(best);
                    if(i++==1000){
                        location = lms.getLastKnownLocation("network");
                        break;
                    }
                }

                lat=location.getLatitude();
                lng=location.getLongitude();
            }else{
                lms.requestLocationUpdates(best, 2000, 0, this);
                lat=25.0402555;
                lng=121.512377;
            }
        }

        double[] array_latilongi = {lat,lng};
        return array_latilongi;
    }

    private void getLocation(final Location location) { //將定位資訊顯示在畫面中
        if(location != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    Double longitude = location.getLongitude();   //取得經度
                    Double latitude = location.getLatitude();     //取得緯度
                    if (link) {
                        try {
                            DataOutputStream out = new DataOutputStream(client.getOutputStream());
                            out.writeUTF(String.valueOf(longitude) + "," + String.valueOf(latitude));
                            Log.i(TAG, String.valueOf(longitude) + "," + String.valueOf(latitude));
                        } catch (Exception e) {
                            Log.i(TAG, e.toString() + " 1");
                            try {
                                client.close();
                            } catch (Exception ee) {

                            }
                            link = false;
                        }
                    } else {
                        try {
                            Log.i(TAG, " RElink");
                            client = new Socket("59.127.53.197", 5555);
                            Log.i(TAG, " GetRelink");
                            link = true;
                            try {
                                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                                out.writeUTF(String.valueOf(longitude) + "," + String.valueOf(latitude));
                            } catch (Exception e) {
                                Log.i(TAG, e.toString() + " 2");
                                try {
                                    client.close();
                                } catch (Exception ee) {

                                }

                                link = false;
                            }
                        } catch (Exception e) {
                            Log.i(TAG, e.toString() + " 3");
                            try {
                                client.close();
                            } catch (Exception ee) {

                            }
                            link = false;
                        }
                    }
                }
            }.start();
        }
        else {
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {  //當地點改變時
        // TODO 自動產生的方法 Stub
        getLocation(location);
    }
    @Override
    public void onProviderDisabled(String arg0) {//當GPS或網路定位功能關閉時
        // TODO 自動產生的方法 Stub
    }
    @Override
    public void onProviderEnabled(String arg0) { //當GPS或網路定位功能開啟
        // TODO 自動產生的方法 Stub
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //定位狀態改變
        // TODO 自動產生的方法 Stub
    }
}
