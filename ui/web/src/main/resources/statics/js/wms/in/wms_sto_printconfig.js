
var vm = new Vue({
    el:'#vue-app',

    data:{
        c:{},
        WERKS:'',//工厂
    },
    watch: {
        WERKS: {
            handler: function (newVal, oldVal) {
                $.ajax({
                    url: baseURL + "",
                    datatype: "json",
                    type: "post",
                    data: {
                        "WERKS": newVal,
                    },
                    async: true,
                    success: function (resp) {
                        if (resp && resp.data.length > 0) {

                        }

                    }
                })

            }

        }
    },
        creat:function(){

        },
    methods: {
        query: function () {
            $("#searchForm").submit();

        },
        refresh: function () {
            $("#searchForm").submit();
        }
    }
});






$(function () {
    $('#dataGrid').dataGrid({
        searchForm: $("#searchForm"),
        columnModel: [
            {header:'工厂', name:'WORKS', width:60, align:"center"},
            {header:'公司代码', name:'COMPANY_CODE', width:120, align:"center"},
            {header:'公司名称', name:'COMPANY_NAME', width:120, align:"center"},
            {header:'公司英文名称', name:'COMPANY_ENGLISH', width:160, align:"center"},
            {header:'电话', name:'TEL', width:60, align:"center"},
            {header:'传真', name:'FAX', width:60, align:"center"},
            {header:'邮编', name:'ZIP_CODE', width:60, align:"center"},
            {header:'地址', name:'ADRESS', width:60, align:"center"},
            {header:'公司英文地址', name:'ADRESS_ENGLISH', width:160, align:"center"},
            {header:'发货人电话', name:'CONSIGNOR_TEL', width:120, align:"center"},
            {header:'发货人', name:'CONSIGNOR', width:100, align:"center"},
            {header:'发货人地址', name:'CONSIGNOR_ADRESS', width:130, align:"center"},
            {header:'收货人电话', name:'CONSIGNEE_TEL', width:130, align:"center"},
            {header:'收货人', name:'CONSIGNEE', width:100, align:"center"},
            {header:'收货人地址', name:'CONSIGNEE_ADRESS', width:130, align:"center"},


        ],
        autoScroll: true,
        rowNum: 15,
        showCheckbox:true,
        multiselect: true,
    });
});
 function edit(){
        var ids = $("#dataGrid").getGridParam("selarrrow");
     if(ids.length===0){
         alert('请勾选后再编辑！');
         return;
     }

       var rowData = $("#dataGrid").jqGrid('getRowData',ids);
     var options = {
         type: 2,
         maxmin: true,
         shadeClose: true,
         area: ["580px", "400px"],
         content: 'wms/in/wms_sto_printconfig_edit.html',
         btn: ['<i class="fa fa-check"></i>'+'确定'],
         success:function(layero, index){
             var win = top[layero.find('iframe')[0]['name']];
             win.vm.getdata=rowData;
             win.vm.saveFlag=false;
         },
         btn1:function(index,layero){
             var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
             $(btnSubmit).click();
               vm.refresh();
              closeOptions(index);
             }
         }
           options.btn.push('<i class="fa fa-close"></i>'+'关闭');
           js.layer.open(options)
         }


function add(){
    var options = {
        type: 2,
        maxmin: true,
        area:["580px","400px"],
        content:'wms/in/wms_sto_printconfig_add.html',
        btn:['<i class="fa fa-close"></i>'+'确定'],
        success:function (layero,index) {
        },
        btn1:function (index,layero) {
            var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
            $(btnSubmit).click();
            vm.refresh();
            closeOptions(index);
        },

    };
    options.btn.push('<i class="fa fa-close"></i>'+'关闭');
    js.layer.open(options)

};
function remove(){
    var ids=$("#dataGrid").getGridParam("selarrrow");
    if(ids.length===0){
        alert('请勾选后再删除！');
        return;
    }
    var rowData = $("#dataGrid").jqGrid('getRowData',ids);

    $.ajax({
        type:'post',
        dataType:'json',
        url:baseURL+'in/sto/del',
        data:{"params":JSON.stringify(rowData)},
        success:function (resp) {
            if(resp.code == 0){
                js.showMessage("删除成功")
                vm.refresh();
            }else{
                alert("删除失败")
            }
        }

    });
}
function  closeOptions(index){
   parent.layer.close(index);
}
