$(function() {

	// 予約を更新
	function reloadReserve() {
		var html = '';

		// 迷子注文一覧
		lostHtml = "";
		var roomNo = null;

		$.ajax({
			url: '/order/admin/omiyage/lost/order/' + roomNo + '/',
			type: 'GET',
			dataType: 'json'
		})
		.done(function(data) {
			if (data.length != 0) {
				lostHtml += '<div class="card-panel red lighten-4">';
				lostHtml += '<b class="red-text">※現在のユーザーと紐付いていない注文が存在します。下記注文を処理してください。</b>';
				lostHtml += '<table class="omiyage">';
				lostHtml += '<thead><tr>';
				lostHtml += '<th class="room">客室</th><th class="omiyage_name">商品名</th><th class="price">金額</th><th class="num">個数</th></th>';
				lostHtml += '</tr></thead>';
				lostHtml += '<tbody>';
				$.each(data,function(i, item) {
					lostHtml += '<tr>';
					lostHtml += '<td><a href="/order/admin/omiyage/' + item['room_no'] + '">' + item['room_no'] + '号室</a></td>';
					lostHtml += '<td>' + item['name'] + '</td>';
					lostHtml += '<td class="price">' + item['price'].toLocaleString() + '円</td>';
					lostHtml += '<td class="num">' + item['num'] + '個</td>';
					lostHtml += '</tr>';
				});
				lostHtml += '</tbody>';
				lostHtml += '</table>';
			}
		})
		.fail(function() {
			lostHtml += '<p>ERROR - 取得できませんでした</p>';
		})
		.always(function() {
			$('#lostOrderTable').html(lostHtml);
		});
	}

    // 初回実行
	var date = new Date();
	var y = date.getFullYear();
	var m = ("00" + (date.getMonth()+1)).slice(-2);
	var d = ("00" + date.getDate()).slice(-2);
	var h = ("00" + date.getHours()).slice(-2);
	var mi = ("00" + date.getMinutes()).slice(-2);
	var html = '<p class="update_date">' + y + "/" + m + "/" + d + " " + h + ':' +  mi + '  更新<p>';
	$('#update_date').html(html);

	$(window).on('load',function(){
		// 定期実行
	    setInterval(function(){ location.reload(); }, 300000);

	    reloadReserve();
	});
});