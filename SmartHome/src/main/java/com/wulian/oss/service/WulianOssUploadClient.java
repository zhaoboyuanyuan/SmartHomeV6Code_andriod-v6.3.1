/**
 * Project Name:  WulianICamOSS
 * File Name:     WulianOssUploadClient.java
 * Package Name:  com.wulian.oss.service
 * @Date:         2017年3月16日
 * Copyright (c)  2017, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.model.FederationToken;

/**
 * @ClassName: WulianOssUploadClient
 * @Date: 2017年3月16日
 */
public class WulianOssUploadClient {
	private ClientConfiguration conf;
	private OSSClient mOSSClient;

	public void initConfigData() {
		conf = new ClientConfiguration();
		conf.setConnectionTimeout(ConfigLibrary.CONNECTION_TIMEOUT);
		conf.setSocketTimeout(ConfigLibrary.SOTIMEOUT);
		conf.setMaxConcurrentRequest(ConfigLibrary.MAX_CONCURRENT_TASK_NUM);
		conf.setMaxErrorRetry(ConfigLibrary.MAX_ERROR_RETRY); // 失败后最大重试次数，默认2次
	}

	public String uploadOSSObjectName(Context context, FederationToken token,
			String endPoint, String bucketName, String key) {
		OSSCredentialProvider credentialProvider;
		credentialProvider = new OSSStsTokenCredentialProvider(
				token.getAccessKeyId(), token.getAccessKeySecret(),
				token.getSecurityToken());
		OSSLog.enableLog();
		mOSSClient = new OSSClient(context, endPoint, credentialProvider, conf);
		try {
			String URL = mOSSClient.presignConstrainedObjectURL(bucketName,
					key, 3600);
			return URL;
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return null;
	}
}
