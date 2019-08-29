/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "admin",
                "offLineDisable": true,
                "item": [
                    {
                        "type": "custom",
                        "name": lang_DoorLookPasswordmanager,
                        "action": "custom:OW_Account_Manage",
                        "param": [
                            {
                                "key": "deviceID",
                                "type": "string",
                                "value": deviceID
                            },
                            {
                                "key": "url",
                                "type": "string",
                                "value": "accountManage.html"
                            }
                        ]
                    }
                ]
            },
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
                        "name": lang_Alarm,
                        "action": "jump:Alarm"
                    },
                    {
                        "type": "jump",
                        "name": lang_Log,
                        "action": "jump:Log"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}