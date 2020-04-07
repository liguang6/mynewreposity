var lastrow,lastcell;
$(function () {
    $("#dataGrid").dataGrid({
    	searchForm: $("#searchForm"),
        columnModel: [	
			{ label: '加工流程代码', name: 'actions', width: 220,align:'center',formatter:function(cellval, obj, row){
				var html='<a title="查看加工节点" onclick="vm.showNodeGrid(\''+row.process_flow_code+'\')" style="cursor:pointer;">'+row.process_flow_code+'</a>';
				return html;
			}},
			{ label: '加工流程名称', name: 'process_flow_name', index: 'process_flow_name', width: 380,align:'center'},
			{ label: '状态', name: 'status_desc', index: 'status_desc', width: 180,align:'center',formatter:function(cellval, obj, row){
				var html="";
				if(row.status=='0'){
					html= "<span>正常</span>"
				}
				if(row.status=='1'){
					html= "<span>停用</span>"
				}
				return html;
			}},
			{ label: '编辑人', name: 'editor', index: 'editor', width: 100,align:'center'  }, 			
			{ label: '编辑时间', name: 'edit_date', index: 'edit_date', width: 230,align:'center'  },
			{ label: 'ID', name: 'id', index: 'id', hidden:true},	
			{ label: 'Status', name: 'status', index: 'status', hidden:true},	
			{ label: 'process_flow_code', name: 'process_flow_code', index: 'process_flow_code', hidden:true},
		],
		loadGridComplete:function(){
			js.closeLoading();
			$(".btn").attr("disabled", false);
		},
		shrinkToFit: false,
    	viewrecords: true,
        showRownum: true, 
        rowNum:15,
        rownumWidth: 35, 
        rowList : [ 15,50,100 ],
    });
    //------删除操作---------
	$("#deleteOperation").click(function(){
		var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		if (gr != null  && gr!==undefined) {
			layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
				var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
				$.ajax({
				type: "POST",
				url: baseURL + "config/bjMesProcessFlow/deleteProcessFlow/"+grData.process_flow_code,
				contentType: "application/json",
				success: function(r){
					if(r.code == 0){
						js.showMessage('删除成功!');
						vm.refresh();
					}else{
						alert(r.msg);
					}
				}
			});
			layer.close(index);
		});
		} 
		else layer.msg("请选择要删除的行",{time:1000});
	});
	//------新增操作---------
	$("#newOperation").click(function(){
		layer.open({
			type : 1,
			offset : '20px',
			skin : 'layui-layer-molv',
			title : "<i class='fa fa-plus' aria-hidden='true'></i>新增加工流程",
			area : [ '500px', '320px' ],
			shade : 0,
			shadeClose : false,
			zIndex :20000,
			content : jQuery("#addDiv"),
			btn : ['保存', '关闭'],
			btn1 : function(index) {
				vm.save();
				return false;
			}
		});
		var mydata=[{status_desc:'正常',status:'0'}];
		$("#addGrid").dataGrid({
			datatype: "local",
			data: mydata,
			colModel: [	
				{label:"<a id=\"addRow\" href=\"#\" onClick='vm.addRow(\"#addGrid\","+JSON.stringify(mydata)+")' title=\"新增\"><i class=\"fa fa-plus-square\"></i>&nbsp;</a>",width:40,align:"center",name:"EMPTY_COL", sortable:false, fixed:true, formatter: function(val, obj, row, act){
					var actions = [];
					actions.push('<a href="#" id=\'del_'+obj.rowId+'\' onclick="vm.delRow(\''+obj.rowId+'\')"><i class="fa fa-trash-o"></i></a>&nbsp;');
					return actions.join('');
				}, editoptions: {defaultValue: 'new'}},
				{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>加工流程代码', name: 'process_flow_code',width: "120",align:"center",sortable:false,editable:true,editrules:{required: true},},
				{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>加工流程名称', name: 'process_flow_name', width: "180",align:"center",sortable:false,editable:true,editrules:{required: true},},
				{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>状态</', name: 'status_desc', width: "80",align:"center",sortable:false,editable:true,edittype:"select",
				editrules:{required: true},editoptions: {value:"0:正常;1:停用",dataEvents:[
					{type:"click",fn:function(e){}},	
				    ]},
				},
				{label: 'status', name: 'status',hidden:true},
			],
			beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			afterSaveCell:function(rowid, cellname, value, iRow, iCol){
				var rowData = $("#addGrid").jqGrid('getRowData',rowid);
				if(cellname=='process_flow_code' && value!=''){
					var rows = $("#addGrid").jqGrid('getRowData');
					var existed = rows.filter(function(val,index,arr){
						return (val.process_flow_code === rowData.process_flow_code);
					});
					if(existed.length >=2){
						js.showErrorMessage(value+":加工流程代码已经存在！");
						$("#addGrid").jqGrid('setCell',rowid,"process_flow_code","&nbsp;");
					}
				}
				if(cellname=='status_desc' && value!='正常'){
					$("#addGrid").jqGrid('setCell',rowid,"status","0"); 
				}
				if(cellname=='status_desc' && value!='停用'){
					$("#addGrid").jqGrid('setCell',rowid,"status","1"); 
				}
			},
			viewrecords: true,	
			shrinkToFit: false,
			cellEdit:true,
			rownumbers: true,
			cellurl:'#',
			cellsubmit:'clientArray',
		});	
	});
	//-------编辑操作--------
	$("#editOperation").click(function(){
		var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		if(gr !== null && gr!==undefined){
			var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			vm.process_flow=grData;
			layer.open({
				type : 1,
				offset : '20px',
				skin : 'layui-layer-molv',
				title : "<i class='fa fa-pencil-square-o' aria-hidden='true'></i>编辑加工流程",
				area : [ '380px', '220px' ],
				shade : 0,
				shadeClose : false,
				//zIndex :20000,
				content : jQuery("#editDiv"),
				btn : ['保存', '关闭'],
				btn1 : function(index) {
					vm.update();
					return false;
				}
			});
		}else{
			layer.msg("请选择要编辑的行",{time:1000});
		}
	});
	
	//加工节点
	$("#newComp").click(function(){
		var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
		if(gr !== null && gr!==undefined){
			var grData = $("#dataGrid").jqGrid("getRowData",gr);//获取选中的行数据
			vm.process_flow=grData;
			layer.open({
				type : 1,
				offset : '20px',
				skin : 'layui-layer-molv',
				title : "<i class='fa fa-plus' aria-hidden='true'></i>&nbsp;新增加工节点",
				area : [ '620px', '360px' ],
				shade : 0,
				shadeClose : false,
				content : jQuery("#editNodeDiv"),
				btn : ['保存', '关闭'],
				btn1 : function(index) {
					vm.saveNode();
					return false;
				}
			});
			vm.getNodeInfo(grData.process_flow_code);
		}else{
			layer.msg("请选择需要编辑的加工流程代码行",{time:2000});
		}
	});
});

