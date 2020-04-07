
$(function () {

    $("#dataGrid").dataGrid({
        searchForm: $("#searchForm"),
        datatype : "local",
        columnModel: [
            { label: '客户权限值', name: 'KUNNR',  index:'KUNNR', width: 80,align:'center'},
            { label: '工厂权限值', name: 'WERKS', index:'WERKS', width: 80,align:'center'},
            { label: '隐藏字段', name: 'HIDDEN_FIELD', index:'HIDDEN_FIELD', width: 80,align:'center'},
            { label: '含有二维码', name: 'QRCODE', index:'QRCODE', width: 80,align:'center'},
            { label: '含有条形码', name: 'BARCODE', index:'BARCODE', width: 80,align:'center'},
            { label: 'ID', name: 'ID',index:'ID',hidden:true},
            { label: 'TEMPLATE_ID', name: 'TEMPLATE_ID',index:'TEMPLATE_ID',hidden:true}
        ],
        onSelectRow: function (rowId,status, e) {},
        viewrecords: true,
        rowNum: 30
    });

    //------删除操作---------
    $("#deleteOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        var grData = $("#dataGrid").jqGrid("getRowData",gr);//获取行数据
        if (gr != null  && gr!==undefined) {
            layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
                $.ajax({
                    type: "POST",
                    url: baseURL + "config/printTemplate/deleteConfig/"+grData.ID,
                    async : false,
                    success: function(r){
                        if(r.code == 0){
                            js.showMessage('删除成功!');
                            vm.reload();
                        }else{
                            js.showErrorMessage(r.msg);
                        }
                    }
                });
                layer.close(index);
            });
        }
        else layer.msg("请选择要删除的行",{time:1000});
    });

    //------新增操作---------
    $("#newOperation").click(function(){
        var options = {
            type: 2,
            maxmin: true,
            shadeClose: true,
            title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增模板配置',
            area: ["500px", "420px"],
            content: "wms/config/print_template_config_edit.html",
            btn: ['<i class="fa fa-check"></i> 确定'],
            success: function (layero, index) {
                var win = top[layero.find('iframe')[0]['name']];
                win.vm.getTemplate($("#TEMPLATE_ID").val(),$("#TEMP_TYPE").val(),$("#TEMP_NAME").val());

            },
            btn1: function (index, layero) {
                var win = top[layero.find('iframe')[0]['name']];
                var btnSubmit = $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
                $(btnSubmit).click();
                if (typeof listselectCallback == 'function') {
                    listselectCallback(index, win.vm.saveFlag);
                }
            }
        };
        options.btn.push('<i class="fa fa-close"></i> 关闭');
        options['btn' + options.btn.length] = function (index, layero) {
        };
        js.layer.open(options);

    });
    //-------编辑操作--------
    $("#editOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        if(gr !== null && gr!==undefined){
            var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
            var options = {
                type: 2,
                maxmin: true,
                shadeClose: true,
                title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改模板配置',
                area: ["500px", "420px"],
                content: 'wms/config/print_template_config_edit.html',
                btn: ['<i class="fa fa-check"></i> 确定'],
                success:function(layero, index){

                    var win = top[layero.find('iframe')[0]['name']];
                    win.vm.getTemplate($("#TEMPLATE_ID").val(),$("#TEMP_TYPE").val(),$("#TEMP_NAME").val());
                    win.vm.getTemplateConfig(grData.ID, grData.KUNNR,
                        grData.WERKS, grData.HIDDEN_FIELD, grData.QRCODE=='是'? 'Y':'N', grData.BARCODE=='是'? 'Y':'N');

                },
                btn1:function(index,layero){
                    var win = top[layero.find('iframe')[0]['name']];
                    var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
                    $(btnSubmit).click();
                    if(typeof listselectCallback == 'function'){
                        listselectCallback(index,win.vm.saveFlag);
                    }
                }
            };
            options.btn.push('<i class="fa fa-close"></i> 关闭');
            options['btn'+options.btn.length] = function(index, layero){
                if(typeof listselectCallback == 'function'){
                }
            };
            js.layer.open(options);
        }else{
            layer.msg("请选择要编辑的行",{time:1000});
        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
    },
    methods: {
        query: function () {
            js.loading();
            vm.$nextTick(function(){//数据渲染后调用
                $("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                js.closeLoading();
            })

        },
        reload: function (event) {
            $("#searchForm").submit();
        }
    }
});


function listselectCallback(index,rtn){
    rtn = rtn ||false;
    if(rtn){
        //操作成功，刷新表格
        vm.reload();
//        parent.layer.close(index);
        parent.parent.layer.close(index);
    }
}