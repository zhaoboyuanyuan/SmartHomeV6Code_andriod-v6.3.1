var dec = null;
var G_DevId = '';
var G_appID;
var G_gwID;
var G_deviceIsShare;//设备是否为被分享的设备
var G_bindDataDic = {};

var G_curEditepnum; //当前编辑的epNum，字符串

/*
* 每个ep的模式信息
* 0x0002; 开关模式
* 0x0103; 绑定模式
* 0x0004; 场景模式
* */
var G_epModeDic = {"1":0x0002,"2":0x0103,"3":0x0004};





initlan();
plus.plusReady(function () {

    //设备详情数据获取
    plus.gatewayCmd.newDataRefresh(function (data) {
        console.log(data)

        if (data.cmd == "500" && G_DevId == data.devID) {
            plus.gatewayCmd.getDeviceInfo('',function (data) {
                makeData(data);
            });
        } else if (data.cmd == "502" && G_DevId == data.devID) {
            if(data.retCode === 0 || !data.retCode){
                if(data.name != undefined || data.name != ''){
                    $(".deviceName").html(data.name)
                }
                var name = data.endpointName;
                if (name == '' || name == undefined || name == null) {
                    name = lang_device_switch + data.endpointNumber;
                }
                $("#name_"+data.endpointNumber).html(name);
            }else if(data.retCode === 1){
                if(data.endpointName){
                    window.showDialog.show(languageUtil.getResource("Device_More_Rename_Error"),3000);
                }
            }
        } else if (data.cmd == "513" && G_DevId == data.devID) {
            //刷新绑定关系
            plus.gatewayCmd.getDeviceInfo('',function (data) {
                makeData(data);
            });
        }
    });
    plus.gatewayCmd.getDeviceInfo("",function (data) {
        console.log(data)
        console.log("getDeviceInfo")

        G_DevId = data.devID;
        G_appID = data.appID;
        G_gwID = data.gwID;
        if (data.mode == "2") {
            $(".mask_layer").show()
        } else {
            $(".mask_layer").hide()
        }
        if(data.isShared == true){
            G_deviceIsShare = true;
            $(".rename i").hide();
        }else{
            G_deviceIsShare = false;
            $(".rename i").show();
        }
        makeData(data);
        var dev = deviceControl(data.gwID, data.devID, 1, data.endpoints[0].clusters[0].clusterId, 2);
        plus.gatewayCmd.controlDevice(dev, function (data) {});
        getMoreConfig(G_DevId);
        $("#back").unbind('click').on("click", function () {
            plus.tools.back(function () {})
        });
        $(".more").unbind('click').on("click", function () {
            plus.tools.more(moreConfig, function () {})
        });
    });

});
function cancel() {
    if ($(".sect_content")) {
        $(".sect_content").remove();
    }
}
function rename(endpointNumber,endpointName){
    var jsonData = {
        "cmd":"502",
        "appID":G_appID,
        "gwID":G_gwID,
        "mode":2,
        "devID":G_DevId,
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
        //解析extData 绑定关系
        if(data.extData){
            var extData = JSON.parse(new Base64().decode(data.extData));
            G_bindDataDic = {};
            if (extData) {
                bindDataArry = extData;
            } else {
                bindDataArry = [];
            }
            for (extDataIndex in extData) {
                var tempExtData = extData[extDataIndex];
                if (tempExtData.endpointNumber) {
                    G_bindDataDic[tempExtData.endpointNumber] = tempExtData;
                }
            }

            console.log("G_bindDataDic")
            console.log(G_bindDataDic)

        }

        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoints = data.endpoints[i];

            //给模式字典赋值
            G_epModeDic['' + endpoints.endpointNumber] = endpoints.endpointType;

            //开关模式解析
            if (endpoints.endpointType && endpoints.endpointType == 0x0002) {
                if (endpoints.endpointNumber == 1 || endpoints.endpointNumber == 2 || endpoints.endpointNumber == 3) {
                    var clusters = endpoints.clusters;
                    for (var k = 0; k < clusters.length; k++) {
                        var attributes = clusters[k].attributes;
                        if (attributes) {
                            for (var j = 0; j < attributes.length; j++) {
                                var attributeId = attributes[j].attributeId;
                                var attributeValue = attributes[j].attributeValue;
                                var name = endpoints.endpointName;
                                if (name == '' || name == undefined || name == null) {
                                    name = lang_device_switch + endpoints.endpointNumber;
                                }
                                var statusStr;
                                if (attributeValue == "0" && attributeId == 0x0000) {
                                    statusStr = "../fonts/icon_close.png";
                                } else if (attributeValue == "1" && attributeId == 0x0000) {
                                    statusStr = "../fonts/icon_open.png"
                                } else {
                                    statusStr = "../fonts/icon_close.png";
                                }

                                switch (attributeId) {
                                    case 0x0000:
                                        //开关
                                        switch (endpoints.endpointNumber) {
                                            case 1:
                                                reloadUI(0, attributeValue, name);
                                                control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                                                break;
                                            case 2:
                                                reloadUI(1, attributeValue, name);
                                                control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                                                break;
                                            case 3:
                                                reloadUI(2, attributeValue, name);
                                                control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
                                                break;
                                        }
                                        break;

                                }

                            }
                        }
                    }
                }
            }

            //绑定模式解析
            if (endpoints.endpointType && endpoints.endpointType == 0x0103) {
                var tempExtData = G_bindDataDic[endpoints.endpointNumber];
                if (isNull(tempExtData)) continue;
                reloadUI(endpoints.endpointNumber - 1, "5", tempExtData.name);
                control("", data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
            }

            //场景模式解析
            if (endpoints.endpointType && endpoints.endpointType == 0x0004) {
                var tempExtData = G_bindDataDic[endpoints.endpointNumber];
                if (isNull(tempExtData)) continue;
                reloadUI(endpoints.endpointNumber - 1, "6", tempExtData.sceneName);
                control("", data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
            }
        }

    }

    var name = data.name.indexOf("#$default$#") != -1 ? device_Bw_name + data.name.split("#")[2] : data.name
    $(".deviceName").html(name)
}

function reloadUI(i, status, name) {
    $(".switch_handle").eq(i).siblings("em").removeClass("rotate").hide()
    $(".switch_handle").eq(i).siblings("i").hide();
    $("#name_"+(i+1)).html(name);
    if (status == "0" || status == "") {  //关闭
        if (G_deviceIsShare) {
            //是被分享的设备，不能修改昵称，绑定了别的设备的按钮也不可修改昵称
            $(".rename i").eq(i).hide();
        } else {
            $(".rename i").eq(i).show();
        }
        $(".socket:eq(" + i + ") span").html(device_Bw_off)
        $(".socket:eq(" + i + ") span").css("color", "gray")
        $(".switch_handle").eq(i).attr("data-state", "off")
        $(".switch_handle").eq(i).attr("src", "../fonts/icon_close.png")

    } else if (status == "1") { //打开
        if (G_deviceIsShare) {
            //是被分享的设备，不能修改昵称，绑定了别的设备的按钮也不可修改昵称
            $(".rename i").eq(i).hide();
        } else {
            $(".rename i").eq(i).show();
        }
        $(".socket:eq(" + i + ") span").html(device_Bw_on)
        $(".socket:eq(" + i + ") span").css("color", "#8dd652")
        $(".switch_handle").eq(i).attr("data-state", "on")
        $(".switch_handle").eq(i).attr("src", "../fonts/icon_open.png")

    } else if (status == "5") { //绑定
        //当前绑定的设备信息
        var bindDataDicTemp = G_bindDataDic[i+1];

        var bindDevID = bindDataDicTemp.bindDevID;
        var bindDevtype = bindDataDicTemp.bindDevtype;

        if (!isNull(bindDevID)){
            $("#name_"+(i+1)).html("");

            $(".switch_handle").eq(i).attr("data-state", "bind")
            $(".switch_handle").eq(i).attr("src", "../fonts/icon_binding.png")

            // $(".switch_handle").eq(i).css('background-color', '#76BB3F')
            // $(".switch_handle").eq(i).css('-webkit-mask', 'url("../fonts/icon_modebinding.png") no-repeat')
            // $(".switch_handle").eq(i).css("mask", "url('../fonts/icon_modebinding.png') no-repeat")
            // $(".switch_handle").eq(i).css("-webkit-mask-size", "50% 50%")
            // $(".switch_handle").eq(i).css("mask-size", "50% 50%")
            // $(".switch_handle").eq(i).css("-webkit-mask-position", "50% 50%")
            // $(".switch_handle").eq(i).css("mask-position", "50% 50%")
            //
            // // $(".switch_handle").eq(i).css('filter','invert(61%) sepia(64%) saturate(426%) hue-rotate(50deg) brightness(125%) contrast(88%)')
            // $(".switch_handle").eq(i).css('padding','3rem 3rem')
            // $(".switch_handle").eq(i).css('box-sizing','border-box')
            // $(".switch_handle").eq(i).parent().css('border','1.5px solid #76BB3F')
            // $(".switch_handle").eq(i).parent().css('border-radius','50%')

            var bindDevName = bindDataDicTemp.name;

            var deviceName = window.deviceList[bindDevtype].name;

            name = bindDevName.indexOf("#$default$#") != -1 ? deviceName + bindDevName.split("#")[2]:bindDevName

            $(".socket:eq(" + i + ") span").html(name)
            $(".socket:eq(" + i + ") span").css("color", "gray")
        } else {
            $("#name_"+(i+1)).html("");

            $(".switch_handle").eq(i).attr("data-state", "addDev")
            $(".switch_handle").eq(i).attr("src", "../fonts/icon_add.png")

            // $(".switch_handle").eq(i).css('background-color', '')
            // $(".switch_handle").eq(i).css('-webkit-mask', '')
            // $(".switch_handle").eq(i).css("mask", "")
            // $(".switch_handle").eq(i).css("-webkit-mask-size", "")
            // $(".switch_handle").eq(i).css("mask-size", "")
            // $(".switch_handle").eq(i).css("-webkit-mask-position", "")
            // $(".switch_handle").eq(i).css("mask-position", "")
            //
            // $(".switch_handle").eq(i).css('filter','none')
            // $(".switch_handle").eq(i).css('padding','3rem 3rem')
            // $(".switch_handle").eq(i).css('box-sizing','border-box')
            // $(".switch_handle").eq(i).parent().css('border','1.5px solid #D2D5D7')
            // $(".switch_handle").eq(i).parent().css('border-radius','50%')

            $(".socket:eq(" + i + ") span").html(bindNoneDevice)
            $(".socket:eq(" + i + ") span").css("color", "gray")
        }
    } else if (status == "6") { //场景
        var bindDataDicTemp = G_bindDataDic[i+1];

        var bindSceneID1 = bindDataDicTemp.sceneID;
        var bindSceneID2 = bindDataDicTemp.sceneID_D;

        if (isNull(bindSceneID1) && isNull(bindSceneID2)){
            $("#name_"+(i+1)).html("");

            $(".switch_handle").eq(i).attr("data-state", "addScene")
            $(".switch_handle").eq(i).attr("src", "../fonts/icon_add.png")

            // $(".switch_handle").eq(i).css('filter','none')
            // $(".switch_handle").eq(i).css('padding','3rem 3rem')
            // $(".switch_handle").eq(i).css('box-sizing','border-box')
            // $(".switch_handle").eq(i).parent().css('border','1.5px solid #D2D5D7')
            // $(".switch_handle").eq(i).parent().css('border-radius','50%')

            $(".socket:eq(" + i + ") span").html(bindNoneScene)
            $(".socket:eq(" + i + ") span").css("color", "gray")
        } else {

            $("#name_"+(i+1)).html("");
            $(".switch_handle").eq(i).attr("data-state", "bind")
            $(".switch_handle").eq(i).attr("src", "../fonts/icon_scene.png")

            // $(".switch_handle").eq(i).css('filter','invert(61%) sepia(64%) saturate(426%) hue-rotate(50deg) brightness(125%) contrast(88%)')
            // $(".switch_handle").eq(i).css('padding','3rem 3rem')
            // $(".switch_handle").eq(i).css('box-sizing','border-box')
            // $(".switch_handle").eq(i).parent().css('border','1.5px solid #76BB3F')
            // $(".switch_handle").eq(i).parent().css('border-radius','50%')

            if (!isNull(bindSceneID1) && !isNull(bindSceneID2)) {
                var name1 = bindDataDicTemp.sceneName.length>2 ? bindDataDicTemp.sceneName.substr(0, 2)+'..' : bindDataDicTemp.sceneName
                var name2 = bindDataDicTemp.sceneName_D.length>2 ? bindDataDicTemp.sceneName_D.substr(0, 2)+'..' : bindDataDicTemp.sceneName_D
                name = name1 + ' / ' + name2
            } else {
                name = bindDataDicTemp.sceneName + bindDataDicTemp.sceneName_D
            }

            $(".socket:eq(" + i + ") span").html(name)
            $(".socket:eq(" + i + ") span").css("color", "gray")

        }
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
        "commandType": 01,
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
    if ($(self).attr("data-state") == "on" || $(self).attr("data-state") == "off"){
        $(self).siblings("em").show().addClass("rotate")
        $(self).siblings("i").show()
        $(self).attr('src', '../fonts/icon_loading.png');
        doTimeout(endpointNumber, statusStr,self)
    }

    if ($(self).attr("data-state") == "bind") {

        showDialog.show(lang_has_been_bound, 3000)

    } else if ($(self).attr("data-state") == "on") {
        dec = deviceControl(gwID, devID, endpointNumber, clusterId, 0)
        plus.gatewayCmd.controlDevice(dec, function (data) {
        })
    } else if ($(self).attr("data-state") == "off") {
        dec = deviceControl(gwID, devID, endpointNumber, clusterId, 1)
        plus.gatewayCmd.controlDevice(dec, function (data) {
        })
    } else if ($(self).attr("data-state") == "addDev") {
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        var info = window.sysfun;
        info.init("device_Bw");
        info.setItem('devID_Bw', devID);
        info.setItem('gwID_Bw', gwID);
        info.setItem('num_Bw', endpointNumber);
        info.setItem('G_bindDataDic', JSON.stringify(G_bindDataDic));

        window.location="choose_other_device.html";
    } else if ($(self).attr("data-state") == "addScene") {
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        var sceneID = ""
        var sceneID_D = ""
        var tempData = G_bindDataDic[endpointNumber]
        if (!isNull(tempData)){
            sceneID = tempData.sceneID
            sceneID_D = tempData.sceneID_D
        }

        window.location="sceneList.html?pageMode=0&epNum="+endpointNumber
            +"&sceneID="+sceneID+"&sceneID_D="+sceneID_D;
    }
}

function doTimeout(endpointNumber, statusStr,self) {
    var timer = setTimeout(function () {
        $(".switch_handle").eq(endpointNumber - 1).siblings("em").removeClass("rotate").hide()
        $(".switch_handle").eq(endpointNumber - 1).siblings("i").hide();
        $(self).parent().siblings("span").css("color", "gray");
        $(self).attr('src', statusStr);
    }, 15000)
}

// 根据按钮模式显示菜单
function menuLayout(mode) {
    switch (mode) {
        case 0: //重置，全部显示
            $('.menuPopup li').each(function () {
                $(this).css('display','block');
                $(this).css('border-radius','');
            });
            break;
        case 0x0002: //开关模式
            $('.menuPopup li').eq(0).css('border-radius','0.6rem 0.6rem 0 0');
            $('.menuPopup li').eq(1).css('display','none');
            $('.menuPopup li').eq(2).css('display','none');
            $('.menuPopup li').eq(3).html(modeSwitch+'<em></em>');
            $('.menuPopup li').eq(3).css('border-radius','0 0 0.6rem 0.6rem');
            $('.menuPopup li').eq(4).css('border-radius','0.6rem 0.6rem 0.6rem 0.6rem');
            break;
        case 0x0103: // 绑定模式
            $('.menuPopup li').eq(0).css('display','none');
            $('.menuPopup li').eq(1).css('border-radius','0.6rem 0.6rem 0 0');
            $('.menuPopup li').eq(2).css('display','none');
            $('.menuPopup li').eq(3).html(modeBind+'<em></em>');
            $('.menuPopup li').eq(3).css('border-radius','0 0 0.6rem 0.6rem');
            if (isNull(G_bindDataDic[G_curEditepnum].bindDevID)) {
                $('.menuPopup li').eq(1).css('display','none');
                $('.menuPopup li').eq(3).css('border-radius','0.6rem 0.6rem 0.6rem 0.6rem');
            }
            $('.menuPopup li').eq(4).css('border-radius','0.6rem 0.6rem 0.6rem 0.6rem');
            break;
        case 0x0004: // 场景模式
            $('.menuPopup li').eq(0).css('display','none');
            $('.menuPopup li').eq(1).css('display','none');
            $('.menuPopup li').eq(2).css('border-radius','0.6rem 0.6rem 0 0');
            $('.menuPopup li').eq(3).html(modeScene+'<em></em>');
            $('.menuPopup li').eq(3).css('border-radius','0 0 0.6rem 0.6rem');
            if (isNull(G_bindDataDic[G_curEditepnum].sceneID) && isNull(G_bindDataDic[G_curEditepnum].sceneID_D)) {
                $('.menuPopup li').eq(2).css('display','none');
                $('.menuPopup li').eq(3).css('border-radius','0.6rem 0.6rem 0.6rem 0.6rem');
            }
            $('.menuPopup li').eq(4).css('border-radius','0.6rem 0.6rem 0.6rem 0.6rem');
            break;
    }


}


/***********UI绑定事件************/
//菜单蒙版点击
$('.menuPopup div').on('click',function () {
    //隐藏弹窗
    $('.menuPopup').css('display','none');
    menuLayout(0);
});

//取消按钮
$('.menuPopup li').eq(4).on('click', function () {
    $('.menuPopup').css('display','none');
    menuLayout(0);
});

// 三个编辑按钮
$(".rename i").on("click", function () {
    //显示弹窗
    $('.menuPopup').css('display','block');

    G_curEditepnum = $(this).attr("id");

    var curEpMode = G_epModeDic[G_curEditepnum];

    menuLayout(curEpMode);
});

//重命名按钮
$('.menuPopup li').eq(0).on('click', function () {

    editPopup({
        "num": 5,
        "titTxt": lang_device_rename,
        "placeTxt": lang_device_input_name,
        "isClick": 2,
        "aTxt1": lang_device_cancal,
        "aTxt2": lang_device_sure
    })
    $(".inputDiv i").on("click", function () {
        $(this).siblings("input").val("")
        $(this).siblings("input").attr("placeholder", lang_device_input_name)
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

                rename(parseInt(G_curEditepnum), $(".popup input").val())
                cancel()

            }
        }
    })
});

