// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import FastClick from 'fastclick'
import Vuex from 'vuex'
import vuexI18n from 'vuex-i18n'
import VueRouter from './router/index'
import App from './App'

require("./bridge/plus")
require("./bridge/gatewayCmd")
require("./bridge/tools")

Vue.use(Vuex)
/**  Vuex i18n **/
let store = new Vuex.Store({
  state: {
    deviceInfo: {},
    appID: null,
    adminToken: null,
    isIphoneX: false
  },
  getters: {
    deviceID: ({ deviceInfo }) => {
      return deviceInfo.devID;
    },
    devType: ({ deviceInfo }) => {
      return deviceInfo.type;
    },
    gwID: ({ deviceInfo }) => {
      return deviceInfo.gwID;
    }
  },
  mutations: {
    updateDeviceInfo: (state, deviceInfo) => {
      // 变更状态
      state.deviceInfo = deviceInfo;
    },
    commitAppID: (state, appID) => {
      state.appID = appID;
    },
    commitAdminToken: (state, token) => {
      state.adminToken = token;
    },
    changeToIphoneX: (state, isPX) => {
      state.isIphoneX = isPX;
    }
  },
  modules: {
    i18n: vuexI18n.store
  }
})
Vue.use(vuexI18n.plugin, store)
const enLocales = require('json-loader!yaml-loader!./locales/en.yml')
const zhLocales = require('json-loader!yaml-loader!./locales/zh-CN.yml')
const finalLocales = {
  'en': enLocales,
  'zh-CN': zhLocales
}
for (let i in finalLocales) {
  Vue.i18n.add(i, finalLocales[i])
}

/** router **/
const router = VueRouter

FastClick.attach(document.body)

Vue.config.productionTip = false

/** vux Plugin **/
import { AlertPlugin, LocalePlugin, ConfirmPlugin, ToastPlugin, LoadingPlugin } from 'vux'
Vue.use(AlertPlugin)
Vue.use(LocalePlugin)
Vue.use(ConfirmPlugin)
Vue.use(ToastPlugin)
Vue.use(LoadingPlugin)

/** 判断语言用国际化 **/
const nowLocale = Vue.locale.get()
if (/zh/.test(nowLocale)) {
  Vue.i18n.set('zh-CN')
} else {
  Vue.i18n.set('en')
}

/* eslint-disable no-new */
new Vue({
  store,
  router,
  render: h => h(App)
}).$mount('#app-box')
