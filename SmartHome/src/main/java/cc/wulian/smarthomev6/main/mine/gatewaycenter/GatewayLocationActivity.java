package cc.wulian.smarthomev6.main.mine.gatewaycenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.BaseTitleActivity;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.device.dreamFlower.setting.GatewayCityActivity;
import cc.wulian.smarthomev6.main.device.dreamFlower.setting.GatewayProvinceActivity;
import cc.wulian.smarthomev6.support.core.mqtt.MQTTManager;
import cc.wulian.smarthomev6.support.core.mqtt.bean.GatewayInfoBean;
import cc.wulian.smarthomev6.support.event.GatewayInfoEvent;
import cc.wulian.smarthomev6.support.utils.ToastUtil;
import cc.wulian.smarthomev6.support.utils.WLog;

/**
 * created by huxc  on 2017/12/15.
 * func：梦想之花网关地理位置(仅限于梦想之花)
 * email: hxc242313@qq.com
 */

public class GatewayLocationActivity extends BaseTitleActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PROVINCE = 1;
    private static final int REQUEST_CODE_CITY = 2;

    private RelativeLayout itemProvince;
    private RelativeLayout itemCountry;
    private RelativeLayout itemCity;
    private TextView tvProvince;
    private TextView tvCityHint;
    private TextView tvCity;

    private GatewayInfoBean gatewayInfoBean;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_location, true);
        EventBus.getDefault().register(this);
    }

    //由于暂时只支持中国，所以没有做国际化
    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle("位置设置");
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        itemCountry.setOnClickListener(this);
        itemProvince.setOnClickListener(this);
        itemCity.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        itemCity = (RelativeLayout) findViewById(R.id.item_city);
        itemCountry = (RelativeLayout) findViewById(R.id.item_country);
        itemProvince = (RelativeLayout) findViewById(R.id.item_province);
        tvCityHint = (TextView) findViewById(R.id.tv_city);
        tvCity = (TextView) findViewById(R.id.tv_city_name);
        tvProvince = (TextView) findViewById(R.id.tv_province_name);
    }

    @Override
    protected void initData() {
        super.initData();
        if (!TextUtils.isEmpty(preference.getCurrentGatewayInfo())) {
            gatewayInfoBean = JSON.parseObject(preference.getCurrentGatewayInfo(), GatewayInfoBean.class);
           if(!TextUtils.isEmpty(gatewayInfoBean.gwLocation)){
               tvProvince.setText(gatewayInfoBean.gwLocation.split(",")[0]);
               tvCity.setText(gatewayInfoBean.gwLocation.split(",")[1]);
           }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item_province:
                Intent it = new Intent(this, GatewayProvinceActivity.class);
                if (!TextUtils.isEmpty(tvProvince.getText())) {
                    it.putExtra("province", tvProvince.getText());
                }
                startActivityForResult(it, REQUEST_CODE_PROVINCE);
                break;
            case R.id.item_city:
                if (TextUtils.isEmpty(tvProvince.getText())) {
                    setHintState(false);
                } else {
                    setHintState(true);
                    Intent it2 = new Intent(this, GatewayCityActivity.class);
                    it2.putExtra("province", tvProvince.getText());
                    if (!TextUtils.isEmpty(tvCity.getText())) {
                        it2.putExtra("city", tvCity.getText());
                    }
                    startActivityForResult(it2, REQUEST_CODE_CITY);
                }
                break;
        }
    }

    /**
     * 设置城市字体颜色和是否点击事件
     * @param isEnable
     */
    private void setHintState(boolean isEnable) {
        if (isEnable) {
            itemCity.setEnabled(isEnable);
            tvCityHint.setTextColor(getResources().getColor(R.color.black));
        } else {
            itemCity.setEnabled(isEnable);
            tvCityHint.setTextColor(getResources().getColor(R.color.grey));
        }
    }


    /**
     * 设置梦想之花地理位置
     * @param cityCode
     */
    private void setGatewayLocation(String cityCode) {
        final JSONObject object = new JSONObject();
        object.put("cmd", "512");
        object.put("gwID", gatewayInfoBean.gwID);
        object.put("mode", 1);//模式
        object.put("cityCode", cityCode);//城市city
        object.put("appID", gatewayInfoBean.appID);
        object.put("gwLocation", tvProvince.getText()+","+tvCity.getText());
        MainApplication.getApplication()
                .getMqttManager()
                .publishEncryptedMessage(object.toString(), MQTTManager.MODE_GATEWAY_FIRST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PROVINCE:
                    tvProvince.setText(data.getStringExtra("province"));
                    setHintState(true);
                    break;
                case REQUEST_CODE_CITY:
                    tvCity.setText(data.getStringExtra("city"));
                    setGatewayLocation(data.getStringExtra("cityCode"));
                    break;
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGatewayInfoChangedEvent(GatewayInfoEvent event) {
        if (event.bean != null) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
