/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "log",
                "param": [
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "gwID",
                        "type": "string",
                        "value": gwID
                    }
                ],
                "item": [
                    {
                        "type": "jump",
                        "name": lang_Bo_initial_action,//初始动作设置
                        "action": "jump:H5BridgeCommon",
                        "desc": lang_Bo_initial_action_hint,//"注：当设置的按钮动作与实际设备响应相反时，可通过调整初始动作使之保持一致"
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/Translator_Bo/roadTranslatorSetting.html"
                            }
                        ]
                    },
                    {
                         "type": "custom",
                         "name": "按键背光灯",//按键背光灯
                         "action": "custom:Bo_Backlight_Status"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}