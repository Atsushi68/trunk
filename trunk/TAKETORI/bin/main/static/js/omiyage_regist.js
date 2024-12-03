/**
 * お土産登録画面
 * @returns
 */
$(document).ready(function() {

	// tab
	$('.tabs').tabs();

	// 商品詳細
	$('#textarea1').characterCounter();

	// ハッシュタグ設定
	$('.chips-autocomplete').chips({

		// 初期値
		// Ajax
		data: [{
			tag: 'Apple',
		}, {
			tag: 'Microsoft',
		}, {
			tag: 'Google',
		}],

		// オートコンプリート
		// Ajax
		autocompleteOptions: {
			data: {
				'Apple': null,
				'Microsoft': null,
				'Google': null
			},
			limit: Infinity,
			minLength: 1
		}
	});

	// 登録ボタン押下時
	$("#omiyage_sub").on("click", function() {

		// ハッシュタグ
		// ---------------------------
		var hashTag = null;
		var inputTags = M.Chips.getInstance($('.chips-autocomplete')).chipsData;
		for(var i=0; i<inputTags.length; i++) {
			if (hashTag == null) {
				hashTag = inputTags[i].tag;
			} else {
				hashTag += "," + inputTags[i].tag;
			}
		}

		// サブミット
		$('#omiyageForm').submit();
	});

});