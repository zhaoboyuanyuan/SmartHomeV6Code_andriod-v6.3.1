package cc.wulian.smarthomev6.main.device.device_CG27.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCommunityInfoBean;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class CG27InformationActivity extends BaseTitleActivity implements View.OnClickListener {
    private TextView tvDeviceType;
    private TextView tvProvince;
    private TextView tvCity;
    private TextView tvCommunity;
    private TextView tvCommunityNumber;
    private TextView tvBuildingNumber;
    private TextView tvUnitNumber;
    private TextView tvRoomNumber;
    private TextView tvFloor;

    private DeviceApiUnit deviceApiUnit;
    private String communityId;
    private String uc;
    private EntranceGuardCommunityInfoBean.CommunityInformationsBean communityInformationsBean;

    public static void start(Context context, String communityId, String uc) {
        Intent intent = new Intent(context, CG27InformationActivity.class);
        intent.putExtra("communityId", communityId);
        intent.putExtra("uc", uc);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_cg27_information, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Detail));
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceType = (TextView) findViewById(R.id.tv_device_type);
        tvProvince = (TextView) findViewById(R.id.tv_province);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCommunity = (TextView) findViewById(R.id.tv_community);
        tvCommunityNumber = (TextView) findViewById(R.id.tv_community_number);
        tvBuildingNumber = (TextView) findViewById(R.id.tv_building_number);
        tvUnitNumber = (TextView) findViewById(R.id.tv_unit_number);
        tvRoomNumber = (TextView) findViewById(R.id.tv_room_number);
        tvFloor = (TextView) findViewById(R.id.tv_floor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initData() {
        super.initData();
        communityId = getIntent().getStringExtra("communityId");
        uc = getIntent().getStringExtra("uc");
        deviceApiUnit = new DeviceApiUnit(this);
        getAddressInfo();
        getAllCommunityInfo();
    }

    private void getAddressInfo() {
        deviceApiUnit.getEntranceGuardAddressInfo(communityId, uc, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardAddressBean>() {
            @Override
            public void onSuccess(EntranceGuardAddressBean bean) {
                if (bean != null && bean.getCommunityAddressVO() != null) {
                    tvBuildingNumber.setText(bean.getCommunityAddressVO().get(0).getBb());
                    tvCommunityNumber.setText(bean.getCommunityAddressVO().get(0).getDd());
                    tvUnitNumber.setText(bean.getCommunityAddressVO().get(0).getRr());
                    tvFloor.setText(bean.getCommunityAddressVO().get(0).getFf());
                    tvRoomNumber.setText(bean.getCommunityAddressVO().get(0).getIi());
                }

            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getAllCommunityInfo() {
        deviceApiUnit.getEntranceGuardCommunityInfo(new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardCommunityInfoBean>() {
            @Override
            public void onSuccess(EntranceGuardCommunityInfoBean bean) {
                if (bean != null && bean.getCommunityInformations() != null) {
                    for (EntranceGuardCommunityInfoBean.CommunityInformationsBean bean1 :
                            bean.getCommunityInformations()) {
                        if (TextUtils.equals(bean1.getCommunityId(), communityId)) {
                            communityInformationsBean = bean1;
                        }
                    }
                    tvProvince.setText(communityInformationsBean.getProvinceName());
                    tvCity.setText(communityInformationsBean.getCityName());
                    tvCommunity.setText(communityInformationsBean.getCommunityName());
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_firmware_version:
                break;
        }
    }

}
