package cc.wulian.smarthomev6.support.event;

import cc.wulian.smarthomev6.support.core.apiunit.bean.UserBean;

/**
 * 作者: mamengchao
 * 时间: 2017/3/30 0030
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class AccountEvent {

    public static final int ACTION_LOGIN = 0;
    public static final int ACTION_LOGOUT = -1;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_CHANGE_PWD = 2;

    public final int action;
    public UserBean userBean;

    public AccountEvent(int action, UserBean userBean)
    {
        this.action = action;
        this.userBean = userBean;
    }
}