var vm = new Vue({
	el:'#rrapp',
    data:{
		process_flow:{},
    },
	methods: {
		refresh:function(){
			$("#searchForm").submit();
        },
		getProcessFlowInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/bjMesProcessFlow/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.process_flow = r.data;
	            }
			});
		},
		showNodeGrid:function(process_flow_code){
			$.ajax({
				type: "POST",
				url: baseUrl+"config/bjMesProcessFlow/getNodeList",
				data: {
					process_flow_code: process_flow_code,
				},
				dataType: "json",
				success: function(result){
					if(result.code == 0){
						layer.open({
							type : 1,
							offset : '20px',
							skin : 'layui-layer-molv',
							title : "查看加工节点",
							area : [ '420px', '320px' ],
							shade : 0,
							shadeClose : false,
							content : jQuery("#editNodeDiv"),
							btn : ['关闭'],
						});
						var mydata=result.data!=null ? result.data : [];
						jQuery('#addNodeGrid').GridUnload(); 
						$("#addNodeGrid").dataGrid({
							datatype: "local",
							data: mydata,
							colModel: [	
								{label: '节点代码', name: 'node_code',index: 'node_code',width: "65",align:"center",sortable:false,},
								{label: '节点名称', name: 'node_name',index: 'node_name', width: "120",align:"center",sortable:false,},
								{label: '是否扫描', name: 'scan_flag_desc', width: "65",align:"center",sortable:false,},
								{label: '上下线节点标识', name: 'plan_node_flag_desc', width: "110",align:"center",sortable:false,},
							],	
							shrinkToFit: false,
							viewrecords: true,	
							rownumbers: true,
							cellurl:'#',
							cellsubmit:'clientArray',
						});		
					}else{
						js.showErrorMessage(result.msg);
					}
				}
			});
		},
		getNodeInfo:function(process_flow_code){
			$.ajax({
				type: "POST",
				url: baseUrl+"config/bjMesProcessFlow/getNodeList",
				data: {
					process_flow_code: process_flow_code,
				},
				dataType: "json",
				success: function(result){
					if(result.code == 0){
						var mydata=result.data!=null ? result.data : [];
						vm.editNodeGrid(mydata);	
					}else{
						js.showErrorMessage(result.msg);
					}
				}
			});
		},
		editNodeGrid:function(datalist){
			jQuery('#addNodeGrid').GridUnload(); 
			var mydata=[{scan_flag_desc:'是',scan_flag:'X'}];
			$("#addNodeGrid").dataGrid({
				datatype: "local",
				data: datalist,
				colModel: [	
					{label:"<a onClick='vm.addRow(\"#addNodeGrid\","+JSON.stringify(mydata)+")' title=\"向后追加行\"><i class=\"fa fa-plus-square\"></i>&nbsp;</a>",width:80,align:"center",name:"EMPTY_COL", sortable:false, fixed:true, formatter: function(val, obj, row, act){
						var actions = [];
						actions.push("<a onClick='vm.addRow(\"#addNodeGrid\","+JSON.stringify(mydata)+",\""+obj.rowId+"\")' title=\"向上追加行\"><i class=\"fa fa-plus-square\"></i>&nbsp;</i></a>&nbsp;&nbsp;");
						actions.push('<a href="#" onclick="vm.delNodeRow(\''+obj.rowId+'\',\''+row.id+'\')"  title=\"删除\"><i class="fa fa-trash-o"></i></a>&nbsp;&nbsp;');
						actions.push('<a href="#" onclick="BindBtnUpMethod(\''+obj.rowId+'\')"  title=\"上移\"><i class="fa fa-long-arrow-up"></i></a>&nbsp;&nbsp;');
						actions.push('<a href="#" onclick="BindBtnDownMethod(\''+obj.rowId+'\')" title=\"下移\"><i class="fa fa-long-arrow-down"></i></a>&nbsp;');
						return actions.join('');
					}, editoptions: {defaultValue: 'new'}},
					{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>节点代码', name: 'node_code',index: 'node_code',width: "100",align:"center",sortable:false,editable:true,editrules:{required: true},editoptions:{
						dataInit:function(el){
						　  $(el).keydown(function(){
							　  getNodeInfoSelect(el,function(node){
								var rowid=$(el).attr("rowid");
								$("#addNodeGrid").jqGrid('setCell', rowid, 'node_name', node.PROCESS_NAME,'not-editable-cell');
								$('#addNodeGrid').jqGrid("saveCell", lastrow, lastcell);
							});
						});
					},}},
					{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>节点名称', name: 'node_name',index: 'node_name', width: "180",align:"center",sortable:false,},
					{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>是否扫描', name: 'scan_flag_desc', width: "80",align:"center",sortable:false,editable:true,edittype:"select",
					editrules:{required: true},editoptions: {value:"X:是;0:否",dataEvents:[
						{type:"click",fn:function(e){}},	
						]},
					},
					{label: '<SPAN STYLE="COLOR:RED"><B>*</B></SPAN>上下线节点标识', name: 'plan_node_flag_desc', width: "120",align:"center",sortable:false,editable:true,edittype:"select",
					editrules:{required: true},editoptions: {value:"0:上线;1:下线",dataEvents:[
						{type:"click",fn:function(e){}},	
						]},
					},
					{label: 'scan_flag', name: 'scan_flag',hidden:true},
					{label: 'online_plan_node_flag', name: 'online_plan_node_flag',hidden:true},
					{label: 'offline_plan_node_flag', name: 'offline_plan_node_flag',hidden:true},
					{label: 'id', name: 'id',hidden:true},
				],
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
				afterSaveCell:function(rowid, cellname, value, iRow, iCol){
					if(cellname=='node_code' && value!=''){
						var rowData = $("#addNodeGrid").jqGrid('getRowData',rowid);
						var rows = $("#addNodeGrid").jqGrid('getRowData');
						var existed = rows.filter(function(val,index,arr){
							return (val.node_code === rowData.node_code);
						});
						console.log("existed",existed);
						if(existed.length >=2){
							js.showErrorMessage(value+":节点代码已经存在！");
							$("#addNodeGrid").jqGrid('setCell',rowid,"node_code","&nbsp;");
							$("#addNodeGrid").jqGrid('setCell',rowid,"node_name","&nbsp;"); 	
						}
					}
					if(cellname=='scan_flag_desc' && value=='X'){
						$("#addNodeGrid").jqGrid('setCell',rowid,"scan_flag","X"); 
					}
					if(cellname=='scan_flag_desc' && value=='0'){
						$("#addNodeGrid").jqGrid('setCell',rowid,"scan_flag","0"); 
					}
					if(cellname=='plan_node_flag_desc' && value=='0'){ // 0 否 X是
						$("#addNodeGrid").jqGrid('setCell',rowid,"online_plan_node_flag","X");
						$("#addNodeGrid").jqGrid('setCell',rowid,"offline_plan_node_flag","0"); 
					}
					if(cellname=='plan_node_flag_desc' && value=='1'){ // 0 否 X是
						$("#addNodeGrid").jqGrid('setCell',rowid,"online_plan_node_flag","0");
						$("#addNodeGrid").jqGrid('setCell',rowid,"offline_plan_node_flag","X"); 
					}
				},
				shrinkToFit: false,
				viewrecords: true,	
				cellEdit:true,
				rownumbers: true,
				cellurl:'#',
				cellsubmit:'clientArray',
			});	
		},
		save:function(){
			$('#addGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#addGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.showErrorMessage("没有需要保存的数据！");
			    return false;
			}
			for(var i in rows){
				var data=rows[i];
				if(data.process_flow_code=='' || data.process_flow_name==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行加工流程代码、名称不能为空！");
					return false;
				}
				delete data.status_desc;
				delete data.EMPTY_COL;
			}
			$.ajax({
				type: "POST",
				url: baseUrl+"config/bjMesProcessFlow/saveProcessFlow",
				data: {
					data_list:JSON.stringify(rows)
				},
				dataType: "json",
				success: function(result){
					if(result.code == 0){//成功
						js.showMessage("保存成功");
						var index =layer.index;// parent.layer.getFrameIndex(window.name); 
						layer.close(index);
						vm.refresh();
					}else{
						js.showErrorMessage(result.msg);
					}
				}
			});
		},
		update:function(){
			$.ajax({
				type: "POST",
				url: baseUrl+"config/bjMesProcessFlow/updateProcessFlow",
				data: {
					process_flow_code: vm.process_flow.process_flow_code,
					process_flow_name: vm.process_flow.process_flow_name,
					status:vm.process_flow.status,
				},
				dataType: "json",
				success: function(result){
					if(result.code == 0){
						var index =layer.index;// parent.layer.getFrameIndex(window.name); 
						layer.close(index);
						vm.process_flow={};
						vm.refresh();
					}
				}
			});
		},
		saveNode:function(){
			$('#addNodeGrid').jqGrid("saveCell", lastrow, lastcell);
			var ids=$("#addNodeGrid").jqGrid('getDataIDs');
			if(ids.length <= 0){
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			var rowData=[];
			for(var i in ids){
				var data=$("#addNodeGrid").jqGrid('getRowData',ids[i]);
				if(data.node_code=='' || data.node_name==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行节点代码、节点名称不能为空！");
					return false;
				}
				if(data.scan_flag=='' || data.plan_node_flag_desc==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行是否扫描、上下线标示不能为空！");
					return false;
				}
				data.seq=$("#addNodeGrid").getCell(ids[i],'rn');
				delete data.status_desc;
				delete data.EMPTY_COL;
				rowData.push(data);
			}
			$.ajax({
				type: "POST",
				url: baseUrl+"config/bjMesProcessFlow/saveNode",
				data: {
					data_list:JSON.stringify(rowData),
					process_flow_code:vm.process_flow.process_flow_code,
					process_flow_name:vm.process_flow.process_flow_name,
					status:vm.process_flow.status,
				},
				dataType: "json",
				success: function(result){
					if(result.code == 0){//成功
						js.showMessage("保存成功");
						var index =layer.index;// parent.layer.getFrameIndex(window.name); 
						layer.close(index);
						vm.process_flow={};
						vm.refresh();
					}else{
						js.showErrorMessage(result.msg);
					}
				}
			});
		},
		addRow:function(tableId,data,selectedId){
			//保存编辑的最后一个单元格
			$(tableId).jqGrid("saveCell", lastrow, lastcell);		
		    var ids = $(tableId).jqGrid('getDataIDs');
			if(ids.length==0){
				ids=[0];
			}
		    var rowid = Math.max.apply(null,ids);
			var newrowid = rowid+1;
			if(selectedId!=undefined){
				$(tableId).jqGrid("addRowData", newrowid, data, "before", selectedId);
			}else{
				$(tableId).jqGrid("addRowData", newrowid, data, "last");
			}
		},
		delRow:function(rowid){
			$('#addGrid').dataGrid('delRowData',''+rowid+'');		
		},
		delNodeRow:function(rowid,dataid){
			var deldata=$("#addNodeGrid").jqGrid('getRowData',rowid);
			$('#addNodeGrid').dataGrid('delRowData',rowid);	
			if(dataid!='' && dataid!='undefined'){
			    var ids=$("#addNodeGrid").jqGrid('getDataIDs');
				var rowData=[];
				for(var i in ids){
					var data=$("#addNodeGrid").jqGrid('getRowData',ids[i]);
					data.seq=$("#addNodeGrid").getCell(ids[i],'rn');
					delete data.status_desc;
					delete data.EMPTY_COL;
					rowData.push(data);
				}
				js.confirm('你确认要删除该加工节点吗？',function(){
					$.ajax({
						url:baseUrl + "config/bjMesProcessFlow/deleteNode",
						data:{
							id:dataid,// 删除行数据id值
							data_list:JSON.stringify(rowData),
						},
						type:"post",
						dataType:"json",
						success:function(resp){
							js.closeLoading();
							if(resp.code == 0){
								js.showMessage("删除成功！");
							}else{
								vm.getNodeInfo(deldata.process_flow_code);
								alert("删除失败：" + resp.msg);
							} 
						},
					});
				  }
			   )
			}	
		},
	}
});
//内容：按钮上移
function BindBtnUpMethod(rowid) {
    var obj = $("#addNodeGrid");
    var IDs = obj.getDataIDs();
    var rowIndex = getRowIndexByRowId(IDs, rowid);
    if (rowIndex == 0) {
		layer.msg("已经置底，不能上移!",{time:1000});
    } else {
        var srcrowid = getRowIdByRowIndex(IDs, rowIndex - 2);
        var rowUpId = getRowIdByRowIndex(IDs, rowIndex - 1);
        var rowData = obj.jqGrid('getRowData', rowid);
        var rowUpData = obj.jqGrid('getRowData', rowUpId);
        //删除当前行
        obj.delRowData(rowid);
        obj.delRowData(rowUpId);
        //新插入一行
        obj.addRowData(rowid, rowData, "after", srcrowid);
        obj.addRowData(rowUpId, rowUpData, "after", rowid);
        //默认再选中新增的这个行
        obj.setSelection(rowid);
    }
}

