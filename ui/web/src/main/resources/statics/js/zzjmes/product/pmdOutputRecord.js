var lastrow,lastcell,lastcellname;
var last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		workgroup:"",
		workgroup_list :[],
		team :"",
		team_list :[],
		process :"",
		process_list :[],
		zzj_plan_batch :'',
		order_no:"",
		menu_key : 'ZZJMES_PMD_OUTPUT_QUERY',
		bind_machine_list:[],
		machine:'',
		user:{},
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
		this.bind_machine_list = this.getJTProcess();
		if(this.bind_machine_list!=null && this.bind_machine_list.length>0){
			this.machine = this.bind_machine_list[0].machine_code;
			this.werks = this.bind_machine_list[0].werks;
			this.workshop = this.bind_machine_list[0].workshop;
			this.process = this.bind_machine_list[0].process_name||"";
			this.line = this.bind_machine_list[0].line||"";
			this.workgroup = this.bind_machine_list[0].workgroup_name||"";
			this.team = this.bind_machine_list[0].team_name||"";
		}
		this.getUser();
		
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			  vm.getUserWorkshop(newVal);
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				//alert(newVal)
				vm.$nextTick(function(){
					vm.getUserLine(newVal);
					vm.getProcess(vm.werks,newVal);
					vm.getWorkgroupList(newVal);
				})
				
			}
		},
		workgroup : {
			handler:function(newVal,oldVal){
				vm.$nextTick(function(){
					vm.getTeamList(newVal);
				})
				
			}
		},
	},
	methods: {
		getUser: function(){
			$.getJSON("/web/sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,vm.getBatchSelects);
		},
		getBatchSelects:function(order){
			vm.order_no = order.order_no;
			//console.info(order)
			$.ajax({
				type : "post",
				dataType : "json",
				async : false,
				url :baseUrl+ "/zzjmes/common/getPlanBatchList",
				data : {
					"werks" : this.werks,
					"workshop" : this.workshop,
					"line" : this.line,
					"order_no":order.order_no
				},
				success:function(response){
					getBatchSelects(response.data,"","#zzj_plan_batch","全部");	
				}	
			});
		},
		getUserWorkshop : function(werks){
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks,
					"MENU_KEY":vm.menu_key,
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					vm.workshop=vm.workshop||response.data[0]['code'];
					if(vm.workshop){
						vm.getUserLine(vm.workshop);
					}
				}
			});	
		},
		getUserLine :function(workshop){
			$.ajax({
				url:baseURL+"masterdata/getUserLine",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":vm.werks,
					"WORKSHOP":workshop,
					"MENU_KEY":vm.menu_key,
				},
				async: true,
				success: function (response) { 
					vm.line_list=response.data;
					vm.line=response.data[0]['code'];
				}
			});	
		},	
		getProcess:function(werks,workshop){
			 $.ajax({
	        	  url:baseUrl + "masterdata/getWorkshopProcessList",
	        	  data:{
	        		  "WERKS":werks,
	        		  "WORKSHOP":workshop,
	        		  "MENU_KEY":vm.menu_key,
	        		  "ALL_OPTION":'X'
	        	  },
	        	  success:function(resp){
	        		 vm.process_list = resp.data;
	        		// vm.sectionlist= resp.data;	        		 
	        	  }
	          })
		
		},
		getWorkgroupList : function(workshop){
			 $.ajax({
	        	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
	        	  data:{
	        		  "WERKS":vm.werks,
	        		  "WORKSHOP":workshop,
	        		  "MENU_KEY":vm.menu_key,
	        		  "ALL_OPTION":'X'
	        	  },
	        	  success:function(resp){
	        		 vm.workgroup_list = resp.data;	        		 
	        	  }
	          })
		},
		getTeamList : function(workgroup){
			$.ajax({
	        	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
	        	  data:{
	        		  "PARENT_WORKGROUP_NO":workgroup,
		    		  "WORKSHOP":vm.workshop,
		    		  "TYPE":'1'
	        	  },
	        	  success:function(resp){
	        		  vm.team_list = resp.result;	        		
	        	  }
	          })
		},
		getJTProcess : function(){  // 根据账号获取权限机台列表
			var process=[];
			$.ajax({
				url : baseURL+"zzjmes/common/getJTProcess",
				method:'get',
				dataType:'json',
				async:false,
				data:{
				},
				success:function(response){
					if(response.msg="success"){
						process=response.process;
					}
				}
			});
			return process;
		},
		query:function(){
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "zzjmes/jtOperation/queryOutputRecords");
			ajaxQuery();
			
		},
		enter:function(e){
			/**
			 * PC端扫描零部件，解析二维码
			 */
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				//alert($(e.target).val())
				vm.order_no=val_json.order_no;
				vm.zzj_plan_batch = val_json.zzj_plan_batch;
				vm.zzj_no=val_json.zzj_no;
				$(e.target).val(vm.zzj_no);
			}
			vm.$nextTick(function(){
				vm.query();
			})
			
		},
		exp:function(){
			$("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "zzjmes/jtOperation/expData");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
		},	
		
		
	}
});

