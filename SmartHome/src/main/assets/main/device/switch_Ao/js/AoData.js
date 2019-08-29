/* 问：为什么要自己设置一个对象，而不是直接使用网关中的数据？
 * 答：因为网关直接返回的数据比较零碎，有时候可能是分步返回的，不完整。还有冗余数据，所以不能直接使用网关中的数据。
 * */

var AoEndPointInfo;

/*三键金属开关的实体类*/
function initEndPoint(epNum){
	var endPoint={};
	endPoint.endpointNumber=epNum;/*端点编号*/
	endPoint.mode=con_mode_switch;/*模式编号*/
	endPoint.switchStatus=0;/*开关状态，默认关*/
	endPoint.endpointName="";/*按键名称*/
	endPoint.bindDevID="";/*绑定模式下：绑定的设备*/
	endPoint.bindDevtype="";/*绑定模式下，绑定设备的类型*/
	endPoint.bindDevName="";/*绑定模式下：绑定设备的名称*/
	endPoint.bindDevEP="";/*绑定模式下，绑定设备的端点*/
	endPoint.bindingDevEpName="";/*绑定模式下：绑定设备的名称*/
	endPoint.sceneID="";/*场景ID*/
	endPoint.sceneName="";/*场景名称*/
	endPoint.sceneIcon="";/*绑定场景*/
	return endPoint;
}
function initEndPointForTest(epNum){
	var endPoint={};
	endPoint.endpointNumber=epNum;/*端点编号*/
	endPoint.mode=con_mode_bind;/*模式编号*/
	endPoint.switchStatus=0;/*开关状态，默认关*/
	endPoint.endpointName="";/*按键名称*/
	endPoint.bindDevID="001";/*绑定模式下：绑定的设备*/
	endPoint.bindDevtype="Ar";/*绑定模式下，绑定设备的类型*/
	endPoint.bindDevName="";/*绑定模式下：绑定设备的名称*/
	endPoint.bindDevEP="1";/*绑定模式下，绑定设备的端点*/
	endPoint.bindingDevEpName="2223";/*绑定模式下：绑定设备的名称*/
	endPoint.sceneID="1";/*场景ID*/
	endPoint.sceneName="01";/*场景名称*/
	endPoint.sceneIcon="01";/*绑定场景*/
	return endPoint;
}
/*初始化开关各个端点的信息*/
function initAllEndPointForTest(devType){
	var devEndPoints={};
	devEndPoints.devType=devType;
	devEndPoints.devID="";
	devEndPoints.endPoints=new Array();
	devEndPoints.endPoints.push(initEndPointForTest(1));
	devEndPoints.endPoints.push(initEndPointForTest(2));
	devEndPoints.endPoints.push(initEndPointForTest(3));
	return devEndPoints;
}
/*初始化开关各个端点的信息*/
function initAllEndPoint(devType){
	var devEndPoints={};
	devEndPoints.devType=devType;
	devEndPoints.devID="";
	devEndPoints.endPoints=new Array();
	devEndPoints.endPoints.push(initEndPoint(1));
	devEndPoints.endPoints.push(initEndPoint(2));
	devEndPoints.endPoints.push(initEndPoint(3));
	return devEndPoints;
}

function Test_ForData(){
	/*下面几句是造的假数据*/
	Test_setEpmode(1,con_mode_switch);
	Test_setEpmode(2,con_mode_scene);
	Test_setEpmode(3,con_mode_bind);
}

function Test_setEpmode(epNum,epMode){
	var theEpInfo=getEndPointByEpNum(epNum);
	if(theEpInfo!=null){		
		theEpInfo.mode=epMode;
	}
}

/*设置设备ID*/
function setDevAoDevID(devID){
	AoEndPointInfo.devID=devID;
}
/*根据Ep获取当前缓存中的端点信息*/
function getEndPointByEpNum(epNum){
	var theEpInfo=null;
	if(AoEndPointInfo!=null&&AoEndPointInfo.endPoints!=null&&AoEndPointInfo.endPoints.length>0){
		for(var i=0;i<AoEndPointInfo.endPoints.length;i++){
			if(AoEndPointInfo.endPoints[i].endpointNumber==epNum){
				theEpInfo=AoEndPointInfo.endPoints[i];
			}
		}
	}
	return theEpInfo;
}

