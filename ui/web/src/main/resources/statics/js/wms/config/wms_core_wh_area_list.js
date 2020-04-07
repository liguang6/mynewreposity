$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '仓库代码', name: 'whNumber', index: 'whNumber', width: 70,align:'center' },
			{ label: '存储区代码', name: 'storageAreaCode', index: 'storageAreaCode', width: 90,align:'center'  }, 
		    { label: '存储区名称', name: 'areaName', index: 'areaName', width: 90,align:'center'  },
		    { label: '存储模式', name: 'storageModel', index: 'storageModel', width: 80,align:'center',formatter: function(item, index){
 		       var result='';
		       if(item=='00'){result='固定存储';}
		       if(item=='01'){result='随机存储';}
	    	   return result;
	    	   }  
		    },
		    { label: '入库规则', name: 'putRule', index: 'putRule', width: 100,align:'center',formatter: function(item, index){
	 		       var result='';
			       if(item=='00'){result='空仓位';}
			       if(item=='01'){result='添加至现有仓位';}
			       if(item=='02'){result='一般存储区';}
		    	   return result;
		    	   }    
		    },
		    { label: '混储', name: 'mixFlag', index: 'mixFlag', width: 60,align:'center',formatter: function(item, index){
	 		       var result='';
			       if(item=='0'){result='否';}
			       if(item=='X'){result='是';}
		    	   return result;
		    	   }   
		    },
		    { label: '库容检查', name: 'storageCapacityFlag', index: 'storageCapacityFlag', width: 70,align:'center',formatter: function(item, index){
	 		       var result='';
			       if(item=='0'){result='否';}
			       if(item=='X'){result='是';}
		    	   return result;
		    	   }  },
		    { label: '自动上架', name: 'autoPutawayFlag', index: 'autoPutawayFlag', width: 70,align:'center',formatter: function(item, index){
	 		       var result='';
			       if(item=='0'){result='否';}
			       if(item=='X'){result='是';}
		    	   return result;
		    	   }
		    },
		    { label: '自动补货', name: 'autoReplFlag', index: 'autoReplFlag', width: 80,align:'center',formatter: function(item, index){
	 		       var result='';
			       if(item=='0'){result='否';}
			       if(item=='X'){result='是';}
		    	   return result;
		    	   }
		    },
		    { label: '重量检查', name: 'checkWeightFlag', index: 'checkWeightFlag', width: 80,align:'center' ,formatter: function(item, index){
	 		       var result='';
			       if(item=='0'){result='否';}
			       if(item=='X'){result='是';}
		    	   return result;
		    	   }
		    },
		    { label: '楼层', name: 'floor', index: 'floor', width: 60,align:'center'},
		    { label: '坐标', name: 'coordinate', index: 'coordinate', width: 80,align:'center'  },
		    { label: '状态', name: 'status', index: 'status', width: 60,align:'center'  ,formatter: function(item, index){
	 		       var result='';
			       if(item=='00'){result='启用';}
			       if(item=='01'){result='停用';}
		    	   return result;
		    }},
		    { label: '空储位搜索顺序', name: 'binSearchSequence', index: 'binSearchSequence', width: 80,align:'center'  ,formatter: function(item, index){
	 		       var result='';
			       if(item=='00'){result='按储位编码排序';}
			       if(item=='01'){result='按储位坐标排序';}
		    	   return result;
		    }},
			{ label: '创建人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id' ,hidden:true},
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
				    url: baseURL + "config/wharea/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增仓库存储区配置',
	    		area: ["520px", "450px"],
	    		content: baseURL + "wms/config/wms_core_wh_area_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改仓库存储区配置',
				area: ["520px", "450px"],
				content: baseURL + 'wms/config/wms_core_wh_area_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
					$(win.document.body).find("#whNumber").attr("readonly",true);
					$(win.document.body).find("#areaCode").attr("readonly",true);
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入仓库存储区配置",
				area: ["820px", "520px"],
				content: baseUrl + "wms/config/wms_core_wh_area_upload.html",
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
	created: function(){
    	// 加载存储类型代码select
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getWhNoFuzzy:function(){
			getWhNoSelect("#whNumber",null,null);
		},
	}
});
function export2Excel(){
	var column=[
      	{"title":"仓库代码","class":"center","data":"whNumber"},
      	{"title":"存储类型代码","class":"center","data":"storageAreaCode"},
      	{"title":"存储区名称","class":"center","data":"areaName"},
      	{"title":"存储模式","class":"center","data":"storageModel"},
      	{"title":"入库规则","class":"center","data":"putRule"},
      	{"title":"是否混储","class":"center","data":"mixFlag"},
      	{"title":"是否库容检查","class":"center","data":"storageCapacityFlag"},
      	{"title":"是否自动上架","class":"center","data":"autoPutawayFlag"},
      	{"title":"是否自动补货","class":"center","data":"autoReplFlag"},
      	{"title":"重量检查","class":"center","data":"checkWeightFlag"},
      	{"title":"楼层","class":"center","data":"floor"},
      	{"title":"坐标","class":"center","data":"coordinate"},
      	{"title":"状态","class":"center","data":"status"},
      	{"title":"空储位搜索顺序","class":"center","data":"binSearchSequence"},
      	{"title":"创建人","class":"center","data":"editor"},
      	{"title":"创建时间","class":"center","data":"editDate"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/wharea/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[0];
	for(var i=0;i<results.length;i++){
		
		if(results[i].storageModel=="00"){
			results[i].storageModel="固定存储";
		}else{
			results[i].storageModel="随机存储";
		}
		
		if(results[i].putRule=="00"){
			results[i].putRule="空仓位";
		}else{
			results[i].putRule="添加至现有仓位";
		}
		
		if(results[i].mixFlag=="0"){
			results[i].mixFlag="否";
		}else{
			results[i].mixFlag="是";
		}
		
		if(results[i].storageCapacityFlag=="0"){
			results[i].storageCapacityFlag="否";
		}else{
			results[i].storageCapacityFlag="是";
		}
		
		if(results[i].autoPutawayFlag=="0"){
			results[i].autoPutawayFlag="否";
		}else{
			results[i].autoPutawayFlag="是";
		}
		
		if(results[i].autoReplFlag=="0"){
			results[i].autoReplFlag="否";
		}else{
			results[i].autoReplFlag="是";
		}
		
		if(results[i].checkWeightFlag=="0"){
			results[i].checkWeightFlag="否";
		}else{
			results[i].checkWeightFlag="是";
		}
		
		if(results[i].status=="00"){
			results[i].status="启用";
		}else{
			results[i].status="停用";
		}
		if(results[i].binSearchSequence=="00"){
			results[i].binSearchSequence="按储位编码排序";
		}else{
			results[i].binSearchSequence="按储位坐标排序";
		}
	}
	
	getMergeTable(results,column,rowGroups,"仓库存储类型配置");	
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
