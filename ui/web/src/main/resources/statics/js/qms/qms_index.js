var vm = new Vue({
	el:'#qms_index',
	data:{
		user:{},
	},
	methods:{
		getUser: function(){
			$.getJSON("/web/sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
	},
	created: function(){
		this.getUser();
	},
	
	
})

function testRecord(){
	//fadeMessageAlert('',"请选中一条需要显示的图纸！",'gritter-error',3000);
	tab_id="#tab_品质记录表录入-生产";
	addTabs({id:"品质记录表录入-生产",title:"品质记录表录入-生产",close: "true",url:"\\BMS\\quality\\zzjTestRecordProduct",param: ""});
}