<template>
    <div>
    <header :class="[$store.state.isIphoneX ? 'header-iphoneX' : 'header']" :style="{'background-image': bgImg}">
      <a class="header-left-img" @click="backAction"></a>
      <p class="title">{{ title }}</p> 
      <a class="more" @click="saveAction">{{ moreStr }}</a>
    </header>
    <div v-bind:style="{ width: 100, height: posHeight + 'px' }"></div>
    </div>
</template>

<script>
export default {
  name: "headtop",
  components: {},
  props: {
    title: {
      type: String,
      default: ""
    },
    moreStr: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      bgImg: 'url('+require("../assets/headerBackground.png")+')'
    };
  },
  computed: {
    posHeight () {
      return this.$store.state.isIphoneX ? 86 : 64 
    }
  },
  methods: {
    saveAction() {
      if (this.moreStr != "") {
        this.$emit("moreAction");
      }
    },
    backAction() {
      this.$emit("backAction");
    }
  }
};
</script>

<style lang="less" scoped>
.text {
  font-size: 18px;
  color: #ffffff;
  font-family: "微软雅黑";
}
.sub {
  display: block;
  float: left;
  margin: 30px auto 0 auto;
  padding: 0;
  overflow: hidden;
  .text;
}
.headercommon {
  height: 64px;
  width: 100%;
  margin: 0;
  text-align: center;
  overflow: hidden;
  position: fixed;
  top: 0;
  background-size: 100% 100%;
  .text;
}
.header-iphoneX {
  padding-top: 22px;
  .headercommon;
}
.header {
  padding-top: 0;
  .headercommon;
}
.title {
  .sub;
  width: 76%;
}
.header-left-img {
  .sub;
  width: 12%;
  height: 24px;
  background: url("../assets/icon_back.png") no-repeat center center;
  background-size: 22px 16px;
}
.more {
  .sub;
  width: 12%;
}
</style>
