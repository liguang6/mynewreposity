var vm = new Vue({
    el:'#rrapp',
    data:{

    },
    created:function(){

    },
    methods: {
        query: function () {
            js.loading();
            vm.$nextTick(function(){//数据渲染后调用
                $("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                js.closeLoading();
            })

        },
        exp: function () {
            export2Excel();
        }
    }
})


$(function(){

    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype: "local",
        colModel: [
            {label: '物料号', name: 'MATNR',index:"MATNR", width: 200,align:"center"},
            {label: '物料描述',name: 'MAKTX',index:"MAKTX",width: 200,align:"center"},
            {label: '英文描述', name: 'MAKTX_EN',index:"MAKTX_EN", width: 150,align:"center" },
            {label: '采购类型', name: 'BESKZ',index:"BESKZ", width: 150,align:"center" },
            {label: '工厂代码', name: 'WERKS',index:"WERKS", width: 150,align:"center" },
            {label: '物料状态', name: 'MMSTA',index:"MMSTA", width: 150,align:"center" },
            {label: '质检周期', name: 'PRFRQ',index:"PRFRQ", width: 150,align:"center" },
            {label: '计量单位', name: 'MEINS',index:"MEINS", width: 150,align:"center" },
            {label: '采购单位', name: 'BSTME',index:"BSTME", width: 150,align:"center" },
            {label: '发货单位', name: 'AUSME',index:"AUSME", width: 150,align:"center" },
            {label: '导入时间', name: 'IMPORT_DATE',index:"IMPORT_DATE", width: 150,align:"center" }

        ],
        rowNum: 15,
        rownumWidth: 25

    });

});

function export2Excel(){

    var column= [
        {title:'物料号', data:'MATNR', "class":"center"},
        {title:'物料描述', data:'MAKTX', "class":"center"},
        {title:'英文描述', data:'MAKTX_EN', "class":"center"},
        {title:'采购类型', data:'BESKZ', "class":"center"},
        {title:'工厂代码', data:'WERKS', "class":"center"},
        {title:'物料状态', data:'MMSTA', "class":"center"},
        {title:'质检周期', data:'PRFRQ', "class":"center"},
        {title:'计量单位', data:'MEINS', "class":"center"},
        {title:'采购单位', data:'BSTME', "class":"center"},
        {title:'发货单位', data:'AUSME', "class":"center"},
        {title:'导入时间', data:'IMPORT_DATE', "class":"center"},
    ]
    var results=[];
    var params=$("#searchForm").serialize();
    var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i");
    var l =params.replace(reg,'$1');
    params=l;
    $.ajax({
        url:baseURL+"in/wmsinreceipt/exportExcel",
        dataType : "json",
        type : "post",
        data : params,
        async: false,
        success: function (response) {
            results=response.page.list;
        }
    });
    toExcel("物料信息",results,column);
}
