var mydata = [];
var dataRow = {};
var sourcedata=[];
var lastrow='';
var lastcell='';
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		order_no:"",
		zzj_no:"",
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		batch :'',
		error_data:[],
		check_exsist:false,
		machine_plan_head_id:0,
		quantity:0,
		zzj_pmd_items_id:0,
		is_edit:false,// 是否存在编辑数据(不存在：false)
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks : {
			handler:function(newVal,oldVal){
			  $.ajax({
				  url:baseURL+"masterdata/getUserWorkshopByWerks",
				  dataType : "json",
				  type : "post",
				  data : {
					  "WERKS":newVal,
					  "MENU_KEY":'ZZJMES_MACHINE_PLAN_MANAGE',
				  },
				  async: true,
				  success: function (response) { 
					  vm.workshop_list=response.data;
					  if(response.data && response.data.length && response.data.length>0){
						  vm.workshop = response.data[0]['code'];
						  vm.getBatchSelects();
  
						  $.ajax({
							  url:baseURL+"masterdata/getUserLine",
							  dataType : "json",
							  type : "post",
							  data : {
								  "WERKS":vm.werks,
								  "WORKSHOP":vm.workshop,
								  "MENU_KEY":'ZZJMES_MACHINE_PLAN_MANAGE',
							  },
							  async: true,
							  success: function (response) { 
								  vm.line_list=response.data;
								  if(response.data && response.data.length && response.data.length>0){
									  vm.line = response.data[0]['code'];
								  }else{
									  vm.line = '';
								  }
							  }
						  });	
  
					  }else{
						  vm.workshop = '';
					  }
				  }
			  });	
			}
		  },
		  workshop: {
			  handler:function(newVal,oldVal){
				  $.ajax({
					  url:baseURL+"masterdata/getUserLine",
					  dataType : "json",
					  type : "post",
					  data : {
						  "WERKS":vm.werks,
						  "WORKSHOP":newVal,
						  "MENU_KEY":'ZZJMES_MACHINE_PLAN_MANAGE',
					  },
					  async: true,
					  success: function (response) { 
						  vm.line_list=response.data;
						  if(response.data && response.data.length && response.data.length>0){
							  vm.line=response.data[0]['code'];
							  vm.getBatchSelects();
						  }else{
							  vm.line = '';
						  }
					  }
				  });	
			  }
		  }
	
	},
	methods: {
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#order_no",null,vm.getBatchSelects);
		},
		getBatchSelects:function(selectval){
			vm.order_no=$("#order_no").val();
			if($("#order_no").val()!=''){
				$.ajax({
					type : "post",
					dataType : "json",
					async : false,
					url :baseUrl+ "/zzjmes/common/getPlanBatchList",
					data : {
						"werks" : this.werks,
						"workshop" : this.workshop,
						"line" : this.line,
						"order_no":$("#order_no").val()
					},
					success:function(response){
						if(response.data!=null && response.data.length>0){
							vm.batch=response.data[0].batch;
							getBatchSelects(response.data,vm.batch,"#zzj_plan_batch",null);
						}
					}	
				});
			}
		},
		query:function(){

			if($("#order_no").val()==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			if($("#zzj_plan_batch").val()==''){
				js.showErrorMessage("请输入批次！");
				return;
			}
			vm.is_edit=false;
			jQuery('#dataGrid').GridUnload();  
			js.loading("正在加载数据,请您耐心等待...");
			$("#btnQuery").attr("disabled","disabled");
			var subcontracting_type= $("#subcontracting_type").multipleSelect('getSelects').join(",")
			$("#subcontracting_type_submit").val(subcontracting_type);
			$.ajax({
				url:baseUrl + "zzjmes/machinePlan/getMachinePlanList",
				data:{
					werks:$("#werks :selected").val(),
					workshop:$("#workshop :selected").val(),
					line:$("#line :selected").val(),
					order_no:$("#order_no").val(),
					zzj_plan_batch:$('#zzj_plan_batch').val(),
					zzj_no:$('#zzj_no').val(),
					assembly_position:$("#assembly_position").val(),
					process:$("#process").val(),
					plan_process:$("#plan_process").val(),
					machine:$("#machine").val(),
					status:$("#status").val(),
					product_order:$("#product_order").val(),
					process_flow:$("#process_flow").val(),
					accuracy_demand:$("#accuracy_demand").val(),
					subcontracting_type:$("#subcontracting_type_sumbit").val(),
					start_date:$("#start_date").val(),
					end_date:$("#end_date").val(),
				},
				type:"post",
				dataType:"json",
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
					   vm.showTable(resp.data);
					   if(resp.data.length==0){
						   js.showErrorMessage("未找到符合条件的数据！");
					   }
					}else{
						js.showErrorMessage("查询失败：" + resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnQuery").removeAttr("disabled");
				}
			});
		},
		save:function(){
			if($("#order_no").val()==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			if($("#zzj_plan_batch").val()==''){
				js.showErrorMessage("请输入批次！");
				return;
			}
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var addData=[];
			var updateData=[];
			//获取选中表格数据
			var ids=$("#dataGrid").jqGrid("getGridParam","selarrrow");
			if(ids.length == 0){
				alert("当前还没有任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);

				if(data["process"]=='' || data["assembly_position"]==''){
					js.showErrorMessage('第'+(Number(i)+1)+'行使用工序、装配位置不能为空');
					save_flag=false;
					return false;
				}		
				if(data["zzj_no"]=='' || data["process_flow"]==''){
					js.showErrorMessage('第'+(Number(i)+1)+'行零部件号、工艺流程不能为空！');
					save_flag=false;
					return false;
				}
				if(data["plan_process"]==null || data["plan_quantity"]==''){
					js.showErrorMessage('第'+(Number(i)+1)+'行计划工序、计划数量不能为空！');
					save_flag=false;
					return false;
				}	
				if(data["machine"]=='' || data["plan_date"]==''){
					js.showErrorMessage('第'+(Number(i)+1)+'行机台、计划日期不能为空！');
					save_flag=false;
					return false;
				}
				
				delete data["actions"]; 
				console.log("rn",$("#dataGrid").getCell(ids[i],'rn'));
				data.row_no=$("#dataGrid").getCell(ids[i],'rn');
				if(data["operate_type"]=='00'){
					data.prod_quantity='0';
					addData.push(data);
				}
				if(data["operate_type"]=='01'){
					var prod_quantity=Number(data["prod_quantity"]);
					var plan_quantity=Number(data["plan_quantity"]);
					if(prod_quantity>plan_quantity){
						js.showErrorMessage('第'+(Number(i)+1)+'行 计划数量不能小于已生产数量：'+prod_quantity);
						return false;
					}
					updateData.push(data);
				}				
			}
			if(addData.length==0 && updateData.length==0){
				alert("没有编辑数据，无法保存！");
				return false;
			}
			js.loading("正在保存数据,请您耐心等待...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/machinePlan/save",
				data:{
					werks:$("#werks :selected").val(),
					werks_name:$("#werks :selected").text(),
					workshop:$("#workshop :selected").val(),
					workshop_name:$("#workshop :selected").text(),
					line:$("#line :selected").val(),
					line_name:$("#line :selected").text(),
					order_no:$("#order_no").val(),
					zzj_plan_batch:$('#zzj_plan_batch').val(),
					add_list:JSON.stringify(addData),
					modify_list:JSON.stringify(updateData)
				},
				type:"post",
				dataType:"json",
				success:function(resp){
					js.closeLoading();
					vm.showTable(resp.data);
					dataGridMorePaste();
					if(resp.code == 0){
						vm.is_edit=false;
					    modify_list=[];
					    add_list=[];
						js.showMessage("保存成功!");
						vm.query();
					}else{
						alert("保存失败：" + resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnSave").removeAttr("disabled");
				}
			});
		},
		del:function(){	
			var delData=[];
			var ids=$('#dataGrid').jqGrid('getGridParam','selarrrow');
			if(ids.length == 0){
				alert("当前还没有任何行项目数据！");
				return false;
			}
			js.confirm('你确认要删除数据吗？',function(){
				var machine_plan_head_id=0;
				for (var i = 0; i < ids.length; i++) {	
					var data = $("#dataGrid").jqGrid("getRowData",ids[i]);
					if(data.id==0){
						$('#dataGrid').dataGrid('delRowData',ids[i]);
						continue;
					}
					if(data.status=='2' || data.status== '3'){
						js.showErrorMessage("第"+$("#dataGrid").getCell(ids[i],'rn')+"行:生产中及已完成状态的机台加工计划不允许删除！");
						return;
					}
					machine_plan_head_id=data.machine_plan_head_id;
					data.operate_type='02';
					delete data["actions"]; 
					delete data["assembly_position"]; 
					delete data["process_flow"]; 
					delete data["process"]; 
					delete data["zzj_plan_batch"];
					delete data["quantity"];
					delete data["zzj_name"];
					delete data["status_desc"];
					delete data["creator"];
					delete data["create_date"];
					delete data["init_plan_quantity"];
					delete data["init_plan_process"];
					delete data["init_machine"];
					delete data["machine_plan_head_id"];
					delData.push(data);
				}
				js.loading("正在删除数据,请您耐心等待...");
				$("#btnDel").attr("disabled","disabled");
				console.log("delData",delData);
				$.ajax({
					url:baseUrl + "zzjmes/machinePlan/del",
					data:{
						machine_plan_head_id:machine_plan_head_id,
						delete_list:JSON.stringify(delData),
					},
					type:"post",
					dataType:"json",
					success:function(resp){
						js.closeLoading();
						if(resp.code == 0){
							for(var i in ids){
								$('#dataGrid').dataGrid('delRowData',''+ids[i]+'');	
							}
							js.showMessage("删除成功！");
						}else{
							alert("删除失败：" + resp.msg);
						} 
					},
					complete:function(XMLHttpRequest, textStatus){
						$("#btnDel").removeAttr("disabled");
					}
				});
			});
		},
		exp:function(){
			$("#exportForm").submit();	
		},	
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
		        colModel: [	
		        	{label:"<a id='addRow' href='#' onClick='vm.addRow({})' title='新增'><i class='fa fa-plus-square'></i>&nbsp;</a>",width:40,align:"center",name:"actions", sortable:false, fixed:true, formatter: function(val, obj, row, act){
						var actions = [];
						// 生产中及已完成状态的机台加工计划不允许删除
						if(row.status!='2' && row.status!='3'){
							actions.push('<a href="#" id=\'del_'+obj.rowId+'\' onclick="delRow(\''+row.id+'\',\''+obj.rowId+'\')"><i class="fa fa-trash-o"></i></a>&nbsp;');
						}
						return actions.join('');
					}, editoptions: {defaultValue: 'new'}},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:false,editable:true,editrules:{required: true},},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>装配位置', name: 'assembly_position',index:"assembly_position", width: "150",align:"center",sortable:false,editable:true,editrules:{required: true},}, //frozen: true
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:true,editrules:{required: true},},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "100",align:"center",sortable:false,editable:true},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>工艺流程', name: 'process_flow',index:"process_flow", width: "170",align:"center",sortable:true,editable:true,editrules:{required: true},},
		            {label: '批次', name: 'zzj_plan_batch',index:"zzj_plan_batch", width: "45",align:"center",sortable:false,editable:false,},
		            {label: '车付数', name: 'quantity',index:"quantity", width: "55",align:"center",sortable:false,editable:false},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>计划工序</B></SPAN>', name: 'plan_process',index:"plan_process", width: "70",align:"center",sortable:false,
		            	editable:true,editrules:{required: true},
		            },
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>计划数量</B></SPAN>', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",
		            	sortable:false,editable:true,editrules:{required: true,number:true},},
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>机台</B></SPAN>', name: 'machine',index:"machine", width: "100",align:"center",sortable:false,editable:true,editrules:{required: true},editoptions:{
	            		dataInit:function(el){
		            		　 $(el).click(function(){
		            		　        getZZJMachineNoSelect(el,null,vm.werks,vm.workshop,vm.line);
		            		　 });
		            	},dataEvents:[
							{type:"click",fn:function(e){
								var val = this.value;
								var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');						
								//$("#dataGrid").jqGrid('setCell',rowId,"",val);  
							}},
						]}
		            },
		            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>计划日期</B></SPAN>', name: 'plan_date',index:"plan_date", width: "90",align:"center",editable:true,editrules:{required: true},
		            	editoptions:{
		            		dataInit:function(el){
		            		　 $(el).click(function(){
		            		　         WdatePicker();
		            		　 });
		            	},dataEvents:[
							{type:"click",fn:function(e){
								var val = this.value;
								var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');						
								//$("#dataGrid").jqGrid('setCell',rowId,"",val);  
							}},
						]}
		            },
		        	{label: 'SAP码', name: 'sap_mat',index:"sap_mat", width: "70",align:"center",sortable:false,editable:true},
		            {label: '生产工单', name: 'product_order',index:"product_order", width: "75",align:"center",sortable:false,editable:true},
		            {label: '状态', name: 'status_desc',index:"status_desc", width: "60",align:"center",sortable:false,formatter: function(val, obj, row, act){
						var status_desc='';
						if(row.status=='0') status_desc='未锁定';
						if(row.status=='1') status_desc='已锁定';
						if(row.status=='2') status_desc='生产中';
						if(row.status=='3') status_desc='已完成';
						return status_desc;
					}},
		            {label: '创建人', name: 'creator',index:"creator", width: "75",align:"center",sortable:false,},
		            {label: '创建时间', name: 'create_date',index:"create_date", width: "120",align:"center",sortable:false,},
		            {label:'ID',name:'id',hidden:true,formatter:"integer",formatter: function(val, obj, row, act){
						return row.id!=undefined ? val : 0;
					}},
		            {label:'状态值',name:'status',hidden:true,formatter:"String"},
		            {label:'操作类型',name:'operate_type',hidden:true,},
		            {label:'下料明细',name:'zzj_pmd_items_id',hidden:true,formatter:"String"},
		            {label:'机台计划抬头',name:'machine_plan_head_id',hidden:true,formatter:"String"},
		            {label:'生产数量',name:'prod_quantity',hidden:true,formatter:"String"},
		            {label:'初始计划工序',name:'init_plan_process',hidden:true,formatter:"String",formatter: function(val, obj, row, act){
						return row.plan_process;
					}},
					{label:'初始计划机台',name:'init_machine',hidden:true,formatter:"String",formatter: function(val, obj, row, act){
						return row.machine;
					}},
		            {label:'初始计划数量',name:'init_plan_quantity',hidden:true,formatter:"String",formatter:"String",formatter: function(val, obj, row, act){
						return row.plan_quantity!=undefined ? row.plan_quantity : 0;
					}},
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
					sourcedata =  $("#dataGrid").jqGrid('getRowData');
					if(sourcedata.length>0){
						vm.machine_plan_head_id=sourcedata[0].machine_plan_head_id;
					}else{
						vm.machine_plan_head_id=0;
					}
				},
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
				beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
					var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
					if (cm[i].name == 'cb') {
						$('#dataGrid').jqGrid('setSelection', rowid);
					}
					return (cm[i].name == 'cb');
				},
				onSelectAll: function(aRowids,status) {
			        if (status) {
			            var cbs = $("tr.jqgrow > td > input.cbox:disabled", $("#dataGrid")[0]);
			            cbs.removeAttr("checked");
			            $("#dataGrid")[0].p.selarrrow = $("#dataGrid").find("tr.jqgrow:has(td > input.cbox:checked)")
			                .map(function() { return this.id; }) // convert to set of ids
			                .get(); // convert to instance of Array
			        }
			    },
			    onSelectRow:function(rowid,status){
			    
			    	if(status == true){
			    		var cbs = $("tr.jqgrow > td > input.cbox:disabled", $("#dataGrid")[0]);
			            cbs.removeAttr("checked");
			            $("#dataGrid")[0].p.selarrrow = $("#dataGrid").find("tr.jqgrow:has(td > input.cbox:checked)")
			                .map(function() { return this.id; }) // convert to set of ids
			                .get();
			    	}	
			    },
				onCellSelect : function(rowid,iCol,cellcontent,e){
		   	 		// 编辑修改 不允许修改以下字段
			   	 	var data =  $("#dataGrid").jqGrid('getRowData', rowid);
					if(data.id!='0'){
				       $("#dataGrid").jqGrid('setCell', rowid, 'process', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'assembly_position', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'zzj_no', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'zzj_name', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'process', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'process_flow', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'sap_mat', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'product_order', '','not-editable-cell');
				       $("#dataGrid").jqGrid('setCell', rowid, 'plan_process', '','not-editable-cell');
				       // 已完成状态的加工计划不允许修改
					   if(data.status=='3'){
						   
					       $("#dataGrid").jqGrid('setCell', rowid, 'plan_quantity', '','not-editable-cell');
					       $("#dataGrid").jqGrid('setCell', rowid, 'machine', '','not-editable-cell');
					       $("#dataGrid").jqGrid('setCell', rowid, 'plan_date', '','not-editable-cell');
					   }
					   // 生产中 状态 不允许修改工序
					   if(data.status=='2'){
						   $("#dataGrid").jqGrid('setCell', rowid, 'machine', '','not-editable-cell');
					   }
					}
			   	},
				afterSaveCell:function(rowid, cellname, value, iRow, iCol){
					var rowData = $("#dataGrid").jqGrid('getRowData',rowid);
					if(value!=''){
						if(cellname=='plan_process'){
							
						}
						//生产中： 修改计划数量时不能小于已录入的产量数量
						if(cellname=='plan_quantity'){
							console.log(rowData.plan_quantity+"----------"+value);
						    if(rowData.status=='2'){
	                        	var prod_quantity=rowData.prod_quantity!=null ? parseInt(rowData.prod_quantity) : 0;
	                        	var plan_quantity=parseInt(value);
	                        	if(plan_quantity<prod_quantity){
	                        		alert("计划数量不能小于已生产数量："+prod_quantity);
									//$("#dataGrid").jqGrid('setCell',rowid,"plan_quantity",rowData.init_plan_quantity); 
	                        		//return false;
	                        	}
	                        }
						}
						//校验机台  带出机台绑定工序
						if(cellname=='machine'){
							var machineList=getMachineInfo(value);
						    if(machineList.length==0){
						    	alert(value+"：机台未维护！");
						    	$("#dataGrid").jqGrid('setCell',rowid,"machine",rowData.init_machine);
						    	return false;
						    }else{
						    	var plan_process=machineList[0].split("#")[1];
							    var process_flow=rowData.process_flow;
							    if(process_flow!=''){
							    	var strArr=process_flow.split("-");
							    	var isOk=false;
							    	strArr.forEach((item,index,array)=>{
							    		if(item==plan_process){
							    			isOk=true;
			                        	}
							    	});
							    	if(!isOk){
							    		alert("工艺流程不包含该机台绑定的计划工序！");
							    		if(rowData.init_plan_process!=undefined){
							    			$("#dataGrid").jqGrid('setCell',rowid,"plan_process",rowData.init_plan_process); 
							    			$("#dataGrid").jqGrid('setCell',rowid,"machine",rowData.init_machine);
							    			return false;
							    		}
							    	}
								    $("#dataGrid").jqGrid('setCell',rowid,"plan_process",plan_process);
		                        }else{
		                        	alert("请先录入工艺流程！");
		                        	if(rowData.init_plan_process!=undefined){
			                        	$("#dataGrid").jqGrid('setCell',rowid,"plan_process",rowData.init_plan_process); 
			                        	$("#dataGrid").jqGrid('setCell',rowid,"machine",rowData.init_machine);
		                        		return false;
		                        	}
		                        }
						    }
						}
					    dataRow = rowData;
						var filtered = sourcedata.filter(dofilter);
						if(filtered.length>0){
							alert(rowData.zzj_no+":数据重复！");
							return false;
						}
						if(rowData.process!='' && rowData.assembly_position!='' && rowData.zzj_no!=''
							&& rowData.plan_process!='' && rowData.plan_date!='' && rowData.machine!=''
								&& rowData.process_flow!=''){
							if(rowData.id!=0){
								var filtered = sourcedata.filter(function(item) {　　// 使用filter方法
									if( (dataRow.id == item.id) && 
										  (dataRow.plan_process != item.plan_process ||dataRow.machine != item.machine
										  || dataRow.plan_quantity != item.plan_quantity || dataRow.plan_date != item.plan_date)){
										$("#dataGrid").jqGrid('setCell',rowid,"operate_type","01"); 
										vm.is_edit=true;
								    }
							　　});
							}else{
								$("#dataGrid").jqGrid('setCell',rowid,"operate_type","00"); 
							}    
						}
					}
				},
				viewrecords: true,	
				shrinkToFit: false,
		        cellEdit:true,
				rownumbers: false,
		        cellurl:'#',
				cellsubmit:'clientArray',
		        showCheckbox:true,
		        multiselect: true,
		    });	
		},
		addRow:function(jsondata){
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			if(vm.is_edit){
				if(confirm('你确认要放弃已修改的数据吗？')){
					vm.is_edit=false;
				    vm.showTable(sourcedata);	
				}else{
					return false;
				}
			}
		    var rows = $("#dataGrid").jqGrid('getRowData');
		    var ids = $("#dataGrid").jqGrid('getDataIDs');
			if(ids.length==0){
				ids=[0];
			}
		    var rowid = Math.max.apply(null,ids);
		    var newrowid = rowid+1;
		    jsondata.zzj_plan_batch=(rows.length>0 ? rows[0].zzj_plan_batch : "");
		    jsondata.quantity=(rows.length>0 ? rows[0].quantity : "");
			jsondata.machine_plan_head_id=(rows.length>0 ? rows[0].machine_plan_head_id : "");
			jsondata.operate_type="00";
	        $("#dataGrid").jqGrid('addRow',{  
	            rowID : newrowid,  
	            initdata : jsondata,  
	            position :"first",  
	            useDefValues : true,  
	            useFormatter : true,  
	            addRowParams : {extraparam:{  
	            }}  
	        }); 
	        $("#dataGrid").jqGrid('setCell', newrowid, 'creator', '','not-editable-cell');
		    $("#dataGrid").jqGrid('setCell', newrowid, 'create_date', '','not-editable-cell');
		    dataGridMorePaste();
		},
		more: function(e){
			$("#moreGrid").html("")
			$.each(moredata,function(i,d){
				var tr=$("<tr index="+i+"/>");
				var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
				var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
				$(tr).append(td_1)
				$(tr).append(td_2)
				$("#moreGrid").append(tr)
			});
			layer.open({
			  type: 1,
			  title:'选择更多',
			  closeBtn: 1,
			  btn:['确认'],
			  offset:'t',
			  resize:true,
			  maxHeight:'300',
			  area: ['400px','400px'],
			  skin: 'layui-bg-green', //没有背景色
			  shadeClose: false,
			  content: "<div id='more_div'>"+$('#layer_more').html()+"</div>",
			  btn1:function(index){
				  var ckboxs=$("#more_div tbody").find("tr").find("input[type='checkbox']:checked");
				  var values=[];
				  $.each(ckboxs,function(i,ck){
					  var input=$(ck).parent("td").next("td").find("input")
					  if($(input).val().trim().length>0){
						  values.push($(input).val())
					  }					  
				  })
				  $(e).val(values.join(","))
			  }
			});
	  },
	}
});
function delRow(id,rowId){
	dataGridMorePaste();
	// 手动新增行删除
	if(id=='undefined'){
		js.confirm('你确认要删除这条数据吗？', 
			function(){
			   $('#dataGrid').dataGrid('delRowData',''+rowId+'');
			}
		);
	}else{ //导入记录数据删除 
		var delData=[];
		var data = $("#dataGrid").jqGrid("getRowData",rowId);
		data.operate_type='02';
		delete data["actions"]; 
		delData.push(data);
		js.confirm('你确认要删除这条数据吗？',function(){
			$.ajax({
				url:baseUrl + "zzjmes/machinePlan/del",
				data:{
					machine_plan_head_id:vm.machine_plan_head_id,
					delete_list:JSON.stringify(delData),
				},
				type:"post",
				dataType:"json",
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
						js.showMessage("删除成功！");
						$('#dataGrid').dataGrid('delRowData',''+rowId+'');
					}else{
						alert("删除失败：" + resp.msg);
					} 
				},
			});
		  }
	   )
	}
}
$(function () {
	var now = new Date(); 
	var nowTime = now.getTime() ; 
	var day = now.getDay();
	var oneDayTime = 24*60*60*1000 ; 
	//显示周一
	var MondayTime = nowTime - (day-1)*oneDayTime ; 
	//显示周日
	var SundayTime =  nowTime + (7-day)*oneDayTime ; 
	//初始化日期时间
	var monday = new Date(MondayTime);
	var sunday = new Date(SundayTime);
	$("#start_date").val(formatDate(monday));
	$("#end_date").val(formatDate(sunday));
	$("#subcontracting_type").multipleSelect('destroy').multipleSelect({
        selectAll: true,
    }).multipleSelect('uncheckAll');
	
	vm.showTable();
	
	dataGridMorePaste();
	
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#zzj_no",function(e){
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				vm.order_no=val_json.order_no;
				vm.batch = val_json.zzj_plan_batch;
				vm.zzj_no =val_json.zzj_no;
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
				vm.line = val_json.line;
				vm.zzj_pmd_items_id = val_json.pmd_item_id||0;
				console.log(vm.workshop+"-"+vm.line);
			}
			vm.query();
		}
	});
	$(document).on("click","#checkall",function(e){

		if ($(e.target).is(":checked")) {
			check_All_unAll("#moreGrid",true);	
		}
		if($(e.target).is(":checked")==false){
			check_All_unAll("#moreGrid",false);
		}
	});
	//粘贴
	$(document).on("paste","#more_div input[type='text']",function(e){
		setTimeout(function(){
			var tr=$(e.target).parent("td").parent("tr")	
			var index = parseInt($("#more_div tbody").find("tr").index(tr));
			console.info(index)
			var copy_text=$(e.target).val();		 
			var dist_list=copy_text.split(" ");
			if(dist_list.length <=0){
				return false;
			}
			$.each(dist_list,function(i,value){
				$("#more_div tbody").find("tr").eq(index+i).find("input").val(value)
			});	 
		},100)
	});
});

