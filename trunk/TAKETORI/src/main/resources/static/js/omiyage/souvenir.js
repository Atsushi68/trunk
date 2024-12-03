$(function() {

	// カテゴリー選択時
	$('#drop').on("change", function() {

		// 選択したカテゴリーの値を取得
		var category = $(this).val();

		// ページ数を取得
		var page = 1;

		// リダイレクト
		var url = location.pathname + "?category=" + category + "&page=" + page;
		window.location.href = url;

		return false;
	});

});