var mydata = [];
var lastrow,lastcell,lastcellname;
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		zzj_plan_batch :'',
		order_no: '',
    },
	created:function(){	
		var order_no=getUrlKey("order_no");
		var werks=getUrlKey("werks");
		if(undefined != order_no && 'undefined' != order_no && null != order_no && order_no!=''){
			this.order_no = order_no;
		}
		if(undefined != werks && 'undefined' != werks && null != werks && werks!=''){
			this.werks = werks;
		}else{
			this.werks = $("#werks").find("option").first().val();
		}
	},
	watch:{
		werks : {
		  handler:function(newVal,oldVal){
			  var workshop=getUrlKey("workshop");
			$.ajax({
				url:baseURL+"masterdata/getUserWorkshopByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":newVal,
					"MENU_KEY":'ZZJMES_MACHINE_PLAN_QUERY',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					if(undefined != workshop && 'undefined' != workshop && null != workshop && workshop!=''){
						vm.workshop = workshop;
					}else{
						if(response.data && response.data.length &&  response.data.length>0){
							vm.workshop=response.data[0]['code'];
						}
					}
					if(undefined != vm.workshop && 'undefined' != vm.workshop && null != vm.workshop && vm.workshop!=''){
						var line=getUrlKey("line");
						$.ajax({
							url:baseURL+"masterdata/getUserLine",
							dataType : "json",
							type : "post",
							data : {
								"WERKS":vm.werks,
								"WORKSHOP": vm.workshop,
								"MENU_KEY":'ZZJMES_MACHINE_PLAN_QUERY',
							},
							async: true,
							success: function (response) { 
								vm.line_list=response.data;
								if(undefined != line && 'undefined' != line && null != line && line!=''){
									vm.line = line;
								}else{
									if(response.data && response.data.length &&  response.data.length>0){
										vm.line=response.data[0]['code'];
									}
								}
								
							}
						});	
					}

					vm.getBatchSelects();
				}
			});	
		  }
		},
		workshop: {
			handler:function(newVal,oldVal){
				var line=getUrlKey("line");
				$.ajax({
					url:baseURL+"masterdata/getUserLine",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":vm.werks,
						"WORKSHOP":newVal,
						"MENU_KEY":'ZZJMES_MACHINE_PLAN_QUERY',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						if(undefined != line && 'undefined' != line && null != line && line!=''){
							vm.line = line;
						}else{
							if(response.data && response.data.length &&  response.data.length>0){
								vm.line=response.data[0]['code'];
							}
						}
						vm.getBatchSelects();
					}
				});	
			}
		},
		zzj_plan_batch: {
			handler: function(newVal,oldVal){
				var werks=getUrlKey("werks");
				if(undefined != werks && 'undefined' != werks && null != werks && werks!=''){
					vm.$nextTick(function(){
						vm.query();
					})
				}
			}
		}
	},
	methods: {
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,fn_cb_getOrderInfo_search);
		},
		getBatchSelects:function(selectval){
			if($("#search_order").val()!=''){
				$.ajax({
					type : "post",
					dataType : "json",
					async : false,
					url :baseUrl+ "/zzjmes/common/getPlanBatchList",
					data : {
						"werks" : this.werks,
						"workshop" : this.workshop,
						"line" : this.line,
						"order_no":$("#search_order").val()
					},
					success:function(response){
						getBatchSelects(response.data,selectval,"#zzj_plan_batch","全部");	
						var zzj_plan_batch = getUrlKey("zzj_plan_batch");
						if(undefined != zzj_plan_batch && 'undefined' != zzj_plan_batch && null != zzj_plan_batch && zzj_plan_batch!=''){
							vm.zzj_plan_batch = zzj_plan_batch;
						}
					}	
				});
			}
		},
		query:function(){
			if($("#search_order").val()==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			jQuery('#dataGrid').GridUnload();
			var subcontracting_type= $("#subcontracting_type").multipleSelect('getSelects').join(",")
			$("#subcontracting_type_submit").val(subcontracting_type);
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "zzjmes/machinePlan/queryPage");
			$('#dataGrid').dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
					{label: '报废', name: 'EMPTY_COL', index: '', align:"center",width: 40, sortable:false,formatter:function(val, obj, row, act){
							var actions = [];
							if($("#scrape_auth").val()=="Y" && Number(row.prod_quantity)>0){
								actions.push('<a href="#" onClick=scrape('+row.id+',"'+row.zzj_plan_batch+'","'+row.zzj_name+'") title="报废" ><i class="fa fa-pencil" style=\"color:blue\"></i></a>');
							}
							return actions.join("&nbsp;");
						},unformat:function(cellvalue, options, rowObject){
						return "";
					}}, 
					
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "100",align:"center",sortable:false,editable:true},
		            {label: '批次', name: 'zzj_plan_batch',index:"zzj_plan_batch", width: "50",align:"center",sortable:false,editable:false,},
		            {label: '车付数', name: 'quantity',index:"quantity", width: "60",align:"center",sortable:false,editable:false},
		            {label: '计划工序', name: 'plan_process',index:"plan_process", width: "70",align:"center",sortable:false,},
		            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "65",align:"center",sortable:false,},
		            {label: '已生产数量', name: 'prod_quantity',index:"prod_quantity", width: "75",align:"center",sortable:false,},
		            {label: '机台', name: 'machine',index:"machine", width: "110",align:"center",sortable:false, },
		            {label: '计划日期', name: 'plan_date',index:"plan_date", width: "90",align:"center",},
		        	{label: 'SAP码', name: 'sap_mat',index:"sap_mat", width: "70",align:"center",sortable:false,},
		            {label: '生产工单', name: 'product_order',index:"product_order", width: "75",align:"center",sortable:false,},
		            {label: '状态', name: 'status_desc',index:"status_desc", width: "60",align:"center",sortable:false,formatter: function(val, obj, row, act){
						var status_desc='';
						if(row.status=='0') status_desc='未锁定';
						if(row.status=='1') status_desc='已锁定';
						if(row.status=='2') status_desc='生产中';
						if(row.status=='3') status_desc='已完成';
						return status_desc;
					}},
		            {label: '工艺流程', name: 'process_flow',index:"process_flow", width: "170",align:"center",sortable:true,},
					{label: '使用工序', name: 'process',index:"process", width: "70",align:"center",sortable:false,},
					{label: '使用工序名称', name: 'process_name',index:"process_name", width: "90",align:"center",sortable:false,},
		            {label: '装配位置', name: 'assembly_position',index:"assembly_position", width: "120",align:"center",sortable:false,},
		            {label: '材料规格', name: 'specification',index:"specification", width: "75",align:"center",sortable:false,},
		            {label: '精度要求', name: 'accuracy_demand',index:"accuracy_demand", width: "75",align:"center",sortable:false,},
		            {label: '分包类型', name: 'subcontracting_type',index:"subcontracting_type", width: "75",align:"center",sortable:false,},
					{label: '加工顺序', name: 'process_sequence',index:"process_sequence", width: "75",align:"center",sortable:false,},
					{label: '创建人', name: 'creator',index:"creator", width: "75",align:"center",sortable:false,},
		            {label: '创建时间', name: 'create_date',index:"create_date", width: "120",align:"center",sortable:false,},
		            {label: 'werks',name:'werks',hidden:true,formatter:"String"},
		            {label: 'workshop',name:'workshop',hidden:true,formatter:"String"},
		            {label: 'line',name:'line',hidden:true,formatter:"String"},
		            {label: 'order_no',name:'order_no',hidden:true,formatter:"String"},
		            {label: 'pmd_item_id',name:'pmd_item_id',hidden:true,formatter:"String"},
		            {label: 'use_workshop',name:'use_workshop',hidden:true,formatter:"String"},
		            {label: 'single_qty',name:'single_qty',hidden:true,formatter:"String"},
		            {label: 'filling_size',name:'filling_size',hidden:true,formatter:"String"},
		            {label: 'order_desc',name:'order_desc',hidden:true,formatter:"String"},
		            {label: 'aperture',name:'aperture',hidden:true,formatter:"String"},
		        ],	
