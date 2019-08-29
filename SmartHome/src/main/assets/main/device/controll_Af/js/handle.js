var info = window.sysfun;
info.init("device_Af");
var devID = '';
var gwID = '';
var ep = 1;//parseInt(info.getItem("endpointNumber"));
var name = info.getItem("name");
$(".deviceName").html(name);
var switchAttributes = [];
//
var deviceState = "";//开关状态 0：关闭
var deviceMode = "";
var deviceFan = "";
var deviceUnit = "";//温标 0：摄氏度°C 1：华氏度F
var deviceInDoorTemperature = "";//室内温度
var deviceSetTemperature = 16;//设置温度

var deviceCountDownState = "";//倒计时状态 0：关闭定时 1：定时开机 2：定时关机

//wulian温控器
var deviceSetHeatTemperature = 18;//设置的制热温度
//wulian温控器
var deviceSetCoolTemperature = 26;//设置制冷温度

///////////
var deviceHeatMaxTemperature = 32;
var deviceHeatMinTemperature = 10;
var deviceCoolMaxTemperature = 32;
var deviceCoolMinTemperature = 10;
var deviceSuooortedModes = ['1','2','3'];//支持的模式 1 :制热  2 : 制冷 3 : 通风
var deviceSuooortedFans = ['1','2','3','4'];//支持的风速 0 : 关闭 1 : 低风 2 : 中风 3 : 高风 4 : 自动
///////////
var fakeLoadertime;
var timer;

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

var hoursArry = [];
for (var i = 0;i < 24;i++){
    var dic = {};
    dic.text = i+languageUtil.getResource("Hour");
    dic.value = i;
    hoursArry.push(dic)
}
var minusArry = [];
for (var j = 0;j < 60;j++){
    var dic = {};
    dic.text = j+languageUtil.getResource("Minute");
    dic.value = j;
    minusArry.push(dic)
}
var choosePicker;

document.getElementById("cancel").addEventListener("click",function () {
    //点击取消倒计时按钮，弹框确认
    $("#cancelCountDown").show()
})

function btn_cancel() {
    $("#cancelCountDown").hide()
}

//
function cancelCountDown() {
    $("#cancelCountDown").hide()
    $(".theLast").hide();
}

function startCountDown(timevalue) {
    if ((deviceCountDownState == '1' && deviceState == '0') || (deviceCountDownState == '2' && deviceState == '1')){
        countTime(timevalue);
        $(".theLast").show();
    } else {
        stopCountDown();
    }

}

