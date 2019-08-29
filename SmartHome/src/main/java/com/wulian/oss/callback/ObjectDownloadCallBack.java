/**
 * Project Name:  WulianICamOSS
 * File Name:     PictureDownloadCallBack.java
 * Package Name:  com.wulian.oss.callback
 * @Date:         2016年7月8日
 * Copyright (c)  2016, wulian All Rights Reserved.
*/

package com.wulian.oss.callback;

import android.graphics.Bitmap;

/**
 * @ClassName: ObjectDownloadCallBack
 * @Function:
 * @Date:      2016年7月8日
 * @author     Puml
 * @email      puml@wuliangroup.cn
 */
public interface ObjectDownloadCallBack {
	public void  onObjectCallBack(Bitmap object, boolean isSuccess, String objectName);
}

