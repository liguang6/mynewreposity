var exception_type_code_option = "";
var reason_type_name_option = "";
var vm = new Vue({
	el:'#rrapp',
	data:{
		user:{},
		mydata:[],
    workshoplist:[],
    newException:{},
		lastrow:'',
		lastcell:'',
		check:true
	},
	created:function(){
		this.getUser();
		getWorkShopList();
	},
	methods: {
		getUser: function(){
			$.getJSON("../../sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		query: function () {
      $("#btnSearchData").attr("disabled","disabled");
			$.ajax({
				url:baseURL+"bjmes/productManage/getExceptionList",
				dataType : "json",
				type : "post",
				data : {
          "product_no":$("#search_product_no").val(),
          "werks":$("#search_werks").val(),
          "order_no":$("#search_order").val(),
          "workshop":$("#search_workshop").val(),
          "product_type":$("#search_product_type").val(),
          "product_name":$("#search_product_name").val(),
          "process_name":$("#search_process_name").val(),
          "exception_type_code":$("#search_exception_type_code").val(),
          "WERKS":$("#search_werks").val(),
          "WERKS":$("#search_werks").val(),
          "WERKS":$("#search_werks").val(),
        },
        async: true,
				success: function (response) { 
          $("#dataGrid").jqGrid("clearGridData", true);
          vm.mydata.length=0;
					if(response.code == "0"){
						if(response.result.length === 0){
							vm.mydata = [];
							js.showMessage("没有查询到任何数据！");
						}else{
							vm.mydata = response.result;
						}
					}
					$("#dataGrid").jqGrid('GridUnload');
					fun_ShowTable();
					dataGridMorePaste();
					$("#btnSearchData").removeAttr("disabled");
        }
      })
    },
    saveException : function() {
      if($("#new_product_no").val() === ''){
				alert('请输入或扫描产品编号！')
				return false
      }
      if($("#new_order").val() === ''){
				alert('请输入或扫描编号后回车获取产品相关信息！')
				return false
      }
      $.ajax({
				url:baseURL+"bjmes/productManage/insertProductionException",
				dataType : "json",
				type : "post",
				data : {
          "werks":vm.newException.werks,
          "werks_name":vm.newException.werks_name,
          "workshop":vm.newException.workshop,
          "workshop_name":vm.newException.workshop_name,
          "product_no":vm.newException.product_no,
          "order_no":vm.newException.order_no,
          "product_code":vm.newException.product_code,
          "process_name":$("#new_process_name").val(),
          "exception_type_code":$("#new_exception_type_code").val(),
          "exception_type_name":$("#new_exception_type_code").find("option:selected").text(),
          "reason_type_code":$("#new_reason_type_code").val(),
          "reason_type_name":$("#new_reason_type_code").find("option:selected").text(),
          "start_time":$("#new_start_time").val(),
          "severity_level":$("#new_severity_level").val(),
          "duty_department_name":$("#new_duty_department_name").val(),
        },
        async: true,
				success: function (response) { 
          if(response.code === 0){
            alert("操作成功")
          }else{
            alert("操作失败")
          }
        }
      })

    },
    addException : function() {
      layer.open({
        type : 1,
        offset : '50px',
        skin : 'layui-layer-molv',
        title : "新增生产异常",
        area : [ '550px', '300px' ],
        shade : 0,
        shadeClose : true,
        content : jQuery("#addLayer"),
        btn : [ '确定' ,	'取消'],
        btn1 : function(index) {
          return vm.saveException()
        },
        btn2 : function(index) {
          console.log('btn2' , vm.newException)
          layer.close(index);
        }
      });
    },
    getProInfo: function() {
      if(13 === window.event.keyCode){
        $.ajax({
          url:baseURL+"bjmes/productManage/getProductNoinfo",
          dataType : "json",
          type : "post",
          data : {
            "product_no":$("#new_product_no").val(),
          },
          async: true,
          success: function (response) { 
            if(response.result.length === 0){
              $("#new_werks").val('')
              $("#new_workshop").val('')
              $("#new_order").val('')
              $("#new_product_name").val('')
              vm.newException = {}
              alert("没有此产品相关信息，请重新输入或扫描！")
            }else{
              $("#new_werks").val(response.result[0].werks_name)
              $("#new_workshop").val(response.result[0].workshop_name)
              $("#new_order").val(response.result[0].order_no)
              $("#new_product_name").val(response.result[0].product_name)
              vm.newException = response.result[0]
            }
          }
        })
      }
    },
    saveException : function() {
      $('#dataGrid').jqGrid("saveCell", vm.lastrow, vm.lastcell);
      var ids = $("#dataGrid").jqGrid("getGridParam", "selarrrow");	
      console.log('-->ids : ',ids);
      var arrList = new Array();
      if(ids.length == 0){
        alert("当前还没有勾选任何行项目数据！");
        return false;
      }
      for (var i = 0; i < ids.length; i++) {
        arrList.push(vm.mydata[parseInt(ids[i])-1])
      }
      $("#btnEdit").val("保存中");
      $("#btnEdit").attr("disabled","disabled");	
      $.ajax({
        url:baseURL+"bjmes/productManage/editProductionException",
        dataType : "json",
        type : "post",
        data : {
          "ARRLIST":JSON.stringify(arrList)
        },
        async: true,
        success: function (response) {
          if(response.code == "0"){
            js.showMessage("保存成功");
          }
          $("#btnEdit").val("保存");
          $("#btnEdit").removeAttr("disabled");
          vm.query();
        }
      })

    },
		refresh : function() {
			$('#dataGrid').trigger('reloadGrid');
		},
		getOrderNoFuzzy:function(){
			getBjOrderNoSelect("#search_order",null,null)	
		},
		onWerksChange:function(event) {
			getWorkShopList()
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onWorkshopChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		onOrderChange:function(event) {
			$("#dataGrid").jqGrid("clearGridData", true);
			vm.mydata.length=0;
			fun_ShowTable();
		},
		exceptionConfirm: function() {

		}
	},
	watch: {
		
	}
});
$(function () {
  fun_ShowTable();
  dataGridMorePaste();

  $("#search_exception_type_code option").each(function (){
		if($(this).val() != '')exception_type_code_option += $(this).val() + ":" + $(this).text() + ";"
  })
  $("#new_reason_type_code option").each(function (){
		if($(this).val() != '')reason_type_name_option += $(this).val() + ":" + $(this).text() + ";"
  })
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: vm.mydata,
		columnModel: [
      { label: '生产工厂', name: 'werks_name', align:'center', index: 'werks_name', width: 100 },
      { label: '生产车间', name: 'workshop_name', align:'center', index: 'workshop_name', width: 100 },
      { label: '订单编号', name: 'order_no',align:'center',  index: 'order_no', width: 80 }, 		
      { label: '产品类别', name: 'product_type_name', align:'center', index: 'product_type_name', width: 100 }, 		
      { label: '产品名称', name: 'product_name', align:'center', index: 'product_name', width: 100 },		
      { label: '产品编号', name: 'product_no', align:'center', index: 'product_no', width: 100 }, 	
      { label: '生产节点', name: 'process_name', align:'center', index: 'process_name', width: 100, editable:true }, 	
      { label: '异常类型', name: 'exception_type_name', align:'center', index: 'exception_type_name', width: 100, edittype:'select',editable:true }, 			
      { label: '异常原因', name: 'reason_type_name', align:'center',  index: 'reason_type_name', width: 120, edittype:'select',editable:true }, 				
      { label: '严重等级', name: 'severity_level', align:'center',  index: 'severity_level', width: 100, edittype:'select',editable:true ,
        formatter:function(val){	//'严重等级 0:不影响;1:普通;2:严重',
          if("0"==val){return "不影响";}
          if("1"==val){return "普通";}
          if("2"==val){return "严重";}
        }
      }, 	
      { label: '开始时间', name: 'start_time', align:'center', index: 'start_time', width: 140, editable:true },
      { label: '责任单位', name: 'duty_department_name', align:'center', index: 'duty_department_name', width: 100, editable:true },	
      { label: '处理方案', name: 'solution', align:'center',  index: 'solution', width: 100 }, 			
      { label: '受理者', name: 'processor', align:'center', index: 'processor', width: 100 },
      { label: '处理时间', name: 'process_date', align:'center', index: 'process_date', width: 100 },		
      { label: '状态', name: 'status', align:'center', index: 'status', width: 100 ,
        formatter:function(val){	//0:未处理；1：处理中 2 已处理
          if("0"==val){return "未处理";}
          if("1"==val){return "处理中";}
          if("2"==val){return "已处理";}
        }
      },	
      { label: '维护者', name: 'editor', align:'center', index: 'editor', width: 100 },
      { label: '维护时间', name: 'edit_date', align:'center', index: 'edit_date', width: 100 },
      { label: 'id', name: 'id', align:'center', index: 'id', width: 10 ,hidden:true}, 
    ],
		beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			vm.lastrow=iRow;
			vm.lastcell=iCol;
			vm.check = false;
		},
		afterEditCell:function(rowid, cellname, v, iRow, iCol){			
			vm.check = true;
		},
    formatCell:function(rowid, cellname, value, iRow, iCol){
      if(cellname=='severity_level'){
        $('#dataGrid').jqGrid('setColProp', 'severity_level', { 
          editoptions: {value:'0:不影响;1:普通;2:严重'}
        });
      }
      if(cellname=='exception_type_name'){
        if(exception_type_code_option.substring(exception_type_code_option.length-1,exception_type_code_option.length) === ';')exception_type_code_option = exception_type_code_option.substring(0,exception_type_code_option.length-1)
        $('#dataGrid').jqGrid('setColProp', 'exception_type_name', { 
          editoptions: {value:exception_type_code_option}
        });
      }
      if(cellname=='reason_type_name'){
        if(reason_type_name_option.substring(reason_type_name_option.length-1,reason_type_name_option.length) === ';')reason_type_name_option = reason_type_name_option.substring(0,reason_type_name_option.length-1)
        $('#dataGrid').jqGrid('setColProp', 'reason_type_name', { 
          editoptions: {value:reason_type_name_option}
        });
      }
    },
    onCellSelect:function(rowid,iCol,cellcontent,e){
      if(vm.mydata[rowid-1].status === '2'){
        $("#dataGrid").jqGrid('setCell', rowid, 'process_name', '','not-editable-cell');
        $("#dataGrid").jqGrid('setCell', rowid, 'exception_type_name', '','not-editable-cell');
        $("#dataGrid").jqGrid('setCell', rowid, 'reason_type_name', '','not-editable-cell');
        $("#dataGrid").jqGrid('setCell', rowid, 'severity_level', '','not-editable-cell');
        $("#dataGrid").jqGrid('setCell', rowid, 'start_time', '','not-editable-cell');
        $("#dataGrid").jqGrid('setCell', rowid, 'duty_department_name', '','not-editable-cell');
      }
    },
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'columnModel');
      //console.log(rowid + "|" + cm[i].name)
      if (cm[i].name == 'workshop_name') {      
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'workshop_name');
		},
		shrinkToFit: false,
    width:1900,
    viewrecords: false,
    showRownum: true, 
    cellEdit:true,
    cellurl:'#',
		cellsubmit:'clientArray',
    rowNum:25,
    rownumWidth: 25,
    showCheckbox:true
  });
}
function getWorkShopList(){
	$.ajax({
    url:baseUrl + "masterdata/getUserWorkshopByWerks",
    data:{
      "WERKS":$("#search_werks").val(),
      "MENU_KEY":"ZZJMES_PMD_MANAGE",
    },
    success:function(resp){
      vm.workshoplist = resp.data;
    }
  })
}

function dataGridMorePaste(){
	$('#dataGrid').bind('paste', function(e) {
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "")
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		
		var rows = tab.rows.length; // 总行数
		for (var i = 0; i < arr.length; i++) {
			if(startRow + i >= rows){
				$("#newOperation").click();
			}
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j < cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				//console.log('-->Row ' + parseInt(startRow) + '|Cell ' + (startCell + j) + "|" + vm.mydata.length)
				//if((startCell + j) === 3)vm.mydata[parseInt(startRow)-1].no=arr[i][j]
				
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid').jqGrid("saveCell", startRow + i, startCell + j);
				$(cell).html(arr[i][j]);
			}
		}
	});
}