$(function(){
	var now = new Date(); 
	var nowTime = now.getTime() ; 
	var day = now.getDay();
	var oneDayTime = 24*60*60*1000 ; 

	//显示周一
	var startTime = nowTime - (7-day)*oneDayTime ; 
	//显示周日
	var endTime =  nowTime; 
	//初始化日期时间
	var startday = new Date(startTime);
	var endday = new Date(endTime);
	$("#start_date").val(formatDate(startday));
	$("#end_date").val(formatDate(endday));


	
})

var ajaxQuery = function(){
	var data = $("#dataGrid").data("dataGrid");
	if (data) {
		$("#dataGrid").jqGrid('GridUnload');
		js.closeLoading();
		$(".btn").attr("disabled", false);
		/*$(vm.table_id).jqGrid("clearGridData");
		$(vm.table_id).jqGrid('setGridParam',{datatype:'local'}).trigger('reloadGrid'); */
	}
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [{
			label:'id',name:'id', index: 'id',key: true ,hidden:true},{ 
				label: '操作', name: 'EMPTY_COL', index: '', align:"center",width: 50, sortable:false,formatter:function(val, obj, row, act){
					var actions = [];
					console.info("user: "+vm.user.STAFF_NUMBER)
					if(row.editor.indexOf(vm.user.STAFF_NUMBER)>=0 && row.output_type=='1'){
						actions.push('<a  href="#" onClick="delItem('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times fa-lg" style=\"color:red;\"></i></a>');
					}					
				/*	if($("#scrape_auth").val()=="Y"&&row.output_type=='1'){
						actions.push('<a href="#" onClick="scrapeItem('+row.id+')" title="报废" ><i class="fa fa-eraser " style=\"color:gray\"></i></a>');
					}		*/			
					return actions.join("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				},unformat:function(cellvalue, options, rowObject){
				return "";
			}}, 
			{label: '记录类型', name: 'output_type',index:"output_type", width: "70",align:"center",sortable:false,formatter:function(val, obj, row, act){
				return val=="1"?"产量新增":"产量报废"
			}},
            {label: '图号', name: 'material_no',index:"material_no", width: "120",align:"center",sortable:false,}, 
            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",},
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "130",align:"center",sortable:false,},
            {label: '订单', name: 'order_desc',index:"order_desc", width: "150",align:"center",sortable:false,},
            {label: '工段', name: 'section',index:"section", width: "80",align:"center",sortable:false,},
            {label: '班组', name: 'workgroup',index:"workgroup", width: "80",align:"center",sortable:false,},
            {label: '生产批次', name: 'zzj_plan_batch',index:"zzj_plan_batch", width: "70",align:"center",sortable:false,},
            {label: '<span style="color:blue">数量</span>', name: 'quantity',index:"quantity", width: "70",align:"center",sortable:false,editable:true,editrules:{ required:true,number:true},  formatter:'integer',},
           /* {label: '报废数量', name: 'scrape_quantity',index:"scrape_quantity", width: "80",align:"center",sortable:false,editable:true,editrules:{ required:true,number:true},  formatter:'integer',},
            {label: '原因', name: 'memo',index:"memo", width: "130",align:"center",sortable:false,editable:true,},*/
            {label: '<span style="color:blue">生产日期</span>', name: 'product_date',index:"product_date", width: "80",align:"center",sortable:false,editable:true,editrules:{ required:true,date:true}, editoptions:{
    	        dataInit:function(e){
    	            $(e).click(function(e){
    	            	WdatePicker({dateFmt:'yyyy-MM-dd'})
    	            });
    	        }
    	    }},
            {label: '<span style="color:blue">加工人</span>', name: 'productor',index:"productor", width: "70",align:"center",sortable:false,editable:true,},
            {label: '生产工序', name: 'process',index:"process", width: "70",align:"center",sortable:false,},
            {label: '工艺流程', name: 'process_flow',index:"process_flow", width: "130",align:"center",sortable:false,},
            {label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "90",align:"center",sortable:false,},
            {label: '使用工序', name: 'user_process_name',index:"user_process_name", width: "70",align:"center",sortable:false,},           
            {label: '加工机台', name: 'machine_code',index:"machine_code", width: "110",align:"center",sortable:false, },
            {label: '工厂', name: 'werks_name',index:"werks_name", width: "90",align:"center",},
        	{label: '车间', name: 'workshop_name',index:"workshop_name", width: "70",align:"center",sortable:false,},
            {label: '线别', name: 'line_name',index:"line_name", width: "75",align:"center",sortable:false,},           
            {label: '录入人', name: 'editor',index:"editor", width: "75",align:"center",sortable:false,},
            {label: '录入时间', name: 'edit_date',index:"edit_date", width: "120",align:"center",sortable:false,},
            {label:'prod_quantity',name:'prod_quantity',index:'prod_quantity',hidden:true},
            {label:'last_process',name:'last_process',index:'last_process',hidden:true},
            {label:'machine_plan_items_id',name:'machine_plan_items_id',index:'machine_plan_items_id',hidden:true},
            {label:'zzj_pmd_items_id',name:'zzj_pmd_items_id',index:'zzj_pmd_items_id',hidden:true},
            {label:'plan_quantity',name:'plan_quantity',index:'plan_quantity',hidden:true},
            {label: 'werks', name: 'werks',index:"werks", hidden:true},
            {label: 'workshop', name: 'workshop',index:"workshop", hidden:true},
            {label: 'line', name: 'line',index:"line", hidden:true},
			{label: 'team', name: 'team',index:"team", hidden:true},
			{label:'next_process',name:'next_process',index:'next_process',hidden:true},
		],	
		viewrecords: true,
		showRownum : false,
        shrinkToFit:false,
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
        rowNum: 15,  
		rowList : [15,50 ,100],	
		gridComplete : function() {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			/*if (data.code == 500) {
				js.showErrorMessage(data.msg, 1000 * 10);
				return false;
			}*/
			var rows=$("#dataGrid").jqGrid('getRowData');
			
			rows.forEach(function(row,index,arr){
				$("#dataGrid").jqGrid('setCell',Number(row.id), "prod_quantity", row.quantity,'not-editable-cell');				
				if(row.editor.indexOf(vm.user.STAFF_NUMBER)>=0){					
					$("#dataGrid").jqGrid('setCell', Number(row.id), "quantity", row.quantity,'editable-cell');
				}else{					
					$("#dataGrid").jqGrid('setCell',Number(row.id), "quantity", row.quantity,'not-editable-cell');
				}
			})
		
		},
		 beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
				lastcellname=cellname;
			},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row =  $("#dataGrid").jqGrid('getRowData', rowid);
			/**
			 * 产量修改，校验产量数据是否有效:
			 * 数量<=计划数量-累计产量 且 <=上一道工序（可以通过下料明细ID关联取工艺流程判断）的累计产量
			 * 如果上一道工序有多道工序时，不判断；
			 */
			if(cellname=="quantity"){
				var able_qty = getPmdAbleQty(row);
				if (able_qty == null ) {
					$("#dataGrid").jqGrid('setCell', Number(row.id), "quantity",  row.prod_quantity,'editable-cell');
					return false;
				}
				/**if(able_qty==0 && row.last_process!=null && row.last_process!=""){
					js.showErrorMessage("抱歉，上工序："+row.last_process+"产量为0！",3000);
					$("#dataGrid").jqGrid('setCell', Number(row.id), "quantity",  row.prod_quantity,'editable-cell');
					return false;
				}
				if(value>Number(able_qty)){
					js.showErrorMessage("抱歉，不能超出可录入产量"+Number(able_qty),3000);
					$("#dataGrid").jqGrid('setCell', Number(row.id), "quantity", row.prod_quantity,'editable-cell');
					return false;
				}*/
				if(value<=0){
					js.showErrorMessage("抱歉，数量必须大于0，如需置0请点击删除！",3000);
					$("#dataGrid").jqGrid('setCell', Number(row.id), "quantity", row.prod_quantity,'editable-cell');
					return false;
				}

			}
			
			if(cellname == "scrape_quantity"){
				if(value > row.prod_quantity){
					js.showErrorMessage("抱歉，不能超出已录入产量"+prod_quantity,3000);
					$("#dataGrid").jqGrid('setCell', Number(row.id), "scrape_quantity", '&nbsp;','editable-cell');
					return false;
				}
				if(value<=0){
					js.showErrorMessage("抱歉，报废数量必须大于0！",3000);
					$("#dataGrid").jqGrid('setCell', Number(row.id), "scrape_quantity", '&nbsp;','editable-cell');
					return false;
				}
			}
			
			savePmdOutQty(row);
		},

	});
}

