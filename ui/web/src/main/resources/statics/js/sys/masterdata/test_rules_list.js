$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [		
            { label: '工厂', name: 'WERKS', index: 'WERKS', width: 100,align:'center'  },
            { label: '车间名称', name: 'WORKSHOP_NAME', index: 'WORKSHOP_NAME', width: 100,align:'center'  },
            { label: '订单区域代码', name: 'ORDER_AREA_CODE', index: 'ORDER_AREA_CODE', width: 100,align:'center',hidden:true  },
			{ label: '订单区域名称', name: 'ORDER_AREA_NAME', index: 'ORDER_AREA_NAME', width: 100,align:'center'  },
			
			{ label: '开始数量', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',
				formatter:function(val, obj, row, act){
					var from_str = ""
	            	var ruleJson = jQuery.parseJSON(val);
	            	for(var i in ruleJson){
	            		from_str += ruleJson[i].from + '<hr/>';
	            	}
	            	return from_str.substring(0,from_str.length-5);
				}	
			},
			{ label: '截至数量', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',
				formatter:function(val, obj, row, act){
					var from_str = ""
	            	var ruleJson = jQuery.parseJSON(val);
	            	for(var i in ruleJson){
	            		from_str += ((ruleJson[i].to===''||ruleJson[i].to==='0')?'-':ruleJson[i].to) + '<hr/>';
	            	}
	            	return from_str.substring(0,from_str.length-5);
				}	
			},
			{ label: '抽样规则', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',
				formatter:function(val, obj, row, act){
					var from_str = ""
	            	var ruleJson = jQuery.parseJSON(val);
	            	for(var i in ruleJson){
	            		from_str += ruleJson[i].dec + '<hr/>';
	            	}
	            	return from_str.substring(0,from_str.length-5);
				}	
			},
			{ label: '生产抽样数', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',
				formatter:function(val, obj, row, act){
					var from_str = ""
	            	var ruleJson = jQuery.parseJSON(val);
	            	for(var i in ruleJson){
	            		from_str += ruleJson[i].prducu + '<hr/>';
	            	}
	            	return from_str.substring(0,from_str.length-5);
				}	
			},
			{ label: '品质抽样数', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',
				formatter:function(val, obj, row, act){
					var from_str = ""
	            	var ruleJson = jQuery.parseJSON(val);
	            	for(var i in ruleJson){
	            		from_str += ruleJson[i].qc + '<hr/>';
	            	}
	            	return from_str.substring(0,from_str.length-5);
				}	
			},
			{ label: '备注', name: 'MEMO', index: 'MEMO', width: 100,align:'center'  },
			{ label: '编辑人', name: 'EDITOR', index: 'EDITOR', width: 80,align:'center',hidden:true  }, 			
			{ label: '编辑时间', name: 'EDIT_DATE', index: 'EDIT_DATE', width: 80,align:'center',hidden:true  },	
			{ label: '抽样规则', name: 'TEST_RULES', index: 'TEST_RULES', width: 100,align:'center',hidden:true  },
			{ label: 'ID', name: 'ID', index: 'ID', hidden:true }
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
				    url: baseURL + "masterdata/testRules/deleteById?id="+grData.ID,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增检验规则',
	    		area: ["700px", "500px"],
	    		content: "sys/masterdata/test_rules_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改检验规则',
				area: ["700px", "500px"],
				content: 'sys/masterdata/test_rules_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.ID);
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
    	WERKS:'',
    	WORKSHOP:'',
    	STANDARD_TYPE:'',
    	workshoplist:[]
    },
    created: function(){},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MASTERDATA_TEST_RULES",
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