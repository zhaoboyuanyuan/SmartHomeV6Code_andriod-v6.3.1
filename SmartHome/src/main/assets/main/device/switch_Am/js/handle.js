/**
 * Created by Veev on 2017/7/13.
 */
var switchMode = [];
var switchName = [];
var switchExtdata = [];
var switchAttributes = [];
var switchState = [1,1,1]; // 开关状态
var jsonData;
var chooseEp; // 选择的端点
var G_deviceIsShare;//设备是否为被分享的设备
initlan();
initMui();
var info = window.sysfun;
info.init("deviceAm");
plus.plusReady(function () {
    initEvent()
    plus.gatewayCmd.getCurrentAppID(function(result) {
        appID = result;
    });
    plus.gatewayCmd.getDeviceInfo(function (data) {
        gwID = data.gwID;
        devID = data.devID;
        getMoreConfig(devID);
        reloadUI(data)
        sendCmd(gwID,devID,0,0x02,["0"])
        // contorl(data.gwID,data.devID,data.endpoints[0].endpointNumber)
        if(data.isShared == true){
            G_deviceIsShare = true;
        }else{
            G_deviceIsShare = false;
        }
    });
    plus.gatewayCmd.newDataRefresh(function(data){
        if(data.cmd == "500"/* && data.devID == devID*/){
            reloadUI(data)
        }else if(data.cmd == "502"/* && data.devID == devID*/){
            if(data.endpointName != undefined || data.endpointName != ''){
                switchName[data.endpointNumber] = data.endpointName;
                updateName()
            }
        } else if (data.cmd == "513") {
            var ep = ~~(data.data[0].endpointNumber)
            if (data.devID == devID) {
                sendCmd(gwID,devID,ep,0x02,["0"])
            }
        }
    })
});

function sendCmd(gwID,dID,endpointNumber,commandId,parameter){
    var jsonData = {}
    jsonData.cmd = "501";
    jsonData.gwID = gwID;
    jsonData.devID = dID;
    jsonData.endpointNumber = endpointNumber;
    jsonData.commandType = 1;
    jsonData.commandId = commandId;
    if(parameter){
        jsonData.parameter = parameter;
    }
    plus.gatewayCmd.controlDevice(jsonData,function(){})
}

function reloadUI(data) {
    if (data.mode == 2) {
        $(".mask_layer").show();
    } else {
        $(".mask_layer").hide();
        editHtml(data)
    }
    var name = data.name.indexOf("#$default$#") != -1 ? lang_SwitchAnTitle + data.name.split("#")[2] : data.name
    $(".deviceName").html(name)
    devID = data.devID;
    info.setItem("deviceID", devID);
    info.setItem("gwID", data.gwID);
    jsonData = getMoreConfig(devID);
}

function initMui() {
    mui.init({
        gestureConfig: {
            longtap: true, //默认为false
            swiperight: true
        }
    });
}

function editHtml(data) {
    if (data.extData != undefined && data.extData != null && data.extData != '') {
        var mData = JSON.parse(new Base64().decode(data.extData));
        for (var i = 0; i < mData.length; i++) {
            var extData = mData[i]
            // var bindDevEP = extData.bindDevEP;
            // var bindDevID = extData.bindDevID;
            // var bindDevtype = extData.bindDevtype;
            var endpointNumber = extData.endpointNumber;
            // var epName = extData.epName;
            // var epNammodee = extData.mode;
            // var name = extData.name;
            // var sceneID = extData.sceneID;

            switchExtdata[endpointNumber] = extData;
        }
    }
    for (var i = 0; i < data.endpoints.length; i++) {
        var endpoint = data.endpoints[i];
        var endpointNumber = endpoint.endpointNumber;
        var endpointName = endpoint.endpointName;
        var endpointType = endpoint.endpointType;

        switchMode[endpointNumber] = endpointType;
        switchName[endpointNumber] = getSwitchName(endpointType, endpointName, endpointNumber);

        var clusters = endpoint.clusters;
        var attributes = clusters[0].attributes;
        switchAttributes[endpointNumber] = attributes[0];
    }
    updateMode();
    updateName();
    updateImg();
}

