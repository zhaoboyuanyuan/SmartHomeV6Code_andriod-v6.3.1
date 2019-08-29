/**
 * Created by Administrator on 2017/11/7.
 * project:
 * 1.wulian
 * 2.5038
 */
window.addDeviceModel = {
    "zmartplus_app": [
        {
            //网关类设备
            "categoryType": "gateway",
            //name国际化key
            "name": "addScene_addgateway",
            //此字段无效，先暂时保存
            "href": "addDevice_gateway.html?type=gateway&project=wulian_app",
            //二级页面中的设备列表
            "deviceList": [
                {
                    //网关02型（局域网）
                    "deviceType": "GW12",
                    "deviceName": "addDevice_GW12_name",
                    "deviceHref": "addDevice_gateway_12_01.html",
                    "pinyin": "wangguan02xing",
                    "initialPY": "wg02x",
                    "english": "Gateway(Enhanced version)",
                    "initialE": "GEV"
                }
            ]
        }, {
            //门锁类设备
            "categoryType": "lock",
            "name": "addScene_Intelligentdoorlock",
            "href": "addDevice_lock.html?type=lock&project=wulian_app",
            "deviceList": [
                {
                    "deviceType": "OW",
                    "deviceName": "addDevice_OW_name",
                    "deviceHref": "DoorLock_OW_01.html",
                    "pinyin": "zhijiarenz8yunzhinengsuo",
                    "initialPY": "zjrz8zns",
                    "english": "Z8 cloud smart lock",
                    "initialE": "ZCSL"
                }
            ]
        }, {
            //摄像机类设备
            "categoryType": "camera",
            "name": "addScene_Videocamera",
            "href": "addDevice_camera.html?type=camera&project=wulian",
            "deviceList": [{
                "deviceType": "CMICA2",
                "deviceName": "Lookever",
                "pinyin": "suibiankan",
                "initialPY": "sbk",
                "english": "Lookever Camera",
                "initialE": "LC"
            },
                {
                    "deviceType": "CMICA1",
                    "deviceName": "addDevice_maoy",
                    "pinyin": "maoyan",
                    "initialPY": "my",
                    "english": "Door Guardian",
                    "initialE": "DG"
                }
            ]
        },
        {
            //开关类设备
            "categoryType": "switch",
            "name": "Adddevice_Switchgear",
            "href": "addDevice_switch.html?type=switch&project=wulian",
            "deviceList": [{
                "deviceType": "37",
                "deviceName": "addDevice_37_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "liuluchangjingkaiguan",
                "initialPY": "llcjkg",
                "english": "Scene Switch",
                "initialE": "SS"
            },
                {
                    "deviceType": "34",
                    "deviceName": "Device_34_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "sijianchangjingkaiguan",
                    "initialPY": "sjcjkg",
                    "english": "Scene Switch",
                    "initialE": "SS"
                },
                {
                    "deviceType": "Aj",
                    "deviceName": "addDevice_Aj_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "neiqianshilinghuoyilukaiguan",
                    "initialPY": "nqslhylkg",
                    "english": "Embedded Switch (1 gang)",
                    "initialE": "ES"
                },
                {
                    "deviceType": "Am",
                    "deviceName": "device_Am_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jinshudanlukaiguan",
                    "initialPY": "jsdlkg",
                    "english": "Metallic Switch (1 gang)",
                    "initialE": "MS"
                },
                {
                    "deviceType": "An",
                    "deviceName": "device_An_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jinshulianglukaiguan",
                    "initialPY": "jsllkg",
                    "english": "Metallic Switch (2 gangs)",
                    "initialE": "MS"
                },
                {
                    "deviceType": "Ao",
                    "deviceName": "device_Ao_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jinshusanlukaiguan",
                    "initialPY": "jsslkg",
                    "english": "Metallic Switch (3 gangs)",
                    "initialE": "MS"
                },
                {
                    "deviceType": "At",
                    "deviceName": "addDevice_At_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "neiqianshilinghuoerlukaiguan",
                    "initialPY": "nqslhelkg",
                    "english": "Embedded Switch (2 gangs)",
                    "initialE": "ES"
                },
                {
                    "deviceType": "61",
                    "deviceName": "addDevice_61_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "huwaigaoqingshexiangji",
                    "initialPY": "hwgqsxj",
                    "english": "Weather-proof HD Camera",
                    "initialE": "WHDC"
                },
                {
                    "deviceType": "62",
                    "deviceName": "addDevice_62_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianglukaiguan",
                    "initialPY": "llkg",
                    "english": "Two-way Switch",
                    "initialE": "TS"
                },
                {
                    "deviceType": "63",
                    "deviceName": "addDevice_63_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "sanlukaiguan",
                    "initialPY": "slkg",
                    "english": "Three-way Switch",
                    "initialE": "TS"
                },
                {
                    "deviceType": "Ai",
                    "deviceName": "addDevice_Ai_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yiweiDINjiliangkaiguan",
                    "initialPY": "ywkg",
                    "english": "Air Switch",
                    "initialE": "AS"
                },
                {
                    "deviceType": "12",
                    "deviceName": "addDevice_12_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "linghuoxiandanluchumotiaoguangkaiguan",
                    "initialPY": "lhxdlcmtgkg",
                    "english": "Smart dimmer switch (One gang， L)",
                    "initialE": "SDS"
                },
                {
                    "deviceType": "52",
                    "deviceName": "addDevice_52_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yilubangdingkaiguan",
                    "initialPY": "ylbdkg",
                    "english": "Binding switch (One gang)",
                    "initialE": "BS"
                },
                {
                    "deviceType": "54",
                    "deviceName": "addDevice_54_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "erlubangdingkaiguan",
                    "initialPY": "elbdkg",
                    "english": "Binding switch (Two gangs)",
                    "initialE": "BS"
                },
                {
                    "deviceType": "55",
                    "deviceName": "addDevice_55_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "sanlubangdingkaiguan",
                    "initialPY": "slbdkg",
                    "english": "Binding switch (Three gangs)",
                    "initialE": "BS"
                }
            ]
        },
        {
            //插座类设备
            "categoryType": "socket",
            "name": "addScene_addSocket",
            "href": "addDevice_socket.html?type=socket&project=wulian",
            "deviceList": [{
                "deviceType": "50",
                "deviceName": "addDevice_50_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "qiangmianchazuo",
                "initialPY": "qmcz",
                "english": "Wall Socket",
                "initialE": "WS"
            },
                {
                    "deviceType": "77",
                    "deviceName": "addDevice_77_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yidongchazuojiliangban",
                    "initialPY": "ydczjlb",
                    "english": "Mobile Socket (metering)",
                    "initialE": "MS"
                },
                {
                    "deviceType": "16",
                    "deviceName": "addDevice_16_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yidongchazuofeijiliangban",
                    "initialPY": "ydczfjlb",
                    "english": "",
                    "initialE": ""
                }
            ]
        },
        {
            //传感器类设备
            "categoryType": "security",
            "name": "Adddevice_Security_Equipment",
            "href": "addDevice_sensor.html?type=security&project=wulian",
            "deviceList": [{
                "deviceType": "03",
                "deviceName": "addDevice_03_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "menchuangqiganyingqi",
                "initialPY": "mccgyq",
                "english": "Contact Sensor",
                "initialE": "CS"
            },
                {
                    "deviceType": "43",
                    "deviceName": "addDevice_43_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yanwujianceqi",
                    "initialPY": "ywjcq",
                    "english": "Smoke Sensor",
                    "initialE": "SS"
                },
                {
                    "deviceType": "06",
                    "deviceName": "addDevice_06_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "shuijinjianceqi",
                    "initialPY": "sjjcq",
                    "english": "Water Leakage Sensor",
                    "initialE": "WLS"
                },
                {
                    "deviceType": "02",
                    "deviceName": "addDevice_02_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "hongwairuqintanceqi",
                    "initialPY": "hwrqtcq",
                    "english": "PIR Motion Sensor",
                    "initialE": "PMS"
                },
                {
                    "deviceType": "09",
                    "deviceName": "addDevice_09_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "keranqitiexieloutanceqi",
                    "initialPY": "krqtxltcq",
                    "english": "Flammable Gas Detector",
                    "initialE": "FGD"
                },
                {
                    "deviceType": "09",
                    "deviceIcon": "09_01",
                    "deviceName": "addScene_Freestanding_gasdetector_69",
                    "deviceHref": "addDevice_09Self_01.html",
                    "pinyin": "dulishikeranqititanceqi",
                    "initialPY": "dlskrqttcq",
                    "english": "Detached Flammable Gas Detector",
                    "initialE": "DFGD"
                },
                {
                    "deviceType": "43",
                    "deviceIcon": "43_01",
                    "deviceName": "addScene_Freestanding_Firedetectionalarm_43",
                    "deviceHref": "addDevice_43Self_01.html", //独立式光电感烟火灾探测报警器
                    "pinyin": "dulishiguangdianganyanhuozaitancejinbaoqi",
                    "initialPY": "dlsgdgyhztcbjq",
                    "english": "Detached Smoke Sensor",
                    "initialE": "DSS"
                }, {
                    "deviceType": "04",
                    "deviceName": "addDevice_04_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jinjianniu",
                    "initialPY": "jjan",
                    "english": "Panic Button",
                    "initialE": "PB"
                },
                {
                    "deviceType": "01",
                    "deviceName": "addScene_Acoustoopticalarm",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "shengguangbaojingqi",
                    "initialPY": "sgbjq",
                    "english": "Smart Siren",
                    "initialE": "SS"
                },
                {
                    "deviceType": "A6",
                    "deviceName": "addDevice_A6_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "menlingshengguangqi",
                    "initialPY": "mlsgq",
                    "english": "Doorbell",
                    "initialE": "D"
                },
                {
                    "deviceType": "A5",
                    "deviceName": "addDevice_A5_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "menlinganniu02xing",
                    "initialPY": "mlan02x",
                    "english": "Doorbell button",
                    "initialE": "DB"
                },
                {
                    "deviceType": "25",
                    "deviceName": "addDevice_25_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jixieshou",
                    "initialPY": "jxs",
                    "english": "",
                    "initialE": ""
                }
            ]
        },
        {
            //环境检测类设备
            "categoryType": "environment",
            "name": "Adddevice_Environmental_Monitoring",
            "href": "addDevice_environmentMonitoring.html?type=environment&project=wulian",
            "deviceList": [{
                "deviceType": "42",
                "deviceName": "addDevice_42_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "eryanghuatanjianceqi",
                "initialPY": "eyhtjcq",
                "english": "CO2 detector",
                "initialE": "CO2D"
            },
                {
                    "deviceType": "17",
                    "deviceName": "addDevice_17_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "wenshidujianceqi",
                    "initialPY": "wsdjcq",
                    "english": "Temp and Humi Sensor",
                    "initialE": "TAHS"
                },
                {
                    "deviceType": "Og",
                    "deviceName": "addDevice_Og_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "PM2.5jianceyi",
                    "initialPY": "pm2.5jcy",
                    "english": "",
                    "initialE": ""
                }
            ]
        },
        {
            //窗帘类设备
            "categoryType": "curtains",
            "name": "addScene_Windowcurtains",
            "href": "addDevice_curtains.html?type=curtains&project=wulian",
            "deviceList": [{
                "deviceType": "Ar",
                "deviceName": "addDevice_Ar_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "jinshuchuangliankongzhiqi",
                "initialPY": "jsclkzq",
                "english": "Metallic Curtain Controller",
                "initialE": "MCC"
            },
                {
                    "deviceType": "80",
                    "deviceName": "device_80_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "danguizidongchuanglian",
                    "initialPY": "dgzdcl",
                    "english": "Curtain Motor",
                    "initialE": "CM"
                },
                {
                    "deviceType": "65",
                    "deviceName": "addDevice_65_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "chuangliankongzhiqi02xing",
                    "initialPY": "clkzq02x",
                    "english": "Curtain Controller",
                    "initialE": "CC"
                },
                {
                    "deviceType": "81",
                    "deviceName": "addDevice_81_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "shuangguishuangquzidongchuanglian",
                    "initialPY": "sgsqzdcl",
                    "english": "Double-track & dual-driver curtain",
                    "initialE": "D&DC"
                }
            ]
        },
        {
            //控制器类设备
            "categoryType": "remote",
            "name": "Adddevice_Remote_Control",
            "href": "addDevice_infraredTransponder.html?type=remote&project=wulian",
            "deviceList": [{
                "deviceType": "23",
                "deviceName": "addScene_Infraredtransponder",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "hongwaizhuanfaqi",
                "initialPY": "hwzfq",
                "english": "IR Transmitter",
                "initialE": "IRT"
            }, {
                "deviceType": "22",
                "deviceName": "addDevice_22_name_1",
                "deviceIcon": "22_01",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "quanjiaoduhongwaizhuanfaqi",
                "initialPY": "qjdhwzfq",
                "english": "IR Transmitter",
                "initialE": "IRT"
            }, {
                "deviceType": "22",
                "deviceName": "addDevice_22_name_2",
                "deviceIcon": "22_02",
                "deviceHref": "addDevice_22_02_01.html",
                "pinyin": "dantoucefahongwaizhuanfaqi",
                "initialPY": "dtcfhwzfq",
                "english": "IR Transmitter",
                "initialE": "IRT"
            }, {
                "deviceType": "22",
                "deviceName": "addDevice_22_name_3",
                "deviceIcon": "22_03",
                "deviceHref": "addDevice_22_03_01.html",
                "pinyin": "dantoudingfahongwaizhuanfaqi",
                "initialPY": "dtdfhwzfq",
                "english": "IR Transmitter",
                "initialE": "IRT"
            }]
        },
        {
            //中继器类设备
            "categoryType": "repeatersort",
            "name": "Adddevice_Relay",
            "href": "addDevice_repeater01.html?type=repeatersort&project=wulian",
            "deviceList": [{
                "deviceType": "31",
                "deviceName": "addDevice_31_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "zhongjiqi",
                "initialPY": "zjq",
                "english": "Repeater",
                "initialE": "R"
            }]
        },
        {
            //控制器类设备
            "categoryType": "controller",
            "name": "Adddevice_Controller",
            "href": "addDevice_classify.html?type=camera&project=wulian",
            "deviceList": [{
                "deviceType": "Ap",
                "deviceName": "device_Ap_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "dinuankongzhiqi",
                "initialPY": "dnkzq",
                "english": "Heating Controller",
                "initialE": "HC"
            },
                {
                    "deviceType": "82",
                    "deviceName": "device_82_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "wenkongqi",
                    "initialPY": "wkq",
                    "english": "Controller",
                    "initialE": "C"
                }
            ]
        },
        {
            //控制器类设备
            "categoryType": "bgm",
            "name": "Adddevice_Music",
            "href": "addDevice_classify.html?type=camera&project=wulian",
            "deviceList": [{
                "deviceType": "DD",
                "deviceName": "device_DD_name",
                "deviceHref": "addDevice_DD_01.html",
                "pinyin": "quanzhaibeijingyinyue",
                "initialPY": "qzbjyy",
                "english": "todo",
                "initialE": "todo"
            }
            ]
        }],
};

