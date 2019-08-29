package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardCityBean {
    private List<CityInformationsBean> cityInformations;

    public List<CityInformationsBean> getCityInformations() {
        return cityInformations;
    }

    public void setCityInformations(List<CityInformationsBean> cityInformations) {
        this.cityInformations = cityInformations;
    }

    public static class CityInformationsBean {
        /**
         * cityId : 440300
         * cityName : 深圳市
         */

        private String cityId;
        private String cityName;

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
    }
}
