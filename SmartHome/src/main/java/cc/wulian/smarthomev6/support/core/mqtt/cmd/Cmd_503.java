package cc.wulian.smarthomev6.support.core.mqtt.cmd;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.event.SceneInfoEvent;

/**
 * Created by Veev on 2017/5/19
 * Tel: 18365264930
 * QQ: 2355738466
 * Function:
 */

public class Cmd_503 implements CmdHandle {
    @Override
    public void handle(String msgContent) {
        SceneBean sceneBean = JSON.parseObject(msgContent, SceneBean.class);
        if (sceneBean.mode == 3) {
            MainApplication.getApplication().getSceneCache().remove(sceneBean);
        } else {
            if ("2".equals(sceneBean.status)) {
                for (SceneBean b : MainApplication.getApplication().getSceneCache().getScenes()) {
                    if (b.status.equals("2")) {
                        b.status = "1";
                    }
                }
            }
            MainApplication.getApplication().getSceneCache().add(sceneBean);
            MainApplication.getApplication().setSceneCached(true);
        }
        EventBus.getDefault().post(new SceneInfoEvent(sceneBean));
    }
}
