/**
 * Created by Administrator on 2018/9/12.
 */

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
function isNull(arg) {
    var isnull = arg == null || arg == undefined || arg == "undefined" || arg == "";

    return isnull;
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
