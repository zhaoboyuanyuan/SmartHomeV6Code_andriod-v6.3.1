

var devID = '';
var gwID = '';
var ep = 1;
var songsList = [];
var currentPage = 1;
//国际化初始化函数
initlan();

plus.plusReady(function(){

    //返回到设备详情页
    $(".back").on("click",function(){
        plus.tools.back(function() {})
    })
    //跳转到更多页面
    $(".more").on("click",function(){
        plus.tools.more(moreConfig, function() {})
    })

    ////////////////////////////查询---start////////////////////////////
    //查询
    ////////////////////////////查询---end////////////////////////////
    ////////////////////////////处理数据---start////////////////////////////
    function handleData(data){
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            var endpointNumber = endpoint.endpointNumber;
            var endpointName = endpoint.endpointName;
            var endpointType = endpoint.endpointType;
            var clusters = endpoint.clusters;
            var attributes = clusters[0].attributes;
            for (var j = 0;j < attributes.length;j++){
                var attributeId = attributes[j].attributeId;
                var attributeValue = attributes[j].attributeValue;
                switchAttributes[attributeId] = attributeValue;
                handleDataDepth(attributes[j]);
            }

        }
    }

    function handleDataDepth(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId){

        }
    }


    ////////////////////////////处理数据---end////////////////////////////
    plus.gatewayCmd.getDeviceInfo(null, function(data){
        if(data.mode == 2){
            $(".mask_layer").show();
        }else{
            $(".mask_layer").hide();
        }
        var name = data.name.indexOf("#$default$#") != -1 ? lang_name_82+data.name.split("#")[2]:data.name
        $(".deviceName").html(name)
        devID = data.devID;
        gwID = data.gwID;
        ep = ~~(data.endpoints[0].endpointNumber)
        handleExtData(data);
        getMoreConfig(devID);
        requestMusicLists(1);
        setTimeout(function(){ requestRadioCurrentMusic(); }, 500);

    })

    plus.gatewayCmdRush.newDataRefresh(function(data){
        if(data.cmd == "500" && devID == data.devID){
            if(data.mode == 2){
                $(".mask_layer").show();
            }else{
                $(".mask_layer").hide();
            }
            handleData(data);
            handleExtData(data);
            var name = data.name.indexOf("#$default$#") != -1 ? lang_name_82+data.name.split("#")[2]:data.name
            $(".deviceName").html(name)
        }
    })

    ////////////////////////////处理数据---start////////////////////////////
    function handleExtData(data) {
        if (data.extData){
            $('.songsDelayTimeText').show();
            var mData = JSON.parse(new Base64().decode(data.extData));
            var delaySec = mData[0].delaySec;
            if (delaySec == 0){
                $('.songsDelayTimeText').hide();
                $('.timerTips').text(languageUtil.getResource("Radio_Countdown_close"));
            }else {
                var showTimeText = getshowDelayDate(delaySec);
                $('.songsDelayTimeText').text(showTimeText + languageUtil.getResource("Radio_play_stop"));
                $('.timerTips').text(parseInt(delaySec/60) + languageUtil.getResource("Radio_close_later"));
            }
        }else {
            $('.songsDelayTimeText').hide();
            $('.timerTips').text(languageUtil.getResource("Radio_Countdown_close"));
        }
    }
    function handleData(data){
        for (var i = 0; i < data.endpoints.length; i++) {
            var endpoint = data.endpoints[i];
            var endpointNumber = endpoint.endpointNumber;
            var endpointName = endpoint.endpointName;
            var endpointType = endpoint.endpointType;
            var clusters = endpoint.clusters;
            var attributes = clusters[0].attributes;
            for (var j = 0;j < attributes.length;j++){
                var attributeId = attributes[j].attributeId;
                var attributeValue = attributes[j].attributeValue;
                handleDataDepth(attributes[j]);
            }

        }
    }

    function handleDataDepth(attributeData) {
        var attributeId = attributeData.attributeId;
        var attributeValue = attributeData.attributeValue;
        switch (attributeId){
            case 0x0001:
            {
                requestRadioCurrentMusic();
            }
                break;
            case 0x0003:{
                mPlayState = attributeValue;
                updatePlayState();
            }
                break;
            case 0x0004:{
                mVoice = attributeValue;
                updateVoice();
            }
                break;
        }
    }


    ////////////////////////////处理数据---end////////////////////////////
})

