import Vue from 'vue'
import Vuex from 'vuex'

import user from './module/user'
import app from './module/app'
import wms_in from './module/in'
import wms_out from './module/out'
import qc from './module/qc'
//import { stat } from 'fs'
import wms_return from './module/return'

import {getInventoryResult,getLabelInfoByNo,saveResult} from '@/api/data'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userName: '',
    userWerks: '',
    userWhNumber: '',
    result:'',
    pageStoreData: {},           //存储当前页面数据
  },
  mutations: {
    setUserName (state, userName) {
      state.userName = userName
    },
    setUserWerks(state, userWerks){
      state.userWerks = userWerks;
    },
    setUserWhNumber(state, userWhNumber){
      state.userWhNumber = userWhNumber;
    },
    setPageStoreData (state, pageStoreData) {state.pageStoreData = pageStoreData},
    setResult (state, result) {state.result = result},
  },
  actions: {
    getInventoryResult({ commit }, { WERKS,WH_NUMBER,INVENTORY_NO,TYPE }) {
      return new Promise((resolve, reject) => {
        try {
          getInventoryResult(WERKS,WH_NUMBER,INVENTORY_NO,TYPE).then(res => {
            if(0 != res.data.code){
              commit('setResult', 'getInventoryResult ERROR')
            }
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'getInventoryResult ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'getInventoryResult ERROR')
          reject(error)
        }
      })
    },
    getLabelInfoByNo({ commit }, { LABEL_NO }) {
      return new Promise((resolve, reject) => {
        try {
          getLabelInfoByNo(LABEL_NO).then(res => {
            resolve(res)
          }).catch(err => {
            commit('setResult', 'getLabelInfo ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'getLabelInfo ERROR')
          reject(error)
        }
      })
    },
    saveResult({ commit }, { SAVE_DATA,TYPE,INVENTORY_NO }) {
      return new Promise((resolve, reject) => {
        try {
          saveResult(SAVE_DATA,TYPE,INVENTORY_NO).then(res => {
            resolve(res)
          }).catch(err => {
            commit('setResult', 'saveResult ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'saveResult ERROR')
          reject(error)
        }
      })
    }
  },
  modules: {
    user,
    app,
    wms_in,
    wms_out,
    qc,
    wms_return,
    tabbar : {
      strict: true,
      namespaced: true,
       state : {
         index : 0
       },
      mutations : {
         set : function (state,index) {
            state.index = index
         }
      },
      actions: {
        
      }
    }
  }
})
