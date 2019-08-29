var info = window.sysfun;
info.init("device_a0");
var devID = "";
var gwID = "";
var ep = parseInt(getUrlParam("endpointNumber"));
var name = info.getItem("name");
$(".deviceName").html(name);
var switchAttributes = [];
//
var isMaster = false;
var deviceState = ""; //开关状态
var deviceMode = "";
var deviceFan = "";
var deviceDs = "";
var deviceInDoorTemperature = ""; //室内温度
var deviceSetTemperature = ""; //设置温度

///////////
var deviceHeatMaxTemperature;
var deviceHeatMinTemperature;
var deviceCoolMaxTemperature;
var deviceCoolMinTemperature;
var deviceSupportedModes = []; //支持的模式
var deviceSupportedFans = []; //支持的风速
var deviceSupportedFX; //支持的风向
var deviceSupportedFXs = []; //支持的风向档位
var deviceSupportedVentilationModes = []; //支持的换气模式
var s_Oi_temperatureUnit;
var s_Oi_EmergencyHeatData;
///////////
var fakeLoadertime;
var timer;
var reloadtime;

var isNeedHandleTemp = false;
var devicesList = {};
var choosePicker;

function showLoading() {
    $(".saveToast").show();
    fakeLoadertime = setTimeout(function () {
        hideLoading();
    }, 10000);
}

