package cc.wulian.smarthomev6.main.device.camera_lc.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcCustomTimeBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDetectionPlanBean;
import cc.wulian.smarthomev6.support.tools.skin.SkinManager;
import cc.wulian.smarthomev6.support.tools.skin.SkinResouceKey;
import cc.wulian.smarthomev6.support.utils.LogUtil;
import cc.wulian.smarthomev6.support.utils.ToastUtil;

public class LcProtectTimeActivity extends BaseTitleActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ImageView ivAlldayYes;
    private LinearLayout llAllday;
    private ImageView ivDayYes;
    private LinearLayout llDay;
    private ImageView ivNightYes;
    private LinearLayout llNight;
    private ImageView ivUserDefine;
    private LinearLayout llUserDefined;
    private RelativeLayout rlWorkday;
    private CheckBox cbWorkday;
    private Button btnSure;
    private HashMap<String, List<LcCustomTimeBean>> dateMap;

    private String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final String WORKDAY = "Monday,Tuesday,Wednesday,Thursday,Friday";
    private static final String EVERYDAY = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday";

    private static final String ALLDAY = "0";
    private static final String DAY = "1";
    private static final String NIGHT = "2";
    private static final String CUSTOM = "3";

    private String protectTimeType = ALLDAY;
    private String ruleType;
    private String deviceId;
    private String channelId;
    private boolean isSelectWorkday;
    private DeviceApiUnit deviceApiUnit;


    public static void start(Context context, String deviceId, String channelId) {
        Intent it = new Intent(context, LcProtectTimeActivity.class);
        it.putExtra("deviceId", deviceId);
        it.putExtra("channelId", channelId);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc_protect_time, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.deviceLC_Protection_time));
    }

    @Override
    protected void initView() {
        super.initView();
        ivAlldayYes = (ImageView) findViewById(R.id.iv_allday_yes);
        ivDayYes = (ImageView) findViewById(R.id.iv_day_yes);
        ivNightYes = (ImageView) findViewById(R.id.iv_night_yes);
        ivUserDefine = (ImageView) findViewById(R.id.iv_user_define);
        llAllday = (LinearLayout) findViewById(R.id.ll_allday);
        llDay = (LinearLayout) findViewById(R.id.ll_day);
        llNight = (LinearLayout) findViewById(R.id.ll_night);
        llUserDefined = (LinearLayout) findViewById(R.id.ll_user_defined);
        rlWorkday = (RelativeLayout) findViewById(R.id.rl_workday);
        cbWorkday = (CheckBox) findViewById(R.id.cb_workday);
        btnSure = (Button) findViewById(R.id.btn_sure);
    }

    @Override
    protected void updateSkin() {
        super.updateSkin();
        SkinManager skinManager = MainApplication.getApplication().getSkinManager();
        skinManager.setBackground(btnSure, SkinResouceKey.BITMAP_BUTTON_BG_S);
        skinManager.setTextColor(btnSure, SkinResouceKey.COLOR_BUTTON_TEXT);
    }

    @Override
    protected void initData() {
        super.initData();
        deviceApiUnit = new DeviceApiUnit(this);
        deviceId = getIntent().getStringExtra("deviceId");
        channelId = getIntent().getStringExtra("channelId");
        getDetectionPlan();
    }


    @Override
    protected void initListeners() {
        super.initListeners();
        ivAlldayYes.setOnClickListener(this);
        llAllday.setOnClickListener(this);
        ivDayYes.setOnClickListener(this);
        llDay.setOnClickListener(this);
        ivNightYes.setOnClickListener(this);
        llNight.setOnClickListener(this);
        ivUserDefine.setOnClickListener(this);
        llUserDefined.setOnClickListener(this);
        cbWorkday.setOnCheckedChangeListener(this);
        btnSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_allday:
                protectTimeType = ALLDAY;
                selectProtectTime();
                rlWorkday.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_day:
                protectTimeType = DAY;
                selectProtectTime();
                rlWorkday.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_night:
                protectTimeType = NIGHT;
                selectProtectTime();
                rlWorkday.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_user_defined:
                startActivityForResult(new Intent(this, LcCustomProtectTimeActivity.class)
                        .putExtra("dateMap", dateMap)
                        .putExtra("ruleType", ruleType), 0);
                break;
            case R.id.btn_sure:
                setProtectTime();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            protectTimeType = CUSTOM;
            selectProtectTime();
            rlWorkday.setVisibility(View.GONE);
            dateMap = (HashMap<String, List<LcCustomTimeBean>>) data.getSerializableExtra("dataMap");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_workday:
                if (isChecked) {
                    isSelectWorkday = true;
                } else {
                    isSelectWorkday = false;
                }
                break;
        }

    }

    private void getDetectionPlan() {
        deviceApiUnit.getDeviceDetectionPlan(deviceId, channelId, new DeviceApiUnit.DeviceApiCommonListener<LcDetectionPlanBean>() {
            @Override
            public void onSuccess(LcDetectionPlanBean bean) {
                if (bean != null && bean.getRuleType() != null) {
                    protectTimeType = bean.getRuleType();
                    ruleType = bean.getRuleType();
                    if (!TextUtils.equals(protectTimeType, CUSTOM)) {
                        rlWorkday.setVisibility(View.VISIBLE);
                        isSelectWorkday = (bean.getRules().size() == 5);
                        if (isSelectWorkday) {
                            cbWorkday.setChecked(true);
                        } else {
                            cbWorkday.setChecked(false);
                        }
                    } else {
                        rlWorkday.setVisibility(View.GONE);
                        parserCustomTime(bean);
                    }
                }
                selectProtectTime();
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }


    private void parserCustomTime(LcDetectionPlanBean bean) {
        dateMap = new HashMap<>();

        for (LcDetectionPlanBean.RulesBean rulesBean : bean.getRules()
                ) {
            String key = rulesBean.getPeriod();
            List<LcCustomTimeBean> dayList = dateMap.get(key);
            if (dayList == null) {
                dayList = new ArrayList<>();
            }
            dayList.add(new LcCustomTimeBean(rulesBean.getBeginTime() + "--" + rulesBean.getEndTime(), false));
            dateMap.put(key, dayList);
        }
    }

    private void selectProtectTime() {
        llAllday.setBackgroundResource(R.drawable.bg_timeset_normal);
        llDay.setBackgroundResource(R.drawable.bg_timeset_normal);
        llNight.setBackgroundResource(R.drawable.bg_timeset_normal);
        llUserDefined.setBackgroundResource(R.drawable.bg_timeset_normal);

        ivAlldayYes.setVisibility(View.GONE);
        ivDayYes.setVisibility(View.GONE);
        ivNightYes.setVisibility(View.GONE);
        ivUserDefine.setVisibility(View.GONE);

        switch (protectTimeType) {
            case ALLDAY:
                ivAlldayYes.setVisibility(View.VISIBLE);
                break;
            case DAY:
                ivDayYes.setVisibility(View.VISIBLE);
                break;
            case NIGHT:
                ivNightYes.setVisibility(View.VISIBLE);
                break;
            case CUSTOM:
                ivUserDefine.setVisibility(View.VISIBLE);
                break;
        }
    }

    private JSONArray getProtectTimeData() {
        JSONArray jsonArray = new JSONArray();
        switch (protectTimeType) {
            case ALLDAY:
                jsonArray = getCommonProtectTime("00:00:00", "23:59:59", isSelectWorkday);
                break;
            case DAY:
                jsonArray = getCommonProtectTime("08:00:00", "20:00:00", isSelectWorkday);
                break;
            case NIGHT:
                jsonArray = getCommonProtectTime("20:00:00", "08:00:00", isSelectWorkday);
                break;
            case CUSTOM:
                jsonArray = getCustomProtectTime();
                break;
        }
        return jsonArray;
    }

    private JSONArray getCommonProtectTime(String beginTime, String endTme, boolean isSelectWorkday) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("beginTime", beginTime);
            jsonObject.put("endTime", endTme);
            if (isSelectWorkday) {
                jsonObject.put("period", WORKDAY);
            } else {
                jsonObject.put("period", EVERYDAY);
            }
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    private JSONArray getCustomProtectTime() {
        JSONArray jsonArray = new JSONArray();
        for (String day :
                week) {
            List<LcCustomTimeBean> list = dateMap.get(day);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String beginTime = list.get(i).getTime().split("--")[0];
                        String endTme = list.get(i).getTime().split("--")[1];
                        jsonObject.put("beginTime", beginTime);
                        jsonObject.put("endTime", endTme);
                        jsonObject.put("period", day);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return jsonArray;
    }

    private void setProtectTime() {
        progressDialogManager.showDialog("SET", LcProtectTimeActivity.this, "", null, 5000);
        deviceApiUnit.setDeviceDetectionPlan(deviceId, channelId, protectTimeType, getProtectTimeData(), new DeviceApiUnit.DeviceApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog("SET", 0);
                Log.i(LogUtil.LC_TAG, "设置动检计划成功 ");
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog("SET", 0);
                ToastUtil.show(msg);
            }
        });
    }

}
