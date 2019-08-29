package cc.wulian.smarthomev6.support.core.apiunit;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.UserPushInfo;
import cc.wulian.smarthomev6.entity.WishActivateBean;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AreaBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.AreaListTemp;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CheckV6SupportBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ChildDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CylincamChildDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.CylincamChildDeviceDataBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceIsPushBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListBeanAll;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceListTemp;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceRelationListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.DeviceSupportV6Bean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayBackDataListBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewayLastVersionBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.GatewaySupportDeviceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDetectionPlanBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDetectionSwitchBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcLocalRecordBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcRevertStatusBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.LcSimpleInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.ResponseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.WishBgmIdBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.icam.ICamInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardAddressBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardBuildingBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCityBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCityDistrictBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCommunityBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardCommunityInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardDeviceInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardDistrictBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardFloorBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHostInfo;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardHouseBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardPropertyInfoBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardProvinceBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardRegisterBean;
import cc.wulian.smarthomev6.support.core.apiunit.bean.legrand.EntranceGuardUnitBean;
import cc.wulian.smarthomev6.support.core.cipher.CipherUtil;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;
import cc.wulian.smarthomev6.support.core.device.GatewayDataEntity;
import cc.wulian.smarthomev6.support.core.device.GatewayManager;
import cc.wulian.smarthomev6.support.event.GatewaySoftwareUpdateEvent;
import cc.wulian.smarthomev6.support.tools.Preference;
import cc.wulian.smarthomev6.support.utils.MD5Util;
import cc.wulian.smarthomev6.support.utils.StringUtil;
import cc.wulian.smarthomev6.support.utils.WLog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by mamengchao on 2017/3/2 0002.
 * Tips:用户设备关系
 */

public class DeviceApiUnit {
    private Context context;

    public DeviceApiUnit(Context context) {
        this.context = context;
    }

    public interface DeviceApiCommonListener<T> {
        void onSuccess(T bean);

        void onFail(int code, String msg);
    }