function getshowDelayDate(delaySec) {
    var showTime;
    if (delaySec){
        var timestamp =(new Date()).valueOf();
        timestamp += delaySec*1000;
        var newData = new Date(timestamp);
        var hour = newData.getHours();
        var minute = newData.getMinutes();
        showTime = hour +':'+minute;
    }
    return showTime;
}
function requestMusicLists(pager) {
    currentPage = pager;
    var paramToNative = {};
    var urlStr = "/ent/music/get/music/list";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.platformId = 0;
    if(pager){
        paramToCilent.page = pager;
    }else {
        paramToCilent.page = 1;
    }
    paramToCilent.count = 20;
    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;
    plus.gatewayCmd.WLHttpGetBase(paramToNative, function(result) {
        console.log(result);
        if(result.data){
            if(result.data.currentPage == 1){
                mui('#refreshContainer').pullRefresh().endPulldownToRefresh();
                songsList.length = 0;
                songsList = result.data.entContents;
                musicListCount = result.data.totalCount;
                resetSongList();
                updateCurrentSong();
            }else if (result.data.currentPage == pager){
                mui('#refreshContainer').pullRefresh().endPullupToRefresh();// 加载数据最没有更多内容
                songsList = songsList.concat(result.data.entContents);
                resetSongList();
                updateCurrentSong();
            }
        }
    });
}

function searchSelectedMusic(musicName) {
    var songItem="";
    if (musicName && musicName.length > 0){
        var SongIndex = songsList.length;
        while (SongIndex--){
            if(songsList[SongIndex].trackTitle == musicName){
                songItem = songsList[SongIndex].songId;
                return songItem;
            }
        }
    }
    return songItem;
}

function searchSelectedMusicModel(musicId) {
    var songItem={};
    if (musicId){
        var SongIndex = songsList.length;
        while (SongIndex--){
            if(songsList[SongIndex].songId == musicId){
                songItem = songsList[SongIndex];
                return songItem;
            }
        }
    }
    return songItem;
}

function deleteSongListsWithId(musicId) {
    if (musicId && musicId.length > 0){
        for (var songDeleteIndex in songsList){
            var songItem= songsList[songDeleteIndex];
            if (songItem.songId == musicId){
                songsList.splice(songDeleteIndex,1);
                return true;
            }
        }
    }
    return false;
}

function requestRadioCurrentMusic() {

    var paramToNative = {};
    var urlStr = "/ent/music/get/playMode";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.platformId = 0;
    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;

    plus.gatewayCmd.WLHttpGetBase(paramToNative, function(result) {
        console.log(result);
        if(result.data){
            if (result.data.entContent){
                mMusicInfo.id = result.data.entContent.songId;
                mMusicInfo.name = result.data.entContent.trackTitle;
                mMusicInfo.singer = result.data.entContent.nickname;
                mMusicInfo.album = result.data.entContent.avatarUrl;
                if (result.data.entContent.status == 2){
                    mPlayState = 1;
                }else if(result.data.entContent.status == 1){
                    mPlayState = 0;
                }
                mPlayMode = result.data.playMode;
                mVoice = result.data.volume;
                updateSongs();
                updateCurrentSong();
                updateVoice();
                updatePlayMode();
            }
        }
    });
}

