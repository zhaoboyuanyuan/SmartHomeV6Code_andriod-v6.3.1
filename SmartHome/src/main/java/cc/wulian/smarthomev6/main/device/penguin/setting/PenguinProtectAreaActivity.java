package cc.wulian.smarthomev6.main.device.penguin.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.LinkedList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.lookever.setting.LookEverProtectSettingActivity;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamDeviceBean;
import cc.wulian.smarthomev6.support.customview.CustomOverlayView;
import cc.wulian.smarthomev6.support.event.LastFrameEvent;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.MonitorArea;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/6/8.
 * func:企鹅机防护区域界面
 */

public class PenguinProtectAreaActivity extends Activity implements View.OnClickListener {
    private  static final String TAG = "PenguinProtectAreaActivity";
    private String deviceID;
    private String sipDomain;
    private ICamDeviceBean iCamDeviceBean;
    private Button btnSure;
    private Button btnReset;
    private RelativeLayout rlBg;
    CustomOverlayView cov;
    private int type;
    private String area;
    Handler myHandler;
    public LinkedList<MonitorArea> mas = new LinkedList<MonitorArea>();
    private Dialog tipDialog;
    private boolean isFirstRun;
    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penguin_protect_area);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void initView() {
        cov = (CustomOverlayView) findViewById(R.id.cov);
        btnSure = (Button) findViewById(R.id.btn_sure);
        btnReset = (Button) findViewById(R.id.btn_reset);
        rlBg = (RelativeLayout) findViewById(R.id.rl_bg);

    }

    protected void initData() {
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        deviceID = iCamDeviceBean.did;
        sipDomain = iCamDeviceBean.sdomain;
        isFirstRun();
        filePath = FileUtil.getLastFramePath() + "/" + deviceID+".jpg";
        showSnapshot(filePath);
        type = getIntent().getIntExtra("type", 1);
        area = getIntent().getStringExtra("area");
        myHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                switch (msg.what) {
                    case 1:
                        cov.restoreMonitorArea(area.split(";"));
                        break;
                    case 2:
                        cov.restoreMonitorArea(area.split(";"));
                        break;
                }
            }
        };
        // 3、还原原先配置的数据
        switch (type) {
            case LookEverProtectSettingActivity.REQUESTCODE_MOVE_AREA:
                myHandler.sendEmptyMessageDelayed(1, 500);
                break;
            case LookEverProtectSettingActivity.REQUESTCODE_COVER_AREA:
                MonitorArea first = cov.mas.getFirst();
                cov.mas.clear();
                cov.mas.add(first);
                myHandler.sendEmptyMessageDelayed(2, 500);
                break;
        }
    }

    protected void initListeners() {
        btnSure.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        rlBg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                sure();
                break;
            case R.id.btn_reset:
                cov.reset();
                break;
        }
    }

    public void sure() {
        if (cov.getPointResult().length >= 0) {
//            for (String i : cov.getPointResult()) {
//                Utils.sysoInfo(i);
//            }
            Intent it = new Intent();
            it.putExtra("area", cov.getPointResultString());
            WLog.i("protectArea",cov.getPointResultString());
            setResult(RESULT_OK, it);
            this.finish();
        } else {
            ToastUtil.show(R.string.Set_Protection_Area);
        }
    }

    private void isFirstRun(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstRun", false);
        editor.commit();
        if(isFirstRun){
            showTips();
        }
    }
    private void showTips(){
        tipDialog = DialogUtil.showProtectAreaTipDialog(this, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
            }
        });
    }
    private void showSnapshot(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Drawable drawable =new BitmapDrawable(bitmap);
                cov.setBackground(drawable);
            }
        }
    }
}
