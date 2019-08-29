var deviceInfo;/*当前设备的信息*/
var btn01_img,btn01_name,btn01_mode;
var btn02_img,btn02_name,btn02_mode;
var btn03_img,btn03_name,btn03_mode;
var body_layer;/*遮罩层*/
var switch_updatename,scene_edit,scene_delete,binding_again,binding_undo,layer_cancel;
var curEpNum="";//当前的epnum，在长按时使用到

function initBtn(){
	btn01_img=document.getElementById("btn01_img");
	btn01_name=document.getElementById("btn01_name");
	btn01_mode=document.getElementById("btn01_mode");
	btn01_img.tag="1";
	btn01_name.tag="1";
	btn01_mode.tag="1";
	
	btn02_img=document.getElementById("btn02_img");
	btn02_name=document.getElementById("btn02_name");
	btn02_mode=document.getElementById("btn02_mode");
	btn02_img.tag="2";
	btn02_name.tag="2";
	btn02_mode.tag="2";
	
	btn03_img=document.getElementById("btn03_img");
	btn03_name=document.getElementById("btn03_name");
	btn03_mode=document.getElementById("btn03_mode");
	btn03_img.tag="3";
	btn03_name.tag="3";
	btn03_mode.tag="3";
	
	body_layer=document.getElementById("body_layer");
	switch_updatename=document.getElementById("switch_updatename");
	scene_edit=document.getElementById("scene_edit");
	scene_delete=document.getElementById("scene_delete");
	binding_again=document.getElementById("binding_again");
	binding_undo=document.getElementById("binding_undo");
	layer_cancel=document.getElementById("layer_cancel");
}
function registerBtnEvent(){
	btn01_img.addEventListener("click",onClick_img);
	btn01_img.addEventListener("longtap",onLongTap_img_name);
	btn01_name.addEventListener("longtap",onLongTap_img_name);
	btn01_img.addEventListener("hold",onHold_img);
	btn01_img.addEventListener("release",onRelease_img);
	btn01_mode.addEventListener("click",onClick_mode);
	
	
	btn02_img.addEventListener("click",onClick_img);
	btn02_img.addEventListener("longtap",onLongTap_img_name);
	btn02_img.addEventListener("hold",onHold_img);
	btn02_img.addEventListener("release",onRelease_img);
	btn02_name.addEventListener("longtap",onLongTap_img_name);
	btn02_mode.addEventListener("click",onClick_mode);
	
	btn03_img.addEventListener("click",onClick_img);
	btn03_img.addEventListener("longtap",onLongTap_img_name);
	btn03_img.addEventListener("hold",onHold_img);
	btn03_img.addEventListener("release",onRelease_img);
	btn03_name.addEventListener("longtap",onLongTap_img_name);
	btn03_mode.addEventListener("click",onClick_mode);
	
	
	switch_updatename.addEventListener("tap",onclick_switch_updatename);
	scene_edit.addEventListener("tap",onclick_scene_edit);
	scene_delete.addEventListener("tap",onclick_scene_delete);
	binding_again.addEventListener("tap",onclick_binding_again);
	binding_undo.addEventListener("tap",onclick_binding_undo);
	layer_cancel.addEventListener("tap",onclick_layer_cancel);
//	body_layer.addEventListener("click",onclick_layer_cancel);
	
	setModeImg(btn01_img,0x0002,0);
	setModeDesc(btn01_name,0x0002,0);
	btn01_mode.innerText=getModeName(0x0002);
	setModeImg(btn02_img,0x0002,0);
	setModeDesc(btn02_name,0x0002,0);
	btn02_mode.innerText=getModeName(0x0002);
	setModeImg(btn03_img,0x0002,0);
	setModeDesc(btn03_name,0x0002,0);
	btn03_mode.innerText=getModeName(0x0002);
	
	toast_cancel();
}

/*按住图标*/
function onHold_img(){
	var eventid= window.event.srcElement.getAttribute("id");
	var epNum=getEpNumByBtnid(eventid);
	var theEpInfo=getEndPointByEpNum(epNum);
	if(theEpInfo.mode==con_mode_switch){
		document.getElementById(eventid).style.backgroundColor="#F2FAED";
	}
}
/*离开图标*/
function onRelease_img(){
	var eventid= window.event.srcElement.getAttribute("id");
	document.getElementById(eventid).style.backgroundColor="white";
}

