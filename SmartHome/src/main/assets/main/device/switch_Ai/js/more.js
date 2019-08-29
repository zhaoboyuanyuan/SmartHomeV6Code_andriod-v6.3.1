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
                        "offLineDisable": true,
                        "name": "",//恢复断电前状态
                        "action": "custom:ai_recover_status",
                    },
                    {
                        "type": "custom",
                        "offLineDisable": true,
                        "name": lang_custom_02,//过载保护设置
                        "action": "custom:Set_Overload_Protection",
                    },
                    {
                        "type": "custom",
                        "offLineDisable": true,
                        "name": lang_custom_01,
                        "action": "custom:Aj_Clear_Quantity"
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