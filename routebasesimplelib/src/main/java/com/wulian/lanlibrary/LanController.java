/**
 * Project Name:  RouteLibrary
 * File Name:     LanController.java
 * Package Name:  com.wulian.routelibrary.lan
 *
 * @Date: 2014年11月7日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.lanlibrary;

import java.util.HashMap;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.TaskResultListener;

/**
 * @ClassName: LanController
 * @Function: 局域网控制器
 * @Date: 2014年11月7日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class LanController {
    private static MulticastContinueLanClient mClient;

    public static void setLocalIpV4(String localIp) {
        ExternLanApi.setLocalIpV4(localIp);
    }

    protected void setLogLevel(int level) {
        ExternLanApi.setLevel(level);
    }

    /**
     * @MethodName getFourStringPassword
     * @Function 返回8位密码
     * @author Puml
     * @date: 2014年12月3日
     * @email puml@wuliangroup.cn
     * @param fourStr
     *            4位字符串
     * @return 返回8位密码
     */
    public static String getFourStringPassword(String fourStr) {
        return ExternLanApi.getFourStringPassword(fourStr);
    }

    public static String EncodeMappingString(String EncodeString) {
        return ExternLanApi.EncodeMappingString(EncodeString);
    }

    public static void stopRequest() {
        if (mClient != null) {
            mClient.stopSendRequest();
            mClient = null;
        }
    }

    public static void executeRequest(RouteApiType type,
                                      HashMap<String, String> mParams, TaskResultListener task) {
        String localMac = "";
        if (mParams != null) {
            localMac = mParams.get("localMac");
        }
        stopRequest();
        mClient = new MulticastContinueLanClient(type, localMac, task);
        mClient.connect();
    }

    /**
     * @MethodName executeRequest
     * @Function 运行请求
     * @author Puml
     * @date: 2014年11月7日
     * @email puml@wuliangroup.cn
     * @param type
     *            类型
     * @param mParams
     *            参数
     * @return
     */
    public static String executeRequest(RouteApiType type,
                                        HashMap<String, String> mParams) {
        String result = "";
        switch (type) {
            case BindSeedSet:
                result = BindSeedSet(mParams);
                break;
            case SearchAllDevice:
                result = SearchAllDevice(mParams);
                break;
            case SearchCurrentDevice:
                result = SearchCurrentDevice(mParams);
                break;
            case getAllDeviceInformation:
                result = getAllDeviceInformation(mParams);
                break;
            case getCurrentDeviceInformation:
                result = getCurrentDeviceInformation(mParams);
                break;
            case setWirelessWifiForDevice:
                result = setWirelessWifiForDevice(mParams);
                break;
            case getWirelessWifiConnectInformationForDevice:
                result = getWirelessWifiConnectInformationForDevice(mParams);
                break;
            default:
                break;
        }
        return result;
    }

    private static String SearchAllDevice(HashMap<String, String> mParams) {
        String localMac = "";
        if (mParams != null) {
            localMac = mParams.get("localMac");
        }
        return ExternLanApi.SearchAllDevice(localMac);
    }

    private static String SearchCurrentDevice(HashMap<String, String> mParams) {
        String localMac = "";
        if (mParams != null) {
            localMac = mParams.get("localMac");
        }
        return ExternLanApi.SearchCurrentDevice(localMac);
    }

    private static String getAllDeviceInformation(
            HashMap<String, String> mParams) {
        String localMac = "";
        if (mParams != null) {
            localMac = mParams.get("localMac");
        }
        return ExternLanApi.getAllDeviceInformation(localMac);
    }

    private static String getCurrentDeviceInformation(
            HashMap<String, String> mParams) {
        String localMac = "";
        if (mParams != null) {
            localMac = mParams.get("localMac");
        }
        return ExternLanApi.getCurrentDeviceInformation(localMac);
    }

    private static String setWirelessWifiForDevice(
            HashMap<String, String> mParams) {
        String localMac = "";
        String remoteMac = "";
        String ssid = "";
        String encryption = "";
        String key = "";
        String remoteIp = "";
        if (mParams != null) {
            remoteIp = mParams.get("remoteIp");
            localMac = mParams.get("localMac");
            remoteMac = mParams.get("remoteMac");
            ssid = mParams.get("ssid");
            encryption = mParams.get("encryption");
            key = mParams.get("key");
        }
        return ExternLanApi.setWirelessWifiForDevice(remoteIp, localMac,
                remoteMac, ssid, encryption, key);
    }

    private static String getWirelessWifiConnectInformationForDevice(
            HashMap<String, String> mParams) {
        String localMac = "";
        String remoteMac = "";
        String remoteIp = "";
        if (mParams != null) {
            remoteIp = mParams.get("remoteIp");
            localMac = mParams.get("localMac");
            remoteMac = mParams.get("remoteMac");
        }
        return ExternLanApi.getWirelessWifiConnectInformationForDevice(
                remoteIp, localMac, remoteMac);
    }

    public static String BindSeedSet(HashMap<String, String> mParams) {
        String remoteIp = "";
        String deviceID = "";
        String bind_seed = "";
        if (mParams != null) {
            remoteIp = mParams.get("remoteIp");
            deviceID = mParams.get("deviceID");
            bind_seed = mParams.get("bind_seed");
        }

        return ExternLanApi.BindSeedSet(remoteIp, deviceID, bind_seed);
    }
}
