package cc.wulian.smarthomev6.support.core.device;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupListBean;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneGroupSet;

/**
 * Created by zbl on 2017/4/11.
 * 场景信息缓存
 */

public class SceneCache {
    private LinkedHashMap<String, SceneBean> sceneHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, SceneGroupListBean.DataBean> groupHashMap = new LinkedHashMap<>();

    public SceneCache() {
    }

    public void add(@NonNull SceneBean sceneBean) {
        sceneHashMap.put(sceneBean.sceneID, sceneBean);
    }

    public void addAll(@NonNull List<SceneBean> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<SceneBean>() {
                @Override
                public int compare(SceneBean o1, SceneBean o2) {
                    return o1.sceneID.compareTo(o2.sceneID);
                }
            });
            for (SceneBean bean : list) {
                sceneHashMap.put(bean.sceneID, bean);
            }
        }
    }

    public void remove(@NonNull SceneBean sceneBean) {
        sceneHashMap.remove(sceneBean.sceneID);
    }

    public SceneBean get(@NonNull String sceneID) {
        return sceneHashMap.get(sceneID);
    }

    public boolean hasScene(String sceneID) {
        return sceneHashMap.containsKey(sceneID);
    }

//    public String getSceneName(@NonNull String sceneID) {
//
//        String name = null;
//        MainApplication mainApplication = MainApplication.getApplication();
//        if (TextUtils.isEmpty(sceneID)) {
//            name = mainApplication.getString(R.string.text_activity_area_detail_text_NoneArea);
//        } else {
//            SceneBean sceneBean = get(sceneID);
//            if (sceneBean != null) {
//                name = sceneBean.name;
//            } else {
//                name = "";
//            }
//        }
//        return name;
//    }

    public Collection<SceneBean> getScenes() {
        return sceneHashMap.values();
    }

    public void clear() {
        sceneHashMap.clear();
    }

    public void addAllGroups(@NonNull List<SceneGroupListBean.DataBean> list) {
        groupHashMap.clear();
        if (list != null) {
            Collections.sort(list, new Comparator<SceneGroupListBean.DataBean>() {
                @Override
                public int compare(SceneGroupListBean.DataBean o1, SceneGroupListBean.DataBean o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (SceneGroupListBean.DataBean bean : list) {
                groupHashMap.put(bean.getGroupID(), bean);
            }
        }
    }

    public void addGroup(SceneGroupSet sceneGroupSet) {
        SceneGroupListBean.DataBean bean = new SceneGroupListBean.DataBean();
        bean.setGroupID(sceneGroupSet.getGroupID());
        bean.setName(sceneGroupSet.getName());
        groupHashMap.put(bean.getGroupID(), bean);
    }

    public String getGroupName(@NonNull String groupID) {
        if (groupHashMap.containsKey(groupID)) {
            return groupHashMap.get(groupID).getName();
        } else {
            return "";
        }
    }

    public List<SceneGroupListBean.DataBean> getGroups() {
        List<SceneGroupListBean.DataBean> groupList;
        groupList = new ArrayList<>(groupHashMap.values());
        Collections.sort(groupList, new Comparator<SceneGroupListBean.DataBean>() {
            @Override
            public int compare(SceneGroupListBean.DataBean o1, SceneGroupListBean.DataBean o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return groupList;
    }

    public void removeGroup(String groupID) {
        groupHashMap.remove(groupID);
    }

    public void clearGroups() {
        groupHashMap.clear();
    }
}
