var moreSettingData;
function getMoreConfig(deviceID) {
    moreSettingData = {
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
                        "name": lang_change_password,
                        "action": "jump:Dev69_ChangePWD"
                    },
                    {
                        "type": "jump",
                        "name": moreSetting06,
                        "action": "jump:Alarm"
                    },
                    {
                        "type": "jump",
                        "name": moreSetting07,
                        "action": "jump:Log"
                    }
                ]
            }
        ]
    };
}
