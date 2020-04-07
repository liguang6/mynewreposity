// 初始化 dataTable默认加载10行
var mydata = 
	[{ id: "1",value1: ""},{ id: "2",value1: ""},{ id: "3",value1: ""},
	 { id: "4",value1: ""},{ id: "5",value1: ""},{ id: "6",value1: ""},
	 { id: "7",value1: ""},{ id: "8",value1: ""},{ id: "9",value1: ""},
	 { id: "10",value1: ""}];
var trindex = 11; // 新增行号
var lastrow,lastcell; 
var returnValue='';
var activeTab="#one"; // 当前显示Tab选项卡 默认选择单一值
var vm = new Vue({
	el:'#rrapp',
	data:{
	},
	created:function(){
	},
	methods: {
		showTable:function (val){
			var oldval=trim(val);
			if(oldval!=''){
				// 范围显示
				if(oldval.indexOf('~')>0){
					activeTab='#two';
					if(oldval.indexOf('!')>=0){
						activeTab='#four';
					}
					var arr= oldval.split(",");
					if(arr.length>mydata.length){
						var datalength=mydata.length;
						for(var i=0;i<=(arr.length-datalength);i++){
							var data={};
							data.id=datalength+i+1;
							data.value1='';
							data.value2='';
							mydata.push(data);
						}
					}
					for(var i=0;i<arr.length;i++){
						var array=arr[i].split("~");
						if(activeTab=='#two'){
							mydata[i].value1=array[0];
							mydata[i].value2=array[1];
						}else{
							mydata[i].value1=array[0].substring(1,array[0].length);
							mydata[i].value2=array[1].substring(1,array[1].length);
						}
						
					}
				}
				// 单一值显示(不包含'~'字符)
				if( oldval.indexOf('~')<0){
					if(oldval.indexOf('!')>=0){
						activeTab='#three';
					}
					var arr= oldval.split(",");
					if(arr.length>mydata.length){
						var datalength=mydata.length;
						for(var i=0;i<=arr.length-datalength;i++){
							var data={};
							data.id=datalength+i+1;
							data.value1='';
							mydata.push(data);
						}
					}
					for(var i=0;i<arr.length;i++){
						if(activeTab=='#one'){
							mydata[i].value1=arr[i];
						}else{
							mydata[i].value1=arr[i].substring(1,arr[i].length);
						}
					}
				}
				$("a[href='"+activeTab+"']").parent().addClass("active");
				$("a[href='"+activeTab+"']").parent().siblings().removeClass("active");
			}
			$("#dataGrid").dataGrid({
				datatype: "local",
				data: mydata,
		        colModel: [			
		            {label: '<span style="color:blue">单值</span>', name: 'value1',index:"value1", width: "150",align:"center",sortable:false,editable:true}, 	
		            {label: '<span style="color:blue"></span>', name: 'value2',index:"value2", width: "150",align:"center",sortable:false,editable:true,hidden:true}, 	
		        ],
				beforeEditCell:function(rowid, cellname, v, iRow, iCol){
					lastrow=iRow;
					lastcell=iCol;
				},
				afterSaveCell:function(rowid, cellname, value, iRow, iCol){
					var ids = $("#dataGrid").jqGrid('getDataIDs');
					for (var i = 0; i < ids.length; i++) {
						var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
						if(data['value']!=''){
							returnValue+=data['value']+",";
						}
					}
					$("#returnValue").val(returnValue);
				},
				shrinkToFit: false,
		        cellEdit:true,
		        cellurl:'#',
				cellsubmit:'clientArray',
		    });
			if(activeTab=='#one' || activeTab=='#three'){
				$("#dataGrid").jqGrid('setLabel','value1','<span style="color:blue">单值</span>');
			    jQuery("#dataGrid").setGridParam().hideCol("value2").trigger("reloadGrid");
			}
			if(activeTab=='#two' || activeTab=='#four'){
				console.log('tabName',activeTab);
				$("#dataGrid").jqGrid('setLabel','value1','<span style="color:blue">上限</span>');
			    $("#dataGrid").jqGrid('setLabel','value2','<span style="color:blue">下限</span>');
			    jQuery("#dataGrid").setGridParam().showCol("value2").trigger("reloadGrid");
			}
		},
		getTableData:function(){
			$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
			var trs=$("#dataGrid").children("tbody").children("tr");
			var retVal='';
			console.log('activeTab',activeTab);
			$.each(trs,function(index,tr){
				var td1 = $(tr).find("td").eq(1);
				var td2 = $(tr).find("td").eq(2);
				if(trim($(td1).text())!=''  && trim($(td2).text())==''){
					if(activeTab=="#one"){  // 选择单一值：多个值用“,”隔开 a,b,c
						retVal+=$(td1).text()+',';
					}
					
					if(activeTab=="#three"){  // 排除单一值：!a,!b,!c
						retVal+='!'+$(td1).text()+',';
					}
				}
				
				if(trim($(td1).text())!='' && trim($(td2).text())!=''){
					console.log('val',trim($(td1).text())+"-"+trim($(td2).text()));
					if(activeTab=="#two"){  // 选择范围：用~表示；例如： a~b,c~d
						retVal+=$(td1).text()+'~'+$(td2).text()+",";
					}
					if(activeTab=="#four"){  // 选择范围：用~表示；例如： !a~!b,!c~!d
						retVal+='!'+$(td1).text()+'~'+'!'+$(td2).text()+",";
					}
					console.log('retVal',retVal);
				}
			});
			
			$("#returnValue").val(retVal.substring(0,retVal.length-1));
		}
	}
});
$(function(){
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		vm.showTable("");
		// 获取已激活的标签页的名称
	    activeTab = $(e.target).attr("href"); 
		if(activeTab=='#one' || activeTab=='#three'){
			$("#dataGrid").jqGrid('setLabel','value1','<span style="color:blue">单值</span>');
		    jQuery("#dataGrid").setGridParam().hideCol("value2").trigger("reloadGrid");
		}
		if(activeTab=='#two' || activeTab=='#four'){
			$("#dataGrid").jqGrid('setLabel','value1','<span style="color:blue">上限</span>');
		    $("#dataGrid").jqGrid('setLabel','value2','<span style="color:blue">下限</span>');
		    jQuery("#dataGrid").setGridParam().showCol("value2").trigger("reloadGrid");
		}
		var ids = $("#dataGrid").jqGrid('getDataIDs');
		for (var i = 0; i < ids.length; i++) {
			var data=$("#dataGrid").jqGrid('getRowData',ids[i]);
			$("#dataGrid").jqGrid('setCell',ids[i],"value1",' ');
			$("#dataGrid").jqGrid('setCell',ids[i],"value2",' ');
		}
		$("#returnValue").val(returnValue);
	});
	
	
	$('#dataGrid').bind('paste', function(e) {
		console.log("-->paste");
		e.preventDefault(); // 消除默认粘贴
		// 获取粘贴板数据
		var data = null;
		var clipboardData = window.clipboardData || e.originalEvent.clipboardData; // IE || chrome
		data = clipboardData.getData('Text');
		// console.log(data.replace(/\t/g, '\\t').replace(/\n/g,'\\n')); //data转码
		// 解析数据
		var arr = data.split('\n').filter(function(item) { // 兼容Excel行末\n，防止出现多余空行
			return (item !== "");
		}).map(function(item) {
			return item.split("\t");
		});
		// 输出至网页表格
		var tab = this; // 表格DOM
		var td = $(e.target).parents('td');
		var startRow = td.parents('tr')[0].rowIndex;
		var startCell = td[0].cellIndex;
		var rows = tab.rows.length; // 总行数
		if(rows<arr.length){
			for(var i=0;i<=(arr.length-rows);i++){
				addRow();
			}
			rows=arr.length;
		}
		
		for (var i = 0; i < arr.length && startRow + i <=rows; i++) {
			var cells = tab.rows[startRow + i].cells.length; // 该行总列数
			for (var j = 0; j < arr[i].length && startCell + j <=cells; j++) {
				var cell = tab.rows[startRow + i].cells[startCell + j];
				$(cell).find(':text').val(arr[i][j]); //找到cell下的input:text，设置value
				$('#dataGrid').jqGrid("saveCell", lastrow, lastcell);
				$(cell).html(arr[i][j]);
			}
		}
	});
});	

function addRow(){
	mydata.push([{ id: trindex,value1: ""}]);
	var addtr = '<tr id = "'+trindex+'" role="row" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr '+((trindex%2 == 0)?"ui-priority-secondary":"")+'" aria-selected = "false">'+
	'<td role="gridcell" class="ui-state-default jqgrid-rownum" style="text-align:center;" title="2" aria-describedby="dataGrid_rn">' + trindex + 
	'</td><td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'<td role="gridcell" style="text-align:center;" aria-describedby="dataGrid_'+trindex+'"></td>'+
	'</tr>';
	$("#dataGrid tbody").append(addtr);
	trindex++ ;
}
function trim(str){ 
    return str.replace(/(^\s*)|(\s*$)/g, ""); 
}