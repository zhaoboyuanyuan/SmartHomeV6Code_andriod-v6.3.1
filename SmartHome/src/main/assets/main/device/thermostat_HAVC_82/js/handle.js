

var devID = '';
var gwID = '';
var ep = 1;
var switchAttributes = [];

var MaxTemperature=32;
var MinTemperature=10;
//////////////
var shownum=0;//显示的数字
var shownumStep=0.5;//显示的数字步进

var selectedMode = "heat";//"heat""cool"默认heat为最小值
var heatTemperatureShowNum;//auto模式下的最小值、制热模式下的设置温度
var coolTemperatureShowNum;//auto模式下的最大值、制冷模式下的设置温度
//////////////
var isNeedHandleTemp = false;
//国际化初始化函数
initlan()

plus.plusReady(function(){
    $(".toSetMask_p").html(lang_Device_82_Details_Front_Settings_Tips3 +"<br>" + lang_Device_82_Details_Front_Settings_Tips4);
    //返回到设备详情页
    $(".back").on("click",function(){
        plus.tools.back(function() {})
    })
    //跳转到更多页面
    $(".more").on("click",function(){
        plus.tools.more(moreConfig, function() {})
        requestThermostatModeInfo();
    })

    $(".contrl_mode").on("click",function () {
        //切换mode
        setThermostatMode();
    })

    $(".contrl_fan").on("click",function () {
        //切换fan
        setThermostatFan();
    })

    $(".contrl_btn").on("click",function () {
        //开关
        setThermostatState();
    })

    ////////////////////////////查询---start////////////////////////////
    //查询
    ////////////////////////////查询---end////////////////////////////
    ////////////////////////////处理数据---start////////////////////////////
    function handleData(data){
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            var endpointNumber = endpoint.endpointNumber;
            var endpointName = endpoint.endpointName;
            var endpointType = endpoint.endpointType;
            var clusters = endpoint.clusters;
            var attributes = clusters[0].attributes;
            for (var j = 0;j < attributes.length;j++){
                var attributeId = attributes[j].attributeId;
                var attributeValue = attributes[j].attributeValue;
                switchAttributes[attributeId] = attributeValue;
                handleDataDepth(attributes[j]);
            }

        }
    }

    function handleDataDepth(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId){
            case 0x8001:
            case 0x8002:{
                updataModelWith(attributeValue);
                refreshUI();
            }
                break;
            case 0x8005:{
                switch (attributeValue){
                    case 129:{
                        //查询状态错误
                    }
                        break;
                    case 130:{
                        //开关设置错误
                    }
                        break;
                    case 131:{
                        //模式设置错误
                    }
                        break;
                    case 132:{
                        //风机设置错误
                    }
                        break;
                    case 133:{
                        //温标设置错误
                    }
                        break;
                    case 134:{
                        //设置系统模式错误
                    }
                        break;
                    case 135:{
                        //时间设置错误
                    }
                        break;
                    case 136:{
                        //温度相关错误
                    }
                        break;
                    case 137:{
                        //声效设置错误
                    }
                        break;
                    case 138:{
                        //紧急制热错误
                    }
                        break;
                    case 139:{
                        //用户编程模式1错误
                    }
                        break;
                    case 140:{
                        //用户编程模式2错误
                    }
                        break;
                    case 141:{
                        //震动设置错误
                    }
                        break;
                }
            }
                break;
        }
    }


    ////////////////////////////处理数据---end////////////////////////////
    ////////////////////////////刷新页面---start////////////////////////////
    function refreshUI() {
        //初次设置
        if ((s_82_systemType == "00") && (s_82_temperatureModeType =="00")){
            $(".toSetMask_layer").show();
        } else {
            $(".toSetMask_layer").hide();
        }
        //onoff
        if (s_82_onOff == "01"){
            $(".contrl_btn").find("i").css("background","url(img/icon_openbtn.png) no-repeat center center")
            $(".contrl_btn").find("i").css("background-size","100% auto");
        } else {
            $(".contrl_btn").find("i").css("background","url(img/icon_closebtn.png) no-repeat center center")
            $(".contrl_btn").find("i").css("background-size","100% auto");
        }
        //mode
        if (s_82_mode =="01"){
            //制热
            $(".smartsection_mode").html(lang_Device_82_Details_Type+"："+lang_Device_82_Details_Type2);
            $(".numshow_mode_change").hide();
        } else if (s_82_mode =="02"){
            //制冷
            $(".smartsection_mode").html(lang_Device_82_Details_Type+"："+lang_Device_82_Details_Type1);
            $(".numshow_mode_change").hide();
        } else if (s_82_mode =="03"){
            //自动
            $(".smartsection_mode").html(lang_Device_82_Details_Type+"："+lang_Device_82_Details_Type3);
            $(".numshow_mode_change").show();
            updateNumshow_mode_change();
        } else {
            $(".numshow_mode_change").hide();
        }
        //fan
        if (s_82_fan == "01"){
            //auto
            $(".smartsection_fan").html(lang_Device_82_Details_Speed+"："+lang_Device_82_Details_Type3);
        } else {
            //开
            $(".smartsection_fan").html(lang_Device_82_Details_Speed+"："+lang_Device_82_Details_Speed1);
        }
        //紧急制热模式
        if (s_82_EmergencyHeatData == "01"){
            $(".smartsection_mode").html(lang_Device_82_Details_Type+"："+lang_Device_82_Details_Heating);
            $(".smartsection_fan").html("");
        }
        //当前温度及温标显示
        if (s_82_temperatureUnit == "00"){
            //摄氏
            $(".contrl_temp").find("p").html(getFixValue(returnFformC(s_82_currentTemperature/10.0)));
            $(".contrl_temp").find("i").css("background","url(img/icon_wenbiao.png) no-repeat center center")
            $(".contrl_temp").find("i").css("background-size","100% auto");
            $(".num_scroll_Unit").css("background","url(img/icon_wenbiao.png) no-repeat center center")
            $(".num_scroll_Unit").css("background-size","100% auto");
            shownumStep = 0.5;
            MaxTemperature = 32;
            MinTemperature = 10;
            updatePickerArry();
        }else {
            $(".contrl_temp").find("p").html(getFixValue(returnFformC(s_82_currentTemperature/10.0)));
            $(".contrl_temp").find("i").css("background","url(img/icon_wenbiao_F.png) no-repeat center center")
            $(".contrl_temp").find("i").css("background-size","100% auto");
            $(".num_scroll_Unit").css("background","url(img/icon_wenbiao_F.png) no-repeat center center")
            $(".num_scroll_Unit").css("background-size","100% auto");
            shownumStep = 1;
            MaxTemperature = 90;
            MinTemperature = 50;
            updatePickerArry();
        }
        //当前设置温度显示
        if (s_82_mode == "01"){
            if (s_82_temperatureUnit == "01"){
                //华氏
                var getNum = getFixValue(s_82_heatTemperature/100.0);
                setShowNum(getFixValue(exChange(getNum)));
                // setShowNum(getFixValue(exChange(s_82_heatTemperature/100.0)));
            }else {
                setShowNum(s_82_heatTemperature/100.0);
            }
        }else if (s_82_mode == "02"){
            if (s_82_temperatureUnit == "01"){
                //华氏
                // $(".num_show").find("p").html(getFixValue(exChange(s_82_coolTemperature/100.0)));
                var getNum = getFixValue(s_82_coolTemperature/100.0);
                setShowNum(getFixValue(exChange(getNum)));
            }else {
                setShowNum(s_82_coolTemperature/100.0);
            }
        }else if (s_82_mode == "03"){
            if (s_82_temperatureUnit == "01"){
                //华氏
                // $(".num_show").find("p").html(getFixValue(exChange(s_82_heatTemperature/100.0)));
                var getNum = getFixValue(s_82_heatTemperature/100.0);
                setShowNum(getFixValue(exChange(getNum)));
                coolTemperatureShowNum = getFixValue(exChange(s_82_autoCoolTemperature/100.0));
                heatTemperatureShowNum = getFixValue(exChange(s_82_autoHeatTemperature/100.0));
                updateNumshow_mode_change();
            }else {
                setShowNum(s_82_heatTemperature/100.0);
                coolTemperatureShowNum = (s_82_autoCoolTemperature/100.0);
                heatTemperatureShowNum = (s_82_autoHeatTemperature/100.0);
                updateNumshow_mode_change();
            }
        }


    }



    ////////////////////////////刷新页面---end////////////////////////////
    ////////////////////////////控制---start////////////////////////////
    //设置开关
    function setThermostatState() {
        if (devID){
            if (s_82_onOff == "00"){
                sendCmd(gwID,devID,ep,1);
            } else if(s_82_onOff == "01"){
                sendCmd(gwID,devID,ep,0);
            } else {
                //当前开关状态异常
            }
        } else{

        }
    }

    //查询基本数据
    function requestThermostatBaseInfo() {
        if (devID){
            sendCmd(gwID,devID,ep,0x8010);
        } else{

        }
    }

    //查询模式数据
    function requestThermostatModeInfo() {
        if (devID){
            sendCmd(gwID,devID,ep,0x8011);
        } else{

        }
    }

    //模式设置
    function setThermostatMode(){
        if (!(s_82_onOff == "01") || ((s_82_temperatureModeType =="01") && (s_82_systemType =="01")) || ((s_82_temperatureModeType == "01") && (s_82_systemType =="02")) || ((s_82_temperatureModeType =="02")&&(s_82_systemType =="01")) || ((s_82_temperatureModeType =="02")&&(s_82_systemType =="02"))){
            //系统关闭时可改&heatOnly及coolOnly的情况 不可更改模式
            return;
        }
        //模式影响设置
        if (s_82_mode =="01") {
            var sendReqStr = "20000000000";
            sendCmd(gwID,devID,ep,0x8012,[sendReqStr]);
        }else if(s_82_mode =="02"){
            var sendReqStr = "30000000000";
            sendCmd(gwID,devID,ep,0x8012,[sendReqStr]);
        }else{
            var sendReqStr = "10000000000";
            sendCmd(gwID,devID,ep,0x8012,[sendReqStr]);
        }
    }

    function setThermostatFan() {
        if (s_82_fan =="01") {
            sendCmd(gwID,devID,ep,0x8013,["2"]);
        }else{
            sendCmd(gwID,devID,ep,0x8013,["1"]);
        }
    }



    ////////////////////////////控制---end////////////////////////////
    plus.gatewayCmd.getDeviceInfo(null, function(data){
        if(data.mode == 2){
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        var name = data.name.indexOf("#$default$#") != -1 ? lang_name_82+data.name.split("#")[2]:data.name
        $(".deviceName").html(name)
        devID = data.devID;
        gwID = data.gwID;
        ep = ~~(data.endpoints[0].endpointNumber)
        getMoreConfig(devID);
        handleData(data);
        requestThermostatBaseInfo();
    })

    plus.gatewayCmdRush.newDataRefresh(function(data){
        if(data.cmd == "500" && devID == data.devID){
            if(data.mode == 2){
                $(".mask_layer").show();
            }else{
                $(".mask_layer").hide();
            }
            var name = data.name.indexOf("#$default$#") != -1 ? lang_name_82+data.name.split("#")[2]:data.name
            $(".deviceName").html(name)
            handleData(data);
        }
    })


})

