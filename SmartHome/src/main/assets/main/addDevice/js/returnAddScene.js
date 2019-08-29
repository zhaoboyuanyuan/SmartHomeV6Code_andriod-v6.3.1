function load(){
	$(".back_addScene").on("click", function() {
		window.history.back(-1);
	})
	$(".back_addGw").on("click", function() {
		window.location.href = "addGateway.html"
	})
	setupWebViewJavascriptBridge(function(bridge) {
		if(browser.versions.android) {
			bridge.init(function(message, responseCallback) {})
		}
		$(".returnScene").on("click", function() {
			bridge.callHandler('back', null, function(data) {})
		})
		$("#gofinish").on("click", function() {
			bridge.callHandler('back', null, function(data) {})
		})
		$(".sure_finish").on("click", function() {
			bridge.callHandler('back', null, function(data) {})
		})
		$(".gw_finish").on("click", function() {
			bridge.callHandler('back', null, function(data) {})
		})
		$(".switch_ok").on("click", function() {
			bridge.callHandler('back', null, function(data) {})
		})
		//获取扫描二维码
		$("#getCode").on("click", function() {
			var self = $(this)
			bridge.callHandler('getQRCode', null, function(data) {
				$(self).siblings("input").val(data)
			})
		})
        $(".gwPwd").on("focus",function(){
            $(this).attr("placeholder",addGateway_toast)
            $(this).siblings("a").css("visibility","visible")
        })
        $("#auto").on("click",function(){
            $(".gwPwd").val($(".gwId").val().substr($(".gwId").val().length-6,6))
		})
		//判断网关登录还是云登录
		$(".gateway").on("click", function() {
			bridge.callHandler('getLoginType', null, function(data) {
				if(data == "100") {
					window.location = "addGateway.html"
				} else {
					$(".gw_toast").show()
					setTimeout(function() {
						$(".gw_toast").hide()
					}, 3000)
				}
			})
		})
		$("#nextStep").unbind("click").on("click", function() {
			var deviceId = $(".gwId").val()
			var devicePasswd = $(".gwPwd").val()
			if(deviceId == '' || devicePasswd == '') {
				//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_00"));
				$(".toast_span").html(addGateway_empty);
				$(".toast_gw").fadeIn()
				setInterval(function() {
					$(".toast_gw").fadeOut()
				}, 4000)
			} else {
				var jsonData = {
					"deviceType": '',
					"deviceId": deviceId,
					"devicePasswd": devicePasswd
				}
				bridge.callHandler('bindingDevice', jsonData, function(data) {
					console.log(data)
					if(data.resultCode == 0) {
						window.location = "addGateway04.html"
					} else {
						switch(data.resultCode) {
							case 20101:
								{
									$(".toast_span").html(addGateway3_toast_01)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_01"));//用户不存在
								}
								break;
							case 20102:
								{
									$(".toast_span").html(addGateway3_toast_02)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_02"));//用户密码错误
								}
								break;
							case 20103:
								{
									$(".toast_span").html(addGateway3_toast_03)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_03"));//token错误
								}
								break;
							case 20104:
								{
									$(".toast_span").html(addGateway3_toast_04)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_04"));//用户名已被使用
								}
								break;
							case 20105:
								{
									$(".toast_span").html(addGateway3_toast_05)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_05"));//手机号已被注册
								}
								break;
							case 20106:
								{
									$(".toast_span").html(addGateway3_toast_06)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_06"));//手机号未认证
								}
								break;
							case 20107:
								{
									$(".toast_span").html(addGateway3_toast_07)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_07"));//邮箱已被注册
								}
								break;
							case 20108:
								{
									$(".toast_span").html(addGateway3_toast_08)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_08"));//邮箱未认证
								}
								break;
							case 20109:
								{
									$(".toast_span").html(addGateway3_toast_09)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_09"));//手机验证码错误
								}
								break;
							case 20110:
								{
									$(".toast_span").html(addGateway3_toast_10)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_10"));//设备不存在
								}
								break;
							case 20111:
								{
									$(".toast_span").html(addGateway3_toast_11)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_11"));//该设备不是网关
								}
								break;
							case 20112:
								{
									$(".toast_span").html(addGateway3_toast_12)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_12"));//设备密码错误
								}
								break;
							case 20113:
								{
									$(".toast_span").html(addGateway3_toast_13)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_13"));//用户不是设备的拥有者
								}
								break;
							case 20114:
								{
									$(".toast_span").html(addGateway3_toast_14)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_14"));//设备已与用户绑定
								}
								break;
							case 20115:
								{
									$(".toast_span").html(addGateway3_toast_15)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_15"));//设备与用户未绑定
								}
								break;
							case 20116:
								{
									$(".toast_span").html(addGateway3_toast_16)
									//$(".toast_gw").html(languageUtil.getResource("addGateway3_toast_16"));//设备密码错误
								}
								break;
							default:
								break;
						}

						$(".toast_gw").fadeIn();
						setInterval(function() {
							$(".toast_gw").fadeOut()
						}, 4000)
					}
				})
			}
		})
	})
}

addLoadEvent(load);
