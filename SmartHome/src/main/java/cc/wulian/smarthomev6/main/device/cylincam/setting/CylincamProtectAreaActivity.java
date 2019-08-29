package cc.wulian.smarthomev6.main.device.cylincam.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import java.io.File;
import java.util.LinkedList;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.support.customview.CustomOverlayView;
import cc.wulian.smarthomev6.support.utils.DialogUtil;
import cc.wulian.smarthomev6.support.utils.FileUtil;
import cc.wulian.smarthomev6.support.utils.MonitorArea;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * Created by hxc on 2017/6/8.
 * func:随便看防护区域界面
 */

public class CylincamProtectAreaActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CylincamProtectAreaActivity";
    private String deviceId;
    private Button btnSure;
    private Button btnReset;
    private RelativeLayout rlBg;
    private CustomOverlayView cov;
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
        setContentView(R.layout.activity_cylincam_protect_area);
        initView();
        initListeners();
        initData();
    }


    protected void initView() {
        cov = (CustomOverlayView) findViewById(R.id.cov);
        btnSure = (Button) findViewById(R.id.btn_sure);
        btnReset = (Button) findViewById(R.id.btn_reset);
        rlBg = (RelativeLayout) findViewById(R.id.rl_bg);

    }

    protected void initData() {
        isFirstRun();
        deviceId = getIntent().getStringExtra("deviceId");
        filePath = FileUtil.getLastFramePath() + "/" + deviceId + ".jpg";
        showSnapshot(filePath);
        area = getIntent().getStringExtra("area");
        type = getIntent().getIntExtra("type", 1);
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
            case CylincamProtectSettingActivity.REQUESTCODE_MOVE_AREA:
                myHandler.sendEmptyMessageDelayed(1, 500);
                break;
            case CylincamProtectSettingActivity.REQUESTCODE_COVER_AREA:
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
            WLog.i(TAG, "selected area = "+cov.getPointResultString().toString());
            setResult(RESULT_OK, it);
            this.finish();
        } else {
            ToastUtil.show(getString(R.string.Set_Protection_Area));
        }
    }

    private void isFirstRun() {
//        isFirstRun = preference.cylincamIsFirstSetArea();
//        if (isFirstRun) {
//            showTips();
//        }
    }

    private void showTips() {
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
                Drawable drawable = new BitmapDrawable(bitmap);
                cov.setBackground(drawable);
            }
        }
    }
}