function updateMode() {
    $("#modeName1").html(getModeName(switchMode[1]));
}

function updateImg() {
    // todo 后续这里 id 改掉
    for (var i = 1; i <= 2; i++) {
        $('#switch' + i).attr('class', 'switchOneImage');
        switch (switchMode[i]) {
            case 0x0002:
                if (switchAttributes[i] && switchAttributes[i].attributeId == 0 && switchAttributes[i].attributeValue == "1") {
                    // $('#switch' + i).addClass('switchOpen');
                    $('#switch' + i).css({
                        'border-color': '#8dd652',
                        'background':'url("fonts/switch_open.png") no-repeat center #fff',
                        'background-size':'4.5rem auto'
                    });
                    switchState[i] = 0;
                } else {
                    // $('#switch' + i).addClass('switchClose');
                    $('#switch' + i).css({
                        'border-color': '#c9cfdb',
                        'background':'url("fonts/switch_close.png") no-repeat center #fff',
                        'background-size':'4.5rem auto'
                    });
                    switchState[i] = 1;
                }
                break;
            case 0x0103:
                if(switchExtdata[i]) {
                    var bindDevtype = (switchExtdata[i].bindDevtype) || '';
                    // $('#switch' + i).addClass('bindDevice' + bindDevtype);
                    var icon = 'add.png';
                    var color = '#c9cfdb';
                    if (bindDevtype == 'Am') {
                        icon = 'icon_am.png';
                        color = "#8dd652";
                    } else if (bindDevtype == "An") {
                        icon = 'icon_an.png';
                        color = "#8dd652";
                    } else if (bindDevtype == "Ao") {

                        icon = 'icon_ao.png';
                        color = "#8dd652";
                    }
                    $('#switch' + i).css({
                        'border-color': color,
                        'background':'url("fonts/' + icon + '") no-repeat center #fff',
                        'background-size':'4.5rem auto'
                    });
                } else {
                    // $('#switch' + i).addClass('bindDevice');
                    $('#switch' + i).css({
                        'border-color': '#c9cfdb',
                        'background':'url("fonts/add.png") no-repeat center #fff',
                        'background-size':'4.5rem auto'
                    });
                }
                break;
            case 0x0004:
                if(switchExtdata[i]) {
                    var sceneIcon = (switchExtdata[i].sceneIcon) || '01';
                    if (switchExtdata[i] == undefined
                        || switchExtdata[i].sceneIcon == undefined
                        || switchExtdata[i].sceneIcon == null
                        || switchExtdata[i].sceneIcon == ""
                        || switchExtdata[i].sceneIcon == -1) {
                        // $('#switch' + i).addClass('bindNone');
                        $('#switch' + i).css({
                            'border-color': '#c9cfdb',
                            'background':'url("fonts/add.png") no-repeat center #fff',
                            'background-size':'4.5rem auto'
                        });
                    } else {
                        $('#switch' + i).css({
                            'border-color': '#8dd652',
                            'background':'url(../../source/sceneIcon/scene_normal_pre_'+ sceneIcon +'.png) no-repeat center #fff',
                            'background-size':'4.5rem auto'
                        });
                    }
                } else {
                    // $('#switch' + i).addClass('bindNone');
                    $('#switch' + i).css({
                        'border-color': '#c9cfdb',
                        'background':'url("fonts/add.png") no-repeat center #fff',
                        'background-size':'4.5rem auto'
                    });
                }
                break;
            default:
                $('#switch' + i).addClass('switchClose');
                break;
        }
    }
}