function getPmdAbleQty(row){
	var able_qty=null;
	$.ajax({
		url : baseURL+"zzjmes/jtOperation/getPmdAbleQty",
		method:'post',
		dataType:'json',
		async:false,
		data: row,
		success:function(response){
			if(response.msg=="success"){
				able_qty=response.data;
			}else{
				js.showErrorMessage(response.msg,10*1000)
			}
		}
	});
	return able_qty;
}

function savePmdOutQty(row){
	$.ajax({
		url : baseURL+"zzjmes/jtOperation/savePmdOutQty",
		method:'post',
		dataType:'json',
		async:false,
		data: row,
		success:function(response){
			//js.showMessage(response.msg)
			ajaxQuery();
		}
	});
}

function delItem(rowid){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var row =  $("#dataGrid").jqGrid('getRowData', rowid);
	if(confirm("是否确定删除该行数据？"))
		$.ajax({
			url : baseURL+"zzjmes/jtOperation/deletePmdOutInfo",
			method:'get',
			dataType:'json',
			async:false,
			data: row,
			success:function(response){
				
				if(response.code=="0"){
					js.showMessage(response.msg)
					$("#dataGrid").delRowData(rowid)
				}else
					js.showErrorMessage(response.msg,10*1000)
			}
		});
}

function scrapeItem(rowid){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var row =  $("#dataGrid").jqGrid('getRowData', rowid);
	if(row.scrape_quantity==null || row.scrape_quantity=="" ||row.scrape_quantity==0){
		js.showErrorMessage("请输入报废数量！",3000);
		return false;
	}
	if(row.memo==null || row.memo==""){
		js.showErrorMessage("请输入报废原因！",3000);
		return false;
	}
	
	if(confirm("是否确定报废？"))
		$.ajax({
			url : baseURL+"zzjmes/jtOperation/scrapePmdOutInfo",
			method:'get',
			dataType:'json',
			async:false,
			data: row,
			success:function(response){
				js.showMessage(response.msg)
				if(response.code=="0"){
					$("#dataGrid").delRowData(rowid)
				}
			}
		});
}

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
	vm.zzj_plan_batch = mat.zzj_plan_batch;
	vm.order_no= mat.order_no;
	
	$("#"+last_scan_ele).val(mat.zzj_no);
	$("#"+last_scan_ele).trigger(e);

}