//
$(".toSetMask_span").on("click",function(){
    window.location.href='HAVCSetting.html?fromMainView';
})

//
$(".numshow_mode_change_max").on("click",function(){
    selectedMode = "cool";
    updateNumshow_mode_change();
})

$(".numshow_mode_change_min").on("click",function(){
    selectedMode = "heat";
    updateNumshow_mode_change();
})

function updateNumshow_mode_change() {
    if(selectedMode == "heat"){
        $(".numshow_mode_change_min").css("color","black");
        $(".numshow_mode_change_max").css("color","#999999");
        if(s_82_temperatureUnit == "01"){
            setShowNum(exChange(s_82_autoHeatTemperature/100.0));
            MinTemperature =  50;
            MaxTemperature =  coolTemperatureShowNum-6;
            updatePickerArry()
        }else {
            setShowNum(s_82_autoHeatTemperature/100.0);
            MinTemperature =  10;
            MaxTemperature =  coolTemperatureShowNum-3;
            updatePickerArry()
        }

    }else {
        $(".numshow_mode_change_max").css("color","black");
        $(".numshow_mode_change_min").css("color","#999999")
        if(s_82_temperatureUnit == "01"){
            setShowNum(exChange(s_82_autoCoolTemperature/100.0));
            MaxTemperature =  90;
            MinTemperature =  heatTemperatureShowNum + 6;
            updatePickerArry()
        }else {
            setShowNum(s_82_autoCoolTemperature/100.0);
            MaxTemperature =  32;
            MinTemperature =  heatTemperatureShowNum + 3;
            updatePickerArry()
        }
    }
}

