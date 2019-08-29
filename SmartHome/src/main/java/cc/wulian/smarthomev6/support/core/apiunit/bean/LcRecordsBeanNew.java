package cc.wulian.smarthomev6.support.core.apiunit.bean;

public class LcRecordsBeanNew {
    public LcLocalRecordBean.RecordsBean mRecordsBean;
    private String groupTime;
    private boolean isGroup;

    private  LcRecordsBeanNew(LcLocalRecordBean.RecordsBean recordsBean) {
        mRecordsBean = recordsBean;
        groupTime = "";
        isGroup = false;
    }


    public static LcRecordsBeanNew getLcRecordsBeanNew(LcLocalRecordBean.RecordsBean recordsBean) {
        LcRecordsBeanNew info = new LcRecordsBeanNew(recordsBean);
        info.setIsGroup(false);
        info.setGroupTime("");
        return info;
    }


    public String getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(String timeGroup) {
        this.groupTime = timeGroup;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    public LcLocalRecordBean.RecordsBean getmRecordsBean() {
        return mRecordsBean;
    }

    public void setmRecordsBean(LcLocalRecordBean.RecordsBean mRecordsBean) {
        this.mRecordsBean = mRecordsBean;
    }
}
