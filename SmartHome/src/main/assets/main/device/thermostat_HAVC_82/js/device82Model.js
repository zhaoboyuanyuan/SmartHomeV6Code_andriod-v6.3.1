/**
 *  0=off 1=on
 */
var s_82_onOff;

/**
 *  1=heat 2=cool 3=auto
 */
var s_82_mode;

/**
 *  1=automatic 2=always on
 */
var s_82_fan;

/**
 *  00 摄氏 01 华氏
 */
var s_82_temperatureUnit;

/**
 *  0=正 1=负(暂时弃用，现正负使用16进制补码方式)
 */
var s_82_temperatureSymbol;
/**
 *  系统类型（如 一级制冷）
 */
var s_82_systemType;
/**
 *  设备供热模式(如电热、水泵)01=[Conventional,Single Fuel];02=[Conventional,Dual Fuel];03=[Heat Pump,Single Fuel];04=[Heat Pump,Dual Fuel];//[@"Conventional",@"Heat Pump"];[@"Single Fuel",@"Dual Fuel"];
 */
var s_82_temperatureModeType;

/**
 *  制热温度按照10进制(扩大一百倍)
 */
var s_82_heatTemperature;

/**
 *  制冷温度按照10进制(扩大一百倍)
 */
var s_82_coolTemperature;
/**
 *  当前温度 16进制－》10进制－》除10
 */
var  s_82_currentTemperature;

/**
 *  当前环境湿度
 */
var  s_82_currentHumidity;

/**
 *  自动制热温度按照10进制(扩大一百倍)
 */
var s_82_autoHeatTemperature;

/**
 *  自动制冷温度按照10进制(扩大一百倍)
 */
var s_82_autoCoolTemperature;

/**
 *  设置锁定🔒
 */
var  s_82_lock;


//温度校正数据（废弃）
var  s_82_tempcorrectionData;
//swing设置数据
var  s_82_svingData;
//diff设置类型
var  s_82_diffData;
//third设置类型
var  s_82_thirdData;
//声音数据00:NO 01:YES
var  s_82_soundData;
//紧急制热数据00:NO 01:YES
var  s_82_EmergencyHeatData;
//时间数据(暂不需解析)
var  s_82_timeData;
//震动数据00:NO 01:YES
var  s_82_vibrate;

function updataModelWith(dataStr) {
    if (dataStr.length == 36){
        s_82_onOff = dataStr.substring(0,2);//xx
        s_82_fan = dataStr.substring(2,4);//yy
        s_82_temperatureUnit = dataStr.substring(4,6);//mm
        s_82_temperatureModeType = dataStr.substring(6,8);//zz
        s_82_systemType = dataStr.substring(8, 10);//nn

        s_82_mode = dataStr.substring(10, 12);
        s_82_heatTemperature = parseInt(dataStr.substring(12, 16),16);//bb
        s_82_coolTemperature = parseInt(dataStr.substring(16, 20),16);//cc
        s_82_currentTemperature = parseInt(dataStr.substring(20, 24),16);//qqqq
        s_82_currentHumidity = dataStr.substring(24, 2);
        s_82_autoHeatTemperature = parseInt(dataStr.substring(26, 30),16);
        s_82_autoCoolTemperature = parseInt(dataStr.substring(30, 34),16);
        s_82_lock = dataStr.substring(34, 36);
    } else if (dataStr.length == 26){
        s_82_tempcorrectionData = dataStr.substring(0, 2);
        s_82_svingData = dataStr.substring(2, 4);
        s_82_diffData = dataStr.substring(4, 6);
        s_82_thirdData = dataStr.substring(6, 8);
        s_82_soundData = dataStr.substring(8, 10);
        s_82_EmergencyHeatData = dataStr.substring(10, 12);

        s_82_vibrate = dataStr.substring(24,26);
    } else {
        //
    }
}