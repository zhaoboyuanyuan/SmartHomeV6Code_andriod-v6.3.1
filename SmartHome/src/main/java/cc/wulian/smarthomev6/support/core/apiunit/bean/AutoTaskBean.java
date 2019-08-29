package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

public class AutoTaskBean {

    /**
     * p : {"t":1,"i":1}
     * appID : android867519039700340
     * i : 1
     * cmd : 508
     * gwID : 50294D20C9BE
     * ruleArray : [{"programType":"2","programName":"情景任务","programID":"11","status":"1","triggerArray":[{"name":"#$default$#","exp":"=0#","type":"2","object":"F983F50E004B1200>1>02"}]},{"programType":"2","programName":"定时任务","programID":"12","status":"1","triggerArray":[{"exp":"00 09 ? * 2,3,4,5,6,7,1","type":"1","object":"CURTIME"}]},{"programType":"2","programName":"开启啊暖风防护","programID":"13","status":"1","triggerArray":[{"exp":"01 00 ? * 2,3,4,5,6,7,1","type":"1","object":"CURTIME"}]},{"programType":"2","programName":"开灯咳咳","programID":"14","status":"1","triggerArray":[{"name":"#$default$#","exp":"=0","type":"2","object":"AEFADD0E004B1200>1>03"}]},{"programType":"2","programName":"定时任务","programID":"15","status":"1","triggerArray":[{"exp":"00 06 ? * 2,3,5,6,7,1","type":"1","object":"CURTIME"}]}]
     * userID : ff0934ecece2469e905a58e35193323e
     */

    private PBean p;
    private String appID;
    private int i;
    private String cmd;
    private String gwID;
    private String userID;
    private List<RuleArrayBean> ruleArray;

    public PBean getP() {
        return p;
    }

    public void setP(PBean p) {
        this.p = p;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getGwID() {
        return gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<RuleArrayBean> getRuleArray() {
        return ruleArray;
    }

    public void setRuleArray(List<RuleArrayBean> ruleArray) {
        this.ruleArray = ruleArray;
    }

    public static class PBean {
        /**
         * t : 1
         * i : 1
         */

        private int t;
        private int i;

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    public static class RuleArrayBean {
        /**
         * programType : 2
         * programName : 情景任务
         * programID : 11
         * status : 1
         * triggerArray : [{"name":"#$default$#","exp":"=0#","type":"2","object":"F983F50E004B1200>1>02"}]
         */

        private String programType;
        private String programName;
        private String programID;
        private String status;
        private List<TriggerArrayBean> triggerArray;

        public String getProgramType() {
            return programType;
        }

        public void setProgramType(String programType) {
            this.programType = programType;
        }

        public String getProgramName() {
            return programName;
        }

        public void setProgramName(String programName) {
            this.programName = programName;
        }

        public String getProgramID() {
            return programID;
        }

        public void setProgramID(String programID) {
            this.programID = programID;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<TriggerArrayBean> getTriggerArray() {
            return triggerArray;
        }

        public void setTriggerArray(List<TriggerArrayBean> triggerArray) {
            this.triggerArray = triggerArray;
        }

        public static class TriggerArrayBean {
            /**
             * name : #$default$#
             * exp : =0#
             * type : 2
             * object : F983F50E004B1200>1>02
             */

            private String name;
            private String exp;
            private String type;
            private String object;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getExp() {
                return exp;
            }

            public void setExp(String exp) {
                this.exp = exp;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getObject() {
                return object;
            }

            public void setObject(String object) {
                this.object = object;
            }
        }
    }
}
