package com.panyko.autoclick;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;

import com.panyko.autoclick.service.FloatingService;

public class MainActivity extends AppCompatActivity {
    private volatile boolean isServiceBind;//是否绑定了服务
    private Intent floatingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingIntent = new Intent(this, FloatingService.class);
    }

    public void showFloatingView(View view) {
        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            if (!isServiceBind) {
                isServiceBind = bindService(floatingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Settings.canDrawOverlays(this)) {
                if (!isServiceBind) {
                    isServiceBind = bindService(floatingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }

    public void closeFloatingView(View view) {
        if (isServiceBind) {
            unbindService(serviceConnection);
            isServiceBind = false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FloatingService.FloatingBinder binder = (FloatingService.FloatingBinder) service;
            binder.getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void checkAccessibility(View view) {
        Intent intent=new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}