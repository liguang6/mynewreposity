/**
 * 
 * 配置路由
 * 
 */

export default [
  {
    path: '/login',
    name: 'login',
    meta: {
      title: 'Login - 登录',
      hideInMenu: true
    },
    component: () => import('@/view/LoginForm.vue')
  },
  {
    path: '/wms-app',
    name: 'home',
    meta: {
      title: 'WMS - 首页',
      hideInMenu: true
    },
    component: () => import('@/view/home.vue')
  },
  {
    path: '/setting',
    name: 'setting',
    meta: {
      title: 'WMS - 设置',
      hideInMenu: true
    },
    component: () => import('_c/settingsPage.vue')
  }
]
