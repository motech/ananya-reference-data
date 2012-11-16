$(document).ready(function(){
    $('#j_download').click(function(){
        $('#downloadHelper').attr('src', "admin/location/download");
    });

    $("#fileData").live('change', function(){
        $(".alert").remove();
    })
});


