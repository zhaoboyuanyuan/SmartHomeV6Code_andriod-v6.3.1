
/*人体侦测开关：开、关*/
function onclick_alarmOpen(){
	var data_state=$("#alarmOpen").find("span").attr("data-state");
	console.log(data_state);
	swichOnOff(data_state);
	if(data_state=="on"){
		saveSettingPir('0','10');
		saveSettingCamera(0,1);
	} else {
        saveSettingPir('40','10');
	}
}

function swichOnOff(data_state){
	if(data_state=="on"){
		$("#alarmOpen").find("span").addClass("gray")
    	$("#alarmOpen").find("span").attr("data-state","off")
    	$("#alarmSensitivity")[0].style.display="none";
    	$("#alarmStay")[0].style.display="none";
    	$("#alarmNote")[0].style.display="none";
    	$("#alarmDesc")[0].style.display="none";
//  	$("#divOk")[0].style.display="none";
	}else if(data_state=="off"){
		$("#alarmOpen").find("span").removeClass("gray")
        $("#alarmOpen").find("span").attr("data-state","on")
        $("#alarmSensitivity")[0].style.display="block";
        $("#alarmStay")[0].style.display="block";
    	$("#alarmNote")[0].style.display="block";
    	$("#alarmDesc")[0].style.display="block";
//  	$("#divOk")[0].style.display="block";
	}
}

/*选择人体侦测灵敏度*/
function onclick_alarmSensitivity(){
//	 document.getElementById("bodylayer").style.display="block";
	 var picker=new mui.PopPicker();
	 picker.setData(
		 	[
		 		{value:'40',text:alarmSetting_js_04},/*高*/
		 		{value:'60',text:alarmSetting_js_08},/*低*/
		 	]
	 	);
	 
	 picker.show(function (selectItems) {  
	 	alarmSensitivity_value=selectItems[0].value;
	 	document.getElementById("alarmSensitivityLevel").innerText=selectItems[0].text;
//		document.getElementById("bodylayer").style.display="none";
		saveSettingPir(alarmSensitivity_value,alarmStay_value);
		document.getElementsByClassName("mui-poppicker")[0].remove();
		
	 })  
	 langBtnCancelOk();
//	 pick_setOnclck();
//	 pick_setTitle("人体侦测灵敏度");
}

/*选择逗留检测时间*/
function onclick_alarmStay(){
//	 document.getElementById("bodylayer").style.display="block";
	 var picker=new mui.PopPicker();
	 picker.setData(
		 	[
		 		{value:'5',text:'5s'},
		 		{value:'10',text:'10s'},
		 		{value:'15',text:'15s'},
		 	]
	 	);
	 picker.show(function (selectItems) {  
	 	alarmStay_value=selectItems[0].value;
	 	document.getElementById("alarmStayTime").innerText=selectItems[0].text;
//		document.getElementById("bodylayer").style.display="none";
		saveSettingPir(alarmSensitivity_value,alarmStay_value);
		document.getElementsByClassName("mui-poppicker")[0].remove();
	 })  
	 langBtnCancelOk();
}

/*选择记录类型*/
function onclick_alarmNote(){
//	document.getElementById("bodylayer").style.display="block";
	 var picker=new mui.PopPicker();
	 picker.setData(
		 	[
		 		{value:'0',text:alarmSetting_js_07},/*无*/
		 		{value:'1',text:alarmSetting_js_10},/*抓拍*/
		 		{value:'2',text:alarmSetting_js_11},/*录像*/
		 	]
	 	);
	 picker.show(function (selectItems) {  
	 	alarmStay_value=selectItems[0].value;
	 	document.getElementById("alarmNoteState").innerText=selectItems[0].text;
//		document.getElementById("bodylayer").style.display="none";
		saveSettingCamera(alarmStay_value,1);
		document.getElementsByClassName("mui-poppicker")[0].remove();
	 })  
	 langBtnCancelOk();
//	 pick_setTitle("记录类型");
//	 pick_setOnclck();
}
function langBtnCancelOk(){
	var btnCancels= document.getElementsByClassName("mui-poppicker-btn-cancel");
	var btnOks=document.getElementsByClassName("mui-poppicker-btn-ok");
	for(var i=0;i<btnCancels.length;i++){
		btnCancels[i].innerHTML=cancel;
	}
	for(var i=0;i<btnOks.length;i++){
		btnOks[i].innerHTML=sure;
	}
}
/*对picker的改造*/
/*语言*/
function pick_initLang(){
//	var btnOk=document.getElementsByClassName("mui-btn mui-btn-blue mui-poppicker-btn-ok")[0];
//	var btnCancel=document.getElementsByClassName("mui-poppicker-btn-cancel")[0];
//	btnOk.style.backgroundColor="transparent";
//	btnOk.style.color="#99CCFF";
//	btnOk.style.zIndex="999";
//	btnCancel.style.zIndex="999";
//	btnCancel.style.zIndex="999";
}
function pick_setTitle(title){
	var divTitle=document.getElementsByClassName("mui-poppicker-header")[0];
	var input_title=document.createElement("div");
	input_title.innerText=title;
	input_title.textAlign="center";
	input_title.style.width=(document.documentElement.clientWidth-160)+"px";
	input_title.style.backgroundColor="transparent";
	input_title.style.position="relative";
	input_title.style.marginTop="-25px";
	input_title.style.float="left";
	input_title.style.marginLeft="80px";
	input_title.style.marginRight="80px";
	input_title.align="center";
	input_title.style.zIndex="1";
	input_title.style.color="#000000"
	divTitle.appendChild(input_title);
}
function pick_setOnclck(){
	pick_initLang();
	var btnCancel=document.getElementsByClassName("mui-poppicker-btn-cancel")[0];
	btnCancel.onclick=function(){
//		document.getElementById("bodylayer").style.display="none";
		document.getElementsByClassName("mui-poppicker")[0].remove();
	};
	
}
/**
 * Created by Administrator on 2017/6/14.
 */
function createToast(msg){
    var sec = '<section class="comToast"><i><em class="rotate"></em>'+msg+'</i></section>'
    $("body").append(sec)
}