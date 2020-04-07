var vm = new Vue({
	el:'#rrapp',
	data:{
		orderQty:0,
		mydata:[],
		search_werks:'',
		search_workshop:"",
		search_line:'',
		workshoplist:[],
		linelist:[],
		lastrow:'',
		lastcell:'',
		check:true,
		showMore:true,
		assembly_position:''
	},
	created:function(){
		//getWorkShopList();
		this.search_werks = $("#search_werks").find("option").first().val();
	},
	methods: {
		query: function () {
			$("#searchPage_werks").val($("#search_werks").val())
			$("#searchPage_order").val($("#search_order").val())
			$("#searchPage_workshop").val($("#search_workshop").val())
			$("#searchPage_line").val($("#search_line").val())
			$("#searchPage_zzj_no").val($("#search_zzj_no").val())		
			$("#searchPage_process_flow").val($("#search_process_flow").val())
			$("#searchPage_section").val($("#search_section").val())
			$("#searchPage_material_no").val($("#search_material_no").val())
			$("#searchPage_assembly_position").val($("#search_assembly_position").val())
			$("#searchPage_use_workshop").val($("#search_use_workshop").val())
			$("#searchPage_process").val($("#search_process").val())
			$("#searchPage_surface_treatment").val($("#search_surface_treatment").val())
			$("#searchPage_process_machine").val($("#search_process_machine").val())
			$("#searchPage_subcontracting_type").val($("#search_subcontracting_type").val())
			$("#searchPage_filling_size").val($("#search_filling_size").val())
			$("#searchPage_specification").val($("#search_specification").val())
			$("#searchPage_cailiao_type").val($("#search_cailiao_type").val())
			$("#searchPage_aperture").val($("#search_aperture").val())
			$("#searchPage_change_subject").val($("#search_change_subject").val())
			$("#searchPage_change_type").val($("#search_change_type").val())
			$("#searchPage_maiban").val($("#search_maiban").val())
			$("#searchPage_banhou").val($("#search_banhou").val())
			$("#searchPage_filling_size_max").val($("#search_filling_size_max").val())	
			$("#searchPage_nopic").val("0")
			$("#export_nopic").val("0")
			$("#searchPage_Stype").val($("input[name='Stype']:checked").val())
			
			$("#searchPageForm").submit();
			if($("#search_order").val() === ''){
				alert("请输入订单号查询！")
				return false;
			}
		},
		query_nopic: function () {
			$("#searchPage_werks").val($("#search_werks").val())
			$("#searchPage_order").val($("#search_order").val())
			$("#searchPage_workshop").val($("#search_workshop").val())
			$("#searchPage_line").val($("#search_line").val())
			$("#searchPage_zzj_no").val($("#search_zzj_no").val())		
			$("#searchPage_process_flow").val($("#search_process_flow").val())
			$("#searchPage_section").val($("#search_section").val())
			$("#searchPage_material_no").val($("#search_material_no").val())
			$("#searchPage_assembly_position").val($("#search_assembly_position").val())
			$("#searchPage_use_workshop").val($("#search_use_workshop").val())
			$("#searchPage_process").val($("#search_process").val())
			$("#searchPage_surface_treatment").val($("#search_surface_treatment").val())
			$("#searchPage_process_machine").val($("#search_process_machine").val())
			$("#searchPage_subcontracting_type").val($("#search_subcontracting_type").val())
			$("#searchPage_filling_size").val($("#search_filling_size").val())
			$("#searchPage_specification").val($("#search_specification").val())
			$("#searchPage_cailiao_type").val($("#search_cailiao_type").val())
			$("#searchPage_aperture").val($("#search_aperture").val())
			$("#searchPage_change_subject").val($("#search_change_subject").val())
			$("#searchPage_change_type").val($("#search_change_type").val())
			$("#searchPage_maiban").val($("#search_maiban").val())
			$("#searchPage_banhou").val($("#search_banhou").val())
			$("#searchPage_filling_size_max").val($("#search_filling_size_max").val())	
			$("#searchPage_nopic").val("1")
			$("#export_nopic").val("1")
			$("#searchPage_Stype").val($("input[name='Stype']:checked").val())
			$("#searchPageForm").submit();
			if($("#search_order").val() === ''){
				alert("请输入订单号查询！")
				return false;
			}
			$("#dataGrid_1").jqGrid('GridUnload');
			fun_ShowMoreTable();
		},
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,null)	
		},
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		addEcnTr: function() {
			var addtr = '<tr><td>&nbsp;&nbsp;</td><td><input type="text" value=""/></td><td><input type="text" value=""/></td><td><input type="text" value=""/></td></tr>'
			$("#ecnTable tbody").append(addtr);
		},
		onWerksChange:function(event) {
			getWorkShopList()
		},
		onWorkshopChange:function(event) {
			getLineList()
		},
		searchMore:function(event) {
			if(vm.showMore){
				$("#searchForm").removeClass("hide")
				vm.showMore = false
			}else{
				$("#searchForm").addClass("hide")
				vm.showMore = true
			}
		},
		moreZzjNo: function() {
			fun_ShowMoreTable();
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
		exportExcel : function() {
			var trs=$("#dataGrid").children("tbody").children("tr");
			console.log('-->trs.length = ',trs.length);
			if(trs.length === 1){
				alert("请先查询到数据再进行导出，导出数据不分页")
				return false;
			}
			$("#btnExport").attr("disabled","disabled");
			$("#btnExport").val("导出中")
			js.showMessage("后台正在生成导出数据，请稍候...");
			$("#export_werks").val($("#search_werks").val())
			$("#export_order").val($("#search_order").val())
			$("#export_workshop").val($("#search_workshop").val())
			$("#export_line").val($("#search_line").val())
			$("#export_zzj_no").val($("#search_zzj_no").val())
			$("#export_process_flow").val($("#search_process_flow").val())
			$("#export_section").val($("#search_section").val())
			$("#export_material_no").val($("#search_material_no").val())
			$("#export_assembly_position").val($("#search_assembly_position").val())
			$("#export_use_workshop").val($("#search_use_workshop").val())
			$("#export_process").val($("#search_process").val())
			$("#export_surface_treatment").val($("#search_surface_treatment").val())
			$("#export_process_machine").val($("#search_process_machine").val())
			$("#export_subcontracting_type").val($("#search_subcontracting_type").val())
			$("#export_filling_size").val($("#search_filling_size").val())
			$("#export_specification").val($("#search_specification").val())
			$("#export_cailiao_type").val($("#search_cailiao_type").val())
			$("#export_aperture").val($("#search_aperture").val())
			$("#export_change_subject").val($("#search_change_subject").val())
			$("#export_change_type").val($("#search_change_type").val())
			$("#export_maiban").val($("#search_maiban").val())
			$("#export_banhou").val($("#search_banhou").val())
			$("#export_filling_size_max").val($("#search_filling_size_max").val())
			$("#exportForm").submit();
			//$("#btnExport").delay(5000).removeAttr("disabled");
			setTimeout(function(){ $("#btnExport").removeAttr("disabled");$("#btnExport").val("导出"); }, 8000);
		},
		viewDrawing: function(event,material_no){
			getPdf($("#search_werks").val(),material_no);
		}
	},
	watch: {
		'mydata': function(newVal){
			console.log('->vm.mydata.length : ' + vm.mydata.length)
			//$("#btnExport").attr("disabled","disabled");
		},
		search_werks : {
			handler:function(newVal,oldVal){
				getWorkShopList()
			}
	  	},
		search_workshop : {
			handler:function(newVal,oldVal){
				getLineList(newVal);
			}
	  	}
	  }
});
$(function () {
	var order_no=getUrlKey("order_no");
	var werks=getUrlKey("werks");
	var workshop=getUrlKey("workshop");
	var line=getUrlKey("line");
	var assembly_position = getUrlKey("assembly_position");
	if(undefined != assembly_position && 'undefined' != assembly_position && null != assembly_position && assembly_position!=''){
		vm.assembly_position = assembly_position;
		$("#search_assembly_position").val(assembly_position);
		$("#searchPage_assembly_position").val(assembly_position);
	} 
	$("#search_order").val(order_no);

	if(undefined != werks && 'undefined' != werks && null != werks && werks!=''){
		//$("#search_werks option[value='"+werks+"']").prop("selected",true);
		vm.search_werks = werks;
	}
	getWorkShopList();
	if(undefined != workshop && 'undefined' != workshop && null != werks && workshop!=''){
		//$("#search_werks option[value='"+werks+"']").prop("selected",true);
		vm.search_workshop = workshop;
	}
	if(undefined != line && 'undefined' != line && null != line && line!=''){
		//$("#search_werks option[value='"+werks+"']").prop("selected",true);
		vm.search_line = line;
	}
	
	$(document).on("click",".removeEcnTr",function(e){
		$(e.target).closest("tr").remove();		
	})
	
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
	});
	if(undefined != werks && 'undefined' != werks && null != werks && werks!=''){
		$("#searchPage_werks").val(werks)
		$("#searchPage_order").val(order_no)
		$("#searchPage_workshop").val(workshop)
		$("#searchPage_line").val(line)
	}
	fun_ShowTable();
	
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#search_zzj_no",function(e){		
		if(e.keyCode == 13){
			console.log(e.target.value)
			var mat = JSON.parse(e.target.value);	
			$("#search_order").val(mat.order_no);
			$("#search_zzj_no").val(mat.zzj_no);
		}
	})
});

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
		var mat = JSON.parse(result);	
		$("#search_order").val(mat.order_no);
		$("#search_zzj_no").val(mat.zzj_no);

}

