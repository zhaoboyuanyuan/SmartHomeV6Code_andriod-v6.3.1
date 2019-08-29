var info = window.sysfun;
info.init("device_Ol");
var devID = '';
var gwID = '';
var ep;
var switchAttributes = [];
//
var deviceState = "";//开关状态
var deviceMode = "";
var deviceFan = "";
var deviceInDoorTemperature = "";//室内温度
var deviceSetTemperature = 10;//设置温度

var deviceCountDownState = "";//倒计时状态 0：关闭定时 1：定时开机 2：定时关机

///////////
var deviceHeatMaxTemperature = 32;
var deviceHeatMinTemperature = 10;
var deviceCoolMaxTemperature = 32;
var deviceCoolMinTemperature = 10;
var deviceSuooortedModes = ['0','1','2'];//支持的模式 1 : 制冷 2 : 制热 3 : 通风
var deviceSuooortedFans = ['1','2','3','4'];//支持的风速 0 : 高速 1 : 中速 2 : 低速 3 : 自动
///////////
var fakeLoadertime;
var timer;

var isNeedHandleTemp = false;
var devicesList = {};

//返回到设备详情页
$("#back").on("click",function(){
    plus.tools.back(function() {})
});

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
        // countTime(timevalue);
        if(deviceState == 0){
            $(".theLast span").html(timevalue+lang_device_Ol_hour+lang_device_Ol_Countdown_open)
        }else{
            $(".theLast span").html(timevalue+lang_device_Ol_hour+lang_device_Ol_Countdown_closed)

        }
        $(".theLast").show();
    } else {
        stopCountDown();
    }

}

