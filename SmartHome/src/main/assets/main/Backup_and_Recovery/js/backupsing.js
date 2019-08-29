initlan();
plus.plusReady(function(){
    plus.gatewayCmdRush.newDataRefresh(function(data){
        if(data){
            window.location.href="backups_result.html";
        }else{
            window.location.href="backups_result.html";
        }

    })
    var progressbar={
        init:function(){
            var count=0;
            //通过间隔定时器实现百分比文字效果,通过计算CSS动画持续时间进行间隔设置
            var timer=setInterval(function(e){
                count++;
                if(count===100) clearInterval(timer);
            },17);
        }
    };
    progressbar.init();
})