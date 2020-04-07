
function getInfo(id){
    $("#sterilisationId").val(id);
}
function printpage(){
        var qtyNum = $("#qtyNum").val();//获取总数
        var boxNum = $("#boxNum").val();//获取每箱数量
        var sterilisationId =$("#sterilisationId").val();
        var options = {
            type: 2,
            maxmin: true,
            shadeClose: true,
            title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>条码打印',
            area: ["70%", "50%"],
            content: baseURL + 'wms/kn/wms_c_barcode_sterilisation_barcode.html',
            btn: ['<i class="fa fa-close"></i> 关闭'],
            success:function(layero, index){
                var win = layero.find('iframe')[0].contentWindow;
                win.getInfo(qtyNum,boxNum,sterilisationId);
                win.init();
            },
            // btn1:function(index,layero){
            //     var win = top[layero.find('iframe')[0]['name']];
            //     win.printView();
            //
            // }
        };
        // options.btn.push('<i class="fa fa-close"></i> 关闭');
        js.layer.open(options);
};

function init(){
    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype: "json",
        colModel: [
            {label: '物料号', name: 'MATNR',index:"MATNR", width: 200,align:"center"},
            // {label: '数量', name: 'QTYWMS',index:"QTYWMS", width: 150,align:"center" },
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: 150,align:"center" },
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: 150,align:"center" },
            {label: '批次', name: 'BATCH',index:"BATCH", width: 150,align:"center" },
            {label: '工厂', name: 'WERKS',index:"WERKS", width: 150,align:"center" },
            {label: '单位', name: 'UNIT',index:"UNIT", width: 150,align:"center" },
            {label: '订单号', name: 'WMSNO',index:"WMSNO", width: 150,align:"center" },
            {label: '行项目', name: 'WMSITEMNO',index:"WMSITEMNO", width: 150,align:"center" },
            {label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true}//隐藏此列
        ],
        viewrecords: true,
        rowNum: 15,
        rownumWidth: 25,
        height: '30px',
    });
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