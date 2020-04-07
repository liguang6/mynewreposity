$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'WERKS', width: 60,align:'center' },
			{ label: '业务类型代码', name: 'BUSINESSCODE', index: 'BUSINESSCODE', width: 80,align:'center'  }, 			
			{ label: 'WMS业务分类', name: 'BUSINESSCLASS', index: 'BUSINESSCLASS', width: 90,align:'center'  }, 			
			{ label: 'WMS业务类型', name: 'BUSINESSTYPE', index: 'BUSINESSTYPE', width: 90,align:'center'  }, 			
			{ label: 'WMS业务类型名称', name: 'BUSINESSNAME', index: 'BUSINESSNAME', width: 100,align:'center'  }, 			
			{ label: '库存类型', name: 'SOBKZ', index: 'SOBKZ', width: 70,align:'center'  }, 			
			{ label: 'SAP过账标识', name: 'SYNFLAG', index: 'SYNFLAG', width: 80,align:'center', formatter: function(val, obj, row, act){
				return val=='0' ? '同步' : '异步';
			}}, 
			{ label: '是否需要审批', name: 'APPROVALFLAG', index: 'APPROVALFLAG', width: 90,align:'center', formatter: function(val, obj, row, act){
				return val=='0' ? '否' : (val=='X' ? '是' : '');
			}}, 
			{ label: '是否可超需求', name: 'OVERSTEPREQFLAG', index: 'OVERSTEPREQFLAG', width: 80,align:'center', formatter: function(val, obj, row, act){
				return val=='0' ? '否' : (val=='X' ? '是' : '');
			}}, 
			{ label: '是否可超虚拟库存', name: 'OVERSTEPHXFLAG', index: 'OVERSTEPHXFLAG', width: 80,align:'center', formatter: function(val, obj, row, act){
				return val=='0' ? '否' : (val=='X' ? '是' : '');
			}},
            {label: 'EWM条码共享', name: 'PUBLICWERKS', index: 'PUBLICWERKS', width: 100,align:'center'},
			{ label: '中转库', name: 'LGORT', index: 'LGORT', width: 50,align:'center'}, 	
			{ label: '排序号', name: 'SORTNO', index: 'SORTNO', width: 50,align:'center'}, 	
			{ label: '创建人', name: 'CREATOR', index: 'CREATOR', width: 60,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATEDATE', index: 'CREATEDATE', width: 80,align:'center'  },
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true },
		],
		rowNum : 15,
		//orderBy : "WERKS ASC"
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/plantbusiness/delById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增工厂业务类型配置',
	    		area: ["500px", "480px"],
	    		content: baseURL + "wms/config/wms_c_plant_business_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
//	    			var win = top[layero.find('iframe')[0]['name']];
//	    			win.vm.saveFlag = false;
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改工厂业务类型配置',
				area: ["500px", "480px"],
				content: baseURL + 'wms/config/wms_c_plant_business_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
					$(win.document.body).find("#werks").attr("readonly",true);
					$(win.document.body).find("#businessCode").attr("readonly",true);
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入工厂业务类型配置",
				area: ["880px", "520px"],
				content: baseUrl + "wms/config/wms_c_plant_business_upload.html",
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
	  $("#copyOperation").click(function(){
			var options = {
	    		type: 2,
	    		maxmin: true,
	    		shadeClose: true,
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>复制工厂业务类型配置',
	    		area: ["960px", "540px"],
	    		content: baseURL + "wms/config/wms_c_plant_business_copy.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
//	    			var win = top[layero.find('iframe')[0]['name']];
//	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
	    			var win = layero.find('iframe')[0].contentWindow;
					var flag=false;
					
					var btnSave= $("#save", layero.find("iframe")[0].contentWindow.document);
					$(btnSave).click();
					if(typeof listselectCallback == 'function'){
						console.log("win.vm.saveFlag",win.vm.saveFlag);
						listselectCallback(index,win.vm.saveFlag);
					}
	    		}
	    	};
	    	options.btn.push('<i class="fa fa-close"></i> 关闭');
	    	options['btn'+options.btn.length] = function(index, layero){
         };
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
		}
	}
});
function export2Excel(){
	var column=[
      	{ "title": '工厂代码', "data": 'WERKS',"class":'center' },
		{ "title": '业务类型代码', "data": 'BUSINESSCODE',"class":'center'  }, 
		{ "title": 'WMS业务分类', "data": 'BUSINESSCLASS',"class":'center'  }, 			
		{ "title": 'WMS业务类型', "data": 'BUSINESSTYPE',"class":'center'  }, 			
		{ "title": 'WMS业务类型名称', "data": 'BUSINESSNAME',"class":'center'  }, 			
		{ "title": '仓库特殊类型', "data": 'SOBKZ',"class":'center'  }, 
		{ "title": 'SAP同步过账标识', "data": 'SYNFLAGDESC',"class":'center'  }, 
		{ "title": '是否需要审批', "data": 'APPROVALFLAGDESC',"class":'center'  }, 
		{ "title": '是否可超需求', "data": 'OVERSTEPREQFLAGDESC',"class":'center'  }, 
		{ "title": '是否可超虚拟库存', "data": 'OVERSTEPHXFLAGDESC',"class":'center'  }, 
		{ "title": '排序号', "data": 'SORTNO', "class":'center'} 			
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/plantbusiness/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"工厂业务类型配置表");	
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