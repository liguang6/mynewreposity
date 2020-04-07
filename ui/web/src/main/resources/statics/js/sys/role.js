//菜单树
var menu_ztree;
var menu_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "menuId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    },
    check:{
        enable:true,
        nocheckInherit:true
    }
};

//部门结构树
var dept_ztree;
var dept_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};

//数据树
var data_ztree;
var data_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: 1
        },
        key: {
            url:"nourl"
        }
    },
 /**
  * 异步加载ztree,dataFilter很重要
  */
/*    async:{
    	enable:true,
    	autoParam:["deptId=nodeid"],
    	url:baseURL+"masterdata/dept/list",
    	type:"post",
    	dataFilter:filter
    },*/
    check:{
        enable:true,
        nocheckInherit:true,
        chkboxType:{ "Y" : "s", "N" : "s" }
    }
};

function filter(treeId, parentNode, childNodes) {
    if (!childNodes) return null;
    for (var i=0, l=childNodes.length; i<l; i++) {
        childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
    }
    return childNodes;
}

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            roleName: null
        },
        showList: true,
        title:null,
        role:{
            deptId:null,
            deptName:null
        }
    },
    methods: {
        query: function () {
        	vm.showList = true;
        	//alert($("#searchForm").attr("action"))
        	$("#searchForm").submit();
           // vm.reload();
        },
        cancel:function(){
        	vm.showList = true;
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增角色";
            vm.role = {deptName:null, deptId:null};
            vm.getMenuTree(null);

           // vm.getDept();

            //vm.getDataTree();
        },
        update: function () {
            var roleId = getSelectedRow();
            if(roleId == null){
                return ;
            }

            vm.showList = false;
            vm.title = "修改角色";
            //vm.getDataTree();
            vm.getMenuTree(roleId);

            //vm.getDept();
        },
        del: function () {
            var roleIds = getSelectedRows();
            if(roleIds == null){
                return ;
            }

            js.confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/role/delete",
                    contentType: "application/json",
                    data: JSON.stringify(roleIds),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.query();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        getRole: function(roleId){
            $.get(baseURL + "sys/role/info/"+roleId, function(r){
                vm.role = r.role;

                //勾选角色所拥有的菜单
                var menuIds = vm.role.menuIdList;
                for(var i=0; i<menuIds.length; i++) {
                    var node = menu_ztree.getNodeByParam("menuId", menuIds[i]);
                    menu_ztree.checkNode(node, true, false);
                }

                //勾选角色所拥有的部门数据权限
                var deptIds = vm.role.deptIdList;
                if(deptIds && deptIds.length){
                    for(var i=0; i<deptIds.length; i++) {
                        var node = data_ztree.getNodeByParam("deptId", deptIds[i]);
                        data_ztree.checkNode(node, true, false);
                    }
                }


                //vm.getDept();
            });
        },
        saveOrUpdate: function () {
            //获取选择的菜单
            var nodes = menu_ztree.getCheckedNodes(true);
            var menuIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                menuIdList.push(nodes[i].menuId);
            }
            vm.role.menuIdList = menuIdList;

            //获取选择的数据
           // var nodes = data_ztree.getCheckedNodes(true);
            //var deptIdList = new Array();
            //for(var i=0; i<nodes.length; i++) {
             //   deptIdList.push(nodes[i].deptId);
          //  }
          //  vm.role.deptIdList = deptIdList;

            var url = vm.role.roleId == null ? "sys/role/save" : "sys/role/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.role),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            //vm.reload();
                        	vm.query();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getMenuTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "sys/menu/list", function(r){
                menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, r);
                //展开所有节点
                menu_ztree.expandAll(false);

                if(roleId != null){
                    vm.getRole(roleId);
                }
            });
        },
        getDataTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "masterdata/dept/listall", function(r){
                data_ztree = $.fn.zTree.init($("#dataTree"), data_setting,r);
                //展开所有节点
                data_ztree.expandAll(true);
            });
        },
        getDept: function(){
            //加载部门树
            $.get(baseURL + "masterdata/dept/list", function(r){
                dept_ztree = $.fn.zTree.init($("#deptTree"), dept_setting, r);
                var node = dept_ztree.getNodeByParam("deptId", vm.role.deptId);
                if(node != null){
                    dept_ztree.selectNode(node);

                    vm.role.deptName = node.name;
                }
            })
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = dept_ztree.getSelectedNodes();
                    //选择上级部门
                    vm.role.deptId = node[0].deptId;
                    vm.role.deptName = node[0].name;

                    layer.close(index);
                }
            });
        },
    }
});

