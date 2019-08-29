package cc.wulian.smarthomev6.main.device.device_if02.fan;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by huxc on 2018/9/10.
 */

public class ControllerViewHelper {

    private Map<String, keyItem> keys = new LinkedHashMap<>();

    private static String[] IF02_KET_TAG = {"power", "boot", "menu", "signal", "mute", "back",
            "vol+", "vol-", "ch+", "ch-",
            "up", "down", "left", "right", "ok",
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "0"};

    //风扇模板按键组
    private static String[] FAN_KET_TAG = {"power", "oscillation", "mode", "fanspeed"};

    //投影仪模板按键组
    private static String[] PROJECTOR_KET_TAG = {"power", "menu", "back",
            "up", "down", "left", "right", "ok",
            "pic+","pic-"
    };

    public Map<String, keyItem> getAllKeys() {
        return keys;
    }

    public int getCount() {
        return keys.size();
    }

    public int getCurrentCustomValue() {
        return keys.size() + 1;
    }

    public keyItem getKeyByTag(String tag) {
        return keys.get(tag);
    }


    public List<keyItem> getFanCustomKeys() {
        List<keyItem> list = new ArrayList<>();
        outer:
        for (String tag : keys.keySet()) {
            for (String value : FAN_KET_TAG) {
                if (TextUtils.equals(value, tag)) {
                    continue outer;
                }
            }
            list.add(keys.get(tag));
        }
        return list;
    }
    public List<keyItem> getProjectorCustomKeys() {
        List<keyItem> list = new ArrayList<>();
        outer:
        for (String tag : keys.keySet()) {
            for (String value : PROJECTOR_KET_TAG) {
                if (TextUtils.equals(value, tag)) {
                    continue outer;
                }
            }
            list.add(keys.get(tag));
        }
        return list;
    }


    public List<keyItem> getIF02CustomKeys() {
        List<keyItem> list = new ArrayList<>();
        outer:
        for (String tag : keys.keySet()) {
            for (String value : IF02_KET_TAG) {
                if (TextUtils.equals(value, tag)) {
                    continue outer;
                }
            }
            list.add(keys.get(tag));
        }
        return list;
    }

    public void convertFanKeyToItem(List<ControllerBlocksBean.keyBean> key) {
        //设置固定键盘值
        outer:
        for (String tag : FAN_KET_TAG) {
            for (ControllerBlocksBean.keyBean bean : key) {
                if (TextUtils.equals(bean.keyId, tag)) {
                    keyItem newItem = new keyItem(tag);
                    keys.put(tag, newItem);
                    continue outer;
                }
            }
        }

        if (key.size() == 0) {
            return;
        }
        //添加多余按钮
        for (ControllerBlocksBean.keyBean bean : key) {
            if (StringUtil.isNullOrEmpty(bean.keyId)) {
                continue;
            }
            if (!keys.containsKey(bean.keyId)) {
                keyItem newItem = new keyItem(bean.keyId,bean.keyName,bean.code);
                keys.put(bean.keyId, newItem);
            }
        }
    }

    public void convertProjectorKeyToItem(List<ControllerBlocksBean.keyBean> key) {
        //设置固定键盘值
        outer:
        for (String tag : PROJECTOR_KET_TAG) {
            for (ControllerBlocksBean.keyBean bean : key) {
                if (TextUtils.equals(bean.keyId, tag)) {
                    keyItem newItem = new keyItem(tag);
                    keys.put(tag, newItem);
                    continue outer;
                }
            }
        }

        if (key.size() == 0) {
            return;
        }
        //添加多余按钮
        for (ControllerBlocksBean.keyBean bean : key) {
            if (StringUtil.isNullOrEmpty(bean.keyId)) {
                continue;
            }
            if (!keys.containsKey(bean.keyId)) {
                keyItem newItem = new keyItem(bean.keyId,bean.keyName,bean.code);
                keys.put(bean.keyId, newItem);
            }
        }
    }

    public void convertCustomKeyToItem(List<ControllerBlocksBean.keyBean> key) {
        if (key.size() == 0) {
            return;
        }
        //添加多余按钮
        for (ControllerBlocksBean.keyBean bean : key) {
            if (StringUtil.isNullOrEmpty(bean.keyId)) {
                continue;
            }
            if (!keys.containsKey(bean.keyId)) {
                keyItem newItem = new keyItem(bean.keyId,bean.keyName,bean.code);
                keys.put(bean.keyId, newItem);
            }
        }
    }

    public void addKeys(Map<String, String> newkey) {
        for (String key : newkey.keySet()) {
            if (keys.containsKey(key)) {
                continue;
            }
            keyItem newItem = new keyItem(key, newkey.get(key));
            keys.put(key, newItem);
        }
    }

    public static class keyItem {
        private String tag;
        private boolean enable;
        private String value;
        private String keyName;

        public keyItem(String tag, boolean enable) {
            this.tag = tag;
            this.enable = enable;
        }

        public keyItem(String tag) {
            this.tag = tag;
            this.enable = true;
        }

        public keyItem(String tag, String value) {
            this.tag = tag;
            this.enable = true;
            this.value = value;
        }

        public keyItem(String tag, String keyName, String value) {
            this.tag = tag;
            this.enable = true;
            this.value = value;
            this.keyName = keyName;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
    }
}
