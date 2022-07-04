package com.panyko.autoclick.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.panyko.autoclick.R;

public class FloatingView implements View.OnTouchListener {
    private Context context;
    //private View view;
    private ImageButton btnAutoClick;//自动点击开关按钮
    private ImageButton btnSight;//瞄准点

    private WindowManager mWindowManager;//悬浮窗视图管理器

    private WindowManager.LayoutParams mLayoutParams;
    private int x;
    private int y;
    private boolean mIsShow;
    private View autoClickView;
    private View sightView;
    private int mWidthPixels;
    private int mHeightPixels;
    private boolean isBtnActivated;

    @SuppressLint("ClickableViewAccessibility")
    public FloatingView(Context context) {
        this.context = context;
        autoClickView = LayoutInflater.from(context).inflate(R.layout.view_floating_auto_click, null);
        sightView = LayoutInflater.from(context).inflate(R.layout.view_floating_sight, null);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(outMetrics);
        mWidthPixels = outMetrics.widthPixels;
        mHeightPixels = outMetrics.heightPixels;

        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        btnAutoClick = autoClickView.findViewById(R.id.btn_auto_click);
        btnSight = sightView.findViewById(R.id.btn_sight);
        btnSight.setOnTouchListener(this);
        btnAutoClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBtnActivated = !isBtnActivated;
                btnAutoClick.setActivated(isBtnActivated);
                btnSight.setActivated(isBtnActivated);
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mWindowManager != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mLayoutParams.x = mLayoutParams.x + movedX;
                    mLayoutParams.y = mLayoutParams.y + movedY;
                    if (mLayoutParams.x < 0) {
                        mLayoutParams.x = 0;
                    }
                    if (mLayoutParams.y < 0) {
                        mLayoutParams.y = 0;
                    }
                    mWindowManager.updateViewLayout(sightView, mLayoutParams);
                    break;
            }
        }
        return false;
    }


    /**
     * 显示悬浮窗
     */
    public void show() {
        if (!mIsShow) {
            mLayoutParams.x = 0;
            mLayoutParams.y = 0;
            mWindowManager.addView(autoClickView, mLayoutParams);
            mLayoutParams.x = mWidthPixels / 2;
            mLayoutParams.y = mHeightPixels / 2;
            mWindowManager.addView(sightView, mLayoutParams);
            mIsShow = true;
        }
    }

    /**
     * 关闭悬浮窗
     */
    public void dismiss() {
        if (mIsShow) {
            mWindowManager.removeView(autoClickView);
            mWindowManager.removeView(sightView);
            mIsShow = false;
        }
    }
}
