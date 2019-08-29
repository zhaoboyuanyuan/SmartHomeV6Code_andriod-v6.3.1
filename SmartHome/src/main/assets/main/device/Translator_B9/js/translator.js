initlan();
var attributeValue,endpointStatus,deviceInfo,deviceID,wgID,appID;

document.getElementById("back").addEventListener("click", function() {
    plus.tools.back(function() {});
});
document.getElementsByClassName("more")[0].addEventListener("click", function() {
    plus.tools.more(moreConfig, function() {});
});

$("#two_way").on("click",function () {
    window.location.href = "./output.html";
})
//获取设备信息
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
            $(".spinner").css("display","none");
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        var name = result.name.indexOf("#$default$#") != -1 ? device_B9_name + result.name.split("#")[2]:result.name;
        $(".deviceName").html(name);
        sendCmd_501(wgID,deviceID,appID,0,0x0102,"");
        sendCmd_500(result);
    })

    plus.tools.getLoginType(function (data) {
        if (data == "101") {
            $("#mess_detail").hide();
        }else{
            var endTime = new Date().getTime();
            var startTime = endTime - 7*24*60*60*1000;
            var param = {
                "deviceId":deviceID,
                "deviceType":"B9",
                "messageCode":"0102101",
                "dataType":"1",
                "startTime":startTime+"",
                "endTime":endTime+""
            };
            plus.gatewayCmd.getAlarmList(param, function(result){
                if(!result || result == ''){
                    $("#mess_detail ul").append('<li class="">'+device_B9_noData+'</li>');
                    return;
                }
                for(var i=0;i<8;i++){
                    var str = '<li>' +
                        '<span>'+result[i].message+'</span>' +
                        '<span>'+timestampToTime(parseInt(result[i].time))+'</span>' +
                        '</li>';
                    $("#mess_detail ul").append(str);
                }
            })
        }
    });

    plus.gatewayCmd.newDataRefresh(function(result){
        if(result.cmd == "500" && result.devID == deviceID){
            var name = result.name.indexOf("#$default$#") != -1 ? device_B9_name + result.name.split("#")[2]:result.name;
            $(".deviceName").html(name);
            sendCmd_500(result);
        }else if(result.cmd == "502" && result.devID == deviceID){
            sendCmd_502(result)
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

function sendCmd_502(result) {
    $(".spinner").css("display","none");
    var number = result.endpointNumber;
    switch(result.mode){
        case 0:
            if(number==1){
                if(result.endpointStatus==1){
                    $(".spinner").css("display","none");
                    $("#translator_classify>li").children("img").eq(1).attr("src","fonts/fortify-icon.png");
                    $("#translator_classify>li").children("img").eq(0).attr("src","fonts/chefang-icon.png");
                }else if(result.endpointStatus==0){
                    $(".spinner").css("display","none");
                    $("#translator_classify>li").children("img").eq(1).attr("src","fonts/fortify-dark.png");
                    $("#translator_classify>li").children("img").eq(0).attr("src","fonts/chefang-light-icon.png");
                }
            }
            break;
        case 2:
            if(number==1){
                $("#one_way>span").text(result.endpointName);
            }else if(number==2){
                $("#two_way>span").text(result.endpointName);
            }
            break;
    }
}
//判断是否设防
function if_fortify(result) {
    for(var a in result.endpoints){
        if(result.endpoints[a].endpointNumber==1){
            if(result.endpoints[a].endpointName!==""){
                $("#one_way>span").text(result.endpoints[a].endpointName);
            }else{
                $("#one_way>span").text("1"+device_B9_way_in);
            }
            if(result.endpoints[a].endpointStatus==1){
                $(".spinner").css("display","none");
                $("#translator_classify>li").children("img").eq(1).attr("src","fonts/fortify-icon.png");
                $("#translator_classify>li").children("img").eq(0).attr("src","fonts/chefang-icon.png");
            }else if(result.endpoints[a].endpointStatus==0){
                $(".spinner").css("display","none");
                $("#translator_classify>li").children("img").eq(1).attr("src","fonts/fortify-dark.png");
                $("#translator_classify>li").children("img").eq(0).attr("src","fonts/chefang-light-icon.png");
            }else{
                $(".spinner").css("display","none");
                console.log("1111");
                $("#shade").css("display","block");
                $("#pop").css("display","block");
            }
        }else if(result.endpoints[a].endpointNumber==2){
            if(result.endpoints[a].endpointName!==""){
                $("#two_way>span").text(result.endpoints[a].endpointName);
            }else{
                $("#two_way>span").text("2"+device_B9_way_out);
            }
        }
    }
}

//跳输入端详情页
$("#one_way").on("click",function () {
    window.location.href = "./fortify-normal.html";
})
//长按事件
$.fn.longPress = function(fn) {
    var timeout = undefined;
    var $this = this;
    for(var i = 0;i<$this.length;i++){
        $this[i].addEventListener('touchstart', function(event) {
            timeout = setTimeout(fn, 300);
        }, false);
        $this[i].addEventListener('touchend', function(event) {
            clearTimeout(timeout);
        }, false);
    }
}
//调用长按函数
$("#one_way").longPress(function(){
    $("#rename").css("display","block");
    $("#shade").css("display","block");
    $("#rename_content").val($("#one_way>span").text());
    $(".sure").attr("id","fone_way");
})

$("#two_way").longPress(function(){
    $("#rename").css("display","block");
    $("#shade").css("display","block");
    $("#rename_content").val($("#two_way>span").text());
    $(".sure").attr("id","stwo_way");
})
//根据id不同去存取输入输出端的重命名
function sure(ID) {
    if(ID=="fone_way"){
        $("#one_way>span").text($("#rename_content").val());
        keepCmd_502(wgID,deviceID,2,1,$("#rename_content").val());
    }else if(ID=="stwo_way"){
        $("#two_way>span").text($("#rename_content").val());
        keepCmd_502(wgID,deviceID,2,2,$("#rename_content").val());
    }
    cancel();
}
//取消重命名
function cancel() {
    $("#rename").css("display","none");
    $("#shade").css("display","none");
}
function keepCmd_502(gwID,devID,mode,endpointNumber,value){
    var dataJson = {
        "cmd":"502",
        "gwID":gwID,
        "mode":mode,
        "devID":devID,
        "endpointNumber":endpointNumber,
    };
    if(mode == 0){
        dataJson.endpointStatus = value;
    }else if(mode == 2){
        dataJson.endpointName = value;
    }
    plus.gatewayCmd.controlDevice(dataJson,function(){
    })
}

function timestampToTime(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = (date.getDate() < 10 ? '0'+(date.getDate()) : date.getDate()) + ' ';
    h = (date.getHours()< 10 ? '0'+(date.getHours()) : date.getHours())  + ':';
    m = (date.getMinutes()< 10 ? '0'+(date.getMinutes()) : date.getMinutes())+ ':';
    s = (date.getSeconds()< 10 ? '0'+(date.getSeconds()) : date.getSeconds());
    return Y+M+D+h+m+s;
}

$("#getMoreMess").on("click",function(){
    plus.tools.goAlarm(function(){});
});

//超时隐藏
function overtime() {
    $("#shade").css("display","none");
    $("#pop").css("display","none");
}

$("#one_way").children("img").eq(0).on("click",function (e) {
    e.stopPropagation();
    if($(e.target).attr("src")=="fonts/chefang-icon.png"){
        keepCmd_502(wgID,deviceID,0,1,"0");
    }
})

$("#one_way").children("img").eq(1).on("click",function (e) {
    e.stopPropagation();
    if($(e.target).attr("src")=="fonts/fortify-dark.png"){
        keepCmd_502(wgID,deviceID,0,1,"1");
    }
})







