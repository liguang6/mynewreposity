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
			
	 		var days=$("#days").val();
	 		if(days==''){
	 			layer.msg("请输入呆滞天数！",{time:1000});
	 			return false;
	 		}
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
		},
		more: function(e){
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
	  },

	}
});
$(function () {
	$('#dataGrid').dataGrid({
		datatype: "local",
		searchForm: $("#searchForm"),
		data: listdata,
		columnModel: [
			{header:'工厂', name:'WERKS', width:45, align:"center",frozen: true},
			{header:'仓库号', name:'WH_NUMBER', width:55, align:"center",frozen: true},
			{header:'库位', name:'LGORT', width:55, align:"center",frozen: true},
			{header:'储位', name:'BIN_CODE', width:50, align:"center",frozen: true},
			{header:'料号', name:'MATNR', width:90, align:"center",frozen: true},
			{header:'物料描述', name:'MAKTX', width:160, align:"center"},
			{header:'批次', name:'BATCH', width:95, align:"center"},
			{header:'库存类型', name:'SOBKZ', width:50, align:"center"},
			{header:'数量', name:'QTY', width:50, align:"center"},
			{header:'单位', name:'MEINS', width:50, align:"center"},
			{header:'入库时间', name:'LAST_INBOUND_DATE', width:120, align:"center"},
			{header:'最近出库时间', name:'LAST_OUTBOUND_DATE', width:120, align:"center"},
			{header:'呆滞天数', name:'DAYS', width:65, align:"center"},
		],	
		viewrecords: true,
		cellEdit : true,
	    cellurl : '#',
	    cellsubmit : 'clientArray',
	    viewrecords: false,
	    rowNum: 15,
	    rownumbers: true, 
	    rownumWidth: 25, 
	    autowidth:true,
		showCheckbox:false,
		gridComplete: function() {
			/*var ids = $("#dataGrid").getDataIDs();
	        for(var i=0;i<ids.length;i++){
	            var rowData = $("#dataGrid").getRowData(ids[i]);
	            if(Number(rowData.ERP_QTY) != Number(rowData.STOCK_QTY)){
	                $('#'+ids[i]).find("td").addClass("SelectBG");
	            } 
	        }*/
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
function export2Excel(){
	var column= [
		{title:'工厂', data:'WERKS'},
		{title:'仓库号', data:'WH_NUMBER'},
		{title:'库位', data:'LGORT'},
		{title:'储位', data:'BIN_CODE'},
		{title:'料号', data:'MATNR'},
		{title:'物料描述', data:'MAKTX'},
		{title:'批次', data:'BATCH'},
		{title:'库存类型', data:'SOBKZ'},
		{title:'数量', data:'QTY'},
		{title:'单位', data:'MEINS'},
		{title:'入库时间', data:'LAST_INBOUND_DATE'},
		{title:'最近出库时间', data:'LAST_OUTBOUND_DATE'},
		{title:'呆滞天数', data:'DAYS'},
	]
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"report/stagnateStockReport/list",
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
	toExcel("呆滞库存报表",results,column);
}

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


