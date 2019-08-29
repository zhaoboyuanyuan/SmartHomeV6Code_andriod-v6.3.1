package cc.wulian.smarthomev6.support.core.apiunit.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Veev on 2017/12/19.
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    BgmCmd517Bean
 */

public class BgmCmd517Bean {
    public String cmd;
    public String gwID;
    public String devID;
    public int page;

    @JSONField(serialize = false)
    public String data;

}
