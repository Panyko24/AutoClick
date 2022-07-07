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
        instance.mContext = mContext;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        instance.mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting, null);
        instance.btnSave = mView.findViewById(R.id.btn_save);
        instance.editTime = mView.findViewById(R.id.edit_time);
        builder.setView(mView);
        instance.mAlertDialog = builder.create();
        instance.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = instance.editTime.getText().toString().trim();
                if (time.length() == 0 || time.startsWith("0")) {
                    CommonData.interval = 1000;
                } else {
                    CommonData.interval = Integer.valueOf(time);
                }
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                close();
            }
        });
    }

    public void show() {
        if (instance.mAlertDialog != null && !instance.mAlertDialog.isShowing()) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                instance.mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            }else{
                instance.mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            instance.mAlertDialog.show();
        }
    }

    public void close() {
        if (instance.mAlertDialog != null && instance.mAlertDialog.isShowing()) {
            instance.mAlertDialog.dismiss();
        }
    }

}
