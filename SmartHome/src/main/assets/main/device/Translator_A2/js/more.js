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
                    }
                ],
                "item": [
                    {
                        "type": "jump",
                        "name": lang_A2_initial_action,//初始动作设置
                        "action": "jump:H5BridgeCommon",
                        "desc": lang_A2_initial_action_hint,//"注：当设置的按钮动作与实际设备响应相反时，可通过调整初始动作使之保持一致"
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/Translator_A2/roadTranslatorSetting.html"
                            },{
                                "key": "gwID",
                                "type": "string",
                                "value": gwID
                            },{
                                "key": "devId",
                                "type": "string",
                                "value": deviceID
                            }
                        ]
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}