function deleteRadioMusicRequest(musicId) {
    var paramToNative = {};
    var urlStr = "/ent/music/delete/music";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.platform = 0;
    paramToCilent.id = musicId;

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;
    plus.gatewayCmd.WLHttpGetBase(paramToNative, function(result) {
        console.log(result);
        if(result.resultCode == 0){
            //剔除掉数组中相应ID的数据，并刷新列表
            var deleteResult = deleteSongListsWithId(musicId)
            if (deleteResult){
                resetSongList();
            }else {
                requestMusicLists(1);
            }

        }
    });
}

function requestRadioLast() {
    var paramToNative = {};
    var urlStr = "/ent/music/nextOrLast";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.nextOrLast = "last";

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;
    plus.gatewayCmd.WLHttpPostBase(paramToNative, function(result) {
        console.log(result);
        if(result.resultCode == 0){
            requestRadioCurrentMusic();
        }
    });
}

function requestRadioNext() {
    var paramToNative = {};
    var urlStr = "/ent/music/nextOrLast";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.nextOrLast = "next";

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;
    plus.gatewayCmd.WLHttpPostBase(paramToNative, function(result) {
        console.log(result);
        if(result.resultCode == 0){
            requestRadioCurrentMusic();
        }
    });
}

function actionMusicPlay(index) {
    if (index < songsList.length){
        var musicId = songsList[index].songId;
        var playUrl = songsList[index].playUrl;

        var paramToNative = {};
        var urlStr = "/ent/music/playMusic";
        var paramToCilent = {};
        paramToCilent.deviceId = devID;
        paramToCilent.songId = musicId;

        paramToNative.url = urlStr;
        paramToNative.param = paramToCilent;

        plus.gatewayCmd.WLHttpPostBase(paramToNative, function(result) {
            console.log(result);
            if(result.data){

            }
        });
        // sendCmd(gwID,devID,ep,0x8001,[playUrl]);
    }
}

function actionMusicPlayWithModel(model) {
    if (model){
        var musicId = model.songId;
        var playUrl = model.playUrl;

        var paramToNative = {};
        var urlStr = "/ent/music/playMusic";
        var paramToCilent = {};
        paramToCilent.deviceId = devID;
        paramToCilent.songId = musicId;

        paramToNative.url = urlStr;
        paramToNative.param = paramToCilent;

        plus.gatewayCmd.WLHttpPostBase(paramToNative, function(result) {
            console.log(result);
            if(result.resultCode == 0){
                requestRadioCurrentMusic();
            }
        });
        // sendCmd(gwID,devID,ep,0x8001,[playUrl]);
    }
}

function changeMusicPlayMode(sendMode) {
    var paramToNative = {};
    var urlStr = "/ent/music/set/playMode";
    var paramToCilent = {};
    paramToCilent.deviceId = gwID;
    paramToCilent.childDeviceId = devID
    paramToCilent.playMode = sendMode;

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;
    plus.gatewayCmd.WLHttpPostBase(paramToNative, function(result) {
        console.log(result);
        if(result.resultCode == 0){
            requestRadioCurrentMusic();
        }
    });
}

function updateMusicPlayState(state) {
    if (state == 1){
        sendCmd(gwID,devID,ep,0x8003,["1"]);
    }else if(state == 0){
        sendCmd(gwID,devID,ep,0x8003,["0"]);
    }
}

function updateMusicVoiceState(vol) {
    if (vol == 0){
        sendCmd(gwID,devID,ep,0x8004,["0"]);
    }else if(vol <= 100){
        var volStr = vol.toString();
        sendCmd(gwID,devID,ep,0x8004,[volStr]);
    }
}

/*设置延时暂停 time为秒*/
function setMusicDelayStop(time) {
    if (time >= 0){
        var sendTimeStr = time.toString();
        sendCmd(gwID,devID,ep,0x8007,[sendTimeStr]);
    }
}

//截取字符串最大长度
function substrWithLength(str,maxLength) {
    var resultStr = str;
    var strlength = str.length;
    if (strlength > 0 && strlength >= maxLength){
        resultStr = str.substring(0, maxLength);
    }
    return resultStr;

}

//
$(".toXmly").on("click",function(){
    window.location.href='xmly.html';
})
