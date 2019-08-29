// 控制命令
function control(jsonData) {
	if (jsonData.cmd == '515'|| jsonData.cmd == '524'){
		showWaiting();
	}
	plus.gatewayCmd.controlDevice(jsonData, function(result) {
	});
}

plus.gatewayCmd.newDataRefresh(function(data) {
	if(data.cmd == "515" && data.appID == appId && data.gwID == gwId  ) {
		hideWaiting();
		var result = data;
//		result = JSON.parse(result);
		var mode = result.mode;
		var code = result.code;
		var data = result.data;
		dealModel(result);
	}
	if (data.cmd == "524" && data.appID == appId && data.gwID == gwId  ) {
        hideWaiting();
        var result = data.result;
        if (result == 0){
            toast(Fog_calculation_Bind_success);
            showStatus = 4;
            masterGw = data.masterID;
            showFromStatus();
		} else {
            toast(Fog_calculation_New_06);
		}
	}
})

// mode处理
function dealModel(result) {
	//	1：切换为雾计算主机
	//2：切换为雾计算子机
	//3：查询子机列表
	//4：搜索局域网下的子机
	//5：绑定子机
	//6：解绑子机
	var mode = result.mode;
	var data = result.data;
	if(mode == 1) {
		switchToMainGWDeal(result);
	} else if(mode == 2) {
		switchToSubGWDeal(result);
	} else if(mode == 3) {
		dealGWList(data);
	} else if(mode == 4) {
		lanListDeal(data);
	} else if(mode == 5) {
		bindResultDeal(result);
	} else if(mode == 6) {
		unbindDeal(result);
	}
}

// 处理code
function dealCode(result) {
	//	0：成功
	//1：失败；
	//2：密码错误(绑定子机的密码不正确时，会返回此code)
	//3：该网关不能作为主机；
	//4：该网关不能作为子机；
	//5：当前网关为主机且主机下有绑定子机，需解绑子机方可切换为子机，
	//6：当前网关已被主机管理，需解绑方可切换成主机
	//7：其他错误
	var mode = result.mode;
	var code = result.code;
	if(code == 1) {

	} else if(code == 2 && mode == 5) {

	} else if(code == 3 && mode == 1) {

	} else if(code == 4 && mode == 2) {

	} else if(code == 5 && mode == 2) {

	} else if(code == 6 && mode == 1) {

	} else if(code == 7) {

	}
}

//	获取绑定列表
function getGWBindList() {
	info.setItem('BindGWList', "");
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 3,
	};
	control(jsonData);
}

//解绑子网关
function unbindGW(subGwid) {
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 6,
		"subGwid": subGwid,
	};
	control(jsonData);
}

//绑定子网关
function bindGW(subGwid, subGwpwd) {
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 5,
		"subGwid": subGwid,
		"subGwpwd": subGwpwd
	};
	control(jsonData);
}

//绑定子网关
function bindMainGWControl(mainGwid, mainGwIP) {
    var jsonData = {
        "cmd": "524",
        "gwID": gwId,
        "appID": appId,
        "masterID": mainGwid,
        "masterIP": mainGwIP
    };
    control(jsonData);
}

//切换子网关
function switchToSubGW() {
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 2,
	};
	control(jsonData);
}

//切换主网关
function switchToMainGW() {
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 1,
	};
	control(jsonData);
}

// 搜索局域网下网关
function searchLanGW() {
	var jsonData = {
		"cmd": "515",
		"gwID": gwId,
		"appID": appId,
		"mode": 4,
	};
	control(jsonData);
}

function send512query(){
	var jsonData = {
		"cmd": "512",
		"gwID": gwId,
		"appID": appId,
		"mode": 0,
	};
	control(jsonData);
}
