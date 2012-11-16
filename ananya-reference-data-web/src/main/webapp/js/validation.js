$(document).ready(function () {
    $('form').submit(function (event) {
        var form = $(event.target);
        var result = true;

        var requiredInputs = form.find('.required').each(function (index, element) {
            element = $(element);
            if (element.val() == "") {
                removeErrorMsg(element);
                element.parents('.control-group').addClass('error');
                element.parents('.controls').append('<br class="error-help"/><span class="help-inline error-help span2">This is required.</span>');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });

        var fileFormatCheck = form.find('#j_file').each(function(index, element){
            element = $(element);
            var fileName = element.val();
            var fileNameArray = fileName.split(".");
            if(fileNameArray[fileNameArray.length-1] != "csv" && fileName.length != 0)
            {
                removeErrorMsg(element);
                element.parents('.control-group').addClass('error');
                element.parents('.controls').append('<br class="error-help"/><span class="help-inline error-help span2">Invalid file format.</span>');
                removeErrorMsgOnChange(element);
                result = false;
            }
        });

        return result;
    })

    var removeErrorMsgOnChange = function () {
        var elements = arguments;
        $(elements).each(function (i, targetEle) {
            $(targetEle).change(function () {
                $(elements).each(function (j, ele) {
                    removeErrorMsg($(ele));
                });
            });
        });
    }

    var removeErrorMsg = function (element) {
        element = $(element);
        element.parents('.control-group').removeClass('error');
        element.parents('.controls').children('.error-help').remove();
    }
})