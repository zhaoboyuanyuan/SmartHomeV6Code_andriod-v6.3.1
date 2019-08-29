<template>
  <div>
    <headtop :title="$t('maintitle')" @backAction="backAction"></headtop>
    <group>
      <cell v-for="(item, index) in dataSource" :title="item.title" :link="item.routerStr" @click.native="cellClick(index)" :border-intent="false" :key="index" is-link></cell>
    </group>
    <confirm :title="$t('adminPassword')" v-model="showAdminConfirm" @on-confirm="adminAction" @on-cancel="adminPass = null">
      <div slot class="adminInput">
      <x-input v-model="adminPass" :type="adminConfirmType" :placeholder="$t('inputPassword')" :show-clear="false">
        <img :src="eyeSrc" slot="right" 
        style="width:16px;height:12px"
        @click="adminConfirmType == 'password' ? (adminConfirmType='text') : (adminConfirmType='password')">
      </x-input>
      </div>
    </confirm>
  </div>
</template>

<script>
import { Group, Cell, Confirm, XInput } from "vux";
import headtop from "./headerTop";

export default {
  components: {
    Group,
    Cell,
    headtop,
    Confirm,
    XInput
  },
  data() {
    return {
      // note: changing this line won't causes changes
      // with hot-reload because the reloaded component
      // preserves its current state and we are modifying
      // its initial state.
      dataSource: [
        { title: this.$t("maincelltitle1") },
        { title: this.$t("maincelltitle2"), routerStr: "/sysView/sys" },
        { title: this.$t("maincelltitle3"), routerStr: "/selfTestView" },
        { title: this.$t("maincelltitle4"), routerStr: "/sysView/ring" }
      ],
      showAdminConfirm: false,
      adminConfirmType: "password",
      adminPass: null
    };
  },
  computed: {
    eyeSrc() {
      return this.adminConfirmType == "password"
        ? require("../assets/passwordOff.png")
        : require("../assets/passwordOn.png");
    }
  },
  methods: {
    backAction() {
      window.plus.tools.back();
    },
    adminAction() {
      var cmd = {
        cmd: "501",
        appID: this.$store.state.appID,
        gwID: this.$store.getters.gwID,
        devID: this.$store.getters.deviceID,
        endpointNumber: 1,
        clusterId: 0x0101,
        commandType: 1,
        commandId: 0x800b,
        parameter: [this.adminPass]
      };
      console.log(cmd);
      var vm = this;
      plus.gatewayCmd.controlDevice(cmd, result => {
        console.log(result);
        vm.$vux.loading.show({ text: null });
        setTimeout(() => {
          vm.$vux.loading.hide();
        }, 60000);
      });
      this.adminPass = null;
    },
    cellClick(index) {
      if (index == 0) {
        this.showAdminConfirm = true;
      }
    }
  },
  created() {
    try {
      var iphoneX = {
        sysFunc: "getItem:",
        room: "iphoneX",
        id: "iphoneX",
        data: ""
      };
      var iStr = "";
      if (!window.v6sysfunc) {
        iStr = prompt(JSON.stringify(iphoneX));
      }
      if (iStr == "iphoneX") {
        this.$store.commit("changeToIphoneX", true);
      }
    } catch (e) {
      //TODO handle the exception
    }

    window.plus.plusReady(function(){

    })
  },
  mounted: function() {
    var vm = this;
    var plus = window.plus;
    plus.gatewayCmd.getDeviceInfo(function(result) {
      // console.log(result);
      vm.$store.commit("updateDeviceInfo", result);
    });
    plus.gatewayCmd.getCurrentAppID(result => {
      vm.$store.commit("commitAppID", result);
      // console.log("11---"+result);
      // console.log("12---"+vm.$store.state.appID);
    });
    plus.gatewayCmd.newDataRefresh(result => {
      if (result.cmd == "500" && result.devID == vm.$store.getters.deviceID) {
        if (
          result.endpoints[0].clusters[0].attributes[0].attributeId == 0x8006
        ) {
          console.log(result);
          vm.$vux.loading.hide();
          vm.$vux.toast.show({
            text: vm.$t("passErr"),
            position: "middle",
            type: "text"
          });
        } else if (
          result.endpoints[0].clusters[0].attributes[0].attributeId == 0x8007
        ) {
          var value =
            result.endpoints[0].clusters[0].attributes[0].attributeValue;
          if (value.indexOf("01") == 0) {
            vm.$store.commit(
              "commitAdminToken",
              value.substr(2, value.length - 2)
            );
            vm.$vux.loading.hide();
            vm.$router.push("helpView");
          }
        }
      }
    });
  }
};
</script>

<style lang="less" scoped>
.adminInput {
  height: 40px;
  border: 0.8px solid #bbbbbb;
  border-radius: 3px;
  padding: 0;
  margin-bottom: 15px;
}
</style>

<style lang="less">
.vux-loading .weui-toast {
  top: 50%;
  transform: translateX(-50%) translateY(-50%);
}
.vux-x-dialog .weui-dialog {
  top: 28%;
}
</style>