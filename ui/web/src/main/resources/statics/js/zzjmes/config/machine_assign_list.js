$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '机台编码', name: 'machine_code', index: 'machine_code', width: 150,align:'center'  },
			{ label: '机台名称', name: 'machine_name', index: 'machine_name', width: 100,align:'center'  },
			{ label: '工厂', name: 'werks', index: 'werks', width: 100,align:'center'  },
			{ label: '车间', name: 'workshop', index: 'workshop', width: 100,align:'center'  },
			{ label: '线别', name: 'line', index: 'line', width: 100,align:'center'  },
			{ label: '设备类型', name: 'device_code', index: 'device_code', width: 100,align:'center'  },
			{ label: '设备类型名称', name: 'device_name', index: 'device_name', width: 100,align:'center'  },
			{ label: '设备规格', name: 'specification_model', index: 'specification_model', width: 100,align:'center'  },
			{ label: '工序名称', name: 'process_name', index: 'process_name', width: 100,align:'center'  },
			{ label: '工段', name: 'section', index: 'section', width: 100,align:'center'  },
			{ label: '班组', name: 'workgroup_name', index: 'workgroup_name', width: 100,align:'center'  },
			{ label: '小班组', name: 'team_name', index: 'team_name', width: 100,align:'center'  },
			{ label: '人员', name: 'username', index: 'username', width: 100,align:'center'  },
			{ label: '状态', name: 'status', index: 'status', width: 100,align:'center',formatter:function(cellval, obj, row){
				var html="";
				if(row.status=='00'){
					html= "<span>正常</span>"
				}
				if(row.status=='01'){
					html= "<span>停用</span>"
				}
				return html;
				
			}  },
			{ label: '备注', name: 'memo', index: 'memo', width: 100,align:'center'  },
			{ label: '创建人', name: 'creator', index: 'creator', width: 80,align:'center'  }, 			
			{ label: '创建时间', name: 'create_date', index: 'create_date', width: 80,align:'center'  },	
			{ label: 'werks_name', name: 'werks_name', index: 'werks_name', hidden:true },
			{ label: 'workshop_name', name: 'workshop_name', index: 'workshop_name', hidden:true  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true }
		],
		rowNum : 15,
		showCheckbox:true,
		shrinkToFit:false,
		autowidth:true
		
    });
  //------删除操作---------
	  $("#deleteOperation").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		  if (gr != null  && gr!==undefined) {
			  layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				  var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				  $.ajax({
					type: "POST",
				    url: baseURL + "zzjmes/machineAssign/deleteById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增机台',
	    		area: ["650px", "350px"],
	    		content: "zzjmes/config/machine_assign_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改机台',
				area: ["650px", "350px"],
				content: 'zzjmes/config/machine_assign_edit.html',
				btn: ['<i class="fa fa-check"></i> 确定'],
				success:function(layero, index){
					var win = top[layero.find('iframe')[0]['name']];
					win.vm.saveFlag = false;
					win.vm.getInfo(grData.id);
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
    	werks:'',
    	workshop:'',
    	workgroup:'',
    	line:'',
    	machine:'',
    	device_code:'',
    	device_name:'',
    	process:'',
    	username:'',
    	status:'',
    	
    	workshoplist:[],
    	workgrouplist:[],
    	linelist:[]
    },
    created: function(){
    	this.werks = $("#werks").find("option").first().val();
    },
	watch:{
		werks:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.workshop=resp.data[0].CODE;
		        		 }
		        		 
		        	  }
		          })
			}
		
		},
		workshop:{
			handler:function(newVal,oldVal){
				//班组
				 $.ajax({
		        	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
		        	  data:{
		        		  "WERKS":vm.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workgrouplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.workgroup=resp.data[0].CODE;
		        		 }
		        		 
		        	  }
		          })
		        //线别
		          $.ajax({
		        	  url:baseUrl + "masterdata/getUserLine",
		        	  data:{
		        		  "WERKS":vm.werks,
		        		  "WORKSHOP":newVal,
		        		  "MENU_KEY":"MACHINE_MNG",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.linelist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.line=resp.data[0].CODE;
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
		print: function () {
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){
				
				$("#labelList").val(JSON.stringify(saveData));
				$("#printButton").click();
			}
		}
	}
});
function export2Excel(){
	var column=[
      	{"title":"机台编码","class":"center","data":"machine_code"},
      	{"title":"机台名称","class":"center","data":"machine_name"},
      	{"title":"工厂","class":"center","data":"werks"},
      	{"title":"车间","class":"center","data":"workshop"},
      	{"title":"车间名称","class":"center","data":"workshop_name"},
      	{"title":"线别","class":"center","data":"line"},
      	{"title":"设备类型","class":"center","data":"device_code"},
      	{"title":"设备类型名称","class":"center","data":"device_name"},
      	{"title":"设备规格","class":"center","data":"specification_model"},
      	{"title":"工序名称","class":"center","data":"process_name"},
      	{"title":"工段","class":"center","data":"section"},
      	{"title":"班组","class":"center","data":"workgroup_name"},
      	{"title":"小班组","class":"center","data":"team_name"},
      	{"title":"状态","class":"center","data":"status"},
      	{"title":"备注","class":"center","data":"memo"}
      	
      ]	;
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"zzjmes/machineAssign/getMachineAssignList",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    for(var m=0;m<results.length;m++){
		    	if(results[m].status=='00'){
		    		results[m].status='正常'
		    	}
		    	if(results[m].status=='01'){
		    		results[m].status='停用'
		    	}
		    }
		}
	})
	var rowGroups=[];
	getMergeTable(results,column,rowGroups,"机台维护");	
}
function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		parent.layer.close(index);
	}
}