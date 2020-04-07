var detail=[];
var vm = new Vue({
	el:'#rrapp',
	data:{
		lgortList:[],
		wh_list:[],
		werks:'',
		whNumber:'',
	},
	watch:{
	  werks : {
		handler:function(newVal,oldVal){
			var werks=getUrlKey("werks");
			$.ajax({
				url:baseURL+"common/getWhDataByWerks",
				dataType : "json",
				type : "post",
				data : {
					"WERKS":werks
				},
				async: true,
				success: function (response) { 
					vm.wh_list=response.data
					//vm.whNumber=response.data[0]['WH_NUMBER']
					vm.lgortList=getLgortList();	
//					vm.$nextTick(function(){
//						initLgortSelect()
//					})
					
				}
			});
				
			}
		}
	},
	created:function(){
		var whNumber=getUrlKey("whNumber");
		$("#searchForm").validate({
            rules:{
              //进仓单号
              INBOUND_NO:{
            	  required:true
              }
            },
        });
		$.ajax({
			url:baseURL+"common/getWhDataByWerks",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val()
			},
			async: true,
			success: function (response) { 
				$("#wh").empty();
				var strs = "";
			    $.each(response.data, function(index, value) {
			    	if(value.WH_NUMBER==whNumber){
			    		strs += "<option value=" + value.WH_NUMBER + " selected=true>" + value.WH_NUMBER + "</option>";
			    	}else{
				    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    	}
			    });
			    $("#wh").append(strs);
			}
		});
	},
	methods: {
		
		detail: function () {
			
			$.ajax({
				url:baseURL+"query/inboundQuery/detail",
				dataType : "json",
				type : "post",
				data : {INBOUND_NO:inboundNo},
				async: false,
				success: function (response) { 
				    detail=response.list;
				    showTable(detail);
				}
			});
		},
		onPlantChange:function(event){
			  //工厂变化的时候，更新库存下拉框
	          var werks = event.target.value;
	          $.ajax({
	  			url:baseURL+"common/getWhDataByWerks",
	  			dataType : "json",
	  			type : "post",
	  			data : {
	  				"WERKS":werks
	  			},
	  			async: true,
	  			success: function (response) { 
	  				$("#wh").empty();
	  				var strs = "";
	  			    $.each(response.data, function(index, value) {
	  			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
	  			    });
	  			    $("#wh").append(strs);
	  			}
	  		});
	       },
		exp: function(){
		   export2Excel();
		},
		more: function(e){
			var options = {
		    		type: 2,
		    		maxmin: true,
		    		shadeClose: true,
		    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>选择更多',
		    		area: ["400px", "380px"],
		    		content: "wms/query/conditionChoose.html",  
		    		btn: ['<i class="fa fa-check"></i> 确定'],
		    		success:function(layero, index){
		    			var win = layero.find('iframe')[0].contentWindow;
		    			win.vm.showTable($(e).val());
		    		},
		    		btn1:function(index,layero){
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.getTableData();
						var returnValue= $("#returnValue", layero.find("iframe")[0].contentWindow.document);
						var retVal='';
						$(e).val($(returnValue).val());
						try {
							parent.layer.close(index);
				        } catch(e){
				        	layer.close(index);
				        }
						if(typeof listselectCallback == 'function'){
							try {
								parent.layer.close(index);
					        } catch(e){
					        	layer.close(index);
					        }
						}
		    		}
		    	};
		    	options.btn.push('<i class="fa fa-close"></i> 关闭');
		    	options['btn'+options.btn.length] = function(index, layero){
		     };
		     js.layer.open(options);
		},
		
		printInInternalboundSmallList:function(){//小letter
			var inBoundType=getUrlKey("inBoundType");
			if(inBoundType=='外购进仓单'||'00'==inBoundType){
				$("#subInBoundType1").val("01");
			}else if(inBoundType=='自制进仓单'||'01'==inBoundType){
				$("#subInBoundType1").val("02");
			}
			
			$("#printButton2").click();
			},
		printInboundBigList:function(){//大letter
			var inBoundType=getUrlKey("inBoundType");
			if(inBoundType=='外购进仓单'||'00'==inBoundType){
				$("#subInBoundType").val("01");
			}else if(inBoundType=='自制进仓单'||'01'==inBoundType){
				$("#subInBoundType").val("02");
			}
			$("#printButton3").click();
		},
		del:function(){
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data["ITEM_STATUS"]!='00' && data["ITEM_STATUS"]!='01'){
					js.showErrorMessage('第'+(i+1)+'行项目状态为'+data["ITEM_STATUS_DESC"]+",不允许关闭操作");
					save_flag=false;
					return false;
				}
				
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			var url = baseURL+"query/inboundQuery/delete";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
				data : {
					saveData:JSON.stringify(saveData),
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
			        	//$("#dataGrid").dataGrid("clearGridData");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		}
	}
});
$(function(){
	var inboundNo=getUrlKey("inboundNo");
	var werks=getUrlKey("werks");
	var whNumber=getUrlKey("whNumber");
	var inBoundType=getUrlKey("inBoundType");
	$("#inboundNo").val(inboundNo);
	$("#inboundNo1").val(inboundNo);
	$("#inboundNo2").val(inboundNo);
	$("#inboundNo3").val(inboundNo);
	if(werks!=''){
		$("#werks option[value='"+werks+"']").prop("selected",true);
	}
	if(whNumber!=''){
		$("#whNumber option[value='"+whNumber+"']").prop("selected",true);
	}
	$("#dataGrid").dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'进仓单号', name:'INBOUND_NO', width:110, align:"center"},
			{header:'料号', name:'MATNR', width:90, align:"center"},
			{header:'物料描述', name:'MAKTX', width:165, align:"center",sortable:false},
			{header:'库位', name:'LGORT', width:50, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'储位', name:'BIN_CODE', width:70, align:"center"},
			{header:'供应商', name:'LIFNR', width:60, align:"center",sortable:false},
			{header:'供应商名称', name:'LIKTX', width:120, align:"center",sortable:false},
			{header:'单位', name:'UNIT', width:45, align:"center",sortable:false},
			{header:'库存类型', name:'SOBKZ', width:45, align:"center",sortable:false},
			{header:'进仓数量', name:'IN_QTY', width:70, align:"center",sortable:false},
			{header:'已进仓数量', name:'REAL_QTY', width:80, align:"center",sortable:false},
			{header:'状态', name:'ITEM_STATUS', width:70, align:"center",sortable:false},
			{header:'创建人', name:'CREATOR', width:60, align:"center",sortable:false},
			{header:'创建时间', name:'CREATE_DATE', width:120, align:"center",sortable:false},
			{header:'备注', name:'MEMO', width:80, align:"center",sortable:false},
		],	
		viewrecords: true,
	    shrinkToFit:false,
        rownumWidth: 35, 
        showCheckbox:true,
        multiselect: true,
		rowNum: 15,  
		rowList : [15,30 ,50],
        rownumWidth: 45, 
	});
});
function showTable(data){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: data,
		columnModel: [
	
			
			{header:'料号', name:'MATNR', width:100, align:"center"},
			{header:'物料描述', name:'MAKTX', width:120, align:"center",sortable:false},
			{header:'库位', name:'LGORT', width:60, align:"center"},
			{header:'批次', name:'BATCH', width:100, align:"center"},
			{header:'供应商', name:'LIFNR', width:100, align:"center",sortable:false},
			{header:'单位', name:'UNIT', width:50, align:"center",sortable:false},
			{header:'进仓数量', name:'IN_QTY', width:80, align:"center",sortable:false},
			{header:'已进仓数量', name:'REAL_QTY', width:80, align:"center",sortable:false},
			{header:'备注', name:'MEMO', width:80, align:"center",sortable:false},
			
		],	
		shrinkToFit: false,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
	});
}
function export2Excel(){
	var column=[
		{"title":"单号","data":"INBOUND_NO","class":"center"},
		{"title":'工厂', "data":'WERKS', "class":"center"},
		{"title":'仓库号', "data":'WH_NUMBER',"class":"center"},
		{"title":'库位', "data":'LGORT',"class":"center"},
		{"title":"料号", "data":"MATNR", "class":"center"},
		{"title":"物料描述", "data":"MAKTX", "class":"center"},
		{"title":'批次', "data":'BATCH',"class":"center"},
		{"title":'储位', "data":'BIN_CODE',"class":"center"},
		{"title":'单位', "data":'UNIT', "class":"center"},
		{"title":'进仓数量', "data":'IN_QTY',"class":"center"},
		{"title":'已进仓数量', "data":'REAL_QTY',"class":"center"},
		{"title":'满箱数量', "data":'FULL_BOX_QTY',"class":"center"},
		{"title":'箱数', "data":'BOX_COUNT', "class":"center"},
		{"title":'仓管员', "data":'WH_MANAGER',"class":"center"},
		{"title":'供应商', "data":'LIFNR',"class":"center"}
      ]	;
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	
	var exportlist=[];
	$.ajax({
		url:baseURL+"query/inboundQuery/detail",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
			exportlist=response.page.list;
			console.info(exportlist);
		}
	});
	
	var rowGroups=[];
	getMergeTable(exportlist,column,rowGroups,"进仓单记录");	
}
function getLgortList(){
	var lgortList=[];
	//查询库位
  	$.ajax({
      	url:baseUrl + "common/getLoList",
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
function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}