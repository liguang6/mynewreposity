//初始化 dataTable默认加载1行
var mydata = [
	 { id:"1",werks:"",whNumber:"",matnr:"",maktx:"",lifnr:"",liktx:"",authorizeCode:"",memo:"",msg:""}
	];
var dataRow = {};
var trindex = 2; // 新增行号
var lastrow,lastcell; 
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS: "",
		WH_NUMBER: "",
		warehourse:""
	},
    created: function(){
		this.WERKS=$("#werks").find("option").first().val();
	},
	watch:{
		WERKS:{
			handler:function(newVal,oldVal){
				  //工厂变化的时候，更新库存下拉框
		          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS":newVal},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.WH_NUMBER = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		showTable:function (){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
		        colModel: [			
		            {label: '工厂', name: 'werks',index:"werks", width: "60",align:"center",sortable:false,editable:true}, 
		            {label: '仓库号', name: 'whNumber',index:"whNumber", width: "60",align:"center",sortable:false,editable:false}, 	
		            {label: 'SAP料号', name: 'matnr',index:"matnr", width: "120",align:"center",sortable:false}, 	
		            {label: '物料描述', name: 'maktx',index:"maktx", width: "120",align:"center",sortable:false},
		            {label: '供应商代码', name: 'lifnr',index:"lifnr", width: "90",align:"center",sortable:false}, 
		            {label: '供应商名称', name: 'liktx',index:"liktx", width: "130",align:"center",sortable:false}, 	
		            {label: '授权码', name: 'authorizeCode',index:"authorizeCode", width: "80",align:"center",sortable:false}, 
		            {label: '备注', name: 'memo',index:"memo", width: "120",align:"center",sortable:false}, 	
		            {label: '校验信息提示', name: 'msg',index:"msg", width: "200",align:"center",sortable:false,cellattr: addCellAttr}
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
				url:baseUrl + "config/matmanager/batchSave",
				type:"post",
				dataType:"json",
                data:{
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
		var werks=$('#werks').val();
		var whNumber=$('#whNumber').val();
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["werks"]=arr[i][0];
			dataRow["whNumber"]=arr[i][1];
			dataRow["matnr"]=arr[i][2];
			dataRow["maktx"]=arr[i][3];
			dataRow["lifnr"]=arr[i][4];
			dataRow["liktx"]=arr[i][5];
			dataRow["authorizeCode"]=arr[i][6];
			dataRow["memo"]=arr[i][7];
			if(dataRow["werks"]!=werks && arr[i][1]!=whNumber){
				js.showErrorMessage("请导入"+dataRow["werks"]+"工厂、"+dataRow["whNumber"]+"仓库的数据");
				return;
			}
			checkdata.push(dataRow);
		}
		console.log("checkdata",checkdata);
		var arr=[];
		var url = baseURL+"config/matmanager/checkPasteData";
		$.ajax({
			type: "POST",
		    url: url,
		    dataType : "json",
			data : {
				werks:$("#werks").val(),
				whNumber:$("#whNumber").val(),
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
  // 工厂名称过滤【一次只能复制一个工厂的数据】
  if(dataRow.werks && dataRow.werks == element.werks){ 
	//  alert("一次只能复制一个工厂的数据");
    return false;
  }
  return true;
}