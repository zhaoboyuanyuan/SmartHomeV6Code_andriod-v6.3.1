/*
 * type:
 * 1:提示电工作业危险类型
 * 2:提示接通电源 （type1、2 都为强电设备，暂时一样）
 * 3:提示电池设备
 */
window.Model = {
    //type:1类型

    //声光报警器
    "01": {
        type: "1",
        img1: "../fonts/add_01.png",
        img2: "../fonts/add_tint_01.png",
        title: "addDevice_01_title",
        name: "addDevice_01_name",
        hint2: "addDevice_01_02_step"
    },
    //添加Wulian单火线单路触摸调光开关
    "12": {
        type: "1",
        img1: "../fonts/add_12_01.png",
        img2: "../fonts/add_12_02.png",
        title: "addDevice_12_title",
        name: "addDevice_12_name",
        hint2: "addDevice_01_02_step"
    },
    //添加中继器
    "31": {
        type: "1",
        img1: "../fonts/repeater_add_01.png",
        img2: "../fonts/repeater_add_02.png",
        title: "addDevice_31_title",
        name: "addDevice_31_name",
        hint2: "addDevice_01_02_step"
    },
    //添加二氧化碳监测器
    "42": {
        type: "1",
        img1: "../fonts/add_42_01.png",
        img2: "../fonts/add_42_02.png",
        title: "addDevice_42_title",
        name: "addDevice_42_name",
        hint2: "addDevice_01_02_step"
    },
    //墙面插座器
    "50": {
        type: "1",
        img1: "../../source/addDevicePic/add_50_01.png",
        img2: "../../source/addDevicePic/add_50_02.png",
        title: "addDevice_50_title",
        name: "addDevice_50_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    //墙面插座器
    "Bt": {
        type: "1",
        img1: "../fonts/add_Bt.png",
        img2: "../fonts/add_Bt_02.png",
        title: "addDevice_Bt_title",
        name: "device_Bt_name",
        hint2: "addDevice_Bt_way"
    },
    //添加窗帘电机
    "80": {
        type: "1",
        img1: "../fonts/add_80_01.png",
        img2: "../fonts/add_80_02.png",
        title: "addDevice_80_title",
        name: "device_80_name",
        hint2: "addDevice_01_02_step"
    },
    //添加杜亚窗帘电机
    "Co": {
        type: "1",
        img1: "../../source/addDevicePic/add_Co_01.png",
        img2: "../../source/addDevicePic/add_Co_02.png",
        title: "addDevice_Co_title",
        name: "device_Co_name",
        hint2: "addDevice_01_02_step"
    },
    //添加双轨双驱自动窗帘
    "81": {
        type: "1",
        img1: "../fonts/add_81_01.png",
        img2: "../fonts/add_81_02.png",
        title: "addDevice_81_title",
        name: "addDevice_81_name",
        hint2: "addDevice_01_02_step"
    },
    //添加门铃声光器
    "A6": {
        type: "1",
        img1: "../fonts/add_A6_01.png",
        img2: "../fonts/add_A6_02.png",
        title: "addDevice_A6_title",
        name: "addDevice_A6_name",
        hint2: "addDevice_01_02_step"
    },
    //添加内嵌零火一路开关
    "Aj": {
        type: "1",
        img1: "../fonts/add_Aj_01.png",
        img2: "../fonts/add_Aj_02.png",
        title: "addDevice_Aj_title",
        name: "addDevice_Aj_name",
        hint2: "addDevice_Aj_02"
    },
    //添加内嵌零火二路开关
    "At": {
        type: "1",
        img1: "../fonts/add_Aj_01.png",
        img2: "../../source/addDevicePic/add_At_02.png",
        title: "addDevice_At_title",
        name: "addDevice_At_name",
        hint2: "addDevice_Aj_02"
    },
    //风盘地暖控制器（江森温控器）
    "Ol": {
        type: "1",
        img1: "../fonts/add_Ol_01.png",
        img2: "../fonts/add_Ol_02.png",
        title: "addDevice_Ol_title",
        name: "device_Ol_name",
        hint2: "addDevice_01_02_step"
    },
    //联排温控器
    "Br": {
        type: "1",
        img1: "../fonts/add_Br_01.png",
        img2: "../fonts/add_Br_02.png",
        title: "addDevice_Br_title",
        name: "device_Br_name",
        hint2: "addDevice_01_02_step"
    },
    //丹佛斯地暖翻译器
    "Bs": {
        type: "1",
        img1: "../fonts/add_Bs_01.png",
        img2: "../fonts/add_Bs_02.png",
        title: "addDevice_name_Bs",
        name: "Device_name_Bs",
        hint2: "addDevice_01_02_step"
    },
    //新风翻译器（海信日立全热交换新风系统）
    "On": {
        type: "3",
        img1: "../fonts/add_On_01.png",
        img2: "../fonts/add_On_02.png",
        title: "addDevice_On_title",
        name: "device_On_name",
        hint2: "addDevice_01_02_step"
    },
    "Bu": {
        type: "1",
        img1: "../../source/addDevicePic/add_Bu_01.png",
        img2: "../../source/addDevicePic/add_Bu_02.png",
        title: "addDevice_Bu_title",
        name: "device_Bu_name",
        hint2: "addDevice_Bu_way"
    },
    "Bw": {
        type: "1",
        img1: "../../source/addDevicePic/add_Bw_01.png",
        img2: "../../source/addDevicePic/add_Bw_02.png",
        title: "addDevice_Bw_title",
        name: "device_Bw_name",
        hint2: "addDevice_Bw_way"
    },
    "Bu_01": {
        type: "1",
        img1: "../../source/addDevicePic/add_Bu_01_01.png",
        img2: "../../source/addDevicePic/add_Bu_01_02.png",
        title: "addDevice_Bu_01_title",
        name: "device_Bu_01_name",
        hint2: "addDevice_Bu_way"
    },
    "Bv_01": {
        type: "1",
        img1: "../../source/addDevicePic/add_Bv_01_01.png",
        img2: "../../source/addDevicePic/add_Bv_01_02.png",
        title: "addDevice_Bv_01_title",
        name: "device_Bv_01_name",
        hint2: "addDevice_Bw_way"
    },
    "Bw_01": {
        type: "1",
        img1: "../../source/addDevicePic/add_Bw_01_01.png",
        img2: "../../source/addDevicePic/add_Bw_01_02.png",
        title: "addDevice_Bw_01_title",
        name: "device_Bw_01_name",
        hint2: "addDevice_Bw_way"
    },

    //type:2类型

    //添加一路绑定开关
    "52": {
        type: "2",
        img1: "../fonts/add_52_01.png",
        img2: "../fonts/add_52_02.png",
        title: "adddevice_52_title",
        name: "addDevice_52_name",
        hint2: "addDevice_01_02_step"
    },

    //添加两路绑定开关
    "54": {
        type: "2",
        img1: "../fonts/add_54_01.png",
        img2: "../fonts/add_54_02.png",
        title: "adddevice_54_title",
        name: "addDevice_54_name",
        hint2: "addDevice_01_02_step"
    },

    //添加两路绑定开关
    "55": {
        type: "2",
        img1: "../fonts/add_55_01.png",
        img2: "../fonts/add_55_02.png",
        title: "adddevice_55_title",
        name: "addDevice_55_name",
        hint2: "addDevice_01_02_step"
    },

    //添加单路绑定开关
    "61": {
        type: "2",
        img1: "../../source/addDevicePic/add_61_01.png",
        img2: "../../source/addDevicePic/add_61_02.png",
        title: "addDevice_61_name",
        name: "addDevice_61_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    //添加两路开关
    "62": {
        type: "2",
        img1: "../../source/addDevicePic/add_62_01.png",
        img2: "../../source/addDevicePic/add_62_02.png",
        title: "addDevice_62_name",
        name: "addDevice_62_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    //添加三路定开关
    "63": {
        type: "2",
        img1: "../../source/addDevicePic/add_63_01.png",
        img2: "../../source/addDevicePic/add_63_02.png",
        title: "addDevice_63_name",
        name: "addDevice_63_name",
        hint2: "addDevice_01_03_step_LGL"
    },

    //窗帘控制器02型
    "65": {
        type: "2",
        img1: "../fonts/add_65_01.png",
        img2: "../fonts/add_65_02.png",
        title: "adddevice_65_name",
        name: "addDevice_65_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    //移动插座计量版
    "77": {
        type: "3",
        img1: "../../source/addDevicePic/add_77_01.png",
        img2: "../../source/addDevicePic/add_77_02.png",
        title: "adddevice_77_title",
        name: "addDevice_77_name",
        hint2: "addDevice_Aj_02"
    },
    //一位DIN开关计量版
    "Ai": {
        type: "2",
        img1: "../../source/addDevicePic/add_Ai_01.png",
        img2: "../../source/addDevicePic/add_Ai_02.png",
        title: "adddevice_Ai_title",
        name: "addDevice_Ai_name",
        hint2: "addDevice_Aj_02"
    },
    //金属单路开关
    "Am": {
        type: "2",
        img1: "../fonts/add_Am_01.png",
        img2: "../fonts/add_Am_02.png",
        title: "addDevice_Am_name",
        name: "device_Am_name",
        hint2: "addDevice_Ar_p2_step"
    },
    //金属两路开关
    "An": {
        type: "2",
        img1: "../fonts/add_An_01.png",
        img2: "../fonts/add_An_02.png",
        title: "addDevice_An_name",
        name: "device_An_name",
        hint2: "addDevice_Ar_p2_step"
    },
    //金属三路开关
    "Ao": {
        type: "2",
        img1: "../fonts/icon_switch_ao_02.png",
        img2: "../fonts/add_Ao_02.png",
        title: "addDevice_Ao_name",
        name: "device_Ao_name",
        hint2: "addDevice_Ar_p2_step"
    },
    //金属窗帘控制器
    "Ar": {
        type: "2",
        img1: "../fonts/add_Ar_01.png",
        img2: "../fonts/add_Ar_02.png",
        title: "addDevice_Ar_title",
        name: "addDevice_Ar_name",
        hint2: "addDevice_Ar_p2_step"
    },
    //内嵌式窗帘控制器
    "Au": {
        type: "2",
        img1: "../fonts/add_Au_01.png",
        img2: "../fonts/add_Au_02.png",
        title: "addDevice_Au_title",
        name: "Device_Au_name",
        hint2: "addDevice_01_02_step"
    },
    "Bh": {
        type: "2",
        img1: "../fonts/add_Bh_01.png",
        img2: "../fonts/add_Bh_02.png",
        title: "addDevice_Bh_title",
        name: "addDevice_Bh_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    "OZ": {
        type: "2",
        img1: "../../source/addDevicePic/add_OZ_01.png",
        img2: "../../source/addDevicePic/add_OZ_02.png",
        title: "device_OZ_title",
        name: "device_OZ_name",
        hint2: "device_OZ_Step02"
    },
    "A1": {
        type: "2",
        img1: "../../source/addDevicePic/add_A1_01.png",
        img2: "../../source/addDevicePic/add_A1_02.png",
        title: "addDevice_A1_title",
        name: "device_A1_name",
        hint2: "addDevice_01_02_step"
    },
    //输入输出型翻译器
    B9: {
        type: "2",
        img1: "../fonts/add-translator-icon.png",
        img2: "../fonts/add-translator-end.png",
        title: "addDevice_B9_title",
        name: "device_B9_name",
        hint2: "addDevice_01_02_step"
    },

    //type:3类型

    //添加红外入侵探测器
    "02": {
        type: "3",
        img1: "../fonts/add_02_01.png",
        img2: "../fonts/add_02_02.png",
        title: "addDevice_02_title",
        name: "addDevice_02_name",
        hint2: "addDevice_01_02_step"
    },

    //添加门窗磁感应器
    "03": {
        type: "3",
        img1: "../fonts/add_03_01.png",
        img2: "../fonts/add_03_02.png",
        title: "addDevice_03_title",
        name: "addDevice_03_name",
        hint2: "addDevice_01_02_step"
    },

    //添加紧急按钮
    "04": {
        type: "3",
        img1: "../fonts/add_04_01.png",
        img2: "../fonts/add_04_02.png",
        title: "addDevice_04_title",
        name: "addDevice_04_name",
        hint2: "addDevice_04_step"
    },

    //水浸监测器
    "06": {
        type: "3",
        img1: "../fonts/add_06_01.png",
        img2: "../fonts/add_06_02.png",
        title: "addDevice_06_title",
        name: "addDevice_06_name",
        hint2: "addDevice_01_02_step"
    },

    //可燃气体泄漏
    "09": {
        type: "3",
        img1: "../fonts/add_09_01.png",
        img2: "../fonts/add_09_02.png",
        title: "addDevice_09_title",
        name: "addDevice_09_name",
        hint2: "addDevice_01_02_step"
    },

    //添加温湿度监测器
    "17": {
        type: "3",
        img1: "../fonts/add_17_01.png",
        img2: "../fonts/add_17_02.png",
        title: "addDevice_17_title",
        name: "addDevice_17_name",
        hint2: "addDevice_01_02_step"
    },

    //添加六键场景开关
    "37": {
        type: "3",
        img1: "../fonts/add_37_01.png",
        img2: "../fonts/add_37_02.png",
        title: "addDevice_37_title",
        name: "addDevice_37_name",
        hint2: "addDevice_01_02_step"
    },

    //添加六键场景开关
    "91": {
        type: "3",
        img1: "../fonts/add_91_01.png",
        img2: "../fonts/add_91_01.png",
        title: "addDevice_91_title",
        name: "device_91_name",
        hint2: "addDevice_91_step_2"
    },

    //添加四键场景开关
    "34": {
        type: "3",
        img1: "../fonts/add_34_01.png",
        img2: "../fonts/add_34_02.png",
        title: "addDevice_34_title",
        name: "Device_34_name",
        hint2: "addDevice_01_02_step"
    },

    //添加烟雾探测器
    "43": {
        type: "3",
        img1: "../fonts/add_43_01.png",
        img2: "../fonts/add_43_02.png",
        title: "addDevice_43_title",
        name: "addDevice_43_name",
        hint2: "addDevice_01_02_step"
    },

    //添加门铃按钮
    A5: {
        type: "3",
        img1: "../fonts/add_A5_01.png",
        img2: "../fonts/add_A5_02.png",
        title: "addDevice_A5_title",
        name: "addDevice_A5_name",
        hint2: "addDevice_04_step"
    },

    //紫金山锁
    OW: {
        type: "3",
        img1: "../../source/addDevicePic/img_OW_set_1.png",
        img2: "../../source/addDevicePic/img_OW_set_2.png",
        title: "addDevice_OW_name",
        name: "addDevice_OW_name",
        hint2: "addDevice_OW_zj_step2"
    },

    //红外转发器
    "23": {
        type: "3",
        img1: "../fonts/add_23_01.png",
        img2: "../fonts/add_23_02.png",
        title: "AddDevice_23_Infraredtransponder",
        name: "AddDevice_23_Infraredtransponder",
        hint2: "addDevice_01_02_step"
    },
    Bj: {
        type: "3",
        img1: "../fonts/add_Bj_01.png",
        img2: "../fonts/add_Bj_02.png",
        title: "addDevice_Bj_title",
        name: "Device_Bj_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    Bk: {
        type: "3",
        img1: "../fonts/add_Bk_01.png",
        img2: "../fonts/add_Bk_02.png",
        title: "addDevice_Bk_title",
        name: "Device_Bk_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    Bl: {
        type: "3",
        img1: "../../source/addDevicePic/add_Bl_01.png",
        img2: "../../source/addDevicePic/add_Bl_02.png",
        title: "addDevice_Bl_title",
        name: "addDevice_Bl_title",
        hint2: "addDevice_01_03_step_LGL"
    },
    "82": {
        type: "2",
        img1: "../fonts/add_82_01.png",
        img2: "../fonts/add_82_02.png",
        title: "device_82_title",
        name: "device_82_name",
        hint2: "addDevice_01_02_step"
    },
    OF: {
        type: "3",
        img1: "../fonts/add_OF_01.png",
        img2: "../fonts/add_OF_01.png",
        title: "addDevice_OF_title",
        name: "device_OF_name",
        hint2: "addDevice_01_02_step"
    },
    Oj: {
        type: "3",
        img1: "../fonts/add_Oj_01.png",
        img2: "../fonts/add_Oj_02.png",
        title: "addDevice_Oj_title",
        name: "device_Oj_name",
        hint2: "addDevice_Oj_step_3"
    },
    Oi: {
        type: "2",
        img1: "../fonts/add_Oi_01.png",
        img2: "../fonts/add_Oi_02.png",
        title: "device_Oi_title",
        name: "device_Oi_name",
        hint2: "device_Oi_Step02"
    },
    "32": {
        type: "3",
        img1: "../fonts/add_32_01.png",
        img2: "../fonts/add_32_02.png",
        title: "addDevice_32_title",
        name: "Device_32_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    // 添加物联地暖
    Ap: {
        type: "2",
        img1: "../fonts/add_Ap_01.png",
        img2: "../fonts/add_Ap_02.png",
        title: "device_Ap_title",
        name: "device_Ap_name",
        hint2: "addDevice_01_02_step"
    },
    // 添加5038地暖
    Bm: {
        type: "2",
        img1: "../fonts/add_Bm_01.png",
        img2: "../fonts/add_Bm_02.png",
        title: "device_Bm_title",
        name: "device_Bm_name",
        hint2: "addDevice_01_02_step_LGL"
    },
    // 添加三位场景开关
    "33": {
        type: "3",
        img1: "../fonts/add_33_01.png",
        img2: "../fonts/add_33_02.png",
        title: "addDevice_33_title",
        name: "Device_33_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    // 添加一位免零线智能调光开关
    Av: {
        type: "1",
        img1: "../fonts/add_Av_01.png",
        img2: "../fonts/add_Av_02.png",
        title: "addDevice_Av_title",
        name: "addDevice_Av_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    // 添加一位场景开关
    Bi: {
        type: "3",
        img1: "../fonts/add_Bi_01.png",
        img2: "../fonts/add_Bi_02.png",
        title: "addDevice_Bi_title",
        name: "Device_Bi_name",
        hint2: "addDevice_01_03_step_LGL"
    },
    // 添加移动插座
    "16": {
        type: "3",
        img1: "../../source/addDevicePic/add_77_01.png",
        img2: "../../source/addDevicePic/add_77_02.png",
        title: "addDevice_16_name",
        name: "addDevice_16_name",
        hint2: "addDevice_01_02_step"
    },
    // 添加机械手
    "25": {
        type: "3",
        img1: "../fonts/add_25_01.png",
        img2: "../fonts/add_25_02.png",
        title: "addDevice_25_name",
        name: "addDevice_25_name",
        hint2: "addDevice_01_02_step"
    },
    // 添加光强监测器
    "19": {
        type: "3",
        img1: "../fonts/add_19_01.png",
        img2: "../fonts/add_19_02.png",
        title: "device_19_name",
        name: "device_19_name",
        hint2: "addDevice_01_02_step"
    },
    // 添加PM2.5监测仪
    Og: {
        type: "3",
        img1: "../fonts/add_Og_01.png",
        img2: "../fonts/add_Og_02.png",
        title: "addDevice_Og_name",
        name: "addDevice_Og_name",
        hint2: "addDevice_step_2_1"
    },
    //红外转发器
    "22": {
        type: "1",
        img1: "../fonts/add_22_1_01.png",
        img2: "../fonts/add_22_1_02.png",
        title: "addDevice_22_name_1",
        name: "addDevice_22_name_1",
        hint2: "addDevice_01_02_step"
    },
    //led炫彩灯
    "90": {
        type: "1",
        img1: "../fonts/add_90_01.png",
        img2: "../fonts/add_90_01.png",
        title: "addDevice_90_title",
        name: "device_90_name",
        hint2: "addDevice_01_02_step"
    },
    //六键场景开关（电池）
    Az: {
        type: "3",
        img1: "../fonts/add_Az_01.png",
        img2: "../fonts/add_Az_02.png",
        title: "addDevice_Az_title",
        name: "Device_Az_name",
        hint2: "addDevice_01_02_step"
    },
    //六键场景开关（电源）
    Bb: {
        type: "2",
        img1: "../fonts/add_Bb_01.png",
        img2: "../fonts/add_Bb_02.png",
        title: "addDevice_Bb_title",
        name: "Device_Bb_name",
        hint2: "addDevice_01_02_step"
    },
    //1/2模组（场景）
    Ay: {
        type: "2",
        img1: "../fonts/add_Ay_01.png",
        img2: "../fonts/add_Ay_02.png",
        title: "addDevice_Ay_title",
        name: "Device_Ay_name",
        hint2: "addDevice_01_02_step"
    },
    //两位插座
    Ba: {
        type: "3",
        img1: "../fonts/add_Ba_01.png",
        img2: "../fonts/add_Ba_02.png",
        title: "Device_Ba_name",
        name: "Device_Ba_name",
        hint2: "addDevice_01_02_step"
    },
    //1/2开关模组（单零火通用）
    Aw: {
        type: "1",
        img1: "../fonts/add_Aw_01.png",
        img2: "../fonts/add_Aw_02.png",
        title: "addDevice_Aw_title",
        name: "Device_Aw_name",
        hint2: "addDevice_01_02_step"
    },
    //六键开关模组
    Ax: {
        type: "1",
        img1: "../fonts/add_Ax_01.png",
        img2: "../fonts/add_Ax_02.png",
        title: "addDevice_Ax_title",
        name: "Device_Ax_name",
        hint2: "addDevice_01_02_step"
    },
    //1/2模组-调光
    Be: {
        type: "3",
        img1: "../fonts/add_Be_01.png",
        img2: "../fonts/add_Be_02.png",
        title: "Device_Be_name",
        name: "Device_Be_name",
        hint2: "addDevice_01_02_step"
    },
    //万能红外转发器
    "24": {
        type: "3",
        img1: "../fonts/add_24_01.png",
        img2: "../fonts/add_22_2_02.png",
        title: "addDevice_24_title",
        name: "Device_24_name",
        hint2: "addDevice_01_02_step"
    },
    //镜面触屏玻璃门锁
    "69": {
        type: "3",
        img1: "../fonts/add_69_01.png",
        img2: "../fonts/add_69_02.png",
        title: "addDevice_69_title",
        name: "Device_69_name",
        hint1: "addDevice_step_01_05",
        hint2: "addDevice_step_02_05"
    },
    //血压计
    "48": {
        type: "3",
        img1: "../fonts/add_48_01.png",
        img2: "../fonts/add_48_02.png",
        title: "addDevice_48_title",
        name: "Device_48_name",
        hint2: "addDevice_01_02_step"
    },
    //体重秤
    "45": {
        type: "3",
        img1: "../fonts/add-scale-first.png",
        img2: "../fonts/add-scale-second.png",
        title: "Device_45_name",
        name: "Device_45_name",
        hint2: "addDevice_01_02_step"
    },
    //智能水阀
    "28": {
        type: "3",
        img1: "../fonts/add_28_01.png",
        img2: "../fonts/add_28_02.png",
        title: "addDevice_28_title",
        name: "device_28_name",
        hint2: "device_28_Join_network_02"
    },
    //幕帘探测器
    a1: {
        type: "1",
        img1: "../fonts/add_a1_01.png",
        img2: "../fonts/add_a1_02.png",
        title: "addDevice_a1_title",
        name: "Device_a1_name",
        hint2: "addDevice_01_02_step"
    },
    //幕帘探测器
    "44": {
        type: "1",
        img1: "../fonts/add_44_01.png",
        img2: "../fonts/add_44_02.png",
        title: "addDevice_44_title",
        name: "Device_44_name",
        hint2: "addDevice_01_02_step"
    },
    //多功能红外人体探测器（电池版）
    Ad: {
        type: "3",
        img1: "../fonts/add_Ad_01.png",
        img2: "../fonts/add_Ad_02.png",
        title: "Device_Ad_name",
        name: "Device_Ad_name",
        hint2: "addDevice_01_02_step"
    },
    //多功能红外人体探测器（强电版）
    C0: {
        type: "3",
        img1: "../fonts/add_Ad_01.png",
        img2: "../fonts/add_Ad_02.png",
        title: "Device_C0_name",
        name: "Device_C0_name",
        hint2: "addDevice_01_02_step"
    },
    //海信分体空调
    Ok: {
        type: "3",
        img1: "../fonts/add_Ok_01.png",
        img2: "../fonts/add_Ok_01.png",
        title: "device_Ok_title",
        name: "device_Ok_name",
        hint2: "addDevice_01_02_step"
    },
    //二路输出型翻译器
    A2: {
        type: "3",
        img1: "../fonts/add_A2_01.png",
        img2: "../fonts/add_A2_02.png",
        title: "addDevice_A2_title",
        name: "device_A2_name",
        hint2: "addDevice_A2_07"
    },
    //二路输出型翻译器02型
    Bo: {
        type: "3",
        img1: "../fonts/add_Bo_01.png",
        img2: "../fonts/add_Bo_02.png",
        title: "addDevice_Bo_title",
        name: "device_Bo_name",
        hint2: "addDevice_01_02_step"
    },
    //罗格朗VRV空调
    Bp: {
        type: "3",
        img1: "../fonts/add_Bp_01.png",
        img2: "../fonts/add_Bp_02.png",
        title: "addDevice_Bp_title",
        name: "device_Bp_name",
        hint2: "Bp_Prompt"
    },
    //空调
    Af: {
        type: "1",
        img1: "../fonts/add_Af_01.png",
        img2: "../fonts/add_Af_02.png",
        title: "addDevice_Af_title",
        name: "device_Af_name",
        hint2: "addDevice_01_02_step"
    },
    //大金中央空调翻译器
    a0: {
        type: "2",
        img1: "../../source/addDevicePic/add_a0_01.png",
        img2: "../../source/addDevicePic/add_a0_02.png",
        title: "addDevice_a0_title",
        name: "device_a0_name",
        hint2: "addDevice_01_02_step"
    },
    //联排2路开关
    Bv: {
        type: "2",
        img1: "../fonts/add_Bv_01.png",
        img2: "../fonts/add_Bv_02.png",
        title: "addDevice_Bv_title",
        name: "device_Bv_name",
        hint2: "addDevice_Bv_way"
    },
    //联排地暖控制器
    Bx: {
        type: "2",
        img1: "../../source/addDevicePic/add_Bx_01.png",
        img2: "../../source/addDevicePic/add_Bx_02.png",
        title: "device_Bx_title",
        name: "device_Bx_name",
        hint2: "addDevice_01_02_step_LGL"
    },
    //智敬空调控制器
    By: {
        type: "2",
        img1: "../../source/addDevicePic/add_By_01.png",
        img2: "../../source/addDevicePic/add_By_02.png",
        title: "device_By_title",
        name: "device_By_name",
        hint2: "addDevice_01_02_step_LGL"
    },
    //小型四键遥控器
    "38": {
        type: "3",
        img1: "../../source/addDevicePic/add_38_01.png",
        img2: "../../source/addDevicePic/add_38_02.png",
        title: "addDevice_38_title",
        name: "device_38_name",
        hint2: "addDevice_38_Screening"
    },
    //智敬系列调光开关
    "Cc": {
        type: "2",
        img1: "../../source/addDevicePic/add_Cc_01.png",
        img2: "../../source/addDevicePic/add_Cc_02.png",
        title: "addDevice_Cc_title",
        name: "device_Cc_name",
        hint2: "addDevice_Cc_step_1"
    },
    "Ca": {
        type: "1",
        img1: "../../source/addDevicePic/add_Ca_01.png",
        img2: "../../source/addDevicePic/add_Ca_02.png",
        title: "addDevice_Ca_title",
        name: "device_Ca_name",
        hint2: "addDevice_Ca_step_1"
    },
    "Cb": {
        type: "1",
        img1: "../../source/addDevicePic/add_Cb_01.png",
        img2: "../../source/addDevicePic/add_Cb_02.png",
        title: "addDevice_Cb_title",
        name: "device_Cb_name",
        hint2: "addDevice_Ca_step_1"
    },
    "Op": {
        type: "2",
        img1: "../../source/addDevicePic/add_Op_01.png",
        img2: "../../source/addDevicePic/add_Op_02.png",
        title: "addDevice_Op_title",
        name: "device_Op_name",
        hint2: "addDevice_Op_Screening"
    },
    //联排地暖控制器
    Cj: {
        type: "2",
        img1: "../../source/addDevicePic/add_Cj_01.png",
        img2: "../../source/addDevicePic/add_Cj_02.png",
        title: "device_Cj_title",
        name: "device_Cj_name",
        hint2: "device_Oi_Step02"
    },
    //一路智能开关
    "Cl": {
        type: "1",
        img1: "../../source/addDevicePic/add_Cl_01.png",
        img2: "../../source/addDevicePic/add_Cl_02.png",
        title: "addDevice_Cl_title",
        name: "device_Cl_name",
        hint2: "addDevice_Bu_way"
    },
    //二路智能开关
    "Cm": {
        type: "1",
        img1: "../../source/addDevicePic/add_Cm_01.png",
        img2: "../../source/addDevicePic/add_Cm_02.png",
        title: "addDevice_Cm_title",
        name: "device_Cm_name",
        hint2: "addDevice_Bu_way"
    },
    //三路智能开关
    "Cn": {
        type: "1",
        img1: "../../source/addDevicePic/add_Cn_01.png",
        img2: "../../source/addDevicePic/add_Cn_02.png",
        title: "addDevice_Cn_title",
        name: "device_Cn_name",
        hint2: "addDevice_Bu_way"
    },
    //智敬新风控制器
    "Ck": {
        type: "1",
        img1: "../fonts/add_Ck_01.png",
        img2: "../fonts/add_Ck_02.png",
        title: "addDevice_Ck_title",
        name: "device_Ck_name",
        hint2: "addDevice_Ck_Join_network"
    },
    //智敬四键场景开关
    "Cp": {
        type: "3",
        img1: "../../source/addDevicePic/add_Cp_01.png",
        img2: "../../source/addDevicePic/add_Cp_02.png",
        title: "addDevice_Cp_title",
        name: "device_Cp_name",
        hint2: "addDevice_Ca_step_1"
    },
};
