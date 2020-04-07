var mydata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var last_scan_ele;

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		lgortList:[],
		wh_list:[],
		werks:'',
		relatedareaname:[],//仓管员
		whNumber:'',
		sobkzList:[],
		STOCK_TYPE:'仓库库存'
	},
	watch:{
		whNumber:{
			handler:function(newVal,oldVal){
				$.ajax({
		        	  url:baseUrl + "in/wmsinbound/relatedAreaNamelist",
		        	  data:{"WERKS":vm.werks,"WH_NUMBER":newVal},
		        	  success:function(resp){
		        		 vm.relatedareaname = resp.result;
		        	  }
		          })
			}
		
		},
		werks : {
			handler:function(newVal,oldVal){
				
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal,
						"MENU_KEY":"QUERY_STOCK"
					},
					async: false,
					success: function (response) { 
						vm.wh_list=response.data
						if(response.data.length>0){
							vm.whNumber=response.data[0].WH_NUMBER;
						}else{
							vm.whNumber='';
						}
						
						vm.lgortList=getLgortList();	
						vm.$nextTick(function(){
							initLgortSelect()
						})
						
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
				"WERKS":this.werks
			},
			async: true,
			success: function (response) { 
				//$("#wh").empty();
				var strs = "";
				this.whNumber=response.data[0].WH_NUMBER
				this.wh_list=response.data;
			    /*$.each(response.data, function(index, value) {
			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    });
			    $("#wh").append(strs);*/
			}
		});
		//this.lgortList=getLgortList();
		this.sobkzList = getDictList('SOBKZ');	
		
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		exp: function () {
			export2Excel();
		},
//		more: function(e){
//			$("#moreGrid").html("")
//			$.each(mydata,function(i,d){
//				var tr=$("<tr index="+i+"/>");
//				var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
//				var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
//				$(tr).append(td_1)
//				$(tr).append(td_2)
//				$("#moreGrid").append(tr)
//			})
//			
//			layer.open({
//			  type: 1,
//			  title:'选择更多',
//			  closeBtn: 1,
//			  btn:['确认'],
//			  offset:'t',
//			  resize:true,
//			  maxHeight:'300',
//			  area: ['400px','400px'],
//			  skin: 'layui-bg-green', //没有背景色
//			  shadeClose: false,
//			  content: "<div id='more_div'>"+$('#layer_more').html()+"</div>",
//			  btn1:function(index){
//				  var ckboxs=$("#more_div tbody").find("tr").find("input[type='checkbox']:checked");
//				  var values=[]
//				  $.each(ckboxs,function(i,ck){
//					  var input=$(ck).parent("td").next("td").find("input")
//					  if($(input).val().trim().length>0){
//						  values.push($(input).val())
//					  }					  
//				  })
//				  $(e).val(values.join(","))
//			  }
//			});
//	  },
	  
	}
});
$(function () {
	//$("#checkall").attr("checked",true);
	$(document).on("click","#checkall",function(e){

		if ($(e.target).is(":checked")) {
			check_All_unAll("#moreGrid",true);	
		}
		if($(e.target).is(":checked")==false){
			//alert("反选")
			check_All_unAll("#moreGrid",false);
		}
	})
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', width:45, align:"center",frozen: true},
			{header:'仓库号', name:'WH_NUMBER', width:55, align:"center",frozen: true},
			{header:'库位', name:'LGORT', width:55, align:"center",frozen: true},
			{header:'储位', name:'BIN_CODE', width:110, align:"center",frozen: true},
			{header:'料号', name:'MATNR', width:90, align:"center",frozen: true},
			{header:'物料描述', name:'MAKTX', width:160, align:"center"},			
			{header:'单位', name:'MEINS', width:50, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:75, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:75, align:"center"},
			{header:'库存类型', name:'SOBKZ', width:65, align:"center"},
			{header:'非限制', name:'STOCK_QTY', width:75, align:"center"},
			{header:'冻结', name:'FREEZE_QTY', width:65, align:"center"},
			{header:'虚拟', name:'VIRTUAL_QTY', width:65, align:"center"},
			{header:'虚拟锁定', name:'VIRTUAL_LOCK_QTY', width:65, align:"center"},
			{header:'锁定', name:'LOCK_QTY', width:65, align:"center"},
			{header:'预留数量', name:'RSB_QTY', width:65, align:"center"},
			{header:'下架', name:'XJ_QTY', width:65, align:"center"},
			{header:'下架储位', name:'XJ_BIN_CODE', width:110, align:"center"},
			{header:'批次', name:'BATCH', width:95, align:"center"},
			{header:'源批次', name:'F_BATCH', width:95, align:"center"},
			{header:'销售订单', name:'SO_NO', width:90, align:"center"},
			{header:'行项目', name:'SO_ITEM_NO', width:60, align:"center"},
			
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:2500,
		showCheckbox:true,
		rowList : [  15,30 ,50],
        rownumWidth: 35, 
        multiselect: true,
	});
	$("#dataGrid").jqGrid("setFrozenColumns");
	var hideCol=[];
	if(vm.STOCK_TYPE=='收料房库存'){
		hideCol=['FREEZE_QTY','VIRTUAL_QTY','VIRTUAL_LOCK_QTY','LOCK_QTY','XJ_QTY','RSB_QTY',
			'XJ_BIN_CODE','SO_NO','SO_ITEM_NO'];
	}
	if(vm.STOCK_TYPE=='仓库库存'){
		hideCol=['VIRTUAL_QTY','VIRTUAL_LOCK_QTY'];
	}	
	
	
	DynamicColume(hideCol,"显示列..","#dataGrid","#example-getting-started");
	
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
		{title:'工厂', data:'WERKS', "class":"center" },
		{title:'仓库号', data:'WH_NUMBER', "class":"center"},
		{title:'库位', data:'LGORT', "class":"center"},
		{title:'储位', data:'BIN_CODE', "class":"center"},
		{title:'料号', data:'MATNR', "class":"center"},
		{title:'物料描述', data:'MAKTX', "class":"center"},			
		{title:'单位', data:'MEINS', "class":"center"},
		{title:'供应商代码', data:'LIFNR', "class":"center"},
		{title:'供应商名称', data:'LIKTX', "class":"center"},
		{title:'库存类型', data:'SOBKZ', "class":"center"},
		{title:'非限制', data:'STOCK_QTY', "class":"center"},
		{title:'冻结', data:'FREEZE_QTY', "class":"center"},
		{title:'虚拟', data:'VIRTUAL_QTY', "class":"center"},
		{title:'虚拟锁定', data:'VIRTUAL_LOCK_QTY', "class":"center"},
		{title:'锁定', data:'LOCK_QTY', "class":"center"},
		{title:'预留数量',data:'RSB_QTY', "class":"center"},
		{title:'下架', data:'XJ_QTY', "class":"center"},
		{title:'下架储位', data:'XJ_BIN_CODE', "class":"center"},
		{title:'批次', data:'BATCH', "class":"center"},
		{title:'源批次', data:'F_BATCH', "class":"center"},
		{title:'销售订单', data:'SO_NO', "class":"center"},
		{title:'行项目', data:'SO_ITEM_NO', "class":"center"},
		
	] 
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"query/stockQuery/list",
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
//	getMergeTable(results,column,rowGroups,"库存明细");	
	toExcel("库存明细",results,column);
}

