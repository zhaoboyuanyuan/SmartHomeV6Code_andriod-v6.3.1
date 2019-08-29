
package cc.wulian.smarthomev6.support.core.apiunit.bean.icam;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 回看接口返回数据
 */
public class ICamReplayBean {

    @JSONField(name = "data")
    private ICamReplayDataBean mICamReplayDataBean;
    @JSONField(name = "status")
    private Long mStatus;

    public ICamReplayDataBean getData() {
        return mICamReplayDataBean;
    }

    public void setData(ICamReplayDataBean ICamReplayDataBean) {
        mICamReplayDataBean = ICamReplayDataBean;
    }

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

}
