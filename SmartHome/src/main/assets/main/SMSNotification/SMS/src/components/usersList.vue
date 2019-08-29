<template>
    <div>
        <div>
            <p class="message">{{ userCountDesc }}</p>
        </div>
        <group>
            <transition-group name="list" mode="out-in">
              <cell class="list-item" v-for="(item, index) in list" :title="item" :border-intent="false" :key="index">
                <img style="width:20px;height:20px" @click="delUser(index)" src="../assets/del_btn.png" slot="default">
              </cell>
            </transition-group>
        </group>
        <div class="addBtn" v-show="list.length < 3" @click="addUser"></div>
        <confirm v-model="showDelConfirm" @on-confirm="delAction">
            <p solt="default" style="font-size:20px">{{ $t("delDesc") }}</p>
        </confirm>
    </div>
</template>

<script>
import { Group, Cell, Confirm } from "vux";
export default {
  name: "usersList",
  components: {
    Group,
    Cell,
    Confirm
  },
  model: {
    prop: "list",
    event: "input"
  },
  props: { list: Array },
  data() {
    return {
      showDelConfirm: false,
      selectIndex: Number
    };
  },
  computed: {
    userCountDesc() {
      return this.$t("userCountDesc", { userCount: this.list.length });
    }
  },
  methods: {
    addUser() {
      var vm = this;
      this.$vux.confirm.prompt(this.$t("phoneAlertPlacehold"), {
        title: this.$t("phoneAlertTitle"),
        onShow() {},
        onCancel() {},
        onConfirm(val) {
          const PHONE_RE = /^1\d{10}$/;
          var result = PHONE_RE.test(val);
          if (result) {
            var list = vm.list;
            list.push(val);
            vm.$emit("input", list);
          } else {
            vm.$vux.toast.show({
              text: vm.$t("phoneErr"),
              position: "middle",
              type: "text"
            });
          }
        }
      });
    },
    delUser(index) {
      this.selectIndex = index;
      this.showDelConfirm = true;
    },
    delAction() {
      var list = this.list;
      this.$delete(list, this.selectIndex);
      this.$emit("input", list);
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
.addBtn {
  width: 30px;
  height: 30px;
  margin: 15px auto 0 auto;
  background-image: url("../assets/add_btn.png");
  background-size: 30px 30px;
}
.list-enter{
  opacity: 0;
  transform: translateX(30px);
}
.list-enter-active{
  transition: all 0.6s;
}
</style>

