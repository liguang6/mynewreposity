$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '小班组代码', name: 'CODE', index: 'CODE', width: 80,align:'center' },
			{ label: '小班组名称', name: 'NAME', index: 'NAME', width: 80,align:'center' },
            { label: '创建人', name: 'CREATE_BY', index: 'CREATE_BY', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'CREATE_DATE', index: 'CREATE_DATE', width: 80,align:'center'  },
			{ label: '编辑人', name: 'UPDATE_BY', index: 'UPDATE_BY', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'UPDATE_DATE', index: 'UPDATE_DATE', width: 80,align:'center'  },	
			{ label: 'DEPT_ID', name: 'DEPT_ID', index: 'DEPT_ID', hidden:true },
			{ label: 'PARENT_ID', name: 'PARENT_ID', index: 'PARENT_ID', hidden:true }
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
				    url: baseURL + "masterdata/workgroup/deleteDept",
				    data:{
				    	"DEPT_ID":grData.DEPT_ID
				    },
		            //contentType: "application/json",
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增小班组',
	    		area: ["350px", "280px"],
	    		content: "sys/masterdata/work_team_edit.html",  
	    		btn: ['<i class="fa fa-check"></i> 确定'],
	    		success:function(layero, index){
	    			var win = top[layero.find('iframe')[0]['name']];
	    			win.vm.saveFlag = false;
	    		},
	    		btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改小班组',
				area: ["350px", "280px"],
				content: 'sys/masterdata/work_team_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.DEPT_ID);
					//$(win.document.body).find("#WERKS").attr("readonly",true);
				},
				btn1:function(index,layero){
					var win = top[layero.find('iframe')[0]['name']];
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
    	WERKS:"",
    	whNumber: "",
    	warehourse:[],
    	workshoplist:[],
    	workgrouplist:[],
    	WORKSHOP:"",
    	WORKGROUP:""
    },
    created: function(){
    	this.WERKS = $("#WERKS").find("option").first().val();
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MASTERDATA_WORKGROUP",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.WORKSHOP=resp.data[0].CODE;
		        		 }
		        		 
		        	  }
		          })
			}
		},
		WORKSHOP:{

			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
		        	  data:{
		        		  "WERKS":vm.WERKS,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MASTERDATA_TEAM",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workgrouplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.WORKGROUP=resp.data[0].CODE;
		        		 }
		        		 
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
		parent.layer.close(index);
	}
}