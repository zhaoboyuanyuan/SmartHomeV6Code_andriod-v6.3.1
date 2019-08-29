/**
 * Created by Veev on 2017/6/9.
 */
function getMoreConfig(deviceID,gwID) {
    var config = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "sleep_mode",
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
                ]
            }
        ]
    };

    return config;
}