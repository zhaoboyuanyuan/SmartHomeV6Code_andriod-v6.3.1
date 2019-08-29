var lockPWD = "";
var numlimmitmax = 9;//密码长度最大限制
var numlimmitmin = 6;//密码长度最小限制
function load(){
	var info = window.sysfun;
	info.init("V6DoorLockVOC");
//	var info = window.localStorage;
//	window.getItem();window.setItem()
    var btnList = document.getElementsByClassName("lockKeyBoard")[0].getElementsByTagName("span");
    for(var i=0;i<12;i++){
        btnList[i].children[0].addEventListener("touchstart",function(){
            this.style.background = "rgba(0,0,0,.4)";
            if(this.id === "lockKeyBoard_Clean"){
                this.style.background = "rgba(0,0,0,.4) url(fonts/icon_doorLock_del_nor.png) no-repeat center center";
                this.style.backgroundSize = "1.8rem auto";
            }else if(this.id === "lockKeyBoard_Submit"){
                this.style.background = "rgba(0,0,0,.4) url(fonts/icon_doorLock_open_nor.png) no-repeat center center";
                this.style.backgroundSize = "2.7rem auto";
            }
        });
        btnList[i].children[0].addEventListener("touchend",function(){
            this.style.background = "#e5e5e5";
            if(this.id === "lockKeyBoard_Clean"){
                this.style.background = "#e5e5e5 url(fonts/icon_doorLock_del_nor.png) no-repeat center center";
                this.style.backgroundSize = "1.8rem auto";
            }else if(this.id === "lockKeyBoard_Submit"){
                this.style.background = "#e5e5e5 url(fonts/icon_doorLock_open_nor.png) no-repeat center center";
                this.style.backgroundSize = "2.7rem auto";
            }
        });
    }
	document.getElementsByClassName("lockKeyBoard")[0].addEventListener("click",function(){
        plus.gatewayCmd.isLockClick(function(){});
		var num = event.target.textContent;
		//输入
		if(num.length > 0 && num.length < 3 && lockPWD.length < numlimmitmax){
			lockPWD += num;
			var notice = document.getElementsByClassName("lockStateNotice")[0].getElementsByTagName("a")[0];
			notice.textContent = lang_DoorLookInputPWD;
		}
		//密码输入框的状态
			var imglength = document.getElementsByClassName("lockPWDInputBg")[0].getElementsByTagName("a").length;
			if(imglength > lockPWD.length){
				for(var i = imglength; i>lockPWD.length;i--){
					var parent=document.getElementsByClassName("lockPWDInputBg")[0];
					var child=document.getElementsByClassName("lockPWDInputBg")[0].getElementsByTagName("a")[i-1];
					parent.removeChild(child);
				}
			}else{
				for(var i = imglength; i < lockPWD.length;i++){
					var element=document.getElementsByClassName("lockPWDInputBg")[0];
					var para=document.createElement("a");
					element.appendChild(para);
				}
			}
		if(lockPWD.length > 0){
//			清空按钮状态
			var doorLock_del_pre = "#e5e5e5 url(fonts/icon_doorLock_del_nor.png) no-repeat center center";
			document.getElementById("lockKeyBoard_Clean").style.background = doorLock_del_pre;
			document.getElementById("lockKeyBoard_Clean").style.backgroundSize = "1.8rem auto";
//			开锁按钮状态
			if(lockPWD.length >= numlimmitmin){
				var doorLock_open_pre = "#e5e5e5 url(fonts/icon_doorLock_open_nor.png) no-repeat center center";
				document.getElementById("lockKeyBoard_Submit").style.background = doorLock_open_pre;
                document.getElementById("lockKeyBoard_Submit").style.backgroundSize = "3rem auto";
			}
		}else{
//			未输入密码状态
            var doorLock_del_pre = "#e5e5e5 url(fonts/icon_doorLock_del_nor.png) no-repeat center center";
            document.getElementById("lockKeyBoard_Clean").style.background = doorLock_del_pre;
            document.getElementById("lockKeyBoard_Clean").style.backgroundSize = "1.8rem auto";
            var doorLock_open_pre = "#e5e5e5 url(fonts/icon_doorLock_open_nor.png) no-repeat center center";
            document.getElementById("lockKeyBoard_Submit").style.background = doorLock_open_pre;
            document.getElementById("lockKeyBoard_Submit").style.backgroundSize = "3rem auto";
		}

		console.log(lockPWD)
	})
	document.getElementById("lockKeyBoard_Clean").addEventListener("click",function(){
//		清空
		lockPWD = "";
		console.log(lockPWD)
	})
}

addLoadEvent(load)
