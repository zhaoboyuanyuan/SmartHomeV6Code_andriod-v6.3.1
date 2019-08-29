//model   数据操作
MVC.M = {
	getDeviceInfo: function() {
		plus.gatewayCmd.getDeviceInfo(function(result) {
			DEVICEINFO = result;
			ENDPOINTNUMBER = result.endpoints[0].endpointNumber;
			DEVID = result.devID;
			GWID = result.gwID;
			if(result.mode == 2) {
				shadow.style.display = "block";
			} else {
				shadow.style.display = "none";
			}
            getMoreConfig(DEVID)
			var name = result.name.indexOf("#$default$#") != -1 ? lang_01name + result.name.split("#")[2]:result.name
			deviceName.textContent = name;
			MVC.M.rush_500(DEVID, GWID);
		});
	},
	/*
	 * 500信息返回
	 */
	rush_500: function(dID, gID) {
		plus.gatewayCmd.newDataRefresh(function(result) {
			if(result.cmd == "500" && dID == result.devID && gID == result.gwID) {
				if(result.mode == 2) {
					shadow.style.display = "block";
				} else if(result.mode == 1) {
					shadow.style.display = "none";
				} else if(result.mode == 0) {
					shadow.style.display = "none";
					var status = result.endpoints[0].clusters[0].attributes[0].attributeValue;
					if(result.endpoints[0].clusters[0].clusterId == 1280)
						MVC.M.reloadUI(status);
				}
			}else if(result.cmd == "502" && dID == result.devID && gID == result.gwID){
				if(result.name != undefined){
						deviceName.textContent = result.name;
					}
			}
		})
	},
	/*
	 * 设备控制
	 */
	sendCmd: function(attributeId) {
		var jsonData = {
			"cmd": "501",
			"gwID": GWID,
			"devID": DEVID,
			"endpointNumber": ENDPOINTNUMBER,
			"clusterId": 1280,
			"commandType": 01,
			"commandId": attributeId,
		};
		plus.gatewayCmd.controlDevice(jsonData, function(result) {
			console.log("result" + result);
		})
	},
	/*
	 * 根据返回值刷新UI
	 */
	reloadUI: function(status) {
		if(status == 0) {
			MVC.V.stopAlarm();
		} else if(status == 1) {
			MVC.V.norAlarm();
		} else if(status == 2) {
			MVC.V.fireAlarm();
		}
	},
};