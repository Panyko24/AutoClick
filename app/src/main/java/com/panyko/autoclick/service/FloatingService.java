package com.panyko.autoclick.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.panyko.autoclick.activity.MainActivity;
import com.panyko.autoclick.dialog.SettingDialog;
import com.panyko.autoclick.view.FloatingView;

public class FloatingService extends Service {
    private FloatingView mFloatingView;
    private static final String TAG = "FloatingService";

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = FloatingView.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mFloatingView.show();
        return new FloatingBinder();
    }

    public class FloatingBinder extends Binder {
        public FloatingService getInstance() {
            return FloatingService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mFloatingView.dismiss();
    }
}
