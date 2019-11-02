package com.jimmiker.get;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Socket client;
    private boolean link = false;
    private DataInputStream dini = null;
    private String tmp = null;
    private Timer mTimer;
    private TextView textView;
    private Button button1;
    private Button button2;
    Marker myMarker = null;
    boolean move = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        textView = findViewById(R.id.textView2);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(String.valueOf(textView.getText()));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move = true;
            }
        });
        mTimer = new Timer();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void copyToClipboard(String str){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(str);
            Log.e("version","1 version");
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text label",str);
            clipboard.setPrimaryClip(clip);
            Log.e("version","2 version");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    client = new Socket("59.127.53.197", 5555);
                    link = true;
                }
                catch (Exception e)
                {
                    link = false;
                }
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                    if(link) {
                        try {
                            dini = new DataInputStream(client.getInputStream());
                            tmp = new String(dini.readUTF());

                        } catch (Exception e) {
                            try {
                                client.close();
                                link = false;
                            } catch (Exception ee) {

                            }
                        }
                    }
                    else
                    {
                        try {
                            client = new Socket("59.127.53.197", 5555);
                            link = true;
                        }
                        catch (Exception e)
                        {
                            link = false;
                        }
                    }
                }
            }
        }.start();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 500, 3000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    try {
                        String[] tokens = tmp.split(",");
                        setUpMap(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
                        textView.setText(tokens[1] + "," + tokens[0]);
                    }
                    catch (Exception e)
                    {

                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private void addMarker(LatLng place, String title, String snippet) {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title)
                .snippet(snippet)
                .icon(icon);

        mMap.addMarker(markerOptions);
    }
    private void setUpMap(double x, double y) {
        // 刪除原來預設的內容
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
/*
        // 建立位置的座標物件
        LatLng place = new LatLng(y, x);
        // 移動地圖
        moveMap(place);

        // 加入地圖標記
        addMarker(place, "由天才製作令人感到恐懼的全球追蹤系統", " 神秘人物的位置");*/

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(y, x);
        try
        {
            myMarker.remove();
            myMarker = null;
        }
        catch (Exception e)
        {

        }
        myMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("由天才製作令人感到恐懼的全球追蹤系統").snippet("神秘人物的位置"));
        if(move) {
            moveMap(sydney);
            move = false;
        }

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}; // 选择你需要申请的权限
            for (int i = 0; i < permissions.length; i++) {
                int state = ContextCompat.checkSelfPermission(this, permissions[i]);
                if (state != PackageManager.PERMISSION_GRANTED) { // 判断权限的状态
                    ActivityCompat.requestPermissions(this, permissions, 200); // 申请权限
                    return;
                }
            }
        }
    }
}
