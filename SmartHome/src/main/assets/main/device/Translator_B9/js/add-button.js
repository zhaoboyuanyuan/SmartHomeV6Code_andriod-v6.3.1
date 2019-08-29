initlan();
var deviceInfo,deviceID,wgID,appID,editName,editIndex,editEpdata,picker,picker2,defaultTime_one=10,defaultOpear_one=2;

plus.plusReady(function(){
    editName = decodeURI(getUrlParam("name"));
    editIndex = decodeURI(getUrlParam("index"));
    editEpdata = decodeURI(getUrlParam("epdata"));
    $("#button_name").val(editName);
    if(editName!==""){
        var opearname;
        $("#fuxuan").prop("checked",true);
        $("#line_action").show();
        if(editEpdata.substr(0,1)=="0"||editEpdata.substr(0,1)=="1"){
            if(editEpdata.substr(0,1)==0){
                defaultOpear_one = 0;
            }else if(editEpdata.substr(0,1)==1){
                defaultOpear_one = 1;
            }
            opearname = getOpearName(defaultOpear_one);
            $("#action").html(opearname);
            $("#time").text(editEpdata.substr(1)+"s");
        }else{
            if(editEpdata.substr(0,1)==3){
                defaultOpear_one = 3;
            }else if(editEpdata.substr(0,1)==4){
                defaultOpear_one = 4;
            }
            opearname = getOpearName(defaultOpear_one);
            $("#action").html(opearname);
            //$()塞时间
            $("#time").text(editEpdata.substr(1)+"s");
            $("#closeTime").show();
        }
    }
    plus.gatewayCmd.getCurrentAppID(function(result){
        appID = result;
    });
    plus.gatewayCmd.getDeviceInfo(function (result) {
        deviceInfo = result;
        getMoreConfig(deviceInfo.devID);
        deviceID = result.devID;
        wgID = result.gwID;
    })

    plus.gatewayCmd.newDataRefresh(function(result){
        if(result.cmd == "518" && deviceID == result.devID) {
            if(result.operType == 1 || result.operType == 3) {
                window.location = "./output.html";
            }
        }
    })
})

$("#keep").on("click",function () {
    var action = defaultOpear_one;
    var time = "";
    if(action==2){
        time = "00";
        action=1;
    } else if(action=="0"||action=="1"){
        time = "00";
    }else if(action=="3"||action=="4"){
        time = defaultTime_one;
    }
    if($("#button_name").val()==""){
        alert(device_B9_enter_name);
    }else{
        var data = [];
        var dataObj = {};
        dataObj.name = $("#button_name").val();
        dataObj.epData = action + time;
        if(editIndex!==""){
            dataObj.index = editIndex;
            data.push(dataObj);
            send518(3,data);
        }else{
            data.push(dataObj);
            send518(1,data);
        }
    }
})

function send518(operType, data) {
    var jsonData = {};
    jsonData.cmd = "518";
    jsonData.gwID = wgID;
    jsonData.devID = deviceID;
    jsonData.operType = operType;
    jsonData.data = data;
    plus.gatewayCmd.controlDevice(jsonData,function(){
    })

}

$("#fuxuan").on("click",function () {
    if($("#fuxuan").prop("checked")==true){
        $("#line_action").show();
        if(defaultOpear_one=="3"||defaultOpear_one=="4"){
            $("#closeTime").show();
        }else{
            $("#closeTime").hide();
        }
    }else{
        $("#line_action").hide();
        $("#closeTime").hide();
    }
})

$("#action").on("click",function () {
    var _this = $(this);
    if(!picker){
        picker = new mui.PopPicker({
            layer: 1
        });
        picker.setData([{
            value: "1",
            text: device_B9_action_close
        },{
            value: "0",
            text: device_B9_action_open
        },{
            value: "3",
            text: device_B9_action_open_close
        },{
            value: "4",
            text: device_B9_action_close_open
        }]);
        $(".mui-poppicker-btn-cancel").html(device_B9_cancel);
        $(".mui-poppicker-btn-ok").html(device_B9_ok);
        $(".mui-poppicker-clear").html(lang_A2_action);
    }

    var index = getOpearIndex(defaultOpear_one);
    picker.pickers[0].setSelectedIndex(index);
    picker.show(function(item) {
        console.log(item);
        var windStr = item[0].text;
        var windSend = item[0].value;
        $(_this).html(windStr);
        defaultOpear_one = windSend;
        if(windSend == "3" || windSend == "4") {
            if(windSend == "4") {
                $("#qhTime").html(device_B9_close_time);
            } else {
                $("#qhTime").html(device_B9_open_time);
            }
            $("#closeTime").show();
        } else {
            $("#closeTime").hide();
        }
    });
})

function getOpearIndex(opearValue) {
    var index;
    if(opearValue == 0) {
        index = 1;
    } else if(opearValue == 1 || opearValue == 2){
        index = 0;
    } else if(opearValue == 3){
        index = 3;
    } else if(opearValue == 4){
        index = 2;
    }
    return index;
}

function getOpearName (opearType) {

    var operaName ;
    if(opearType == 0) {
        operaName = device_B9_action_open;
    } else if(opearType == 1){
        operaName = device_B9_action_close;
    } else if(opearType == 3){
        operaName = device_B9_action_close_open;
    } else if(opearType == 4){
        operaName = device_B9_action_open_close;
    }
    return operaName;
}

$("#time").on("click",function(){
    var _this = $(this);
    if(!picker2){
        picker2 = new mui.PopPicker({
            layer: 1
        });
        picker2.setData(getArr(0,99));
        picker2.body.setAttribute('id', 'timePicker');
        $(".mui-backdrop").css("opacity", "0");
        $(".mui-poppicker-btn-cancel").html(device_B9_cancel);
        $(".mui-poppicker-btn-ok").html(device_B9_ok);
        $("#timePicker .mui-picker").eq(0).find(".mui-pciker-rule-ft").html(device_B9_s);
    }
    picker2.pickers[0].setSelectedIndex(defaultTime_one);
    $("#timePicker .mui-picker").eq(0).find(".mui-pciker-rule-ft").html(device_B9_s);
    picker2.show(function(item) {
        console.log(item);
        var windStr = item[0].text;
        defaultTime_one = windStr;
        $(_this).html(windStr + "s");
    });
});

function getArr(start, end) {
    var arr = [];
    for (var i = start; i <= end; i++) {
        if (i < 10) {
            i = "0" + i;
        }
        arr.push({text:"" + i})
    }
    return arr;
}