//内容：根据rowid，获取索引值
function getRowIndexByRowId(Ids, id) {
    var index = 0;
    for (var i = 0; i < Ids.length; i++) {
        if (Ids[i] == id) {
            index = i;
        }
    }
    return index;
}

//内容：根据索引值获取rowid值
function getRowIdByRowIndex(Ids, index) {
    var rowid = "";
    for (var i = 0; i < Ids.length; i++) {
        if (i == index) {
            rowid = Ids[i];
        }
    }
    return rowid;
}

//内容：按钮下移
function BindBtnDownMethod(rowid) {
    var obj = $("#addNodeGrid");
    var IDs = obj.getDataIDs();
    var rowIndex = getRowIndexByRowId(IDs, rowid);
    if (rowIndex == (IDs.length - 1)) {
		layer.msg("已经置底，不能下移!",{time:1000});
    } else {
        var srcrowid = getRowIdByRowIndex(IDs, rowIndex - 1);
        var rowDownId = getRowIdByRowIndex(IDs, rowIndex + 1);
        var rowData = obj.jqGrid('getRowData', rowid);
        var rowDownData = obj.jqGrid('getRowData', rowDownId);
        //删除当前行
        obj.delRowData(rowid);
        obj.delRowData(rowDownId);
        //新插入一行
        obj.addRowData(rowDownId, rowDownData, "after", srcrowid);
        obj.addRowData(rowid, rowData, "after", rowDownId);
        //默认再选中新增的这个行
        obj.setSelection(rowid);
    }
}
function getNodeInfoSelect(elementId, fn_backcall) {
	var node={};
	var list;
	$(elementId).typeahead({	
		source : function(input, process) {
			var data={
				"process":input,
			};		
			return $.ajax({
				url:baseUrl + "masterdata/getProcessList",
				dataType : "json",
				type : "post",
				data : data,
				async: false,
				success: function (response) { 
					list = response.data;
					var results = new Array();
					$.each(list, function(index, value) {
						results.push(value.PROCESS_CODE);
					})
					return process(results);
				}
			});
		},
		items : 15,
		highlighter : function(item) {
			var PROCESS_NAME = "";
			$.each(list, function(index, value) {
				if (value.PROCESS_CODE == item) {
					PROCESS_NAME = value.PROCESS_NAME;
				}
			})
			return item + "  " + PROCESS_NAME;
		},
		matcher : function(item) {
			return true;
		},
		updater : function(item) {
			return item;
		},
		afterSelect:function(item){
			$.each(list, function(index, value) {
				if (value.PROCESS_CODE == item) {
					node=value;
					$(elementId).val(item);	
				}
			});
			if (typeof (fn_backcall) == "function") {
				fn_backcall(node);
			}
		}
	})
}