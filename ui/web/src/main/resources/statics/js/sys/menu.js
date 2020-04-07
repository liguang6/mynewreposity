var expand = false;
var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null
    },
    methods: {
    	add: function(){
        	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 增加菜单</i>', baseURL + 'sys/menu_addform.html', true, true)
        },
    }
});

function fun_delete(menu_id){
	layer.confirm('确定要删除该菜单？', {icon: 3, title:'提示'},function(){
		$.ajax({
            type: "POST",
            url: baseURL + "sys/menu/delete",
            data: "menuId=" + menu_id,
            success: function(r){
                if(r.code === 0){
                    expand = true;
                    $("#dataGrid").trigger("reloadGrid");
                }else{
                    alert(r.msg);
                }
            }
        });
	});
}

function fun_edit(menu_id){
	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 编辑菜单</i>', baseURL + 'sys/menu_addform.html?menuId='+menu_id, true, true)
}

$('#dataGrid').dataGrid({
	searchForm: $("#searchForm"),
	columnModel: [
		{header:'菜单名称', name:'name', width:250, align:"left", frozen:true},
		{header:'菜单ID', name:'menuId', width:200, align:"left"},
		{header:'类型', name:'type', width:80, align:"center"},
		{header:'父菜单ID', name:'parentId', width:180, align:"center"},
		{header:'父菜单名称', name:'parentName', width:180, align:"center"},
		{header:'菜单图标', name:'icon', width:150, align:"center"},
		{header:'排序', name:'orderNum', width:80, align:"center"},
		{header:'菜单URL', name:'url', width:250, align:"center"},
		{header:'授权', name:'perms', width:250, align:"center"},
		{header:'操作', name:'actions', width:130, sortable:false, title:false, formatter: function(val, obj, row, act){
			var actions = [];
			actions.push('<a href="#" title="编辑菜单" onclick="fun_edit('+row.menuId+');"><i class="fa fa-pencil"></i></a>&nbsp;');
			if(row.treeLeaf == "1")actions.push('<a href="#" title="删除菜单" onclick="fun_delete('+row.menuId+');" ><i class="fa fa-trash-o"></i></a>&nbsp;');

		return actions.join('');
	}}
	],
	treeGrid: true,				// 启用树结构表格
	defaultExpandLevel: 1,		// 默认展开的层次
	expandNodeClearPostData: 'name,menuId,type,', // 展开节点清理请求参数数据（一般设置查询条件的字段属性，否则在查询后，不能展开子节点数据）	// 加载成功后执行事件
	//viewrecords: true,
    //height: 250,
    //rowNum: 5,
    //pager: "#jqGridPager",
	ajaxSuccess: function(data){
		if(expand)$("#btnExpandTreeNode")[0].click();
		expand = false;
	}
});