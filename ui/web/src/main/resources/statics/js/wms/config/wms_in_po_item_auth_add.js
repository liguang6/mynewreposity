var mydata = [];
var lastrow,lastcell; 
$(function(){
	  
	  $("#dataGrid").dataGrid({
		  	datatype: "local",
			data: mydata,
	        colModel: [			
	            {label: '采购工厂', name: 'WERKS',index:"WERKS", width: 200,align:"center"},
	            {label: '采购订单', name: 'EBELN',index:"EBELN", width: 200,align:"center"},
				{label: '行号', name: 'EBELP',index:"EBELP", width: 200,align:"center"},
	            {label: '物料号', name: 'MATNR',index:"MATNR", width: 200,align:"center"},
				{label: '物料描述',name: 'TXZ01',index:"TXZ01",width: 200,align:"center"},
				{label: '供应商代码',name: 'LIFNR',index:"LIFNR",width: 200,align:"center"},
				{label: '最大可收货数量', name: 'MAX_MENGE',index:"MAX_MENGE", width: 170,align:"center" },
				{label: '<span style="color:blue"><b>授权工厂</b></span>', name: 'AUTHWERKS',index:"AUTHWERKS", width: 150,align:"center",editable:true },
				{label:'ID',width:75,name:'ID',index:"ID",align:"center",hidden:true}//隐藏此列
	        ],
	        beforeEditCell:function(rowid, cellname, v, iRow, iCol){
				lastrow=iRow;
				lastcell=iCol;
			},
			viewrecords: true,
	        //rowNum: 15,
			rownumWidth: 25, 
			cellEdit:true,
			cellurl:'#',
			cellsubmit:'clientArray',
	        
	        showCheckbox:true
	    });
	  
	
	  $("#btnPoAuthSearch").click(function () {
		  if($("#ebeln").val()==""){
			  js.showMessage("必须输入采购订单！");
			  return false;
		  }
		  
			$.ajax({
				url:baseURL+"config/poAuth/Polist",
				dataType : "json",
				type : "post",
				data : {
					"ebeln":$("#ebeln").val()
				},
				async: true,
				success: function (response) { 
					$("#dataGrid").jqGrid("clearGridData", true);
					mydata.length=0;
					if(response.code == "0"){
						mydata = response.retPolist;
						$('#dataGrid').jqGrid('setGridParam',{data: mydata}).trigger( 'reloadGrid' );
						//$("#dataGrid").jqGrid("setFrozenColumns");
						//$("#jqg_dataGrid_2").removeClass("noselect2");
					}else{
						alert(response.msg)
					}
				}
			});
		});
	  
	  /*$("#btnPoAuthSave").click(function () {
		  $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
		  
		  var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
			var arrList = new Array();
			for (var i = 0; i < ids.length; i++) {
				arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(ids.length == 0){
				alert("请至少选中一条数据！");
				return false;
			}
			
			var fgstr="";//是否有重复的行项目
			$.ajax({
				url:baseURL+"config/poAuth/queryByEbeln",
				dataType : "json",
				type : "post",
				data : {
					"ARRLIST":JSON.stringify(arrList)
				},
				async: false,
				success: function (response) { 
					if(response.code == "0"){
						var retQuery=response.retQuery;
						for(var i=0;i<retQuery.length;i++){
							fgstr=fgstr+"订单:"+retQuery[i].ebeln+" 行号:"+retQuery[i].ebelp
						}
						
					}
	            	
				}
			});
			
			if(fgstr!=""){
				//记录存在的显示框
				$("#outNo").html(fgstr+" 已经存在，是否覆盖？");
				layer.open({
	        		type : 1,
	        		offset : '50px',
	        		skin : 'layui-layer-molv',
	        		title : "提示",
	        		area : [ '500px', '200px' ],
	        		shade : 0,
	        		shadeClose : false,
	        		content : jQuery("#resultLayer"),
	        		btn : [ '是','否'],
	        		btn1 : function(index) {
	        			$.ajax({
	        				url:baseURL+"config/poAuth/PoAuthSave",
	        				dataType : "json",
	        				type : "post",
	        				data : {
	        					"ARRLIST":JSON.stringify(arrList),
	        					"FG":"Y"
	        				},
	        				async: true,
	        				success: function (response) { 
	        					js.showMessage("保存成功");
	        					close();
	        				}
	        			});
	        			layer.close(index);
	        		},
	        		btn2 : function(index) {
	        			$.ajax({
	        				url:baseURL+"config/poAuth/PoAuthSave",
	        				dataType : "json",
	        				type : "post",
	        				data : {
	        					"ARRLIST":JSON.stringify(arrList),
	        					"FG":"N"
	        				},
	        				async: true,
	        				success: function (response) { 
	        					js.showMessage("保存成功");
	        					close();
	        				}
	        			})
	        			layer.close(index);
	        		}
	        	});
			}else{
				$.ajax({
					url:baseURL+"config/poAuth/PoAuthSave",
					dataType : "json",
					type : "post",
					data : {
						"ARRLIST":JSON.stringify(arrList),
						"FG":""
					},
					async: true,
					success: function (response) { 
						js.showMessage("保存成功");
						close();
					}
				});
				
			}
			
	  });*/
	  
	
});

