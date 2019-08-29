/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;

function getMoreConfig(devID, gwID) {
    var config = {
        "deviceID": devID,
        "data": [
            {
                "group": "device",
                "param": [{
                    "key": "deviceID",
                    "type": "string",
                    "value": devID
                }],
                "item": [
                    {
                        "type": "custom",
                        "name": "",//
                        "action": "custom:Co_Settings"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}