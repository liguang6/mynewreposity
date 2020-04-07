/* eslint-disable no-console */
import {
  queryBarcodeOnly,listSaveByPdaBarcode,listByPdaBarcode,listScmMat,getScmBarCodeInfo,getMatBarcodeList,getAllMatBarcodeList,
  listSKInfo,boundIn,queryBarcode,ubTransferInit,ubTransferBarcodeDesc,getLabelInfo,list303Mat,listCloudMat,
  listReqMat,listReqMatIns,queryBarcodeFlag,queryWhBinSEQ
  ,getShelfBatcodeInfo,getBusinessList,
  queryByBarcode,validateBinCode,commitByHwyd
  } from '@/api/in'

  export default {
    state: {
      page: '',
      assno: '',
      barCode: '',        //存储当前页面扫描的条码
      pageData: {},       //存储当前页面数据
      metCheckId: '',     //物料点检ID
      pageDataList: [],   //查询数据列表
      checkDataList: [],  //选中数据
      result: ''
    },
    mutations: {
      setPage (state, page) {state.page = page},
      setAssno (state, assno) {state.assno = assno},
      setBarCode (state, barCode) {state.barCode = barCode},
      setPageData (state, pageData) {state.pageData = pageData},
      setMetCheckId (state, metCheckId) {state.metCheckId = metCheckId},
      setPageDataList (state, list) {state.pageDataList = list},
      setCheckDataList (state, list) {state.checkDataList = list},
      setResult (state, result) {state.result = result},
      resetState(state){
        state.page = ''
        state.assno = ''
        state.barCode = ''
        state.pageData = {}
        state.pageDataList = []
        state.checkDataList = []
        state.result = ''
      }
    },
    actions: {
      getBusinessList({ commit }, { werks,businessClass }) {
        return new Promise((resolve, reject) => {
          try {
            getBusinessList(werks,businessClass)
              .then(res => {
                if (0 === res.data.code) {                
                } else {                 
                }
                const data = res;
                resolve(data);
              })
              .catch(err => {                
                reject(err);
              });
          } catch (error) {           
            reject(error);
          }
        });
      },
      listSaveByPdaBarcode ({ commit },{ list }) {
        return new Promise((resolve, reject) => {
          try {
            listSaveByPdaBarcode(list).then(res => {
              if(0 === res.data.code){

              }else{

              }
              const data = res
              resolve(data)
            }).catch(err => {

              reject(err)
            })
          } catch (error) {

            reject(error)
          }
        })
      },
      commitByHwyd ({ commit },{ list }) {

        return new Promise((resolve, reject) => {
          try {
            commitByHwyd(list).then(res => {
              if(0 === res.data.code){

              }else{

              }
              const data = res
              resolve(data)
            }).catch(err => {

              reject(err)
            })
          } catch (error) {

            reject(error)
          }
        })
      },
      validateBinCode({ commit },{ barcode }){
        return new Promise((resolve, reject) => {
          try {
            validateBinCode(barcode).then(res => {
              if(0 === res.data.code){
                commit('setPageDataList', res.data)
              }else{
                commit('setPageDataList', [])
              }
              const data = res
              resolve(data)
            }).catch(err => {
              commit('setPageDataList', [])
              reject(err)
            })
          } catch (error) {
            commit('setPageDataList', [])
            reject(error)
          }
        })
      },
      queryByBarcode({ commit },{ barcode }){
        return new Promise((resolve, reject) => {
          try {
            queryByBarcode(barcode).then(res => {
              if(0 === res.data.code){
                commit('setPageDataList', res.data)
              }else{
                commit('setPageDataList', [])
              }
              const data = res
              resolve(data)
            }).catch(err => {
              commit('setPageDataList', [])
              reject(err)
            })
          } catch (error) {
            commit('setPageDataList', [])
            reject(error)
          }
        })
      },
      queryBarcodeOnly({ commit },{ barcode }){
        return new Promise((resolve, reject) => {
          try {
            queryBarcodeOnly(barcode).then(res => {
              if(0 === res.data.code){
                commit('setPageDataList', res.data)
              }else{
                commit('setPageDataList', [])
              }
              const data = res
              resolve(data)
            }).catch(err => {
              commit('setPageDataList', [])
              reject(err)
            })
          } catch (error) {
            commit('setPageDataList', [])
            reject(error)
          }
        })
      },
      listByPdaBarcode ({ commit },{ barcode }) {
        return new Promise((resolve, reject) => {
          try {
            listByPdaBarcode(barcode).then(res => {
              if(0 === res.data.code){
                commit('setPageDataList', res.data)
              }else{
                commit('setPageDataList', [])
              }
              const data = res
              resolve(data)
            }).catch(err => {
              commit('setPageDataList', [])
              reject(err)
            })
          } catch (error) {
            commit('setPageDataList', [])
            reject(error)
          }
        })
      },
        listScmMat ({ commit },{ assno }) {
          console.log('-->module listScmMat listScmMat assno : ' + assno)
          return new Promise((resolve, reject) => {
            try {
              listScmMat(assno).then(res => {
                if(0 === res.data.code){
                  commit('setPageDataList', res.data.page.list)
                }else{
                  commit('setPageDataList', [])
                }
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setPageDataList', [])
                reject(err)
              })
            } catch (error) {
              commit('setPageDataList', [])
              reject(error)
            }
          })
        },
        getLabelInfo ({ commit },{ LabelNo,LGORT,BIN_CODE }) {
          return new Promise((resolve, reject) => {
            try {
              getLabelInfo(LabelNo,LGORT,BIN_CODE).then(res => {
                const data = res
                resolve(data)
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
        list303Mat ({ commit },{ BUSINESS_NAME,SAP_MATDOC_NO,WH_NUMBER,WERKS,PZ_YEAR }) {
          return new Promise((resolve, reject) => {
            try {
              list303Mat(BUSINESS_NAME,SAP_MATDOC_NO,WH_NUMBER,WERKS,PZ_YEAR).then(res => {
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setResult', 'list303Mat ERROR')
                reject(err)
              })
            } catch (error) {
              commit('setResult', 'list303Mat ERROR')
              reject(error)
            }
          })
        },
        getScmBarCodeInfo ({ commit },{ BARCODE }) {
          return new Promise((resolve, reject) => {
            try {
              getScmBarCodeInfo(BARCODE).then(res => {
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setResult', 'getScmBarCodeInfo ERROR')
                reject(err)
              })
            } catch (error) {
              commit('setResult', 'getScmBarCodeInfo ERROR')
              reject(error)
            }
          })
        },
        getAllMatBarcodeList ({ commit },{ ASS_NO }) {
          return new Promise((resolve, reject) => {
            try {
              getAllMatBarcodeList(ASS_NO).then(res => {
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setResult', 'getAllMatBarcodeList ERROR')
                reject(err)
              })
            } catch (error) {
              commit('setResult', 'getAllMatBarcodeList ERROR')
              reject(error)
            }
          })
        },
        getMatBarcodeList ({ commit },{ ASS_NO,MATNR,PO_NO,ROWITEM }) {
          return new Promise((resolve, reject) => {
            try {
              getMatBarcodeList(ASS_NO,MATNR,PO_NO,ROWITEM).then(res => {
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setResult', 'getMatBarcodeList ERROR')
                reject(err)
              })
            } catch (error) {
              commit('setResult', 'getMatBarcodeList ERROR')
              reject(error)
            }
          })
        },
        listReqMat ({ commit },{ barcode , werks }) {
          return new Promise((resolve, reject) => {
            try {
              listReqMat(barcode,werks).then(res => {
                if(0 === res.data.code){
                  commit('setPageDataList', res.data)
                }else{
                  commit('setPageDataList', [])
                }
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setPageDataList', [])
                reject(err)
              })
            } catch (error) {
              commit('setPageDataList', [])
              reject(error)
            }
          })
        },
        listReqMatIns ({ commit },{ list }) {

          return new Promise((resolve, reject) => {
            try {
              listReqMatIns(list).then(res => {
                if(0 === res.data.code){

                }else{

                }
                const data = res
                resolve(data)
              }).catch(err => {

                reject(err)
              })
            } catch (error) {

              reject(error)
            }
          })
        },

        listSKInfo ({ commit },{ assno ,BUSINESS_NAME}) {
          return new Promise((resolve, reject) => {
            let pageData = {}
            try {
              listSKInfo(assno,BUSINESS_NAME).then(res => {
                if(0 === res.data.code){
                  pageData = {'skList': res.data}
                  commit('setPageData', pageData)
                }else{
                  commit('setPageData', {})
                }
                const data = res
                resolve(data)
              }).catch(err => {
                commit('setPageData', {})
                reject(err)
              })
            } catch (error) {
              commit('setPageData', {})
              reject(error)
            }
          })
        },
        listCloudMat ({ commit },{ assno ,BUSINESS_NAME}) {
          return new Promise((resolve, reject) => {
            try {
              listCloudMat(assno,BUSINESS_NAME).then(res => {
                if(0 === res.data.code){
                  commit('setResult', 'SUCCESS')
                }else{
                  commit('setResult', 'listCloudMat ERROR')
                }
                resolve(res)
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
        boundIn ({ commit },{ matList,allDataList,skList,WERKS,BUSINESS_NAME,ASNNO,PZ_DATE,JZ_DATE }) {
          return new Promise((resolve, reject) => {
            try {
              boundIn(matList,allDataList,skList,WERKS,BUSINESS_NAME,ASNNO,PZ_DATE,JZ_DATE).then(res => {
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
        }
    },
    //入库功能的子模块store定义
    modules : {
      //入库上架
      shelf : {
        strict: true,
        namespaced: true,
        state : {
          barcode : '',//条码
          postVehicleID : '',//物流载具ID
          storeArea : '',//存储区
          initTaskCols:{batch:'物料号批次',vendor:'供应商',vendorName:'供应商名称',qty:'数量'},
          initTaskTabs:[],
          initTaskDatatableBatch:null,//条码明细，批次号
          inboundTaskNo:"",//上架任务单号
          whTaskList:[],//上架任务单列表
          positionTag:'',//定位标签
          displayNo:false,//显示理序号
          hasShelfTasks:[],//已上架任务列表
          displayWhTaskListType:'0',//上架清单 0-全部 1-已上架 2-未上架



          scanner_label_list:[],//扫描标签入库 - 标签列表
          scanner_barcode:'',//扫描标签入库 - 条码
          scanner_orderItemEnable:[],//扫描标签入库 - 是否对进仓单生效
          scanner_bin_code:'',//扫描标签入库 - 储位

          matList:[],
          ub_barcode : '',
          ub_werks : '',
          ub_bin_code : '',//ub 储位
          ub_lgort :"",//ub 库位
          ub_label_list:[],//ub转储上架的标签列表
          ub_in_inbound_no:{},//ub 数据表选择的进仓单

          shelfType:'',//上架任务的类型，扫条码/上架任务单
          scannerBarcode: false,//是否扫条码
          barcodeFlag:null,//条码管理标识 true:开启了条码管理 false:没有开启条码管理
        },
        mutations : {
          ub_lgort(state,value){
            state.ub_lgort = value;
          },
          ub_in_inbound_no(state,value){
            state.ub_in_inbound_no = value;
          },
          ub_label_list(state,value){
            state.ub_label_list = value;
          },
          scanner_bin_code(state,value){
            state.scanner_bin_code = value;
          },
          scanner_orderItemEnable(state,value){
            state.scanner_orderItemEnable = value;
          },
          scanner_barcode(state,value){
            state.scanner_barcode = value;
          },
          scanner_label_list(state,value){
            state.scanner_label_list = value;
          },
          displayWhTaskListType(state,value){
            state.displayWhTaskListType = value;
          },
          hasShelfTasks(state,value){
            state.hasShelfTasks = value;
          },
          displayNo(state,value){
            state.displayNo = value;
          },
          positionTag(state,value){
             state.positionTag = value;
          },
          whTaskList(state,value){
            state.whTaskList = value;
          },
          inboundTaskNo(state,value){
            state.inboundTaskNo = value;
          },
          initTaskDatatableBatch(state,val){
            state.initTaskDatatableBatch = val;
          },
          setInitTaskTabs(state,newval){
            state.initTaskTabs = newval;
          },
          setBarcodeFlag(state,barcodeFlag){
            state.barcodeFlag = barcodeFlag;
          },
          setBarCode(state, barCode) {
            state.barcode = barCode
          },
          setPostVehicleID(state,id){
            state.postVehicleID = id
          },
          setStoreArea(state,storeArea){
            state.storeArea = storeArea
          },
          setMat(state,data){
            state.matList = data
          },
          ub_barcode(state,val){
            state.ub_barcode = val
          },
          ub_werks(state,val){
            state.ub_werks = val
          },
          ub_bin_code(state,val){
            state.ub_bin_code = val
          },
          shelfType(state,val){
            state.shelfType = val
          },
          scannerBarcode(state,val){
            state.scannerBarcode = val
          }
        },
        actions : {

          scannerbarcode({commit},barcode){
            return  queryBarcode(barcode).then((resp)=>{
               return resp.data
            })
          },
          ubTransferQuery({commit},data){
            return ubTransferInit(data).then(resp=>resp.data)
          },
          queryUbTransferBarcodeDesc({commit},data){
            return ubTransferBarcodeDesc(data).then(resp=>resp.data)
          },
          setBarcodeFlag({commit,state},payload){
            //只查一次
            if(state.barcodeFlag != null)
              return;
            //设置条码管理标识
            queryBarcodeFlag({"WERKS":payload.WERKS,"WH_NUMBER":payload.WH_NUMBER})
                .then(d=>d.data)
                .then(d=>{
                  if(d.code == '0'){
                    let whConfigList = d.data;
                    let barcode = whConfigList[0].BARCODE_FLAG === 'X' ? true : false;
                    commit("setBarcodeFlag",barcode)
                  }
                });
          },
          //根据库存的顺序配置，对仓库任务排序
          //如果存在定位标签重新排序
          orderInTaskList({commit,state},whNumber) {
            //根据定位标签排序
           return  queryWhBinSEQ({"WH_NUMBER":whNumber,"SEQ_TYPE":"00"}).then(d=>{
              let resp = d.data;
              if(resp.code == '0' && resp.data.length > 0){
                let data = resp.data;
                //标记储位排序号
                for(let task of state.whTaskList){
                  for(let SEQ of data){
                    if(SEQ.BIN_CODE == task.TO_BIN_CODE){
                      task.SEQNO = SEQ.SEQNO;
                    }
                  }
                }

                //根据储位排序号重新排序
                //TIP sort 方法更新的对象 能被vue响应
                state.whTaskList.sort(function(a,b){
                  return parseInt(a.SEQNO) - parseInt(b.SEQNO);
                });

                if(this.positionTag !== ''){
                  //根据定位标签重新排序
                  //把定位标签之后的数据移到最前
                  let index = 0;
                  for(let i = 0;i<state.whTaskList.length;i++){
                    if(state.whTaskList[i].TO_BIN_CODE == state.positionTag){
                      index = i;
                      break;
                    }
                  }
                  if(index !== 0){
                    let first =  state.whTaskList.slice(0,index);
                    let second = state.whTaskList.slice(index);
                    let newTaskList = second.concat(first);
                    commit("whTaskList",newTaskList);
                  }
                }
              }
            });
          }
        }
      }
    }
  }
