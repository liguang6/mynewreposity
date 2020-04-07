/* eslint-disable no-console */
//入库模块后台调用API

import wms_axios from '@/libs/wms.request'

/**
 * 根据SCM送货单获取物料列表；
 * com.byd.wms.business.modules.in.controller.WmsInReceiptController :: listScmMat
 *  */
export const listScmMat = (ASNNO) => {
  console.log('-->api in listScmMat ASNNO = ' + ASNNO)
  const data = {
    'ASNNO': ASNNO
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/listScmMat',
    data,
    method: 'post'
  })
}

export const getLabelInfo = (LabelNo,LGORT,BIN_CODE) => {
  const data = {
    'LabelNo': LabelNo,
    'LGORT': LGORT,
    'BIN_CODE': BIN_CODE
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/getLabelInfo',
    data,
    method: 'post'
  })
}
export const list303Mat = (BUSINESS_NAME,SAP_MATDOC_NO,WH_NUMBER,WERKS,PZ_YEAR) => {
  const data = {
    'BUSINESS_NAME': BUSINESS_NAME,
    'SAP_MATDOC_NO': SAP_MATDOC_NO,
    'WH_NUMBER': WH_NUMBER,
    'WERKS':WERKS,
    'PZ_YEAR': PZ_YEAR,
    'JZ_DATE':''
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/list303Mat',
    data,
    method: 'post'
  })
}

export const getScmBarCodeInfo = (BARCODE) => {
  console.log('-->api in getScmBarCodeInfo BARCODE = ' + BARCODE)
  const data = {
    'BARCODE': BARCODE
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/getScmBarCodeInfo',
    data,
    method: 'post'
  })
}
export const getAllMatBarcodeList = (ASS_NO) => {
  console.log('-->api in getAllMatBarcodeList ASS_NO = ' + ASS_NO)
  const data = {
    'ASS_NO': ASS_NO
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/getAllMatBarcodeList',
    data,
    method: 'post'
  })
}

export const getMatBarcodeList = (ASS_NO,MATNR,PO_NO,ROWITEM) => {
  console.log('-->api in getMatBarcodeList')
  const data = {
    'ASS_NO': ASS_NO,
    'MATNR': MATNR,
    'PO_NO': PO_NO,
    'ROWITEM': ROWITEM
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/getMatBarcodeList',
    data,
    method: 'post'
  })
}

export const queryByBarcode = (barcode) => {
  const data = {
    'LABEL_NO': barcode
  }
  return wms_axios.request({
    url: '/pda/queryScannerByPda',
    data,
    method: 'post'
  })
}

export const validateBinCode = (barcode) => {
  const data = {
    'LABEL_NO': barcode
  }
  return wms_axios.request({
    url: '/pda/validateBinCode',
    data,
    method: 'post'
  })
}

export const commitByHwyd = (list) => {
  const data = {
    'params': list
  }
  return wms_axios.request({
    url: '/pda/commitByHwyd',
    data,
    method: 'post'
  })
}

export const queryBarcodeOnly = (barcode) => {
  const data = {
    'LABEL_NO': barcode
  }
  return wms_axios.request({
    url: '/out/scanner/queryBarcodeOnly',
    data,
    method: 'post'
  })
}

/**
 * 根据条码查询标签信息
 * @param {} barcode
 *
 */
export const listByPdaBarcode = (barcode) => {
  const data = {
    'LABEL_NO': barcode
  }
  return wms_axios.request({
    url: '/out/scanner/queryScannerByPda',
    data,
    method: 'post'
  })
}

/**
 * 保存标签信息到缓存表
 * @param {} barcode
 *
 */
export const listSaveByPdaBarcode = (list) => {
  const data = {
    'params': list
  }
  return wms_axios.request({
    url: '/out/scanner/listSaveByPdaBarcode',
    data,
    method: 'post'
  })
}


export const listReqMat = (barcode,werks) => {
  const data = {
    'matnr': barcode,
    'werks':werks
  }
  return wms_axios.request({
    url: '/out/outreq/reqCreate',
    data,
    method: 'post'
  })
}

export const listReqMatIns = (list) => {
  const data = {
    'list': list
  }
  return wms_axios.request({
    url: '/out/outreq/reqCreateIns',
    data,
    method: 'post'
  })
}

//获取包装箱信息
export const listSKInfo = (ASNNO,BUSINESS_NAME) => {
  const data = {
    'ASNNO': ASNNO,
    'BUSINESS_NAME': BUSINESS_NAME?BUSINESS_NAME:'01'
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/listSKInfo',
    data,
    method: 'post'
  })
}

