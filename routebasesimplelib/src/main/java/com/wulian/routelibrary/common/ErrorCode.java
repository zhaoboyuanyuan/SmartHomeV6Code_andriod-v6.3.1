/**
 * Project Name:  RouteLibrary
 * File Name:     ErrorCode.java
 * Package Name:  com.wulian.routelibrary.common
 * @Date:         2014-9-6
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.routelibrary.common;

/**
 * @ClassName: ErrorCode
 * @Function: 网络请求错误码
 * @Date: 2014-9-6
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum ErrorCode {
	SUCCESS(200, "成功", "Success"), NO_INTERNET(-1, "无网络", "No Network"), TIMEOUT_ERROR(
			-2, "网络不给力", "The network is not so good"), NETWORK_ERROR(-3,
			"网络错误,请检查网络连接", "Network Error"), NO_WIFI(-4, "无连接Wi-Fi",
			"No Wi-Fi"), AIRPLANE_MODE(-5, "飞行模式", "Flight mode"), NOSDCARD(-6,
			"没有SD卡", "No SD-Card"), FILE_EXIST(-7, "文件存在",
			"File already exists"), INVALID_REQUEST(-8, "请求错误",
			"Invalid Request"), INVALID_IO(-9, "请求错误", "Invalid Request"), UNKNOWN_EXCEPTION(
			-10, "未知错误", "Unknown Error"), FORBIDDEN_ERROR(403, "越权的访问",
			"Forbidden"), NOT_FOUND(404, "路径未找到", "Path not found"), SERVER_ERROR(
			500, "服务器错误", "Server Error"), SERVER_ERROR_SHUTDOWN(502,
			"服务器维护升级,请稍后", "Server maintenance, retry later"), UNKNOWN_ERROR(0,
			"未知错误", "Unknown Error"), INVALID_TOKEN(1000, "非法Token",
			"Invalid Token"), TOKEN_EXPIRED(1001, "Token失效,请校验您手机时间。",
			"Token timeout,please check your phone time"), INVALIDSTRLENGTH(
			1002, "非法字符串长度", "Invalid string length"), INVALID_DEVICE_BIND(1006,"没有查到该设备记录","No record of the equipment"),LIMIT_EXCEEDED(1010,
			"超过限制", "More than limit"), PARAM_MISSING(1100, "缺少必要参数",
			"Lack necessary parameters"), INVALID_PHONE(1101, "非法手机号",
			"Illeagl phonenum"), INVALID_MODEL(1102, "非法型号/App",
			"Invalid Model/App"), INVALID_SOURCE(1103, "非法资源",
			"Invalid Resource"), INVALID_TYPE(1104, "非法类型", "Invalid Type"), INVALID_EMAIL(
			1105, "非法邮箱", "Invalid Email"), INVALID_DEVICE_ID(1106, "非法设备编号",
			"Invalid device id"), INVALID_SEED(1107, "非法Seed", "Invalid seed"), INVALID_URL(
			1108, "非法网址", "Invalid websites"),INVALID_BINDER_USERNAME(1110,"非法的用户名","Invalid username"), INVALID_USER(1111, "非法用户或密码",
			"Invalid user or password"), INVALID_CODE(1122, "非法验证码",
			"Invalid verification code"), INVALID_APPKEY(1123, "非法AppKey",
			"Invalid appkey"), INVALID_REDIRECT_URI(1124, "非法回调地址",
			"Invalid redirect url"), INVALID_APPSECRET(1125, "非法用户或密码",
			"Invalid appsecret"), INVALID_LOGIN_AUTH(1126, "非法登录校验码,请注销后重新登录",
			"Invalid login check code, please logout and relogin"), INVALID_HASH(
			1127, "非法Hash", "Invalid hash"), DUMPLICATED_DATA(2000, "重复数据",
			"Dumplicated data"), NO_ROWS_AFFECTED(2020, "未影响数据", "Process fail"), UNAUTHORIZED_DEVICE(
			2021, "无权限的设备", "Unauthorized device");

	int errorCode;// 错误码
	String description;// 描述
	String description_en;// 描述

	private ErrorCode(int errorCode, String desc, String desc_en) {
		this.errorCode = errorCode;
		this.description = desc;
		this.description_en = desc_en;
	}

	/**
	 * 
	 * @ClassName getErrorCode
	 * @Function 获取错误码
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @return
	 */
	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @ClassName getTypeByCode
	 * @Function 根据Code获取类型
	 * @author Puml
	 * @date: 2014-9-6 email puml@wuliangroup.cn
	 * @param code
	 * @return
	 */
	public static ErrorCode getTypeByCode(int code) {
		for (ErrorCode mErrorCode : ErrorCode.values()) {
			if (mErrorCode.getErrorCode() == code) {
				return mErrorCode;
			}
		}
		// UNKNOWN_ERROR.setDescription(UNKNOWN_ERROR.getDescription() + ":"
		// + code);
		// UNKNOWN_ERROR.setDescription_en(UNKNOWN_ERROR.getDescription_en() +
		// ":"
		// + code);
		UNKNOWN_ERROR.setDescription("未知错误" + ":" + code);
		UNKNOWN_ERROR.setDescription_en("Unknown Error" + ":" + code);
		return UNKNOWN_ERROR;
	}
}
