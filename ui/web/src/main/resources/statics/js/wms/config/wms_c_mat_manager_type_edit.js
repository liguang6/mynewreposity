var vm = new Vue({
    el:'#rrapp',
    data:{
        matManagerType:{
        	werks:"",
        	whNumber:"",
        },
        warehourse:[],
    	saveFlag:false
    },
    created: function(){
    	this.matManagerType.werks = $("#werks").find("option").first().val();
	},
    watch:{
    	'matManagerType.werks': {
		　　　　handler(newValue, oldValue) {
		　　　　　　          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS": newValue},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.matManagerType.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })
		          
		          if($("#matManagerType").val()!=null && $("#matManagerType").val()!=''){
		        	  if($("#matManagerType").val()=='20'){
		        		 var str=vm.loadLgortSelect($("#werks").val());
		        		 $("#authorizeCodeDiv").html(str);
		  			  }
			          if($("#matManagerType").val()=='30'){
				         var str=vm.loadWhAreaSelect($("#whNumber").val());
				         $("#authorizeCodeDiv").html(str);
				   	  }
		          }
		　　　　}
		},
    	'matManagerType.whNumber': {
		　　　　handler(newValue, oldValue) {
		          if($("#matManagerType").val()!=null && $("#matManagerType").val()!=''){
		        	  if($("#matManagerType").val()=='20'){
		        		 var str=vm.loadLgortSelect($("#werks").val());
		        		 $("#authorizeCodeDiv").html(str);
		  			  }
			          if($("#matManagerType").val()=='30'){
				         var str=vm.loadWhAreaSelect($("#whNumber").val());
				         $("#authorizeCodeDiv").html(str);
				   	  }
		          }
		　　　　}
		}
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matmanagertype/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	vm.matManagerType = r.wmsCMatManagerType;
	            	if(vm.matManagerType.matManagerType=='20'){
	            		var str=vm.loadLgortSelect(vm.matManagerType.werks);
	            		$("#authorizeCodeDiv").html(str);
	            		$("#authorizeCode option[value='"+vm.matManagerType.authorizeCode+"']").attr("selected",true);
                    }
	            	if(vm.matManagerType.matManagerType=='30'){
	            		var str=vm.loadWhAreaSelect(vm.matManagerType.whNumber);
	            		$("#authorizeCodeDiv").html(str);
	            		$("#authorizeCode option[value='"+vm.matManagerType.authorizeCode+"']").attr("selected",true);
                    }
                    $('#werks').attr("disabled",true);
                    $('#whNumber').attr("disabled",true);
	            }
			});
    	},
		matManagerTypeChange:function(e){
			var thisVal=$(e.target).val();
			var str='';
			if(thisVal=='20'){
				str=vm.loadLgortSelect($("#werks").val());
			}else if(thisVal=='30'){
				str=vm.loadWhAreaSelect($("#whNumber").val());
			}else{
				str='<input type="text" id="authorizeCode" class="form-control required"' 
					+'name="authorizeCode" placeholder="授权码" v-model="matManagerType.authorizeCode"/>';
			}
			$("#authorizeCodeDiv").html(str);
        },
		loadLgortSelect:function(werks){
			
			if(werks!=''){
				var str="";
				var url="config/matmanagertype/getLgortSelect";
				$.ajax({
					type: "POST",
			        url: baseURL + url,
			        dataType: "json",
			        data: {WERKS:werks},
			        async:false,
			        success: function(data){
			           str='<select id="authorizeCode" name="authorizeCode" class="form-control" v-model="matManagerType.authorizeCode">';
			           var list=data.list;
			           for(var i=0;i<list.length;i++){
			        	   str+="<option value='"+list[i].LGORT+"'>"+list[i].LGORT+"</option>";
			           }
			           str+='</select>';
			        }
				});
				return str;
			}
		},
        loadWhAreaSelect:function(whNumber){
			
			if(whNumber!=''){
				var str="";
				var url="config/matmanagertype/getWhAreaSelect";
				$.ajax({
					type: "POST",
			        url: baseURL + url,
			        dataType: "json",
			        data: {whNumber:whNumber},
			        async:false,
			        success: function(data){
			           str='<select id="authorizeCode" name="authorizeCode" class="form-control" v-model="matManagerType.authorizeCode">';
			           var list=data.list;
			           for(var i=0;i<list.length;i++){
			        	   str+="<option value='"+list[i].storageAreaCode+"'>"+list[i].storageAreaCode+"</option>";
			           }
			           str+='</select>';
			        }
				});
				return str;
			}
		}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		// 物料号字段存在小写的转化成大写
		//vm.matManagerType.matManagerTypeCode=vm.matManagerType.matManagerTypeCode.toUpperCase();
		var url = vm.matManagerType.id == null ? "config/matmanagertype/save" : "config/matmanagertype/update";
		vm.matManagerType.werks=$("#werks").val();
		vm.matManagerType.whNumber=$("#whNumber").val();
		vm.matManagerType.matManagerType=$("#matManagerType").val();
		vm.matManagerType.managerType=$("#managerType").val();
		vm.matManagerType.authorizeCode=$("#authorizeCode").val();
		$.ajax({
	        type: "POST",
	        url: baseURL + url,
	        dataType: "json",
	        data:vm.matManagerType,
	        async:false,
	        success: function(data){
	        	if(data.code == 0){
                    js.showMessage('操作成功');
                    vm.saveFlag= true;
                }else{
                    alert(data.msg);
                }
	        }
	    });
    }
});
