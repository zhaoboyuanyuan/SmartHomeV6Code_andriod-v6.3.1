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
                        "action": "custom:Ap_System_Choose",
                        "param": [
                            {
                                "key": "sys",  // 系统切换
                                "type": "string",
                                "value": "device/thermostat_Ap/system.html"
                            },
                            {
                                "key": "anti",   // 防冻保护
                                "type": "string",
                                "value": "device/thermostat_Ap/antiFreezing.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": Device_Ap_Details_Scale,// 温标
                        "action": "custom:Ap_Temp_Scale",
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/thermostat_Ap/tempScale.html"//?devID=" + devID + "&gwID=" + gwID
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": Device_Bm_Details_Return,// 回差设置
                        "action": "custom:Ap_Return_Setts"
                    },
                    {
                        "type": "custom",
                        "name": Device_Bm_Details_Save,// 节能
                        "action": "custom:Ap_Save_Energy"
                    },
                    {
                        "type": "jump",
                        "name": Device_Bm_Details_High_Protect,// 地面过温保护
                        "offLineDisable":true,
                        "action": "jump:H5BridgeCommon",
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "device/thermostat_Ap/groundTemp.html"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": Device_Af_Details_Sound,// 设备按键声音
                        "action": "custom:Ap_Button_Sound"
                    },
                    {
                        "type": "custom",
                        "name": Device_Af_Details_Shock,// 设备按键震动
                        "action": "custom:Ap_Button_Shock"
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
                        "action": "custom:Ap_System_Reset"
                    }
                ]
            }
        ]
    };

    moreConfig = config;
    return config;
}