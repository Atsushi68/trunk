$(function() {

	$('.datepicker').datepicker({
		i18n: {
		months:  ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
		monthsShort: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
        weekdays:  ["日", "月", "火", "水", "木", "金", "土"],
        weekdaysShort: ["日", "月", "火", "水", "木", "金", "土"],
        weekdaysAbbrev: ["日", "月", "火", "水", "木", "金", "土"],
        clear: "クリア",
        cancel: "閉じる",
        done: "決定"
		},
        format: "yyyy-mm-dd",
    });

	$('.timepicker').timepicker({
		i18n: {
	        cancel: "閉じる",
	        done: "決定"
		},
		twelveHour: false
	});

	// 納品書ダウンロードボタンクリック時
	$(".btn_invoice").on("click", function() {
		$("#pdfType").val("invoice");
	});

	// チェックリストダウンロードボタンクリック時
	$(".btn_check_list").on("click", function() {
		$("#pdfType").val("check_list");
	});


	// 表示・非表示
	$("#select_range_check").on("click", function() {
		selectRangeShowHide(true);
	});
	function selectRangeShowHide(anim) {
		var checked = $("#select_range_check").prop("checked");
		if (checked) {
			if (anim) {
				$("#select_range").show(500);
			} else {
				$("#select_range").show();
			}

		} else {
			if (anim) {
				$("#select_range").hide(500);
			} else {
				$("#select_range").hide();
			}
		}
	}
	// 初期表示
	selectRangeShowHide(false);

	// 予約を更新
	function reloadReserve() {

		// お土産取得
		var omiyageHtml = '';
		var roomNo = null;
		$.ajax({
			url: '/order/admin/omiyage/order/' + roomNo + "/",
			type: 'GET',
			dataType: 'json'
		})
		.done(function(data) {
			omiyageHtml += '<table class="omiyage">';
			omiyageHtml += '<thead><tr>';
			omiyageHtml += '<th class="room">客室</th><th class="omiyage_name">商品名</th><th class="price">金額</th><th class="num">個数</th>';
			omiyageHtml += '</tr></thead>';
			omiyageHtml += '<tbody>';
			$.each(data,function(i, roomList) {
				$.each(roomList,function(i, item) {
					omiyageHtml += '<tr>';
					omiyageHtml += '<td>' + item['room_no'] + '号室</td>';
					omiyageHtml += '<td>' + item['name'] + '</td>';
					omiyageHtml += '<td class="price">' + item['price'].toLocaleString() + '円</td>';
					omiyageHtml += '<td class="num">' + item['num'] + '個</td>';
					omiyageHtml += '</tr>';
				});
			});
			omiyageHtml += '</tbody>';
			omiyageHtml += '<table>';
		})
		.fail(function() {
			omiyageHtml += '<p>ERROR - 取得できませんでした</p>';
		})
		.always(function() {
	    	var date = new Date();
	    	var y = date.getFullYear();
	    	var m = ("00" + (date.getMonth()+1)).slice(-2);
	    	var d = ("00" + date.getDate()).slice(-2);
	    	var h = ("00" + date.getHours()).slice(-2);
	    	var mi = ("00" + date.getMinutes()).slice(-2);
	    	omiyageHtml += '<p class="update_date">' + y + "/" + m + "/" + d + " " + h + ':' +  mi + '  更新<p>';
			$('#omiyageTable').html(omiyageHtml);
		});
	}

	$(window).on('load',function(){
		// 定期実行
	    setInterval(function(){
	    	reloadReserve();
	    }, 300000);

	    // 初回実行
	    reloadReserve();
	});
});