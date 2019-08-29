function showLog(msg){
	console.log(msg);
}
function isNull(arg) {
	var isnull = arg == null || arg == undefined || arg == "undefined" || arg == "";
	
	return isnull;
}
/*判断是否是合法数据*/
function isRightData(data){
	var isRight=false;	
	if(data!=null&&parseInt(data)>=parseInt("0")){
		 isRight=true;
	}
	return isRight;
}
/*根据模式编码获取模式名称*/
function getModeName(modeCode){
	var curModeName="";
	if(modeCode==con_mode_switch){
		curModeName=languageUtil.getResource("modeSwitch");	
	}else if(modeCode==con_mode_bind){
		curModeName=languageUtil.getResource("modeBind");
	}else if(modeCode==con_mode_scene){
		curModeName=languageUtil.getResource("modeScene");
	}
	return curModeName;
}
function showTimeOut(msg){
		setTimeout(function() {
					$(".comToast").hide();
					window.showDialog.show(msg,2000);
		}, 60000)
}
function showSucToast(msg,ggg){
	setTimeout(function() {
					$(".comToast").hide();
					$("#fail").show();
					$("#toastText").html(msg);
					setTimeout(function() {
						$("#fail").hide();
						ggg();
					}, 600)
		},500)
}
/**
 * Created by Administrator on 2017/6/14.
 */
function createToast(msg){
    var sec = '<section class="comToast"><i><em class="rotate"></em>'+msg+'</i></section>'
    $("body").append(sec)
}
function closeToast(){
	$(".comToast").hide();
}

