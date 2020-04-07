//初始化 dataTable默认加载1行
var mydata = [
	 { id:"1",assemblyWerks:"",werksF:"",wmsFlagF:"",lgortF:"",sobkz:"",wmsMoveType:"",sapFlagF:"",sapMoveType:"",msg:""},
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
		            { label: '总装工厂代码', name: 'assemblyWerks', index: 'assemblyWerks', width: 80,align:'center',editable:true },
					{ label: '供货工厂代码', name: 'werksF', index: 'werksF', width: 80,align:'center'  }, 			
					{ label: '是否上wms', name: 'wmsFlagF', index: 'wmsFlagF', width: 80,align:'center'},
					{ label: '发货库位', name: 'lgortF', index: 'lgortF', width: 80,align:'center'  }, 			
					{ label: '库存类型', name: 'sobkz', index: 'sobkz', width: 80,align:'center'  },
					{ label: 'WMS过账移动类型', name: 'wmsMoveType', index: 'wmsMoveType', width: 80,align:'center'  },
					{ label: '过账标识', name: 'sapFlagF', index: 'sapFlagF', width: 80,align:'center'},
					{ label: 'SAP过账移动类型', name: 'sapMoveType', index: 'sapMoveType', width: 80,align:'center'  },
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
				url:baseUrl + "config/assemblylogistics/batchSave",
				type:"post",
				dataType:"json",
				data:{
                    detailList:JSON.stringify(mydata)
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
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["assemblyWerks"]=arr[i][0];
			dataRow["werksF"]=arr[i][1];
			dataRow["wmsFlagF"]=arr[i][2];
			dataRow["lgortF"]=arr[i][3];
			dataRow["sobkz"]=arr[i][4];
			dataRow["wmsMoveType"]=arr[i][5];
			dataRow["sapFlagF"]=arr[i][6];
			dataRow["sapMoveType"]=arr[i][7];
			
			var filtered = checkdata.filter(dofilter);
			console.log("filtered",filtered);
			if(filtered.length>0){
				js.showErrorMessage("存在重复数据！");
				return false;
			}
			checkdata.push(dataRow);
		}
		var arr=[];
		var url = baseURL+"config/assemblylogistics/checkPasteData";
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
  // 名称过滤
  if((dataRow.werksF && dataRow.werksF != element.werksF)
		  ||(dataRow.assemblyWerks && dataRow.assemblyWerks != element.assemblyWerks)
		  ||(dataRow.wmsFlagF && dataRow.wmsFlagF != element.wmsFlagF)
		  ||(dataRow.lgortF && dataRow.lgortF != element.lgortF)
		  ||(dataRow.sobkz && dataRow.sobkz != element.sobkz)
		  ||(dataRow.wmsMoveType && dataRow.wmsMoveType != element.wmsMoveType)
		  ||(dataRow.sapFlagF && dataRow.sapFlagF != element.sapFlagF)
		  ||(dataRow.sapMoveType && dataRow.sapMoveType != element.sapMoveType)){ 
    return false;
  }
  return true;
}
