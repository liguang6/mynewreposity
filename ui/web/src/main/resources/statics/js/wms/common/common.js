//jqGrid的配置信息
$.jgrid.defaults.width = 1000;
$.jgrid.defaults.responsive = true;
$.jgrid.defaults.styleUI = 'Bootstrap';

$(document).ready(function(){
	$("input").attr("autocomplete","off");
})

// 表单数据转JSON对象
$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

// js获取项目根路径，如： http://localhost:8083/vmes
function getRootPath(){
    // 获取当前网址，如： http://localhost:8083/vmes/dept/xxx
    var curWwwPath=window.document.location.href;
    // 获取主机地址之后的目录，如： vmes/dept/xxx
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    // 获取主机地址，如： http://localhost:8083
    var localhostPaht=curWwwPath.substring(0,pos);
    // 获取带"/"的项目名，如：/vmes
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName+"/");
}

function getContextPath() {
    var pathName = document.location.pathname;
    var index = pathName.substr(1).indexOf("/");
    var result = pathName.substr(0,index+1);
    return result;

}

var baseURL = getContextPath()+"/";



// 工具集合Tools
window.T = {};

// 获取请求参数
// 使用示例
// location.href = http://localhost:8080/index.html?id=123
// T.p('id') --> 123;
var url = function(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
};
T.p = url;

// 全局配置
$.ajaxSetup({
	dataType: "json",
	cache: false,
	complete:function(XMLHttpRequest,textStatus){
        if(textStatus=="parsererror"){
          // SESSION超时，点击页面按钮，跳转到登录页面
          var responseText=XMLHttpRequest.responseText;
          if(responseText.indexOf("请登录")>=0){
             console.log("XMLHttpRequest",responseText.indexOf("请登录"));
             var p = window; 
             while(p != p.parent){ 
                p = p.parent; 
             } 
             js.showErrorMessage("登陆超时,请刷新页面重新登陆！");
             p.location.href="login.html";
          }
        } else if(textStatus=="error"){
        js.showErrorMessage("请求出错,请联系管理员！");
        } else if(textStatus=="timeout"){
        js.showErrorMessage("请求超时,请联系管理员！");
        }
    }

});

// 重写alert
window.alert = function(msg, callback){
	try {
		parent.layer.alert(msg, function(index){
			parent.layer.close(index);
			if(typeof(callback) === "function"){
				callback("ok");
			}
		});
	} catch(e){
		layer.alert(msg, function(index){
			layer.close(index);
			if(typeof(callback) === "function"){
				callback("ok");
			}
		});
	}
}

// 重写confirm式样框
/*
 * window.confirm = function(msg, callback){ parent.layer.confirm(msg, {btn:
 * ['确定','取消']}, function(index){//确定事件 if(typeof(callback) === "function"){
 * parent.layer.close(index); callback("ok"); } }); }
 */

// 选择一条记录
function getSelectedRow() {
    var grid = $("#dataGrid");
    var rowKey = grid.getGridParam("selrow");
    // alert(rowKey)
    if(!rowKey){
    	alert("请选择一条记录");
    	return ;
    }
    
    var selectedIDs = grid.getGridParam("selarrrow");
    if(selectedIDs.length > 1){
    	alert("只能选择一条记录");
    	return ;
    }
    
    return rowKey;
}

// 选择多条记录
function getSelectedRows(table_id) {
	table_id=table_id||"#dataGrid"
    var grid = $(table_id);
    var rowKey = grid.getGridParam("selrow");
    if(rowKey==undefined||rowKey.trim().length==0){
    	alert("请选择至少一条记录");
    	return ;
    }
    
    return grid.getGridParam("selarrrow");
}

// 判断是否为空
function isBlank(value) {
    return !value || !/\S/.test(value)
}

function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return decodeURI(r[2]); return null; 
} 

function getDeptList(deptType){
	var deptList=[];
	$.ajax({
		url:baseURL+"masterdata/dept/ztreeDepts",
		dataType : "json",
		type : "post",
		data : {
			deptType:deptType
		},
		async: false,
		success: function (response) { 
			deptList=response;
		}
	})
	return deptList;
}

/*
 * 填充下拉列表 with id=>value;包括全部选项
 */
function getSelects(data, selectval, element,defaultVal,valName) {	
	var strs ="";
	if(defaultVal!=undefined){
		strs = "<option value=''>"+defaultVal+"</option>";
	}
	$(element).html("");
	$.each(data, function(index, value) {
		if(valName=="name"){
			if (selectval!=null &&(selectval == value.id || selectval == value.name || selectval == value.deptId)) {
				strs += "<option value=" + value.name +(value.deptId?(" deptId="+value.deptId):"")+ " selected='selected'" + ">"
						+ value.name + "</option>";
			} else {
				strs += "<option value=" + value.name +(value.deptId?(" org_id="+value.deptId):"")+ ">" + value.name
						+ "</option>";
			}
		}else{
			if (selectval!=null &&(selectval == value.id || selectval == value.name || selectval == value.deptId)) {
				strs += "<option value="  +(value.deptId?value.deptId:value.id)+ " selected='selected'" + ">"
						+ value.name + "</option>";
			} else {
				strs += "<option value=" +(value.deptId?value.deptId:value.id)+ ">" + value.name
						+ "</option>";
			}
		}
	});
	$(element).append(strs);
}

