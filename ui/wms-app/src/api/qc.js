import wms_axios from '@/libs/wms.request'

export const wmsQcSingle = (qcObj) => {
    wms_axios.request({
        url: 'qc/qcSingle',
        data : qcObj,
        method : 'post'
    })
}

//获取质检的下拉选择项
export const getQcResultDict = () => wms_axios.request({
    url:"/qc/pda/qc_result_dict",
    method : "get",
    responseType : 'json'
})

//获取不良原因列表
export const getQcReasonList = () => wms_axios.request({
    url :'/config/wmscqcreturnreasons/list',
    method : 'get'
})

export const getQcResultConfig = (werks) => wms_axios.request({
    url :'/config/wmscqcresult/list2',
    method : 'post',
    data : {'werks':werks}
})

export const boxQcSave = (data) => wms_axios.request({
    url :'/qc/pda/box_qc_save',
    method : 'post',
    data : data
})

export const queryBatchInfo = (data) => wms_axios.request({
    url : 'qc/pda/batch_qc_query',
    method : 'post',
    data : data
})

export const qcBtachCommit = (data) => wms_axios.request({
    url : 'qc/pda/batch_qc_save',
    data : data,
    method : 'post'
})
//根据箱号查询质检改判信息
export const single_qc_change_query = (data) => wms_axios.request({
    url:'qc/pda/single_qc_change_query',
    data : data,
    method : 'post'
}).then(d => d.data)
//单箱改判提交API
export const single_qc_change_save = (data) => wms_axios.request({
    url:'qc/pda/single_qc_change_save',
    data : data,
    method : 'post'
}).then(d=>d.data)

//查询批次改判信息
export const  batch_qc_change_query = (data) => wms_axios.request({
    url : 'qc/pda/batch_qc_change_query',
    data : data,
    method :'post'
}).then(d=>d.data)

//保存整批改判结果
export const batch_qc_change_save = (data) => wms_axios.request({
    url :'qc/pda/batch_qc_change_save',
    method : 'post',
    data :data
}).then(d=>d.data)

export const single_qc_rejudge_save = (data) => wms_axios.request({
    url :'qc/pda/single_qc_rejudge_save',
    method : 'post',
    data:data
}).then(d=>d.data)

//查询单箱复检数据
export const batch_qc_rejudge_query = (data) => wms_axios.request({
    url : 'qc/pda/batch_qc_rejudge_query',
    method : 'post',
    data : data
}).then(d=>d.data)

//保存整批复检结果
export const batch_qc_rejudge_save = (data) => wms_axios.request({
    url : 'qc/pda/batch_qc_rejudge_save',
    method :'post',
    data : data
}).then(d=>d.data)
