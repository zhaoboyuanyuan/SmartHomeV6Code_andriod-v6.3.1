package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardCommunityInfoBean {
    private List<CommunityInformationsBean> communityInformations;

    public List<CommunityInformationsBean> getCommunityInformations() {
        return communityInformations;
    }

    public void setCommunityInformations(List<CommunityInformationsBean> communityInformations) {
        this.communityInformations = communityInformations;
    }

    public static class CommunityInformationsBean {
        /**
         * cityId : 440300
         * cityName : 深圳市
         * communityId : 75500067
         * communityName : 华盛珑悦
         * districtId : 440304
         * districtName : 福田区
         * provinceId : 440000
         */

        private String cityId;
        private String cityName;
        private String communityId;
        private String communityName;
        private String districtId;
        private String districtName;
        private String provinceId;
        private String provinceName;

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

        public String getCommunityId() {
            return communityId;
        }

        public void setCommunityId(String communityId) {
            this.communityId = communityId;
        }

        public String getCommunityName() {
            return communityName;
        }

        public void setCommunityName(String communityName) {
            this.communityName = communityName;
        }

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

        public String getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(String provinceId) {
            this.provinceId = provinceId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }
    }
}
