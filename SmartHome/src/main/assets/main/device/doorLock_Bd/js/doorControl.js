var lockPWD = "";
var numlimmitmax = 6; //密码长度最大限制
var numlimmitmin = 6; //密码长度最小限制
var openLock = false;
var timer;
function load() {
	document.getElementsByClassName("lockKeyBoard")[0].addEventListener("click", function() {
		if(openLock){
			return;
		}
		var num = event.target.textContent;
		//输入
		if(num.length == 1 && lockPWD.length < numlimmitmax) {
			lockPWD += num;
			var notice = document.getElementsByClassName("lockStateNotice")[0].getElementsByTagName("a")[0];
			notice.textContent = alarmSet_js_03;//"请输入密码";
		}
		if(num.length > 0 && lockPWD.length == numlimmitmax){
			var notice = document.getElementsByClassName("lockStateNotice")[0].getElementsByTagName("a")[0];
			notice.textContent = alarmSet_js_32;//"开锁中...";
			openLock = true;
			timer = setTimeout("cleanpassword()",10000)
			sendCmd(lockPWD);
		}
		//密码输入框的状态
		var imglength = document.getElementsByClassName("lockPWDInputBg")[0].getElementsByTagName("a").length;
		if(imglength > lockPWD.length) {
			for(var i = imglength; i > lockPWD.length; i--) {
				var parent = document.getElementsByClassName("lockPWDInputBg")[0];
				var child = document.getElementsByClassName("lockPWDInputBg")[0].getElementsByTagName("a")[i - 1];
				parent.removeChild(child);
			}
		} else {
			for(var i = imglength; i < lockPWD.length; i++) {
				var element = document.getElementsByClassName("lockPWDInputBg")[0];
				var para = document.createElement("a");
				element.appendChild(para);
			}
		}
		if(lockPWD.length > 0) {
			//			清空按钮状态
			var doorLock_del_pre = "url(fonts/icon_doorLock_del_pre.png)";
			document.getElementById("lockKeyBoard_Clean").getElementsByTagName("i")[0].style.backgroundImage = doorLock_del_pre;
			//			back按钮状态
			var lockKeyBoard_backspace = "url(fonts/backspace_pre.png)";
			document.getElementById("lockKeyBoard_backspace").getElementsByTagName("i")[0].style.backgroundImage = lockKeyBoard_backspace;
		} else {
			//			未输入密码状态
			var doorLock_del_nor = "url(fonts/icon_doorLock_del_nor.png)";
			document.getElementById("lockKeyBoard_Clean").getElementsByTagName("i")[0].style.backgroundImage = doorLock_del_nor;
			var lockKeyBoard_backspace = "url(fonts/backspace.png)";
			document.getElementById("lockKeyBoard_backspace").getElementsByTagName("i")[0].style.backgroundImage = lockKeyBoard_backspace;
		}

		console.log(lockPWD)
	})
	document.getElementById("lockKeyBoard_Clean").addEventListener("click", function() {
		//		清空
		lockPWD = "";
		console.log(lockPWD)
	})
	document.getElementById("lockKeyBoard_backspace").addEventListener("click", function() {
		//		清空
		lockPWD = lockPWD.substring(0,lockPWD.length-1)
	})
}

addLoadEvent(load)