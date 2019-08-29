package cc.wulian.smarthomev6.main.device;

import cc.wulian.smarthomev6.entity.MoreConfig;

/**
 * Created by Veev on 2017/6/5
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    IDeviceMore
 */

public interface IDeviceMore {

    void onBindView(MoreConfig.ItemBean bean);

    void onViewRecycled();
}
