/**
 * Created by Veev on 2017/6/9.
 */
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
                        "name": account,
                        "action": "custom:OP_Account_Manage",
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
                        "enableWithEnterType":"account",
                        "name": sms,
                        "action": "jump:Log"
                    },
                    {
                        "type": "jump",
                        "enableWithEnterType":"account",
                        "name": alarm,
                        "action": "jump:Alarm"
                    },
                    {
                        "type": "jump",
                        "showWithEnterType": "account",
                        "name": setAlarm,
                        "action": "jump:OP_Alarm_Setts",
                        "offLineDisable": true,
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "alarmSet.html"
                            }
                        ]
                    }
                ]
            }
        ]
    };

    return config;
}