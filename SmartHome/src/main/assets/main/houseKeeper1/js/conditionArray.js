/**
 * Created by Administrator on 2018/4/4.
 */
function createConditionArr() {
    window.conditionModel = {
        "03": {//门磁感应器
            "icon": "url(../../source/deviceIcon/device_icon_03.png)",
            "name": languageUtil.getResource("addScene_doorContack"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("setSceneState_txt02"),
                    "1": languageUtil.getResource("setSceneState_txt01")
                }
            },
            "conditionUrl":"setSceneState.html"
        },
        "17": {//温湿度监测器
            "icon": "url(../../source/deviceIcon/device_icon_17.png)",
            "name": languageUtil.getResource("addDevice_17_name"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "℃",
                    "> ": languageUtil.getResource("addHumiture1_txt1"),
                    "< ": languageUtil.getResource("addHumiture1_txt2")
                },
                "2": {
                    "unit": "%",
                    "> ": languageUtil.getResource("circumstances_txt14"),
                    "< ": languageUtil.getResource("circumstances_txt15")
                }
            },
            "conditionUrl":"setHumitureDevice.html"
        },
        "06": {//水浸监测器
            "icon": "url(../../source/deviceIcon/device_icon_06.png)",
            "name": languageUtil.getResource("addDevice_06_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt04"),
                    "1": languageUtil.getResource("warn_01")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "43": {//烟雾探测器
            "icon": "url(../../source/deviceIcon/device_icon_43.png)",
            "name": languageUtil.getResource("addDevice_43_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt04"),
                    "1": languageUtil.getResource("warn")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "A5": {//门铃按钮02型
            "icon": "url(../../source/deviceIcon/device_icon_A5.png)",
            "name": languageUtil.getResource("addDevice_A5_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt04"),
                    "1": languageUtil.getResource("houseKeeper1_04_pressed")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "A0": {//二氧化碳探测器
            "icon": "url(../../source/deviceIcon/device_icon_A0.png)",
            "name": languageUtil.getResource("device_name_A0"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "ppm",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "09": {//可燃气探测器
            "icon": "url(../../source/deviceIcon/device_icon_09.png)",
            "name": languageUtil.getResource("addDevice_09_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt04"),
                    "1": languageUtil.getResource("circumstances_txt11")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "42": {//二氧化碳探测器
            "icon": "url(../../source/deviceIcon/device_icon_42.png)",
            "name": languageUtil.getResource("device_name_42"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "ppm",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "D4": {//噪声监测器
            "icon": "url(../../source/deviceIcon/device_icon_D4.png)",
            "name": languageUtil.getResource("device_name_D4"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "dB",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "02": {//红外入侵探测器
            "icon": "url(../../source/deviceIcon/device_icon_02.png)",
            "name": languageUtil.getResource("addDevice_02_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt06"),
                    "1": languageUtil.getResource("circumstances_txt07"),
                    "0#": languageUtil.getResource("circumstances_txt08"),
                    "1#": languageUtil.getResource("circumstances_txt09")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "Og": {//PM2.5
            "icon": "url(../../source/deviceIcon/device_icon_Og.png)",
            "name": languageUtil.getResource("device_name_Og"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "ug/m3",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "44": {//粉尘监测器
            "icon": "url(../../source/deviceIcon/device_icon_44.png)",
            "name": languageUtil.getResource("device_name_44"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "ug/m3",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "04": {//紧急按钮
            "icon": "url(../../source/deviceIcon/device_icon_04.png)",
            "name": languageUtil.getResource("houseKeeper1_04_name"),
            "flag": "= ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt04"),
                    "1": languageUtil.getResource("houseKeeper1_04_pressed")
                }
            },
            "conditionUrl":"setSecurityDevice.html"
        },
        "D6": {//VOC探测器
            "icon": "url(../../source/deviceIcon/device_icon_D6.png)",
            "name": languageUtil.getResource("device_name_D6"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit":"ppm",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt03"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt04")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        },
        "19": {//光强检测器
            "icon": "url(../../source/deviceIcon/device_icon_19.png)",
            "name": languageUtil.getResource("device_19_name"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "LX",
                    "> ": languageUtil.getResource("device_LX_Above_02"),
                    "< ": languageUtil.getResource("device_LX_Below_02")
                }
            },
            "conditionUrl":"setLightIntensity.html"
        },
        "Ad": {//多功能红外探测器（电池版）不做为生效条件
            "icon": "url(../../source/deviceIcon/device_icon_Ad.png)",
            "name": languageUtil.getResource("device_name_Ad"),
            "flag": "> ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt06"),
                    "1": languageUtil.getResource("circumstances_txt07"),
                    "0#": languageUtil.getResource("circumstances_txt08"),
                    "1#": languageUtil.getResource("circumstances_txt09")
                },
                "2": {
                    "unit":"℃",
                    "> ": languageUtil.getResource("circumstances_txt12"),
                    "< ": languageUtil.getResource("circumstances_txt13")
                },
                "3": {
                    "unit":"%",
                    "> ": languageUtil.getResource("circumstances_txt14"),
                    "< ": languageUtil.getResource("circumstances_txt15")
                },
                "4": {
                    "unit": "LX",
                    "> ": languageUtil.getResource("device_LX_Above_02"),
                    "< ": languageUtil.getResource("device_LX_Below_02")
                }
            },
            "conditionUrl":"setHumitureDevice.html"
        },
        "C0": {//多功能红外探测器（强电版）不做为生效条件
            "icon": "url(../../source/deviceIcon/device_icon_C0.png)",
            "name": languageUtil.getResource("device_name_C0"),
            "flag": "> ",
            "action": {
                "1": {
                    "0": languageUtil.getResource("circumstances_txt06"),
                    "1": languageUtil.getResource("circumstances_txt07"),
                    "0#": languageUtil.getResource("circumstances_txt08"),
                    "1#": languageUtil.getResource("circumstances_txt09")
                },
                "2": {
                    "unit":"℃",
                    "> ": languageUtil.getResource("circumstances_txt12"),
                    "< ": languageUtil.getResource("circumstances_txt13")
                },
                "3": {
                    "unit":"%",
                    "> ": languageUtil.getResource("circumstances_txt14"),
                    "< ": languageUtil.getResource("circumstances_txt15")
                },
                "4": {
                    "unit": "LX",
                    "> ": languageUtil.getResource("device_LX_Above_02"),
                    "< ": languageUtil.getResource("device_LX_Below_02")
                }
            },
            "conditionUrl":"setHumitureDevice.html"
        },
        "D5": {//噪声监测器
            "icon": "url(../../source/deviceIcon/device_icon_D5.png)",
            "name": languageUtil.getResource("device_name_D5"),
            "flag": "> ",
            "action": {
                "1": {
                    "unit": "ppm",
                    "> ": languageUtil.getResource("setCarbonDioxide_txt01"),
                    "< ": languageUtil.getResource("setCarbonDioxide_txt02")
                }
            },
            "conditionUrl":"setcarbonDioxideDevice.html"
        }
    }
}
