! function (win) {
    function resize() {
        var domWidth = domEle.getBoundingClientRect().width;
        if (domWidth / v > 750) {
            domWidth = 750 * v;
        }
        win.rem = domWidth / 37.5;
        domEle.style.fontSize = win.rem + "px";
    }

    var v, initial_scale, timeCode, dom = win.document,
        domEle = dom.documentElement,
        viewport = dom.querySelector('meta[name="viewport"]'),
        flexible = dom.querySelector('meta[name="flexible"]');
    if (viewport) {
        //viewport：<meta name="viewport"content="initial-scale=0.5, minimum-scale=0.5, maximum-scale=0.5,user-scalable=no,minimal-ui"/>
        var o = viewport.getAttribute("content").match(/initial\-scale=(["']?)([\d\.]+)\1?/);
        if (o) {
            initial_scale = parseFloat(o[2]);
            v = parseInt(1 / initial_scale);
        }
    } else {
        if (flexible) {
            var o = flexible.getAttribute("content").match(/initial\-dpr=(["']?)([\d\.]+)\1?/);
            if (o) {
                v = parseFloat(o[2]);
                initial_scale = parseFloat((1 / v).toFixed(2))
            }
        }
    }
    if (!v && !initial_scale) {
        var n = (win.navigator.appVersion.match(/android/gi), win.navigator.appVersion.match(/iphone/gi));
        v = win.devicePixelRatio;
        v = n ? v >= 3 ? 3 : v >= 2 ? 2 : 1 : 1, initial_scale = 1 / v
    }
    //没有viewport标签的情况下
    if (domEle.setAttribute("data-dpr", v), !viewport) {
        if (viewport = dom.createElement("meta"), viewport.setAttribute("name", "viewport"), viewport.setAttribute("content", "initial-scale=" + initial_scale + ", maximum-scale=" + initial_scale + ", minimum-scale=" + initial_scale + ", user-scalable=no"), domEle.firstElementChild) {
            domEle.firstElementChild.appendChild(viewport)
        } else {
            var m = dom.createElement("div");
            m.appendChild(viewport), dom.write(m.innerHTML)
        }
    }
    win.dpr = v;
    win.addEventListener("resize", function () {
        clearTimeout(timeCode), timeCode = setTimeout(resize, 300)
    }, false);
    win.addEventListener("pageshow", function (b) {
        b.persisted && (clearTimeout(timeCode), timeCode = setTimeout(resize, 300))
    }, false);
    // 个人觉得没必要完成后就把body的字体设置为12
    "complete" === dom.readyState ? dom.body.style.fontSize = 12 * v + "px" : dom.addEventListener("DOMContentLoaded", function () {
        dom.body.style.fontSize = 12 * v + "px"
    }, false);
    resize();

}(window);

function addLoadEvent(func) {
    var oldonload = window.onload; //得到上一个onload事件的函数
    if (typeof window.onload != 'function') { //判断类型是否为'function',注意typeof返回的是字符串
        window.onload = func;
    } else {
        window.onload = function () {
            oldonload(); //调用之前覆盖的onload事件的函数---->由于我对js了解不多,这里我暂时理解为通过覆盖onload事件的函数来实现加载多个函数
            func(); //调用当前事件函数
        }
    }
}


function ios12Keyboard() {
    // 键盘收起事件
    document.body.addEventListener('focusout', function () {
        console.log(document.documentElement.scrollTop || document.body.scrollTop) // 0
        var u = navigator.userAgent,
            app = navigator.appVersion;
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if (isiOS) {
            window.scrollTo(0, 0);
        }
    });
}

addLoadEvent(ios12Keyboard);

(function () {
    var timeOver;
    var showDialog = {
        init: function (text) {
            var div = document.createElement("div");
            div.id = "toastDialog";
            div.style.display = "block";
            div.style.boxSizing = "border-box";
            div.style.position = "fixed";
            div.style.bottom = "8rem";
            div.style.maxWidth = "90%";
            div.style.lineHeight = "1.6rem";
            div.style.padding = "1rem";
            div.style.borderRadius = "5px";
            div.style.background = "rgba(0,0,0,.7)";
            div.style.fontSize = "1.4rem";
            div.style.color = "#ffffff";
            div.style.zIndex = "99999999999999999";
            // div.style.transform = "translateX(-50%)";
            div.innerHTML = text;
            document.body.appendChild(div);
        },
        show: function (text, time, completeFunc) {
            clearTimeout(timeOver);
            if (document.getElementById("toastDialog")) {
                document.body.removeChild(document.getElementById("toastDialog"));
            }
            this.init(text);
            var curWidth = document.documentElement.clientWidth || document.body.clientWidth;
            var width = document.getElementById("toastDialog").clientWidth;
            var leftWidth = (curWidth - width) / 2;
            document.getElementById("toastDialog").style.left = leftWidth + "px";
            timeOver = setTimeout(function () {
                document.body.removeChild(document.getElementById("toastDialog"));
                if (completeFunc != undefined && completeFunc != null && typeof completeFunc == 'function'){
                    completeFunc();
                }
            }, time)
        }
    };
    window.showDialog = showDialog;
})();
(function () {
    var timeOver;
    var loadingPrompt = {
        init: function (text,top) {
            var loadingStr = '<section style="position:absolute;left:0;top:'+top+';width:100%;height:calc(100% - '+top+');">' +
                '    <div style="position:absolute;left:50%;top:20%;width:8.5rem;height:8.5rem;background:url(../../source/commonImg/loading_bg.png) no-repeat center center;background-size:8.5rem auto;transform: translateX(-50%);-webkit-transform: translateX(-50%);border-radius:10px;">' +
                '        <div style="width:100%;height:5.2rem;text-align:center;overflow:hidden;">' +
                '            <img src="../../source/commonImg/loading_flower.gif" alt="" style="display:inline-block;width:3.2rem;height:3.2rem;margin-top:1rem;-webkit-backface-visibility: hidden;">' +
                '        </div>' +
                '        <p style="width:90%;height:3.3rem;text-align:center;padding:0 5%;font-size:1.4rem;color:#fff;">'+text+'</p>' +
                '    </div>' +
                '</section>';
            var div = document.createElement("div");
            div.id = "loadingPrompt";
            div.innerHTML = loadingStr;
            document.body.appendChild(div);
            // div.appendChild(section);
            // section.appendChild(divSection);
            // divSection.appendChild(divDiv);
            // divSection.appendChild(p);
            // divDiv.appendChild(img);
        },
        show: function (text, top, time, tipsText) {
            if (document.getElementById("loadingPrompt")) {
                document.body.removeChild(document.getElementById("loadingPrompt"));
            }
            this.init(text, top);
            this.loadOverTime(time,tipsText);
        },
        hide: function(){
            if (document.getElementById("loadingPrompt")) {
                document.body.removeChild(document.getElementById("loadingPrompt"));
            }
            this.clearTime();
        },
        loadOverTime:function(time,text){
            clearTimeout(timeOver);
            var _this = this;
            timeOver = setTimeout(function(){
                _this.hide();
                window.showDialog.show(text,3000);
            },time);
        },
        clearTime:function(){
            clearTimeout(timeOver);
        }
    };
    window.loadingPrompt = loadingPrompt;
})();