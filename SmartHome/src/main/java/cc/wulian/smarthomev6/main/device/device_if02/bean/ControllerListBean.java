package cc.wulian.smarthomev6.main.device.device_if02.bean;

import android.support.annotation.DrawableRes;

import java.util.List;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.device.device_if02.WifiIRManage;

/**
 * created by huxc  on 2018/6/19.
 * func：遥控器列表
 * email: hxc242313@qq.com
 */

public class ControllerListBean {

    public List<ControllerBean>blocks;

    public  static class ControllerBean{
        public String blockType;
        public String blockName;
        public String blockId;
        public String codeLib;

        public
        @DrawableRes
        int getWidgetImg(boolean online, String type) {
            int imgRes = R.drawable.icon_uei_widget_air;
            switch (type) {
                case WifiIRManage.TYPE_AIR:
                    imgRes = online ? R.drawable.icon_uei_widget_air : R.drawable.icon_uei_widget_air_none;
                    break;
                case WifiIRManage.TYPE_TV:
                    imgRes = online ? R.drawable.icon_uei_widget_tv : R.drawable.icon_uei_widget_tv_none;
                    break;
                case WifiIRManage.TYPE_STB:
                    imgRes = online ? R.drawable.icon_uei_widget_stb : R.drawable.icon_uei_widget_stb_none;
                    break;
                case WifiIRManage.TYPE_FAN:
                    imgRes = online ? R.drawable.icon_uei_widget_fan : R.drawable.icon_uei_widget_fan_none;
                    break;
                case WifiIRManage.TYPE_IT_BOX:
                    imgRes = online ? R.drawable.icon_uei_widget_it_box : R.drawable.icon_uei_widget_it_box_none;
                    break;
                case WifiIRManage.TYPE_PROJECTOR:
                    imgRes = online ? R.drawable.icon_uei_widget_projector : R.drawable.icon_uei_widget_projector_none;
                    break;
                case WifiIRManage.TYPE_CUSTOM:
                    imgRes = online ? R.drawable.icon_uei_widget_custom : R.drawable.icon_uei_widget_custom_none;
                    break;
            }
            return imgRes;
        }
    }


}
