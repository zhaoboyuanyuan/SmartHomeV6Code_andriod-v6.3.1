package cc.wulian.smarthomev6.support.core.apiunit.bean;

public class LcSimpleInfoBean {
    /**
     * devices : {"deviceType":"xxxx","location":"x"}
     */

    private DevicesBean devices;

    public DevicesBean getDevices() {
        return devices;
    }

    public void setDevices(DevicesBean devices) {
        this.devices = devices;
    }

    public static class DevicesBean {
        /**
         * deviceType : xxxx
         * location : x
         */

        private String deviceType;
        private String location;

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
