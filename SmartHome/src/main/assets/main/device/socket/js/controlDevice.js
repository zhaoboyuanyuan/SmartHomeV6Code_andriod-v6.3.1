$(function() {
	var dec = null;
	var timer = null;
	var dId = '';
    initlan();
    var iconId = "";
    var socketState = "";
	plus.plusReady(function(){
		//获取是否设置显示icon
		plus.gatewayCmd.getDeviceIconByType(function(data){
			if(data){
                iconId = data
			}
		});
		//设备详情数据获取
		plus.gatewayCmd.newDataRefresh(function(data) {
			if(data.cmd == "500" && dId == data.devID){
				if(data.mode == "2"){
					$(".mask_layer").show();
				}else{
					$(".mask_layer").hide();
					//$(".deviceName").html(data.name.indexOf("#$default$#")!= -1?"墙面插座"+data.name.split("#")[2]:data.name)
					var attribute = data.endpoints[0].clusters[0].attributes[0]
                    $(".switch_handle").siblings("em").removeClass("rotate").hide()
                    $(".switch_handle").siblings("i").hide();
					//同步网关的设备信息
					if(attribute.attributeValue == "0") {
						$(".socket span").html(lang_socket_close)
						$(".switch_handle").attr("data-state", "off")
						$(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_"+iconId+".png");
                        $(".state").css("color","#666")
                        socketState = "0"
					} else if(attribute.attributeValue == "1") {
						$(".socket span").html(lang_socket_open)
						$(".switch_handle").attr("data-state", "on")
						$(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_pre_"+iconId+".png");
                        $(".state").css("color","#8dd652")
                        socketState = "1"
					}
				}
                var name = data.name.indexOf("#$default$#") != -1 ? lang_socket_01+data.name.split("#")[2]:data.name
                $(".deviceName").html(name)
			}else if(data.cmd == "502" && dId == data.devID){
                plus.gatewayCmd.getDeviceIconByType(function(data){
                    if(data){
                        iconId = data;
                        if(socketState === "0"){
                            $(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_"+iconId+".png");
						}else if(socketState === "1"){
                            $(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_pre_"+iconId+".png");
						}
                    }
                });
                if(data.name != undefined || data.name != ''){
                    $(".deviceName").html(data.name)
                }
			}
		})
		plus.gatewayCmd.getDeviceInfo(function(data) {
			dId = data.devID
            getMoreConfig(dId)
			if(data.mode == "2"){
				$(".mask_layer").show()
			}else{
				$(".mask_layer").hide()
				// $(".deviceName").html(data.name.indexOf("#$default$#")!= -1?"墙面插座"+data.name.split("#")[2]:data.name)
				if(data.endpoints[0].clusters[0].attributes[0].attributeValue == "0") {
					$(".socket span").html(lang_socket_close)
					$(".switch_handle").attr("data-state", "off")
					$(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_"+iconId+".png")
                    $(".state").css("color","#666")
                    socketState = "0"
				} else if(data.endpoints[0].clusters[0].attributes[0].attributeValue == "1") {
					$(".socket span").html(lang_socket_open)
					$(".switch_handle").attr("data-state", "on")
					$(".switch_handle").attr("src", "../../../source/deviceImg/socket_icon_pre_"+iconId+".png")
                    $(".state").css("color","#8dd652")
                    socketState = "1"
				}
				control(data.gwID, data.devID, data.endpoints[0].endpointNumber, data.endpoints[0].clusters[0].clusterId)
			}
            var name = data.name.indexOf("#$default$#") != -1 ? lang_socket_01+data.name.split("#")[2]:data.name
            $(".deviceName").html(name)
		});
		$("#back").on("click",function(){
			plus.tools.back(function(){})
		});
		$(".more").on("click",function(){
			plus.tools.more(moreConfig,function(){})
		})
	});
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
				plus.gatewayCmd.controlDevice(dec, function(data) {})
			} else {
				dec = deviceControl(gwID, devID, endpointNumber, clusterId, 1)
                plus.gatewayCmd.controlDevice(dec, function(data) {})
			}
		})
	}
});