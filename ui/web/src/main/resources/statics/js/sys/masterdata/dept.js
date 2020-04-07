var expand = true;
var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null
    },
    methods: {
    	add: function(){
        	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 增加部门</i>', baseURL + 'sys/masterdata/dept_addform.html', true, true)
        },
        refresh:function(){
        	expand = true;
        	$("#btnExpandTreeNode")[0].click();
        }
    }
});

function fun_disabled(dep_id){
	js.confirm('确定要停用该部门？', function(){
		$.ajax({
            type: "POST",
            url: baseURL + "masterdata/dept/disabledDept",
            data: "deptId=" + dep_id,
            success: function(r){
                if(r.code === 0){
                    expand = true;
                    $("#dataGrid").trigger("reloadGrid");
                    js.showMessage('操作成功！');
                }else{
                    alert(r.msg);
                }
            }
        });
	});
}
function fun_enable(dep_id){
	js.confirm('确定要启用该部门？', function(){
		$.ajax({
            type: "POST",
            url: baseURL + "masterdata/dept/enableDept",
            data: "deptId=" + dep_id,
            success: function(r){
                if(r.code === 0){
                    expand = true;
                    $("#dataGrid").trigger("reloadGrid");
                    js.showMessage('操作成功！');
                }else{
                    alert(r.msg);
                }
            }
        });
	});
}
function fun_delete(dep_id){
	js.confirm('确定要删除该部门？', function(){
		$.ajax({
            type: "POST",
            url: baseURL + "masterdata/dept/delete",
            data: "deptId=" + dep_id,
            success: function(r){
                if(r.code === 0){
                    expand = true;
                    $("#dataGrid").trigger("reloadGrid");
                    js.showMessage('删除成功！');
                }else{
                    alert(r.msg);
                }
            }
        });
	});
}

function fun_edit(dep_id){
	js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 编辑部门</i>', baseURL + 'sys/masterdata/dept_addform.html?depId='+dep_id, true, true)
}

$('#dataGrid').dataGrid({
	searchForm: $("#searchForm"),
	columnModel: [
		{header:'部门名称', name:'name', width:250, align:"left", frozen:true},
		{header:'部门编码', name:'code', width:200, align:"left"},
		{header:'部门类型', name:'deptTypeName', width:80, align:"center"},
		{header:'部门类别', name:'deptKind', width:80, align:"center", formatter: function(val, obj, row, act){
				return (val == '0')?"管理型":"生产型";
			},
		},
		{header:'创建人', name:'createBy', width:80, align:"center"},
		{header:'创建时间', name:'createDate', width:80, align:"center"},
		{header:'操作', name:'actions', width:130, sortable:false, title:false, formatter: function(val, obj, row, act){
			var actions = [];
				actions.push('<a href="#" title="编辑公司" onclick="fun_edit('+row.deptId+');"><i class="fa fa-pencil"></i></a>&nbsp;');
				if (row.status == Global.STATUS_NORMAL){
					actions.push('<a href="#" title="停用部门" onclick="fun_disabled('+row.deptId+');" ><i class="glyphicon glyphicon-ban-circle"></i></a>&nbsp;');
				}
				if (row.status == Global.STATUS_DISABLE){
					actions.push('<a href="#" title="启用部门" onclick="fun_enable('+row.deptId+');" ><i class="glyphicon glyphicon-ok-circle"></i></a>&nbsp;');
				}
				if(row.treeLeaf == "1")actions.push('<a href="#" title="删除部门" onclick="fun_delete('+row.deptId+');" ><i class="fa fa-trash-o"></i></a>&nbsp;');
			return actions.join('');
		}}
	],
	treeGrid: true,				// 启用树结构表格
	defaultExpandLevel: 0,		// 默认展开的层次
	expandNodeClearPostData: 'name,code,deptType,', // 展开节点清理请求参数数据（一般设置查询条件的字段属性，否则在查询后，不能展开子节点数据）	// 加载成功后执行事件
	//viewrecords: true,
    //height: 250,
    //rowNum: 5,
    //pager: "#jqGridPager",
	ajaxSuccess: function(data){
		if(expand)$("#btnExpandTreeNode")[0].click();
		expand = false;
	}
});