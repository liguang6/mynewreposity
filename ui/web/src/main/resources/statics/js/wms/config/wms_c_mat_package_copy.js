//初始化 dataTable默认加载1行
var mydata = [
	 { id:"1",PACKAGINGMATNR:"",PACKAGINGMATNRTYPE:"",QTY:"",UNIT:"",PACKAGELEVEL:"",PACKAGEQTY:"",msg:""},
	];
var dataRow = {};
var trindex = 20000; // 新增行号
var lastrow,lastcell; 
var vm = new Vue({
	el:'#rrapp',
	data:{
        matPackage:{WERKS:"",PACKAGE_TYPE:"",STATUS:""},
        warehourse:[],
    },
    created: function(){
    	this.matPackage.WERKS = $("#werks").find("option").first().val();
	},
    watch:{
    	headId:{
			handler:function(){
				this.$nextTick(function(){	
                    this.showDataGrid();
				})
			}
		},
		matPackage: {
		　　　　handler(newValue, oldValue) {
		　　　　　　          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS": newValue.WERKS},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.matPackage.WH_NUMBER = resp.data[0].WH_NUMBER;
		        	  }
		          })
		　　　　},
		　　　　deep: true
		}
	},
	methods: {
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$('#werks').val());
		},
		showTable:function (){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				columnModel: [
					{header:"<a id='addRow' href='#' onClick='vm.addRow({})' title='新增'><i class='fa fa-plus-square'></i>&nbsp;</a>", name:'actions', width:80,align:"center", sortable:false, fixed:true, formatter: function(val, obj, row, act){
						var actions = [];	
						actions.push('<a href="#" onclick="vm.delRow('+obj.rowId+','+row.ID+')"><i class="fa fa-trash-o"></i></a>&nbsp;');						
						return actions.join('');
					}, editoptions: {defaultValue: 'new'}},
					{header:'包材物料号', name:'PACKAGINGMATNR',index:'packagingMatnr', width:120, align:"center",editable:true},
					{header:'包材类型', name:'PACKAGINGMATNRTYPE',index:'packagingMatnrType', width:120, align:"center",editable:true},
					{header:'包材使用数量', name:'QTY', width:90,index:'qty', align:"center",editable:true,editrules:{number:true}},
					{header:'包材单位', name:'UNIT',index:'unit', width:60, align:"center",editable:true},
					{header:'<font style="color:red;font-weight:bold">*</font>包装层级', name:'PACKAGELEVEL',index:'packageLevel', width:80, align:"center",editable:true,editrules:{required: true,number:true}},
					{header:'<font style="color:red;font-weight:bold">*</font>包数', name:'PACKAGEQTY',index:'packageQty', width:70, align:"center",editable:true,editrules:{required: true,number:true}},
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
		addRow:function(jsondata){
            //保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		    var rows = $("#dataGrid").jqGrid('getRowData');
		    //获得当前最大行号（数据编号）
		    var rowid = rows.length;
		    //获得新添加行的行号（数据编号）
	        var newrowid = rowid+1;
		    //使用addRowData方法把dataRow添加到表格中
		    //$("#dataGrid").jqGrid("addRowData", newrowid, dataRow, "last");
	        $("#dataGrid").jqGrid('addRow',{  
	            rowID : trindex,  
	            initdata : jsondata,  
	            position :"last",  
	            useDefValues : true,  
	            useFormatter : true,  
	            addRowParams : {extraparam:{  
	            }}  
	        }); 
	        trindex++;
        },
       delRow: function (rowId,id){
            //保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
            // 手动新增行删除
            if(id==undefined){
              
                    $('#dataGrid').dataGrid('delRowData',rowId);
           
            }else{ 
               js.confirm('你确认要删除这条数据吗？', 
				  function(){
					var url = "config/matPackage/delById";
				    $.ajax({
				        type: "POST",
				        url: baseURL + url,
				        dataType : "json",
				        async:false,
						data : {
							"id":id,
						},
				        success: function(data){
				        	if(data.code == 0){
				        		$('#dataGrid').dataGrid('delRowData',rowId);
			                    js.showMessage('删除成功');
			                }else{
			                    alert(data.msg);
			                }
				        }
				    });
				});
            }
        },
	
		saveOrUpdate:function(){
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			 var ids = $("#dataGrid").getDataIDs();	
			// if(ids.length == 0){
			// 	alert("当前还没有勾选任何行项目数据！");
			// 	return false;
			// }
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				// if(data["PACKAGINGMATNR"]=='' || data["PACKAGINGMATNRTYPE"]==''){
				// 	js.showErrorMessage('第'+(i+1)+'行包材物料号/包材类型不能为空');
				// 	save_flag=false;
				// 	return false;
				// }
				if(data["PACKAGEQTY"]==''){
					js.showErrorMessage('第'+(i+1)+'行包数不能为空！');
					save_flag=false;
					return false;
				}
				if(data["PACKAGELEVEL"]==null || data["PACKAGEQTY"]==''){
					js.showErrorMessage('第'+(i+1)+'行包装层级/包数不能为空！');
					save_flag=false;
					return false;
				}
				saveData[i] = data;
				delete saveData[i]["actions"]; 
			}
			var url = "config/matPackage/save";
		    $.ajax({
		        type: "POST",
		        url: baseURL + url,
		        dataType : "json",
		        async:false,
				data : {
					"werks":$("#werks").val(),
					"whNumber":$("#whNumber").val(),
					"packageType":$("#packageType").val(),
					"matnr":$("#matnr").val(),
					"lifnr":$("#lifnr").val(),
					"fullBoxQty":$("#fullBoxQty").val(),
					"status":$("#status").val(),
					"items":JSON.stringify(saveData),
					"headId":vm.headId,
				},
		        success: function(data){
		        	if(data.code == 0){
		        		vm.saveFlag= true;
	                    js.showMessage('操作成功');
	                }else{
	                    alert(data.msg);
	                }
		        }
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
		for(var i = 0; i < arr.length;i++){
			dataRow={};
			dataRow["PACKAGINGMATNR"]=arr[i][0];
			dataRow["PACKAGINGMATNRTYPE"]=arr[i][1];
			dataRow["QTY"]=arr[i][2];
			dataRow["UNIT"]=arr[i][3];
			dataRow["PACKAGELEVEL"]=arr[i][4];
			dataRow["PACKAGEQTY"]=arr[i][5];
			if(dataRow["PACKAGELEVEL"]==''){
				js.showErrorMessage("层级不能为空");
				return;
			}
			
			checkdata.push(dataRow);
		}
//		var arr=[];
//		var url = baseURL+"config/keyparts/checkPasteData";
//		$.ajax({
//			type: "POST",
//		    url: url,
//		    dataType : "json",
//			data : {
//				saveData:JSON.stringify(checkdata),
//			},
//			async:false,
//		    success: function(r){
//		    	if(r.code === 0){
//					var werks=$('#werks').val();
//					mydata=r.list;
		    		$('#dataGrid').jqGrid('setGridParam', {
		    			data: checkdata}).trigger('reloadGrid');
//				}else{
//					js.showMessage(r.msg);
//				}
//			}
//		});
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
