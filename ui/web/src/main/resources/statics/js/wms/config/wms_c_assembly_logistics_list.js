$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '总装工厂代码', name: 'assemblyWerks', index: 'assemblyWerks', width: 80,align:'center' },
			{ label: '供货工厂代码', name: 'werksF', index: 'werksF', width: 80,align:'center'  }, 			
			{ label: '是否上wms', name: 'wmsFlagF', index: 'wmsFlagF', width: 80,align:'center',
				formatter: function(item, index){
    		    return item=='0' ? "否" : (item=='X' ? '是' : '');
	    	}  },
			{ label: '发货库位', name: 'lgortF', index: 'lgortF', width: 80,align:'center'  }, 			
			{ label: '库存类型', name: 'sobkz', index: 'sobkz', width: 80,align:'center'  },
			{ label: 'WMS过账移动类型', name: 'wmsMoveType', index: 'wmsMoveType', width: 80,align:'center'  },
			{ label: '过账标识', name: 'sapFlagF', index: 'sapFlagF', width: 80,align:'center',
				formatter: function(item, index){
					if(item=='00'){
						return "无需过账";
					}else if(item=='01'){
						return "实时过账";
					}else if(item=='02'){
						return "异步过账";
					}else{
						return "";
					}
	    	}  },
			{ label: 'SAP过账移动类型', name: 'sapMoveType', index: 'sapMoveType', width: 80,align:'center'  },
			{ label: '创建人', name: 'creator', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'createDate', index: 'createDate', width: 80,align:'center'  },
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true }
		],
		rowNum : 15
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/assemblylogistics/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增总装物流参数配置',
	    		area: ["500px", "320px"],
	    		content: baseUrl +"wms/config/wms_c_assembly_logistics_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
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
			 var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改总装物流参数配置',
				area: ["500px", "320px"],
				content: baseURL + 'wms/config/wms_c_assembly_logistics_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
	//-------导入操作--------
	  $("#importOperation").click(function(){
		  var options = {
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 批量粘贴新增",
				area: ["820px", "520px"],
				content: baseUrl + "wms/config/wms_c_assembly_logistics_upload.html",
				btn: ['<i class="fa fa-check"></i> 保存'],
				
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveUpload();
//					var saveUpload= $("#saveUpload", layero.find("iframe")[0].contentWindow.document);
//					$(saveUpload).click();
					// 保存成功 关闭窗口
					if(win.vm.saveFlag){
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
			getPlantNoSelect("#werks",null,null);
		},
	}
});
function export2Excel(){
	var column=[
      	{ "title": '总装工厂代码', "data": 'assemblyWerks', "class":'center' },
		{ "title": '供货工厂代码', "data": 'werksF', "class":'center'  }, 			
		{ "title": '是否上wms', "data": 'wmsFlagF', "class":'center'  },
		{ "title": '发货库位', "data": 'lgortF',"class":'center'  }, 			
		{ "title": '库存类型', "data": 'sobkz',"class":'center'  },
		{ "title": 'WMS过账移动类型', "data": 'wmsMoveType',"class":'center'  },
		{ "title": '过账标识', "data": 'sapFlagF',"class":'center'  },
		{ "title": 'SAP过账移动类型', "data": 'sapMoveType',"class":'center'  },
		{ "title": '录入人员', "data": 'creator',"class":'center'  }, 			
		{ "title": '录入时间', "data": 'createDate',"class":'center'  },
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/assemblylogistics/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	if(results[i].wmsFlagF=='0'){
		    		results[i].wmsFlagF='否';
		    	}else if(results[i].wmsFlagF=='X'){
		    		results[i].wmsFlagF='是';
		    	}
		    	
		    	if(results[i].sapFlagF=='00'){
		    		results[i].sapFlagF='无需过账';
		    	}else if(results[i].sapFlagF=='01'){
		    		results[i].sapFlagF='实时过账';
		    	}else if(results[i].sapFlagF=='02'){
		    		results[i].sapFlagF='异步过账';
		    	}
		    }
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"总装物流参数配置");	
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