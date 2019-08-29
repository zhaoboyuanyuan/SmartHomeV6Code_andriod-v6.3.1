var moreSettingData;
function getMoreConfig(gwID, deviceID) {
    moreSettingData = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "log",
                "enableWithEnterType":"account",
                "param": [
                	{
                        "key":"gwID",
                        "type":"string",
                        "value":gwID
                    },
                    {
                        "key": "devId",
                        "type": "string",
                        "value": deviceID
                    },
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
                    }
                ]
            }
        ]
    };
}
