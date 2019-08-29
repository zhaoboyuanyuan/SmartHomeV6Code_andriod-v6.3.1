<template>
 <div>
    <headtop class="topheader" :title="title" :moreStr="$t('confirm')" @moreAction="saveAction" @backAction="backAction"></headtop>
    <p class="message">{{ message }}</p>
    <group :title="$t('checkTime')">
        <div class="time" @click="selectTime">{{num2string(slTime[0], 2)+":"+num2string(slTime[1], 2)}}</div>
    </group>
    <p class="tip">{{ $t("repeat") }}</p>
    <div class="weekday-box">
        <checker class="weekday" v-model="slWeekday" type="checkbox" default-item-class="weekday-item" selected-item-class="weekday-item-selected">
            <checker-item :value="item.value" v-for="(item, index) in weekday" :key="index">{{item.text}}</checker-item>
        </checker>
    </div>
    <checklist title="检测内容" :options="msgCodeList" v-model="slMsgCode" label-position="left"></checklist>
    <usersList v-model="users"></usersList>
    <div>
    <popup-picker v-show="false" :show="showTime" :data="time" v-model="slTime" :fixed-columns="2" @on-hide="showTime = false"></popup-picker>
    </div>
 </div>
</template>


<script type="text/ecmascript-6">
import headtop from './headerTop'
import usersList from './usersList'
import {
  Checker,
  CheckerItem,
  Checklist,
  Group,
  Picker,
  PopupPicker
} from 'vux'

let n = 0
let hour = [...Array(24)].map(_ => n++)
n = 0
let min = [...Array(60)].map(_ => n++)

