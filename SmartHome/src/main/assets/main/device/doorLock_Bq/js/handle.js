/**
 * Created by Administrator on 2017/6/2.
 */
initlan();
var endpointNumber;
var endpointType;
var clusterId;
var dID;
var gwID;
var openLock;
var timer;
var str = '';
var deviceName = $(".deviceName")
var moreConfig;//更多配置
var hint_txt = $(".pwd_toast")

// 清除按钮是否可用
var couldDelete = true;
var isSendCmd = false;

/*上滑解锁*/
$(".slideUpOpenDoor").on("touchstart",function(event){
    startY = event.targetTouches[0].pageY;
    startX = event.targetTouches[0].pageX;
});
$(".slideUpOpenDoor").on("touchmove",function(event){
    moveY = event.targetTouches[0].pageY - startY;
    moveX = event.targetTouches[0].pageX - startX;
    isScrolling = Math.abs(moveX) < Math.abs(moveY) ? 1:0; //isScrolling为1时，表示纵向滑动，0为横向滑动
    if(isScrolling === 1){
        event.preventDefault();
        $(this).css("bottom",-moveY);
    }
});
$(".slideUpOpenDoor").on("touchend",function(){
    $(".lockState").hide();
    $(".lockPassword").show();
    $(this).css("bottom",0);
    isBiometricSupport(dID)
});

