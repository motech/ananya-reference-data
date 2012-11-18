$(document).ready(function(){

    $('#download_location').click(function(){
        $('#downloadHelper').attr('src', "admin/location/download");
    });

    $("#fileData").live('change', function(){
        $(".alert").remove();
    })
});


