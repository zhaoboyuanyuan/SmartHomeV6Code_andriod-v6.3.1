package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardProvinceBean {
    private List<ProvinceInformationsBean> provinceInformations;

    public List<ProvinceInformationsBean> getProvinceInformations() {
        return provinceInformations;
    }

    public void setProvinceInformations(List<ProvinceInformationsBean> provinceInformations) {
        this.provinceInformations = provinceInformations;
    }

    public static class ProvinceInformationsBean {
        /**
         * provinceId : 440000
         * provinceName : 广东
         */

        private String provinceId;
        private String provinceName;

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
