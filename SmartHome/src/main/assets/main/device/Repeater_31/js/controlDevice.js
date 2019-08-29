$(function() {
	var dec = null;
	var Bridge;
	var dId = ''
	initlan();
	setupWebViewJavascriptBridge(function(bridge) {
		Bridge = bridge
		if(browser.versions.android) {
			bridge.init(function(message, responseCallback) {})
		}
		//设备详情数据获取
		bridge.registerHandler("newDataRefresh", function(data) {
			if(data.cmd == "500" && dId == data.devID) {
				makeData(data);
			} else if(data.cmd == "502" && dId == data.devID) {
				if(data.name != undefined || data.name != '') {
					$(".deviceName").html(data.name)
				}
			}
		})
		bridge.callHandler("getDeviceInfo", null, function(data) {
			dId = data.devID
			makeData(data);
			getMoreConfig(dId)
			$("#back").on("click", function() {
				bridge.callHandler("back", null, function() {})
			})
			$(".more").on("click", function() {
				bridge.callHandler("more", moreConfig, function() {})
			})
			
		})
	})

	function makeData(data) {
		if(data.mode == "2") {
			$(".mask_layer").show()
		} else {
			$(".mask_layer").hide()
		}
		var name = data.name.indexOf("#$default$#") != -1 ? languageUtil.getResource("device_31_name") + data.name.split("#")[2] : data.name
		$(".deviceName").html(name)
	}

	
})