$('#dataGrid').dataGrid({
	searchForm: $("#searchForm"),
	columnModel: [
		{header:'角色ID', name:'roleId', width:50, align:"center", frozen:true},
		{header:'角色名称', name:'roleName', width:120, align:"center"},
		{header:'排序号', name:'roleSort', width:50, align:"center"},
		{header:'备注', name:'remark', width:80, align:"center"},
		{header:'状态', name:'status', width:80, align:"center",formatter:function(val, obj, row, act){
			return row.status == Global.STATUS_NORMAL?"<span class=\"label label-success\">正常</span>":"<span class=\"label label-danger\">冻结</span>"
		}},
		{header:'创建人', name:'createBy', width:80, align:"center"},
		{header:'更新时间', name:'createTime', width:80, align:"center"},
		{header:'操作', name:'actions', align:"left", sortable:false,width:70, sortable:false, title:false, formatter: function(val, obj, row, act){
			var actions = [];
			var flag = getPermission();
			if (flag == "1"){
				if (row.status == Global.STATUS_NORMAL){
					actions.push('<a href="'+baseURL+'sys/role/disableRole/'+row.roleId+'" class="btnList" title="停用角色" data-confirm="确认要停用该角色吗？" ><i class="glyphicon glyphicon-ban-circle"></i></a>&nbsp;');
				}
				if (row.status == Global.STATUS_DISABLE){
					actions.push('<a href="'+baseURL+'sys/role/enableRole/'+row.roleId+'" class="btnList" title="启用角色" data-confirm="确认要启用该角色吗？" ><i class="glyphicon glyphicon-ok-circle"></i></a>&nbsp;');
				}
				
				actions.push('<a href="#" onclick="authUser('+row.roleId+',\''+row.roleName+'\')"  title="分配用户"><i class="fa fa-user"></i></a>&nbsp;');
				
			}
			return actions.join('');
		}}
	],
	showCheckbox:true,
	showRownum:false,
	rowNum: 15,
	dataId:'roleId',
	ajaxSuccess: function(data){}
});


function authUser(roleId,roleName){
	var selectUser = ajaxGetRoleUser(roleId);//已分配的用户
	

	var options = {
		type: 2,
		maxmin: true,
		shadeClose: true,
		title: '角色('+roleName+')授权用户',
		area: ["1300px", "600px"],
		content: 'sys/user_auth.html',
		btn: ['<i class="fa fa-check"></i> 确定'],
		success:function(layero, index){
			var selectUsersInput= $("#selectUsers", layero.find("iframe")[0].contentWindow.document);
			$(selectUsersInput).val(selectUser)
		},
		btn1:function(index,layero){
			//alert(layero.find('iframe')[0]['name'])
			var win = layero.find('iframe')[0].contentWindow;
			var selectData = win.getSelectData();
			var userIds = [];
			//alert(JSON.stringify(selectData))
/*			if(typeof listselectCheck == 'function'){
				if (!listselectCheck('userSelect', selectData)){
					return false;
				}
			}*/
			// 点击确定，获取用户选择数据
			if(typeof listselectSetSelectData == 'function'){
				listselectSetSelectData('userSelect', selectData);
			}else{
				$.each(selectData, function(key, value){
					userIds.push(value['userId']);
				});
			}

			if(typeof listselectCallback == 'function'){
				listselectCallback(roleId, 'ok', index, layero, userIds);
			}
		}
	};
	options.btn.push('<i class="fa fa-close"></i> 关闭');
	options['btn'+options.btn.length] = function(index, layero){
		if(typeof listselectCallback == 'function'){
			listselectCallback(roleId, 'cancel', index, layero);
		}
	};
	js.layer.open(options);
}

/**
 * 根据角色获取已分配的用户
 * @returns
 */
function ajaxGetRoleUser(roleId){
	var userIds=[];
	$.ajaxSettings.async = false;
	$.get(baseURL + "sys/role/getRoleUser/"+roleId, function(r){
		userIds= r.list
	})
	$.ajaxSettings.async = true;
	return userIds;
}

function listselectCallback(roleId, action, index, layero,userIds){
	if (roleId&& action == 'ok'){
		$.ajax({
			type: "POST",
	        url: baseURL + "sys/role/authRoleUser",
	        data:{
	        	roleId: roleId,
				userIds:userIds
	        },
	        dataType:"json",
	        success: function(data){
	        	js.showMessage(data.msg);
	    		try {
	    			parent.layer.close(index);
	            } catch(e){
	            	layer.close(index);
	            }
	        }
	    });
	}
	
}