function getMergeTable(result,columns,rowsGroup,filename){
	/**
	 * 创建table
	 */
	var table=document.getElementById("tb_excel");
	
	//var table=$("<table id='tb_excel'></table>")
	
	/**
	 * 创建table head
	 */
	var table_head=$("<tr />");
	$.each(columns,function(i,column){
		var th=$("<th />");
		th.attr("class",column.class);
		th.attr("width",column.width);
		th.html(column.title);
		$(table_head).append(th);
	})
	
	$(table).append($(table_head));
	
	var warp = document.createDocumentFragment();// 创建文档碎片节点,最后渲染该碎片节点，减少浏览器渲染消耗的资源
	
	var data_process={};
	data_process.result=result;
	data_process.columns=columns;
	data_process.rowsGroup=rowsGroup;
	

    var curWwwPath=window.document.location.href;  
    // 获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName=window.document.location.pathname;  
    var pos=curWwwPath.indexOf(pathName);  
    // 获取主机地址，如： http://localhost:8083
    var localhostPath=curWwwPath.substring(0,pos);  
    // 获取带"/"的项目名，如：/uimcardprj
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);  
    var baseRoot = localhostPath+projectName;  
	//alert(localhostPath);
	
	var worker=new Worker(localhostPath+"/statics/js/mergeTableCell.js")
	worker.postMessage(data_process);
	
	worker.onmessage=function(event){
		
	var trs_data=event.data;
	//alert(JSON.stringify(trs_data))
	$.each(trs_data,function(i,tr_obj){
		var tr=$("<tr />");
		$.each(tr_obj,function(j,td_obj){
			var td=$("<td />");
			td.attr("class",td_obj.class);
			td.attr("id",td_obj.id);
			td.attr("rowspan",td_obj.rowspan);
			td.attr("width",td_obj.width);
			td.html(td_obj.html);

			if(!td_obj.hidden){
				$(td).appendTo(tr)
			}
			
		});
		$(warp).append(tr);
	})
	
	$(table).append($(warp));
	//alert($(table).html())
	
	/**
	 * 导出excel
	 */
	htmlToExcel("tb_excel", "", "",filename,"");
	
	
	//导出后清除表格
	$(table).empty();
	//document.body.removeChild(table);
	worker.terminate();
	}
}
function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
}
function close(){
	try {
		var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		parent.layer.close(index); //再执行关闭 
	} catch(e){
		var index = layer.getFrameIndex(window.name); //先得到当前iframe层的索引
		layer.close(index); //再执行关闭 
	}
}
/*
 * 工厂代码模糊查询 submitId： 用于提交的元素的id
 */
