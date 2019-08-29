initlan();
var attributeValue,endpointStatus,deviceInfo,deviceID,wgID,appID,timer,num = 2;
plus.plusReady(function () {
    plus.gatewayCmd.getCurrentAppID(function(result){
        appID = result;
    });
    plus.gatewayCmd.getDeviceInfo(function (result) {
        deviceInfo = result;
        getMoreConfig(deviceInfo.devID);
        deviceID = result.devID;
        wgID = result.gwID;
        if(result.mode==2){
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        sendCmd_501(wgID,deviceID,appID,1,0x0102,"");
        sendCmd_500(result);
    })
    plus.gatewayCmd.newDataRefresh(function(result){
        if(result.cmd == "500" && result.devID == deviceID){
            sendCmd_500(result);
        }else if(result.cmd == "502" && result.devID == deviceID){
            sendCmd_502(result);
        }
    })
    //设防撤防切换
    $("#button_iffortify").on("click",function () {
        if($(this).val()==device_B9_disarming){
            keepCmd_502(wgID,deviceID,0,1,"0");
            $(this).val(device_B9_fortify);
        }else if($(this).val()==device_B9_fortify){
            keepCmd_502(wgID,deviceID,0,1,"1");
            $(this).val(device_B9_disarming);
        }
    })
})

function sendCmd_501(gwID,devID,appID,endpointNumber,commandId,parameter){
    var jsonData = {};
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = devID;
    jsonData.appID = appID;
    jsonData.endpointNumber = endpointNumber;
    jsonData.clusterId = 0x0201;
    jsonData.commandType = 1;
    jsonData.commandId = commandId;
    if(parameter){
        jsonData.parameter = parameter;
    }
    plus.gatewayCmd.controlDevice(jsonData,function(){})
}

function sendCmd_500(result) {
    switch(result.mode){
        case 2:
            $(".mask_layer").show();
            break;
        case 0:
        case 1:
        case 4:
            if_fortify(result);
            break;
    }
}

//判断是否设防
function if_fortify(result) {
    for(var a in result.endpoints){
        if(result.endpoints[a].endpointNumber==1){
            if(result.endpoints[a].endpointName!==""){
                $(".deviceName").html(result.endpoints[a].endpointName);
            }else{
                $(".deviceName").html("1"+device_B9_way_in);
            }

            var attributeId = result.endpoints[a].clusters[0].attributes;
            var imgState = "";
            for(var i in attributeId){
                if(attributeId[i].attributeId.toString(16)=="8101"){
                    imgState = attributeId[i].attributeValue;
                    if(attributeId[i].attributeValue==1){
                        //开路输入
                        $("#ifOpen_icon").children("img").attr("src","fonts/open.png");
                    }else if(attributeId[i].attributeValue==0){
                        //闭路输入
                        $("#ifOpen_icon").children("img").attr("src","fonts/close.png");
                    }
                }
                if(result.endpoints[a].endpointStatus==1){
                    $("#ifFortify").children("img").attr("src","fonts/fortifying.png");
                    $("#ifFortify").children("span").text(device_B9_fortifying);
                    $("#button_iffortify").val(device_B9_disarming);
                    if(attributeId[i].attributeId.toString(16)=="8102"){
                        if(attributeId[i].attributeValue==1){
                            //报警
                            if(imgState == "1"){
                                $("#ifOpen_icon").children("img").attr("src","fonts/close.png");
                            }else if(imgState == "0"){
                                $("#ifOpen_icon").children("img").attr("src","fonts/open.png");
                            }
                            $("body").css("background-image","url('fonts/fortify-abnormal.png')");
                            $("#ifOpen_icon").children("span").text(device_B9_abnormal);
                            clearInterval(timer);
                            timer = setInterval(beginCircle, 300);
                        }else if(attributeId[i].attributeValue==0){
                            //正常
                            clearInterval(timer);
                            $(".loading i").css("display","none")
                            $("body").css("background-image","url('fonts/fortify-normal.png')");
                            $("#ifOpen_icon").children("span").text(device_B9_normal);
                        }
                    }
                }else if(result.endpoints[i].endpointStatus==0){
                    $("#ifFortify").children("img").attr("src","fonts/fortifyed.png");
                    $("#ifFortify").children("span").text(device_B9_disarmed);
                    $("#button_iffortify").val(device_B9_fortify);
                    $("#ifOpen_icon").children("span").text("");
                    $("body").css("background-image","url('fonts/fortifyed-bj.png')");
                }
            }
        }
    }
}

function keepCmd_502(gwID,devID,mode,endpointNumber,value){
    var dataJson = {
        "cmd":"502",
        "gwID":gwID,
        "mode":mode,
        "devID":devID,
        "endpointNumber":endpointNumber,
        "endpointStatus":value
    };
    plus.gatewayCmd.controlDevice(dataJson,function(){
    })
}

function sendCmd_502(result) {
    if(result.endpointNumber == 1){
        switch(result.mode){
            case 0:
                var status = result.endpointStatus;
                if(status == 0) {
                    $("#ifFortify").children("img").attr("src","fonts/fortifyed.png");
                    $("#ifFortify").children("span").text(device_B9_disarmed);
                    $("#button_iffortify").val(device_B9_fortify);
                    $("body").css("background-image","url('fonts/fortifyed-bj.png')");
                    $("#ifOpen_icon").children("span").text("");
                }else if(status == 1){
                    $("#ifFortify").children("img").attr("src","fonts/fortifying.png");
                    $("#ifFortify").children("span").text(device_B9_fortifying);
                    $("#button_iffortify").val(device_B9_disarming);
                    $("#ifOpen_icon").children("span").text(device_B9_normal);
                    $("body").css("background-image","url('fonts/fortify-normal.png')");
                }
                break;
            case 2:
                $(".deviceName").html(result.endpointName);
                break;
        }
    }

}

function beginCircle() {
    if(num >= 0) {
        $(".loading i").eq(num).css("display","block")
    }
    num = num - 1;
    if(num == -2) {
        num = 2;
        $(".loading i").css("display","none")
    }
}