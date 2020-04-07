var setting = {
	view : {
		dblClickExpand : false
	},
	data : {
		simpleData : {
			enable : true,
			idKey : "deptId",
			pIdKey : "parentId",
			rootPId : "0"
		}
	},
	callback : {
		beforeClick : beforeClick,
		onClick : onClick
	}
};

var zNodes = [];

function beforeClick(treeId, treeNode) {
	/*
	 * var check = (treeNode && !treeNode.isParent); if (!check)
	 * alert("只能选择城市..."); return check;
	 */
	return true;
}

function onClick(e, treeId, treeNode) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
			.getSelectedNodes(), v = "";
	nodes.sort(function compare(a, b) {
		return a.id - b.id;
	});
	for (var i = 0, l = nodes.length; i < l; i++) {
		v += nodes[i].name + ",";
	}
	if (v.length > 0)
		v = v.substring(0, v.length - 1);
	var cityObj = $("#deptSel");
	cityObj.attr("value", v);

	// -----

	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	var nodes = zTree.getSelectedNodes();
	if (nodes.length >= 1) {
		nodes = zTree.transformToArray(nodes[0]);
	}
	var deptIds = "";
	for (index in nodes) {
		deptIds += (nodes[index].deptId + ",");
	}
	deptIds = deptIds.substring(0, deptIds.length - 1);
	$("#deptIds").val(deptIds);
}

function showMenu() {
	var cityObj = $("#deptSel");
	var cityOffset = $("#deptSel").offset();
	$("#menuContent").css({
		left : cityOffset.left + "px",
		top : cityOffset.top + cityObj.outerHeight() + "px"
	}).slideDown("fast");

	$("body").bind("mousedown", onBodyDown);
}
function hideMenu() {
	$("#menuContent").fadeOut("fast");
	$("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
	if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(
			event.target).parents("#menuContent").length > 0)) {
		hideMenu();
	}
}

$(function () {
    $("#dataGrid").dataGrid({
        url: baseUrl + 'setting/settingprocessflow/listUnique',
        datatype: "json",
        colModel: [			
			{ label: '工厂', name: 'factoryName', index: 'factory_name', width: 80 }, 			
			{ label: '车间', name: 'workshopName', index: 'workshop_name', width: 80 }, 			
			{ label: '线别', name: 'lineName', index: 'line_name', width: 80 },
			{label: "车辆类型", name:"vehicleTypeName",index:"vehicle_type_name",width: 80},
			{ label: '车型名称', name: 'busTypeName', index: 'bus_type_name', width: 80 },
			{ label: '操作', name: 'actions', index: 'ops', width: 50 ,align:'left',formatter:function(val, obj, row, act){
				actions = [];
				if(hasDeletePermission){
					actions.push('<a  href="'+baseUrl+'setting/settingprocessflow/delete_single?deptId='+row.deptId+'&busTypeCode='+row.busTypeCode+'&vehicleType='+row.vehicleType+'" class="btnList op" title="删除" data-confirm="确认要删除该车型工序吗？"><i class="fa fa-trash-o"></i>&nbsp;</a>');
				}
				var link = '<a class="op"  href="#" onClick="openEditor(\''+row.deptId+'\',\''+row.busTypeCode+'\',\''+row.vehicleType+'\')"  title="编辑"><i class="fa fa-pencil"></i>&nbsp;</a>';
				if(hasUpdatePermission){
					actions.push(link);
				}
				actions.push('<a  href="#"  onClick="openInfo(\''+row.deptId+'\',\''+row.busTypeCode+'\',\''+row.vehicleType+'\')" class="op" title="查看"><i class="fa fa-search" aria-hidden="true"></i>&nbsp;</a>');
			    return actions.join('');
			}}, 						
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        }
    });
});

function openInfo(deptId,busTypeCode,vehicleType){
    var url = baseUrl + "setting/settingprocessflow/toProcessInfo?deptId="+deptId+"&busTypeCode="+busTypeCode+"&vehicleType="+vehicleType;
	var options = {
			type: 2,
			maxmin: true,
			shadeClose: false,
			title: "查看车型工序",
			area: ["80%", "80%"],
			content: url,
			end:function(){
				$("#searchForm").submit();
			}
		};
		js.layer.open(options);
}

function openEditor(deptId,busTypeCode,vehicleType){
    var url = baseUrl + "setting/settingprocessflow/toProcessEditor?deptId="+deptId+"&busTypeCode="+busTypeCode+"&vehicleType="+vehicleType;
	var options = {
			type: 2,
			maxmin: true,
			shadeClose: false,
			title: "编辑车型工序",
			area: ["80%", "80%"],
			content: url,
			end:function(){
				$("#searchForm").submit();
			}
		};
		js.layer.open(options);
}

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		settingProcessFlow: {},
		busTypes:[],
		vehicleTypes:[]
	},
	created:function(){
		//
		$.ajax({
	    	url:baseUrl + "masterdata/dict/list",
	    	data:{
	    		pageSize:10000,
	    		pageNo:1,
	    		orderBy:"",
	    		type:"bus_type"
	    	},
	    	success:function(data){
	    		this.busTypes = data.page.list;
	    	}.bind(this)
	    });
		//
		$.ajax({
	    	url:baseUrl + "masterdata/dict/list",
	    	data:{
	    		pageSize:10000,
	    		pageNo:1,
	    		orderBy:"",
	    		type:"vehicle_type"
	    	},
	    	success:function(data){
	    		this.vehicleTypes = data.page.list;
	    	}.bind(this)
	    });
		//load query deptIds
		 $.ajax({
		    	url:baseUrl + "masterdata/dept/ztreeDepts",
		    	success: function(data){
		    		zNodes = data;
		    		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		    	}
		 })
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.settingProcessFlow = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.settingProcessFlow.id == null ? "setting/settingprocessflow/save" : "setting/settingprocessflow/update";
			$.ajax({
				type: "POST",
			    url: baseUrl + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.settingProcessFlow),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseUrl + "setting/settingprocessflow/delete",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		showDeptMenu: function(){
			showMenu();
			return false;
		},
		getInfo: function(id){
			$.get(baseUrl + "setting/settingprocessflow/info/"+id, function(r){
                vm.settingProcessFlow = r.settingProcessFlow;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
		openNew:function(){
			//console.log(window.document.location.href);
			var options = {
					type: 2,
					maxmin: true,
					shadeClose: false,
					title: "新增车型工序",
					area: ["80%", "80%"],
					content:baseUrl +  "modules/setting/settingprocessflow_editor_new.html",
					end:function(){
						$("#searchForm").submit();
					}
				};
				js.layer.open(options);
		}
	}
});