function hideLoading() {
    window.clearInterval(fakeLoadertime);
    $(".saveToast").hide();
}
//
//国际化初始化函数
initlan();
plus.plusReady(function () {
    window.modeList = {
        "0": lang_Details_Type1,
        "1": lang_Details_Type2,
        "2": lang_Details_Type3,
        "3": lang_Details_Type4,
        "7": lang_Details_Type5
    };

    $(".contrl_mode").on("click", function () {
        //切换mode
        if (deviceState == "1") {
            setThermostatMode();
        }
    });

    $(".contrl_fan").on("click", function () {
        //切换fan
        if (deviceState == "1" && deviceMode != "7") {
            setThermostatFan();
        }
    });

    $(".contrl_ds").on("click", function () {
        //弹出倒计时选择框
        var hoursArry = [];
        var minusArry = [];
        for (var i = 0; i < 24; i++) {
            var dic = {};
            dic.text = i < 10 ? "0" + i : i; //languageUtil.getResource("时");
            dic.value = i;
            hoursArry.push(dic);
        }

        for (var j = 0; j < 2; j++) {
            var dic = {};
            dic.text = j == 0 ? "00" : "30"; //languageUtil.getResource("分");((j < 10) ? ("0" + j) : j);
            dic.value = j;
            minusArry.push(dic);
        }
        if (!choosePicker) {
            choosePicker = new Picker({
                data: [hoursArry, minusArry]
            });
        }

        choosePicker.show();
        $(".picker").addClass("pickerIndex0");
        $(".picker .picker-panel .picker-choose .picker-title")[0].innerHTML =
            deviceState == 0 ? count_down_on : count_down_off;
        document.getElementsByClassName("cancel-hook")[0].innerHTML = lang_Cancel;
        document.getElementsByClassName("confirm-hook")[0].innerHTML = lang_Confirm;

        choosePicker.on("picker.select", function (selectedVal, selectedIndex) {
            //确定
            if (selectedVal[0] != 0 || selectedVal[1] != 0) {
                if (deviceState == 0) {
                    //打开倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    var sendSec = selectedHour * 3600 + selectedMin * 30 * 60;
                    requestDelayOpen(sendSec.toString());
                } else if (deviceState == 1) {
                    //关闭倒计时
                    var selectedHour = selectedVal[0];
                    var selectedMin = selectedVal[1];
                    var sendSec = selectedHour * 3600 + selectedMin * 30 * 60;
                    requestDelayClose(sendSec.toString());
                }
            } else {}
        });
    });

    function btn_cancel() {
        $("#cancelCountDown").hide();
    }

    //
    function cancelCountDown() {
        $("#cancelCountDown").hide();
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

    //延时关闭
    function requestDelayClose(sec) {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800A, [sec]);
        } else {}
    }

    //延时打开
    function requestDelayOpen(sec) {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800B, [sec]);
        } else {}
    }

    //取消延时
    function requestTurnOffDelay() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x800C);
        } else {}
    }

    $(".back").on("click", function () {
        window.history.back(-1);
        //window.location = "roadTranslator.html";
        clearInterval(reloadtime);
    });

    $(".controlSwitch_button i").on("click", function () {
        //开关
        setThermostatState();
    });

    $("#cancel").on("click", function () {
        //点击取消倒计时按钮，弹框确认
        $("#cancelCountDown").show();
    });

    $(".sure_click").on("click", function () {
        cancelCountDown();
        requestTurnOffDelay();
    });

    $(".cancel_click").on("click", function () {
        $("#cancelCountDown").hide();
    });

    plus.gatewayCmd.getDeviceInfo(null, function (data) {
        if (data.mode == 2) {
            $(".mask_layer").show();
        } else {
            $(".mask_layer").hide();
        }
        devID = data.devID;
        gwID = data.gwID;
        // ep = ~~(data.endpoints[0].endpointNumber)
        requestThermostatPlanes();
        getThermostatAllInfo();
        handleData(data);
    });

    plus.gatewayCmdRush.newDataRefresh(function (data) {
        console.log(data);
        if (data.cmd == "500" && devID == data.devID) {
            hideLoading();
            if (data.mode == 2) {
                $(".mask_layer").show();
            } else {
                $(".mask_layer").hide();
            }
            handleData(data);
        } else if (data.cmd == "502" && devID == data.devID) {
            var endpointName = data.endpointName;
            var endpointNumber = data.endpointNumber;
            if (!endpointName) {
                endpointName = endpointNumber;
            }
            $(".deviceName").html(endpointName);
        }
    });

    //控制室内机的运转停止
    function setThermostatState() {
        if (deviceState == "0") {
            sendCmd(gwID, devID, ep, 0x0101, ["1"]);
        } else if (deviceState == "1") {
            sendCmd(gwID, devID, ep, 0x0101, ["0"]);
        } else {
            //开关异常
        }
    }

    //设置运转模式
    function setThermostatMode() {
        var currentModeNum = parseInt(deviceMode);
        if (currentModeNum >= 0) {
            var modeIndex = $.inArray(
                currentModeNum.toString(),
                deviceSupportedModes
            );
            if (modeIndex >= 0) {
                var sendIndex = (modeIndex + 1) % deviceSupportedModes.length;
                var sendStr = deviceSupportedModes[sendIndex];
                if (!isMaster && (sendStr == "1" || sendStr == "2")) {
                    window.showDialog.show(languageUtil.getResource("Cooling_and_heating"), 3000);
                }
                sendCmd(gwID, devID, ep, 0x0102, [sendStr]);
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
                if (deviceSupportedFans.length == 0) {
                    $(".toast").html(lang_Details_Nospeed);
                    $(".toast").show();
                    setTimeout(function () {
                        $(".toast").hide();
                    }, 3000);
                } else {
                    var FanIndex = $.inArray(currentFanNum, deviceSupportedFans);
                    if (FanIndex >= 0) {
                        var sendIndex = (FanIndex + 1) % deviceSupportedFans.length;
                        var sendFanStr = "" + deviceSupportedFans[sendIndex];
                        sendCmd(gwID, devID, ep, 0x0106, [sendFanStr]);

                        console.log("风量" + sendFanStr);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////
    //获取各种状态的指令

    //获取室内机的运转停止状态
    function requestThermostatState() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0201);
        } else {}
    }

    //获取运转模式
    function requestThermostatMode() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0202);
        } else {}
    }

    //获取设定的温度
    function requestThermostatTemperature() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0203);
        } else {}
    }

    //获取设定的风向
    function requestThermostatFX() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0205);
        } else {}
    }

    //获取设定的风量
    function requestThermostatFan() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0206);
        } else {}
    }

    //获取所有信息
    function getThermostatAllInfo() {
        sendCmd(gwID, devID, ep, 0x0200);
        timerRefresh();
    }

    function timerRefresh() {
        clearInterval(reloadtime);
        reloadtime = setInterval(function () {
            refreshAllInfo();
        }, 5000);
    }

    //获取已连接的室内机的厂家信息（默认第一个编号为"01"室内机）
    function requestThermostatFirstPlane() {
        if (devID) {
            sendCmd(gwID, devID, ep, 0x0303);
        } else {}
    }

    //获取已连接的室内机的性能信息
    function requestThermostatPlanes() {
        if (devID) {
            var ep1 = (ep + "").length == 1 ? "0" + ep : "" + ep;
            sendCmd(gwID, devID, ep, 0x0304, [ep1]);
        } else {}
    }
    /////////////////////////////////////////////////////////////

    function handleData(data) {
        // drawDeviceList(data);
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            if (endpoint.endpointNumber == ep) {
                var clusters = endpoint.clusters;
                for (var k = 0; k < clusters.length; k++) {
                    var attributes = clusters[k].attributes;
                    for (var j = 0; j < attributes.length; j++) {
                        var attributeId = attributes[j].attributeId;
                        var attributeValue = attributes[j].attributeValue;
                        switchAttributes[attributeId] = attributeValue;
                        refreshUI(attributes[j]);
                        timerRefresh();
                    }
                }
            }
        }
        //extData
        if (data.endpoints[0].endpointNumber == ep) {
            if (data.extData) {
                var mData = JSON.parse(new Base64().decode(data.extData));
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
            } else {
                //不显示
                stopCountDown();
            }
        }
    }

    function refreshUI(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId) {
            case 0x8101:
                {
                    /*
                              0室内机停止
                              1室内机运转*/
                    if (attributeValue == 0) {
                        $(".controlSwitch_button")
                            .children("i")
                            .css(
                                "background",
                                "url(img/icon_closebtn.png) no-repeat center center"
                            );
                        $(".controlSwitch_button")
                            .children("i")
                            .css("background-size", "100% auto");
                        $(".controlSwitch_button")
                            .children("span")
                            .text(lang_Details_Open);
                        deviceState = attributeValue;
                    } else if (attributeValue == 1) {
                        $(".controlSwitch_button")
                            .children("i")
                            .css(
                                "background",
                                "url(img/icon_openbtn.png) no-repeat center center"
                            );
                        $(".controlSwitch_button")
                            .children("i")
                            .css("background-size", "100% auto");
                        $(".controlSwitch_button")
                            .children("span")
                            .text(lang_Details_Close);
                        deviceState = attributeValue;
                    }
                }
                break;
            case 0x8102:
                {
                    /*运转模式数据上报
                              0:"送风";
                              1:"制热";
                              2:"制冷";
                              3:"自动";
                              4:"除湿";
                              */
                    if (attributeValue == 0) {
                        $(".smartsection_mode").html(
                            lang_Details_Type + "：" + lang_Details_Type1
                        );
                    } else if (attributeValue == 1) {
                        $(".smartsection_mode").html(
                            lang_Details_Type + "：" + lang_Details_Type2
                        );
                    } else if (attributeValue == 2) {
                        $(".smartsection_mode").html(
                            lang_Details_Type + "：" + lang_Details_Type3
                        );
                    } else if (attributeValue == 3) {
                        $(".smartsection_mode").html(
                            lang_Details_Type + "：" + lang_Details_Type4
                        );
                    } else if (attributeValue == 7) {
                        $(".smartsection_mode").html(
                            lang_Details_Type + "：" + lang_Details_Type5
                        );
                    }
                    deviceMode = attributeValue;
                }
                break;
            case 0x8103:
                {
                    /*设置的温度值*/
                    if (attributeValue && attributeValue != deviceSetTemperature) {
                        $(".num_show")
                            .find("div")
                            .find("p")
                            .html(attributeValue);
                        deviceSetTemperature = attributeValue;
                        setShowNum(parseInt(deviceSetTemperature));
                    }
                }
                break;
            case 0x8104:
                {
                    /*设置的的湿度值*/
                    if (attributeValue) {
                        /*交互暂无适度*/
                    }
                }
                break;
            case 0x0209:
                {
                    /*实际的室内湿度*/
                }
                break;
            case 0x020a:
                {
                    /*实际的温度值*/
                    if (attributeValue) {
                        $(".contrl_temp")
                            .find("p")
                            .html(attributeValue);
                        deviceInDoorTemperature = attributeValue;
                    }
                }
                break;
            case 0x8105:
                {
                    /*xxyy
                                  xx 为设置的风向值：(00:上下，01：左右)，
                                  yy 为叶片方向（01-06），摆动：07，自动：00，停止：09*/
                    // if (attributeValue) {
                    //     deviceFX = attributeValue.substring(2, 4);
                    //     var showFX;
                    //     if (deviceFX == "00") {
                    //         showFX = lang_Details_Speed4;
                    //     } else if (deviceFX == "07") {
                    //         showFX = "07";
                    //     } else if (deviceFX == "09") {
                    //         showFX = "09";
                    //     } else {
                    //         showFX = deviceFX
                    //     }
                    //     $(".smartsection_fx").html(lang_Details_Direction + "：" + showFX);
                    // }
                }
                break;
            case 0x8106:
                {
                    /*设置的风量值：自动：0 其他（1-6）*/
                    if (attributeValue) {
                        var windSpeed = lang_Details_Speed3;
                        if (deviceSupportedFans.length > 0) {
                            if (attributeValue == "1") {
                                if (deviceSupportedFans.length == 5) {
                                    windSpeed = lang_Details_Speed4;
                                } else {
                                    windSpeed = lang_Details_Speed3;
                                }
                            } else if (attributeValue == "2") {
                                windSpeed = lang_Details_Speed3;
                            } else if (attributeValue == "3") {
                                windSpeed = lang_Details_Speed3;
                            } else if (attributeValue == "4") {
                                windSpeed = lang_Details_Speed1;
                            } else if (attributeValue == "5") {
                                if (deviceSupportedFans.length == 5) {
                                    windSpeed = lang_Details_Speed5;
                                } else {
                                    windSpeed = lang_Details_Speed1;
                                }
                            }
                        }
                        $(".smartsection_fan").html(lang_Details_Speed + "：" + windSpeed);
                        deviceFan = attributeValue;
                    }
                }
                break;
            case 0x8107:
                {
                    /*换气模式：自动：0，全热：1，普通：2*/
                }
                break;
            case 0x8108:
                {
                    /*辅热状态：关闭：0，开启：1*/
                }
                break;
            case 0x8301:
                {
                    /*转换器状态：未就绪：0， 已就绪：1*/
                    attributeValue == 0 ? false : true;
                    if (attributeValue) {
                        $(".mask_loading").hide();
                    } else {
                        $(".mask_loading").show();
                        sendCmd(gwID, devID, 0, 0x0301);
                    }
                }
                break;
            case 0x8302:
                {
                    /*室内机状态：未连接：0.， 已连接：1*/
                }
                break;
            case 0x8303:
                {
                    /*AABB……BB
                                  室内机厂家信息：
                                  品牌信息代号AA：（大金：01，日历：02，美的：03，格力：04，海信：05，东芝：06， 三菱电机：07，三菱重工：08  备注：默认为 01）
                                  型号信息：BB.....BB*/
                    // if (attributeValue) {
                    //     var deviceBrand = parseInt(attributeValue.substring(0, 2));
                    //     if (deviceBrand > 8) {
                    //         //暂不支持
                    //         alert("暂不支持的品牌" + attributeValue);
                    //     }
                    // }
                }
                break;
            case 0x8304:
                {
                    /*异常信息返回：
                                  1：转换器通信异常
                                  2：室内机通信异常
                                  3：指令非法，该指令不支持
                                  4：从机地址错误、室内机编号等数据错误
                                  5：校验错误*/
                    if (attributeValue) {
                        if (attributeValue == "1") {
                            // alert("转换器通信异常");
                        } else if (attributeValue == "2") {
                            // alert("室内机通信异常");
                        } else if (attributeValue == "3") {
                            // alert("指令非法，该指令不支持");
                        } else if (attributeValue == "4") {
                            // alert("从机地址错误、室内机编号等数据错误");
                        } else if (attributeValue == "5") {
                            // alert("校验错误");
                        }
                    }
                }
                break;
            case 0x8401:
                {
                    /*室内机性能信息——模式: 0:无   1：有   （）括号内的数字表示第几位
                              （1） 制冷模式  （2） 制热模式  （3） 送风模式  （4） 预热模式  （5） 除湿模式
                              （6） 自动模式  （7） 干燥模式  （8） 清爽模式  （9） 睡眠模式  （10） 杀菌模式
                               (11） 干爽除湿模式   （12）强力除湿模式*/

                    if (attributeValue.length >= 5) {
                        deviceSupportedModes.length = 0;
                        for (var i = 0; i < 5; i++) {
                            //默认只支持制冷、制热、送风、除湿、自动
                            var tempStr = attributeValue.substring(i, i + 1);
                            if (tempStr == "1") {
                                if (i == 4) {
                                    deviceSupportedModes.push((i + 3).toString());
                                } else {
                                    deviceSupportedModes.push(i.toString());
                                }
                            }
                        }
                    }
                }
                break;
            case 0x8402:
                {
                    /*室内机的制热模式设定温度下限*/
                    if (attributeValue && attributeValue != deviceHeatMinTemperature) {
                        deviceHeatMinTemperature = parseInt(attributeValue);
                        MinTemperature = deviceHeatMinTemperature;
                        if (!(deviceMode == "02")) {
                            updatePickerArry();
                        }
                    }
                }
                break;
            case 0x8403:
                {
                    /*室内机的制热模式设定温度上限*/
                    if (attributeValue && attributeValue != deviceHeatMaxTemperature) {
                        deviceHeatMaxTemperature = parseInt(attributeValue);
                        MaxTemperature = deviceHeatMaxTemperature;
                        if (!(deviceMode == "02")) {
                            updatePickerArry();
                        }
                    }
                }
                break;
            case 0x8404:
                {
                    /*制冷模式温度上限*/
                    if (attributeValue && attributeValue != deviceCoolMaxTemperature) {
                        deviceCoolMaxTemperature = parseInt(attributeValue);
                        MaxTemperature = deviceCoolMaxTemperature;
                        if (!(deviceMode == "01")) {
                            updatePickerArry();
                        }
                    }
                }
                break;
            case 0x8405:
                {
                    /*制冷模式温度下限*/
                    if (attributeValue && attributeValue != deviceCoolMinTemperature) {
                        deviceCoolMinTemperature = parseInt(attributeValue);
                        MinTemperature = deviceCoolMinTemperature;
                        if (!(deviceMode == "01")) {
                            updatePickerArry();
                        }
                    }
                }
                break;
            case 0x8406:
                {
                    /*室内机性能信息：0：无 1：有(1) 支持换气  (2) 自动换气  (3) 全热换气  (4) 普通换气*/
                    // if (attributeValue) {
                    //     deviceSupportedVentilationModes.length = 0;
                    //     for (var i = 0; i < deviceSupportedVentilationModes.length; i++) {
                    //         var tempStr = deviceSupportedVentilationModes.substring(i, i + 1);
                    //         if (tempStr == "1") {
                    //             deviceSupportedModes.push((i + 1).toString());
                    //         }
                    //     }
                    // }
                }
                break;
            case 0x8407:
                {
                    /*主从机属性
                              x为1：从机       x为2：主机
                              */
                    if (attributeValue == 1) {
                        $(".contrl_button")
                            .children("i")
                            .css(
                                "background",
                                "url(img/icon_slave.png) no-repeat center center"
                            );
                        $(".contrl_button")
                            .children("i")
                            .css("background-size", "100% auto");
                        $(".contrl_button")
                            .children("span")
                            .text(lang_slave);
                        isMaster = false;
                    } else if (attributeValue == 2) {
                        $(".contrl_button")
                            .children("i")
                            .css(
                                "background",
                                "url(img/icon_host.png) no-repeat center center"
                            );
                        $(".contrl_button")
                            .children("i")
                            .css("background-size", "100% auto");
                        $(".contrl_button")
                            .children("span")
                            .text(lang_host);
                        isMaster = true;
                    }
                }
                break;
            case 0x8408:
                {
                    /*室内性能信息-风向风量
                               a：风量自动    0：无      1：有
                               b：风向自动    0：无      1：有
                               c：风向叶片位置数设定（数值范围：0-5，0表示固定）
                               d：风向设定
                               e：风量/换气量的风速设定  （数值范围：0-6）（当 c 的值为 0 时，此值无意义）
                               f：风量调节   0：无      1：有
                               */
                    if (attributeValue) {
                        //000021
                        deviceSupportedFans.length = 0;
                        deviceSupportedFXs.length = 0;
                        if (attributeValue.substring(0, 1) == "1") {
                            //风量自动
                            deviceSupportedFans.push("0");
                        }
                        if (attributeValue.substring(1, 2) == "1") {
                            deviceSupportedFXs.push("0");
                        }
                        if (attributeValue.substring(3, 4) == "1") {
                            //上下风向
                            deviceSupportedFX = "0";
                            var suooortedFXsB = parseInt(attributeValue.substring(2, 3));
                            for (var i = 0; i < suooortedFXsB; i++) {
                                deviceSupportedFXs.push(i + 1);
                            }
                        } else {
                            deviceSupportedFXs.length = 0;
                        }
                        if (attributeValue.substring(5, 6) == "1") {
                            var deviceSupportedFanA = parseInt(
                                attributeValue.substring(4, 5)
                            );
                            for (var i = 0; i < deviceSupportedFanA; i++) {
                                if (deviceSupportedFanA == 1) {
                                    deviceSupportedFans.push(5);
                                } else if (deviceSupportedFanA == 2) {
                                    if (i == 0) {
                                        deviceSupportedFans.push(i + 1);
                                    } else {
                                        deviceSupportedFans.push(i + 4);
                                    }
                                } else if (deviceSupportedFanA == 3) {
                                    deviceSupportedFans.push(2 * i + 1);
                                } else {
                                    deviceSupportedFans.push(i + 1);
                                }
                            }
                        } else {
                            deviceSupportedFans.length = 0;
                        }
                    }
                }
                break;
        }
    }
});

