var devID = "";
var gwID = "";
var ep = 1;
var songsList = [];
var currentPage = 1;
//国际化初始化函数
initlan();

plus.plusReady(function () {
  //返回到设备详情页
  $(".back").on("click", function () {
    plus.tools.back(function () {});
  });
  //跳转到更多页面
  $(".more").on("click", function () {
    plus.tools.more(moreConfig, function () {});
  });

  ////////////////////////////处理数据---end////////////////////////////
  plus.gatewayCmd.getDeviceInfo(null, function (data) {
    if (data.mode == 2) {
      $(".mask_layer").show();
    } else {
      $(".mask_layer").hide();
    }
    var name =
      data.name.indexOf("#$default$#") != -1 ?
      lang_name_XW01 + data.name.split("#")[2] :
      data.name;
    $(".deviceName").html(name);
    devID = data.devID;
    if (data.gwID != undefined && data.gwID.length > 0) {
      gwID = data.gwID;
    } else {
      gwID = data.devID;
    }
    getMoreConfig(devID);
    requestMusicLists(1);
    setTimeout(function () {
      requestRadioCurrentMusic();
    }, 500);
  });

  plus.gatewayCmdRush.newDataRefresh(function (data) {
    if (devID == data.devID) {
      if (data.cmd == "502") {
        var name =
          data.name.indexOf("#$default$#") != -1 ?
          lang_name_XW01 + data.name.split("#")[2] :
          data.name;
        $(".deviceName").html(name);
      } else if (data.cmd == "500" && data.extData != undefined) {
        var extData = JSON.parse(data.extData);
        var entContentData = extData.entContent;
        if (entContentData && entContentData.duration != undefined) {
          if (entContentData.duration != 0) {
            mMusicInfo.id = entContentData.songId;
            mMusicInfo.name = entContentData.trackTitle;
            mMusicInfo.singer = entContentData.nickname;
            if (
              entContentData.avatarUrl == undefined ||
              entContentData.avatarUrl.length == 0
            ) {
              mMusicInfo.album = "fonts/icon_play_disc_default.png";
            } else {
              mMusicInfo.album = entContentData.avatarUrl;
            }
          }
          if (extData.volume) {
            mVoice = extData.volume;
          }
          if (extData.source) {
            mMusicSource = extData.source;
          }
          if (extData.playMode) {
            mPlayMode = extData.playMode;
          }
          if (extData.soundEffects) {
            mPlayEffect = extData.soundEffects;
          }
          if (extData.entContent.status == 0) {
            mPlayState = 0;
          } else {
            mPlayState = 1;
          }
          updateSongs();
          updateCurrentSong();
          updateVoice();
          updatePlayMode();
          updateEffectMode();
          updateVoiceSource();
        }
      }
    }
  });
});

//获取倒计时时间显示
function getshowDelayDate(delaySec) {
  var showTime;
  if (delaySec) {
    var timestamp = new Date().valueOf();
    timestamp += delaySec * 1000;
    var newData = new Date(timestamp);
    var hour = newData.getHours();
    var minute = newData.getMinutes();
    showTime = hour + ":" + minute;
  }
  return showTime;
}
//请求歌曲列表
function requestMusicLists(pager) {
  currentPage = pager;
  var paramToNative = {};
  var urlStr = "/ent/music/get/music/list";
  var paramToCilent = {};
  paramToCilent.deviceId = devID;
  paramToCilent.platformId = 2;
  if (pager) {
    paramToCilent.page = pager;
  } else {
    paramToCilent.page = 1;
  }
  paramToCilent.count = 20;
  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpGetBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      if (result.data) {
        if (result.data.currentPage == 1) {
          mui("#refreshContainer")
            .pullRefresh()
            .endPulldownToRefresh();
          songsList.length = 0;
          songsList = result.data.entContents;
          musicListCount = result.data.totalCount;
          resetSongList();
          updateCurrentSong();
        } else if (
          result.data.currentPage == pager &&
          result.data.totalPage >= pager
        ) {
          mui("#refreshContainer")
            .pullRefresh()
            .endPullupToRefresh(); // 加载数据最没有更多内容
          songsList = songsList.concat(result.data.entContents);
          resetSongList();
          updateCurrentSong();
        }
      }
    }
  });
}

function searchSelectedMusic(musicName) {
  var songItem = "";
  if (musicName && musicName.length > 0) {
    var SongIndex = songsList.length;
    while (SongIndex--) {
      if (songsList[SongIndex].trackTitle == musicName) {
        songItem = songsList[SongIndex].songId;
        return songItem;
      }
    }
  }
  return songItem;
}

function searchSelectedMusicModel(musicId) {
  var songItem = {};
  if (musicId) {
    var SongIndex = songsList.length;
    while (SongIndex--) {
      if (songsList[SongIndex].songId == musicId) {
        songItem = songsList[SongIndex];
        return songItem;
      }
    }
  }
  return songItem;
}

