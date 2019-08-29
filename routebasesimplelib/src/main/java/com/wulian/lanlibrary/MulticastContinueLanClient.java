/**
 * Project Name:  RouteLibrary
 * File Name:     MulticastContinueLanController.java
 * Package Name:  com.wulian.lanlibrary
 * @Date:         2015年9月14日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.lanlibrary;

import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.controller.TaskResultListener;

/**
 * @ClassName: MulticastContinueLanController
 * @Function:
 * @Date: 2015年9月14日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class MulticastContinueLanClient {
	private static TaskResultListener mListener;
	private Thread mContinueThread;
	private static RouteApiType mApi;
	private String mLocalMac;

	public MulticastContinueLanClient(RouteApiType api,
			String localMac, TaskResultListener taskResult) {
		mApi = api;
		mLocalMac = localMac;
		mListener = taskResult;
	}

	public void connect() {
		if (mContinueThread != null && mContinueThread.isAlive()) {
			return;
		}
		mContinueThread = new Thread(new Runnable() {
			@Override
			public void run() {
				WulianLANApi.getAllDeviceInformationCallBack(mLocalMac,
						LanConfigure.CONTINUE_TIME_OUT);
			}
		});
		mContinueThread.start();
	}

	public static void receivedData(String[] strArray) {
		mListener.OnSuccess(mApi, ExternLanApi
				.changeStringArrayDateToJsonByMulticateData(strArray));
	}

	public void stopSendRequest() {
		WulianLANApi.stopCallBack();
	}

	public interface Listener {
		public void onConnect();

		public void onMessage(String[] result);

		public void onDisconnect();

		public void onError(Exception error);
	}
}
