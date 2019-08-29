var info = window.sysfun;
info.init("device_Ol");
var devID = '';
var gwID = '';
var ep = parseInt(info.getItem("endpointNumber"));
var name = info.getItem("name");
$(".deviceName").html(name);
var switchAttributes = [];
//
var deviceState = ""; //开关状态
var deviceMode = "";
var deviceFan = "";
var deviceInDoorTemperature = ""; //室内温度
var deviceSetTemperature = 16; //设置温度

///////////
var deviceHeatMaxTemperature = 32;
var deviceHeatMinTemperature = 16;
var deviceCoolMaxTemperature = 32;
var deviceCoolMinTemperature = 16;
var deviceSuooortedModes = ['1', '2', '3', '4', '5']; //支持的模式 1 : 制冷 2 : 制热 3 : 通风 4 : 地暖 5 : 地暖+制热
var deviceSuooortedFans = ['0', '1', '2', '3']; //支持的风速 0 : 高速 1 : 中速 2 : 低速 3 : 自动
///////////
var fakeLoadertime;
var timer;

var isNeedHandleTemp = false;
var devicesList = {};

function showLoading() {
    $(".saveToast").show();
    fakeLoadertime = setTimeout(function () {
        hideLoading();
    }, 10000)
}

function hideLoading() {
    window.clearInterval(fakeLoadertime);
    $(".saveToast").hide();
}

var hoursArry = [];
for (var i = 0; i < 24; i++) {
    var dic = {};
    dic.text = i + languageUtil.getResource("device_Ol_hour");
    dic.value = i;
    hoursArry.push(dic)
}
var minusArry = [];
for (var j = 0; j < 60; j++) {
    var dic = {};
    dic.text = j + languageUtil.getResource("device_Ol_Minute");
    dic.value = j;
    minusArry.push(dic)
}
var choosePicker;

