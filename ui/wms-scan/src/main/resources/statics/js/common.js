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


// 判断是否为空
function isBlank(value) {
    return !value || !/\S/.test(value)
}

function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return decodeURI(r[2]); return null; 
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
		}
	};
};


function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
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

var jsonreader = {
		root:"page.list",
		page: "page.currPage",
		total: "page.totalPage",
		records: "page.totalCount",
		repeatitems: false
};