var mydata =[];
var lastrow,lastcell,lastcellname,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks: '',
		workshoplist: [],
		useWorkshoplist:[],
		workshop: '',
		processList: [],
		zzj_pmd_items_id: '',
		order_no: '', //查询订单
		zzj_no: '',
		use_workshop: '',
		assembly_position:'',
		process: '',
		total_qty: 0,
		total_type: 0,
		batch_quantity:'',
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
	},
	methods: {
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			vm.order_no='';
			vm.assembly_position='';
			vm.total_qty=0;
			vm.total_type=0;
			vm.batch_quantity='';
		},
		getOrderNoFuzzy: function(){
			if($("#werks").val() === ''){
				alert('请先选择工厂！');
				return false;
			}
			getZZJOrderNoSelect("#order_no",null,fn_cb_getOrderInfo,"#werks");
		},
		getAssemblyPositionNoFuzzy: function(){
			if($("#order_no").val() === ''){
				return false;
			}
		    getAssemblyPositionNoSelect('#assembly_position',fn_cb_getAssemblyPositionInfo,"#order_no");
		},
		query: function(){
         	//$("#dataGrid").jqGrid('clearGridData');
         	//vm.total_qty=0;
			//vm.total_type=0;
         	vm.queryMatInfo();
		},
		queryMatInfo: function(){
			if(vm.order_no==''){
				js.showErrorMessage('请选择订单！');
			 return false;
			}
			if(vm.werks==''){
				js.showErrorMessage('请选择工厂！');
				return false;
			}
			if(vm.worshop==''){
				js.showErrorMessage('请选择车间！');
				return false;
			}
			if(vm.use_workshop==''){
				js.showErrorMessage('请选择使用车间！');
				return false;
			}
			if(vm.assembly_position==''){
				js.showErrorMessage('请输入装配位置！');
				return false;
			}
			js.loading("正在查询加载数据，请稍等...");
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
			$.ajax({
				url:baseUrl + "zzjmes/matHandover/getSupplyMatInfo",
				data:{
					"werks": vm.werks,
					"workshop": vm.workshop,
					"line": vm.line,
					"order_no": vm.order_no,
					"zzj_no": vm.zzj_no,
					"zzj_pmd_items_id": vm.zzj_pmd_items_id,
					"use_workshop": vm.use_workshop,
					"assembly_position": vm.assembly_position,
					"process": vm.process,
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					vm.zzj_no="";
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("未找到符合条件的供货数据！");
							return false;
						}
						addMat(response.data);
					}
				},
			});
		},
		save: function(){
            if(vm.order_no==''){
           		js.showErrorMessage('请选择订单！');
        		return false;
        	}
           	//初始化工厂、车间、线别名称值
        	$('#werks_name').val($('#werks').find("option:selected").data("name"));
        	$('#workshop_name').val($('#workshop').find("option:selected").text());
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
        	console.log("rows.length",rows.length);
        	$("#psw").val("");
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			for(var i in rows){
				var data=rows[i];
				if(data.deliver_date=='' || data.rdeliver_user==''|| data.receive_user=='' || data.supply_quantity==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行数量录入不完整！");
					return false;
				}
				delete data.EMPTY_COL;
			}
			/*layer.open({
        		type : 1,
        		offset : '50px',
        		skin : 'layui-layer-molv',
        		title : "供货确认",
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
        						//var delivery_user=result.currentUser.FULL_NAME;
        						$.ajax({
        							type : "post",
        							dataType: "json",
        							async : true,
        							url :baseUrl+ "zzjmes/matHandover/saveSupply",
        							data : {
        								"werks" : vm.werks,
        								"werks_name": $('#werks_name').val(),
        								"workshop" : vm.workshop,
        								"workshop_name": $('#workshop_name').val(),
        								"order_no" : vm.order_no,
        								//"deliver_user" : delivery_user,
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
        									vm.batch_quantity='';
        									vm.zzj_no='';
        									vm.assembly_position='';
        									$("#resultMsg").html("操作成功！");
        						        	$("#order_no").val(vm.order_no);
        						        	$("#matList").val(JSON.stringify(rows));
        						        	layer.open({
        						        		type : 1,
        						        		offset : '50px',
        						        		skin : 'layui-layer-molv',
        						        		title : "发货成功",
        						        		area : [ '500px', '250px' ],
        						        		shade : 0,
        						        		shadeClose : false,
        						        		content : jQuery("#resultLayer"),
        						        		btn : [ '确定'],
        						        		btn1 : function(index) {
        						        			layer.close(index);
        											//$("#btnCreat").removeAttr("disabled");
        						        		}
        						        	});						        	
        								}	
        							}	
        						});
        					/*}else{
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
	        		  "MENU_KEY":"ZZJMES_WORKSHOPSUPPLY",
	        	  },
	        	  success:function(resp){
	        		 vm.workshoplist = resp.data;
	        		 if(resp.data.length>0){
	        			vm.workshop=resp.data[0].CODE;
	        		 } 
	        	  }
			  });
			  
			  $.ajax({
				url:baseUrl + "masterdata/getWerksWorkshopList",
				data:{
					"CODE":newVal,
					"WERKS":newVal,
				},
				success:function(resp){
				   vm.useWorkshoplist = resp.data;
				   if(resp.data.length>0){
					   vm.use_workshop=resp.data[0].NAME;
					   getProcessList();
				   } 
				}
			});
			}
		},
		use_workshop:{
			handler:function(newVal,oldVal){
				getProcessList();
			}
		},
		batch_quantity:{
			handler:function(newVal,oldVal){
				var ids = $("#dataGrid").getDataIDs();
				vm.total_qty=0;
				$.each(ids,function(i,rowid){
					var row=$("#dataGrid").jqGrid('getRowData',rowid);
					if((Number(row.demand_quantity)-Number(row.already_supply_quantity))>=Number(vm.batch_quantity)*Number(row.quantity)){
						$("#dataGrid").jqGrid('setCell',rowid, "supply_quantity", Number(vm.batch_quantity)*Number(row.quantity),'&nbsp;');
						vm.total_qty+=Number(Number(vm.batch_quantity)*Number(row.quantity));
					}else{
						vm.total_qty+=Number(row.supply_quantity);
					}
				});
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
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				console.log("val_json",val_json);	
				vm.order_no=val_json.order_no;
				//vm.zzj_plan_batch = val_json.zzj_plan_batch;
				vm.zzj_no =val_json.zzj_no;
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
				//vm.line = val_json.line;
				//vm.zzj_pmd_items_id = val_json.pmd_item_id||0;
			}
			vm.queryMatInfo();	
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
			{label:'receive_workshop',name:'receive_workshop',hidden:true},
			{label:'init_supply_quantity',name:'init_supply_quantity',hidden:true,formatter:function(val, obj, row, act){
				return row.supply_quantity;
			}},
			{label:'id',name:'id', index: 'id',key: true ,hidden:true,formatter:function(val, obj, row, act){
				return obj.rowId
			}},
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				return actions.join(",");
   			}},
   			{ label: '供应工厂', name: 'werks', align:'center', index: 'werks', width: 65 }, 		
    		{ label: '供应车间', name: 'workshop_name', align:'center', index: 'workshop_name', width: 65 }, 
   			{ label: '图号', name: 'material_no', align:'center', index: 'material_no', width: 110,
   				formatter:function(val, obj, row, act){
   		        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getPdf('"+row.werks+"','"+val+"')>"+val+"</a>";
   				},
   				unformat:function(cellvalue, options, rowObject){
   					return cellvalue;
   				}
   			},
   			{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 
   			{ label: '零部件名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 120 }, 
			{ label: '使用车间', name: 'receive_workshop_name', align:'center', index: 'receive_workshop_name', width: 65 }, 	
			{ label: '单车用量', name: 'quantity', align:'center', index: 'quantity', width: 65 }, 	
			{ label: '需求数量', name: 'demand_quantity', align:'center', index: 'demand_quantity', width: 65 }, 	
			{ label: '已生产数量', name: 'prod_quantity', align:'center', index: 'prod_quantity', width: 75 }, 	
            { label: '已供货数量', name: 'already_supply_quantity', align:'center', index: 'already_supply_quantity', width: 75 }, 	
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>供货数量</b>', name: 'supply_quantity', align:'center', index: 'supply_quantity', width: 70 ,editable:true,editrules:{
   				required:true,number:true},  formatter:'number',formatoptions:{
   	   				decimalSeparator:".", thousandsSeparator: ",",decimalPlaces: 0,defaultValue:''}
   				},
   				
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>交付人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'deliver_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'deliver_user', align:"center",index: 'deliver_user', width: 70,editable:true,
    			editrules:{},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　      doScan('deliver_user');
            		　 });
            	}}
            },
            { label: '<span style="color:red">*</span><span style="color:blue"><b>接收人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'receive_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'receive_user', align:"center",index: 'receive_user', width: 70,editable:true,
    			editrules:{},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　      doScan('receive_user');
            		　 });
            	}}
            },
            { label: '<span style="color:red">*</span><span style="color:blue"><b>交付日期</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'deliver_date\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'deliver_date', align:'center', index: 'deliver_date', width: 90,editable:true,
    			editrules:{required: true},
            	editoptions:{
            	   dataInit:function(el){
            		　 $(el).click(function(){
            		　         WdatePicker();
            	   });
            	}}
            }, 
   			{ label: '装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 120 },
    		{ label: '工艺流程', name: 'process_flow', align:'center',  index: 'process_flow', width: 120 }, 			
    		{ label: '使用工序', name: 'process', align:'center', index: 'process', width: 70 }, 	
    		{ label: '装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 120 }, 	
    		{ label: '订单', name: 'order_desc',align:"center", index: 'order_desc', width: 150 },
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,	
        gridComplete:function(){
        	// 累计已生产数量：小于累计已供货数量的整行字体标红。
            var ids = $("#dataGrid").getDataIDs();
            for(var i=0;i<ids.length;i++){
                var rowData = $("#dataGrid").getRowData(ids[i]);
                if(Number(rowData.prod_quantity)<Number(rowData.already_supply_quantity)){//如果天数等于0，则背景色置灰显示
                    $('#'+ids[i]).find("td").addClass("selectBG");
                }
            }
        },
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
			if(cellname=='supply_quantity'){
				var field="供货数量";
				var able_qty= Number(row.init_supply_quantity);
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
				var ids = $("#dataGrid").getDataIDs();
				vm.total_qty=0;
				$.each(ids,function(i,rowid){
					var row=$("#dataGrid").jqGrid('getRowData',rowid);
					vm.total_qty+=Number(row.supply_quantity);
				});
			}		
		},
    });
}

function fn_cb_getOrderInfo(order){
	vm.order_no = order.order_no;
}
function fn_cb_getAssemblyPositionInfo(assemblyPosition){
	vm.assembly_position = assemblyPosition.assembly_position;
}
function addMat(matList){
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.zzj_pmd_items_id +";";
	});	
	// 扫描二维码  填充抬头字段   ：允许连续扫描多个零部件图号做批量交接，多个零部件的订单和使用车间、装配位置必须相同
    if(rows.length==0){
    	vm.assembly_position=matList[0].assembly_position;
    	vm.use_workshop=matList[0].use_workshop;
    }
    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){	
		if(vm.assembly_position!=row.assembly_position || 
				vm.use_workshop!=row.use_workshop){
			js.showErrorMessage("多个零部件的订单和使用车间、装配位置必须相同");
			return true;
		}
		if(addedMatStr.indexOf(";" + row.zzj_pmd_items_id+";") == -1){
			var data = {};
			data.id = ++rowid;  			
			data.zzj_pmd_items_id = row.zzj_pmd_items_id;
			data.order_no = vm.order_no;
			data.order_desc = row.order_desc;
			data.werks = row.werks;
			data.workshop_name = row.workshop_name;
			data.receive_workshop_name = vm.use_workshop;
			data.process = row.process;
			data.material_no = row.material_no;
	    	data.zzj_no = row.zzj_no;
	    	data.zzj_name = row.zzj_name;
	    	data.assembly_position = row.assembly_position;
	    	data.process = row.process;
	    	data.quantity = row.quantity;
	    	data.process_flow = row.process_flow;	
	    	data.demand_quantity=row.demand_quantity;
	    	data.deliver_date=formatDate(new Date());
	    	data.prod_quantity = row.prod_quantity;	
	    	data.already_supply_quantity = row.already_supply_quantity;
	    	data.supply_quantity =Number(row.demand_quantity)-Number(row.already_supply_quantity);	
	    	data.receive_process_name = vm.receive_process_name;
	    	if(data.supply_quantity<=0){
	    		return true;
	    	}
	    	$("#dataGrid").jqGrid("addRowData", rowid, data, "last");
	    	vm.total_type++;
	    	vm.total_qty+=Number(data.supply_quantity);
		}else{
			js.showErrorMessage(row.zzj_no+"数据重复！");
		}    	
	});	
}

function delMat(id){
	
	var data=$("#dataGrid").jqGrid('getRowData',id);
	vm.total_type--;
	vm.total_qty=vm.total_qty-Number(data.supply_quantity!='' ? data.supply_quantity : '0');
	$("#dataGrid").delRowData(id);
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
function getProcessList(){
	$.ajax({
		url : baseUrl + "masterdata/getWorkshopProcessList",
		data : {
			"WERKS" : vm.werks,
			"WORKSHOP" : vm.use_workshop,
		},
		success : function(resp) {
			vm.processList = resp.data;
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