function setThermostatTemperature(firstNum,secondNum){
    if(s_82_mode == "01"){
        var sendNum;
        if(s_82_temperatureUnit == "01"){
            sendNum = exChange(firstNum)*100;
        }else {
            sendNum = firstNum*100;
        }
        var sendNumStr = "1"+"1"+sendNum+"0000";
        sendCmd(gwID,devID,ep,0x8012,[sendNumStr]);
    }else if(s_82_mode == "02"){
        var sendNum;
        if(s_82_temperatureUnit == "01"){
            sendNum = exChange(firstNum)*100;
        }else{
            sendNum = firstNum*100;
        }
        var sendNumStr = "2"+"1"+"0000"+sendNum;
        sendCmd(gwID,devID,ep,0x8012,[sendNumStr]);
    }else if(s_82_mode == "03"){
        var sendNum1;
        var sendNum2;
        if(s_82_temperatureUnit == "01"){
            sendNum1 = exChange(firstNum)*100;
            sendNum2 = exChange(secondNum)*100;
        }else{
            sendNum1 = firstNum*100;
            sendNum2 = secondNum*100;
        }
        var sendNumStr = "3"+"1"+sendNum1+sendNum2;
        sendCmd(gwID,devID,ep,0x8012,[sendNumStr]);
    }

}

