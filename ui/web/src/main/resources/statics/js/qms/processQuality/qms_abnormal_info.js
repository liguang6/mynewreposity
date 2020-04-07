var vm = new Vue({
	el:'#page_div',
	data:{
		user:{},
		CONFIRMOR:'',
		SOLUTION_PERSON:'',
		SOLUTION:'',
		REASON:'',
		RESP_UNIT:'',
		BAD_SOURCE:'',
		BAD_CLASS:'',
		MEMO:'',
		FAULT_TYPE_LIST:[],
		WERKS:'',
		RESP_UNIT_LIST : [], //责任车间
		RESP_WORKGROUP_LIST :[], //责任班组
		RESP_WORKGROUP:'',
	},
	watch:{
		RESP_UNIT:{
			handler :function(newVal,oldVal){
				vm.getRespWorkgroupList()
			}
		},
		WERKS:{
			handler :function(newVal,oldVal){
				vm.getRespUnitList()
			}
		}
	},
	methods:{
		getUser: function(){
			$.getJSON("/web/sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
				vm.CONFIRMOR=vm.user.FULL_NAME
			});
		},
		getRespUnitList : function(){
			$.ajax({  
			         type : "post",  
			          url : baseUrl+"masterdata/getWerksWorkshopList",  
			          data : {
			        	  WERKS:vm.WERKS
			          },  
			          async : false,  
			          success : function(result){  
			        	  vm.RESP_UNIT_LIST=result.data; 
			          }  
			     }); 
		},
		getRespWorkgroupList : function(){
			$.ajax({  
			         type : "post",  
			          url : baseUrl+"masterdata/getWorkshopWorkgroupList",  
			          data : {
			        	  WERKS:vm.WERKS,
			        	  WORKSHOP:vm.RESP_UNIT,
			          },  
			          async : false,  
			          success : function(result){  
			        	  vm.RESP_WORKGROUP_LIST=result.data; 
			          }  
			     }); 
		},
		
	},
	created:function(){
		this.getUser();
	},

})



