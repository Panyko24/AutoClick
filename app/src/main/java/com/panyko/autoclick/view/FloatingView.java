package com.panyko.autoclick.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.panyko.autoclick.R;
import com.panyko.autoclick.activity.MainActivity;
import com.panyko.autoclick.activity.SettingActivity;
import com.panyko.autoclick.dialog.SettingDialog;
import com.panyko.autoclick.service.AutoClickService;
import com.panyko.autoclick.util.CommonCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloatingView implements View.OnTouchListener {
    private Context context;
    //private View view;
    private ImageButton btnAutoClick;//自动点击开关按钮
    private ImageButton btnSetting;
    private ImageButton btnSight;//瞄准点
    private ImageButton btnAdd;
    private ImageButton btnReduce;
    private WindowManager mWindowManager;//悬浮窗视图管理器

    private WindowManager.LayoutParams mLayoutParams;
    private int x;
    private int y;
    private boolean mIsShow;
    private View managerView;
    private View sightView;
    private int mWidthPixels;
    private int mHeightPixels;
    private boolean isBtnActivated;
    private static final String TAG = "FloatingView";

    private Map<String, View> sightViewMap;
    private int[] sightResourceIds = new int[]{R.mipmap.icon_number_one, R.mipmap.icon_number_two, R.mipmap.icon_number_three, R.mipmap.icon_number_four, R.mipmap.icon_number_five};

    @SuppressLint("ClickableViewAccessibility")
    public FloatingView(Context context) {
        this.context = context;

        sightViewMap = new HashMap<>();
        managerView = LayoutInflater.from(context).inflate(R.layout.view_floating_manager, null);
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

        btnAutoClick = managerView.findViewById(R.id.btn_auto_click);
        btnSetting = managerView.findViewById(R.id.btn_setting);
        btnAdd = managerView.findViewById(R.id.btn_add);
        btnReduce = managerView.findViewById(R.id.btn_reduce);
        btnSight = sightView.findViewById(R.id.btn_sight);
        btnSight.setTag("sight");
        btnSight.setOnTouchListener(this);

        btnAutoClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBtnActivated = !isBtnActivated;
                btnAutoClick.setActivated(isBtnActivated);
                btnSight.setActivated(isBtnActivated);

                Intent intent = new Intent(context, AutoClickService.class);
                if (isBtnActivated) {
                    int[] location = new int[2];
                    btnSight.getLocationOnScreen(location);
                    int pointX = location[0] + btnSight.getMeasuredWidth() / 2;
                    int pointY = location[1] + btnSight.getMeasuredHeight() / 2;

                    intent.putExtra("action", CommonCode.ACTION_AUTO_CLICK_START);
                    intent.putExtra("pointX", pointX);
                    intent.putExtra("pointY", pointY);
                    btnSight.setVisibility(View.GONE);
                } else {
                    intent.putExtra("action", CommonCode.ACTION_AUTO_CLICK_STOP);
                    btnSight.setVisibility(View.VISIBLE);
                }
                context.startService(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingDialog.getInstance().show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sightViewMap.size()<5) {
                    mLayoutParams.x = mWidthPixels / 2;
                    mLayoutParams.y = mHeightPixels / 2;
                    SightView sightView = new SightView(context, sightResourceIds[sightViewMap.size()]);
                    mWindowManager.addView(sightView, mLayoutParams);
                    String name = "sight_number_" + sightViewMap.size() + 1;
                    sightViewMap.put(name, sightView);
                    sightView.setTag(name);
                    sightView.setOnTouchListener(FloatingView.this::onTouch);
                }

            }
        });


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(v.getTag().toString());
        if (mWindowManager != null && !isBtnActivated) {
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
                    mLayoutParams.x = ((int)event.getX()) + movedX;
                    mLayoutParams.y = ((int)event.getY()) + movedY;
                    if (mLayoutParams.x < 0) {
                        mLayoutParams.x = 0;
                    }
                    if (mLayoutParams.y < 0) {
                        mLayoutParams.y = 0;
                    }

                    View view = sightViewMap.get(v.getTag().toString());
                    if (view!=null){
                        mWindowManager.updateViewLayout(view, mLayoutParams);
                    }
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
            mWindowManager.addView(managerView, mLayoutParams);
            mIsShow = true;
        }
    }

    /**
     * 关闭悬浮窗
     */
    public void dismiss() {
        if (mIsShow) {
            Intent intent = new Intent(context, AutoClickService.class);
            intent.putExtra("action", CommonCode.ACTION_AUTO_CLICK_STOP);
            context.startService(intent);
            mWindowManager.removeView(managerView);
            mWindowManager.removeView(sightView);
            mIsShow = false;
        }
    }


}
