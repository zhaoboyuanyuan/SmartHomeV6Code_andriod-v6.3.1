/**
 * Project Name:  WulianICamOSS
 * File Name:     WulianOSSPictureDownload.java
 * Package Name:  com.wulian.oss.service
 * @Date:         2016年7月8日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.oss.service;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.wulian.oss.ConfigLibrary;
import com.wulian.oss.callback.ObjectDownloadCallBack;
import com.wulian.oss.model.FederationToken;

/**
 * @ClassName: WulianOSSPictureDownload
 * @Function:
 * @Date: 2016年7月8日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianOSSPictureDownload {
	private ClientConfiguration conf;
	private OSSClient mOSSClient;

	public void initConfigData() {
		conf = new ClientConfiguration();
		conf.setConnectionTimeout(ConfigLibrary.CONNECTION_TIMEOUT);
		conf.setSocketTimeout(ConfigLibrary.SOTIMEOUT);
		conf.setMaxConcurrentRequest(ConfigLibrary.MAX_CONCURRENT_TASK_NUM);
		conf.setMaxErrorRetry(ConfigLibrary.MAX_ERROR_RETRY); // 失败后最大重试次数，默认2次
	}

	public void getBitmap(Context context, FederationToken token,
			String endPoint, String bucketName, String objectName,
			ObjectDownloadCallBack callback) {
		
		OSSCredentialProvider credentialProvider;
		credentialProvider = new OSSStsTokenCredentialProvider(
				token.getAccessKeyId(), token.getAccessKeySecret(),
				token.getSecurityToken());
		OSSLog.enableLog();
		mOSSClient = new OSSClient(context, endPoint, credentialProvider, conf);
		try {
			String URL=mOSSClient.presignConstrainedObjectURL(bucketName, objectName, 3600);
			Log.d("PML", "URL is:"+URL);
		} catch (ClientException e) {
			e.printStackTrace();
			
		}
//		
//		asyncGetObjectSample(bucketName, objectName, callback);
	}

	public void asyncGetObjectSample(String bucketName,
			final String objectName, final ObjectDownloadCallBack callback) {

		GetObjectRequest get = new GetObjectRequest(bucketName, objectName);

		OSSAsyncTask task = mOSSClient.asyncGetObject(get,
				new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
					@Override
					public void onSuccess(GetObjectRequest request,
							GetObjectResult result) {
						// 请求成功
						InputStream inputStream = result.getObjectContent();
						Bitmap bitmap = InputStream2Bitmap(inputStream);
						OSSLog.logD("Bitmap is :"+(bitmap==null?"NULL":"not null"));
						if (callback != null) {
							OSSLog.logD("callback not null");
							callback.onObjectCallBack(bitmap, true, objectName);
						}
					}

					@Override
					public void onFailure(GetObjectRequest request,
							ClientException clientExcepion,
							ServiceException serviceException) {
						// 请求异常
						if (clientExcepion != null) {
							// 本地异常如网络异常等
							clientExcepion.printStackTrace();
						}
						if (serviceException != null) {
							// 服务异常
							OSSLog.logE("ErrorCode"
									+ serviceException.getErrorCode());
							OSSLog.logE("RequestId"
									+ serviceException.getRequestId());
							OSSLog.logE("HostId" + serviceException.getHostId());
							OSSLog.logE("RawMessage"
									+ serviceException.getRawMessage());
						}
						if (callback != null) {
							callback.onObjectCallBack(null, false, objectName);
						}
					}
				});
	}

	public Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}
}
