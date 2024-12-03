$(function() {

	// 削除ボタン押下時（確認）
	$('.delate_button').on("click", function() {

		// クリックした商品名をセット
		var omiyageId = $(this).data("omiyage");
		var price = $(this).data("price");
		var omiyageName = $(this).data("name");
		var carttoken = $(this).data("carttoken");

		// hiddenにセット
		$("#delete_omiyage_id").val(omiyageId);
		$("#delete_price").val(price);
		$("#delete_omiyage_name").val(omiyageName);

		// ダイアログ表示
		$('#modal_wrap, #delete_modal').fadeTo(200,1);

		return false;
	});

	// 削除ボタンクリック(削除）
	$("#delete_modal_omiyage").on("click", function() {
		// submit
		$("#delete_form").submit();
		return false;
	});

	// 範囲外クリック
	$('#modal_wrap').on("click", function() {
		// ダイアログ非表示
		$('#modal_wrap, #delete_modal').fadeTo(200,0).hide();
		$('#modal_wrap, #order_modal').fadeTo(200,0).hide();
	});

	// 購入ボタンクリック(確認）
	$('.order_btn').on("click", function() {
		// ダイアログ表示
		$('#modal_wrap, #order_modal').fadeTo(200,1);
		return false;
	});

	// 購入ボタンクリック（購入）
	$('#order_modal_omiyage').on("click", function() {
		// submit
		$("#order_form").submit();
		return false;
	});

});