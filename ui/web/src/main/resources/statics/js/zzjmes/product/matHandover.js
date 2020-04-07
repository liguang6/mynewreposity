var mydata =[];
var lastrow,lastcell,lastcellname,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks: '',
		workshoplist: [],
		workshop: '',
		processList: [],
		workgroupList:[],
		zzj_pmd_items_id: '',
		order_no: '', 
		batchplanlist: [],
		zzj_plan_batch: '',
		zzj_no: '',
		handover_type:'',
		handoverTypeList:[],
		receive_process: '',
		deliver_process:'',
		receive_process_name: '',
		deliver_process_name:'',
		deliver_workgroup:'',
		receive_workgroup:'',
		receive_workgroup_name:'',
		deliver_workgroup_name:'',
		total_qty: 0,
		total_type: 0,
		show:true,
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
		//this.handover_type='01';
	},
	methods: {
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			vm.order_no='';
			vm.zzj_no='';
			vm.batchplanlist=[];
			vm.total_qty=0;
			vm.total_type=0;
		},
		getOrderNoFuzzy: function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order_no",null,fn_cb_getOrderInfo,"#werks");
		},
		getBatchSelects: function(){
			if(vm.order_no!=''){
				$.ajax({
					type : "post",
					dataType : "json",
					async : false,
					url :baseUrl+ "/zzjmes/common/getPlanBatchList",
					data : {
						"werks" : vm.werks,
						"workshop" : vm.workshop,
						//"line" : vm.line,
						"order_no": vm.order_no
					},
					success:function(response){
						vm.batchplanlist = response.data;
					}	
				});
			}
		},
		queryMatInfo: function(){
         	if(vm.order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
        	if(vm.zzj_plan_batch==''){
           		js.showErrorMessage('请选择批次！');
        		return false;
        	}
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
			$.ajax({
				url:baseUrl + "zzjmes/matHandover/getMatInfo",
				data:{
					"werks": vm.werks,
					"workshop": vm.workshop,
//					"line": vm.line,
					"order_no": vm.order_no,
					"handover_type": vm.handover_type,
					"zzj_plan_batch": vm.zzj_plan_batch,
					"zzj_no": vm.zzj_no,
					"zzj_pmd_items_id": vm.zzj_pmd_items_id,
					"deliver_process_name": $("#deliver_process").find("option:selected").text(),
					"receive_process_name": $("#receive_process").find("option:selected").text(),
					"deliver_workgroup_name": $("#deliver_workgroup").find("option:selected").text(),
				},
				type:"post",
				success:function(response){
					vm.zzj_no="";
					if(response.code==500){
						js.showErrorMessage(response.msg,5*1000);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							if(vm.handover_type=='01'){
							   js.showErrorMessage("未找到符合查询条件的数据或该零部件工艺流程不包含所选交接、交付工序！",5*1000);
							}
							if(vm.handover_type!='01'){
								js.showErrorMessage("未找到符合查询条件的数据！",5*1000);
							}
							return false;
						}
						addMat(response.data);
					}
				},
			});
		},
		btnSave: function(){
            if(vm.order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
        	if(vm.zzj_plan_batch==''){
           		js.showErrorMessage('请选择批次！');
        		return false;
        	}
        	$("#psw").val("");
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
        	
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			for(var i in rows){
				var data=rows[i];
				if(vm.handover_type=='01'){
					if(data.deliver_process_name==''){
						js.showErrorMessage("请选择交付工序！");
						return false;
					}
					if(data.receive_process_name==''){
						js.showErrorMessage("请选择接收工序！");
						return false;
					}
					if(data.deliver_process_name==data.receive_process_name){
						js.showErrorMessage("交付工序不能与接收工序相同！");
						return false;
					}
					data.receive_team='';
					data.receive_team_name='';
					data.receive_workgroup='';
					data.receive_workgroup_name='';
					data.deliver_team='';
					data.deliver_team_name='';
					data.deliver_workgroup='';
					data.deliver_workgroup_name='';
				}
				if(vm.handover_type!='01'){
					if(data.deliver_workgroup_name==''){
						js.showErrorMessage("请选择交付班组！");
						return false;
					}
					if(data.receive_workgroup_name==''){
						js.showErrorMessage("请选择接收班组！");
						return false;
					}
					if(data.receive_workgroup_name==data.deliver_workgroup_name){
						js.showErrorMessage("交付班组不能与接收班组相同！");
						return false;
					}
					if(vm.handover_type=='02'){
						data.receive_team=data.receive_workgroup;
						data.receive_team_name=data.receive_workgroup_name;
						data.receive_workgroup='';
						data.receive_workgroup_name='';
						data.deliver_team=data.deliver_workgroup;
						data.deliver_team_name=data.deliver_workgroup_name;
						data.deliver_workgroup='';
						data.deliver_workgroup_name='';
					}
					if(vm.handover_type=='03'){
						data.receive_team='';
						data.receive_team_name='';
						data.deliver_team='';
						data.deliver_team_name='';
					}
					data.receive_process='';
					data.receive_process_name='';
					data.delivery_process='';
					data.delivery_process_name='';
				}
				if(data.deliver_date=='' || data.deliver_user=='' || data.receive_user=='' || data.deliver_qty==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行:可编辑单元格数据不能为空！");
					return false;
				}
				delete data.EMPTY_COL;
			}
			/*layer.open({
        		type : 1,
        		offset : '50px',
        		skin : 'layui-layer-molv',
        		title : "交接确认",
        		area : [ '380px', '200px' ],
        		shade : 0,
        		shadeClose : false,
        		content : jQuery("#loginDiv"),
        		btn : [ '确定'],
        		btn1 : function(index) {
        			layer.close(index);
        			$.ajax({
        				type: "POST",
        			    url: baseUrl+"checkLogin",
        			    data: {
        			    	username: $("#username").val(),
        			    	password: $("#psw").val(),
        			    },
        			    dataType: "json",
        			    success: function(result){
        					if(result.code == 0){//登录成功*/
			js.loading("处理中...");
			$("#btnSave").html("保存中...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				type : "post",
				dataType: "json",
				async : true,
				url :baseUrl+ "zzjmes/matHandover/save",
				data : {
					"werks" : vm.werks,
					"werks_name": $('#werks_name').val(),
					"workshop" : vm.workshop,
					"workshop_name": $('#workshop_name').val(),
					"order_no" : vm.order_no,
					"zzj_plan_batch": vm.zzj_plan_batch,
					"handover_type":vm.handover_type,
					"matInfoList": JSON.stringify(rows),
				},
				success:function(response){
					$("#btnSave").html("保存");	
					$("#btnSave").removeAttr("disabled");
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.code==0){
						vm.clearTable();
						vm.total_qty=0;
						vm.total_type=0;
						js.showMessage("保存成功！"); 						        	
					}	
				}	
			});
        				/*	}else{
        						js.showErrorMessage("用户名不存在或密码错误！");
        					}
        				}
        			});
        		}
        	});*/
		},
	},
	watch: {
		werks: {
			handler:function(newVal,oldVal){
				$.ajax({
	        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
	        	  data:{
	        		  "WERKS":newVal,
	        		  "MENU_KEY":"ZZJMES_MAT_HANDOVER",
	        	  },
	        	  success:function(resp){
	        		 vm.workshoplist = resp.data;
	        		 if(resp.data.length>0){
	        			 vm.workshop=resp.data[0].CODE;
	        			 getHandoverRuleList();
	        		 } 
	        	  }
	          })
			}
		},
		workshop: {
			handler:function(newVal,oldVal){
				getHandoverRuleList();
		    }
		},
		deliver_process: {
			handler:function(newVal,oldVal){
				vm.deliver_process_name=$("#deliver_process :selected").text();
				if(vm.order_no!='' && vm.zzj_no!='' && vm.zzj_plan_batch!=''){
					vm.queryMatInfo();
				}
			}
		},
		receive_process: {
			handler:function(newVal,oldVal){
				vm.receive_process_name=$("#receive_process :selected").text();
				var rows=$("#dataGrid").jqGrid('getRowData');				
				$.each(rows,function(i,row){
					$("#dataGrid").jqGrid('setCell',Number(row.id), 'receive_process_name', vm.receive_process_name,'','');
					$("#dataGrid").jqGrid('setCell',Number(row.id), 'receive_process', vm.receive_process,'','');
				});
				if(vm.order_no!='' && vm.zzj_no!='' && vm.zzj_plan_batch!=''){
					vm.queryMatInfo();
				}
			}
		},
		receive_workgroup: {
			handler:function(newVal,oldVal){
				if(vm.handover_type!='01'){
					vm.receive_workgroup_name=$("#receive_workgroup :selected").text();
					var rows=$("#dataGrid").jqGrid('getRowData');				
					$.each(rows,function(i,row){
						$("#dataGrid").jqGrid('setCell',Number(row.id), 'receive_workgroup_name', vm.receive_workgroup_name,'','');
						$("#dataGrid").jqGrid('setCell',Number(row.id), 'receive_workgroup', vm.receive_workgroup,'','');
					});
				}
			}
		},
		deliver_workgroup: {
			handler:function(newVal,oldVal){
				if(vm.handover_type!='01'){
					vm.deliver_workgroup_name=$("#deliver_workgroup :selected").text();
					var rows=$("#dataGrid").jqGrid('getRowData');				
					$.each(rows,function(i,row){
						$("#dataGrid").jqGrid('setCell',Number(row.id), 'deliver_workgroup_name', vm.deliver_workgroup_name,'','');
						$("#dataGrid").jqGrid('setCell',Number(row.id), 'deliver_workgroup', vm.deliver_workgroup,'','');
					});
				}
			}
		},
		order_no: {
			handler:function(newVal,oldVal){
				if(vm.order_no!=''){
					vm.getBatchSelects();
				}
			}
		},
		handover_type:{
			handler:function(newVal,oldVal){
				if(vm.handover_type=='01'){
					vm.show=true;
					$("#dataGrid").setGridParam().hideCol("deliver_workgroup_name");
					$("#dataGrid").setGridParam().hideCol("receive_workgroup_name");
					$("#dataGrid").setGridParam().showCol("deliver_process_name");
					$("#dataGrid").setGridParam().showCol("receive_process_name");
				}else{
					vm.show=false;
					$("#dataGrid").setGridParam().hideCol("deliver_process_name");
					$("#dataGrid").setGridParam().hideCol("receive_process_name");
					$("#dataGrid").setGridParam().showCol("deliver_workgroup_name");
					$("#dataGrid").setGridParam().showCol("receive_workgroup_name");
				}
				if(vm.handover_type=='01'){
	    			getProcessList();
				}
				if(vm.handover_type=='02'){
					getTeamList();
				}
				if(vm.handover_type=='03'){
					getworkgroupList();
				}
			}
		}
	}
});
$(function () {
	fun_ShowTable();
	
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#zzj_no",function(e){
		if(vm.handover_type==''){
			js.showErrorMessage('请维护交接规则！');
			return false;
		}
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			if(vm.handover_type=='01'){
				if(vm.deliver_process==''){
	           		js.showErrorMessage('请选择交付工序！');
	           		vm.zzj_no="";
	        		return false;
	        	}
				if(vm.receive_process==''){
	           		js.showErrorMessage('请选择接收工序！');
	           		vm.zzj_no="";
	        		return false;
	        	}
			}
			if(vm.handover_type=='02' || vm.handover_type=='03'){
				if(vm.deliver_workgroup==''){
	           		js.showErrorMessage('请选择交付班组！');
	           		vm.zzj_no="";
	        		return false;
	        	}
			}
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				console.log("val_json",val_json);
				vm.order_no=val_json.order_no;
				vm.zzj_plan_batch = val_json.zzj_plan_batch;
				vm.zzj_no =val_json.zzj_no;
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
				vm.line = val_json.line;
				//vm.zzj_pmd_items_id = val_json.pmd_item_id||0;
                vm.queryMatInfo();
				
			}else{
				vm.queryMatInfo();
			}
		}
	});
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{label:'order_no',name:'order_no',hidden:true},
			{label:'zzj_pmd_items_id',name:'zzj_pmd_items_id',hidden:true},
			{label:'zzj_plan_batch',name:'zzj_plan_batch',hidden:true},
			{label:'init_deliver_qty',name:'init_deliver_qty',hidden:true,formatter:function(val, obj, row, act){
				return row.deliver_qty;
			}},
			{label:'prod_quantity',name:'prod_quantity',hidden:true},
			{label:'demand_quantity',name:'demand_quantity',hidden:true},
			{label:'already_handover_quantity',name:'already_handover_quantity',hidden:true},
			{label:'deliver_process',name:'deliver_process',hidden:true},
			{label:'receive_process',name:'receive_process',hidden:true},
			{label:'deliver_workgroup',name:'deliver_workgroup',hidden:true},
			{label:'receive_workgroup',name:'receive_workgroup',hidden:true},
			{label:'handover_type',name:'handover_type',hidden:true},
			{label:'id',name:'id', index: 'id',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				
   				return actions.join(",");
   			}},
   			{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 		
    		{ label: '零部件名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 150 }, 
			{ label: '交付工序', name: 'deliver_process_name', align:'center', index: 'deliver_process_name', width: 80 }, 	  	
    		{ label: '接收工序', name: 'receive_process_name', align:'center', index: 'receive_process_name', width: 80 },
    		{ label: '交付班组', name: 'deliver_workgroup_name', align:'center', index: 'deliver_workgroup_name', width: 80 }, 	  	
    		{ label: '接收班组', name: 'receive_workgroup_name', align:'center', index: 'receive_workgroup_name', width: 80 },
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>交付人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'deliver_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'deliver_user', align:'center', index: 'deliver_user', width: 90 ,editable:true,
    			editrules:{required: true},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　        doScan('deliver_user');
            		　 });
            }},sortable:false},
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>交接数量</b>', name: 'deliver_qty', align:'center', index: 'deliver_qty', width: 90 ,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   	   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 0,defaultValue:''}
   			,sortable:false	}, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>接收日期</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'receive_date\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'receive_date', align:'center', index: 'receive_date', width: 90,editable:true,
    			editrules:{required: true},
            	editoptions:{
            	   dataInit:function(el){
            		　 $(el).click(function(){
            		　         WdatePicker();
            	   });
            	}},sortable:false
            }, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>接收人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'receive_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'receive_user', align:"center",index: 'receive_user', width: 80,editable:true,
    			editrules:{required: true},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　      doScan('receive_user');
            		　 });
            	}},sortable:false
            },   		
    		{ label: '工艺流程', name: 'process_flow', align:'center',  index: 'process_flow', width: 120 }, 			
    		{ label: '使用工序', name: 'process', align:'center', index: 'process', width: 70 }, 	
    		{ label: '装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 120 }, 	
    		{ label: '订单', name: 'order_desc',align:"center", index: 'order_desc', width: 150 },
    		{ label: '批次', name: 'zzj_plan_batch',align:'center',  index: 'zzj_plan_batch', width: 70 }, 		
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,			
 	   	beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
			if(value==""){
				$("#dataGrid").jqGrid('setCell', rowid, cellname, '&nbsp;','editable-cell');
			}
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			if(cellname=='deliver_qty'){
				var field="交接数量";
				var able_qty= Number(row.init_deliver_qty);
				console.log(able_qty+"--"+value);
				if(Number(value)>Number(able_qty)){
					js.alert(field+"不能大于"+able_qty);
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
				    return;
				}
				if(value.trim()=="" ){
					js.alert(field+"不能为空！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					return;
				}
				if(value=="0" ){
					js.alert(field+"必须大于0！");
					$("#dataGrid").jqGrid('setCell', rowid, cellname, able_qty,'editable-cell');
					return;
				}
				vm.total_qty=vm.total_qty-Number(row.init_deliver_qty)+Number(value);	
			}		
		},
    });
}

