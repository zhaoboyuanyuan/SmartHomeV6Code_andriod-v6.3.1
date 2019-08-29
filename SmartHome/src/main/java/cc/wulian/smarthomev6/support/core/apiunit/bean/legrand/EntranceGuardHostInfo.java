package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

public class EntranceGuardHostInfo {
    /**
     * communityAddressVO : {"bb":"1","communityId":"75500068","dd":"1","deviceName":"1区1栋1单元0层0室","fc":"155","ff":"0","ii":"0","nn":"01","rr":"1","uc":"2"}
     */

    private CommunityAddressVOBean communityAddressVO;

    public CommunityAddressVOBean getCommunityAddressVO() {
        return communityAddressVO;
    }

    public void setCommunityAddressVO(CommunityAddressVOBean communityAddressVO) {
        this.communityAddressVO = communityAddressVO;
    }

    public static class CommunityAddressVOBean {
        /**
         * bb : 1
         * communityId : 75500068
         * dd : 1
         * deviceName : 1区1栋1单元0层0室
         * fc : 155
         * ff : 0
         * ii : 0
         * nn : 01
         * rr : 1
         * uc : 2
         * deviceSipNumber : 7550006801001010000201
         */

        private String bb;
        private String communityId;
        private String dd;
        private String deviceName;
        private String fc;
        private String ff;
        private String ii;
        private String nn;
        private String rr;
        private String uc;
        private String deviceSipNumber;

        public String getDeviceSipNumber() {
            return deviceSipNumber;
        }

        public void setDeviceSipNumber(String deviceSipNumber) {
            this.deviceSipNumber = deviceSipNumber;
        }

        public String getBb() {
            return bb;
        }

        public void setBb(String bb) {
            this.bb = bb;
        }

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public String getDd() {
            return dd;
        }

        public void setDd(String dd) {
            this.dd = dd;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getFc() {
            return fc;
        }

        public void setFc(String fc) {
            this.fc = fc;
        }

        public String getFf() {
            return ff;
        }

        public void setFf(String ff) {
            this.ff = ff;
        }

        public String getIi() {
            return ii;
        }

        public void setIi(String ii) {
            this.ii = ii;
        }

        public String getNn() {
            return nn;
        }

        public void setNn(String nn) {
            this.nn = nn;
        }

        public String getRr() {
            return rr;
        }

        public void setRr(String rr) {
            this.rr = rr;
        }

        public String getUc() {
            return uc;
        }

        public void setUc(String uc) {
            this.uc = uc;
        }
    }
}
