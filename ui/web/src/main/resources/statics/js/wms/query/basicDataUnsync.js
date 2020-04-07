var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS:'',
		BUKRS:'',
		MEG_TYPE:'',
	},
	created:function(){
		this.WERKS=$("#WERKS").val();
	},
})	

$(function () {

	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{header:'工厂', name:'WERKS', align:"center",},
			{header:'公司代码', name:'BUKRS', align:"center",},
			{header:'主数据', name:'BASIC_DATA', align:"center",},
			{header:'记录时间', name:'CREATE_TIME', align:"center",},
			{header:'异常类别', name:'MEG_TYPE', align:"center",formatter:function(val, obj, row, act){
					var html="";
					if(val=='00'){
						 html='供应商主数据'
					}
					if(val=='01'){
						 html='物料主数据'
					}
					return html;
				}
			}
		],	
		viewrecords: true,
	    shrinkToFit:false,
		rowNum: 15,  
		width:1100,
		//showCheckbox:true,
		autowidth:true,
		rowList : [  15,30 ,50],
        rownumWidth: 35, 
        //multiselect: true,
	});
	
	
});

function query(){
	$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid'); 
}