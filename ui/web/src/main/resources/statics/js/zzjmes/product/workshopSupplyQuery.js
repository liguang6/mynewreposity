var mydata = [];
var moredata = [
    { id: "1",more_value: ""},{ id: "2",more_value: ""},{ id: "3",more_value: ""},{ id: "4",more_value: ""},{ id: "5",more_value: ""}];
var vm = new Vue({
	el:'#rrapp',
	data:{
		werks:"",
		workshop:"",
		use_workshop:"",
		workshop_list :[],
		process:"",
		process_list:[],
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
					"MENU_KEY":'ZZJMES_WORKSHOP_SUPPLY_QUERY',
				},
				async: true,
				success: function (response) { 
					if(response.data!=null && response.data.length>0){
						vm.workshop_list=response.data;
						vm.workshop=response.data[0]['code'];
						vm.use_workshop=response.data[0]['NAME'];
						getProcessList();
					}
					
				}
			});	
		  }
		},
		use_workshop:{
			handler:function(newVal,oldVal){
				getProcessList();
			}
		},
	},
	methods: {
		getOrderNoFuzzy:function(){
			getZZJOrderNoSelect("#search_order",null,"#werks");
		},
		getAssemblyPositionNoFuzzy: function(){
			if($("#search_order").val() === ''){
				return false;
			}
		    getAssemblyPositionNoSelect('#assembly_position',null,"#search_order");// fn_cb_getAssemblyPositionInfo
		},
		enter:function(e){
			vm.query();
		},
		
		showTable:function(mydata){
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [	   
					{ label: '供应工厂', name: 'werks',align:'center',  index: 'werks', width: 80 }, 		
					{ label: '供应车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 80 }, 		
					{ label: '使用车间', name: 'line_name',align:'center',  index: 'line_name', width: 80 }, 		
		    		{ label: '使用工序', name: 'process', align:'center',  index: 'process', width: 80 },
					{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 
		    		{ label: '零部件名称', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 180 }, 	
		    		{ label: '单车用量', name: 'quantity', align:'center',  index: 'quantity', width: 180 },
		    		{ label: '需求数量', name: 'demand_quantity', align:'center',  index: 'demand_quantity', demand_qty: 80 },
		    		{ label: '已生产数量', name: 'prod_qty', align:'center',  index: 'prod_qty', width: 80 },
		    		{ label: '已供货数量', name: 'deliver_qty', align:'center',  index: 'zzj_name', deliver_qty: 80 },
		    		{ label: '待供货数量', name: 'undeliver_qty', align:'center',  index: 'zzj_name', undeliver_qty: 80 },
		    		{ label: '状态', name: 'receive_date', align:'center',  index: 'receive_date', width: 80 },
		        ],
		        loadComplete:function(){
					js.closeLoading();
					$(".btn").attr("disabled", false);
				},
				viewrecords: true,
		        shrinkToFit:false,
				rowNum: 15,  
				rowList : [30,50,100],
				cellsubmit:'clientArray',
		    });	
		},
		query:function(){
			if($("#search_order").val()==''){
				js.showErrorMessage("请输入订单号！");
				return;
			}
			if($("#use_workshop").val()==''){
				js.showErrorMessage("请选择使用车间！");
				return;
			}
			jQuery('#dataGrid').GridUnload();
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "zzjmes/matHandover/querySupplyPage");
			$("#dataGrid").dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
					{ label: '供应工厂', name: 'werks_name',align:'center',  index: 'werks_name', width: 70 }, 		
					{ label: '供应车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 65 }, 		
					{ label: '使用车间', name: 'use_workshop',align:'center',  index: 'use_workshop', width: 65 }, 		
		    		{ label: '使用工序', name: 'process', align:'center',  index: 'process', width: 65 },
		    		{ label: '装配位置', name: 'assembly_position', align:'center',  index: 'assembly_position', width: 120 },
		    		{ label: '图号', name: 'material_no', align:'center', index: 'material_no', width: 120,
		   				formatter:function(val, obj, row, act){
		   		        	return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getPdf('"+row.werks+"','"+val+"')>"+val+"</a>";
		   				},
		   				unformat:function(cellvalue, options, rowObject){
		   					return cellvalue;
		   				}
		   			},
		    		{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 
		    		{ label: '零部件名称', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 160 }, 	
		    		{ label: '单车用量', name: 'quantity', align:'center',  index: 'quantity', width: 65 },
		    		{ label: '需求数量', name: 'demand_quantity', align:'center',  index: 'demand_quantity', width: 70 },
		    		{ label: '已生产数量', name: 'prod_quantity', align:'center',  index: 'prod_quantity', width: 80 },
		    		{ label: '已供货数量', name: 'deliver_qty', align:'center',  index: 'deliver_qty', width: 75, formatter:function(val, obj, row, act){
	   		        	if(Number(val)>0)
		    			  return "<a href='javascript:void(0);' style='text-align:center;width:100%;' onclick=getDetail('"+row.zzj_pmd_items_id+"')>"+val+"</a>";
	   		        	else
	   		        		return '';
	   		        }, },
		    		{ label: '待供货数量', name: 'undeliver_qty', align:'center',  index: 'undeliver_qty', width: 75,formatter:function(val, obj, row, act){
                        return Number(row.demand_quantity)-Number(row.deliver_qty);
		    		},},
		    		{ label: '状态', name: 'status', align:'center',  index: 'status', width: 70, formatter:function(val, obj, row, act){
                         var status_desc='';
                         if(row.deliver_qty=='' || Number(row.deliver_qty)==0){
                        	 status_desc='未供货';
                         }
                         if(row.deliver_qty!='' && Number(row.deliver_qty)>0){
                        	 if(Number(row.demand_quantity)-Number(row.deliver_qty)>0)
                        	    status_desc='供货中';
                        	 if(Number(row.demand_quantity)-Number(row.deliver_qty)==0)
                         	    status_desc='已完成';
                         }
                         return status_desc;
		    		},},
		    		{ label: '订单', name: 'order_desc',align:'center',  index: 'order_desc', width: 160 }, 
		    		{ label: 'zzj_pmd_items_id', name: 'zzj_pmd_items_id', index: 'zzj_pmd_items_id',hidden : true },
		    	],
				shrinkToFit: false,
		        width:1900,
		    	viewrecords: false,
		        showRownum: true, 
		        rowNum:15,
		        rownumWidth: 45, 
		        rowList : [ 15,50,100 ],
		    });
		},
		detailTable:function(mydata){
			jQuery('#detailGrid').GridUnload(); 
			$("#detailGrid").dataGrid({
				datatype: "local",
				data: mydata,
				colModel: [
		            {label: '零部件号', name: 'zzj_no',index:"zzj_no", width: "120",align:"center",},
		            {label: '零部件名称', name: 'zzj_name',index:"zzj_name", width: "130",align:"center",sortable:false,},
		            {label: '供货数量', name: 'deliver_qty',index:"deliver_qty", width: "70",align:"center",sortable:false,},
		            {label: '交付人', name: 'deliver_user',index:"deliver_user", width: "70",align:"center",sortable:false,editable:true,},
		            {label: '接收人', name: 'receive_user',index:"receive_user", width: "80",align:"center",sortable:false,},
		            {label: '交付日期', name: 'deliver_date',index:"deliver_date", width: "80",align:"center",sortable:false,},
				],	
				viewrecords: true,
				showRownum:false,
				cellEdit:true,
				cellurl:'#',
				cellsubmit:'clientArray',
		        shrinkToFit:false,
	            height:"100%",
				loadComplete : function(data) {
					js.closeLoading();
					$(".btn").attr("disabled", false);
					if (data.code == 500) {
						js.showErrorMessage(data.msg, 1000 * 10);
						return false;
					}
				},
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
	  exp:function(){
		    $("#searchForm").attr("id","exportForm");
			$("#exportForm").attr("action",baseUrl + "zzjmes/matHandover/expSupplyData");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
	  }
	}
});