/*单击图标*/
function onClick_img(){
	/* 1.在开关模式下
	 * 		切换开、关
	 * 2.非开关模式，若是空的绑定则
	 * 		2.1 绑定模式：跳转到设备列表选择设备
	 * 		2.2 场景模式：跳转到场景列表选择场景
	 * 3.非开关模式，若不是空绑定，则不起作用
	 * */
	var eventid= window.event.srcElement.getAttribute("id");
	var epNum=getEpNumByBtnid(eventid);
	var theEpInfo=getEndPointByEpNum(epNum);
	if(theEpInfo.mode != con_mode_switch) {
		if(G_deviceIsShare = true) {
			window.showDialog.show(Share_No_Permission,3000);
			return;
		}
	}
	curEpNum=epNum;

	if(theEpInfo!=null){
		if(theEpInfo.mode==con_mode_switch){
			document.getElementById(eventid).style.backgroundColor="#F2FAED";
			setTimeout(function(){
				document.getElementById(eventid).style.backgroundColor="white";
			},200);
			if(theEpInfo.switchStatus==0){
				powerOpen(epNum);
			}else{
				powerClose(epNum);
			}			
			
		}else if(theEpInfo.mode==con_mode_bind){
			if(isNull(theEpInfo.bindDevID)){
				var isBindAr=getIsBindAr();
				info.setItem("deviceID", devID);
	    		info.setItem("gwID", gwID);
	    		info.setItem("endpointNumber", curEpNum);
	    		info.setItem("isBindAr",isBindAr);
				var bindNum=getbindModeNum();
				window.location="bindDevice.html?bindNum="+bindNum;
			}
		}else if(theEpInfo.mode==con_mode_scene){
			if(isNull(theEpInfo.sceneID)){
				window.location="sceneList.html?sceneID=null&pageMode=0"
				+"&epNum="+epNum;
			}
		}
	}
	showLog(eventid+" 单击");
}
/*长按图标*/
function onLongTap_img_name(){
	/* 1.开关模式下
	 * 		呼出修改开关名称选项；
	 * 2.绑定模式下
	 * 		可以呼出重新绑定和解除绑定选项。
	 * 3.场景模式下
	 * 		呼出编辑和删除场景选项。
	 * */

	if(G_deviceIsShare == true) {
		window.showDialog.show(Share_No_Permission,3000);
		return;
	}
	var eventid= window.event.srcElement.getAttribute("id");
	document.getElementById(eventid).style.backgroundColor="white";
	var epNum=getEpNumByBtnid(eventid);
	curEpNum=epNum;
	var theEpInfo=getEndPointByEpNum(epNum);
	if(theEpInfo!=null){
		var isShowlayer=false;
		if(theEpInfo.mode==con_mode_switch){
			isShowlayer=true;
		}else if(theEpInfo.mode==con_mode_bind){
			if(!isNull(theEpInfo.bindDevID)){
				isShowlayer=true;
			}
		}else if(theEpInfo.mode==con_mode_scene){
			if(!isNull(theEpInfo.sceneID)){
				isShowlayer=true;
			}
		}
		if(isShowlayer){
			/*之所以添加这个代码，是因为layer隐藏时，有时候会触发这个事件*/
			btn03_mode.removeEventListener("click",onClick_mode);
			showlayer(theEpInfo.mode);
		}
	}
		
	showLog(eventid+" 单击长按:theEpInfo.mode="+theEpInfo.mode+" theEpInfo.sceneID="+theEpInfo.sceneID);
}
function onClick_mode(){
	/* 1.开关模式、绑定模式、场景界面
	 * 		进入切换模式界面;
	 * */
	if(G_deviceIsShare == true) {
		window.showDialog.show(Share_No_Permission,3000);
		return;
	}
	var eventid= window.event.srcElement.getAttribute("id");
	var epNum=getEpNumByBtnid(eventid);
	var theEpInfo=getEndPointByEpNum(epNum);	
	showLog(eventid+" 单击");
	var allUrl="changemode.html?devID="+devID
					+"&epNum="+epNum
					+"&epMode="+theEpInfo.mode;
	if(theEpInfo.bindDevtype==null||theEpInfo.bindDevtype==undefined||theEpInfo.bindDevtype==""){
		allUrl=allUrl+"&epbindType="+theEpInfo.bindDevtype
	}else{
		allUrl=allUrl+"&epbindType="+theEpInfo.bindDevtype
	}
	window.location=allUrl;
}

