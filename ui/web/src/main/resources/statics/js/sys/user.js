$(function () {
	//---------------------------------------
    $("#jqGrid").dataGrid({
    	searchForm:$("#searchForm"),
        url: baseURL + 'sys/user/list',
        datatype: "json",
        colModel: [			
			{ label: '用户名', name: 'username', width: 75,frozen:true},
			{label: '操作',name: 'actions',width:130,sortable:false,frozen:true, title:false, formatter:function(val, obj, row, act){
				var actions = [];
				//if(row.leaveWay == 1){//借用此字段做权限控制
					actions.push('<a href="'+baseURL+'sys/user/resetPwd?userId='+row.userId+'" class="btnList" title="重置密码" data-confirm="确认要重置该用户的密码吗？"><i class="fa fa-cog"></i>&nbsp;</a>');
					if(row.status !==0){
						actions.push('<a href="'+baseURL+'sys/user/disable?userId='+row.userId+'" class="btnList" title="停用用户" data-confirm="确认要停用该用户吗？"><i class="glyphicon glyphicon-ban-circle"></i>&nbsp;</a>')
					}else{
						actions.push('<a href="'+baseURL+'sys/user/enable?userId='+row.userId+'" class="btnList" title="启用用户" data-confirm="确认要启用该用户吗？"><i class="glyphicon glyphicon-ok-circle"></i>&nbsp;</a>')
					}
					actions.push('<a href="'+baseURL+'sys/user/deleteSingle?userId='+row.userId+'" class="btnList" title="删除用户" data-confirm="确认要删除该用户吗？"><i class="fa fa-trash-o"></i>&nbsp;</a>');
					actions.push('<a href="'+baseURL+'sys/user/edit?userId='+row.userId+'"  title="编辑用户"><i class="fa fa-pencil"></i>&nbsp;</a>');
					actions.push('<a href="'+baseURL+'sys/user/auth?userId='+row.userId+'"  title="用户授权" ><i class="fa fa-chevron-circle-right"></i></a>&nbsp;');
				//}
				return actions.join("");
			}},
			{ label: '账号状态', name: 'status', width: 75, formatter: function(value, options, row){
				return value === 0 ? 
					'<span class="label label-danger">禁用</span>' : 
					'<span class="label label-success">正常</span>';
			}},
			{label:'在职状态',name:'workingStatus',width: 75,formatter: function(value, options, row){
				return value === 0 ?
						'<span class="label label-danger">离职</span>':
						'<span class="label label-success">在职</span>';
			}},
			{label:"工号",name: 'staffNumber',width: 75,frozen: true},
            { label: '所属部门', name: 'deptName', sortable: false, width: 75 },
			{ label: '邮箱', name: 'email', width: 90 },
			{ label: '手机号', name: 'mobile', width: 100 },
			{label:'姓名',width:75,name:'fullName'},
			/*{label:'身份证号码',width:90,name:"identityCard"},
			{label:'员工级别',name:"staffLevel",width: 75},
			{label:"岗位",name:"job",width:75},
			{label:'工作地点',name:"workplace",width:75},
			{label:"性别",name:"sex",width:50},
			{label:"员工类型",name:"userType",width:75},
			{label:'生日',name:"birthday",width:75},*/
			{label:"最近登录IP",name:"lastLoginIp",width:100},
			{label:"最近登录",name:"lastLoginDate"}	
        ],
		viewrecords: true,
        /*height: "100%",*/
        rowNum: 15,
        rownumbers: false, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
        //横向滚动条
        shrinkToFit:false,  
        autoScroll: true
    });
    
    $("#jqGrid").jqGrid("setFrozenColumns");    
    
    $.get(baseURL + "masterdata/dept/ztreeDepts", function(r){
        ztree = $.fn.zTree.init($("#deptTree"), setting, r);
    })
});



function uploadExcel() {
	//上传导入文件
	layer.open({
		type : 1,
		offset : '50px',
		skin : 'layui-layer-molv',
		title : "选择文件",
		area : [ '300px', '250px' ],
		shade : 0,
		shadeClose : false,
		content : jQuery("#uploadExcel"),
		btn : [ '确定', '取消' ],
		btn1 : function(index) {
			var excelName = $("#excel").val();
			if (excelName.indexOf("xlsx") === -1) {
				alert("不是excel文件")
			} else {
				//FormData ajax 文件上传
				var formData = new FormData($("#uploadExcelForm")[0]);
				$.ajax({
					url : baseURL + "sys/user/import",
					type : "post",
					data : formData,
					async : false,
					cache : false,
					contentType : false,
					processData : false,
					success : function(rep) {
						if (rep.code === 0) {
							alert("上传成功");
						} else {
							alert("上传失败");
						}
					}
				});
			}
		}
	});

}


var ztree;

var setting = {
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
}


function selectDept() {

	layer.open({
		type : 1,
		offset : '50px',
		skin : 'layui-layer-molv',
		title : "选择部门",
		area : [ '300px', '450px' ],
		shade : 0,
		shadeClose : false,
		content : jQuery("#deptLayer"),
		btn : [ '确定', '取消' ],
		btn1 : function(index) {
			var node = ztree.getSelectedNodes();
			//选择上级部门
			var deptId = node[0].deptId;
			var deptName = node[0].name;
			$("#deptId").val(deptId);
			$("#deptName").val(deptName);
			layer.close(index);
		}
	});

}


