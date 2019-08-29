/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceType":"Bw",
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
                    // {
                    //     "type": "jump",
                    //     "name": device_Bw_control,
                    //     "action": "jump:H5BridgeCommon",
                    //     "param":[
                    //         {
                    //             "key":"url",
                    //             "type":"string",
                    //             "value":"device/switch_Bw/html/linkageControl.html"
                    //         }
                    //     ]
                    // },
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