function testResult(){
	var result={
    "cmd":"500",
    "devID":"D7D5140B004B1200",
    "endpoints":[
        {
            "clusters":[
                {
                    "attributes":[
                        {
                            "attributeId":-1,
                            "attributeValue":""
                        }
                    ],
                    "clusterId":6
                }
            ],
            "endpointName":"",
            "endpointNumber":1,
            "endpointStatus":"1",
            "endpointType":2
        },
        {
            "clusters":[
                {
                    "attributes":[
                        {
                            "attributeId":-1,
                            "attributeValue":""
                        }
                    ],
                    "clusterId":7
                }
            ],
            "endpointName":"",
            "endpointNumber":2,
            "endpointStatus":"1",
            "endpointType":259
        },
        {
            "clusters":[
                {
                    "attributes":[
                        {
                            "attributeId":32769,
                            "attributeValue":"00303"
                        }
                    ],
                    "clusterId":5
                }
            ],
            "endpointName":"",
            "endpointNumber":3,
            "endpointStatus":"1",
            "endpointType":4
        }
    ],
    "extData":[
        {
            "bindDevID":"",
            "bindDevtype":"",
            "endpointNumber":"2",
            "epName":"",
            "mode":"2",
            "name":"",
            "sceneID":"",
            "sceneIcon":"",
            "sceneName":""
        },
        {
            "bindDevID":"",
            "bindDevtype":"",
            "endpointNumber":"3",
            "epName":"",
            "mode":"3",
            "name":"",
            "sceneID":"",
            "sceneIcon":"",
            "sceneName":""
        },
        {
            "bindDevID":"",
            "bindDevtype":"",
            "endpointNumber":"1",
            "epName":"",
            "mode":"1",
            "name":"",
            "sceneID":"",
            "sceneIcon":"",
            "sceneName":""
        }
    ],
    "gwID":"50294D20B3CC",
    "gwName":"",
    "messageCode":"wl/dev/gw/50294D20B3CC/data",
    "mode":0,
    "name":"#$default$#001",
    "roomID":"",
    "roomName":"",
    "time":"1500430485842",
    "type":"Ao"
};
	refreshData(result);	
	/*var isRight=isRightData(-1);
	console.log(isRight);*/
}

function refreshData(result){
//	console.log("refreshData");
	deviceInfo=result;
	if(!isNull(deviceInfo)){
		var endPoints=deviceInfo.endpoints;		
		var strJson="";
		if(deviceInfo.extData!=null&&deviceInfo.extData!=undefined&&deviceInfo.extData!=""){
			var base = new Base64(); 
			strJson=base.decode(deviceInfo.extData);
		}		
		var extData= null;
//		console.log("strJson="+strJson);
		if(strJson!=""&&strJson.indexOf("[")>=0&&strJson.indexOf("]")>=0){
//			extData = JSONArray.fromObject(strJson);
			extData= eval(strJson); 
		}
		showLog("endLen："+endPoints.length)
		if(!isNull(endPoints)&&endPoints.length>0){			
			for(var i=0;i<endPoints.length;i++){
				var endpointNumber=deviceInfo.endpoints[i].endpointNumber;/*确定是哪个键*/
				var endpointName=deviceInfo.endpoints[i].endpointName;/*确定是哪个键*/
				var endpointType=deviceInfo.endpoints[i].endpointType;/*确定这个键是什么模式*/
				var attribute=deviceInfo.endpoints[i].clusters[0].attributes[0];/*找某个属性*/
				var attributeId=attribute.attributeId;
				var attributeValue=attribute.attributeValue;
				showLog(endpointNumber+"键："
					+"endpointType="+endpointType
					+ " attributeId="+attributeId
					+ " attributeValue="+attributeValue);
				var isRight=isRightData(endpointType)&&attributeValue!="F";
				if(isRight==false){
					console.log("不合法的数据，忽略！");
					continue;
				}
				var theEpInfo=getEndPointByEpNum(endpointNumber);
				theEpInfo.mode=endpointType;
				showLog("theEpInfo.mode="+theEpInfo.mode+" endpointType="+endpointType);
				theEpInfo.endpointName=endpointName;
				var epExtData=null;
				if(extData!=null){
					epExtData=getExtDataByEpNum(extData,endpointNumber);
					if(epExtData==null){
						showLog("epExtData is null");
					}
				}else{
					showLog("extData is null");
				}
				
				if(endpointType==con_mode_switch){
					if(attributeId==0x0000&&!isNull(attributeValue)){
						theEpInfo.switchStatus=parseInt(attributeValue);
					}					
				}else if(endpointType==con_mode_bind){
					if(epExtData!=null){
						theEpInfo.bindDevID=epExtData.bindDevID;
						theEpInfo.bindDevtype=epExtData.bindDevtype;
						theEpInfo.bindDevName=epExtData.name;
						theEpInfo.bindDevEP=epExtData.bindDevEP;
//						theEpInfo.epName=epExtData.epName;
					}
//					else{
//						theEpInfo.bindingDevEpName="";
//						theEpInfo.bindDevID="";
//						theEpInfo.bindDevName="";
////						theEpInfo.epName="";
//					}
					
				}else if(endpointType==con_mode_scene){
					if(epExtData!=null){
						theEpInfo.sceneID=epExtData.sceneID;
						theEpInfo.sceneName=epExtData.sceneName;
						theEpInfo.sceneIcon=epExtData.sceneIcon;
					}
//					else{
//						theEpInfo.sceneID="";
//						theEpInfo.sceneName="";
//						theEpInfo.sceneIcon="";
//					}
				}
			}
			updateUIByEndPointInfo();
		}
	}
}
/*从扩展字段中根据EpNum获取该ep的信息*/
function getExtDataByEpNum(extData,epNum) {
	var epExtData=null;
	if(extData!=null&&extData.length>0){
		for(var i=0;i<extData.length;i++){
			if(extData[i].endpointNumber==epNum+""){
				epExtData=extData[i];
			}
		}
	}
	return epExtData;
}

