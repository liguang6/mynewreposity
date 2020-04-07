$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂代码', name: 'WERKS', index: 'werks', width: 80,align:'center' },
			{ label: '仓库号', name: 'WH_NUMBER', index: 'whNumber', width: 80,align:'center' },
			{ label: '业务类型名称', name: 'BUSINESS_NAME_DESC', index: 'businessNameDesc', width: 80,align:'center'  }, 			
			{ label: '部门', name: 'DEPARTMENT', index: 'department', width: 80,align:'center'  },
			{ label: '工号', name: 'STAFF_NUMBER', index: 'staffNumber', width: 80,align:'center'  },
			{ label: '姓名', name: 'USER_NAME', index: 'userName', width: 80,align:'center'  },
            { label: '状态', name: 'STATUS', index: 'status', width: 80,align:'center'  },
            { label: '有效截止日期', name: 'END_DATE', index: 'status', width: 80,align:'center'  },
			{ label: '创建人', name: 'CREATOR', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATE_DATE', index: 'createDate', width: 80,align:'center'  },
			{ label: '编辑人', name: 'EDITOR', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'EDIT_DATE', index: 'createDate', width: 80,align:'center'  },	
			{ label: 'ID', name: 'ID', index: 'id', hidden:true },
			{ label: 'BUSINESS_NAME', name: 'BUSINESS_NAME', index: 'businessName', hidden:true },
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
				    url: baseURL + "config/handover/delById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增交接人员',
	    		area: ["500px", "380px"],
	    		content: baseURL + "wms/config/wms_c_handover_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改交接人员',
				area: ["500px", "380px"],
				content: baseURL + 'wms/config/wms_c_handover_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = layero.find('iframe')[0].contentWindow;
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
					$(win.document.body).find("#werks").attr("readonly",true);
				},
				btn1:function(index,layero){
					var win = layero.find('iframe')[0].contentWindow;
					var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
					$(btnSubmit).click();
					if(typeof listselectCallback == 'function'){
						console.log("win.vm.saveFlag",win.vm.saveFlag);
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
      	{"title":"部门","class":"center","data":"DEPARTMENT"},
      	{"title":"工号","class":"center","data":"STAFF_NUMBER"},
      	{"title":"姓名","class":"center","data":"USER_NAME"},
      	{"title":"状态","class":"center","data":"STATUS"},
      	{"title":'创建人', "data": 'CREATOR',"class":'center'}, 			
		{"title":'创建时间', "data": 'CREATE_DATE',"class":'center'},
		{"title":'最近更新人', "data": 'EDITOR', "class":'center'}, 			
		{"title":'最近更新时间', "data": 'EDIT_DATE',"class":'center'},	
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"config/handover/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
//		    for(var i=0;i<results.length;i++){
//		    	var status=results[i].status;
//		    	if(status=='X'){results[i].status='锁定';}
//			    if(status=='0'){results[i].status='开通';}
//		    }	    
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"交接人员配置");	
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