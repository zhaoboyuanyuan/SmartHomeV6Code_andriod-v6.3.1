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
                        "name": lang_custom_02,
                        "action": "custom:At_Recover_State",
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
                        "action": "custom:At_Bind_Mode",
                        "param": [
                            {
                                "key": "desc",
                                "type": "string",
                                "value": lang_custom_03_desc_01
                            },{
                                "key": "url",
                                "type": "string",
                                "value": "device/switch_At/openBindMode.html"
                            }
                        ],
                    },
                    {
                        "type": "jump",
                        "name": lang_sms,
                        "action": "jump:Log"
                    }
                ]
            }
        ]
    };

    return config;
}