function updateUIByEndPointInfo(){ 
	var epNum1=getEndPointByEpNum(1);
	var epNum2=getEndPointByEpNum(2);
	var epNum3=getEndPointByEpNum(3);
	
	if(epNum1!=null){
		btn01_img.style.backgroundColor="white";
		setModeImg(btn01_img,epNum1);
		setModeDesc(btn01_name,epNum1);
		btn01_mode.innerText=getModeName(epNum1.mode);
		btn01_mode.className="btnMode";
		console.log("btn01_mode.style.width="+btn01_mode.offsetWidth);
		btn01_mode.style.backgroundPositionX=(btn01_mode.offsetWidth-25)+"px";
		setModeImgText(btn01_img,epNum1);
		
	}
	if(epNum2!=null){
		btn02_img.style.backgroundColor="white";
		setModeImg(btn02_img,epNum2);
		setModeDesc(btn02_name,epNum2);
		btn02_mode.innerText=getModeName(epNum2.mode);
		btn02_mode.className="btnMode";
		btn02_mode.style.backgroundPositionX=(btn01_mode.offsetWidth-25)+"px";
		setModeImgText(btn02_img,epNum2);
	}
	if(epNum3!=null){
		btn03_img.style.backgroundColor="white";
		setModeImg(btn03_img,epNum3);
		setModeDesc(btn03_name,epNum3);
		btn03_mode.innerText=getModeName(epNum3.mode);
		btn03_mode.className="btnMode";
		btn03_mode.style.backgroundPositionX=(btn01_mode.offsetWidth-25)+"px";
		setModeImgText(btn03_img,epNum3);
	}
}
/*显示遮罩层
 * mode 1 开关
 * */
function showlayer(mode){
	document.getElementById("body_layer").style.display="block";
	if(mode==con_mode_switch){
		switch_updatename.style.display="block";
	}else if(mode==con_mode_bind){
		var isBindAr=getIsBindAr();
		if(isBindAr=="1"){
			binding_again.style.display="none";
			binding_undo.style.borderTopLeftRadius="10px";
			binding_undo.style.borderTopRightRadius="10px";
		}else{
			binding_again.style.display="block";
			binding_undo.style.borderTopLeftRadius="0px";
			binding_undo.style.borderTopRightRadius="0px";
		}		
		binding_undo.style.display="block";
	}else if(mode==con_mode_scene){
		scene_edit.style.display="block";
		scene_delete.style.display="block";
	}
}
/*隐藏遮罩层*/
function hidelayer(){
	document.getElementById("body_layer").style.display="none";
	switch_updatename.style.display="none";
	scene_edit.style.display="none";
	scene_delete.style.display="none";
	binding_again.style.display="none";
	binding_undo.style.display="none";
	setTimeout(function(){
		btn03_mode.addEventListener("click",onClick_mode);
	},200);	
}

