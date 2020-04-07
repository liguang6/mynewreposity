import axios from '@/libs/api.request'
import wms_axios from '@/libs/wms.request'

export const getTableData = () => {
  return axios.request({
    url: 'get_table_data',
    method: 'get'
  })
}

export const getDragList = () => {
  return axios.request({
    url: 'get_drag_list',
    method: 'get'
  })
}

export const errorReq = () => {
  return axios.request({
    url: 'error_url',
    method: 'post'
  })
}

export const saveErrorLogger = info => {
  return axios.request({
    url: 'save_error_logger',
    data: info,
    method: 'post'
  })
}

export const uploadImg = formData => {
  return axios.request({
    url: 'image/upload',
    data: formData
  })
}

export const getOrgData = () => {
  return axios.request({
    url: 'get_org_data',
    method: 'get'
  })
}

export const getTreeSelectData = () => {
  return axios.request({
    url: 'get_tree_select_data',
    method: 'get'
  })
}

export const getInventoryResult = (WERKS,WH_NUMBER,INVENTORY_NO,TYPE) => {
  const data = {
    'WERKS': WERKS,
    'WH_NUMBER': WH_NUMBER,
    'INVENTORY_NO':INVENTORY_NO,
    'TYPE':TYPE?TYPE:'00'
  }
  return wms_axios.request({
    url: '/kn/inventory/getInventoryResult',
    data,
    method: 'post'
  })
}

export const saveResult = (SAVE_DATA,TYPE,INVENTORY_NO) => {
  const data = {
    'SAVE_DATA': SAVE_DATA,
    'INVENTORY_NO':INVENTORY_NO,
    'TYPE':TYPE?TYPE:'00',
    'USERNAME':'PDA'
  }
  return wms_axios.request({
    url: '/kn/inventory/saveResult',
    data,
    method: 'post'
  })
}

export const getLabelInfoByNo = (LABEL_NO) => {
  const data = {
    'LABEL_NO': LABEL_NO
  }
  return wms_axios.request({
    url: '/common/getLabelInfo',
    data,
    method: 'post'
  })
}