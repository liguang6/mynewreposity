var vm = new Vue({
    el:'#rrapp',
    data:{
    },
    methods: {
        print: function () {
            //获取选中表格数据
            //var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
            var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
            if(ids.length == 0){
                alert("当前还没有勾选任何行项目数据！");
                return false;
            }
            var saveData = [];
            for (var i = 0; i < ids.length; i++) {
                var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
            }
            if(saveData.length>0)
                $("#labelList").val(JSON.stringify(saveData));
            $("#printButton").click();
        },
        printA4: function () {
            //获取选中表格数据
            //var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
            var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
            if(ids.length == 0){
                alert("当前还没有勾选任何行项目数据！");
                return false;
            }
            var saveData = [];
            for (var i = 0; i < ids.length; i++) {
                var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
            }
            if(saveData.length>0)
                $("#labelListA4").val(JSON.stringify(saveData));
            $("#printButtonA4").click();
        }
    }
});
function getInfo(qtyNum,boxNum,sterilisationId){
    $("#qtyNum").val(qtyNum);
    $("#boxNum").val(boxNum);
    $("#sterilisationId").val(sterilisationId);
}

function init(){
    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype: "json",
        colModel: [
            // {label: '标签号', name: 'LABEL_NO',index:"LABEL_NO", width: 200,align:"center"},
            // {label: '物料号', name: 'MATNR',index:"MATNR", width: 200,align:"center"},
            // {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: 150,align:"center" },
            // {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: 150,align:"center" },
            // {label: '批次', name: 'BATCH',index:"BATCH", width: 150,align:"center" },
            // {label: '工厂', name: 'WERKS',index:"WERKS", width: 150,align:"center" },
            // {label: '单位', name: 'UNIT',index:"UNIT", width: 150,align:"center" },
            // {label: '订单号', name: 'WMSNO',index:"WMSNO", width: 150,align:"center" },
            // {label: '装箱数', name: 'BOX_QTY',index:"BOX_QTY", width: 150,align:"center" },
            // {label: '生产日期', name: 'PRODUCT_DATE',index:"PRODUCT_DATE", width: 150,align:"center" },
            // {header:'行项目号', name:'PO_ITEM_NO', width:70, align:"center"},
            // {label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true}//隐藏此列

            {label:'工厂', name:'WERKS', width:60, align:"center"},
            {label:'条码号', name:'LABEL_NO', width:60, align:"center"},
            {label:'订单号', name:'PO_NO', width:70, align:"center"},
            {label:'行项目号', name:'PO_ITEM_NO', width:70, align:"center"},
            {label:'料号', name:'MATNR', width:100, align:"center"},
            {label:'物料描述', name:'MAKTX', width:150, align:"center"},
            {label:'供应商代码', name:'LIFNR', width:90, align:"center"},
            {label:'数量', name:'BOX_QTY', width:60, align:"center"},
            {label:'单位', name:'UNIT', width:60, align:"center"},
            {label:'生产日期', name:'PRODUCT_DATE', width:100, align:"center"},
            {label:'BYD生产批次', name:'BATCH', width:100, align:"center"},
            { label: 'ID', name: 'ID', index: 'ID',hidden:true},
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25,
        height: '30px',
        showCheckbox:true,
    });
}

function printView() {
    var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
    if (ids.length == 0) {
        alert("当前还没有勾选任何行项目数据！");
        return false;
    }
    var saveData = [];
    for (var i = 0; i < ids.length; i++) {
        var data = $("#dataGrid").jqGrid('getRowData', ids[i]);
        saveData[i] = $("#dataGrid").jqGrid('getRowData', ids[i]);
    }
    js.layer.closeAll('iframe');
    if (saveData.length > 0) {
        // $.ajax({
        //     url: baseURL + "/docPrint/labelLabelPreview",
        //     dataType: "json",
        //     type: "post",
        //     data: {
        //         labelList: JSON.stringify(saveData)
        //     },
        //     async: false,
        //     success: function (data) {
        //     }
        // });
        $("#labelList").val(JSON.stringify(saveData));
        $("#printButton").click();
    }
}

function getSelRow(gridSelector){
    var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
    return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function listselectCallback(index,rtn){
    rtn = rtn ||false;
    if(rtn){
        //操作成功，刷新表格
        vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
    }
}