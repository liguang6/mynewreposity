//初始化 dataTable默认加载1行
var mydata = [
	 { id:"1",werks:"",whNumber:"",matManagerType:"",authorizeCode:"",authorizeName:"",managerType:"",managerStaff:"",manager:"",leaderStaff:"",leader:"",msg:""},
	];
var dataRow = {};
var trindex = 2; // 新增行号
var lastrow,lastcell; 
var vm = new Vue({
	el:'#vue-app',
	data:{},
	created:function(){},
	methods: {
		showTable:function (){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
		        colModel: [			
		            {label: '工厂', name: 'werks',index:"werks", width: "80",align:"center",sortable:false,editable:true}, 
		            {label: '仓库号', name: 'whNumber',index:"whNumber", width: "80",align:"center",sortable:false,editable:false}, 	
		            {label: '物料管理方式', name: 'matManagerTypeDesc',index:"matManagerTypeDesc", width: "100",align:"center",sortable:false}, 	
		            {label: '授权码', name: 'authorizeCode',index:"authorizeCode", width: "70",align:"center",sortable:false},
		            {label: '授权名称', name: 'authorizeName',index:"authorizeName", width: "80",align:"center",sortable:false}, 
		            {label: '类型', name: 'managerTypeDesc',index:"managerTypeDesc", width: "80",align:"center",sortable:false}, 	
		            {label: '管理员工号', name: 'managerStaff',index:"managerStaff", width: "80",align:"center",sortable:false}, 
		            {label: '管理员姓名', name: 'manager',index:"manager", width: "80",align:"center",sortable:false},
		            {label: '主管工号', name: 'leaderStaff',index:"leaderStaff", width: "80",align:"center",sortable:false}, 
		            {label: '主管姓名', name: 'leader',index:"leader", width: "80",align:"center",sortable:false}, 	
                    {label: '校验信息提示', name: 'msg',index:"msg", width: "160",align:"center",sortable:false,cellattr: addCellAttr},
                     {label: '物料管理方式代码', name: 'matManagerType',index:"matManagerType",hidden:true}, 
                      {label: '物料管理方式代码', name: 'matManagerType',index:"matManagerType",hidden:true}, 	
		        ],
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
				shrinkToFit: false,
		        cellEdit:true,
		        cellurl:'#',
				cellsubmit:'clientArray',
		    });
		},
		saveUpload(event){
			for(var i=0;i<mydata.length;i++){
				var msg=mydata[i].msg;
				console.log("msg",msg);
				if(msg!='' && msg!=null){
					alert("第"+(i+1)+"行数据校验未通过");
					return false;
				}
			}
			//保存
			$.ajax({
				url:baseUrl + "config/matmanagertype/batchSave",
				type: "POST",
                dataType : "json",
                data : {
                    detailList:JSON.stringify(mydata),
                },
                async:false,
				success:function(data){
					if(data.code === 0)
						vm.saveFlag= true;
					else
						alert('保存失败，'+data.msg);
				}.bind(this)
			});
		}
	}
});
$(function(){
	vm.showTable();
	$('#dataGrid').bind('paste', function(e) {
		$('#dataGrid').jqGrid('setGridParam', {
			data: mydata}).trigger('reloadGrid');
		console.log("-->paste");
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// console.log(data.replace(/\t/g, '\\t').replace(/\n/g,'\\n')); //data转码
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "");
		}).map(function(item) {
			return item.split("\t");
		});
		var checkdata=[];
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["werks"]=arr[i][0];
			dataRow["whNumber"]=arr[i][1];
			dataRow["matManagerType"]=arr[i][2];
			dataRow["authorizeCode"]=arr[i][3];
			dataRow["authorizeName"]=arr[i][4];
			dataRow["managerType"]=arr[i][5];
			dataRow["managerStaff"]=arr[i][6];
			dataRow["manager"]=arr[i][7];
			dataRow["leaderStaff"]=arr[i][8];
			dataRow["leader"]=arr[i][9];
//			var filtered = checkdata.filter(dofilter);
//			if(filtered.length>0){
//				alert(dataRow["matnr"]+":零部件重复！");
//				return false;
//			}
			checkdata.push(dataRow);
		}
		console.log("submit checkdata",checkdata);
		var arr=[];
		var url = baseURL+"config/matmanagertype/checkPasteData";
		$.ajax({
			type: "POST",
		    url: url,
		    dataType : "json",
			data : {
				saveData:JSON.stringify(checkdata),
			},
			async:false,
		    success: function(r){
		    	if(r.code === 0){
		    		mydata=r.list;
		    		$('#dataGrid').jqGrid('setGridParam', {
		    			data: mydata}).trigger('reloadGrid');
				}else{
					js.showMessage(r.msg);
				}
			}
		});
	});
});	

function addRow(){
	mydata.push([{ id: trindex}]);
	var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
	'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trindex + 
	'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'</tr>';
	$("#dataGrid tbody").append(addtr);
	trindex++;
}
function addCellAttr(rowId, val, rowObject, cm, rdata) {
    if(rowObject.msg != null && rowObject.msg!='' ){
        return "style='color:red'";
    }
}
function trim(str){ 
    return str.replace(/(^\s*)|(\s*$)/g, ""); 
}
//filter回调函数
function dofilter(element, index, array) {
  // 零部件名称过滤
  if(dataRow.matnr && dataRow.matnr != element.matnr){ 
    return false;
  }
  return true;
}