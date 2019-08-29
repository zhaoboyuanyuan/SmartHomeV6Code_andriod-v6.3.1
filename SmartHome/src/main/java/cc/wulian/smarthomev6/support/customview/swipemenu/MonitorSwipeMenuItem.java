package cc.wulian.smarthomev6.support.customview.swipemenu;

import android.content.Context;

import cc.wulian.smarthomev6.support.utils.DisplayUtil;


/**
 * Created by yanzy on 2016-6-17
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public abstract class MonitorSwipeMenuItem extends SwipeMenuItem {
	public MonitorSwipeMenuItem(Context context) {
		super(context);
		setWidth(DisplayUtil.dip2Pix(context,100));
	}
	
	public abstract void onClick(int columnPosition);
	

}
