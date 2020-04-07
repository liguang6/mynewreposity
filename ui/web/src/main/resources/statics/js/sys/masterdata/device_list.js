$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [			
			{ label: '工厂', name: 'werks', index: 'werks', width: 80,align:'center' },
			
			{ label: '设备类型编码', name: 'deviceCode', index: 'deviceCode', width: 80,align:'center'  },
			{ label: '设备类型名称', name: 'deviceName', index: 'deviceName', width: 80,align:'center'  },
			{ label: '设备编码', name: 'machineCode', index: 'machineCode', width: 80,align:'center'  },
			{ label: '设备名称', name: 'machineName', index: 'machineName', width: 80,align:'center'  },
			{ label: '规格型号', name: 'specificationModel', index: 'specificationModel', width: 80,align:'center'},
			{ label: '状态', name: 'status', index: 'status', width: 80,align:'center',formatter:function(cellval, obj, row){
				var html="";
				if(row.status=='00'){
					html= "<span>正常</span>"
				}
				if(row.status=='01'){
					html= "<span>停用</span>"
				}
				return html;
				
			}},
			{ label: '备注', name: 'memo', index: 'memo', width: 80,align:'center'},
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'editDate', index: 'editDate', width: 80,align:'center'  },	
			{ label: 'ID', name: 'id', index: 'id', hidden:true }
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
				    url: baseURL + "masterdata/device/deleteById?id="+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增设备',
	    		area: ["650px", "300px"],
	    		content: "sys/masterdata/device_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改设备',
				area: ["650px", "300px"],
				content: 'sys/masterdata/device_edit.html',
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
    	WERKS:"",
    	DEVICE_CODE:"",
    	MACHINE_CODE:"",
    	STATUS:"",
    	warehourse:[],
    	workshoplist:[],
    	linelist:[],
    	machinelist:[],
    	WORKSHOP:"",
    	LINE:""
    },
    created: function(){
    	this.WERKS = $("#WERKS").find("option").first().val();
	},
	watch:{
		
	},
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},
		getDeviceTypeNoFuzzy:function(){
  			//模糊查找设备类型
  			getDeviceTypeNoSelect("#DEVICE_CODE",null,null,fn_cb_getDeviceCode);
  			
		},
		getMachineList:function(){
			vm.$nextTick(function(){//数据渲染后调用	
			$.ajax({
	        	  url:baseUrl + "masterdata/device/getDeviceList",
	        	  data:{
	        		  "WERKS":vm.WERKS,
	        		  "DEVICE_CODE":$("#DEVICE_CODE").val(),
	        		  "MENU_KEY":"MASTERDATA_DEVICE",
	        		  "ALL_OPTION":'X'
	        	  },
	        	  success:function(resp){
	        		 vm.machinelist = resp.data;
	        		 /*if(resp.data.length>0){
	        			 vm.MACHINE_CODE=resp.data[0].MACHINE_CODE;
	        		 }*/
	        		 
	        	  }
	          })
			})
		
		}
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

function fn_cb_getDeviceCode(devicecode){
	vm.DEVICE_CODE=devicecode.DEVICECODE;
	vm.getMachineList();
}