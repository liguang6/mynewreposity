var lastrow='';
var lastcell='';
var vm = new Vue({
	el:'#rrapp',
	data:{
		order_no : '', //订单
		batch : '', //批次
		batch_list : [],
		zzj_no : '', //零部件号
		werks : '',
		workshop : '',
		workshop_list :[],
		line:"",
		line_list :[],
		werks_name : '',
		workshop_name : '',
		head_id:0,
		zzj_pmd_items_id:0,
		test_qty:0,
		demand_qty:0,
	},
	watch : {
		werks : {
			handler:function(newVal,oldVal){
			  $.ajax({
				  url:baseURL+"masterdata/getUserWorkshopByWerks",
				  dataType : "json",
				  type : "post",
				  data : {
					  "WERKS":newVal,
					  "MENU_KEY":'ZZJMES_QC_TEST_RECORD',
				  },
				  async: true,
				  success: function (response) { 
					  vm.workshop_list=response.data;
					  if(response.data && response.data.length && response.data.length>0){
						  vm.workshop=response.data[0]['code'];
  
						  $.ajax({
							  url:baseURL+"masterdata/getUserLine",
							  dataType : "json",
							  type : "post",
							  data : {
								  "WERKS":vm.werks,
								  "WORKSHOP": vm.workshop,
								  "MENU_KEY":'ZZJMES_QC_TEST_RECORD',
							  },
							  async: true,
							  success: function (response) { 
								  vm.line_list=response.data;
								  if(response.data && response.data.length && response.data.length>0){
									  vm.line=response.data[0]['code'];
								  }else{
									  vm.line = '';
								  }
							  }
						  });
  
					  }else{
						  vm.workshop = '';
					  }
					  if(vm.order_no!=''){
						  getBatchSelects(vm.werks,vm.order_no);
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
						  "MENU_KEY":'ZZJMES_QC_TEST_RECORD',
					  },
					  async: true,
					  success: function (response) { 
						  vm.line_list=response.data;
						  if(response.data && response.data.length && response.data.length>0){
							  vm.line = response.data[0]['code'];
						  }else{
							  vm.line = "";
						  }
					  }
				  });	
			  }
		  },  
		order_no : {
			handler : function(newVal,oldVal){
				if(vm.order_no!=''){
					getBatchSelects(vm.werks,vm.order_no);
				}	
				vm.$nextTick(function(){
					if(vm.order_no!='' && vm.zzj_no!='' && vm.batch!=''){
						vm.search();
					}	
				});
			}
		},
		batch : {
			handler : function(newVal,oldVal){
				vm.$nextTick(function(){
					if(vm.zzj_no!='' && vm.order_no!=''){
						vm.search();
					}	
				})
			}
		},
		zzj_no : {
			handler : function(newVal,oldVal){
				if(vm.order_no!='' && vm.batch!=''){
					//vm.search();
				}						
			}
		},		
	},
	methods : {
		save:function(){
			$('#dataGrid1').jqGrid("saveCell", lastrow, lastcell);
			$('#dataGrid2').jqGrid("saveCell", lastrow, lastcell);
        	var rows=$("#dataGrid1").jqGrid('getRowData');
        	if(rows.length == 0){
				alert("当前还没有数据！");
				return false;
			}
        	var size_standard=rows[0].size_standard;
			var outward_standard=rows[0].outward_standard;
			var head_id=rows[0].head_id;
        	for(var i in rows){
        		var data=rows[i];
        		if(data.outward_standard=='' || 
        			data.size_standard=='' || data.outward_result=='' ||
        			data.size_result=='' || data.test_result==''){
        			js.showErrorMessage("生产检验第"+(Number(i)+1)+"行：数据录入不完整！");
        			return;
        		}
        		delete data.size_standard;
        		delete data.outward_standard;
        		delete data.head_id;
        		delete data.zzj_name;
        		delete data.product_test;
        		delete data.product_test_date;
        	}
        	var qc_rows=$("#dataGrid2").jqGrid('getRowData');
        	if(qc_rows.length == 0){
				alert("当前还没有数据！");
				return false;
			}
        	var qc_test=qc_rows[0].qc_test;
			var qc_test_date=qc_rows[0].qc_test_date;
        	for(var i in qc_rows){
        		var data=qc_rows[i];
        		if(data.outward_result=='' ||
        			data.size_result=='' || data.test_result==''){
        			js.showErrorMessage("品质检验第"+(Number(i)+1)+"行：数据录入不完整！");
        			return false;
        		}
        		delete data.qc_test;
        		delete data.qc_test_date;
        	}
        	js.loading("正在保存数据,请您耐心等待...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/qmTestRecord/save",
				data:{
					size_standard:size_standard,
					outward_standard:outward_standard,
					qc_test:qc_test,
					qc_test_date:qc_test_date,
					pro_detail_list:JSON.stringify(rows),
					qc_detail_list:JSON.stringify(qc_rows),
					head_id:head_id,
					result_type:'1'
				},
				type:"post",
				dataType:"json",
				async: true,
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
						js.showMessage("保存成功!");
						$("#dataGrid1").jqGrid('clearGridData');
						$("#dataGrid2").jqGrid('clearGridData');
						vm.order_no='';
						vm.zzj_no='';
						vm.test_qty=0;
						vm.demand_qty=0;
						vm.batch_list=[];
					}else{
						alert("保存失败：" + resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnSave").removeAttr("disabled");
				}
			});	
		},
		search:function(){
			if(vm.order_no==''){
				js.showErrorMessage("请输入订单号！");
           		return false;
			}
            if(vm.batch==''){
            	js.showErrorMessage("请选择批次！");
           		return false;
			}
    		js.loading("正在查询,请稍候...");
    		$("#btn_Search").val("正在查询...");	
    		$("#btn_Search").attr("disabled","disabled");
    		$.ajax({
				url : baseURL+"/zzjmes/qmTestRecord/getQcTestRules",
				type : "post",
				data : {
					"werks" : vm.werks,
					"workshop" : vm.workshop,
					"line" : vm.line,
					"order_no":vm.order_no,
					"zzj_plan_batch":vm.batch,
					"zzj_no":vm.zzj_no,
				},
				//async : false,
				error : function(response) {
					alert("获取数据失败，请联系系统管理员！")
				},
				success : function(response) {
					if(response.code=='0'){
						fun_jqGridData01(response.product_data);
						var mydata=[];
						var rules=response.data;
	                    var demand_quantity=0;
	                    var qc=0; // 自检抽样数
	                    var zzj_name="";
	                    var material_no="";
	                    var pmdList=response.pmdList;
	                    if(pmdList!=null && pmdList.length>0){
	                 	   demand_quantity=pmdList[0].demand_quantity;
	                 	   zzj_name=pmdList[0].zzj_name;
	                 	   material_no=pmdList[0].material_no;
	                    }else{
	                    	js.showErrorMessage("下料明细不存在！");
	                   		return false;
	                    }
	                    if(rules!=null && rules.length>0){
	                 	  var rule=rules[0];
	                   	  if(rule.TEST_RULES.indexOf("[{")<0 && rule.TEST_RULES.indexOf("}]")<0){
	                   		  js.showErrorMessage("检验规则录入格式错误！");
	                   		  return false;
	                   	  }
	                 	  var jsonArr = jQuery.parseJSON(rule.TEST_RULES);
	                 	  for(var i in jsonArr){
	                 		  var from=parseInt(jsonArr[i].from);
	                 		  var to=parseInt(jsonArr[i].to);
	                 		  if(demand_quantity>=from && demand_quantity<=to){
	                 			  if(jsonArr[i].qc!=''){
	                 				 qc=parseInt(jsonArr[i].qc!='全检' ? jsonArr[i].qc : demand_quantity);
		                 			 break;
	                 			  }
	                 		  }
	                 	  }
	                    }
	                    vm.test_qty=qc;
	                    vm.demand_qty=demand_quantity;
	                    if(qc==0){
	                    	js.showErrorMessage("该需求数量内未配置检验规则！");
	                   		  return false;
	                    }
	                    while(qc>0){
	                 	   var testRecord={};
	                 	   testRecord.test_result="OK";
	                 	   testRecord.qc_test_date=formatDate(new Date());
	                 	   testRecord.zzj_name=zzj_name;
	                 	   mydata.push(testRecord);
	                 	   qc--;
	                    }
						fun_jqGridData02(mydata);
						if(material_no!=''){
							getPdf(vm.werks,material_no);
						}
					}else{
						js.showErrorMessage(response.msg);
					}
				},
				complete:function(XMLHttpRequest, textStatus){
					js.closeLoading();
					$("#btn_Search").val("查询");	
					$("#btn_Search").removeAttr("disabled");
				}
    		});
    	},
		/*************** tangj end********************/
	},
	created:function(){
		this.werks = $("#werks").find("option").first().val();
	}
})	