export default {
  components: {
    headtop,
    usersList,
    Checker,
    CheckerItem,
    Checklist,
    Group,
    Picker,
    PopupPicker
  },
  data() {
    return {
      users: [],
      title: this.$t('maincelltitle3'),
      message: this.$t('selfTestDesc'),
      slWeekday: [],
      weekday: [
        { value: 1, text: 'SUN' },
        { value: 2, text: 'MON' },
        { value: 3, text: 'TUE' },
        { value: 4, text: 'WED' },
        { value: 5, text: 'THU' },
        { value: 6, text: 'FRI' },
        { value: 7, text: 'SAT' }
      ],
      slTime: ['0', '0'],
      showTime: false,
      time: [hour, min],
      msgCodeList: [
        { key: '1', value: '未上保险', inlineDesc: '' },
        { key: '2', value: '未反锁', inlineDesc: '' }
      ],
      slMsgCode: []
    }
  },
  computed: {},
  mounted: function() {
    var vm = this
    plus.tools.getSmsPhone({ messageCode: '0103101' }, function(result) {
      console.log(result)
      if (result.resultCode == 0 && result.data.audience != undefined) {
        vm.users = result.data.audience
      }
    })

    if (this.$store.getters.devType == 'Bg' || this.$store.getters.devType == 'Bq' || this.$store.getters.devType == 'Bn') {
      this.msgCodeList.splice(0, 1)
    }

    var cmd = {
      cmd: '501',
      appID: this.$store.state.appID,
      gwID: this.$store.getters.gwID,
      devID: this.$store.getters.deviceID,
      endpointNumber: 1,
      clusterId: 0x0101,
      commandType: 1,
      commandId: 0x8032
    }
    plus.gatewayCmd.controlDevice(cmd, result => {
      vm.$vux.loading.show({ text: null, position: 'center' })
      setTimeout(() => {
        vm.$vux.loading.hide()
      }, 3000)
    })
    plus.gatewayCmd.newDataRefresh(result => {
      if (result.cmd == '500' && result.devID == vm.$store.getters.deviceID) {
        if (
          result.endpoints[0].clusters[0].attributes[0].attributeId == 0x8009
        ) {
          var value =
            result.endpoints[0].clusters[0].attributes[0].attributeValue
          vm.$vux.loading.hide()
          if (value.length > 0) {
            let valueArr = value.split(' ')

            var weekStr = valueArr[4]
            var MM = valueArr[0]
            if (MM.indexOf('0') == 0) {
              MM = MM.substr(1, 1)
            }
            var HH = valueArr[1]
            if (HH.indexOf('0') == 0) {
              HH = HH.substr(1, 1)
            }
            vm.slTime = [HH, MM]
            var week = weekStr.split(',').map(n => parseInt(n))
            var errIndex = -1
            for (let index = 0; index < week.length; index++) {
              const element = week[index]
              if (isNaN(element)) {
                errIndex = index
              }
            }
            if (errIndex != -1) {
              week.splice(errIndex, 1)
            }
            vm.slWeekday = week

            if (valueArr.length == 6) {
              let msgCodeStr = valueArr[5]
              if (msgCodeStr == '1') {
                vm.slMsgCode = ['1']
              } else if (msgCodeStr == '2') {
                vm.slMsgCode = ['2']
              } else if (msgCodeStr == '3') {
                vm.slMsgCode = ['1', '2']
                if (vm.$store.getters.devType == 'Bg' || vm.$store.getters.devType == 'Bq' || vm.$store.getters.devType == 'Bn') {
                  vm.slMsgCode = ['2']
                }
              } else {
                vm.slMsgCode = []
              }
            } else {
              vm.slMsgCode = ['1', '2']
              if (vm.$store.getters.devType == 'Bg' || vm.$store.getters.devType == 'Bq' || vm.$store.getters.devType == 'Bn') {
                vm.slMsgCode = ['2']
              }
            }
          } else {
            vm.slTime = ['0', '0']
            vm.slWeekday = []
            vm.slMsgCode = []
          }
        }
      }
    })
  },

  methods: {
    saveAction() {
      var vm = this

      let slMsgCodeList = []
      slMsgCodeList.push(...this.slMsgCode)
      let messageCodeStr =
        this.num2string(this.slTime[1], 2) +
        ' ' +
        this.num2string(this.slTime[0], 2) +
        ' ? * ' +
        this.slWeekday.join(',')
      if (this.slMsgCode.length == 2) {
        messageCodeStr = messageCodeStr + ' 3'
      } else if (this.slMsgCode.length == 1) {
        messageCodeStr = messageCodeStr + ' ' + this.slMsgCode[0]
      } else if (this.slMsgCode.length == 0) {
        messageCodeStr = ''
      }
      if (this.slWeekday.length == 0) {
        messageCodeStr = ''
      }

      var cmd = {
        cmd: '501',
        appID: this.$store.state.appID,
        gwID: this.$store.getters.gwID,
        devID: this.$store.getters.deviceID,
        endpointNumber: 1,
        clusterId: 0x0101,
        commandType: 1,
        commandId: 0x8031,
        parameter: [messageCodeStr]
      }
      plus.gatewayCmd.controlDevice(cmd, result => {
        // console.log(result);
      })

      plus.tools.setSmsPhone(
        { messageCode: '0103101,0103111,0103121', phone: this.users.join(',') },
        function(result) {
          // console.log(result);
          vm.$router.go(-1)
        }
      )
    },
    backAction() {
      this.$router.go(-1)
    },
    num2string(num, n) {
      if (num == undefined) {
        num = 0
      }
      var len = num.toString().length
      while (len < n) {
        num = '0' + num
        len++
      }
      return num
    },
    selectTime() {
      this.showTime = true
    }
  }
}
</script>


<style lang="less" scoped>
.message {
  width: 90%;
  font-size: 18px;
  display: block;
  margin: 20px auto 0 auto;
}
.time {
  width: 100%;
  height: 44px;
  text-align: center;
  line-height: 44px;
}
.tip {
  width: 92%;
  font-size: 14px;
  color: #A1A1A1;
  margin: 14px auto 0px auto;
}
.weekday-box {
  width: 100%;
  height: 50px;
  background-color: #fff;
  border-top: 0.5px solid #eaeaea;
  border-bottom: 0.5px solid #eaeaea;
  margin-top: 2px;
  .weekday {
    display: flex;
    justify-content: space-around;
    align-items: center;
    width: 90%;
    height: 44px;
    margin: 3px auto 0 auto;
  }
}
.weekday-item {
  border: 1px solid #ccd1d8;
  width: 36px;
  height: 36px;
  padding: 0;
  margin: 0;
  text-align: center;
  line-height: 36px;
  border-radius: 50%;
  color: #ccd1d8;
  font-size: 12px;
}
.weekday-item-selected {
  color: #fff;
  background-color: #8cd451;
  border-color: #8cd451;
}
</style>

<style lang="less">
.weui-icon-checked.vux-checklist-icon-checked:before {
  color: #8cd451 !important;
}
.weui-cell:before {
  left: 0 !important;
}
</style>
