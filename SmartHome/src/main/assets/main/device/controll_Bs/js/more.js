/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": deviceID,
        "deviceType":"Bs",
        "data": [
            {
                "group": "admin",
                "offLineDisable": true,
                "item": [
                    {
                        "type": "custom",
                        "action": "custom:bs_more_setting",
                        "param": [
                            {
                                "key": "deviceID",
                                "type": "string",
                                "value": deviceID
                            }
                        ]
                    }
                ]
            }]
    };

    moreConfig = config;
    return config;
}