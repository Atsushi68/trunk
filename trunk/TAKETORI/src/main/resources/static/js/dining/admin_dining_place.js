$(function() {    



    

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
            location.href = "/order/admin/dining/place?days=" + dateText;
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

            location.href = "/order/admin/dining/place?days=" + dateText;
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

            location.href = "/order/admin/dining/place?days=" + dateText;
        }
        return false;
    });
});