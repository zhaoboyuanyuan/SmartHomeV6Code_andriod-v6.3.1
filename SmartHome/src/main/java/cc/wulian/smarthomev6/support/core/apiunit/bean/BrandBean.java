package cc.wulian.smarthomev6.support.core.apiunit.bean;

import java.util.List;

/**
 * Created by Veev on 2017/9/12
 * Tel:         18365264930
 * QQ:          2355738466
 * Email:       wei.wang@wuliangroup.com
 * Function:    空调品牌
 */

public class BrandBean {
    public List<BrandsBean> brands;

    public static class BrandsBean {

        public String groupName;
        public List<GroupBean> group;
    }

    public static class GroupBean {
        public String localName;
        public String brandName;
        public String brandId;
    }

    public static class BrandSortBean {
        /**
         * 0 item, 1 group
         */
        public int type;
        public String groupName;
        public String localName;
        public String brandName;
        public String brandId;

        private BrandSortBean() {
        }

        public static BrandSortBean groupBean(String groupName) {
            BrandSortBean b = new BrandSortBean();
            b.type = 1;
            b.groupName = groupName;
            b.brandName = groupName;
            return b;
        }

        public static BrandSortBean itemBean(GroupBean g, String groupName) {
            BrandSortBean b = new BrandSortBean();
            b.type = 0;
            b.localName = g.localName;
            b.brandName = g.brandName;
            b.groupName = groupName;
            b.brandId = g.brandId;
            return b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BrandSortBean that = (BrandSortBean) o;

            if (type != that.type) return false;
            return brandName.equals(that.brandName);

        }

        @Override
        public int hashCode() {
            int result = type;
            result = 31 * result + brandName.hashCode();
            return result;
        }
    }

    public static class CodeListBean {
        public List<String> codes;
    }

    public static class CodeBean {
        public String picks;
    }
}
