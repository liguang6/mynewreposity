var vm = new Vue({
    el:'#rrapp',
    data:{
    	faultLibrary:{},
    	saveFlag:false
    },
    methods:{
    	getInfo:function(id){
			$.ajax({
	            type: "GET",
	            url: baseURL + "qms/config/qmsFaultLibrary/info/"+id,
	            contentType: "application/json",
	            success: function(r){
	            	console.info("r.entity",r.entity)
	            	vm.faultLibrary = r.entity;
	            }
			});
    	},
    }
});
$("#saveForm").validate({
	
	submitHandler: function(form){
		// 仓库代码字段存在小写的转化成大写
		//vm.wh.whNumber=vm.wh.whNumber.toUpperCase();
		// VUE绑定读取不到通过模糊查找选中的值，所以重新赋值
		//vm.wh.werks=$('#werks').val();
		//vm.wh.lgortNo=$('#lgortNo').val();
		vm.faultLibrary.faultCode=$("#faultCode").val();
		console.log("vm",$("#faultCode").val()+"--"+vm.faultLibrary.faultCode);
	
		var url = vm.faultLibrary.id == null ? "qms/config/qmsFaultLibrary/save" : "qms/config/qmsFaultLibrary/update";
	    $.ajax({
	        type: "POST",
	        url: baseURL + url,
	        contentType: "application/json",
	        data: JSON.stringify(vm.faultLibrary),
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

//输入汉字  输出汉字拼音首字母
function getPinyin()
{
//	type【0:带声调拼音;1:不带声调拼音;2:拼音首字母】
	var value = $('#faultType').val();
	var type = '2';
	var polyphone = false; // 是否支持多音字
	var result = '';
	if(value)
	{
		switch(type)
		{
			case '0': result = pinyinUtil.getPinyin(value, ' ', true, polyphone); break;
			case '1': result = pinyinUtil.getPinyin(value, ' ', false, polyphone); break;
			case '2': result = pinyinUtil.getFirstLetter(value, polyphone); break;
			default: break;
		}
	}
	
	$('#faultCode').val(result);
}
document.getElementById('faultType').addEventListener('input', getPinyin);

getPinyin();