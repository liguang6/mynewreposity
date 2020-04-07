
var vm = new Vue({
	el:'#rrapp',
	data:{
		whNumber:"",
		warehourse:[],
        tempTypeList:[],
        tempSizeList:[]
	},
	created:function(){
		loadWhNumber();
        this.tempTypeList = getPrintTemplateBySysDict("PRINT_TEMPLATE_TPYE");
        this.tempSizeList = getPrintTemplateBySysDict("PRINT_TEMPLATE_SIZE");
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
		onPlantChange:function(){
			loadWhNumber();
		},
		add:function(){
        	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px;color:blue">创建xx</i>', 'wms/kn/xx.html', true, true);
		},
		query: function () {
			js.loading();
			vm.$nextTick(function(){//数据渲染后调用
				$("#dataGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
				js.closeLoading();
			})

		},
		print: function () {
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}

            layer.open({
                type: 1,
                title:'打印模板选择',
                closeBtn: 1,
                btn:['确认'],
                offset:'50px',
                resize:true,
                maxHeight:'300',
                area: ['350px','450px'],
                skin: 'layui-bg-blue', //没有背景色
                shadeClose: false,
                content: $('#select_template').html(),
                btn1:function(index){

                    $('#labelSize').val($("input[name='temp_size']:checked").val());
                    $('#labelType').val($("input[name='temp_type']:checked").val());
                    $('#labelSizeA4').val($("input[name='temp_size']:checked").val());
                    $('#labelTypeA4').val($("input[name='temp_type']:checked").val());
                    var saveData = [];
                    for (var i = 0; i < ids.length; i++) {
                        var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                        saveData[i] = data;
                    }
                    //模板的业务类型：原材料打印
                    //data.AUTH_TYPE = "raw-material";
                    if(saveData.length>0){
                        $("#labelList").val(JSON.stringify(saveData));
                        $("#labelListA4").val(JSON.stringify(saveData));
					}

                    if($('#labelSize').val().indexOf("A4") < 0){
                        $("#printButton").click();
					}else{
                        $("#printButtonA4").click();
					}

                }
            });


		},
		printA4: function () {
            //获取选中表格数据
            //var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
            var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
            if(ids.length == 0){
                alert("当前还没有勾选任何行项目数据！");
                return false;
            }
            var saveData = [];
            for (var i = 0; i < ids.length; i++) {
                var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
            }
            if(saveData.length>0)
                $("#labelListA4").val(JSON.stringify(saveData));
            $("#printButtonA4").click();
        },
		barcodeCf: function () {
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			if(ids.length >1){
				alert("请勾选一条数据！");
				return false;
			}

			var numNo = "";
			var trs=$("#dataGrid").children("tbody").children("tr");
			$.each(trs,function(index,tr){
				var cbx=$(tr).find("td").find("input").attr("type");
				if(cbx!=undefined){
					var c_checkbox=$(tr).find('input[type=checkbox]');
					var ischecked=$(c_checkbox).is(":checked");
					if(ischecked){
						var num = $(tr).find("td").eq(11).find("input").val();
						if(num != "")numNo += num + ",";
					}
				}
			});
			numNo = numNo.substring(0,numNo.length-1);

			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				//var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(ids !== null && ids!==undefined){
				var grData = $("#dataGrid").jqGrid("getRowData",ids)//获取选中的行数据
				var options = {
					type: 2,
					maxmin: true,
					shadeClose: true,
					title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>条码拆分',
					area: ["1000px", "500px"],
					content: baseURL + 'wms/kn/labelRecord_cf.html',
					btn: ['<i class="fa fa-check"></i> 确定'],
					success:function(layero, index){
						var win = layero.find('iframe')[0].contentWindow;
						win.vm.saveFlag = false;
						if(numNo==null || numNo=="" || numNo=='undefined'){
							alert("请输入拆分数量!");
							parent.layer.close(index);
						}
						if(numNo<=0){
							alert("拆分数量必须大于0!");
							parent.layer.close(index);
						}
						if(saveData.length>0)
							win.getInfo(JSON.stringify(saveData),numNo);

						win.init();
					},
					btn1:function(index,layero){

						var win = layero.find('iframe')[0].contentWindow;
						win.printpage();
						vm.reload();
						//parent.layer.close(index);
					}
				};
				options.btn.push('<i class="fa fa-close"></i> 关闭');
				options['btn'+options.btn.length] = function(index, layero){
					if(typeof listselectCallback == 'function'){
					}
				};
				js.layer.open(options);
			}else{
				layer.msg("请选择要编辑的行",{time:1000});
			}
		},
		
		transfer: function () {
			let deliveryNo = $("#deliveryNo").val();
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}
			var saveData = [];
			for (var i = 0; i < ids.length; i++) {
				var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
				saveData[i] = $("#dataGrid").jqGrid('getRowData',ids[i]);
			}
			if(saveData.length>0){
				if(deliveryNo.trim()){
					saveData.forEach(ele=>{
						ele.DELIVERY_NO = deliveryNo;
					})
					$.ajax({
						  url:baseUrl + "in/sto/queryBydeliveryNo",
						  type:"post",
						  //async:false,
						  contentType: 'application/json',
						  data:JSON.stringify(saveData),
						  success:function(resp){
							  if(resp.code === 0&&resp.data.length){
								  $("#transferlabel").val(JSON.stringify(resp.data));
								  $("#transfer").submit();
							  }else if(resp.code === 0&&!resp.data.length){
								  alert("配送单号没有该物料或配送单号不存在!");
							  }
							  else{
								 alert("操作失败,"+resp.msg);
							  }
						  }
					  });
				}else
					alert("请输入配送单号!");
			}
		},
		printAgain: function () {
			//获取选中表格数据
			//var ids = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
			var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");
			if(ids.length == 0){
				alert("当前还没有勾选任何行项目数据！");
				return false;
			}

            layer.open({
                type: 1,
                title:'打印模板选择',
                closeBtn: 1,
                btn:['确认'],
                offset:'50px',
                resize:true,
                maxHeight:'300',
                area: ['350px','450px'],
                skin: 'layui-bg-blue', //没有背景色
                shadeClose: false,
                content: $('#select_template').html(),
                btn1:function(index){

                    $('#labelSizeBD').val($("input[name='temp_size']:checked").val());
                    $('#labelTypeBD').val($("input[name='temp_type']:checked").val());
                    var saveData = [];
                    for (var i = 0; i < ids.length; i++) {
                        var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
                        saveData[i] = data;
                    }
                    //模板的业务类型：原材料打印
                    //data.AUTH_TYPE = "raw-material";
                    if(saveData.length>0){
                        $("#labelListBD").val(JSON.stringify(saveData));
					}
                    $("#printAgainButton").click();                

                }
            });
            //刷新表格
		}
	}
});




