var vm = new Vue({
    el:'#rrapp',
    data:{
        matmanager:{"werks":"","whNumber":""},
    	saveFlag:false,
    	warehourse:[]
    },
    created: function(){
    	this.matmanager.werks = $("#werks").find("option").first().val();
	},
    watch:{
    	'matmanager.werks': {
		　　　　handler(newValue, oldValue) {
		　　　　　　          //查询工厂仓库
		          $.ajax({
		        	  url:baseUrl + "common/getWhDataByWerks",
		        	  data:{"WERKS": newValue},
		        	  success:function(resp){
		        		 vm.warehourse = resp.data;
		        		 vm.matmanager.whNumber = resp.data[0].WH_NUMBER;
		        	  }
		          })
		          
		          vm.getAuthorityCodeSelect(vm.matmanager.werks,vm.matmanager.whNumber);
		　　　　}
		},
    	'matmanager.whNumber': {
		　　　　handler(newValue, oldValue) {
				vm.getAuthorityCodeSelect(vm.matmanager.werks,vm.matmanager.whNumber);
		　　　　}
		}
	},
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "config/matmanager/info/"+id,
	            contentType: "application/json",
	            success: function(r){
                    vm.matmanager = r.wmsCMatManager;
                    vm.getAuthorityCodeSelect(vm.matmanager.werks,vm.matmanager.whNumber);
                    $('#werks').attr("disabled",true);
                    $('#whNumber').attr("disabled",true);
                    $('#matnr').attr("readonly",true);
	            }
			});
        },
        
		getMaterialNoFuzzy:function(){
            console.log("werks=",$("#werks").val());			
            /*
			 * $("#werks").val() : 工厂代码
			 * "#matnr" ： 物料号ID
			 * "#maktx" : 物料描述ID ;根据料号带出描述
			 */
			getMaterialNoSelect($("#werks").val(),"#matnr","#maktx",null,null);
		},
		getVendorNoFuzzy:function(){
			getVendorNoSelect("#lifnr",null,null,null);
		},
		getVerdorName:function(){
			var mapcond={lifnr:$("#lifnr").val()};
			$.ajax({
		        url: baseURL + "config/CVendor/listVendortName",
		        dataType : "json",
				type : "post",
		        data: mapcond,
		        async:false,
		        success: function(data){
		        	if(data.code == 0){
		        		vm.matmanager.liktx=data.vendorName;
		        		$("#liktx").val(data.vendorName);
		        	}
		        }
			})
		},
		getAuthorityCodeSelect:function(werks,whNumber){
			
			if(whNumber!=''){
				var str="";
				var url="config/matmanagertype/getAuthorityCodeSelect";
				$.ajax({
					type: "POST",
			        url: baseURL + url,
			        dataType: "json",
			        data: {
			        	werks:werks,
			        	whNumber:whNumber
			        },
			        async:false,
			        success: function(data){
			           str='';
			           var list=data.list;
			           for(var i=0;i<list.length;i++){
			        	   str+="<option value='"+list[i].authorizeCode+"'>"+list[i].authorizeCode+"</option>";
			           }
			           $("#authorizeCode").html(str);
			        }
				});
			}
		}
    }
});
$("#saveForm").validate({
	rules : {
		
	},
	submitHandler: function(form){
         console.log('url',"ERERERER");
		// 存放区代码字段存在小写的转化成大写
		//vm.matmanager.areaCode=vm.area.areaCode.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		vm.matmanager.werks=$('#werks').val();
		vm.matmanager.whNumber=$('#whNumber').val();
		vm.matmanager.matnr=$('#matnr').val();
		vm.matmanager.maktx=$('#maktx').val();
		vm.matmanager.lifnr=$('#lifnr').val();
		vm.matmanager.authorizeCode=$('#authorizeCode').val();
        var url = vm.matmanager.id == null ? "config/matmanager/save" : "config/matmanager/update";
        console.log('url',url);
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        dataType: "json",
	        data: vm.matmanager,
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
