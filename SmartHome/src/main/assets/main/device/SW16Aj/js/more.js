/**
 * Created by Veev on 2017/6/9.
 */
function getMoreConfig(deviceID,gwID) {
    var config = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "log",
                "enableWithEnterType": "account",
                "param": [
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "gwID",
                        "type": "string",
                        "value": gwID
                    }
                ],
                "item": [
                    {
                        "type": "custom",
                        "name": lang_custom_01,
                        "action": "custom:Aj_Clear_Quantity"
                    },
                    {
                        "type": "custom",
                        "name": lang_custom_02,
                        "action": "custom:Aj_Recover_State",
                        "param": [
                            {
                                "key": "desc",
                                "type": "string",
                                "value": lang_custom_02_desc_01
                            }
                        ],
                    },
                    {
                        "type": "custom",
                        "name": lang_custom_03,
                        "action": "custom:Aj_Bind_Mode",
                        "param": [
                            {
                                "key": "desc",
                                "type": "string",
                                "value": lang_custom_03_desc_01
                            }
                        ],
                    },
                    {
                        "type": "jump",
                        "name": lang_sms,
                        "action": "jump:Log"
                    },
                    {
                        "type": "jump",
                        "name": lang_alarm,
                        "action": "jump:Alarm"
                    }
                ]
            }
        ]
    };

    return config;
}