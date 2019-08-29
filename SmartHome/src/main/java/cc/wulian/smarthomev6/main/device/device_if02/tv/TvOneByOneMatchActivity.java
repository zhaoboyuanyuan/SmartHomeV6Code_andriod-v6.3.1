package cc.wulian.smarthomev6.main.device.device_if02.tv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.AirCodeLibsBean;
import cc.wulian.smarthomev6.main.device.device_if02.match.DownLoadCodeActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.tools.dialog.ProgressDialogManager;
import cc.wulian.smarthomev6.support.tools.dialog.WLDialog;
import cc.wulian.smarthomev6.support.utils.VibratorUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2018/8/16.
 * func：电视逐个匹配界面
 * email: hxc242313@qq.com
 */

public class TvOneByOneMatchActivity extends BaseTitleActivity {
    private static final String QUERY = "query";
    private String[] keysCode;
    private String[] tagArray = {"power", "vol+", "ch+"};

    private LinearLayout llControlResult;
    private FrameLayout flControlView;
    private TextView tvStateClose;
    private TextView tvStateOpen;
    private TextView tvCodeLibSum;
    private TextView tvActionTip;
    private TextView tvButtonName;
    private TextView tvResultTip;
    private TextView tvMatchTip;
    private TextView tvNoCodeLibTip;
    private Button btnNextCodeLib;
    private Button btnLastCodeLib;
    private ImageView bgControl;


    private String deviceId;
    private String type;
    private String brandId;
    private String brandName;
    private int codeLibsSum;
    private int codeNum = 1;
    private int index;//每组码库中三个按键的切换下标

    private DataApiUnit dataApiUnit;
    private List<AirCodeLibsBean.codeLibsBean> codeLibsList;
    private HashMap<String, String> codeMap;
    private WLDialog matchFailDialog;

    public static void start(Context context, String deviceId, String type, String brandId, String brandName) {
        Intent intent = new Intent(context, TvOneByOneMatchActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("type", type);
        intent.putExtra("brandId", brandId);
        intent.putExtra("brandName", brandName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_one_by_one_match, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        deviceId = getIntent().getStringExtra("deviceId");
        type = getIntent().getStringExtra("type");
        brandId = getIntent().getStringExtra("brandId");
        brandName = getIntent().getStringExtra("brandName");
        setToolBarTitle(brandName + getString(R.string.Infraredrelay_Addremote_Television));
    }

    @Override
    protected void initView() {
        super.initView();
        llControlResult = (LinearLayout) findViewById(R.id.ll_control_result);
        flControlView = (FrameLayout) findViewById(R.id.layout_control);
        tvCodeLibSum = (TextView) findViewById(R.id.tv_code_sum);
        tvActionTip = (TextView) findViewById(R.id.tv_action_tip);
        tvResultTip = (TextView) findViewById(R.id.tv_control_result_tip);
        tvStateClose = (TextView) findViewById(R.id.tv_state_close);
        tvStateOpen = (TextView) findViewById(R.id.tv_state_open);
        tvButtonName = (TextView) findViewById(R.id.tv_button_name);
        tvMatchTip = (TextView) findViewById(R.id.tv_match_tip);
        tvNoCodeLibTip = (TextView) findViewById(R.id.tv_no_more_codelib);
        btnNextCodeLib = (Button) findViewById(R.id.btn_next_codeLib);
        btnLastCodeLib = (Button) findViewById(R.id.btn_last_codeLib);
        bgControl = (ImageView) findViewById(R.id.bg_control);
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
        keysCode = new String[3];
        codeMap = new HashMap<>();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_control:
                llControlResult.setVisibility(View.VISIBLE);
                tvActionTip.setVisibility(View.INVISIBLE);
                sendControlCmd(tagArray[index]);
                break;
            case R.id.tv_state_close:
                index = 0;
                flControlView.setTag(tagArray[index]);
                if (codeNum == codeLibsSum) {
                    showMatchFailDialog();
                } else {
                    updateView(++codeNum);
                }
                llControlResult.setVisibility(View.INVISIBLE);
                tvActionTip.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_state_open:
                llControlResult.setVisibility(View.INVISIBLE);
                tvActionTip.setVisibility(View.VISIBLE);
                if (index == 2) {
                    DownLoadCodeActivity.start(this, deviceId, brandName, type, codeLibsList.get(codeNum - 1).codeLib);
                    finish();
                } else {
                    ++index;
                    flControlView.setTag(tagArray[index]);
                }
                setControlButtonView();
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
        index = 0;
        setControlButtonView();
        tvCodeLibSum.setText(codeNum + "/" + codeLibsSum);
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
        getKeyCodesArray();
    }

    private void setControlButtonView() {
        flControlView.setVisibility(View.VISIBLE);
        if (index == 0) {
            tvButtonName.setText("");
            tvActionTip.setText(getString(R.string.IF_038));
            tvResultTip.setText(getString(R.string.IF_039));
            tvMatchTip.setText(getString(R.string.IF_037));
            bgControl.setBackground(getResources().getDrawable(R.drawable.selector_if01_match_open));
        } else if (index == 1) {
            tvButtonName.setText(getString(R.string.IF_049) + "+");
            tvActionTip.setText(getString(R.string.IF_041));
            tvResultTip.setText(getString(R.string.IF_042));
            tvMatchTip.setText(getString(R.string.IF_040));
            bgControl.setBackground(getResources().getDrawable(R.drawable.selector_if01_control_match));
        } else if (index == 2) {
            tvButtonName.setText(getString(R.string.IF_048) + "+");
            tvActionTip.setText(getString(R.string.IF_041));
            tvResultTip.setText(getString(R.string.IF_042));
            tvMatchTip.setText(getString(R.string.IF_040));
            bgControl.setBackground(getResources().getDrawable(R.drawable.selector_if01_control_match));
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

    /**
     * 遍历出每一个codeLibs中电源、音量、频道对应的码
     */
    private void getKeyCodesArray() {
        if (codeLibsList == null || codeLibsList.size() == 0) {
            tvNoCodeLibTip.setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < codeLibsList.get(codeNum - 1).rcCommand.size(); i++) {
            if (tagArray[0].equals(codeLibsList.get(codeNum - 1).rcCommand.get(i).kn)) {
                keysCode[0] = codeLibsList.get(codeNum - 1).rcCommand.get(i).src;
                codeMap.put(tagArray[0], codeLibsList.get(codeNum - 1).rcCommand.get(i).src);
            } else if (tagArray[1].equals(codeLibsList.get(codeNum - 1).rcCommand.get(i).kn)) {
                keysCode[1] = codeLibsList.get(codeNum - 1).rcCommand.get(i).src;
                codeMap.put(tagArray[1], codeLibsList.get(codeNum - 1).rcCommand.get(i).src);
            } else if (tagArray[2].equals(codeLibsList.get(codeNum - 1).rcCommand.get(i).kn)) {
                keysCode[2] = codeLibsList.get(codeNum - 1).rcCommand.get(i).src;
                codeMap.put(tagArray[2], codeLibsList.get(codeNum - 1).rcCommand.get(i).src);
            }
        }
        WLog.i(TAG, tagArray[0] + keysCode[0] + "\n" + tagArray[1] + keysCode[1] + "\n" + tagArray[2] + keysCode[2]);
    }


    private void sendControlCmd(String tag) {
        dataApiUnit.doControlWifiIR(deviceId, "2", null, null, codeMap.get(tag), new DataApiUnit.DataApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
