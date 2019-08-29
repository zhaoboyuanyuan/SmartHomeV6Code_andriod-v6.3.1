/**
 * Created by Veev on 2017/6/9.
 */
var moreConfig;
function getMoreConfig(devID, gwID) {
    var config = {
        "deviceID": devID,
        "data": [
            {
                "group": "log",
                "enableWithEnterType":"account",
                "param": [
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": devID
                    }
                ],
                "item": [
                    {
                        "type": "jump",
                        "name": lang_Alarm,
                        "action": "jump:Alarm"
                    }
                ]
            },
            {
                "group": "device",
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
                        "name": Device_Bm_Details_System,// 系统切换
                        "action": "custom:Bm_System_Choose",
                        "param": [
                            {
                                "key": "sys",  // 系统切换
                                "type": "string",
                                "value": "device/thermostat_Bm/system.html"
                            },
                            {
                                "key": "anti",   // 防冻保护
                                "type": "string",
                                "value": "device/thermostat_Bm/antiFreezing.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": Device_Bm_Details_Return,// 回差设置
                        "action": "custom:Bm_Return_Setts"
                    },
                    {
                        "type": "custom",
                        "name": Device_Bm_Details_High_Protect,// 地表过温保护
                        "action": "custom:Bm_Over_Temp_Protection"
                    },
                    {
                        "type": "custom",
                        "name": "室内温度校准",// 室内温度校准
                        "action": "custom:Bm_Calibration_Indoor"
                    },
                    {
                        "type": "custom",
                        "name": "地表温度校准",// 地表温度校准
                        "action": "custom:Bm_Calibration_Surface"
                    },
                    {
                        "type": "custom",
                        "name": "选择传感器",// 选择传感器
                        "action": "custom:Bm_Sensor_Setting"
                    }
                ]
            },
            {
                "group": "sys",
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
                        "name": Device_Bm_Details_Restore,// 出厂
                        "action": "custom:Bm_System_Reset"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}