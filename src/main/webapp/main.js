$(document).ready(function(event) {
    $(".add-smile").click(function (){
        $(this).next().toggleClass("display_none");
    });
    /*$(".smile-btn").click(function(){
        var smile = $(".smile-btn img");
        $("#message").append(smile);
        $(".smile-list").toggleClass("display_none");
    });*/
});

