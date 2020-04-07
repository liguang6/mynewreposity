var mydata = [];
var lastrow,lastcell;
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		WERKS: "",
		whNumber:"",
		warehourse:[]
	},
	created:function(){
		this.WERKS = $("#werks").find("option").first().val();
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
		        		 vm.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })			
			}
		}
	},
	methods: {
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		reload: function (event) {
			$("#searchForm").submit();
		},
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		enter:function(e){
			$("#btnQuery").trigger("click");
		},
    	addtaginput:function(id){
    		$(id).tagsinput({
        		confirmKeys:[32],
        		tagClass:'label label-primary label-tag'
        	});
    	},
		saveResult: function (event) {
			var save_flag=true;
			//保存编辑的最后一个单元格
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var saveData=[];
			//获取选中表格数据
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	

			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			for(var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data["STOCK_QTY"].replace(/(^\s*)|(\s*$)/g, '')==''){
					js.showErrorMessage('第'+(ids[i])+'行移储数量不能为空');
					save_flag=false;
					return false;
				}
				if(data["TARGET_BIN_CODE"].replace(/(^\s*)|(\s*$)/g, '')==''){
					js.showErrorMessage('第'+(ids[i])+'行目标储位不能为空');
					save_flag=false;
					return false;
				}
				saveData.push(data);
			}		
			console.log("saveData",saveData);
			var url = baseURL+"kn/storageMove/save";
			if(save_flag){
				js.loading("正在保存数据，请稍等...");
				$("#btnAdd").html("保存中...");
				$("#btnAdd").attr("disabled","disabled");
				$.ajax({
					type: "POST",
				    url: url,
				    dataType : "json",
				    //async: false,
					data : {
						SAVE_DATA:JSON.stringify(saveData),
					},
				    success: function(r){
				    	if(r.code === 0){
				        	js.showMessage(r.msg+"：保存成功！");
				        	mydata = [];
				        	showTable(mydata);
						}else{
							js.showMessage(r.msg);
						}
					},
					complete: function(XMLHttpRequest, textStatus){
						$("#btnAdd").html("保存");	
						$("#btnAdd").removeAttr("disabled");
						js.closeLoading();
					}
				});
			}
		},
		more: function(e){
			$("#moreGrid").html("")
			$.each(moredata,function(i,d){
				var tr=$("<tr index="+i+"/>");
				var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' > ")
				var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text'name='val_more' style='width:100%;height:30px;border:0'> ")
				$(tr).append(td_1)
				$(tr).append(td_2)
				$("#moreGrid").append(tr)
			})
			
			layer.open({
			  type: 1,
			  title:'选择更多',
			  closeBtn: 1,
			  btn:['确认'],
			  offset:'t',
			  resize:true,
			  maxHeight:'300',
			  area: ['400px','400px'],
			  skin: 'layui-bg-green', //没有背景色
			  shadeClose: false,
			  content: "<div id='more_div'>"+$('#layer_more').html()+"</div>",
			  btn1:function(index){
				  var ckboxs=$("#more_div tbody").find("tr").find("input[type='checkbox']:checked");
				  var values=[]
				  $.each(ckboxs,function(i,ck){
					  var input=$(ck).parent("td").next("td").find("input")
					  if($(input).val().trim().length>0){
						  values.push($(input).val())
					  }					  
				  })
				  $(e).val(values.join(","))
			  }
			});
	  },
	}
});
$(function () {
	// 多个料号隔开js插件
	//vm.addtaginput($("#matnr"));
	$(document).on("click","#checkall",function(e){

		if ($(e.target).is(":checked")) {
			check_All_unAll("#moreGrid",true);	
		}
		if($(e.target).is(":checked")==false){
			//alert("反选")
			check_All_unAll("#moreGrid",false);
		}
	});
	//粘贴
	$(document).on("paste","#more_div input[type='text']",function(e){
		setTimeout(function(){
			//alert($(e.target).val())
			var tr=$(e.target).parent("td").parent("tr")
			
			var index = parseInt($("#more_div tbody").find("tr").index(tr));
			console.info(index)
			 var copy_text=$(e.target).val();		 
			 var dist_list=copy_text.split(" ");
			
			 if(dist_list.length <=0){
				 return false;
			 }
			 $.each(dist_list,function(i,value){
				 console.info(value)
				 $("#more_div tbody").find("tr").eq(index+i).find("input").val(value)
			 })
			 
		},100)
		
	})
	// 初始化，加载table，只显示表头
	showTable(mydata);
	$("#btnQuery").click(function () {
		if($("#matnr").val()==''){
			layer.msg("请输入料号",{time:1000});
			return ;
		}
		$("#btnQuery").val("查询中...");
		$("#btnQuery").attr("disabled","disabled");	
		
		$.ajax({
			url:baseURL+"kn/storageMove/getStockList",
			dataType : "json",
			type : "post",
			data : {
				"WERKS":$("#werks").val(),
				"WH_NUMBER":$("#whNumber").val(),
				"MATNR":$("#matnr").val(),
			},
			async: true,
			success: function (response) { 
				$("#dataGrid").jqGrid("clearGridData", true);
				mydata.length=0;
				if(response.code == "0"){
					mydata = response.list;
					$("#dataGrid").jqGrid('GridUnload');
					showTable(mydata);
					if(mydata.length=="")alert("未查询到符合条件的数据！");
					$("#btnQuery").removeAttr("disabled");
					$("#btnQuery").val("查询");
				}else{
					alert(response.msg);
					$("#btnQuery").val("查询");
					$("#btnQuery").removeAttr("disabled");
				}				
			}
		});
	});	
});
function showTable(data){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: data,
        colModel: [		
            {label: '工厂', name: 'WERKS',index:"WERKS", width: "60",align:"center",sortable:false,frozen: true,},
            {label: '仓库号', name: 'WH_NUMBER',index:"WH_NUMBER", width: "60",align:"center",sortable:false,frozen: true},
            {label: '库位', name: 'LGORT',index:"LGORT", width: "50",align:"center",sortable:false},
            {label: '料号', name: 'MATNR',index:"MATNR", width: "100",align:"center",sortable:true,frozen: true},
            {label: '物料描述', name: 'MAKTX',index:"MAKTX", width: "150",align:"center",sortable:false,frozen: true},
            {label: '单位', name: 'MEINS',index:"MEINS", width: "50",align:"center",sortable:false},
            {label: '批次', name: 'BATCH',index:"BATCH", width: "50",align:"center",sortable:false},
            {label: '特殊库存类型', name: 'SOBKZ',index:"SOBKZ", width: "50",align:"center",sortable:false},
            {label: '供应商代码', name: 'LIFNR',index:"LIFNR", width: "80",align:"center",sortable:false},
            {label: '供应商名称', name: 'LIKTX',index:"LIKTX", width: "120",align:"center",sortable:false},
            {label: '非限制数量', name: 'QTY',index:"QTY", width: "75",align:"center",sortable:false,
            	formatter:function(val, obj, row, act){
				return row.STOCK_QTY;
		      }
            },
            {label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>移储数量</B></SPAN>', name: 'STOCK_QTY',index:"STOCK_QTY", width: "70",align:"center",sortable:false,editable:true,
            editrules:{required: true,number:true}}, 
            {label: '锁定数量', name: 'LOCK_QTY',index:"LOCK_QTY", width: "70",align:"center",sortable:false,},
            {label: '冻结数量', name: 'FREEZE_QTY',index:"FREEZE_QTY", width: "70",align:"center",sortable:false,},
            {label: '源储位', name: 'BIN_CODE',index:"BIN_CODE", width: "130",align:"center",sortable:false,},
        	{label: '<SPAN STYLE="COLOR:RED">*</SPAN><SPAN STYLE="COLOR:BLUE"><B>目标储位</B></SPAN>', name: 'TARGET_BIN_CODE',index:"TARGET_BIN_CODE", width: "130",align:"center",sortable:false,editable:true},
            {label: 'ID', name: 'ID',index:"ID", width: "0",align:"center",hidden:true},
            {label: 'TARGET_BIN_NAME', name: 'TARGET_BIN_NAME',index:"TARGET_BIN_NAME", width: "0",align:"center",hidden:true},
            {label:'是否启用条码管理',name:'BARCODE_FLAG',hidden:true},
        ],
        gridComplete: function() { 
   		 var rowIds = $("#dataGrid").jqGrid("getDataIDs"); 
   		 for(var i = 0, l = rowIds.length; i<l; i++) { 
   			 var rowData = $("#dataGrid").jqGrid("getRowData", rowIds[i]); 
   			 // 启用标签管理  不允许修改移储数量
   			 if(rowData && rowData.BARCODE_FLAG == 'X') { 
   				 $("#dataGrid").jqGrid('setCell', rowIds[i],'STOCK_QTY', rowData.STOCK_QTY,'not-editable-cell');
   			 } 
   		}       	
       },
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			 // 仓库启用条码管理  冻结、解冻数量不可编辑
			var data=$("#dataGrid").jqGrid('getRowData',rowid);
            if(data.BARCODE_FLAG=='X'){
                 $("#dataGrid").jqGrid('setCell', rowid,'QUANTITY', data.QTY,'not-editable-cell');
            }
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		afterSaveCell:function(rowid, cellname, value, iRow, iCol){
			var row=$('#dataGrid').jqGrid('getRowData',rowid);
			// 校验目标储位正确性
			if(cellname=='TARGET_BIN_CODE'){
				var bin=checkBin(row.WH_NUMBER,value);
				console.log("bin.binCode",bin.binCode);
				if(bin.binCode=='' || bin.binCode==undefined){
					js.showErrorMessage('储位【'+value+'】未维护');
					$("#dataGrid").jqGrid('setCell', rowid, cellname, ' ','editable-cell');
					return ;
				}else{
					$("#dataGrid").jqGrid('setCell', rowid, 'TARGET_BIN_NAME',bin.binName ,'not-editable-cell');
				}
			}
			// enter键 移动到下一行单元格
			if(cellname=='STOCK_QTY' || cellname=='TARGET_BIN_CODE'){
				window.setTimeout(function(){ 
               	   $("#dataGrid").jqGrid("editCell",iRow+1,iCol,true); 
               	},100);
			}
			if(cellname == 'STOCK_QTY'){
        		//校验移储数量是否超出非限制数量
        		if(parseInt(value)>parseInt(row.QTY)){
        			js.showErrorMessage('本次输入的移储数量不能超过非限制数量:'+row.QTY);
        			$("#dataGrid").jqGrid('setCell', rowid, cellname, ' ','editable-cell');
        			return ;
        		}
        	}
		},
		shrinkToFit: false,
        cellEdit:true,
		rownumbers: false,
        cellurl:'#',
		cellsubmit:'clientArray',
        showCheckbox:true
    });
	$("#dataGrid").jqGrid("setFrozenColumns");
}
function checkBin(whNumber,binCode){
	var bin={};
	$.ajax({
		url:baseURL+"kn/storageMove/checkBin",
		dataType : "json",
		type : "post",
		data : {
			WH_NUMBER:whNumber,
			BIN_CODE:binCode
		},
		async: false,
		success: function (response) { 
		    if(response.list.length>0){
		    	bin=response.list[0];
		    	console.log("bin",bin);
		    }
		}
	})
	return bin;
}

function listselectCallback(index,rtn){
	rtn = rtn ||false;
	if(rtn){
		//操作成功，刷新表格
		vm.reload();
		try {
			parent.layer.close(index);
        } catch(e){
        	layer.close(index);
        }
	}
}

function refreshMore(){
  $.each($("#more_div").find("input[type='text']"),function(i,input){
	  $(input).val("")
  })
}

function addMore(){
	var tr=$("<tr />");
	  var td_1=$("<td style='height:30px;padding: 0 0' />").html("<input type='checkbox' /> ")
	  var td_2=$("<td style='height:30px;padding: 0 0' />").html("<input type='text' style='width:100%;height:30px;border:0'> ")
	  $(tr).append(td_1)
	  $(tr).append(td_2)
	  $("#moreGrid tbody").append(tr)
}

function delMore(){	
	$.each($("#more_div").find("input[type='checkbox']"),function(i,input){
		  if($(input).is(':checked') ){
			  $(input).parent("td").parent("tr").remove()
		  }
	  })
}
//表格下复选框全选、反选;checkall:true全选、false反选
function check_All_unAll(tableId, checkall) {
	if (checkall) {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked", true);
	} else {
		$(tableId + " tbody").find("input[type='checkbox']").prop("checked",false);
	}
}