function onclick_layer_cancel(){
	hidelayer();
}
function onclick_switch_updatename(){
	if(!isNull(curEpNum)){
		toast_show_forRename();
	}	
	hidelayer();
}
/*编辑场景*/
function onclick_scene_edit(){	
	if(!isNull(curEpNum)){
		var theEpInfo=getEndPointByEpNum(curEpNum);
		if(theEpInfo!=null){
			window.location="sceneList.html?pageMode=0&epNum="+curEpNum
			+"&sceneID="+theEpInfo.sceneID;
		}
	}
	hidelayer();
}
/*删除场景*/
function onclick_scene_delete(){
	if(!isNull(curEpNum)){
		toast_show_forask(30);
	}
	hidelayer();
}
/*重新绑定设备*/
function onclick_binding_again(){	
	if(!isNull(curEpNum)){
		var theEpInfo=getEndPointByEpNum(curEpNum);
		if(theEpInfo!=null){
			
			/*window.location="deviceList.html?pageMode=0&epNum="+curEpNum
			+"&bindDevID="+theEpInfo.bindDevID;*/
			var isBindAr=getIsBindAr();
			info.setItem("deviceID", devID);
    		info.setItem("gwID", gwID);
    		info.setItem("endpointNumber", curEpNum);
    		info.setItem("isBindAr",isBindAr);
    		var bindNum=getbindModeNum();
			window.location="bindDevice.html?bindNum="+bindNum;
		}
	}
	hidelayer();
}
/*解除绑定*/
function onclick_binding_undo(){
	if(!isNull(curEpNum)){
		var theEpInfo=getEndPointByEpNum(curEpNum);
		if(theEpInfo.bindDevtype!="Ar"){
			/*若解除的是窗帘控制器的绑定需要特殊提醒*/
			toast_show_forask(10);
		}else{
			toast_show_forask(11);
		}
	}
	hidelayer();
	
}

/*单独测试期间三个键分别是开关模式、绑定模式、场景模式*/
function getEpNumByBtnid(id){
	var epNum=0;
	if(id.indexOf("01_")>=0){
		epNum=1;
	}else if(id.indexOf("02_")>=0){
		epNum=2;
	}else if(id.indexOf("03_")>=0){
		epNum=3;
	}
	return epNum;
}
/*显示修改名称的弹窗*/
function toast_show_forRename(){
	document.getElementById("rename").style.display="block";
}

var asktoastType=0;
var isRunUnBinding=false;
/*显示询问弹窗
 * asktype:
 * 	10:解除普通设备的提醒；
 * 	11:解除窗帘控制器时的提醒;
 * 	20：三键金属开关的重新绑定；
 * 	30:删除场景的询问
 */
function toast_show_forask(asktype){
	if(asktype>0){
		var askContent="";
		asktoastType=asktype;
		var divaskContent=document.getElementById("askContent");
		if(asktoastType==10){
			askContent=switch_Ao_02;//"确定解绑该设备？";
			divaskContent.style.textAlign="center";
		}else if(asktoastType==11){
			askContent=switch_Ao_03;//"该绑定设备为窗帘控制器，解绑其中一个按键则三键全部解绑，确认全部解除绑定吗？";
			divaskContent.style.textAlign="center";
		}
		else if(asktoastType==20){
			askContent=switch_Ao_04;//"绑定窗帘控制器后，1键为开，2键为暂停，3键为关，当前按键数量不足，确认清空并重新绑定吗？";
			divaskContent.style.textAlign="left";
		}else if(asktoastType==30){
			askContent=switch_Ao_05;//"确定要删除该场景吗？";
			divaskContent.style.textAlign="center";
		}
		if(!isNull(askContent)){
			divaskContent.innerText=askContent;
			document.getElementById("toast_forask").style.display="block";
		}
	}
}

