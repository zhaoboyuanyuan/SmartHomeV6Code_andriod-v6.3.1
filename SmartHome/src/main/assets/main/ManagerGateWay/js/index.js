// 获取详情
function getInfo() {

    plus.gatewayCmd.getCurrentAppID(function (data) {
        appId = data;
        plus.gatewayCmd.getCurrentGWInfo(function (data) {
            var gwInfo = JSON.parse(data);

            gwType = gwInfo.gwType;
            gwId = gwInfo.gwID;
            var hostFlag = gwInfo.hostFlag;//1代表主机 0代表子机
            if (hostFlag == '1') {
                isMainEngine = true;
                showStatus = 2;
                showFromStatus();
                var bindlist = info.getItem('BindGWList');
                if (bindlist != undefined && bindlist != "" && bindlist != null && bindlist != "[]") {
                    // 从上一页返回
                    var bindListData = JSON.parse(bindlist);
                    dealGWList(bindListData);
                } else {
                    // 从外面进入
                    getGWBindList();
                }
            } else {
                if (gwInfo.masterGw.length > 0) {
                    isMainEngine = false;
                    showStatus = 4;
                    masterGw = gwInfo.masterGw
                    showFromStatus();
                } else {
                    isMainEngine = false;
                    showStatus = 1;

                    //TODO 测试
                    // showStatus = 4;
                    // masterGw = "12312sadfqw"

                    showFromStatus();
                }

            }

            // 判断是否显示帮助
            var isShowHelp = info.getItem('ShowHelp');
            if (!isShowHelp && needShow) {
                showHelp();
            }
        });
    });
}

// 子机列表处理
function dealGWList(result) {
    $(".getMission .lists").empty();
    if (result.length == 0) {
        showStatus = 2;
        showFromStatus();
    } else {
        showStatus = 3;
        showFromStatus();
    }
    // 缓存网关数据
    info.setItem('BindGWList', JSON.stringify(result));
    for (var i = 0; i < result.length; i++) {
        var model = result[i];
        var gwModel = GetGWModel(model.gwName, model.subid, model.masterid, model.gwType);
        createLi(gwModel);
    }
}

// 根据子机类型显示不同
function dealSubGWType(gwType)
{
	if (gwType == 'GW01' || gwType == 'GW09' || gwType == 'GW10' || gwType == "GW08"){
		// 可切换为主机的子机
		$("#subgwDesc").text(languageUtil.getResource("Fog_calculation_Sub_machine_mode_can_Switch"));
		$("#switchToMainEngine").css("display","block");
	} else {
		// 不可作为主机的子机
		$("#subgwDesc").text(languageUtil.getResource("Fog_calculation_Sub_machine_mode_no_Switch"));
		$("#switchToMainEngine").css("display","none");
	}
}

// 解绑弹框
function leftDeleteAction(gwID) {
    var tip = languageUtil.getResource("Fog_calculation_prompt");
    var msg = languageUtil.getResource("Fog_calculation_Unbundled_prompt");
    var cancle = languageUtil.getResource("Fog_calculation_cancel");
    var sure = languageUtil.getResource("Fog_calculation_Unbundled");

    showDouble(tip, msg, cancle, sure, function () {
        if (showStatus == 4){
            //子网关解绑自己
            unbindGW(gwId);
        } else {
            // 主网关解绑子机
            unbindGW(gwID);
        }
    });
}

// 切换为子网关处理
function switchToSubGWDeal(result) {
    var mode = result.mode;
    var code = result.code;
    var data = result.data;
    if (code == 0) {
        toast(switchSuccessful);
        showStatus = 1;
        showFromStatus();
        send512query();
    } else if (code == 4) {
        toast("该网关不能作为子机");
    } else {
        toast(switchFailed);
    }
}

// 切换为主网关处理
function switchToMainGWDeal(result) {
    var mode = result.mode;
    var code = result.code;
    var data = result.data;
    if (code == 0) {
        toast(switchSuccessful);
        showStatus = 2;
        showFromStatus();
        send512query();
    } else if (code == 3) {
        toast(notSwitchToSub);
    } else {
        toast(switchFailed);
    }
}

// 解绑成功
function unbindDeal(result) {
    var mode = result.mode;
    var code = result.code;
    var data = result.data;
    if (code == 0) {
        if (showStatus == 4){
            showStatus = 1;
            masterGw = ""
        } else {
            showStatus = 2;
            getGWBindList();
        }

        showFromStatus();

        toast(unbindSuccessful);
    } else {
        toast(unbindFail);
    }
}

