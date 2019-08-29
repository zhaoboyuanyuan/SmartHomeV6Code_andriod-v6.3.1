/**
 * Created by Veev on 2017/6/9.
 */

var leaveHomeButton = "离家按钮";

function getMoreConfig(deviceID, gwID) {
    var config = {
        "deviceID": deviceID,
        "deviceType": "Bq",
        "data": [
            {
                "group": "admin",
                "offLineDisable": true,
                "item": [
                    {
                        "type": "custom",
                        "name": moreSettingUserManger,
                        "action": "custom:DevBc_userManager",
                        "param": [
                            {
                                "key": "gwID",
                                "type": "string",
                                "value": gwID
                            },
                            {
                                "key": "devId",
                                "type": "string",
                                "value": deviceID
                            }
                        ]
                    },
                    {
                        "offLineDisable": true,
                        "type": "jump",
                        "name": moreSettingPush,
                        "action": "jump:DevBc_AlarmSetting",
                        "enableWithEnterType": "account",
                        "param": [
                            {
                                "key": "gwID",
                                "type": "string",
                                "value": gwID
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
                        ]
                    },
                    {
                        "type": "jump",
                        "name": "短信通知", /*短信通知*/
                        "action": "jump:Lock_Message",
                        "showWithEnterType": "account",
                        "showInLocale": "zh",
                        "param": [
                            {
                                "key": "url",
                                "type": "string",
                                "value": "SMSNotification/smsDist/sms.html"
                            },
                            {
                                "key": "gwID",
                                "type": "string",
                                "value": gwID
                            },
                            {
                                "key": "devId",
                                "type": "string",
                                "value": deviceID
                            }
                        ]
                    },
                    {
                        "type": "custom",
                        "name": moreSettingUserManger,
                        "action": "custom:touchid_unlock",
                        "param": [
                            {
                                "key": "gwID",
                                "type": "string",
                                "value": gwID
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
                "group": "bgLockRing", /*一键锁死及门铃免打扰*/
                "offLineDisable": true,
                "param": [
                    {
                        "key": "gwID",
                        "type": "string",
                        "value": gwID
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
                        "name": "bg_lock_ring",
                        "action": "custom:bg_lock_ring"
                    }
                ]
            },
            {
                "group": "bq",
                "param": [
                    {
                        "key": "gwID",
                        "type": "string",
                        "value": gwID
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
                "offLineDisable": true,
                "item": [
                    {
                        "type": "custom",
                        "name": moreSettingLeaveHomeBtn,
                        "action": "custom:LeaveHomeBtn"
                    }
                ]
            }
        ]
    };

    return config;
}