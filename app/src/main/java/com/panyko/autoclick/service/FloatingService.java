package com.panyko.autoclick.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.panyko.autoclick.view.FloatingView;

public class FloatingService extends Service {
    private FloatingView mFloatingView;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = new FloatingView(this);
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
        mFloatingView.dismiss();
    }
}
