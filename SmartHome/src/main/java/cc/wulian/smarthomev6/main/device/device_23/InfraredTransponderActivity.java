package cc.wulian.smarthomev6.main.device.device_23;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.device.DeviceMoreActivity;

public class InfraredTransponderActivity extends BaseTitleActivity {

    private String deviceID;

    public static void start(Context activity, String deviceID) {
        Intent intent = new Intent(activity, InfraredTransponderActivity.class);
        intent.putExtra("deviceID", deviceID);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infrared_transponder, true);
    }

    @Override
    protected void initData() {
        deviceID = getIntent().getStringExtra("deviceID");
    }

    @Override
    protected void initTitle() {
        super.initTitle();

        setToolBarTitleAndRightImg(R.string.Device_Default_Name_23, R.drawable.icon_more);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_right:
                Intent intent = new Intent(this, DeviceMoreActivity.class);
                intent.putExtra(DeviceMoreActivity.KEY_DEVICE_ID, deviceID);
                startActivity(intent);
                break;
        }
    }
}