function getPlantNoSelect(elementId, submitId, fn_backcall) {

	$(submitId).val("")
	var plant={};
	var plantlist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"werks":input
			};
			$.ajax({
				url:baseURL+"common/getPlantList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					plantlist = response.list;
					var results = new Array();
					$.each(plantlist, function(index, value) {
						results.push(value.WERKS);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var werks = "";
			var werksName = "";
			$.each(plantlist, function(index, value) {
				if (value.WERKS == item) {
					werks = value.WERKS;
					werksName = value.WERKSNAME;
				}
			})
			return werks + "  " + werksName ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(plantlist, function(index, value) {
				if (value.werks == item) {
					werks=value;
					//selectId = value.id;
					$(submitId).val(value.WERKS);
					$(submitId).attr("WERKS_NAME",value.WERKSNAME);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(item);
			}
		}
	});
}
/*
 * 仓库代码模糊查询 submitId： 用于提交的元素的id
 */
function getWhNoSelect(elementId, submitId, fn_backcall) {

	$(submitId).val("")
	var wh={};
	var whlist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
//					"werks":werks,
					"whNumber":input
			};
			$.ajax({
				url:baseURL+"common/getWhList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function(index, value) {
						results.push(value.WHNUMBER);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function(index, value) {
				if (value.WHNUMBER == item) {
					whNumber = value.WHNUMBER;
					whName = value.WHNAME;
				}
			})
			return whNumber + "  " + whName ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(whlist, function(index, value) {
				if (value.WHNUMBER == item) {
					whNumber=value;
					//selectId = value.id;
					$(submitId).val(value.WHNUMBER);
					$(submitId).attr("WH_NAME",value.WHNAME);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}

/*
 * 供应商代码模糊查询 submitId： 用于提交的元素的id
 */
function getVendorNoSelect(elementId, submitId, fn_backcall, werks) {
	werks = werks || "";
	$(submitId).val("")
	var vendor={};
	var vendorlist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"lifnr":input,
					"werks":werks
			};
			$.ajax({
				url:baseURL+"common/getVendorList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					vendorlist = response.list;
					var results = new Array();
					$.each(vendorlist, function(index, value) {
						results.push(value.LIFNR);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var lifnr = "";
			var name1 = "";
			$.each(vendorlist, function(index, value) {
				if (value.LIFNR == item) {
					lifnr = value.LIFNR;
					name1 = value.NAME1;
				}
			})
			return lifnr + "  " + name1 ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			var lifnr ;
			$.each(vendorlist, function(index, value) {
				if (value.LIFNR == item) {
					lifnr=value;
					$(elementId).val(value.LIFNR)
					$(submitId).val(value.NAME1)
				}
			})
			console.info(JSON.stringify(lifnr))
			if (typeof (fn_backcall) == "function") {
				
				fn_backcall(lifnr);
			}
		}
	});
}
/*
 * 仓库代码模糊查询 submitId： 用于提交的元素的id; maktx
 */
function getMaterialNoSelect(werks,elementId,maktx ,submitId, fn_backcall) {

	$(submitId).val("");
	var material={};
	var materiallist;
	$(elementId).typeahead({		
		source : function(input, process) {
			//alert($("#werks").val());
			var data={
				"werks":$("#werks").val(),
				"matnr":input
			};
			$.ajax({
				url:baseURL+"common/getMaterialList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) {
					materiallist = response.list;
					var results = new Array();
					$.each(materiallist, function(index, value) {
						results.push(value.MATNR);
					})
					return process(results);
				}
			});
		},
		items : 10,
		highlighter : function(item) {
			var matnr = "";
			var maktx = "";
			$.each(materiallist, function(index, value) {
				if (value.MATNR == item) {
					matnr = value.MATNR;
					maktx = value.MAKTX;
				}
			})
			return matnr + " " + maktx ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(materiallist, function(index, value) {
				if (value.MATNR == item) {
					matnr=value;
					//selectId = value.id;
					$(submitId).val(value.MATNR);
					$(maktx).val(value.MAKTX);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(matnr);
			}
		}
	});
}

function getBusinessList(business_class,MENU_KEY,WERKS){
	var list=[];
	WERKS = WERKS || '';
	$.ajax({
		url:baseURL+"common/getBusinessList",
		dataType : "json",
		type : "post",
		async: false,
		data : {
			business_class:business_class,
			MENU_KEY: MENU_KEY,
			WERKS: WERKS
		},
		success:function(response){
			list=response.data;
		}
	})
	return list;
}


function getUserSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"userName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getUserList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.USERNAME);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.USERNAME == item) {
					whNumber = value.USERID;
					whName = value.USERNAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.USERNAME == item) {
					whNumber = value;
					//selectId = value.id;
					$("#userId").val(value.USERID);
					$(submitId).val(value.USERID)
				}


			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}


function getRoleSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"roleName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getRoleList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.ROLEID);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.ROLEID == item) {
					whNumber = value.ROLEID;
					whName = value.ROLENAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.ROLENAME == item) {
					whNumber = value;
					//selectId = value.id;
					$("#roleId").val(value.ROLEID);
					$(submitId).val(value.ROLEID)
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}


function getMenuSelect(elementId, submitId, fn_backcall) {
	$(submitId).val("")
	var wh = {};
	var whlist;
	$(elementId).typeahead({
		source: function (input, process) {
			var data = {
				"menuName": input
			};
			$.ajax({
				url: baseURL + "sys/user/getMenuList",
				dataType: "json",
				type: "post",
				data: data,
				success: function (response) {
					whlist = response.list;
					var results = new Array();
					$.each(whlist, function (index, value) {
						results.push(value.MENUNAME);
					})
					return process(results);
				}, error: function () {
					alert("error");
				}
			});
		},
		items: 20,
		highlighter: function (item) {
			var whNumber = "";
			var whName = "";
			$.each(whlist, function (index, value) {
				if (value.MENUNAME == item) {
					whNumber = value.MENUID;
					whName = value.MENUNAME;
				}
			})
			return whNumber + "  " + whName;
		},
		matcher: function (item) {
			return true;
		},
		updater: function (item) {
			return item;
		},
		afterSelect: function (item) {
			$.each(whlist, function (index, value) {
				if (value.MENUNAME == item) {
					whNumber = value;
					$("#menuId").val(value.MENUID);
					$(submitId).val(value.MENUID);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(whNumber);
			}
		}
	});
}

/**格局化日期：yyyy-MM-dd */
function formatDate(date) { 
	var myyear = date.getFullYear(); 
	var mymonth = date.getMonth()+1; 
	var myweekday = date.getDate();
	if(mymonth < 10){ 
		mymonth = "0" + mymonth; 
	} 
	if(myweekday < 10){ 
		myweekday = "0" + myweekday; 
	} 
	return (myyear+"-"+mymonth + "-" + myweekday); 
}

function getCurDate(){
	var cd=new Date();
	l_y = cd.getFullYear();
	l_m = cd.getMonth() + 1;
	l_d = cd.getDate();
	l_m = l_m< 10 ? "0" + l_m :l_m;
	l_d = l_d < 10 ? "0" + l_d : l_d;
	return l_y+"-"+l_m+"-"+l_d;
}

function getDictList(dictType){
	var list=[];
	$.ajax({
		url:baseURL+"common/getDictList",
		dataType : "json",
		type : "post",
		async: false,
		data : {
			type:dictType
		},
		success:function(response){
			list=response.data;
		}
	})
	return list;
}
function more(e){
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>选择更多',
		area: ["400px", "380px"],
		content: baseURL +"wms/query/conditionChoose.html",  
		btn: ['<i class="fa fa-check"></i> 确定','<i class="fa"></i> 删除'],
		success:function(layero, index){
			var win = layero.find('iframe')[0].contentWindow;
			win.vm.showTable($(e).val());
		},
		// 确定
		btn1:function(index,layero){
			var win = layero.find('iframe')[0].contentWindow;
			win.vm.getTableData();
			var returnValue= $("#returnValue", layero.find("iframe")[0].contentWindow.document);
			var retVal='';
			$(e).val($(returnValue).val());
			try {
				parent.layer.close(index);
				if(typeof listselectCallback == 'function'){
					parent.layer.close(0);
				}
			} catch (e) {
				layer.close(index);
				if(typeof listselectCallback == 'function'){
					layer.close(0);
				}
			}
			
		},
		// 删除
		btn2:function(index,layero){
			var win = layero.find('iframe')[0].contentWindow;
			var dataGrid= $("#dataGrid", layero.find("iframe")[0].contentWindow.document);
			var ids = $(dataGrid).jqGrid('getDataIDs');
			for (var i = 0; i < ids.length; i++) {
				var data=$(dataGrid).jqGrid('getRowData',ids[i]);
				$(dataGrid).jqGrid('setCell',ids[i],"value1",' ');
				$(dataGrid).jqGrid('setCell',ids[i],"value2",' ');
			}
			return false;
		}
	};
	options.btn.push('<i class="fa fa-close"></i> 关闭');
	options['btn'+options.btn.length] = function(index, layero){
 };
 js.layer.open(options);
}

function checkRequiredCell(cm,row){
	   var flag=true;
	   $.each(cm,function(k,o){
		   if(o.editrules&&o.editable){
			   //console.info($("#dataGrid").jqGrid('getColProp',o.name))
			   if(o.editrules.required&&(row[o.name]==undefined||row[o.name].trim().length==0)){
				   flag=false;
				   js.showErrorMessage("请填写"+o.display)
				   return false;
			   }
		   }
	   })   
	   return flag;
}


function getCustomerSelect(elementId, submitId, fn_backcall) {

	$(submitId).val("")
	var customer={};
	var clist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"kunnr":input,
					"pageNo":1,
					"pageSize":15,
			};
			$.ajax({
				url:baseURL+"config/sapCustomer/list",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					clist = response.page.list;
					var results = new Array();
					$.each(clist, function(index, value) {
						results.push(value.kunnr);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var kunnr = "";
			var name = "";
			$.each(clist, function(index, value) {
				if (value.kunnr == item) {
					kunnr = value.kunnr;
					name = value.name1;
				}
			})
			return kunnr + "  " + name ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(clist, function(index, value) {
				if (value.kunnr == item) {
					kunnr=value;
					//selectId = value.id;
					customer=value;
					$(submitId).val(value.kunnr);
					$(submitId).attr("customer_name",value.name1);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(customer);
			}
		}
	});
}



var smallTools = function () { 
	return {     
		/**
		 * 通过界面需要国际化的key
		 * 调用方式为：var smallUtil=new smallTools();
		 * 		   var array = new Array("WELCOME_TO_WMS","LANGUAGE",...)
		 *         smallUtil.loadlanguageByPKeyAndLKey(array);
		 *           
		 */
		loadlanguageByPKeyAndLKey:function(array,inshowType){
			var languageObj = null;
			$.ajax({
				url:baseUrl+"sys/language/getLocale",
				type:"post",
				async:false,
				dataType:"json",
				data:{
					keyArray:JSON.stringify(array),
					showType:inshowType
				},
				success:function(response){
					
					if(response.msg=="success"){
						languageObj=response.locales;
					}else{
						alert('提示',respText.msg);
					}
				}
			})
			return languageObj;
			
		},
		
		/**
		 * 国际化中带参数，如：欢迎{0}使用WMS  
		 * 调用方式为：var smallUtil=new smallTools();
		 *         smallUtil.getPKey("PLEASE_FILL_IN","M","语言") //返回：请填写 语言
		 *           
		 */
		getPKey:function(key,inshowType,params){
			var languageValue = null;
			$.ajax({
				url:baseUrl+"sys/language/getPKey",
				type:"post",
				async:false,
				dataType:"json",
				data:{
					keyStr:key,
					showType:inshowType,
					localeparams:params
				},
				success:function(response){
//					alert(response.msg);
					if(response.msg=="success"){
						languageValue=response.localebykey;
					}else{
						alert('提示',respText.msg);
					}
				}
			})
			return languageValue;
		},
		//表格自定义弹窗	
		gridCustom:function(gridname) {
		    var options = {
		        	id: 'insert-form',
		            type: 2,
		            maxmin: true,
		            shadeClose: true,
		            title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>自定义表格',
		            area: ["600px", "500px"],
		            content: "wms/sys/userGridSet.html",
		            btn: ['<i class="fa fa-check"></i> 确定'],
		            success:function(layero, name){
		                var win = layero.find('iframe')[0].contentWindow;
//		                var body = layer.getChildFrame('body',name);
//		                body.find("#gridNo").val(gridname);
		                win.getGridNo(gridname);
		                win.init();
		            },
		            btn1:function(index,layero){
		                var win = layero.find('iframe')[0].contentWindow;
		                flag=false;
		            
		                var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
		                $(btnSubmit).click();
		                parent.layer.close(index);
		            }
		        };
		        options.btn.push('<i class="fa fa-close"></i> 关闭');
		        options['btn'+options.btn.length] = function(index, layero){
		        };
		        js.layer.open(options);
		},

		//表头重新排序
		colModelCustom:function(model, gridNo) {
		    myModel = model.concat();
		    var colnum = 0;
		    $.ajax({
		        url: baseURL + "sys/columnConfiguration/trans",
		        dataType: "json",
		        data: {
		            gridNo: gridNo
		        },
		        async: false,
		        success: function(data) {
		            if (data.list.length >= model.length) {
		            for (var i = 0; i < data.list.length; i++) {
		                if (data.list[i].HIDERMK == "0") {
		                    for (var j = 0; j < model.length; j++) {
		                        if (data.list[i].COLUMNNAME == model[j].header || data.list[i].COLUMNNAME == model[j].label) {
		                            model[j]["width"] = data.list[i].COLUMNWIDTH;
		                            myModel.splice(colnum, 1, model[j]);
		                            colnum++;
		                        }
		                    }
		                }
		            }
		            if (colnum < myModel.length) {
		                myModel.splice(colnum, model.length - colnum);
		            }
		        }
		    }
		});
		return myModel
		}
		
	};
};

/*
 * onchange 根据工厂、物料取物料描述
 */
function getMaktxByMatnr(werks,matnr,maktx) {
	var data={
			"werks":$(werks).val(),
			"matnr":$(matnr).val().replace(/(^\s*)|(\s*$)/g, "")
		};
	$.ajax({
		url:baseURL+"common/getMaterialList",
		dataType : "json",
		type : "post",
		data : data,
		async: false,
		success: function (response) {
			var materiallist = JSON.stringify(response.list);
			if (materiallist != '[]'){
				$(matnr).val(response.list[0].MATNR);
				$(maktx).val(response.list[0].MAKTX);
			} else {
				$(matnr).val("");
				$(maktx).val("");
			}
		}
	});
}

/**
 * 出入库策略控制标识 查询
 * @param elementId
 * @param submitId
 * @param fn_backcall
 */
function getControlFlagNoSelect(whNumber,type,elementId, submitId, fn_backcall) {

	$(submitId).val("")
	var controllist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"whNumber":whNumber,
					"controlFlagType":type,
					"controlFlag":input
			};
			$.ajax({
				url:baseURL+"common/getControlFlagList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					controllist = response.list;
					var results = new Array();
					$.each(controllist, function(index, value) {
						results.push(value.CONTROL);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var control = "";
			var controlDesc = "";
			$.each(controllist, function(index, value) {
				if (value.CONTROL == item) {
					control = value.CONTROL;
					controlDesc = value.CONTROLDESC;
				}
			})
			return control + "  " + controlDesc ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(controllist, function(index, value) {
				if (value.CONTROL == item) {
					control = value;
					CONTROL=value;
					$(submitId).val(value.CONTROL);
					if("00"==type){//入库
						vm.matstorage.inControlFlag=value.CONTROL;
					}
					if("01"==type){//出库
						vm.matstorage.outControlFlag=value.CONTROL;
					}
					
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(control);
			}
		}
	});
}

function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}

/**
 * fn_bk1：交接并过帐回调函数
 * fn_bk2：无交接并过帐回调函数
 * @returns
 */
function commonHandover(fn_bk1,fn_bk2,handover_type){
	layer.open({
		type : 2,
		offset : '50px',
		skin : 'layui-layer-molv',
		title : "确认人信息",
		area : [ '400px', '350px' ],
		shade : 0,
		shadeClose : false,
		content : baseURL+"wms/common/wms_c_handover.html", 
		btn : [ '交接并过帐','关闭'],
		success: function(layero, index){		
			handover_type=handover_type||'03'
			$("input[name='group1'][value='"+handover_type+"']", layero.find("iframe")[0].contentWindow.document).attr("checked","checked")
			console.info("交接模式:"+handover_type)
			if(handover_type=='03'){
				$("#tr_password", layero.find("iframe")[0].contentWindow.document).css("display","")
			}else{
				$("#tr_password", layero.find("iframe")[0].contentWindow.document).css("display","none")
			}
		},
		btn1 : function(index,layero) {
			layer.close(index);
			var win = layero.find('iframe')[0].contentWindow;
			var user =  $("#c_userNo", layero.find("iframe")[0].contentWindow.document).val();
			var username =  $("#c_name", layero.find("iframe")[0].contentWindow.document).val();
			
			handover_type=handover_type||'03'
			var flag=true;
			if(handover_type=='03'){	//密码模式校验账号密码
				var password =  $("#c_password", layero.find("iframe")[0].contentWindow.document).val();
				flag=checkPassword(user,password)
			}
			if(!flag){
				return false;
			}
			
			console.info(user)
			if(typeof fn_bk1 == 'function'){
				fn_bk1(user,username);
			}
		},
		/*btn2 : function(index,layero) {
			$(".btn").attr("disabled", false);		
			var win = top[layero.find('iframe')[0]['name']];
			var user =  $("#c_userNo", layero.find("iframe")[0].contentWindow.document).val();
			layer.close(index);
			if(typeof fn_bk2 == 'function'){
				fn_bk2(user,username);
			}
		},*/
		btn2: function(index) {
			$(".btn").attr("disabled", false);
			layer.close(index);
		}
	});
}

function checkPassword(user,password){
	var flag=true;
	$.ajax({
		url:baseURL+"common/checkPassword",
		dataType : "json",
		type : "post",
		data : {
			username:user,
			password:password
		},
		async: false,
		success: function (response) { 
			if(response.code=='500'){
				alert(response.msg)
				flag=false;
			}
		}
	});
	return flag;
}

/**
 *  二维码标签扫描
 * @param labelStr
 */
function getLabelData(labelStr){
	var labelObj={};
	var labelStrArray=labelStr.toUpperCase().split(";");
	
	for(var i=0;i<labelStrArray.length;i++){
		var label_o=labelStrArray[i];
		var label_o_Array=label_o.split(":");
		
		var label_o_name=label_o_Array[0];
		var label_o_value=label_o_Array[1];
		
		if(label_o_name=="P"){//工厂
			labelObj.WERKS=label_o_value;
		}
		
		if(label_o_name=="V"){//供应商代码
			labelObj.LIFNR=label_o_value;
		}
		
		if(label_o_name=="M"){//物料编码
			var matnrUnitArray=label_o_value.split("/");
			labelObj.MATNR=matnrUnitArray[0];
			labelObj.UNIT=matnrUnitArray[1];
		}
		
		if(label_o_name=="B"){//批次
			labelObj.BATCH=label_o_value;
		}
		
		if(label_o_name=="S"||label_o_name=="PN"){//标签号
			labelObj.LABEL_NO=label_o_value;
		}
		
		if(label_o_name=="PO"){//订单号
			labelObj.PO_NO=label_o_value;
		}
		
		if(label_o_name=="Q"){//数量
			var qtyArray=label_o_value.split("/");
			labelObj.QTY=qtyArray[0];
			labelObj.BOX_SN=qtyArray[1];
		}
		
		if(label_o_name=="D"){//生产日期
			labelObj.PRODUCT_DATE=label_o_value;
		}
		
		if(label_o_name=="YX"){//有效日期
			labelObj.EFFECT_DATE=label_o_value;
		}
		
		if(label_o_name=="MO"){//模俱编号
			labelObj.MOULD_NO=label_o_value;
		}
		
		if(label_o_name=="W"){//作业员
			labelObj.OPERATOR=label_o_value;
		}
		
		if(label_o_name=="PR"){//产品图号
			labelObj.DRAWING_NO=label_o_value;
		}
		
		if(label_o_name=="WP"){//工位
			labelObj.PRO_STATION=label_o_value;
		}
		
		if(label_o_name=="C"){//车型
			labelObj.CAR_TYPE=label_o_value;
		}
		
		if(label_o_name=="T"){//班次
			labelObj.WORKGROUP_NO=label_o_value;
		}
		
	}
	return labelObj;
}


/***导出excel文件 支持上万条数据
/***toExcel:
* FileName文件名
* JSONData导出的数据
* ShowLabel表头
*/
var DownloadEvt = null; 
function toExcel(FileName, JSONData, ShowLabel) {
  var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;

  var docData = new StringBuffer();
  
  // Header 
  var innerHTML = new StringBuffer();
  docData.append('<table><thead>');
  
  var columns = [];
  var table_head=$("<tr />");
  $.each(ShowLabel,function(i,column){
		var json1 = {};
		json1[column.data] = column.title;
		columns.push(json1);
		innerHTML.append('<td align='+column.class+'><b>' + column.title + '</b></td>\r');
  })
  docData.append('<tr>\r' + innerHTML.toString() + '</tr>');
  docData.append('</thead><tbody>');
  // Row Vs Column
  for (var i = 0; i < arrData.length; i++) {
    var tdRow = new StringBuffer();
      for (var y = 0; y < columns.length; y++) {
          for (var k in columns[y]) {
              if (columns[y].hasOwnProperty(k)) {
                tdRow.append('<td style="vnd.ms-excel.numberformat:@">' + (arrData[i][k] === null ? '' : arrData[i][k]) + '</td>\r');
              }
          }
      }
      docData.append('<tr>\r' + tdRow.toString() + '</tr>');
  }

  docData.append('</tbody></table>');

  console.log("docData:",docData);

  var docFile = new StringBuffer();

  docFile.append('<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">');
  docFile.append('<meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">');
  docFile.append("<head>");
  docFile.append("<!--[if gte mso 9]>");
  docFile.append("<xml>");
  docFile.append("<x:ExcelWorkbook>");
  docFile.append("<x:ExcelWorksheets>");
  docFile.append("<x:ExcelWorksheet>");
  docFile.append("<x:Name>");
  docFile.append("Sheet1");
  docFile.append("</x:Name>");
  docFile.append("<x:WorksheetOptions>");
  docFile.append("<x:DisplayGridlines/>");
  docFile.append("</x:WorksheetOptions>");
  docFile.append("</x:ExcelWorksheet>");
  docFile.append("</x:ExcelWorksheets>");
  docFile.append("</x:ExcelWorkbook>");
  docFile.append("</xml>");
  docFile.append("<![endif]-->");
  docFile.append("</head>");
  docFile.append("<body>");
  for (var i = 0; i < docData.content.length; i++) {
      docFile.append(docData.content[i].toString());
  }
  // docFile.append( docData.toString());  
  docFile.append("</body>");
  docFile.append("</html>");

  console.log('docFile:',docFile);

  try {
      var blob = new Blob(docFile.content, {
          type: 'application/vnd.ms-excel;charset=UTF-8;'
      });
      saveAs(blob, FileName + '.xls');
  } catch (e) {
      downloadFile(FileName + '.xls',
          'data:application/vnd.ms-excel;base64,',
          docFile.toString());
  }
}

function StringBuffer() {  
  this.content = new Array;  
}  
StringBuffer.prototype.append = function(str) {  
  this.content.push(str);  
}  
StringBuffer.prototype.prepend = function(str) {  
  this.content.unshift(str);  
}  
StringBuffer.prototype.toString = function() {  
  return this.content.join("");  
}  

function saveAs(blob, filename) {

  var type = blob.type;
  var force_saveable_type = 'application/octet-stream';
  if (type && type != force_saveable_type) { // 强制下载，而非在浏览器中打开  
      var slice = blob.slice || blob.webkitSlice || blob.mozSlice;
      blob = slice.call(blob, 0, blob.size, force_saveable_type);
  }

  var url = URL.createObjectURL(blob);
  var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
  save_link.href = url;
  save_link.download = filename;

  console.log('save_link:',save_link);

  var event = document.createEvent('MouseEvents');
  event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
  save_link.dispatchEvent(event);
  URL.revokeObjectURL(url);
}

function downloadFile(filename, header, data) {  

  var ua = window.navigator.userAgent;  
  if (ua.indexOf("MSIE ") > 0 || !!ua.match(/Trident.*rv\:11\./)) {  
    // Internet Explorer (<= 9) workaround by Darryl (https://github.com/dawiong/tableExport.jquery.plugin)  
    // based on sampopes answer on http://stackoverflow.com/questions/22317951  
    // ! Not working for json and pdf format !  
    var frame = document.createElement("iframe");  

    if (frame) {  
      document.body.appendChild(frame);  
      frame.setAttribute("style", "display:none");  
      frame.contentDocument.open("txt/html", "replace");  
      frame.contentDocument.write(data);  
      frame.contentDocument.close();  
      frame.focus();  

      frame.contentDocument.execCommand("SaveAs", true, filename);  
      document.body.removeChild(frame);  
    }  
  }  
  else {  
    var DownloadLink = document.createElement('a');  

    if (DownloadLink) {  
      DownloadLink.style.display = 'none';  
      DownloadLink.download = filename;  
        
      console.log((header + base64encode(data)).length);  
        
      if (header.toLowerCase().indexOf("base64,") >= 0)  
        DownloadLink.href = header + base64encode(data);  
          
         
      else  
        DownloadLink.href = encodeURIComponent(header + data);  

      document.body.appendChild(DownloadLink);  

      if (document.createEvent) {  
        if (DownloadEvt == null)  
          DownloadEvt = document.createEvent('MouseEvents');  

        DownloadEvt.initEvent('click', true, false);  
        DownloadLink.dispatchEvent(DownloadEvt);  
      }  
      else if (document.createEventObject)  
        DownloadLink.fireEvent('onclick');  
      else if (typeof DownloadLink.onclick == 'function')  
          
        DownloadLink.onclick();  

      /*document.body.removeChild(DownloadLink);*/  
    }  
  }  
}


function base64encode(input) {  
  var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";  
  var output = "";  
  var chr1, chr2, chr3, enc1, enc2, enc3, enc4;  
  var i = 0;  
  input = utf8Encode(input);  
  while (i < input.length) {  
    chr1 = input.charCodeAt(i++);  
    chr2 = input.charCodeAt(i++);  
    chr3 = input.charCodeAt(i++);  
    enc1 = chr1 >> 2;  
    enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);  
    enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);  
    enc4 = chr3 & 63;  
    if (isNaN(chr2)) {  
      enc3 = enc4 = 64;  
    } else if (isNaN(chr3)) {  
      enc4 = 64;  
    }  
    output = output +  
            keyStr.charAt(enc1) + keyStr.charAt(enc2) +  
            keyStr.charAt(enc3) + keyStr.charAt(enc4);  
  }  
  return output;  
}  

function utf8Encode(string) {  
  string = string.replace(/\x0d\x0a/g, "\x0a");  
  var utftext = "";  
  for (var n = 0; n < string.length; n++) {  
    var c = string.charCodeAt(n);  
    if (c < 128) {  
      utftext += String.fromCharCode(c);  
    }  
    else if ((c > 127) && (c < 2048)) {  
      utftext += String.fromCharCode((c >> 6) | 192);  
      utftext += String.fromCharCode((c & 63) | 128);  
    }  
    else {  
      utftext += String.fromCharCode((c >> 12) | 224);  
      utftext += String.fromCharCode(((c >> 6) & 63) | 128);  
      utftext += String.fromCharCode((c & 63) | 128);  
    }  
  }  
  return utftext;  
}

Date.prototype.Format = function(fmt)   
{ 
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}  


function getDeviceTypeNoSelect(elementId,devicename ,submitId, fn_backcall) {

	$(submitId).val("");
	var devicecode={};
	var deviceTypelist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"code":input
			};
			$.ajax({
				url:baseURL+"masterdata/device/getDeviceTypeList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) {
					deviceTypelist = response.list;
					var results = new Array();
					$.each(deviceTypelist, function(index, value) {
						results.push(value.DEVICECODE);
					})
					return process(results);
				}
			});
		},
		items : 10,
		highlighter : function(item) {
			var devicecode = "";
			var devicename = "";
			$.each(deviceTypelist, function(index, value) {
				if (value.DEVICECODE == item) {
					devicecode = value.DEVICECODE;
					devicename = value.DEVICENAME;
				}
			})
			return devicecode + " " + devicename ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(deviceTypelist, function(index, value) {
				if (value.DEVICECODE == item) {
					devicecode=value;
					//selectId = value.id;
					$(submitId).val(value.DEVICECODE);
					$(devicename).val(value.DEVICENAME);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(devicecode);
			}
		}
	});
}

//查询用户信息
function getUserInfoNoSelect(submitId, fn_backcall) {
	$(submitId).val("");
	var userInfo={};
	var userInfolist;
	$(submitId).typeahead({		
		source : function(input, process) {
			var data={
				"user":input,
				
			};
			
			$.ajax({
				url:baseURL+"sys/user/getUserMap",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) {
					userInfolist = response.data;
					var results = new Array();
					$.each(userInfolist, function(index, value) {
						results.push(value.USERNAME);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var username = "";
			var fullname = "";
			$.each(userInfolist, function(index, value) {
				if (value.USERNAME == item) {
					username = value.USERNAME;
					fullname = value.FULL_NAME;
				}
			})
			return username + " " + fullname ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(userInfolist, function(index, value) {
				if (value.USERNAME == item) {
					userInfo=value;
					//selectId = value.id;
					$(submitId).val(value.USERNAME);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(userInfo);
			}
		}
	});
}


//查询工序信息
function getProcessNoSelect(submitId, fn_backcall) {
	$(submitId).val("");
	var processInfo={};
	var processlist;
	$(submitId).typeahead({		
		source : function(input, process) {
			var data={
				"process":input,
				
			};
			
			$.ajax({
				url:baseURL+"masterdata/process/getProcessList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) {
					processlist = response.data;
					var results = new Array();
					$.each(processlist, function(index, value) {
						results.push(value.PROCESS_NAME);
					})
					return process(results);
				}
			});
		},
		items : 20,
		highlighter : function(item) {
			var process_code = "";
			var process_name = "";
			$.each(processlist, function(index, value) {
				if (value.PROCESS_NAME == item) {
					process_code = value.PROCESS_CODE;
					process_name = value.PROCESS_NAME;
				}
			})
			return process_code + " " + process_name ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(processlist, function(index, value) {
				if (value.PROCESS_NAME == item) {
					processInfo=value;
					//selectId = value.id;
					$(submitId).val(value.PROCESS_NAME);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(processInfo);
			}
		}
	});
}
	//查询
	function getProcessByflowcodeNoSelect(submitId,process_flow_code,fn_backcall) {
		$(submitId).val("");
		var processInfo={};
		var processlist;
		$(submitId).typeahead({		
			source : function(input, process) {
				var data={
					"process":input,
					"process_flow_code":process_flow_code
					
				};
				
				$.ajax({
					url:baseURL+"config/bjMesProcessFlow/getList",
					dataType : "json",
					type : "post",
					data : data,
					async: false,
					success: function (response) {
						processlist = response.data;
						var results = new Array();
						$.each(processlist, function(index, value) {
							results.push(value.node_name);
						})
						return process(results);
					}
				});
			},
			items : 20,
			highlighter : function(item) {
				var node_code = "";
				var node_name = "";
				$.each(processlist, function(index, value) {
					if (value.node_name == item) {
						node_code = value.node_code;
						node_name = value.node_name;
					}
				})
				return node_code + " " + node_name ;
			},
			matcher : function(item) {
				return true;
			},
			updater : function(item) {
				return item;
			},
			afterSelect:function(item){
				$.each(processlist, function(index, value) {
					if (value.node_name == item) {
						processInfo=value;
						//console.info("dizhi:",submitId);
						processInfo.dizhi=submitId;
						$(submitId).val(value.node_name);
					}
				})
				if (typeof (fn_backcall) == "function") {
					fn_backcall(processInfo);
				}
			}
		});
}

function getQueryVariable(variable)
{
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
	    if(pair[0] == variable){return pair[1];}
	}
	return(false);
}
