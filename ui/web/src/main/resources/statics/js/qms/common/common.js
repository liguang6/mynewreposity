
function getContextPath() {
    var pathName = document.location.pathname;
    var index = pathName.substr(1).indexOf("/");
    var result = pathName.substr(0,index+1);
    return result;

}

var baseURL = getContextPath()+"/";
/*
 * 车型模糊查询 submitId： 用于提交的元素的id
 */
function getBusTypeCodeNoSelect(elementId, submitId, fn_backcall) {
	var busTypeCode={};
	var busTypeCodelist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"busTypeCode":input
			};
			$.ajax({
				url:baseURL+"qms/common/getBusTypeCodeList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					busTypeCodelist = response.list;
					var results = new Array();
					$.each(busTypeCodelist, function(index, value) {
						results.push(value.busTypeCode+" "+value.testTypeDesc);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var str="";
			$.each(busTypeCodelist, function(index, value) {
				if ((value.busTypeCode+" "+value.testTypeDesc) == item) {
					str=value.busTypeCode+" "+value.testTypeDesc;
				}
			})
			return str;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(busTypeCodelist, function(index, value) {
				if ((value.busTypeCode+" "+value.testTypeDesc) == item) {
					busTypeCode=value;
					$(submitId).val(value.busTypeCode);
					$(submitId).attr("testType",value.testType);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall();
			}
		}
	});
}

var tabs = 1;
var addTabs = function (options) {
	 var url = window.location.protocol + '//' + window.location.host;
	 if(url.indexOf("http://") == -1){
		 options.url = url + options.url;
	 }
	 id = "tab_" + options.id;
	 //如果TAB不存在，创建一个新的TAB 允许最多同时打开6个tab
	 var active=window.parent.document.getElementsByClassName("active");
	 var idobject=window.parent.document.getElementById(id);
	if (!$(idobject)[0]) {
		 $(active).removeClass("active");
		 //固定TAB中IFRAME高度
		 mainHeight = $(window.parent.document).outerHeight()-115 ;//125
		 //创建新TAB的title
		 title = '<li class="myCloseTabClass" role="presentation" id="tab_' + id + '"><a href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab"><i class="'+options.icon+'"></i>&nbsp;&nbsp;' + options.title;
		 //是否允许关闭
		 if (options.close) {
			 title += ' <i class="glyphicon glyphicon-remove-sign" tabclose="' + id + '" onclick="closeTab(\''+id+'\')"></i>';
		 }
		 title += '</a></li>';
		 //是否指定TAB内容
		 if (options.content) {
			 content = '<div role="tabpanel" class="tab-pane" id="' + id + '">' + options.content + '</div>';
		 } else {//没有内容，使用IFRAME打开链接
		  content = '<div role="tabpanel" class="tab-pane active" id="' + id + '"><iframe id="frame_'+id+'" src="'+options.url+'" width="100%" height="' + mainHeight +
		   '" frameborder="no" border="0" marginwidth="0" marginheight="0"  scrolling="yes" allowtransparency="yes"></iframe></div>';
		 }
		 tabs =tabs+1;
		 //加入TABS
		 var nav_tabs=window.parent.document.getElementsByClassName('nav-tabs');
		 var nav_content=window.parent.document.getElementsByClassName('tab-content');
		 $(nav_tabs).append(title);
		 $(nav_content).append(content);
	 }else{
		 $(active).removeClass("active");
	 }
	 //激活TAB
	var oldSrc = $(idobject).children('iframe ').attr('src');
	var newSrc =options.url+options.param;
	$(idobject).children('iframe ').attr('src',newSrc);
	var tabid=window.parent.document.getElementById('tab_'+id);
	$(tabid).addClass("active");
	$(idobject).addClass("active");
    //激活左边菜单
    $('#li_tab_'+options.id).addClass('active');  
/*	$(".myCloseTabClass").on("click", function (e) {
		 var pid = $(".myCloseTabClass.active").attr('id');
		 pid = pid.substring(4);
		 var id = $(this).attr("id");
		 id = id.substring(4);
		 $('#li_'+pid).removeClass('active');
	     $('#li_'+id).addClass('active');
	 });*/
};


var closeTab = function (id) {
	console.info(id)
	var mpid = $(".myCloseTabClass.active").attr('id');	 
	var prev_id=$("#tab_" + id).prev().attr("id").replace("tab_","");
	var tab_prev=$("#tab_" + id).prev();
	console.info("前一个TAB："+prev_id)
	//关闭TAB
	tabs =tabs-1;
	$("#tab_" + id).remove();
	$("#" + id).remove();
 //如果关闭的是当前激活的TAB，激活他的前一个TAB
 if (mpid== "tab_" + id) {
	 $(tab_prev).addClass('active');
	 $("#" + prev_id).addClass('active');
    console.info("点击前一个TAB")
    $(tab_prev).children("a").trigger("click");
 }
 
};

/*
 * 订单模糊查询 submitId： 用于提交的元素的id
 */
function getOrderNoSelect(elementId, submitId, fn_backcall) {
	var order={};
	var orderlist;
	$(elementId).typeahead({		
		source : function(input, process) {
			var data={
					"orderNo":input
			};
			$.ajax({
				url:baseURL+"qms/common/getOrderNoList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					orderlist = response.list;
					var results = new Array();
					$.each(orderlist, function(index, value) {
						results.push(value.orderNo);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var orderNo = "";
			var orderText = "";
			$.each(orderlist, function(index, value) {
				if (value.orderNo == item) {
					orderNo = value.orderNo;
					orderText = value.orderText;
				}
			})
			return orderNo + "  " + orderText ;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(orderlist, function(index, value) {
				if (value.orderNo == item) {
					order=value;
					$(submitId).val(value.orderNo);
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(order);
			}
		}
	});
}


/*
 * VIN/车号模糊查询 submitId： 用于提交的元素的id
 */
function getBusSelect(elementId,ele_factory,ele_busType,fn_backcall) {
	var bus={};
	var bus_list=[];
	$(elementId).typeahead({		
		source : function(input, process) {
			var busType=$(ele_busType).val()||"";
			var factory=$(ele_factory).val()||"";
			var data={
					"busNo":input,
					"factory":$(ele_factory).val()||"",
					//"busType":$(ele_busType).val()||""
			};
			$.ajax({
				url:baseURL+"qms/common/getBusList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					bus_list = response.list;
					var results = new Array();
					$.each(bus_list, function(index, value) {
						if(value.TEST_TYPE=='01'){
							results.push(value.BUS_NO);
						}
						if(value.TEST_TYPE=='02'){
							results.push(value.VIN);
						}				
						
					})
					//console.info(JSON.stringify(results))
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var busNo = "";
			var vin = "";
			var orderText = "";
			//var busType=$(ele_busType).val()||"";
			$.each(bus_list, function(index, value) {
				if(value.TEST_TYPE=='01' && value.BUS_NO==item){
					busNo = value.BUS_NO;
					vin = value.VIN;
					orderText = value.ORDER_TEXT;
				}
				
				if(value.TEST_TYPE=='02' && value.VIN==item){
					vin = value.VIN;
					orderText = value.ORDER_TEXT;
				}
			})
			return busNo + "&nbsp;&nbsp;&nbsp;" +vin;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			//var busType=$(ele_busType).val()||"";
			$.each(bus_list, function(index, value) {
				if(value.TEST_TYPE=='01' && value.BUS_NO==item){
					bus=value;
				}				
				if(value.TEST_TYPE=='02' && value.VIN==item){
					bus=value;
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(bus);
			}
		}
	});
}

function getCABSelect(elementId,ele_factory,ele_busType,fn_backcall) {
	var bus={};
	var bus_list=[];
	$(elementId).typeahead({		
		source : function(input, process) {
			var busType=$(ele_busType).val()||"";
			var factory=$(ele_factory).val()||"";
			var data={
					"cabNo":input,
					"factory":$(ele_factory).val()||"",
					"busType":$(ele_busType).val()||""
			};
			$.ajax({
				url:baseURL+"qms/common/getBusList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					bus_list = response.list;
					var results = new Array();
					$.each(bus_list, function(index, value) {
						results.push(value.CAB_NO);						
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var CAB_NO = "";
			var orderText = "";
			var busType=$(ele_busType).val()||"";
			$.each(bus_list, function(index, value) {
				if( value.CAB_NO==item){
					CAB_NO = value.CAB_NO;
					orderText = value.ORDER_TEXT;
				}				
			})
			return CAB_NO;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			var busType=$(ele_busType).val()||"";
			$.each(bus_list, function(index, value) {
				if(value.CAB_NO==item){
					bus=value;
				}
			})
			if (typeof (fn_backcall) == "function") {
				fn_backcall(bus);
			}
		}
	});
}

function getTestToolSelect(elementId,ele_factory,fn_backcall) {
	var tt={};
	var data_list=[];
	var newstr=""
	$(elementId).typeahead({		
		source : function(input, process) {
			var factory=$(ele_factory).val()||"";
			var newparam="";
			newstr=input;
			var inputArr=input.split(",");
			if(inputArr.length==1){
				newparam=inputArr[0];
			}else{
				newparam=inputArr[inputArr.length-1];
			}
			var data={
					"testToolNo":newparam,
					"WERKS":$(ele_factory).val()||"",
			};
			$.ajax({
				url:baseURL+"qms/common/getTestTools",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					data_list = response.list;
					var results = new Array();
					$.each(data_list, function(index, value) {
						results.push(value.TEST_TOOL_NO);						
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var TEST_TOOL_NO = "";
			var TEST_TOOL_NAME = "";
			$.each(data_list, function(index, value) {
				if( value.TEST_TOOL_NO==item){
					TEST_TOOL_NO = value.TEST_TOOL_NO;
					TEST_TOOL_NAME = value.TEST_TOOL_NAME;
				}				
			})
			return TEST_TOOL_NO+"  "+TEST_TOOL_NAME;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(data_list, function(index, value) {
				if(value.TEST_TOOL_NO==item){
					tt=value;
					var s="";
					var inputArr=newstr.split(",");
					if(inputArr.length>1){
						for(var i=0;i<inputArr.length-1;i++){
							s+=inputArr[i]+",";
						}
					}
					$(elementId).val(s+item+",");	
					tt.TEST_TOOL_NO=s+item+","
				}
			})

			if (typeof (fn_backcall) == "function") {
				fn_backcall(tt);
			}
		}
	});
}


function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return decodeURI(r[2]); return null; 
} 

