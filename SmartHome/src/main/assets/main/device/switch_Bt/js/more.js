/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": deviceID,
        "data": [
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
                        "type": "custom",
                        "offLineDisable": true,
                        "name": "",//恢复断电前状态
                        "action": "custom:Bt_recover_status",
                    },
                    {
                        "type": "jump",
                        "name": lang_bt_control,
                        "action": "jump:H5BridgeCommon",
                        "offLineDisable": true,
                        "param":[
                            {
                                "key":"url",
                                "type":"string",
                                "value":"device/switch_Bt/html/socket_Bt_bind.html?devID="+deviceID
                            }
                        ]
                    },
                    {
                        "type": "jump",
                        "name": lang_Log,
                        "enableWithEnterType":"account",
                        "action": "jump:Log"
                    }
                ]
            }
        ]
    };
    moreConfig = config;
    return config;
}