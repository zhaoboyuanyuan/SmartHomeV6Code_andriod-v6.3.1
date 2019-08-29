var info = window.sysfun;
info.init("gateway_Recover");
initlan();
plus.plusReady(function(){
        plus.gatewayCmd.getGatewayBackupData(obj, function(data){
            alert(data);
            var pingjie='', newData = data.data.backupData;
            if(data){
                for(var i=0;0<newData.length;i++){
                    var type = newData[i].type === "0" ? "自动备份" : "手动备份";
                    pingjie = pingjie +'<li>'+type+':'+newData[i].time+'<input type="radio" name="we" id="'+newData[i].bid+'"/></li>';
                }
                $(".backups_num").html(pingjie);
                $(".backups_num").show();
                $(".result_tips").hide();
            }else{
                $(".backups_num").hide();
                $(".result_tips").show();
            }
        });
        $("input").on("click",function () {
            $("#start_recovery").attr("disabled",false);
            $("#start_recovery").css("background-color","#8dd652");
        })
        $("#start_recovery").on("click",function () {
            var bid = $("input:selected").attr("id");
            var obj = {
                bid:bid
            };
            plus.gatewayCmd.recoveryGatewayData(obj, function(data){
                if(data.result=="success"){
                    window.location.href="backupsing.html";
                }
            })
        })
})