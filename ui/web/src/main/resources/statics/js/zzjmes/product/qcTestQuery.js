var mydata = [];
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		workshop_list :[],
		line:"",
		line_list :[],
		zzj_plan_batch :'',
		show_zzj_no:'',
		show_zzj_name:''
    },
	created:function(){	
		this.werks = $("#werks").find("option").first().val();
	},
	watch:{
		werks : {
			handler:function(newVal,oldVal){
			  $.ajax({
				  url:baseURL+"masterdata/getUserWorkshopByWerks",
				  dataType : "json",
				  type : "post",
				  data : {
					  "WERKS":newVal,
					  "MENU_KEY":'ZZJMES_MACHINE_PLAN_QUERY',
				  },
				  async: true,
				  success: function (response) { 
					  vm.workshop_list=response.data;
					  vm.workshop=vm.workshop||response.data[0]['code'];
					  if($("#search_order").val()!=''){
						  vm.getBatchSelects();
					  }	
					  if(vm.workshop){
						  $.ajax({
							  url:baseURL+"masterdata/getUserLine",
							  dataType : "json",
							  type : "post",
							  data : {
								  "WERKS":vm.werks,
								  "WORKSHOP": vm.workshop,
								  "MENU_KEY":'ZZJMES_QC_TEST_QUERY',
							  },
							  async: true,
							  success: function (response) { 
								  vm.line_list=response.data;
								  vm.line=vm.line||response.data[0]['code'];
								  if($("#search_order").val()!=''){
									  vm.getBatchSelects();
								  }
							  }
						  });							
					  }
				  }
			  });	
			}
		  },
		  workshop: {
			  handler:function(newVal,oldVal){
				  $.ajax({
					  url:baseURL+"masterdata/getUserLine",
					  dataType : "json",
					  type : "post",
					  data : {
						  "WERKS":vm.werks,
						  "WORKSHOP":newVal,
						  "MENU_KEY":'ZZJMES_QC_TEST_QUERY',
					  },
					  async: true,
					  success: function (response) { 
						  vm.line_list=response.data;
						  vm.line=response.data[0]['code'];
						  if($("#search_order").val()!=''){
							  vm.getBatchSelects();
						  }
					  }
				  });	
			  }
		  }
  
	},
	methods: {
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,vm.getBatchSelects);
		},
		getBatchSelects:function(selectval){
			if($("#search_order").val()!=''){
				$.ajax({
					type : "post",
					dataType : "json",
					async : false,
					url :baseUrl+ "/zzjmes/common/getPlanBatchList",
					data : {
						"werks" : this.werks,
						"workshop" : this.workshop,
						"line" : this.line,
						"order_no":$("#search_order").val()
					},
					success:function(response){
						getBatchSelects(response.data,selectval,"#zzj_plan_batch","全部");	
					}	
				});
			}
		},
		query:function(){
			$('#searchForm').submit();
		},
		enter:function(e){
			vm.query();
		},
		show:function(id,zzj_no,zzj_name){
			js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px"> 查看明细</i>',
				'zzjmes/product/testRecordEdit.html?id='+id+'&zzj_no='+zzj_no+'&zzj_name='+zzj_name+'&type=qc&operate=0', 
						true, true);
		},
        update:function(id,zzj_no,zzj_name){
        	js.addTabPage(null, '<i class="fa fa-search" style="font-size:12px"> 编辑明细</i>',
    			'zzjmes/product/testRecordEdit.html?id='+id+'&zzj_no='+zzj_no+'&zzj_name='+zzj_name+'&type=qc&operate=1', 
    					true, true);
		},
		
		del:function(){	
			var delData=[];
			var rowids=$('#dataGrid').jqGrid('getGridParam','selarrrow');
			if(rowids.length == 0){
				alert("当前还没有任何行项目数据！");
				return false;
			}
			var ids="";
			js.confirm('你确认要删除数据吗？',function(){
				for (var i = 0; i < rowids.length; i++) {	
					var data = $("#dataGrid").jqGrid("getRowData",rowids[i]);
					ids+=data["id"]+",";
				}
				js.loading("正在删除数据,请您耐心等待...");
				$("#btnDel").attr("disabled","disabled");
				$.ajax({
					url:baseUrl + "zzjmes/qmTestRecord/del",
					data:{
						ids:ids,
						result_type:'1'
					},
					type:"post",
					dataType:"json",
					success:function(resp){
						js.closeLoading();
						if(resp.code == 0){
							js.showMessage("删除成功！");
							vm.query();
						}else{
							alert("删除失败：" + resp.msg);
						} 
					},
					complete:function(XMLHttpRequest, textStatus){
						$("#btnDel").removeAttr("disabled");
					}
				});
			});
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
	var now = new Date(); 
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	$("#start_date").val(formatDate(new Date(startDate)));
	$("#end_date").val(formatDate(now));
	$("#dataGrid").dataGrid({
		searchForm: $("#searchForm"),
		colModel: [
			{ label: '操作', name: 'actions', width: 80 ,align:'center',formatter:function(val, obj, row, act){
    			var actions = [];
    			var link = "<a href='#' onClick='vm.show("+row.id+",\""+row.zzj_no+"\",\""+row.zzj_name+"\")' title='查看'><i class='fa fa-search'></i>&nbsp;</a>&nbsp;";
    			if(row.login_user==row.qc_editor){
        			link += "<a href='#' onClick='vm.update("+row.id+",\""+row.zzj_no+"\",\""+row.zzj_name+"\")' title='修改'><i class='fa fa-pencil'></i>&nbsp;</a>";
    			}
				actions.push(link);
    		    return actions.join('');
    		}},
    		{ label: '订单', name: 'order_desc',align:'center',  index: 'order_desc', width: 180 }, 		
    		{ label: '图号', name: 'material_no', align:'center', index: 'material_no', width: 120,formatter:function(val, obj, row, act){
		        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getPdf('"+vm.werks+"','"+val+"')>"+val+"</a>";
				},
				unformat:function(cellvalue, options, rowObject){
					return cellvalue;
			}}, 
   			{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 150 }, 
    		{ label: '零部件名称', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 180 }, 	
    		{ label: '批次', name: 'zzj_plan_batch', align:'center', index: 'zzj_plan_batch', width: 70 }, 			
    		{ label: '车付数', name: 'quantity', align:'center',  index: 'quantity', width: 70 }, 			
    		{ label: '判定', name: 'test_result_desc', align:'center', index: 'test_result_desc', width: 60 }, 	
    		{ label: '操作人', name: 'qc_editor', align:'center', index: 'qc_editor', width: 80 }, 			
    		{ label: '操作时间', name: 'qc_edit_date', index: 'qc_edit_date',  align:'center',width: 115 },
    		{ label: 'ID', name: 'id', index: 'id',hidden:true },
    		{ label: '当前登录用户', name: 'login_user', index: 'login_user',hidden:true },
    	],
    	dataGridLoadComplete: function(data) {
			js.closeLoading();
			$(".btn").attr("disabled", false);
			/*$(".jqgrow", $("#dataGrid")).each(function (index, row) {
		        var $row = $(row);
		        if(data.page.list[index].login_user!=data.page.list[index].qc_editor){
		        	console.log(data.page.list[index].qc_editor+":disabled");
		        	$row.find("input:checkbox").attr("disabled", "disabled");
		        }
			});*/
		},
		beforeSelectRow : function(rowid, e) { // 点击复选框才选中行
			var $myGrid = $(this), i = $.jgrid.getCellIndex($(e.target).closest('td')[0]), cm = $myGrid.jqGrid('getGridParam', 'colModel');
			if (cm[i].name == 'cb') {
				$('#dataGrid').jqGrid('setSelection', rowid);
			}
			return (cm[i].name == 'cb');
		},
		onSelectAll: function(aRowids,status) {
	        if (status) {
	            var cbs = $("tr.jqgrow > td > input.cbox:disabled", $("#dataGrid")[0]);
	            cbs.removeAttr("checked");
	            $("#dataGrid")[0].p.selarrrow = $("#dataGrid").find("tr.jqgrow:has(td > input.cbox:checked)")
	                .map(function() { return this.id; }) // convert to set of ids
					.get(); // convert to instance of Array
					
				for(var i in aRowids){
					console.log("ids[i]",aRowids[i]);
					var rowNo=$("#dataGrid").getCell(aRowids[i],'rn');
					var data = $("#dataGrid").jqGrid("getRowData",aRowids[i]);
					if(data.login_user!=data.product_editor){
						js.showErrorMessage("第"+rowNo+"行：非本人录入数据，不能删除！");
						$("#dataGrid").jqGrid("setSelection", aRowids[i],false);
						continue;
					}
				}
			}	
	    },
	    onSelectRow:function(rowid,status){
			var rowNo=$("#dataGrid").getCell(rowid,'rn');
	    	if(status == true){
	    		var cbs = $("tr.jqgrow > td > input.cbox:disabled", $("#dataGrid")[0]);
	            cbs.removeAttr("checked");
	            $("#dataGrid")[0].p.selarrrow = $("#dataGrid").find("tr.jqgrow:has(td > input.cbox:checked)")
	                .map(function() { return this.id; }) // convert to set of ids
	                .get();
			}	
			var data = $("#dataGrid").jqGrid("getRowData",rowid);
			if(data.login_user!=data.product_editor){
				js.showErrorMessage("第"+rowNo+"行：非本人录入数据，不能删除！");
				$("#dataGrid").jqGrid("setSelection", rowid,false);
				return false;
			}
	    },
	    viewrecords: true,
        shrinkToFit:false,
        showCheckbox:true,
        multiselect: true,
		rowNum: 15, 
    });
	/**
	 * 扫描零部件二维码,解析二维码
	 */
	$(document).on("keydown","#zzj_no",function(e){
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				$("#search_order").val(val_json.order_no);
				vm.zzj_plan_batch = val_json.zzj_plan_batch;
				$("#zzj_no").val(val_json.zzj_no);
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
				vm.line = val_json.line;
				console.log(vm.workshop+"-"+vm.line);
			}
			vm.query();
		}
	});
	$(document).on("click","#checkall",function(e){

		if ($(e.target).is(":checked")) {
			check_All_unAll("#moreGrid",true);	
		}
		if($(e.target).is(":checked")==false){
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
//调用摄像头扫二维码
function doScan(ele){
	var url = "../../doScan";
	last_scan_ele = ele;
	var iframe_id=self.frameElement.getAttribute('id');
	$(window.parent.document).find("#activeIframe").val(iframe_id)
	var a = document.createElement("a");
	a.target="_parent";
	a.href = url;
	a.click();
}

function funFromjs(result){ 
	var e = $.Event('keydown');
    e.keyCode = 13; 	
	$("#"+last_scan_ele).val(result);
    $("#"+last_scan_ele).trigger(e);
}