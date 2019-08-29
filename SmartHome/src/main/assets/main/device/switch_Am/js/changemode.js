var chooseMode=null;
var divMode_Switch,divMode_binding,divMode_secne;
var divMode_Switch_check,divMode_binding_check,divMode_scene_check;
function initModeList(mode){
	
	if(mode==con_mode_switch){
		divMode_Switch.style.display="none";
		document.getElementById("divCurMode_img").src="fonts/icon_modeswitch.png";
		document.getElementById("line1").style.display="none";
	}else if(mode==con_mode_bind){
		divMode_binding.style.display="none";
		document.getElementById("divCurMode_img").src="fonts/icon_modebinding.png";
		document.getElementById("line2").style.display="none";
	}else if(mode==con_mode_scene){
		divMode_secne.style.display="none";
		document.getElementById("divCurMode_img").src="fonts/icon_modescene.png";
		document.getElementById("line3").style.display="none";
	}
	var curModeName=getModeName(mode);
	document.getElementById("divCurMode_p1").innerText=curModeName;
}

function initUIDev(){
	divMode_Switch=document.getElementById("divMode_Switch");
	divMode_binding=document.getElementById("divMode_binding");
	divMode_secne=document.getElementById("divMode_secne");
	divMode_Switch_check=document.getElementById("divMode_Switch_check");
	divMode_binding_check=document.getElementById("divMode_binding_check");
	divMode_scene_check=document.getElementById("divMode_scene_check");
	
	divMode_Switch.addEventListener("click",onclick_modeItem);
	divMode_binding.addEventListener("click",onclick_modeItem);
	divMode_secne.addEventListener("click",onclick_modeItem);
}

function onclick_modeItem(){
	var eventid= window.event.srcElement.getAttribute("id");
	resetCheck();
	if(eventid=="divMode_Switch"){
		divMode_Switch_check.style.display="block";
		chooseMode=con_mode_switch;
	}else if(eventid=="divMode_binding"){
		divMode_binding_check.style.display="block";
		chooseMode=con_mode_bind;
	}else if(eventid=="divMode_secne"){
		divMode_scene_check.style.display="block";
		chooseMode=con_mode_scene;		
	}
	if(!isNull(chooseMode)){
		document.getElementById("submit").style.display="block";
	}
}

function resetCheck(){
	divMode_Switch_check.style.display="none";
	divMode_binding_check.style.display="none";
	divMode_scene_check.style.display="none";
}
var couldSubmit = true;

/*提交*/
function submit_complete(){
	if (!couldSubmit) {
		return;
	}
    couldSubmit = false;
	if(!isNull(chooseMode)){
		var intEpNum=parseInt(epNum);
		if(chooseMode==con_mode_switch){
			isBinding=true;
			changeModeToSwitch(intEpNum);
		}else if(chooseMode==con_mode_scene){
			isBinding=true;
			changeModeToScene(intEpNum);
		}else if(chooseMode==con_mode_bind){
			isBinding=true;
			changeModeToBind(intEpNum);
		}
		if(isBinding){
			createToast(languageUtil.getResource("isSaving"));
		}			
	}
}



