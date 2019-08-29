package cc.wulian.smarthomev6.main.device;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.smarthomev6.main.h5.H5BridgeActivity;
import cc.wulian.smarthomev6.main.h5.WVJBWebViewClient;
import cc.wulian.smarthomev6.support.core.apiunit.DeviceApiUnit;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;

public class NoviceGuideActivity extends H5BridgeActivity {
    private static final String VIRTUAL_GW_ID = "000000000000";
    private boolean isShowVirtualGw;
    private boolean isShowBindGw;
    private DeviceApiUnit deviceApiUnit;

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected void registerHandler() {
        super.registerHandler();
        mWebViewClient.registerHandler("noviceGuide", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                verifyVirtualGwStatus();
                verifyBindGwStatus();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("isShowVirtualGw", isShowBindGw);
                    jsonObject.put("isShowBindGw", isShowBindGw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.callback(jsonObject);
            }
        });
    }

    private void verifyVirtualGwStatus() {
        deviceApiUnit = new DeviceApiUnit(this);
        deviceApiUnit.getExperienceGatewayStatus(VIRTUAL_GW_ID, new DeviceApiUnit.DeviceApiCommonListener() {
            @Override
            public void onSuccess(Object bean) {
                String jsonData = bean.toString();
                String data = null;
                try {
                    org.json.JSONObject object = new org.json.JSONObject(jsonData);
                    data = object.optString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals("1", data)) {
                    deviceApiUnit.doGetAllDevice(new DeviceApiUnit.DeviceApiCommonListener<List<DeviceBean>>() {
                        @Override
                        public void onSuccess(List<DeviceBean> deviceBeanList) {
                            List<DeviceBean> allGateway = new ArrayList<>();
                            List<DeviceBean> gateWayList = new ArrayList<>();
                            for (DeviceBean deviceBean : deviceBeanList) {
                                if (!deviceBean.isShared()) {
                                    gateWayList.add(deviceBean);
                                }
                                allGateway.add(deviceBean);
                            }
                            if (allGateway.isEmpty() || allGateway.size() == 0) {
                                isShowVirtualGw = true;
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            Log.i(TAG, "onFail: " + msg);
                        }
                    });
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void verifyBindGwStatus() {
        if (TextUtils.isEmpty(preference.getCurrentGatewayID())) {
            isShowBindGw = true;
        } else {
            isShowBindGw = false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