function deleteSongListsWithId(musicId) {
  if (musicId && musicId.length > 0) {
    for (var songDeleteIndex in songsList) {
      var songItem = songsList[songDeleteIndex];
      if (songItem.songId == musicId) {
        songsList.splice(songDeleteIndex, 1);
        return true;
      }
    }
  }
  return false;
}
//请求当前音乐
function requestRadioCurrentMusic() {
  var paramToNative = {};
  var urlStr = "/ent/music/get/playMode";
  var paramToCilent = {};
  paramToCilent.deviceId = devID;
  paramToCilent.platformId = 2;
  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;

  plus.gatewayCmd.WLHttpGetBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      if (result.data) {
        $(".mask_layer").hide();
        if (result.data.entContent) {
          mMusicInfo.id = result.data.entContent.songId;
          mMusicInfo.name = result.data.entContent.trackTitle;
          mMusicInfo.singer = result.data.entContent.nickname;
          if (
            result.data.entContent.avatarUrl == undefined ||
            result.data.entContent.avatarUrl.length == 0
          ) {
            mMusicInfo.album = "fonts/icon_play_disc_default.png";
          } else {
            mMusicInfo.album = result.data.entContent.avatarUrl;
          }
          if (result.data.entContent.status == 0) {
            mPlayState = 0;
          } else {
            mPlayState = 1;
          }
          mPlayMode = result.data.playMode;
          mVoice = result.data.volume;
          mPlayEffect = result.data.soundEffects;
          mMusicSource = result.data.source;
          updateSongs();
          updateCurrentSong();
          updateVoice();
          updatePlayMode();
          updateEffectMode();
          updateVoiceSource();
        }
      }
    } else if (result.resultCode == 6000001) {
      $(".mask_layer").show();
    }
  });
}
//删除歌曲
function deleteRadioMusicRequest(musicId) {
  var paramToNative = {};
  var urlStr = "/ent/music/delete/music";
  var paramToCilent = {};
  paramToCilent.deviceId = devID;
  paramToCilent.platform = 0;
  paramToCilent.id = musicId;

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpGetBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      //剔除掉数组中相应ID的数据，并刷新列表
      var deleteResult = deleteSongListsWithId(musicId);
      if (deleteResult) {
        resetSongList();
      } else {
        requestMusicLists(1);
      }
    }
  });
}
//上一首
function requestRadioLast() {
  var paramToNative = {};
  var urlStr = "/ent/music/nextOrLast";
  var paramToCilent = {};
  paramToCilent.deviceId = devID;
  paramToCilent.nextOrLast = "last";

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    }
  });
}
//下一首
function requestRadioNext() {
  var paramToNative = {};
  var urlStr = "/ent/music/nextOrLast";
  var paramToCilent = {};
  paramToCilent.deviceId = devID;
  paramToCilent.nextOrLast = "next";

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    }
  });
}

function actionMusicPlay(index) {
  if (index < songsList.length) {
    var musicId = songsList[index].songId;
    var playUrl = songsList[index].playUrl;

    var paramToNative = {};
    var urlStr = "/ent/music/playMusic";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.songId = musicId;

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;

    plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
      console.log(result);
      if (result.data) {}
    });
  }
}
//播放
function actionMusicPlayWithModel(model) {
  if (model) {
    var musicId = model.songId;
    var playUrl = model.playUrl;

    var paramToNative = {};
    var urlStr = "/ent/music/playMusic";
    var paramToCilent = {};
    paramToCilent.deviceId = devID;
    paramToCilent.songId = musicId;

    paramToNative.url = urlStr;
    paramToNative.param = paramToCilent;

    plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
      console.log(result);
      if (result.resultCode == 0) {
        requestRadioCurrentMusic();
      }
    });
  }
}
//播放模式切换
function changeMusicPlayMode(sendMode) {
  var paramToNative = {};
  var urlStr = "/ent/music/set/playMode";
  var paramToCilent = {};
  paramToCilent.deviceId = gwID;
  paramToCilent.childDeviceId = devID;
  paramToCilent.playMode = sendMode;
  paramToCilent.soundEffects = mPlayEffect;
  // paramToCilent.volume = mVoice;

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    } else if (result.resultCode == 6000001) {
      alert(languageUtil.getResource("offLine"));
    }
  });
}
//播放/暂停
function updateMusicPlayState(state) {
  var paramToNative = {};
  var urlStr = "/ent/music/set/play/status";
  var paramToCilent = {};
  paramToCilent.deviceId = gwID;
  paramToCilent.childDeviceId = devID;
  paramToCilent.playStatus = state;

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    }
  });
}
//音量调节
function updateMusicVoiceState(vol) {
  var paramToNative = {};
  var urlStr = "/ent/music/set/playMode";
  var paramToCilent = {};
  paramToCilent.deviceId = gwID;
  paramToCilent.childDeviceId = devID;
  paramToCilent.playMode = mPlayMode;
  paramToCilent.soundEffects = mPlayEffect;
  paramToCilent.volume = vol;

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    } else if (result.resultCode == 6000001) {
      alert(languageUtil.getResource("offLine"));
    }
  });
}

function updateMusicEffectState(index) {
  var paramToNative = {};
  var urlStr = "/ent/music/set/playMode";
  var paramToCilent = {};
  paramToCilent.deviceId = gwID;
  paramToCilent.childDeviceId = devID;
  paramToCilent.playMode = mPlayMode;
  paramToCilent.soundEffects = index;
  // paramToCilent.volume = mVoice;

  paramToNative.url = urlStr;
  paramToNative.param = paramToCilent;
  plus.gatewayCmd.WLHttpPostBase(paramToNative, function (result) {
    console.log(result);
    if (result.resultCode == 0) {
      requestRadioCurrentMusic();
    } else if (result.resultCode == 6000001) {
      alert(languageUtil.getResource("offLine"));
    }
  });
}

/*设置延时暂停 time为秒*/
function setMusicDelayStop(time) {
  // if (time >= 0){
  //     var sendTimeStr = time.toString();
  //     sendCmd(gwID,devID,ep,0x8007,[sendTimeStr]);
  // }
}

//截取字符串最大长度
function substrWithLength(str, maxLength) {
  var resultStr = str;
  var strlength = str.length;
  if (strlength > 0 && strlength >= maxLength) {
    resultStr = str.substring(0, maxLength);
  }
  return resultStr;
}