package cc.wulian.smarthomev6.main.device.device_if02.airconditioner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;
import cc.wulian.smarthomev6.main.device.device_if02.bean.AirCodeLibsBean;
import cc.wulian.smarthomev6.main.device.device_if02.match.DownLoadCodeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;

/**
 * created by huxc  on 2018/8/16.
 * func：机顶盒逐个匹配界面
 * email: hxc242313@qq.com
 */

public class AirOneByOneMatchActivity extends BaseTitleActivity {
    private static final String QUERY = "query";

    private LinearLayout llControlResult;
    private FrameLayout flControlView;
    private TextView tvStateClose;
    private TextView tvStateOpen;
    private TextView tvCodeLibSum;
    private TextView tvActionTip;
    private TextView tvNoCodeLibTip;
    private Button btnNextCodeLib;
    private Button btnLastCodeLib;

    private String deviceId;
    private String type;
    private String brandId;
    private String brandName;
    private int codeLibsSum;
    private int codeNum = 1;

    private DataApiUnit dataApiUnit;
    private List<AirCodeLibsBean.codeLibsBean> codeLibsList;
    private WLDialog matchFailDialog;

    public static void start(Context context, String deviceId, String type, String brandId, String brandName) {
        Intent intent = new Intent(context, AirOneByOneMatchActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("type", type);
        intent.putExtra("brandId", brandId);
        intent.putExtra("brandName", brandName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_one_by_one_match, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceId = getIntent().getStringExtra("deviceId");
        type = getIntent().getStringExtra("type");
        brandId = getIntent().getStringExtra("brandId");
        brandName = getIntent().getStringExtra("brandName");
        setToolBarTitle(brandName + WifiIRManage.getDeviceTypeName(this, type));
    }

    @Override
    protected void initView() {
        super.initView();
        llControlResult = (LinearLayout) findViewById(R.id.ll_control_result);
        flControlView = (FrameLayout) findViewById(R.id.layout_control);
        tvCodeLibSum = (TextView) findViewById(R.id.tv_code_sum);
        tvActionTip = (TextView) findViewById(R.id.tv_action_tip);
        tvStateClose = (TextView) findViewById(R.id.tv_state_close);
        tvStateOpen = (TextView) findViewById(R.id.tv_state_open);
        tvNoCodeLibTip = (TextView) findViewById(R.id.tv_no_more_codelib);
        btnNextCodeLib = (Button) findViewById(R.id.btn_next_codeLib);
        btnLastCodeLib = (Button) findViewById(R.id.btn_last_codeLib);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvStateOpen.setOnClickListener(this);
        tvStateClose.setOnClickListener(this);
        btnNextCodeLib.setOnClickListener(this);
        btnLastCodeLib.setOnClickListener(this);
        flControlView.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        dataApiUnit = new DataApiUnit(this);
        getPartCodeLib();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_control:
                llControlResult.setVisibility(View.VISIBLE);
                tvActionTip.setVisibility(View.INVISIBLE);
                sendControlCmd();
                break;
            case R.id.tv_state_close:
                if (codeNum == codeLibsSum) {
                    showMatchFailDialog();
                } else {
                    updateView(++codeNum);
                }
                llControlResult.setVisibility(View.INVISIBLE);
                tvActionTip.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_state_open:
                DownLoadCodeActivity.start(this, deviceId, brandName, type, codeLibsList.get(codeNum - 1).codeLib);
                finish();
                break;
            case R.id.btn_next_codeLib:
                llControlResult.setVisibility(View.INVISIBLE);
                updateView(++codeNum);
                VibratorUtil.holdSpeakVibration();
                break;
            case R.id.btn_last_codeLib:
                llControlResult.setVisibility(View.INVISIBLE);
                VibratorUtil.holdSpeakVibration();
                updateView(--codeNum);
                break;
        }
    }


    private void getPartCodeLib() {
        ProgressDialogManager.getDialogManager().showDialog(QUERY, this, null, null, 10000);
        dataApiUnit.doGetWifiIRPartCodeLib(deviceId, type, brandId, new DataApiUnit.DataApiCommonListener<AirCodeLibsBean>() {
            @Override
            public void onSuccess(AirCodeLibsBean bean) {
                ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
                if (bean != null && bean.codeLibs != null && bean.codeLibs.size() > 0) {
                    codeLibsList = bean.codeLibs;
                    codeLibsSum = bean.codeLibs.size();
                    updateView(codeNum);
                } else {
                    tvNoCodeLibTip.setVisibility(View.VISIBLE);
                    tvActionTip.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onFail(int code, String msg) {
                ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
                tvNoCodeLibTip.setVisibility(View.VISIBLE);
                tvActionTip.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateView(int codeNum) {
        tvCodeLibSum.setText(codeNum + "/" + codeLibsSum);
        flControlView.setVisibility(View.VISIBLE);
        if (codeNum == 1) {
            btnLastCodeLib.setVisibility(View.INVISIBLE);
        } else {
            btnLastCodeLib.setVisibility(View.VISIBLE);
        }
        if (codeNum == codeLibsSum) {
            btnNextCodeLib.setVisibility(View.INVISIBLE);
        } else {
            btnNextCodeLib.setVisibility(View.VISIBLE);
        }
    }


    private void showMatchFailDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getString(R.string.Infraredrelay_Custom_Matchfailed))
                .setMessage(getString(R.string.IF_044))
                .setPositiveButton(getString(R.string.Sure))
                .setCancelOnTouchOutSide(false)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View var1, String msg) {
                        matchFailDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onClickNegative(View var1) {

                    }
                });
        matchFailDialog = builder.create();
        if (!matchFailDialog.isShowing()) {
            matchFailDialog.show();
        }
    }


    private void sendControlCmd() {
        String src = codeLibsList.get(codeNum - 1).rcCommand.get(0).src;
        dataApiUnit.doControlWifiIR(deviceId, "2", null, null, src, new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
