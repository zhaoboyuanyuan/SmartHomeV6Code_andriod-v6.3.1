var dec = null;
var dId = '';
var appID;
var gwID;
var timer1;
var timer2;
var timer3;

initlan();
plus.plusReady(function () {
    //设备详情数据获取
    plus.gatewayCmd.newDataRefresh(function (data) {
        if (data.cmd == "500" && dId == data.devID) {
            makeData(data);
        } else if (data.cmd == "502" && dId == data.devID) {
            if(data.retCode === 0 || !data.retCode){
                if (data.name != undefined || data.name != '') {
                    $(".deviceName").html(data.name)
                }
                var name = data.endpointName;
                if (name == '' || name == undefined || name == null) {
                    name = device_switch + data.endpointNumber;
                }
                $("#name_"+data.endpointNumber).html(name);
            }else if(data.retCode === 1){
                if(data.endpointName){
                    window.showDialog.show(languageUtil.getResource("Device_More_Rename_Error"),3000);
                }
            }
        }
    });
    plus.gatewayCmd.getDeviceInfo(function (data) {
        dId = data.devID;
        appID = data.appID;
        gwID = data.gwID;
        if (data.mode == "2") {
            $(".mask_layer").show()
        } else {
            $(".mask_layer").hide()
        }
        if(data.isShared == true){
            $(".rename i").hide();
        }else{
            $(".rename i").show();
        }
        makeData(data);
        var dev = deviceControl(data.gwID, data.devID, 1, data.endpoints[0].clusters[0].clusterId, 2);
        plus.gatewayCmd.controlDevice(dev, function (data) {});
        getMoreConfig(dId);
        $("#back").on("click", function () {
            plus.tools.back(function () {})
        });
        $(".more").on("click", function () {
            plus.tools.more(moreConfig, function () {})
        })
    });
    $(".rename i").on("click", function () {
        var $this = $(this);
        editPopup({
            "num": 5,
            "titTxt": device_rename,
            "placeTxt": device_input_name,
            "isClick": 2,
            "aTxt1": device_cancal,
            "aTxt2": device_sure
        })
        $(".inputDiv i").on("click", function () {
            $(this).siblings("input").val("")
            $(this).siblings("input").attr("placeholder", device_input_name)
        });
        $(".popup input").on("blur",function(){
            var value = $(this).val();
            var valueFace = noFace(value);
            console.log(valueFace);
            $(this).val(valueFace);
        });
        $(".alertBtn a").on("click", function () {
            var index = $(this).index()
            if (index == 0) {
                cancel()
            } else {
                if($(".popup input").val().trim() != "") {
                    if ($this.attr("id") == "1") {
                        rename(1, $(".popup input").val())
                        //$("#name_1").html($(".popup input").val())
                        cancel()
                    } else if ($this.attr("id") == "2") {
                        rename(2, $(".popup input").val())
                        //$("#name_2").html($(".popup input").val())
                        cancel()
                    } else {
                        rename(3, $(".popup input").val())
                        //$("#name_3").html($(".popup input").val())
                        cancel()
                    }
                }
            }
        })
    })
});
function cancel() {
    if ($(".sect_content")) {
        $(".sect_content").remove();
    }
}
function rename(endpointNumber,endpointName){
    var jsonData = {
        "cmd":"502",
        "appID":appID,
        "gwID":gwID,
        "mode":2,
        "devID":dId,
        "endpointNumber":endpointNumber,
        "endpointName":endpointName
    };
    plus.gatewayCmd.controlDevice(jsonData,function(){})
}
function makeData(data) {
    if (data.mode == "2") {
        $(".mask_layer").show()
    } else {
        $(".mask_layer").hide()
        // $(".deviceName").html(data.name.indexOf("#$default$#")!= -1?"墙面插座"+data.name.split("#")[2]:data.name)
        $.each(data.endpoints, function (i, endpoints) {
            var status = endpoints.clusters[0].attributes[0].attributeValue;
            var name = endpoints.endpointName;
            if (name == '' || name == undefined || name == null) {
                name = device_switch + endpoints.endpointNumber;
            }
            var statusStr;
            if (status == "0") {
                statusStr = "../fonts/icon_close.png";
            } else if (status == "1") {
                statusStr = "../fonts/icon_open.png"
            }
            switch (endpoints.endpointNumber) {
                case 1:
                    reloadUI(0, status, name);
                    control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                    //关闭超时1
                    clearInterval(timer1);
                    break;
                case 2:
                    reloadUI(1, status, name);
                    control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                    //关闭超时2
                    clearInterval(timer2);
                    break;
                case 3:
                    reloadUI(2, status, name);
                    control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                    //关闭超时3
                    clearInterval(timer3);
                    break;
            }
        })
    }
    var name = data.name.indexOf("#$default$#") != -1 ? lang_socket_01 + data.name.split("#")[2] : data.name
    $(".deviceName").html(name)
}