    /**
     * 绑定设备
     * {
     * “token”: “xxxxxxxxxxx”,
     * “uId”:”xxxxxx”,
     * “deviceId”:”xxxxxxxx”,
     * “devicePasswd”:“xxxxxxxxx”,
     * “deviceType”:”xxxx”
     * }
     */
    public void doBindDevice(String deviceId, String devicePasswd, String deviceType, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("deviceType", deviceType);
            if (!TextUtils.isEmpty(devicePasswd)) {
                if (DeviceInfoDictionary.isLcCamera(deviceType)) {
                    msgContentJson.put("devicePasswd", devicePasswd);
                } else {
                    msgContentJson.put("devicePasswd", MD5Util.encrypt(devicePasswd));
                }
            } else {
                msgContentJson.put("devicePasswd", null);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        WLog.e("DeviceApiUnit: ", responseBean.toString() + " " + resultDesc);
                        if (responseBean.isSuccess()) {
                            // 绑定网关的数量 + 1
                            Preference.getPreferences().saveBindGatewayCount(Preference.getPreferences().getBindGatewayCount() + 1);
                            listener.onSuccess(null);
                        } else {
                            if (TextUtils.isEmpty(resultCode)) {
                                listener.onFail(-1, resultDesc);
                            } else {
                                listener.onFail(Integer.valueOf(resultCode), resultDesc);
                            }
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("DeviceApiUnit:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 解绑设备
     */
    public void doUnBindDevice(final String deviceId, final DeviceApiCommonListener listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" +
                deviceId + "?token=" + ApiConstant.getAppToken();
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("UnBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            // 绑定网关的数量 - 1
                            Preference.getPreferences().saveBindGatewayCount(Preference.getPreferences().getBindGatewayCount() - 1);
                            Preference.getPreferences().removeGatewayFromList(deviceId);
                            MainApplication.getApplication().getDeviceCache().clearDatabaseCache(deviceId);
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("UnBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备信息查询(查询子设备用，查询网关信息请调用doGetChildDevice)
     */
    public void doGetDeviceInfo(String deviceId, final DeviceApiCommonListener<ChildDeviceInfoBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken(), true)
                .execute(new EncryptCallBack<ResponseBean<ChildDeviceInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ChildDeviceInfoBean> responseBean, Call call, Response response) {
                        WLog.e("UnBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("UnBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
        if (deviceId.length() == 12) {

        } else {

        }

    }

    /**
     * 设备信息变更(目前只能修改网关信息)
     * {
     * “token”: “xxxxxxxxxxx”,
     * “uId”:”xxxxxx”,
     * “deviceId”:”xxxxxxxx”,
     * “deviceName”:”xxxxxx”
     * }
     */
    public void doUpdateDeviceInfo(String deviceId, String deviceName, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("deviceName", deviceName);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices";

        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("DeviceApiUnit:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("DeviceApiUnit:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 获取所有设备
     * <p>
     * 注意：该接口现在只在体验网关时会调用，体验网关不采取分页的原因时理论上体验网关不会出现数据量很多的情况(2019/04/24)
     */
    public void doGetAllDevice(final DeviceApiCommonListener<List<DeviceBean>> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<DeviceListTemp>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceListTemp> responseBean, Call call, Response response) {
                        WLog.e("GetAllDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            GatewayManager gatewayManager = new GatewayManager();
                            DeviceListTemp deviceBeanList = responseBean.data;
                            List<DeviceBean> deviceList = new ArrayList<>();

                            List<DeviceBean> bindDeviceList = deviceBeanList.boundDevices;

                            for (DeviceBean deviceBean : bindDeviceList) {
                                if (deviceBean.isGateway()) {
                                    deviceBean.setName((DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName())));
                                    deviceList.add(deviceBean);
                                    gatewayManager.saveGatewayInfo(deviceBean);
                                }
                            }

                            List<DeviceBean> authDeviceList = deviceBeanList.grantDevices;
                            for (DeviceBean deviceBean : authDeviceList) {
                                if (deviceBean.isGateway()) {
                                    deviceBean.setName((DeviceInfoDictionary.getNameByTypeAndName(deviceBean.getType(), deviceBean.getName())));
                                    deviceList.add(deviceBean);
                                    gatewayManager.saveGatewayInfo(deviceBean);
                                }
                            }

                            // 保存 绑定 的 网关的 数量
                            if (deviceList.isEmpty()) {
                                Preference.getPreferences().saveBindGatewayCount(0);
                            } else {
                                Preference.getPreferences().saveBindGatewayCount(deviceList.size());
                            }
                            List<String> gatewayIdList = new ArrayList<>();
                            for (DeviceBean deviceBean : deviceList) {
                                gatewayIdList.add(deviceBean.deviceId);
                            }
                            Preference.getPreferences().saveCurrentGatewayList(gatewayIdList);

                            listener.onSuccess(deviceList);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetAllDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取所有授权设备
     */
    public void doGetAllAuthDevice(final DeviceApiCommonListener<List<DeviceBean>> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<DeviceListTemp>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceListTemp> responseBean, Call call, Response response) {
                        WLog.e("GetAllDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        List<DeviceBean> deviceBeanList = responseBean.data.grantDevices;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(deviceBeanList);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetAllDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取所有wifi设备
     */
    public void doGetAllWifiDevice(final DeviceApiCommonListener<List<DeviceBean>> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<DeviceListTemp>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceListTemp> responseBean, Call call, Response response) {
                        WLog.e("GetWifiDevice:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            List<DeviceBean> deviceBeanList = new ArrayList<>();
                            for (DeviceBean deviceBean : responseBean.data.boundDevices) {
                                //排除移康猫眼，移康猫眼设备列表使用sdk获取
                                if (TextUtils.isEmpty(deviceBean.getType())
                                        || !(deviceBean.getType().startsWith("GW") ||
                                        TextUtils.equals(deviceBean.getType(), "CMICY1"))) {
//                                    deviceBean.state = "1";//调试
                                    deviceBeanList.add(deviceBean);
                                }
                            }
                            for (DeviceBean deviceBean : responseBean.data.grantDevices) {
                                if (TextUtils.isEmpty(deviceBean.getType())
                                        || !(deviceBean.getType().startsWith("GW") ||
                                        TextUtils.equals(deviceBean.getType(), "CMICY1"))) {
                                    deviceBeanList.add(deviceBean);
                                }
                            }
                            listener.onSuccess(deviceBeanList);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetWifiDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取子设备信息
     */
    public void doGetChildDeviceInfo(String deviceId, final DeviceApiCommonListener<ChildDeviceInfoBean> listener) {

        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<ChildDeviceInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ChildDeviceInfoBean> responseBean, Call call, Response response) {
                        WLog.e("GetAllDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetAllDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 新增区域
     * {
     * “token”: “xxxxxxxxxxx”,
     * “groupName”:”xxxx”
     * }
     */
    public void doAddArea(String groupName, final DeviceApiCommonListener<AreaBean> listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("groupName", groupName);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<AreaBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AreaBean> responseBean, Call call, Response response) {
                        WLog.e("doBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取所有区域信息
     */
    public void doGetAllArea(final DeviceApiCommonListener<List<AreaBean>> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups";

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<AreaListTemp>>() {
                    @Override
                    public void onSuccess(ResponseBean<AreaListTemp> responseBean, Call call, Response response) {
                        WLog.e("doGetAllArea:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            if (responseBean.data.groups == null) {
                                listener.onFail(-1, "No Groups");
                            }
                            List<AreaBean> areaBeanList = responseBean.data.groups;
                            listener.onSuccess(areaBeanList);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetAllArea:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取区域详情
     */
    public void doGetAreaDetail(String groupId, final DeviceApiCommonListener<AreaBean> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups/" + groupId;

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<AreaBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<AreaBean> responseBean, Call call, Response response) {
                        WLog.e("doGetAllArea:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetAllArea:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 区域信息更新
     * {
     * “token”: “xxxxxxxxxxx”,
     * “groupId”:”xxxx”
     * “groupName”:”xxxx”
     * }
     */
    public void doUpdateArea(String groupId, String groupName, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("groupId", groupId);
            msgContentJson.put("groupName", groupName);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups";
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 删除区域
     * {
     * “token”: “xxxxxxxxxxx”,
     * “groupId”:”xxxx”
     * }
     */
    public void doDeleteArea(String groupId, final DeviceApiCommonListener listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups/" + groupId + "?token=" + ApiConstant.getAppToken();
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.e("doBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备绑定区域
     * {
     * “token”: “xxxxxxxxxxx”,
     * “groupId”:”xxxx”
     * “deviceId”:”xxxx”
     * }
     */
    public void doBindArea(String groupId, String deviceId, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("groupId", groupId);
            msgContentJson.put("deviceId", deviceId);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups" + "/devices";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备解绑区域
     * {
     * “token”: “xxxxxxxxxxx”,
     * “groupId”:”xxxx”
     * “deviceId”:”xxxx”
     * }
     */
    public void doUnBindArea(String groupId, String deviceId, final DeviceApiCommonListener listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/groups/" + groupId + "/devices/"
                + deviceId + "?token=" + ApiConstant.getAppToken();
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doBindDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 校验网关密码
     * {
     * “token”: “xxxxxxxxxxx”,
     * “deviceId”:”xxxxxxxx”,
     * “devicePassword”:“xxxxxxxxx”
     * }
     */
    @Deprecated
    public void doVerifyGwPwd(String deviceId, String pwd, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("devicePassword", MD5Util.encrypt(pwd));
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices" + "/password-verify";
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doVerifyGwPwd:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doVerifyGwPwd:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 用户局域网登录状态维护
     * {
     * “token”:”xxxxxx”,
     * “deviceId”:”xxxxxxxxx”,
     * “isPush”:”xxxxxxx”
     * }
     */
    public void doIsPush(boolean ispush, String deviceId, final DeviceApiCommonListener listener) {

        String pushType = null;
        if (ispush) {
            pushType = "1";
        } else {
            pushType = "0";
        }
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("isPush", pushType);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/push";
        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doIsPush:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doIsPush:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询是否推送
     * {
     * “token”:”xxxxxx”,
     * “deviceId”:”xxxxxxxxx”,
     * “isPush”:”xxxxxxx”
     * }
     */
    public void doGetIsPush(String deviceId, final DeviceApiCommonListener<DeviceIsPushBean> listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/push";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<DeviceIsPushBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceIsPushBean> responseBean, Call call, Response response) {
                        WLog.i("doGetIsPush:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetIsPush:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void cancelRequest() {
        OkGo.getInstance().cancelTag(this);
    }

    public void doBridgeGet(String sectionUrl, Map<String, String> params, final DeviceApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + sectionUrl;

        if (params == null) {
            params = new HashMap<>();
        }

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params, time);
        OkGo.get(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o);
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    public void doBridgePost(String sectionUrl, JSONObject params, final DeviceApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + sectionUrl;

        String time = System.currentTimeMillis() + "";
        String sign = CipherUtil.getClondApiSign(params.toString(), time);
        OkGo.post(url)
                .tag(this)
                .headers("WL-PARTNER-ID", ApiConstant.APPID)
                .headers("WL-TIMESTAMP", time)
                .headers("WL-SIGN", sign)
                .headers("WL-TID", ApiConstant.getTerminalId())
                .upJson(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o);
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    public void doEncryptBridgeGet(String sectionUrl, Map<String, String> params, final DeviceApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + sectionUrl;

        if (params == null) {
            params = new HashMap<>();
        }
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        JSONObject object = null;
                        try {
                            object = new JSONObject(JSON.toJSONString(responseBean));
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    public void doEncryptBridgePost(String sectionUrl, JSONObject params, final DeviceApiCommonListener listener) {
        String url = ApiConstant.getBaseServer() + sectionUrl;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(params.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        JSONObject object = null;
                        try {
                            object = new JSONObject(JSON.toJSONString(responseBean));
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 设备数据统计
     */
    public void doGetDeviceStatistic(String deviceId, String devType, String dataType, String childDeviceId, String startTime, String endTime, final DeviceApiCommonListener listener) {

        String url = ApiConstant.URL_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/statistics";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("childDeviceId", childDeviceId)
                .params("devType", devType)
                .params("dataType", dataType)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .execute(new MsgContentCallBack() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.i("UnBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备数据统计
     */
    public void doGetBloodPressureHistory(String deviceId, String childDeviceId, String startTime, String endTime, String pageSize, String userNumber, final DeviceApiCommonListener listener) {
        String url = ApiConstant.URL_BLOOD_PRESSURE_DATA_RECORDS + "/" + ApiConstant.getUserID() + "/turgoscope";
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .params("deviceId", deviceId)
                .params("childDeviceId", childDeviceId)
                .params("startTime", startTime)
                .params("endTime", endTime)
                .params("pageSize", pageSize)
                .params("userNumber", userNumber)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o);
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 绑定门锁
     * {
     * "token": "xxxxxxxxx",
     * "deviceId": "cmic0103aea213cd1070",
     * "deviceType": "CMICA2",
     * "targetDeviceid":"76BFBE05004B1200",
     * "targetDevicetype": "70","op",
     * "gatewayId": "50294D123456",
     * "relationType": "1"
     * }
     */

    public void doBindDoorLock(String deviceId, String deviceType, String targetDeviceid, String targetDevicetype, String gatewayId, String relationType, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("deviceType", deviceType);
            msgContentJson.put("targetDeviceid", targetDeviceid);
            msgContentJson.put("targetDevicetype", targetDevicetype);
            msgContentJson.put("gatewayId", gatewayId);
            msgContentJson.put("relationType", relationType);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/device-relation";
        OkGo.post(url).tag(this).upJson(jsonObject).execute(new EncryptCallBack<ResponseBean<Object>>() {
            @Override
            public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                WLog.i("hxc", responseBean.toString());
                WLog.i("hxc", response.body());
                String resultDesc = responseBean.resultDesc;
                String resultCode = responseBean.resultCode;
                listener.onSuccess(responseBean);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                WLog.i("hxc", e.toString());
                String msg = context.getString(R.string.Service_Error);
            }
        });
    }


    /**
     * 获取绑定门锁信息
     *
     * @param deviceId
     * @param targetDeviceid
     */
    public void doGetBindLock(String deviceId, String targetDeviceid, final DeviceApiCommonListener<DeviceRelationListBean> listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/device-relation/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<DeviceRelationListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceRelationListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DeviceApiUnit:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    public void doUnBindDoorLock(String deviceId, String targetDeviceid, final DeviceApiCommonListener listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/device-relation/" + deviceId + "?token=" + ApiConstant.getAppToken() + "&targetDeviceid" + targetDeviceid;
        OkGo.delete(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doBindDevice:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doBindDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 查询用户推送信息
     */
    public void doQueryUserPushSetts(String json, final DeviceApiCommonListener listener) {

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/users-push-info";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("token", ApiConstant.getAppToken());
            paramsMap.put("deviceId", jsonObject.optString("deviceId"));
            if (!TextUtils.isEmpty(jsonObject.optString("messageCode"))) {
                paramsMap.put("messageCode", jsonObject.optString("messageCode"));
            }
            paramsMap.put("gradeType", jsonObject.optString("gradeType"));

            OkGo.get(url)
                    .tag(this)
                    .params(paramsMap)
                    .execute(new MsgContentCallBack() {
                        @Override
                        public void onSuccess(Object o, Call call, Response response) {
                            try {
                                JSONObject object = new JSONObject(o.toString());
                                listener.onSuccess(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFail(-1, e.getMessage());
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            WLog.i("DataApi", "onError: " + response);
                            WLog.i("doQueryUserPushSetts:onError", e.toString());
                            String msg = context.getString(R.string.Service_Error);
                            listener.onFail(-1, msg);
                            super.onError(call, response, e);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询用户推送信息
     *
     * @param gradeType 级别：1用户级别 2设备级别 3消息级别
     */
    public void doQueryDevicePushSetts(String deviceId, String gradeType, String cascadeFlag, final DeviceApiCommonListener listener) {

        final String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/users-push-info";

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("token", ApiConstant.getAppToken());
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("gradeType", gradeType);
        paramsMap.put("cascadeFlag", cascadeFlag);

        OkGo.get(url)
                .tag(this)
                .params(paramsMap)
                .execute(new EncryptCallBack<ResponseBean<UserPushInfo>>() {
                    @Override
                    public void onSuccess(ResponseBean<UserPushInfo> userPushInfoResponseBean, Call call, Response response) {
                        if (userPushInfoResponseBean.isSuccess()) {
                            listener.onSuccess(userPushInfoResponseBean.data);
                        } else {
                            listener.onFail(userPushInfoResponseBean.getResultCode(), userPushInfoResponseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.i("doQueryUserPushSetts:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 更新用户推送信息
     */
    public void doSaveUserPushSetts(String json, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = null;
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson = new JSONObject(json);
            msgContentJson.put("token", ApiConstant.getAppToken());
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/users-push-info";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new MsgContentCallBack() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.i("doSaveUserPushSetts:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 更新用户推送信息
     */
    public void doSaveUserPushSetts(String deviceId, String messageCode, String pushFlag, String pushType, String msgType, String pushTime, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = null;
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson = new JSONObject();
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("messageCode", messageCode);
            msgContentJson.put("pushFlag", pushFlag);
            msgContentJson.put("pushType", pushType);
            msgContentJson.put("msgType", msgType);
            if (!TextUtils.isEmpty(pushTime)) {
                Date date = new Date();
                SimpleDateFormat myFmt = new SimpleDateFormat("z");
                String timeZone = myFmt.format(date).toString();
                msgContentJson.put("time", pushTime);
                msgContentJson.put("timeZone", timeZone);
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/users-push-info";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new MsgContentCallBack() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o.toString());
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("DataApi", "onError: " + response);
                        WLog.i("doSaveUserPushSetts:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备信息查询(查询爱看摄像机设备)
     */
    public void doGetICamDeviceInfo(String deviceId, final DeviceApiCommonListener<ICamInfoBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken(), true)
                .execute(new EncryptCallBack<ResponseBean<ICamInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<ICamInfoBean> responseBean, Call call, Response response) {
                        WLog.i("doGetICamDeviceInfo:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetICamDeviceInfo:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
        if (deviceId.length() == 12) {

        } else {

        }

    }

    /**
     * 设备信息
     */
    public void doGetZigBeeDeviceInfo(String deviceId, final DeviceApiCommonListener<DeviceBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/staticInfo?deviceId=" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken(), true)
                .execute(new EncryptCallBack<ResponseBean<DeviceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceBean> responseBean, Call call, Response response) {
                        WLog.i("doGetICamDeviceInfo:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetICamDeviceInfo:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });

    }


    /**
     * 设备是否V6支持
     *
     * @param deviceId
     * @param listener
     */
    public void doGetIsV6Support(String deviceId, final DeviceApiCommonListener<DeviceSupportV6Bean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        } else if (deviceId.startsWith("AV08") && deviceId.length() > 16) {//AV08开头的id需要截取
            deviceId = deviceId.substring(0, 16);
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/check/deviceId/v6/supportOrNot";
        HashMap<String, String> params = new HashMap<>();
        params.put("deviceId", deviceId);
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<DeviceSupportV6Bean>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceSupportV6Bean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetIsV6Support:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 验证网关密码，并保存当前登录网关
     */
    public void doVerifyGwPwdAndSaveGwId(String deviceId, String devicePassword, String type, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("deviceId", deviceId);
            if (!TextUtils.isEmpty(type)) {
                msgContentJson.put("type", type);
            }
            if (!TextUtils.isEmpty(devicePassword)) {
                msgContentJson.put("devicePassword", MD5Util.encrypt(devicePassword));
            }
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/rememberCurrentDevice/verfyPassword";

        OkGo.put(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doVerifyGwPwdAndSaveGwId:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(null);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doVerifyGwPwdAndSaveGwId:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 保存当前登录网关,不带回调
     */
    public void doSwitchGatewayId(String deviceId) {
        doSwitchGatewayId(deviceId, null);
    }

    /**
     * 保存当前登录网关
     */
    public void doSwitchGatewayId(String deviceId, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("uId", ApiConstant.getUserID());
            msgContentJson.put("deviceId", deviceId);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/device/switch";

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        WLog.i("doSwitchGatewayId:onSuccess", responseBean.toString());
                        if (listener != null) {
                            String resultDesc = responseBean.resultDesc;
                            String resultCode = responseBean.resultCode;
                            if (responseBean.isSuccess()) {
                                listener.onSuccess(null);
                            } else {
                                int code = -1;
                                try {
                                    code = Integer.parseInt(resultCode);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                listener.onFail(code, resultDesc);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doSwitchGatewayId:onError", e.toString());
                        if (listener != null) {
                            String msg = context.getString(R.string.Service_Error);
                            listener.onFail(-1, msg);
                        }
                    }
                });
    }

    /**
     * 获取小物环境信息
     */

    public void doGetEnvironmentInfo(String deviceId, final DeviceApiCommonListener<HashMap> listener) {

        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId + "/getChildDevices";

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<CylincamChildDeviceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CylincamChildDeviceBean> responseBean, Call call, Response response) {
                        WLog.i("GetAllDevice:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            HashMap valueMap = new HashMap();
                            if (responseBean.data.childDevices != null) {
                                for (CylincamChildDeviceBean.ChildDevices device : responseBean.data.childDevices) {
                                    if (device != null && device.endpointDatas != null) {
                                        for (int i = 0; i < device.endpointDatas.size(); i++) {
                                            CylincamChildDeviceDataBean endpointData = device.endpointDatas.get(i);
                                            try {
                                                if (!TextUtils.isEmpty(endpointData.data)) {
                                                    JSONObject json = new JSONObject(endpointData.data);
                                                    JSONArray array = json.getJSONArray("attributes");
                                                    String value = array.getJSONObject(0).getString("attributeValue");
                                                    valueMap.put(device.type + "_" + endpointData.epNum, value);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                            listener.onSuccess(valueMap);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("GetAllDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 设备激活
     */
    public void doActiveDevice(final String deviceId, String activeCode, final String type, final DeviceApiCommonListener listener) {
        JSONObject msgContentJson = null;
        JSONObject jsonObject = new JSONObject();
        try {
            msgContentJson = new JSONObject();
            msgContentJson.put("token", ApiConstant.getAppToken());
            msgContentJson.put("deviceId", deviceId);
            msgContentJson.put("activeCode", activeCode);
            msgContentJson.put("type", type);
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(msgContentJson.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = String.format(ApiConstant.URL_DEVICE_ACTIVE, ApiConstant.getUserID());

        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> o, Call call, Response response) {
                        WLog.i("doActiveDevice", "onSuccess: " + response);
                        if (o.isSuccess()) {
                            listener.onSuccess(null);
                            if ("DD".equals(type)) {
                                MainApplication.getApplication().getActivationCache().activeDevice(deviceId);
                            }
                        } else {
                            try {
                                int i = Integer.parseInt(o.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        WLog.i("DataApi", "onError: " + response);
                        WLog.i("doActiveDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 设备是否V6支持
     *
     * @param deviceId
     * @param listener
     */
    public void doCheckV6Support(String deviceId, final DeviceApiCommonListener<CheckV6SupportBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/device/check/v6/support";

        HashMap<String, String> params = new HashMap<>();
        params.put("deviceId", deviceId);
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<CheckV6SupportBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<CheckV6SupportBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doGetIsV6Support:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取某类型网关最新版本号,用于和当前网关版本进行比较
     */
    public void doGetGatewayVersion(final String gatewayType, final DeviceApiCommonListener<GatewayLastVersionBean> listener) {

        String url = String.format(ApiConstant.URL_GET_GATEWAY_VERSION, ApiConstant.getUserID());

        OkGo.get(url)
                .tag(this)
                .params("type", gatewayType)
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
                .cacheTime(3600000)
                .execute(new EncryptCallBack<ResponseBean<GatewayLastVersionBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<GatewayLastVersionBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onCacheSuccess(ResponseBean<GatewayLastVersionBean> responseBean, Call call) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 判断网关版本
     * 如果网关版本低，则显示网关升级相关View
     */
    public void checkGatewaySoftwareUpdate() {
        Preference preference = Preference.getPreferences();
        if (Preference.ENTER_TYPE_ACCOUNT.equals(preference.getUserEnterType())) {
            GatewayManager gatewayManager = new GatewayManager();
            final String currentGatewayID = preference.getCurrentGatewayID();
            if (!TextUtils.isEmpty(currentGatewayID)) {
                final GatewayDataEntity gatewayDataEntity = gatewayManager.getGatewayInfo(currentGatewayID);
                if (gatewayDataEntity != null) {
                    doGetGatewayVersion(gatewayDataEntity.getType(), new DeviceApiCommonListener<GatewayLastVersionBean>() {
                        @Override
                        public void onSuccess(GatewayLastVersionBean bean) {
                            try {
                                String currentGatewayVersion = gatewayDataEntity.getSoftVer();
                                if (currentGatewayVersion.substring(currentGatewayVersion.length() - 3).compareTo(bean.latestVersion) < 0) {//Fixme 测试时用>=，正常应为 小于号<
                                    //提醒升级网关
                                    GatewaySoftwareUpdateEvent event = new GatewaySoftwareUpdateEvent();
                                    event.status = 1;
                                    event.gatewayID = currentGatewayID;
                                    event.oldVersion = currentGatewayVersion;
                                    event.newVersion = currentGatewayVersion.substring(0, currentGatewayVersion.length() - 3) + bean.latestVersion;
                                    EventBus.getDefault().post(event);
                                } else {
                                    //取消升级网关提醒
                                    GatewaySoftwareUpdateEvent event = new GatewaySoftwareUpdateEvent();
                                    event.status = 0;
                                    event.gatewayID = currentGatewayID;
                                    event.oldVersion = currentGatewayVersion;
                                    event.newVersion = currentGatewayVersion;
                                    EventBus.getDefault().post(event);
                                }
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                        }
                    });
                }
            }
        }
    }


    /**
     * 查询是否激活过
     *
     * @param deviceId
     * @param listener
     */
    public void doGetWishBmgActivateStatus(String deviceId, final DataApiUnit.DataApiCommonListener<WishActivateBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        String url = String.format(ApiConstant.URL_WISH_ACTIVATE, ApiConstant.getUserID());
        OkGo.get(url)
                .tag(this)
                .params("deviceId", deviceId)
                .execute(new EncryptCallBack<ResponseBean<WishActivateBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<WishActivateBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 获取向往背景音乐设备id
     *
     * @param qrInfo
     * @param listener
     */
    public void doGetWishBgmDeviceId(final String qrInfo, final DeviceApiCommonListener<WishBgmIdBean> listener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(CipherUtil.createCloudMessage(qrInfo.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/third/xw/device/reg";
        OkGo.post(url)
                .tag(this)
                .upJson(jsonObject)
                .execute(new EncryptCallBack<ResponseBean<WishBgmIdBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<WishBgmIdBean> responseBean, Call call, Response response) {
                        WLog.e("doGetWishBgmDeviceId:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetWishBgmDeviceId:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 获取海信设备子设备信息
     *
     * @param deviceId
     * @param listener
     */
    public void doGetHisenseChildDevicesInfo(String deviceId, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_HISENSE_CHILD_DEVICE, ApiConstant.getUserID(), deviceId);

        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken())
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            WLog.e("doGetHisenseChildDevicesInfo:onSuccess", responseBean.toString());
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    public void doControlHisDevice(String deviceId, String zone, int value, final DeviceApiCommonListener<Object> listener) {
        JSONObject msgContentJson = null;
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("deviceId", deviceId);
            JSONObject extData = new JSONObject();
            jsonObject1.put("extData", extData);
            JSONObject temperature = new JSONObject();
            temperature.put("zone", zone);
            temperature.put("value", value);
            JSONObject action = new JSONObject();
            action.put("Temperature", temperature);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("action", action);
            jsonObject3.put("endpointNum", 1);
            JSONArray array = new JSONArray();
            array.put(0, jsonObject3);
            jsonObject1.put("endpoint", array);

            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject1.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/ctrl_device" + "?token=" + ApiConstant.getAppToken();
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<WishBgmIdBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<WishBgmIdBean> responseBean, Call call, Response response) {
                        WLog.e("doGetWishBgmDeviceId:onSuccess", responseBean.toString());
                        String resultDesc = responseBean.resultDesc;
                        String resultCode = responseBean.resultCode;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("doGetWishBgmDeviceId:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * APP查询备份数据
     *
     * @param gatewayId
     * @param listener
     */
    public void getGatewayBackupData(String gatewayId, final DeviceApiCommonListener<GatewayBackDataListBean> listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/backup?token=" + ApiConstant.getAppToken() + "&deviceId=" + gatewayId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<GatewayBackDataListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<GatewayBackDataListBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                    }
                });
    }

    /**
     * 通知网关备份接口
     *
     * @param gatewayId
     * @param listener
     */
    public void backupGatewayData(String gatewayId, final DeviceApiCommonListener<ResponseBean> listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/backup";
        JSONObject msgContentJson = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("deviceId", gatewayId);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<GatewayBackDataListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                    }
                });
    }

    /**
     * 通知网关恢复接口
     *
     * @param gatewayId
     * @param listener
     */
    public void recoveryGatewayData(String gatewayId, String bGwID, String bid, final DeviceApiCommonListener<ResponseBean> listener) {
        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/restore";
        JSONObject msgContentJson = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("deviceId", gatewayId);
            jsonObject.put("bGwID", bGwID);
            jsonObject.put("bid", bid);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<GatewayBackDataListBean>>() {
                    @Override
                    public void onSuccess(ResponseBean responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                    }
                });
    }

    public void getExperienceGatewayStatus(String deviceId, final DeviceApiCommonListener listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        int partnerId;
        //其他定制app传1获取体验网关状态不可用
        if (TextUtils.equals("wulian_app", ApiConstant.APPID)) {
            partnerId = 2;
        } else {
            partnerId = 1;
        }
        String url = ApiConstant.URL_EXPERIENCE_GW;
        OkGo.get(url)
                .tag(this)
                .params("deviceId", deviceId)
                .params("partnerId", partnerId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String o, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(o);
                            listener.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取账号下绑定的WiFi设备和网关列表
     *
     * @param pageNum
     * @param pageSize
     * @param listener
     */
    public void getWifiDeviceList(String pageNum, String pageSize, final DeviceApiCommonListener<DeviceListBeanAll> listener) {
        String url = String.format(ApiConstant.URL_WIFI_DEVICE_LIST, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<DeviceListBeanAll>>() {
                    @Override
                    public void onSuccess(ResponseBean<DeviceListBeanAll> responseBean, Call call, Response response) {
                        WLog.e("GetWifiDevice:onSuccess", responseBean.toString());
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.e("GetWifiDevice:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 校验网关是否支持设备（lite网关）
     *
     * @param gatewayType
     * @param deviceType
     * @param listener
     */
    public void checkGatewaySupportDevice(String gatewayType, String deviceType, final DeviceApiCommonListener<GatewaySupportDeviceBean> listener) {
        String url = String.format(ApiConstant.URL_GATEWAY_SUPPORT_DEVICE, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gatewayType", gatewayType);
            jsonObject.put("deviceType", deviceType);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<GatewaySupportDeviceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<GatewaySupportDeviceBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                    }
                });
    }


    /**
     * 校验网关密码
     *
     * @param deviceId
     * @param password
     * @param listener
     */
    public void checkV5GatewayPassword(String deviceId, String password, final DeviceApiCommonListener<ResponseBean> listener) {
        String url = String.format(ApiConstant.URL_V5_GATEWAY_PWD_CHECK, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("password", MD5Util.encrypt(password).toLowerCase());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                    }
                });
    }

    /**
     * 升级网关版本
     *
     * @param deviceId
     * @param model
     * @param listener
     */
    public void updateV5GatewayVersion(String deviceId, String model, final DeviceApiCommonListener<ResponseBean> listener) {
        String url = String.format(ApiConstant.URL_V5_GATEWAY_UPGRADE, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("model", model);
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取网关版本
     *
     * @param deviceId
     * @param listener
     */
    public void getV5GwVersion(String deviceId, final DeviceApiCommonListener<ResponseBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/v5/gw/version?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取设备简略信息
     *
     * @param deviceId
     * @param listener
     */
    public void getDeviceSimpleInfo(String deviceId, final DeviceApiCommonListener<LcSimpleInfoBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/simple-info?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcSimpleInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcSimpleInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取设备翻转状态
     *
     * @param deviceId
     * @param channelId
     * @param listener
     */
    public void getDeviceReverseStatus(String deviceId, String channelId, final DeviceApiCommonListener<LcRevertStatusBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/camera/frame-reverse-status?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId + "&channelId=" + channelId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcRevertStatusBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcRevertStatusBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 设置设备翻转状态
     *
     * @param deviceId
     * @param channelId
     * @param status
     * @param listener
     */
    public void setDeviceReverseStatus(String deviceId, String channelId, int status, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_SET_REVERSE_STATUS, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("status", status);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 设置动检开关
     *
     * @param deviceId
     * @param channelId
     * @param status
     * @param listener
     */
    public void setDeviceDetectionSwitch(String deviceId, String channelId, int status, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_SET_DETECTION_SWITCH, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("status", status);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取动检开关状态
     *
     * @param deviceId
     * @param channelId
     * @param listener
     */
    public void getDeviceDetectionSwitch(String deviceId, String channelId, final DeviceApiCommonListener<LcDetectionSwitchBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/alarm/status?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId + "&channelId=" + channelId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcDetectionSwitchBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcDetectionSwitchBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取动检计划
     *
     * @param deviceId
     * @param channelId
     * @param listener
     */
    public void getDeviceDetectionPlan(String deviceId, String channelId, final DeviceApiCommonListener<LcDetectionPlanBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/alarm/info?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId + "&channelId=" + channelId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcDetectionPlanBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcDetectionPlanBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 设置动检计划
     *
     * @param deviceId
     * @param channelId
     * @param data
     * @param listener
     */
    public void setDeviceDetectionPlan(String deviceId, String channelId, String ruleType, JSONArray data, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_SET_DETECTION_PAN, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("ruleType", ruleType);
            jsonObject.put("rules", data);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 设备升级
     *
     * @param deviceId
     * @param listener
     */
    public void setDeviceUpgrade(String deviceId, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_DEVICE_UPGRADE, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * SD卡格式化
     *
     * @param deviceId
     * @param channelId
     * @param listener
     */
    public void setSDCardFormat(String deviceId, String channelId, final DeviceApiCommonListener<LcRevertStatusBean> listener) {
        String url = String.format(ApiConstant.URL_SD_FORMAT, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<LcRevertStatusBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcRevertStatusBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 云台转动
     *
     * @param deviceId
     * @param channelId
     * @param operation
     * @param duration
     * @param h
     * @param v
     * @param z
     * @param listener
     */
    public void setCameraPtzControl(String deviceId, String channelId, String operation, String duration, double h, double v, double z, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_CAMERA_PTZ, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("operation", operation);
            jsonObject.put("duration", duration);
            jsonObject.put("h", h);
            jsonObject.put("v", v);
            jsonObject.put("z", z);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取设备状态（暂时只支持大华设备）
     *
     * @param deviceId
     * @param listener
     */
    public void getLcDeviceStatus(String deviceId, final DeviceApiCommonListener<LcRevertStatusBean> listener) {
        String url = ApiConstant.getBaseServer() + "/iot/v2/devices/" + ApiConstant.getUserID() + "/status?token=" + ApiConstant.getAppToken() + "&deviceId=" + deviceId;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcRevertStatusBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcRevertStatusBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                            WLog.e("data:onSuccess", responseBean.data);
                        } else {
                            listener.onFail(-1, responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取乐橙摄像机详细信息
     *
     * @param deviceId
     * @param listener
     */
    public void getLcDeviceInfo(String deviceId, final DeviceApiCommonListener<LcDeviceInfoBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }

        String url = ApiConstant.BASE_URL_USER + "/" + ApiConstant.getUserID() + "/devices/" + deviceId;
        OkGo.get(url)
                .tag(this)
                .params("token", ApiConstant.getAppToken(), true)
                .execute(new EncryptCallBack<ResponseBean<LcDeviceInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcDeviceInfoBean> responseBean, Call call, Response response) {
                        WLog.i("doLcDeviceInfo:onSuccess");
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("doLcDeviceInfo:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取乐橙摄像机本地录像片段
     *
     * @param deviceId   设备id
     * @param channelId  通道id
     * @param beginTime  起始时间
     * @param endTime    结束时间
     * @param type       录像类型（默认All）
     *                   type=Manual表示手动录像
     *                   type=Event表示事件录像
     *                   type=All表示所有录像
     * @param queryRange 第几条到第几条,单次查询上限30,1-30表示第1条到第30条,包含30
     *                   <p>
     *                   数字取值范围为：[1,N]（N为正整数，且后者＞前者）; 差值取值范围为：[0, 29]
     * @param listener
     */
    public void getLocalRecordFragment(String deviceId, String channelId, String beginTime, String endTime, String type, String queryRange, final DeviceApiCommonListener<LcLocalRecordBean> listener) {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }

        String url = String.format(ApiConstant.URL_LOCAL_RECORD, ApiConstant.getUserID()) + "?token=" + ApiConstant.getAppToken() + "&deviceId="
                + deviceId + "&channelId=" + channelId + "&beginTime=" + beginTime + "&endTime=" + endTime + "&type=" + type + "&queryRange=" + queryRange;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<LcLocalRecordBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<LcLocalRecordBean> responseBean, Call call, Response response) {
                        WLog.i("getLocalRecordFragment:onSuccess");
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        WLog.i("getLocalRecordFragment:onError", e.toString());
                        String msg = context.getString(R.string.Service_Error);
                        listener.onFail(-1, msg);
                        super.onError(call, response, e);
                    }
                });
    }


    /**
     * 罗格朗门禁开锁
     *
     * @param communityId
     * @param uc
     * @param listener
     */
    public void entranceGuardUnlock(String communityId, String uc, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_UNLOCK, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("communityId", communityId);
            jsonObject.put("uc", uc);
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 远程挂断
     *
     * @param communityId
     * @param uc
     * @param listener
     */
    public void remoteHangUp(String communityId, String uc, final DeviceApiCommonListener<Object> listener) {
        String url = String.format(ApiConstant.URL_REMOTE_HANG_UP, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("communityId", communityId);
            jsonObject.put("uc", uc);
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("appId", ApiConstant.APPID);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * APP音视频账号注册
     *
     * @param communityId
     * @param uc
     * @param listener
     */
    public void entranceGuardSipRegister(String communityId, String uc, final DeviceApiCommonListener<EntranceGuardRegisterBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_SIP_REG, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("communityId", communityId);
            jsonObject.put("uc", uc);
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardRegisterBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardRegisterBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区信息，wulian授权管控的省
     *
     * @param listener
     */
    public void getEntranceGuardProvince(final DeviceApiCommonListener<EntranceGuardProvinceBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_PROVINCE, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardProvinceBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardProvinceBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区信息，wulian授权管控的省的市
     *
     * @param provinceId
     * @param listener
     */
    public void getEntranceGuardCity(String provinceId, final DeviceApiCommonListener<EntranceGuardCityBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_CITY, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("provinceId", provinceId);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardCityBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardCityBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区信息，wulian授权管控的省的市的区
     *
     * @param provinceId
     * @param cityId
     * @param listener
     */
    public void getEntranceGuardCityDistrict(String provinceId, String cityId, final DeviceApiCommonListener<EntranceGuardCityDistrictBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_CITY_DISTRICT, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("provinceId", provinceId);
            jsonObject.put("cityId", cityId);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardCityDistrictBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardCityDistrictBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取社区信息，wulian授权管控的省的市的区的社区
     *
     * @param provinceId
     * @param cityId
     * @param districtId
     * @param listener
     */
    public void getEntranceGuardCommunity(String provinceId, String cityId, String districtId, final DeviceApiCommonListener<EntranceGuardCommunityBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_COMMUNITY, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            jsonObject.put("provinceId", provinceId);
            jsonObject.put("cityId", cityId);
            jsonObject.put("districtId", districtId);
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardCommunityBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardCommunityBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区的地址信息，社区的区
     *
     * @param communityId
     * @param listener
     */
    public void getEntranceGuardDistrict(String communityId, final DeviceApiCommonListener<EntranceGuardDistrictBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_DISTRICT, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardDistrictBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardDistrictBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取社区的地址信息，社区的区的楼栋
     *
     * @param communityId
     * @param districtId
     * @param listener
     */
    public void getEntranceGuardBuilding(String communityId, String districtId, final DeviceApiCommonListener<EntranceGuardBuildingBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_BUILDING, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("districtId", districtId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardBuildingBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardBuildingBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区的地址信息，社区的区的楼栋的单元
     *
     * @param communityId
     * @param districtId
     * @param buildingId
     * @param listener
     */
    public void getEntranceGuardUnit(String communityId, String districtId, String buildingId, final DeviceApiCommonListener<EntranceGuardUnitBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_UNIT, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("districtId", districtId);
        params.put("buildingId", buildingId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardUnitBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardUnitBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取社区的地址信息，社区的区的楼栋的单元的楼层
     *
     * @param communityId
     * @param districtId
     * @param buildingId
     * @param unitId
     * @param listener
     */
    public void getEntranceGuardFloor(String communityId, String districtId, String buildingId, String unitId, final DeviceApiCommonListener<EntranceGuardFloorBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_FLOOR, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("districtId", districtId);
        params.put("buildingId", buildingId);
        params.put("unitId", unitId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardFloorBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardFloorBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取社区的地址信息，社区的区的楼栋的单元的楼层的户
     *
     * @param communityId
     * @param districtId
     * @param buildingId
     * @param unitId
     * @param floorId
     * @param listener
     */
    public void getEntranceGuardHouse(String communityId, String districtId, String buildingId, String unitId, String floorId, final DeviceApiCommonListener<EntranceGuardHouseBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_HOUSE, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("districtId", districtId);
        params.put("buildingId", buildingId);
        params.put("unitId", unitId);
        params.put("floorId", floorId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardHouseBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardHouseBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取社区的地址信息，社区的区的楼栋的单元的楼层的户的设备
     *
     * @param communityId
     * @param districtId
     * @param buildingId
     * @param unitId
     * @param floorId
     * @param houseId
     * @param listener
     */
    public void getEntranceGuardDeviceInfo(String communityId, String districtId, String buildingId, String unitId, String floorId, String houseId, final DeviceApiCommonListener<EntranceGuardDeviceInfoBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_DEVICE_INFO, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("districtId", districtId);
        params.put("buildingId", buildingId);
        params.put("unitId", unitId);
        params.put("floorId", floorId);
        params.put("houseId", houseId);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardDeviceInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardDeviceInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取社区的地址信息
     *
     * @param communityId
     * @param uc
     * @param listener
     */
    public void getEntranceGuardAddressInfo(String communityId, String uc, final DeviceApiCommonListener<EntranceGuardAddressBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_ADDRESS_INFO, ApiConstant.getUserID());
        HashMap<String, String> params = new HashMap<>();
        params.put("communityId", communityId);
        params.put("uc", uc);
        params.put("token", ApiConstant.getAppToken());
        OkGo.get(url)
                .tag(this)
                .params(params)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardAddressBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardAddressBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            try {
                                int i = Integer.parseInt(responseBean.resultCode);
                                listener.onFail(i, "");
                            } catch (NumberFormatException e) {
                                listener.onFail(-1, "");
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }

    /**
     * 获取罗格朗社区信息
     *
     * @param listener
     */
    public void getEntranceGuardCommunityInfo(final DeviceApiCommonListener<EntranceGuardCommunityInfoBean> listener) {
        String url = String.format(ApiConstant.URL_ENTRANCE_GUARD_COMMUNITY_INFO, ApiConstant.getUserID());
        JSONObject msgContentJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", ApiConstant.getAppToken());
            msgContentJson = new JSONObject(CipherUtil.createCloudMessage(jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.post(url)
                .tag(this)
                .upJson(msgContentJson)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardCommunityInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardCommunityInfoBean> responseBean, Call call, Response response) {
                        String resultDesc = responseBean.resultDesc;
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(-1, resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                    }
                });
    }


    /**
     * 获取主机信息
     *
     * @param communityId
     * @param dd
     * @param bb
     * @param rr
     * @param listener
     */
    public void getEntranceGuardHostInfo(String communityId, String dd, String bb, String rr, final DeviceApiCommonListener<EntranceGuardHostInfo> listener) {
        String url;
        url = String.format(ApiConstant.URL_ENTRANCE_GUARD_HOST_INFO, ApiConstant.getUserID()) + "?communityId=" + communityId + "&dd=" + dd + "&token=" + ApiConstant.getAppToken() + "&bb=" + bb + "&rr=" + rr;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardHostInfo>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardHostInfo> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取验证码
     *
     * @param communityId
     * @param dd
     * @param bb
     * @param rr
     * @param ff
     * @param ii
     * @param listener
     */
    public void getEntranceGuardVerifyCode(String communityId, String dd, String bb, String rr, String ff, String ii, final DeviceApiCommonListener<Object> listener) {
        String url;
        url = String.format(ApiConstant.URL_ENTRANCE_GUARD_VERIFY_CODE, ApiConstant.getUserID()) + "?communityId=" + communityId + "&dd=" + dd + "&token=" + ApiConstant.getAppToken() + "&bb=" + bb + "&rr=" + rr + "&ff=" + ff + "&ii=" + ii;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<Object>>() {
                    @Override
                    public void onSuccess(ResponseBean<Object> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                        super.onError(call, response, e);
                    }
                });
    }

    /**
     * 获取小区物业信息
     *
     * @param communityId
     * @param pageNo
     * @param pageSize
     * @param listener
     */
    public void getEntranceGuardPropertyInfo(String communityId, String pageNo, String pageSize, final DeviceApiCommonListener<EntranceGuardPropertyInfoBean> listener) {
        String url;
        url = String.format(ApiConstant.URL_ENTRANCE_GUARD_VERIFY_CODE, ApiConstant.getUserID()) + "?communityId=" + communityId + "&pageNo=" + pageNo + "&token=" + ApiConstant.getAppToken() + "&pageSize=" + pageSize;
        OkGo.get(url)
                .tag(this)
                .execute(new EncryptCallBack<ResponseBean<EntranceGuardPropertyInfoBean>>() {
                    @Override
                    public void onSuccess(ResponseBean<EntranceGuardPropertyInfoBean> responseBean, Call call, Response response) {
                        if (responseBean.isSuccess()) {
                            listener.onSuccess(responseBean.data);
                        } else {
                            listener.onFail(responseBean.getResultCode(), responseBean.resultDesc);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        listener.onFail(-1, e.getMessage());
                        super.onError(call, response, e);
                    }
                });
    }
}
