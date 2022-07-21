package com.panyko.autoclick.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.panyko.autoclick.enums.TypeEnum;
import com.panyko.autoclick.pojo.Floating;
import com.panyko.autoclick.util.CommonCode;
import com.panyko.autoclick.util.CommonData;
import com.panyko.autoclick.view.FloatingView;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoClickService extends AccessibilityService {
    private static final String TAG = "AutoClickService";

    private List<HashMap<String, Object>> dataList;
    private int currentPosition;
    private ScheduledExecutorService mScheduledExecutorService;
    private int executeCount;

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
                dataList = (List<HashMap<String, Object>>) intent.getSerializableExtra("data");
                currentPosition = 0;
                if (CommonData.type == TypeEnum.TYPE_COMMON.getCode()) {
                    autoClickViewByCommon();
                } else if (CommonData.type == TypeEnum.TYPE_GGS_LOGIN.getCode()) {
                    autoClickViewByGGSLogin();
                }
            } else if (action.equals(CommonCode.ACTION_AUTO_CLICK_STOP)) {
                if (mScheduledExecutorService != null) {
                    mScheduledExecutorService.shutdown();
                    mScheduledExecutorService = null;
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 普通模式下自动点击
     */
    private void autoClickViewByCommon() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        executeCount = 0;
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                if (CommonData.count > 0 && executeCount >= CommonData.count) {
                    return;
                }
                HashMap<String, Object> map = dataList.get(currentPosition);
                currentPosition++;
                Path path = new Path();
                path.moveTo(((Integer) map.get("pointX")), (Integer) map.get("pointY"));
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
                if (currentPosition >= dataList.size()) {
                    currentPosition = 0;
                    executeCount++;
                }

            }
        }, CommonData.interval, CommonData.interval, TimeUnit.MILLISECONDS);
    }

    /**
     * 贵高速登录模式下自动点击
     */
    private void autoClickViewByGGSLogin() {
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        executeCount = 0;
        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                List<AccessibilityNodeInfo> nodeInfoList1 =getRootInActiveWindow().findAccessibilityNodeInfosByText("允许");
                if (nodeInfoList1 != null && nodeInfoList1.size() > 0) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfoList1) {
                        if (nodeInfo.getClassName().toString().equals("android.widget.Button")) {
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    List<AccessibilityNodeInfo> nodeInfoList2 = getRootInActiveWindow().findAccessibilityNodeInfosByText("个人登录");
                    if (nodeInfoList2 != null && nodeInfoList2.size() > 0) {
                        for (AccessibilityNodeInfo nodeInfo : nodeInfoList2) {
                            if (nodeInfo.getClassName().toString().equals("android.widget.TextView")) {
                                HashMap<String, Object> map = dataList.get(0);
                                Path path = new Path();
                                path.moveTo(((Integer) map.get("pointX")), (Integer) map.get("pointY"));
                                GestureDescription description = new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(path, 100L, 100L)).build();
                                dispatchGesture(description, null, null);
                            }
                        }

                    }
                }
            }
        }, CommonData.interval, CommonData.interval, TimeUnit.MILLISECONDS);

    }


    private void autoClickViewTest(){
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        executeCount = 0;
        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                List<AccessibilityWindowInfo> windows = getWindows();
                for (AccessibilityWindowInfo window : windows) {
                    List<AccessibilityNodeInfo> nodeInfoList = window.getRoot().findAccessibilityNodeInfosByViewId("com.moutai.mall:id/btVerifyCode");
                    if (nodeInfoList != null && nodeInfoList.size() > 0) {
                        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                            Log.i(TAG, "run: "+nodeInfo.getText());
                        }
                    }
                }

            }
        }, CommonData.interval, CommonData.interval, TimeUnit.MILLISECONDS);

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
