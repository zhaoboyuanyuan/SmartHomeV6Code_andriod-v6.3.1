/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;

function getMoreConfig(devID, gwID) {
    var config = {
        "deviceID": devID,
        "data": [{
            "group": "log",
            "enableWithEnterType": "account",
            "param": [{
                "key": "deviceID",
                "type": "string",
                "value": devID
            }],
            "item": [{
                "type": "jump",
                "name": lang_Alarm,
                "action": "jump:Alarm"
            }]
        },
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
                        "name": Device_Bx_Details_System, // 系统切换
                        "action": "custom:Bx_System_Choose",
                        "param": [{
                            "key": "sys", // 系统切换
                            "type": "string",
                            "value": "device/thermostat_Bx/system.html"
                        },
                            {
                                "key": "anti", // 防冻保护
                                "type": "string",
                                "value": "device/thermostat_Bx/antiFreezing.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": Device_Bx_Details_Return, // 回差设置
                        "action": "custom:Bx_Return_Setts"
                    },
                    {
                        "type": "custom",
                        "name": Device_Bx_Details_High_Protect, // 地面过温保护
                        "action": "custom:Bx_Return_protect"
                    },
                    {
                        "type": "custom",
                        "name": "室内温度校准",// 室内温度校准
                        "action": "custom:Bx_Calibration_Indoor"
                    },
                    {
                        "type": "custom",
                        "name": "地表温度校准",// 地表温度校准
                        "action": "custom:Bx_Calibration_Surface"
                    },
                    {
                        "type": "custom",
                        "name": "温度控制模式",// 温度控制模式
                        "action": "custom:Bx_Sensor_Setting"
                    }
                ]
            },
            {
                "group": "sys",
                "param": [{
                    "key": "deviceID",
                    "type": "string",
                    "value": devID
                }],
                "item": [{
                    "type": "custom",
                    "name": Device_Bx_Details_Restore, // 出厂
                    "action": "custom:Bx_System_Reset"
                }]
            }
        ]
    };

    moreConfig = config;
    return config;
}