package cc.wulian.smarthomev6.entity;

import java.util.List;

import cc.wulian.smarthomev6.support.utils.StringUtil;

/**
 * Created by Veev on 2017/6/5
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    设备更多页面配置
 */

public class MoreConfig {

    /**
     * deviceID : 3546fea54684da2156
     * data : [{"group":"admin","name":"管理员设置","offLineDisable":true,"param":[{"key":"url","type":"string","value":"/device/oplock/账号管理.html"}],"item":[{"type":"jump","name":"账号管理","action":"jump:Dialog","offLineDisable":true,"param":[{"key":"url","type":"string","value":"/device/oplock/账号管理.html"}]},{"type":"jump","name":"防胁迫管理","action":"jump:Dialog","param":[{"key":"url","type":"string","value":"/device/oplock/防胁迫管理.html"}]},{"type":"jump","name":"测试管理","action":"jump:H5","offLineDisable":false,"param":[{"key":"url","type":"string","value":"这里是测试管理, 哈哈哈"}]},{"type":"custom","name":"离家按钮","action":"custom:ViewLockBtn","offLineDisable":false}]},{"group":"log","item":[{"type":"jump","name":"日志","action":"jump:Log","param":[{"key":"deviceID","type":"string","value":"abb548642dddcef5"}]},{"type":"jump","name":"报警消息","action":"jump:Alarm","param":[{"key":"deviceID","type":"string","value":"abb548642dddcef5"}]}]}]
     */

    public String deviceID;
    public List<DataBean> data;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MoreConfig{");
        sb.append("deviceID='").append(deviceID).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    public class DataBean {
        /**
         * group : admin
         * name : 管理员设置
         * offLineDisable : true
         * param : [{"key":"url","type":"string","value":"/device/oplock/账号管理.html"}]
         * item : [{"type":"jump","name":"账号管理","action":"jump:Dialog","offLineDisable":true,"param":[{"key":"url","type":"string","value":"/device/oplock/账号管理.html"}]},{"type":"jump","name":"防胁迫管理","action":"jump:Dialog","param":[{"key":"url","type":"string","value":"/device/oplock/防胁迫管理.html"}]},{"type":"jump","name":"测试管理","action":"jump:H5","offLineDisable":false,"param":[{"key":"url","type":"string","value":"这里是测试管理, 哈哈哈"}]},{"type":"custom","name":"离家按钮","action":"custom:ViewLockBtn","offLineDisable":false}]
         */

        public String group;
        public String name;
        public boolean offLineDisable;
        public String enableWithEnterType;
        public List<ParamBean> param;
        public List<ItemBean> item;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DataBean{");
            sb.append("group='").append(group).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", offLineDisable=").append(offLineDisable);
            sb.append(", param=").append(param);
            sb.append(", item=").append(item);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class ItemBean {
        /**
         * type : jump
         * name : 账号管理
         * action : jump:Dialog
         * offLineDisable : true
         * param : [{"key":"url","type":"string","value":"/device/oplock/账号管理.html"}]
         */

        public String type;
        public String name;
        public String action;
        public String desc;
        public boolean offLineDisable;
        public String enableWithEnterType;
        public List<ParamBean> param;

        /**
         * 根据Key获取相应的value
         * @param key 参数的key值
         * @return value值
         */
        public String getValueByKey(String key){
            String value="";
            if(!StringUtil.isNullOrEmpty(key)){
                for(ParamBean bean:param){
                    if(bean.key.equals(key)){
                        value=bean.value;
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ItemBean{");
            sb.append("type='").append(type).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", action='").append(action).append('\'');
            sb.append(", offLineDisable=").append(offLineDisable);
            sb.append(", param=").append(param);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class ParamBean {
        /**
         * key : url
         * type : string
         * value : /device/oplock/账号管理.html
         */

        public String key;
        public String type;
        public String value;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ParamBean{");
            sb.append("key='").append(key).append('\'');
            sb.append(", type='").append(type).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
