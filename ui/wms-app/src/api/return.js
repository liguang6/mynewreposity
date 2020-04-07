//退货模块后台调用API

import wms_axios from '@/libs/wms.request'

export const getReturnInfoByBarcode = (barCode) => {
  const data = {
    barCode: barCode
  }
  return wms_axios.request({
    url: '/returngoods/getReturnInfoByBarcode',
    data,
    method: 'post',
  })
}

export const createReceiveRoomOutReturn = (WERKS,WH_NUMBER,BUSINESS_NAME,BUSINESS_CODE,USERNAME,ARRLIST) => {
  const data = {
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    BUSINESS_NAME: BUSINESS_NAME,
    BUSINESS_CODE: BUSINESS_CODE,
    USERNAME: USERNAME,
    ARRLIST: ARRLIST
  }
  return wms_axios.request({
    url: '/returngoods/createReceiveRoomOutReturnPda',
    data,
    method: 'post',
  })
}

export const confirmReceiveRoomOutReturn = (WERKS,RETURN_NO,PZ_DATE,JZ_DATE,USERNAME,ARRLIST) => {
  const data = {
    WERKS: WERKS,
    RETURN_NO: RETURN_NO,
    PZ_DATE: PZ_DATE,
    JZ_DATE: JZ_DATE,
    USERNAME: USERNAME,
    ARRLIST: ARRLIST
  }
  return wms_axios.request({
    url: '/returngoods/confirmReceiveRoomOutReturnPda',
    data,
    method: 'post',
  })
}

export const getSapPoInfo = (PO_NO) => {
  const data = {
    PO_NO: PO_NO
  }
  return wms_axios.request({
    url: '/returngoods/getSapPoInfo',
    data,
    method: 'post',
  })
}
//查询 【库房退货:采购订单退货122】数据
export const getWareHouseOutData28 = (WERKS,WH_NUMBER,PONO) => {
  const data = {
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    LGORT: '',
    BUSINESS_NAME: '28',
    BUSINESS_CODE: '28',
    PONO: PONO
  }
  return wms_axios.request({
    url: '/returngoods/getWareHouseOutData28',
    params:data,
    method: 'post',
  })
}
//查询 【库房退货:采购订单退货161】数据
export const getWareHouseOutData29 = (WERKS,WH_NUMBER,PONO) => {
  const data = {
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    LGORT: '',
    BUSINESS_NAME: '29',
    BUSINESS_CODE: '29',
    PONO: PONO
  }
  return wms_axios.request({
    url: '/returngoods/getWareHouseOutData29',
    params:data,
    method: 'post',
  })
}
export const wareHouseOutReturnConfirm = (WERKS,WH_NUMBER,BUSINESS_TYPE,BUSINESS_NAME,ARRLIST,PZ_DATE,JZ_DATE) => {
  const data = {
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    BUSINESS_TYPE: BUSINESS_TYPE,
    BUSINESS_NAME: BUSINESS_NAME,
    ARRLIST: ARRLIST,
    PZ_DATE: PZ_DATE,
    JZ_DATE: JZ_DATE
  }
  return wms_axios.request({
    url: '/returngoods/pdaWareHouseOutReturnConfirm',
    data,
    method: 'post',
  })
}
export const getSapPoBarcodeInfo = (PO_NO,Barcode) => {
  const data = {
    PO_NO: PO_NO,
    Barcode: Barcode
  }
  return wms_axios.request({
    url: '/returngoods/getSapPoBarcodeInfo',
    data,
    method: 'post',
  })
}