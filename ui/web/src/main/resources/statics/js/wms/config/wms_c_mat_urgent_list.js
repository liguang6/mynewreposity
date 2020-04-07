$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'werks', index: 'werks', width: 80,align:'center' },
			{ label: '物料号', name: 'matnr', index: 'matnr', width: 80,align:'center'  }, 			
			{ label: '物料描述', name: 'maktx', index: 'maktx', width: 80,align:'center'  }, 			
			{ label: '是否是紧急物料', name: 'urgentFlag', index: 'urgentFlag', width: 80,align:'center',formatter: function(item, index){
        		    return item=='X' ? "<span class='label label-danger'>是</span>" : '<span class="label label-success">否</span>';
		    	}
		    }, 	
		    { label: '有效开始日期', name: 'startDate', index: 'startDate', width: 80,align:'center'  },
			{ label: '有效结束日期', name: 'endDate', index: 'endDate', width: 80,align:'center'  }, 			
			{ label: '备注', name: 'memo', index: 'memo', width: 80,align:'center'  }, 			
			{ label: '创建人', name: 'creator', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'createDate', index: 'createDate', width: 80,align:'center'  },
			{ label: '编辑人', name: 'editor', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'editDate', index: 'createDate', width: 80,align:'center'  },			
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
			{ label: 'urgentFlag', name: 'urgentFlag', index: 'urgentFlag', hidden:true },
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
				    url: baseURL + "config/maturgent/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增紧急物料',
	    		area: ["500px", "360px"],
	    		content: baseURL + "wms/config/wms_c_mat_urgent_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改紧急物料',
				area: ["500px", "360px"],
				content: baseURL + 'wms/config/wms_c_mat_urgent_edit.html',
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入紧急物料",
				area: ["880px", "520px"],
				content: baseUrl + "wms/config/wms_c_mat_urgent_upload.html",
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
function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"werks"},
      	{"title":"物料号","class":"center","data":"matnr"},
      	{"title":"物料描述","class":"center","data":"maktx"},
      	{"title":"是否是紧急物料","class":"center","data":"urgentFlag"},
      	{"title":"有效开始日期","class":"center","data":"startDate"},
      	{"title":"有效结束日期","class":"center","data":"endDate"},
      	{"title":"备注","class":"center","data":"memo"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/maturgent/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	var urgentFlag=results[i].urgentFlag;
		    	if(urgentFlag=='X'){results[i].urgentFlag='是';}
			    if(urgentFlag=='0'){results[i].urgentFlag='否';}
		    }
		    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"紧急物料");	
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