initlan();
var attributeValue,endpointStatus,deviceInfo,deviceID,wgID,appID,dataArr = [];
$("#output_add").on("click",function () {
    window.location.href = "./add-button.html";
})

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
        sendCmd_501(wgID,deviceID,appID,2,0x0105,"");
        sendCmd_500(result);
        sendCmd518(4,null);
    })

    plus.gatewayCmd.newDataRefresh(function(result){
        if(result.cmd == "500" && result.devID == deviceID){
            sendCmd_500(result);
        } else if(result.cmd == "518" && deviceID == result.devID) {
            if(result.operType == 4) {
                dataArr = result.data;
            } else if(result.operType == 2) {
                for(var i = 0; i < dataArr.length; i++) {
                    var item = dataArr[i];
                    var backItem = result.data[0];
                    if(item.index == backItem.index) {
                        dataArr.splice(i,1);
                    }
                }
            }
            getButtonList(dataArr);
        }else if(result.cmd == "502" && result.devID == deviceID){
            if (result.endpointName != undefined || result.endpointName != '') {
                $(".deviceName").html(result.endpointName);
            }
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
    plus.gatewayCmd.controlDevice(jsonData,function(){
    })
}

function sendCmd518(operType, data) {
    var jsonData = {};
    jsonData.cmd = "518";
    jsonData.gwID = wgID;
    jsonData.devID = deviceID;
    jsonData.operType = operType;
    if(data) {
        jsonData.data = data;
    }
    plus.gatewayCmd.controlDevice(jsonData,function(){
    });
}

function sendCmd_500(result) {
    $(".spinner").css("display","none");
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

function if_fortify(result) {
    for(var a in result.endpoints){
         if(result.endpoints[a].endpointNumber==2){
             if(result.endpoints[a].endpointName!==""){
                 $(".deviceName").html(result.endpoints[a].endpointName);
             }else{
                 $(".deviceName").html("2"+device_B9_way_out);
             }
             var attributeId = result.endpoints[a].clusters[0].attributes;
             for(var a in attributeId){
                 if(attributeId[a].attributeId.toString(16)=="8105"||attributeId[a].attributeId.toString(16)=="8103"){
                     if(attributeId[a].attributeValue==1 || attributeId[a].attributeValue==4){
                         //闭路
                         $("#output_icon").children("img").attr("src","fonts/output-close-icon.png");
                     }else if(attributeId[a].attributeValue==0 || attributeId[a].attributeValue==3){
                         //开路
                         $("#output_icon").children("img").attr("src","fonts/output-open-icon.png");
                     }
                 }
             }
        }
    }
}

function getButtonList(dataArr) {
    var pingjie = "";
    for(var i in dataArr){
        pingjie = pingjie+"<input value="+dataArr[i].name+" type='button' id="+dataArr[i].index+" epData="+dataArr[i].epData+">";
    }
    $("#button_list").html(pingjie);
    var len = $("#button_list").children().length;
    for(var i=0;i<len;i++){
        var domObj = $("#button_list").children().eq(i);
        domObj.on({
            touchstart: function(e) {
                // 长按事件触发
                timeOutEvent = setTimeout(function() {
                    timeOutEvent = 0;
                    $(".edit").attr("id",$(e.target).attr("id"));
                    $(".deleteButton").attr("id",$(e.target).attr("id"));
                    $(".edit").on("click",function () {
                        var nextName = $(e.target).val();
                        var nextIndex = $(e.target).attr("id");
                        var nextEpdata = $(e.target).attr("epData");
                        window.location.href="./add-button.html?name="+nextName+"&index="+nextIndex+"&epdata="+nextEpdata;
                    })
                    $(".deleteButton").on("click",function () {
                        var data = [];
                        var dataDic = {};
                        var indexID = $(e.target).attr("id");
                        var domName = $(e.target).val();
                        var dataDetail = $(e.target).attr("epData");
                        dataDic.index = indexID;
                        dataDic.name = domName;
                        dataDic.epData = dataDetail;
                        data.push(dataDic);
                        sendCmd518(2,data);
                        cancel();
                    })
                    show_list();
                }, 400);
                //长按400毫秒
                // e.preventDefault();
            },
            touchmove: function() {
                clearTimeout(timeOutEvent);
                timeOutEvent = 0;
            },
            touchend: function(e) {
                clearTimeout(timeOutEvent);
                if (timeOutEvent != 0) {
                    sendCommend(e.target);
                }
                return false;
            }
        })
    }
}

function sendCommend(event) {
    /*var index = $(this).attr("id");
    var name = $(this).val();*/
    var epData =[$(event).attr("epData")];
    sendCmd_501(wgID,deviceID,appID,2,0x0103,epData);
    alert(device_B9_sent);
}

function show_list() {
    $("#shade").css("display","block");
    $(".pop_list").css("display","block");
}

function cancel() {
    $("#shade").css("display","none");
    $(".pop_list").css("display","none");
}