$(function () {

	getZZJOrderNoSelect("#order_no", null, function(order){
		vm.order_no = order.order_no;
	});
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
			vm.search();
		}
	});
	$('#dataGrid1').dataGrid({
		//searchForm: $("#searchForm"),
		columnModel: [		
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*检具编号</B></SPAN>', name: 'test_tool_no',index:"test_tool_no", width: "150",align:"center",sortable:false,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*外观标准</B></SPAN>', name: 'outward_standard',index:"outward_standard", width: "160",align:"center",editable:true,editoptions:{
        		dataInit:function(el){
	          		　 $(el).keypress(function(){
	          		　         getTestStandardNoSelect(el,null);
	          		　 });
          	     }}  // ,dataEvents:[{type:"change",fn:function(e){}},]
            },
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*尺寸标准</B></SPAN>', name: 'size_standard',index:"size_standard", width: "120",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*外观检验结果</B></SPAN>', name: 'outward_result',index:"outward_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*尺寸检验结果</B></SPAN>', name: 'size_result',index:"size_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*判定</B></SPAN>', name: 'test_result',index:"test_result", width: "70",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){}},	
				]},
			},
            {label: '检验员', name: 'product_test',index:"product_test", width: "90",align:"center",sortable:false},
            {label: '检验日期', name: 'product_test_date',index:"product_test_date", width: "120",align:"center",},
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "100",align:"center",sortable:false},
            {label:'ID',name:'id',hidden:true,formatter:"integer"},
            {label:'HEAD_ID',name:'head_id',hidden:true,formatter:"integer"},
        ],
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var grid=$("#dataGrid1");
			var rowData = $(grid).jqGrid('getRowData',rowid);
			// 第一行录入数据  自动向下填充（除判定）
			if(iRow==1 && cellname!='test_result'){
				var ids=  $(grid).jqGrid("getDataIDs");
				for(var i in ids){
					$(grid).jqGrid("setCell",ids[i],cellname,value);
				}
			}
		},
        shrinkToFit: false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
		cellEdit:true,
		rowNum: 10000,
	});
	$('#dataGrid2').dataGrid({
		columnModel: [		
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*外观检验结果</B></SPAN>', name: 'outward_result',index:"outward_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*尺寸检验结果</B></SPAN>', name: 'size_result',index:"size_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*判定</B></SPAN>', name: 'test_result',index:"test_result", width: "120",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){}},	
				]},
			},
			{label: '<SPAN STYLE="COLOR:BLUE"><B>复判</B></SPAN>', name: 'retest_result',index:"retest_result", width: "120",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){}},	
				]},
			},
			{label: '<SPAN STYLE="COLOR:BLUE"><B>*检验员</B></SPAN>', name: 'qc_test',index:"qc_test", width: "90",align:"center",editable:true,sortable:false},
            {label: '<SPAN STYLE="COLOR:BLUE"><B>*检验日期</B></SPAN>', name: 'qc_test_date',index:"qc_test_date", width: "120",align:"center",editable:true,editrules:{required: true},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　         WdatePicker();
            		  });
            	}}
            },
        ],
        loadComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
			var ids = $("#dataGrid2").getDataIDs();//所有的id
			for(var i in ids){
				$("#dataGrid2").jqGrid('setCell', ids[i], 'retest_result', '','not-editable-cell');
			}
		},
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			console.log("iRow-iCol",cellname+":"+iRow+"-"+iCol+":"+value);
			var grid=$("#dataGrid2");
			var rowData = $(grid).jqGrid('getRowData',rowid);
			// 第一行录入数据  自动向下填充（除判定）
			if(iRow==1 && cellname!='test_result' && cellname!='retest_result'){
				var ids=  $(grid).jqGrid("getDataIDs");
				for(var i in ids){
					$(grid).jqGrid("setCell",ids[i],cellname,value);
				}
			}
			if(cellname=='test_result'){
                if(value=='NG'){            
             	    $("#dataGrid2").jqGrid('setCell', rowid, 'retest_result', '','edit-cell');
             	    $("#dataGrid2").find("#"+rowid).children().eq(iCol+1).removeClass('not-editable-cell');
                }
                if(value=='OK'){
             	   $("#dataGrid2").jqGrid('setCell', rowid, 'retest_result', ' ','not-editable-cell');
                }
			}
		},
        shrinkToFit: false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
		cellEdit:true,
		rowNum: 10000,
	});	
})

