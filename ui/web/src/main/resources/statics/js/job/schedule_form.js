var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
		},
		title: '增加',
		schedule: {}
	},
    created: function(){
    	this.getSchedule();
    },
	methods: {
        getSchedule: function () {
        	if(this.getUrlKey("jobId") != null){
        		this.title = '修改';
	            $.getJSON(baseURL + "sys/schedule/info/"+ this.getUrlKey("jobId"), function (r) {
	            	console.info(r.schedule);
	                vm.schedule = r.schedule;
	            });
        	}
        },
		saveOrUpdate: function (event) {
			var url = vm.schedule.jobId == null ? "sys/schedule/save" : "sys/schedule/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.schedule),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							//vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
    	reset:function(){
    		vm.schedule = {}
    	},
    	getUrlKey: function(name){
    		return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
    	}
	}
});