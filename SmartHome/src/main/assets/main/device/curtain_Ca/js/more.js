/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceType":"Ca",
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
                        "name": lang_Ca_mode,//模式切换
                        "action": "custom:changeMode",
                        "param":[
                            {
                                "key":"url",
                                "type":"string",
                                "value":"device/curtain_Ca/html/modeChange.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": lang_Ca_motor,//"电机反转"
                        "action": "custom:motorInversion"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}