var chooseMode=null;
var divMode_Switch,divMode_binding,divMode_secne;
var divMode_Switch_check,divMode_binding_check,divMode_scene_check;
function initModeList(mode){
	
	if(mode==0x0002){
		divMode_Switch.style.display="none";
		document.getElementById("divCurMode_img").src="../fonts/icon_modeswitch.png";
		document.getElementById("line1").style.display="none";
	}else if(mode==0x0103){
		divMode_binding.style.display="none";
		document.getElementById("divCurMode_img").src="../fonts/icon_modebinding.png";
		document.getElementById("line2").style.display="none";
	}else if(mode==0x0004){
		divMode_secne.style.display="none";
		document.getElementById("divCurMode_img").src="../fonts/icon_modescene.png";
		document.getElementById("line3").style.display="none";
	}
	var curModeName=getModeName(mode);
	document.getElementById("divCurMode_p1").innerText=curModeName;
}

/*根据模式编码获取模式名称*/
function getModeName(modeCode){
    var curModeName="";
    if(modeCode==0x0002){
        curModeName=modeSwitch; //"开关模式";
    }else if(modeCode==0x0103){
        curModeName=modeBind;//"绑定模式";
    }else if(modeCode==0x0004){
        curModeName=modeScene;//"场景模式";
    }
    return curModeName;
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
		chooseMode=0x0002;
	}else if(eventid=="divMode_binding"){
		divMode_binding_check.style.display="block";
		chooseMode=0x0103;
	}else if(eventid=="divMode_secne"){
		divMode_scene_check.style.display="block";
		chooseMode=0x0004;
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
/*提交*/
function submit_completeBind(){
	if(!isNull(chooseMode)){		
		var intEpNum=parseInt(epNum);
		if(chooseMode==0x0002){
			isBinding=true;
			changeModeToSwitch(intEpNum);
		}else if(chooseMode==0x0004){
			isBinding=true;
			changeModeToScene(intEpNum);
		}else if(chooseMode==0x0103){
			isBinding=true;
			changeModeToBind(intEpNum);
		}
		if(isBinding){
			console.log("开始更改模式");
//			createToast("");
		}			
	}
}



