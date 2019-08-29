package cc.wulian.smarthomev6.main.device.device_23.tv;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.smarthomev6.main.device.device_if02.bean.ControllerBlocksBean;
import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by 上海滩小马哥 on 2017/11/10.
 */

public class TvViewHelper {

    private Map<String, TvItem> keys = new LinkedHashMap<>();
    private static String[] KET_TAG = {"1", "121", "33", "22", "8", "37",
            "6", "7", "4", "5",
            "38", "39", "40", "41", "42",
            "9", "10", "11", "12", "13",
            "14", "15", "16", "17", "18"};

    //wifi红外电视机、机顶盒、互联网盒子按键组
    private static String[] IF02_KET_TAG = {"power", "boot", "menu", "signal", "mute", "back",
            "vol+", "vol-", "ch+", "ch-",
            "up", "down", "left", "right", "ok",
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "0"};


    public Map<String, TvItem> getAllKeys() {
        return keys;
    }

    public int getCount() {
        return keys.size();
    }

    public int getCurrentCustomValue() {
        return keys.size() + 1;
    }

    public TvItem getKeyByTag(String tag) {
        return keys.get(tag);
    }

    public List<TvItem> getCustomKeys() {
        List<TvItem> list = new ArrayList<>();
        outer:
        for (String tag : keys.keySet()) {
            for (String value : KET_TAG) {
                if (TextUtils.equals(value, tag)) {
                    continue outer;
                }
            }
            list.add(keys.get(tag));
        }
        return list;
    }

    public List<TvItem> getIF02CustomKeys() {
        List<TvItem> list = new ArrayList<>();
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

    public void convertKeyToItem(String[] list) {
        //设置固定键盘值
        outer:
        for (String tag : KET_TAG) {
            for (String value : list) {
                if (TextUtils.equals(value, tag)) {
                    TvItem newItem = new TvItem(tag);
                    keys.put(tag, newItem);
                    continue outer;
                }
            }
        }

        if (list.length == 0) {
            return;
        }
        //添加多余按钮
        for (String tag : list) {
            if (TextUtils.equals(tag, "0") || StringUtil.isNullOrEmpty(tag)) {
                continue;
            }
            if (!keys.containsKey(tag)) {
                TvItem newItem = new TvItem(tag);
                keys.put(tag, newItem);
            }
        }
    }

    public void convertKeyToItem(Map<String, String> list) {
        //设置固定键盘值
        outer:
        for (String tag : KET_TAG) {
            for (String key : list.keySet()) {
                if (TextUtils.equals(key, tag)) {
                    TvItem newItem = new TvItem(tag, list.get(key));
                    keys.put(tag, newItem);
                    continue outer;
                }
            }
        }

        if (list.size() == 0) {
            return;
        }
        //添加多余按钮
        for (String key : list.keySet()) {
            if (TextUtils.equals(key, "0") || StringUtil.isNullOrEmpty(key)) {
                continue;
            }
            if (!keys.containsKey(key)) {
                TvItem newItem = new TvItem(key, list.get(key));
                keys.put(key, newItem);
            }
        }
    }


    public void convertKeyToItem(List<ControllerBlocksBean.keyBean> key) {
        //设置固定键盘值
        outer:
        for (String tag : IF02_KET_TAG) {
            for (ControllerBlocksBean.keyBean bean : key) {
                if (TextUtils.equals(bean.keyId, tag)) {
                    TvItem newItem = new TvItem(tag);
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
//                TvItem newItem = new TvItem(bean.keyId);
                TvItem newItem = new TvItem(bean.keyId,bean.keyName,bean.code);
                keys.put(bean.keyId, newItem);
            }
        }
    }

    public void addKeys(Map<String, String> newkey) {
        for (String key : newkey.keySet()) {
            if (keys.containsKey(key)) {
                continue;
            }
            TvItem newItem = new TvItem(key, newkey.get(key));
            keys.put(key, newItem);
        }
    }

    public static class TvItem {
        private String tag;
        private boolean enable;
        private String value;
        private String keyName;

        public TvItem(String tag, boolean enable) {
            this.tag = tag;
            this.enable = enable;
        }

        public TvItem(String tag) {
            this.tag = tag;
            this.enable = true;
        }

        public TvItem(String tag, String value) {
            this.tag = tag;
            this.enable = true;
            this.value = value;
        }

        public TvItem(String tag, String keyName, String value) {
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
