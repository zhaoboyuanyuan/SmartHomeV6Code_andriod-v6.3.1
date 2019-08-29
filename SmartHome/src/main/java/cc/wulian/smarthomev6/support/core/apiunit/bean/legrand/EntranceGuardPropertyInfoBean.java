package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardPropertyInfoBean {
    private List<PropertyMsgInfosBean> propertyMsgInfos;

    public List<PropertyMsgInfosBean> getPropertyMsgInfos() {
        return propertyMsgInfos;
    }

    public void setPropertyMsgInfos(List<PropertyMsgInfosBean> propertyMsgInfos) {
        this.propertyMsgInfos = propertyMsgInfos;
    }

    public static class PropertyMsgInfosBean {
        /**
         * releaseTime : 2016-10-5 12:00:00
         * messageTitle : 停电通知
         * propertyMessage : 今天至明天停电…
         * remark : 123
         */

        private String releaseTime;
        private String messageTitle;
        private String propertyMessage;
        private String remark;

        public String getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(String releaseTime) {
            this.releaseTime = releaseTime;
        }

        public String getMessageTitle() {
            return messageTitle;
        }

        public void setMessageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
        }

        public String getPropertyMessage() {
            return propertyMessage;
        }

        public void setPropertyMessage(String propertyMessage) {
            this.propertyMessage = propertyMessage;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
