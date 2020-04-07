
var vm = new Vue({
    el:'#rrapp',
    data:{
        storage:{},

        saveFlag:false
    },
    created:function(){
    },
    methods:{
        getTemplate:function(templateId,templateType,templateName){
            $("#TEMPLATE_ID").val(templateId);
            $("#TEMP_TYPE").val(templateType);
            $("#TEMP_NAME").val(templateName);
        },
        getTemplateConfig:function(id, type, werks, field, qrCode, barCode){
            $("#ID").val(id);
            $("#KUNNR").val(type);
            $("#WERKS").val(werks);
            $("#HIDDEN_FIELD").val(field);
            $("#QRCODE").val(qrCode);
            $("#BARCODE").val(barCode);
        },
    }
});

//设置

$("#saveForm").validate({
    rules : {

    },
    submitHandler: function(form){
        var url;
        if($("#ID").val()==null || $("#ID").val()==""){
            url = "config/printTemplate/saveConfig";
        }else {
            url = "config/printTemplate/updateConfig";
        }
        vm.storage.TEMPLATE_ID=$('#TEMPLATE_ID').val();
        vm.storage.ID=$('#ID').val();
        vm.storage.KUNNR=$('#KUNNR').val().trim().replace(/，/g,",");
        vm.storage.WERKS=$('#WERKS').val().trim().replace(/，/g,",");
        vm.storage.HIDDEN_FIELD=$('#HIDDEN_FIELD').val().trim().replace(/，/g,",");
        vm.storage.QRCODE=$('#QRCODE').val();
        vm.storage.BARCODE=$('#BARCODE').val();
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





