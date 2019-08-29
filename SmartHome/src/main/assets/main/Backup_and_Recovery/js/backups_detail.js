var info = window.sysfun;
info.init("gateway_Recover");
initlan();
plus.plusReady(function(){
    plus.gatewayCmd.getGatewayBackupData(null,function(data){
        alert(data);
        var pingjie='', newData = data.data.backupData;
        if(data){
            for(var i=0;0<newData.length;i++){
                var type = newData[i].type === "0" ? "自动备份" : "手动备份";
                pingjie = pingjie +'<li>'+type+':'+newData[i].time+'</li>';
            }
            $(".backups_num").html(pingjie);
            $(".backups_num").show();
            $(".backups_record_null").hide();
        }else{
            $(".backups_num").hide();
            $(".backups_record_null").show();
        }
    });
    $("#add_backups").on("click",function () {
        $(".mask").show();
        $(".popUp").show();
    })
    $("#backups_cancel").on("click",function () {
        $(".mask").hide();
        $(".popUp").hide();
    })
    $("#backups_start").on("click",function () {
        plus.gatewayCmd.backupGatewayData(function(data){
            if(data.resultDesc=="success"){
                    window.location.href="backupsing.html";
            }
        });

    })

})