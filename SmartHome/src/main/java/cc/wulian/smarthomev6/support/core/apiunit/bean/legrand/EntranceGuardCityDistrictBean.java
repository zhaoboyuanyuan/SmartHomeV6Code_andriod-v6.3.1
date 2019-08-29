package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardCityDistrictBean {
    private List<DistrictInformationsBean> districtInformations;

    public List<DistrictInformationsBean> getDistrictInformations() {
        return districtInformations;
    }

    public void setDistrictInformations(List<DistrictInformationsBean> districtInformations) {
        this.districtInformations = districtInformations;
    }

    public static class DistrictInformationsBean {
        /**
         * districtId : 440304
         * districtName : 福田区
         */

        private String districtId;
        private String districtName;

        public String getDistrictId() {
            return districtId;
        }

        public void setDistrictId(String districtId) {
            this.districtId = districtId;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }
    }
}
