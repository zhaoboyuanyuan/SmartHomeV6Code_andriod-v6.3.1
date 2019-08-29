package cc.wulian.smarthomev6.support.core.apiunit.bean.icam;

import java.util.List;

/**
 * Created by zbl on 2017/5/11.
 * 请求爱看设备列表
 */

public class ICamDevicesBean {
    public List<ICamDeviceBean> shared;
    public String URL;//"https:\/\/test.wuliangroup.cn\/v3\/user\/devices",
    public int page;//1,
    public int status;//1,
    public int pages;//1,
    public List<ICamDeviceBean> owned;
}
