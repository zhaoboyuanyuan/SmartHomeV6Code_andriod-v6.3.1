package cc.wulian.smarthomev6.support.core.mqtt;

import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.wulian.smarthomev6.entity.LocalInfo;
import cc.wulian.smarthomev6.main.application.MainApplication;
import cc.wulian.smarthomev6.support.core.mqtt.bean.SceneBindingBean;
import cc.wulian.smarthomev6.support.utils.MD5Util;

/**
 * Created by zbl on 2017/4/1.
 * 帮助生成网关命令
 */

public class MQTTCmdHelper {

    /**
     * 控制设备 501
     */
    public static String createControlDevice(String gwID, String devID, String data) {
        //TODO 控制设备
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "501");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("devID", devID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 开锁 501
     */
    public static String createUnlock(String gwID, String devID, String pwd) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "501");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("devID", devID);
            jsonMsgContent.put("clusterId", 257);
            jsonMsgContent.put("commandType", 1);
            jsonMsgContent.put("commandId", 32772);
            jsonMsgContent.put("endpointNumber", 1);
            JSONArray array = new JSONArray();
            array.put(pwd);
            jsonMsgContent.put("parameter", array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 设置设备信息 502
     *
     * @param mode //模式：0 切换状态（撤防、布防），2 修改，3 删除
     */
    public static String createSetDeviceInfo(String gwID, String devID, int mode, String name, String roomID) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "502");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("devID", devID);
            jsonMsgContent.put("mode", mode);//模式：0 切换状态（撤防、布防），2 修改，3 删除
            jsonMsgContent.put("name", name);//设备名称
            jsonMsgContent.put("roomID", roomID);//设备区域ID
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 设置场景信息 503
     *
     * @param mode   模式：0 切换状态,1 新增,2 修改,3 删除
     * @param status "1" 取消激活，"2" 激活
     */
    public static String createSetSceneInfo(String gwID, int mode, String sceneID, String name, String icon, String status, String groupID) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "503");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("mode", mode);//模式：0 切换状态,1 新增,2 修改,3 删除
            jsonMsgContent.put("sceneID", sceneID);//场景ID
            jsonMsgContent.put("name", name);//场景名称
            jsonMsgContent.put("icon", icon);//场景图标
            jsonMsgContent.put("status", status);//场景图标
            jsonMsgContent.put("groupID", groupID);//场景分组id
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 获取场景列表 504
     */
    public static String createGetAllScenes(String gwID) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "504");
            jsonMsgContent.put("gwID", gwID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 设置区域信息 505
     *
     * @param mode 模式：1 新增,2 修改,3 删除
     */
    public static String createSetRoomInfo(String gwID, int mode, String name, String roomID, String icon) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "505");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("mode", mode);//模式：1 新增,2 修改,3 删除
            jsonMsgContent.put("name", name);//区域名称
            jsonMsgContent.put("roomID", roomID);//区域ID
            jsonMsgContent.put("icon", icon);//区域图标
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 获取区域列表 506
     */
    public static String createGetAllRooms(String gwID) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "506");
            jsonMsgContent.put("gwID", gwID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 修改网关密码 509
     * 修改密码后仅返回数据给请求的客户端，其它客户端连接断开
     *
     * @param gwPwd 原密码
     * @param data  新密码
     */
    public static String createChangeGatewayPassword(String gwID, String gwPwd, String data) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "509");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("gwPwd", MD5Util.encrypt(gwPwd));//原密码
            jsonMsgContent.put("data", MD5Util.encrypt(data));//新密码
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 获取设备列表 510
     */
    public static String createGetAllDevices(String gwID, String appID) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "510");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("appID", appID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 允许/禁止设备加入网关 511
     *
     * @param data 十进制0到255,0 禁止，255 永久允许，1到254 允许秒数
     */
    public static String createAddDevice(String gwID, String appID, String devID, String type, int data) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "511");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("appID", appID);
            jsonMsgContent.put("type", type);
            jsonMsgContent.put("devID", devID);//设备ID(为空:广播所有设备,指定设备:单播该设备)
            jsonMsgContent.put("data", data);//十进制0到255,0 禁止，255 永久允许，1到254 允许秒数
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 设置或请求网关信息信息 512
     *
     * @param mode 模式(0:获取,1:修改,2:重启网关,3:网关功能恢复出厂（删除网关数据，管家场景等数据都会被清空）,4：ZigBee模块进行复位(网关中添加的子设备会离线)，5：彻底恢复出厂(
     *             执行3,4同时执行)
     */
    public static String createGatewayInfo(String gwID, int mode, String appID, String gwName) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "512");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("mode", mode);//模式
            jsonMsgContent.put("gwName", gwName);//网关名称
            jsonMsgContent.put("appID", appID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 通用设备配置协议
     *
     * @param mode 模式：(1:新增,2:更新,3:获取,4:删除,5获取自增值,6创建自增变量,7删除该设备下所有的数据)
     * @param k    配置项key
     * @param v    数据
     * @param t    时间戳，模式为获取时加该字段
     * @return
     */
    public static String createGatewayConfig(String gwID, int mode, String appID, String deviceID, String k, String v, String t) {
        JSONObject jsonMsgContent = new JSONObject();
        try {
            jsonMsgContent.put("cmd", "521");
            jsonMsgContent.put("gwID", gwID);
            jsonMsgContent.put("m", mode);//模式
            jsonMsgContent.put("d", deviceID);//设备id
            jsonMsgContent.put("appID", appID);
            jsonMsgContent.put("k", k);
            jsonMsgContent.put("v", v);
            jsonMsgContent.put("t", t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMsgContent.toString();
    }

    /**
     * 网关注册 03
     */
    public static String createRegisterMessage(Context context, String gwID, String gwPwd) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "03");
            json.put("appType", "1");
            //数据
            JSONObject gwJsonObj = new JSONObject();
            gwJsonObj.put("gwID", gwID);//MQTTApiConfig.gwID);
            gwJsonObj.put("gwPwd", gwPwd);//MD5Util.encrypt(MQTTApiConfig.gwPassword));
            JSONArray gwJsonArray = new JSONArray();
            gwJsonArray.put(gwJsonObj);
            json.put("data", gwJsonArray);

            LocalInfo localInfo = MainApplication.getApplication().getLocalInfo();
            json.put("appVer", localInfo.appVersion);
            // SIM卡的唯一标识
            //String simId = tm.getSubscriberId();
            // SIM卡序列号
            //String simSerialNo = tm.getSimSerialNumber();
            // SIM卡运营商的国家代码
            //String simCountryIso = tm.getSimCountryIso();
            // SIM卡运营商名称
            //String simOperatorName = tm.getSimOperatorName();

            json.put("appID", localInfo.appID);
            json.put("netType", "wifi");
//            json.put("ismiId", simId);
//            json.put("simSerialNo", simSerialNo);
//            json.put("simCountryIso", simCountryIso);
//            json.put("simOperatorName", simOperatorName);
            json.put("phoneType", localInfo.osVersion);
            json.put("phoneOS", localInfo.os);
            json.put("lang", localInfo.appLang);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 网关遗嘱 15
     */
    public static String createGatewayWill(String appID) {
        JSONObject gatewayWillJson = new JSONObject();
        try {
            gatewayWillJson.put("cmd", "15");
            gatewayWillJson.put("appID", appID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gatewayWillJson.toString();
    }

    /**
     * 云遗嘱 15
     */
    public static String createCloudWill(String token) {
        JSONObject cloudWillJson = new JSONObject();
        try {
//            cloudWillJson.put("cmd", "15");
            cloudWillJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cloudWillJson.toString();
    }

    /**
     * :513：设置场景开关绑定信息
     *
     * @param gwID     网关ID
     * @param devID    设备ID
     * @param dataList 绑定信息列表
     * @return
     */
    public static String createSetSceneBinding(String gwID, String devID, List<SceneBindingBean> dataList) {
        JSONObject setSceneBindingJson = new JSONObject();
        try {
            setSceneBindingJson.put("cmd", "513");
            setSceneBindingJson.put("gwID", gwID);
            setSceneBindingJson.put("devID", devID);
            setSceneBindingJson.put("mode", 1);
            JSONArray dataJson = new JSONArray();
            for (SceneBindingBean dataItem : dataList) {
                JSONObject dataItemJson = new JSONObject();
                dataItemJson.put("endpointNumber", dataItem.endpointNumber);
                dataItemJson.put("sceneID", dataItem.sceneID);
                dataJson.put(dataItemJson);
            }
            setSceneBindingJson.put("data", dataJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return setSceneBindingJson.toString();
    }

    /**
     * 514：读取场景开关绑定信息
     *
     * @param gwID  网关ID
     * @param devID 设备ID
     * @return
     */
    public static String createGetSceneBinding(String gwID, String devID) {
        JSONObject getSceneBindingJson = new JSONObject();
        try {
            getSceneBindingJson.put("cmd", "514");
            getSceneBindingJson.put("gwID", gwID);
            getSceneBindingJson.put("devID", devID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getSceneBindingJson.toString();
    }

    /**
     * 515：雾计算主机_515
     * 模式
     * 1：切换为雾计算主机
     * 2：切换为雾计算子机
     * 3：查询子机列表
     * 4：搜索局域网下的子机
     * 5：绑定子机
     * 6：解绑子机
     *
     * @return
     */
    public static String createMultiGateway(String gwID, String subGwid, String subGwpwd, int mode) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "515");
            json.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            json.put("gwID", gwID);
            json.put("subGwid", subGwid);
            json.put("subGwpwd", subGwpwd);
            json.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 315：app设置网关远程调试功能
     *
     * @param gwID   网关ID
     * @param appID  appID
     * @param status 1：开始调试;0 关闭调试；
     * @return
     */
    public static String appSetGwDebug(String gwID, String appID, String status) {
        JSONObject getAppSetGwDebug = new JSONObject();
        try {
            getAppSetGwDebug.put("cmd", "315");
            getAppSetGwDebug.put("gwID", gwID);
            getAppSetGwDebug.put("appID", appID);
            getAppSetGwDebug.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAppSetGwDebug.toString();
    }

    /**
     * 316：app调用网关自动升级或重启
     *
     * @param gwID    网关ID
     * @param appID   appID
     * @param cmdCode 0：自动升级;1 重启；
     * @return
     */
    public static String gatewayAutoupgradeOrReboot(String gwID, String appID, int cmdCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            String data = null;
            if (cmdCode == 0) {
                data = "#autoupgrade&";
            } else {
                data = "#reboot";
            }
            jsonObject.put("cmd", "316");
            jsonObject.put("gwID", gwID);
            jsonObject.put("appID", appID);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 527 设置场景分组
     *
     * @param mode    模式(0:新增,1:修改,2:删除)
     * @param groupID 分组ID
     * @param name    分组名称
     */
    public static String setSceneGroup(String gwID, int mode, String groupID, String name) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "527");
            json.put("gwID", gwID);
            json.put("groupID", groupID);
            json.put("name", name);
            json.put("mode", mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 528 读取分组列表
     */
    public static String getSceneGroupList(String gwID) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "528");
            json.put("gwID", gwID);
            json.put("appID", MainApplication.getApplication().getLocalInfo().appID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 529 批量设置场景分组
     *
     * @param groupID 分组ID
     * @param data    分组名称 数据（JsonArray["sceneID1", "sceneID2"]）
     */
    public static String volumeSetSceneGroup(String gwID, String groupID, JSONArray data) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "529");
            json.put("gwID", gwID);
            json.put("groupID", groupID);
            json.put("appID", MainApplication.getApplication().getLocalInfo().appID);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    /**
     * 获取lite网关信号强度
     * @param gwID
     * @param data start：允许网关上报wifi信号值 stop：禁止网关上报wifi信号值
     * @return
     */
    public static String getSignalStrength(String gwID, String data) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "580");
            json.put("gwID", gwID);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
