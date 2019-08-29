package cc.wulian.smarthomev6.support.core.mqtt.bean;

public class EntranceGuardCallBean {

    /**
     * devID : 755000681
     * extData : {"dd":"0000001","bb":"0000001","rr":"0000001","ff":"0001","ii":"0001","communityId":"75500068","uc":"1"}
     * messageCode : 0404012
     * gwID : 755000681
     * type : CG27
     */

    private String devID;
    private String extData;
    private String messageCode;
    private String gwID;
    private String type;

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getGwID() {
        return gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class ExtData {
        String dd;
        String bb;
        String rr;
        String ff;
        String ii;
        String communityId;
        String uc;

        public String getDd() {
            return dd;
        }

        public void setDd(String dd) {
            this.dd = dd;
        }

        public String getBb() {
            return bb;
        }

        public void setBb(String bb) {
            this.bb = bb;
        }

        public String getRr() {
            return rr;
        }

        public void setRr(String rr) {
            this.rr = rr;
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

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public String getUc() {
            return uc;
        }

        public void setUc(String uc) {
            this.uc = uc;
        }
    }

}
