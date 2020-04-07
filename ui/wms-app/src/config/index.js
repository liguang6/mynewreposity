
const path = require('path')
function resolve (dir) {
  return path.join(__dirname, '..', dir)
}
export default {

  /**
   * @description 配置显示在浏览器标签的title
   */
  title: 'WMS',
  /**
   * @description token在Cookie中存储的天数，默认1天
   */
  cookieExpires: 1,
  /**
   * @description 是否使用国际化，默认为false
   *              如果不使用，则需要在路由中给需要在菜单中展示的路由设置meta: {title: 'xxx'}
   *              用来在菜单中显示文字
   */
  useI18n: true,
  /**
   * @description api请求基础路径
   */
  tokenName:{
    dev: 'tokenDev',
    pro: 'tokenPro',
  },
  baseUrl: {

    dev: 'http://localhost:9090/admin-service',

    pro: 'http://10.23.20.31:9090/admin-service',

    wms_dev: 'http://localhost:8889/wms-service',

    wms_pro: 'http://10.23.20.31:8889/wms-service'
  },
    /**
   * @description 登录页面路由值
   */
  LOGIN_PAGE_NAME: 'login',
  /**
   * @description 默认打开的首页的路由name值，默认为home
   */
  homeName: 'wms-app',
  homePage: 'home',
  /**
   * @description 需要加载的插件
   */
  plugin: {
    'error-store': {
      showInHeader: true, // 设为false后不会在顶部显示错误日志徽标
      developmentOff: true // 设为true后在开发环境不会收集错误信息，方便开发中排查错误
    }
  },
  resolve : {
    extensions: ['vue','js'],
    alias : {
      '_c':resolve('src/component')
    }
  }
}