function getModeName(type) {
    switch (type) {
        case 0x0002:
            return languageUtil.getResource("modeSwitch");
        case 0x0103:
            return languageUtil.getResource("modeBind");
        case 0x0004:
            return languageUtil.getResource("modeScene");
        default:
            return languageUtil.getResource("modeSwitch");
    }
}

function getSwitchName(type, endpointName, endpointNumber) {
    switch (type) {
        case 0x0002:
            var name = endpointName || ((endpointName == '' || endpointName == null) ? lang_switch + endpointNumber : endpointName);
            return (name);
        case 0x0103:
            var name2 = bindNoneDevice;
            if (switchExtdata[endpointNumber] == undefined || switchExtdata[endpointNumber].name == undefined || switchExtdata[endpointNumber].name == null || switchExtdata[endpointNumber].name == "") {
                return name2;
            }
            var bindName = switchExtdata[endpointNumber].name;
            switch (switchExtdata[endpointNumber].bindDevtype) {
                case "Am":
                    var name2 = bindName.indexOf("#$default$#") != -1 ? lang_name_am + bindName.split("#")[2] : bindName
                    break;
                case "An":
                    var name2 = bindName.indexOf("#$default$#") != -1 ? lang_name_an + bindName.split("#")[2] : bindName
                    break;
                case "Ao":
                    var name2 = bindName.indexOf("#$default$#") != -1 ? lang_name_ao + bindName.split("#")[2] : bindName
                    break;
            }
            return name2;
        case 0x0004:
            if (switchExtdata[endpointNumber] == undefined
                || switchExtdata[endpointNumber].sceneID == undefined
                || switchExtdata[endpointNumber].sceneID == null
                || switchExtdata[endpointNumber].sceneID == ""
                || switchExtdata[endpointNumber].sceneID == -1) {
                return bindNoneScene;
            }
            return (switchExtdata[endpointNumber].sceneName) || bindNoneScene;
        default:
            return lang_switch + '1';
    }
}

function updateName() {
    for (var i = 1; i <= 2; i++) {
        var name = switchName[i];
        if (name == undefined || name == null) {
            name = lang_switch + i;
        }
        if (name.length > 9) {
            name = name.substr(0, 9) + '...'
        }
        $("#switchName" + i).html(name);
    }
}

function initEvent() {
    $("#switchName1").on("longtap", function () {
        // showPupWindow(switchMode[1]);
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        jump1()
    });

    $("#switchName2").on("longtap", function () {
        // showPupWindow(switchMode[2]);
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        jump2()
    });

    $("#switch1").on("longtap", function () {
        // showPupWindow(switchMode[2]);
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        jump1()

    });
    $("#switch1").on("click", function () {
        // showPupWindow(switchMode[2]);
        click1()
    });
    $("#switch2").on("longtap", function () {
        // showPupWindow(switchMode[2]);
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
        jump2()
    });
    $("#switch2").on("click", function () {
        // showPupWindow(switchMode[2]);
        click2()
    });

    $(".pupCancel").on("click", function () {
        $(".pupWindow").hide();
    });
    $("#pupReName").on("click", function () {
        $(".pupWindow").hide();
        $("#dialogRename").show();
    });
    $("#reBind").on("click", function () {
        // UnBindingDev(chooseEp);
        $(".pupWindow").hide();
        window.location = "bindDevice.html";
    });
    $("#unBind").on("click", function () {
        $(".pupWindow").hide();
        $("#dialogUnBind").show();
    });
    $("#editScene").on("click", function () {
        // unBindScene(chooseEp);
        $(".pupWindow").hide();
        var sceneID = '';
        if (switchExtdata[chooseEp]) {
            if (switchExtdata[chooseEp].sceneID) {
                sceneID = switchExtdata[chooseEp].sceneID;
            }
        }
        window.location="sceneList.html?pageMode=0&epNum="+chooseEp
            +"&sceneID="+ sceneID;
    });
    $("#deleteScene").on("click", function () {
        $(".pupWindow").hide();
        $("#dialogDeleteScene").show();
    });
    $("#modeName1").on("click", function () {
        // info.setItem("endpointNumber", 1);
        // window.location = "bindDevice.html";
        if(G_deviceIsShare == false) {
            window.location="../switch_Am/changemode.html?devID="+devID
                +"&epNum="+1
                +"&epMode="+switchMode[1];
        } else {
            window.showDialog.show(Share_No_Permission,3000);
        }

    });
    //返回到设备详情页
    $(".back").on("click", function () {
        plus.tools.back(function () {
        })
    });
//跳转到更多页面
    $(".more").on("click", function () {
        
    });

}

