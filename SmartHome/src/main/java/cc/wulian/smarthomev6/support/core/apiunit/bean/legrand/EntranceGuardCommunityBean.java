package cc.wulian.smarthomev6.support.core.apiunit.bean.legrand;

import java.util.List;

public class EntranceGuardCommunityBean {
    private List<CommunityInformationsBean> communityInformations;

    public List<CommunityInformationsBean> getCommunityInformations() {
        return communityInformations;
    }

    public void setCommunityInformations(List<CommunityInformationsBean> communityInformations) {
        this.communityInformations = communityInformations;
    }

    public static class CommunityInformationsBean {
        /**
         * communityId : 75500067
         * communityName : 华盛珑悦
         */

        private String communityId;
        private String communityName;

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
    }
}
