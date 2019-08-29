window.recommedDeviceList = {
    "02":{
        //开关类设备
        "Aj":[
            {
                "name":languageUtil.getResource("device_Aj_name"), //内嵌一路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Aj.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"11",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "At":[
            {
                "name":languageUtil.getResource("device_At_name"), //内嵌二路
                "deviceState":"1:"+languageUtil.getResource('switch_AoEs_01') +" 2:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_At.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Am":[
            {
                "name":languageUtil.getResource("lang_devicename_Am"), //金属一
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Am.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "An":[
            {
                "name":languageUtil.getResource("lang_devicename_An"), //金属二
                "deviceState":"1:"+languageUtil.getResource('switch_AoEs_01') +" 2:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_An.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"411",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Ao":[
            {
                "name":languageUtil.getResource("lang_devicename_Ao"), //金属三
                "deviceState":"1:"+languageUtil.getResource('switch_AoEs_01') +" 2:"+languageUtil.getResource('switch_AoEs_01') +" 3:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_Ao.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "61":[
            {
                "name":languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_61.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "62":[
            {
                "name":languageUtil.getResource("Onewayswitch_62_adddevice"), //Wulian二路开关
                "deviceState":"1:"+ languageUtil.getResource("switch_AoEs_01") +" 2:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_62.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"11",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "63":[
            {
                "name":languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
                "deviceState":"1:"+languageUtil.getResource('switch_AoEs_01') +" 2:"+languageUtil.getResource('switch_AoEs_01') +" 3:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_63.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource('switch_AoEs_01') +" 100%",
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"100",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ai":[
            {
                "name":languageUtil.getResource("device_name_Ai"), //DIN
                "deviceState":languageUtil.getResource('openSingle'),
                "icon":"../../source/deviceIcon/device_icon_Ai.png",
                "stateUrl": "setDoorState.html",
                "epData":"11",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "90":[
            {
                "name":languageUtil.getResource("device_name_90"), //led炫彩灯
                "deviceState":languageUtil.getResource('LEDlight_90_sunlight') +" 100%",
                "icon":"../../source/deviceIcon/device_icon_90.png",
                "stateUrl": "setColorfulLight_90.html",
                "epData":"D255",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bu":[
            {
                "name":languageUtil.getResource("device_name_Bu"), //联排一路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Bu.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bv":[
            {
                "name":languageUtil.getResource("device_name_Bv"), //联排二路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01") +" 2:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_Bv.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"411",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bw":[
            {
                "name":languageUtil.getResource("device_name_Bw"), //联排三路
                "deviceState":"1:"+languageUtil.getResource('switch_AoEs_01') +" 2:"+languageUtil.getResource('switch_AoEs_01') +" 3:"+languageUtil.getResource('switch_AoEs_01'),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"4111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //插座类设备
        "50":[
            {
                "name":languageUtil.getResource("addScene_socket"), //led炫彩灯
                "deviceState":languageUtil.getResource('openSingle'),
                "icon":"../../source/deviceIcon/device_icon_50.png",
                "stateUrl": "setDoorState.html",
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "77":[
            {
                "name":languageUtil.getResource("device_name_77"), //联排一路
                "deviceState":languageUtil.getResource('openSingle'),
                "icon":"../../source/deviceIcon/device_icon_77.png",
                "stateUrl": "setDoorState.html",
                "epData":"11",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "16":[
            {
                "name":languageUtil.getResource("device_name_16"), //联排二路
                "deviceState":languageUtil.getResource('openSingle'),
                "icon":"../../source/deviceIcon/device_icon_16.png",
                "stateUrl": "setDoorState.html",
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bt":[
            {
                "name":languageUtil.getResource("device_name_Bt"), //联排三路
                "deviceState":languageUtil.getResource('openSingle'),
                "icon":"../../source/deviceIcon/device_icon_Bt.png",
                "stateUrl": "setDoorState.html",
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //摄像机安全防护类设备
        "CMICA2": [
            {
                "name": languageUtil.getResource("device_name_CMICA2"), //随便看
                "icon": "../../source/deviceIcon/device_icon_CMICA2.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA3": [
            {
                "name": languageUtil.getResource("device_name_CMICA3"), //企鹅
                "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA4": [
            {
                "name": languageUtil.getResource("device_name_CMICA4"), //小物摄像机
                "icon": "../../source/deviceIcon/device_icon_CMICA4.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA5": [
            {
                "name": languageUtil.getResource("device_name_CMICA5"), //户外
                "icon": "../../source/deviceIcon/device_icon_CMICA5.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        //安防类设备
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
    },
    "01":{
        //开关类设备
        "Aj":[
            {
                "name":languageUtil.getResource("device_Aj_name"), //内嵌一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Aj.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "At":[
            {
                "name":languageUtil.getResource("device_At_name"), //内嵌二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_At.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"100",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Am":[
            {
                "name":languageUtil.getResource("lang_devicename_Am"), //金属一
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Am.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "An":[
            {
                "name":languageUtil.getResource("lang_devicename_An"), //金属二
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_An.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"400",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Ao":[
            {
                "name":languageUtil.getResource("lang_devicename_Ao"), //金属三
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Ao.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "61":[
            {
                "name":languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_61.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "62":[
            {
                "name":languageUtil.getResource("Onewayswitch_62_adddevice"), //Wulian二路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_62.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"00",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "63":[
            {
                "name":languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_63.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ai":[
            {
                "name":languageUtil.getResource("device_name_Ai"), //DIN
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_Ai.png",
                "stateUrl": "setDoorState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "90":[
            {
                "name":languageUtil.getResource("device_name_90"), //led炫彩灯
                "deviceState":"OFF",
                "icon":"../../source/deviceIcon/device_icon_90.png",
                "stateUrl": "setColorfulLight_90.html",
                "epData":"D000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bu":[
            {
                "name":languageUtil.getResource("device_name_Bu"), //联排一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bu.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bv":[
            {
                "name":languageUtil.getResource("device_name_Bv"), //联排二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bv.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"400",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bw":[
            {
                "name":languageUtil.getResource("device_name_Bw"), //联排三路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"4000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //插座类设备
        "50":[
            {
                "name":languageUtil.getResource("addScene_socket"), //led炫彩灯
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_50.png",
                "stateUrl": "setDoorState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "77":[
            {
                "name":languageUtil.getResource("device_name_77"), //联排一路
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_77.png",
                "stateUrl": "setDoorState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "16":[
            {
                "name":languageUtil.getResource("device_name_16"), //联排二路
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_16.png",
                "stateUrl": "setDoorState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bt":[
            {
                "name":languageUtil.getResource("device_name_Bt"), //联排三路
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_Bt.png",
                "stateUrl": "setDoorState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //摄像机安全防护类设备
        "CMICA2": [
            {
                "name": languageUtil.getResource("device_name_CMICA2"), //随便看
                "icon": "../../source/deviceIcon/device_icon_CMICA2.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA3": [
            {
                "name": languageUtil.getResource("device_name_CMICA3"), //企鹅
                "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA4": [
            {
                "name": languageUtil.getResource("device_name_CMICA4"), //小物摄像机
                "icon": "../../source/deviceIcon/device_icon_CMICA4.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA5": [
            {
                "name": languageUtil.getResource("device_name_CMICA5"), //户外
                "icon": "../../source/deviceIcon/device_icon_CMICA5.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        //安防类设备
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
    },
    "10":{
        //开关类设备
        "Aj":[
            {
                "name":languageUtil.getResource("device_Aj_name"), //内嵌一路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Aj.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"11",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "At":[
            {
                "name":languageUtil.getResource("device_At_name"), //内嵌二路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_At.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Am":[
            {
                "name":languageUtil.getResource("lang_devicename_Am"), //金属一
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Am.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "An":[
            {
                "name":languageUtil.getResource("lang_devicename_An"), //金属二
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_An.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"411",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Ao":[
            {
                "name":languageUtil.getResource("lang_devicename_Ao"), //金属三
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01")+" 3:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Ao.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "61":[
            {
                "name":languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_61.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "62":[
            {
                "name":languageUtil.getResource("Onewayswitch_62_adddevice"), //Wulian二路开关
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_62.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"11",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "63":[
            {
                "name":languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01")+" 3:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_63.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"111",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource("switch_AoEs_01")+" 100%",
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"100",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ai":[
            {
                "name":languageUtil.getResource("device_name_Ai"), //DIN
                "deviceState":languageUtil.getResource("openSingle"),
                "icon":"../../source/deviceIcon/device_icon_Ai.png",
                "stateUrl": "setDoorState.html",
                "epData":"11",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "90":[
            {
                "name":languageUtil.getResource("device_name_90"), //led炫彩灯
                "deviceState":languageUtil.getResource("LEDlight_90_sunlight")+" 100%",
                "icon":"../../source/deviceIcon/device_icon_90.png",
                "stateUrl": "setColorfulLight_90.html",
                "epData":"D255",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bu":[
            {
                "name":languageUtil.getResource("device_name_Bu"), //联排一路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Bu.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bv":[
            {
                "name":languageUtil.getResource("device_name_Bv"), //联排二路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Bv.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"411",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bw":[
            {
                "name":languageUtil.getResource("device_name_Bw"), //联排三路
                "deviceState":"1:"+languageUtil.getResource("switch_AoEs_01")+" 2:"+languageUtil.getResource("switch_AoEs_01")+" 3:"+languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4111",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "91":[
            {
                "name":languageUtil.getResource("device_91_name"), //联排三路
                "deviceState":languageUtil.getResource("switch_AoEs_01"),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "device_91_1.html",
                "epData":"1255,2255",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ]
    },
    "11":{
        //开关类设备
        "Aj":[
            {
                "name":languageUtil.getResource("device_Aj_name"), //内嵌一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Aj.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "At":[
            {
                "name":languageUtil.getResource("device_At_name"), //内嵌二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_At.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"100",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Am":[
            {
                "name":languageUtil.getResource("lang_devicename_Am"), //金属一
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Am.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "An":[
            {
                "name":languageUtil.getResource("lang_devicename_An"), //金属二
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_An.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"400",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Ao":[
            {
                "name":languageUtil.getResource("lang_devicename_Ao"), //金属三
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Ao.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "61":[
            {
                "name":languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_61.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "62":[
            {
                "name":languageUtil.getResource("Onewayswitch_62_adddevice"), //Wulian二路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_62.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"00",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "63":[
            {
                "name":languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_63.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ai":[
            {
                "name":languageUtil.getResource("device_name_Ai"), //DIN
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_Ai.png",
                "stateUrl": "setDoorState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "90":[
            {
                "name":languageUtil.getResource("device_name_90"), //led炫彩灯
                "deviceState":"OFF",
                "icon":"../../source/deviceIcon/device_icon_90.png",
                "stateUrl": "setColorfulLight_90.html",
                "epData":"D000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bu":[
            {
                "name":languageUtil.getResource("device_name_Bu"), //联排一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bu.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bv":[
            {
                "name":languageUtil.getResource("device_name_Bv"), //联排二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bv.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"400",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bw":[
            {
                "name":languageUtil.getResource("device_name_Bw"), //联排三路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"4000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "91":[
            {
                "name":languageUtil.getResource("device_91_name"), //联排三路
                "deviceState":languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_91.png",
                "stateUrl": "device_91_1.html",
                "epData":"2000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ]
    },
    "14":{
        //安防类设备
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ]
    },
    "15":{
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ]
    },
    "05":{
        //窗帘类设备
        "65":[
            {
                "name": languageUtil.getResource("device_name_65"), //"窗帘控制器02型"
                "icon": "../../source/deviceIcon/device_icon_65.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("openSingle"),
                "epData":"2",
                "endpointNumber":"1",
                "isRoomName":"true",
                "delay":"4"
            }
        ],
        "Ar":[
            {
                "name": languageUtil.getResource("addDevice_Ar_name"), //"金属窗帘"
                "icon": "../../source/deviceIcon/device_icon_Ar.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("openSingle"),
                "epData":"2",
                "endpointNumber":"1",
                "isRoomName":"true",
                "delay":"4"
            }
        ],
        "80":[
            {
                "name": languageUtil.getResource("device_80_name"), //"窗帘电机"
                "icon": "../../source/deviceIcon/device_icon_80.png",
                "stateUrl": "curtain_80.html",
                "deviceState":languageUtil.getResource("openSingle"),
                "epData":"0100",
                "endpointNumber":"1",
                "isRoomName":"true",
                "delay":"4"
            }
        ],
        "81":[
            {
                "name": languageUtil.getResource("device_name_81"), //"调光开关"
                "icon": "../../source/deviceIcon/device_icon_81.png",
                "stateUrl": "setSwitch.html",
                "deviceState":languageUtil.getResource("device_list_Curtain") + languageUtil.getResource("Music_volume_1")+languageUtil.getResource("openSingle"),
                "epData":"01009000",
                "endpointNumber":"1",
                "isRoomName":"true",
                "delay":"4"
            },
            {
                "name": languageUtil.getResource("device_name_81"), //"调光开关"
                "icon": "../../source/deviceIcon/device_icon_81.png",
                "stateUrl": "setSwitch.html",
                "deviceState":languageUtil.getResource("device_list_Curtain") + languageUtil.getResource("Music_volume_2")+languageUtil.getResource("openSingle"),
                "epData":"90000100",
                "endpointNumber":"2",
                "isRoomName":"true",
                "delay":"4"
            }
        ],
        "Au":[
            {
                "name": languageUtil.getResource("device_name_Au"), //"内嵌式窗帘控制器"
                "icon": "../../source/deviceIcon/device_icon_Au.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("openSingle"),
                "epData":"2",
                "endpointNumber":"1",
                "isRoomName":"true",
                "delay":"4"
            }
        ],
        //打开背景音乐
        "XW01": [
            {
                "name": languageUtil.getResource("device_name_XW01"), //向往背景音乐
                "icon": "../../source/deviceIcon/device_icon_XW01.png",
                "stateUrl": "music_XW01.html",
                "deviceState":languageUtil.getResource("Music_houseKeeper_play")+" "+languageUtil.getResource("minigateway_Sound_volume")+":7",
                "epData":{"action": "controlDevice", "endpoint": [{"endpointNum": 1, "action": {"Play": {"value": "play"}}},{"endpointNum": 1, "action": {"Volume": {"value": "7"}}}]},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "DD": [
            {
                "name": languageUtil.getResource("device_name_DD"), //"背景音乐"
                "icon": "../../source/deviceIcon/device_icon_DD.png",
                "stateUrl": "device_DD.html",
                "deviceState":languageUtil.getResource("Music_houseKeeper_play")+languageUtil.getResource("Music_volume_1")+"7",
                "epData":"0,17",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //关闭摄像机安全防护
        "CMICA2": [
            {
                "name": languageUtil.getResource("device_name_CMICA2"), //随便看
                "icon": "../../source/deviceIcon/device_icon_CMICA2.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA3": [
            {
                "name": languageUtil.getResource("device_name_CMICA3"), //企鹅
                "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA4": [
            {
                "name": languageUtil.getResource("device_name_CMICA4"), //小物摄像机
                "icon": "../../source/deviceIcon/device_icon_CMICA4.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        "CMICA5": [
            {
                "name": languageUtil.getResource("device_name_CMICA5"), //户外
                "icon": "../../source/deviceIcon/device_icon_CMICA5.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_close"),
                "epData":{"action": "stopSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "false","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "false","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "endpointNumber":"1",
                "isRoomName":"false"
            }
        ],
        //安防类设备
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("disarming"),
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
    },
    "03":{
        //开关类设备
        "Aj":[
            {
                "name":languageUtil.getResource("device_Aj_name"), //内嵌一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Aj.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "At":[
            {
                "name":languageUtil.getResource("device_At_name"), //内嵌二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_At.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"100",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Am":[
            {
                "name":languageUtil.getResource("lang_devicename_Am"), //金属一
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Am.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "An":[
            {
                "name":languageUtil.getResource("lang_devicename_An"), //金属二
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_An.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"400",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "Ao":[
            {
                "name":languageUtil.getResource("lang_devicename_Ao"), //金属三
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Ao.png",
                "stateUrl": "lock_chooseSwichState.html",
                "epData":"4000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "61":[
            {
                "name":languageUtil.getResource("Onewayswitch_61_adddevice"), //Wulian单路开关
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_61.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"0",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "62":[
            {
                "name":languageUtil.getResource("Onewayswitch_62_adddevice"), //Wulian二路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_62.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"00",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "63":[
            {
                "name":languageUtil.getResource("Threewayswitch_63_adddevice"), //Wulian三路开关
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_63.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"000",
                "endpointNumber":"0",
                "isRoomName":"true"
            }
        ],
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ai":[
            {
                "name":languageUtil.getResource("device_name_Ai"), //DIN
                "deviceState":languageUtil.getResource("closeSingle"),
                "icon":"../../source/deviceIcon/device_icon_Ai.png",
                "stateUrl": "setDoorState.html",
                "epData":"10",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "90":[
            {
                "name":languageUtil.getResource("device_name_90"), //led炫彩灯
                "deviceState":"OFF",
                "icon":"../../source/deviceIcon/device_icon_90.png",
                "stateUrl": "setColorfulLight_90.html",
                "epData":"D000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bu":[
            {
                "name":languageUtil.getResource("device_name_Bu"), //联排一路
                "deviceState":"1:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bu.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"110",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bv":[
            {
                "name":languageUtil.getResource("device_name_Bv"), //联排二路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bv.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"400",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Bw":[
            {
                "name":languageUtil.getResource("device_name_Bw"), //联排三路
                "deviceState":"1:"+languageUtil.getResource("close")+" 2:"+languageUtil.getResource("close")+" 3:"+languageUtil.getResource("close"),
                "icon":"../../source/deviceIcon/device_icon_Bw.png",
                "stateUrl": "chooseSwitchState.html",
                "epData":"4000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //窗帘类设备
        "65":[
            {
                "name": languageUtil.getResource("device_name_65"), //"窗帘控制器02型"
                "icon": "../../source/deviceIcon/device_icon_65.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("closeSingle"),
                "epData":"3",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ar":[
            {
                "name": languageUtil.getResource("addDevice_Ar_name"), //"金属窗帘"
                "icon": "../../source/deviceIcon/device_icon_Ar.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("closeSingle"),
                "epData":"3",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "80":[
            {
                "name": languageUtil.getResource("device_80_name"), //"窗帘电机"
                "icon": "../../source/deviceIcon/device_icon_80.png",
                "stateUrl": "curtain_80.html",
                "deviceState":languageUtil.getResource("closeSingle"),
                "epData":"0000",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "81":[
            {
                "name": languageUtil.getResource("device_name_81"), //"调光开关"
                "icon": "../../source/deviceIcon/device_icon_81.png",
                "stateUrl": "setSwitch.html",
                "deviceState":languageUtil.getResource("device_curtain")+languageUtil.getResource("Music_volume_1")+languageUtil.getResource("closeSingle"),
                "epData":"00009000",
                "endpointNumber":"1",
                "isRoomName":"true"
            },
            {
                "name": languageUtil.getResource("device_name_81"), //"调光开关"
                "icon": "../../source/deviceIcon/device_icon_81.png",
                "stateUrl": "setSwitch.html",
                "deviceState":languageUtil.getResource("device_curtain")+languageUtil.getResource("Music_volume_2")+languageUtil.getResource("closeSingle"),
                "epData":"90000000",
                "endpointNumber":"2",
                "isRoomName":"true"
            }
        ],
        "Au":[
            {
                "name": languageUtil.getResource("device_name_Au"), //"内嵌式窗帘控制器"
                "icon": "../../source/deviceIcon/device_icon_Au.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("closeSingle"),
                "epData":"3",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        //摄像机安全防护类设备
        "CMICA2": [
            {
                "name": languageUtil.getResource("device_name_CMICA2"), //随便看
                "icon": "../../source/deviceIcon/device_icon_CMICA2.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA3": [
            {
                "name": languageUtil.getResource("device_name_CMICA3"), //企鹅
                "icon": "../../source/deviceIcon/device_icon_CMICA3.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA4": [
            {
                "name": languageUtil.getResource("device_name_CMICA4"), //小物摄像机
                "icon": "../../source/deviceIcon/device_icon_CMICA4.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        "CMICA5": [
            {
                "name": languageUtil.getResource("device_name_CMICA5"), //户外
                "icon": "../../source/deviceIcon/device_icon_CMICA5.png",
                "stateUrl": "setCameraAction.html",
                "deviceState":languageUtil.getResource("lang_camera_safety_open"),
                "epData":{"action": "startSafety","data": {"linkageGuardDic":{"day":["Sun","Mon","Tues","Wed","Thurs","Fri","Sat"],"enable": "true","time":[{"end": "23:59","start": "00:00"}]},"motionDetectionDic": {"enable": "true","sensitivity": 80,"areaArray": [0,0,320,240]}}},
                "isRoomName":"false"
            }
        ],
        //安防类设备
        "03": [
            {
                "name": languageUtil.getResource("addScene_doorContack"), //"门磁感应器"
                "icon": "../../source/deviceIcon/device_icon_03.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "02": [
            {
                "name": languageUtil.getResource("addDevice_02_name"), //"红外入侵监测器"
                "icon": "../../source/deviceIcon/device_icon_02.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "06": [
            {
                "name": languageUtil.getResource("addDevice_06_name"), //"水浸监测器"
                "icon": "../../source/deviceIcon/device_icon_06.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "43": [
            {
                "name": languageUtil.getResource("addDevice_43_name"), //"烟雾监测器"
                "icon": "../../source/deviceIcon/device_icon_43.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "09": [
            {
                "name": languageUtil.getResource("addDevice_09_name"), //"可燃气体泄漏探测器"
                "icon": "../../source/deviceIcon/device_icon_09.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "A5": [
            {
                "name": languageUtil.getResource("device_name_A5"), //"门铃按钮02型"
                "icon": "../../source/deviceIcon/device_icon_A5.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "C0": [
            {
                "name": languageUtil.getResource("device_name_C0"), //多功能红外人体探测器（强电版）
                "icon": "../../source/deviceIcon/device_icon_C0.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
        "Ad": [
            {
                "name": languageUtil.getResource("device_name_Ad"), //多功能红外人体探测器（电池版）
                "icon": "../../source/deviceIcon/device_icon_Ad.png",
                "stateUrl": "setDoorState.html",
                "deviceState":languageUtil.getResource("fortify"),
                "epData":"1",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
    },
    "04":{
        "12":[
            {
                "name":languageUtil.getResource("device_name_12"), //调光开关
                "deviceState":languageUtil.getResource("switch_AoEs_01")+" 30%",
                "icon":"../../source/deviceIcon/device_icon_12.png",
                "stateUrl": "device_12.html",
                "epData":"030",
                "endpointNumber":"1",
                "isRoomName":"true"
            }
        ],
    },
};