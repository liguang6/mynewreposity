var mydata =[];
var lastrow,lastcell,lastcellname,last_scan_ele;
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks: '',
		werks_name: '',
		workshopList: [],
		receive_workshop_list:[],
		workgroupList:[],
		workshop: '',
		workshop_name: '',
		order_no: '',
		product_no: '',
		receive_workshop: '',
		receive_workshop_name:'',
		deliver_workgroup:'',
		receive_workgroup:'',
		handover_type:'',// 1:班组交接  2：车间交接
		show:false,
	},
	created:function(){
		this.werks=$("#werks").find("option").first().val();
	},
	methods: {
		//清空表格数据
		clearTable: function() {
			mydata = [];
			$("#dataGrid").jqGrid('clearGridData');
			vm.product_no='';
		},
		
		query: function(){
			if(vm.product_no==''){
           		js.showErrorMessage('请输入产品编号！');
        		return false;
        	}
         	if(vm.werks==''){
           		js.showErrorMessage('请选择工厂！');
        		return false;
        	}
         	if(vm.worshop==''){
           		js.showErrorMessage('请选择交付车间！');
        		return false;
        	}
         	
         	vm.queryMatInfo();
		},
		queryMatInfo: function(){
			js.loading("正在查询加载数据，请稍等...");
			$.ajax({
				url:baseUrl + "bjmes/productHandover/getProductInfo",
				data:{
					"werks": vm.werks,
					"workshop": vm.workshop,
					//"order_no": vm.order_no,
					"product_no": vm.product_no,					
				},
				type:"post",
				success:function(response){
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.data){
						if(response.data.length <= 0){
							js.showMessage("未找到符合条件的供货数据！");
							return false;
						}
						addMat(response.data);
					}
				},
			});
		},
		save: function(){
           	//初始化工厂、车间、线别名称值
        	vm.werks_name=$('#werks').find("option:selected").data("name");
        	vm.workshop_name=$('#workshop').find("option:selected").text();
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var rows=$("#dataGrid").jqGrid('getRowData');
			if(rows.length <= 0){
				js.closeLoading();
				$("#btnSave").attr("disabled", false);
				js.showErrorMessage("没有需要保存的数据！");
			   return false;
			}
			for(var i in rows){
				var data=rows[i];
				if(data.deliver_date=='' || data.rdeliver_user==''|| data.receive_user==''){
					js.showErrorMessage("第"+(Number(i)+1)+"行数量录入不完整！");
					return false;
				}
				delete data.EMPTY_COL;
			}
			
			js.loading("正在保存数据，请稍等...");
			$("#btnSave").html("保存中...");
			$("#btnSave").attr("disabled","disabled");
			$.ajax({
				type : "post",
				dataType: "json",
				async : true,
				url :baseUrl+ "bjmes/productHandover/save",
				data : {
					"werks" : vm.werks,
					"werks_name": vm.werks_name,
					"workshop" : vm.workshop,
					"workshop_name": vm.workshop_name,
					"handover_type" : vm.handover_type,
					"matInfoList": JSON.stringify(rows),
				},
				success:function(response){
					$("#btnSave").html("保存");	
					$("#btnSave").removeAttr("disabled");
					js.closeLoading();
					if(response.code==500){
						js.showErrorMessage(response.msg);
						return false;
					}
					if(response.code==0){
						vm.clearTable();
						js.showMessage("保存成功！");					        	
					}	
				}	
			});				
		},
	},
	watch: {
		werks: {
			handler:function(newVal,oldVal){
				$.ajax({
	        	    url:baseUrl + "masterdata/getUserWorkshopByWerks",
	        	    data:{
	        		    "WERKS":newVal,
	        		    "MENU_KEY":"BJMES_PRODUCT_HANDOVER",
	        	    },
	        	    success:function(resp){
	        		    vm.workshopList = resp.data;
	        		    if(resp.data.length>0){
	        			    vm.workshop=resp.data[0].CODE;
	        			    if(vm.handover_type=='1'){
	        				    getWorkgroupList();
	        			    }
	        		    } 
	        	    }
			    });
			    $.ajax({
					url:baseUrl + "masterdata/getWerksWorkshopList",
					data:{
						"CODE":newVal,
						"WERKS":newVal,
					},
					success:function(resp){
						vm.receive_workshop_list = resp.data;
						if(resp.data.length>0){
							vm.receive_workshop=resp.data[0].CODE;
							vm.receive_workshop_name=resp.data[0].NAME;
							console.log("handler werks",vm.receive_workshop_name);	
						} 
					}
				});
			}
		},
		workshop: {
			handler:function(newVal,oldVal){
				if(vm.handover_type=='1'){
   				   getWorkgroupList();
   			    }
		    }
		},
		receive_workshop: {
			handler:function(newVal,oldVal){
				if(vm.handover_type=='2'){
					$("#receive_workshop option[value='"+vm.receive_workshop+"']").attr("selected",true);
					vm.receive_workshop_name=$("#receive_workshop :selected").text();
					var rows=$("#dataGrid").jqGrid('getDataIDs');	
					console.log("handler receive_workshop",vm.receive_workshop_name);			
					$.each(rows,function(i,rowid){
						$("#dataGrid").jqGrid('setCell',rowid, 'receive_workshop_name', vm.receive_workshop_name);
						$("#dataGrid").jqGrid('setCell',rowid, 'receive_workshop', vm.receive_workshop);
					});
   			    }
		    }
		},
		receive_workgroup: {
			handler:function(newVal,oldVal){
				if(vm.handover_type=='1'){
					vm.receive_workgroup_name=$("#receive_workgroup :selected").text();
					var rows=$("#dataGrid").jqGrid('getDataIDs');				
					$.each(rows,function(i,rowid){
						$("#dataGrid").jqGrid('setCell',rowid, 'receive_workgroup_name', vm.receive_workgroup_name);
						$("#dataGrid").jqGrid('setCell',rowid, 'receive_workgroup', vm.receive_workgroup);
					});
				}
			}
		},
		deliver_workgroup: {
			handler:function(newVal,oldVal){
				if(vm.handover_type=='1'){
					vm.deliver_workgroup_name=$("#deliver_workgroup :selected").text();
					var rows=$("#dataGrid").jqGrid('getDataIDs');				
					$.each(rows,function(i,rowid){
						$("#dataGrid").jqGrid('setCell',rowid, 'deliver_workgroup_name', vm.deliver_workgroup_name);
						$("#dataGrid").jqGrid('setCell',rowid, 'deliver_workgroup', vm.deliver_workgroup);
					});
				}
			}
		},
	}
});
$(function () {
	
	var handover_type=getUrlKey("handover_type");
	vm.handover_type=handover_type;
	if(vm.handover_type=='1'){// 班组交接
		getWorkgroupList();
		vm.show=false;
	}
    if(vm.handover_type=='2'){// 车间交接
    	vm.show=true;
	}
    console.log(handover_type+"-"+vm.show);
    
	fun_ShowTable();
	
	/**
	 * 扫描产品编码二维码,解析二维码
	 */
	$(document).on("keydown","#product_no",function(e){
		if(e.keyCode == 13){
			if($(e.target).val() ==null || $(e.target).val().trim().length==0){
				return false;
			}
			var val_json = {};
			//扫描零部件条码
			if($(e.target).val()!=null && $(e.target).val().trim().length>0 && $(e.target).val().indexOf("{")>=0){
				val_json =  JSON.parse($(e.target).val());
				console.log("val_json",val_json);	
				vm.order_no=val_json.order_no;
				vm.zzj_no =val_json.zzj_no;
				vm.werks = val_json.werks;
				vm.workshop = val_json.workshop;
			}
			vm.queryMatInfo();	
		}
	});
});
function fun_ShowTable(){
	$("#dataGrid").dataGrid({
		datatype: "local",
		data: mydata,
	    cellEdit:true,
		cellurl:'#',
		cellsubmit:'clientArray',
		columnModel: [
			{ label: ' ', name: 'EMPTY_COL', index: '', align:"center",width: 30, sortable:false,formatter:function(val, obj, row, act){
   				var actions = [];
   				actions.push('<a href="#" onClick="delMat('+row.id+')" title="删除" data-confirm="确认要删除该行数据吗？"><i class="fa fa-times" style=\"color:red\"></i></a>');
   				return actions.join(",");
   			}},
   			{ label: '产品编号', name: 'product_no', align:'center', index: 'product_no', width: 120 }, 
   			{ label: '产品名称', name: 'product_name', align:'center', index: 'product_name', width: 140 }, 
			{ label: '订单编号',name:'order_no', align:'center', index: 'order_no', width: 100 }, 
   			{ label: '产品类别', name: 'product_type_name', align:'center', index: 'product_type_name', width: 120 }, 	
			{ label: '工厂', name: 'werks', align:'center', index: 'werks', width: 80 }, 		
   			{ label: '供应车间', name: 'workshop_name', align:'center', index: 'workshop_name', width: 120 }, 	
			{ label: '接收车间', name: 'receive_workshop_name', align:'center', index: 'receive_workshop_name', width: 120 }, 
			{ label: '供应班组', name: 'deliver_workgroup_name', align:'center', index: 'workgroup_name', width: 100 }, 	
			{ label: '接收班组', name: 'receive_workgroup_name', align:'center', index: 'receive_workgroup_name', width: 100 }, 
    		{ label: '<span style="color:red">*</span><span style="color:blue"><b>交付人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'deliver_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'deliver_user', align:"center",index: 'deliver_user', width: 90,editable:true,
    			editrules:{},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　     doScan('deliver_user');
            		　 });
            	}}
            },
            { label: '<span style="color:red">*</span><span style="color:blue"><b>接收人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'receive_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'receive_user', align:"center",index: 'receive_user', width: 90,editable:true,
    			editrules:{},
            	editoptions:{
            		dataInit:function(el){
            		　 $(el).click(function(){
            		　      doScan('receive_user');
            		　 });
            	}}
            },
            { label: '<span style="color:red">*</span><span style="color:blue"><b>交付日期</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'deliver_date\')" title="向下填充" style="color:green" aria-hidden="true"></i>', name: 'deliver_date', align:'center', index: 'deliver_date', width: 120,editable:true,
    			editrules:{required: true},
            	editoptions:{
            	   dataInit:function(el){
            		　 $(el).click(function(){
            		　     WdatePicker();
            	   });
            	}}
            }, 
    		{ label: 'receive_workshop', name: 'receive_workshop', hidden:true, },
			{ label: 'receive_workgroup',name:'receive_workgroup',hidden:true},
			{ label: 'deliver_workgroup', name: 'deliver_workgroup',hidden : true },
			{ label: 'product_type_code', name: 'product_type_code',hidden : true,}, 	
    	],
    	viewrecords: false,
        showRownum: true, 
		shrinkToFit: false,
        width:1900,
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,	
        gridComplete:function(){
        	if(vm.show){// 车间交接
	        	$("#dataGrid").setGridParam().hideCol("deliver_workgroup_name");
				$("#dataGrid").setGridParam().hideCol("receive_workgroup_name");
				$("#dataGrid").jqGrid('setLabel','receive_user', '<span style="color:blue"><b>接收人</b></span><i class="fa fa-clone fill-down" onclick="fillDown(\'receive_user\')" title="向下填充" style="color:green" aria-hidden="true"></i>', { 'text-align':'center'});
        	}else{// 班组交接
        		$("#dataGrid").jqGrid('setLabel', 'workshop_name', '车间', { 'text-align':'center'}); 				
        		$("#dataGrid").setGridParam().hideCol("receive_workshop_name");
        	}
        },
 	   	beforeEditCell:function(rowid, cellname, v, iRow, iCol){
			lastrow=iRow;
			lastcell=iCol;
			lastcellname=cellname;
		},
        afterSaveCell:function(rowid, cellname, value, iRow, iCol){	
				
		},
    });
}

