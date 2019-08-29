package cc.wulian.smarthomev6.support.core.apiunit.bean.icam;

import java.io.Serializable;

/**
 * Created by hxc on 2017/5/10.
 */

public class ICamCheckBindBean implements Serializable {
    /**
     * serialVersionUID 作用:
     */
    private static final long serialVersionUID = -6026158830330184980L;
    public String uuid;
    public String seed;
    public long timestamp;
    public String email;
    public String status;
    public String username;
}
