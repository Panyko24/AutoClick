package com.panyko.autoclick.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.panyko.autoclick.R;
import com.panyko.autoclick.dialog.SettingDialog;
import com.panyko.autoclick.service.AutoClickService;
import com.panyko.autoclick.service.FloatingService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private volatile boolean isServiceBind;//是否绑定了服务
    private Intent floatingIntent;
    private Button btnOpenAccessibility;
    private Button btnOpenFloating;
    private Button btnManagerFloatingView;
    private boolean isFloatingViewShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnOpenAccessibility = findViewById(R.id.btn_open_accessibility);
        btnOpenFloating = findViewById(R.id.btn_open_floating);
        btnManagerFloatingView = findViewById(R.id.btn_floating_manager);

        floatingIntent = new Intent(this, FloatingService.class);
        btnOpenAccessibility.setOnClickListener(this);
        btnOpenFloating.setOnClickListener(this);
        btnManagerFloatingView.setOnClickListener(this);
        SettingDialog.getInstance().init(this);
        checkPermission();
    }

    private void checkPermission() {
        if (checkAccessibility()) {
            btnOpenAccessibility.setEnabled(false);
            btnOpenAccessibility.setText("无障碍服务已开启");
        } else {
            btnOpenAccessibility.setEnabled(true);
            btnOpenAccessibility.setText("开启无障碍服务");
        }
        if (checkFloatingPermission()) {
            btnOpenFloating.setEnabled(false);
            btnOpenFloating.setText("悬浮窗权限已开启");
        } else {
            btnOpenFloating.setEnabled(true);
            btnOpenFloating.setText("开启悬浮窗权限");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            checkPermission();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_accessibility) {
            startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
        } else if (v.getId() == R.id.btn_open_floating) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else if (v.getId() == R.id.btn_floating_manager) {
            if (!isServiceBind) {
                if (!checkAccessibility()) {
                    Toast.makeText(this, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkFloatingPermission()) {
                    Toast.makeText(this, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                isServiceBind = bindService(floatingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                btnManagerFloatingView.setText("关闭悬浮窗");
            } else {
                unbindService(serviceConnection);
                isServiceBind = false;
                btnManagerFloatingView.setText("开启悬浮窗");
            }
        }
    }

    /**
     * 检查无障碍服务是否开启
     *
     * @return
     */
    private boolean checkAccessibility() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = mActivityManager.getRunningServices(100);
        if (runningServices.size() <= 0) return false;
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            ComponentName service = runningService.service;
            if (service.getClassName().contains(AutoClickService.class.getName())) return true;
        }
        return false;
    }

    /**
     * 检查悬浮窗权限
     *
     * @return
     */
    private boolean checkFloatingPermission() {
        return Settings.canDrawOverlays(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}