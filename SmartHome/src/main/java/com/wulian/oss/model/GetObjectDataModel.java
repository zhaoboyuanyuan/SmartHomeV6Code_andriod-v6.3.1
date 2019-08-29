/**
 * Project Name:  WulianICamOSS
 * File Name:     GetObjectRequestModel.java
 * Package Name:  com.wulian.oss.model
 * @Date:         2016年3月3日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.oss.model;

/**
 * @ClassName: GetObjectRequestModel
 * @Function:
 * @Date: 2016年3月3日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class GetObjectDataModel {
	private String mFilePath;
	private String mObjectName;
	private String mBucketName;
	private long mTimeStamp;
	private boolean IsPlayFlag;
	private int mFileSize;

	public GetObjectDataModel() {
		this.setIsPlayFlag(false);
		this.mFilePath = null;
	}

	public GetObjectDataModel(String objectName, long timeStamp, int fileSize) {
		this.mObjectName = objectName;
		this.mTimeStamp = timeStamp;
		this.mFileSize = fileSize;
		this.mBucketName = null;
		this.setIsPlayFlag(false);
	}

	public GetObjectDataModel(String bucketName, String objectName,
                              long timeStamp, int fileSize) {
		this.mObjectName = objectName;
		this.mTimeStamp = timeStamp;
		this.mFileSize = fileSize;
		this.mBucketName = bucketName;
		this.setIsPlayFlag(false);
	}

	public String getBucketName() {
		return mBucketName;
	}

	public void setBucketName(String bucketName) {
		this.mBucketName = bucketName;
	}

	public void setFilePath(String filePath) {
		this.mFilePath = filePath;
	}

	public String getFilePath() {
		return this.mFilePath;
	}

	public String getObjectName() {
		return mObjectName;
	}

	public void setObjectName(String mObjectName) {
		this.mObjectName = mObjectName;
	}

	public int getFileSize() {
		return mFileSize;
	}

	public void setFileSize(int mFileSize) {
		this.mFileSize = mFileSize;
	}

	public long getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(long mTimeStamp) {
		this.mTimeStamp = mTimeStamp;
	}

	public boolean isIsPlayFlag() {
		return IsPlayFlag;
	}

	public void setIsPlayFlag(boolean isPlayFlag) {
		this.IsPlayFlag = isPlayFlag;
	}
}