//获取500信息
plus.plusReady(function(){
    //点击写入密码并发送开锁命令
    plus.gatewayCmd.getDeviceInfo(function(result){
        endpointNumber = result.endpoints[0].endpointNumber
        endpointType = result.endpoints[0].endpointType
        clusterId = result.endpoints[0].clusters[0].clusterId
        dID = result.devID

        gwID = result.gwID
        // 获取更多配置
        moreConfig = getMoreConfig(dID, gwID);
        //设备名称
        var name = result.name.indexOf("#$default$#") != -1 ? device_Bg_title + result.name.split("#")[2]:result.name
        $(".deviceName").html(name)
        //判断设备mode，并作出相应操作
        deviceMode(result,false)
        sendCmd('',32778)//查询设备当前状态
        //sendCmd('2',32797)//APP一键锁死功能,获取是否支持一键锁死功能
    });
    plus.gatewayCmd.newDataRefresh(function(data){
        if(data.cmd == "500" && data.devID == dID){
            var name = data.name.indexOf("#$default$#") != -1 ? device_Bg_title + data.name.split("#")[2]:data.name
            $(".deviceName").html(name)
            deviceMode(data,true)
            isSendCmd = false;
        }else if(data.cmd == "502" && data.devID == dID){
            if(data.name != undefined || data.name != ''){
                $(".deviceName").html(data.name)
            }
        }
    })

})
$(".btn-i span").find("i").on("touchstart",function(){
    //按键点击高亮
    $(this).css("background","rgba(0,0,0,.4)");
}).on("touchend",function(){
    $(this).css("background","#e5e5e5");
});
$(".btn-i span").find("i").on("click",function(){
    console.log("click: " + $(this).html());
    //var len = $(".pwd-show").find('.bgColor').length;
    // console.log(len)
    //全部清空操作
    plus.gatewayCmd.isLockClick(function(){});
    if($(this).parent().index() == 9){
        if (couldDelete) {
            str = ''
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
        couldDelete = false;
        //单步清除操作
    }else if($(this).parent().index() == 11){
        if (couldDelete) {
            str = str.substr(0,str.length-1)
            $(".pwd-show i").eq(str.length).removeClass("bgColor")
        }
        console.log(str)
        if(str.length == 0){
            $(".removeAll i").css({"color":"#000"})
            couldDelete = false;
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
        }else{
            $(".remove").css({"background":"url(fonts/backspace_pre.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#8dd652"})
            couldDelete = true;
        }
        //密码写入
    }else{
        if (isSendCmd) {
            return;
        }
        $(".remove").css({"background":"url(fonts/backspace_pre.png) no-repeat center center","background-size":"3.15rem auto"})
        $(".removeAll i").css({"color":"#8dd652"})

        couldDelete = true;
        if(str.length < 6){
            str += $(this).html()
            $(".pwd-show").find("i").eq(str.length-1).addClass("bgColor")
            if(str.length >= 6){
                console.log(str)
                // reloadUI(32770,300)
                hint_txt.html(lang_txt_23)
                openLock = true;
                timer = setTimeout("cleanpassword()",20000)
                $(".removeAll i").css({"color":"#000"})
                couldDelete = false;
                $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
                //发送控制命令开锁
                sendCmd(str.substr(0, 6),32772)
                isSendCmd = true;
                str = '';
            }
        }
    }
})
$("#back").on("click",function(){
    if($(".lockPassword").css("display") == "block"){
        $(".lockPassword").hide();
        $(".lockState").show();
    }else{
        plus.tools.back(function(){})
    }
})
$(".more").on("click",function(){
    plus.tools.more(moreConfig,function(){})
})
function deviceMode(list,flag){
    if(list.mode == 2){
        //设备离线
        $(".mask_layer").show()
    }else if(list.mode == 3){
        //设备退网，原生做处理
    }else{
        //设备状态改变或设备第一次上线或设备上线，设备页面可操作
        if(list.mode == 4){
            $(".stateImg").attr("data-init","init");
        }else{
            $(".stateImg").attr("data-init","");
        }
        //根据协议更新ui界面
        $(".mask_layer").hide()
        list.endpoints.forEach(function (endpoint) {
            var clusters = endpoint.clusters;
            clusters.forEach(function (cluster) {
                var attributes = cluster.attributes;
                attributes.forEach(function (attribute) {
                    var attributeId = attribute.attributeId;
                    var attributeValue = attribute.attributeValue;
                    reloadUI(attributeId, attributeValue,flag);
                })
            })
        });
    }
}
function reloadUI(attributeId,attributeValue,flag){
    switch (attributeId){
        case 0:{
            //针对设备状态作出相应的ui改变
            goldLockStatus(attributeValue)
        }
        break;
        case 32769:{
            //表示告警类，不做操作
            if(attributeValue == 4){
                hint_txt.html(lang_txt_19)
                $(".lock_img").css({"background":"url(fonts/icon_doorLock_on.png) no-repeat center center","background-size":"3.5rem auto"})
                $(".pwd-show").find('i').removeClass("bgColor")
                $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
                $(".removeAll i").css({"color":"#000"})
            }
        }
        break;
        case 32770:{
            console.log("我开锁啦")
            //开锁方式
            if(flag){
                clearTimer()
                hint_txt.html(lang_txt_17)

                $(".stateImg").hide();
                $(".doorIsOpen").show();
                $(".text02").show();
                $(".text01").hide();
                $(".text02").html(lang_txt_17);

                $(".lock_img").css({"background":"url(fonts/icon_doorLock_on.png) no-repeat center center","background-size":"3.5rem auto"})
                $(".pwd-show").find('i').removeClass("bgColor")
                $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
                $(".removeAll i").css({"color":"#000"})
                setTimeout(function () {
                    hint_txt.html(lang_txt_01)
                    $(".stateImg").show();
                    $(".doorIsOpen").hide();
                    $(".text02").hide();
                    $(".text01").show();
                }, 5000);
            }
        }
        break;
        case 32771:{
            // 1表示密码验证成功
            // 2表示密码验证失败
            // 3表示强制上锁
            // 4表示自动上锁
            console.log("上锁")
            unlockingStatus(attributeValue)
        }
        break;
        case 32773:{
            // 1aabbccddee
            // 2AABBCCDDEE
            // 3HHMMhhmm
            // 4XX
            // 1:锁的当前状态
            // 2:锁具支持功能
            // 3:门铃参数
            // 4:APP一键锁死
            // aa表示锁具方舌状态：bb表示系统锁定状态 cc表示反锁状态 dd表示电量 ee表示是否允许远程控制 方舌缩进为“01”，系统锁定为“01”，反锁为“01”，电量低为“01”，禁止远程控制'01' 反之填“00”；
            // AA表示密码；BB表示射频卡；CC表示指纹；DD表示纽扣；EE表示RTC；若支持为01，若不支持则为00
            // HH，hh：小时 MM，mm：分钟
			// XX：01锁死启动 00：锁死取消 02：不支持此功能
            console.log("状态")
            isRemoteUnlock(attributeValue)
        }
        break;
        case 32774:{
            //错误code操作
            //1：无线端密码开锁失败 2：开锁密码验证错误 3：时间同步失败 4：管理员用户认证失败 5：管理员密码验证错误 6：动态密码失败 7：密码重复 8：密码非法 9：时钟无效 10：操作失败
            unlockingFail(attributeValue)
        }
        break;
        default:
        {

        }
        break;
    }
}

/**
 * 0x0000 设备有以下状态
 * 1表示上锁
 * 2表示上保险
 * 3表示解除保险
 * 4表示反锁
 * 5表示解除反锁
 * 7解除锁定
 */

//0x0000,设备状态
function goldLockStatus(attributeValue){
    switch (attributeValue){
        case "1":{
            //上锁
            console.log("上锁")
            hint_txt.html(lang_txt_01)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "2":{
            //上保险
            hint_txt.html(lang_txt_01)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "3":{
            //解除保险
            hint_txt.html(lang_txt_01)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
        }
            break;
        case "4":{
            //反锁
            hint_txt.html(lang_txt_04);

            $(".locked").attr("state-locked","locked");
            $(".text02").hide();
            $(".text01").show();
            $(".text01").html(doorLock_Double_locked);

            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "5":{
            //解除反锁
            hint_txt.html(lang_txt_01)

            $(".locked").attr("state-locked","1");
            $(".text02").hide();
            $(".text01").show();
            $(".text01").html(doorLock_No_Double_locked);

            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "7":{
            //解除锁定
            hint_txt.html(lang_txt_06)
        }
            break;
        default :{

        }
        break;

    }
}

//1：无线端密码开锁失败 2：开锁密码验证错误 3：时间同步失败 4：管理员用户认证失败 5：管理员密码验证错误 6：动态密码失败 7：密码重复 8：密码非法 9：时钟无效 10：操作失败
function unlockingFail(attributeValue){
    switch (attributeValue){
        case "1":{
            //无线端密码开锁失败
            clearTimer()
            hint_txt.html(lang_txt_18)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "2":{
            //开锁密码验证错误
            clearTimer()
            hint_txt.html(lang_txt_18)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "10":{
            //操作失败
            clearTimer()
            hint_txt.html(lang_txt_16)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        default:{

        }
            break;
    }
}
// 1表示密码验证成功
// 2表示密码验证失败
// 3表示强制上锁
// 4表示自动上锁
//5表示允许远程开锁
//6表示禁止远程开锁

function unlockingStatus(attributeValue){
    switch (attributeValue){
        case "1":{
            //表示密码验证成功
            clearTimer()
            hint_txt.html(lang_txt_17);

            $(".stateImg").hide();
            $(".doorIsOpen").show();
            $(".text02").html(lang_txt_17);
            $(".text02").show();
            $(".text01").hide();

            $(".lock_img").css({"background":"url(fonts/icon_doorLock_on.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
            setTimeout(function () {
                hint_txt.html(lang_txt_01)
                $(".stateImg").show();
                $(".doorIsOpen").hide();
                $(".text02").show();
                $(".text01").hide();
            }, 5000);
        }
            break;
        case "2":{
            //开锁密码验证错误
            clearTimer()
            hint_txt.html(lang_txt_18)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
            setTimeout(function () {
                hint_txt.html(lang_txt_01)
            }, 3000);
        }
            break;
        case "3":{
            //3表示强制上锁
            hint_txt.html(lang_txt_22)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
            break;
        case "4":{
            //4表示自动上锁
            hint_txt.html(lang_txt_20)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
        }
        break;
        case "5":{
            //5表示允许远程开锁
            console.log("允许网络开锁")
            hint_txt.html(lang_txt_01)
            $(".lock_img").css({
                "background": "url(fonts/icon_doorLock_off.png) no-repeat center center",
                "background-size": "3.5rem auto"
            })
            $(".btn-i").find(".noClick").hide()
            $(".btn-i").find("span").removeClass("not_click")
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
            str = ''
        }
            break;
        case "6":{
            //6表示禁止远程开锁
            console.log("不允许网络开锁")
            hint_txt.html(lang_txt_21)
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            $(".btn-i").find(".noClick").show()
            $(".btn-i").find("span").addClass("not_click")
        }
            break;
        default:{

        }
            break;
    }

}
//是否允许远程控制
function isRemoteUnlock(attributeValue){
    if(attributeValue.substr(0,1) == "1"){
        if(attributeValue.substr(5,2) == "01"){
            $(".lock_img").css({"background":"url(fonts/icon_doorLock_off.png) no-repeat center center","background-size":"3.5rem auto"})
            hint_txt.html(lang_txt_04)
            $(".pwd-show").find('i').removeClass("bgColor")
            $(".remove").css({"background":"url(fonts/backspace.png) no-repeat center center","background-size":"3.15rem auto"})
            $(".removeAll i").css({"color":"#000"})
            //01反锁
            $(".locked").attr("state-locked","locked");
            $(".text02").hide();
            $(".text01").show();
            $(".text01").html(doorLock_Double_locked);

        }else{
            //00未反锁
            $(".locked").attr("state-locked","");
            $(".text02").hide();
            $(".text01").show();
            $(".text01").html(doorLock_No_Double_locked);
        }
        //电量
        if(attributeValue.substr(7,2) == "00"){
            $(".electricity i").attr("state-elec","empty")
        }else if(attributeValue.substr(7,2) == "01"){
            $(".electricity i").attr("state-elec","small")
        }else if(attributeValue.substr(7,2) == "02"){
            $(".electricity i").attr("state-elec","large")
        }else if(attributeValue.substr(7,2) == "03"){
            $(".electricity i").attr("state-elec","all")
        }
    }
}

//501控制
function sendCmd(pwd,commandId) {
    var jsonData = {
        "cmd": "501",
        "gwID": gwID,
        "devID": dID,
        "endpointNumber": endpointNumber,
        "clusterId": 257,
        "commandType": 1,
        "commandId": commandId,
        "parameter":[pwd],
    };
    plus.gatewayCmd.controlDevice(jsonData, function(result) {
        str = ''
        console.log("result" + result);
    })
}
function isBiometricSupport(dID) {
    plus.gatewayCmd.isBiometricSupport(dID, function(result) {
        str = ''
        console.log("result" + result);
    })
}
function clearTimer() {
    if(timer) {
        clearTimeout(timer);
    }
}
function cleanpassword() {
    if(openLock) {
        clearTimer();
        console.log(str)
        hint_txt.html(lang_txt_24)
        openLock = false;
        isSendCmd = false;
    }
    //清除密码
    $(".pwd-show").find('i').removeClass("bgColor")
}





