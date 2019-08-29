package cc.wulian.smarthomev6.support.core.apiunit.bean;

/**
 * Created by zbl on 2017/12/20.
 */

public class GrantRelationBean {
    public String grantRelation;

    public boolean isGrant() {
        return !"0".equals(grantRelation);
    }
}
