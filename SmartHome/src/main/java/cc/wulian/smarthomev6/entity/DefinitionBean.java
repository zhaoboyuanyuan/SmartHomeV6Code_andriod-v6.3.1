package cc.wulian.smarthomev6.entity;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

/**
 * Created by zbl on 2017/6/8.
 * 清晰度
 */

public class DefinitionBean {
    public int value;
    public String name;

    public DefinitionBean(int value) {
        this.value = value;
        this.name = getNameResByValue(value);
    }

    public static String getNameResByValue(int value) {
        if (value == 1) {
            return MainApplication.getApplication().getString(R.string.Standard_Definition);
        } else if (value == 2) {
            return MainApplication.getApplication().getString(R.string.High_Definition);
        } else if (value == 3) {
            return MainApplication.getApplication().getString(R.string.Super_Definition);
        } else return "";
    }
}
