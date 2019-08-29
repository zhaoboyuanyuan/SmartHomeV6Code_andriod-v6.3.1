package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

public class LcDeviceInfoBean {
    /**
     * adminFlag : 1
     * deviceId : xxx
     * name : 小物摄像机
     * password : a9c4020fa0fc89c333afe8fa91228d28
     * relationFlag : 1
     * sdomain : test.wuliangroup.cn
     * sipInfo : {"deviceSips":[{"deviceId":"cmic10d250294d4144ac","sdomain":"test.wuliangroup.cn"}],"sdomain":"test.wuliangroup.cn","spassword":"f2951e02772cabb6","suid":"us397596g1zo3dhlmatc"}
     * softVersion : 4.01.017
     * state : 0
     * type : GW01
     * roomId :
     * roomName :
     * version :
     * deviceFlag :
     * data :
     * isPush :
     * tutkid : xxx
     * tutkidPwd : xxx
     * upgradeStatus : 1
     * extData : {"token":"xxxx","channels":[{"channelId":0,"alarmStatus":"xxxx","channelStatus":"xxxx"}]}
     */

    private String adminFlag;
    private String deviceId;
    private String name;
    private String password;
    private int relationFlag;
    private String sdomain;
    private SipInfoBean sipInfo;
    private String softVersion;
    private String state;
    private String type;
    private String roomId;
    private String roomName;
    private String version;
    private String deviceFlag;
    private String data;
    private String isPush;
    private String tutkid;
    private String tutkidPwd;
    private int upgradeStatus;
    private ExtDataBean extData;

    public String getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(String adminFlag) {
        this.adminFlag = adminFlag;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRelationFlag() {
        return relationFlag;
    }

    public void setRelationFlag(int relationFlag) {
        this.relationFlag = relationFlag;
    }

    public String getSdomain() {
        return sdomain;
    }

    public void setSdomain(String sdomain) {
        this.sdomain = sdomain;
    }

    public SipInfoBean getSipInfo() {
        return sipInfo;
    }

    public void setSipInfo(SipInfoBean sipInfo) {
        this.sipInfo = sipInfo;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceFlag() {
        return deviceFlag;
    }

    public void setDeviceFlag(String deviceFlag) {
        this.deviceFlag = deviceFlag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIsPush() {
        return isPush;
    }

    public void setIsPush(String isPush) {
        this.isPush = isPush;
    }

    public String getTutkid() {
        return tutkid;
    }

    public void setTutkid(String tutkid) {
        this.tutkid = tutkid;
    }

    public String getTutkidPwd() {
        return tutkidPwd;
    }

    public void setTutkidPwd(String tutkidPwd) {
        this.tutkidPwd = tutkidPwd;
    }

    public int getUpgradeStatus() {
        return upgradeStatus;
    }

    public void setUpgradeStatus(int upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }

    public ExtDataBean getExtData() {
        return extData;
    }

    public void setExtData(ExtDataBean extData) {
        this.extData = extData;
    }

    public static class SipInfoBean {
        /**
         * deviceSips : [{"deviceId":"cmic10d250294d4144ac","sdomain":"test.wuliangroup.cn"}]
         * sdomain : test.wuliangroup.cn
         * spassword : f2951e02772cabb6
         * suid : us397596g1zo3dhlmatc
         */

        private String sdomain;
        private String spassword;
        private String suid;
        private List<DeviceSipsBean> deviceSips;

        public String getSdomain() {
            return sdomain;
        }

        public void setSdomain(String sdomain) {
            this.sdomain = sdomain;
        }

        public String getSpassword() {
            return spassword;
        }

        public void setSpassword(String spassword) {
            this.spassword = spassword;
        }

        public String getSuid() {
            return suid;
        }

        public void setSuid(String suid) {
            this.suid = suid;
        }

        public List<DeviceSipsBean> getDeviceSips() {
            return deviceSips;
        }

        public void setDeviceSips(List<DeviceSipsBean> deviceSips) {
            this.deviceSips = deviceSips;
        }

        public static class DeviceSipsBean {
            /**
             * deviceId : cmic10d250294d4144ac
             * sdomain : test.wuliangroup.cn
             */

            private String deviceId;
            private String sdomain;

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public String getSdomain() {
                return sdomain;
            }

            public void setSdomain(String sdomain) {
                this.sdomain = sdomain;
            }
        }
    }

    public static class ExtDataBean {
        /**
         * token : xxxx
         * channels : [{"channelId":0,"alarmStatus":"xxxx"}]
         */

        private String token;
        private List<ChannelsBean> channels;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<ChannelsBean> getChannels() {
            return channels;
        }

        public void setChannels(List<ChannelsBean> channels) {
            this.channels = channels;
        }

        public static class ChannelsBean {
            /**
             * channelId : 0
             * alarmStatus : xxxx
             * channelStatus : xxxx
             * channelName : xxxx
             */

            private int channelId;
            private int alarmStatus;
            private int channelStatus;
            private String channelName;

            public int getChannelStatus() {
                return channelStatus;
            }

            public void setChannelStatus(int channelStatus) {
                this.channelStatus = channelStatus;
            }

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }

            public int getChannelId() {
                return channelId;
            }

            public void setChannelId(int channelId) {
                this.channelId = channelId;
            }

            public int getAlarmStatus() {
                return alarmStatus;
            }

            public void setAlarmStatus(int alarmStatus) {
                this.alarmStatus = alarmStatus;
            }

        }
    }
}
