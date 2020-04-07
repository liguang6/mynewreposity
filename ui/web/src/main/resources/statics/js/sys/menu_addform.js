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
    }
};
var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title:null,
        type:"增加",
        menu:{
        	menuId:null,
            name:null,
            parentName:null,
            parentId:null,
            type:null,
            url:null
        }
    },
    methods: {
    	reset:function(){
    		vm.menu.menuId=null,
			vm.menu.menuKey=null,
    		vm.menu.name=null,
    		vm.menu.parentName=null,
    		vm.menu.parentId=null,
    		vm.menu.type=null,
    		vm.menu.url=null
    	},
    	getMenu: function(){
            //加载部门树
            $.get(baseURL + "sys/menu/list2", function(r){
            	menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, r);
                var node = menu_ztree.getNodeByParam("menuId", vm.menu.parentName);
                if(node != null){
                    menu_ztree.selectNode(node);
                    vm.menu.parentName = node.name;
                }
            })
        },
        test:function(){
        	vm.getMenu();
        },
    	menuTree: function(){
    		vm.getMenu();
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择菜单",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#menuLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = menu_ztree.getSelectedNodes();
                    //选择上级菜单
                    vm.menu.parentId = node[0].menuId;
                    vm.menu.parentName = node[0].name;
                    layer.close(index);
                }
            });
        },
        saveOrUpdate:function(){
    		var flag=true,alertMsg="";
        	if(vm.menu.parentId == null && $("#menuType").val()=="1"){
				alertMsg+="请选择上级菜单！<br>";
				flag=false;
        	}
			if((vm.menu.menuKey == null || vm.menu.menuKey == "") && $("#menuType").val()!="2"){
				alertMsg+="请输入菜单编码！<br>";
				flag=false;
			}
        	if(vm.menu.name == null || vm.menu.name == ""){
				alertMsg+="请输入菜单名称！<br>";
				flag=false;
        	}
        	if(vm.menu.treeSort == null || vm.menu.treeSort == ""){
				alertMsg+="请输入节点排序[菜单排序]！<br>";
				flag=false;
        	}
        	if(!flag){
				alert(alertMsg);
				return false;
			}
        	vm.menu.type = $("#menuType").val();
        	vm.menu.treeLeaf = $("#treeLeaf").val();
        	
        	var url = vm.menu.menuId == null ? "sys/menu/save" : "sys/menu/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.menu),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                        	vm.reset();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
    }
});

$(function () {
	if(getUrlKey("menuId") != null){
		$.ajax({
            type: "GET",
            url: baseURL + "sys/menu/info/" + getUrlKey("menuId"),
            contentType: "application/json",
            success: function(r){
            	vm.menu.menuId = r.menu.menuId;
            	vm.menu.parentId = r.menu.parentId;
                vm.menu.parentName = r.menu.parentName;
				vm.menu.menuKey = r.menu.menuKey;
                vm.menu.name = r.menu.name;
                vm.menu.orderNum = 	r.menu.orderNum;
                vm.menu.url = 	r.menu.url;
                vm.menu.treeSort = 	r.menu.treeSort;
                vm.menu.treeLeaf = 	r.menu.treeLeaf;
                vm.menu.treeLevel = r.menu.treeLevel;
                vm.menu.type = r.menu.type;
                vm.menu.perms = r.menu.perms;
                vm.menu.icon = r.menu.icon;
                vm.type = "编辑";
				if(vm.menu.menuId == null || vm.menu.menuId == ""){
					// $("#menuKey").attr("disabled", false);
					$("#menuKey").attr("readonly", "false");
				}else if(vm.menu.menuId>=0){//修改,不能编辑菜单编码,用于云平台修改菜单信息用(云平台统一用菜单code【menuKey】作为唯一键)
					// $("#menuKey").attr("disabled", true);
					$("#menuKey").attr("readonly", "true");
				}
            }
		});
	}

	function getUrlKey(name){
		return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
	}
});

