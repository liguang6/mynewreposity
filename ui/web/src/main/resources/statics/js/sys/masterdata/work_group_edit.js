var workgroupcode_edit='';
var workshop_edit='';
var vm = new Vue({
    el:'#rrapp',
    data:{
    	workshoplist:[],
    	workgrouplist:[],
    	workshopname:'',
    	workshopcode:'',
    	workgroupcode:'',
    	werks:'',
    	dept_id:'',
    	tree_names:'',
    	saveFlag:false,
    	tree_level:'',
    	id:''
    },
    created: function(){
    	this.werks = $("#WERKS").find("option").first().val();
	},
    watch:{
    	werks:{
			handler:function(newVal,oldVal){
				 $.ajax({
		        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
		        	  data:{
		        		  "WERKS":newVal,
		        		  "MENU_KEY":"MASTERDATA_WORKGROUP",
		        		  "ALL_OPTION":'X'
		        	  },
		        	  success:function(resp){
		        		 vm.workshoplist = resp.data;
		        		 if(resp.data.length>0){
		        			 vm.workshopcode=resp.data[0].CODE;
		        		 }
		        		 if(workshop_edit!=''){
		        			 vm.workshopcode=workshop_edit;
		        		 }
		        		 
		        	  }
		          })
			}
		},
		workshopcode:{//车间改变，获取班组
			handler:function(newVal,oldVal){
				$.ajax({
			      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
			    	  data:{
			    		  "WORKSHOP":vm.workshopcode,
			    		  "TYPE":'0'
			    	  },
			    	  success:function(resp){
			    		 vm.workgrouplist = resp.result;
			    		 if(workgroupcode_edit!=''){
			    			 vm.workgroupcode=workgroupcode_edit;
			    		 }
			    	  }
			      });
				
				for(var i=0;i<vm.workshoplist.length;i++){//获取车间的dept_id
       			 if(vm.workshoplist[i].CODE==vm.workshopcode){
  	        		  vm.dept_id=vm.workshoplist[i].DEPT_ID;
  	      			  vm.tree_names=vm.workshoplist[i].TREE_NAMES;
  	      			  vm.tree_level=vm.workshoplist[i].TREE_LEVEL;
  	        	  }
       		 	}
			}
		},
		workgroupcode:{

			/*handler:function(newVal,oldVal){//增加班组的前提是在 sys_dept中存在对应的车间
	    		$.ajax({
			      	  url:baseUrl + "masterdata/workgroup/getWorkShopByCode",
			    	  data:{
			    		  "WORKSHOP":$("#WORKSHOP").val(),
			    		  "WERKS":$("#WERKS").val()
			    	  },
			    	  success:function(resp){
			    		  if(resp.result.length>0){
			    			  //vm.workshopname = resp.result[0].WORKSHOP;
			    			  //vm.werks=resp.result[0].WERKS;
			    			  vm.dept_id=resp.result[0].DEPT_ID;
			    			  vm.tree_names=resp.result[0].TREE_NAMES;
			    		  }else{
			    			  js.alert("组织结构中还没有添加该车间");
			    			  return;
			    		  }
			    		 
			    	  }
			      })
			  }*/
		
		}
    },
    methods:{
    	getWorkGroupInfo:function(){
    		/*$.ajax({
		      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
		    	  data:{
		    		  "WORKSHOP":$("#WORKSHOP").val(),
		    		  "TYPE":'0'
		    	  },
		    	  success:function(resp){
		    		 vm.workgrouplist = resp.result;
		    	  }
		      });*/
    		//增加班组的前提是在 sys_dept中存在对应的车间
    		/*$.ajax({
		      	  url:baseUrl + "masterdata/workgroup/getWorkShopByCode",
		    	  data:{
		    		  "WORKSHOP":$("#WORKSHOP").val(),
		    		  "WERKS":$("#WERKS").val()
		    	  },
		    	  success:function(resp){
		    		  if(resp.result.length>0){
		    			  //vm.workshopname = resp.result[0].WORKSHOP;
		    			  //vm.werks=resp.result[0].WERKS;
		    			  vm.dept_id=resp.result[0].DEPT_ID;
		    			  vm.tree_names=resp.result[0].TREE_NAMES;
		    		  }else{
		    			  js.alert("组织结构中还没有添加该车间");
		    			  return;
		    		  }
		    		 
		    	  }
		      })*/
      },
      getInfo:function(id){
    	  
			//页面赋值
			
				$.ajax({
		            type: "GET",
		            url: baseURL + "masterdata/workgroup/getWorkGroupById",
		            data:{"ID":id},
		            success: function(r){
			            vm.id = r.result.DEPT_ID;
		            	vm.workshopname = r.result.WORKGROUPNAME;
		            	vm.workshopcode = r.result.WORKGROUPCODE;
		            	vm.workgroupcode = r.result.CODE;
		            	vm.werks = r.result.WERKS;
		            	vm.tree_names = r.result.TREE_NAMES;
		            	vm.dept_id = r.result.PARENT_ID;
		            	vm.tree_level = r.result.TREE_LEVEL;
		            	
		            	workgroupcode_edit=r.result.CODE;
		            	workshop_edit=r.result.WORKGROUPCODE;
		            	
		            	$.ajax({
			  		      	  url:baseUrl + "masterdata/workgroup/getStandardWorkGroupList",
			  		    	  data:{"WORKSHOP":r.result.WORKGROUPCODE},
			  		    	  success:function(resp){
			  		    		 vm.workgrouplist = resp.result;
			  		    		 
			  		    	  }
			  		      	});
		            	$.ajax({
				        	  url:baseUrl + "masterdata/getUserWorkshopByWerks",
				        	  data:{
				        		  "WERKS":r.result.WERKS,
				        		  "MENU_KEY":"MASTERDATA_WORKGROUP",
				        		  "ALL_OPTION":'X'
				        	  },
				        	  success:function(resp){
				        		 vm.workshoplist = resp.data;
				        		 
				        	  }
				          })
	            	}
		            
				});
				
				
			
			
  	}
    }
});
$("#saveForm").validate({
	submitHandler: function(form){
		js.loading("正在保存,请稍候...");
		var url = vm.id =="" ? "masterdata/workgroup/saveDept" : "masterdata/workgroup/updateDept";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        //contentType: "application/json",
	        data: {
	        	"DEPT_ID":vm.id,
	        	"PARENT_ID":$("#PARENT_ID").val(),
	        	"WORK_GROUP_NO":$("#WORKGROUP_NO").val(),
	        	"WORK_GROUP_NAME":$("#WORKGROUP_NO").find("option:selected").text(),
	        	"TREE_NAMES":$("#TREE_NAMES").val(),
	        	"DEPT_TYPE":$("#DEPT_TYPE").val(),
	        	"TREE_LEVEL":vm.tree_level,
	        	
	        	"WORKSHOP":$("#WORKSHOP").val(),
	    		"WERKS":$("#WERKS").val()
	        },
	        async:false,
	        success: function(data){
	        	js.closeLoading();
	        	if(data.code == 0){
                    js.showMessage('操作成功');
                    vm.saveFlag= true;
                }else{
                    alert(data.msg);
                }
	        }
	    }); 
//	  }
//  }); 
 }
});