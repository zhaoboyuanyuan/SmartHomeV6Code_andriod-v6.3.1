//model   数据操作
MVC.M = {
	getDeviceInfo: function() {
		plus.gatewayCmd.getDeviceInfo(function(result) {
			DEVICEINFO = result;
			DEVID = result.devID;
			GWID = result.gwID;
			APPID = result.appID;
			endPointNum = getUrlParam("endPointNum");
			bindSceneID = getUrlParam("bindSceneID");
			if (bindSceneID == '-1'){
				article.setAttribute('binded', "on");
			}
			//获取场景列表
			plus.gatewayCmd.getSceneList(function(result) {
				if(result != undefined) {
					sceneList = result;
					nav.innerHTML = '';
					for(var i = 0; i < sceneList.length; i++)
					{
						var scene = sceneList[i];
						var sceneID = scene.sceneID;
						var sceneName = scene.sceneName;
						var sceneIcon = scene.sceneIcon;
						sceneIcon = 'url(../../source/sceneIcon/scene_normal_' + sceneIcon + '.png)';
						addLi(sceneID,sceneName,sceneIcon);
					}
				}
			});
            MVC.M.rush_500(DEVID,GWID);
		});
	},
	
	/*
	 * 500信息返回
	 */
	rush_500: function(dID, gID) {
		plus.gatewayCmd.newDataRefresh(function(result) {
			if(result.cmd == "513" && result.devID == dID) {
				if(result.mode == 1 && result.data[0].endpointNumber == endPointNum){
					//绑定成功
                    window.loadingPrompt.hide();
                    window.location = 'SceneKey.html';
				}else if(result.mode == 3 && result.data[0].endpointNumber == endPointNum){
					//解除绑定
                    window.loadingPrompt.hide();
                    window.location = 'SceneKey.html';
				}
			} else if(result.cmd == "502" && dID == result.devID && gID == result.gwID) {
			
			}
		});
	},
	/*
	 * 设置场景绑定
	 */
	setBind: function(data) {
		var jsonData = {
			"cmd": "513",
			"gwID": GWID,
			"devID": DEVID,
			"appID": APPID,
			"mode": 1,
			"data": data
		};
		plus.gatewayCmd.controlDevice(jsonData, function(result) {
			console.log("result" + result);
		})
	},
	
	/*
	 * 解绑
	 */
	unBind:function(data){
			var jsonData = {
			"cmd": "513",
			"gwID": GWID,
			"devID": DEVID,
			"mode": 3,
			"data": data
		};
		plus.gatewayCmd.controlDevice(jsonData, function(result) {
			console.log("result" + result);
		})
	},
};