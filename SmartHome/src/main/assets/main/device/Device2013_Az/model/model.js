//model   数据操作
MVC.M = {
	getDeviceInfo: function() {
		plus.gatewayCmd.getDeviceInfo(function(result) {
			DEVID = result.devID;
            GWID = result.gwID;
            APPID = result.appID;
            DEVICEINFO = getMoreConfig(DEVID);
            if(result.mode == 2) {
				shadow.style.display = "block";
			} else {
				shadow.style.display = "none";
			}
			var name = result.name.indexOf("#$default$#") != -1 ? deviceDefaultName + result.name.split("#")[2] : result.name
			deviceName.textContent = name;
			MVC.M.rush_500(DEVID, GWID);
			//获取场景列表
			plus.gatewayCmd.getSceneList(function(result) {
				SceneList = result;
			});
			MVC.M.getBindList();
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
				}
			} else if(result.cmd == "502" && dID == result.devID && gID == result.gwID) {
				if(result.name != undefined) {
					deviceName.textContent = result.name;
				}
			} else if(result.cmd == "514" && dID == result.devID && gID == result.gwID) {
				if(result.data != undefined) {
					MVC.M.dealBindInfo(result.data);
				}
			} else if(result.cmd == "513" && dID == result.devID && gID == result.gwID) {
				MVC.M.getBindList();
			}
		});
	},

	/*
	 * 获取场景绑定
	 */
	getBindList: function() {
		var jsonData = {
			"cmd": "514",
			"gwID": GWID,
			"devID": DEVID,
			"appID": APPID,
		};
		plus.gatewayCmd.controlDevice(jsonData, function(result) {
			console.log("result" + result);
		})
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

	//处理获取到的绑定结果
	dealBindInfo: function(data) {
		MVC.M.clearContent();
		var bindList = data;
		for(var i = 0; i < bindList.length; i++) {
			var bindInfo = bindList[i];
			var endPointNum = bindInfo.endpointNumber;
			var sceneID = bindInfo.sceneID;
			MVC.M.bindMapSet(endPointNum,sceneID);
			var keyText = document.getElementById("key" + endPointNum);
			var sceneImg = document.getElementById("scene" + endPointNum);
			var keyImg = document.getElementById("keyImg" + endPointNum);
			for(var j = 0; j < SceneList.length; j++) {
				var scene = SceneList[j];
				if(scene.sceneID == sceneID) {
					var sceneName = scene.sceneName;
					var sceneIcon = scene.sceneIcon;
					keyText.innerText = sceneName;
					keyText.style.color = '#54bf00';
					var imageName = "scene_normal_pre_" + sceneIcon + ".png";
					imageName = "url(../../source/sceneIcon/" + imageName + ")";
					sceneImg.style.display = 'block';
					sceneImg.style.background = imageName + 'no-repeat center center';
					sceneImg.style.backgroundSize = '30%';
					keyImageName = "url(./fonts/keybg.png)";
					keyImg.style.background = keyImageName + 'no-repeat center center';
					keyImg.style.backgroundSize = '60%';
					break;
				}
			}
		}
	},
	
	/*
	 * 清空界面
	 */
	clearContent:function(){
		for (var i = 1; i < 7; i ++){
			var keyText = document.getElementById("key" + String(i));
			var sceneImg = document.getElementById("scene" + String(i));
			var keyImg = document.getElementById("keyImg" + String(i));
			
			keyText.innerText = noBinding;
			keyText.style.color = '#535353';
			
			sceneImg.style.display = 'none';
			keyImageName = "url(./fonts/icon_add_1.png)";
			keyImg.style.background = keyImageName + 'no-repeat center center';
			keyImg.style.backgroundSize = '60%';
		}
	},

	bindMapSet: function(endPointNum, sceneID) {
		switch(endPointNum) {
			case '1':
				bindMap['1'] = sceneID;
				break;
			case '2':
				bindMap['2'] = sceneID;
				break;
			case '3':
				bindMap['3'] = sceneID;
				break;
			case '4':
				bindMap['4'] = sceneID;
				break;
			case '5':
				bindMap['5'] = sceneID;
				break;
			case '6':
				bindMap['6'] = sceneID;
				break;
			default:
				break;
		}
	}

};