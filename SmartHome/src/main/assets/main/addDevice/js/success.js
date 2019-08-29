/**
 * Created by Administrator on 2017/6/5.
 */
var lang_name_op = "门锁";
plus.plusReady(function(){
    plus.gatewayCmd.newDataRefresh(function(data){
        if(data.cmd == "500"){
            if(data.mode == 4 && data.type == "OP") {
                // console.log(data);
                // info.setItem("decInfo",JSON.stringify(data));
                window.location = "addDevice_succ1.html?name="+ languageUtil.getResource("addDevice_op_title");
            }
        }
    })
})