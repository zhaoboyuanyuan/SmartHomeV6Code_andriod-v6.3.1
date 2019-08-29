<template>
    <div>
        <headtop :title="title" :moreStr="$t('confirm')" @moreAction="saveAction" @backAction="backAction"></headtop>
            <p class="message">{{ message }}</p>
            <group :title="$t('setHelpUser')">
                <cell
                class="lockCell"
                :title="selectedUserName"
                is-link
                :border-intent="false"
                :arrow-direction="showLockUser ? 'up' : 'down'"
                @click.native="showLockUser = !showLockUser">
                </cell>
            </group>
            <div v-if="showLockUser" class="selectView">
                <div class="locklistItem" v-for="lock in lockUsers" @click="selectLockUser(lock)" :key="lock.uid" >{{ getUserName(lock) }}</div>
            </div>
            <p class="tip">{{ $t("helpSmsTip") }}</p>
            <usersList v-model="users"></usersList>
    </div>
</template>

<script>
import headtop from "./headerTop";
import usersList from "./usersList";
import { Cell, Group } from "vux";

export default {
  data() {
    return {
      lockUsers: [{ uid: "999999", selected: "" }],
      selectedLock: {},
      users: [],
      title: this.$t("maincelltitle1"),
      message: this.$t("helpSmsDesc"),
      showLockUser: false
    };
  },

  components: {
    headtop,
    usersList,
    Cell,
    Group
  },

  computed: {
    selectedUserName() {
      if (this.selectedLock.uid == undefined) {
        return "不设置";
      } else if (this.selectedLock.uid == "999999") {
        return "不设置";
      } else {
        return this.$t("admin") + this.selectedLock.uid;
      }
    }
  },

  mounted: function() {
    var cmd = {
      cmd: "520",
      appID: this.$store.state.appID,
      gwID: this.$store.getters.gwID,
      devID: this.$store.getters.deviceID,
      operType: 1,
      data: {
        tk: this.$store.state.adminToken,
        ut: "0",
        f: "2"
      }
    };
    var vm = this;
    plus.gatewayCmd.controlDevice(cmd, result => {
      console.log(result);
      vm.$vux.loading.show({ text: null });
      setTimeout(() => {
        vm.$vux.loading.hide();
      }, 5000);
    });
    plus.gatewayCmd.newDataRefresh(result => {
      if (
        result.cmd == "520" &&
        result.devID == vm.$store.getters.deviceID &&
        result.operType == 1
      ) {
        vm.$vux.loading.hide();
        vm.lockUsers = vm.lockUsers.concat(result.data.list);
        result.data.list.forEach(element => {
          if (element.me == "1") {
            vm.selectedLock = element;
            vm.lockUsers[0].selected = element.uid;
          }
        });
      }
    });
    plus.tools.getSmsPhone({ messageCode: "0103051" }, function(result) {
      console.log(result);
      if (result.resultCode == 0 && result.data.audience != undefined) {
        vm.users = result.data.audience;
      }
    });
  },

  methods: {
    saveAction() {
      var vm = this;
      //云短信设置
      plus.tools.setSmsPhone(
        { messageCode: "0103051", phone: this.users.join(",") },
        function(result) {
          console.log(result);
          vm.$router.go(-1);
        }
      );

      //网关防胁迫设置
      let cmd = null;
      if (this.selectedLock.uid != undefined) {
        if (this.selectedLock.uid != "999999") {
          cmd = {
            cmd: "520",
            appID: this.$store.state.appID,
            gwID: this.$store.getters.gwID,
            devID: this.$store.getters.deviceID,
            operType: 6,
            data: {
              tk: this.$store.state.adminToken,
              uid: this.selectedLock.uid,
              me: "1"
            }
          };
        } else {
          if (this.selectedLock.selected != "") {
            cmd = {
              cmd: "520",
              appID: this.$store.state.appID,
              gwID: this.$store.getters.gwID,
              devID: this.$store.getters.deviceID,
              operType: 6,
              data: {
                tk: this.$store.state.adminToken,
                uid: this.selectedLock.selected,
                me: "0"
              }
            };
          } else {
            return;
          }
        }

        plus.gatewayCmd.controlDevice(cmd, result => {
          console.log(result);
        });
      }
    },

    backAction() {
      this.$router.go(-1);
    },
    selectLockUser(val) {
      console.log(val);
      this.selectedLock = val;
      this.showLockUser = false;
    },

    getUserName(lock) {
      let str = "";
      if (lock.uid != "999999") {
        str = this.$t("admin") + lock.uid;
      } else {
        str = "不设置";
      }
      return str;
    }
  }
};
</script>
<style lang='less' scoped>
.message {
  width: 90%;
  font-size: 18px;
  display: block;
  margin: 20px auto 0 auto;
}
.lockCell {
  height: 25px;
}
.selectView {
  position: absolute;
  width: 100%;
  background-color: #fff;
  z-index: 999;
}
.locklistItem {
  width: 100%;
  height: 44px;
  border-bottom: 0.5px solid #f2f2f2;
  text-align: center;
  line-height: 44px;
}
.tip {
  padding-left: 15px;
  color: #8cd451;
  font-size: 13px;
  margin-top: 10px;
  margin-bottom: 15px;
}
</style>