package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardDeviceInfoBean {
    private List<DeviceInformationsBean> deviceInformations;

    public List<DeviceInformationsBean> getDeviceInformations() {
        return deviceInformations;
    }

    public void setDeviceInformations(List<DeviceInformationsBean> deviceInformations) {
        this.deviceInformations = deviceInformations;
    }

    public static class DeviceInformationsBean {
        /**
         * uc : 2
         * nn : 2
         * fc : 2
         * deviceName : 2
         * deviceSipNumber : 2
         */

        private String uc;
        private String nn;
        private String fc;
        private String deviceName;
        private String deviceSipNumber;

        public String getUc() {
            return uc;
        }

        public void setUc(String uc) {
            this.uc = uc;
        }

        public String getNn() {
            return nn;
        }

        public void setNn(String nn) {
            this.nn = nn;
        }

        public String getFc() {
            return fc;
        }

        public void setFc(String fc) {
            this.fc = fc;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceSipNumber() {
            return deviceSipNumber;
        }

        public void setDeviceSipNumber(String deviceSipNumber) {
            this.deviceSipNumber = deviceSipNumber;
        }
    }
}
