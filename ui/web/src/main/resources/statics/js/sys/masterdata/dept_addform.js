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
var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title:null,
        type:"增加",
        dept:{
        	deptId:null,
            name:null,
            parentId:null,
            parentName:null,
            code:null,
            deptKind:'0',
            deptType:null,
            treeSort:null,
            leader:null,
            remarks:null
        },
        //deptKind: '0',  
        deptKinds: [  
          { title: '管理型', value: '0' },  
          { title: '生产型', value: '1' }
        ],
    },
    watch: {
        "dept.deptType": {
            // 数据变化时执行的逻辑代码
            handler:function(newV, oldV) {
        		if(newV!=''){
        			if(newV=='WERKS'){
        				$("#WERKS-DIV").show().siblings().not("label").hide();
        				vm.dept.code = $("#WERKS").val();
        			}else if(newV=='WORKSHOP'){
        				vm.dept.code = $("#WORKSHOP").val();
        				$("#WORKSHOP-DIV").show().siblings().not("label").hide();
        			}else if(newV=='LINE'){
        				$("#LINE-DIV").show().siblings().not("label").hide();
        				vm.dept.code = $("#LINE").val();
        			}else{
        				$("#other-div").show().siblings().not("label").hide();
        			}
        		}
            },
            // 开启深度监听
            deep: true
        }
    },
    methods: {
    	reset:function(){
    		vm.dept.deptId=null,
    		vm.dept.name=null,
    		vm.dept.parentId=null,
    		vm.dept.parentName=null,
    		vm.dept.code=null,
    		vm.dept.treeSort=null,
    		vm.dept.leader=null,
    		vm.dept.remarks=null
    	},
    	getDept: function(){
            //加载部门树
            $.get(baseURL + "masterdata/dept/ztreeDepts", function(r){
                dept_ztree = $.fn.zTree.init($("#deptTree"), dept_setting, r);
                var node = dept_ztree.getNodeByParam("deptId", vm.dept.parentName);
                if(node != null){
                    dept_ztree.selectNode(node);
                    vm.dept.parentName = node.name;
                }
            })
        },
        test:function(){
        	vm.getDept();
        },
    	deptTree: function(){
    		vm.getDept();
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
                    vm.dept.parentId = node[0].deptId;
                    vm.dept.parentName = node[0].name;
                    layer.close(index);
                }
            });
        },
        saveOrUpdate:function(){
        	if(vm.dept.parentId == null){
        		alert("请选择上级部门！")
        		return false;
        	}
        	/*if(vm.dept.treeSort == null){
        		alert("请输入部门排序！")
        		return false;
        	}*/
        	vm.dept.deptKind = $("#deptKind").val();
        	vm.dept.deptType = $("#deptType").val();
        	if(vm.dept.deptType=='WERKS'){
        		vm.dept.name=$("#WERKS :selected").text();
        		vm.dept.code = $("#WERKS").val();
        		if(vm.dept.name == null){
            		alert("请选择工厂！");
            		return false;
            	}
			}else if(vm.dept.deptType=='WORKSHOP'){
				vm.dept.name=$("#WORKSHOP :selected").text();
				vm.dept.code = $("#WORKSHOP").val();
				if(vm.dept.name == null){
            		alert("请选择车间！");
            		return false;
            	}
			}else if(vm.dept.deptType=='LINE'){
				vm.dept.name=$("#LINE :selected").text();
				vm.dept.code = $("#LINE").val();
				if(vm.dept.name == null){
            		alert("请选择线别！");
            		return false;
            	}
			}
        	if(vm.dept.name == null){
        		alert("请输入部门名称！")
        		return false;
        	}
        	var url = vm.dept.deptId == null ? "masterdata/dept/save" : "masterdata/dept/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.dept),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                        	if(vm.dept.deptId == null){
                        		vm.reset();
                        	}
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
	if(getUrlKey("depId") != null){
		$.getJSON( baseURL + "masterdata/dept/info/" + getUrlKey("depId"), function (r) {
			vm.dept = r.dept;
            vm.type = "编辑";
            $("#deptType option[value='"+r.dept.deptType+"']").attr("selected",true);
            if(r.dept.deptType=='WERKS'){
				$("#WERKS-DIV").show().siblings().not("label").hide();
				$("#WERKS option[value='"+r.dept.name+"']").attr("selected",true);
			}else if(r.dept.deptType=='WORKSHOP'){
				$("#WORKSHOP-DIV").show().siblings().not("label").hide();
				$("#WORKSHOP option[value='"+r.dept.name+"']").prop("selected",true);
			}else if(r.dept.deptType=='LINE'){
				$("#LINE-DIV").show().siblings().not("label").hide();
				$("#LINE option[value='"+r.dept.name+"']").prop("selected",true);
			}else{
				$("#other-div").show().siblings().not("label").hide();
			}
		});
	}
	function getUrlKey(name){
		return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
	}
});

function selectChanged(target){
	vm.dept.code = $(target).val();
}