//根据需要把摄氏转化成华氏
function returnFformC(c) {
    if (s_82_temperatureUnit =="01"){
        return  c*1.8+32;
    }else{
        return  c;
    }
}

//根据需要把华氏转化成摄氏
function returnCformF(f) {
    if (s_82_temperatureUnit =="01"){
        return  (f-32)/1.8;
    }else{
        return  f;
    }
}

//根据需要把摄氏下室温显示为(.0/.5)
function getFixValue(valuef){
    //摄氏需要转一下子
    var showValue = valuef;//最后用来显示的温度
    if (s_82_temperatureUnit == "00") {
        var valueH = parseInt(valuef);//取整
        var unit = (valuef > 0)?1:-1;//修复负数现实的问题
        var valuel = valuef - valueH;
        if (valuel*unit > 0 && valuel*unit < 0.5) {
            valuel =0.0;
        }else if(valuel*unit >= 0.5){
            valuel = 0.5;
        }else{
            valuel = 0;
        }
        showValue = valueH +valuel*unit;
    }else {
        var valueH = parseInt(valuef);//取整
        var unit = (valuef > 0)?1:-1;//修复负数现实的问题
        var valuel = valuef - valueH;
        if (valuel*unit > 0 && valuel*unit < 0.5) {
            valuel =0.0;
        }else if(valuel*unit >= 0.5){
            valuel = 1;
        }else{
            valuel = 0;
        }
        showValue = valueH +valuel*unit;
    }
    return showValue;
}