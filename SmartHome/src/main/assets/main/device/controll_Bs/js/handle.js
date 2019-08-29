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
var deviceSetTemperature;//设置温度

var deviceCountDownState = "";//倒计时状态 0：关闭定时 1：定时开机 2：定时关机

///////////
var deviceHeatMaxTemperature = 32;
var deviceHeatMinTemperature = 10;
var deviceCoolMaxTemperature;
var deviceCoolMinTemperature;
var MaxTemperature=35;
var MinTemperature=5;
var deviceSuooortedModes = ['1','2','3'];
var deviceSuooortedFans = ['1','2','3','4'];//支持的风速 0 : 高速 1 : 中速 2 : 低速 3 : 自动
///////////
var fakeLoadertime;
var timer;

var isNeedHandleTemp = false;
var devicesList = {};
var target_temperature;
var ground_temperature;
var leaveState;

//返回到设备详情页
$("#back").on("click",function(){
    plus.tools.back(function() {})
});

//跳转到更多页面
$(".more").on("click",function(){
    plus.tools.more(moreConfig, function() {})
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
//
//国际化初始化函数
initlan()
plus.plusReady(function(){

    $(".contrl_mode").on("click",function () {
        //切换mode
        if(deviceState=="00" || leaveState == "01"){
        }else if(deviceState=="01"){
            setThermostatMode();
        }
    })

    $(".contrl_temp").on("click",function () {
        //切换mode
        if(deviceState=="00"){
        }else if(deviceState=="01"){
            changeLeaveState();
        }
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
        getMoreConfig(devID);
        ep = data.endpoints[0].endpointNumber;
        queryCurrentState();
        handleData(data);
    })

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
        if (deviceState == "00"){
            sendCmd(gwID,devID,ep,0x0102,["1"]);
        } else if (deviceState == "01"){
            sendCmd(gwID,devID,ep,0x0102,["0"]);
        }else {
            //开关异常
        }
    }

    //设置运转模式
    function setThermostatMode() {
        var currentModeNum = parseInt(deviceMode.substr(1,1));
        if (currentModeNum >= 0){
            var modeIndex = $.inArray(deviceMode.substr(1,1), deviceSuooortedModes);
            if(modeIndex >= 0){
                var sendIndex = (modeIndex + 1)%(deviceSuooortedModes.length);
                var sendStr = deviceSuooortedModes[sendIndex];
                sendCmd(gwID,devID,ep,0x0107,[sendStr]);
            }
        }
    }
    
    //改变离开状态
    function changeLeaveState() {
        if(leaveState == "00"){
            sendCmd(gwID,devID,ep,0x0106,["1"]);
            if (deviceMode == '01' || deviceMode == '02'){
                sendCmd(gwID,devID,ep,0x0105,["0180"]);
            }else if(deviceMode == '03'){
                sendCmd(gwID,devID,ep,0x0105,["0240"]);
            }
        }else if(leaveState == "01"){
            sendCmd(gwID,devID,ep,0x0106,["0"]);
        }
    }

    ////////////////////////////////////////////////////////////
    //获取各种状态的指令
    //查询当前状态
    function queryCurrentState() {
        if (devID){
            sendCmd(gwID,devID,ep,0x0101);
        } else{

        }
    }

    function handleData(data){
        // drawDeviceList(data);
        // alert(JSON.stringify(data));
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
            case 0x8101:{
                /*
                0室内机停止
                1室内机运转*/
                // $(".smartsection_fd").hide();
                if(attributeValue == "00"){
                    isCanChangeNum = false;
                    $(".contrl_button").children('i').css("background","url(img/icon_openbtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(Device_Bs_on);
                    $(".contrl_temp").css("opacity","0.4");
                    $(".contrl_mode").css("opacity","0.4");
                    deviceState = attributeValue;
                } else if (attributeValue == "01"){
                    isCanChangeNum = true;
                    $(".contrl_button").children('i').css("background","url(img/icon_closebtn.png) no-repeat center center")
                    $(".contrl_button").children('i').css("background-size","100% auto");
                    $(".contrl_button").children('span').text(Device_Bs_off);
                    $(".contrl_temp").css("opacity","1");
                    $(".contrl_mode").css("opacity","1");
                    deviceState = attributeValue;
                }
                hideLoading();
            }
                break;
            //目标室温设定
            case 0x8102:{
                /*设置的温度值*/
                if (attributeValue){
                    target_temperature = attributeValue;
                }
            }
                break;
            //目标地温设置
            case 0x8103:{
                /*当前温度*/
                if (attributeValue){
                    ground_temperature = attributeValue;
                }
            }
                break;
            case 0x8105:{
                //离开状态 00离开关闭 01离开打开
                if(attributeValue == "00"){
                    if(deviceState=="00"){
                        isCanChangeNum = false;
                        $(".contrl_mode").css("opacity","0.4");
                    }else{
                        isCanChangeNum = true;
                        $(".contrl_mode").css("opacity","1");
                    }
                    $(".contrl_temp").children("div").css("background","url(img/icon_leave.png) no-repeat center center")
                    $(".contrl_temp").children('div').css("background-size","100% auto");
                    leaveState = attributeValue;
                }else if(attributeValue == "01"){
                    isCanChangeNum = false;
                    $(".contrl_temp").children("div").css("background","url(img/icon_leave_2.png) no-repeat center center")
                    $(".contrl_temp").children('div').css("background-size","100% auto");
                    $(".contrl_mode").css("opacity","0.4");
                    leaveState = attributeValue;
                }
                hideLoading();
            }
                break;
            //温度控制模式
            case 0x8106:{
                if(attributeValue == "01"){
                    MaxTemperature=35;
                    MinTemperature=5;
                    deviceCoolMaxTemperature = 35;
                    deviceCoolMinTemperature = 5;
                    $("#model").html(Device_Bs_Mode_02);
                    $("#control_model").html(Device_Bs_temperature_01);
                    $(".num_show").find("div").find("p").html(target_temperature);
                    deviceSetTemperature = target_temperature;
                    setShowNum(target_temperature);
                    deviceMode = attributeValue;
                }else if(attributeValue == "02"){
                    MaxTemperature=35;
                    MinTemperature=5;
                    deviceCoolMaxTemperature = 35;
                    deviceCoolMinTemperature = 5;
                    $("#model").html(Device_Bs_Mode_03);
                    $("#control_model").html(Device_Bs_temperature_01);
                    $(".num_show").find("div").find("p").html(target_temperature);
                    deviceSetTemperature = target_temperature;
                    setShowNum(target_temperature);
                    deviceMode = attributeValue;
                }else if(attributeValue == "03"){
                    MaxTemperature=45;
                    MinTemperature=20;
                    deviceCoolMaxTemperature = 45;
                    deviceCoolMinTemperature = 20;
                    $("#model").html(Device_Bs_Mode_04);
                    $("#control_model").html(Device_Bs_temperature_02);
                    $(".num_show").find("div").find("p").html(ground_temperature);
                    deviceSetTemperature = ground_temperature;
                    setShowNum(ground_temperature);
                    deviceMode = attributeValue;
                }
                if(leaveState=="01"){
                    if(deviceMode=="01" || deviceMode=="02"){
                        $(".num_show").find("div").find("p").html("18");
                        deviceSetTemperature = "18";
                        setShowNum("18");
                    }else if(deviceMode=="03"){
                        $(".num_show").find("div").find("p").html("24");
                        deviceSetTemperature = "24";
                        setShowNum("24");
                    }
                    $("#model").html(Device_Bs_temperature_005);
                    $("#control_model").html(Device_Bs_temperature_02);
                }
                hideLoading();
            }
                break;
            //室内温度
            case 0x810B:{
                /*设置的温度值*/
                if($("#model").html() == Device_Bs_Mode_02 || $("#model").html() == Device_Bs_Mode_03){
                    $(".contrl_ds").children("p").html(parseInt(attributeValue));
                }else{
                    $(".contrl_ds").children("p").html("--");
                }
                if(leaveState=="01"){
                    if(deviceMode == "01" || deviceMode == "02"){
                        $(".contrl_ds").children("p").html(parseInt(attributeValue));
                    }else{
                        $(".contrl_ds").children("p").html("--");
                    }
                }
                $(".contrl_ds").children("p").css("color","#000000");
            }
                break;
            //地表温度
            case 0x810C:{
                /*设置的温度值*/
                if($("#model").html() == Device_Bs_Mode_04 || $("#model").html() == Device_Bs_Mode_03){
                    $(".contrl_fan").children("p").html(parseInt(attributeValue));
                }else{
                    $(".contrl_fan").children("p").html("--");
                }
                if(leaveState=="01"){
                    if(deviceMode == "03" || deviceMode == "02"){
                        $(".contrl_fan").children("p").html(parseInt(attributeValue));
                    }else{
                        $(".contrl_fan").children("p").html("--");
                    }
                }
                $(".contrl_fan").children("p").css("color","#000000");
            }
                break;
            case 0x810D:{
                //故障提醒
                var  breakdow = '';
                if (attributeValue.substr(3,1)==1){
                    breakdow = '<p class="smartsection_mode">'+Device_Bs_Alarm_05+'</p>';
                    $(".smartsection_show").children("div").children().not("#model").remove();
                    $(".smartsection_show").children("div").append(breakdow);
                }else if (attributeValue.substr(4,1)==1){
                    breakdow = '<p class="smartsection_mode">'+Device_Bs_Alarm_04+'</p>';
                    $(".smartsection_show").children("div").children().not("#model").remove();
                    $(".smartsection_show").children("div").append(breakdow);
                }else if (attributeValue.substr(5,1)==1){
                    breakdow = '<p class="smartsection_mode">'+Device_Bs_Alarm_03+'</p>';
                    $(".smartsection_show").children("div").children().not("#model").remove();
                    $(".smartsection_show").children("div").append(breakdow);
                    $(".contrl_ds").children("p").css("color","#CD5C5C");
                    $(".contrl_fan").children("p").css("color","#CD5C5C");
                    $(".contrl_ds").children("p").html("!");
                    $(".contrl_fan").children("p").html("!");
                }else if (attributeValue.substr(6,1)==1){
                    $(".contrl_fan").children("p").html("--");
                }else if (attributeValue.substr(7,1)==1){
                    breakdow = '<p class="smartsection_mode">'+Device_Bs_Alarm_01+'</p>';
                    $(".smartsection_show").children("div").children().not("#model").remove();
                    $(".smartsection_show").children("div").append(breakdow);
                    $(".contrl_ds").children("p").css("color","#CD5C5C");
                    $(".contrl_ds").children("p").html("!");
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
    if (deviceMode && deviceMode != ""){
        if (deviceMode == '01' || deviceMode == '02') {
            //单室内温度控制
            if ((TempStr*10).toString().length==3){
                var sendTempStr = "0" + (TempStr*10);
                sendCmd(gwID,devID,ep,0x0103,[sendTempStr]);
            }else if((TempStr*10).toString().length==2){
                var sendTempStr = "00" + (TempStr*10);
                sendCmd(gwID,devID,ep,0x0103,[sendTempStr]);
            }
        } else if (deviceMode == '03'){
            //单地面温度控制
            if ((TempStr*10).toString().length==3){
                var sendTempStr = "0" + (TempStr*10);
                sendCmd(gwID,devID,ep,0x0104,[sendTempStr]);
            }else if((TempStr*10).toString().length==2){
                var sendTempStr = "00" + (TempStr*10);
                sendCmd(gwID,devID,ep,0x0104,[sendTempStr]);
            }
        }

    } else{

    }
}
