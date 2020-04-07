$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
        	{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 80,align:'center' },
			{ label: '仓库代码', name: 'WH_NUMBER', index: 'WH_NUMBER', width: 80,align:'center'  }, 		
        	{ label: '项目代码', name: 'PROJECT_CODE', index: 'PROJECT_CODE', width: 80,align:'center' },
        	{ label: '项目名称', name: 'PROJECT_NAME', index: 'PROJECT_NAME', width: 80,align:'center' },
			{ label: '安全库存', name: 'SAFE_STOCK', index: 'LGORT_NO', width: 80,align:'center'  },
			{ label: '最低库存', name: 'MIN_STOCK', index: 'LANGUAGENAME', width: 80,align:'center'  },
			{ label: '正常库存', name: 'NORMAL_STOCK', index: 'COUNTRY', width: 80,align:'center'  },
			{ label: '最高库存', name: 'MAX_STOCK', index: 'PROVINCE', width: 80,align:'center'  },
			
			{ label: '物料号', name: 'MATNR', index: 'MATNR', width: 80,align:'center'  },
			{ label: '物料描述', name: 'MAKTX', index: 'MAKTX', width: 80,align:'center'  },
			{ label: '单量', name: 'SINGLE_QTY', index: 'SINGLE_QTY', width: 80,align:'center'  },
			{ label: '供应商代码', name: 'LIFNR', index: 'LIFNR', width: 80,align:'center'  },
			{ label: '供应商名称', name: 'LIKTX', index: 'LIKTX', width: 80,align:'center'  },
			{ label: '库位', name: 'LGORT', index: 'LGORT', width: 80,align:'center'  },
			{ label: '责任人', name: 'PERSON', index: 'PERSON', width: 80,align:'center'  },
			
			{ label: '编辑人', name: 'EDITOR', index: 'EDITOR', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'EDIT_DATE', index: 'EDIT_DATE', width: 80,align:'center'  },
			{ label: 'HEADID', name: 'HEADID', index: 'HEADID' , hidden:true},
			{ label: 'ITEMID', name: 'ITEMID', index: 'ITEMID' , hidden:true},
		],
		viewrecords: true,
		rowNum: 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/engine/delById?HEADID="+grData.HEADID+"&ITEMID="+grData.ITEMID+"&PROJECT_CODE="+grData.PROJECT_CODE,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增配置信息',
	    		area: ["560px", "650px"],
	    		content: baseURL + "wms/config/wms_core_engine_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = layero.find('iframe')[0].contentWindow;
	    			console.log(win.vm)
	    			console.log(win.vm.saveFlag)
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
	    	options.btn.push('<i class="fa fa-close"></i> 关闭');
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改配置信息',
				area: ["560px", "450px"],
				content: baseURL + 'wms/config/wms_core_engine_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					var win = layero.find('iframe')[0].contentWindow;
	    			win.vm.saveFlag = false;
					win.vm.getInfo(grData.HEADID,grData.ITEMID);
					$(win.document.body).find("#WERKS").attr("readonly",true);
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
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#WERKS",null,null);
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#WH_NUMBER",null,null);
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