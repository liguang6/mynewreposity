
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
		this.businessList=getBusinessList("01");
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		detail: function (inboundNo) {
			var options = {
		    		type: 2,
		    		maxmin: true,
		    		shadeClose: true,
		    		title: '<i aria-hidden="true" class="fa fa-plus">&nbsp;</i>退货单明细',
		    		area: ["1000px", "560px"],
		    		content: "wms/query/returngoodsDetailQuery.html",  
		    		btn: ['<i class="fa fa-check"></i> 导出'],
		    		success:function(layero, index){
		    			var win = layero.find('iframe')[0].contentWindow;
		    			win.vm.detail(returnNo);
		    		},
		    		btn1:function(index,layero){
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.exp(returnNo);
		    		}
		    	};
		    	options.btn.push('<i class="fa fa-close"></i> 关闭');
		    	options['btn'+options.btn.length] = function(index, layero){
	         };
	         js.layer.open(options);
		},
	}
});
$(function () {
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'单据编号', name:'RETURN_NO', width:180, align:"center",formatter:function(val, obj, row, act){
				var actions = [];
				var link = '<a onClick=vm.detail("'+val+'") title="明细">'+val+'</a>';
				actions.push(link);
			    return actions.join('');
			}},
			{header:'单据类型', name:'RETURN_TYPE', width:120, align:"center"},
			{header:'工厂', name:'WERKS', width:100, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:100, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:100, align:"center"},
			{header:'创建人', name:'CREATOR', width:120, align:"center"},
			{header:'创建时间', name:'CREATE_DATE', width:190, align:"center"},
			{header:'状态', name:'RETURN_STATUS', width:160, align:"center"},
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		rowList : [  15,30 ,50],
        rownumWidth: 45, 
	});
});
