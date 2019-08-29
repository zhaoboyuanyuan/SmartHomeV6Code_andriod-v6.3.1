/**
 * Project Name:  RouteLibrary
 * File Name:     RouteApiType.java
 * Package Name:  com.wulian.routelibrary.common
 *
 * @Date: 2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

/**
 * @ClassName: RouteApiType
 * @Function: 路径类型
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum RouteApiType {
    V3_APP_BELL("/v3/app/bell", RequestType.HTTP_POST), // 指定唤醒或休眠设备id
    V3_SMARTROOM_LOGIN("/v3/smartroom/login", RequestType.HTTP_POST), //
    V3_SMARTROOM_SSO_LOGIN("/v3/smartroom/login/sso", RequestType.HTTP_POST),//智能家居V6 SSO登录
    V3_SMARTROOM_PASSWORD("/v3/smartroom/password", RequestType.HTTP_POST), //
    USER_LOGIN_WLAIKAN("/user/loginWlAikan", RequestType.HTTP_STREAM), // 爱看账户登录(详见南京物联App
    // IPC
    // SDK接口.docx)
    // USER_CHECK_LOGIN("/user/check_login", RequestType.HTTP_POST), // 登录校验
    V3_LOGIN("/v3/login", RequestType.HTTP_POST), // 登录校验
    V3_USER_PASSWORD("/v3/user/password", RequestType.HTTP_POST), // 修改密码
    // USER_LOGOUT("/user/logout",RequestType.HTTP_POST),//注销
    V3_LOGOUT("/v3/logout", RequestType.HTTP_POST), // 注销
    // BINDING_CHECK("/binding/check", RequestType.HTTP_POST), // 查询绑定状态
    // BINDING_BIDN("/binding/bind", RequestType.HTTP_POST), // 绑定设备
    // BINDING_UNBIND("/binding/unbind", RequestType.HTTP_POST), // 解除绑定
    V3_BIND_UNBIND("/v3/bind/unbind", RequestType.HTTP_POST), // 解除绑定
    // BINDING_CHECKSEED("/binding/check_seed", RequestType.HTTP_POST), //
    // 校验Seed

    // DEVICE_EDIT_META("/device/edit_meta", RequestType.HTTP_POST), // 修改设备描述
    V3_USER_DEVICE("/v3/user/device", RequestType.HTTP_POST), // 修改设备描述
    // DEVICE_LIST("/device/list", RequestType.HTTP_POST), // 获取绑定设备列表
    V3_USER_DEVICES("/v3/user/devices", RequestType.HTTP_POST), // 获取绑定设备
    // DEVICE_LOCALE("/device/locale",RequestType.HTTP_POST),//更新设备地区
    // STS_GETSECTOKEN("/sts/getsectoken",RequestType.HTTP_GET),//获取OSS的安全Token

    // V2_APP_SERVER("/v2/app/server",RequestType.HTTP_POST),//V2 国际化服务器选择
    // V2_APP_DEVICE_FLAG("/v2/app/device/flag",RequestType.HTTP_POST),//V2设备标示
    V3_NOTICE_MOVE_LIST("/v3/notice/move/list/filter", RequestType.HTTP_POST), // 获取移动侦测消息列表
    V3_NOTICE_VIDEO_LIST("/v3/notice/video/list/filter", RequestType.HTTP_POST), // 获取短视频测消息列表

    V3_SERVER("/v3/server", RequestType.HTTP_POST), // 服务器域名
    V3_BIND_CHECK("/v3/bind/check", RequestType.HTTP_POST), // 检查设备绑定
    V3_BIND_RESULT("/v3/bind/result", RequestType.HTTP_POST), // 检查绑定状态
    V3_APP_FLAG("/v3/app/flag", RequestType.HTTP_POST), // 查询设备标识

    V3_TOKEN_DOWNLOAD_PIC("/v3/token/download/pic", RequestType.HTTP_POST), // 获取移动侦测秘钥
    V3_TOKEN_DOWNLOAD_REPLAY("/v3/token/download/replay", RequestType.HTTP_POST), // 获取回看秘钥
    V3_TOKEN_DOWNLOAD_VIDEO("/v3/token/download/video", RequestType.HTTP_POST), // 获取短视频秘钥

    V3_VERSION_STABLE("/v3/version/stable", RequestType.HTTP_GET), // 查询稳定版本更新

    SearchAllDevice(null, RequestType.LAN_REQUEST), // 搜索周围设备
    SearchCurrentDevice(null, RequestType.LAN_REQUEST), // 搜索当前周围设备
    getAllDeviceInformation(null, RequestType.LAN_REQUEST), // 获取设备信息
    getCurrentDeviceInformation(null, RequestType.LAN_REQUEST), // 获取当前设备信息
    BindSeedSet(null, RequestType.LAN_REQUEST), // 三方绑定设置

    getAllDeviceInformationByMulticast(null, RequestType.LAN_MULTICAST_REQUEST), // 组播获取设备信息
    setWirelessWifiForDevice(null, RequestType.LAN_REQUEST), // 配置wifi
    getWirelessWifiConnectInformationForDevice(null, RequestType.LAN_REQUEST);// 获取连接信息

    private String mURL;// 路径
    private RequestType mRequestType;// 请求类型

    private RouteApiType(String url, RequestType requestType) {
        this.mURL = url;
        this.mRequestType = requestType;
    }

    /**
     *
     * @ClassName getmType
     * @Function 获取请求类型
     * @author Puml
     * @date: 2014-9-6 email puml@wuliangroup.cn
     * @return
     */
    public RequestType getRequestType() {
        return mRequestType;
    }

    /**
     *
     * @ClassName getmURL
     * @Function 获取请求路径
     * @author Puml
     * @date: 2014-9-6 email puml@wuliangroup.cn
     * @return
     */
    public String getmURL() {
        return mURL;
    }

    public void setRequestType(RequestType requestType) {
        this.mRequestType = requestType;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

}
