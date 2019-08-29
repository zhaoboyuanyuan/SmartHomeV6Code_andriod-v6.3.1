function refreshBellState(state){
	// 0不响  1响
	if (state == "1"){
		bellShake();
	} else{
		stopShake();
	}
}
// 振动
function bellShake(){
	$(".bell").addClass("shake-rotate shake-constant bellShake")
	$(".bell").removeClass("bellNormal")
}

// 停止振动
function stopShake(){
	$(".bell").removeClass("shake-rotate shake-constant bellShake")
	$(".bell").addClass("bellNormal")
}

// 点击门铃
function controlBell(type){
	if (type == 1){
		// 短铃
		controlDevice(1,6,8,null);
	} else if (type == 2){
		// 长铃
	    controlDevice(1,6,9,["1"]);
	} else if (type == 3){
		// 停止
		controlDevice(1,6,9,["0"]);
	}
}


// 控制命令
function controlDevice(endpointNumber, clusterId, commandId, parm){
	var deviceData = {
            "cmd": "501",
            "gwID": gwId,
            "devID": deviceId,
            "endpointNumber": endpointNumber,
            "clusterId": clusterId,
            "commandType": 1,
            "commandId": commandId,
        }
		if (parm != null && parm != undefined){
			deviceData["parameter"] = parm;
		}
		plus.gatewayCmd.controlDevice(deviceData, function(result) {})
}


// function  初始化事件
function initEvent()
{
	$("#back").on("click", function() {
        plus.tools.back(function() {})
    });
    $(".more").on("click", function() {
        plus.tools.more(moreConfig, function() {})
    });
    $(".keyScene div").on("click", function() {
        if (SceneList != undefined && SceneList.length == 0) {
            window.location = "SceneEmpty.html";
        } else {
            var sceneId = $("#keySceneImg").attr("data-sceneid");
            window.location = "bindScene.html?sceneId=" + sceneId;
        }
    });
    $("#keyView").on("click", function() {
        var state = $("#keySwitchImg").attr("openState");
        doTimeout();
        if (state == "on") {
            $("#keySwitchImgLoading").show().addClass("rotate");
            controlDevice(1, 6, 3, null);
        } else {
            $("#keySwitchImgLoading").show().addClass("rotate");
            controlDevice(1, 6, 4, null);
        }
    });
    
    var device_Aw_short = document.getElementById("device_Aw_short");
	device_Aw_short.ontouchstart=function(){  
         $("#device_Aw_short").css("background","url(./fonts/radiusLeft_hight.png) no-repeat center center");
			 $("#device_Aw_short").css("backgroundSize","8.4rem 3.8rem");
    }  
    device_Aw_short.ontouchend=function(){  
         $("#device_Aw_short").css("background","url(./fonts/radiusLeft.png) no-repeat center center");
			 $("#device_Aw_short").css("backgroundSize","8.4rem 3.8rem");
    }
    var device_Aw_long = document.getElementById("device_Aw_long");
	device_Aw_long.ontouchstart=function(){  
        $("#device_Aw_long").css("background","url(./fonts/rect_hight.png) no-repeat center center");
		$("#device_Aw_long").css("backgroundSize","8.3rem 3.8rem");
    }  
    device_Aw_long.ontouchend=function(){  
        $("#device_Aw_long").css("background","url(./fonts/rect.png) no-repeat center center");
		$("#device_Aw_long").css("backgroundSize","8.3rem 3.8rem");
    }
     var device_Aw_stop = document.getElementById("device_Aw_stop");
	device_Aw_stop.ontouchstart=function(){  
        $("#device_Aw_stop").css("background","url(./fonts/radiusRight_hight.png) no-repeat center center");
		$("#device_Aw_stop").css("backgroundSize","8.4rem 3.8rem");
    }  
    device_Aw_stop.ontouchend=function(){  
        $("#device_Aw_stop").css("background","url(./fonts/radiusRight.png) no-repeat center center");
		$("#device_Aw_stop").css("backgroundSize","8.4rem 3.8rem");
    }
}