function addMat(matList){
	var selectedId = $("#dataGrid").jqGrid("getGridParam", "selrow"); 
	var ids = $("#dataGrid").jqGrid('getDataIDs');
	if(ids.length==0){
		ids=[0]
	}
	var addedMatStr = "";
	var rows=$("#dataGrid").jqGrid('getRowData');
	$.each(rows,function(i,row){	
		addedMatStr += ";" + row.product_no +";";
	});	
    var rowid = Math.max.apply(null,ids);
	$.each(matList,function(i,row){	
		if(addedMatStr.indexOf(";" + row.product_no+";") == -1){
			var data = {};
			if(row.id==null || row.id==''){
				data.product_no = row.product_no;
				data.product_name = row.product_name;
				data.order_no = row.order_no;
				data.werks = row.werks;
			    data.workshop_name = row.workshop_name;
				data.product_type_code = row.product_type_code;
				data.product_type_name = row.product_type_name;
				data.deliver_date=formatDate(new Date());
				console.log("receive_workshop_name",vm.handover_type+"-"+vm.receive_workshop+"-"+vm.receive_workshop_name);
				if(vm.handover_type=='1'){
					data.receive_workgroup=vm.receive_workgroup;
					data.receive_workgroup_name=vm.receive_workshop_workgroup_name;
				}
				if(vm.handover_type=='2'){
					data.receive_workshop=vm.receive_workshop;
					data.receive_workshop_name=vm.receive_workshop_name;
				}
				$("#dataGrid").jqGrid("addRowData", rowid+1, data, "last");
			}else{
				js.showMessage(row.product_no+":已交接！");
			}
		}else{
			js.showMessage(row.product_no+"：数据重复！");
		}    	
	});	
}