function toast_cancel(){
	asktoastType=0;
	document.getElementById("rename").style.display="none";
	document.getElementById("toast_forask").style.display="none";
}
function toast_delrename(){
	document.getElementById("renameInput").value="";
}
/*修改开关名称*/
function toast_surename(){
	var reName=document.getElementById("renameInput").value.trim();
    if (reName == '') {
        return;
    }
	showLog("reName="+reName);
	var tempEpNum=curEpNum;
	toast_cancel();
	createToast(switch_Ao_Saving);
	updateEpName(tempEpNum,reName);	
	showTimeOut(switch_Ao_timeout);//超时
}
/*询问弹窗确定事件*/
function toast_sureask(){
	var tempToastType=asktoastType;;
	toast_cancel();
	var theEpInfo=getEndPointByEpNum(curEpNum);
	if(tempToastType==10){/*解除普通设备*/
		createToast(swich_Ao_Unbundling);//正在解绑
		isRunUnBinding=true;
		UnBindingDev(curEpNum);
		showTimeOut(switch_Ao_timeout);
	}else if(tempToastType==11){/*解除窗帘控制器*/
		createToast(swich_Ao_Unbundling);//正在解绑		
		isRunUnBinding=true;
		UnBindingDev(0);
		showTimeOut(switch_Ao_timeout);
	}
	else if(tempToastType==20){/*绑定窗帘控制器时，按键不足时的提醒*/
		showLog("开始绑定窗帘控制器");
	}else if(tempToastType==30){/*删除场景时的提醒*/
		showLog(switch_Ao_10);//删除场景...
		unBindingScene(curEpNum);
	}
	
	
}

/*根据模式获取图片*/
function setModeImg(modeImg,epNumInfo){
	var imgPath="";
	var borderIsGrey=false;/*边框是否设置为灰色*/
	if(epNumInfo.mode==con_mode_switch){
		if(epNumInfo.switchStatus==0){
			borderIsGrey=true;
			imgPath="fonts/icon_btnclose.png";
		}else if(epNumInfo.switchStatus==1){
			imgPath="fonts/icon_btnopen.png";
		}	
	}else if(epNumInfo.mode==con_mode_bind){
		if(!isNull(epNumInfo.bindDevtype)){
			imgPath="../../source/deviceIcon/device_icon_"+epNumInfo.bindDevtype+".png";
		}		
	}else if(epNumInfo.mode==con_mode_scene){
		if(!isNull(epNumInfo.sceneIcon)){
			imgPath="../../source/sceneIcon/scene_normal_pre_"+epNumInfo.sceneIcon+".png";
		}
	}
	if(!isNull(imgPath)){		
		modeImg.style.backgroundImage="url('"+imgPath+"')";
	}else{
		borderIsGrey=true;
		modeImg.style.backgroundImage="url('fonts/icon_add.png')";
	}
	if(borderIsGrey){
		modeImg.style.borderColor="#E2E2E2";
	}else{
		modeImg.style.borderColor="#8DD652";
	}
//	setModeImgText(modeImg,epNumInfo);
}
function setModeImgText(modeImg,epNumInfo){
	if(epNumInfo.mode==con_mode_bind&&epNumInfo.bindDevtype=="Ar"){
		var epName=getEpBindDevEpName(epNumInfo);
		modeImg.innerText=epName;
	}else{
		modeImg.innerText="";
	}
}
/*设置模式名称上方的描述信息
 * 1.开关状态；
 * 2.设备名称；
 * 3.场景名称；
 * */
function setModeDesc(btnName,epNumInfo){
	var modeDesc="";
	var textIsGrey=false;/*字体颜色是否设置为灰色*/
	if(epNumInfo.mode==con_mode_switch){		
		modeDesc=getEpName(epNumInfo);
//		modeDesc="的事发时的发送到发送到发送到";
		if(epNumInfo.switchStatus==0){
			textIsGrey=true;
		}else if(epNumInfo.switchStatus==1){
			textIsGrey=false;
		}	
	}else if(epNumInfo.mode==con_mode_bind){
		if(!isNull(epNumInfo.bindDevID)){
			textIsGrey=false;
			modeDesc=getBindDevName(epNumInfo);//+getEpBindDevEpName(epNumInfo);
		}else{
			textIsGrey=true;
			modeDesc=switch_Ao_06;//"未绑定设备";//
		}
		
	}else if(epNumInfo.mode==con_mode_scene){
		if(!isNull(epNumInfo.sceneID)&&epNumInfo.sceneName!=null&&epNumInfo.sceneName!=undefined&&epNumInfo.sceneName!=""){
			textIsGrey=false;
			modeDesc=epNumInfo.sceneName;
		}else{
			textIsGrey=true;
			modeDesc=switch_Ao_07;//"未绑定场景";
		}
		
	}
	btnName.innerText=getDisplayName(modeDesc);
	if(textIsGrey){
		btnName.style.color="#000000";
	}else{
		btnName.style.color="#8DD652";	
	}
}

