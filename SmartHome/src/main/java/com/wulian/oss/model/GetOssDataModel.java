/**
 * Project Name:  WulianOSS
 * File Name:     GetOssDataModel.java
 * Package Name:  com.wulian.oss.model
 * @Date:         2015年10月17日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.oss.model;

import com.wulian.oss.ConfigLibrary;

/**
 * @ClassName: GetOssDataModel
 * @Function:
 * @Date:      2015年10月17日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public class GetOssDataModel {
	byte[] mData;
	int mReadLength;
	
 	public GetOssDataModel() {
 		mData=new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];
 		mReadLength=0;
	}
 	
 	public GetOssDataModel(byte[] data,int length) {
 		mData=new byte[ConfigLibrary.DEFAULT_GET_OBJECT_LENGTH];
 		System.arraycopy(data, 0, mData, 0, length);
 		mReadLength=length;
 	}
 	
 	public byte[] getData() {
 		return mData;
 	}
 	
 	public int getReadLength() {
 		return mReadLength;
 	}
}

