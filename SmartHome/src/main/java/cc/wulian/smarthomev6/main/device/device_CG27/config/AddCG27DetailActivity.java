package cc.wulian.smarthomev6.main.device.device_CG27.config;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.ICamCloudApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.IcamCloudCheckBindBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardBuildingBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCityBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCityDistrictBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCommunityBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardDistrictBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardFloorBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHouseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardProvinceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardUnitBean;
import cc.wulian.smarthomev6.support.customview.BottomMenu;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class AddCG27DetailActivity extends BaseTitleActivity {
    private static final String QUERY = "QUERY";
    private TextView tvProvince;
    private TextView tvCity;
    private TextView tvCityDistrict;
    private TextView tvCommunity;
    private TextView tvCommunityNumber;
    private TextView tvBuildingNumber;
    private TextView tvUnitNumber;
    private TextView tvUnitFloor;
    private TextView tvRoomNumber;
    private TextView tvVerifyCode;
    private EditText etVerifyCode;
    private Button btnNextStep;

    //门禁的deviceId生成规则：communityId + uc
    private String deviceId;
    private String provinceId;
    private String cityId;
    private String cityDistrictId;
    private String communityId;
    private String districtId;
    private String buildingId;
    private String unitId;
    private String floorId;
    private String houseId;
    private String verifyCode;
    private String uc;
    private String tag;


    private List<EntranceGuardProvinceBean.ProvinceInformationsBean> provinceList;
    private List<EntranceGuardCityBean.CityInformationsBean> cityList;
    private List<EntranceGuardCityDistrictBean.DistrictInformationsBean> cityDistrictList;
    private List<EntranceGuardCommunityBean.CommunityInformationsBean> communityList;
    private List<String> districtList;
    private List<String> buildingList;
    private List<String> unitList;
    private List<String> floorList;
    private List<String> houseList;

    private HashMap<String, String> infoMap;
    private List<String> tagList;

    private BottomMenu bottomMenu;
    private DeviceApiUnit deviceApiUnit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acs_detail, true);
    }

    @Override
    protected void initView() {
        super.initView();
        tvProvince = (TextView) findViewById(R.id.tv_province);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCityDistrict = (TextView) findViewById(R.id.tv_city_strict);
        tvCommunity = (TextView) findViewById(R.id.tv_community);
        tvCommunityNumber = (TextView) findViewById(R.id.tv_community_number);
        tvBuildingNumber = (TextView) findViewById(R.id.tv_building_number);
        tvUnitNumber = (TextView) findViewById(R.id.tv_unit_number);
        tvUnitFloor = (TextView) findViewById(R.id.tv_unit_floor);
        tvRoomNumber = (TextView) findViewById(R.id.tv_room_number);
        tvVerifyCode = (TextView) findViewById(R.id.tv_code);
        btnNextStep = (Button) findViewById(R.id.btn_next_step);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvCityDistrict.setOnClickListener(this);
        tvCommunity.setOnClickListener(this);
        tvCommunityNumber.setOnClickListener(this);
        tvBuildingNumber.setOnClickListener(this);
        tvUnitNumber.setOnClickListener(this);
        tvUnitFloor.setOnClickListener(this);
        tvRoomNumber.setOnClickListener(this);
        tvVerifyCode.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceApiUnit = new DeviceApiUnit(this);
        infoMap = new HashMap<>();
        bottomMenu = new BottomMenu(this, new BottomMenu.MenuClickListener() {
            @Override
            public void onSure() {
                infoMap.put(tag, getInfos(bottomMenu.getCurrent()));
//                setEnable();
                setClickable(tag);
            }

            @Override
            public void onCancel() {

            }
        });
        initTagList();
        setClickable("province");

    }

    private void initTagList() {
        tagList = new ArrayList<>();
        tagList.add(0, "province");
        tagList.add(1, "city");
        tagList.add(2, "cityDistrict");
        tagList.add(3, "community");
        tagList.add(4, "district");
        tagList.add(5, "building");
        tagList.add(6, "unit");
        tagList.add(7, "floor");
        tagList.add(8, "house");
    }


    private void setClickable(String currentTag) {
        int index = tagList.indexOf(currentTag);
        for (int i = index + 1; i < tagList.size(); i++) {
            infoMap.remove(tagList.get(i));
        }
        tvProvince.setEnabled(true);
        tvCity.setEnabled(infoMap.containsKey("province"));
        tvCity.setText(infoMap.containsKey("city") ? tvCity.getText().toString() : "未选择");
        tvCityDistrict.setEnabled(infoMap.containsKey("city"));
        tvCityDistrict.setText(infoMap.containsKey("cityDistrict") ? tvCityDistrict.getText().toString() : "未选择");
        tvCommunity.setEnabled(infoMap.containsKey("cityDistrict"));
        tvCommunity.setText(infoMap.containsKey("community") ? tvCommunity.getText().toString() : "未选择");
        tvCommunityNumber.setEnabled(infoMap.containsKey("community"));
        tvCommunityNumber.setText(infoMap.containsKey("district") ? tvCommunityNumber.getText().toString() : "未选择");
        tvBuildingNumber.setEnabled(infoMap.containsKey("district"));
        tvBuildingNumber.setText(infoMap.containsKey("building") ? tvBuildingNumber.getText().toString() : "未选择");
        tvUnitNumber.setEnabled(infoMap.containsKey("building"));
        tvUnitNumber.setText(infoMap.containsKey("unit") ? tvUnitNumber.getText().toString() : "未选择");
        tvUnitFloor.setEnabled(infoMap.containsKey("unit"));
        tvUnitFloor.setText(infoMap.containsKey("floor") ? tvUnitFloor.getText().toString() : "未选择");
        tvRoomNumber.setEnabled(infoMap.containsKey("floor"));
        tvRoomNumber.setText(infoMap.containsKey("house") ? tvRoomNumber.getText().toString() : "未选择");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next_step:
//                if (TextUtils.equals(etVerifyCode.getText().toString().trim(), verifyCode)) {
                getDeviceInfo();
//                } else {
//                    ToastUtil.show(R.string.Login_WrongToken);
//                }
                break;
            case R.id.tv_province:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getProvinces(v);
                break;
            case R.id.tv_city:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getCity(v);
                break;
            case R.id.tv_city_strict:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getCityDistrict(v);
                break;
            case R.id.tv_community:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getCommunity(v);
                break;
            case R.id.tv_community_number:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getDistrict(v);
                break;
            case R.id.tv_building_number:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getBuilding(v);
                break;
            case R.id.tv_unit_number:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getUnit(v);
                break;
            case R.id.tv_unit_floor:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getFloor(v);
                break;
            case R.id.tv_room_number:
                progressDialogManager.showDialog(QUERY, this, "", null, 5000);
                getHouse(v);
                break;
            case R.id.tv_code:
                showCountDownTimer();
                getVerifyCode();
                break;
        }
    }

    private void showCountDownTimer() {
        tvVerifyCode.setClickable(false);
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                tvVerifyCode.setText(value + "s");
            }

            @Override
            public void onFinish() {
                tvVerifyCode.setClickable(true);
                tvVerifyCode.setText(getString(R.string.Login_GetToken));
            }
        };
        countDownTimer.start();
    }

    private String getInfos(int index) {
        String result = null;
        switch (tag) {
            case "province":
                result = provinceId = provinceList.get(index).getProvinceId();
                tvProvince.setText(provinceList.get(index).getProvinceName());
                break;
            case "city":
                result = cityId = cityList.get(index).getCityId();
                tvCity.setText(cityList.get(index).getCityName());
                break;
            case "cityDistrict":
                result = cityDistrictId = cityDistrictList.get(index).getDistrictId();
                tvCityDistrict.setText(cityDistrictList.get(index).getDistrictName());
                break;
            case "community":
                result = communityId = communityList.get(index).getCommunityId();
                tvCommunity.setText(communityList.get(index).getCommunityName());
                break;
            case "district":
                result = districtId = districtList.get(index);
                tvCommunityNumber.setText(districtList.get(index));
                break;
            case "building":
                result = buildingId = buildingList.get(index);
                tvBuildingNumber.setText(buildingList.get(index));
                break;
            case "unit":
                result = unitId = unitList.get(index);
                tvUnitNumber.setText(unitList.get(index));
                break;
            case "floor":
                result = floorId = floorList.get(index);
                tvUnitFloor.setText(floorList.get(index));
                break;
            case "house":
                result = houseId = houseList.get(index);
                tvRoomNumber.setText(houseList.get(index));
                break;

        }
        return result;
    }

    private void getProvinces(final View v) {
        tag = "province";
        deviceApiUnit.getEntranceGuardProvince(new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardProvinceBean>() {
            @Override
            public void onSuccess(EntranceGuardProvinceBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    provinceList = bean.getProvinceInformations();
                    List<String> list = new ArrayList<>();
                    for (EntranceGuardProvinceBean.ProvinceInformationsBean provinceBean :
                            provinceList) {
                        list.add(provinceBean.getProvinceName());
                    }
                    if (list.size() > 0) {
                        bottomMenu.setData((ArrayList) list);
                        bottomMenu.show(v);
                    }

                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getCity(final View v) {
        tag = "city";
        deviceApiUnit.getEntranceGuardCity(provinceId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardCityBean>() {
            @Override
            public void onSuccess(EntranceGuardCityBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    cityList = bean.getCityInformations();
                    List<String> list = new ArrayList<>();
                    for (EntranceGuardCityBean.CityInformationsBean cityBean :
                            cityList) {
                        list.add(cityBean.getCityName());
                    }
                    if (list.size() > 0) {
                        bottomMenu.setData((ArrayList) list);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getCityDistrict(final View v) {
        tag = "cityDistrict";
        deviceApiUnit.getEntranceGuardCityDistrict(provinceId, cityId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardCityDistrictBean>() {
            @Override
            public void onSuccess(EntranceGuardCityDistrictBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    cityDistrictList = bean.getDistrictInformations();
                    List<String> list = new ArrayList<>();
                    for (EntranceGuardCityDistrictBean.DistrictInformationsBean cityDistrictBean :
                            cityDistrictList) {
                        list.add(cityDistrictBean.getDistrictName());
                    }
                    if (list.size() > 0) {
                        bottomMenu.setData((ArrayList) list);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getCommunity(final View v) {
        tag = "community";
        deviceApiUnit.getEntranceGuardCommunity(provinceId, cityId, cityDistrictId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardCommunityBean>() {
            @Override
            public void onSuccess(EntranceGuardCommunityBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    communityList = bean.getCommunityInformations();
                    List<String> list = new ArrayList<>();
                    for (EntranceGuardCommunityBean.CommunityInformationsBean communityBean :
                            communityList) {
                        list.add(communityBean.getCommunityName());
                    }
                    if (list.size() > 0) {
                        bottomMenu.setData((ArrayList) list);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getDistrict(final View v) {
        tag = "district";
        deviceApiUnit.getEntranceGuardDistrict(communityId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardDistrictBean>() {
            @Override
            public void onSuccess(EntranceGuardDistrictBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    districtList = bean.getDistrictInformations();
                    if (districtList.size() > 0) {
                        bottomMenu.setData((ArrayList) districtList);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getBuilding(final View v) {
        tag = "building";
        deviceApiUnit.getEntranceGuardBuilding(communityId, districtId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardBuildingBean>() {
            @Override
            public void onSuccess(EntranceGuardBuildingBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    buildingList = bean.getBuildingInformations();
                    if (buildingList.size() > 0) {
                        bottomMenu.setData((ArrayList) buildingList);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getUnit(final View v) {
        tag = "unit";
        deviceApiUnit.getEntranceGuardUnit(communityId, districtId, buildingId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardUnitBean>() {
            @Override
            public void onSuccess(EntranceGuardUnitBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    unitList = bean.getUnitInformations();
                    if (unitList.size() > 0) {
                        bottomMenu.setData((ArrayList) unitList);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getFloor(final View v) {
        tag = "floor";
        deviceApiUnit.getEntranceGuardFloor(communityId, districtId, buildingId, unitId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardFloorBean>() {
            @Override
            public void onSuccess(EntranceGuardFloorBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    floorList = bean.getFloorInformations();
                    if (floorList.size() > 0) {
                        bottomMenu.setData((ArrayList) floorList);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void getHouse(final View v) {
        tag = "house";
        deviceApiUnit.getEntranceGuardHouse(communityId, districtId, buildingId, unitId, floorId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardHouseBean>() {
            @Override
            public void onSuccess(EntranceGuardHouseBean bean) {
                progressDialogManager.dimissDialog(QUERY, 0);
                if (bean != null) {
                    houseList = bean.getHouseInformations();
                    if (houseList.size() > 0) {
                        bottomMenu.setData((ArrayList) houseList);
                        bottomMenu.show(v);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

    private void checkBind() {
        ICamCloudApiUnit iCamCloudApiUnit;
        iCamCloudApiUnit = new ICamCloudApiUnit(this);
        iCamCloudApiUnit.doCheckBind(deviceId, "CG27", null, null, new ICamCloudApiUnit.IcamApiCommonListener<IcamCloudCheckBindBean>() {
            @Override
            public void onSuccess(IcamCloudCheckBindBean bean) {
                if (bean.boundRelation == 0) {
                    Log.i(TAG, "未绑定");
                    deviceApiUnit.doBindDevice(deviceId, "", "CG27", new DeviceApiUnit.DeviceApiCommonListener() {
                        @Override
                        public void onSuccess(Object bean) {
                            Log.i(TAG, "罗格朗门禁绑定成功");
                            AddCG27SuccessActivity.start(AddCG27DetailActivity.this, deviceId);
                            finish();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.show(msg);

                        }
                    });
                } else if (bean.boundRelation == 1 || bean.boundRelation == 2) {
                    Log.i(TAG, "已绑定");
                    DeviceAlreadyBindActivity.start(AddCG27DetailActivity.this, deviceId, bean.boundUser, bean.boundRelation);
                    finish();
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void getDeviceInfo() {
        deviceApiUnit.getEntranceGuardDeviceInfo(communityId, districtId, buildingId, unitId, floorId, houseId, new DeviceApiUnit.DeviceApiCommonListener<EntranceGuardDeviceInfoBean>() {
            @Override
            public void onSuccess(EntranceGuardDeviceInfoBean bean) {
                if (bean != null) {
                    uc = bean.getDeviceInformations().get(0).getUc();
                    deviceId = communityId + uc;
                    checkBind();
                }

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void getVerifyCode() {
        tag = "code";
        deviceApiUnit.getEntranceGuardVerifyCode(communityId, "1", "1", "1", "1", "1", new DeviceApiUnit.DeviceApiCommonListener<Object>() {
//            @Override
//            public void onSuccess(EntranceGuardCodeBean bean) {
//                if (bean != null) {
//                    ToastUtil.show(R.string.addDevice_CG27_Verification_code);
//                    verifyCode = bean.getCode();
//                }
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                ToastUtil.show(msg);
//            }

            @Override
            public void onSuccess(Object bean) {
                ToastUtil.show(R.string.addDevice_CG27_Verification_code);
            }

            @Override
            public void onFail(int code, String msg) {
                ToastUtil.show(msg);
            }
        });
    }

}
