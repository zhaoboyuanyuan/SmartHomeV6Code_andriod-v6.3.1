package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

public class LcDetectionPlanBean {
    /**
     * channelId : 0
     * ruleType : 0
     * rules : [{"beginTime":"00:00:00","period":"Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday","endTime":"23:59:59"}]
     */

    private String channelId;
    private String ruleType;
    private List<RulesBean> rules;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public List<RulesBean> getRules() {
        return rules;
    }

    public void setRules(List<RulesBean> rules) {
        this.rules = rules;
    }

    public static class RulesBean {
        /**
         * beginTime : 00:00:00
         * period : Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday
         * endTime : 23:59:59
         */

        private String beginTime;
        private String period;
        private String endTime;

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
