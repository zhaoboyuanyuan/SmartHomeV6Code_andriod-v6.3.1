function initlan() {
    try {
        var iphoneX = {
            sysFunc: "getItem:",
            room: "iphoneX",
            id: "iphoneX",
            data: ""
        };
        var iStr = '';
        if (!window.v6sysfunc) {
            iStr = prompt(JSON.stringify(iphoneX))
        }
        if (iStr == "iphoneX") {
            window.iphoneXTimeTop = "8.8rem";
            window.iphoneXTimeTop10_8 = "13.2rem";
            var arr88 = document.getElementsByClassName("iphoneX");
            for (var i = 0; i < arr88.length; i++) {
                arr88[i].style.marginTop = "8.8rem";
            }
            var arr44 = document.getElementsByClassName("iphoneX44");
            for (var i = 0; i < arr44.length; i++) {
                arr44[i].style.marginTop = "4.4rem";
            }
            var body = document.getElementsByClassName("header")[0];
            body.style.paddingTop = "4.4rem"

            // 设备筛选等，批量修改等样式
            var list = document.getElementsByClassName("get_list")
            if (list.length != 0) {
                var ulList = document.getElementsByClassName("get_list")[0].style.paddingTop;
                if (ulList == "6.4rem") {
                    document.getElementsByClassName("get_list")[0].style.paddingTop = "8.8rem";
                }
            }
            var iphoneXList = document.getElementsByClassName("iphoneXList");
            if (iphoneXList.length != 0) {
                for (var i = 0; i < iphoneXList.length; i++) {
                    iphoneXList[i].style.paddingTop = "8.8rem";
                    iphoneXList[i].style.minHeight = "calc(100% - 8.8rem)";
                }
            }
            var zone = document.getElementsByClassName("zoneList")
            if (zone.length != 0) {
                zone[0].style.top = "12rem";
            }
            var mainview = document.getElementsByClassName("mainview")
            if (mainview.length != 0) {
                mainview[0].style.marginTop = "8.8rem";
                mainview[0].style.minHeight = "calc(100% - 8.8rem)";
            }


            var top3 = document.getElementsByTagName("body")[0].children[1].style.paddingTop;
            if (top3 == "6.4rem") {
                document.getElementsByTagName("body")[0].children[1].style.paddingTop = "8.8rem";
            } else if (top3 == "9.8rem") {
                document.getElementsByTagName("body")[0].children[1].style.paddingTop = "12.2rem";
            }
            var marginTop = document.getElementsByTagName("body")[0].children[1].style.marginTop;
            if (marginTop == "6.4rem") {
                document.getElementsByTagName("body")[0].children[1].style.marginTop = "8.8rem";
            }
            var demo = document.getElementById("demo");
            if (demo.length != 0) {
                var demoTop = demo.style.top;
                if (demoTop == "7rem") {
                    demo.style.top = "9.4rem";
                }
            }
        }
    } catch (e) {
        //TODO handle the exception
    }
    var languageCode = window.sysfun.getLang();
    languageUtil.onJsLoaded = function () {
        initLanguage();
    }
    languageUtil.init(languageCode, "../lang/");
}