//模式切换按钮
$('.menuPopup li').eq(3).on('click', function () {
    var allUrl="changemode.html?devID="+G_DevId
        +"&epNum="+G_curEditepnum
        +"&epMode="+G_epModeDic[G_curEditepnum]
        +"&epbindType="+G_bindDataDic[G_curEditepnum].bindDevtype;

    window.location=allUrl;
});

// 解除绑定按钮
$('.menuPopup li').eq(1).on('click', function () {

    var type = G_bindDataDic[G_curEditepnum].bindDevtype

    if (type == 'Ar' || type == '65' || type == '80' || type == 'Au') {
        editPopup({
            "num": 2,
            "titTxt": "",
            "txt": switch_Ao_03,
            "placeTxt": "",
            "isClick": 2,
            "aTxt1": lang_device_cancal,
            "aTxt2": lang_device_sure
        })

        $(".sect_content span").css('color','black')

        $(".alertBtn a").on("click", function () {
            var index = $(this).index()
            if (index == 0) {
                //取消
                cancel()
            } else {
                //确定
                cancel()
                var jsonData = {
                    "cmd": "501",
                    "gwID": G_gwID,
                    "devID": G_DevId,
                    "endpointNumber": 0,
                    "commandType": 1,
                    "commandId": 0x8012,
                    "parameter": ['0']
                };

                plus.gatewayCmd.controlDevice(jsonData, function(result) {
                    console.log("result " + result);
                })
            }
        })

    } else {
        var jsonData = {
            "cmd": "501",
            "gwID": G_gwID,
            "devID": G_DevId,
            "endpointNumber": parseInt(G_curEditepnum),
            "commandType": 1,
            "commandId": 0x8012,
            "parameter": [G_curEditepnum]
        };

        plus.gatewayCmd.controlDevice(jsonData, function(result) {
            console.log("result " + result);
        })
    }

});

// 编辑按钮
$('.menuPopup li').eq(2).on('click', function () {
    var curEpMode = G_epModeDic[G_curEditepnum];
    switch (curEpMode) {
        case 0: //重置，全部显示

            break;
        case 0x0002: //开关模式

            break;
        case 0x0103: // 绑定模式
            var info = window.sysfun;
            info.init("device_Bw");
            info.setItem('devID_Bw', G_DevId);
            info.setItem('gwID_Bw', G_gwID);
            info.setItem('num_Bw', G_curEditepnum);
            info.setItem('G_bindDataDic', JSON.stringify(G_bindDataDic));

            window.location="choose_other_device.html";
            break;
        case 0x0004: // 场景模式
            var sceneID = ""
            var sceneID_D = ""
            var tempData = G_bindDataDic[G_curEditepnum]
            if (!isNull(tempData)){
                sceneID = tempData.sceneID
                sceneID_D = tempData.sceneID_D
            }

            window.location="sceneList.html?pageMode=0&epNum="+G_curEditepnum
                +"&sceneID="+sceneID+"&sceneID_D="+sceneID_D;
            break;
    }
});