function getcategoryList(project, ele) {
    $(ele).html("");
    for (var i = 0; i < window.addDeviceModel[project].length; i++) {
        var a = '<a href="addDevice_classify.html?type=' + window.addDeviceModel[project][i].categoryType + '&project=' + project + '"><i style="background:url(../../source/deviceIcon/icon_' + window.addDeviceModel[project][i].categoryType + '.png) no-repeat center center;background-size:3.4rem auto;"></i><span class="autoSwitchLanguager" id="' + window.addDeviceModel[project][i].name + '"></span></a>';
        $(ele).append(a);
    }
}

function getDeviceList(project, categoryType, ele, ele2) {
    $(ele).html("");
    var categoryList = window.addDeviceModel[project];
    for (var i in categoryList) {
        if (categoryList[i].categoryType == categoryType) {
            $(ele2).html(languageUtil.getResource(categoryList[i].name));
            var deviceList = categoryList[i].deviceList;
            for (var j in deviceList) {
                var icon = deviceList[j].deviceType;
                if (deviceList[j].deviceIcon) {
                    icon = deviceList[j].deviceIcon;
                }
                var a = '<a onclick="jump(\'' + deviceList[j].deviceHref + '\',\'' + deviceList[j].deviceType + '\',\'' + deviceList[j].deviceHandle + '\')"><i style="background:url(../../source/deviceIcon/device_icon_' + icon + '.png) no-repeat left center;background-size:3.4rem auto;"></i><span class="autoSwitchLanguager" id="' + deviceList[j].deviceName + '">' + languageUtil.getResource(deviceList[j].deviceName) + '</span></a>'
                $(ele).append(a);
            }
        }
    }
}

function getSearchList(project, ele) {
    $(ele).find("ul").html("");
    var list = window.addDeviceModel[project];
    for (var i in list) {
        var deviceList = list[i].deviceList;
        for (var j in deviceList) {
            var icon = deviceList[j].deviceType;
            if (deviceList[j].deviceIcon) {
                icon = deviceList[j].deviceIcon;
            }
            var li = '<a href="javascript:;" pinyin="' + deviceList[j].pinyin + '" initialPY="' + deviceList[j].initialPY + '" english="' + deviceList[j].english + '" initialE="' + deviceList[j].initialE + '" onclick="jump(\'' + deviceList[j].deviceHref + '\',\'' + deviceList[j].deviceType + '\',\'' + list[i].categoryType + '\')"><i class="left" style="background:url(../../source/deviceIcon/device_icon_' + icon + '.png) no-repeat left center;background-size:4rem auto;"></i><span class="autoSwitchLanguager gateway" id="' + deviceList[j].deviceName + '">' + languageUtil.getResource(deviceList[j].deviceName) + '</span></a>'
            $(ele).find("div").append(li);
        }
    }
}