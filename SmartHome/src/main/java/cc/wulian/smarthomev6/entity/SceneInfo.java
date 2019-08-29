package cc.wulian.smarthomev6.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;

import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBean;
import cc.wulian.smarthomev6.support.utils.ConstUtil;

/**
 */
@Entity
public class SceneInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String gwID;
    @NotNull
    private String sceneID;
    private int sceneIDInt;
    @NotNull
    private String name;
    private String icon;
    private String groupID;
    private String programID;
    private String groupName;
    private String status;

    private int sort;

    public SceneInfo() {

    }

    public SceneInfo(SceneBean bean) {
        gwID = bean.gwID;
        sceneID = bean.sceneID;
        programID = bean.programID;
        name = bean.name;
        icon = bean.icon;
        status = bean.status;
        groupID = bean.groupID;
    }

    /**
     * 更新this
     * 并且返回是否需要更新
     *
     * @param bean that bean
     * @return true    需要更新 false	不需要更新
     */
    public boolean update(SceneBean bean) {
        boolean isNeedUpdated = false;

        if (!TextUtils.isEmpty(bean.programID) && !TextUtils.equals(bean.programID, programID)) {
            programID = bean.programID;
            isNeedUpdated = true;
        }
        if (!TextUtils.isEmpty(bean.name) && !TextUtils.equals(bean.name, name)) {
            name = bean.name;
            isNeedUpdated = true;
        }
        if (!TextUtils.isEmpty(bean.icon) && !TextUtils.equals(bean.icon, icon)) {
            icon = bean.icon;
            isNeedUpdated = true;
        }
        if (!TextUtils.isEmpty(bean.status) && !TextUtils.equals(bean.status, status)) {
            status = bean.status;
            isNeedUpdated = true;
        }
        if (!TextUtils.equals(bean.groupID, groupID)) {
            groupID = bean.groupID;
            isNeedUpdated = true;
        }

        return isNeedUpdated;
    }

    public SceneBean getBean() {
        SceneBean bean = new SceneBean();
        bean.gwID = gwID;
        bean.sceneID = sceneID;
        programID = bean.programID;
        bean.name = name;
        bean.icon = icon;
        bean.status = status;
        return bean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SceneInfo info = (SceneInfo) o;

        return sceneID != null ? sceneID.equals(info.sceneID) : info.sceneID == null;

    }

    @Override
    public int hashCode() {
        return sceneID != null ? sceneID.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SceneInfo{");
        sb.append("id=").append(id);
        sb.append(", gwID='").append(gwID).append('\'');
        sb.append(", sceneID='").append(sceneID).append('\'');
        sb.append(", sceneIDInt=").append(sceneIDInt);
        sb.append(", name='").append(name).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", groupID='").append(groupID).append('\'');
        sb.append(", programID='").append(programID).append('\'');
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", sort=").append(sort);
        sb.append('}');
        return sb.toString();
    }

    public SceneInfo(String sceneID, String name, String icon, String status) {
        this.sceneID = sceneID;
        this.name = name;
        this.icon = icon;
        this.status = status;
    }

    public SceneInfo(String name, String icon, String status) {
        this.name = name;
        this.icon = icon;
        this.status = status;
    }

    public SceneInfo(JSONObject jsonObj) {
        gwID = jsonObj.getString(ConstUtil.KEY_GW_ID);
        sceneID = jsonObj.getString(ConstUtil.KEY_SCENE_ID);
        name = jsonObj.getString(ConstUtil.KEY_NAME);
        icon = jsonObj.getString(ConstUtil.KEY_ICON);
        groupID = jsonObj.getString(ConstUtil.KEY_GROUP_ID);
        groupName = jsonObj.getString(ConstUtil.KEY_GROUP_NAME);
        status = jsonObj.getString(ConstUtil.KEY_STUS);
    }

    @Generated(hash = 1014064319)
    public SceneInfo(Long id, String gwID, @NotNull String sceneID, int sceneIDInt,
                     @NotNull String name, String icon, String groupID, String programID,
                     String groupName, String status, int sort) {
        this.id = id;
        this.gwID = gwID;
        this.sceneID = sceneID;
        this.sceneIDInt = sceneIDInt;
        this.name = name;
        this.icon = icon;
        this.groupID = groupID;
        this.programID = programID;
        this.groupName = groupName;
        this.status = status;
        this.sort = sort;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGwID() {
        return this.gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public String getSceneID() {
        return this.sceneID;
    }

    public void setSceneID(String sceneID) {
        this.sceneID = sceneID;
    }

    public int getSceneIDInt() {
        return sceneIDInt;
    }

    public void setSceneIDInt(int sceneIDInt) {
        this.sceneIDInt = sceneIDInt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupID() {
        return this.groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProgramID() {
        return this.programID;
    }

    public void setProgramID(String programID) {
        this.programID = programID;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