//				viewrecords: true,
//		        shrinkToFit:false,
		        showCheckbox:true,
		        multiselect: true,
		        shrinkToFit: false,
		        width:1900,
		    	viewrecords: true,
		        showRownum: true, 
		        rownumWidth: 25, 
		        rowList : [ 15,30,100 ],
//				rowNum: 15,  
			});
		},
		enter:function(e){
			vm.query();
		},
		exp:function(){
			$("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "zzjmes/machinePlan/expData");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
		},	
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
		            
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",sortable:true,editable:true,editrules:{required: true},},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "100",align:"center",sortable:false,editable:true},
		            {label: '批次', name: 'zzj_plan_batch',index:"zzj_plan_batch", width: "50",align:"center",sortable:false,editable:false,},
		            {label: '车付数', name: 'quantity',index:"quantity", width: "60",align:"center",sortable:false,editable:false},
		            {label: '计划工序', name: 'plan_process',index:"plan_process", width: "70",align:"center",sortable:false,},
		            {label: '计划数量', name: 'plan_quantity',index:"plan_quantity", width: "70",align:"center",sortable:false,},
		            {label: '已生产数量', name: 'prod_quantity',index:"prod_quantity", width: "80",align:"center",sortable:false,},
		            {label: '机台', name: 'machine',index:"machine", width: "110",align:"center",sortable:false, },
		            {label: '计划日期', name: 'plan_date',index:"plan_date", width: "90",align:"center",},
		        	{label: 'SAP码', name: 'sap_mat',index:"sap_mat", width: "70",align:"center",sortable:false,editable:true},
		            {label: '生产工单', name: 'product_order',index:"product_order", width: "75",align:"center",sortable:false,editable:true},
		            {label: '状态', name: 'status_desc',index:"status_desc", width: "60",align:"center",sortable:false},
		            {label: '创建人', name: 'creator',index:"creator", width: "75",align:"center",sortable:false,editable:true},
		            {label: '创建时间', name: 'create_date',index:"create_date", width: "120",align:"center",sortable:false,editable:true},
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				viewrecords: true,
		        shrinkToFit:false,
		        showCheckbox:true,
		        multiselect: true,
				rowNum: 30,  
				rowList : [30,50,100],
				cellsubmit:'clientArray',
		    });	
		},
		scrapeTable:function(mydata){
			jQuery('#scrapeGrid').GridUnload(); 
			$("#scrapeGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [
					{label:'id',name:'id', index: 'id',key: true ,hidden:true},
					{label: '操作', name: 'EMPTY_COL', index: '', align:"center",width: 40, sortable:false,formatter:function(val, obj, row, act){
							var actions = [];
							if($("#scrape_auth").val()=="Y"){
								actions.push('<a href="#" onClick="scrapeItem('+obj.rowId+')" title="报废" ><i class="fa fa-eraser " style=\"color:gray\"></i></a>');
							}					
							return actions.join("&nbsp;&nbsp;");
					}}, 
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "130",align:"center",sortable:false,},
		            {label: '生产数量', name: 'prod_quantity',index:"prod_quantity", width: "70",align:"center",sortable:false,},
		            {label: '<SPAN STYLE="COLOR:BLUE"><B>*报废数量</B></SPAN>', name: 'scrape_quantity',index:"scrape_quantity", width: "70",align:"center",sortable:false,editable:true,editrules:{ required:true,number:true},  formatter:'integer',},
		            {label: '<SPAN STYLE="COLOR:BLUE"><B>*报废原因</B></SPAN>', name: 'memo',index:"memo", width: "130",align:"center",sortable:false,editable:true,},
		            {label: '<SPAN STYLE="COLOR:BLUE"><B>*报废人</B></SPAN>', name: 'productor',index:"productor", width: "70",align:"center",sortable:false,editable:true,},
		            {label: '<SPAN STYLE="COLOR:BLUE"><B>*报废日期</B></SPAN>', name: 'product_date',index:"product_date", width: "90",align:"center",sortable:false,editable:true,editrules:{required: true},
		            	editoptions:{
	            		   dataInit:function(el){
		            		　 $(el).click(function(){
		            		　         WdatePicker();
		            	   });
		            	}},formatoptions:{defaultValue:formatDate(new Date()),edittype:"text"}
		            },
		            {label: '班组', name: 'workgroup_name',index:"workgroup_name", width: "80",align:"center",sortable:false,},
		            {label: '小班组', name: 'team_name',index:"team_name", width: "80",align:"center",sortable:false,},
		            {label: '生产批次', name: 'zzj_plan_batch',index:"zzj_plan_batch", width: "70",align:"center",sortable:false,},
		            {label: '生产工序', name: 'process',index:"process", width: "70",align:"center",sortable:false,},
		            {label: '加工机台', name: 'machine_code',index:"machine_code", width: "110",align:"center",sortable:false, },
		            {label: '工厂', name: 'werks_name',index:"werks_name", width: "90",align:"center", hidden:true},
		        	{label: '车间', name: 'workshop_name',index:"workshop_name", width: "70",align:"center",sortable:false, hidden:true},
		            {label: '线别', name: 'line_name',index:"line_name", width: "75",align:"center",sortable:false, hidden:true},           
		            {label: 'machine_plan_items_id',name:'machine_plan_items_id',index:'machine_plan_items_id',hidden:true},
		            {label: 'zzj_pmd_items_id',name:'zzj_pmd_items_id',index:'zzj_pmd_items_id',hidden:true},
		            {label: 'plan_quantity',name:'plan_quantity',index:'plan_quantity',hidden:true},
		            {label: 'werks', name: 'werks',index:"werks", hidden:true},
		            {label: 'workshop', name: 'workshop',index:"workshop", hidden:true},
		            {label: 'line', name: 'line',index:"line", hidden:true},
		            {label: '订单', name: 'order_no',index:"order_no",sortable:false,hidden:true},
				],	
				viewrecords: true,
				showRownum:false,
				cellEdit:true,
				cellurl:'#',
				cellsubmit:'clientArray',
		        shrinkToFit:false,
	            height:"100%",
				loadComplete : function(data) {
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if (data.code == 500) {
						js.showErrorMessage(data.msg, 1000 * 10);
						return false;
					}
				},
				beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
					var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
					if (cm[i].name == 'cb') {
						$('#scrapeGrid').jqGrid('setSelection', rowid);
					}
					return (cm[i].name == 'cb');
				},
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
					lastcellname=cellname;
				},
				afterSaveCell:function(rowid, cellname, value, iRow, iCol){
					var row =  $("#scrapeGrid").jqGrid('getRowData', rowid);
					if(cellname == "scrape_quantity"){
						if(Number(value) > Number(row.prod_quantity)){
							js.showErrorMessage("抱歉，不能超出已录入产量"+row.prod_quantity,3000);
							$("#scrapeGrid").jqGrid('setCell', Number(row.id), "scrape_quantity", '&nbsp;','editable-cell');
							return false;
						}
						if(value<=0){
							js.showErrorMessage("抱歉，报废数量必须大于0！",3000);
							$("#scrapeGrid").jqGrid('setCell', Number(row.id), "scrape_quantity", '&nbsp;','editable-cell');
							return false;
						}
					}
				}
		    });	
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
				  var values=[]
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
	  print:function(){	
			var printData=[];
			var ids=$('#dataGrid').jqGrid('getGridParam','selarrrow');
			if(ids.length == 0){
				alert("当前还没有任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {	
				var data = $("#dataGrid").jqGrid("getRowData",ids[i]);
				data.plan_batch_qty=data.quantity; 
				data.zzj_pmd_items_id=data.pmd_item_id;
				delete data.EMPTY_COL;
				delete data.creator;
				delete data.create_date;
				delete data.plan_date; 
				delete data.sap_mat; 
				delete data.plan_process; 
				delete data.status_desc;
				delete data.product_order;
				delete data.machine;
				delete data.prod_quantity;
				printData.push(data);
			}
			var url = baseURL+"zzjmes/jtOperation/labelPreview"
			 var form = $('<form />', {action : url, method:"post", style:"display:none;"}).appendTo('body');
			$(form).attr("target","_blank");
			$(form).append("<input type='hidden' name='label_list' value='"+JSON.stringify(printData)+"'>")
			form.submit();
		},
		reset:function(){
			vm.order_no='';
			vm.zzj_plan_batch='';
			$("#dataGrid").jqGrid('clearGridData');
			$("#machine").val("");
			$("#plan_process").val("");
			$("#zzj_no").val("");
			$("#product_order").val("");
			$("#process").val("");
			$("#assembly_position").val("");
			$("#specification").val("");
			$("#accuracy_demand").val("");
			$("#process_flow").val("");
			$("#accuracy_demand").val("");
		}
	}
});

