package cc.wulian.smarthomev6.support.core.mqtt.bean;

import android.support.annotation.DrawableRes;

import java.util.List;

import cc.wulian.smarthomev6.R;

import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_AC;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_CUSTOM;
import static cc.wulian.smarthomev6.main.device.device_22.Device22Activity.TYPE_TV;

/**
 * Created by 上海滩小马哥 on 2017/12/12.
 */

public class Device22DetailBean {
    public String type;
    public String index;
    public String name;
    public boolean hasKeyCodeCatche;
    public List<Device22KeyBean.KeyData> data;

    public Device22DetailBean(String type, String index, String name, List<Device22KeyBean.KeyData> data) {
        this.type = type;
        this.index = index;
        this.name = name;
        this.data = data;
        this.hasKeyCodeCatche = false;
    }

    public
    @DrawableRes
    int getWidgetImg(boolean online, String type) {
        int imgRes = R.drawable.icon_uei_widget_air;
        switch (type) {
            case TYPE_TV:
                imgRes = online ? R.drawable.icon_uei_widget_tv : R.drawable.icon_uei_widget_tv_none;
                break;
            case TYPE_AC:
                imgRes = online ? R.drawable.icon_uei_widget_air : R.drawable.icon_uei_widget_air_none;
                break;
            case TYPE_CUSTOM:
                imgRes = online ? R.drawable.icon_uei_widget_custom : R.drawable.icon_uei_widget_custom_none;
                break;
        }
        return imgRes;
    }
}
