package cc.wulian.smarthomev6.support.core.apiunit.bean;

import android.text.TextUtils;

import java.util.List;

import cc.wulian.smarthomev6.main.home.widget.HomeItemBean;
import cc.wulian.smarthomev6.main.home.widget.HomeWidgetManager;
import cc.wulian.smarthomev6.support.tools.Preference;

/**
 * Created by Veev on 2017/6/19
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    WidgetBean
 */

public class WidgetBean {
    public List<WidgetsBean> widgets;

    public static class WidgetsBean {
        /**
         * id : xxxx
         * uid : xxxx
         * topDeviceId : xxxx
         * widgetId : xxxx
         * widgetType : xxxx
         * isdisplay : xxxx
         * sortNum : xxxx
         * topDeviceFlag : xxxx
         */

        public String id;
        public String uid;
        public String topDeviceId;
        public String widgetId;
        public String widgetType;
        public String isdisplay;
        public String sortNum;
        public String topDeviceFlag;

        public WidgetsBean() {
            widgetId = "0";
            widgetType = "0";
            isdisplay = "0";
            sortNum = "0";
            uid = Preference.getPreferences().getUserID();
            topDeviceId = Preference.getPreferences().getCurrentGatewayID();
        }

        public WidgetsBean(HomeItemBean bean) {
            uid = Preference.getPreferences().getUserID();
            topDeviceId = Preference.getPreferences().getCurrentGatewayID();
            if (TextUtils.isEmpty(topDeviceId)) {
                topDeviceId = null;
            }
            if (TextUtils.equals(bean.getType(), HomeWidgetManager.Banner)) {
                widgetId = "1";
                widgetType = "adv";
            } else if (TextUtils.equals(bean.getType(), HomeWidgetManager.Scene)) {
                widgetId = "2";
                widgetType = "scene";
            } else if (TextUtils.equals(bean.getType(), HomeWidgetManager.Video)) {
                widgetId = "3";
                widgetType = "video";
            } else {
                widgetId = bean.getWidgetID();
                widgetType = bean.getType();
            }
            isdisplay = "0";
            sortNum = bean.getSort() + "";
        }
    }
}
