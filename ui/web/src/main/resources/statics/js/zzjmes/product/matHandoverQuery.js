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
					"MENU_KEY":'ZZJMES_HANDOVER_QUERY',
				},
				async: true,
				success: function (response) { 
					vm.workshop_list=response.data;
					vm.workshop=response.data[0]['code'];
					vm.getBatchSelects();
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
						"MENU_KEY":'ZZJMES_HANDOVER_QUERY',
					},
					async: true,
					success: function (response) { 
						vm.line_list=response.data;
						vm.line=response.data[0]['code'];
						vm.getBatchSelects();
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
					//{ label: '供应线别', name: 'line_name',align:'center',  index: 'line_name', width: 80 }, 		
		    		{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 
		    		{ label: '零部件名称', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 180 }, 	
		    		{ label: '装配位置', name: 'assembly_position', align:'center',  index: 'assembly_position', width: 180 },
		    		{ label: '使用工序', name: 'process', align:'center',  index: 'process', width: 80 },
		    		{ label: '工艺流程', name: 'process_flow', align:'center',  index: 'process_flow', width: 180 },
		    		{ label: '交接数量', name: 'deliver_qty', align:'deliver_qty',  index: 'zzj_name', width: 80 },
		    		{ label: '交接日期', name: 'receive_date', align:'center',  index: 'receive_date', width: 80 },
		    		{ label: '交付人', name: 'deliver_user', align:'center',  index: 'deliver_user', width: 70 },
		    		{ label: '接收人', name: 'receive_user', align:'center',  index: 'receive_user', width: 70 },
		    		{ label: '交付工序', name: 'deliver_process_name', align:'center',  index: 'deliver_process_name', width: 80 },
		    		{ label: '接收工序', name: 'receive_process_name', align:'center',  index: 'receive_process_name', width: 80 },
		    		{ label: '批次', name: 'zzj_plan_batch', align:'center', index: 'zzj_plan_batch', width: 60 }, 			
		    		{ label: '订单', name: 'order_desc', align:'center', index: 'order_desc', width: 160 }, 	
		    		{ label: '操作人', name: 'editor', align:'center', index: 'editor', width: 80 }, 			
		    		{ label: '操作时间', name: 'edit_date', index: 'edit_date', width: 150 },
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
//			if($("#zzj_plan_batch").val()==''){
//				js.showErrorMessage("请输入批次！");
//				return;
//			}
			jQuery('#dataGrid').GridUnload();
			$("#exportForm").attr("id","searchForm");
			$("#searchForm").attr("action",baseUrl + "zzjmes/matHandover/queryPage");
			$("#dataGrid").dataGrid({
				searchForm: $("#searchForm"),
				columnModel: [
					{ label: '供应工厂', name: 'werks',align:'center',  index: 'werks', width: 70 }, 		
					{ label: '供应车间', name: 'workshop_name',align:'center',  index: 'workshop_name', width: 70 }, 		
//					{ label: '供应线别', name: 'line_name',align:'center',  index: 'line_name', width: 80 }, 		
		    		{ label: '零部件号', name: 'zzj_no', align:'center', index: 'zzj_no', width: 120 }, 
		    		{ label: '零部件名称', name: 'zzj_name', align:'center',  index: 'zzj_name', width: 180 }, 	
		    		{ label: '装配位置', name: 'assembly_position', align:'center',  index: 'assembly_position', width: 180 },
		    		{ label: '使用工序', name: 'process', align:'center',  index: 'process', width: 80 },
		    		{ label: '工艺流程', name: 'process_flow', align:'center',  index: 'process_flow', width: 180 },
		    		{ label: '交接数量', name: 'deliver_qty', align:'deliver_qty',  index: 'zzj_name', width: 80 },
		    		{ label: '交接日期', name: 'receive_date', align:'center',  index: 'receive_date', width: 80 },
		    		{ label: '交付人', name: 'deliver_user', align:'center',  index: 'deliver_user', width: 70 },
		    		{ label: '接收人', name: 'receive_user', align:'center',  index: 'receive_user', width: 70 },
		    		{ label: '交接方式', name: 'handover_type_desc', align:'center',  index: 'handover_type_desc', width: 80 },
		    		{ label: '交付工序/班组', name: 'deliver', align:'center',  index: 'deliver', width: 100 },
		    		{ label: '接收工序/班组', name: 'receive', align:'center',  index: 'receive', width: 100 },
		    		{ label: '批次', name: 'zzj_plan_batch', align:'center', index: 'zzj_plan_batch', width: 60 }, 			
		    		{ label: '订单', name: 'order_desc', align:'center', index: 'order_desc', width: 160 }, 	
		    		{ label: '操作人', name: 'editor', align:'center', index: 'editor', width: 80 }, 			
		    		{ label: '操作时间', name: 'edit_date', index: 'edit_date', width: 150 },
		    	],
				shrinkToFit: false,
		        width:1900,
		    	viewrecords: false,
		        showRownum: true, 
		        rowNum:15,
		        rownumWidth: 25, 
		        rowList : [ 15,50,200 ],
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
			$("#exportForm").attr("action",baseUrl + "zzjmes/matHandover/expData");
			$("#exportForm").unbind("submit"); // 移除jqgrid的submit事件,否则会执行dataGrid的查询
			$("#exportForm").submit();	
	  }
	}
});

$(function () {
	var now = new Date(); 
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	$("#start_date").val(formatDate(new Date(startDate)));
	$("#end_date").val(formatDate(now));
	
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