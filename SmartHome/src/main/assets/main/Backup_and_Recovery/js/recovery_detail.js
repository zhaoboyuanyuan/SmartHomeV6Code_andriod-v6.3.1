var info = window.sysfun;
info.init("gateway_Recover");
initlan();
plus.plusReady(function(){
    $("input").on("input",function () {
        if($(this).val()!='' && $("#pass").val()!=''){
            $("#complete_location").css("background-color","#8dd652");
            $("#complete_location").attr("disabled",false);
        }else{
            $("#complete_location").css("background-color","#eee");
            $("#complete_location").attr("disabled",true);
        }
    })
    $("#test").on("click",function () {
        var account = $("#account").val();
        var pass = $("#pass").val();
        var obj = {
            gatewayId:account,
            password:pass
        }
        plus.gatewayCmd.verifyGateway(obj, function(data){
            if(data.data.result==1){

            }else if(data.data.result==2){

            }else if(data.data.result==3){

            }else if(data.data.result==4){

            }else if(data.data.result==5){
                window.location.href="recovery_start.html";
            }
        })
    })

})