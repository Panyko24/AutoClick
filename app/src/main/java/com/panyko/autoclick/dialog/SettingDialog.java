package com.panyko.autoclick.dialog;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.panyko.autoclick.R;
import com.panyko.autoclick.util.CommonData;

public class SettingDialog {
    private Context mContext;
    private View mView;
    private Button btnSave;
    private EditText editTime;
    private EditText editCount;
    private AlertDialog mAlertDialog;
    private static SettingDialog instance;

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
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                close();
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
        }
    }

    public void close() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}