function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		searchForm: $("#searchPageForm"),
		columnModel: [
    		{ label: '变更起始台数', name: 'change_from',align:'center', index: 'change_from', width: 120 ,sortable:false,frozen: true}, 		
    		{ label: '序号', name: 'no', align:'center', index: 'no', width: 50 ,frozen: true,sortable:true,editable:false}, 			
    		{ label: '*图号', name: 'material_no', align:'center', index: 'material_no', width: 150 ,frozen: true,sortable:true,editable:false,
    			formatter:function(val, obj, row, act){
    				vm.mydata.push(row)
    				//return "<i onClick='vm.viewDrawing(event,"+val+")' title='查看图纸' style='font-size:12px;color:green;cursor: pointer;' >"+val+"</i>";
    				return '<a href="#" title="查看图纸" onClick="vm.viewDrawing(event,\''+val+'\')">'+val+'</a>'
    			},
    		}, 
    		{ label: '*零部件号', name: 'zzj_no', align:'center',  index: 'zzj_no', width: 150 ,frozen: true,sortable:true,editable:false,
    			formatter:function(val, obj, row, act){
    	        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getException('"+row.werks+"','0','"+row.i_id+"')>"+val+"</a>";
    			},
    		}, 	
    		{ label: '*零部件名称', name: 'zzj_name', align:'center', index: 'zzj_name', width: 150 ,sortable:false,editable:false}, 		
    		{ label: '*物料描述', name: 'mat_description', align:'center', index: 'mat_description', width: 150 ,sortable:false,editable:false}, 	 		
    		{ label: '*物料类型', name: 'mat_type', align:'center', index: 'mat_type', width: 100 ,sortable:false,editable:false}, 	
    		{ label: '*材料/规格', name: 'specification', align:'center', index: 'specification', width: 100 ,sortable:false,editable:false}, 			
    		{ label: '下料尺寸', name: 'filling_size', index: 'filling_size', width: 100 ,sortable:false,editable:false},	
    		{ label: '*材料类型', name: 'cailiao_type', align:'center',  index: 'cailiao_type', width: 100 ,sortable:false,editable:false}, 			
    		{ label: '*装配位置', name: 'assembly_position', align:'center', index: 'assembly_position', width: 100 ,sortable:false,editable:false},
    		{ label: '*使用工序代码', name: 'process', align:'center', index: 'process', width: 100 ,sortable:false,editable:false}, 
    		{ label: '使用工序名称', name: 'process_name', align:'center', index: 'process_name', width: 100 ,sortable:false,editable:false}, 
    		{ label: '加工顺序', name: 'process_sequence', align:'center', index: 'process_sequence', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*工艺流程', name: 'process_flow', align:'center', index: 'process_flow', width: 100 ,sortable:false,editable:false}, 
    		{ label: '加工工时', name: 'process_time', align:'center', index: 'process_time', width: 100 ,sortable:false,editable:false}, 
    		{ label: '加工设备', name: 'process_machine', align:'center', index: 'process_machine', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*分包类型', name: 'subcontracting_type', align:'center', index: 'subcontracting_type', width: 100 ,sortable:false,editable:false}, 
    		{ label: '孔特征', name: 'aperture', align:'center', index: 'aperture', width: 100 ,sortable:false,editable:false}, 
    		{ label: '精度要求', name: 'accuracy_demand', align:'center', index: 'accuracy_demand', width: 100 ,sortable:false,editable:false}, 
    		{ label: '表面处理', name: 'surface_treatment', align:'center', index: 'surface_treatment', width: 100 ,sortable:false,editable:false}, 
    		{ label: '单重', name: 'weight', align:'center', index: 'weight', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*使用车间', name: 'use_workshop', align:'center', index: 'use_workshop', width: 100 ,sortable:false,editable:false}, 
    		{ label: '单位', name: 'unit', align:'center', index: 'unit', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*单车用量', name: 'quantity', align:'center', index: 'quantity', width: 100 ,sortable:false}, 
    		{ label: '*单车损耗%', name: 'loss', align:'center', index: 'loss', width: 100 ,sortable:false,editable:false}, 
    		{ label: '总重含损耗', name: 'total_weight', align:'center', index: 'total_weight', width: 100 ,sortable:false,editable:false}, 
    		{ label: '备注', name: 'memo', align:'center', index: 'memo', width: 100 ,sortable:false,editable:false}, 
    		{ label: '工艺备注', name: 'processflow_memo', align:'center', index: 'processflow_memo', width: 100 ,sortable:false,editable:false}, 
    		{ label: '*工段', name: 'section', align:'center', index: 'section', width: 100 ,sortable:false,editable:false}, 
    		{ label: '埋板', name: 'maiban', align:'center', index: 'maiban', width: 100 ,sortable:false,editable:false}, 
    		{ label: '板厚', name: 'banhou', align:'center', index: 'banhou', width: 100 ,sortable:false,editable:false}, 
    		{ label: '特殊大尺寸', name: 'filling_size_max', align:'center', index: 'filling_size_max', width: 100 ,sortable:false,editable:false}, 
    		{ label: 'SAP码', name: 'sap_mat', align:'center', index: 'sap_mat', width: 100 ,sortable:false,editable:false}, 
    		{ label: '变更说明', name: 'change_description', align:'center', index: 'change_description', width: 100 ,sortable:false,editable:false}, 
    		{ label: '变更主体', name: 'change_subject', align:'center', index: 'change_subject', width: 100 ,sortable:false,editable:false}, 
    		{ label: '变更类型', name: 'change_type', align:'center', index: 'change_type', width: 100 ,sortable:false,editable:false,
    			formatter:function(val){	//变更类型 1 技改 2 非技改
            		if("1"==val){return "技改";}
            		else if("2"==val){return "非技改";}
            		else{return "";}
            	}
    		}, 
    		{ label: '操作类型', name: 'operate_type', align:'center', index: 'operate_type', width: 100 ,hidden:false,
    			formatter:function(val, obj, row, act){
    				//操作类型 00 新增 01 修改 02 删除
    				if(val=="00")return "新增";
    				if(val=="01")return "修改";
    				if(val=="02")return "删除";
    				if(val==undefined)return "";
    			}
    		},
    		{ label: '创建人', name: 'creator', align:'center', index: 'creator', width: 100 ,sortable:false,editable:false}, 
    		{ label: '创建时间', name: 'creat_date', align:'center', index: 'creat_date', width: 150 ,sortable:false,editable:false}, 
    		{ label: '编辑人', name: 'editor', align:'center', index: 'editor', width: 100 ,sortable:false,editable:false}, 
    		{ label: '编辑时间', name: 'edit_date', align:'center', index: 'edit_date', width: 150 ,sortable:false,editable:false}, 
    		{ label: 'id', name: 'id', align:'center', index: 'id', width: 100 ,hidden:true}, 
    	],
    	onCellSelect:function(rowid,iCol,cellcontent,e){
    		vm.orderQty = $("#search_order").attr("orderQty")
			var row = $("#dataGrid").jqGrid('getRowData', rowid);
    		var trs=$("#ecnTable").children("tbody").children("tr");
			$.each(trs,function(index,tr){
				if(index > 0)tr.remove()
			})

			if(iCol === 1||iCol === 25){
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
							var addtr = '<tr><td>&nbsp;&nbsp;</td><td><input type="text" disabled="disabled" value="'+start+'"/></td><td><input type="text" disabled="disabled" value="'+end+'"/></td><td><input type="text" disabled="disabled" value="'+val+'"/></td></tr>'
							$("#ecnTable tbody").append(addtr);
						}
					}
				}
				layer.open({
					type : 1,
					offset : '50px',
					skin : 'layui-layer-molv',
					title : "变更起始台数 订单数量" + vm.orderQty + "台",
					area : [ '500px', '360px' ],
					shade : 0.5,
					shadeClose : true,
					content : jQuery("#ecnLayer"),
					btn : [ '确定'],
					btn1 : function(index) {
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
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			
		},
		pager : '#dataGridPage',
		shrinkToFit: false,
		width:4000,
		rowNum:15,
		rownumWidth: 15, 
		rowList : [ 15,50,500 ],
		cellEdit:false,
		cellurl:'#',
		cellsubmit:'clientArray'
    });
	
	$("#dataGrid").jqGrid("setFrozenColumns");
}

function getWorkShopList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
  	  async: false,
  	  data:{
  		  "WERKS":vm.search_werks,
  		  "MENU_KEY":"ZZJMES_PMD_MANAGE",
  	  },
  	  //async:false,
  	  success:function(resp){
  		  vm.workshoplist = resp.data;
  		  getLineList(vm.workshoplist[0].code)
  		  if(resp.data && resp.data.length &&  resp.data.length>0){
  			  vm.search_workshop = resp.data[0].CODE; 
  		  }
  	  }
    })
}
function getLineList(workshop){
	$.ajax({
  	  url:baseUrl + "masterdata/getUserLine",
  	  async: false,
  	  data:{
  		  "WERKS": vm.search_werks,
  		  "WORKSHOP": $("#search_workshop").val()?$("#search_workshop").val():workshop,
  		  "MENU_KEY": "ZZJMES_PMD_MANAGE",
  	  },
  	  success:function(resp){
  		 vm.linelist = resp.data;
  		if(resp.data && resp.data.length &&  resp.data.length>0){
  			 vm.search_line = resp.data[0].CODE; 
  		 }
  		
  	  }
    })
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
function dataGridMorePaste_1(){
	$('#dataGrid_1').bind('paste', function(e) {
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
				$("#newOperation_1").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid_1').jqGrid("saveCell", startRow + i, 1);
				$(cell).html(arr[i][j]);
			}
		}
	});
}

function getException(werks,plan_item_id,pmd_id){
	console.log("-->getException werks:" + werks + "|plan_item_id:" + plan_item_id + "|pmd_item_id:" + pmd_id)
	//默认通过plan_item_id查询工厂车零部件号等信息。没有计划时plan_item_id取0，通过pmd_item_id查询工厂车零部件号等信息
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>异常录入',
		area: ["600px", "400px"],
		content: "/web/zzjmes/product/productionException.html?plan_item_id="+plan_item_id + "&pmd_item_id=" + pmd_id,  
		btn : [ '确定' ,	'取消'],
		btn1 : function(index,layero) {
			console.log('btn1 ' + index)
			var win = top[layero.find('iframe')[0]['name']];
			var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
			$(btnSubmit).click();
			js.layer.close(index);
		},
		btn2 : function(index) {
			console.log('btn2 ' + index)
			js.layer.close(index);
			$("#btnConfirm").removeAttr("disabled");
		}
	};
	js.layer.open(options);
}

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}