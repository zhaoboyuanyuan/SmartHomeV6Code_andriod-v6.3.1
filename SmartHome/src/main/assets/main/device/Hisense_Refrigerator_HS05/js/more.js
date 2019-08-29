/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(devID) {
    var config = {
        "deviceID": devID,
        "data": [
            {
                "group": "dd",
                "param": [
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": devID
                    }
                ],
                "item": [
                    {
                        "type": "jump",
                        "name": "wifi配置",//选择音源
                        "action": "jump:HisenseWiFi",
                        "param": [
                            {
                                "key": "type",
                                "type": "string",
                                "value": "HS05"
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