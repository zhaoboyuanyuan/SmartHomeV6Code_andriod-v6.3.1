var itemHeight="45px";
var imgSize="32px";
var chooseSceneid="";
var bindingSceneid="";
var arrSceneid=new Array();
/*生成场景列表项*/
function createSceneItem(sceneid,scenename,sceneicon){
	var newItem=document.createElement("div");
	newItem.id="item_"+sceneid;
	newItem.tag=sceneid;
	newItem.style.backgroundColor="#FFFFFF";
	newItem.style.width="100%";
    newItem.style.overflow="hidden";
	/*1.场景图标*/
	if (!isNull(sceneid)) {
		var div_img=document.createElement("div");
		div_img.style.float="left";
		div_img.style.width="15%";
		div_img.style.height="45px";
		div_img.tag=sceneid;
		div_img.style.backgroundColor="#FFFFFF";
		var sceneImg=document.createElement("img");
		sceneImg.tag=sceneid;
		sceneImg.id="sceneImg_"+sceneid;
		sceneImg.style.height="32px";
		sceneImg.style.margin="5px";
		sceneImg.src="../../../source/sceneIcon/scene_normal_"+sceneicon+".png";
		div_img.appendChild(sceneImg);
		newItem.appendChild(div_img);
    }
	/*2.场景名称*/
	var div_name=document.createElement("div");
	div_name.style.float="left";
	div_name.style.width="78%";
	div_name.style.height="45px";
	div_name.style.backgroundColor="#FFFFFF";
	div_name.tag=sceneid;
	var sceneName=document.createElement("p");
	sceneName.id="sceneName_"+sceneid;
	sceneName.style.height=itemHeight;
	sceneName.style.lineHeight=itemHeight;
	sceneName.style.overflow="hidden";
	sceneName.style.marginLeft="20px";
	sceneName.style.fontWeight="bold";
    sceneName.style.width="90%";
    sceneName.style.backgroundColor="#FFFFFF";
    sceneName.style.textOverflow='ellipsis';
    sceneName.style.whiteSpace='nowrap';
	sceneName.innerText=scenename;
	sceneName.tag=sceneid;
	div_name.appendChild(sceneName);
	newItem.appendChild(div_name);

    if (isNull(sceneid)) {
        var div_unbind=document.createElement("div");
        div_unbind.style.float="left";
        div_unbind.style.width="15%";
        div_unbind.style.height="45px";
        div_unbind.tag=sceneid;
        newItem.appendChild(div_unbind);
    }

	/*3.选择场景后的钩*/
	var sceneChoose=document.createElement("img");
	sceneChoose.id="sceneChoose_"+sceneid;
	sceneChoose.src="../fonts/icon_choose.png";
	sceneChoose.style.float="left"
	sceneChoose.style.height="100%";
	sceneChoose.style.marginTop="16px";
	sceneChoose.style.visibility="hidden";
	sceneChoose.style.width="5%";
	sceneChoose.style.backgroundColor="#FFFFFF";
	newItem.appendChild(sceneChoose);
	var emptydiv=document.createElement("div");
	emptydiv.style.float="left";
	emptydiv.style.width="2%";
	emptydiv.style.height="45px";
	emptydiv.style.backgroundColor="#FFFFFF";
	emptydiv.innerText="";
	newItem.appendChild(emptydiv);
	/*4.注册事件*/
	newItem.onclick=itemOnClick;
	return newItem;
}
/*创建分割线*/
function createSplitLine(){
	var spliteLine=document.createElement("div");
	spliteLine.style.width="100%";
	spliteLine.style.height="1px";
	spliteLine.style.background="#E2E2E2";
//	spliteLine.align="bottom";
	spliteLine.style.float="left";
	return spliteLine;
}
/*生成场景列表*/
function createSceneList(result){
	arrSceneid.splice(0,arrSceneid.length);//清空数组
	chooseSceneid="";
	var bindingScene=document.getElementById("bindingScene");
	bindingScene.style.display="none";
	var divSceneList=document.getElementById("divSceneList");
	divSceneList.innerHTML="";

	//MARK: 试验
    var nullItem=createSceneItem("",lock_Scenes_bind_dont,"");
    divSceneList.appendChild(nullItem);
    divSceneList.appendChild(createSplitLine());


	var i=0;
	for(var item in result){		
//		console.log(item.toString());
		var sceneID=result[item]["sceneID"];
		arrSceneid[i]=sceneID;
		i++;
		var sceneName=result[item]["sceneName"];
		var sceneIcon=result[item]["sceneIcon"];
		var newItem=createSceneItem(sceneID,sceneName,sceneIcon);
		divSceneList.appendChild(newItem);
		divSceneList.appendChild(createSplitLine());
	}

    arrSceneid.push("")
}


function itemOnClick(){
	var sceneChoose="";
	var Sceneitem=document.getElementById(window.event.srcElement.id);
	 if(Sceneitem.tag!=undefined&&Sceneitem.tag!=chooseSceneid){
	 	chooseSceneid=Sceneitem.tag;
		for(var i=0;i<arrSceneid.length;i++){
			var imgid="sceneChoose_"+arrSceneid[i];
			document.getElementById(imgid).style.visibility="hidden";
		}
		 console.log("chooseSceneid="+chooseSceneid);
		 selectItem(chooseSceneid);
	 }	 
}

function selectItem(sceneid){
	// if(sceneid!=null&&sceneid!=''&&sceneid!="null"){
		var sceneChoose="sceneChoose_"+sceneid;	
		document.getElementById(sceneChoose).style.visibility="visible";
		document.getElementById("bindingScene").style.display="block";
	// } else {
	//
	// }
}


function resetItem(){
	document.getElementById("bindingScene").style.display="none";
	chooseSceneid="";
	bindingSceneid="";
	var divSceneList=document.getElementById("divSceneList");
	if(divSceneList!=null&&divSceneList!=undefined){
		var imgList= divSceneList.getElementsByTagName("img");
		if(imgList!=null&&imgList.length>0){
			for(var i=0;i<imgList.length;i++){
				if(imgList[i].id.contains("sceneChoose_")){
					imgList[i].style.visibility="hidden";
				}
			}
		}
	}
}

/**
 * Created by Administrator on 2017/6/14.
 */
function createToast(msg){
    var sec = '<section class="comToast"><i><em class="rotate"></em>'+msg+'</i></section>'
    $("body").append(sec)
}