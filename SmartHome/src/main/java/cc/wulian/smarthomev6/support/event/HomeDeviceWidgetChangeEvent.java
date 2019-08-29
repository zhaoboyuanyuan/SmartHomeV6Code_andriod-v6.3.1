package cc.wulian.smarthomev6.support.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cc.wulian.smarthomev6.main.home.widget.HomeItemBean;

/**
 * Created by Veev on 2017/5/15
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 首页widget 新增和移除的事件
 */

public class HomeDeviceWidgetChangeEvent {

    /**
     * 全部刷新
     */
    public static final int ALL = 0;
    /**
     * 添加
     */
    public static final int ADD = 1;
    /**
     * 删除
     */
    public static final int DELETE = 2;
    /**
     * 同步
     */
    public static final int SYNC = 3;

    @IntDef({ALL, ADD, DELETE, SYNC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChangeType{}

    /**
     * 事件类型
     * 0 ALL        全部刷新
     * 1 ADD        添加
     * 2 DELETE     删除
     * 3 SYNC       同步
     */
    private @ChangeType int mType = ALL;
    private HomeItemBean mHomeItemBean;

    private HomeDeviceWidgetChangeEvent(int type, HomeItemBean bean){
        mType = type;
        mHomeItemBean = bean;
    }

    public static HomeDeviceWidgetChangeEvent ADD(HomeItemBean bean) {
        return new HomeDeviceWidgetChangeEvent(ADD, bean);
    }

    public static HomeDeviceWidgetChangeEvent DELETE(HomeItemBean bean) {
        return new HomeDeviceWidgetChangeEvent(DELETE, bean);
    }

    public static HomeDeviceWidgetChangeEvent ALL() {
        return new HomeDeviceWidgetChangeEvent(ALL, null);
    }

    public static HomeDeviceWidgetChangeEvent SYNC() {
        return new HomeDeviceWidgetChangeEvent(SYNC, null);
    }

    public @ChangeType int getType() {
        return mType;
    }

    public HomeItemBean getHomeItemBean() {
        return mHomeItemBean;
    }
}
