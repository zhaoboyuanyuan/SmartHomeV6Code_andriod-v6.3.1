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
                    // {
                    //     "type": "custom",
                    //     "name": "help",
                    //     "action": "custom:2013_help"
                    // },
                    // {
                    //     "type": "custom",
                    //     "offLineDisable": true,
                    //     "name": "",
                    //     "action": "custom:Ba_Set_Overload_Protection"
                    // },
                    {
                        "type": "custom",
                        "offLineDisable": true,
                        "name": "",
                        "action": "custom:Ba_Clear_Quantity"
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