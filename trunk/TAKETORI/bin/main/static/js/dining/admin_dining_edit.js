$(function() {

    // 一覧に戻る
    $("#backbtn").on("click", function() {
        var dateText = $("#displayDays").val();
        location.href = "/order/admin/dining?days=" + dateText;
    });
});