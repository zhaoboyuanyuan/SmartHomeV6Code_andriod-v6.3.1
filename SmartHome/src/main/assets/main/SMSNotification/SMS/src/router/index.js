import Vue from 'vue'
import Router from 'vue-router'
// import Home from '../components/smsMainView'
// import sysView from '../components/sysSmsView'
// import helpView from "../components/helpSmsView"
// import selfTestView from "../components/selfTestSmsView"

const selfTestView = () => import("../components/selfTestSmsView")

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      component: function (resolve) {
        require(['../components/smsMainView'], resolve)
      }
    },
    {
      path: '/sysView/:viewtype',
      component: function (resolve) {
        require(['../components/sysSmsView'], resolve)
      }
    },
    {
      path: '/helpView',
      component: function (resolve) {
        require(['../components/helpSmsView'], resolve)
      }
    },
    {
      path: '/selfTestView',
      component: selfTestView
    }
  ]
})
