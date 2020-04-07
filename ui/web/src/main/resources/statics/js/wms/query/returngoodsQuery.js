
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		businessList:[],
	},
	created:function(){
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
			    	strs += "<option value=" + value.WH_NUMBER + ">" + value.WH_NUMBER + "</option>";
			    });
			    $("#wh").append(strs);
			}
		});
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		returnDocTypeChange: function(){
			console.log('returnDocTypeChange')
			getReturnType();
		},
		detail: function (returnNo,werks,whNumber,BUSINESS_CLASS,BUSINESS_NAME) {
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 退货单明细</i>',
					baseURL+'wms/query/returngoodsDetailQuery.html?returnNo='+returnNo+'&werks='+werks
					+'&whNumber='+whNumber +'&bclass='+BUSINESS_CLASS +'&bname='+BUSINESS_NAME, 
							true, true);
//			var options = {
//		    		type: 2,
//		    		maxmin: true,
//		    		shadeClose: true,
//		    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>退货单明细',
//		    		area: ["1000px", "560px"],
//		    		content: "wms/query/returngoodsDetailQuery.html",  
//		    		btn: ['<i class="fa fa-check"></i> 导出'],
//		    		success:function(layero, index){
//		    			var win = top[layero.find('iframe')[0]['name']];
//		    			win.vm.detail(returnNo);
//		    		},
//		    		btn1:function(index,layero){
//						var win = top[layero.find('iframe')[0]['name']];
//						win.vm.exp(returnNo);
//		    		}
//		    	};
//		    	options.btn.push('<i class="fa fa-close"></i> 关闭');
//		    	options['btn'+options.btn.length] = function(index, layero){
//	         };
//	         js.layer.open(options);
		},
		more: function(e){
			var options = {
		    		type: 2,
		    		maxmin: true,
		    		shadeClose: true,
		    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>选择更多',
		    		area: ["400px", "380px"],
		    		content: baseURL+"wms/query/conditionChoose.html",  
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
		onPlantChange : function(event) {
				// 工厂变化的时候，更新库存下拉框BUSINESS_NAME
				var werks = event.target.value;
				if (werks === null || werks === '' || werks === undefined) {
					return;
				}
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
	}
});
$(function () {
	getReturnDocType();
	
	$("#returnDocType").change(function () {
		console.log('-->returnDocTypeChange')
		getReturnType($("#returnDocType").val())
	});
	
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-6*24*3600*1000);
	$("#createDateStart").val(formatDate(startDate));
	$("#createDateEnd").val(formatDate(now));
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'单据编号', name:'RETURN_NO', width:180, align:"center",formatter:function(val, obj, row, act){
				var actions = [];
				var link = '<a onClick=vm.detail("'+val+'","'+row.WERKS+'","'+row.WH_NUMBER+'","'+row.BUSINESS_CLASS+'","'+row.BUSINESS_NAME+'") title="明细" style="cursor: pointer;">'+val+'</a>';
				actions.push(link);
			    return actions.join('');
			}},
			{header:'退货类型', name:'RETURN_TYPE', width:120, align:"center"},
			{header:'工厂', name:'WERKS', width:100, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:100, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:100, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:160, align:"center"},
			{header:'创建人', name:'CREATOR', width:120, align:"center"},
			{header:'创建时间', name:'CREATE_DATE', width:120, align:"center"},
			{header:'状态', name:'RETURN_STATUS', width:100, align:"center",formatter:function(val, obj, row, act){
				if(row.DEL=='X')return '删除';
				switch(val){
				case '00':
					return '创建'
					break;
				case '01':
					return '部分下架'
					break;
				case '02':
					return '下架 '
					break;
				case '03':
					return '完成'
					break;
				default:
					return '--'
				}
			}},//退货单状态 字典定义：（00创建 01 部分下架 02下架  03完成）
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		rowList : [  15,30 ,50],
        rownumWidth: 45, 
	});
});
// 退货单类型
function getReturnDocType(){
	$.ajax({
		url:baseURL+"query/returngoodsQuery/queryReturnDocTypeList",
		dataType : "json",
		type : "post",
		data : {},
		async: true,
		success: function (response) { 
			$("#returnDocType").empty();
			var strs = "<option value=''>全部</option>";
		    $.each(response.list, function(index, value) {
		    	strs += "<option value=" + value.CODE + ">" + value.VALUE + "</option>";
		    });
		    $("#returnDocType").append(strs);
		    //getReturnType($("#returnDocType").val());
		}
	});
}
// 退货类型
function getReturnType(type){
	$.ajax({
		url:baseURL+"query/returngoodsQuery/queryReturnTypeList",
		dataType : "json",
		type : "post",
		data : {
			"type":type
		},
		async: true,
		success: function (response) { 
			$("#returnType").empty();
			var strs = "<option value=''>全部</option>";
		    $.each(response.list, function(index, value) {
		    	strs += "<option value=" + value.BUSINESS_NAME + ">" + value.BUSINESS_NAME_DESC + "</option>";
		    });
		    $("#returnType").append(strs);
		}
	});
}