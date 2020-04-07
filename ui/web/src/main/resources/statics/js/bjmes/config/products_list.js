$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
			
			{ label: 'product_type_code', name: 'product_type_code', index: 'product_type_code', width: 80,align:'center',hidden:true  },
			{ label: '产品类别', name: 'product_type_name', index: 'product_type_name', width: 80,align:'center'  },
			{ label: '产品代码', name: 'product_code', index: 'product_code', width: 80,align:'center'},
			{ label: '产品名称', name: 'product_name', index: 'product_name', width: 80,align:'center'},
			
			{ label: '工位代码', name: 'process_code', index: 'process_code', width: 80,align:'center'},
			{ label: '工位名称', name: 'process_name', index: 'process_name', width: 80,align:'center'},
			{ label: '加工流程代码', name: 'process_flow_code', index: 'process_flow_code', width: 80,align:'center'},
			{ label: '加工流程名称', name: 'process_flow_name', index: 'process_flow_name', width: 80,align:'center'},
			{ label: '状态', name: 'status', index: 'status', width: 80,align:'center',formatter:function(cellval, obj, row){
				var html="";
				if(row.status=='0'){
					html= "<span>正常</span>"
				}
				if(row.status=='1'){
					html= "<span>停用</span>"
				}
				return html;
				
			}},
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'edit_date', index: 'edit_date', width: 80,align:'center'  },	
			{ label: 'parent_product_id', name: 'parent_product_id', index: 'parent_product_id', width: 80,align:'center',hidden:true  },
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
				    url: baseURL + "config/bjMesProducts/delById/"+grData.id,
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
	    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>新增产品',
	    		area: ["650px", "300px"],
	    		content: "bjmes/config/product_edit.html",  
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
				title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改产品',
				area: ["650px", "300px"],
				content: 'bjmes/config/product_edit.html',
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
	  
	  //组件
	  
	  $("#newComp").click(function(){
		  var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			 if(gr !== null && gr!==undefined){
				 var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				 var options = {
					type: 2,
					maxmin: true,
					shadeClose: true,
					title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>部件维护',
					area: ["650px", "400px"],
					content: 'bjmes/config/product_add.html',
					btn: ['<i class="fa fa-check"></i> 确定'],
					success:function(layero, index){
						var win = top[layero.find('iframe')[0]['name']];
						win.vm.saveFlag = false;
						win.vm.getInfo(grData.id);
					},
					btn1:function(index,layero){
						
						var win = top[layero.find('iframe')[0]['name']];
						win.vm.saveOrUpdate();
						/*var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
						$(btnSubmit).click();*/
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
		getProductNoFuzzy:function(){
			//模糊查找
			getBjProductNoSelect("#product",fn_cb_getProductCode);
			
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

function fn_cb_getProductCode(product){
	$("#product").val(product.product_code);
}
