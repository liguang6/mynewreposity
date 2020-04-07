var cailiao_type_option = "";
var subcontracting_type_option = "";
var section_option = "";
var workshop_option = "";
var process_option = "";
var oldval = '';
var vm = new Vue({
	el:'#rrapp',
	data:{
		orderQty:0,
		mydata:[],
		workshoplist:[],
		linelist:[],
		processlist:[],
		lastrow:'',
		lastcell:'',
		del_ids:'',
		check:true
	},
	created:function(){
		getWorkShopList();
	},
	methods: {
		query: function () {
			if($("#search_order").val() === ''){
				alert("请输入订单号查询！")
				return false;
			}
			if($("#search_zzj_no").val() === '' && $("#search_process_flow").val() === '' 
				&& $("#search_section").val() === '' && $("#search_material_no").val() === ''
				&& $("#search_assembly_position").val() === '' && $("#search_use_workshop").val() === '' 
				&& $("#search_process").val() === '' && $("#search_surface_treatment").val() === ''
				&& $("#search_process_machine").val() === '' && $("#search_filling_size").val() === '' 
				&& $("#search_specification").val() === ''
				&& $("#search_aperture").val() === '' && $("#search_change_subject").val() === '' 
				&& $("#search_maiban").val() === '' && $("#search_banhou").val() === ''
				&& $("#search_filling_size_max").val() === ''){
				alert("工厂，订单，车间，线别以外还 请至少输入一个条件！")
				return false;
			}
			
			$("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"zzjmes/pmdManager/getPmdList",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":$("#search_werks").val(),
					"ORDER_NO":$("#search_order").val(),
					"WORKSHOP":$("#search_workshop").val(),
					"LINE":$("#search_line").val(),
					"ZZJ_NO":$("#search_zzj_no").val(),
					"PROCESS":$("#search_process").val(),
					"SECTION":$("#search_section").val(),
					"MATERIAL_NO":$("#search_material_no").val(),
					"ASSEMBLY_POSITION":$("#search_assembly_position").val(),
					"USE_WORKSHOP":$("#search_use_workshop").val(),
					"SURFACE_TREATMENT":$("#search_surface_treatment").val(),
					"PROCESS_MACHINE":$("#search_process_machine").val(),
					"SUBCONTRACTING_TYPE":$("#search_subcontracting_type").val(),
					"FILLING_SIZE":$("#search_filling_size").val(),
					"SPECIFICATION":$("#search_specification").val(),
					"CAILIAO_TYPE":$("#search_cailiao_type").val(),
					"APERTURE":$("#search_aperture").val(),
					"CHANGE_SUBJECT":$("#search_change_subject").val(),
					"CHANGE_TYPE":$("#search_change_type").val(),
					"MAIBAN":$("#search_maiban").val(),
					"BANHOU":$("#search_banhou").val(),
					"PROCESS_FLOW":$("#search_process_flow").val(),
					"FILLING_SIZE_MAX":$("#search_filling_size_max").val(),
					"Stype":"new"
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					vm.del_ids = '';
					vm.mydata.length=0;
					if(response.code == "0"){
						if(response.result.length === 0){
							vm.mydata = [];
							js.showMessage("没有查询到任何数据！");
						}else{
							vm.mydata = response.result;
						}
					}
					$("#dataGrid").jqGrid('GridUnload');
					fun_ShowTable();
					dataGridMorePaste()
					$("#btnSearchData").removeAttr("disabled");
				}
			})
		},
		editPmd: function() {
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
			if(vm.check){	//防止用户在输入数量的文本框没有消失就直接点创建
				var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
				var arrList = new Array();
				if(ids.length == 0){
					alert("当前还没有勾选任何行项目数据！");
					return false;
				}

				var t = false;	//“零件号+装配位置+使用工序+工艺流程” 唯一性校验 标识
				console.log('-->ids : ' + ids)
				for (var i = 0; i < ids.length; i++) {
					if(parseInt(ids[i]) <= vm.mydata.length){
						arrList.push(vm.mydata[parseInt(ids[i])-1])
						console.log(vm.mydata[parseInt(ids[i])-1])
						if(vm.mydata[parseInt(ids[i])-1].zzj_no === '' || vm.mydata[parseInt(ids[i])-1].zzj_no === undefined){
							alert("选中的第" + (i+1) + "行零部件号不能为空！");
							return false
						}
						if(vm.mydata[parseInt(ids[i])-1].assembly_position === '' || vm.mydata[parseInt(ids[i])-1].assembly_position === undefined){
							alert("选中的第" + (i+1) + "行装配位置不能为空！");
							return false
						}
						if(vm.mydata[parseInt(ids[i])-1].process === '' || vm.mydata[parseInt(ids[i])-1].process === undefined){
							alert("选中的第" + (i+1) + "行工序代码不能为空！");
							return false
						}
						if(vm.mydata[parseInt(ids[i])-1].process_flow === '' || vm.mydata[parseInt(ids[i])-1].process_flow === undefined){
							alert("选中的第" + (i+1) + "行工艺流程不能为空！");
							return false
						}
						//console.log(ids[i],vm.mydata)
						console.log('i_id : ' + vm.mydata[parseInt(ids[i])-1].i_id)
						//“零件号+装配位置+使用工序+工艺流程” 唯一性校验
						if(vm.mydata[parseInt(ids[i])-1].i_id == '0'){
							//新增行时未校验“变更起始台数”必填。
							if(vm.mydata[parseInt(ids[i])-1].change_from === '' || vm.mydata[parseInt(ids[i])-1].change_from === undefined){
								alert("新增行变更起始台数必填!");
								return false
							}
							
						}

						var s = vm.mydata[parseInt(ids[i])-1].zzj_no + vm.mydata[parseInt(ids[i])-1].assembly_position + vm.mydata[parseInt(ids[i])-1].process + vm.mydata[parseInt(ids[i])-1].process_flow
						var s1= '';
						for(j = 0; j < vm.mydata.length; j++) {
							//if(vm.mydata[j].i_id != '0'){
							if(j != parseInt(ids[i])-1){
								s1 = vm.mydata[j].zzj_no + vm.mydata[j].assembly_position + vm.mydata[j].process + vm.mydata[j].process_flow
								console.log(j + "--" + s +":" + s1)
								if(s === s1){
									console.log(ids[i] + "|" + j)
									t=true
								}
							}
						}

						if(t){
							alert("零件号+装配位置+使用工序+工艺流程不能重复！")
							return false
						}
						
						//必填校验
						//console.log(vm.mydata[parseInt(ids[i])-1].mat_description)
						if(vm.mydata[parseInt(ids[i])-1].mat_description === '' || vm.mydata[parseInt(ids[i])-1].mat_description === undefined){
							alert("选中的第" + (i+1) + "行物料描述不能为空！");
							return false
						}
						
					}
				}
				$("#btnEdit").val("保存中");
				$("#btnEdit").attr("disabled","disabled");	
				//console.log(arrList);
				$.ajax({
					url:baseURL+"zzjmes/pmdManager/editPmdList",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":$("#search_werks").val(),
						"WERKS":$("#search_werks").val(),
						"ORDER_NO":$("#search_order").val(),
						"WORKSHOP":$("#search_workshop").val(),
						"LINE":$("#search_line").val(),
						"ARRLIST":JSON.stringify(arrList),
						"del_ids":''
					},
					async: true,
					success: function (response) {
						if(response.code == "0"){
							js.showMessage("保存成功");
						}
						$("#btnEdit").val("保存");
						$("#btnEdit").removeAttr("disabled");
						vm.query();
					}
				})
			}
			vm.check = true;
		},
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,null)	
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		addEcnTr: function() {
			var ecntbody=$("#ecntbody").find("tr");
			var t = 0;
			$.each(ecntbody,function(index,param){
				var tds=$(param).children("td");
				t = parseInt($(tds[2]).find("input").val())+1
			})
			var addtr = '<tr><td>&nbsp;&nbsp;<i id="removeEcnTr" class="fa fa-times removeEcnTr" style="cursor: pointer;color: blue;"></i></td><td><input type="text" value="'+t+'"/></td><td><input type="text" value="'+vm.orderQty+'"/></td><td><input type="text" value=""/></td></tr>'
			$("#ecnTable tbody").append(addtr);
		},
		onWerksChange:function(event) {
			getWorkShopList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onWorkshopChange:function(event) {
			getLineList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onLineChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onOrderChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		moreZzjNo: function() {
			fun_ShowMoreTable();
			dataGridMorePaste();
			var orders = "";
			layer.open({
	    		type : 1,
	    		offset : '50px',
	    		title : "批量输入零部件号：",
	    		area : [ '500px', '350px' ],
	    		shadeClose : false,
	    		content : jQuery("#moreZzjNoLayer"),
	    		btn : [ '确定'],
	    		btn1 : function(index) {
	    			layer.close(index);
	    			var trs=$("#dataGrid_1").children("tbody").children("tr");
	    			$.each(trs,function(index,tr){
	    				if(index>0&&($(tr).find("td").eq(1).text()!=" "&&$(tr).find("td").eq(1).text()!=""))orders += $(tr).find("td").eq(1).text() + ",";
	    			});
	    			$("#search_zzj_no").val(orders.substring(0,orders.length-1));
	    		}
			});
		},
		getEcnStr: function(rowid) {
			var ecntbody=$("#ecntbody").find("tr");
			var check = true;
			var quantity = 0;
			//校验车号是否连续
			var lastEnd = 0;
			$.each(ecntbody,function(index,param){
				var tds=$(param).children("td");
				console.log('ecntbody.length = ' + ecntbody.length)
				if($(tds[1]).find("input").val() === '' || $(tds[2]).find("input").val()=== '' || $(tds[3]).find("input").val()=== ''){
					alert("数据不能为空！");
					check = false;
					return false;
				}
				
				if(index > 0 ){
					if(parseInt($(tds[1]).find("input").val()) != lastEnd + 1){
						alert("车号不连续！");
						check = false;
						return false;
					}
				}
				if(index + 1 === ecntbody.length){
					if(parseInt($(tds[2]).find("input").val()) != vm.orderQty){
						alert("车号结束数量不等于订单数量！！");
						check = false;
						return false;
					}
				}
				lastEnd = parseInt($(tds[2]).find("input").val());
			})
			if(!check){
				return false;
			}
			
			//拼成变更起始字符串
			var ecnStr = ""
			$.each(ecntbody,function(index,param){
				var tds=$(param).children("td");
				ecnStr += $(tds[1]).find("input").val() + "-" + $(tds[2]).find("input").val() + ":" + $(tds[3]).find("input").val() + ";"
				quantity = $(tds[3]).find("input").val();
			})
			console.log('-->rowid : ' + rowid + ' ;ecnStr : ' + ecnStr)
			vm.mydata[rowid-1].change_from = ecnStr
			vm.mydata[rowid-1].quantity = quantity
			var trs=$("#dataGrid").children("tbody").children("tr");
			var tr = trs[rowid]
			$(tr).find("td").eq(2).html(ecnStr);
			$(tr).find("td").eq(27).html(quantity);
		},
		viewDrawing: function(event,material_no){
			getPdf($("#search_werks").val(),material_no);
		}
	},
	watch: {
		
	}
});
$(function () {
	fun_ShowTable();
	dataGridMorePaste()

	$("#newOperation").click(function () {
		if(vm.mydata.length>0){
			var trMoreindex = $("#dataGrid").children("tbody").children("tr").length;
			vm.mydata.push({ id: 0,i_id:0,zzj_name: "",mat_description:""});
			var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
			'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;width: 30px;" aria-describedby="dataGrid_rn">' + trMoreindex + 
			'</td><td role="gridcell" style="text-align:center;width: 25px;" aria-describedby="dataGrid_1_"><input role="checkbox" type="checkbox" id="jqg_dataGrid_'+trMoreindex+'" class="cbox noselect2" name="jqg_dataGrid_'+trMoreindex+'"></td>'+
			'<td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td>'+
			'<td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td>'+
			'<td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td><td  role="gridcell" style="text-align:center;"></td></tr>'
			$("#dataGrid tbody").append(addtr);
		}else{
			alert("请先查询需要编辑的下料明细！")
		}
	});
	
	$(document).on("click",".removeEcnTr",function(e){
		$(e.target).closest("tr").remove();		
	})

	$("#btn_delete").click(function () {
		//20191031调整为直接删除
		var trs=$("#dataGrid").children("tbody").children("tr");
		var reIndex = vm.mydata.length;
		var hasPlan = false;
		var hasCheck = false;
		$.each(trs,function(index,tr){
			var td1 = $(tr).find("td").eq(40);
			var td2 = $(tr).find("td").eq(41);
			var td_check = $(tr).find("td").eq(1).find("input");
			if(index > 0){
				var ischecked=$(td_check).is(":checked");
				if(ischecked){
					hasCheck = true;
					reIndex--;
					if(td2.text() != "" && td2.text() != "0")hasPlan = true;
				}
			}
		})
		if(!hasCheck){
			alert("请选择需要删除的项目！")
			return false
		}
		if(hasPlan){
			alert("已存在相应的机台加工计划的下料明细不允许删除，如需删除需先将机台加工计划删除！")
			return false
		}
		var r = confirm("确认删除吗?")
		if (r == true){
			console.log("confirm true")
			for(var i=trs.length-1;i>0;i--){
				var tr = trs[i]
				var td = $(tr).find("td").eq(0);
				var td1 = $(tr).find("td").eq(40);
				var td_check = $(tr).find("td").eq(1).find("input");
				
				var ischecked=$(td_check).is(":checked");
				if(ischecked){
					if(td1.text() != '')vm.del_ids += td1.text() + ','
					vm.mydata.splice(i-1,1);
					tr.remove();
				}else{
					td.text(reIndex);
					$(tr).attr('id',reIndex);
					$(td_check).attr('name','jqg_dataGrid_' + reIndex);
					$(td_check).attr('id','jqg_dataGrid_' + reIndex);
					reIndex--;
				}
			}
			
			console.log("-->vm.del_ids : " + vm.del_ids)
			$.ajax({
				url:baseURL+"zzjmes/pmdManager/deletePmdList",
				dataType : "json",
				type : "post",
				data : {
					"del_ids":vm.del_ids
				},
				async: true,
				success: function (response) {
					if(response.code == "0"){
						js.showMessage("删除成功");
					}
				}
			})

		}

	})
	
	$("#btn_delete2").click(function () {
		var trs=$("#dataGrid").children("tbody").children("tr");
		var reIndex = vm.mydata.length;
		var hasPlan = false;
		//vm.del_ids = '';
		$.each(trs,function(index,tr){
			var td1 = $(tr).find("td").eq(40);
			var td2 = $(tr).find("td").eq(41);
			var td_check = $(tr).find("td").eq(1).find("input");
			if(index > 0){
				var ischecked=$(td_check).is(":checked");
				if(ischecked){
					//console.log('-->i_id : ' + td1.text() + '-->p_id : ' + td2.text())
					reIndex--;
					if(td2.text() != "" && td2.text() != "0")hasPlan = true;
				}
			}
		})
		if(hasPlan){
			alert("已存在相应的机台加工计划的下料明细不允许删除，如需删除需先将机台加工计划删除！")
			return false
		}
		
		for(var i=trs.length-1;i>0;i--){
			var tr = trs[i]
			var td = $(tr).find("td").eq(0);
			var td1 = $(tr).find("td").eq(40);
			var td_check = $(tr).find("td").eq(1).find("input");
			
			var ischecked=$(td_check).is(":checked");
			if(ischecked){
				if(td1.text() != '')vm.del_ids += td1.text() + ','
				vm.mydata.splice(i-1,1);
				tr.remove();
			}else{
				td.text(reIndex);
				$(tr).attr('id',reIndex);
				$(td_check).attr('name','jqg_dataGrid_' + reIndex);
				$(td_check).attr('id','jqg_dataGrid_' + reIndex);
				reIndex--;
			}
		}
		
		console.log("-->vm.del_ids : " + vm.del_ids)

	});
	$(document).bind("click",function(e){
		if("searchForm" == $(e.target).attr("id")||"ui-jqgrid-bdiv" == $(e.target).attr("class")||"form-inline" == $(e.target).attr("class")){
			$('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
		}
	});

	$("#newOperation_1").click(function () {
		var trMoreindex = $("#dataGrid_1").children("tbody").children("tr").length;
		var addtr = '<tr id = "'+trMoreindex+'"  role="row" tabindex="-1" class="ui-widget-content jqgrow jqgrow ui-row-ltr '+((trMoreindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
		'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trMoreindex + 
		'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"></td>'+
		'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_1_"><i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="delMoreTr fa fa-times"></i></td><td></td></tr>'
		$("#dataGrid_1 tbody").append(addtr);
	});
	$("#newReset_1").click(function () {
		$("#dataGrid_1").jqGrid('GridUnload');
		fun_ShowMoreTable();
		dataGridMorePaste();
	});
	
	$("#search_cailiao_type option").each(function (){
		if($(this).val() != '')cailiao_type_option += $(this).val() + ":" + $(this).text() + ";"
	})
	$("#search_subcontracting_type option").each(function (){
		if($(this).val() != '')subcontracting_type_option += $(this).val() + ":" + $(this).text() + ";"
	})
	$("#search_section option").each(function (){
		if($(this).val() != '')section_option += $(this).val() + ":" + $(this).text() + ";"
	})
	
});
function fun_ShowTable(){
	workshop_option = "";
	$("#search_workshop option").each(function (){
		if($(this).val() != '')workshop_option += $(this).text() + ":" + $(this).text() + ";"
	})

	$("#dataGrid").dataGrid({
		datatype: "local",
		data: vm.mydata,
		columnModel: [
    		{ label: '变更起始台数', name: 'change_from',align:'center', index: 'change_from', width: 150 ,sortable:false,frozen: true}, 		
    		{ label: '序号', name: 'no', align:'center', index: 'no', width: 50 ,frozen: true,sortable:true,editable:true}, 			
    		{ label: '*图号', name: 'material_no', align:'center', index: 'material_no', width: 120 ,frozen: true,sortable:true,editable:true}, 
    		{ label: '图纸', name: 'material_no', align:'center', index: 'material_no', width: 80 ,frozen: true,sortable:true,editable:false,
    			formatter:function(val, obj, row, act){
    				return '<a href="#" title="查看图纸" onClick="vm.viewDrawing(event,\''+val+'\')">查看图纸</a>'
    			},
    		}, 
    		{ label: '*零部件号', name: 'zzj_no', align:'center',  index: 'zzj_no', width: 120 ,frozen: true,sortable:true,editable:true}, 	
    		{ label: '*零部件名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 150 ,sortable:true,editable:true}, 		
    		{ label: '*物料描述', name: 'mat_description', align:'center', index: 'mat_description', width: 150 ,sortable:false,editable:true}, 	 		
    		{ label: '*物料类型', name: 'mat_type', align:'center', index: 'mat_type', width: 100 ,sortable:false,editable:true}, 	
    		{ label: '*材料/规格', name: 'specification', align:'center', index: 'specification', width: 100 ,sortable:false,editable:true}, 			
    		{ label: '下料尺寸', name: 'filling_size', index: 'filling_size', width: 100 ,sortable:false,editable:true},	
    		{ label: '*材料类型', name: 'cailiao_type', align:'center',  index: 'cailiao_type', width: 100 ,sortable:false,edittype:'select',editable:true}, 			
    		{ label: '*装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 100 ,sortable:false,editable:true},
    		{ label: '*使用车间', name: 'use_workshop', align:'center', index: 'use_workshop', width: 100 ,sortable:false,edittype:'select',editable:true}, 
    		{ label: '*使用工序代码', name: 'process', align:'center', index: 'process', width: 100 ,sortable:false,edittype:'select',editable:true}, 
    		{ label: '使用工序名称', name: 'process_name', align:'center', index: 'process_name', width: 100 ,sortable:false,editable:false}, 
    		{ label: '加工顺序', name: 'process_sequence', align:'center', index: 'process_sequence', width: 100 ,sortable:false,editable:true}, 
    		{ label: '*工艺流程', name: 'process_flow', align:'center', index: 'process_flow', width: 100 ,sortable:false,editable:true}, 
    		{ label: '加工工时', name: 'process_time', align:'center', index: 'process_time', width: 100 ,sortable:false,editable:true}, 
    		{ label: '加工设备', name: 'process_machine', align:'center', index: 'process_machine', width: 100 ,sortable:false,editable:true}, 
    		{ label: '*分包类型', name: 'subcontracting_type', align:'center', index: 'subcontracting_type', width: 100 ,sortable:false,edittype:'select',editable:true}, 
    		{ label: '孔特征', name: 'aperture', align:'center', index: 'aperture', width: 100 ,sortable:false,editable:true}, 
    		{ label: '精度要求', name: 'accuracy_demand', align:'center', index: 'accuracy_demand', width: 100 ,sortable:false,editable:true}, 
    		{ label: '表面处理', name: 'surface_treatment', align:'center', index: 'surface_treatment', width: 100 ,sortable:false,editable:true}, 
    		{ label: '单重', name: 'weight', align:'center', index: 'weight', width: 100 ,sortable:false,editable:true}, 
    		{ label: '单位', name: 'unit', align:'center', index: 'unit', width: 100 ,sortable:false,editable:true}, 
    		{ label: '*单车用量', name: 'quantity', align:'center', index: 'quantity', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*单车损耗%', name: 'loss', align:'center', index: 'loss', width: 100 ,sortable:false,editable:true}, 
    		{ label: '总重含损耗', name: 'total_weight', align:'center', index: 'total_weight', width: 100 ,sortable:false,editable:true}, 
    		{ label: '备注', name: 'memo', align:'center', index: 'memo', width: 100 ,sortable:false,editable:true}, 
    		{ label: '工艺备注', name: 'processflow_memo', align:'center', index: 'processflow_memo', width: 100 ,sortable:false,editable:true}, 
    		{ label: '*工段', name: 'section', align:'center', index: 'section', width: 100 ,sortable:false,edittype:'select',editable:true}, 
    		{ label: '埋板', name: 'maiban', align:'center', index: 'maiban', width: 100 ,sortable:false,editable:true}, 
    		{ label: '板厚', name: 'banhou', align:'center', index: 'banhou', width: 100 ,sortable:false,editable:true}, 
    		{ label: '特殊大尺寸', name: 'filling_size_max', align:'center', index: 'filling_size_max', width: 100 ,sortable:false,editable:true}, 
    		{ label: 'SAP码', name: 'sap_mat', align:'center', index: 'sap_mat', width: 100 ,sortable:false,editable:true}, 
    		{ label: '变更说明', name: 'change_description', align:'center', index: 'change_description', width: 100 ,sortable:false,editable:true}, 
    		{ label: '变更主体', name: 'change_subject', align:'center', index: 'change_subject', width: 100 ,sortable:false,editable:true}, 
    		{ label: '变更类型', name: 'change_type', align:'center', index: 'change_type', width: 100 ,sortable:false,edittype:'select',editable:true,
    			formatter:function(val){	//变更类型 1 技改 2 非技改
            		if("1"==val){return "技改";}
            		else if("2"==val){return "非技改";}
            		else{return "";}
            	}
    		}, 
    		{ label: 'i_id', name: 'i_id', align:'center', index: 'i_id', width: 10 ,hidden:true}, 
    		{ label: 'P_ID', name: 'P_ID', align:'center', index: 'P_ID', width: 10 ,hidden:true}, 
    		{ label: 'id', name: 'id', align:'center', index: 'id', width: 10 ,hidden:true}, 
    	],
    	formatCell:function(rowid, cellname, value, iRow, iCol){
    		if(cellname=='change_type'){
    			$('#dataGrid').jqGrid('setColProp', 'change_type', { 
    				editoptions: {value:'1:技改;2:非技改'}
	            });
    		}
    		if(cellname=='cailiao_type'){
					if(cailiao_type_option.substring(cailiao_type_option.length-1,cailiao_type_option.length) === ';')cailiao_type_option = cailiao_type_option.substring(0,cailiao_type_option.length-1)
    			$('#dataGrid').jqGrid('setColProp', 'cailiao_type', { 
    				editoptions: {value:cailiao_type_option}
	          });
    		}
    		if(cellname=='subcontracting_type'){
    			if(subcontracting_type_option.substring(subcontracting_type_option.length-1,subcontracting_type_option.length) === ';')subcontracting_type_option = subcontracting_type_option.substring(0,subcontracting_type_option.length-1)
    			$('#dataGrid').jqGrid('setColProp', 'subcontracting_type', { 
    				editoptions: {value:subcontracting_type_option}
	        });
    		}
    		if(cellname=='section'){
    			if(section_option.substring(section_option.length-1,section_option.length) === ';')section_option = section_option.substring(0,section_option.length-1)
    			$('#dataGrid').jqGrid('setColProp', 'section', { 
    				editoptions: {value:section_option}
	        });
				}
				if(cellname=='use_workshop'){
					if(workshop_option.substring(workshop_option.length-1,workshop_option.length) === ';')workshop_option = workshop_option.substring(0,workshop_option.length-1)
    			$('#dataGrid').jqGrid('setColProp', 'use_workshop', { 
    				editoptions: {value:workshop_option}
	        });
				}
				if(cellname=='process'){
					var row =  $("#dataGrid").jqGrid('getRowData', rowid);
					console.log('use_workshop : ' + row.use_workshop + ' p_len = ' + vm.processlist.length)
					getProcessList(row.use_workshop)
					//process_option  vm.processlist
					process_option = "";
					for(var i = 0;i<vm.processlist.length;i++){
						process_option += vm.processlist[i].PROCESS_CODE + ":" + vm.processlist[i].PROCESS_CODE + ";"
					}
					if(process_option.substring(process_option.length-1,process_option.length) === ';')process_option = process_option.substring(0,process_option.length-1)
    			$('#dataGrid').jqGrid('setColProp', 'process', { 
    				editoptions: {value:process_option}
	        });
				}
    	},
    	onCellSelect:function(rowid,iCol,cellcontent,e){
    		var data =  $("#dataGrid").jqGrid('getRowData', rowid);
    		console.log('-->rowid = ' + rowid + ";ID = " + vm.mydata[rowid-1].i_id)
				if(vm.mydata[rowid-1].i_id != '0'){
					$("#dataGrid").jqGrid('setCell', rowid, 'no', '','not-editable-cell');
					$("#dataGrid").jqGrid('setCell', rowid, 'zzj_no', '','not-editable-cell');
					$("#dataGrid").jqGrid('setCell', rowid, 'zzj_name', '','not-editable-cell');
					//$("#dataGrid").jqGrid('setCell', rowid, 'process', '','not-editable-cell');
				}
    		
    		vm.orderQty = $("#search_order").attr("orderQty")
    		
    		var trs=$("#ecnTable").children("tbody").children("tr");
			$.each(trs,function(index,tr){
				if(index > 0)tr.remove()
			})
			
    		var row =  $("#dataGrid").jqGrid('getRowData', rowid);	
    		
			if((iCol === 2||iCol === 27 )){
				console.log('-->change_from : ' + row.change_from )
				if("" === row.change_from){
					$("#end_busnum").val($("#search_order").attr("orderQty"))
					$("#ecnt_value").val(vm.mydata[rowid-1].quantity)
				}else{
					var str = row.change_from
					var t = str.substring(0,str.length-1).split(";")
					for(i = 0; i < t.length; i++) {
						var start = t[i].substring(0,t[i].indexOf('-'))
						var end = t[i].substring(t[i].indexOf('-')+1,t[i].indexOf(':'))
						var val = t[i].substring(t[i].indexOf(':')+1,t[i].length)
						if(i===0){
							$("#end_busnum").val(end)
							$("#ecnt_value").val(val)
						}else{
							var addtr = '<tr><td>&nbsp;&nbsp;<i id="removeEcnTr" class="fa fa-times removeEcnTr" style="cursor: pointer;color: blue;"></i></td><td><input type="text" value="'+start+'"/></td><td><input type="text" value="'+end+'"/></td><td><input type="text" value="'+val+'"/></td></tr>'
							$("#ecnTable tbody").append(addtr);
						}
					}
				}
				layer.open({
					type : 1,
					offset : '50px',
					skin : 'layui-layer-molv',
					title : "编辑变更起始台数 订单数量" + vm.orderQty + "台",
					area : [ '500px', '360px' ],
					shade : 0.5,
					shadeClose : true,
					content : jQuery("#ecnLayer"),
					btn : [ '确定','清空','取消'],
					btn1 : function(index) {
						return vm.getEcnStr(rowid)
					},
					btn2 : function(index) {
						vm.mydata[rowid-1].change_from = ''
						var trs=$("#dataGrid").children("tbody").children("tr");
						var tr = trs[rowid]
						$(tr).find("td").eq(2).html('');
					},
					btn3 : function(index) {
						console.log('btn3')
						layer.close(index);
					}
				});
			}
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			vm.lastrow=iRow;
			vm.lastcell=iCol;
			vm.check = false;
		},
		afterEditCell:function(rowid, cellname, v, iRow, iCol){			
			vm.check = true;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		beforeSaveCell:function(rowid, cellname, value, iRow, iCol){
			if(cellname=='use_workshop')oldval = vm.mydata[rowid-1].use_workshop
			if(cellname=='process')oldval = vm.mydata[rowid-1].process
			if(cellname=='process_flow')oldval = vm.mydata[rowid-1].process_flow
			if(cellname=='no')oldval = vm.mydata[rowid-1].no
			if(cellname=='zzj_no')oldval = vm.mydata[rowid-1].zzj_no
			if(cellname=='zzj_name')oldval = vm.mydata[rowid-1].zzj_name
			if(cellname=='process')oldval = vm.mydata[rowid-1].process
			if(cellname=='process_name')oldval = vm.mydata[rowid-1].process_name
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);	
			if(cellname=='use_workshop'){
				//console.log('-->use_workshop : ' + vm.mydata[rowid-1].use_workshop)
				var c = false
				$.each(vm.workshoplist,function(index,workshop){
					if(value === workshop.NAME)c = true
				})
				if(c){
					vm.mydata[rowid-1].use_workshop=value
				}else{
					alert(value + "不是此工厂下的车间！");
					vm.mydata[rowid-1].use_workshop=oldval;
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(14).html(oldval);
					return false
				}
			}
			if(cellname=='process'){
				var c = false
				var pname = ''
				console.log(vm.processlist)
				$.each(vm.processlist,function(index,process){
					if(value === process.PROCESS_CODE){
						c = true
						pname =  process.PROCESS_NAME
					}
				})
				if(c){
					vm.mydata[rowid-1].process=value
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(16).html(pname);
				}else{
					alert(value + "不是此车间下的工序！");
					vm.mydata[rowid-1].process=oldval;
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(15).html(oldval);
					return false
				}
			}
			if(cellname=='process_flow'){
				var t = value.split("-")
				for(i = 0; i < t.length; i++) {
					var c = false
					$.each(vm.processlist,function(index,process){
						console.log(t[i] + "|" + process.PROCESS_NAME)
						if(t[i] === process.PROCESS_NAME)c = true
					})
					if(c){
						//vm.mydata[rowid-1].process_flow=value
					}else{
						alert(t[i] + "不是此车间下的工序！");
						vm.mydata[rowid-1].process_flow=oldval;
						var trs=$("#dataGrid").children("tbody").children("tr");
						$(trs[rowid]).find("td").eq(18).html(oldval);
						return false
					}
				}
				//BUG #1586  修改工艺流程时需校验“被修改”的“工序”是否存在机台加工计划，如存在，则提示“XXX工序已存在机台加工计划”。
				var c2 = true;
				var r = oldval.split("-")
				for(i = 0; i < r.length; i++) {
					if(value.indexOf(r[i]) < 0 ){
						console.log(r[i])
						console.log(vm.mydata[rowid-1].werks + vm.mydata[rowid-1].workshop + vm.mydata[rowid-1].line + vm.mydata[rowid-1].zzj_no)
						$.ajax({
					  	  url:baseUrl + "zzjmes/pmdManager/getPmdProcessPlanCount",
					  	  data:{
					  		  "werks":vm.mydata[rowid-1].werks,
					  		  "workshop":vm.mydata[rowid-1].workshop,
					  		  "line":vm.mydata[rowid-1].line,
									"zzj_no":vm.mydata[rowid-1].zzj_no,
									"order_no":vm.mydata[rowid-1].order_no,
					  		  "plan_process":r[i]
					  	  },
					  	  async:false,
					  	  success:function(resp){
					  		  if(resp.result > 0){
					  			  alert(r[i] + "工序已存在机台加工计划");
					  			  vm.mydata[rowid-1].process_flow=oldval;
					  			  var trs=$("#dataGrid").children("tbody").children("tr");
					  			  $(trs[rowid]).find("td").eq(18).html(oldval);
					  			  c2 = false;
					  			  return false
					  		  }
					  	  }
						})
					}
				}
				if(c2){
					console.log("c2 = " + c2)
					vm.mydata[rowid-1].process_flow=value
				}
				console.log("process_flow = " + vm.mydata[rowid-1].process_flow)
			}

			if(cellname=='no'){
				if(vm.mydata[rowid-1].i_id != 0){
					vm.mydata[rowid-1].no=oldval;
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(3).html(oldval);
					alert("序号不允许修改！")
					return false
				}
			}
			if(cellname=='zzj_no'){
				if(vm.mydata[rowid-1].i_id != 0){
					vm.mydata[rowid-1].zzj_no=oldval;
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(6).html(oldval);
					alert("零部件号不允许修改！")
					return false
				}
			}
			if(cellname=='zzj_name'){
				if(vm.mydata[rowid-1].i_id != 0){
					vm.mydata[rowid-1].zzj_name=oldval;
					var trs=$("#dataGrid").children("tbody").children("tr");
					$(trs[rowid]).find("td").eq(7).html(oldval);
					alert("零部件名称不允许修改！")
					return false
				}
			}
			
			if(cellname=='no')vm.mydata[rowid-1].no=value
			if(cellname=='material_no')vm.mydata[rowid-1].material_no=value
			if(cellname=='zzj_no')vm.mydata[rowid-1].zzj_no=value
			if(cellname=='zzj_name')vm.mydata[rowid-1].zzj_name=value
			if(cellname=='mat_description')vm.mydata[rowid-1].mat_description=value
			if(cellname=='mat_type')vm.mydata[rowid-1].mat_type=value
			if(cellname=='specification')vm.mydata[rowid-1].specification=value
			if(cellname=='filling_size')vm.mydata[rowid-1].filling_size=value
			if(cellname=='cailiao_type')vm.mydata[rowid-1].cailiao_type=value
			if(cellname=='assembly_position')vm.mydata[rowid-1].assembly_position=value
			if(cellname=='process')vm.mydata[rowid-1].process=value
			if(cellname=='process_name')vm.mydata[rowid-1].process_name=value
			if(cellname=='process_sequence')vm.mydata[rowid-1].process_sequence=value
			//if(cellname=='process_flow')vm.mydata[rowid-1].process_flow=value
			if(cellname=='process_time')vm.mydata[rowid-1].process_time=value
			if(cellname=='process_machine')vm.mydata[rowid-1].process_machine=value
			if(cellname=='subcontracting_type')vm.mydata[rowid-1].subcontracting_type=value
			if(cellname=='aperture')vm.mydata[rowid-1].aperture=value
			if(cellname=='accuracy_demand')vm.mydata[rowid-1].accuracy_demand=value
			if(cellname=='surface_treatment')vm.mydata[rowid-1].surface_treatment=value
			if(cellname=='weight')vm.mydata[rowid-1].weight=value
			if(cellname=='use_workshop')vm.mydata[rowid-1].use_workshop=value
			if(cellname=='unit')vm.mydata[rowid-1].unit=value
			if(cellname=='quantity')vm.mydata[rowid-1].quantity=value
			if(cellname=='loss')vm.mydata[rowid-1].loss=value
			if(cellname=='total_weight')vm.mydata[rowid-1].total_weight=value
			if(cellname=='memo')vm.mydata[rowid-1].memo=value
			if(cellname=='processflow_memo')vm.mydata[rowid-1].processflow_memo=value
			if(cellname=='section')vm.mydata[rowid-1].section=value
			if(cellname=='maiban')vm.mydata[rowid-1].maiban=value
			if(cellname=='banhou')vm.mydata[rowid-1].banhou=value
			if(cellname=='filling_size_max')vm.mydata[rowid-1].filling_size_max=value
			if(cellname=='sap_mat')vm.mydata[rowid-1].sap_mat=value
			if(cellname=='change_description')vm.mydata[rowid-1].change_description=value
			if(cellname=='change_subject')vm.mydata[rowid-1].change_subject=value
			if(cellname=='change_type')vm.mydata[rowid-1].change_type=value
					
			vm.check = true;
		},
		shrinkToFit: false,
        width:4000,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	//$("#dataGrid").jqGrid("setFrozenColumns");
}

function getWorkShopList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
  	  data:{
  		  "WERKS":$("#search_werks").val(),
  		  "MENU_KEY":"ZZJMES_PMD_MANAGE",
  	  },
  	  //async:false,
  	  success:function(resp){
  		  vm.workshoplist = resp.data;
  		  getLineList(vm.workshoplist[0].code)
				getProcessList(vm.workshoplist[0].code)
  	  }
    })
}
function getLineList(workshop){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserLine",
  	  data:{
  		  "WERKS": $("#search_werks").val(),
  		  "WORKSHOP": $("#search_workshop").val()?$("#search_workshop").val():workshop,
  		  "MENU_KEY": "ZZJMES_PMD_MANAGE",
  	  },
  	  success:function(resp){
  		 vm.linelist = resp.data;
  	  }
    })
}
function getProcessList(workshop){
	$.ajax({
		url : baseUrl + "masterdata/getWorkshopProcessList",
		data : {
			"WERKS" : $("#search_werks").val(),
			"WORKSHOP" : workshop,
			"MENU_KEY" : "ZZJMES_PMD_MANAGE",
		},
		async:false,
		success : function(resp) {
			vm.processlist = resp.data;
		}
	})
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
			if(startRow + i >= rows){
				$("#newOperation").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				//console.log('-->Row ' + parseInt(startRow) + '|Cell ' + (startCell + j) + "|" + vm.mydata.length)
				if((startCell + j) === 3)vm.mydata[parseInt(startRow)-1].no=arr[i][j]
				if((startCell + j) === 4)vm.mydata[parseInt(startRow)-1].material_no=arr[i][j]
				if((startCell + j) === 6)vm.mydata[parseInt(startRow)-1].zzj_no=arr[i][j]
				if((startCell + j) === 7)vm.mydata[parseInt(startRow)-1].zzj_name=arr[i][j]
				if((startCell + j) === 8)vm.mydata[parseInt(startRow)-1].mat_description=arr[i][j]
				if((startCell + j) === 9)vm.mydata[parseInt(startRow)-1].mat_type=arr[i][j]
				if((startCell + j) === 10)vm.mydata[parseInt(startRow)-1].specification=arr[i][j]
				if((startCell + j) === 11)vm.mydata[parseInt(startRow)-1].filling_size=arr[i][j]
				if((startCell + j) === 12)vm.mydata[parseInt(startRow)-1].cailiao_type=arr[i][j]
				if((startCell + j) === 13)vm.mydata[parseInt(startRow)-1].assembly_position=arr[i][j]
				if((startCell + j) === 14)vm.mydata[parseInt(startRow)-1].use_workshop=arr[i][j]
				if((startCell + j) === 15)vm.mydata[parseInt(startRow)-1].process=arr[i][j]
				if((startCell + j) === 16)vm.mydata[parseInt(startRow)-1].process_name=arr[i][j]
				if((startCell + j) === 17)vm.mydata[parseInt(startRow)-1].process_sequence=arr[i][j]
				if((startCell + j) === 18)vm.mydata[parseInt(startRow)-1].process_flow=arr[i][j]
				if((startCell + j) === 19)vm.mydata[parseInt(startRow)-1].process_time=arr[i][j]
				if((startCell + j) === 20)vm.mydata[parseInt(startRow)-1].process_machine=arr[i][j]
				if((startCell + j) === 21)vm.mydata[parseInt(startRow)-1].subcontracting_type=arr[i][j]
				if((startCell + j) === 22)vm.mydata[parseInt(startRow)-1].aperture=arr[i][j]
				if((startCell + j) === 23)vm.mydata[parseInt(startRow)-1].accuracy_demand=arr[i][j]
				if((startCell + j) === 24)vm.mydata[parseInt(startRow)-1].surface_treatment=arr[i][j]
				if((startCell + j) === 25)vm.mydata[parseInt(startRow)-1].weight=arr[i][j]
				//if((startCell + j) === 25)vm.mydata[parseInt(startRow)-1].use_workshop=arr[i][j]
				if((startCell + j) === 26)vm.mydata[parseInt(startRow)-1].unit=arr[i][j]
				if((startCell + j) === 27)vm.mydata[parseInt(startRow)-1].quantity=arr[i][j]
				if((startCell + j) === 28)vm.mydata[parseInt(startRow)-1].loss=arr[i][j]
				if((startCell + j) === 29)vm.mydata[parseInt(startRow)-1].total_weight=arr[i][j]
				if((startCell + j) === 30)vm.mydata[parseInt(startRow)-1].memo=arr[i][j]
				if((startCell + j) === 31)vm.mydata[parseInt(startRow)-1].processflow_memo=arr[i][j]
				if((startCell + j) === 32)vm.mydata[parseInt(startRow)-1].section=arr[i][j]
				if((startCell + j) === 33)vm.mydata[parseInt(startRow)-1].maiban=arr[i][j]
				if((startCell + j) === 34)vm.mydata[parseInt(startRow)-1].banhou=arr[i][j]
				if((startCell + j) === 35)vm.mydata[parseInt(startRow)-1].filling_size_max=arr[i][j]
				if((startCell + j) === 36)vm.mydata[parseInt(startRow)-1].sap_mat=arr[i][j]
				if((startCell + j) === 37)vm.mydata[parseInt(startRow)-1].change_description=arr[i][j]
				if((startCell + j) === 38)vm.mydata[parseInt(startRow)-1].change_subject=arr[i][j]
				if((startCell + j) === 39)vm.mydata[parseInt(startRow)-1].change_type=arr[i][j]
				
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid').jqGrid("saveCell", startRow + i, startCell + j);
				$(cell).html(arr[i][j]);
			}
		}
	});
}

function fun_ShowMoreTable(){
	$("#dataGrid_1").dataGrid({
		datatype: "local",
		data: [{id:0},{id:1}],
        colModel: [			
            {label: '<span style="color:red">*</span><span style="color:blue"><b>零部件号</b></span>', name: 'zzj_no',index:"zzj_no", width: "200",align:"center",sortable:false,editable:true}, 
            {label: '删除', name: 'AUFNR',index:"AUFNR", width: "80",align:"center",sortable:false,editable:false,
            	formatter:function(value, name, record){
            		return '<i title="删除" aria-hidden="true" onclick="$(this).parent().parent().remove();delMoreTr();" class="fa fa-times"></i>';
            	}, 
            },
            {label: '', name: '',index:"AUFNR", width: "150",align:"center",sortable:false,editable:false} 
        ],
		shrinkToFit: false,
        width:600,
        cellEdit:true,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:false
    });
}
