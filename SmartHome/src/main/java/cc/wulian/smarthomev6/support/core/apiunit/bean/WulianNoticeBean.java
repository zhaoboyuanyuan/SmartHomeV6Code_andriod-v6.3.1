package cc.wulian.smarthomev6.support.core.apiunit.bean;


import java.util.List;

public class WulianNoticeBean {

    private List<NoticesBean> notices;

    public List<NoticesBean> getNotices() {
        return notices;
    }

    public void setNotices(List<NoticesBean> notices) {
        this.notices = notices;
    }

    public static class NoticesBean {
        /**
         * index : 1
         * content : test1
         */

        private int index;
        private String content;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
