$(function() {
    
    // Or with jQuery
    $('.modal').modal();
    $(".modal-trigger").on("click", function() {
        var room = $(this).data("room");
        
        // クリックした部屋の備考を追加してモーダル表示
        var biko = $("#memo_" + room).text();
        
        $("#model_title").text(room + "号室　の備考");
        $("#model_text").text(biko);
        
    });
    

    function displayDaysCheck() {
        var displayDays = $("#displayDays").val();
        if (!displayDays){
            return false;
        }
        return true;
    }


    // 入力日で検索
    $("#search-days").on("click", function() {
        
        if (displayDaysCheck()) {
            // 日付取得
            var displayDays = $("#displayDays").val();
            date = new Date(displayDays);
            var year = date.getFullYear().toString().padStart(4, '0');
            var month = (date.getMonth() + 1).toString().padStart(2, '0');
            var day = date.getDate().toString().padStart(2, '0');
            dateText = year + "-" + month + "-" + day;
            location.href = "/order/admin/dining/time?days=" + dateText;
        }
        return false;
    });

    // 前の日クリック時
    $("#prev-days").on("click", function() {

        if (displayDaysCheck()) {
            var displayDays = $("#displayDays").val();
            date = new Date(displayDays);
            // 前の日
            date.setDate(date.getDate() - 1);
            var year = date.getFullYear().toString().padStart(4, '0');
            var month = (date.getMonth() + 1).toString().padStart(2, '0');
            var day = date.getDate().toString().padStart(2, '0');
            dateText = year + "-" + month + "-" + day;

            location.href = "/order/admin/dining/time?days=" + dateText;
        }
        return false;
    });
    
    // 次の日クリック時
    $("#next-days").on("click", function() {

        if (displayDaysCheck()) {
            var displayDays = $("#displayDays").val();
            date = new Date(displayDays);
            // 次の日
            date.setDate(date.getDate() + 1);
            var year = date.getFullYear().toString().padStart(4, '0');
            var month = (date.getMonth() + 1).toString().padStart(2, '0');
            var day = date.getDate().toString().padStart(2, '0');
            dateText = year + "-" + month + "-" + day;

            location.href = "/order/admin/dining/time?days=" + dateText;
        }
        return false;
    });
});