function stopCountDown() {
    // window.clearInterval(timer);
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
        "4":lang_Details_Type4,
        "5":lang_Details_Type5,
    };

    $(".contrl_mode").on("click",function () {
        //切换mode
        if(deviceState==0){

        }else if(deviceState==1){
            setThermostatMode();
        }
    })

    $(".contrl_fan").on("click",function () {
        //切换fan.
        if(deviceState==0){

        }else if(deviceState==1){
            setThermostatFan();
        }
    })

    $(".contrl_ds").on("click",function () {
        //弹出倒计时选择框
        hoursArry.length = 0;
        for (var i = 0;i < 25;i++){
            var dic = {};
            dic.text = i+languageUtil.getResource("Hour");
            dic.value = i;
            hoursArry.push(dic)
        }
        minusArry.length = 0;
        for (var j = 0;j < 60;j++){
            var dic = {};
            dic.text = j+languageUtil.getResource("Minute");
            dic.value = j;
            if(j==30 || j==0){
                minusArry.push(dic)
            }
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
                    var selectedMin = selectedVal[1];
                    if(selectedMin == 0){
                        if(selectedHour<5){
                            var sendSec = '0'+selectedHour*2;
                        }else{
                            var sendSec = selectedHour*2;
                        }
                    }else if(selectedHour==0){
                        var sendSec = '01';
                    }else{
                        for(var h=1;h<25;h++){
                            if(selectedHour==h){
                                if(selectedHour<5){
                                    var sendSec ='0'+ (selectedHour * 2 + 1);
                                }else{
                                    var sendSec =+ selectedHour * 2 + 1;
                                }

                            }
                        }
                    }
                    requestDelayOpen('1'+sendSec);
                } else if (deviceState == 1){
                    //关闭倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    if(selectedMin == 0){
                        if(selectedHour<5){
                            var sendSec = '0'+selectedHour*2;
                        }else{
                            var sendSec = selectedHour*2;
                        }
                    }else if(selectedHour==0){
                        var sendSec = '01';
                    }else{
                        for(var h=1;h<25;h++){
                            if(selectedHour==h){
                                if(selectedHour<5){
                                    var sendSec ='0'+ (selectedHour * 2 + 1);
                                }else{
                                    var sendSec =+ selectedHour * 2 + 1;
                                }

                            }
                        }
                    }
                    requestDelayOpen('2'+sendSec);
                }
            }else{

            }
        });
    })

    $(".contrl_button").on("click",function () {
        //开关
        setThermostatState();
    })

    $(".sure_click").on("click",function (){
        cancelCountDown();
        requestTurnOffDelay();
    })

    plus.gatewayCmd.getDeviceInfo(null, function(data){
        if(data.mode == 2){
            hideLoading();
            $(".mask_layer").show();
        }else{
            ep = data.endpoints[0].endpointNumber;
            // $(".deviceName").html(data.name);
            // alert(JSON.stringify(data));
            queryCurrentState();
            $(".mask_layer").hide();
        }
        // requestThermostatState();
        // requestThermostatAntiFreezingState();//防冻状态
        // requestThermostatCurrent();//当前温度
        devID = data.devID;
        gwID = data.gwID;
        getMoreConfig(devID);
        handleData(data);

    })
    //跳转到更多页面
    $(".more").unbind("click").bind("click",function(){
        console.log(moreConfig);
        plus.tools.more(moreConfig, function() {})
    });

    plus.gatewayCmdRush.newDataRefresh(function(data){
        console.log(data);
        if(data.cmd == "500" && devID == data.devID){
            // alert(JSON.stringify(data));
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

    //控制温控器的运转停止
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
            var modeIndex = $.inArray(deviceMode, deviceSuooortedModes);
            if(modeIndex >= 0){
                var sendIndex = (modeIndex + 1)%(deviceSuooortedModes.length);
                var sendStr = deviceSuooortedModes[sendIndex];
                sendCmd(gwID,devID,ep,0x0102,[sendStr]);
            }
        }
    }

    //设定风量
    function setThermostatFan() {
        if(deviceFan){
            var currentFanNum = parseInt(deviceFan);
            if(currentFanNum >= 0){
                var FanIndex = $.inArray(deviceFan, deviceSuooortedFans);
                if(FanIndex >= 0){
                    var sendIndex = (FanIndex+1)%(deviceSuooortedFans.length);
                    if (deviceMode == '2'){
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
    //查询当前状态
    function queryCurrentState() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0100);
        } else{

        }
    }

    //延时打开
    function requestDelayOpen(sec) {
        if (devID){
            sendCmd(gwID,devID,ep,0x0201,[sec]);
        } else{

        }
    }

    //取消延时
    function requestTurnOffDelay() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0201,['0']);
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
    }
    function refreshUI(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId){
            case 33025:{
                /*
                0室内机停止
                1室内机运转*/
                $(".smartsection_fd").hide();
                if(attributeValue == 0){
                    isCanChangeNum = false;
                    $(".contrl_button").children('i').css("background","url(img/icon_openbtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(lang_Details_Open);
                    $(".contrl_mode").css("opacity","0.4");
                    $(".contrl_fan").css("opacity","0.4");
                    deviceState = attributeValue;
                } else if (attributeValue == 1){
                    isCanChangeNum = true;
                    $(".contrl_button").children('i').css("background","url(img/icon_closebtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(lang_Details_Close);
                    $(".contrl_mode").css("opacity","1");
                    $(".contrl_fan").css("opacity","1");
                    deviceState = attributeValue;
                }
                hideLoading();
            }
                break;
            case 33028:{
                /*运转模式数据上报
                01：制冷 lang_Details_Type1
                02：制热 lang_Details_Type2
                03：通风 lang_Details_Type3
                */
                isShowChangeNum = true;
                if (attributeValue == "0"){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type2);
                } else if (attributeValue == "1"){
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type1);
                } else if (attributeValue == "2"){
                    isShowChangeNum = false;
                    isCanChangeNum = false;
                    $(".smartsection_mode").html(lang_Details_Type+"："+lang_Details_Type3);
                }
                deviceMode = attributeValue;
                hideLoading();
            }
                break;
            //制冷值
            case 33030:{
                /*设置的温度值*/
                if (attributeValue){
                    if($(".smartsection_mode").html()== lang_Details_Type+"："+lang_Details_Type1 ){
                        $(".num_show").find("div").find("p").html(attributeValue);
                        deviceSetTemperature = attributeValue;
                        setShowNum(attributeValue);
                    }else if($(".smartsection_mode").html()== lang_Details_Type+"："+lang_Details_Type3 ) {
                        $(".num_show").find("div").find("p").html('--');
                        deviceSetTemperature = '--';
                        setShowNum('--');
                    }
                    hideLoading();
                }
            }
                break;
            //制热值
            case 33029:{
                /*设置的温度值*/
                if (attributeValue){
                    if($(".smartsection_mode").html()== lang_Details_Type+"："+lang_Details_Type2 ){
                        $(".num_show").find("div").find("p").html(attributeValue);
                        deviceSetTemperature = attributeValue;
                        setShowNum(attributeValue);
                    }else if($(".smartsection_mode").html()== lang_Details_Type+"："+lang_Details_Type3){
                        $(".num_show").find("div").find("p").html('--');
                        deviceSetTemperature = '--';
                        setShowNum('--');
                    }
                    hideLoading();
                }
            }
                break;
            case 33026:{
                /*设置的风量值*/
                if (attributeValue){
                    var fanStr = "";
                    if (attributeValue == '0'){
                        fanStr = lang_Details_Closed;
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
            case 33031:{
                /*当前温度*/
                if (attributeValue){
                    $(".contrl_temp").find('p').html(parseFloat(attributeValue));
                    deviceInDoorTemperature = parseFloat(attributeValue);
                }
            }
                break;
            case 33044:{
                /*过滤提醒*/
                if (attributeValue){
                    $(".smartsection_fd").html(attributeValue+'个月没有换过滤器了');
                    $(".smartsection_fd").show();
                }
            }
                break;
            case 33033:{
                /*定时器状态 0：关闭定时
                           1：定时开机
                           2：定时关机*/
                if (attributeValue == 0){
                    //关闭定时
                    deviceCountDownState = '0';
                    stopCountDown();
                } else if (attributeValue == 1){
                    //定时开机
                    deviceCountDownState = '1';
                } else if (attributeValue == 2){
                    //定时关机
                    deviceCountDownState = '2';
                }

            }
                break;
            case 33034:{
                /*定时时间*/
                if (attributeValue){
                    if(attributeValue.substr(0,1)==0){
                        var hour = attributeValue.substr(1,1);
                        startCountDown(hour/2);
                    }else{
                        startCountDown(attributeValue/2);
                    }
                }
            }
                break;
        }
    }


})

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
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) + ":" + ((second + '').length == 1 ? "0" + second : second)+lang_device_Ol_Countdown_open)
        }else{
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) + ":" + ((second + '').length == 1 ? "0" + second : second)+lang_device_Ol_Countdown_closed)

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
        if (deviceMode == '0') {
            //制热
            if (sendTempStr.length == 2){
                sendTempStr = TempStr.toString();
                sendCmd(gwID,devID,ep,0x0104,[sendTempStr]);
            } else if (sendTempStr.length == 4) {
                sendTempStr = TempStr.toString();
                sendCmd(gwID,devID,ep,0x0104,[sendTempStr]);
            }

        } else if (deviceMode == '1'){
            //制冷
            // sendCmd(gwID,devID,ep,0x0105,[sendTempStr.toString()]);
            // sendCmd(gwID,devID,ep,0x0105,["002620.5"]);
            if (sendTempStr.length == 2){
                sendTempStr = TempStr.toString();
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            } else if (sendTempStr.length == 4) {
                sendTempStr = TempStr.toString();
                sendCmd(gwID,devID,ep,0x0105,[sendTempStr]);
            }
        }

    } else{

    }
}
