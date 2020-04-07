var listdata = [];
$(function () {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	var endDate=new Date(now.getTime()+7*24*3600*1000);
	startDate.setHours(0);
	startDate.setMinutes(0);
	startDate.setSeconds(0);
	endDate.setHours(0);
	endDate.setMinutes(0);
	endDate.setSeconds(0);
	$("#createDateStart").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#createDateEnd").val(now.Format("yyyy-MM-dd hh:mm:ss"));
	$("#DATE_START").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#DATE_END").val(endDate.Format("yyyy-MM-dd hh:mm:ss"));

	initGridTable();

});

function initGridTable(){
	$("#dataGrid").dataGrid({
		searchForm:$("#searchForm"),
		datatype: "json",
		colModel:[
					{ label: '日志ID', name: 'PK_LOG', index: 'PK_LOG', width: 60 },
					{ label: '接口编号', name: 'FLOW_NO', index: 'FLOW_NO', width: 160 },
					{ label: '接口源', name: 'FROM_NO', index: 'FROM_NO', width: 120 },
					{ label: '接口目标', name: 'TO_NO', index: 'TO_NO', width: 120 },
					{ label: '接口执行状态', name: 'VSTATUS', index: 'VSTATUS', width: 90,
						formatter:function(val){
		            		if("100"==val)return "成功";
		            		if("101"==val)return "失败";
		            	}
					},
					{ label: '接口执行时间', name: 'CTDT', index: 'CTDT', width: 200 }
					// { label: '操作', name: 'ACTIONS', index: 'ACTIONS', width: 80,align:'center' ,formatter: function(val, obj, row, act){
					// 	var actions = [];
					// 		actions.push('<a href="'+baseURL+'in/webservicelog/retrigger?pkLog='+row.PK_LOG+'"  title="重触发" ><i class="fa fa-chevron-circle-right"></i></a>&nbsp;');
					// 		// actions.push('<a href="#" title="重触发" onclick=reTriggerClick("'+row.PK_LOG+'")><i class="fa fa-chevron-circle-right"></i></a>');
					// 	return actions.join('  ');
				    //   },unformat:function(cellvalue, options, rowObject){
					// 		return "";
					// 	}
				    // }

		          ],
		rowNum: 15,
	    rownumWidth: 25,
	    multiselect:true,
	    beforeSelectRow: function (rowid, e) {  //点击复选框才选中行
			   var $myGrid = $(this),
			   i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
			   cm = $myGrid.jqGrid('getGridParam', 'colModel');
			   /*if(cm[i].name == 'cb'){
				   console.info(">>>");
				   $('#dataGrid').jqGrid('setSelection',rowid);
			   }*/

			   return (cm[i].name == 'cb');
			}
	});
}

var vm = new Vue({
	el:"#vue-app",
	data:{
		whNumber:"",
		warehourse:[]
	},
	created:function(){


	},
	methods: {

		retriggerLogsClick:function(){
			var ids = $("#dataGrid").jqGrid('getGridParam','selarrrow');
			$(".btn").attr("disabled", true);
			// if(ids.length==0){
			// 	js.alert("请选择要重新触发接口的数据!");
			// 	$(".btn").attr("disabled", false);
			// 	return;
			// }
			if(ids.length != 1){
				js.alert("请选择一条重新触发接口的数据!");
				$(".btn").attr("disabled", false);
				return;
			}

			var rows = [];
			for(var id in ids){
				var row = $("#dataGrid").jqGrid('getRowData',ids[id]);

				rows[id] = row;

				if("失败"!=row.VSTATUS){
					js.alert("只有接口执行状态为 “失败” 的才能进行重新触发!");
					$(".btn").attr("disabled", false);
					return;
				}

				if("sendQmsData"!=row.FLOW_NO){
					js.alert("请选择送检单接口进行重触发!");
					$(".btn").attr("disabled", false);
					return;
				}
			}

			layer.confirm('确定触发选中的行?', {icon: 3, title:'提示'}, function(index){
				$.ajax({
					url:baseUrl + "in/webservicelog/retriggerLogs",
					dataType : "json",
					type : "post",
					data : {
						"ARRLIST":JSON.stringify(rows)
					},
					async: false,
					success:function(resp){
						if(resp.code == '0'){
							js.showMessage("接口重触发成功!  "+resp.msg,null,null,1000*600000);
							//
							$("#searchForm").submit();
							$(".btn").attr("disabled", false);
						}else{
							js.alert(resp.msg);
							$(".btn").attr("disabled", false);
						}
					}
				});
			});


			$(".btn").attr("disabled", false);
		}
	}
});
/**
 * 点击放大镜，重新触发
 * @param pk_log
 */
function reTriggerClick(pk_log){
	alert("成功"+pk_log);
}







