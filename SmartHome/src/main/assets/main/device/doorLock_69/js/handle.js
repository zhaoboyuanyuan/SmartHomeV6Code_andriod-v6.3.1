var fakeLoadertime;
var setPwdtime;

function showLoading() {
    $(".saveToast").show();
    fakeLoadertime = setTimeout(function () {
        hideLoading();
        //超时弹框
        timeOutAlert();

        // setPWDprompt();
    },10000)
}

function hideLoading() {
    window.clearInterval(fakeLoadertime);
    $(".saveToast").hide();
    mui.closePopups();
}

function showSetPwdLoading(){
    $(".saveToast").show();
    setPwdtime = setTimeout(function () {
        hideSetPwdLoading();
        getLockPWDInfo();
    },5000)
}

function hideSetPwdLoading() {
    window.clearInterval(setPwdtime);
    $(".saveToast").hide();
    mui.closePopups();
}

function setPWDprompt() {
    showSetPWDView();

    // var btnArray = ['确定'];
    // mui.prompt('', '6位数字且无空格', '请先设置手机开门密码', btnArray, function(e) {
    //     showSetPwdLoading();
    //     var inputpwd = e.value;
    //     var pattern = /^[0-9]*$/;
    //     if (pattern.test(inputpwd) && inputpwd.length == 6){
    //         setLockPWD(inputpwd);
    //     }else{
    //         mui.toast('6位数字且无空格');
    //         return false;
    //     }
    // })
}

function showSetPWDView() {
    $("#firstSetPWD").show();
    hidetimeOutView();
}
function hideSetPWDView() {
    $("#firstSetPWD").hide();
}

function showtimeOutView() {
    $("#showTimeOutView").show();
    hideSetPWDView();
}
function hidetimeOutView() {
    $("#showTimeOutView").hide();
}

function timeOutAlert() {
    showtimeOutView();
}