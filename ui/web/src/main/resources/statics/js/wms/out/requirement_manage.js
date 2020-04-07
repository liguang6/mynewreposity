

function initTable() {
	var now = new Date(); //当前日期
	var startDate=new Date(now.getTime()-7*24*3600*1000);
	startDate.setHours(0);
	startDate.setMinutes(0);
	startDate.setSeconds(0);
	$("#createDateStart").val(startDate.Format("yyyy-MM-dd hh:mm:ss"));
	$("#createDateEnd").val(now.Format("yyyy-MM-dd hh:mm:ss"));
	
	
	$("#jqGrid").dataGrid({
		searchForm : $("#searchForm"),
		url : baseURL + 'out/requirement/list',
		datatype : "json",
		colModel : [ {
			label : '需求号',
			name : 'REQUIREMENT_NO',
			width : 150,
			formatter : function(v,o,row){
				var url = baseUrl +  "out/requirement/info/" + v+"/"+$("#werks").val()+"/"+$("#WH_NUMBER").val()
				+"/"+row["REQUIREMENT_STATUS"];
				var actions = [];
				var link = '<a onClick=vm.detail("'+url+'") title="需求明细" style="cursor: pointer;">'+v+'</a>';
				actions.push(link);
			    return actions.join('');
			}
		}, {
			label : '需求类型',
			name : 'BUSINESS_NAME_VALUE',
			formatter : function(val) {
				// TODO:BUSINESS_NAME --> 对应的名称
				if(val==null){
					return "";
				}
				return val;
			},
			width : 200
		}, {
			label : '工厂',
			name : 'WERKS',
			width : 60
		}, {
			label : '仓库号',
			name : 'WH_NUMBER',
			width : 60
		}, {
			label : '需求日期',
			name : 'REQUIRED_DATE',
			width : 100
		}, {
			label : '状态',
			name : 'REQUIREMENT_STATUS',
			//状态 状态00已创建 01 已审批 02 备料中 03 部分下架 04 已下架 05 部分交接 06 已交接 07 关闭
			formatter : function(v) {
				if(v == '00'){
					return '已创建'
				}
				if(v == '01') {
					return '已审批'
				}
				if(v == '02') {
					return '备料中'
				}
				if(v == '03') {
					return '部分下架'
				}
				if(v == '04') {
					return '已下架'
				}
				if(v == '05') {
					return '部分交接'
				}
				if(v == '06') {
					return '已交接'
				}
				if(v == '07') {
					return '关闭'
				}
				if(v == 'X') {
					return '已删除'
				}
				return '';
			},
			width : 50
		}, {
			label : '创建人',
			name : 'CREATOR',
			width : 100
		}, {
			label : '创建时间',
			name : 'CREATE_DATE',
			width : 150
		}, ],
		viewrecords : true,
		/* height: "100%", */
		rowNum : 15,
		rownumbers : false,
		rownumWidth : 25,
		autowidth : true,
		multiselect : false,
		// 横向滚动条
		shrinkToFit : false,
		autoScroll : true,
		gridComplete : function(){
			//点击按钮，弹出到新的标签页（框架内的）
			$(document).on("click", ".toNewPage", function(e) {
					var $this = $(this),
						href = $this.attr('href'),
						icon = $this.attr('icon')||$this.data("icon")||"",
						title = $this.data("title") || $this.attr("title") || $this.text();
					if(icon!=''){
						icon = '<i class="'+icon+'"></i> ';
					}else{
						icon = "<i class='fa fa-circle-o'></i>";
					}
					if (href && href != "" && href != "blank") {
						js.addTabPage($this,  icon+$.trim(title || js.text("tabpanel.newTabPage")), href, null, true, false);
						if ($this.parent().hasClass("treeview")) {
							window.location.hash = href.replace("#", "")
						}
						return false
					}
					return true
			});
		}
	});
}

var vm = new Vue({
	el : "#vue-app",
	data : {
		warehourse : [],// 仓库列表
		data : {
			REQUIREMENT_TYPE : null,
			WH_NUMBER : "",
			REQUIREMENT_TYPE : "",
			REQUIREMENT_STATUS : ""
		},// 查询条件
		more_query_params : true,// 查询条件显示
	},
	created : function() {
		// 1. 初始化仓库工厂
		var werks = $("#werks").val();
		this.data.WERKS = werks;
		
		$.ajax({
			url : baseUrl + "common/getWhDataByWerks",
			data : {
				"WERKS" : werks,
				"MENU_KEY":"WMS_OUT_REQUIREMENT_QUERY"
			},
			success : function(resp) {
				vm.warehourse = resp.data;
				vm.data.WH_NUMBER = resp.data[0].WH_NUMBER;
			}
		});
		// 2.初始化表格
		$(function() {
			initTable();
		});

		this.businessList=getBusinessList("07","WMS_OUT_REQUIREMENT_QUERY",werks);
		if(undefined == this.businessList || this.businessList.length<=0){
			js.showErrorMessage("工厂未配置需求业务类型！");
		}else{
			this.requireTypes=this.businessList[0].CODE;
		}
	},
	methods : {
		onPlantChange : function(event) {
			// 工厂变化的时候，更新仓库号下拉框
			var plantCode = event.target.value;
			if (plantCode === null || plantCode === ''
					|| plantCode === undefined) {
				return;
			}
			this.data.WERKS = plantCode;
			// 查询工厂仓库
			$.ajax({
				url : baseUrl + "common/getWhDataByWerks",
				data : {
					"WERKS" : plantCode,
					"MENU_KEY":"WMS_OUT_REQUIREMENT_QUERY"
				},
				success : function(resp) {
					vm.warehourse = resp.data;
					vm.data.WH_NUMBER = resp.data[0].WH_NUMBER;
				}
			});
			
			this.businessList=getBusinessList("07","WMS_OUT_REQUIREMENT_QUERY",plantCode);
			if(undefined == this.businessList || this.businessList.length<=0){
				js.showErrorMessage("工厂未配置需求业务类型！");
			}else{
				this.requireTypes=this.businessList[0].CODE;
			}
		},
		query : function() {
			$("#pageNo").val(1);
			$("#searchForm").submit();
		},
		detail:function(url,werks){
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> WMS事务记录</i>',
			url,true, true);
		}
	}
})