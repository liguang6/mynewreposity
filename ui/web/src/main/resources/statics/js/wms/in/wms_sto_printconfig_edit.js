var vm = new Vue({
    el: '#rrapp',
    data: {
        getdata: {},
        saveFlag :false

    },
});

function edit(){
        $.ajax({
            url:baseUrl +"in/sto/edit",
            type:'post',
            dataType:'json',
            data : {"params":JSON.stringify(vm.getdata)},
            success : function(data) {
                if (data.code == 0) {
                    js.showMessage("修改成功");
                    vm.saveFlag = true;
                } else {
                    alert("修改失败");
                }
            }
        });
    }