function globalLang() {
    window.lang_name_03 = languageUtil.getResource("addScene_doorContack") //"门磁感应器"
    window.lang_name_06 = languageUtil.getResource("addDevice_06_name") //"水浸监测器"
    window.lang_name_43 = languageUtil.getResource("addDevice_43_name") //"烟雾监测器"
    window.lang_name_17 = languageUtil.getResource("addDevice_17_name") //"温湿度监测器"
    window.lang_name_01 = languageUtil.getResource("addDevice_01_name") //"声光报警器"
    window.lang_onLine = languageUtil.getResource("chooseSensor_onLine") //"在线"
    window.lang_group = languageUtil.getResource("addDevice_group_text") //"未分区"
    window.lang_name_80 = languageUtil.getResource("device_80_name") //"窗帘电机"
    window.lang_name_At = languageUtil.getResource("device_At_name") //"内嵌式零火二路开关"
    window.device_switch = languageUtil.getResource("device_switch") //"开关"
    window.lang_name_Am = languageUtil.getResource("device_Am_name") //"单路金属开关"
    window.lang_name_An = languageUtil.getResource("device_An_name") //"二路金属开关"
    window.lang_name_Ao = languageUtil.getResource("device_Ao_name") //"三路金属开关"
    window.lang_name_61 = languageUtil.getResource("Onewayswitch_61_adddevice") //"wulian单路开关"
    window.lang_name_62 = languageUtil.getResource("Twowayswitch_62_adddevice") //"wulian二路开关"
    window.lang_name_63 = languageUtil.getResource("Threewayswitch_63_adddevice") //"wulian三路开关"
    window.lang_name_65 = languageUtil.getResource("device_name_65") //"窗帘控制器02型"
    window.lang_name_Ai = languageUtil.getResource("device_name_Ai") //"DIN"
    window.lang_name_77 = languageUtil.getResource("device_name_77") //"移动计量"
    window.lang_name_16 = languageUtil.getResource("device_name_16") //"移动计量非计量"
    window.lang_name_25 = languageUtil.getResource("device_name_25") //"机械手"
    window.lang_name_12 = languageUtil.getResource("device_name_12") //"调光开关"
    window.lang_name_81 = languageUtil.getResource("device_name_81") //"双轨窗帘"
    window.lang_name_A5 = languageUtil.getResource("addDevice_A5_name") //"门铃按钮02型"
    window.lang_name_A6 = languageUtil.getResource("addDevice_A6_name") //"门铃声光器"
    window.lang_name_Bh = languageUtil.getResource("device_name_Bh") //"两路窗帘"
    window.lang_name_Av = languageUtil.getResource("device_name_Av"); //"调光开关"
    window.lang_name_Ap = languageUtil.getResource("device_Ap_name"); //"wulian地暖"
    window.lang_name_Bm = languageUtil.getResource("device_Bm_name"); //"ZigBee地暖"
    window.lang_name_Oi = languageUtil.getResource("device_Oi_name"); //"5038空调控制器"
    window.lang_name_82 = languageUtil.getResource("device_82_name"); //"wulian美标温控器"
    window.lang_name_D9 = languageUtil.getResource("device_name_D9"); //"电台【梦想之花】"
    window.lang_name_D7 = languageUtil.getResource("device_name_D7"); //"LED炫彩灯"
    window.lang_name_DD = languageUtil.getResource("device_name_DD"); //"背景音乐"
    window.lang_name_90 = languageUtil.getResource("device_name_90"); //"炫彩灯"
    window.lang_name_02 = languageUtil.getResource("addDevice_02_name") //"红外入侵监测器"
    window.lang_name_09 = languageUtil.getResource("addDevice_09_name") //"可燃气体泄漏探测器"
    window.lang_name_50 = languageUtil.getResource("addScene_socket") //"墙面插座"
    window.lang_name_Ar = languageUtil.getResource("addDevice_Ar_name") //"金属窗帘控制器"
    window.lang_name_Au = languageUtil.getResource("device_name_Au"); //"内嵌式窗帘控制器"
    window.lang_offLine = languageUtil.getResource("chooseSensor_offLine") //"离线"
    window.lang_name_Aj = languageUtil.getResource("device_Aj_name") //"内嵌式零火一路开关"
    window.lang_name_D8 = languageUtil.getResource("addDevice_D8_minigateway"); //"mini网关声光设备"

    window.lang_deviceTxt1 = languageUtil.getResource("addHumiture1_txt1") //"温度高于"
    window.lang_deviceTxt2 = languageUtil.getResource("addHumiture1_txt2") //"温度低于"
    window.lang_deviceIf = languageUtil.getResource("addHumiture1_txt3") //"高于指定温度"

    window.lang_deviceTxt3 = languageUtil.getResource("circumstances_txt14") //"湿度高于"
    window.lang_deviceTxt4 = languageUtil.getResource("circumstances_txt15") //"湿度低于"
    window.lang_deviceIf1 = languageUtil.getResource("setHumiture_txt03") //"高于指定湿度"

    window.lang_deviceTxt5 = languageUtil.getResource("setCarbonDioxide_txt03") //"温度高于"
    window.lang_deviceTxt6 = languageUtil.getResource("setCarbonDioxide_txt04") //"温度低于"
    window.lang_deviceIf2 = languageUtil.getResource("setCarbonDioxide_txt01") //"高于指定指数"

    window.switch_Aohk_03 = languageUtil.getResource("switch_Aohk_03") //非开关模式
    window.lang_switch1 = device_switch + "1" //开关1
    window.lang_switch2 = device_switch + "2" //开关2
    window.lang_switch3 = device_switch + "3" //开关3

    window.lang_name_04 = languageUtil.getResource("houseKeeper1_04_name") //"紧急按钮"
    window.lang_txt01 = languageUtil.getResource("setSceneState_txt01") //"被打开"
    window.lang_txt02 = languageUtil.getResource("setSceneState_txt02") //"被关闭"
    window.lang_txt03 = languageUtil.getResource("setSceneState_txt01") //"被打开"
    window.lang_txt_down = languageUtil.getResource("houseKeeper1_04_pressed") //"被按下"
    window.lang_txt04 = languageUtil.getResource("circumstances_txt04") //"消警"
    window.lang_txt05 = languageUtil.getResource("warn_01") //"有漏水"
    window.lang_txt06 = languageUtil.getResource("circumstances_txt06") //"设防下恢复正常"
    window.lang_txt07 = languageUtil.getResource("circumstances_txt07") //"设防下有人经过"
    window.lang_txt08 = languageUtil.getResource("circumstances_txt08") //"撤防下恢复正常"
    window.lang_txt09 = languageUtil.getResource("circumstances_txt09") //"撤防下有人经过"
    window.lang_txt10 = languageUtil.getResource("warn") //"有烟雾"
    window.lang_txt11 = languageUtil.getResource("circumstances_txt11") //"有可燃气体泄漏"
    window.lang_txt12 = languageUtil.getResource("circumstances_txt12") //"温度高于"
    window.lang_txt13 = languageUtil.getResource("circumstances_txt13") //"温度低于"
    window.lang_txt14 = languageUtil.getResource("circumstances_txt14") //"湿度高于"
    window.lang_txt15 = languageUtil.getResource("circumstances_txt15") //"湿度低于"
    window.lang_txt16 = languageUtil.getResource("circumstances_txt16") //"开启"
    window.lang_txt17 = languageUtil.getResource("circumstances_txt17") //"关闭"
    window.lang_txt18 = languageUtil.getResource("disarming") //"撤防"
    window.lang_txt19 = languageUtil.getResource("fortify") //"设防"
    window.lang_play = languageUtil.getResource("device_status_play") //"播放声音"
    window.lang_txt20 = languageUtil.getResource("circumstances_txt20") //"通用报警"
    window.lang_txt21 = languageUtil.getResource("circumstances_txt21") //"火灾报警"
    window.lang_txt22 = languageUtil.getResource("circumstances_txt22") //"停止报警"
    window.lang_txt23 = languageUtil.getResource("circumstances_txt23") //"生效时段"
    window.lang_txt24 = languageUtil.getResource("circumstances_txt24") //"从"
    window.lang_txt25 = languageUtil.getResource("circumstances_txt25") //"到"
    window.lang_txt26 = languageUtil.getResource("circumstances_txt26") //"删除"
    window.lang_txt27 = languageUtil.getResource("circumstances_txt27") //"分"
    window.lang_txt28 = languageUtil.getResource("circumstances_txt28") //"秒执行"
    window.lang_txt29 = languageUtil.getResource("circumstances_toast") //"名称长度为1-20个字符"
    window.lang_txt30 = languageUtil.getResource("circumstances_txt30") //"执行条件或执行任务不能为空，请添加"
    window.lang_txt_open = languageUtil.getResource("openSingle") //"开"
    window.lang_txt_close = languageUtil.getResource("closeSingle") //"关"
    window.lang_txt_stop = languageUtil.getResource("stopSingle") //"停"
    window.lang_device_delete = languageUtil.getResource("device_delete"); //"设备已删除"
    window.lang_scene_delete = languageUtil.getResource("scene_delete"); //"场景已删除"
    window.lang_D8_voice1 = languageUtil.getResource("minigateway_Sound_DingDong") //"叮咚"
    window.lang_D8_voice2 = languageUtil.getResource("minigateway_Sound_Jingle") //"叮铃"
    window.lang_D8_voice3 = languageUtil.getResource("minigateway_Sound_Crisp") //"清脆"
    window.lang_D8_voice4 = languageUtil.getResource("minigateway_Sound_Longsound") //"长音"
    window.lang_D8_voice5 = languageUtil.getResource("minigateway_Sound_Wave") //"波动"
    window.lang_D8_voice6 = languageUtil.getResource("minigateway_Sound_Cuckoo") //"布谷"
    window.lang_D8_voice7 = languageUtil.getResource("minigateway_Sound_Didi") //"嘀嘀"
    window.lang_D8_voice8 = languageUtil.getResource("minigateway_Sound_Fierce") //"激烈"
    window.lang_D8_voice9 = languageUtil.getResource("minigateway_Sound_Quick") //"急促"
    window.lang_D8_voice10 = languageUtil.getResource("minigateway_Sound_Sharp") //"尖利"
    window.lang_D8_voice11 = languageUtil.getResource("minigateway_Sound_Policecar") //"警车"
    window.lang_D8_mode1 = "OFF"
    window.lang_D8_mode2 = languageUtil.getResource("minigateway_Lantern_Always") //"常亮"
    window.lang_D8_mode3 = languageUtil.getResource("minigateway_Lantern_Flowingwater") //"流水"
    window.lang_D8_mode4 = languageUtil.getResource("minigateway_Lantern_Flash") //"快闪"
    window.lang_D8_mode5 = languageUtil.getResource("minigateway_Lantern_Slowflash") //"慢闪"
    window.lang_D8_color1 = languageUtil.getResource("minigateway_Colour_red") //"烈焰红"
    window.lang_D8_color2 = languageUtil.getResource("minigateway_Colour_purple") //"浪漫紫"
    window.lang_D8_color3 = languageUtil.getResource("minigateway_Colour_blue") //"湖水蓝"
    window.lang_D8_color4 = languageUtil.getResource("minigateway_Colour_orange") //"活力橙"
    window.lang_D8_color5 = languageUtil.getResource("minigateway_Colour_green") //"翡翠绿"
    window.lang_D8_color6 = languageUtil.getResource("minigateway_Colour_white") //"日光白"

    window.device_curtain = languageUtil.getResource("device_curtain") //"窗帘"
    window.lang_name_42 = languageUtil.getResource("device_name_42"); //"二氧化碳监测器"
    window.lang_name_19 = languageUtil.getResource("device_19_name"); //"光强监测器"
    window.lang_txt_42_1 = languageUtil.getResource("setCarbonDioxide_txt03"); //"高于指数"
    window.lang_txt_42_2 = languageUtil.getResource("setCarbonDioxide_txt04"); //"低于指数"
    window.lang_name_A0 = languageUtil.getResource("device_name_A0"); //二氧化碳监测器
    window.lang_name_D4 = languageUtil.getResource("device_name_D4"); //噪声监测器
    window.lang_name_D5 = languageUtil.getResource("device_name_D5"); //粉尘监测器
    window.lang_name_D6 = languageUtil.getResource("device_name_D6"); //VOC监测器
    window.lang_name_Og = languageUtil.getResource("device_name_D6"); //VOC监测器
    window.houseKeeper1_19_high = languageUtil.getResource("houseKeeper1_19_high"); //当光照强
    window.device_LX_Above_02 = languageUtil.getResource("device_LX_Above_02"); //当光照强
    window.houseKeeper1_19_low = languageUtil.getResource("houseKeeper1_19_low"); //当光照弱
    window.device_LX_Below_02 = languageUtil.getResource("device_LX_Below_02"); //当光照弱
    window.houseKeeper1_19_suit = languageUtil.getResource("houseKeeper1_19_suit"); //当光照适宜

    window.lang_name_Bc = languageUtil.getResource("device_Bc_name"); //"Bc网络智能锁"
    window.lang_name_OP = languageUtil.getResource("device_OP_name"); //"OP玄武湖"
    window.lang_name_Bd = languageUtil.getResource("device_Bd_name"); //"Bd网络锁"
    window.lang_name_OW = languageUtil.getResource("device_Ow_name"); //"OW紫金山锁"
    window.lang_name_Bg = languageUtil.getResource("device_Bg_name"); //"Bg洞庭湖"
    window.lang_name_Bf = languageUtil.getResource("device_Bf_name"); //"Bf网络锁02型"
    window.lang_name_70 = languageUtil.getResource("device_70_name"); //"70"
    window.lang_name_69 = languageUtil.getResource("device_name_69"); //"69"

    // 6.1.0 DD
    window.lang_DD_play = languageUtil.getResource("Music_houseKeeper_play"); //"背景音乐 播放"
    window.lang_DD_stop = languageUtil.getResource("Music_houseKeeper_stop"); //"背景音乐 停止"
    window.lang_DD_1 = languageUtil.getResource("Music_volume_1"); //"背景音乐 1"
    window.lang_DD_2 = languageUtil.getResource("Music_volume_2"); //"背景音乐 2"
    window.lang_DD_3 = languageUtil.getResource("Music_volume_3"); //"背景音乐 3"
    window.lang_DD_4 = languageUtil.getResource("Music_volume_4"); //"背景音乐 4"
    window.lang_name_OF = languageUtil.getResource("device_name_OF"); //"智能晾衣机"
    window.LEDlight_90_Colorful = languageUtil.getResource("LEDlight_90_Colorful"); //"炫彩";
    window.LEDlight_90_sunlight = languageUtil.getResource("LEDlight_90_sunlight"); //"暖白";
    window.rgb_str = languageUtil.getResource("colour_rgb_title")
    window.light_str = languageUtil.getResource("brightness_title")
    window.LEDlight_90_Interval = languageUtil.getResource("LEDlight_90_Interval") //"间隔";
    window.lang_txt_ringDoor = languageUtil.getResource("lock_ringDoorbell") //"有人按门铃";

    window.lang_D8_light = languageUtil.getResource("minigateway_Lantern") //"彩灯效果"
    window.lang_D8_voice = languageUtil.getResource("minigateway_Soundrepertoire") //"声音曲目"

    window.lang_houseKeeper1_Oi_Temperature = languageUtil.getResource("houseKeeper1_Oi_Temperature") //"温度"
    window.lang_houseKeeper1_Oi_Speed = languageUtil.getResource("houseKeeper1_Oi_Speed") //"风速"
    window.lang_houseKeeper1_82_Temperature1 = languageUtil.getResource("houseKeeper1_82_Temperature1") //"最大"
    window.lang_houseKeeper1_82_Temperature2 = languageUtil.getResource("houseKeeper1_82_Temperature2") //"最小"
    window.lang_houseKeeper1_82_Type1 = languageUtil.getResource("houseKeeper1_82_Type1") //"制冷";
    window.lang_houseKeeper1_82_Type2 = languageUtil.getResource("houseKeeper1_82_Type2") //"制热";
    window.lang_houseKeeper1_82_Type3 = languageUtil.getResource("houseKeeper1_82_Type3") //"Auto"
    window.lang_houseKeeper1_Oi_Cancel = languageUtil.getResource("houseKeeper1_Oi_Cancel"); //
    window.lang_houseKeeper1_Oi_Confirm = languageUtil.getResource("houseKeeper1_Oi_Confirm"); //
    window.lang_houseKeeper1_82_Speed1 = languageUtil.getResource("houseKeeper1_82_Speed1") //开启";
    window.lang_houseKeeper1_82_Speed5 = languageUtil.getResource("houseKeeper1_82_Speed5") //"关闭";

    window.lang_D7_rgb = languageUtil.getResource("colour_rgb_title"); //RGB:
    window.lang_D7_light = languageUtil.getResource("brightness_title"); //亮度
    window.lang_D7_setColor_tips = languageUtil.getResource("set_rgb_ok_error"); //请设置六位有效颜色RGB值

    window.lang_houseKeeper1_Oi_Type1 = languageUtil.getResource("houseKeeper1_Oi_Type1") //"制冷";
    window.lang_houseKeeper1_Oi_Type2 = languageUtil.getResource("houseKeeper1_Oi_Type2") //"制热";
    window.lang_houseKeeper1_Oi_Type3 = languageUtil.getResource("houseKeeper1_Oi_Type3") //"送风";
    window.lang_houseKeeper1_Oi_Type4 = languageUtil.getResource("houseKeeper1_Oi_Type4") //"除湿";
    window.lang_houseKeeper1_Oi_Speed4 = languageUtil.getResource("houseKeeper1_Oi_Speed4") //"自动";

    window.setColor_tips = languageUtil.getResource("set_rgb_ok_error")

    window.LEDlight_90_ok = languageUtil.getResource("LEDlight_90_ok") //"确定";
    window.LEDlight_90_Cancel = languageUtil.getResource("LEDlight_90_Cancel") //"取消";

    window.LEDlight_90_brightness = languageUtil.getResource("LEDlight_90_brightness");

    window.delay_sec = languageUtil.getResource("delay_sec") //"秒"

    window.lang_open = languageUtil.getResource("openSingle") //"开"
    window.lang_close = languageUtil.getResource("closeSingle") //"关"
    window.lang_play = languageUtil.getResource("device_status_play") //"播放"

    window.lang_txt31 = languageUtil.getResource("circumstances_txt16") //"开启"
    window.lang_txt32 = languageUtil.getResource("circumstances_txt17") //"关闭"

    window.lang_scene01 = languageUtil.getResource("setSceneState_txt01") //"被打开"

    window.lang_txt_beclosed = languageUtil.getResource("setSceneState_txt02") //"被关闭"
    window.lang_txt_beopened = languageUtil.getResource("setSceneState_txt01") //"被打开"

    window.circumstances_txt26 = languageUtil.getResource("circumstances_txt26") //"删除";
    window.circumstances_edit = languageUtil.getResource("circumstances_edit") //"编辑";

    window.lang_title = languageUtil.getResource("stewardIndex_time") //定时任务
    window.lang_save = languageUtil.getResource("circumstances_save") //保存
    window.lang_addTime = languageUtil.getResource("timeTask1_addTime") //定时
    window.lang_text = languageUtil.getResource("timeTask1_text") //请至少选择一天
    window.lang_addTask = languageUtil.getResource("addTask");
    window.lang_afterMinsDo = languageUtil.getResource("afterMinsDo"); //分钟后执行
    window.lang_afterSecDo = languageUtil.getResource("afterSecDo"); //秒后执行
    window.lang_immediatelyDo = languageUtil.getResource("immediatelyDo"); //立即执行
    //6.0.3
    window.lang_D7_mode_dinner = languageUtil.getResource("mode_1"); //烛光晚餐
    window.lang_D7_mode_moon = languageUtil.getResource("mode_2"); //月色迷人
    window.lang_D7_mode_blue = languageUtil.getResource("mode_3"); //蓝色小夜曲
    window.lang_D7_mode_green = languageUtil.getResource("mode_4"); //绿色心情

    //6.1.2
    window.lang_switch_cut = languageUtil.getResource("Switch_Setstate_handover"); //切换

    window.device_name_Ba = languageUtil.getResource("device_name_Ba"); //两位插座
    window.device_Ba_SocketName = languageUtil.getResource("device_Ba_SocketName"); //"插座"
    window.device_Ba_Electricity = languageUtil.getResource("device_Ba_Electricity"); //"通电"
    window.device_Ba_PowerFailure = languageUtil.getResource("device_Ba_PowerFailure"); //"断电"

    //6.1.3
    window.device_name_Aw = languageUtil.getResource("device_name_Aw"); //1/2开关模组
    window.device_name_Ax = languageUtil.getResource("device_name_Ax"); //六路开关模组
    window.lang_name_22 = languageUtil.getResource("device_name_22"); //全角度红外转发器
    window.lang_name_23 = languageUtil.getResource("device_name_23"); //半角度红外转发器
    window.lang_name_Oj = languageUtil.getResource("device_name_Oj"); //"新风控制器"

    window.device_name_Be = languageUtil.getResource("device_name_Be"); //"1/2模组-调光"
    window.switch_on = languageUtil.getResource("switch_Aohk_01"); // 打开
    window.switch_off = languageUtil.getResource("switch_Aohk_02"); // 关闭

    //6.1.4
    window.lang_name_OZ = languageUtil.getResource("device_OZ_name"); //"空调翻译器"
    window.lang_OZ_Open = languageUtil.getResource("houseKeeper1_OZ_Open"); //"开关"
    window.lang_OZ_Type = languageUtil.getResource("houseKeeper1_OZ_Type"); // "模式";
    window.lang_OZ_Type1 = languageUtil.getResource("houseKeeper1_OZ_Type1"); // "制冷";
    window.lang_OZ_Type2 = languageUtil.getResource("houseKeeper1_OZ_Type2"); // "制热";
    window.lang_OZ_Type3 = languageUtil.getResource("houseKeeper1_OZ_Type3"); // "送风";
    window.lang_OZ_Type4 = languageUtil.getResource("houseKeeper1_OZ_Type4"); // "除湿";
    window.lang_OZ_Type5 = languageUtil.getResource("houseKeeper1_OZ_Type5"); // "预热";
    window.lang_OZ_Type6 = languageUtil.getResource("houseKeeper1_OZ_Type6"); // "自动";
    window.lang_OZ_Type7 = languageUtil.getResource("houseKeeper1_OZ_Type7"); // "干燥";
    window.lang_OZ_Type8 = languageUtil.getResource("houseKeeper1_OZ_Type8"); // "清爽";
    window.lang_OZ_Type9 = languageUtil.getResource("houseKeeper1_OZ_Type9"); // "睡眠";
    window.lang_OZ_Type10 = languageUtil.getResource("houseKeeper1_OZ_Type10"); // "杀菌";
    window.lang_OZ_Type11 = languageUtil.getResource("houseKeeper1_OZ_Type11"); // "干爽除湿";
    window.lang_OZ_Type12 = languageUtil.getResource("houseKeeper1_OZ_Type12"); // "强力除湿";
    window.lang_OZ_Speed = languageUtil.getResource("houseKeeper1_OZ_Speed"); //"风速";
    window.lang_OZ_Speed1 = languageUtil.getResource("houseKeeper1_OZ_Speed1"); // "高";
    window.lang_OZ_Speed2 = languageUtil.getResource("houseKeeper1_OZ_Speed2"); // "中";
    window.lang_OZ_Speed3 = languageUtil.getResource("houseKeeper1_OZ_Speed3"); // "低";
    window.lang_OZ_Speed4 = languageUtil.getResource("houseKeeper1_OZ_Speed4"); // "自动";
    window.lang_OZ_Direction = languageUtil.getResource("houseKeeper1_OZ_Direction"); // "风向";
    window.lang_OZ_Temperature = languageUtil.getResource("houseKeeper1_OZ_Temperature"); // "温度";
    window.lang_OZ_Confirm = languageUtil.getResource("houseKeeper1_OZ_Confirm"); // "确定";
    window.lang_OZ_Cancel = languageUtil.getResource("houseKeeper1_OZ_Cancel"); // "取消";
    window.lang_OZ_TIPS2 = languageUtil.getResource("houseKeeper3_OZ_TIPS2"); // "全选";

    window.lang_name_24 = languageUtil.getResource("device_name_24"); //万能红外转发器

    //6.1.5
    window.lang_name_28 = languageUtil.getResource("device_name_28"); //"智能水阀"
    window.lang_name_a1 = languageUtil.getResource("device_name_a1"); //幕帘探测器
    window.lang_a1_left = languageUtil.getResource("device_34_01"); //设防下左区域有人经过
    window.lang_a1_right = languageUtil.getResource("device_34_02"); //设防下右区域有人经过
    window.lang_a1_normal = languageUtil.getResource("device_34_03"); //正常
    window.device_28_close_after = languageUtil.getResource("device_28_close_after"); //"后关闭"
    window.lang_name_44 = languageUtil.getResource("device_name_44"); //粉尘检测器
    window.lang_and = languageUtil.getResource("Conditional_relationship_and_02"); //与
    window.lang_or = languageUtil.getResource("Conditional_relationship_or_02"); //或
    window.lang_scene_opend = languageUtil.getResource("Scenes_is_open"); //场景开启时
    window.lang_name_Ad = languageUtil.getResource("device_name_Ad"); //"多功能红外人体探测器（电池版）"
    window.lang_name_C0 = languageUtil.getResource("device_name_C0"); //"多功能红外人体探测器（强电版）"
    window.lang_name_Ol = languageUtil.getResource(""); //"风盘地暖温控器"
    window.lang_item_infrared = languageUtil.getResource("Device_state_01"); //"红外人体探测"
    window.lang_item_humiture = languageUtil.getResource("Device_state_02"); //"温湿度"
    window.lang_item_light_intensity = languageUtil.getResource("Device_state_03"); //"光强"
    window.houseKeeper_Someone_passed = languageUtil.getResource("houseKeeper_Someone_passed"); //"有人经过"

    //6.1.6
    window.lang_name_A1 = languageUtil.getResource("device_name_A1"); //"四路翻译器"
    window.lang_A1_way = languageUtil.getResource("device_A1_way"); //"路"
    window.lang_A1_alarm = languageUtil.getResource("device_A1_abnormal"); //"有报警"
    window.lang_A1_normal = languageUtil.getResource("device_A1_normal"); //"正常"
    window.lang_name_A2 = languageUtil.getResource("device_name_A2"); //"二路输出型翻译器"
    window.lang_name_Bo = languageUtil.getResource("device_name_Bo"); //"二路输出型翻译器02型"

    window.device_Ok_name = languageUtil.getResource("device_Ok_name"); //"分体式智能空调"
    window.houseKeeper_Ok_Mute = languageUtil.getResource("houseKeeper2_Ok_Mute"); //静音
    window.houseKeeper_Ok_Direction2 = languageUtil.getResource("houseKeeper1_Ok_Direction2"); //"左右风";
    window.houseKeeper_Ok_Direction1 = languageUtil.getResource("houseKeeper1_Ok_Direction1"); //"上下风";
    window.houseKeeper_Ok_Direction_close = languageUtil.getResource("airsystem_Oj_close"); //"关闭";

    window.lang_name_Ol = languageUtil.getResource("device_name_Ol"); //风盘地暖温控器
    window.device_Ol_mode_cool = languageUtil.getResource("device_Ol_mode_cool"); //"制冷";
    window.device_Ol_mode_heat = languageUtil.getResource("device_Ol_mode_heat"); //"制热";
    window.device_Ol_mode_fan = languageUtil.getResource("device_Ol_mode_fan"); //"送风";
    window.device_Ol_mode_warm = languageUtil.getResource("device_Ol_mode_warm"); //"地暖";
    window.device_Ol_mode_warm_heat = languageUtil.getResource("device_Ol_mode_warm_heat"); //"地暖+制热";

    window.lang_name_B9 = languageUtil.getResource("device_name_B9");
    window.lang_B9_way_in = languageUtil.getResource("device_B9_way_in");
    window.lang_B9_way_out = languageUtil.getResource("device_B9_way_out");
    window.lang_switch_on = languageUtil.getResource("switch_on"); //"开";
    window.lang_switch_off = languageUtil.getResource("switch_off"); //"关";

    // 6.1.7
    window.aw_bell_short = languageUtil.getResource("device_Aw_short"); //"段铃";
    window.aw_bell_long = languageUtil.getResource("device_Aw_long"); //"响铃";
    window.aw_bell_stop = languageUtil.getResource("device_Aw_stop"); //"停止";
    window.lang_name_XW01 = languageUtil.getResource("device_name_XW01"); //向往背景音乐
    window.lang_XW01_play = languageUtil.getResource("Music_houseKeeper_play"); //播放
    window.lang_XW01_stop = languageUtil.getResource("Music_houseKeeper_stop"); //停止播放
    window.lang_XW01_volume = languageUtil.getResource("minigateway_Sound_volume"); //音量

    window.lang_name_HS05 = languageUtil.getResource("device_HS05_name"); //"海信冰箱"
    window.lang_HS05_model = languageUtil.getResource("Refrigerator_Pattern"); //"模式"
    window.lang_HS05_model_0 = languageUtil.getResource("Refrigerator_mode_Holiday"); //"假日模式"
    window.lang_HS05_model_1 = languageUtil.getResource("Refrigerator_mode_Intelligence"); //"智能模式"
    window.lang_HS05_model_2 = languageUtil.getResource("Refrigerator_mode_Quickfrozen"); //"速冻模式"

    // 6.1.8
    window.lang_name_CMICA2 = languageUtil.getResource("device_name_CMICA2"); //随便看
    window.lang_name_CMICA3 = languageUtil.getResource("device_name_CMICA3"); //企鹅
    window.lang_name_CMICA4 = languageUtil.getResource("device_name_CMICA4"); //小物摄像机
    window.lang_name_CMICA5 = languageUtil.getResource("device_name_CMICA5"); //户外摄像机
    window.lang_name_CMICA6 = languageUtil.getResource("device_name_CMICA6"); //企鹅4G
    window.lang_camera_safety = languageUtil.getResource("lang_camera_safety"); //安全防护
    window.lang_camera_position = languageUtil.getResource("lang_camera_position"); //预置位
    window.lang_camera_opencamera = languageUtil.getResource("camera_houseKeeper_task"); //开启视频
    window.lang_camera_safety_open = languageUtil.getResource("lang_camera_safety_open"); //开启安全防护
    window.lang_camera_safety_close = languageUtil.getResource("lang_camera_safety_close"); //关闭安全防护

    //6.1.9
    window.lang_name_On = languageUtil.getResource("device_name_On"); //全热交换型新风系统
    window.lang_airsystem_Oj_Wind_speed_low = languageUtil.getResource("airsystem_Oj_Wind_speed_low"); //低风
    window.lang_airsystem_Oj_Wind_speed_mid = languageUtil.getResource("airsystem_Oj_Wind_speed_medium"); //中风
    window.lang_airsystem_Oj_Wind_speed_high = languageUtil.getResource("airsystem_Oj_Wind_speed_high"); //高风
    window.lang_allAreas = languageUtil.getResource("device_list_allAreas"); //全部分区
    window.lang_categories = languageUtil.getResource("device_list_Categories"); //全部分区


    window.lang_name_Bn = languageUtil.getResource("device_name_Bn"); //全部分区
    window.lang_name_Bq = languageUtil.getResource("device_Bq_name"); //Bq

    window.lang_name_Af = languageUtil.getResource("device_Af_name"); //Af
    window.device_Af_Cool = languageUtil.getResource("device_Af_Cool") //制冷节能模式";
    window.device_Af_Hot = languageUtil.getResource("device_Af_Hot") //制热节能模式";
    window.lang_name_Bp = languageUtil.getResource("device_Bp_name"); //罗格朗空调
    window.direction_swing = languageUtil.getResource("device_Bp_Swing"); //罗格朗空调摆动

    //6.2.1
    window.lang_openSingle = languageUtil.getResource("openSingle") //"开"
    window.lang_closeSingle = languageUtil.getResource("closeSingle") //"关"
    window.lang_stopSingle = languageUtil.getResource("stopSingle") //"停"
    window.Add_batches_switch = languageUtil.getResource("Add_batches_switch"); //开关类设备
    window.Add_batches_curtain = languageUtil.getResource("Add_batches_curtain"); //窗帘类设备
    window.Add_batches_socket = languageUtil.getResource("Add_batches_socket"); //插座类设备
    window.Add_batches_save = languageUtil.getResource("Add_batches_save"); //请设置完所有设备状态后，再保存
    window.unselect_all = languageUtil.getResource("unselect_all"); //全不选

    //6.2.2
    window.lang_name_Br = languageUtil.getResource("device_Br_name");
    window.lang_name_Bt = languageUtil.getResource("device_name_Bt"); // 联排智能插座
    window.lang_name_IF01 = languageUtil.getResource("device_IF01_name") //"wifi红外转发器"
    window.lang_name_a0 = languageUtil.getResource("device_a0_name") //"wifi红外转发器"
    window.lang_a0_Type1 = languageUtil.getResource("houseKeeper1_OZ_Type3"); // "送风";
    window.lang_a0_Type2 = languageUtil.getResource("houseKeeper1_OZ_Type2"); // "制热";
    window.lang_a0_Type3 = languageUtil.getResource("houseKeeper1_OZ_Type1"); // "制冷";
    window.lang_a0_Type4 = languageUtil.getResource("houseKeeper1_OZ_Type4"); // "除湿";

    window.lang_name_Bu = languageUtil.getResource("device_name_Bu"); // "Bu";
    window.lang_name_Bv = languageUtil.getResource("device_name_Bv"); // "Bv";
    window.Device_Bs_01 = languageUtil.getResource("Device_Bs_01");
    window.Device_Bs_02 = languageUtil.getResource("Device_Bs_02");
    window.Device_Bs_03 = languageUtil.getResource("Device_Bs_03");
    window.Device_Bs_04 = languageUtil.getResource("Device_Bs_04");
    window.device_name_Bs = languageUtil.getResource("device_name_Bs");
    window.Device_Bs_05 = languageUtil.getResource("Device_Bs_05");
    window.Device_Bs_06 = languageUtil.getResource("Device_Bs_06");
    window.lang_name_Bw = languageUtil.getResource("device_name_Bw"); // "Bw";

    window.Device_a0_host = languageUtil.getResource("Device_a0_host");
    window.Device_a0_air = languageUtil.getResource("Device_a0_aircondition");

    //6.2.3
    window.lang_text30_1 = languageUtil.getResource("circumstances_txt30_1");

    //6.2.4
    window.lang_name_Bx = languageUtil.getResource("device_Bx_name"); //联排地暖控制器
    window.lang_19_above = languageUtil.getResource("device_LX_Above_02"); //光强高于
    window.lang_19_below = languageUtil.getResource("device_LX_Below_02"); //光强低于

    //6.2.5
    window.lang_name_By = languageUtil.getResource("device_By_name"); //联排地暖控制器
    window.direction_01 = languageUtil.getResource("Wind_Direction_01"); //大角度
    window.direction_02 = languageUtil.getResource("Wind_Direction_02"); //中角度
    window.direction_03 = languageUtil.getResource("Wind_Direction_03"); //小角度
    window.direction_04 = languageUtil.getResource("Wind_Direction_04"); //扫风

    //6.2.6
    window.lang_name_91 = languageUtil.getResource("device_91_name"); //调色温调光LED灯
    window.device_91_01 = languageUtil.getResource("device_91_01");//"中性白"
    window.device_91_02 = languageUtil.getResource("device_91_02");//"冷色："
    window.device_91_03 = languageUtil.getResource("device_91_03");//"暖色："
    window.device_91_04 = languageUtil.getResource("device_91_04");//"亮度："
    window.lang_PL_01 = languageUtil.getResource("houseKeeper1_Press_button");//按压设备一次

    //6.2.7
    window.lang_top = languageUtil.getResource("houseKeeper1_Press_button");//上键
    window.device_Cb_name = languageUtil.getResource("device_name_Cb"); //智敬二路窗帘
    window.lang_Ca_up = languageUtil.getResource("device_Ca_up");//"上键"
    window.lang_Ca_down = languageUtil.getResource("device_Ca_down");//"下键"
    window.lang_Press_button = languageUtil.getResource("device_Ca_Press_button");//"按压设备一次"
    window.lang_Ca_up_btn = languageUtil.getResource("device_Ca_up_press");//"上键被按下"
    window.lang_Ca_down_btn = languageUtil.getResource("device_Ca_down_press");//"下键被按下"
    window.device_Ca_name = languageUtil.getResource("device_name_Ca");//"智敬一路窗帘"
    window.lang_Arm_01 = languageUtil.getResource("device_03_Arm_01");//"设防下被打开"
    window.lang_Arm_02 = languageUtil.getResource("device_03_Arm_02");//"设防下被关闭"
    window.lang_Disarm_01 = languageUtil.getResource("device_03_Disarm_01");//"撤防下被打开"
    window.lang_Disarm_02 = languageUtil.getResource("device_03_Disarm_02");//"撤防下被关闭"
    window.lang_device_mode_fail = languageUtil.getResource("device_Ca_mode_fail");//"请先将设备设为窗帘模式"
    window.lang_mode_fail_02 = languageUtil.getResource("device_Ca_mode_fail_02");//"请先将设备设为触发开关模式";

    //6.2.8
    window.lang_name_64 = languageUtil.getResource("device_name_64");//"64";
    //6.2.9
    window.lang_name_Cj = languageUtil.getResource("device_Cj_name");//"64";
    window.lang_name_Op = languageUtil.getResource("device_name_Op");//"Op";Bu
    //6.3.0
    window.lang_name_Cl = languageUtil.getResource("device_name_Cl"); // "一路智能开关";
    window.lang_name_Cm = languageUtil.getResource("device_name_Cm"); // "二路智能开关";
    window.lang_name_Cn = languageUtil.getResource("device_name_Cn"); // "三路智能开关";
    window.lang_name_Ck = languageUtil.getResource("device_name_Ck"); // "智敬新风控制器";
    //6.3.1
    window.lang_name_Co = languageUtil.getResource("device_name_Co"); // "杜亚窗帘电机";
}