function getMachineInfo(machine_code){
	var machine=[];
	$.ajax({
		type: "POST",
	    url: baseUrl+"zzjmes/machinePlan/getMachineInfo",
	    dataType : "json",
		data : {
			machine_code:machine_code,
			werks:vm.werks,
			workshop:vm.workshop,
			line:vm.line,
		},
		async:false,
	    success: function(r){
	    	if(r.code == 0){
	    		machine=r.data;
			}
		}
	});
	return machine;
}
function dataGridMorePaste(){
	$('#dataGrid').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
//			if(startRow + i >= rows){
//				$("#newOperation").click();
//			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				//console.log('-->Row ' + parseInt(startRow) + '|Cell ' + (startCell + j) + "|" + vm.mydata.length)
//				if((startCell + j) === 3)vm.mydata[parseInt(startRow)-1].no=arr[i][j]
//				if((startCell + j) === 4)vm.mydata[parseInt(startRow)-1].material_no=arr[i][j]
//				if((startCell + j) === 6)vm.mydata[parseInt(startRow)-1].zzj_no=arr[i][j]
//				if((startCell + j) === 7)vm.mydata[parseInt(startRow)-1].zzj_name=arr[i][j]
//				if((startCell + j) === 8)vm.mydata[parseInt(startRow)-1].mat_description=arr[i][j]
//				if((startCell + j) === 9)vm.mydata[parseInt(startRow)-1].mat_type=arr[i][j]
//				if((startCell + j) === 10)vm.mydata[parseInt(startRow)-1].specification=arr[i][j]
//			
				
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid').jqGrid("saveCell", startRow + i, startCell + j);
				$(cell).html(arr[i][j]);
			}
		}
	});
}
//filter回调函数：页面端 校验修改的数据是否重复
function dofilter(element, index, array) {
  if( (dataRow.id && dataRow.id != element.id) && 
		  (dataRow.zzj_no && dataRow.zzj_no == element.zzj_no)
		  &&(dataRow.process && dataRow.process == element.process)
		  &&(dataRow.assembly_position && dataRow.assembly_position == element.assembly_position)
		  &&(dataRow.process_flow && dataRow.process_flow == element.process_flow)
		  &&(dataRow.plan_process && dataRow.plan_process == element.plan_process)
		  &&(dataRow.machine && dataRow.machine == element.machine)
		  &&(dataRow.plan_date && dataRow.plan_date == element.plan_date)
		  ){ 
    return true;
  }
  return false;
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