var vm = new Vue({
	el:'#rrapp',
	data:{},
	methods: {
		/*refresh:function(){
			$("#searchForm").submit();
        },
		reload: function (event) {
			$("#searchForm").submit();
		},*/
		getPlantNoFuzzy:function(){
			getPlantNoSelect("#werks",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,$("werks").val());
		},
		saveBtn:function(){
			  $('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			  
			  var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
				var arrList = new Array();
				for (var i = 0; i < ids.length; i++) {
					arrList[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
					if(arrList[i].AUTHWERKS == undefined || arrList[i].AUTHWERKS.trim() == ''){
						alert("第"+(i+1)+"行授权工厂不能为空！");
						return false;
					}
				}
				if(ids.length == 0){
					alert("请至少选中一条数据！");
					return false;
				}
				
				var fgstr="";//是否有重复的行项目
				$.ajax({
					url:baseURL+"config/poAuth/queryByEbeln",
					dataType : "json",
					type : "post",
					data : {
						"ARRLIST":JSON.stringify(arrList)
					},
					async: false,
					success: function (response) { 
						if(response.code == "0"){
							var retQuery=response.retQuery;
							for(var i=0;i<retQuery.length;i++){
								fgstr=fgstr+"订单:"+retQuery[i].ebeln+" 行号:"+retQuery[i].ebelp
							}
							
						}
		          	
					}
				});
				
				if(fgstr!=""){
					//记录存在的显示框
					$("#outNo").html(fgstr+" 已经存在，是否覆盖？");
					layer.open({
		      		type : 1,
		      		offset : '50px',
		      		skin : 'layui-layer-molv',
		      		title : "提示",
		      		area : [ '500px', '200px' ],
		      		shade : 0,
		      		shadeClose : false,
		      		content : jQuery("#resultLayer"),
		      		btn : [ '是','否'],
		      		btn1 : function(index) {
		      			$.ajax({
		      				url:baseURL+"config/poAuth/PoAuthSave",
		      				dataType : "json",
		      				type : "post",
		      				data : {
		      					"ARRLIST":JSON.stringify(arrList),
		      					"FG":"Y"
		      				},
		      				async: true,
		      				success: function (response) { 
		      					js.showMessage("保存成功");
		      					close();
		      				}
		      			});
		      			layer.close(index);
		      		},
		      		btn2 : function(index) {
		      			$.ajax({
		      				url:baseURL+"config/poAuth/PoAuthSave",
		      				dataType : "json",
		      				type : "post",
		      				data : {
		      					"ARRLIST":JSON.stringify(arrList),
		      					"FG":"N"
		      				},
		      				async: true,
		      				success: function (response) { 
		      					js.showMessage("保存成功");
		      					close();
		      				}
		      			})
		      			layer.close(index);
		      		}
		      	});
				}else{
					$.ajax({
						url:baseURL+"config/poAuth/PoAuthSave",
						dataType : "json",
						type : "post",
						data : {
							"ARRLIST":JSON.stringify(arrList),
							"FG":""
						},
						async: true,
						success: function (response) { 
							js.showMessage("保存成功");
							close();
						}
					});
					
				}
				


		}
	}
});



function getSelRow(gridSelector){
	var gr = $(gridSelector).jqGrid('getGridParam', 'selrow');//获取选中的行号
	return gr != null?$(gridSelector).jqGrid("getRowData",gr):null;
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