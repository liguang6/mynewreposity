var lastrow='';
var lastcell='';
var vm = new Vue({
	el:'#rrapp',
	data:{
		id:getUrlKey("id"),
		zzj_no:getUrlKey("zzj_no"),
		zzj_name :getUrlKey("zzj_name"),
		type:getUrlKey("type"),
		operate:getUrlKey("operate"),
		show:true,
		result_type:0
    },
	methods: {
		getDetail:function(id){
			$.ajax({
				type : "post",
				dataType : "json",// 返回json格式的数据
				async : false,
				url : baseURL+"/zzjmes/qmTestRecord/getTestRecordList",
				data : {
					"head_id" : id,
				},
				success:function(response){
					if(response.code==0){
						var mydata=response.product_data!=null ? response.product_data : [] ;
						proDetailTable(mydata);
						if(vm.type=='qc'){
							var myqcdata=response.qc_data!=null ? response.qc_data : [];
							qcDetailTable(myqcdata);
						}
					}else{
						js.showErrorMessage("查找检验规则失败！");
					}
				}	
			});
		},
		save:function(){
			$('#dataGrid1').jqGrid("saveCell", lastrow, lastcell);
			$('#dataGrid2').jqGrid("saveCell", lastrow, lastcell);
        	var rows=$("#dataGrid1").jqGrid('getRowData');
        	if(rows.length == 0){
				alert("当前还没有数据！");
				return false;
			}
        	for(var i in rows){
        		var data=rows[i];
        		if(data.outward_standard=='' || 
        			data.size_standard=='' || data.outward_result=='' ||
        			data.size_result=='' || data.test_result==''){
        			js.showErrorMessage("生产检验第"+(Number(i)+1)+"行：数据录入不完整！");
        			return false;
        		}
        	}
        	var qc_rows=[];
        	console.log('vm.type',vm.type);
        	if(vm.type=='qc'){
        		qc_rows=$("#dataGrid2").jqGrid('getRowData');
	        	if(qc_rows.length == 0){
					alert("当前还没有数据！");
					return false;
				}	
	        	for(var i in qc_rows){
	        		var data=qc_rows[i];
	        		if(data.outward_result=='' ||
	        			data.size_result=='' || data.test_result==''){
	        			js.showErrorMessage("品质检验第"+(Number(i)+1)+"行：数据录入不完整！");
	        			return false;
	        		}
	        	}
	        	vm.result_type=1;
        	}
        	js.loading("正在保存数据,请您耐心等待...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				url:baseUrl + "zzjmes/qmTestRecord/save",
				data:{
					size_standard:rows[0].size_standard,
					outward_standard:rows[0].outward_standard,
					pro_detail_list:JSON.stringify(rows),
					qc_detail_list:JSON.stringify(qc_rows),
					head_id:rows[0].head_id,
					product_test:rows[0].product_test,
					product_test_date:rows[0].product_test_date,
					result_type:vm.result_type
				},
				type:"post",
				dataType:"json",
				success:function(resp){
					js.closeLoading();
					if(resp.code == 0){
						js.showMessage("保存成功!");
						$("#dataGrid1").jqGrid('clearGridData');
						$("#dataGrid2").jqGrid('clearGridData');
						vm.zzj_name='';
						vm.zzj_no='';
					}else{
						alert("保存失败：" + resp.msg);
					} 
				},
				complete:function(XMLHttpRequest, textStatus){
					$("#btnSave").removeAttr("disabled");
				}
			});	
		},
	}
});

