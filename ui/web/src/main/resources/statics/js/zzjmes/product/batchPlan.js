var const_int_validate = /^[0-9]+[0-9]*$/;//整数正则表达式
var mydata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		search_werks: '',
		search_workshoplist: [],
		search_workshop: '',
		search_linelist: [],
		search_line: '',
		search_order_no: '',
		
		batchPlan:{
			id:0,werks:'',werks_name:'',workshop:'',workshop_name:'',line:'',line_name:'',batch:'',quantity:0,
			start_date:'',end_date:'',status:'00',change_reason:'',change_content:'',memo:'',order_no:'',
			production_qty:'',old_quantity:'',matainPlan:0,lastBatch:0
		},
		editBatchPlan:{
			id:0,werks:'',werks_name:'',workshop:'',workshop_name:'',line:'',line_name:'',batch:'',quantity:0,
			start_date:'',end_date:'',status:'00',change_reason:'',change_content:'',memo:'',order_no:'',
			production_qty:'',old_quantity:'',matainPlan:0,lastBatch:0
		},
		order:'',
	},
	created:function(){
		this.search_werks=$("#search_werks").find("option").first().val();
	},
	methods: {
		query: function () {
           	if(vm.search_werks==''){
         		js.showErrorMessage('请选择工厂！');
        		return false;
        	}
           	if(vm.search_workshop==''){
           		js.showErrorMessage('请选择车间！');
        		return false;
        	}
           	if(vm.search_line==''){
           		js.showErrorMessage('请选择线别！');
        		return false;
        	}
         	if(vm.search_order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
			
			js.loading();
			$("#btnQuery").val("查询...");	
			$("#btnQuery").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/batchPlan/getBatchList",
				data:{
					'search_werks': vm.search_werks,
					'search_workshop': vm.search_workshop,
					'search_line': vm.search_line,
					'search_order_no': $("#search_order_no").val(),
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					mydata = [];
					$("#dataGrid").jqGrid('clearGridData');
					mydata= response.data;
					$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid'); 
					if(response.code == 0){
						
					}else{
						//$("#dataGrid").jqGrid('setGridParam',{data: mydata,datatype:'local'}).trigger('reloadGrid');
						js.showErrorMessage("查询失败," + response.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnQuery").val("查询");	
					$("#btnQuery").removeAttr("disabled");
					js.closeLoading();
				}
			});
			
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		getOrderNoFuzzy:function(){
			if($("#search_werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#search_order_no",null,fn_cb_getOrderInfo_search,$("#search_werks"));
		},
		btnAdd: function(){
			vm.batchPlan = {
				id:0,werks:'',werks_name:'',workshop:'',workshop_name:'',line:'',line_name:'',batch:'',quantity:0,
				start_date:'',end_date:'',status:'00',change_reason:'',change_content:'',memo:'',order_no:'',
				production_qty:'',old_quantity:'',matainPlan:0,lastBatch:0 
			}
			vm.order = '';
			layer.open({
				type : 1,
				offset : '50px',
				skin : 'layui-layer-molv',
				title : "新增批次计划",
				area : [ '520px', '450px' ],
				shade : 0,
				shadeClose : true,
				content : jQuery("#addLayer"),
				btn : [ '确定' ,	'取消'],
				btn1 : function(index) {
					return vm.addBatchPlan()
				},
				btn2 : function(index) {
					layer.close(index);
					$("#btnConfirm").removeAttr("disabled");
				}
			});
		},
		update: function(event,id){
			$.each(mydata, function(index, row) {
				if(row.id === id){
					vm.editBatchPlan = {
							id:row.id,werks:row.werks,werks_name:'',workshop:row.workshop,workshop_name:row.workshop_name,line:row.line,
							line_name:row.line_name,batch:row.batch,quantity:row.quantity,
							start_date:row.start_date,end_date:row.end_date,status:row.status,change_reason:row.change_reason,
							change_content:row.change_content,memo:row.memo,order_no:row.order_no,
							production_qty:row.production_qty,old_quantity:row.quantity,matainPlan:0,lastBatch:0,
							orderDesc:row.order_desc,
						}
					$("#edit_start_date").val(row.start_date);
					$("#edit_end_date").val(row.end_date);
				}
			})
			layer.open({
				type : 1,
				offset : '50px',
				skin : 'layui-layer-molv',
				title : "修改批次计划",
				area : [ '520px', '450px' ],
				shade : 0,
				shadeClose : true,
				content : jQuery("#editLayer"),
				btn : [ '确定' ,	'取消'],
				btn1 : function(index) {
					return vm.updateBatchPlan()
				},
				btn2 : function(index) {
					layer.close(index);
					$("#btnConfirm").removeAttr("disabled");
				}
			});
		},
		addBatchPlan : function() {
			if(vm.batchPlan.werks === ''){
				alert('请选择工厂！')
				return false
			}
			if(vm.batchPlan.workshop === ''){
				alert('请选择车间！')
				return false
			}
			if(vm.batchPlan.line === ''){
				alert('请选择线别！')
				return false
			}
			if(vm.batchPlan.order_no === '' || vm.batchPlan.order_no.length <= 1 ){
				alert('请选择订单！')
				return false
			}
			if(vm.batchPlan.quantity === '' || Number(vm.batchPlan.quantity) <=0 ){
				alert('请输入大于0的车付数！')
				return false
			}
			if($("#start_date").val() === ''){
				alert('请输入计划开始日期！')
				return false
			}
			if($("#end_date").val() === ''){
				alert('请输入车付数！')
				return false
			}
			
			$.ajax({
				url:baseURL+"zzjmes/batchPlan/addBatchPlan",
				dataType : "json",
				type : "post",
				data : {
					"werks": vm.batchPlan.werks,
					"werks_name": $('#werks').find("option:selected").data("name"),
					"workshop": vm.batchPlan.workshop,
					"workshop_name": $('#workshop').find("option:selected").text(),
					"line": vm.batchPlan.line,
					"line_name": $("#line").find("option:selected").text(),
					"batch": vm.batchPlan.batch,
					"quantity": vm.batchPlan.quantity,
					"start_date": $("#start_date").val(),
					"end_date": $("#end_date").val(),
					"status": vm.batchPlan.status,
					"order_no":vm.batchPlan.order_no,
					"memo":vm.batchPlan.memo,
				},
				async: true,
				success: function (response) {
					if(response.code == "0"){
						js.showMessage("新增计划批次成功！");
						vm.query();
					}else{
						js.showErrorMessage("新增计划批次失败：" + response.msg);
					}
				}
			})
		},
		updateBatchPlan: function(){
			if(vm.editBatchPlan.quantity === '' || Number(vm.editBatchPlan.quantity) <=0 ){
				alert('请输入大于0车付数！')
				return false
			}
			if($("#edit_start_date").val() === ''){
				alert('请输入计划开始日期！')
				return false
			}
			if($("#edit_end_date").val() === ''){
				alert('请输入结束日期！')
				return false
			}
			$.ajax({
				url:baseURL+"zzjmes/batchPlan/editBatchPlan",
				dataType : "json",
				type : "post",
				data : {
					"id": vm.editBatchPlan.id,
					"quantity": vm.editBatchPlan.quantity,
					"start_date": $("#edit_start_date").val(),
					"end_date": $("#edit_end_date").val(),
					"status": vm.editBatchPlan.status,
					"change_reason":vm.editBatchPlan.change_reason,
					"change_content": vm.editBatchPlan.change_content,
					"old_quantity": vm.editBatchPlan.old_quantity,
					"memo":vm.editBatchPlan.memo,
				},
				async: true,
				success: function (response) {
					if(response.code == "0"){
						js.showMessage("修改计划批次成功！");
						vm.query();
					}else{
						js.showErrorMessage("修改计划批次失败：" + response.msg);
					}
				}
			})
		},
		deleteBatchPlan: function(event,id){
			js.confirm('确定要删除选中的记录？', function(){
				var row = null;
				$.each(mydata, function(index, d) {
					if(d.id === id){
						row = d;
					}
				})
				var isExsist = false ;
				$.ajax({
					url : baseURL+"zzjmes/batchPlan/getMachinePlanByBatch",
					dataType : "json",
					data : {
						"werks": row.werks,
						"workshop": row.workshop,
						"line": row.line,
						"batch": row.batch,
						"order_no": row.order_no,
					},
					async : false,
					error : function(response) {
						alert(response.message)
					},
					success : function(response) {
						if(response.data.length>0){
							isExsist = true;
						}
					}
				});
				
				if(isExsist){
					alert("机台加工计划已存在，不允许删除");
					return false;
				}
				
				$.ajax({
					url:baseURL+"zzjmes/batchPlan/deleteBatchPlan",
					dataType : "json",
					type : "post",
					data : {
						"id": id,
					},
					async: true,
					success: function (response) {
						if(response.code == "0"){
							js.showMessage("删除计划批次成功！");
							vm.query();
						}else{
							js.showErrorMessage("删除计划批次失败：" + response.msg);
						}
					}
				});
			});

		},
		//新增页面订单选择
		addPageOrder: function(){
			if(vm.batchPlan.werks === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order",null,fn_cb_getOrderInfo,$("#werks"));
		},
		//获取批次信息
		getOrderBatchList: function(){
			//判断订单、工厂、车间、线别是否为空
			if(undefined == vm.batchPlan.werks || vm.batchPlan.werks=='' ){
				return false;
			}
			if(undefined == vm.batchPlan.workshop || vm.batchPlan.workshop==''){
				return false;
			}
			if(undefined == vm.batchPlan.line || vm.batchPlan.line==''){
				return false;
			}
			if(undefined == vm.batchPlan.order_no || vm.batchPlan.order_no==''){
				return false;
			}
			vm.batchPlan.matainPlan=0;
			vm.batchPlan.lastBatch=0;
			$.ajax({
				url:baseUrl + "zzjmes/batchPlan/getOrderBatchList",
				type: "post",
				dataType:"json",
				async:false,
				data:{
					"werks": vm.batchPlan.werks,
					"workshop": vm.batchPlan.workshop,
					"line": vm.batchPlan.line,
					"order_no":vm.batchPlan.order_no,
				},
				success:function(response){
					if(response.code == '0'){	
						var datalist = response.data;
						if(undefined !=datalist&&datalist.length>0){
							for(var i = 0; i < datalist.length; i++) {
								vm.batchPlan.production_qty = Number(datalist[i]['production_qty']);
								vm.batchPlan.matainPlan = vm.batchPlan.matainPlan+Number(datalist[i]['quantity']);
								if(vm.batchPlan.lastBatch<Number(datalist[i]['batch'].substr(1))){
									vm.batchPlan.lastBatch = Number(datalist[i]['batch'].substr(1));
								}
							}
						}
					}
				}
			});
			//alert("批次号必须为："+(lastBatch+1));
			var new_batch = Number(vm.batchPlan.lastBatch)+1;
			var len = new_batch.toString().length;
			while(len < 3) {
				new_batch = "0" + new_batch;
				len++;
			}
			vm.batchPlan.batch = "P"+new_batch;
			
		}
	},
	watch: {
		search_werks: {
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"ZZJMES_BATCH_PLAN",
		        	  },
		        	  success:function(resp){
		        		 vm.search_workshoplist = resp.data;
		        		 if(resp.data && resp.data.length && resp.data.length>0){
							 vm.search_workshop = resp.data[0].CODE;
							 $.ajax({
								url:baseUrl + "masterdata/getUserLine",
								data:{
									"WERKS": vm.search_werks,
									"WORKSHOP": vm.search_workshop,
									"MENU_KEY": "ZZJMES_BATCH_PLAN",
								},
								success:function(resp){
								   vm.search_linelist = resp.data;
								   if(resp.data && resp.data.length && resp.data.length>0){
									   vm.search_line = resp.data[0].CODE;
								   }else{
										vm.search_line = '';
							   	   }
								   
								}
							})
		        		 }else{
							vm.search_workshop = '';
						 }
		        		 
		        	  }
		          })
			}
		},
		search_workshop: {
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserLine",
		        	  data:{
		        		  "WERKS": vm.search_werks,
		        		  "WORKSHOP": newVal,
		        		  "MENU_KEY": "ZZJMES_BATCH_PLAN",
		        	  },
		        	  success:function(resp){
		        		 vm.search_linelist = resp.data;
		        		 if(resp.data && resp.data.length && resp.data.length>0){
		        			 vm.search_line=resp.data[0].CODE;
		        		 }else{
							vm.search_line = '';
						 }
		        		 
		        	  }
		          })
			}
		},
	    'batchPlan.werks': function(newVal){
			 $.ajax({
	        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
	        	  data:{
	        		  "WERKS":newVal,
	        		  "MENU_KEY":"ZZJMES_BATCH_PLAN",
	        	  },
	        	  success:function(resp){
	        		 vm.batchPlan.workshoplist = resp.data;
	          		 if(resp.data && resp.data.length && resp.data.length>0){
	        			 vm.batchPlan.workshop=resp.data[0].CODE;
	        		 }
	        	  }
	          })
	          vm.getOrderBatchList();
	    },
	    'batchPlan.workshop': function(newVal){
			 $.ajax({
	        	  url:baseUrl + "masterdata/getUserLine",
	        	  data:{
	        		  "WERKS": vm.batchPlan.werks,
	        		  "WORKSHOP": newVal,
	        		  "MENU_KEY": "ZZJMES_BATCH_PLAN",
	        	  },
	        	  success:function(resp){
	        		 vm.batchPlan.linelist = resp.data;
	        		 if(resp.data && resp.data.length && resp.data.length>0){
	        			 vm.batchPlan.line=resp.data[0].CODE;
	        		 }
	        	  }
	          })
	          vm.getOrderBatchList();
	    },
	    'batchPlan.line': function(newVal){
	          vm.getOrderBatchList();
	    },
	    'batchPlan.order_no': function(newVal){
	          vm.getOrderBatchList();
	    },
	    'batchPlan.quantity': function(newVal){
	    	//校验批次数量
	    	if(!const_int_validate.test(newVal)){
	    		alert("请输入正整数！");
	    		vm.batchPlan.quantity = 0;
	    		return false;
	    	}
    		if(Number(vm.batchPlan.matainPlan)+Number(newVal)>Number(vm.batchPlan.production_qty)){
    			alert("计划数量不能大于订单工厂生产数量，本次输入的计划数必须小于等于："+(Number(vm.batchPlan.production_qty)-Number(vm.batchPlan.matainPlan)));
    			vm.batchPlan.quantity = Number(vm.batchPlan.production_qty)-Number(vm.batchPlan.matainPlan);
    			return false;
    		}
	    },
	    'editBatchPlan.quantity': function(newVal){
	    	//校验批次数量
	    	if(!const_int_validate.test(newVal)){
	    		alert("请输入正整数！");
	    		vm.editBatchPlan.quantity = 0;
	    		return false;
	    	}
			vm.editBatchPlan.matainPlan=0;
			$.ajax({
				url:baseUrl + "zzjmes/batchPlan/getOrderBatchList",
				type: "post",
				dataType:"json",
				async:false,
				data:{
					"werks": vm.editBatchPlan.werks,
					"workshop": vm.editBatchPlan.workshop,
					"line": vm.editBatchPlan.line,
					"order_no":vm.editBatchPlan.order_no,
				},
				success:function(response){
					if(response.code == '0'){	
						var datalist = response.data;
						if(undefined !=datalist&&datalist.length>0){
							for(var i = 0; i < datalist.length; i++) {
								vm.editBatchPlan.matainPlan = vm.editBatchPlan.matainPlan+Number(datalist[i]['quantity']);
							}
						}
					}
				}
			});
    		if(Number(vm.editBatchPlan.matainPlan)+Number(newVal)-Number(vm.editBatchPlan.old_quantity)>Number(vm.editBatchPlan.production_qty)){
    			alert("计划数量不能大于订单工厂生产数量，本次输入的计划数必须小于等于："+(Number(vm.editBatchPlan.production_qty)-Number(vm.editBatchPlan.matainPlan)+Number(vm.editBatchPlan.old_quantity)));
    			vm.editBatchPlan.quantity = Number(vm.editBatchPlan.production_qty)-Number(vm.editBatchPlan.matainPlan)+Number(vm.editBatchPlan.old_quantity);
    			return false;
    		}
	    },
	}
});