document.getElementById("cancel").addEventListener("click", function () {
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
    countTime(timevalue);
    $(".theLast").show();
}

function stopCountDown(timevalue) {
    window.clearInterval(timer);
    $(".theLast").hide();
}
//
//国际化初始化函数
initlan()
plus.plusReady(function () {
    window.modeList = {
        "1": lang_Details_Type1,
        "2": lang_Details_Type2,
        "3": lang_Details_Type3,
        "4": lang_Details_Type4,
        "5": lang_Details_Type5,
    };

    $(".contrl_mode").on("click", function () {
        //切换mode
        setThermostatMode();
    })

    $(".contrl_fan").on("click", function () {
        //切换fan
        setThermostatFan();
    })

    $(".contrl_ds").on("click", function () {
        //弹出倒计时选择框
        hoursArry.length = 0;
        for (var i = 0; i < 24; i++) {
            var dic = {};
            dic.text = i + languageUtil.getResource("device_Ol_hour");
            dic.value = i;
            hoursArry.push(dic)
        }
        minusArry.length = 0;
        for (var j = 0; j < 60; j++) {
            var dic = {};
            dic.text = j + languageUtil.getResource("device_Ol_Minute");
            dic.value = j;
            minusArry.push(dic)
        }

        if (deviceState == 0) {
            choosePicker = new Picker({
                title: lang_device_Ol_Countdown_Open,
                data: [hoursArry, minusArry]
            });
        } else if (deviceState == 1) {
            choosePicker = new Picker({
                title: lang_device_Ol_Countdown_Off,
                data: [hoursArry, minusArry]
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
            if (selectedVal[0] != 0 || selectedVal[1] != 0) {
                if (deviceState == 0) {
                    //打开倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    var sendSec = selectedHour * 3600 + selectedMin * 60;
                    requestDelayOpen(sendSec.toString());
                } else if (deviceState == 1) {
                    //关闭倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    var sendSec = selectedHour * 3600 + selectedMin * 60;
                    requestDelayClose(sendSec.toString());
                }
            } else {

            }
        });
    })

    $(".contrl_button").on("click", function () {
        //开关
        setThermostatState();
    })

    $(".sure_click").on("click", function () {
        cancelCountDown();
        requestTurnOffDelay();
    })
    // choosePicker.on('picker.change', function (index, selectedIndex) {
    //     console.log(index);
    // });
    //
    // choosePicker.on('picker.valuechange', function (selectedVal, selectedIndex) {
    //     console.log(selectedVal);
    // });


    plus.gatewayCmd.getDeviceInfo(null, function (data) {
        if (data.mode == 2) {
            $(".mask_layer").show();
        } else {
            $(".mask_layer").hide();
        }
        devID = data.devID;
        gwID = data.gwID;
        requestThermostatState();
        requestThermostatAntiFreezingState(); //防冻状态
        requestThermostatCurrent(); //当前温度
        handleData(data);

    })

    plus.gatewayCmdRush.newDataRefresh(function (data) {
        console.log(data);
        if (data.cmd == "500" && devID == data.devID) {
            hideLoading();
            if (data.mode == 2) {
                $(".mask_layer").show();
            } else {
                $(".mask_layer").hide();
            }
            if (data.endpointNumber == ep) {
                var endpointName = data.endpointName;
                var endpointNumber = data.endpointNumber;
                if (!endpointName) {
                    endpointName = endpointNumber;
                }
                $(".deviceName").html(endpointName);
            }
            handleData(data);
        } else if (data.cmd == "502" && devID == data.devID) {
            if (data.endpointNumber == ep) {
                var endpointName = data.endpointName;
                var endpointNumber = data.endpointNumber;
                if (!endpointName) {
                    endpointName = endpointNumber;
                }
                $(".deviceName").html(endpointName);
            }
        }
    })

    //控制室内机的运转停止
    function setThermostatState() {
        if (deviceState == "0") {
            sendCmd(gwID, devID, ep, 0x8001, ["1"]);
        } else if (deviceState == "1") {
            sendCmd(gwID, devID, ep, 0x8001, ["0"]);
        } else {
            //开关异常
        }
    }

    //设置运转模式
    function setThermostatMode() {
        var currentModeNum = parseInt(deviceMode);
        if (currentModeNum == 6) {
            deviceSuooortedModes = ['1', '2', '3'];
            var modeIndex = rnd(1, deviceSuooortedModes.length - 1);
            var sendIndex = (modeIndex) % (deviceSuooortedModes.length);
            var sendStr = deviceSuooortedModes[sendIndex];
            sendCmd(gwID, devID, ep, 0x8002, [sendStr]);
        } else if (currentModeNum >= 0) {
            var modeIndex = $.inArray(deviceMode, deviceSuooortedModes);
            if (modeIndex >= 0) {
                // modeIndex = modeIndex+rnd(1,deviceSuooortedModes.length-1);
                var sendIndex = (modeIndex + 1) % (deviceSuooortedModes.length);
                var sendStr = deviceSuooortedModes[sendIndex];
                sendCmd(gwID, devID, ep, 0x8002, [sendStr]);
            }
        }
    }

    function rnd(n, m) {
        var random = Math.floor(Math.random() * (m - n + 1) + n);
        return random;
    }

    //设定风量
    function setThermostatFan() {
        if (deviceFan) {
            var currentFanNum = parseInt(deviceFan);
            if (currentFanNum >= 0) {
                var FanIndex = $.inArray(deviceFan, deviceSuooortedFans);
                if (FanIndex >= 0) {
                    var sendIndex = (FanIndex + 1) % (deviceSuooortedFans.length);
                    if (deviceMode == '3') {
                        //通风
                        sendIndex = (FanIndex + 1) % (deviceSuooortedFans.length - 1);
                    }
                    var sendFanStr = deviceSuooortedFans[sendIndex];
                    sendCmd(gwID, devID, ep, 0x8004, [sendFanStr]);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////
    //获取各种状态的指令

    //获取室内机的状态（查询开关机状态、工作模式、设置温度、风机模式）
    function requestThermostatState() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x8007);
        } else {

        }
    }

    //获取防冻温度
    function requestThermostatAntiFreezingValue() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x8008);
        } else {

        }
    }

    //获取防冻开启与否
    function requestThermostatAntiFreezingState() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x8009);
        } else {

        }
    }

    //延时关闭
    function requestDelayClose(sec) {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800A, [sec]);
        } else {

        }
    }

    //延时打开
    function requestDelayOpen(sec) {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800B, [sec]);
        } else {

        }
    }

    //取消延时
    function requestTurnOffDelay() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800C);
        } else {

        }
    }

    //获取当前温度
    function requestThermostatCurrent() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800D);
        } else {

        }
    }

    /////////////////////////////////////////////////////////////

    function handleData(data) {
        // drawDeviceList(data);
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            if (endpoint.endpointType && endpoint.endpointType == 0x0301) {
                if (endpoint.endpointNumber == ep) {
                    var clusters = endpoint.clusters;
                    for (var k = 0; k < clusters.length; k++) {
                        var attributes = clusters[k].attributes;
                        if (attributes) {
                            for (var j = 0; j < attributes.length; j++) {
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
        if (data.extData) {
            var mData = JSON.parse(new Base64().decode(data.extData));
            /*[{
                "delaySec" : "119",
                "endpointnumber" : 1
                }]
            */
            for (var m = 0; m < mData.length; m++) {
                if (mData[m]) {
                    var endpoints = mData[m];
                    var delaySec = endpoints.delaySec;
                    var endpointnumber = endpoints.endpointnumber;
                    if (endpointnumber == ep) {
                        //获取当前子设备的时间
                        if (parseInt(delaySec) > 0) {
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
        switch (attributeId) {
            case 0x0001:
                {
                    /*
                    0室内机停止
                    1室内机运转*/
                    $(".smartsection_fd").hide();
                    if (attributeValue == 0) {
                        isCanChangeNum = false;
                        $(".contrl_button").children('i').css("background", "url(img/icon_closebtn.png) no-repeat center center")
                        $(".contrl_button").children('i').css("background-size", "100% auto");
                        $(".contrl_button").children('span').text(lang_Details_Close);
                        deviceState = attributeValue;
                    } else if (attributeValue == 1) {
                        isCanChangeNum = true;
                        $(".contrl_button").children('i').css("background", "url(img/icon_openbtn.png) no-repeat center center")
                        $(".contrl_button").children('i').css("background-size", "100% auto");
                        $(".contrl_button").children('span').text(lang_Details_Open);
                        deviceState = attributeValue;
                    } else if (attributeValue == 2) {
                        $(".smartsection_fd").show();
                    }
                    hideLoading();
                }
                break;
            case 0x0002:
                {
                    /*运转模式数据上报
                    01：制冷 lang_Details_Type1
                    02：制热 lang_Details_Type2
                    03：通风 lang_Details_Type3
                    04：地暖 lang_Details_Type4
                    05：地暖+制热 lang_Details_Type5
                    */
                    if (attributeValue == "1") {
                        $(".smartsection_mode").html(lang_Details_Type + "：" + lang_Details_Type1);
                    } else if (attributeValue == "2") {
                        $(".smartsection_mode").html(lang_Details_Type + "：" + lang_Details_Type2);
                    } else if (attributeValue == "3") {
                        $(".smartsection_mode").html(lang_Details_Type + "：" + lang_Details_Type3);
                    } else if (attributeValue == "4") {
                        $(".smartsection_mode").html(lang_Details_Type + "：" + lang_Details_Type4);
                    } else if (attributeValue == "5") {
                        $(".smartsection_mode").html(lang_Details_Type + "：" + lang_Details_Type5);
                    }
                    deviceMode = attributeValue;
                    hideLoading();
                }
                break;
            case 0x0003:
                {
                    /*设置的温度值*/
                    if (attributeValue) {
                        $(".num_show").find("div").find("p").html(attributeValue);
                        deviceSetTemperature = parseInt(attributeValue) / 10.0;
                        setShowNum(parseInt(attributeValue) / 10.0);
                        hideLoading();
                    }
                }
                break;
                // case 0x0004:{
                //     /*实际的温度值*/
                //     if (attributeValue){
                //         $(".contrl_temp").find('p').html(attributeValue);
                //         deviceInDoorTemperature = attributeValue;
                //     }
                // }
                //     break;
            case 0x0004:
                {
                    /*设置的风量值*/
                    if (attributeValue) {
                        var fanStr = "";
                        if (attributeValue == '0') {
                            fanStr = lang_Details_Speed1;
                        } else if (attributeValue == '1') {
                            fanStr = lang_Details_Speed2;
                        } else if (attributeValue == '2') {
                            fanStr = lang_Details_Speed3;
                        } else if (attributeValue == '3') {
                            fanStr = lang_Details_Speed4;
                        }
                        $(".smartsection_fan").html(lang_Details_Speed + "：" + fanStr);
                        deviceFan = attributeValue;
                        hideLoading();
                    }
                }
                break;
            case 0x0006:
                {
                    /*防冻功能*/

                }
                break;
            case 0x0007:
                {
                    /*当前温度*/
                    if (attributeValue) {
                        $(".contrl_temp").find('p').html(parseInt(attributeValue) / 10.0);
                        deviceInDoorTemperature = parseInt(attributeValue) / 10.0;
                    }
                }
                break;
        }
    }


})

function countTime(sec) {
    clearInterval(timer)
    timer = setInterval(function () {
        if (sec <= 0) {
            $(".theLast").hide()
            clearInterval(timer)
        }
        sec--;
        var minute = parseInt(sec)
        var hour = (parseInt(minute / 3600) + '').length == 1 ? ("0" + parseInt(minute / 3600)) : (parseInt(minute / 3600) + '')
        var min = (parseInt((minute % 3600) / 60) + '').length == 1 ? ('0' + parseInt((minute % 3600) / 60)) : (parseInt((minute % 3600) / 60) + '')
        var second = parseInt((minute % 60) + '').length == 1 ? ('0' + parseInt(minute % 60)) : (parseInt(minute % 60) + '')
        if (deviceState == 0) {
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) + ":" + ((second + '').length == 1 ? "0" + second : second) + lang_device_Ol_Countdown_open)
        } else {
            $(".theLast span").html(((hour + '').length == 1 ? "0" + hour : hour) + ":" + ((min + '').length == 1 ? "0" + min : min) + ":" + ((second + '').length == 1 ? "0" + second : second) + lang_device_Ol_Countdown_closed)

        }
    }, 1000)
}

function sendCmd(gwID, dID, endpointNumber, commandId, parameter) {
    var jsonData = {}
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = dID;
    jsonData.endpointNumber = endpointNumber;
    jsonData.clusterId = 0x0201;
    jsonData.commandType = 1;
    jsonData.commandId = commandId;
    if (parameter) {
        jsonData.parameter = parameter;
    }
    showLoading();
    plus.gatewayCmd.controlDevice(jsonData, function () {})
}

//设置温度
function setThermostatTemperature(TempStr) {
    if (deviceMode && deviceMode != "") {
        var sendTempStr = TempStr * 10;
        sendCmd(gwID, devID, ep, 0x8003, [sendTempStr.toString()]);
    } else {

    }
}