$(function () {
	
	vm.showTable();
	
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
function getDetail(zzj_pmd_items_id){
	$.ajax({
		type : "post",
		dataType : "json",
		async : false,
		url :baseUrl+ "/zzjmes/matHandover/getSupplyDetailList",
		data : {
			"zzj_pmd_items_id":zzj_pmd_items_id,
		},
		success:function(response){
            if(response.code=='0'){
            	vm.detailTable(response.data);
            	layer.open({
            		type : 1,
            		offset : '50px',
            		skin : 'layui-layer-molv',
            		title : "供货明细",
            		area : [ '580px', '320px' ],
            		shade : 0,
            		shadeClose : false,
            		content : jQuery("#detailDiv"),
            		btn : [ '关闭'],
            		btn1 : function(index) {
            			layer.close(index);
            		}
            	});
            }
		}	
	});
}
function getProcessList(){
	$.ajax({
		url : baseUrl + "masterdata/getWorkshopProcessList",
		data : {
			"WERKS" : vm.werks,
			"WORKSHOP" : $("#use_workshop :selected").attr("code"),
			"MENU_KEY" : "ZZJMES_WORKSHOPSUPPLY",
		},
		success : function(resp) {
			vm.process_list = resp.data;
		}
	})
}
function fn_cb_getOrderInfo(order){
	vm.order_no = order.order_no;
}
//function fn_cb_getAssemblyPositionInfo(assemblyPosition){
//	vm.assembly_position = assemblyPosition.assembly_position;
//}
function doScan(ele){
	var url="doScan";
	last_scan_ele=ele;
	var a=document.createElement("a");
	a.href=url;
	a.click();
}

function funFromjs(result){  
	var e = $.Event('keydown');
    e.keyCode = 13; 	
    $("#"+last_scan_ele).val(result).trigger(e);
}