/*获取当前按键的绑定模式*/
function getCurMode(epNum){
	var curMode=0;
	var theEpInfo=getEndPointByEpNum(epNum);
	if(theEpInfo!=null){
		curMode=theEpInfo.mode;
	}
	return curMode;
}
function getIsBindAr(){
	var isBindAr="0";
	if(AoEndPointInfo!=null&&AoEndPointInfo.endPoints!=null&&AoEndPointInfo.endPoints.length>0){
		for(var i=0;i<AoEndPointInfo.endPoints.length;i++){
			if(AoEndPointInfo.endPoints[i].bindDevtype=="Ar"){
				isBindAr="1";
				break;
			}
		}
	}
	return isBindAr;
}
/*获取按键的名称*/
function getEpName(epInfo){
	var epName="";
	if(epInfo.endpointName==null||epInfo.endpointName==""){
		if(epInfo.mode==con_mode_switch){
			epName=switch_Ao_01+epInfo.endpointNumber;//按键
		}
	}else{
		epName=epInfo.endpointName;
	}
	return epName;
}
/*获取绑定设备的名称*/
function getBindDevName(epInfo){
	var bindDevName="";
	if(epInfo.bindDevName==null||epInfo.bindDevName==""||epInfo.bindDevName.indexOf("#$default$#")!=-1){
		if(epInfo.bindDevtype=="Am"){
			bindDevName=switch_Am_Name;//"一键金属开关";
		}else if(epInfo.bindDevtype=="An"){
			bindDevName=switch_An_Name;//"二键金属开关";
		}else if(epInfo.bindDevtype=="Ao"){
			bindDevName=switch_Ao_Name;//"三键金属开关";
		}else if(epInfo.bindDevtype=="Ar"){
			bindDevName=switch_Ar_Name;//"金属窗帘控制器";
		}
	}else{
		bindDevName=epInfo.bindDevName;
	}
	return bindDevName;
	
}
/*获取绑定的按键的名称*/
/*function getEpBindDevName(epInfo){
	var devName="";
	if(epInfo.bindDevName==null||epInfo.bindDevName==""){
		if(epNumInfo.bindDevtype=="Am"){
			devName="一键金属开关";
		}else if(epNumInfo.bindDevtype=="An"){
			devName="二键金属开关";
		}else if(epNumInfo.bindDevtype=="Am"){
			devName="三键金属开关";
		}else if(epNumInfo.bindDevtype=="Ar"){
			devName="金属窗帘控制器";
		}
	}
	return devName;
}*/

/*获取绑定的按键Ep的名称*/
function getEpBindDevEpName(epInfo){
	var devEpName="";
	if(epInfo.bindDevtype!="Ar"){/*金属窗帘控制器需要做特殊判断*/
		if(epInfo.bindingDevEpName==null||epInfo.bindingDevEpName==""){
			devEpName=switch_Ao_01+epInfo.bindDevEP;//按键
		}
	}else{
		if(epInfo.endpointNumber==1){
			devEpName=switch_Ao_On;//开
		}else if(epInfo.endpointNumber==2){
			devEpName=switch_Ao_Off;//关
		}else if(epInfo.endpointNumber==3){
			devEpName=switch_Ao_Stop;//停
		}
	}	
	return devEpName;
}

function getDisplayName(organName){
	var languageCode = window.sysfun.getLang();
	var maxLen=16;
	if(languageCode=="zh-cn"){
		maxLen=8;
	}
	var displayName="";
	if(organName!=null&&organName!=undefined&&organName!=""){
		if(organName.length>maxLen){
			displayName=organName.substr(0,maxLen+1)+"...";
		}else{
			displayName=organName;
		}
	}else{
		displayName="----";
	}
	
	return displayName;
}
/*获取绑定模式且是空绑定的数量*/
function getbindModeNum(){
	var bindNum=0;
	if(AoEndPointInfo!=null&&AoEndPointInfo.endPoints!=null&&AoEndPointInfo.endPoints.length>0){
		for(var i=0;i<AoEndPointInfo.endPoints.length;i++){
			var right=AoEndPointInfo.endPoints[i].mode==con_mode_bind/*判断是否是绑定模式*/
					&&( /*判断是否是空绑定*/
						AoEndPointInfo.endPoints[i].bindDevID==undefined
						||AoEndPointInfo.endPoints[i].bindDevID==null
						||AoEndPointInfo.endPoints[i].bindDevID==""
					)
			if(right){
				bindNum++;
			}
		}
	}
	return bindNum;
}



