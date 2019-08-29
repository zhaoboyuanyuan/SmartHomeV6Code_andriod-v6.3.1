/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(deviceID) {
    var config = {
        "deviceID": devID,
        "data": [
            {
                "group": "XW01",
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
                        "action": "custom:XW01_Choose_Source",
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/music_XW01/choose_source.html"
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