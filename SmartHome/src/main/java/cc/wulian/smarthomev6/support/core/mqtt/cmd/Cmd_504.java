package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneListBean;
import cc.wulian.smarthomev6.support.event.GetSceneListEvent;

/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 获取场景列表
 */

public class Cmd_504 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        SceneListBean sceneListBean = JSON.parseObject(msgContent, SceneListBean.class);
        MainApplication.getApplication().getSceneCache().addAll(sceneListBean.data);
        MainApplication.getApplication().setSceneCached(true);
        EventBus.getDefault().post(new GetSceneListEvent(sceneListBean));
    }
}