/*
 * 处理标签中的国际化
 */
function initLanguage() {
    globalLang();
    var item = document.getElementsByClassName("autoSwitchLanguager");
    for (var i = 0; i < item.length; i++) {
        var languagerText
        if (!(item[i].getAttribute("key") == null) || item[i].getAttribute("key") == "") {
            languagerText = languageUtil.getResource(item[i].getAttribute("key"));
        } else {
            languagerText = languageUtil.getResource(item[i].id);
        }
        if (item[i].placeholder != "" && item[i].localName == "input") {
            if (languagerText) {
                item[i].placeholder = languagerText;
            }
        } else {
            if (languagerText) {
                if (item[i].type == "button") {
                    item[i].value = languagerText;
                } else {
                    item[i].innerHTML = languagerText;
                }
            }
        }
    }
}

var languageUtil = function () {};

languageUtil.prototype = {

    /**
     * 自定义html控件属性，例如 langKey="Key_hello"; 指明语言资源的键 为  Key_hello
     */
    langKey: "langKey",
    /**
     * 语言文件(例如 zh.js)是否正在加载
     */
    isLoading: false,
    /**
     * 语言文件(例如 zh.js)是否加载完成
     */
    isLoaded: false,
    /**
     * 当前语言  "zh"   "en" ....
     */
    currentlangCode: "",
    /**
     * 当前的语言文件对象
     */
    currentJsObj: null,

    /**
     *  初始化 加载 langCode对应的js
     * @param {Object} langCode 例如 "zh" "en"
     * @param {Object} langSrcFolder 语言文件所在位置的相对路径
     */
    init: function (langCode, langSrcFolder) {
        try {
            if (this.currentlangCode != langCode) {
                this.removeJs(this.currentJsObj);
                this.currentlangCode = langCode;
                var that = this;
                this.currentJsObj = this.loadJs(langSrcFolder + langCode + ".js");
            }
        } catch (e) {
            console.log(e);
        }
        return this.currentJsObj;
    },
    /**
     * 转换语言
     * @param {Object} node 需要转换的节点
     */
    doSwitch: function (node) {
        var children = node.children;
        if (children.length > 0) {
            for (var i = 0; i < children.length; i++) {
                this.doSwitch(children[i]);
            }
        } else {
            var key = node.attributes[this.langKey];
            if (typeof key != "undefined") {
                switch (node.type) {
                    case "button":
                        node.value = $lang[key.value];
                        break;

                    default:
                        node.textContent = $lang[key.value];
                        break;
                }

            }
        }
    },
    /**
     * 动态装载js
     */
    loadJs: function (jsPath) {
        this.isLoaded = false;
        this.isLoading = true;
        var mHead = document.getElementsByTagName("head")[0];
        var jsObj = document.createElement("script");
        jsObj.src = jsPath;
        jsObj.type = "text/javascript";
        mHead.appendChild(jsObj);
        jsObj.onload = function () {
            languageUtil.onJsLoaded();
        };
        jsObj.onerror = function () {
            if (this.currentlangCode != "en") {
                languageUtil.init("en", "lang/");
            }
        }
        return jsObj;
    },
    /**
     * 当动态装载的js准备完毕调用
     */
    onJsLoaded: function () {
        this.isLoaded = true;
        this.isLoading = false;
        var all = document.all;
        for (var i = 0; i < all.length; i++) {
            var node = all[i];
            var key = node.attributes[this.langKey];
            if (typeof key != "undefined") {
                switch (node.type) {
                    case "button":
                        node.value = $lang[key.value];
                        break;

                    default:
                        node.textContent = $lang[key.value];
                        break;
                }

            }
        }

        /*
		var mBody = document.getElementsByTagName("body")[0];
		if (mBody == null) return;
		this.doSwitch(mBody);
                */
    },
    /**
     * 移除
     */
    removeJs: function (jsObj) {
        if (jsObj != null) {
            var mHead = document.getElementsByTagName("head")[0];
            mHead.removeChild(jsObj);
        }
        this.isLoaded = false;
        this.isLoading = false;
    },
    /**
     * 根据 key 获取语言资源文件
     * @param {Object} key
     */
    getResource: function (key) {
        try {
            return $lang[key];
        } catch (e) {
            return "";
        }
    }

};

window.languageUtil = new languageUtil();