export const listCloudMat = (ASNNO,BUSINESS_NAME) => {
  const data = {
    'ASNNO': ASNNO,
    'BUSINESS_NAME': BUSINESS_NAME?BUSINESS_NAME:'78'
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/listCloudMat',
    data,
    method: 'post'
  })
}

//收货确认
export const boundIn = (matList,allDataList,skList,WERKS,BUSINESS_NAME,ASNNO,PZ_DATE,JZ_DATE) => {
  console.log('-->api in boundIn matList = ' + matList + ",WERKS = " + WERKS)
  const data = {
    'matList': matList,
    'allDataList': allDataList,
    'skList': skList,
    'WERKS': WERKS,
    'BUSINESS_NAME': BUSINESS_NAME,
    'ASNNO': ASNNO,
    'PZ_DATE': PZ_DATE,
    'JZ_DATE': JZ_DATE
  }
  return wms_axios.request({
    url: '/in/wmsinreceipt/boundIn',
    data,
    method: 'post'
  })
}


//创建上架任务-获取条码
export const queryBarcode = (barcode) => {
  const data  = {
    'LABEL_NO':barcode
  }
  return wms_axios.request({
    url:'/in/wmsinbound/pda/barcode',
    data,
    method : 'post'
  })
}



export const ubTransferBarcodeDesc = (data) => {
  return wms_axios.request({
    url:'/in/ub_transfer_barcode_desc',
    data,
    method : 'post'
  })
}

//查询工厂配置表
export const queryBarcodeFlag = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/wh_config',
    data,
    method:"post"
  })
}
//查询进仓单
export const queryInInbound = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/inInbound',
    data,
    method:"post"
  })
}
//创建进仓单
export const newInInbound = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/inboundTask',
    data,
    method:"post"
  })
}
//查询仓管员
export const relatedAreaNamelist = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/relatedAreaNamelist',
    data,
    method:"post"
  })
}

export const queryWhTasks = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/wh_task_list',
    data,
    method:"post"
  })
}

//仓库储位排序
export const queryWhBinSEQ = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/wh_bin_seq',
    data,
    method:"post"
  })
}

//仓库任务，上架
export const whTaskShelf  = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/shelf',
    data,
    method:"post"
  })
}

//扫描实物条码上架 - 条码查询
export const queryLabelScannerShelf = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/scanner_real_material/label',
    data,
    method:"post"
  })
}

//扫描实物条码上架 - 上架
export const scannerLabelNoShelf = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/scanner_real_material/shelf',
    data,
    method:"post"
  })
}

//扫描实物条码上架 - 查询最新的移储储位
export const queryLatestMvStorageBincode  =  data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/scanner_real_material/latest_bincode',
    data,
    method:"post"
  })
}

//查询进仓单的库位
export const queryLgortFromInbound = data => {
  return wms_axios.request({
    url:'/in/wmsinbound/pda/ub/logrt',
    data,
    method:"post"
  })
}


//UB转储单调拨进仓 查询
export const ubTransferInit = (data) => {
  return wms_axios.request({
    url : '/in/wmsinbound/pda/ub/label',
    data,
    method:'post'
  })
}


export const getLabelInfoByBarcode = (data) => {
  return wms_axios.request({
    url:'/account/wmsVoucherReversal/getVoucherReversalDataByLable',
    data,
    method :'post'
  })
}

export const confirmWriteOffInfo = (data) => {
  return wms_axios.request({
    url:'/account/wmsVoucherReversal/confirmVoucherReversalData',
    data,
    method :'post'
  })
}

export const getInboundInfoByBarcode = (data) => {
  return wms_axios.request({
    url:'/in/wmsinhandoverbound/list',
    data,
    method :'post'
  })
}

export const confirmInboundInfo = (data) =>{
  return wms_axios.request({
    url:'/in/wmsinhandoverbound/save',
    data,
    method :'post'
  })
}

export const getLabelInfoInbound = (data) =>{
  return wms_axios.request({
    url:'/in/wmsinhandoverbound/getLabelInfo',
    data,
    method :'post'
  })
}

export const getLabelDetailByBarcode = (data)=>{
  return wms_axios.request({
    url:'/in/wmsinhandoverbound/labellist',
    data,
    method :'post'
  })
}

export const validateLableQty = (data) => {
  return wms_axios.request({
    url: '/in/wmsinhandoverbound/ValidlabelQyt',
    data,
    method: 'post',
  })
}

export const checkBinCode = (data) => {
  return wms_axios.request({
    url: '/common/getBinCode',
    data,
    method: 'post',
    async:false
  })
}

export const getBusinessList = (werks,businessClass) => {
  const data = {
    werks: werks,
    businessClass:businessClass
  };
  return wms_axios.request({
    url: "/out/scanner/queryBusinessName",
    data,
    method: "post"
  });
};