function delMat(id){	
	var data=$("#dataGrid").jqGrid('getRowData',id);
	$("#dataGrid").delRowData(id);
}
function getWorkgroupList(){
	$.ajax({
  	  url:baseUrl + "masterdata/getWorkshopWorkgroupList",
  	  data:{
  		  "WERKS": vm.werks,
  		  "WORKSHOP": vm.workshop,
  		  "MENU_KEY":"BJMES_PRODUCT_HANDOVER",
		  "ALL_OPTION":'X'
	  },
  	  success:function(resp){
  		 if(resp.code==0){
  			vm.workgroupList = resp.data;
  			//vm.deliver_workgroup
  		 }
  	  }
    })
}
function fillDown(e){
	$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
	var ids=$("#dataGrid").getGridParam("selarrrow");
	var rowid=lastrow;
	var rows=$("#dataGrid").jqGrid('getRowData');
	var last_row=$("#dataGrid").jqGrid('getRowData',rowid);
	var cellvalue=last_row[lastcellname];	
	if(lastcellname==e){
		$.each(rows,function(i,row){
			if(Number(row.id)>=Number(rowid)&&cellvalue){
				$("#dataGrid").jqGrid('setCell',Number(row.id), e, cellvalue,'editable-cell');
			}
		})
	}
}
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

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}