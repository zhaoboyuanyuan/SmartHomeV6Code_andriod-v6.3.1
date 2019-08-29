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
                "enableWithEnterType":"account",
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
                        "name": languageUtil.getResource("device_A1_normal_set"),//正常状态设置
                        "action": "jump:H5BridgeCommon",
                        "desc": languageUtil.getResource("device_A1_normal_set_hint"),//"注：此状态下不会报警"
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/Translator_A1/translatorState.html"
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