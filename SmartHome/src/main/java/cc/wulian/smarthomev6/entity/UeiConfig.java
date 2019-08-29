package cc.wulian.smarthomev6.entity;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.main.application.MainApplication;

import static cc.wulian.smarthomev6.main.device.device_23.tv.RemoteControlBrandActivity.SINGLE_CODE_PROJECTOR;

/**
 * Created by Veev on 2017/9/19
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    UeiConfig
 */
public class UeiConfig {
    /**
     * desc :
     * time : 1505466737524
     * brand : Hisense
     * type : Z
     * name :
     * pick : Z0477
     */
    public String desc;
    public String time;
    public String brand;
    public String type;
    public String name;
    public String pick;
    public String singleCode;//相同type下的区分code
    public String[] supportKeyArr;
    public LinkedHashMap<String, String> learnKeyCodeDic;

    public UeiConfig() {
    }

    private UeiConfig(String desc, String time, String brand, String type, String singleCode, String name, String pick, String[] supportKeyArr, LinkedHashMap<String, String> learnKeyCodeDic) {
        this.desc = desc;
        this.time = time;
        this.brand = brand;
        this.type = type;
        this.singleCode = singleCode;
        this.name = name;
        this.pick = pick;
        this.supportKeyArr = supportKeyArr;
        this.learnKeyCodeDic = learnKeyCodeDic;
    }

    public static UeiConfig newUeiDevice(String time, String brand, String localName, String pick, String type, String[] supportKeyArr, LinkedHashMap<String, String> learnKeyCodeDic) {
        String name = localName;
        switch (type) {
            case "Z":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Airconditionername), localName);
                }
                break;
            case "T":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_TV), localName);
                }
                break;
            case "C":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Settopbox), localName);
                }
                break;
            case "R,M":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Remote_name_Audio), localName);
                }
                break;
            case "CUSTOM":
                if (!TextUtils.isEmpty(brand)) {
                    name = brand;
                }
                break;
        }
        UeiConfig c = new UeiConfig("", time, brand, type, "", name, pick, supportKeyArr, learnKeyCodeDic);
        return c;
    }

    public static UeiConfig newUeiDevice(String time, String brand, String localName, String pick, String type, String singleCode, String[] supportKeyArr, LinkedHashMap<String, String> learnKeyCodeDic) {
        String name = localName;
        switch (type) {
            case "Z":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Airconditionername), localName);
                }
                break;
            case "T":
                if(SINGLE_CODE_PROJECTOR.equals(singleCode)){
                    if (!TextUtils.isEmpty(brand)) {
                        name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Projector), localName);
                    }
                }else{
                    if (!TextUtils.isEmpty(brand)) {
                        name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_TV), localName);
                    }
                }
                break;
            case "C":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Settopbox), localName);
                }
                break;
            case "R,M":
                if (!TextUtils.isEmpty(brand)) {
                    name = String.format(MainApplication.getApplication().getString(R.string.Remote_name_Audio), localName);
                }
                break;
            case "CUSTOM":
                if (!TextUtils.isEmpty(brand)) {
                    name = brand;
                }
                break;
        }
        UeiConfig c = new UeiConfig("", time, brand, type, singleCode, name, pick, supportKeyArr, learnKeyCodeDic);
        return c;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return String.format(MainApplication.getApplication().getString(R.string.Infraredtransponder_widget_Airconditionername), brand);
        }

        return name;
    }

    public
    @DrawableRes
    int getWidgetImg(boolean online, String type) {
        int imgRes = R.drawable.icon_uei_widget_air;
        switch (type) {
            case "Z":
                imgRes = online ? R.drawable.icon_uei_widget_air : R.drawable.icon_uei_widget_air_none;
                break;
            case "T":
                if(SINGLE_CODE_PROJECTOR.equals(singleCode)){
                    imgRes = online ? R.drawable.icon_uei_projector : R.drawable.icon_uei_projector;
                }else{
                    imgRes = online ? R.drawable.icon_uei_widget_tv : R.drawable.icon_uei_widget_tv_none;
                }
                break;
            case "C":
                imgRes = online ? R.drawable.icon_uei_widget_stb : R.drawable.icon_uei_widget_stb_none;
                break;
            case "CUSTOM":
                imgRes = online ? R.drawable.icon_uei_widget_custom : R.drawable.icon_uei_widget_custom_none;
                break;
        }
        return imgRes;
    }

    public
    @DrawableRes
    int getWidgetImg(boolean online, String type, String singleCode) {
        int imgRes = R.drawable.icon_uei_widget_air;
        switch (type) {
            case "Z":
                imgRes = online ? R.drawable.icon_uei_widget_air : R.drawable.icon_uei_widget_air_none;
                break;
            case "T":
                if(SINGLE_CODE_PROJECTOR.equals(singleCode)){
                    imgRes = online ? R.drawable.icon_uei_widget_projector : R.drawable.icon_uei_widget_projector_none;
                }else{
                    imgRes = online ? R.drawable.icon_uei_widget_tv : R.drawable.icon_uei_widget_tv_none;
                }
                break;
            case "C":
                imgRes = online ? R.drawable.icon_uei_widget_stb : R.drawable.icon_uei_widget_stb_none;
                break;
            case "R,M":
                imgRes = online ? R.drawable.icon_uei_widget_audio : R.drawable.icon_uei_widget_audio_none;
                break;
            case "CUSTOM":
                imgRes = online ? R.drawable.icon_uei_widget_custom : R.drawable.icon_uei_widget_custom_none;
                break;
        }
        return imgRes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UeiConfig{");
        sb.append("desc='").append(desc).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", brand='").append(brand).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", singleCode='").append(singleCode).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", pick='").append(pick).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