// 根据网关状态显示
function showFromStatus() {
    // 1 子机未绑定  2 主机未绑定 3 主机绑定 4 子机已绑定
    if (showStatus == 2) {
        $(".sub_engine_nobind").css("display", "none");
        $(".sub_engine_bind").css("display", "none");
        $(".main_engine_nobind").css("display", "block");
        $(".main_engine_bind").css("display", "none");
    } else if (showStatus == 3) {
        $(".sub_engine_nobind").css("display", "none");
        $(".sub_engine_bind").css("display", "none");
        $(".main_engine_nobind").css("display", "none");
        $(".main_engine_bind").css("display", "block");
    } else if (showStatus == 1) {
        $(".sub_engine_nobind").css("display", "block");
        $(".sub_engine_bind").css("display", "none");
        $(".main_engine_nobind").css("display", "none");
        $(".main_engine_bind").css("display", "none");
    } else if (showStatus == 4) {
        $(".sub_engine_nobind").css("display", "none");
        $(".sub_engine_bind").css("display", "block");
        $(".main_engine_nobind").css("display", "none");
        $(".main_engine_bind").css("display", "none");
        createSubLi(masterGw)
    }
}

//显示帮助
function showHelp() {
    info.setItem('ShowHelp', true);
    $(".help_section").css("display", "block");
}

// 隐藏帮助
function hideHelp() {
    $(".help_section").css("display", "none");
}

function createLi(gwModel) {
    var gwName = gwModel.name;
    var gwID = gwModel.gwID;
    var dataID = gwModel.masterID;
    var gwType = gwModel.type;

    var list = $(".main_engine_bind .getMission .lists");

    var dl = $("<dl></dl>");
    dl.attr("data-id", dataID);
    dl.addClass("task_list");
    dl.appendTo(list);

    var dt = $("<dt></dt>");
    dt.appendTo(dl);

    var a = $("<a></a>");
    a.attr('href', 'deviceList.html?subgwid=' + gwID);
    a.appendTo(dt);
    var background = 'url(../source/deviceIcon/device_icon_' + gwType + '.png)';
    a.css("background-image", background);
    a.css("background-repeat", "no-repeat");
    a.css("background-position", "left center");
    a.css("background-size", "5rem");


    var span = $("<span></span>").text(gwName);
    span.attr("id", "click");
    span.appendTo(a);
    if (gwName == '') {
        plus.gatewayCmd.managerGWName(gwType, function (result) {
            span.text(result)
        });
    }
    var em = $("<em></em>").text(gwID);
    dl.attr("gwID", gwID);
    span.after(em);

    var dd = $("<dd></dd>");
    dd.appendTo(dl);

    var b = $("<b></b>").text(Fog_calculation_Unbundled);
    b.addClass("delete");
    b.appendTo(dd);

    $(".getMission dl").leftDelete({
        marginLeft: "-15%",
    });
}

function createSubLi(gwID) {

    var list = $(".sub_engine_bind .getMission .lists");
    list.empty();

    var li = $("<li></li>");
    li.appendTo(list);

    var dt = $("<dt></dt>").text(Fog_calculation_New_08 + gwID);
    dt.appendTo(li);


    var dd = $("<dd></dd>");
    dd.appendTo(li);

    var b = $("<b></b>").text(Fog_calculation_Unbundled);
    b.addClass("delete");
    b.appendTo(dd);

    $(".getMission li").leftDelete({
        marginLeft: "-15%",
    });
}

//主机局域网查找可绑的子机
function goLanGW() {
    window.location = 'lanGW.html';
}

// 子机指定主机的IP
function bindMainGW() {
    $(".bindMainGW").css("display", "block")
    $("#bindMainGW_cancel").unbind("click").on("click", function (event) {
        $(".bindMainGW").css("display", "none")
    })
    $("#bindMainGW_confirm").unbind("click").on("click", function (event) {
        var masterID = $("#bindMainGW_gwID").val()
        var masterIP = $("#bindMainGW_gwIP").val()

        var GWID_RE = /^[A-Z0-9]{11,12}$/
        var GWID_test = GWID_RE.test(masterID)

        var GWIP_RE = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/
        var GWIP_test = GWIP_RE.test(masterIP)

        if (masterID.length > 0 && masterIP.length > 0 && GWID_test && GWIP_test) {
            bindMainGWControl(masterID, masterIP)
            $(".bindMainGW").css("display", "none")
            $("#bindMainGW_gwID").val("")
            $("#bindMainGW_gwIP").val("")
        } else {
            toast(Fog_calculation_New_06);
        }
    })
}