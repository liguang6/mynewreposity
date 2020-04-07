import {getQcResultDict,getQcReasonList,getQcResultConfig} from '@/api/qc'

export default {
    state : {
        qc_dict : [],
        qc_reason_list : [],
        qc_result_config : [],
        werks : 'C161',
        wh_number : 'C161'
    },
    mutations : {
        qc_dict(state,val){
            state.qc_dict = val
        },
        qc_reason_list(state,newVal){
            state.qc_reason_list = newVal
        },
        qc_result_config(state,v){
            state.qc_result_config = v
        }
    }
    ,
    actions : {
        getQcDict({commit,state}){
            if(state.qc_dict.length == 0) {
                getQcResultDict().then(d=>{
                    return d.data
                }).then(d=>{
                    commit('qc_dict',d.data)
                })
            }
        },
        getQcReasonList({commit,state}){
            if(state.qc_reason_list.length == 0){
                getQcReasonList().then(d=>d.data).then(d=>{
                    commit('qc_reason_list',d.page.list)
                })
            }
        },
        getQcResultConfig({commit,state}) {
            if(state.qc_result_config.length != 0)
                return state.qc_result_config

            getQcResultConfig(state.werks).then(d=> {
                return d.data
            }).then( d=> {
                commit('qc_result_config',d.page.list)
                return d.page.list;
            })
        }
    }
}
