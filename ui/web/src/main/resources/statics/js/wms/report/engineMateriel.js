var listdata = []
var vm = new Vue({
	el:'#rrapp',
	data:{
		wh_list:[],
		werks:'',
		project:[],
		whNumber:''
	},
	watch:{
		werks : {
			handler:function(newVal,oldVal){
				$.ajax({
					url:baseURL+"common/getWhDataByWerks",
					dataType : "json",
					type : "post",
					data : {
						"WERKS":newVal,
						"MENU_KEY":"ENGINE_MATERIEL"
					},
					async: true,
					success: function (response) { 
						vm.wh_list=response.data;
						if(response.data.length>0){
			    		  vm.whNumber=response.data[0].WH_NUMBER
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
				"MENU_KEY":"ENGINE_MATERIEL"
			},
			async: true,
			success: function (response) { 
				var strs = "";
				this.wh_list=response.data;
				if(response.data.length>0){
				  this.whNumber=response.data[0].WH_NUMBER//方便 v-mode取值
	    		}
			}
		});	
	},
	methods: {
		query: function () {
			js.loading();
		$.ajax({
			url:baseURL+"report/engineMaterial/list",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#whNumber").val(),
				"PROJECT_CODE":$("#project").val()
			},
			async: true,
			success: function (response) {
				js.closeLoading();
				if(response.data.length>0){
					$("#dataGrid").jqGrid('clearGridData');
					$("#dataGrid").jqGrid('setGridParam',{ 
						datatype:'local', data : response.data, 
				    }).trigger("reloadGrid");
	    		}else{
	    			listdata = [];
	    			$("#dataGrid").jqGrid('clearGridData');
	    		}
			}
		});
		},
		exp: function () {
			export2Excel();
		}
	}
});
$(function () {
	$('#dataGrid').dataGrid({
		datatype: "local",
		data: listdata,
		columnModel: [
			{header:'物料编码', name:'MATNR', width:80, sortable: false,align:"center",frozen: true,},
			{header:'物料描述', name:'MAKTX', width:80, sortable: false,align:"center",frozen: true,},		
			{header:'责任人', name:'PERSON', width:80, sortable: false,align:"center",frozen: true,},
			{header:'单量', name:'SINGLE', width:80, sortable: false,align:"center",frozen: true,},
			{header:'库存数量', name:'STOCKQTY', width:80, sortable: false,align:"center",frozen: true,},
			{header:'可装数量', name:'VIRTUALQTY', width:80, sortable: false,align:"center",frozen: true,},
			{header:'冻结数量', name:'FREEZEQTY', width:80, sortable: false,align:"center",frozen: true,},
			{header:'供应商代码', name:'LIFNR', width:80, sortable: false,align:"center",frozen: true,},
			{header:'库位', name:'LGORT', width:80, sortable: false,align:"center",frozen: true,},
			{header:'上月结存', name:'SURPLUS', width:80, sortable: false,align:"center",frozen: true,},
			{header:'量产领料', name:'BATCHPICK1', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK1', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN1', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE1', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE1', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN1', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY1', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK2', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK2', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN2', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE2', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE2', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN2', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY2', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK3', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK3', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN3', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE3', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE3', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN3', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY3', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK4', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK4', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN4', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE4', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE4', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN4', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY4', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK5', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK5', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN5', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE5', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE5', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN5', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY5', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK6', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK6', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN6', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE6', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE6', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN6', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY6', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK7', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK7', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN7', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE7', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE7', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN7', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY7', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK8', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK8', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN8', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE8', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE8', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN8', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY8', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK9', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK9', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN9', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE9', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE9', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN9', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY9', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK10', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK10', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN10', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE10', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE10', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN10', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY10', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK11', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK11', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN11', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE11', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE11', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN11', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY11', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK12', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK12', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN12', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE12', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE12', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN12', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY12', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK13', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK13', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN13', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE13', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE13', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN13', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY13', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK14', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK14', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN14', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE14', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE14', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN14', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY14', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK15', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK15', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN15', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE15', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE15', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN15', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY15', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK16', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK16', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN16', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE16', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE16', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN16', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY16', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK17', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK17', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN17', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE17', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE17', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN17', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY17', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK18', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK18', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN18', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE18', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE18', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN18', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY18', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK19', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK19', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN19', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE19', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE19', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN19', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY19', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK20', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK20', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN20', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE20', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE20', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN20', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY20', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK21', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK21', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN21', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE21', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE21', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN21', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY21', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK22', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK22', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN22', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE22', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE22', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN22', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY22', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK23', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK23', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN23', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE23', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE23', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN23', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY23', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK24', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK24', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN24', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE24', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE24', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN24', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY24', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK25', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK25', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN25', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE25', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE25', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN25', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY25', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK26', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK26', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN26', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE26', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE26', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN26', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY26', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK27', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK27', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN27', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE27', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE27', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN27', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY27', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK28', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK28', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN28', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE28', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE28', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN28', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY28', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK29', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK29', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN29', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE29', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE29', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN29', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY29', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK30', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK30', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN30', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE30', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE30', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN30', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY30', width:80, sortable: false,align:"center"},
			{header:'量产领料', name:'BATCHPICK31', width:80, sortable: false,align:"center"},
			{header:'零散领料', name:'BITPICK31', width:80, sortable: false,align:"center"},
			{header:'退货', name:'RETURN31', width:80, sortable: false,align:"center"},
			{header:'售后', name:'AFTERSALE31', width:80, sortable: false,align:"center"},
			{header:'冻结数量', name:'FREEZE31', width:80, sortable: false,align:"center"},
			{header:'合格进仓', name:'IN31', width:80, sortable: false,align:"center"},
			{header:'配件库结存', name:'QTY31', width:80, sortable: false,align:"center"},
			
		],	
		viewrecords: false,
		frozen: true,
		shrinkToFit: false,
		showCheckbox:false,
		gridComplete: function() {
			let day = new Date().getDate()
			for(var i=day+1;i<32;i++){
				$("#dataGrid").setGridParam().hideCol("BATCHPICK"+i)
				$("#dataGrid").setGridParam().hideCol("BITPICK"+i)
				$("#dataGrid").setGridParam().hideCol("RETURN"+i)
				$("#dataGrid").setGridParam().hideCol("AFTERSALE"+i)
				$("#dataGrid").setGridParam().hideCol("FREEZE"+i)
				$("#dataGrid").setGridParam().hideCol("IN"+i)
				$("#dataGrid").setGridParam().hideCol("QTY"+i)
			}
		}
	});	
	var Headers = []
	var weekArray = new Array("日", "一", "二", "三", "四", "五", "六");
	for(var i = 1;i<32;i++){
		let ColumnName = 'BATCHPICK'+i
		let day = new Date().getFullYear()+'/'+(new Date().getMonth()+1)+'/'+i
		let title = day + "&nbsp;&nbsp;&nbsp;" + "星期" + weekArray[new Date(day).getDay()]
		let o = {startColumnName:ColumnName, numberOfColumns:7, titleText: title}
		Headers.push(o)
	}
	$("#dataGrid").jqGrid('setGroupHeaders', {
		useColSpanStyle: true,
		groupHeaders: Headers
	})
	$("#dataGrid").jqGrid('setFrozenColumns');
});
function export2Excel(){
	var column= [
		{title:'物料编码', data:'MATNR' },
		{title:'物料描述', data:'MAKTX'},
		{title:'责任人', data:'PERSON'},
		{title:'料号', data:'MATNR'},
		{title:'单量', data:'SINGLE'},
		{title:'库存数量', data:'STOCKQTY'},
		{title:'可装数量', data:'VIRTUALQTY'},
		{title:'冻结数量', data:'FREEZEQTY'},
		{title:'供应商代码', data:'LIFNR'},
		{title:'库位', data:'LGORT'},
		{title:'上月结存', data:'SURPLUS'},
		{title: '量产领料', data:'BATCHPICK1'},
		{title: '零散领料', data:'BITPICK1'},
		{title: '退货', data:'RETURN1'},
		{title: '售后', data:'AFTERSALE1'},
		{title: '冻结数量', data:'FREEZE1'},
		{title: '合格进仓', data:'IN1'},
		{title: '配件库结存', data:'QTY1'},
		{title: '量产领料', data:'BATCHPICK2'},
		{title: '零散领料', data:'BITPICK2'},
		{title: '退货', data:'RETURN2'},
		{title: '售后', data:'AFTERSALE2'},
		{title: '冻结数量', data:'FREEZE2'},
		{title: '合格进仓', data:'IN2'},
		{title: '配件库结存', data:'QTY2'},
		{title: '量产领料', data:'BATCHPICK3'},
		{title: '零散领料', data:'BITPICK3'},
		{title: '退货', data:'RETURN3'},
		{title: '售后', data:'AFTERSALE3'},
		{title: '冻结数量', data:'FREEZE3'},
		{title: '合格进仓', data:'IN3'},
		{title: '配件库结存', data:'QTY3'},
		{title: '量产领料', data:'BATCHPICK4'},
		{title: '零散领料', data:'BITPICK4'},
		{title: '退货', data:'RETURN4'},
		{title: '售后', data:'AFTERSALE4'},
		{title: '冻结数量', data:'FREEZE4'},
		{title: '合格进仓', data:'IN4'},
		{title: '配件库结存', data:'QTY4'},
		{title: '量产领料', data:'BATCHPICK5'},
		{title: '零散领料', data:'BITPICK5'},
		{title: '退货', data:'RETURN5'},
		{title: '售后', data:'AFTERSALE5'},
		{title: '冻结数量', data:'FREEZE5'},
		{title: '合格进仓', data:'IN5'},
		{title: '配件库结存', data:'QTY5'},
		{title: '量产领料', data:'BATCHPICK6'},
		{title: '零散领料', data:'BITPICK6'},
		{title: '退货', data:'RETURN6'},
		{title: '售后', data:'AFTERSALE6'},
		{title: '冻结数量', data:'FREEZE6'},
		{title: '合格进仓', data:'IN6'},
		{title: '配件库结存', data:'QTY6'},
		{title: '量产领料', data:'BATCHPICK7'},
		{title: '零散领料', data:'BITPICK7'},
		{title: '退货', data:'RETURN7'},
		{title: '售后', data:'AFTERSALE7'},
		{title: '冻结数量', data:'FREEZE7'},
		{title: '合格进仓', data:'IN7'},
		{title: '配件库结存', data:'QTY7'},
		{title: '量产领料', data:'BATCHPICK8'},
		{title: '零散领料', data:'BITPICK8'},
		{title: '退货', data:'RETURN8'},
		{title: '售后', data:'AFTERSALE8'},
		{title: '冻结数量', data:'FREEZE8'},
		{title: '合格进仓', data:'IN8'},
		{title: '配件库结存', data:'QTY8'},
		{title: '量产领料', data:'BATCHPICK9'},
		{title: '零散领料', data:'BITPICK9'},
		{title: '退货', data:'RETURN9'},
		{title: '售后', data:'AFTERSALE9'},
		{title: '冻结数量', data:'FREEZE9'},
		{title: '合格进仓', data:'IN9'},
		{title: '配件库结存', data:'QTY9'},
		{title: '量产领料', data:'BATCHPICK10'},
		{title: '零散领料', data:'BITPICK10'},
		{title: '退货', data:'RETURN10'},
		{title: '售后', data:'AFTERSALE10'},
		{title: '冻结数量', data:'FREEZE10'},
		{title: '合格进仓', data:'IN10'},
		{title: '配件库结存', data:'QTY10'},
		{title: '量产领料', data:'BATCHPICK11'},
		{title: '零散领料', data:'BITPICK11'},
		{title: '退货', data:'RETURN11'},
		{title: '售后', data:'AFTERSALE11'},
		{title: '冻结数量', data:'FREEZE11'},
		{title: '合格进仓', data:'IN11'},
		{title: '配件库结存', data:'QTY11'},
		{title: '量产领料', data:'BATCHPICK12'},
		{title: '零散领料', data:'BITPICK12'},
		{title: '退货', data:'RETURN12'},
		{title: '售后', data:'AFTERSALE12'},
		{title: '冻结数量', data:'FREEZE12'},
		{title: '合格进仓', data:'IN12'},
		{title: '配件库结存', data:'QTY12'},
		{title: '量产领料', data:'BATCHPICK13'},
		{title: '零散领料', data:'BITPICK13'},
		{title: '退货', data:'RETURN13'},
		{title: '售后', data:'AFTERSALE13'},
		{title: '冻结数量', data:'FREEZE13'},
		{title: '合格进仓', data:'IN13'},
		{title: '配件库结存', data:'QTY13'},
		{title: '量产领料', data:'BATCHPICK14'},
		{title: '零散领料', data:'BITPICK14'},
		{title: '退货', data:'RETURN14'},
		{title: '售后', data:'AFTERSALE14'},
		{title: '冻结数量', data:'FREEZE14'},
		{title: '合格进仓', data:'IN14'},
		{title: '配件库结存', data:'QTY14'},
		{title: '量产领料', data:'BATCHPICK15'},
		{title: '零散领料', data:'BITPICK15'},
		{title: '退货', data:'RETURN15'},
		{title: '售后', data:'AFTERSALE15'},
		{title: '冻结数量', data:'FREEZE15'},
		{title: '合格进仓', data:'IN15'},
		{title: '配件库结存', data:'QTY15'},
		{title: '量产领料', data:'BATCHPICK16'},
		{title: '零散领料', data:'BITPICK16'},
		{title: '退货', data:'RETURN16'},
		{title: '售后', data:'AFTERSALE16'},
		{title: '冻结数量', data:'FREEZE16'},
		{title: '合格进仓', data:'IN16'},
		{title: '配件库结存', data:'QTY16'},
		{title: '量产领料', data:'BATCHPICK17'},
		{title: '零散领料', data:'BITPICK17'},
		{title: '退货', data:'RETURN17'},
		{title: '售后', data:'AFTERSALE17'},
		{title: '冻结数量', data:'FREEZE17'},
		{title: '合格进仓', data:'IN17'},
		{title: '配件库结存', data:'QTY17'},
		{title: '量产领料', data:'BATCHPICK18'},
		{title: '零散领料', data:'BITPICK18'},
		{title: '退货', data:'RETURN18'},
		{title: '售后', data:'AFTERSALE18'},
		{title: '冻结数量', data:'FREEZE18'},
		{title: '合格进仓', data:'IN18'},
		{title: '配件库结存', data:'QTY18'},
		{title: '量产领料', data:'BATCHPICK19'},
		{title: '零散领料', data:'BITPICK19'},
		{title: '退货', data:'RETURN19'},
		{title: '售后', data:'AFTERSALE19'},
		{title: '冻结数量', data:'FREEZE19'},
		{title: '合格进仓', data:'IN19'},
		{title: '配件库结存', data:'QTY19'},
		{title: '量产领料', data:'BATCHPICK20'},
		{title: '零散领料', data:'BITPICK20'},
		{title: '退货', data:'RETURN20'},
		{title: '售后', data:'AFTERSALE20'},
		{title: '冻结数量', data:'FREEZE20'},
		{title: '合格进仓', data:'IN20'},
		{title: '配件库结存', data:'QTY20'},
		{title: '量产领料', data:'BATCHPICK21'},
		{title: '零散领料', data:'BITPICK21'},
		{title: '退货', data:'RETURN21'},
		{title: '售后', data:'AFTERSALE21'},
		{title: '冻结数量', data:'FREEZE21'},
		{title: '合格进仓', data:'IN21'},
		{title: '配件库结存', data:'QTY21'},
		{title: '量产领料', data:'BATCHPICK22'},
		{title: '零散领料', data:'BITPICK22'},
		{title: '退货', data:'RETURN22'},
		{title: '售后', data:'AFTERSALE22'},
		{title: '冻结数量', data:'FREEZE22'},
		{title: '合格进仓', data:'IN22'},
		{title: '配件库结存', data:'QTY22'},
		{title: '量产领料', data:'BATCHPICK23'},
		{title: '零散领料', data:'BITPICK23'},
		{title: '退货', data:'RETURN23'},
		{title: '售后', data:'AFTERSALE23'},
		{title: '冻结数量', data:'FREEZE23'},
		{title: '合格进仓', data:'IN23'},
		{title: '配件库结存', data:'QTY23'},
		{title: '量产领料', data:'BATCHPICK24'},
		{title: '零散领料', data:'BITPICK24'},
		{title: '退货', data:'RETURN24'},
		{title: '售后', data:'AFTERSALE24'},
		{title: '冻结数量', data:'FREEZE24'},
		{title: '合格进仓', data:'IN24'},
		{title: '配件库结存', data:'QTY24'},
		{title: '量产领料', data:'BATCHPICK25'},
		{title: '零散领料', data:'BITPICK25'},
		{title: '退货', data:'RETURN25'},
		{title: '售后', data:'AFTERSALE25'},
		{title: '冻结数量', data:'FREEZE25'},
		{title: '合格进仓', data:'IN25'},
		{title: '配件库结存', data:'QTY25'},
		{title: '量产领料', data:'BATCHPICK26'},
		{title: '零散领料', data:'BITPICK26'},
		{title: '退货', data:'RETURN26'},
		{title: '售后', data:'AFTERSALE26'},
		{title: '冻结数量', data:'FREEZE26'},
		{title: '合格进仓', data:'IN26'},
		{title: '配件库结存', data:'QTY26'},
		{title: '量产领料', data:'BATCHPICK27'},
		{title: '零散领料', data:'BITPICK27'},
		{title: '退货', data:'RETURN27'},
		{title: '售后', data:'AFTERSALE27'},
		{title: '冻结数量', data:'FREEZE27'},
		{title: '合格进仓', data:'IN27'},
		{title: '配件库结存', data:'QTY27'},
		{title: '量产领料', data:'BATCHPICK28'},
		{title: '零散领料', data:'BITPICK28'},
		{title: '退货', data:'RETURN28'},
		{title: '售后', data:'AFTERSALE28'},
		{title: '冻结数量', data:'FREEZE28'},
		{title: '合格进仓', data:'IN28'},
		{title: '配件库结存', data:'QTY28'},
		{title: '量产领料', data:'BATCHPICK29'},
		{title: '零散领料', data:'BITPICK29'},
		{title: '退货', data:'RETURN29'},
		{title: '售后', data:'AFTERSALE29'},
		{title: '冻结数量', data:'FREEZE29'},
		{title: '合格进仓', data:'IN29'},
		{title: '配件库结存', data:'QTY29'},
		{title: '量产领料', data:'BATCHPICK30'},
		{title: '零散领料', data:'BITPICK30'},
		{title: '退货', data:'RETURN30'},
		{title: '售后', data:'AFTERSALE30'},
		{title: '冻结数量', data:'FREEZE30'},
		{title: '合格进仓', data:'IN30'},
		{title: '配件库结存', data:'QTY30'},
		{title: '量产领料', data:'BATCHPICK31'},
		{title: '零散领料', data:'BITPICK31'},
		{title: '退货', data:'RETURN31'},
		{title: '售后', data:'AFTERSALE31'},
		{title: '冻结数量', data:'FREEZE31'},
		{title: '合格进仓', data:'IN31'},
		{title: '配件库结存', data:'QTY31'}
		] 
	var results=[];
	var params=$("#searchForm").serialize();
	var reg = new RegExp("(^|&)" + "pageSize" + "=([^&]*)(&|$)", "i"); 
	var l =params.replace(reg,'$1');
	params=l;
	$.ajax({
		url:baseURL+"report/engineMaterial/list",
		dataType : "json",
		type : "post",
		data : params,
		async: false,
		success: function (response) { 
		    results=response.data;		    
		}
	});
	var rowGroups=[];
	getMergeTable1(results,column,rowGroups,"发动机物料报表");	
}
function getMergeTable1(result,columns,rowsGroup,filename){
	/**
	 * 创建table
	 */
	var table=document.getElementById("tb_excel");
	
	/**
	 * 创建table head
	 */
	var title = []
	var weekArray = new Array("日", "一", "二", "三", "四", "五", "六");
	for(var i = 1;i<32;i++){
		let day = new Date().getFullYear()+'/'+(new Date().getMonth()+1)+'/'+i
		title[i] = day + "&nbsp;&nbsp;&nbsp;" + "星期" + weekArray[new Date(day).getDay()]
	}
	var table_head1=$("<tr />");
	var table_head2=$("<tr />");
	for(let i = 0;i<11;i++){
		var th=$("<th />");
		th.html(columns[i].title);
		th.attr('rowspan',2);
		$(table_head1).append(th);
	}
	for(let i = 0;i<31;i++){
		var th=$("<th />");
		th.html(title[i+1]);
		th.attr('colspan',7);
		$(table_head1).append(th);
	}
	$(table).append($(table_head1));
	
	$.each(columns,function(i,column){
		if(i>10){
		var th=$("<th />");
		th.attr("class",column.class);
		th.attr("width",column.width);
		th.html(column.title);
		$(table_head2).append(th);
	}
	})
	$(table).append($(table_head2));
	
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
	
	var worker=new Worker(baseRoot+"/statics/js/mergeTableCell.js")
	worker.postMessage(data_process);
	
	worker.onmessage=function(event){
	var trs_data=event.data;
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
	
	/**
	 * 导出excel
	 */
	htmlToExcel("tb_excel", "", "",filename,"");
	
	//导出后清除表格
	$(table).empty();
	worker.terminate();
	}
}