var getBatchSelects=(werks,order_no)=>{
	$.ajax({
		type : "post",
		dataType : "json",
		async : false,
		url : baseURL+"/zzjmes/common/getPlanBatchList",
		data : {
			"werks" : werks,
			"order_no":order_no
		},
		success:function(response){
			if(response.data==null){
				vm.batch_list=[];
			}else
				vm.batch_list=response.data;
		}	
	});
}
	
function fun_jqGridData01(report01Data){
	$("#dataGrid1").jqGrid('clearGridData');
    vm.$nextTick(function(){//数据渲染后调用
		$("#dataGrid1").jqGrid('setGridParam',{  // 重新加载数据
		      datatype:'local',
		      data : report01Data,
		      page:1
		}).trigger("reloadGrid");
		js.closeLoading();
    })
	$('#dataGrid1').setGridHeight(165);
}

function fun_jqGridData02(report02Data){
	$("#dataGrid2").jqGrid('clearGridData');
	$("#dataGrid2").jqGrid('setGridParam',{  // 重新加载数据
	      datatype:'local',
	      data : report02Data,
	      page:1
	}).trigger("reloadGrid");
}
/*
 * 外观标准模糊查询
 */
function getTestStandardNoSelect(elementId, fn_backcall) {
	var standard={};
	var list;
	$(elementId).typeahead({	
		source : function(input, process) {
			var data={
				//"type":"外观标准",
				"standard_name":input,
			};		
			return $.ajax({
				url:baseUrl + "zzjmes/qmTestRecord/getQmsTestStandard",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					list = response.data;
					var results = new Array();
					$.each(list, function(index, value) {
						results.push(value.STANDARD_NAME);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			return item ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(list, function(index, value) {
				if (value.STANDARD_NAME == item) {
					$(elementId).val(item);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(standard);
			}
		}
	});
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