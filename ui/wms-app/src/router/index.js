import Vue from 'vue'
import Router from 'vue-router'
import routes from './routers'
import store from '@/store'
import { setToken, getToken, canTurnTo, setTitle } from '@/libs/util'
import config from '@/config'
const { homeName,LOGIN_PAGE_NAME } = config

Vue.use(Router)
const router = new Router({
  routes,
  mode: 'history'
})

const turnTo = (to, access, next) => {
  if (canTurnTo(to.name, access, routes)) next() // 有权限，可访问
  else next({ replace: true, name: 'error_401' }) // 无权限，重定向到401页面
}

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (!token && to.name !== LOGIN_PAGE_NAME) {
    // 未登录且要跳转的页面不是登录页
    next({
      name: LOGIN_PAGE_NAME // 跳转到登录页
    })
  } else if (!token && to.name === LOGIN_PAGE_NAME) {
    // 未登陆且要跳转的页面是登录页
    next() // 跳转
  } else if (token && to.name === LOGIN_PAGE_NAME) {
    // 已登录且要跳转的页面是登录页
    next({
      name: homeName // 跳转到homeName页
    })
  } else {
    if(token){
      turnTo(to, store.state.user.access, next)
    } else {
        setToken('')
        next({
          name: 'login'
        })
    }
  }
})

router.afterEach(to => {
  setTitle(to, router.app)
  window.scrollTo(0, 0)
})

export default router
