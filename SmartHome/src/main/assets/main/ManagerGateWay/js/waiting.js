function showWaiting()
{
	$(".waiting").css("display","block");
	setTimeout(function() {
  		$(".waiting").css("display","none");
  	}, 15000);
}

function hideWaiting()
{
	$(".waiting").css("display","none");
}

var toast  = function(msg){  
duration = 1000 * 3;
  duration = isNaN(duration) ? 3000 : duration;
  var m = document.createElement('div');
  m.innerHTML = msg;
  m.style.cssText = "background: rgb(0, 0, 0);\
                     opacity: 0.6;\
                     color: rgb(255, 255, 255);\
                     text-align: center;\
                     border-radius: 5px;\
                     position: fixed;\
                     bottom: 50%;\
                     z-index: 999999;\
                     font-weight: 500;\
                     display: inline;\
                     padding: 15px;\
                     font-family: 'Lucida Grande', 'Helvetica', sans-serif;  \
                     font-size: 1.5rem;\
                     max-width: 70%";
  document.body.appendChild(m);
  m.style.left = ((document.body.clientWidth - m.offsetWidth) / 2) + 'px';
  setTimeout(function() {
    var d = 0.5;
    m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
    m.style.opacity = '0';
    setTimeout(function() { document.body.removeChild(m) }, d * 1000);
  }, duration);
}
 
var containHtml;
//自定义内容,两个个按钮
function showDouble(title, content, cancleText, sureText, callback){
    if(containHtml)return;
    containHtml = '<div class="cover"><div id="tipView"> <div id="tv_title"></div> <div id="tv_content"></div><div> <div id="tv_cancleBtn"></div> <div id="tv_lineVer"></div><div id="tv_sureBtn"></div></div> </div></div>';
    $(document).find("body").append(containHtml);
    $(".cover").css({"background": "rgba(0,0,0,0.5)","position": "fixed", "top": "0", "left": "0", "width": "100%","height": "100%"});
    $("#tipView").css({"position":"fixed","padding-bottom": "0px","left":"4rem","right":"4rem","border-radius":"8px", "box-shadow":"0 0 10px 5px rgba(0, 0, 0, .1), 0 0 10px 5px rgba(0, 0, 0, .1), 0 0 10px 5px rgba(0, 0, 0, .1)","bottom":"35%","margin-bottom":"-30px","background-color":"#fff","text-align":"center","z-index": "1000"});
    $("#tv_title").css({"background-color":"#f3f3f3","border-top-left-radius":"8px","border-top-right-radius":"8px","height": "4rem","line-height":"4rem","text-align": "center","font-size": "2rem"});
    $("#tv_content").css({"height":"10rem", "padding":"30px","font-size":"1.5rem", "display":"-webkit-box","display":"-ms-flexbox","display":"-webkit-flex",display:"flex","-webkit-box-pack":"center","-ms-flex-pack":"center","-webkit-justify-content":"center","justify-content":"center","-webkit-box-align":"center","-ms-flex-align":"center","-webkit-align-items":"center","margin-bottom":"15px"});
    $("#tv_cancleBtn").css({"border-top": "solid 1px gray",'float':'left', "height": "4rem","color":"#000000","width":"49.7%","line-height":"4rem","font-size":"2rem"});
    $("#tv_lineVer").css({ "float":"left","height": "4rem","background-color":"gray","width":"0.3%"});
    $("#tv_sureBtn").css({"border-top": "solid 1px gray",'float':'right',"height": "4rem","color":"#5ec805","width":"50%","line-height":"4rem","font-size":"2rem"});
    $("#tv_cancleBtn").text(cancleText);
    $("#tv_sureBtn").text(sureText);
    showTips(title,content,callback);
    $(".cover").bind("click",removeFromSuperDiv);
    $("#tipView").bind("click",function(event){
        event.stopPropagation();
    });
}

function showTips(title,content,callback) {
    if(!content||content=="")return;
    $("#tv_title").text(title);
    $("#tv_content").text(content);
    $("#tv_sureBtn").click(function () {
        if(callback)callback();
        removeFromSuperDiv();
    });
    $("#tv_cancleBtn").click(function () {
        removeFromSuperDiv();
    });
}

<!--移除弹框-->
function removeFromSuperDiv(){
    $(".cover").remove();
    containHtml=null;
}
