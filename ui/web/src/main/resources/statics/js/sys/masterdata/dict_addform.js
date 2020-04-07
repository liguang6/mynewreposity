
var vm = new Vue({
    el:'#rrapp',
    data:{
        type:"增加",
        title:null,
        dict:{
        	id:null,
            name:null,
            type:null,
            code:null,
            value:null,
            orderNum:null,
            remark:null
        }
    },
    methods: {
    	reset:function(){
    		vm.dict.id=null,
    		vm.dict.name=null,
    		vm.dict.type=null,
    		vm.dict.code=null,
    		vm.dict.value=null,
    		vm.dict.orderNum=null
    		vm.dict.remark=null
    	},
    	
        saveOrUpdate:function(){

        	if(vm.dict.name == null){
        		alert("请输入字典名称！")
        		return false;
        	}
        	if(vm.dict.type == null){
        		alert("请输入字典类型！")
        		return false;
        	}
        	if(vm.dict.code == null){
        		alert("请输入字典码！")
        		return false;
        	}
        	if(vm.dict.value == null){
        		alert("请输入字典值！")
        		return false;
        	}
        	
        	var url = vm.dict.id == null ? "masterdata/dict/save" : "masterdata/dict/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.dict),
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
	if(getUrlKey("id") != null){
		$.ajax({
            type: "GET",
            url: baseURL + "masterdata/dict/info/" + getUrlKey("id"),
            contentType: "application/json",
            success: function(r){
            	vm.dict.id = r.dict.id;
            	vm.dict.name = r.dict.name;
                vm.dict.type = r.dict.type;
                vm.dict.code = r.dict.code;
                vm.dict.value = r.dict.value;
                vm.dict.orderNum = r.dict.orderNum;
                vm.dict.remark = r.dict.remark;
                vm.type = "编辑";
            }
		});
	}

	function getUrlKey(name){
		return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
	}
});
