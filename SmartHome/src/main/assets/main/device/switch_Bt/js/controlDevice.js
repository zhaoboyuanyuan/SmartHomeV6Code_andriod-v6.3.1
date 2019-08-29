$(function() {
	var dec = null;
	var Bridge;
	var timer = null;
	var dId = ''
    initlan();
	setupWebViewJavascriptBridge(function(bridge) {
		Bridge = bridge
		if(browser.versions.android){
			bridge.init(function(message, responseCallback) {})
		}
		//设备详情数据获取
		bridge.registerHandler("newDataRefresh", function(data) {
			if(data.cmd == "500" && dId == data.devID){
				if(data.mode == "2"){
					$(".mask_layer").show();
				}else{
					$(".mask_layer").hide();
					var attribute = data.endpoints[0].clusters[0].attributes[0]
                    $(".switch_handle").siblings("em").removeClass("rotate").hide()
                    $(".switch_handle").siblings("i").hide();

					//同步网关的设备信息
					if(attribute.attributeValue == "0") {
						$(".socket span").html(lang_socket_close)
						$(".switch_handle").attr("data-state", "off")
						$(".switch_handle").attr("src", "../fonts/icon_close.png")
                        $(".state").css("color","#666")
					} else if(attribute.attributeValue == "1") {
						$(".socket span").html(lang_socket_open)
						$(".switch_handle").attr("data-state", "on")
						$(".switch_handle").attr("src", "../fonts/icon_open.png")
                        $(".state").css("color","#8dd652")
					}
				}
                var name = data.name.indexOf("#$default$#") != -1 ? device_Bt_name+data.name.split("#")[2]:data.name
                $(".deviceName").html(name)
			}else if(data.cmd == "502" && dId == data.devID){
                if(data.name != undefined || data.name != ''){
                    $(".deviceName").html(data.name)
                }
			}
		})
		bridge.callHandler("getDeviceInfo", null, function(data) {
			dId = data.devID
            getMoreConfig(dId)
			if(data.mode == "2"){
				$(".mask_layer").show()
			}else{
				$(".mask_layer").hide()

                var attribute = {}
                for (var i = 0; i < data.endpoints[0].clusters[0].attributes.length; i++) {
					var attr = data.endpoints[0].clusters[0].attributes[i]
					if (attr.attributeId == "0") {
                        attribute = attr
					}
                }

				if(attribute.attributeValue == "0") {
					$(".socket span").html(lang_socket_close)
					$(".switch_handle").attr("data-state", "off")
					$(".switch_handle").attr("src", "../fonts/icon_close.png")
                    $(".state").css("color","#666")
				} else if(attribute.attributeValue == "1") {
					$(".socket span").html(lang_socket_open)
					$(".switch_handle").attr("data-state", "on")
					$(".switch_handle").attr("src", "../fonts/icon_open.png")
                    $(".state").css("color","#8dd652")
				}
				control(data.gwID, data.devID, data.endpoints[0].endpointNumber, data.endpoints[0].clusters[0].clusterId)

				// 查询状态
                dec = deviceControl(data.gwID, data.devID, data.endpoints[0].endpointNumber, data.endpoints[0].clusters[0].clusterId, 2)
                Bridge.callHandler("controlDevice", dec, function(data) {})
			}
            var name = data.name.indexOf("#$default$#") != -1 ? device_Bt_name+data.name.split("#")[2]:data.name
            $(".deviceName").html(name)
		})
		$("#back").on("click",function(){
			bridge.callHandler("back",null,function(){})
		})
		$(".more").on("click",function(){
			bridge.callHandler("more", moreConfig, function(){})
		})
	})
		//设备控制
	function deviceControl(gwID, devID, endpointNumber, clusterId, state) {
		var deviceData = {
			"cmd": "501",
			"gwID": gwID,
			"devID": devID,
			"endpointNumber": 1,
			"clusterId": 6,
			"commandType": 1,
			"commandId": state
		}
		return deviceData;
	}

	function control(gwID, devID, endpointNumber, clusterId) {
		$(".switch_handle").on("click", function() {
			//console.log(444)
			var self = $(this)
			$(self).siblings("em").show().addClass("rotate")
			$(self).siblings("i").show()
			timer = setTimeout(function() {
				$(".switch_handle").siblings("em").removeClass("rotate").hide()
				$(".switch_handle").siblings("i").hide();
			}, 15000)
			if($(this).attr("data-state") == "on") {
				dec = deviceControl(gwID, devID, endpointNumber, clusterId, 0)
				Bridge.callHandler("controlDevice", dec, function(data) {})
			} else {
				dec = deviceControl(gwID, devID, endpointNumber, clusterId, 1)
				Bridge.callHandler("controlDevice", dec, function(data) {})
			}
		})
	}
})