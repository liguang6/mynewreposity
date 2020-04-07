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
	hideMenu();//关闭树
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


/**
 * 打开窗口
 * @param url
 */
function openFullWindow(title,url){
	var options = {
		type: 2,
		maxmin: true,
		shadeClose: false,
		title: title,
		area: ["80%", "80%"],
		content: url,
		end:function(){
			$("#searchForm").submit();
		}
	};
	js.layer.open(options);
}		
function openEritorWindow(id){
	openFullWindow("编辑工序",baseUrl + "masterdata/process/editorProcess?processId="+id);
}

$(function () {
	console.log(getRootPath());
    $("#dataGrid").dataGrid({
        url: baseUrl + 'masterdata/process/list',
        searchForm: $("#searchForm"),
        datatype: "json",
        mtype: 'POST', 
        colModel: [			
			/*{ label: 'id', name: 'id', index: 'id', width: 50, key: true },*/
			/*{ label: '部门ID', name: 'deptId', index: 'dept_id', width: 80 }, */	
        	{ label: '工厂', name: 'werks', index: 'werks', width: 55 }, 
			{ label: '工厂名称', name: 'werksName', index: 'werks_name', width: 120 }, 	
			{ label: '车间', name: 'workshop', index: 'workshop', width: 55 }, 
			{ label: '车间名称', name: 'workshopName', index: 'workshop_name', width: 80 }, 
			{ label: '线别', name: 'line', index: 'line', width: 55 }, 
			{ label: '线别名称', name: 'lineName', index: 'line_name', width: 80 }, 			
			{ label: '工序代码', name: 'processCode', index: 'process_code', width: 55 }, 			
			{ label: '工序名称', name: 'processName', index: 'process_name', width: 120 }, 			
			{ label: '生产监控点', name: 'monitoryPointFlag', index: 'monitory_point_flag', width: 80,formatter:function(val, obj, row, act){
				return val ==='0'?"否":"是";
			} }, 			
			/*{ label: '工段ID', name: 'sectionId', index: 'section_id', width: 80 }, 	*/		
			{ label: '工段', name: 'sectionCode', index: 'section_code', width: 55 }, 			
			/*{ label: '计划节点ID', name: 'planNodeId', index: 'plan_node_id', width: 80 }, 	*/		
			{ label: '计划节点', name: 'planNodeName', index: 'plan_node_name', width: 80 }, 			
			{ label: '备注', name: 'memo', index: 'memo', width: 80 }, 			
			/*{ label: '删除标识', name: 'deleteFlag', index: 'delete_flag', width: 80 }, 			
			{ label: '维护人', name: 'editor', index: 'editor', width: 80 }, 			*/
			{ label: '操作', name: 'actions', width: 50 ,align:'left',formatter:function(val, obj, row, act){
				actions = [];
				actions.push('<a href="'+baseUrl+'masterdata/process/delete_single?id='+row.id+'" class="btnList" title="删除" data-confirm="确认要删除该工序吗？"><i class="fa fa-trash-o"></i>&nbsp;</a>');
				var link = "<a id='editor' href='#' onClick='openEritorWindow("+row.id+")' title='编辑'><i class='fa fa-pencil'></i>&nbsp;</a>";
				actions.push(link);
			    return actions.join('');
			}}			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 15,
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,        
       
    });
    
    $.ajax({
    	url:baseUrl + "masterdata/dept/ztreeDepts",
    	success: function(data){
    		zNodes = data;
    		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
    	}
    })
    
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		settingProcess: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		getProcessNoFuzzy:function(){
			getProcessNoSelect("#processCode","code",null);
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.settingProcess = {};
			return false;
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
			var url = vm.settingProcess.id == null ? "masterdata/process/save" : "masterdata/process/update";
			vm.settingProcess.processCode=vm.settingProcess.processCode.toUpperCase();
			$.ajax({
				type: "POST",
			    url: baseUrl + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.settingProcess),
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
		//删除一条记录
		del: function (id) {
			var ids = [id];
			console.log(id);
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseUrl + "masterdata/process/delete",
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
		getInfo: function(id){
			$.get(baseUrl + "masterdata/process/info/"+id, function(r){
                vm.settingProcess = r.settingProcess;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#dataGrid").jqGrid('getGridParam','page');
			//获取选择的节点，以及其子节点
			var nodes;
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
 			nodes = zTree.getSelectedNodes();
 			if(nodes.length >=1){
 			   nodes = zTree.transformToArray(nodes[0]);
 			}
 			var deptIds = [];
 			for(index in nodes){
 				deptIds[index] = nodes[index].deptId;
 			}
			var data = {isMonitoryPoint:$("#monitoryPoint").prop('checked')?'1':'',isPlanNode:$("#planNode").prop('checked')?'1':''};
			var strdata = JSON.stringify(data);
			console.log(strdata);
			$.ajax({
				url:baseUrl+"masterdata/process/list",
				type:"post",
				data:data,
				success:function(result){
					//update dataGrid
					console.log(result);
					$("#dataGrid").jqGrid('setGridParam',{
						datatype:'json',
						data:result.page.list
					}).trigger("reloadGrid");
				}
			});
		},
		showDeptMenu: function(event){
			showMenu();
			return false;
		}
	}
});