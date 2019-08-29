var info = window.sysfun;
info.init("device_OZ");
var devID = '';
var gwID = '';
var ep = parseInt(getUrlParam("endpointNumber"));
var name = info.getItem("name");
$(".deviceName").html(name);
var switchAttributes = [];
//
var deviceState = "";//开关状态
var deviceMode = "";
var deviceFan = "";
var deviceFX = "";
var deviceInDoorTemperature = "";//室内温度
var deviceSetTemperature = "";//设置温度

///////////
var deviceHeatMaxTemperature;
var deviceHeatMinTemperature;
var deviceCoolMaxTemperature;
var deviceCoolMinTemperature;
var deviceSuooortedModes = [];//支持的模式
var deviceSuooortedFans = [];//支持的风速
var deviceSuooortedFX;//支持的风向
var deviceSuooortedFXs = [];//支持的风向档位
var deviceSuooortedVentilationModes = [];//支持的换气模式
var s_Oi_temperatureUnit;
var s_Oi_EmergencyHeatData;
///////////
var fakeLoadertime;

var isNeedHandleTemp = false;
var devicesList = {};

function showLoading() {
    $(".saveToast").show();
    fakeLoadertime = setTimeout(function () {
        hideLoading();
    },10000)
}

function hideLoading() {
    window.clearInterval(fakeLoadertime);
    $(".saveToast").hide();
}
//
//国际化初始化函数
initlan()
plus.plusReady(function(){
    window.modeList = {
        "01":lang_Details_Type1,
        "02":lang_Details_Type2,
        "03":lang_Details_Type3,
        "04":lang_Details_Type5,
        "05":lang_Details_Type4,
        "06":lang_Details_Type6,
        "07":lang_Details_Type7,
        "08":lang_Details_Type8,
        "09":lang_Details_Type9,
        "10":lang_Details_Type10,
        "11":lang_Details_Type11,
        "12":lang_Details_Type12
    };

    $(".contrl_mode").on("click",function () {
        //切换mode
        setThermostatMode();
    })

    $(".contrl_fan").on("click",function () {
        //切换fan
        setThermostatFan();
    })

    $(".contrl_fx").on("click",function () {
        //切换fx
        setThermostatFX();
    })

    $(".contrl_button").on("click",function () {
        //开关
        setThermostatState();
    })

    plus.gatewayCmd.getDeviceInfo(null, function(data){
        if(data.mode == 2){
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        devID = data.devID;
        gwID = data.gwID;
        // ep = ~~(data.endpoints[0].endpointNumber)
        requestThermostatPlanes();
        getThermostatAllInfo();
        handleData(data);

    })

    plus.gatewayCmdRush.newDataRefresh(function(data){
        console.log(data);
        if(data.cmd == "500" && devID == data.devID){
            hideLoading();
            if(data.mode == 2){
                $(".mask_layer").show();
            }else{
                $(".mask_layer").hide();
            }
            handleData(data);
        }else if(data.cmd == "502" && devID == data.devID){
            var endpointName = data.endpointName;
            var endpointNumber = data.endpointNumber;
            if(!endpointName){
                endpointName = endpointNumber;
            }
            $(".deviceName").html(endpointName);
        }
    })

    //控制室内机的运转停止
    function setThermostatState() {
        if (deviceState == "0"){
            sendCmd(gwID,devID,ep,0x0101,["1"]);
        } else if (deviceState == "1"){
            sendCmd(gwID,devID,ep,0x0101,["0"]);
        }else {
            //开关异常
        }
    }

    //设置运转模式
    function setThermostatMode() {
        var currentModeNum = parseInt(deviceMode);
        if (currentModeNum >= 0){
            var modeIndex = $.inArray(currentModeNum.toString(), deviceSuooortedModes);
            if(modeIndex >= 0){
                modeIndex = modeIndex+rnd(1,deviceSuooortedModes.length-1);
                var sendIndex = (modeIndex)%(deviceSuooortedModes.length);
                var sendStr = "0" + deviceSuooortedModes[sendIndex];
                sendCmd(gwID,devID,ep,0x0102,[sendStr]);
            }
        }
    }

    function rnd(n, m){
        var random = Math.floor(Math.random()*(m-n+1)+n);
        return random;
    }

    //设定风向
    function setThermostatFX() {
        if(deviceFX){
            var currentFXNum = parseInt(deviceFX);
            if(currentFXNum >= 0){
                if(deviceSuooortedFXs.length == 0){
                    window.showDialog.show(lang_Details_Nowind,3000);
                }else{
                    var FXIndex = $.inArray(currentFXNum, deviceSuooortedFXs);
                    if(FXIndex >= 0 && deviceSuooortedFX){
                        var sendIndex = (FXIndex+1)%(deviceSuooortedFXs.length);
                        var sendFXStr = "02" +deviceSuooortedFX+"0"+deviceSuooortedFXs[sendIndex];
                        sendCmd(gwID,devID,ep,0x0105,[sendFXStr]);
                    }
                }
            }
        }
    }

    //设定风量
    function setThermostatFan() {
        if(deviceFan){
            var currentFanNum = parseInt(deviceFan);
            if(currentFanNum >= 0){
                if(deviceSuooortedFans.length == 0){
                    window.showDialog.show(lang_Details_Nospeed,3000);
                }else{
                    var FanIndex = $.inArray(currentFanNum, deviceSuooortedFans);
                    if(FanIndex >= 0){
                        var sendIndex = (FanIndex+1)%(deviceSuooortedFans.length);
                        var sendFanStr = "0" + deviceSuooortedFans[sendIndex];
                        sendCmd(gwID,devID,ep,0x0106,[sendFanStr]);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////
    //获取各种状态的指令

    //获取室内机的运转停止状态
    function requestThermostatState() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0201);
        } else{

        }
    }

    //获取运转模式
    function requestThermostatMode() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0202);
        } else{

        }
    }

    //获取设定的温度
    function requestThermostatTemperature() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0203);
        } else{

        }
    }

    //获取设定的风向
    function requestThermostatFX() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0205);
        } else{

        }
    }

    //获取设定的风量
    function requestThermostatFan() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0206);
        } else{

        }
    }

    //获取所有信息
    function getThermostatAllInfo() {
        sendCmd(gwID,devID,ep,0x0200);
    }

    //获取已连接的室内机的厂家信息（默认第一个编号为"01"室内机）
    function requestThermostatFirstPlane() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0303);
        } else{

        }
    }

    //获取已连接的室内机的性能信息
    function requestThermostatPlanes() {
        if (devID){
            var ep1 = (ep+'').length == 0 ? "0"+ep : ''+ep;
            sendCmd(gwID,devID,ep,0x0304,[ep1]);
        } else{

        }
    }
    /////////////////////////////////////////////////////////////

    function handleData(data){
        // drawDeviceList(data);
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            if(endpoint.endpointNumber == ep){
                var clusters = endpoint.clusters;
                for (var k = 0; k < clusters.length; k++) {
                    var attributes = clusters[k].attributes;
                    for (var j = 0;j < attributes.length;j++){
                        var attributeId = attributes[j].attributeId;
                        var attributeValue = attributes[j].attributeValue;
                        switchAttributes[attributeId] = attributeValue;
                        refreshUI(attributes[j]);
                    }
                }
            }
        }
    }
    function refreshUI(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId){
            case 0x8101:{
                /*
                0室内机停止
                1室内机运转*/
                if(attributeValue == 0){
                    $(".contrl_button").children('i').css("background","url(img/icon_closebtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    deviceState = attributeValue;
                } else if (attributeValue == 1){
                    $(".contrl_button").children('i').css("background","url(img/icon_openbtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    deviceState = attributeValue;
                }

            }
                break;
            case 0x8102:{
                /*运转模式数据上报
                01：制冷 lang_Details_Type1
                02：制热 lang_Details_Type2
                03：送风 lang_Details_Type3
                04：预热 lang_Details_Type5
                05：除湿 lang_Details_Type4
                06：自动 lang_Details_Type6
                07：干燥 lang_Details_Type7
                08：清爽 lang_Details_Type8
                09：睡眠 lang_Details_Type9
                10: 杀菌 lang_Details_Type10
                11: 干爽除湿 lang_Details_Type11
                12：强力除湿 lang_Details_Type12
                */
                if (attributeValue == 01){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type1);
                } else if (attributeValue == 02){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type2);
                } else if (attributeValue == 03){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type3);
                } else if (attributeValue == 04){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type5);
                } else if (attributeValue == 05){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type4);
                } else if (attributeValue == 06){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type4);
                } else if (attributeValue == 07){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type7);
                } else if (attributeValue == 08){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type8);
                } else if (attributeValue == 09){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type9);
                } else if (attributeValue == 10){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type10);
                } else if (attributeValue == 11){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type11);
                } else if (attributeValue == 12){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type12);
                }
                deviceMode = attributeValue;
            }
                break;
            case 0x8103:{
                /*设置的温度值*/
                if (attributeValue){
                    $(".num_show").find("div").find("p").html(attributeValue);
                    deviceSetTemperature = attributeValue;
                    setShowNum(parseInt(deviceSetTemperature));
                }
            }
                break;
            case 0x8104:{
                /*设置的的湿度值*/
                if (attributeValue){
                    /*交互暂无适度*/
                }
            }
                break;
            case 0x0209:{
                /*实际的室内湿度*/
            }
                break;
            case 0x020A:{
                /*实际的温度值*/
                if (attributeValue){
                    $(".contrl_temp").find('p').html(attributeValue);
                    deviceInDoorTemperature = attributeValue;
                }
            }
                break;
            case 0x8105:{
                /*xxyy
                    xx 为设置的风向值：(00:上下，01：左右)，
                    yy 为叶片方向（01-06），摆动：07，自动：00，停止：09*/
                if (attributeValue){
                    deviceFX = attributeValue.substring(2,4);
                    var showFX;
                    if (deviceFX=="00"){
                        showFX = lang_Details_Speed4;
                    }else if (deviceFX=="07"){
                        showFX = "07";
                    }else if (deviceFX=="09"){
                        showFX = "09";
                    }else{
                        showFX = deviceFX
                    }
                    $(".smartsection_fx").html(lang_Details_Direction +"："+ showFX);
                }

            }
                break;
            case 0x8106:{
                /*设置的风量值：自动：0 其他（1-6）*/
                if (attributeValue){
                    $(".smartsection_fan").html(lang_Details_Speed +"："+((attributeValue=="00")?lang_Details_Speed4:attributeValue));
                    deviceFan = attributeValue;
                }
            }
                break;
            case 0x8107:{
                /*换气模式：自动：0，全热：1，普通：2*/
            }
                break;
            case 0x8108:{
                /*辅热状态：关闭：0，开启：1*/
            }
                break;
            case 0x8301:{
                /*转换器状态：未就绪：0， 已就绪：1*/
                attributeValue == 0 ? false : true;
                if(attributeValue){
                    $(".mask_loading").hide();
                }else{
                    $(".mask_loading").show();
                    sendCmd(gwID,devID,0,0x0301);
                }
            }
                break;
            case 0x8302:{
                /*室内机状态：未连接：0.， 已连接：1*/
            }
                break;
            case 0x8303:{
                /*AABB……BB
                    室内机厂家信息：
                    品牌信息代号AA：（大金：01，日历：02，美的：03，格力：04，海信：05，东芝：06， 三菱电机：07，三菱重工：08                      备注：默认为 01）
                    型号信息：BB.....BB*/
                if (attributeValue){
                    var deviceBrand = parseInt(attributeValue.substring(0,2));
                    if (deviceBrand > 8){
                        //暂不支持
                        alert("暂不支持的品牌"+attributeValue);
                    }
                }
            }
                break;
            case 0x8304:{
                /*异常信息返回：
                    1：转换器通信异常
                    2：室内机通信异常
                    3：指令非法，该指令不支持
                    4：从机地址错误、室内机编号等数据错误
                    5：校验错误*/
                if (attributeValue){
                    if (attributeValue == '1'){
                        // alert("转换器通信异常");
                    } else if (attributeValue == '2'){
                        // alert("室内机通信异常");
                    } else if (attributeValue == '3'){
                        // alert("指令非法，该指令不支持");
                    } else if (attributeValue == '4'){
                        // alert("从机地址错误、室内机编号等数据错误");
                    } else if (attributeValue == '5'){
                        // alert("校验错误");
                    }
                }
            }
                break;
            case 0x8401:{
                /*室内机性能信息——模式: 0:无   1：有   （）括号内的数字表示第几位
（1） 制冷模式  （2） 制热模式  （3） 送风模式  （4） 预热模式  （5） 除湿模式
（6） 自动模式  （7） 干燥模式  （8） 清爽模式  （9） 睡眠模式  （10） 杀菌模式
（11） 干爽除湿模式   （12）强力除湿模式*/

                if (attributeValue.length == 12){
                    deviceSuooortedModes.length = 0;
                    for (var i = 0;i < attributeValue.length;i++){
                        var tempStr = attributeValue.substring(i,i+1);
                        if (tempStr == "1"){
                            deviceSuooortedModes.push((i+1).toString());
                        }
                    }
                }
            }
                break;
            case 0x8402:{
                /*室内机的制热模式设定温度下限*/
                if (attributeValue){
                    deviceHeatMinTemperature = parseInt(attributeValue);
                    MinTemperature = deviceHeatMinTemperature;
                    if (!(deviceMode == "02")){
                        updatePickerArry();
                    }
                }
            }
                break;
            case 0x8403:{
                /*室内机的制热模式设定温度上限*/
                if (attributeValue){
                    deviceHeatMaxTemperature = parseInt(attributeValue);
                    MaxTemperature = deviceHeatMaxTemperature;
                    if (!(deviceMode == "02")){
                        updatePickerArry();
                    }
                }
            }
                break;
            case 0x8404:{
                /*制冷模式温度上限*/
                if (attributeValue){
                    deviceCoolMaxTemperature = parseInt(attributeValue);
                    MaxTemperature = deviceCoolMaxTemperature;
                    if (!(deviceMode == "01")){
                        updatePickerArry();
                    }
                }
            }
                break;
            case 0x8405:{
                /*制冷模式温度下限*/
                if (attributeValue){
                    deviceCoolMinTemperature = parseInt(attributeValue);
                    MinTemperature = deviceCoolMinTemperature;
                    if (!(deviceMode == "01")){
                        updatePickerArry();
                    }
                }
            }
                break;
            case 0x8406:{
                /*室内机性能信息：0：无 1：有(1) 支持换气  (2) 自动换气  (3) 全热换气  (4) 普通换气*/
                if (attributeValue){

                    deviceSuooortedVentilationModes.length = 0;
                    for (var i = 0;i < deviceSuooortedVentilationModes.length;i++){
                        var tempStr = deviceSuooortedVentilationModes.substring(i,i+1);
                        if (tempStr == "1"){
                            deviceSuooortedModes.push((i+1).toString());
                        }
                    }
                }
            }
                break;
            case 0x8407:{
                /*室内机性能信息——特殊 1：(1) 湿度设定0：无           1：有
(2) 温度单位0：摄氏度     1：华氏度
(3) 角色0：主机         1：从机
(4) 防冻模式0：无           1：有
(5) 辅热模式0：无            1：有*/
                if (attributeValue){
                    s_Oi_temperatureUnit = attributeValue.substring(0,1);
                    s_Oi_EmergencyHeatData = attributeValue.substring(3,4);
                }
            }
                break;
            case 0x8408:{
                /*室内性能信息-风向风量
                 a：风向自动    0：无      1：有
                 b：风量自动    0：无      1：有
                 c：风向调节（上下） 0：无      1：有
                 d：风向叶片位置数设定（上下）（数值范围：0-6）(当 c 的值为 0 时，此值无意义)
                 e：风向调节（左右） 0：无      1：有
                 f：风向叶片位置数设定（左右）（数值范围：0-6）(当 e的值为 0 时，此值无意义)
                 g：风量调节   0：无      1：有
                 h：风量/换气量的风速设定  （数值范围：0-6）（当 g 的值为 0 时，此值无意义）
                 i：风向摆动   0：无      1：有
                 */
                if (attributeValue){
                    //000000150
                    deviceSuooortedFans.length = 0;
                    deviceSuooortedFXs.length = 0;
                    if(attributeValue.substring(0,1)=="1"){
                        //风向自动
                        deviceSuooortedFXs.push("0");
                    }
                    if(attributeValue.substring(1,2)=="1"){
                        deviceSuooortedFans.push("0");
                    }
                    if(attributeValue.substring(2,3)=="1"){
                        //上下风向
                        deviceSuooortedFX = "0"
                        var suooortedFXsB = parseInt(attributeValue.substring(3,4));
                        for(var i=0;i<suooortedFXsB;i++){
                            deviceSuooortedFXs.push(i+1);
                        }
                    } else if(attributeValue.substring(4,5)=="1"){
                        //左右风向
                        deviceSuooortedFX = "1"
                        var suooortedFXsB = parseInt(attributeValue.substring(5,6));
                        for(var i=0;i<suooortedFXsB;i++){
                            deviceSuooortedFXs.push(i+1);
                        }
                    }else{
                        deviceSuooortedFXs.length = 0;
                    }
                    if(attributeValue.substring(6,7)=="1"){
                        var deviceSuooortedFanA = parseInt(attributeValue.substring(7,8));
                        for(var i=0;i<deviceSuooortedFanA;i++){
                            deviceSuooortedFans.push(i+1);
                        }
                    }else{
                        deviceSuooortedFans.length = 0;
                    }
                    if(attributeValue.substring(8,9)=="1"){
                        deviceSuooortedFXs.push("7");
                    }
                }
            }
                break;
        }
    }


})

function sendCmd(gwID,dID,endpointNumber,commandId,parameter){
    var jsonData = {}
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = dID;
    jsonData.endpointNumber = endpointNumber;
    jsonData.clusterId = 0x0201;
    jsonData.commandType = 1;
    jsonData.commandId = commandId;
    if(parameter){
        jsonData.parameter = parameter;
    }
    showLoading();
    plus.gatewayCmd.controlDevice(jsonData,function(){})
}

//设置温度
function setThermostatTemperature(TempStr) {
    if (deviceMode != "03" && deviceMode != "10" && deviceMode != ""){
        TempStr = TempStr + '';
        var sendTempStr = TempStr;
        sendCmd(gwID,devID,ep,0x0103,[sendTempStr]);
    } else{

    }
}