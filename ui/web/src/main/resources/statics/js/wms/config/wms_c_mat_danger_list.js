$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'werks', index: 'werks', width: 80,align:'center' },
			{ label: '供应商代码', name: 'lifnr', index: 'lifnr', width: 100,align:'center' },
			{ label: '物料号', name: 'matnr', index: 'matnr', width: 80,align:'center'  }, 			
			{ label: '物料描述', name: 'maktx', index: 'maktx', width: 80,align:'center'  }, 			
			{ label: '是否是危化品', name: 'dangerFlag', index: 'dangerFlag', width: 80,align:'center',formatter: function(item, index){
        		    return item=='0' ? 
							'<span class="label label-danger">否</span>':
							'<span class="label label-success">是</span>';
		    	}
		    }, 	
		    { label: '保质期时长', name: 'goodDates', index: 'goodDates', width: 80,align:'center'  },
		    { label: '最小剩余保质期时长', name: 'minGoodDates', index: 'minGoodDates', width: 110,align:'center'  },
		    { label: '是否允许延长有效期', name: 'extendedEffectDate', index: 'extendedEffectDate', width: 110,align:'center' ,formatter: function(item, index){
    		    return item=='X' ? 
						'<span class="label label-danger">是</span>':
						'<span class="label label-success">否</span>';
	    	}
	    }, 	
            { label: '备注', name: 'memo', index: 'memo', width: 80,align:'center'  }, 	
            { label: '创建人', name: 'creator', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'createDate', index: 'editDate', width: 80,align:'center'  },		
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true },
			{ label: 'dangerFlag', name: 'dangerFlag', index: 'dangerFlag', hidden:true },
		],
		viewrecords: true,	
        rowNum: 15,
        rownumWidth: 25, 
		
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "config/matdanger/delById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增危化品物料',
	    		area: ["550px", "420px"],
	    		content: baseURL + "wms/config/wms_c_mat_danger_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改危化品物料',
				area: ["550px", "420px"],
				content: baseURL + 'wms/config/wms_c_mat_danger_edit.html',
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入危化品物料",
				area: ["820px", "520px"],
				content: baseUrl + "wms/config/wms_c_mat_danger_upload.html",
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
	}
});
function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"werks"},
      	{"title":"供应商代码","class":"center","data":"lifnr"},
      	{"title":"物料号","class":"center","data":"matnr"},
      	{"title":"物料描述","class":"center","data":"maktx"},
      	{"title":"危化品标示","class":"center","data":"dangerFlag"},
      	{"title":"保质期时长","class":"center","data":"goodDates"},
      	{"title":"最小剩余保质期时长","class":"center","data":"minGoodDates"},
      	{"title":"备注","class":"center","data":"memo"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/matdanger/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var i=0;i<results.length;i++){
		    	var dangerFlag=results[i].dangerFlag;
		    	if(dangerFlag=='X'){results[i].dangerFlag='是';}
			    if(dangerFlag=='0'){results[i].dangerFlag='否';}
		    }
		    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"危化品物料");	
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