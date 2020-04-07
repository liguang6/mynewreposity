export const initShelfTask = req => {
    return [{batch : '123456-00 1809001A',vendor : '30024',vendorName:'比亚迪',qty:3}]
}


export const ubTransfer = req => {
    let reqbody = JSON.parse(req.body)
    return  [
        {orderNum:'38975000 10',store:reqbody.werks,mat:'123456-00',qty:2}
    ]
}

export const queryUbTreansferBarcodeDesc = req => {
    let body = JSON.parse(req.body)
    return [
        {barcode:'2200000010',boxOrder:'1/2',qty:1},
        {barcode:'2200000011',boxOrder:'1/2',qty:1},
        {barcode:'2200000012',boxOrder:'1/2',qty:1}
    ]
}


export const whTaskList = req => {
    return {
        code:'0',
        data:[
            {'INDEX':1,'ID':'724','WH_NUMBER':'C161','WT_STATUS':'01','TASK_NUM':'1WT0000000658','PROCESS_TYPE':'00','MATNR':'10009282-00','MAKTX':'Q370C10F_焊接六角螺母_本色','QUANTITY':'27','BATCH':'1611230003','LABEL_NO':'1BQ0000001271','TO_STORAGE_AREA':'A001',"TO_BIN_CODE":"A1-01-01-2","MOULD_NO":"xxx","LIFNR":"BYD"},
            {'INDEX':2,'ID':'725','WH_NUMBER':'C161','WT_STATUS':'00','TASK_NUM':'1WT0000000659','PROCESS_TYPE':'00','MATNR':'10199083-11','MAKTX':'M6-6900011_副驾驶座椅左侧外护板_M00666','QUANTITY':'10','BATCH':'1611230003','LABEL_NO':'1BQ0000001269','TO_STORAGE_AREA':'A002',"TO_BIN_CODE":"A1-01-01-3","MOULD_NO":"xxx","LIFNR":"BYD"},
            {'INDEX':3,'ID':'726','WH_NUMBER':'C161','WT_STATUS':'01','TASK_NUM':'1WT0000000660','PROCESS_TYPE':'00','MATNR':'10199083-11','MAKTX':'M6-6900011_副驾驶座椅左侧外护板_M00666','QUANTITY':'12','BATCH':'1611230003','LABEL_NO':'1BQ0000001269','TO_STORAGE_AREA':'A002',"TO_BIN_CODE":"A1-01-01-3","MOULD_NO":"xxx","LIFNR":"BYD"}
        ]
    }
}
