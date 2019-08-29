/**
 * Created by Administrator on 2017/11/7.
 * project:
 * 1.wulian
 * 2.5038
 */
window.addDeviceModel = {
    "wulian_app": [{
        //网关类设备
        "categoryType": "gateway",
        //name国际化key
        "name": "addScene_addgateway",
        //此字段无效，先暂时保存
        "href": "addDevice_gateway.html?type=gateway&project=wulian_app",
        //二级页面中的设备列表
        "deviceList": [{
            //竖型网关
            "deviceType": "GW01",
            //设备名称的国际化key
            "deviceName": "addScene_gateway",
            //二级页面中设备跳转的下一级页面
            "deviceHref": "addGateway01.html",
            //搜索页中设备名称的拼音
            "pinyin": "shuxingwangguan",
            //搜索页中设备名称的拼音首字母
            "initialPY": "sxwg",
            //搜索页中设备名称的英文
            "english": "VerticalGateway",
            //搜索页中设备名称的英文首字母
            "initialE": "VG"
        },
            {
                //02型增强网关
                "deviceType": "GW06",
                "deviceName": "Wulian02_Gateway",
                "deviceHref": "addDevice_gateway_02_01.html",
                "pinyin": "zengqiangxingwangguan",
                "initialPY": "zqxwg",
                "english": "Gateway(Enhanced version)",
                "initialE": "GEV"
            },
            {
                //网关02型（局域网）
                "deviceType": "GW12",
                "deviceName": "addDevice_GW12_name",
                "deviceHref": "addDevice_gateway_12_01.html",
                "pinyin": "wangguan02xing",
                "initialPY": "wg02x",
                "english": "Gateway(Enhanced version)",
                "initialE": "GEV"
            },
            {
                //梦想之花网关
                "deviceType": "GW09",
                "deviceName": "addDevice_GW09_name",
                "deviceHref": "addDevice_gateway_09_01.html",
                "pinyin": "mengxiangzhihuawangguan",
                "initialPY": "mxzhwg",
                "english": "VerticalGateway",
                "initialE": "GEV"
            },
            {
                //吸顶网关
                "deviceType": "GW11",
                "deviceName": "Wulian03_Gateway",
                "deviceHref": "addDevice_gateway_11_01.html",
                "pinyin": "xidingwangguan",
                "initialPY": "xdwg",
                "english": "todo",
                "initialE": "todo"
            },
            {
                //吸顶网关
                "deviceType": "GW15",
                "deviceName": "device_GW15_name",
                "deviceHref": "addDevice_gateway_15_01.html",
                "pinyin": "weixinxiangkuangwangguan",
                "initialPY": "wxxk",
                "english": "todo",
                "initialE": "todo"
            },
            {
                //吸顶网关
                "deviceType": "GW14",
                "deviceName": "device_GW14_name",
                "pinyin": "litterwangguan",
                "initialPY": "litterwg",
                "english": "",
                "initialE": "todo"
            },
            {
                //吸顶网关
                "deviceType": "GWv5",
                "deviceName": "Device_upgrade_Entrance",
                "pinyin": "litterwangguan",
                "initialPY": "litterwg",
                "english": "",
                "initialE": "todo"
            }
        ]
    },
        {
            //门锁类设备
            "categoryType": "lock",
            "name": "addScene_Intelligentdoorlock",
            "href": "addDevice_lock.html?type=lock&project=wulian_app",
            "deviceList": [{
                "deviceType": "Bn",
                "deviceName": "device_Bn_name",
                "deviceHref": "addDevice_Bn_01.html",
                "pinyin": "Wu-Lock Crown Plus",
                "initialPY": "WLCP",
                "english": "Wu-Lock Crown Plus",
                "initialE": "WLCP"
            }, {
                "deviceType": "Bq",
                "deviceName": "device_Bq_name",
                "deviceHref": "addDevice_Bq_01.html",
                "pinyin": "Wu-Lock Crown",
                "initialPY": "wls",
                "english": "Wu-Lock Crown",
                "initialE": "WC"
            }, {
                "deviceType": "Bg",
                "deviceName": "addDevice_Bg_name",
                "deviceHref": "addDevice_Bg_01.html",
                "pinyin": "dongtinghusuo",
                "initialPY": "dths",
                "english": "Bern Serial IoT Doorlock",
                "initialE": "BSID"
            }, {
                "deviceType": "Bc",
                "deviceName": "Device_List_Networklock",
                "deviceHref": "DoorLock_Bc_01.html",
                "pinyin": "wangluozhinengsuo",
                "initialPY": "wlzns",
                "english": "IoT Smart Lock",
                "initialE": "ISL"
            }, {
                "deviceType": "70",
                "deviceIcon": "70_01",
                "deviceName": "addScene_doorLockVocFingerprint",
                "deviceHref": "addDoorLockVOCFingerprint1.html",
                "pinyin": "zhiwenmimamensuo",
                "initialPY": "zwmmms",
                "english": "Password & Fingerprint Lock",
                "initialE": "PFL"
            }, {
                "deviceType": "70",
                "deviceIcon": "70_02",
                "deviceName": "addScene_doorLockVocCard",
                "deviceHref": "addDoorLockVOCCard1.html",
                "pinyin": "mimakasuo",
                "initialPY": "mmks",
                "english": "Password & Card Lock",
                "initialE": "PCL"
            },
                {
                    "deviceType": "Bd",
                    "deviceName": "addDevice_Bd_title",
                    "deviceHref": "DoorLock_Bd_01.html",
                    "pinyin": "wangluosuo",
                    "initialPY": "wls",
                    "english": "IoT Lock",
                    "initialE": "IL"
                },
                {
                    "deviceType": "Bf",
                    "deviceName": "addDevice_Bf_title",
                    "deviceHref": "addDevice_Bf_01.html",
                    "pinyin": "wangluosuo",
                    "initialPY": "wls",
                    "english": "IoT Lock 02",
                    "initialE": "IL02"
                }, {
                    "deviceType": "OP",
                    "deviceName": "addDevice_op",
                    "deviceHref": "addDevice_op_01.html",
                    "pinyin": "xuanwuhuwangluosuo",
                    "initialPY": "xwhwls",
                    "english": "Xuan Serial IoT Smart Lock",
                    "initialE": "XSISL"
                },
                {
                    "deviceType": "OW",
                    "deviceName": "addDevice_OW_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zijinshansuo",
                    "initialPY": "zjss",
                    "english": "Z Serial IoT Lock",
                    "initialE": "ZSIL"
                }, {
                    "deviceType": "69",
                    "deviceName": "Device_69_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "jingmianchupingbolimensuo",
                    "initialPY": "jmcpblms",
                    "english": "Smart Touch Type Glass Lock",
                    "initialE": "STTGL"
                },
            ]
        },
        {
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
                    "deviceType": "CMICA3",
                    "deviceName": "addScene_penguin",
                    "pinyin": "qieshexiangji",
                    "initialPY": "qesxj",
                    "english": "Smart Cloud Camera",
                    "initialE": "SCC"
                },
                {
                    "deviceType": "CMICA1",
                    "deviceName": "addDevice_maoy",
                    "pinyin": "maoyan",
                    "initialPY": "my",
                    "english": "Door Guardian",
                    "initialE": "DG"
                },
                {
                    "deviceType": "CMICY1",
                    "deviceName": "Device_Default_Name_CMICY1",
                    "pinyin": "zhinengmaoyanmini",
                    "initialPY": "znmymn",
                    "english": "Ding-dong Mini",
                    "initialE": "DM"
                },
                {
                    "deviceType": "CMICA4",
                    "deviceName": "Cylincam",
                    "pinyin": "xiaowushexiangji",
                    "initialPY": "xwsxj",
                    "english": "Cylincam",
                    "initialE": "C"
                },
                {
                    "deviceType": "CMICA5",
                    "deviceName": "OutdoorCameraName",
                    "pinyin": "huwaigaoqingshexiangji",
                    "initialPY": "hwgqsxj",
                    "english": "Weather-proof HD Camera",
                    "initialE": "WHDC"
                }
                // ,
                // {
                //     "deviceType": "CG27",
                //     "deviceName": "device_CG27_name",
                //     "pinyin": "keshimenjin",
                //     "initialPY": "ksmj",
                //     "english": "",
                //     "initialE": ""
                // }
                // {
                //     "deviceType": "CG22",
                //     "deviceName": "device_CG22_name",
                //     "pinyin": "fangchenfangshuihuwaishexiangji",
                //     "initialPY": "fcfshwsxj",
                //     "english": "",
                //     "initialE": ""
                // },
                // {
                //     "deviceType": "CG23",
                //     "deviceName": "device_CG23_name",
                //     "pinyin": "fangchenfangshuihuwaishexiangji",
                //     "initialPY": "fcfshwsxj",
                //     "english": "",
                //     "initialE": ""
                // },
                // {
                //     "deviceType": "CG24",
                //     "deviceName": "device_CG24_name",
                //     "pinyin": "quanjingyuntaishexiangji",
                //     "initialPY": "qjytsxj",
                //     "english": "",
                //     "initialE": ""
                // },
                // {
                //     "deviceType": "CG25",
                //     "deviceName": "device_CG25_name",
                //     "pinyin": "quanjingyuntaishexiangji",
                //     "initialPY": "qjytsxj",
                //     "english": "",
                //     "initialE": ""
                // }
            ]
        },
        {
            //开关类设备
            "categoryType": "switch",
            "name": "Adddevice_Switchgear",
            "href": "addDevice_switch.html?type=switch&project=wulian",
            "deviceList": [
                // {
                //     "deviceType": "Cl",
                //     "deviceName": "device_Cl_name",
                //     "deviceHref": "addDevice_Common_01.html",
                //     "pinyin": "yiweizhinengkaiguan",
                //     "initialPY": "ywmlxznkg",
                //     "english": "One-way Switch",
                //     "initialE": "OWS"
                // },
                // {
                //     "deviceType": "Cm",
                //     "deviceName": "device_Cm_name",
                //     "deviceHref": "addDevice_Common_01.html",
                //     "pinyin": "erweizhinengkaiguan",
                //     "initialPY": "ewmlxznkg",
                //     "english": "Two-way Switch",
                //     "initialE": "TWS"
                // },
                // {
                //     "deviceType": "Cn",
                //     "deviceName": "device_Cn_name",
                //     "deviceHref": "addDevice_Common_01.html",
                //     "pinyin": "sanweizhinengkaiguan",
                //     "initialPY": "swznkg",
                //     "english": "Three-way Switch",
                //     "initialE": "TWS"
                // },
                {
                    "deviceType": "37",
                    "deviceName": "addDevice_37_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "liuluchangjingkaiguan",
                    "initialPY": "llcjkg",
                    "english": "Scene Switch",
                    "initialE": "SS"
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
                    "deviceType": "Bu_01",
                    "deviceName": "device_Bu_01_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhiyueyilukaiguan",
                    "initialPY": "zyylkg",
                    "english": "Zhi Yue One-way Switch",
                    "initialE": "ZYOS"
                },
                {
                    "deviceType": "Bv_01",
                    "deviceName": "device_Bv_01_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhiyueerlukaiguan",
                    "initialPY": "zyelkg",
                    "english": "Zhi Yue Two-way Switch",
                    "initialE": "ZYTS"
                },
                {
                    "deviceType": "Bw_01",
                    "deviceName": "device_Bw_01_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhiyuesanlukaiguan",
                    "initialPY": "zyslkg",
                    "english": "Zhi Yue Three-way Switch",
                    "initialE": "ZYTS"
                },
                {
                    "deviceType": "Bu",
                    "deviceIcon": "Bu_02",
                    "deviceName": "device_Bu_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijinglianpaiyilukaiguan",
                    "initialPY": "zjlpylkg",
                    "english": "Multiple series one gang switch",
                    "initialE": "MSOGS"
                },
                {
                    "deviceType": "Bv",
                    "deviceIcon": "Bv_02",
                    "deviceName": "device_Bv_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaierlukaiguan",
                    "initialPY": "lpelkg",
                    "english": "Multiple series two gang switch",
                    "initialE": "MSTGS"
                },
                {
                    "deviceType": "Bw",
                    "deviceIcon": "Bw_02",
                    "deviceName": "device_Bw_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaisanlukaiguan",
                    "initialPY": "lpslkg",
                    "english": "Multiple series three gang switch",
                    "initialE": "MSTGS"
                },
                {
                    "deviceType": "Cc",
                    "deviceName": "device_Cc_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijingxilietiaoguangkaiguan",
                    "initialPY": "zzxltgkg",
                    "english": "",
                    "initialE": ""
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
                    "deviceType": "Cp",
                    "deviceName": "device_Cp_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaixingsijianchangjingkaiguan",
                    "initialPY": "lpxsjcjkg",
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
                    "deviceType": "At",
                    "deviceName": "addDevice_At_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "neiqianshilinghuoerlukaiguan",
                    "initialPY": "nqslhelkg",
                    "english": "Embedded Switch (2 gangs)",
                    "initialE": "ES"
                },
                {
                    "deviceType": "62",
                    "deviceIcon": "62_01",
                    "deviceName": "device_62_name_01",
                    "deviceHref": "addDevice_62_01_01.html",
                    "pinyin": "neiqianshilinghuoerlukaiguan",
                    "initialPY": "nqslhelkg",
                    "english": "Smart Embedded Switch",
                    "initialE": "SES"
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
                },
                {
                    "deviceType": "90",
                    "deviceName": "device_90_name",
                    "deviceHref": "addDevice_90_01.html",
                    "pinyin": "ledxuancaideng",
                    "initialPY": "ledxcd",
                    "english": "",
                    "initialE": ""
                },
                {
                    "deviceType": "91",
                    "deviceName": "device_91_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "tiaosewentiaoguangleddeng",
                    "initialPY": "tswtgledd",
                    "english": "",
                    "initialE": ""
                },

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
                    "english": "Mobile Socket（Non metrology）",
                    "initialE": "MS"
                },
                {
                    "deviceType": "Bt",
                    "deviceName": "device_Bt_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "yiweiqiangmianchazuo",
                    "initialPY": "ywqmcz",
                    "english": "Wall Socket",
                    "initialE": "WS"
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
                },
                {
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
                    "english": "Electric Manipulator",
                    "initialE": "EM"
                },
                {
                    "deviceType": "a1",
                    "deviceName": "Device_a1_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "muliantanceqi",
                    "initialPY": "mltcq",
                    "english": "Bi-directional IR Motion Detector",
                    "initialE": "BDIMD"
                },
                {
                    "deviceType": "C0",
                    "deviceName": "Device_C0_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "duogongnenghongwairentitanceqi",
                    "initialPY": "dgnhwrttcq",
                    "english": "Multifunctional motion detector (AC)",
                    "initialE": "MMD"
                },
                {
                    "deviceType": "Ad",
                    "deviceName": "Device_Ad_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "duogongnenghongwairentitanceqi",
                    "initialPY": "dgnhwrttcq",
                    "english": "Multifunctional motion detector (DC)",
                    "initialE": "MMD"
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
                    "deviceType": "09",
                    "deviceName": "addDevice_09_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "keranqitiexieloutanceqi",
                    "initialPY": "krqtxltcq",
                    "english": "Flammable Gas Detector",
                    "initialE": "FGD"
                },
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
                    "deviceType": "19",
                    "deviceName": "device_19_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "guangqiangjianceqi",
                    "initialPY": "gqjcq",
                    "english": "Light intensity monitor",
                    "initialE": "LIM"
                },
                {
                    "deviceType": "Og",
                    "deviceName": "addDevice_Og_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "PM2.5jianceyi",
                    "initialPY": "pm2.5jcy",
                    "english": "PM2.5 Detector Device",
                    "initialE": "PMDD"
                },
                {
                    "deviceType": "44",
                    "deviceName": "Device_44_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "fenchenjianceqi",
                    "initialPY": "fcjcq",
                    "english": "Dust Detector",
                    "initialE": "DD"
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
                    "deviceType": "Co",
                    "deviceName": "device_Co_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "dtzhinengkaihelian",
                    "initialPY": "dtznkhl",
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
                },
                {
                    "deviceType": "Au",
                    "deviceName": "Device_Au_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "neiqianshichuangliankongzhiqi",
                    "initialPY": "nqsclkzq",
                    "english": "smart embedded curtain switch",
                    "initialE": "secs"
                },
                {
                    "deviceType": "80",
                    "deviceName": "addDevice_G50_name",
                    "deviceHref": "addDevice_80_G50_01.html",
                    "pinyin": "G50zhinengkaihechuanglian",
                    "initialPY": "znkhcl",
                    "english": "Curtain Motor",
                    "initialE": "CM"
                },
                {
                    "deviceType": "Ca",
                    "deviceName": "device_Ca_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijingyiluchuanglian",
                    "initialPY": "zjylcl",
                    "english": "",
                    "initialE": ""
                },
                {
                    "deviceType": "Cb",
                    "deviceName": "device_Cb_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijingerluchufakaiguan",
                    "initialPY": "tswtgledd",
                    "english": "",
                    "initialE": ""
                },
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
            }, {
                "deviceType": "24",
                "deviceName": "Device_24_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "wannenghongwaizhuanfaqi",
                "initialPY": "wnhwzfq",
                "english": "IR Transmitter",
                "initialE": "IRT"
            }, {
                "deviceType": "IF02",
                "deviceName": "IF01_name",
                "pinyin": "wifihongwaizhuanfa",
                "initialPY": "wifihwzf",
                "english": "IR Transmitter(WIFI)",
                "initialE": "IRT"
            }, {
                "deviceType": "38",
                "deviceName": "device_38_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "xiaoxingsijianyaokongqi",
                "initialPY": "xxsjykq",
                "english": "Small four key remote control",
                "initialE": "SFKRC"
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
            "deviceList": [
                {
                    "deviceType": "Bx",
                    "deviceName": "device_Bx_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaidinuankongzhiqi",
                    "initialPY": "LPDNKZQ",
                    "english": "Row heating controller",
                    "initialE": "RHC"
                },
                {
                    "deviceType": "By",
                    "deviceName": "device_By_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijingkongtiaokongzhiqi",
                    "initialPY": "ZJKTKZQ",
                    "english": "Controller",
                    "initialE": "C"
                },
                // ,{
                //     "deviceType": "Bp",
                //     "deviceName": "device_Bp_name",
                //     "deviceHref": "addDevice_Common_01.html",
                //     "pinyin": "",
                //     "initialPY": "",
                //     "english": "",
                //     "initialE": ""
                // }
                {
                    "deviceType": "Ck",
                    "deviceName": "device_Ck_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhijingxinfengkongzhiqi",
                    "initialPY": "ZJXFKZQ",
                    "english": "Controller",
                    "initialE": "C"
                }, {
                    "deviceType": "Ap",
                    "deviceName": "device_Ap_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "dinuankongzhiqi",
                    "initialPY": "dnkzq",
                    "english": "Heating Controller",
                    "initialE": "HC"
                },
                {
                    "deviceType": "Br",
                    "deviceIcon": "Br",
                    "deviceName": "device_Br_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaiwenkongqi",
                    "initialPY": "lpwkq",
                    "english": "Combined temperature controller",
                    "initialE": "FCFHT"
                },
                {
                    "deviceType": "Ol",
                    "deviceName": "device_Ol_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "fengpandinuanwenkongqi",
                    "initialPY": "fpdnwkq",
                    "english": "fancoilfloorheatingthermostat",
                    "initialE": "FCFHT"
                },
                {
                    "deviceType": "On",
                    "deviceName": "device_On_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "quanrejiaohuanxingxinfengxitong",
                    "initialPY": "qrjhxxfxt",
                    "english": "Heattransferventilationsystem",
                    "initialE": "HTVS"
                }, {
                    "deviceType": "Af",
                    "deviceName": "device_Af_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "fengjipanguanwendukongzhiqi",
                    "initialPY": "FJPGWDKZQ",
                    "english": "Temperature Controller",
                    "initialE": "TC"
                }, {
                    "deviceType": "a0",
                    "deviceIcon": "a0_01",
                    "deviceName": "device_a0_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhongyangkongtiao",
                    "initialPY": "ZYKT",
                    "english": "Centralairconditioning(P board+translator)",
                    "initialE": "CAC"
                },
                {
                    "deviceType": "Op",
                    "deviceIcon": "Op_1",
                    "deviceName": "device_Op_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhonghongkongtiaokongzhiqi",
                    "initialPY": "ZHKTKZQ",
                    "english": "Controller",
                    "initialE": "C"
                },
                {
                    "deviceType": "Cj",
                    "deviceName": "device_Cj_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "lianpaidinuankongzhiqi",
                    "initialPY": "LPDNKZQ",
                    "english": "Row heating controller",
                    "initialE": "RHC"
                },
                {
                    "deviceType": "Oj",
                    "deviceName": "device_Oj_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "xinfengkongzhiqi",
                    "initialPY": "xfkzq",
                    "english": "Smart Clothes Hanger",
                    "initialE": "SCH"
                },
                {
                    "deviceType": "OF",
                    "deviceName": "device_OF_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhinengliangyiji",
                    "initialPY": "znlyj",
                    "english": "Smart Clothes Hanger",
                    "initialE": "SCH"
                },
                {
                    "deviceType": "OZ",
                    "deviceName": "device_OZ_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhongyangkongtiaofanyiqi",
                    "initialPY": "zyktfyq",
                    "english": "A/C Translator",
                    "initialE": "AT"
                },
                {
                    "deviceType": "28",
                    "deviceName": "device_28_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "zhinengshuifa",
                    "initialPY": "znsf",
                    "english": "Smart Water Valve",
                    "initialE": "SWV"
                },
                {
                    "deviceType": "Bs",
                    "deviceIcon": "Bs",
                    "deviceName": "Device_name_Bs",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "danfosidinuanfanyiqi",
                    "initialPY": "dfsdnfyq",
                    "english": "Danfoss floor warming translator",
                    "initialE": "FCFHT"
                },
                {
                    "deviceType": "A1",
                    "deviceIcon": "a1_01",
                    "deviceName": "device_A1_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "silushuruxingfanyiqi",
                    "initialPY": "slsrxfyq",
                    "english": "4-waytranslator(input)",
                    "initialE": "4wTI"
                },
                {
                    "deviceType": "A2",
                    "deviceName": "device_A2_name",
                    "deviceHref": "addDevice_A2_01.html",
                    "pinyin": "erlushuchuxingfanyiqi",
                    "initialPY": "elscxfyq",
                    "english": "2-waytranslator(output)",
                    "initialE": "TWTO"
                },
                //          		{
                //              		"deviceType": "Bo",
                //              		"deviceName": "device_Bo_name",
                //              		"deviceHref": "addDevice_Bo_01.html",
                //              		"pinyin": "erlushuchuxingfanyiqi02",
                //              		"initialPY": "elscxfyq",
                //              		"english": "Two-way output translator 02",
                //              		"initialE": "TWOT"
                //          		},
                {
                    "deviceType": "B9",
                    "deviceIcon": "B9",
                    "deviceName": "device_B9_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "shurushuchuxingfanyiqi",
                    "initialPY": "srscfyq",
                    "english": "2-waytranslator(input&output)",
                    "initialE": "TWIO"
                },
                {
                    "deviceType": "82",
                    "deviceName": "device_82_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "wenkongqi",
                    "initialPY": "wkq",
                    "english": "Controller",
                    "initialE": "C"
                },
                {
                    "deviceType": "Oi",
                    "deviceName": "device_Oi_name",
                    "deviceHref": "addDevice_Common_01.html",
                    "pinyin": "kongtiaokongzhiqi",
                    "initialPY": "ktkzq",
                    "english": "Controller",
                    "initialE": "C"
                }

            ]
        },
        {
            //背景音乐
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
            }, {
                "deviceType": "XW01",
                "deviceName": "device_XW01_name",
                "pinyin": "zhinengbeijingyinyue",
                "initialPY": "znbjyy",
                "english": "todo",
                "initialE": "todo"
            }]
        },
        {
            //健康管理
            "categoryType": "health",
            "name": "addDevice_Healthy",
            "href": "addDevice_classify.html?type=health&project=wulian",
            "deviceList": [{
                "deviceType": "48",
                "deviceName": "Device_48_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "zhinengxueyaji",
                "initialPY": "znxyj",
                "english": "todo",
                "initialE": "todo"
            }, {
                "deviceType": "45",
                "deviceName": "Device_45_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "zhinengtizhongcheng",
                "initialPY": "zntzc",
                "english": "todo",
                "initialE": "todo"
            }]
        },
        {
            //家用电器
            "categoryType": "domestic_appliance",
            "name": "Adddevice_HA",
            "href": "addDevice_classify.html?type=domestic_appliance&project=wulian",
            "deviceList": [{
                "deviceType": "Ok",
                "deviceName": "device_Ok_name",
                "deviceHref": "addDevice_Common_01.html",
                "pinyin": "haixinfentikongtiao",
                "initialPY": "hxftkt",
                "english": "Split air conditioning",
                "initialE": "SAC"
            }
                // ,
                // {
                //     "deviceType": "HS05",
                //     "deviceName": "device_HS05_name",
                //     "pinyin": "bingxiang",
                //     "initialPY": "bx",
                //     "english": "Refrigerator",
                //     "initialE": "R"
                // },
                // {
                //     "deviceType": "HS01",
                //     "deviceName": "Lookever",
                //     "pinyin": "suibiankan",
                //     "initialPY": "sbk",
                //     "english": "Lookever Camera",
                //     "initialE": "LC"
                // },
                // {
                //     "deviceType": "HS02",
                //     "deviceName": "addScene_penguin",
                //     "pinyin": "qieshexiangji",
                //     "initialPY": "qesxj",
                //     "english": "Smart Cloud Camera",
                //     "initialE": "SCC"
                // },
                // {
                //     "deviceType": "HS03",
                //     "deviceName": "addDevice_maoy",
                //     "pinyin": "maoyan",
                //     "initialPY": "my",
                //     "english": "Door Guardian",
                //     "initialE": "DG"
                // },
                // {
                //     "deviceType": "HS04",
                //     "deviceName": "Device_Default_Name_CMICY1",
                //     "pinyin": "zhinengmaoyanmini",
                //     "initialPY": "znmymn",
                //     "english": "Ding-dong Mini",
                //     "initialE": "DM"
                // },
                // {
                //     "deviceType": "HS06",
                //     "deviceName": "OutdoorCameraName",
                //     "pinyin": "huwaigaoqingshexiangji",
                //     "initialPY": "hwgqsxj",
                //     "english": "Weather-proof HD Camera",
                //     "initialE": "WHDC"
                // }
            ]
        },
        {
            //智能控制
            "categoryType": "smart_control",
            "name": "addDevice_Smartspeaker",
            "href": "addDevice_classify.html?type=health&project=wulian",
            "deviceList": [{
                "deviceType": "smart01",
                "deviceName": "device_smart01_name", //公子小白机器人
                "deviceHref": "javascript:;",
                "pinyin": "gongzixiaobaijiqiren",
                "initialPY": "gzxbjqr",
                "english": "",
                "initialE": ""
            }, {
                "deviceType": "smart02",
                "deviceName": "device_smart02_name", //若琪
                "deviceHref": "javascript:;",
                "pinyin": "ruoqi",
                "initialPY": "rq",
                "english": "",
                "initialE": ""
            }, {
                "deviceType": "smart03",
                "deviceName": "device_smart03_name", //叮咚
                "deviceHref": "javascript:;",
                "pinyin": "dingdong",
                "initialPY": "dd",
                "english": "",
                "initialE": ""
            }, {
                "deviceType": "smart04",
                "deviceName": "device_smart04_name", //天猫精灵
                "deviceHref": "javascript:;",
                "pinyin": "tianmaojingling",
                "initialPY": "tmjl",
                "english": "",
                "initialE": ""
            }, {
                "deviceType": "smart05",
                "deviceName": "device_smart05_name", //小薇
                "deviceHref": "javascript:;",
                "pinyin": "xiaowei",
                "initialPY": "xw",
                "english": "",
                "initialE": ""
            }, {
                "deviceType": "smart06",
                "deviceName": "device_smart06_name", //Echo
                "deviceHref": "javascript:;",
                "pinyin": "Echo",
                "initialPY": "Echo",
                "english": "",
                "initialE": ""
            }]
        }
        // ,
        // {
        //     //安全狗
        //     "categoryType": "safeDog",
        //     "name": "Adddevice_safeDog",
        //     "href": "addDevice_classify.html?type=safeDog&project=wulian",
        //     "deviceList": [{
        //         "deviceType": "sd01",
        //         "deviceName": "device_DD_name",
        //         "deviceHref": "addDevice_DD_01.html",
        //         "pinyin": "quanzhaibeijingyinyue",
        //         "initialPY": "qzbjyy",
        //         "english": "todo",
        //         "initialE": "todo"
        //     }
        //     ]
        // }
    ]
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
                var a = "";
                var lang = window.sysfun.getLang();
                if (deviceList[j].deviceType != "HS05") {
                    a = '<a onclick="jump(\'' + deviceList[j].deviceHref + '\',\'' + deviceList[j].deviceType + '\',\'' + deviceList[j].deviceHandle + '\')"><i style="background:url(../../source/deviceIcon/device_icon_' + icon + '.png) no-repeat left center;background-size:3.4rem auto;"></i><span class="autoSwitchLanguager" id="' + deviceList[j].deviceName + '">' + languageUtil.getResource(deviceList[j].deviceName) + '</span></a>'
                } else {
                    if (browser.versions.android == true) {
                        a = '<a onclick="jump(\'' + deviceList[j].deviceHref + '\',\'' + deviceList[j].deviceType + '\',\'' + deviceList[j].deviceHandle + '\')"><i style="background:url(../../source/deviceIcon/device_icon_' + icon + '.png) no-repeat left center;background-size:3.4rem auto;"></i><span class="autoSwitchLanguager" id="' + deviceList[j].deviceName + '">' + languageUtil.getResource(deviceList[j].deviceName) + '</span></a>'
                    }
                }
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
