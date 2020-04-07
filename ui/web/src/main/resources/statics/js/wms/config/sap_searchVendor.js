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

        }
    }
})


$(function(){

    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype: "local",
        colModel: [
            {label: '供应商代码', name: 'lifnr',index:"lifnr", width: 200,align:"center"},
            {label: '供应商名称',name: 'name1',index:"name1",width: 200,align:"center"},
            {label: '供应商英文名称', name: 'name2',index:"name2", width: 150,align:"center" },
            {label: '同步日期', name: 'importDate',index:"importDate", width: 150,align:"center",
                formatter:'date',formatoptions:{newformat:'Y-m-d H:i:s'} }

        ],
        rowNum: 15,
        rownumWidth: 25

    });

});


