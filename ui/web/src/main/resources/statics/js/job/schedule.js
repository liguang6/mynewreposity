$(function () {
	$('#dataGrid').dataGrid({
		searchForm: $("#searchForm"),
		columnModel: [
			{ label: '任务ID', name: 'jobId', width: 60, key: true },
			{ label: 'bean名称', name: 'beanName', width: 100 },
			{ label: '方法名称', name: 'methodName', width: 100 },
			{ label: '参数', name: 'params', width: 100 },
			{ label: 'cron表达式 ', name: 'cronExpression', width: 100 },
			{ label: '备注 ', name: 'remark', width: 100 },
			{ label: '状态', name: 'status', width: 60, formatter: function(value, options, row){
				return value === 0 ? 
					'<span class="label label-success">正常</span>' : 
					'<span class="label label-danger">暂停</span>';
			}}
        ],
        rowNum: 15,
        multiselect: true,
    	// 加载成功后执行事件
    	ajaxSuccess: function(data){
    		
    	}
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
		},
		showList: true,
		title: null,
		schedule: {}
	},
	methods: {
		query: function () {
			$("#searchForm").submit();
		},
		add: function(){
			vm.title = "新增";
			vm.schedule = {};
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 新增定时任务</i>', baseURL + 'job/schedule_form.html', true, true);
		},
		update: function () {
			var jobId = getSelectedRow();
			if(jobId == null){
				return ;
			}
            vm.title = "修改";
			js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px"> 修改定时任务</i>', baseURL + 'job/schedule_form.html?jobId='+jobId, true, true);
		},
		del: function (event) {
			var jobIds = getSelectedRows();
			if(jobIds == null){
				return ;
			}
			
			js.confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/schedule/delete",
                    contentType: "application/json",
				    data: JSON.stringify(jobIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		pause: function (event) {
			var jobIds = getSelectedRows();
			if(jobIds == null){
				return ;
			}
			
			js.confirm('确定要暂停选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/schedule/pause",
                    contentType: "application/json",
				    data: JSON.stringify(jobIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		resume: function (event) {
			var jobIds = getSelectedRows();
			if(jobIds == null){
				return ;
			}
			
			js.confirm('确定要恢复选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/schedule/resume",
                    contentType: "application/json",
				    data: JSON.stringify(jobIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		runOnce: function (event) {
			var jobIds = getSelectedRows();
			if(jobIds == null){
				return ;
			}
			
			js.confirm('确定要立即执行选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/schedule/run",
                    contentType: "application/json",
				    data: JSON.stringify(jobIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		reload: function (event) {
			vm.showList = true;
			vm.query();
		}
	}
});