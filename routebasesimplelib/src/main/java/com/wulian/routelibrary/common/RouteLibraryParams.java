/**
 * Project Name:  RouteLibrary
 * File Name:     RouteLibraryParams.java
 * Package Name:  com.wulian.routelibrary.common
 *
 * @Date: 2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

import java.util.Date;
import java.util.HashMap;

import com.wulian.routelibrary.utils.AuthCode;

/**
 * @author Puml
 * @ClassName: RouteLibraryParams
 * @Function: 库文件的参数
 * @Date: 2014-9-6
 * @email puml@wuliangroup.cn
 */
public class RouteLibraryParams {

    public static HashMap<String, String> V3AppBell(String auth, String did,
                                                    String action) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        mMap.put("action", action);
        return mMap;
    }

    public static HashMap<String, String> V3TokenDownLoad(String auth, String sdomain, String did) {
        HashMap mMap = new HashMap();
        mMap.put("auth", auth);
        mMap.put("sdomain", sdomain);
        mMap.put("did", did);
        return mMap;
    }

    public static native HashMap<String, String> V3UserPassword(String auth,
                                                                String password);

    public static native HashMap<String, String> V3SmartRoomLogin(String username, String password, String appsecret);

    public static HashMap<String, String> V3SmartRoomSSOLogin(String username, String token, String appsecret) {
        HashMap<String, String> mMap = new HashMap<>();
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        String timestampString = timestamp + "";
        mMap.put("timestamp", timestampString);
        mMap.put("username", username);
        String authToken = AuthCode.encode(token, timestampString);
        mMap.put("token", authToken);
        mMap.put("appsecret", appsecret);
        return mMap;
    }

    public static HashMap<String, String> V3SmartroomPassword(String auth, String password) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        Date date = new Date();
        long timestamp = date.getTime() / 1000;
        mMap.put("timestamp", String.valueOf(timestamp));
        mMap.put("auth", auth);
        mMap.put("password", password);
        return mMap;
    }
    /**
     *
     * @MethodName UserCheckLogin
     * @Function 登录校验
     * @author Puml
     * @date: 2014-9-6
     * @email puml@wuliangroup.cn
     * @param username
     *            用户名 (只有手机号码)
     * @param password
     *            密码(AuthCode加密)
     * @param timestamp
     *            时间戳
     * @param token
     *            AuthCode 加密校验
     * @param meta
     *            是否需要返回用户信息,true为需要
     * @return HashMap
     */
    // public static native HashMap<String, String> UserCheckLogin(
    // String username, String password, boolean meta);

    /**
     * @param username
     * @param password
     * @param meta
     * @return
     * @MethodName V3Login
     * @Function V3登陆
     * @author Puml
     * @date: 2016年4月27日
     * @email puml@wuliangroup.cn
     */
    public static native HashMap<String, String> V3Login(String username,
                                                         String password);

    /**
     *
     * @MethodName UserLogout
     * @Function 用户注销
     * @author Puml
     * @date: 2016年3月26日
     * @email puml@wuliangroup.cn
     * @param auth
     *            用户凭证
     * @return
     */
    // public static HashMap<String, String> UserLogout(String auth) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // mMap.put("auth", auth);
    // return mMap;
    // }
    //

    /**
     * @param auth
     * @return
     * @MethodName V3Logout
     * @Function 注销
     * @author Puml
     * @date: 2016年6月2日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3Logout(String auth) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        return mMap;
    }

    public static String EncodeMappingString(String needEncode) {
        return EncodeMappingString(needEncode, needEncode.length());
    }

    protected static native String getDecodeStringNative(String needDecode,
                                                         String key, int time);

    public static String getDecodeString(String needDecode, String key) {
        return getDecodeStringNative(needDecode, key, 300);
    }

    /**
     * @param needEncode       待编码字符串
     * @param needEncodeLength 待编码字符串长度
     * @return
     * @MethodName EncodeMappingString
     * @Function 编码字符
     * @author Puml
     * @date: 2016年4月26日
     * @email puml@wuliangroup.cn
     */
    protected static native String EncodeMappingString(String needEncode,
                                                       int needEncodeLength);

    /**
     *
     * @MethodName BindingCheck
     * @Function 查询绑定状态(App不需要)
     * @author Puml
     * @date: 2014-9-11
     * @email puml@wuliangroup.cn
     * @param context
     * @param device_id
     *            设备惟一编号,如 fe804c34289
     * @param timestamp
     *            时间戳,如 1409827689
     * @param source
     *            来源,如 JiaRouter-1.0.0
     * @param token
     *            时间戳加密校验
     * @return
     */
    // public static HashMap<String, String> BindingCheck(Context context,
    // String device_id, String auth) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // String versionName = "";
    // String token = "";
    // try {
    // versionName = LibraryPhoneStateUtil.getVersionName(context);
    // } catch (NameNotFoundException e) {
    // versionName = "1.0.0";
    // }
    // try {
    // token = String.valueOf(timestamp) + MD5.MD52(device_id);
    // token = MD5.MD52(token).substring(8, 14);
    // } catch (NoSuchAlgorithmException e) {
    // e.printStackTrace();
    // return null;
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // return null;
    // }
    // String source = LibraryConstants.MD5_CONSTANT2 + "-" + versionName;
    // mMap.put("device_id", device_id);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // mMap.put("source", source);
    // mMap.put("token", token);
    // return mMap;
    // }

    /**
     *
     * @MethodName BindingBind
     * @Function 绑定设备
     * @author Puml
     * @date: 2014年10月30日
     * @email puml@wuliangroup.cn
     * @param auth
     *            登录校验码
     * @param mac
     *            MAC地址
     * @param device_id
     *            设备ID
     * @param seed
     *            seed
     * @return
     */
    // public static HashMap<String, String> BindingBind(String auth,
    // String device_id, String seed) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // mMap.put("auth", auth);
    // mMap.put("device_id", device_id);
    // mMap.put("seed", seed);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // return mMap;
    // }

    /**
     *
     * @MethodName BindingUnbind
     * @Function 解除绑定
     * @author Puml
     * @date: 2014年10月30日
     * @email puml@wuliangroup.cn
     * @param auth
     *            登录校验码
     * @param device_id
     *            设备ID
     * @return
     */
    // public static HashMap<String, String> BindingUnbind(String auth,
    // String device_id) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // mMap.put("auth", auth);
    // mMap.put("device_id", device_id);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // return mMap;
    // }

    /**
     *
     * @MethodName BindingCheckSeed
     * @Function 校验Seed
     * @author Puml
     * @date: 2014-9-11
     * @email puml@wuliangroup.cn
     * @param uuid
     *            用户ID
     * @param seed
     *            加密后的随机校验串
     * @param timestamp
     *            时间戳
     * @param source
     *            来源
     * @param token
     *            时间戳加密校验
     * @return
     */
    // public static HashMap<String, String> BindingCheckSeed(Context context,
    // String uuid, String seed) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // String versionName = "";
    // try {
    // versionName = LibraryPhoneStateUtil.getVersionName(context);
    // } catch (NameNotFoundException e) {
    // versionName = "1.0.0";
    // }
    // String source = LibraryConstants.MD5_CONSTANT2 + "-" + versionName;
    // String token = "";
    // try {
    // seed = MD5.MD52(seed) + seed;
    // seed = MD5.MD52(seed).substring(10, 20);
    //
    // token = String.valueOf(timestamp) + MD5.MD52(seed);
    // token = MD5.MD52(token).substring(8, 14);
    // } catch (NoSuchAlgorithmException e) {
    // e.printStackTrace();
    // return null;
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // return null;
    // }
    // mMap.put("uuid", uuid);
    // mMap.put("seed", seed);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // mMap.put("source", source);
    // mMap.put("token", token);
    // return mMap;
    // }

    /**
     * @param device_id 设备id
     * @param auth      Auth Token
     * @return
     * @MethodName V3BindCheck
     * @Function V3检查设备绑定
     * @author Puml
     * @date: 2016年4月26日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3BindCheck(String auth, String did) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        return mMap;
    }

    /**
     * @param device_id 设备id
     * @param auth      Auth Token
     * @return
     * @MethodName V3BindResult
     * @Function 查询设备绑定结果
     * @author Puml
     * @date: 2016年4月26日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3BindResult(String auth, String did) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        return mMap;
    }

    /**
     * @param did
     * @param auth
     * @return
     * @MethodName V3BindUnbind
     * @Function
     * @author Puml
     * @date: 2016年6月2日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3BindUnbind(String auth, String did) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        return mMap;
    }

    public static HashMap<String, String> V3NoticeMoveList(String auth, String deviceID, int starttime,
                                                           int endtime, int page, int pages) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", deviceID);
        mMap.put("starttime", String.valueOf(starttime));
        mMap.put("endtime", String.valueOf(endtime));
        mMap.put("page", String.valueOf(page));
        mMap.put("pages", String.valueOf(pages));
        return mMap;
    }

    /**
     * @param auth      Auth Token
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @param page      页码
     * @param pages     每页个数，范围：10~100
     * @return
     * @MethodName V3NoticeVideoList
     * @Function 获取短视频测消息列表
     * @author Puml
     * @date: 2017年2月18日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3NoticeVideoList(String auth, int starttime,
                                                            int endtime, int page, int pages) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("starttime", String.valueOf(starttime));
        mMap.put("endtime", String.valueOf(endtime));
        mMap.put("page", String.valueOf(page));
        mMap.put("pages", String.valueOf(pages));
        return mMap;
    }

    /**
     * @param auth      Auth Token
     * @param did       设备id
     * @param starttime 开始时间
     * @param endtime   结束时间
     * @param page      页码
     * @param pages     每页个数，范围：10~100
     * @return
     * @MethodName V3NoticeVideoList
     * @Function 获取短视频测消息列表
     * @author Puml
     * @date: 2017年2月18日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3NoticeVideoList(String auth, String did, int starttime,
                                                            int endtime, int page, int pages) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        mMap.put("starttime", String.valueOf(starttime));
        mMap.put("endtime", String.valueOf(endtime));
        mMap.put("page", String.valueOf(page));
        mMap.put("pages", String.valueOf(pages));
        return mMap;
    }

    /**
     *
     * @MethodName DeviceEditMeta
     * @Function 修改设备描述
     * @author Puml
     * @date: 2014年10月30日
     * @email puml@wuliangroup.cn
     * @param device_id
     *            设备惟一编号
     * @param auth
     *            登录校验码
     * @param nick
     *            设备别名
     * @param desc
     *            设备描述(描述别名至少传一个)
     * @return
     */
    // public static HashMap<String, String> DeviceEditMeta(String device_id,
    // String auth, String nick, String desc) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // mMap.put("device_id", device_id);
    // mMap.put("auth", auth);
    // mMap.put("nick", nick);
    // mMap.put("desc", desc);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // return mMap;
    // }

    /**
     * @param auth Auth Token
     * @param did  设备惟一编号
     * @param nick 设备别名
     * @param desc 设备描述
     * @return
     * @MethodName V3UserDevice
     * @Function 修改设备描述
     * @author Puml
     * @date: 2016年6月3日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3UserDevice(String auth, String did,
                                                       String nick, String desc) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        mMap.put("nick", nick);
        mMap.put("desc", desc);
        return mMap;
    }

    /**
     *
     * @MethodName DeviceList
     * @Function 获取绑定设备列表
     * @author Puml
     * @date: 2014年10月30日
     * @email puml@wuliangroup.cn
     * @param type
     *            设备类型
     * @param auth
     *            登录校验码
     * @return
     */
    // public static HashMap<String, String> DeviceList(String type, String
    // auth) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // long timestamp = (new Date()).getTime() / 1000;
    // mMap.put("type", type);
    // mMap.put("auth", auth);
    // mMap.put("timestamp", String.valueOf(timestamp));
    // return mMap;
    // }

    /**
     * @param type 设备类型，最大长度为6
     * @param auth Auth Token
     * @return
     * @MethodName V3UserDevices
     * @Function 获取绑定设备
     * @author Puml
     * @date: 2016年6月2日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3UserDevices(String auth, String type, int page, int size) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("type", type);
        mMap.put("auth", auth);
        mMap.put("page", String.valueOf(page));
        mMap.put("size", String.valueOf(size));
        return mMap;
    }

    /**
     * @return
     * @MethodName DeviceLocale
     * @Function 更新设备地区
     * @author Puml
     * @date: 2014年11月24日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> DeviceLocale() {
        HashMap<String, String> mMap = new HashMap<String, String>();
        return mMap;
    }

    /**
     * @MethodName OssSTSGetToken
     * @Function 获取OSS的安全Token
     * @author Puml
     * @date: 2015年10月21日
     * @email puml@wuliangroup.cn
     * @param type
     * @param auth
     * @return
     */
    // public static native HashMap<String, String> OssSTSGetToken(String auth,
    // String host, String deviceId);

    /**
     * @param device_id 设备id
     * @param auth      Auth Token
     * @return
     * @MethodName V3AppFlag
     * @Function 查询设备标识
     * @author Puml
     * @date: 2016年4月27日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> V3AppFlag(String device_id,
                                                    String auth) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", device_id);
        return mMap;
    }

    public static HashMap<String, String> V3TokenDownloadReplay(String auth,
                                                                String did, String username) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        mMap.put("sdomain", username);
        return mMap;
    }

    public static HashMap<String, String> V3TokenDownloadPic(String auth,
                                                             String key) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("key", key);
        return mMap;
    }

    public static HashMap<String, String> V3TokenDownloadVideo(String auth,
                                                               String did, String sdomain) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("auth", auth);
        mMap.put("did", did);
        mMap.put("sdomain", sdomain);
        return mMap;
    }

    public static HashMap<String, String> V3VersionCheck(String search, String versionCode) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("search", search);
        mMap.put("version", versionCode);
        return mMap;
    }

    /**
     * @MethodName V2AppDeviceFlag
     * @Function 设备标示
     * @author Puml
     * @date: 2015年7月24日
     * @email puml@wuliangroup.cn
     * @param auth
     *            登录校验码
     * @param deviceid
     *            设备ID
     * @return
     */
    // public static HashMap<String, String> V2AppDeviceFlag(String auth,
    // String deviceid) {
    // HashMap<String, String> mMap = new HashMap<String, String>();
    // mMap.put("auth", auth);
    // mMap.put("deviceid", deviceid);
    // return mMap;
    // }

    /**
     * @param map
     * @param auth
     * @return
     * @MethodName ConvertMapValue
     * @Function 转化
     * @author Puml
     * @date: 2016年2月3日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> ConvertMapValue(
            HashMap<String, String> map, String auth) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.putAll(map);
        if (mMap.containsKey("auth")) {
            mMap.put("auth", auth);
        }
        if (mMap.containsKey("timestamp")) {
            Date date = new Date();
            long timestamp = date.getTime() / 1000;
            mMap.put("timestamp", String.valueOf(timestamp));
        }
        return mMap;
    }

    /***************************** 局域网 *******************************/
    /**
     * @param localMac 本地mac地址
     * @return 字符串数组
     * @MethodName SearchAllDevice
     * @Function 搜索周围设备
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> SearchAllDevice(String localMac) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("localMac", localMac);
        return mMap;
    }

    /**
     * @param localMac 本地mac地址
     * @return 字符串数组
     * @MethodName SearchCurrentDevice
     * @Function 搜索当前周围设备
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> SearchCurrentDevice(String localMac) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("localMac", localMac);
        return mMap;
    }

    /**
     * @param localMac 本地mac地址
     * @return 字符串数组
     * @MethodName getAllDeviceInformation
     * @Function 获取设备信息
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> getAllDeviceInformation(
            String localMac) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("localMac", localMac);
        return mMap;
    }

    /**
     * @param localMac 本地mac地址
     * @return 字符串数组
     * @MethodName getCurrentDeviceInformation
     * @Function 获取设备信息
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> getCurrentDeviceInformation(
            String localMac) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("localMac", localMac);
        return mMap;
    }

    /**
     * @param localMac   本地mac地址
     * @param remoteMac  远端mac地址
     * @param ssid       AP的名称
     * @param encryption AP的加密方式：none, wep, psk, psk2, wpa, wpa2
     * @param key        使用psk, psk2, wpa, wpa2加密时， AP的密码
     * @return 字符串数组
     * @MethodName setWirelessWifiForDevice
     * @Function 配置wifi
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> setWirelessWifiForDevice(
            String remoteIp, String localMac, String remoteMac, String ssid,
            String encryption, String key) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("remoteIp", remoteIp);
        mMap.put("localMac", localMac);
        mMap.put("remoteMac", remoteMac);
        mMap.put("ssid", ssid);
        mMap.put("encryption", encryption);
        mMap.put("key", key);
        return mMap;
    }

    /**
     * @param localMac  本地mac地址
     * @param remoteMac 远端mac地址
     * @return 字符串数组
     * @MethodName getWirelessWifiConnectInformationForDevice
     * @Function 获取连接信息
     * @author Puml
     * @date: 2014年11月5日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> getWirelessWifiConnectInformationForDevice(
            String remoteIp, String localMac, String remoteMac) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("remoteIp", remoteIp);
        mMap.put("localMac", localMac);
        mMap.put("remoteMac", remoteMac);
        return mMap;
    }

    /**
     * @param remoteIp
     * @param deviceID
     * @param bind_seed
     * @return
     * @MethodName BindSeedSet
     * @Function 三方绑定设置
     * @author Puml
     * @date: 2016年7月18日
     * @email puml@wuliangroup.cn
     */
    public static HashMap<String, String> BindSeedSet(String remoteIp,
                                                      String deviceID, String bind_seed) {
        HashMap<String, String> mMap = new HashMap<String, String>();
        mMap.put("remoteIp", remoteIp);
        mMap.put("deviceID", deviceID);
        mMap.put("bind_seed", bind_seed);
        return mMap;
    }

    /***************************** 局域网 *******************************/
    static {
        System.loadLibrary("wulianrouteparams");
    }
}
