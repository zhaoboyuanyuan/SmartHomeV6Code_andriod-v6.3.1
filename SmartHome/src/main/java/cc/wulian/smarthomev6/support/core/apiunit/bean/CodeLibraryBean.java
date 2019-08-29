package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * created by huxc  on 2017/11/2.
 * func：电视机遥控器码库bean
 * email: hxc242313@qq.com
 */

public class CodeLibraryBean {
    public List<String> models;
    public List<String> codes;
    public int pageNum;
    public static class ModelCodeBean {
        public String model;
        public String code;

    }

}
