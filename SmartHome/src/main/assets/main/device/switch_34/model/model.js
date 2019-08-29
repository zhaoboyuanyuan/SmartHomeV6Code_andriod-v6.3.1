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
					if(sceneName != undefined && getStrLength(sceneName) > 16){
                        sceneName = cutstr(sceneName, 16);
					}
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
		for (var i = 1; i < 5; i ++){
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
			default:
				break;
		}
	}

};

/** 
     * 函数说明：获取字符串长度 
     * 备注：字符串实际长度，中文2，英文1 
     * @param:需要获得长度的字符串 
     */  
    function getStrLength(str) {  
        var realLength = 0, len = str.length, charCode = -1;  
        for (var i = 0; i < len; i++) {  
            charCode = str.charCodeAt(i);  
            if (charCode >= 0 && charCode <= 128){  
                realLength += 1;  
            }else{  
                realLength += 2;  
            }  
        }  
        return realLength;  
    }  
    /** 
     * js截取字符串，中英文都能用 
     * @param str：需要截取的字符串 
     * @param len: 需要截取的长度 
     */  
    function cutstr(str, len) {  
        var str_length = 0;  
        var str_len = 0;  
        str_cut = new String();  
        str_len = str.length;  

        //如果给定字符串小于指定长度，则返回源字符串；
        if (getStrLength(str) < len) {
            return str;
        }

        for (var i = 0; i < str_len; i++) {  
            a = str.charAt(i);  
            str_length++;  
            if (escape(a).length > 4) {  
                //中文字符的长度经编码之后大于4  
                str_length++;  
            }    
            if (str_length > len) {  
                str_cut = str_cut.concat("...");  
                return str_cut;  
            }  
            str_cut = str_cut.concat(a);
        }  
    }  