function countTime(sec) {
    clearInterval(timer);
    timer = setInterval(function () {
        if (sec <= 0) {
            $(".theLast").hide();
            clearInterval(timer);
        }
        sec--;
        var minute = parseInt(sec) + 60;
        var hour =
            (parseInt(minute / 3600) + "").length == 1 ?
            "0" + parseInt(minute / 3600) :
            parseInt(minute / 3600) + "";
        var min =
            (parseInt((minute % 3600) / 60) + "").length == 1 ?
            "0" + parseInt((minute % 3600) / 60) :
            parseInt((minute % 3600) / 60) + "";
        var second =
            parseInt((minute % 60) + "").length == 1 ?
            "0" + parseInt(minute % 60) :
            parseInt(minute % 60) + "";
        if (deviceState == 0) {
            $(".theLast span").html(
                count_time_on.replace("%@", hour).replace("%@", min)
            );
        } else {
            $(".theLast span").html(
                count_time_off.replace("%@", hour).replace("%@", min)
            );
        }
    }, 1000);
}

function sendCmd(gwID, dID, endpointNumber, commandId, parameter) {
    var jsonData = {};
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
    plus.gatewayCmd.controlDevice(jsonData, function () {});
}

//设置温度
function setThermostatTemperature(TempStr) {
    if (deviceMode == "1" || deviceMode == "2") {
        TempStr = TempStr + "";
        var sendTempStr = TempStr;
        sendCmd(gwID, devID, ep, 0x0103, [sendTempStr]);
    } else {}
}

function refreshAllInfo() {
    var jsonData = {};
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = devID;
    jsonData.endpointNumber = ep;
    jsonData.clusterId = 0x0201;
    jsonData.commandType = 1;
    jsonData.commandId = 0x0200;

    plus.gatewayCmd.controlDevice(jsonData, function () {});
}