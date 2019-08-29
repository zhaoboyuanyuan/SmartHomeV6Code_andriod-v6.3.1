/**
 * Created by Administrator on 2017/6/14.
 */
function createToast(){
    var sec = '<section class="comToast"><i><em class="rotate"></em>'+'</i></section>'
    $("body").append(sec)
}

/*点击显示隐藏密码*/
var isClick = true;
$(".look").on("click",function(){
    if(isClick == true){
        $(this).css({"background":"url(fonts/icon_look.png) no-repeat center center","background-size":"2rem auto"})
        $(this).siblings("input").attr("type","text")
        isClick = false
    }else{
        $(this).css({"background":"url(fonts/icon_close.png) no-repeat center center","background-size":"2rem auto"})
        $(this).siblings("input").attr("type","password")
        isClick = true
    }
})