function jump1() {
    info.setItem("endpointNumber", 1);
    chooseEp = 1;
    showPupWindow(switchMode[1]);
}

function jump2() {
    info.setItem("endpointNumber", 2);
    chooseEp = 2;
    showPupWindow(switchMode[2]);
}

function click1() {
    if(switchMode[1] != 0x0002) {
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
    }
    info.setItem("endpointNumber", 1);
    switch(switchMode[1]) {
        case 0x0002:
            toogleSwitch(1, switchState[1]);
            break;
        case 0x0103:
            if(switchExtdata[1]) {
                var bindDevtype = (switchExtdata[1].bindDevtype);
                if (bindDevtype == null || bindDevtype == undefined || bindDevtype == '') {
                    window.location = "bindDevice.html";
                }
            }
            break;
        case 0x0004:
            if(switchExtdata[1]) {
                var sceneID = (switchExtdata[1].sceneID);
                if (sceneID == null || sceneID == undefined || sceneID == '') {
                    window.location="sceneList.html?sceneID=null&pageMode=0"
                        +"&epNum=1";
                }
            }
            break;
    }
}

function click2() {
    if(switchMode[2] != 0x0002) {
        if(G_deviceIsShare == true) {
            window.showDialog.show(Share_No_Permission,3000);
            return;
        }
    }
    info.setItem("endpointNumber", 2);
    switch(switchMode[2]) {
        case 0x0002:
            toogleSwitch(2, switchState[2]);
            break;
        case 0x0103:
            if(switchExtdata[2]) {
                var bindDevtype = (switchExtdata[2].bindDevtype);
                if (bindDevtype == null || bindDevtype == undefined || bindDevtype == '') {
                    window.location = "bindDevice.html";
                }
            }
            break;
        case 0x0004:
            if(switchExtdata[2]) {
                var sceneID = (switchExtdata[2].sceneID);
                if (sceneID == null || sceneID == undefined || sceneID == '') {
                    window.location="sceneList.html?sceneID=null&pageMode=0"
                        +"&epNum=2";
                }
            }
            break;
    }
}

function showPupWindow(type) {
    $(".pupItem").hide();
    $(".pupWindow").show();
    switch (type) {
        case 0x0002:
            $("#pupSwitch").show();
            break;
        case 0x0103:
            $("#pupBind").show();
            break;
        case 0x0004:
            $("#pupScene").show();
            break;
        default:
            $("#pupSwitch").show();
            break;
    }
}

//重命名确定
function surename() {
    var value = $('#renameInput').val().trim();
    if (value == '') {
        return;
    }

    $('#renameInput').val('');

    if (value.length > 15) {
        value = value.substring(0, 15);
    }
    updateEpName(chooseEp, value);
    cancel();
}
//解绑确定
function sureUnBind() {
    UnBindingDev(chooseEp);
    cancelUnBind();
}
//场景删除确定
function sureDeleteScene() {
    unBindScene(chooseEp);
    cancelDeleteScene();
}
//重命名取消
function cancel() {
    $(".changeName").hide();
}
//解除绑定取消
function cancelUnBind() {
    $(".changeName").hide();
}
//删除场景取消
function cancelDeleteScene() {
    $(".changeName").hide();
}
