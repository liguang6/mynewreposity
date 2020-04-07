$(function(){

    $("#dataGrid").dataGrid({
        searchForm:$("#searchForm"),
        datatype : "local",
        colModel: [
            {label: '仓库号', name: 'WH_NUMBER',index:"MATNR", width: 200,align:"center"},
            {label: '储位代码',name: 'BIN_CODE',index:"BIN_CODE",width: 200,align:"center"},
            {label: '储位名称', name: 'BIN_NAME',index:"BIN_NAME", width: 150,align:"center" },           
            {label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true}//隐藏此列
        ],
        rowNum: 15,
		showCheckbox:true,
//        beforeSelectRow: function(){
//            $("#jqgridId").jqGrid('resetSelection');
//            return(true);
//        },
    });
    
    $("#printb").click(function(){
    	printLabel(true)
    });
    $("#prints").click(function(){
    	printLabel(false)
    });
    
  //-------导入操作--------
	  $("#importOperation").click(function(){
		  var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入储位",
				area: ["820px", "520px"],
				content: baseUrl + "wms/kn/wms_c_barcode_storage_upload.html",
				btn: ['<i class="fa fa-check"></i> 确认'],
				
	   			btn1:function(index,layero){
	   				var win = layero.find('iframe')[0].contentWindow;
	   				var saveUpload= $("#saveUpload", layero.find("iframe")[0].contentWindow.document);
	   				$(saveUpload).click();
	   				if(win.vm.saveFlag){
	   					$("#dataGrid").dataGrid("clearGridData");
   						let list = JSON.parse(win.vm.list);
   						$("#dataGrid").dataGrid('setGridParam', {
   						    datatype : 'local',
   						    data : list
   						}).trigger("reloadGrid");
	   					if(typeof listselectCallback == 'function'){
	   						listselectCallback(index,true);
	   					}
	   				}
	   			}
			};
			options.btn.push('<i class="fa fa-close"></i> 关闭');
			js.layer.open(options);
	  });
});

 var vm = new Vue({
     el:'#rrapp',
     data:{},
     methods: {
         refresh:function(){
             $("#searchForm").submit();
         },
         reload: function (event) {
             $("#searchForm").submit();
         },
         getPlantNoFuzzy:function(){
             getPlantNoSelect("#werks",null,null);
         },
         getVendorNoFuzzy:function(){
             getVendorNoSelect("#lifnr",null,null,$("werks").val());
         },
         query: function () {
        	 if($("#WH_NUMBER").val()){
      			js.loading();
     			vm.$nextTick(function(){
     				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
     				js.closeLoading();
     			})
        	 }else
        		 js.alert("请选择仓库号!")
 		},
     }
 });


function getSelRow(gridSelector){
    var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
    return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}

function listselectCallback(index,rtn){
    rtn = rtn ||false;
    if(rtn){
        vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
    }
}

function printLabel(flag){
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
	};
	if(saveData.length>0){
		let obj = {list:JSON.stringify(saveData),flag:flag}
		$("#labelList").val(JSON.stringify(obj));
		$("#printButton").click();
	}
}