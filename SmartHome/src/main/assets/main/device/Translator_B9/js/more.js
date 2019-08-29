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
                        "type": "custom",
                        "name": "",
                        "action": "custom:B9_input_output_setting",
                        "param": [
                            {
                                "key": "desc",
                                "type": "string",
                                "value": ""
                            }
                        ],
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}