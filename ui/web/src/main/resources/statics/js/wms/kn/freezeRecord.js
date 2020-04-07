var lastrow,lastcell;
var reasonSelectArr = "";
var mydata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList:false,
		WERKS:"",
		whNumber:"",
		warehourse:[],
		reason:""
	},
	created:function(){	
		this.WERKS = $("#werks").find("option").first().val();
		// 加载库位
		loadLgort();
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
		},
		reason:{
			handler: function(newVal,oldVal){
				vm.reasonChange();
			}
		}
	},
	methods: {
		/*********** type【00:冻结；01：解冻】 ************/ 		
		 save: function (type) {
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
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				if(data["QUANTITY"]==null || data["QUANTITY"]==''){
					js.showErrorMessage('第'+(i+1)+'行操作数量不能为空');
					save_flag=false;
					return false;
				}
				// 冻结时， 冻结原因必填
				if(type=='00'){
					if(data["REASON"]==null || data["REASON"]==''){
						js.showErrorMessage('第'+(i+1)+'行冻结原因不能为空！');
						save_flag=false;
						return false;
					}
				}
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
				//delete saveData[i]["actions"]; 
			}
			var url = baseURL+"kn/freezerecord/save";
			if(save_flag)
			$.ajax({
				type: "POST",
			    url: url,
			    dataType : "json",
				data : {
					saveData:JSON.stringify(saveData),
					freezeType:type,
					werks:$("#werks").val(),
					whNumber:$("#whNumber").val()
				},
			    success: function(r){
			    	if(r.code === 0){
			        	vm.saveFlag= true;
			        	js.showMessage(r.msg+"：保存成功！");
			        	$("#dataGrid").dataGrid("clearGridData");
					}else{
						js.showMessage(r.msg);
					}
				}
			});
		},
		query: function () {
			$("#searchForm").submit();
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("#werks").val());
		},
        onPlantChange:function(event){
	        loadLgort();
		},
		getMaterialNoFuzzy:function(){
			/*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
        },
        // 根据查询条件"状态"的改变，显示或隐藏对应的操作按钮
        statusChange:function(){
            var status=$("#status").val();
            if(status=='00'){
                vm.showList=false;
                $("#dataGrid").jqGrid('setLabel','QTY','可冻结数量');
                $("#dataGrid").jqGrid('setLabel','QUANTITY','<span style="color:blue">冻结数量</span>');
                
                $("#dataGrid").jqGrid('setLabel','REASON','冻结原因');
            }
            if(status=='01'){
                vm.showList=true;
                $("#dataGrid").jqGrid('setLabel','QTY','可解冻数量');
                $("#dataGrid").jqGrid('setLabel','QUANTITY','<span style="color:blue">解冻数量</span>');
                
                $("#dataGrid").jqGrid('setLabel','REASON','解冻原因');
            }
            vm.query();
        },
        // 批量填充冻结原因
        reasonChange:function(){
            var reasonCode=$("#reason").val();
            var reason=$("#reason").val()!='' ? $("#reason :selected").text() : '';
            var ids = $("#dataGrid").jqGrid('getDataIDs');
            for (var i = 0; i < ids.length; i++) {
                var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                $("#dataGrid").jqGrid('setCell', ids[i], "REASON", reason,'editable-cell');
                $("#dataGrid").jqGrid('setCell', ids[i], "REASON_CODE", reasonCode,'not-editable-cell');
            }
        },
        more: function(e){
			$("#moreGrid").html("")
			$.each(mydata,function(i,d){
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
	$("#reason option").each(function(){
		var val = $(this).val();
		var text = $(this).text();
		if(val!=''){
			reasonSelectArr+=val+":"+text+";";
		}
	});
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{header:'料号', name:'MATNR', width:110, align:"center"},
			{header:'物料描述', name:'MAKTX', width:180, align:"center"},
			{header:'工厂', name:'WERKS', width:60, align:"center"},
			{header:'仓库号', name:'WH_NUMBER', width:70, align:"center"},
			{header:'库位', name:'LGORT', width:60, align:"center"},
			{header:'储位', name:'BIN_CODE', width:70, align:"center"},
			{header:'批次', name:'BATCH', width:120, align:"center"},
			{header:'库存类型', name:'SOBKZ', width:75, align:"center"},
			{header:'可冻结数量', name:'QTY', width:90, align:"center"},
            {header:'<span style="color:blue">冻结数量</span>', name:'QUANTITY', width:80, align:"center",editable:true},
            {header:'<span style="color:blue">目标储位</span>', name:'TARGET_BIN_CODE', width:80, align:"center",editable:true,
//            	formatter:function(val, obj, row, act){
//    				return row.BIN_CODE;
//    		      }
                },
			{header:'单位', name:'MEINS', width:50, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:90, align:"center"},
			{header:'供应商名称', name:'LIKTX', width:140, align:"center"},
			{header:'冻结原因', name:'REASON', width:100, align:"center",editable:true,edittype:"select",
               editoptions: {value:reasonSelectArr.substring(0,reasonSelectArr.length-1),dataEvents:[
					{type:"blur",fn:function(e){
						var val = this.value;
						var rowId= $("#dataGrid").jqGrid('getGridParam','selrow');
						$("#dataGrid").jqGrid('setCell',rowId,"REASON_CODE",val);  
					}},
				]},
			},
			{header:'冻结原因代码',name:'REASON_CODE',hidden:true,formatter:"String"},
			{header:'储位名称',name:'BIN_CODE',hidden:true},
            {header:'ID',name:'ID',hidden:true,formatter:"integer"},
            {header:'是否启用条码管理',name:'BARCODE_FLAG',hidden:true},
		],
		showCheckbox:true,
		viewrecords: true,
		rowNum: 15,  
		rowList : [  15,30 ,50],
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        gridComplete: function() { 
    		 var rowIds = $("#dataGrid").jqGrid("getDataIDs"); 
    		 for(var i = 0, l = rowIds.length; i<l; i++) { 
    			 var rowData = $("#dataGrid").jqGrid("getRowData", rowIds[i]); 
    			 // 启用标签管理  不允许修改冻结数量 目标储位
    			 if(rowData && rowData.BARCODE_FLAG == 'X') { 
    				 //$("#" + rowIds[i]).css("background", "gray");
    				 $("#dataGrid").jqGrid('setCell', rowIds[i],'QUANTITY', rowData.QTY,'not-editable-cell');
    				 $("#dataGrid").jqGrid('setCell', rowIds[i],'TARGET_BIN_CODE', rowData.BIN_CODE,'not-editable-cell');
    			 }else{
    				 $("#dataGrid").jqGrid('setCell', rowIds[i],'TARGET_BIN_CODE', rowData.BIN_CODE,'editable-cell');
    			 } 
    		}       	
        },
        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
		},
        beforeSelectRow:function(rowid,e){
            var data=$("#dataGrid").jqGrid('getRowData',rowid);
            // 仓库启用条码管理  冻结、解冻数量不可编辑
//            if(data.BARCODE_FLAG=='X'){
//                 $("#dataGrid").jqGrid('setCell', rowid,'QUANTITY', data.QTY,'not-editable-cell');
//                 $("#dataGrid").jqGrid('setCell', rowid,'TARGET_BIN_CODE', data.BIN_CODE,'not-editable-cell');
//            }
            var $myGrid = $(this),  
            i = $.jgrid.getCellIndex($(e.target).closest('td')[0]);
            cm = $myGrid.jqGrid('getGridParam', 'colModel'); 
            if(cm[i].name == 'cb'){
                $('#dataGrid').jqGrid('setSelection',rowid);
            }   		
            return (cm[i].name == 'cb'); 
        },
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){
        	//var rec =  $("#dataGrid").jqGrid('getRowData', rowid);
        	var row=$('#dataGrid').jqGrid('getRowData',rowid);
        	if(cellname == 'QUANTITY'){
        		//校验数量是否超出
        		if(parseInt(value)>parseInt(row.QTY)){
        			js.showErrorMessage('本次输入的数量不能超过:'+row.QTY);
        			$("#dataGrid").jqGrid('setCell', rowid, cellname, ' ','editable-cell');
        			return ;
        		}else{
        			$("#dataGrid").jqGrid('setCell', rowid, cellname, value,'editable-cell');
        		}
        	}
        	// 校验目标储位正确性
			if(cellname=='TARGET_BIN_CODE'){
				var bin=checkBin(row.WH_NUMBER,value);
				if(bin.binCode.trim()=='' || bin.binCode==undefined){
					js.showErrorMessage('储位【'+value+'】未维护');
					$("#dataGrid").jqGrid('setCell', rowid, cellname, ' ','editable-cell');
					return ;
				}else{
					$("#dataGrid").jqGrid('setCell', rowid, cellname, value,'editable-cell');
				}
			}
        }
	});
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
});	

function loadLgort(){
	//查询库位
  	$.ajax({
      	url:baseUrl + "common/getLoList",
      	data:{
      		"DEL":'0',
      		"WERKS":$("#werks").val(),
//      		"SOBKZ":"Z",
      		},
      	success:function(resp){
      		var lgortlist = resp.data;
      	     // 加载库位
			$("#lgort").empty();
			$("#lgort").append("<option value=''>全部</option>");
    		for (var i = 0; i < lgortlist.length; i++) {  
    			var lgort=lgortlist[i].LGORT;
    			var lgortName=lgortlist[i].LGORT_NAME;
                $("#lgort").append("<option value='" +lgort+ "'>" + lgort + "</option>");  
            }  
      	}
     });
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