function reloadUI(i, status, name) {
    $(".switch_handle").eq(i).siblings("em").removeClass("rotate").hide()
    $(".switch_handle").eq(i).siblings("i").hide();
    $("#name_"+(i+1)).html(name);
    if (status == "0") {
        $(".socket:eq(" + i + ") span").html(lang_socket_close)
        $(".socket:eq(" + i + ") span").css("color", "gray")
        $(".switch_handle").eq(i).attr("data-state", "off")
        $(".switch_handle").eq(i).attr("src", "../fonts/icon_close.png")
    } else if (status == "1") {
        $(".socket:eq(" + i + ") span").html(lang_socket_open)
        $(".socket:eq(" + i + ") span").css("color", "#8dd652")
        $(".switch_handle").eq(i).attr("data-state", "on")
        $(".switch_handle").eq(i).attr("src", "../fonts/icon_open.png")
    }
}

//设备控制
function deviceControl(gwID, devID, endpointNumber, clusterId, state) {
    var deviceData = {
        "cmd": "501",
        "gwID": gwID,
        "devID": devID,
        "endpointNumber": endpointNumber,
        "clusterId": 6,
        "commandType": 1,
        "commandId": state
    }
    return deviceData;
}

function control(statusStr, gwID, devID, endpointNumber, clusterId) {
    $(".switch_handle").eq(endpointNumber - 1).off("click")
    $(".switch_handle").eq(endpointNumber - 1).on("click", function () {
        var self = $(this)
        doClick(self, gwID, devID, endpointNumber, clusterId, statusStr)
    })
}

function doClick(self, gwID, devID, endpointNumber, clusterId, statusStr) {
    $(self).siblings("em").show().addClass("rotate")
    $(self).siblings("i").show()
    $(self).attr('src', '../fonts/icon_loading.png');
    //创建超时
    doTimeout(endpointNumber, statusStr,self)
    if ($(self).attr("data-state") == "on") {
        dec = deviceControl(gwID, devID, endpointNumber, clusterId, 0)
        plus.gatewayCmd.controlDevice(dec, function (data) {
        })
    } else {
        dec = deviceControl(gwID, devID, endpointNumber, clusterId, 1)
        plus.gatewayCmd.controlDevice(dec, function (data) {
        })
    }
}

function doTimeout(endpointNumber, statusStr,self) {
    var timer = setTimeout(function () {
        $(".switch_handle").eq(endpointNumber - 1).siblings("em").removeClass("rotate").hide()
        $(".switch_handle").eq(endpointNumber - 1).siblings("i").hide();
        $(self).parent().siblings("span").css("color", "gray");
        $(self).attr('src', statusStr);
    }, 15000);
    switch (endpointNumber) {
        case 1:
        {
            timer1 = timer;
        }
        break;
        case 2:
        {
            timer2 = timer;
        }
            break;
        case 3:
        {
            timer3 = timer;
        }
            break;
    }
}