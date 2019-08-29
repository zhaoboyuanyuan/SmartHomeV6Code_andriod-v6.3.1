/**
 * Created by Administrator on 2017/6/14.
 */
/*
 * 生成6位密码
 */
function mathRand() {
    var num = "";
    for (var i = 0; i < 6; i++) {
        num += Math.floor(Math.random() * 10);
    }
    return num;
}
/*
 * 6位密码递归验证是否重复
 */
function check(len) {
    var mathNum = mathRand();
    if(!arr){
        arr = []
    }
    if (arr.indexOf(mathNum) == -1) {
        console.log(mathNum)
        console.log(arr.toString())
        $(".createPwd").html(mathNum)
        $(".createPwd").css("background","#fff")
        return 1;
    } else {
        return len * arguments.callee(len - 1);
    }
}
/*
 * 异常处理
 */
function errorCode(code) {
    switch(code) {
        case 1:
            window.showDialog.show(account_txt_16,2000);
            break;
        case 2:
            window.showDialog.show(account_txt_17,2000);
            break;
        case 3:
            window.showDialog.show(account_txt_18,2000);
            break;
        case 4:
            window.showDialog.show(account_txt_19,2000);
            break;
        case 5:
            window.showDialog.show(account_txt_20,2000);
            break;
        case 6:
            window.showDialog.show(account_txt_25,2000);
            break;
        case 7:
            window.showDialog.show(account_txt_26,2000);
            break;
        case 8:
            window.showDialog.show(account_txt_27,2000);
            break;
        case 9:
            window.showDialog.show(account_txt_28,2000);
            break;
        case 10:
            window.showDialog.show(account_txt_29,2000);
            break;
        case 11:
            window.showDialog.show(account_txt_30,2000);
            break;
    }
}

/*
 * 数据回调
 */
function rush_cmd(operType,DEVICEID,APPID) {
    plus.gatewayCmd.newDataRefresh(function(result) {
        if(result.cmd == "520" && result.devID == DEVICEID && result.appID == APPID) {
            if(result.operType == operType) { //operType:2为新增，operType:4为修改
                if(result.data.code != 0) {
                    $(".comToast").hide();
                    errorCode(parseInt(result.data.code))
                    return;
                }
                $(".comToast").hide();
                window.showDialog.show(alarm_txt_05,2000);
                setTimeout(function(){
                    window.location = "accountManage.html"
                },1000)
            }
        }
    })
}
