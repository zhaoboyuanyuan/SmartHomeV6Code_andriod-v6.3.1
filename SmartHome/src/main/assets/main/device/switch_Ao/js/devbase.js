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
		curModeName=window.switch_mode_01; //"开关模式";	
	}else if(modeCode==con_mode_bind){
		curModeName=window.switch_mode_02;//"绑定模式";
	}else if(modeCode==con_mode_scene){
		curModeName=window.switch_mode_03;//"场景模式";
	}
	return curModeName;
}
function showTimeOut(msg){
		setTimeout(function() {
					$(".comToast").hide();
					window.showDialog.show(msg,2000);
		}, 10000)
}
function showTimeOutCallBack(msg,callback){
		setTimeout(function() {
			$(".comToast").hide();
			window.showDialog.show(msg,2000);
            callback();
		}, 10000)
}
function showSucToast(msg,callback){
	setTimeout(function() {
					$(".comToast").hide();
					$("#fail").show();
					$("#toastText").html(msg);
					setTimeout(function() {
						$("#fail").hide();
						callback();
					}, 600)
		},500)
}
function showSucToastLong(msg,callback){
	setTimeout(function() {
					$(".comToast").hide();
					$("#fail").show();
					$("#toastText").html(msg);
					setTimeout(function() {
						$("#fail").hide();
						callback();
					}, 600)
		},1500)
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
