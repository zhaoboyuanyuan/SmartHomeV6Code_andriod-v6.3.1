$(function() {
	var dec = null;
	var dId = '';
	initlan();
	plus.plusReady(function() {
		//设备详情数据获取
		plus.gatewayCmd.newDataRefresh(function(data) {
			if(data.cmd == "500" && dId == data.devID) {
				makeData(data);
			} else if(data.cmd == "502" && dId == data.devID) {
				if(data.name != undefined || data.name != '') {
					$(".deviceName").html(data.name)
				}
			}
		});
		plus.gatewayCmd.getDeviceInfo(function(data) {
			dId = data.devID
			makeData(data);
			getMoreConfig(dId)
			$("#back").on("click", function() {
                plus.tools.back(function() {})
			});
			$(".more").on("click", function() {
                plus.tools.more(moreConfig, function() {})
			})
		})
	})

	function makeData(data) {
		if(data.mode == "2") {
			$(".mask_layer").show()
		} else {
			$(".mask_layer").hide()
			// $(".deviceName").html(data.name.indexOf("#$default$#")!= -1?"墙面插座"+data.name.split("#")[2]:data.name)
			$.each(data.endpoints, function(i, endpoints) {
				var status = endpoints.clusters[0].attributes[0].attributeValue;
				var statusStr;
				if(status == "0") {
					statusStr = "../fonts/icon_close.png";
				} else if(status == "1") {
					statusStr = "../fonts/icon_open.png"
				}
				switch(endpoints.endpointNumber) {
					case 1:
						reloadUI(0, status);
						control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
						break;
					case 2:
						reloadUI(1, status);
						control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
						break;
					case 3:
						reloadUI(2, status);
						control(statusStr, data.gwID, data.devID, endpoints.endpointNumber, endpoints.clusters[0].clusterId)
						break;
				}
			})
		}
		var name = data.name.indexOf("#$default$#") != -1 ? lang_socket_01 + data.name.split("#")[2] : data.name
		$(".deviceName").html(name)
	}

	function reloadUI(i, status) {
		$(".switch_handle").eq(i).siblings("em").removeClass("rotate").hide()
		$(".switch_handle").eq(i).siblings("i").hide();
		if(status == "0") {
			$(".socket:eq(" + i + ") span").html(lang_socket_close)
			$(".socket:eq(" + i + ") span").css("color", "gray")
			$(".switch_handle").eq(i).attr("data-state", "off")
			$(".switch_handle").eq(i).attr("src", "../fonts/icon_close.png")
		} else if(status == "1") {
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
			"commandType": 01,
			"commandId": state
		}
		return deviceData;
	}

	function control(statusStr, gwID, devID, endpointNumber, clusterId) {
		$(".switch_handle").eq(endpointNumber - 1).off("click")
		$(".switch_handle").eq(endpointNumber - 1).on("click", function() {
			var self = $(this)
			doClick(self, gwID, devID, endpointNumber, clusterId, statusStr)
		})
	}

	function doClick(self, gwID, devID, endpointNumber, clusterId, statusStr) {
		$(self).siblings("em").show().addClass("rotate")
		$(self).siblings("i").show()
		$(self).attr('src', '../fonts/icon_loading.png');
		doTimeout(endpointNumber, statusStr)
		if($(self).attr("data-state") == "on") {
			dec = deviceControl(gwID, devID, endpointNumber, clusterId, 0)
			plus.gatewayCmd.controlDevice(dec, function(data) {})
		} else {
			dec = deviceControl(gwID, devID, endpointNumber, clusterId, 1)
            plus.gatewayCmd.controlDevice(dec, function(data) {})
		}
	}

	function doTimeout(endpointNumber, statusStr) {
		var timer = setTimeout(function() {
			$(".switch_handle").eq(endpointNumber - 1).siblings("em").removeClass("rotate").hide();
			$(".switch_handle").eq(endpointNumber - 1).siblings("i").hide();
            $(".socket:eq(" + i + ") span").css("color", "gray");
			$(self).attr('src', statusStr);
		}, 15000)
	}
});