function stopCountDown() {
    window.clearInterval(timer);
    $(".theLast").hide();
}
//
//国际化初始化函数
initlan()
plus.plusReady(function(){
    window.modeList = {
        "1":lang_Details_Type1,
        "2":lang_Details_Type2,
        "3":lang_Details_Type3,
    };

    $("#back").on("click",function(){
        plus.tools.back(function() {})
    });
    //跳转到更多页面
    $(".more").on("click",function(){
        plus.tools.more(moreConfig, function() {})
    });

    $(".contrl_mode").on("click",function () {
        //切换mode
        setThermostatMode();
    })

    $(".contrl_fan").on("click",function () {
        //切换fan
        setThermostatFan();
    })

    $(".contrl_ds").on("click",function () {
        //弹出倒计时选择框
        hoursArry.length = 0;
        for (var i = 0;i < 24;i++){
            var dic = {};
            dic.text = i+languageUtil.getResource("Hour");
            dic.value = i;
            hoursArry.push(dic)
        }
        minusArry.length = 0;
        for (var j = 0;j < 2;j++){
            var dic = {};
            dic.text = j*30+languageUtil.getResource("Minute");
            dic.value = j;
            minusArry.push(dic)
        }

        if (deviceState == 0){
            choosePicker = new Picker({
                title: lang_device_Ol_Countdown_Open,
                data: [hoursArry,minusArry]
            });
        } else if (deviceState == 1){
            choosePicker = new Picker({
                title: lang_device_Ol_Countdown_Off,
                data: [hoursArry,minusArry]
            });
        }
        choosePicker.show();
        $(".picker").addClass("pickerIndex0");
        document.getElementsByClassName("cancel-hook")[0].innerHTML = lang_device_Ol_Cancel;
        document.getElementsByClassName("confirm-hook")[0].innerHTML = lang_device_Ol_ok;
        // $(".cancel-hook")[0].innerText = lang_device_Ol_Cancel;
        // $(".confirm-hook")[0].innerText = lang_device_Ol_ok;
        choosePicker.on('picker.select', function (selectedVal, selectedIndex) {
            //确定
            if(selectedVal[0] != 0 || selectedVal[1] != 0){
                if (deviceState == 0){
                    //打开倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];//每30min
                    var sendSec =  selectedHour*2 + selectedMin*1;
                    requestDelayOpen(sendSec.toString());
                } else if (deviceState == 1){
                    //关闭倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    var sendSec =  selectedHour*2 + selectedMin*1;
                    requestDelayClose(sendSec.toString());
                }
            }else{

            }
        });
    })

    $(".contrl_button").on("click",function () {
        //开关
        setThermostatState();
    })

    $(".contrl_energy").on("click",function () {
        //节能
        if (deviceState == '1'){
            if (deviceMode == '1'){
                //制热->制热节能
                sendCmd(gwID,devID,ep,0x0102,['4']);
            } else if (deviceMode == '2'){
                //制冷->制冷节能
                sendCmd(gwID,devID,ep,0x0102,['5']);
            } else if (deviceMode == '4'){
                //制热节能->制热
                sendCmd(gwID,devID,ep,0x0102,['1']);
            } else if (deviceMode == '5'){
                //制冷节能->制冷
                sendCmd(gwID,devID,ep,0x0102,['2']);
            }
        }
    })

    $(".sure_click").on("click",function (){
        cancelCountDown();
        requestTurnOffDelay();
    })


    plus.gatewayCmd.getDeviceInfo(null, function(data){
        if(data.mode == 2){
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        devID = data.devID;
        gwID = data.gwID;

        //更多页的配置
        getMoreConfig(devID);

        requestThermostatState();
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
            if (data.endpointNumber == ep){
                var endpointName = data.endpointName;
                var endpointNumber = data.endpointNumber;
                if(!endpointName){
                    endpointName = endpointNumber;
                }
                $(".deviceName").html(endpointName);
            }
            handleData(data);
        }else if(data.cmd == "502" && devID == data.devID){
            if (data.endpointNumber == ep){
                var endpointName = data.endpointName;
                var endpointNumber = data.endpointNumber;
                if(!endpointName){
                    endpointName = endpointNumber;
                }
                $(".deviceName").html(endpointName);
            }
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
        if (currentModeNum >= 0 && (deviceState == '1')){
            var modeIndex = $.inArray(deviceMode, deviceSuooortedModes);
            if(modeIndex >= 0){
                var sendIndex = (modeIndex + 1)%(deviceSuooortedModes.length);
                var sendStr = deviceSuooortedModes[sendIndex];
                sendCmd(gwID,devID,ep,0x0102,[sendStr]);
            }
        }
    }

    function rnd(n, m){
        var random = Math.floor(Math.random()*(m-n+1)+n);
        return random;
    }

    //设定风量
    function setThermostatFan() {
        if(deviceFan && (deviceState == '1')){
            var currentModeNum = parseInt(deviceMode);
            var currentFanNum = parseInt(deviceFan);
            if(currentFanNum >= 0 && (currentModeNum != 4) && (currentModeNum != 5)){
                var FanIndex = $.inArray(deviceFan, deviceSuooortedFans);
                if(FanIndex >= 0){
                    var sendIndex = (FanIndex+1)%(deviceSuooortedFans.length);
                    if (deviceMode == '3'){
                        //通风
                        sendIndex = (FanIndex+1)%(deviceSuooortedFans.length-1);
                    }
                    var sendFanStr = deviceSuooortedFans[sendIndex];
                    sendCmd(gwID,devID,ep,0x0103,[sendFanStr]);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////
    //获取各种状态的指令

    //获取室内机的状态（查询开关机状态、工作模式、设置温度、风机模式）
    function requestThermostatState() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0100,['1']);
        } else{

        }
    }

    //延时关闭
    function requestDelayClose(sec) {
        if (devID){
            if (sec.length == 1){
                sec = '0' + sec;
            }
            sendCmd(gwID,devID,ep,0x0201,['2'+sec]);
        } else{

        }
    }

    //延时打开
    function requestDelayOpen(sec) {
        if (devID){
            if (sec.length == 1){
                sec = '0' + sec;
            }
            sendCmd(gwID,devID,ep,0x0201,['1'+ sec]);
        } else{

        }
    }

    //取消延时
    function requestTurnOffDelay() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0201,['000']);
        } else{

        }
    }


    /////////////////////////////////////////////////////////////

    function handleData(data){
        // drawDeviceList(data);
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            if (endpoint.endpointType && endpoint.endpointType == 0x0301){
                if(endpoint.endpointNumber == ep){
                    var clusters = endpoint.clusters;
                    for (var k = 0; k < clusters.length; k++) {
                        var attributes = clusters[k].attributes;
                        if (attributes){
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
        }

        //extData
        if(data.extData){
            var mData = JSON.parse(new Base64().decode(data.extData));
            /*[{
                "delaySec" : "119",
                "endpointnumber" : 1
                }]
            */
            for (var m = 0; m < mData.length; m++){
                if(mData[m]){
                    var endpoints= mData[m];
                    var delaySec = endpoints.delaySec;
                    var endpointnumber = endpoints.endpointnumber;
                    if(endpointnumber == ep){
                        //获取当前子设备的时间
                        if (parseInt(delaySec) > 0){
                            //显示倒计时
                            startCountDown(delaySec);
                        } else {
                            //不显示
                            stopCountDown();
                        }
                        console.log(delaySec);
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
                $(".smartsection_fd").hide();
                if(attributeValue == 0 || attributeValue == '0'){
                    isCanChangeNum = false;
                    $(".contrl_button").children('i').css("background","url(img/icon_openbtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(lang_Details_Open);

                    deviceState = attributeValue;


                } else if (attributeValue == 1){
                    isCanChangeNum = true;
                    if (deviceMode == '3' || deviceMode == '4' || deviceMode == '5') {
                        isCanChangeNum = false;
                    }
                    $(".contrl_button").children('i').css("background","url(img/icon_closebtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(lang_Details_Close);
                    deviceState = attributeValue;
                }
                hideLoading();
            }
                break;
            case 0x8102:{
                /*设置的风量值*/
                if (attributeValue){
                    var fanStr = "";
                    if (attributeValue == '0'){
                        fanStr = '';
                    } else if (attributeValue == '1'){
                        fanStr = lang_Details_Speed3;
                    } else if (attributeValue == '2'){
                        fanStr = lang_Details_Speed2;
                    } else if (attributeValue == '3'){
                        fanStr = lang_Details_Speed1;
                    } else if (attributeValue == '4'){
                        fanStr = lang_Details_Speed4;
                    }
                    $(".smartsection_fan").html(lang_Details_Speed +"："+ fanStr);
                    deviceFan = attributeValue;
                    hideLoading();
                }
            }
                break;
            case 0x8103:{
                /*设置的温标*/
                if (attributeValue == "1"){
                    //华氏度F
                    deviceUnit = '1';
                    $(".contrl_temp").find("i").css("background","url(img/icon_wenbiao_F.png) no-repeat center center")
                    $(".contrl_temp").find("i").css("background-size","100% auto");
                    $(".num_scroll_Unit").css("background","url(img/icon_wenbiao_F.png) no-repeat center center")
                    $(".num_scroll_Unit").css("background-size","100% auto");

                } else {
                    //摄氏度°C
                    deviceUnit = '0';
                    $(".contrl_temp").find("i").css("background","url(img/icon_wenbiao.png) no-repeat center center")
                    $(".contrl_temp").find("i").css("background-size","100% auto");
                    $(".num_scroll_Unit").css("background","url(img/icon_wenbiao.png) no-repeat center center")
                    $(".num_scroll_Unit").css("background-size","100% auto");
                }
            }
                break;
            case 0x8104:{
                /*运转模式数据上报
                01：制热 lang_Details_Type2
                02：制冷 lang_Details_Type1
                03：通风 lang_Details_Type3
                04：制热节能 lang_Details_Type4
                05：制冷节能 lang_Details_Type5
                */
                hideEnergyMode();
                isShowChangeNum = true;
                if (attributeValue == "1"){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type2);
                    isCanChangeNum = true && (deviceState == '1');
                } else if (attributeValue == "2"){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type1);
                    isCanChangeNum = true && (deviceState == '1');
                } else if (attributeValue == "3"){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type3);
                    isShowChangeNum = false;
                    isCanChangeNum = false && (deviceState == '1');
                    setShowNum(10);
                } else if (attributeValue == "4"){
                    //制热节能
                    isCanChangeNum = false && (deviceState == '1');
                    //制热节能
                    $(".smartsection_fd").html(lang_Mode_Hot);
                    showEnergyMode();

                    deviceSetTemperature = 18;
                    setShowNum(18);
                    deviceSetHeatTemperature = 18;


                } else if (attributeValue == "5"){
                    //制冷节能
                    isCanChangeNum = false && (deviceState == '1');
                    //制冷节能
                    $(".smartsection_fd").html(lang_Mode_Cool);
                    showEnergyMode();

                    deviceSetTemperature = 26;
                    setShowNum(26);
                    deviceSetHeatTemperature = 26;
                }
                deviceMode = attributeValue;

                reloadBottomBtnsUI();
                reloadBottomBtnsUI();
                hideLoading();
            }
                break;
            case 0x8105:{
                /*设置的制热温度值*/
                if (attributeValue){
                    if (deviceMode == '1'){
                        deviceSetTemperature = parseFloat(attributeValue);
                        setShowNum(parseFloat(attributeValue));
                    }
                    deviceSetHeatTemperature = parseFloat(attributeValue);

                    hideLoading();
                }
            }
                break;
            case 0x8106:{
                /*设置的制冷温度值*/
                if (attributeValue){
                    if (deviceMode == '2'){
                        deviceSetTemperature = parseFloat(attributeValue);
                        setShowNum(parseFloat(attributeValue));
                    }

                    deviceSetCoolTemperature = parseFloat(attributeValue);

                    hideLoading();
                }
            }
                break;
            case 0x8107:{
                /*当前温度*/
                if (attributeValue){
                    $(".contrl_temp").find('p').html(parseFloat(attributeValue));
                    deviceInDoorTemperature = parseFloat(attributeValue);
                }
            }
                break;
            case 0x8108:{
                /*湿度*/
                if (attributeValue){

                }
            }
                break;
            case 0x8109:{
                /*定时器状态 0：关闭定时
                           1：定时开机
                           2：定时关机*/
                if (attributeValue == '0'){
                    //关闭定时
                    deviceCountDownState = '0';
                    stopCountDown();
                } else if (attributeValue == '1'){
                    //定时开机
                    deviceCountDownState = '1';
                } else if (attributeValue == '2'){
                    //定时关机
                    deviceCountDownState = '2';
                }

            }
                break;
            case 0x810A:{
                /*定时器倒计时时间*/
                if (attributeValue && attributeValue.length > 5){
                    var HHStr = attributeValue.substring(0,2);
                    var mmStr = attributeValue.substring(2,4);
                    var SSStr = attributeValue.substring(4,6);
                    var countSS = parseInt(HHStr)*3600 + parseInt(mmStr)*60 + parseInt(SSStr);
                    startCountDown(countSS);
                }

            }
                break;
            case 0x810B:{
                /*制热节能温度*/
                if (attributeValue){

                }

            }
                break;
            case 0x810C:{
                /*制冷节能温度*/
               if (attributeValue){

                }

            }
                break;
            case 0x8114:{
                // 过滤器提醒
                if (attributeValue){
                    $(".smartsection_FilterScreen").css("display",'block');
                    var remindStr = lang_Remind;
                    var resultRemindStr = remindStr.replace(/\%1\$s/g, attributeValue);
                    $(".smartsection_FilterScreen").html(resultRemindStr);
                } else {
                    $(".smartsection_FilterScreen").css("display",'none');
                    $(".smartsection_FilterScreen").html("");
                }


            }
                break;
        }
    }


})

function showEnergyMode() {
    $(".smartsection_mode").hide();
    $(".smartsection_fan").hide();
    $(".smartsection_fd").show();
}

function hideEnergyMode() {
    $(".smartsection_mode").show();
    $(".smartsection_fan").show();
    $(".smartsection_fd").hide();
}

function countTime(sec) {
    clearInterval(timer)
    timer = setInterval(function() {
        if(sec <= 0) {
            $(".theLast").hide()
            clearInterval(timer)
        }
        sec--;
        var minute = parseInt(sec)
        var hour = (parseInt(minute / 3600) + '').length == 1 ? ("0" + parseInt(minute / 3600)) : (parseInt(minute / 3600) + '')
        var min = (parseInt((minute % 3600) / 60) + '').length == 1 ? ('0' + parseInt((minute % 3600) / 60)) : (parseInt((minute % 3600) / 60) + '')
        var second = parseInt((minute % 60) + '').length == 1 ? ('0' + parseInt(minute % 60)) : (parseInt(minute % 60) + '')
        if(deviceState == 0){
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) +lang_device_Ol_Countdown_open)
        }else{
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) +lang_device_Ol_Countdown_closed)

        }
    }, 1000)
}

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
    if (deviceMode && deviceMode != ""){
        var sendTempStr = TempStr.toString();
        if (deviceMode == '1') {
            //制热
            if (sendTempStr.length == 2){
                sendTempStr = TempStr.toString()+ '000018';
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            } else if (sendTempStr.length == 4) {
                sendTempStr = TempStr.toString()+ '0018';
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            }

        } else if (deviceMode == '2'){
            //制冷
            // sendCmd(gwID,devID,ep,0x0105,[sendTempStr.toString()]);
            sendCmd(gwID,devID,ep,0x0105,["002620.5"]);
            if (sendTempStr.length == 2){
                sendTempStr = '002600' + TempStr.toString();
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            } else if (sendTempStr.length == 4) {
                sendTempStr = '0026' + TempStr.toString();
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            }
        }

    } else{

    }
}

//UI更新
function reloadBottomBtnsUI() {
    if (deviceMode == "4" || deviceMode == "5"){
        $(".contrl_energy").find("i").css("background","url(img/icon_energy_Highlight.png) no-repeat center center")
        $(".contrl_energy").find("i").css("background-size","100% auto");
        $(".contrl_mode").css("opacity", "0.4");
        $(".contrl_fan").css("opacity", "0.4");
    } else {
        $(".contrl_energy").find("i").css("background","url(img/icon_energy_Normal.png) no-repeat center center")
        $(".contrl_energy").find("i").css("background-size","100% auto");
        $(".contrl_mode").css("opacity", "1");
        $(".contrl_fan").css("opacity", "1");
    }
    if (deviceState == '0'){
        $(".contrl_mode").css("opacity", "0.4");
        $(".contrl_fan").css("opacity", "0.4");
    }
    if (deviceMode == '3' || deviceState == '0'){
        $(".contrl_energy").css("opacity", "0.4");
    } else {
        $(".contrl_energy").css("opacity", "1");
    }

}