$(function () {
	vm.getDetail(vm.id);
});
function proDetailTable(mydata){
	$("#dataGrid1").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [		
            {label: '<SPAN STYLE="color:red">*</SPAN>检具编号', name: 'test_tool_no',index:"test_tool_no", width: "150",align:"center",sortable:false,editable:true,
            	editoptions:{
            		dataInit:function(el){
            			console.log("test_tool_no dataInit");
            		　 $(el).keydown(function(){
            		　         getTestToolNoSelect(el,null);
            		　 });
            	},}
            },
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>外观标准', name: 'outward_standard',index:"outward_standard", width: "170",align:"center",editable:true,editoptions:{
        		dataInit:function(el){
          		　 $(el).keydown(function(){
          		　         getTestStandardNoSelect(el,null);
          		　 });
          	}}  // ,dataEvents:[{type:"change",fn:function(e){}},]
          },
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>尺寸标准', name: 'size_standard',index:"size_standard", width: "120",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>外观检验结果', name: 'outward_result',index:"outward_result", width: "150",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>尺寸检验结果', name: 'size_result',index:"size_result", width: "150",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>判定', name: 'test_result',index:"test_result", width: "60",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){
						 
					}},	
				]},
			},
            {label: '检验员', name: 'product_test',index:"product_test", width: "80",align:"center",sortable:false},
            {label: '检验日期', name: 'product_test_date',index:"product_test_date", width: "90",align:"center",sortable:false},
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false},
            {label:'ID',name:'id',hidden:true,formatter:"integer"},
            {label:'HEAD_ID',name:'head_id',hidden:true,formatter:"integer"},
        ],
        loadComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			$("#dataGrid2").jqGrid("saveCell", lastrow, lastcell);
			lastrow=iRow;
			lastcell=iCol;
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var grid=$("#dataGrid1");
			var rowData = $(grid).jqGrid('getRowData',rowid);
			// 第一行录入数据  自动向下填充（除判定）
			if(iRow==1 && cellname!='test_result'){
				var ids=  $(grid).jqGrid("getDataIDs");
				for(var i in ids){
					$(grid).jqGrid("setCell",ids[i],cellname,value);
				}
			}
		},
		shrinkToFit: false,
		rownumbers: false,
		cellsubmit:'clientArray',
		cellEdit:true,
    });	
}	
function qcDetailTable(mydata){
	$("#dataGrid2").dataGrid({
		datatype: "local",
		data: mydata,
        colModel: [		
            
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>外观检验结果', name: 'outward_result',index:"outward_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>尺寸检验结果', name: 'size_result',index:"size_result", width: "180",align:"center",sortable:false,editable:true,},
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>判定', name: 'test_result',index:"test_result", width: "70",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){
						 
					}},	
				]},
			},
			{label: '复判', name: 'retest_result',index:"retest_result", width: "70",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"OK:OK;NG:NG",dataEvents:[
					{type:"click",fn:function(e){
						 
					}},	
				]},
			},
//            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>检验员', name: 'product_test',index:"product_test", width: "90",align:"center",editable:true,sortable:false},
//            {label: '<SPAN STYLE="COLOR:RED">*</SPAN>检验日期', name: 'product_test_date',index:"product_test_date", width: "120",align:"center",editable:true,editrules:{required: true},
//            	editoptions:{
//            		dataInit:function(el){
//            		　 $(el).click(function(){
//            		　         WdatePicker();
//            		　 });
//            	}}
//            },
            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "150",align:"center",sortable:false},
            {label:'ID',name:'id',hidden:true,formatter:"integer"},
            {label:'HEAD_ID',name:'head_id',hidden:true,formatter:"integer"},
        ],
        loadComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
		},
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			$("#dataGrid1").jqGrid("saveCell", lastrow, lastcell);
			lastrow=iRow;
			lastcell=iCol;
		},
		
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var grid=$("#dataGrid2");
			var rowData = $(grid).jqGrid('getRowData',rowid);
			// 第一行录入数据  自动向下填充（除判定）
			if(iRow==1 && cellname!='test_result' && cellname!='retest_result'){
				var ids=  $(grid).jqGrid("getDataIDs");
				for(var i in ids){
					$(grid).jqGrid("setCell",ids[i],cellname,value);
				}
			}
		},
		shrinkToFit: false,
		rownumbers: false,
		cellsubmit:'clientArray',
		cellEdit:true,
    });	
}	
function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}
