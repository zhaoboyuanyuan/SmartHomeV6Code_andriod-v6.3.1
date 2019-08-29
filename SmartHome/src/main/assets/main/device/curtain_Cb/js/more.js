/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceType":"Cb",
        "deviceID": deviceID,
        "data": [
            {
                "group": "device",
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
                        "type": "custom",
                        "name": device_Cb_mode,//模式切换
                        "action": "custom:changeMode",
                        "param":[
                            {
                                "key":"url",
                                "type":"string",
                                "value":"device/curtain_Cb/html/modeChange.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": device_Cb_Motor_reversal,
                        "action": "custom:motorInversion"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}