$(function () {
	var werks=getUrlKey("werks");
	if(undefined == werks || 'undefined' == werks || null == werks || werks==''){
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
	}
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
				vm.zzj_plan_batch = val_json.zzj_plan_batch;
				vm.zzj_no =val_json.zzj_no;
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
				vm.line = val_json.line;
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
			//alert($(e.target).val())
			var tr=$(e.target).parent("td").parent("tr")
			
			var index = parseInt($("#more_div tbody").find("tr").index(tr));
			console.info(index)
			 var copy_text=$(e.target).val();		 
			 var dist_list=copy_text.split(" ");
			
			 if(dist_list.length <=0){
				 return false;
			 }
			 $.each(dist_list,function(i,value){
				 console.info(value)
				 $("#more_div tbody").find("tr").eq(index+i).find("input").val(value)
			 })
			 
		},100)	
	})
});
function scrape(machine_plan_items_id,zzj_plan_batch,zzj_name){
	$.ajax({
		type : "post",
		dataType : "json",
		async : false,
		url :baseUrl+ "/zzjmes/machinePlan/getOutputRecords",
		data : {
			"machine_plan_items_id":machine_plan_items_id,
			"zzj_plan_batch":zzj_plan_batch,
			"zzj_name":zzj_name
		},
		success:function(response){
            if(response.code=='0'){
            	vm.scrapeTable(response.data);
            	layer.open({
            		type : 1,
            		offset : '50px',
            		skin : 'layui-layer-molv',
            		title : "报废编辑",
            		area : [ '780px', '320px' ],
            		shade : 0,
            		shadeClose : false,
            		content : jQuery("#scrapeDiv"),
            		btn : [ '关闭'],
            		btn1 : function(index) {
            			layer.close(index);
            		}
            	});
            }
		}	
	});
}
function scrapeItem(rowid){
	$('#scrapeGrid').jqGrid("saveCell", lastrow, lastcell);
	var row =  $("#scrapeGrid").jqGrid('getRowData', rowid);
	if(row.scrape_quantity==null || row.scrape_quantity=="" ||row.scrape_quantity==0){
		js.showErrorMessage("请输入报废数量！",3000);
		return false;
	}
	if(row.memo==null || row.memo==""){
		js.showErrorMessage("请输入报废原因！",3000);
		return false;
	}
	if(row.productor==null || row.productor==""){
		js.showErrorMessage("请输入报废人！",3000);
		return false;
	}
	if(row.product_date==null || row.product_date==""){
		js.showErrorMessage("请输入报废日期！",3000);
		return false;
	}
	js.confirm("是否确定报废？",function(){
		$.ajax({
			url : baseURL+"zzjmes/jtOperation/scrapePmdOutInfo",
			method:'get',
			dataType:'json',
			async:false,
			data: row,
			success:function(response){
				js.showMessage(response.msg)
				if(response.code==0){
					$("#scrapeGrid").delRowData(rowid);
					var index =layer.index;// parent.layer.getFrameIndex(window.name); 
					layer.close(index);
				}
			}
		});
	});
}

function fn_cb_getOrderInfo_search(order){
	vm.order_no = order.order_no;
	vm.getBatchSelects();
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

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}