$(function () {
	fun_ShowTable();
});

function fn_cb_getOrderInfo(order){
	vm.batchPlan.orderDesc = order.order_name+" "+order.bus_type_code+" "+order.order_qty + "台";
	vm.batchPlan.production_qty = order.order_qty;
	vm.batchPlan.order_no = order.order_no;
	vm.order = order.order_no;
}

function fn_cb_getOrderInfo_search(order){
	vm.search_order_no = order.order_no;
}

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
		columnModel: [
			{label:'order_no',name:'order_no',hidden:true},
			{ label: '工厂', name: 'werks', align:'center', index: 'werks', width: 65 }, 
			/*{ label: '工厂名称', name: 'werks_name', align:'center', index: 'werks_name', width: 120 }, */
    		{ label: '车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 80 }, 	
    		{ label: '线别', name: 'line_name', align:'center', index: 'line_name', width: 65 }, 
    		{ label: '订单', name: 'order_desc', align:'center', index: 'order_desc', width: 220 }, 			
    		{ label: '批次', name: 'batch', align:'center',  index: 'batch', width: 70 }, 	
    		{ label: '车付数', name: 'quantity', align:'center', index: 'quantity', width: 70 }, 			
    		{ label: '开始日期', name: 'start_date', align:'center',  index: 'start_date', width: 90 }, 	
    		{ label: '结束日期', name: 'end_date', align:'center',  index: 'end_date', width: 90 }, 	
    		{ label: '状态', name: 'status', align:'center', index: 'status', width: 70 ,
    			formatter:function(val, obj, row, act){
    				switch(val) {
	    				case '00':
	    			        return '未开始'
	    			        break;
	    				case '01':
	    			        return '生产中'
	    			        break;
	    				case '02':
	    			        return '已完成'
	    			        break;
	    			}
    			}, 			
    		},
    		{ label: '维护人', name: 'editor', align:'center', index: 'creator', width: 90 }, 			
    		{ label: '维护时间', name: 'edit_date', index: 'creat_date', width: 120 },
    		{ label: '操作', name: 'actions', width: 90 ,align:'center',formatter:function(val, obj, row, act){
    			var actions = [];
    			var link = "<i class='fa fa-pencil' onClick='vm.update(event,"+row.id+")' title='修改' style='font-size:16px;color:green;cursor: pointer;' >&nbsp;</i>";
				actions.push(link);
				actions.push("<i class='fa fa-trash-o' onClick='vm.deleteBatchPlan(event,"+row.id+")' title='删除' style='font-size:16px;color:red;cursor: pointer;' >&nbsp;</i>");
    		    return actions.join('');
    		    
    		}},
    		{ label: 'id', name: 'id', index: 'id', width: 80,hidden:true}, 
    		{ label: 'workshop', name: 'workshop', index: 'workshop', width: 80,hidden:true}, 
    		{ label: 'line', name: 'line', index: 'line', width: 80,hidden:true}, 
    		{ label: 'production_qty', name: 'production_qty', index: 'production_qty', width: 80,hidden:true},
    		{ label: 'change_content', name: 'change_content', index: 'change_content', width: 80,hidden:true},
    		{ label: 'change_reason', name: 'change_content', index: 'change_reason', width: 80,hidden:true},
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        orderBy:"creat_date desc",
        autowidth:true,
        multiselect: false,
    });
	//$("#dataGrid").jqGrid("setFrozenColumns");
}
