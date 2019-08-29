/*此文件用于存储各种命令*/
var gwID,devID,appID;
/*
 * 设备控制
 */
function sendCmd(jsonData) {
	plus.gatewayCmd.controlDevice(jsonData, function(result) {
		console.log("result" + result);
	})
}
/*向三键金属开关发送命令
 * endpointNumber：0表示对所有按键都有作用
 * commandId：表示执行不同的操作
 * param:参数,注意，这个是数组
 * */
function sendCmdForAo(endpointNumber,commandId,param){
	var jsonData;
	if(param!=null){
		jsonData = {
			"cmd": "501",
			"gwID": gwID,
			"devID": devID,
			"endpointNumber": endpointNumber,
			"commandType": 1,
			"commandId": commandId,
			"parameter": param
		};
	}else{
		jsonData = {
			"cmd": "501",
			"gwID": gwID,
			"devID": devID,
			"endpointNumber": endpointNumber,
			"commandType": 1,
			"commandId": commandId
		};	
	}	
	sendCmd(jsonData);
}

/*查询状态*/
function searchAoStatus(){
	sendCmdForAo(0,0x02,["0"]);
}
/*切换状态*/
function changeOpenOrClose(endpointNumber){
	showLog(endpointNumber+"键切换状态");
	sendCmdForAo(endpointNumber,0x03,null);
}
function powerOpen(endpointNumber){
	sendCmdForAo(endpointNumber,0x01,null);
}
function powerClose(endpointNumber){
	sendCmdForAo(endpointNumber,0x00,null);
}
/*查询配置信息*/
function searchConfigInfo(){
	sendCmdForAo(0,0x8010,["0"]);
}
/*改变绑定模式：改变为开关模式*/
function changeModeToSwitch(endpointNumber){
	sendCmdForAo(endpointNumber,0x8010,["1"]);
}
/*改变绑定模式：改变为绑定模式*/
function changeModeToBind(endpointNumber){
	sendCmdForAo(endpointNumber,0x8010,["2"]);
}
/*改变绑定模式：改变为场景模式*/
function changeModeToScene(endpointNumber){
	sendCmdForAo(endpointNumber,0x8010,["3"]);
}

function unBindingScene(epNum){
	showLog("appID="+appID);
	var jsonData = {
		"cmd": "513",
		"gwID": gwID,
		"devID":devID,
		"appID":appID,
		"mode": 3,
		"data":[{ "endpointNumber":epNum+""}]
	};
	sendCmd(jsonData);
}
/*绑定设备
 * ep：发起设备的端口,一键为1,二键为2…
 * bindDevId：绑定设备的ID
 * bindEp：绑定设备的端口一键为1,二键为2…
 * */
function BindingDev(ep,bindDevId,bindEp){
	var parm=[
		{"ep":ep+"",
		  "bindDevId":bindDevId,
		  "bindEp":bindEp+""
		}
	]
	sendCmdForAo(ep,0x8011,parm);
}
/*解绑，ep=0表示清除所有的绑定关系*/
function UnBindingDev(ep){
	sendCmdForAo(ep,0x8012,[ep+""]);
}
/*修改Ep的名称
endpointNumber：1，2，3分别表示1键、2键、3键
endpointName：名称
 * */
function updateEpName(endpointNumber,endpointName){
	 var jsonData = {
                "cmd":"502",
                "appID":appID,
                "gwID":gwID,
                "mode":2,
                "devID":devID,
                "endpointNumber":endpointNumber,
                "endpointName":endpointName
      }
	 sendCmd(jsonData);
}

/*改变绑定模式：改变为绑定模式*/
function changeModeToBind(endpointNumber){
	sendCmdForAo(endpointNumber,0x8010,["2"]);
}