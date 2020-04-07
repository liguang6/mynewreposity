$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'werks', index: 'werks', width: 60,align:'center' },
			{ label: '物料号', name: 'matnr', index: 'matnr', width: 80,align:'center'  }, 			
			{ label: '物料描述', name: 'maktx', index: 'maktx', width: 150,align:'center'  }, 			
			{ label: '满箱数量', name: 'fullBoxQty', index: 'fullBoxQty', width: 60,align:'center'}, 	
		    { label: '物流器具', name: 'ltWare', index: 'ltWare', width: 80,align:'center'  },
			{ label: '车型', name: 'carType', index: 'carType', width: 60,align:'center'  }, 			
			{ label: '生产工位', name: 'proStation', index: 'proStation', width: 80,align:'center'  }, 	
			{ label: '配送工位', name: 'disStation', index: 'disStation', width: 80,align:'center'  }, 			
			{ label: '配送地址', name: 'disAddrss', index: 'disAddrss', width: 80,align:'center'  }, 
			{ label: '模具编号', name: 'mouldNo', index: 'mouldNo', width: 80,align:'center'  }, 			
			{ label: '创建人', name: 'creator', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'createDate', index: 'createDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
		],
		rowNum : 15,
		
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/sample/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增物料物流参数配置',
	    		area: ["500px", "450px"],
	    		content: baseURL+"wms/config/wms_c_mat_lt_sample_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改物料物流参数配置',
				area: ["500px", "450px"],
				content: baseURL+'wms/config/wms_c_mat_lt_sample_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					$(win.document.body).find("#werks").attr("readonly",true);
					$(win.document.body).find("#matnr").attr("readonly",true);
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入物料物流参数配置",
				area: ["920px", "520px"],
				content: baseUrl + "wms/config/wms_c_mat_lt_sample_upload.html",
				btn: ['<i class="fa fa-check"></i> 保存'],
				
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var saveUpload= $("#saveUpload", layero.find("iframe")[0].contentWindow.document);
					$(saveUpload).click();
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
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","",null,null);
		},
	}
});
//function export2Excel(){
//	var column=[
//      	{ "title" : '工厂代码', "data": 'werks',"class":'center' },
//		{ "title" : '物料号', "data": 'matnr',"class":'center'  }, 			
//		{ "title" : '物料描述', "data": 'maktx',"class":'center'  }, 			
//		{ "title" : '满箱数量', "data": 'fullBoxQty', "class":'center'}, 	
//	    { "title" : '物流器具', "data": 'ltWare',"class":'center'  },
//		{ "title" : '车型', "data": 'carType',"class":'center'  }, 			
//		{ "title" : '生产工位', "data": 'proStation', "class":'center'  }, 	
//		{ "title" : '配送工位', "data": 'disStation', "class":'center'  }, 			
//		{ "title" : '配送地址', "data": 'disAddrss', "class":'center'  }		
//      ]	;
//	var results=[];
//	var params=$("#searchForm").serialize();
//	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
//	var l =params.replace(reg,'$1');
//	params=l;
//	$.ajax({
//		url:baseURL+"config/sample/list",
//		dataType : "json",
//		type : "post",
//		data : params,
//		async: false,
//		success: function (response) { 
//		    results=response.page.list;
//		}
//	})
//	var rowGroups=[];
//	getMergeTable(results,column,rowGroups,"物料物流参数配置表");	
//}
function export2Excel(){
	var column=[
		{ "title" : '工厂代码', "data": 'werks',"class":'center' },
		{ "title" : '物料号', "data": 'matnr',"class":'center'  }, 			
		{ "title" : '物料描述', "data": 'maktx',"class":'center'  }, 			
		{ "title" : '满箱数量', "data": 'fullBoxQty', "class":'center'}, 	
	    { "title" : '物流器具', "data": 'ltWare',"class":'center'  },
		{ "title" : '车型', "data": 'carType',"class":'center'  }, 			
		{ "title" : '生产工位', "data": 'proStation', "class":'center'  }, 	
		{ "title" : '配送工位', "data": 'disStation', "class":'center'  }, 			
		{ "title" : '模具编号', "data": 'mouldNo', "class":'center'  },
		{ "title" : '配送地址', "data": 'disAddrss', "class":'center'  }
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/sample/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"物料物流参数配置表");	
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