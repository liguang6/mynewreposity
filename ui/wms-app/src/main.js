import Vue from 'vue'
import VeeValidate from 'vee-validate';
import App from './App.vue'
import router from './router'
import store from './store'
// Webpack CSS import
import 'onsenui/css/onsenui.css';
import 'onsenui/css/onsen-css-components.css';
// JS import
import VueOnsen from 'vue-onsenui';

// This already imports 'onsenui'
import AutoFocus from 'vue-auto-focus'
// import VueOnsen from 'vue-onsenui/esm';
// import VOnsPage from 'vue-onsenui/esm/components/VOnsPage';
// import VOnsToolbar from 'vue-onsenui/esm/components/VOnsToolbar';
// Vue.component(VOnsToolbar.name, VOnsToolbar);
VueOnsen.platform.select('ios')
Vue.use(VueOnsen);
Vue.use(AutoFocus);

import i18n from '@/locale'
import config from '@/config'

import tools from '@/libs/tools.js'
Vue.prototype.tools=tools

if(process.env.NODE_ENV !== 'production') require('@/mock') //不是生产环境，导入src/mock下的内容

// 引用axios，并设置基础URL为后端服务api地址
// var axios = require('axios')
// axios.defaults.baseURL = 'http://10.20.10.2:8889'

//自定义验证规则 v-validate="'required|between:10,20'"
const isBetween = (value, { min, max } = {}) => {
  return Number(min) <= value && Number(max) >= value;
};
const paramNames = ['min', 'max'];
VeeValidate.Validator.extend('between', isBetween, {
  paramNames
});

//验证提示信息国际化
var dict = {
  zh_CN: {
   messages: {
      required: function(field){
        return field + '不能为空！';
      },
      between: function(field){
        return field + '输入不符合设定规则！';
      },
       min : function (field,leng) {
           return field + '长度不能小于'+leng+'位';
       }
    }
  }
 };
VeeValidate.Validator.localize('zh_CN', dict.zh_CN);

Vue.use(VeeValidate);
// Vue.component(VOnsPage.name, VOnsPage);
// Vue.component(VOnsToolbar.name, VOnsToolbar);

// 注册一个全局自定义指令 `v-focus`
Vue.directive('focus', {
  inserted: function (el, obj) {
    if (obj.value === true) {
      el.focus()
    }
  },
  // 当指令所在组件的 VNode 及其子 VNode 全部更新后调用
  componentUpdated: function (el, obj) {
    if (obj.value === true) {
      el.focus()
    }
  }
})

/**
 * @description 生产环境关掉提示
 */
Vue.config.productionTip = false

/**
 * @description 全局注册应用配置
 */
Vue.prototype.$config = config

new Vue({
  router,
  i18n,
  store,
  render: h => h(App),
}).$mount('#app')
