/* eslint-disable no-console */
import {
  queryOutReqPda311,
  createOutReqPda311,
  queryUNIT,
  recommend,
  saveWHTask,
  selectCoreWHTask,
  scanLabel,
  pdaPicking,
  querySeqNo,
  handoverList,
  handoverSave,
  queryMatnr,
  inventoryList,
  stepLinkageHandover,
  JITScanLabel,
  JITScanDeliveryNo,
  JITPick,
  DispatchingConfirmList,
  DispatchingConfirm,
  DispatchingConfirmUpdata,
  DispatchingHandoverList,
  dispatchingHandover,
  getLabel
} from "@/api/out";

export default {
  state: {
    pageDataList: [], //查询数据列表
    checkDataList: [],  //选中数据
    taskList: [], //查询任务列表
    result: "",
    scannerBarcode: false, //是否扫条码
    requirementNo: "",
    flag: true,
    dataTable: [],
    pageCache: [],
    inwerks1: "",
    deliveryNo: "",
    pageData: {},       //存储当前页面数据
    sortNo:'',
    totalQty:0
  },
  mutations: {
    setPageDataList(state, list) {
      state.pageDataList = list;
    },
    setTaskList(state, list) {
      state.taskList = list;
    },
    setResult(state, result) {
      state.result = result;
    },
    scannerBarcode(state, val) {
      state.scannerBarcode = val;
    },
    setRequirementNo(state, val) {
      state.requirementNo = val;
    },
    setFlag(state, flag) {
      state.flag = flag;
    },
    setDataTable(state, val) {
      state.dataTable = val;
    },
    setPageCache(state, val) {
      state.pageCache = val;
    },
    setInwerks(state, val) {
      state.inwerks1 = val;
    },
    setDeliveryNo(state, val) {
      state.deliveryNo = val;
    },
    setCheckDataList (state, val) {
      state.checkDataList = val
    },
    setPageData (state, val) {
      state.pageData = val
    },
    setSortNo (state, val) {
      state.sortNo = val
    },
    setTotalQty (state, val) {
      state.totalQty = val
    },
    resetState(state) {
      state.pageDataList = [];
      state.result = "";
      state.scannerBarcode = false;
      state.checkDataList = [];
      state.pageData = {};
    }
  },
  actions: {
    DispatchingConfirmList ({ commit },{ barCode }) {
      return new Promise((resolve, reject) => {
        try {
          DispatchingConfirmList(barCode).then(res => {
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
    DispatchingConfirm({ commit }, { list }) {
      return new Promise((resolve, reject) => {
        try {
          
          DispatchingConfirm(list)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },

    DispatchingConfirmUpdata({ commit }, { list }) {
      return new Promise((resolve, reject) => {
        try {
          
          DispatchingConfirmUpdata(list)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },

    DispatchingHandoverList ({ commit },{ barCode }) {
      return new Promise((resolve, reject) => {
        try {
          DispatchingHandoverList(barCode).then(res => {
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

    dispatchingHandover ({ commit },{ ARRLIST,PZDDT,JZDDT }) {
      return new Promise((resolve, reject) => {
        try {
          dispatchingHandover(ARRLIST,PZDDT,JZDDT).then(res => {
            if(0 === res.data.code){
              commit('setResult', 'SUCCESS')
            }else{
              commit('setResult', '')
            }
            const data = res
            resolve(data)
          }).catch(err => {
            commit('setResult', 'ERROR')
            reject(err)
          })
        } catch (error) {
          commit('setResult', 'ERROR')
          reject(error)
        }
      })
    },

    queryOutReqPda311({ commit }, { barcode, werks }) {
      return new Promise((resolve, reject) => {
        try {
          queryOutReqPda311(barcode, werks)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    queryUNIT({ commit }, { data }) {
      return new Promise((resolve, reject) => {
        try {
          if(data.data[0]){
          queryUNIT(data.data[0].MATNR)
            .then(res => {
              if (res.data.data.length > 0&&res.data.data[0]) {
                data.data[0].UNIT = res.data.data[0].MEINS;
              } else {
              }
              //const data = res
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
          }else
          resolve(data);
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    createOutReqPda311({ commit }, { list }) {
      return new Promise((resolve, reject) => {
        try {
          createOutReqPda311(list)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    recommend({ commit }, { requirementNo, WERKS, WH_NUMBER }) {
      return new Promise((resolve, reject) => {
        try {
          recommend(requirementNo, WERKS, WH_NUMBER)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    saveWHTask({ commit }, { WERKS, WH_NUMBER, REQUIREMENT_NO, SAVE_DATA }) {
      return new Promise((resolve, reject) => {
        try {
          SAVE_DATA.forEach(ele => {
            ele.WH_NUMBER = WH_NUMBER;
            ele.FROM_STORAGE_AREA = ele.STORAGE_AREA_CODE;
            ele.FROM_BIN_CODE = ele.BIN_CODE;
            ele.TO_STORAGE_AREA = ele.STORAGE_AREA_CODE;
            ele.TO_BIN_CODE = "BBBB";
            ele.PROCESS_TYPE = "01";
            ele.QUANTITY = ele.RECOMMEND_QTY;
            ele.CONFIRM_QUANTITY = 0;
            ele.WT_STATUS = "00";
            ele.STOCK_TYPE = ele.SOBKZ;
            ele.REFERENCE_DELIVERY_NO = REQUIREMENT_NO;
            ele.REFERENCE_DELIVERY_ITEM = ele.REQUIREMENT_ITEM_NO;
          });
          saveWHTask(SAVE_DATA)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    pdaPicking({ commit }, { REQUIREMENT_NO,WERKS, WH_NUMBER, SAVE_DATA, addList, modifyList }) {
      return new Promise((resolve, reject) => {
        try {
          SAVE_DATA.forEach(ele => {
            //ele.WH_NUMBER = WH_NUMBER;
            //ele.FROM_STORAGE_AREA = ele.STORAGE_AREA_CODE;
            //ele.FROM_BIN_CODE = ele.BIN_CODE;
            //ele.TO_STORAGE_AREA = ele.STORAGE_AREA_CODE;
           // ele.TO_BIN_CODE = "BBBB";
           // ele.PROCESS_TYPE = "01";
           // ele.STOCK_TYPE = ele.SOBKZ;
           // ele.REFERENCE_DELIVERY_NO = ele.REQUIREMENT_NO;
           // ele.REFERENCE_DELIVERY_ITEM = ele.REQUIREMENT_ITEM_NO;
          })
          SAVE_DATA = JSON.stringify(SAVE_DATA);
          addList = JSON.stringify(addList);
          modifyList = JSON.stringify(modifyList);
          pdaPicking(REQUIREMENT_NO,WERKS, WH_NUMBER, SAVE_DATA, addList, modifyList)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    selectCoreWHTask({ commit }, { requirementNo, status }) {
      return new Promise((resolve, reject) => {
        try {
          selectCoreWHTask(requirementNo, status)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    scanLabel({ commit }, { labelNo, werks }) {
      return new Promise((resolve, reject) => {
        try {
          scanLabel(labelNo, werks)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    querySeqNo({ commit }, { binCode }) {
      return new Promise((resolve, reject) => {
        try {
          querySeqNo(binCode)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    handoverList({ commit }, { requirementNo, WERKS, WH_NUMBER }) {
      return new Promise((resolve, reject) => {
        try {
          handoverList(requirementNo, WERKS, WH_NUMBER)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    handoverSave(
      { commit },
      { PZDDT, JZDDT, WERKS, WH_NUMBER, ARRLIST, USERNAME }
    ) {
      return new Promise((resolve, reject) => {
        try {
          handoverSave(PZDDT, JZDDT, WERKS, WH_NUMBER, ARRLIST, USERNAME)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    queryMatnr({ commit }, { labelNo }) {
      return new Promise((resolve, reject) => {
        try {
          queryMatnr(labelNo)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    inventoryList({ commit }, { werks, matnr }) {
      return new Promise((resolve, reject) => {
        try {
          inventoryList(werks, matnr)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    stepLinkageHandover({ commit }, { list }) {
      return new Promise((resolve, reject) => {
        try {
          stepLinkageHandover(list)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    JITScanLabel({ commit }, { LABEL_NO,werks }) {
      return new Promise((resolve, reject) => {
        try {
          JITScanLabel(LABEL_NO,werks)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    JITScanDeliveryNo({ commit }, { DELIVERY_NO }) {
      return new Promise((resolve, reject) => {
        try {
          JITScanDeliveryNo(DELIVERY_NO)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    JITPick({ commit }, { SAVE_DATA }) {
      return new Promise((resolve, reject) => {
        try {
          //SAVE_DATA = JSON.stringify(SAVE_DATA);
          JITPick(SAVE_DATA)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    },
    getLabel({ commit }, { WERKS }) {
      return new Promise((resolve, reject) => {
        try {
          //SAVE_DATA = JSON.stringify(SAVE_DATA);
          getLabel(WERKS)
            .then(res => {
              if (0 === res.data.code) {
                commit("setResult", "SUCCESS");
              } else {
                commit("setResult", "");
              }
              const data = res;
              resolve(data);
            })
            .catch(err => {
              commit("setResult", "");
              reject(err);
            });
        } catch (error) {
          commit("setResult", "");
          reject(error);
        }
      });
    }
  }
};
