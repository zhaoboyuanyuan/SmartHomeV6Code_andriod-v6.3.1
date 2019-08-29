(function () {
    var AlertControl = {
        // defaults: {
        //     title: "",
        //     content: "",
        //     isInput: false,
        //     buttonNum: 1,
        //     yesBtn: "",
        //     noBtn: "",
        // },
        init:function(defaults){
            var oneCss = "display:block;width:100%;height:100%;position:fixed;left:0;top:0;background:rgba(0,0,0,.4)";
            var twoCss = "display:block;width:76%;overflow:hidden;position:absolute;left:12%;top:50%;" +
                "background:#fff;border-radius:4px;transform:translateY(-50%);-webkit-transform:translateY(-50%)";
            var threeCss = "padding-top:2.8rem";
            var htm = '<div id="alertPopup" style="'+oneCss+'">' +
                '<div style="'+twoCss+'">';
            if(defaults.title){
                threeCss = "padding-top:2rem";
                var titleCss = "width:100%;height:1.8rem;font-size:1.8rem;color:#000;font-weight: bold;padding-top:2.8rem;text-align:center;";
                htm += '<h5 style="'+titleCss+'">'+defaults.title+'</h5>'
            }
            if(defaults.isInput){
                var inputCss = "display:block;box-sizing:border-box;width:86%;height:3.6rem;padding:0.5rem 3%;margin:1.5rem 7%;background:#eef0f1;border:0;";
                htm += '<input style="'+inputCss+'" placeholder="请输入名称" max-length="20" min-length="1" />'
            }else{
                var contentCss = "width:100%;box-sizing:border-box;padding:0 2rem 2.8rem;line-height:2rem;font-size:1.6rem;color:#666;text-align:center;"+threeCss;
                htm += '<p style="'+contentCss+'">'+defaults.content+'</p>';
            }
            htm += '<div style="height:5rem;border-top:1px solid #dcdcdc;">';
            var btnCss = "display:block;box-sizing:border-box;height:5rem;line-height:5rem;font-size:1.7rem;color:#000;text-align:center;";
            if(defaults.buttonNum == 1){
                btnCss += "width:100%;color:#8dd652;";
                htm += '<span id="AlertOk" onclick="AlertControl.hide()" style="'+btnCss+'">'+defaults.yesBtn+'</span>'
            }else if(defaults.buttonNum == 2){
                btnCss += "width:50%;float:left;";
                htm += '<span id="AlertOk" style="'+btnCss+'border-right:1px solid #dcdcdc;">'+defaults.noBtn+'</span>'
                htm += '<span id="AlertCancel" onclick="AlertControl.hide()" style="'+btnCss+'color:#8dd652;">'+defaults.yesBtn+'</span>'
            }
            htm += '</div></div></div>';
            $("body").append(htm);
        },
        show: function(defaults){
            this.init(defaults);
        },
        hide: function(){
            $("#alertPopup").remove();
        }
    };
    window.AlertControl = AlertControl;
})();