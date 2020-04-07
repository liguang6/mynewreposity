var vm = new Vue({
    el: '#rrapp',
    data: {
        getdata: {},

    },
});

function add(){
    $.ajax({
        type:'post',
        async:false,
        dataType:'json',
        url:baseUrl +"in/sto/add",
        data : {"params":JSON.stringify(vm.getdata)},
        success : function(data) {
            if (data.code == 0) {
                js.showMessage("添加成功");
                vm.saveFlag = true;
            } else {
                alert("添加失败");
            }
        }
    });
}

