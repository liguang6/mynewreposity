//国际化取值
var smallUtil=new smallTools();
var array = new Array("roleName","userName","menuName","roleId","authFields","authValue","userId","menuId");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array,"M");



$(function () {
    /*$(document).ready(function(){

        vm.reload();
    });*/

    $("#dataGrid").dataGrid({
        searchForm: $("#searchForm"),
        datatype : "local",
        columnModel: [
            { label: languageObj.roleName, name: 'ROLENAME',  index:'ROLENAME', width: 80,align:'center'},
            { label: languageObj.menuName, name: 'MENUNAME',index:'MENUNAME', width: 80,align:'center'  },
            { label: languageObj.authFields, name: 'AUTHFIELDS', index:'AUTHFIELDS', width: 80,align:'center'  },
            { label: languageObj.authValue, name: 'AUTHVALUE',index:'AUTHVALUE', width: 80,align:'center'  },
//            { label: languageObj.userName, name: 'USERNAME',index:'USERNAME', width: 80,align:'center'  },
            { label: 'ID', name: 'ID',index:'ID',key: true ,hidden:true}
        ],
        /*rowNumber : 15,*/
        viewrecords: true,
        rowNum: 15
    });


    //------删除操作---------
    $("#deleteOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        var grData = $("#dataGrid").jqGrid("getRowData",gr);//获取行数据
        if (gr != null  && gr!==undefined) {
            layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/dataPermission/delete?id="+grData.ID,
                    // data: JSON.stringify(vm.data),
                    contentType: "application/json",
                    success: function(r){
                        if(r.code == 0){
                            js.showMessage('删除成功!');
                            vm.reload();
                        }else{
                            alert(r.msg);
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
            title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增数据权限配置',
            area: ["500px", "320px"],
            content: baseURL + "sys/dataPermission_edit.html",
            btn: ['<i class="fa fa-check"></i> 确定'],
            success:function(layero, index){
            	var win = layero.find('iframe')[0].contentWindow;
                win.vm.saveFlag = false;
                win.vm.getAutuRole($("#roleId").val(),$("#roleName").val());
                //listselectCallback(index,win.vm.saveFlag);
            },
            btn1:function(index,layero){
            	var win = layero.find('iframe')[0].contentWindow;
                var flag=false;

                var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
                $(btnSubmit).click();
                if(typeof listselectCallback == 'function'){
                    listselectCallback(index,win.vm.saveFlag);
                }
            }
        };
        options.btn.push('<i class="fa fa-close"></i> 关闭');
        options['btn'+options.btn.length] = function(index, layero){
        };
        js.layer.open(options);
    });
    //-------编辑操作--------
    $("#editOperation").click(function(){
        var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
        if(gr !== null && gr!==undefined){
            var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
            //alert("adasdas:"+grData.id);
            var options = {
                type: 2,
                maxmin: true,
                shadeClose: true,
                title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改数据权限配置',
                area: ["500px", "320px"],
                content: baseURL + 'sys/dataPermission_edit.html',
                btn: ['<i class="fa fa-check"></i> 确定'],
                success:function(layero, index){
                	var win = layero.find('iframe')[0].contentWindow;
                    win.vm.saveFlag = false;
                    win.vm.getInfo(grData.ID);
                    listselectCallback(index,win.vm.saveFlag);
                },
                btn1:function(index,layero){
                	var win = layero.find('iframe')[0].contentWindow;
                    var flag=false;
                    
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
        refresh:function(){
            $("#searchForm").submit();
        },
        reload: function (event) {
            $("#searchForm").submit();
        },
        getUserNoFuzzy:function(){
            getUserSelect("#userName",null,null);
        },
        getRoleNoFuzzy: function (event) {
            getRoleSelect("#roleName",null,null);
        },
        getMenuNoFuzzy: function (event) {
            getMenuSelect("#menuName",null,null);
        },
    }
});




function listselectCallback(index,rtn){
    rtn = rtn ||false;
    if(rtn){
        //操作成功，刷新表格
        vm.reload();
//      parent.layer.close(index);
        try {
        	parent.parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
        
    }
}