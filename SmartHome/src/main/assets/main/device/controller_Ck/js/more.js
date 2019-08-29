/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;

function getMoreConfig(devID, gwID) {
    var config = {
        "deviceID": devID,
        "data": [
            {
                "group": "device",
                "param": [{
                    "key": "deviceID",
                    "type": "string",
                    "value": devID
                }],
                "item": [
                    {
                        "type": "custom",
                        "name": "室内温度校准",// 室内温度校准
                        "action": "custom:Ck_Calibration_Indoor"
                    },
                    {
                        "type": "custom",
                        "name": "恢复出厂设置", // 出厂
                        "action": "custom:Ck_System_Reset"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}