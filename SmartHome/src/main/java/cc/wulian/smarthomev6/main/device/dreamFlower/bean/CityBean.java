package cc.wulian.smarthomev6.main.device.dreamFlower.bean;

import java.util.List;

/**
 * created by huxc  on 2017/12/19.
 * func： 梦想之花城市bean
 * email: hxc242313@qq.com
 */

public class CityBean {
    public String countryCode;
    public String countryName;
    public String provinceName;
    public List<CityListBean> cityList;

   public  class CityListBean {
        public String cityName;
        public String cityCode;
    }
}
