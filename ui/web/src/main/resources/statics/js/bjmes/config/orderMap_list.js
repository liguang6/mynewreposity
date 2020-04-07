$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
			
			{ label: '订单', name: 'order_no', index: 'order_no', width: 80,align:'center'},
			{ label: '工厂', name: 'werks', index: 'werks', width: 80,align:'center'  },
			{ label: '工厂名称', name: 'werks_name', index: 'werks_name', width: 80,align:'center'  },
			{ label: '车间', name: 'workshop', index: 'workshop', width: 80,align:'center'  },
			{ label: '车间名称', name: 'workshop_name', index: 'workshop_name', width: 80,align:'center',hidden:true  },
			{ label: '产品类别', name: 'product_type_name', index: 'product_type_name', width: 80,align:'center'  },
			
			{ label: '编辑人', name: 'editor', index: 'editor', width: 80,align:'center'  }, 			
			{ label: '编辑时间', name: 'edit_date', index: 'edit_date', width: 80,align:'center'  },
			{ label: '操作', name: 'actions', width: 60 ,align:'center',formatter:function(val, obj, row, act){
    			var actions = [];
    			var link = "<a href='#' onClick='vm.editOrderMap(\""+row.order_no+"\",\""+row.werks+"\",\""+row.workshop+"\",\""+row.product_type_code+"\",\""+row.werks_name+"\",\""+row.workshop_name+"\")' title='新增'><i class='fa fa-pencil'></i>&nbsp;</a> ";
				actions.push(link);
    		    return actions.join('');
    		}},	
			{ label: 'product_type_code', name: 'product_type_code', index: 'product_type_code', width: 80,align:'center',hidden:true  },
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
	  
	
	  
	 
});

var vm = new Vue({
	el:'#rrapp',
    data:{
    	werks:"",
    	
    	workshopList:[]
    	
    },
    created: function(){
    	this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks: {
			handler:function(newVal,oldVal){
				$.ajax({
	        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
	        	  data:{
	        		  "WERKS":newVal,
	        		  "MENU_KEY":"ZZJMES_MACHINE_PLAN_MANAGE",
	        	  },
	        	  success:function(resp){
	        		 vm.workshopList = resp.data;
	        		 
	        	  }
			  });
			

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
		getProductNoFuzzy:function(){
			//模糊查找
			getBjProductNoSelect("#product",fn_cb_getProductCode);
			
	  },
	  editOrderMap:function(order_no,werks,workshop,product_type_code,werks_name,workshop_name){

		var options = {
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: '',
			area: ["800px", "300px"],
			content: "bjmes/config/orderMap_edit.html",  
			btn: [''],
			success:function(layero, index){
				var win = top[layero.find('iframe')[0]['name']];
				win.vm.saveFlag = false;
				win.vm.getInfo(werks,workshop,order_no,product_type_code,werks_name,workshop_name);
			},
			btn1:function(index,layero){
				
			}
		};
		options.btn.push('<i class="fa fa-close"></i> 关闭');
		options['btn'+options.btn.length] = function(index, layero){
   
		 };
		 js.layer.open(options);
		
	  }
	}
});

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
