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
                        "name": lang_Log,
                        "action": "jump:Log"
                    },{
                        "type": "custom",
                        "name": "device_2013_led",
                        "action": "custom:aw_led"
                    },{
                        "type": "custom",
                        "name": "device_2013_aw_mode",
                        "action": "custom:aw_mode"
                    }
                ]
            }
        ]
    };
    moreConfig = config;
    return config;
}