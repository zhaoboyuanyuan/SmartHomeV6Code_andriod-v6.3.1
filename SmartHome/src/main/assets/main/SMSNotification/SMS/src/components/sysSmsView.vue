<template>
    <div>
        <headtop :title="title" :moreStr="$t('confirm')" @moreAction="saveAction" @backAction="backAction"></headtop>
        <div>
            <p class="message">{{ message }}</p>
        </div>
        <usersList v-model="users"></usersList>
    </div>
</template>

<script>
import usersList from "./usersList";
import headtop from "./headerTop";

export default {
  components: {
    usersList,
    headtop
  },
  data() {
    return {
      users: []
    };
  },
  computed: {
    title() {
      return this.$route.params.viewtype == "sys"
        ? this.$t("maincelltitle2")
        : this.$t("maincelltitle4");
    },
    message() {
      return this.$route.params.viewtype == "sys"
        ? this.$t("sysSmsDesc")
        : this.$t("ringSmsDesc");
    },
    msgCode() {
      if (this.$route.params.viewtype == "sys") {
        return "0103011";
      } else {
        var devType = this.$store.getters.devType;
        console.log("devType -- " + devType);
        if (devType == "Bc" || devType == "Bn") {
          return "0103061";
        } else {
          return "0103081";
        }
      }
    }
  },
  mounted: function() {
    console.log("dev 2 " + this.$store.getters.deviceID);
    var plus = window.plus;
    var vm = this;
    plus.tools.getSmsPhone({ messageCode: this.msgCode }, function(result) {
      console.log(result);
      if (result.resultCode == 0 && result.data.audience != undefined) {
        vm.users = result.data.audience;
      }
    });
  },
  methods: {
    saveAction() {
      var vm = this;
      var msgCode = this.msgCode;
      console.log(this.users);
      var plus = window.plus;
      plus.tools.setSmsPhone(
        { messageCode: msgCode, phone: this.users.join(",") },
        function(result) {
          console.log(result);
          vm.$router.go(-1);
        }
      );
    },
    backAction() {
      this.$router.go(-1);
    }
  }
};
</script>

<style lang="less" scoped>
.message {
  width: 90%;
  font-size: 18px;
  display: block;
  margin: 20px auto 0 auto;
}
</style>
