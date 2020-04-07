var const_float_validate= /^[0-9]+[0-9]*\.?[0-9]*$/;//浮点数正则表达式
var const_float_validate_one= /^\d*\.?\d?$/;//一位浮点数正则表达式
var const_int_validate = /^[0-9]+[0-9]*$/;//整数正则表达式

/**
 * 计划批次Select
 * @returns
 */
function getBatchSelects(data,selectval,element,selectAll){
	var strs ="";
	if(selectAll!='' && selectAll!=null && selectAll!=undefined){
		 strs = "<option value=''>"+selectAll+"</option>";
	}
	$(element).html("");
	$.each(data, function(index, value) {
		if (selectval == value.batch) {
			strs += "<option value=" + value.batch + " quantity="+value.quantity+" selected='selected'" + ">"
					+ value.batch + "</option>";
		} else {
			strs += "<option value=" + value.batch + " quantity="+value.quantity+">" + value.batch
					+ "</option>";
		}
	});
	$(element).append(strs);
}

/*
 * 订单编号模糊查询 submitId： 用于提交的元素的id
 * werks：为元素ID，只能动态获取值，否则调用多次时都是取第一次传入的固定值
 */
function getZZJOrderNoSelect(elementId, submitId, fn_backcall, werks) {
	$(submitId).val("");
	var order={};
	var orderlist;
	// alert($(elementId).next().html())
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"search_order":input,
					"search_werks":$(werks).val()||''
			};
			
			$.ajax({
				url:baseURL+"zzjmes/order/getOrderList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					orderlist = response.page.list;
					var results = new Array();
					$.each(orderlist, function(index, value) {
						//console.log(value.order_no);
						results.push(value.order_no);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var order_name = "";
			var bus_type = "";
			var order_qty = "";
			$.each(orderlist, function(index, value) {
				if (value.order_no == item) {
					order_name = value.order_name;
					bus_type = value.internal_name;
					order_qty = value.order_qty + "台";
				}
			})
			return item + "  " + order_name + " " + bus_type + order_qty;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(orderlist, function(index, value) {
				if (value.order_no == item) {
					order=value;
					selectId = value.id;
					$(elementId).attr("orderId", selectId);
					$(submitId).val(value.order_no)
					$(elementId).attr("orderQty", value.order_qty);
					$(elementId).attr("busTypeCode",value.bus_type_code);				
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(order);
			}
		}
	});
}
// 复制多个值
function refreshMore(){
  $.each($("#more_div").find("input[type='text']"),function(i,input){
	  $(input).val("")
  })
}

function addMore(){
	var tr=$("<tr />");
	  var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' /> ")
	  var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text' style='width:100%;height:30px;border:0'> ")
	  $(tr).append(td_1)
	  $(tr).append(td_2)
	  $("#moreGrid tbody").append(tr)
}

function delMore(){	
	$.each($("#more_div").find("input[type='checkbox']"),function(i,input){
		  if($(input).is(':checked') ){
			  $(input).parent("td").parent("tr").remove()
		  }
	  })
}
//表格下复选框全选、反选;checkall:true全选、false反选
function check_All_unAll(tableId, checkall) {
	if (checkall) {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked", true);
	} else {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked",false);
	}
}


// 自制件通过图号获取图纸  tangj 2019-04-02
function getPdf(werks,material_no){
	js.loading(material_no+'图纸加载中...');
/*	var url =baseUrl+"statics/plugins/pdfjs-2.1.266/web/viewer.html?file="+"/web/statics/guide/新WMS操作手册.pdf&username="+123;
	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">'+material_no+'</i>',
			url,true, true);*/
	$.ajax({
        type: "get",
        url:baseURL+"masterdata/getMaterialNoMapFile",
        cache: false,  //禁用缓存
        data: {
        	"material_no":material_no,
        	"werks":werks
        },
        dataType: "json",
        success: function (response) {
        	if(response.code=='0'){
        		var url =baseUrl+"statics/plugins/pdfjs-2.1.266/web/viewer.html?file="+response.data.filePath+"&username="+response.data.username;
        		js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">'+material_no+'</i>',
        				url,true, true);
        	}else{
        		js.showErrorMessage("打开图纸失败："+response.msg);
        	}
        }
	})
	
	js.closeLoading();
}

/*
 * 检具模糊查询 submitId： 用于提交的元素的id
 */
function getTestToolNoSelect(elementId, fn_backcall) {
	var testTool={};
	var testToolList;
	var newstr="";
	$(elementId).typeahead({	
		source : function(input, process) {
			var newparam="";
			newstr=input;
			var inputArr=input.split(",");
			if(inputArr.length==1){
				newparam=inputArr[0];
			}else{
				newparam=inputArr[inputArr.length-1];
			}
			var data={
				"test_tool_no":newparam.toUpperCase(),
				"werks":vm.werks,
				"workshop":vm.workshop,
			};		
			return $.ajax({
				url:baseUrl + "zzjmes/qmTestRecord/getQmsTestToolList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					testToolList = response.data;
					var results = new Array();
					$.each(testToolList, function(index, value) {
						results.push(value.TEST_TOOL_NO);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var test_tool_name = "";
			$.each(testToolList, function(index, value) {
				if (value.TEST_TOOL_NO == item) {
					test_tool_name = value.TEST_TOOL_NAME;
				}
			})
			return item + "  " + test_tool_name;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(testToolList, function(index, value) {
				if (value.TEST_TOOL_NO == item) {
					testTool=value;
					selectId = value.TEST_TOOL_NAME;
					var s="";
					var inputArr=newstr.split(",");
					if(inputArr.length>1){
						for(var i=0;i<inputArr.length-1;i++){
							s+=inputArr[i]+",";
						}
					}
					$(elementId).val(s+item+",");	
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(testTool);
			}
		}
	});
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
/*
 * 机台模糊查询 submitId： 用于提交的元素的id
 */
function getZZJMachineNoSelect(elementId, fn_backcall, werks,workshop,line) {
	var machine={};
	var machinelist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"machine_code":input.toUpperCase(),
					"werks":$('#werks').val()||'',
					"workshop":$('#workshop').val()||'',
					"line":$('#line').val()||''
			};
			
			$.ajax({
				url:baseURL+"zzjmes/common/getMachineList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					machinelist = response.data;
					var results = new Array();
					$.each(machinelist, function(index, value) {
						results.push(value.machine_code);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var machine_name = "";
			$.each(machinelist, function(index, value) {
				if (value.machine_code == item) {
					machine_name = value.machine_name;
				}
			})
			return item + "  " + machine_name ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(machinelist, function(index, value) {
				if (value.machine_code == item) {
					machine=value;
					$(elementId).val(value.machine_code)
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(machine);
			}
		}
	});
}
/*
 * 机台模糊查询 submitId： 用于提交的元素的id
 */
function getAssemblyPositionNoSelect(elementId, fn_backcall,orderId) {
	var assemblyPosition={};
	var list;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
				"assembly_position":input.toUpperCase(),
				"werks":$('#werks').val()||'',
				"workshop":$('#workshop').val()||'',
				"line":$('#line').val()||'',
				"order_no":$(''+orderId).val()||'',
			};
			
			$.ajax({
				url:baseURL+"zzjmes/common/getAssemblyPositionList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					list = response.data;
					var results = new Array();
					$.each(list, function(index, value) {
						results.push(value.assembly_position);
					})
					return process(results);
				}
			});
		},
		items : 20,
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
				if (value.assembly_position == item) {
					assemblyPosition=value;
					$(elementId).val(value.assembly_position)
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(assemblyPosition);
			}
		}
	});
}