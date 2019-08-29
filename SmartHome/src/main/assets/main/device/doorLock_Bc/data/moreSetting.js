var moreSettingData;
function getMoreConfig(gwID, deviceID) {
    moreSettingData = {
        "deviceID": deviceID,
        "data": [
            {
                "group": "bc",
                "param": [
                    {
                        "key":"gwID",
                        "type":"string",
                        "value":gwID
                    },
                    {
                        "key": "devId",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "deviceId",
                        "type": "string",
                        "value": deviceID
                    }
                ],
                "item": [
                    {
                        "type": "jump",
                        "name": moreSetting02,/*抓拍图库*/
                        "action": "jump:DevBc_CaptureImages"
                    },
                    {
                        "type": "jump",
                        "name": moreSetting01,/*访客记录*/
                        "action": "jump:DevBc_VisitorsNotes",
                        "param": [
                            {
                                "key":"devType",
                                "type":"string",
                                "value":"Bc"
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": moreSetting03,/*wifi配置*/
                        "action": "custom:DevBc_wifiSetting"
                    }
                ]
            },
            {
            	"group": "bc_leavehome",
                "param": [
                    {
                        "key":"gwID",
                        "type":"string",
                        "value":gwID
                    },
                    {
                        "key": "devId",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "deviceId",
                        "type": "string",
                        "value": deviceID
                    }
                ],
                "item": [
                    {
                        "type": "custom",
                        "name": "Bd_Door_Panel",
                        "action": "custom:Bd_Door_Panel"
                    },
                    {
                        "type": "custom",
                        "name": moreSetting04,/*离家按钮*/
                        "action": "custom:LeaveHomeBtn"
                    }
                ]
            },
            {
                "group": "admin",
                "offLineDisable": true,                                
                "item": [
                    {
                        "type": "custom",
                        "name": moreSetting05,/*用户管理*/
                        "action": "custom:DevBc_userManager",
                        "param": [
                            {
		                        "key":"gwID",
		                        "type":"string",
		                        "value":gwID
		                    },
		                    {
		                        "key": "devId",
		                        "type": "string",
		                        "value": deviceID
		                    }
                        ]
                    },
                    {
                        "type": "jump",
                        "name": "短信通知",/*短信通知*/
                        "action": "jump:Lock_Message",
                        "showWithEnterType": "account",
                        "showInLocale":"zh",
                        "param": [
                            {
                             "key": "url",
                             "type": "string",
                             "value": "SMSNotification/smsDist/sms.html"
                         	},
                            {
		                        "key":"gwID",
		                        "type":"string",
		                        "value":gwID
		                    },
		                    {
		                        "key": "devId",
		                        "type": "string",
		                        "value": deviceID
		                    }
                        ]
                    }
                ]
            },
            {
                "group": "log",
                "param": [
                	{
                        "key":"gwID",
                        "type":"string",
                        "value":gwID
                    },
                    {
                        "key": "devId",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "deviceID",
                        "type": "string",
                        "value": deviceID
                    }
                ],
                "item": [
                	{
                        "type": "jump",
                        "name": moreSetting06,/*报警消息*/
                        "action": "jump:Alarm_Bc",
                        "showWithEnterType": "account",
                         "param": [
                            {
                             "key": "msgType",
                             "type": "string",
                             "value": "type_alarm"
                         }
                        ]
                    },
                    {
                        "type": "jump",
                        "name": moreSetting07,/*日志*/
                        "action": "jump:Log",
                        "showWithEnterType": "account"
                    },
                    {
                    	"offLineDisable":true,
                        "type": "jump",
                        "name": moreSetting08,/*报警设置*/
                        "action": "jump:DevBc_AlarmSetting"
                    }
                ]
            },
            {
            	"group": "bc_dev",
                "param": [
                    {
                        "key":"gwID",
                        "type":"string",
                        "value":gwID
                    },
                    {
                        "key": "devId",
                        "type": "string",
                        "value": deviceID
                    },
                    {
                        "key": "deviceId",
                        "type": "string",
                        "value": deviceID
                    }
                ],
                "item": [                    
                    {
                        "type": "jump",
                        "name": moreSetting09,/*设备信息*/
                        "action": "jump:DevBc_deviceInfo"
                    },
                ]
            }
        ]
    };
}