$(function () {
	
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		datatype : "local",
		columnModel: [
			{header:'工厂', name:'WERKS', width:80, align:"center"},
			{header:'条码号', name:'LABEL_NO', width:200, align:"center"},

			{header:'订单号', name:'PO_NO', width:150, align:"center"},
			{header:'行项目', name:'PO_ITEM_NO', width:90, align:"center"},

			{header:'料号', name:'MATNR', width:150, align:"center"},
			{header:'物料描述', name:'MAKTX', width:150, align:"center"},
			{header:'供应商代码', name:'LIFNR', width:120, align:"center"},
			{header:'数量', name:'BOX_QTY', width:60, align:"center"},
            {header:'箱号', name:'BOX_SN', width:60, align:"center"},
			{header:'拆分数量', name:'CF_BOX_QTY', width:90, align:"center",sortable:false,
				formatter:function(val){
					return "<input  id = 'test ' type='text' style='width:98%'/>";
				}

			},

			{header:'单位', name:'UNIT', width:60, align:"center"},
			{header:'生产日期', name:'PRODUCT_DATE', width:100, align:"center"},
            {header:'有效日期', name:'EFFECT_DATE', width:100, align:"center"},
			{header:'生产批次', name:'F_BATCH', width:100, align:"center"},

			{header:'BYD生产批次', name:'BATCH', width:110, align:"center"},
			{header:'备注', name:'REMARK', width:70, align:"center"},
			{ label: 'ID', name: 'ID', index: 'ID',hidden:true},

            {label: '收货单号', name: 'RECEIPT_NO', index: 'RECEIPT_NO', hidden: true},
            {label: '收货单行项目号', name: 'RECEIPT_ITEM_NO', index: 'RECEIPT_ITEM_NO', hidden: true},
            {label: '进仓单编号', name: 'INBOUND_NO', index: 'INBOUND_NO', hidden: true},
            {label: '进仓单行项目号', name: 'INBOUND_ITEM_NO', index: 'INBOUND_ITEM_NO', hidden: true},
            {label: 'WMS凭证号', name: 'WMS_NO', index: 'WMS_NO', hidden: true},
            {label: 'WMS凭证行项目', name: 'WMS_ITEM_NO', index: 'WMS_ITEM_NO', hidden: true},
            {label: '源标签号', name: 'F_LABEL_NO', index: 'F_LABEL_NO', hidden: true},
            {label: '标签状态', name: 'LABEL_STATUS', index: 'LABEL_STATUS', hidden: true},
            {label: '箱序号', name: 'BOX_SN', index: 'BOX_SN', hidden: true},
            {label: '满箱数量（定额数量）', name: 'FULL_BOX_QTY', index: 'FULL_BOX_QTY', hidden: true},
            {label: '尾箱标识', name: 'END_FLAG', index: 'END_FLAG', hidden: true},
            {label: '删除标识', name: 'DEL', index: 'DEL', hidden: true},
            {label: '仓库号（进仓）', name: 'WH_NUMBER', index: 'WH_NUMBER', hidden: true	},
            {label: '库位（进仓）', name: 'LGORT', index: 'LGORT', hidden: true},
            {label: '库位（进仓）', name: 'LGORT', index: 'LGORT', hidden: true},
            {label: '储位（进仓）', name: 'BIN_CODE', index: 'BIN_CODE', hidden: true},
            {label: '源单位', name: 'F_UNIT', index: 'F_UNIT', hidden: true},
            {label: '特殊库存类型', name: 'SOBKZ', index: 'SOBKZ', hidden: true},
            {label: '供应商描述', name: 'LIKTX', index: 'LIKTX', hidden: true},
            {label: '质检结果', name: 'QC_RESULT_CODE', index: 'QC_RESULT_CODE', hidden: true},
            {label: '有效期', name: 'F_UEFFECT_DATENIT', index: 'F_UEFFECT_DATENIT', hidden: true},


		],	
		rowNum: 15,
		showCheckbox:true,
	});
});

