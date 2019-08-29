/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": deviceID,
        "data": [
            // {
            //     "group": "log",
            //     "enableWithEnterType":"account",
            //     "param": [
            //         {
            //             "key": "deviceID",
            //             "type": "string",
            //             "value": deviceID
            //         }
            //     ],
            //     "item": [
            //         {
            //             "type": "jump",
            //             "name": lang_Log,
            //             "action": "jump:Log"
            //         }
            //     ]
            // }
        ]
    };

    moreConfig = config;
    return config;
}