//初始化 dataTable默认加载1行
var mydata = [
	 { id:"1",keyPartsNo:"",keyPartsName:"",matnr:"",maktx:"",msg:""},
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
		            {label: '工厂', name: 'werks',index:"werks", width: "90",align:"center",sortable:false,editable:true}, 	
		            {label: '零部件编号', name: 'keyPartsNo',index:"keyPartsNo", width: "100",align:"center",sortable:false}, 
		            {label: '零部件名称', name: 'keyPartsName',index:"keyPartsName", width: "120",align:"center",sortable:false}, 	
		            {label: 'SAP料号', name: 'matnr',index:"matnr", width: "80",align:"center",sortable:false}, 	
		            {label: '物料描述', name: 'maktx',index:"maktx", width: "170",align:"center",sortable:false},
		            {label: '校验信息提示', name: 'msg',index:"msg", width: "220",align:"center",sortable:false,cellattr: addCellAttr}
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
		saveUpload:function(event){
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
				url:baseUrl + "config/keyparts/batchSave",
				type:"post",
				dataType:"json",
				data:{
                    detailList:JSON.stringify(mydata),
                    werks:$('#werks').val(),
                    whNumber:$('#whNumber').val()
               },
				async:false,
				success:function(data){
					if(data.code === 0)
						vm.saveFlag= true;
					else
						alert('保存失败，'+data.msg);
				}.bind(this)
			});
		},
//		clear:function(){
//		
//			mydata = [
//				 { id:"1",keyPartsNo:"",keyPartsName:"",matnr:"",maktx:"",msg:""},
//				];
//			vm.showTable();
//		}
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
		var werks=$('#werks').val();
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["werks"]=arr[i][0];
			dataRow["keyPartsNo"]=arr[i][1];
			dataRow["keyPartsName"]=arr[i][2];
			dataRow["matnr"]=arr[i][3];
			dataRow["maktx"]=arr[i][4];
			if(dataRow["werks"]!=werks){
				js.showErrorMessage("请导入"+dataRow["werks"]+"工厂的数据");
				return;
			}
			var filtered = checkdata.filter(dofilter);
			console.log("filtered",filtered);
			if(filtered.length>0){
				js.showErrorMessage(dataRow["matnr"]+":零部件重复！");
				return false;
			}
			checkdata.push(dataRow);
		}
		var arr=[];
		var url = baseURL+"config/keyparts/checkPasteData";
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
					var werks=$('#werks').val();
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
