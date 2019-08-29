package cc.wulian.smarthomev6.main.device.device_if02.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.device_23.custom.CustomActivity;
import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBean;
import cc.wulian.smarthomev6.support.core.apiunit.DataApiUnit;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

public class AddCustomActivity extends BaseTitleActivity {

    private static  final  String CUSTOM_BLOCK_TYPE = "0";

    private TextView nameTextView;
    private Button nextStep;

    private String deviceId;
    private Device device;
    private DataApiUnit dataApiUnit;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, AddCustomActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_add, true);
    }

    @Override
    protected void initTitle() {
        deviceId = getIntent().getStringExtra("deviceId");
        device = MainApplication.getApplication().getDeviceCache().get(deviceId);
        setToolBarTitle(getResources().getString(R.string.Home_Widget_Addremote));
    }

    @Override
    protected void initView() {
        nameTextView = (TextView) findViewById(R.id.tv_remote_name);
        nextStep = (Button) findViewById(R.id.btn_next_step);
        nextStep.setEnabled(false);
        nextStep.setAlpha(0.54f);
    }

    @Override
    protected void initData() {
        super.initData();
        dataApiUnit = new DataApiUnit(this);
    }

    @Override
    protected void initListeners() {
        super.initListeners();

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataApiUnit.doAddWifiIRController(deviceId, CUSTOM_BLOCK_TYPE, nameTextView.getText().toString(), null, new DataApiUnit.DataApiCommonListener<ControllerBean>() {
                    @Override
                    public void onSuccess(ControllerBean bean) {
                        WLog.i(TAG, "CreateSuccess");
                        CustomMainActivity.learn(AddCustomActivity.this,deviceId,bean.blockType,bean.blockName,bean.blockId);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        WLog.i(TAG, "CreateFail");
                    }
                });
            }
        });

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    nextStep.setEnabled(false);
                    nextStep.setAlpha(0.54f);
                } else {
                    nextStep.setEnabled(true);
                    nextStep.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