function getLgortList(){
	var lgortList=[];
	//查询库位
  	$.ajax({
      	url:baseUrl + "common/getAllLoList",
      	async:false,
      	data:{
      		"DEL":'0',
      		"WERKS":$("#werks").val(),
      		},
      	success:function(resp){
      		lgortList = resp.data;   		
      	}
     });

  	return lgortList;
}

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

function initLgortSelect(){
	$("#lgort").multiselect("destroy").multiselect({
		buttonText:function(options, select) {
            if (options.length === 0) {
                return '请选择';
            }else {
                 var labels = [];
                 options.each(function() {
                     if ($(this).attr('label') !== undefined) {
                         labels.push($(this).attr('label'));
                     }
                     else {
                         labels.push($(this).html());
                     }
                 });
                 return labels.join(', ') + '';
             }
        },
        includeSelectAllOption: true,
        selectAllText: '全选',
        buttonWidth: '100px',
        onChange: function(option, checked, select) {
        	 var selectedOptions = $('#lgort option:selected');
        	 var values = [];
        	 selectedOptions.each(function(){
        		  if ($(this).attr('label') !== undefined) {
        			  values.push($(this).attr('label').trim());
                  }
                  else {
                	  values.push($(this).html().trim());
                  }
        	 })
        	 //alert(values.join(","))
        	 $("input[name='LGORT']").val(values.join(","))
        },
        onSelectAll: function(option, checked, select) {
	       	 var selectedOptions = $('#lgort option:selected');
	    	 var values = [];
	    	 selectedOptions.each(function(){
	    		  if ($(this).attr('label') !== undefined) {
	    			  values.push($(this).attr('label').trim());
	              }
	              else {
	            	  values.push($(this).html().trim());
	              }
	    	 })
	    	 $("input[name='LGORT']").val(values.join(","))
	    }
    });
}

function query(){
	var optionCol=['MAKTX','MEINS','LIFNR','LIKTX','SOBKZ','STOCK_QTY','FREEZE_QTY','VIRTUAL_QTY','VIRTUAL_LOCK_QTY','LOCK_QTY','RSB_QTY',
		'XJ_QTY','XJ_BIN_CODE','BATCH','F_BATCH','SO_NO','SO_ITEM_NO']
	$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).showCol(optionCol).trigger('reloadGrid'); 
	var hideCol=[];
	if(vm.STOCK_TYPE=='收料房库存'){
		hideCol=['FREEZE_QTY','VIRTUAL_QTY','VIRTUAL_LOCK_QTY','LOCK_QTY','XJ_QTY','RSB_QTY',
			'XJ_BIN_CODE','SO_NO','SO_ITEM_NO'];
	}

	if(vm.STOCK_TYPE=='仓库库存'){
		hideCol=['VIRTUAL_QTY','VIRTUAL_LOCK_QTY'];
	}	
	DynamicColume(hideCol,"显示列..","#dataGrid","#example-getting-started");
}

//表格下复选框全选、反选;checkall:true全选、false反选
function check_All_unAll(tableId, checkall) {
	if (checkall) {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked", true);
	} else {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked",false);
	}
}


function funFromjs(result) {
	//js.showMessage("last_scan_ele:"+last_scan_ele);
	if(last_scan_ele.indexOf("bin_code")>=0){
		$(last_scan_ele).val(result);
		return false;
	}
	var str_arr = result.split(";");
	if(str_arr.length>0){
		var matnr = "";
		for(var i=0;i<str_arr.length;i++){
			var str = str_arr[i];
			if(str.split(":")[0]=='M'){
				matnr=str.split(":")[1].split("/")[0];
			}
		}
		
		var e = $.Event('keydown');
		e.keyCode = 13;
		$(last_scan_ele).val(matnr).trigger(e);
		$("#searchForm").submit();
	}
	
}

function doScan(ele) {
	var url = "../../doScan";
	last_scan_ele = "#" + ele;
	if(IsPC() ){
		$(last_scan_ele).focus();
		return false;
	}
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

