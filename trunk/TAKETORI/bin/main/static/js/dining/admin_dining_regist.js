$(function() {

    // 登録時
    $("#updatebtn").on("click", function(e){
        e.preventDefault();

        var roomNo = $("#roomNo").val();
        if (roomNo == "") {
            return false;
        }
        $("#registForm").submit();
    });


    // 削除時
    $("#deletebtn").on("click", function(e){
        e.preventDefault();
        
        $("#deleteForm").submit();
    });

});