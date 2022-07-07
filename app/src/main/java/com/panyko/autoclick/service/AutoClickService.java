package com.panyko.autoclick.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.panyko.autoclick.util.CommonCode;
import com.panyko.autoclick.util.CommonData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoClickService extends AccessibilityService {
    private static final String TAG = "AutoClickService";
    private int mPointX;
    private int mPointY;
    private ScheduledExecutorService mScheduledExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra("action");
            Log.i(TAG, "onStartCommand: " + action);
            if (action.equals(CommonCode.ACTION_AUTO_CLICK_START)) {
                mPointX = intent.getIntExtra("pointX", 0);
                mPointY = intent.getIntExtra("pointY", 0);
                autoClickView();
            } else if (action.equals(CommonCode.ACTION_AUTO_CLICK_STOP)) {
                if (mScheduledExecutorService != null) {
                    mScheduledExecutorService.shutdownNow();
                    mScheduledExecutorService = null;
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void autoClickView() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Path path = new Path();
                path.moveTo(mPointX, mPointY);
                Log.i(TAG, "run: " + mPointX + "," + mPointY);
                GestureDescription description = new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, 100L, 100L)).build();
                dispatchGesture(description, new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        Log.d(TAG, "自动点击完成");
                    }

                    @Override
                    public void onCancelled(GestureDescription gestureDescription) {
                        super.onCancelled(gestureDescription);
                        Log.d(TAG, "自动点击取消");
                    }
                }, null);
            }
        }, 0, CommonData.interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }
}
