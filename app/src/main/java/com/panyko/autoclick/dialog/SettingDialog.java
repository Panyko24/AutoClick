package com.panyko.autoclick.dialog;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.panyko.autoclick.R;
import com.panyko.autoclick.enums.TypeEnum;
import com.panyko.autoclick.util.CommonCode;
import com.panyko.autoclick.util.CommonData;
import com.panyko.autoclick.view.FloatingView;

public class SettingDialog {
    private Context mContext;
    private View mView;
    private Button btnSave;
    private EditText editTime;
    private EditText editCount;
    private RadioGroup rgType;
    private RadioButton rbCommon;
    private RadioButton rbGGSLogin;
    private AlertDialog mAlertDialog;
    private static SettingDialog instance;
    private Integer type;

    public static SettingDialog getInstance() {
        if (instance == null) {
            synchronized (SettingDialog.class) {
                if (instance == null) {
                    instance = new SettingDialog();
                }
            }
        }
        return instance;
    }

    private SettingDialog() {

    }

    public void init(Context mContext) {
        this.mContext = mContext;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting, null);
        btnSave = mView.findViewById(R.id.btn_save);
        editTime = mView.findViewById(R.id.edit_time);
        editCount = mView.findViewById(R.id.edit_count);
        rgType = mView.findViewById(R.id.rg_type);
        rbCommon = mView.findViewById(R.id.rb_common);
        rbGGSLogin = mView.findViewById(R.id.rb_ggs_login);
        builder.setView(mView);
        mAlertDialog = builder.create();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTime.getText().toString().trim();
                String count = editCount.getText().toString().trim();
                if (time.length() == 0 || time.startsWith("0")) {
                    CommonData.interval = 1000;
                } else {
                    CommonData.interval = Integer.valueOf(time);
                }
                if (count.length() == 0) {
                    CommonData.count = 1;
                } else {
                    CommonData.count = Integer.valueOf(count);
                }
                CommonData.type = type;
                FloatingView.getInstance(mContext).clearSightView();
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                close();
            }
        });
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_common) {
                    type = TypeEnum.TYPE_COMMON.getCode();
                } else if (i == R.id.rb_ggs_login) {
                    type = TypeEnum.TYPE_GGS_LOGIN.getCode();
                }
            }
        });
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v.getWindowToken());
                return false;
            }
        });
    }

    public void show() {
        if (mAlertDialog != null && !mAlertDialog.isShowing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mAlertDialog.show();
            type = CommonData.type;
            if (type == TypeEnum.TYPE_GGS_LOGIN.getCode()) {
                rbGGSLogin.setChecked(true);
            }  else {
                rbCommon.setChecked(true);
            }
            editCount.setText(String.valueOf(CommonData.count));
            editTime.setText(String.valueOf(CommonData.interval));
        }
    }

    public void close() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }


    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
