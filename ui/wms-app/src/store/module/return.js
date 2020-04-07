import {
    getReturnInfoByBarcode,createReceiveRoomOutReturn,confirmReceiveRoomOutReturn,getSapPoInfo,getSapPoBarcodeInfo,
    getWareHouseOutData29,wareHouseOutReturnConfirm
  } from '@/api/return'

export default {
  state: {
    page: '',
    barCode: '',        //存储当前页面扫描的条码
    pageData: {},       //存储当前页面数据
    checkDataList: [],  //选中数据
    result: ''
  },
  mutations: {
    setPage (state, page) {state.page = page},
    setBarCode (state, barCode) {state.barCode = barCode},
    setPageData (state, pageData) {state.pageData = pageData},
    setCheckDataList (state, list) {state.checkDataList = list},
    setResult (state, result) {state.result = result},
    resetState(state){
      state.page = ''
      state.barCode = ''
      state.pageData = {}
      state.result = ''
    }
  },
  actions: {
    getReturnInfoByBarcode ({ commit },{ barCode }) {
      return new Promise((resolve, reject) => {
        try {
          getReturnInfoByBarcode(barCode).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'listSKInfo ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'listSKInfo ERROR')
          reject(error)
        }
      })
    },
    createReceiveRoomOutReturn({ commit },{ WERKS,WH_NUMBER,BUSINESS_NAME,BUSINESS_CODE,USERNAME,ARRLIST }) {
      return new Promise((resolve, reject) => {
        try {
          createReceiveRoomOutReturn(WERKS,WH_NUMBER,BUSINESS_NAME,BUSINESS_CODE,USERNAME,ARRLIST).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'createReceiveRoomOutReturn ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'createReceiveRoomOutReturn ERROR')
          reject(error)
        }
      })
    },
    confirmReceiveRoomOutReturn({ commit },{ WERKS,RETURN_NO,PZ_DATE,JZ_DATE,USERNAME,ARRLIST }) {
      return new Promise((resolve, reject) => {
        try {
          confirmReceiveRoomOutReturn(WERKS,RETURN_NO,PZ_DATE,JZ_DATE,USERNAME,ARRLIST).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'confirmReceiveRoomOutReturn ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'confirmReceiveRoomOutReturn ERROR')
          reject(error)
        }
      })
    },
    getSapPoInfo ({ commit },{ PO_NO }) {
      return new Promise((resolve, reject) => {
        try {
          getSapPoInfo(PO_NO).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'getSapPoInfo ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'getSapPoInfo ERROR')
          reject(error)
        }
      })
    },
    getWareHouseOutData29 ({ commit },{ WERKS,WH_NUMBER,PONO }) {
      return new Promise((resolve, reject) => {
        try {
          getWareHouseOutData29(WERKS,WH_NUMBER,PONO).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'getWareHouseOutData29 ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'getWareHouseOutData29 ERROR')
          reject(error)
        }
      })
    },
    wareHouseOutReturnConfirm ({ commit },{ WERKS,WH_NUMBER,BUSINESS_TYPE,BUSINESS_NAME,ARRLIST,PZ_DATE,JZ_DATE }) {
      return new Promise((resolve, reject) => {
        try {
          wareHouseOutReturnConfirm(WERKS,WH_NUMBER,BUSINESS_TYPE,BUSINESS_NAME,ARRLIST,PZ_DATE,JZ_DATE).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'WareHouseOutReturnConfirm ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'WareHouseOutReturnConfirm ERROR')
          reject(error)
        }
      })
    },
    getSapPoBarcodeInfo ({ commit },{ PO_NO,Barcode }) {
      return new Promise((resolve, reject) => {
        try {
          getSapPoBarcodeInfo(PO_NO,Barcode).then(res => {
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'getSapPoBarcodeInfo ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'getSapPoBarcodeInfo ERROR')
          reject(error)
        }
      })
    },
  }
}