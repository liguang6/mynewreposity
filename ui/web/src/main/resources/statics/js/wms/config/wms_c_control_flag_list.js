//国际化取值
var smallUtil=new smallTools();
var array = new Array("FACTORYCODE", "WAREHOUSECODE", "MAKE_SURE_DEL",
		"DELETE_SUCCESS", "SELECT_DELETE", "PROMPT_MESSAGE", "CONFIRM", "ADD",
		"CLOSE", "UPDATE", "SELECT_EDIT", "CREATOR", "CREATE_DATE", "EDITOR",
		"EDIT_DATE","STATUS", "CONTROL_FLAG","CONTROL_FLAG","DESCS","CONTROL_FLAG_TYPE");
var languageObj = smallUtil.loadlanguageByPKeyAndLKey(array, "M");

$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: languageObj.WAREHOUSECODE, name: 'warehouseCode', index: 'warehouseCode', width: 80,align:'center' },
			{ label: languageObj.CONTROL_FLAG, name: 'controlFlag', index: 'controlFlag', width: 80,align:'center'  }, 			
			{ label: languageObj.CONTROL_FLAG_TYPE, name: 'controlFlagType', index: 'controlFlagType', width: 80,align:'center'  ,
				formatter: function(item, index){
	    		    return item=='00' ? "入库" : (item=='01' ? '出库' : '');}},
			{ label: languageObj.DESCS, name: 'des', index: 'des', width: 80,align:'center'  }, 	
			
			
			{ label: languageObj.EDITOR, name: 'editor', index: 'editor', width: 80,align:'center'  },
			{ label: languageObj.EDIT_DATE, name: 'editDate', index: 'editor', width: 80,align:'center'  },
			{ label: 'id', name: 'id', index: 'id', hidden:true },
		],
		rowNum : 15,
		
    });
	  // ------删除操作---------
	$("#deleteOperation").click(
			function() {
				var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');// 获取选中的行号
				if (gr != null && gr !== undefined) {
					layer.confirm(languageObj.MAKE_SURE_DEL, {
						icon : 3,
						title : languageObj.PROMPT_MESSAGE
					}, function(index) {
						var grData = $("#dataGrid").jqGrid("getRowData", gr)// 获取选中的行数据
						$.ajax({
							type : "POST",
							url : baseURL + "config/controlflag/delById?id="
									+ grData.id,
							contentType : "application/json",
							success : function(r) {
								if (r.code == 0) {
									js.showMessage(languageObj.DELETE_SUCCESS);
									vm.reload();
								} else {
									alert(r.msg);
								}
							}
						});
						layer.close(index);
					});
				} else
					layer.msg(languageObj.SELECT_DELETE, {
						time : 1000
					});
			});
	  //------新增操作---------
	  $("#newOperation").click(function(){
		   var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>'+languageObj.ADD,
	    		area: ["580px", "400px"],
	    		content: baseURL + "wms/config/wms_c_control_flag_edit.html",  
	    		btn: ['<i class="fa fa-check"></i>'+languageObj.CONFIRM],
	    		success:function(layero, index){
	    			var win = layero.find('iframe')[0].contentWindow;
	    			win.vm.saveFlag = false;
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
	    	options.btn.push('<i class="fa fa-close"></i>'+languageObj.CLOSE);
	    	options['btn'+options.btn.length] = function(index, layero){
	   
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>'+languageObj.UPDATE,
				area: ["580px", "400px"],
				content: baseURL + 'wms/config/wms_c_control_flag_edit.html',
				btn: ['<i class="fa fa-check"></i>'+languageObj.CONFIRM],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					$(win.document.body).find("#factoryCode").attr("readonly",true);
					$(win.document.body).find("#warehouseCode").attr("readonly",true);
				},
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						listselectCallback(index,win.vm.saveFlag);
					}
				}
			};
			options.btn.push('<i class="fa fa-close"></i>'+languageObj.CLOSE);
			js.layer.open(options);
		 }else{
			 layer.msg(languageObj.SELECT_EDIT,{time:1000});
		 }
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
			getPlantNoSelect("#factoryCode",null,null);
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#warehouseCode",null,null);
		},
	}
});
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