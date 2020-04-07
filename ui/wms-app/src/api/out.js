/* eslint-disable no-console */
//出库模块后台调用API

import wms_axios from "@/libs/wms.request";

export const DispatchingConfirmList = (barCode,fromPlantCode) => {
  const data = {
    DISPATCHING_NO: barCode,
    STATUS:'03',
    FROM_PLANT_CODE:fromPlantCode,
    LINE_CATEGORY:"",
		PLANT_CODE:"C113"
  }
  return wms_axios.request({
    url: '/cswlms/dispatchingBillPicking/listQueRenPDA',
    data,
    method: 'post'
  })
}

export const DispatchingConfirm = list => {
  const data = {
    'ARRLIST': list
  }
  console.log('-->complist: ' ,list)
  return wms_axios.request({
    url: "/cswlms/dispatchingBillPicking/checkQueRenPDA",
    data ,
    method: "post"
  });
};

export const DispatchingConfirmUpdata = list => {
  const data = {
    'ARRLIST': list
  }
  return wms_axios.request({
    url: "/cswlms/dispatchingBillPicking/updateQueRenPDA",
    data ,
    method: "post"
  });
};

export const DispatchingHandoverList = (barCode,fromPlantCode) => {
  const data = {
    DISPATCHING_NO: barCode,
    STATUS:'08',
    FROM_PLANT_CODE:fromPlantCode,
    LINE_CATEGORY:"",
		PLANT_CODE:"C113"
  }
  return wms_axios.request({
    url: '/cswlms/dispatchingBillPicking/listjiaojiePDA',
    data,
    method: 'post'
  })
}

export const dispatchingHandover = (ARRLIST,PZDDT,JZDDT) => {
  console.log('-->api dispatchingHandover = ',ARRLIST)
  const data = {
    ARRLIST: ARRLIST,
    PZDDT: PZDDT,
    JZDDT: JZDDT
  }
  return wms_axios.request({
    url: '/cswlms/dispatchingBillPicking/dispatchingHandoverPDA',
    data,
    method: 'post'
  })
}

export const queryOutReqPda311 = (barcode, werks) => {
  const data = {
    matnr: barcode,
    werks: werks
  };
  return wms_axios.request({
    url: "/out/outreq/queryOutReqPda311",
    data,
    method: "post"
  });
};

export const queryUNIT = barcode => {
  const data = {
    matnr: barcode
  };
  return wms_axios.request({
    url: "/out/outreq/queryUNIT",
    data,
    method: "post"
  });
};

export const createOutReqPda311 = list => {
  const data = list;
  return wms_axios.request({
    url: "/out/outreq/createOutReqPda311",
    data,
    method: "post"
  });
};
//
export const recommend = (requirementNo, WERKS, WH_NUMBER) => {
  let param = new URLSearchParams();
  param.append("REQUIREMENT_NO", requirementNo);
  param.append("WERKS", WERKS);
  param.append("WH_NUMBER", WH_NUMBER);
  return wms_axios.request({
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    url: "/out/picking/pdaRecommend",
    data: param,
    method: "post"
  });
};
//新建仓库任务
export const saveWHTask = SAVE_DATA => {
  const data = SAVE_DATA;
  return wms_axios.request({
    url: "/whTask/save",
    data,
    method: "post"
  });
};

//查询seqno
export const querySeqNo = binCode => {
  const data = {
    binCode: binCode
  };
  return wms_axios.request({
    url: "/whTask/querySeqNo",
    data,
    method: "post"
  });
};
//拣货
export const pdaPicking = (REQUIREMENT_NO,WERKS, WH_NUMBER, SAVE_DATA, addList, modifyList) => {
  const data = {
    REQUIREMENT_NO:REQUIREMENT_NO,
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    SAVE_DATA: SAVE_DATA,
    addList: addList,
    modifyList: modifyList
  }
  return wms_axios.request({
    url: "/out/picking/pdaPicking",
    data,
    method: "post"
  });
};

//查询仓库任务
export const selectCoreWHTask = (requirementNo, status) => {
  const data = {
    REFERENCE_DELIVERY_NO: requirementNo,
    WT_STATUS: status
  };
  return wms_axios.request({
    url: "/whTask/query",
    data,
    method: "post"
  });
};

//拣配扫描
export const scanLabel = (labelNo, werks) => {
  const data = {
    labelNo: labelNo,
    werks: werks
  };
  return wms_axios.request({
    url: "/whTask/scanLabel",
    data,
    method: "post"
  });
};

//过账list
export const handoverList = (requirementNo, WERKS, WH_NUMBER) => {
  const data = {
    REQUIREMENT_NO: requirementNo,
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER
  };
  return wms_axios.request({
    url: "/out/pdahandover/list",
    data,
    method: "post"
  });
};

//过账save
export const handoverSave = (
  PZDDT,
  JZDDT,
  WERKS,
  WH_NUMBER,
  ARRLIST,
  USERNAME
) => {
  const data = {
    ARRLIST: ARRLIST,
    WERKS: WERKS,
    WH_NUMBER: WH_NUMBER,
    PZDDT: PZDDT,
    JZDDT: JZDDT,
    USERNAME: USERNAME
  };
  return wms_axios.request({
    url: "/out/pdahandover/save",
    data,
    method: "post"
  });
};

//查询库存物料号
export const queryMatnr = labelNo => {
  const data = {
    LABEL_NO: labelNo
  };
  return wms_axios.request({
    url: "/inventorySearch/queryMatnr",
    data,
    method: "post"
  });
};

//查询库存列表
export const inventoryList = (werks, matnr) => {
  const data = {
    WERKS: werks,
    MATNR: matnr
  };
  return wms_axios.request({
    url: "/inventorySearch/list",
    data,
    method: "post"
  });
};

//一步联动过账
export const stepLinkageHandover = list => {
  const data = list;
  return wms_axios.request({
    url: "/stepLinkage/handover",
    data,
    method: "post"
  });
};

export const JITScanLabel = (LABEL_NO ,werks)=> {
  const data = {
    LABEL_NO: LABEL_NO,
    WERKS: werks
  };
  return wms_axios.request({
    url: "/cswlms/JITScanLabel",
    data,
    method: "post"
  });
};

export const JITScanDeliveryNo = DELIVERY_NO => {
  const data = {
    DELIVERY_NO: DELIVERY_NO
  };
  return wms_axios.request({
    url: "/cswlms/JITScanDeliveryNo",
    data,
    method: "post"
  });
};

export const JITPick = (SAVE_DATA) => {
  const data = {
    SAVE_DATA: SAVE_DATA
  };
  return wms_axios.request({
    url: "/cswlms/JITPick",
    data,
    method: "post"
  });
};

export const getLabel = (WERKS) => {
  const data = {
    WERKS: WERKS
  };
  return wms_axios.request({
    url: "/out/picking/getLabel",
    data,
    method: "post"
  });
};