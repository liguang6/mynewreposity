
var num;
var vm = new Vue({
    el:'#rrapp',
    data:{
        storage:{},
        tempTypeList:[],
        tempSizeList:[],
        saveFlag:false
    },
    created:function(){
        this.tempTypeList = getPrintTemplateBySysDict("PRINT_TEMPLATE_TPYE");
        this.tempSizeList = getPrintTemplateBySysDict("PRINT_TEMPLATE_SIZE");
    },
    methods:{
        getTemplate:function(templateId,templateType,templateName, templateSize){
            $("#ID").val(templateId);
            $("#TEMP_TYPE").val(templateType);
            $("#TEMP_NAME").val(templateName);
            $("#TEMP_SIZE").val(templateSize);
        }
    }
});


$("#saveForm").validate({
    rules : {

    },
    submitHandler: function(form){
        var url;
        if($("#ID").val()==null || $("#ID").val()==""){
            url = "config/printTemplate/saveTemplate";
        }else {
            url = "config/printTemplate/updateTemplate";
        }
        vm.storage.ID=$('#ID').val();
        vm.storage.TEMP_TYPE=$('#TEMP_TYPE').val();
        vm.storage.TEMP_NAME=$('#TEMP_NAME').val();
        vm.storage.TEMP_SIZE=$('#TEMP_SIZE').val();
        $.ajax({
            type: "POST",
            url: baseURL + url,
            contentType: "application/json",
            data: JSON.stringify(vm.storage),
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

function getPrintTemplateBySysDict(type){
    var list=[];
    $.ajax({
        url:baseURL+"config/printTemplate/getPrintTemplateBySysDict/"+type,
        type : "post",
        async: false,
        success:function(response){
            list=response.data;
        }
    })
    return list;
}