function fn_cb_getOrderInfo(order){
	vm.order_no = order.order_no;
}

function addMat(matList){
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";";
	});	

    var rowid = Math.max.apply(null,ids);
    console.info("addedMatStr:"+addedMatStr);
	$.each(matList,function(i,row){	
		console.info(addedMatStr.indexOf(";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";"));
		if(addedMatStr.indexOf(";" + row.zzj_pmd_items_id+"-"+row.zzj_plan_batch +";") == -1){
			if(vm.handover_type=='01'){
				var process_flow_array=row.process_flow.split("-");
				if(("-"+row.process_flow+"-").indexOf("-"+$("#deliver_process :selected").text()+"-") <0){
					js.showErrorMessage(row.zzj_no+"-【"+row.process_flow+"】该零部件工艺流程不包含交付工序，无需交接！");
					return false;
				}
				console.log(row.process_flow+":"+$("#receive_process :selected").text());
				if(("-"+row.process_flow+"-").indexOf("-"+$("#receive_process :selected").text()+"-") <0){
					js.showErrorMessage(row.zzj_no+"-【"+row.process_flow+"】该零部件工艺流程不包含接收工序，无需交接！");
					return false;
				}
				if(Number(row.prod_quantity)==0){
					js.showErrorMessage(row.zzj_no+"-"+vm.zzj_plan_batch+"零部件交付工序未录入产量或产量为0,不能交接！");
					return false; // 结束循环
				}
			}
			if((Number(row.prod_quantity)-Number(row.already_handover_quantity))<=0){
				js.showErrorMessage(row.zzj_no+"-"+vm.zzj_plan_batch+"零部件剩余交接数量为0,不能交接！");
				return false; // 结束循环
			}
			var data = {};
			data.id = ++rowid;  			
			data.zzj_plan_batch = row.zzj_plan_batch;
			data.zzj_pmd_items_id = row.zzj_pmd_items_id;
			data.order_no = vm.order_no;
			data.order_desc = row.order_desc;
	    	data.zzj_no = row.zzj_no;
	    	data.zzj_name = row.zzj_name;
	    	data.handover_quantity = row.handover_quantity;
	    	data.assembly_position = row.assembly_position;
	    	data.process = row.process;
	    	data.process_flow = row.process_flow;	
	    	if(vm.handover_type=='01'){
		    	data.deliver_qty=Number(row.prod_quantity)-Number(row.already_handover_quantity);
	    	}else{
		    	data.deliver_qty=Number(row.demand_quantity)-Number(row.already_handover_quantity);
	    	}
	    	data.receive_date=formatDate(new Date());
	    	if(vm.handover_type=='01'){
		    	data.deliver_process = vm.deliver_process;	
		    	data.receive_process = vm.receive_process;
		    	data.deliver_process_name = vm.deliver_process_name;	
		    	data.receive_process_name = vm.receive_process_name;
	    	}
	    	if(vm.handover_type!='01'){
	    		data.deliver_workgroup = vm.deliver_workgroup;	
		    	data.receive_workgroup = vm.receive_workgroup;
		    	data.deliver_workgroup_name = vm.deliver_workgroup_name;	
		    	data.receive_workgroup_name = vm.receive_workgroup_name;
	    	}
	    	console.log("data",data);
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
	    	vm.total_type++;
	    	vm.total_qty+=Number(data.deliver_qty!=null ? data.deliver_qty : '0' );			
		}else{
			js.showMessage(row.zzj_no+"-"+row.zzj_plan_batch+"数据重复！");
			return false;
		}    	
	});	
}

function delMat(id){
	console.info(id)
	var data=$("#dataGrid").jqGrid('getRowData',id);
	vm.total_type--;
	vm.total_qty-=Number(data.deliver_qty!='' ? data.deliver_qty : '0');
	$("#dataGrid").delRowData(id)
}


function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');

	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];
	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			if(Number(row.id)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.id), e, cellvalue,'editable-cell');
			}
		})
	}
}
function getHandoverRuleList(){
	$.ajax({
    	url:baseUrl + "zzjmes/matHandover/getHandoverRuleList",
    	data:{
    		  "werks":vm.werks,
    		  "workshop":vm.workshop,
    	},
    	success:function(resp){	  
		  if(resp.code==0){
			  vm.handoverTypeList=resp.data;
			  vm.handover_type=resp.data[0].code;
			  if(vm.handover_type=='01'){
    			  getProcessList();
			  }
			  if(vm.handover_type=='02'){
				  getTeamList();
			  }
			  if(vm.handover_type=='03'){
				  getworkgroupList();
			  }
		  }	        		 
    	}
    })
}
function getProcessList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getWorkshopProcessList",
  	  data:{
  		  "WERKS": vm.werks,
  		  "WORKSHOP": vm.workshop,
  		  //"PROCESS_TYPE": '00' //自制工序
  	  },
  	  success:function(resp){
  		 vm.processList = resp.data;
  		 if(resp.data.length>0){
  			 vm.deliver_process = resp.data[0].PROCESS_CODE;
  			 vm.receive_process = resp.data[0].PROCESS_CODE;
  			 vm.deliver_process_name = resp.data[0].PROCESS_NAME;
  			 vm.receive_process_name = resp.data[0].PROCESS_NAME;
  		 }
  	  }
    })
}
function getworkgroupList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
  	  data:{
  		  "WERKS": vm.werks,
  		  "WORKSHOP": vm.workshop,
  		  "MENU_KEY":"ZZJ_MAT_HANDOVER",
		  "ALL_OPTION":'X'
	  },
  	  success:function(resp){
  		 if(resp.code==0){
  			vm.workgroupList = resp.data;
  			//vm.deliver_workgroup
  		 }
  	  }
    })
}
function getTeamList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getTeamListByWorkshop",
  	  data:{
  		  "WERKS": vm.werks,
  		  "WORKSHOP": vm.workshop,
  		  "MENU_KEY":"ZZJ_MAT_HANDOVER",
		  "ALL_OPTION":'X'
	  },
  	  success:function(resp){
  		 if(resp.code==0){
  			vm.workgroupList = resp.data;
  			//vm.deliver_workgroup
  		 }
  	  }
    })
}
//调用摄像头扫二维码
function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	$("#"+last_scan_ele).val(result);
    $("#"+last_scan_ele).trigger(e);
}