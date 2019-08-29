package cc.wulian.smarthomev6.main.home.voicecontrol;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.smarthomev6.R;
import cc.wulian.smarthomev6.entity.SceneInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.main.home.scene.SceneManager;
import cc.wulian.smarthomev6.support.core.device.Device;
import cc.wulian.smarthomev6.support.core.device.DeviceInfoDictionary;

/**
 * Created by zbl on 2017/9/22.
 * 语法构建
 * 语法解析
 */

public class GrammarManager {

    private static final String[] ACTION_ON = {"打开", "设防", "开启",};
    private static final String[] ACTION_OFF = {"撤防", "关闭", "关掉"};
    private static final String[] ACTION_GET = {"温度", "湿度", "二氧化碳"};
    private static final String[] DEVICE_TYPE = {
            "02", "03", "04", "06", "09",
            "12", "16", "17", "19",
            "22", "25", "28",
            "42", "43", "44",
            "50",
            "61", "62", "63", "65",
            "77",
            "80", "82",
            "90",
            "a1", "A5", "A6", "Ad", "Ai", "Aj", "Am", "An", "Ao", "Ap", "Ar", "At", "Au", "Aw", "Ax",
            "Ba", "Be",
            "C0",
            "OF", "Og", "Oi", "Oj", "OZ"};
    private static final String[][] DEVICE_TYPE_NICK = {
            {"开关", "classKG"},
            {"插座", "classCZ"},
            {"窗帘", "80"},
            {"空调", "OZ"}
    };

    public interface GrammarTextParseCallback {
        void onControlDevice(String action, Device device, String input_text);

        void onControlScene(String action, SceneInfo sceneInfo, String input_text);

        void onFail();
    }

    private HashMap<String, Device> deviceMap = new HashMap<>();
    private HashMap<String, SceneInfo> sceneMap = new HashMap<>();
    private HashMap<String, String> deviceTypeMap = new HashMap<>();

    private GrammarTextParseCallback callback;

    public GrammarManager() {
        deviceMap.clear();
        sceneMap.clear();
        List<Device> deviceList = MainApplication.getApplication().getDeviceCache().getZigBeeDevices();
        for (Device device : deviceList) {
            if (!TextUtils.isEmpty(device.name)) {
                deviceMap.put(stringFilter(device.name), device);
            }
        }
        List<SceneInfo> sceneList = new SceneManager(MainApplication.getApplication()).acquireScene();
        for (SceneInfo sceneInfo : sceneList) {
            if (!TextUtils.isEmpty(sceneInfo.getName())) {
                sceneMap.put(stringFilter(sceneInfo.getName()), sceneInfo);
            }
        }
        for (String type : DEVICE_TYPE) {
            int nameRes = DeviceInfoDictionary.getDefaultNameByType(type);
            if (nameRes != R.string.Device_Unknow) {
                deviceTypeMap.put(MainApplication.getApplication().getString(nameRes), type);
            }
        }
        for (String[] typeNick : DEVICE_TYPE_NICK) {
            deviceTypeMap.put(typeNick[0], typeNick[1]);
        }
    }

    public void setGrammarTextParseCallback(GrammarTextParseCallback callback) {
        this.callback = callback;
    }

    public String buildGrammar() {
        StringBuilder sb = new StringBuilder();
        sb.append("#ABNF 1.0 UTF-8;\n"
                + "language zh-CN;\n"
                + "mode voice;\n"
                + "root $mainAction;\n"
                + "$mainAction = $action$device;\n");
        //动作名称
        sb.append("$action = ");
        for (String action : ACTION_ON) {
            sb.append(action);
            sb.append("|");
        }
        for (String action : ACTION_OFF) {
            sb.append(action);
            sb.append("|");
        }
        for (String action : ACTION_GET) {
            sb.append(action);
            sb.append("|");
        }
        sb.setLength(sb.length() - 1);
        sb.append(";\n");
        sb.append("$device = ");
        //设备类型
        for (String typeName : deviceTypeMap.keySet()) {
            sb.append(stringFilter(typeName));
            sb.append("|");
        }
        //自定义的设备名称
        for (Device device : deviceMap.values()) {
            sb.append(stringFilter(device.name));
            sb.append("|");
        }
        //自定义的场景
        for (SceneInfo sceneInfo : sceneMap.values()) {
            sb.append(stringFilter(sceneInfo.getName()));
            sb.append("|");
        }
        sb.setLength(sb.length() - 1);
        sb.append(";");
        return sb.toString();
    }


    private static String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
    private static Pattern pattern = Pattern.compile(regEx);

    /**
     * 过滤一些特殊字符
     */
    private static String stringFilter(String str) {
        String result = "";
        try {
            str = str.replaceAll("\\\\", "");
            Matcher m = pattern.matcher(str);

            result = m.replaceAll("").trim();
        } catch (Exception e) {
            return str;
        }
        return result;
    }

    /**
     * 解析命令语句
     */
    public void parseControlText(String controlText) {
        if (callback != null && !TextUtils.isEmpty(controlText)) {
            for (String action : ACTION_ON) {
                if (controlText.startsWith(action)) {
                    String name = controlText.substring(action.length());
                    control("on", name, controlText);
                }
            }
            for (String action : ACTION_OFF) {
                if (controlText.startsWith(action)) {
                    String name = controlText.substring(action.length());
                    control("off", name, controlText);
                }
            }
        }
    }

    private void control(String action, String name, String controlText) {
        SceneInfo sceneInfo = sceneMap.get(name);
        if (sceneInfo != null) {
            callback.onControlScene(action, sceneInfo, controlText);
            return;
        }
        Device device = deviceMap.get(name);
        if (device != null) {
            callback.onControlDevice(action, device, controlText);
            return;
        }
        String deviceType = deviceTypeMap.get(name);
        if (deviceType != null) {
            Device d = new Device();
            d.type = deviceType;
            callback.onControlDevice(action, d, controlText);
            return;
        }
        callback.onFail();
    }
}
