/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(devID) {
    var config = {
        "deviceID": devID,
        "data": [
            // {
            //     "group": "log",
            //     "enableWithEnterType":"account",
            //     "param": [
            //         {
            //             "key": "deviceID",
            //             "type": "string",
            //             "value": devID
            //         }
            //     ],
            //     "item": [
            //         {
            //             "type": "jump",
            //             "name": lang_Alarm,
            //             "action": "jump:Alarm"
            //         }
            //     ]
            // }
            {
                "group": "dd",
                "param": [
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": devID
                    }
                ],
                "item": [
                    {
                        "type": "custom",
                        "name": "",//选择音源
                        "action": "custom:DD_Choice_Source",
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/music_DD/choice_source.html"
                            }
                        ]
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}