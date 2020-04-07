var mydata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var listdata = [];
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		wh_list:[],
		werks:'',
		whNumber:''
	},
	watch:{
		whNumber:{
			/*handler:function(newVal,oldVal){
				$.ajax({
		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
		        	  data:{"WERKS":vm.werks,"WH_NUMBER":newVal},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.result;
		        	  }
		          })
			}*/
		
		},
		werks : {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal,
						"MENU_KEY":"STOCK_COMPARE"
					},
					async: true,
					success: function (response) { 
						vm.wh_list=response.data;
						if(response.data.length>0){
			    		  vm.whNumber=response.data[0].WH_NUMBER//方便 v-mode取值
			    		}	
					}
				});
					
				}
			}
		},
	created:function(){
		this.werks=$("#werks").val();
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":this.werks,
				"MENU_KEY":"STOCK_COMPARE"
			},
			async: true,
			success: function (response) { 
				//$("#wh").empty();
				var strs = "";
				this.wh_list=response.data;
				if(response.data.length>0){
				  vm.whNumber=response.data[0].WH_NUMBER//方便 v-mode取值
	    		}

			}
		});
		
	},
	methods: {
		query: function () {
			
	 		$(".btn").attr("disabled", true);
	 		
	 		js.loading();
			vm.$nextTick(function(){//数据渲染后调用
				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid'); 
				js.closeLoading();
			})
	 		$(".btn").attr("disabled", false);
		},
		exp: function () {
			export2Excel();
		}
		/*more: function(e){
			$("#moreGrid").html("")
			$.each(mydata,function(i,d){
				var tr=$("<tr index="+i+"/>");
				var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
				var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
				$(tr).append(td_1)
				$(tr).append(td_2)
				$("#moreGrid").append(tr)
			})
			
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
				  var inputs=$("#more_div tbody").find("tr").find("input[type='text']");
				  var values=[]
				  $.each(inputs,function(i,input){
					  if($(input).val().trim().length>0){
						  values.push($(input).val())
					  }					  
				  })
				  $(e).val(values.join(","))
			  }
			});
	  },*/

	}
});
$(function () {
	var now = new Date(); //当前日期
	//var startDate=new Date(now.getTime()-6*24*3600*1000);
	//$("#createDateStart").val(formatDate(startDate));
	$("#createDateEnd").val(formatDate(now));
	$('#dataGrid').dataGrid({
		datatype: "local",
		searchForm: $("#searchForm"),
		data: listdata,
		columnModel: [
			{header:'发货工厂', name:'WERKS', width:80, align:"center"},
			{header:'发货仓库号', name:'WH_NUMBER', width:100, align:"center"},
			{header:'发货库位', name:'LGORT', width:80, align:"center"},
			{header:'接收工厂', name:'RECEIVE_WERKS', width:80, align:"center"},
			{header:'料号', name:'MATNR', width:100, align:"center"},
			{header:'物料描述', name:'MAKTX', width:200, align:"center"},
			{header:'发出数量', name:'QTY', width:80, align:"center"},
			{header:'单位', name:'UNIT', width:40, align:"center"},
			{header:'发货WMS凭证号/行项目', name:'WMS_DOC', width:200, align:"center"},
			{header:'发货SAP凭证号：行项目', name:'WMS_SAP_MAT_DOC', width:200, align:"center"},
			{header:'接受数量', name:'RECEIVE_QTY', width:80, align:"center"},
			{header:'差异数量', name:'EXCEPTION_QTY', width:80, align:"center",
				styler: function(value,row,index){
					if (value < 0){
						return 'background-color:#ffee00;color:red;';
						
					}
				}
			},
			{header:'发货人', name:'CREATOR', width:120, align:"center"},
			{header:'发货日期', name:'CREATE_DATE', width:160, align:"center"},
			{header:'接受日期', name:'RECEIVE_DATE', width:160, align:"center"},		
			
		],
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:2500,
		rowList : [  15,30 ,50],
        rownumWidth: 35	
	    
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
function export2Excel(){
	var column= [
		{title:'发货工厂', data:'WERKS'},
		{title:'发货仓库号', data:'WH_NUMBER'},
		{title:'发货库位', data:'LGORT'},
		{title:'接收工厂', data:'RECEIVE_WERKS',},
		{title:'料号', data:'MATNR'},
		{title:'物料描述', data:'MAKTX'},
		{title:'发出数量', data:'QTY'},
		{title:'单位', data:'UNIT'},
		{title:'发货WMS凭证号/行项目', data:'WMS_DOC'},
		{title:'发货SAP凭证号：行项目', data:'WMS_SAP_MAT_DOC'},
		{title:'接受数量', data:'RECEIVE_QTY'},
		{title:'差异数量', data:'EXCEPTION_QTY'},
		{title:'发货人', data:'CREATE_DATE'},
		{title:'发货日期', data:'CREATOR'},
		{title:'接受日期', data:'RECEIVE_DATE'},	
		
	]
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"report/stoPutAwayReport/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.page.list;
		    console.log("results",results);
		}
	});
	var rowGroups=[];
//	getMergeTable(results,column,rowGroups,"出入库库存报表");
	toExcel("STO在途报表",results,column);
}
/*
function refreshMore(){
	  $.each($("#more_div").find("input[type='text']"),function(i,input){
		  $(input).val("")
	  })
}

function addMore(){
	var tr=$("<tr />");
	  var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
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
*/

