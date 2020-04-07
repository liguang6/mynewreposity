$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'werks', width: 80,align:'center' },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'whNumber', width: 80,align:'center' },
			{ label: '业务类型名称', name: 'BUSINESS_NAME_DESC', index: 'businessName', width: 80,align:'center'  }, 			
			{ label: '交接模式', name: 'HANDOVER_TYPE_DESC', index: 'handoverTypeDesc', width: 80,align:'center'  },
			{ label: '创建人', name: 'CREATOR', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATE_DATE', index: 'createDate', width: 80,align:'center'  },
			{ label: '最近更新人', name: 'EDITOR', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '最近更新时间', name: 'EDIT_DATE', index: 'createDate', width: 80,align:'center'  },	
			{ label: 'ID', name: 'ID', index: 'id', hidden:true },
			{ label: 'BUSINESS_NAME', name: 'BUSINESS_NAME', index: 'businessName', hidden:true },
			{ label: 'HANDOVER_TYPE', name: 'HANDOVER_TYPE', index: 'handoverType', hidden:true },
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
				    url: baseURL + "config/handoverType/delById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增交接模式配置',
	    		area: ["500px", "260px"],
	    		content: baseURL + "wms/config/wms_c_handover_type_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改交接模式配置',
				area: ["500px", "360px"],
				content: baseURL + 'wms/config/wms_c_handover_type_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
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
				title: "<i class='fa fa-upload' aria-hidden='true'>&nbsp;</i> 导入交接模式配置",
				area: ["580px", "500px"],
				content: baseUrl + "wms/config/wms_c_handover_type_upload.html",
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>复制交接模式配置',
	    		area: ["800px", "540px"],
	    		content: baseURL + "wms/config/wms_c_handover_type_copy.html",  
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
    data:{
    	werks:"",
    	whNumber: "",
    	warehourse:[],
    },
    created: function(){
    	this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
	}
});
function export2Excel(){
	var column=[
      	{"title":"工厂代码","class":"center","data":"WERKS"},
      	{"title":"仓库号","class":"center","data":"WH_NUMBER"},
      	{"title":"业务类型名称","class":"center","data":"BUSINESS_NAME_DESC"},
      	{"title":"交接模式","class":"center","data":"HANDOVER_TYPE"},
      	{"title":"创建人","class":"center","data":"CREATOR"},
      	{"title":"创建时间","class":"center","data":"CREATE_DATE"},
      	{"title":"最近更新人","class":"center","data":"EDITOR"},
      	{"title":"最近更新时间","class":"center","data":"EDIT_DATE"}
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/handoverType/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"交接模式配置");	
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