function loadWhNumber(){
	////初始化查询仓库
	var plantCode = $("#werks").val();

	//初始化仓库
	$.ajax({
		url:baseUrl + "common/getWhDataByWerks",
		data:{"WERKS":plantCode},
		success:function(resp){
			vm.warehourse = resp.data;
			if(resp.data.length>0){
				vm.whNumber=resp.data[0].WH_NUMBER//方便 v-mode取值
			}

		}
	});
}



//------删除操作---------
$("#deleteOperation").click(function(){
	var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
	if (gr != null  && gr!==undefined) {
		layer.confirm('确定删除选中的记录?', {icon: 3, title:'提示'}, function(index){
			var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
			$.ajax({
				type: "POST",
				url: baseURL + "kn/labelRecord/delById?id="+grData.ID,
				contentType: "application/json",
				success: function(r){
					if(r.code == 0){
						js.showMessage('删除成功!');
						vm.reload();
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
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '新增条码',
		area: ["1150px", "500px"],
		content: baseURL + "wms/kn/labelRecord_create.html",
        btn: ['<i class="fa fa-check"></i> 确定','<i class="fa fa-close"></i> 取消'],
        yes:function(index,layero){
        	var win = layero.find('iframe')[0].contentWindow;
            win.vm.saveBtn();
            var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
            $(btnSubmit).click();

        },
        no:function(index,layero){
            layer.close(index);
        }

	};

	js.layer.open(options);
});
//-------编辑操作--------
$("#editOperation").click(function(){
	var gr = $("#dataGrid").jqGrid('getGridParam', 'selrow');//获取选中的行号
	if(gr !== null && gr!==undefined){
		var grData = $("#dataGrid").jqGrid("getRowData",gr)//获取选中的行数据
		var options = {
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: '<i aria-hidden="true" class="fa fa-pencil-square-o">&nbsp;</i>修改条码',
			area: ["500px", "500px"],
			content: baseURL + 'wms/kn/labelRecord_edit.html',
			btn: ['<i class="fa fa-check"></i> 确定'],
			success:function(layero, index){
				var win = layero.find('iframe')[0].contentWindow;
				win.vm.saveFlag = false;
				win.vm.getInfo(grData.ID);
			},
			btn1:function(index,layero){
				var win = layero.find('iframe')[0].contentWindow;
				var btnSubmit= $("#btnSubmit", layero.find("iframe")[0].contentWindow.document);
				$(btnSubmit).click();
				if(typeof listselectCallback == 'function'){
					listselectCallback(index,win.vm.saveFlag);
				}
			}
		};
		options.btn.push('<i class="fa fa-close"></i> 关闭');
		options['btn'+options.btn.length] = function(index, layero){
			if(typeof listselectCallback == 'function'){
			}
		};
		js.layer.open(options);
	}else{
		layer.msg("请选择要编辑的行",{time:1000});
	}
});


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
function getPrintTemplateBySysDict(type){
    var list=[];
    $.ajax({
        url:baseURL+"config/printTemplate/getPrintTemplateBySysDict/"+type,
        type : "post",
        async: false,
        success:function(response){
            list=response.data;
        }
    })
    return list;
}