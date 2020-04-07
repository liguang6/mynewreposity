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
			{ label: '发出工厂', name: 'werksFrom', index: 'werksFrom', width: 80,align:'center' },
			{ label: '接收工厂', name: 'werksTo', index: 'werksTo', width: 80,align:'center' },
			{ label: '凭证类型', name: 'docType', index: 'docType', width: 80,align:'center' },
			{ label: '采购组', name: 'ekgrp', index: 'ekgrp', width: 80,align:'center' },
			{ label: '税码', name: 'taxCode', index: 'taxCode', width: 80,align:'center' },
			{ label: '事业部名称', name: 'buName', index: 'buName', width: 80,align:'center' },
			{ label: '接收采购组织', name: 'ekorg', index: 'ekorg', width: 90,align:'center' },
			{ label: '接收公司代码', name: 'bukrs', index: 'bukrs', width: 90,align:'center' },
			{ label: '客户代码', name: 'kunnr', index: 'kunnr', width: 100,align:'center' },
			{ label: '供应商代码', name: 'lifnr', index: 'lifnr', width: 100,align:'center' },
			{ label: '销售凭证类型', name: 'soDocType', index: 'soDocType', width: 90,align:'center' },
			{ label: '销售组织', name: 'soGroup', index: 'soGroup', width: 80,align:'center' },
			{ label: '分销渠道', name: 'soChannel', index: 'soChannel', width: 80,align:'center' },
			{ label: '产品组', name: 'productGroup', index: 'productGroup', width: 80,align:'center' },
			{ label: '装运点', name: 'shippingPoint', index: 'shippingPoint', width: 80,align:'center' },
			{ label: 'ID', name: 'id', index: 'id', hidden:true }
			],
		rowNum : 15,
		shrinkToFit:false,  
		
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
							url : baseURL + "config/steplinkage/delById?id="
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
	    		content: baseURL+"wms/config/wms_c_step_linkage_edit.html",  
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
				content: baseURL+'wms/config/wms_c_step_linkage_edit.html',
				btn: ['<i class="fa fa-check"></i>'+languageObj.CONFIRM],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					
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