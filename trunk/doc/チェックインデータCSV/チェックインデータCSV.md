# チェックインデータCSV

## 実行可能jarの作成

Eclipseプロジェクトから エクスポート > 実行可能JＡRファイル を選択
![エクスポート1](C:\Development\pleiades\workspace_tokkan\TAKETORI_DOC\チェックインデータCSV\export1.png)

起動構成に RoomCSVUpload を選択。表示されない場合はRoomCSVUploadを実行構成に加えてから再度選択
エクスポート先は任意
![エクスポート2](C:\Development\pleiades\workspace_tokkan\TAKETORI_DOC\チェックインデータCSV\export2.png)


## jarの実行

Windowsクライアント端末にRoomCSVUpload.batと、RoomCSVUpload.jarを同フォルダに配置
タスクスケジューラでRoomCSVUpload.batの実行を登録
```bat
cd %~dp0
java -jar -Dfile.encoding=UTF-8 RoomCSVUpload.jar http://localhost:10443/order/api/room/csv/upload C:/Development/work/date.csv fujiwara@tokkan.org
```
第一引数：アップロードURL /order/api/room/csv/upload
第二引数：チェックインデータCSVの絶対パス
第三引数：実行結果を送信するメールアドレス

## 処理メモ
チェックインデータCSVの絶対パスにファイルが存在しない場合はメールを送信しない
ログをjarと同じディレクトリに出力する
アップロード処理が完了したことを確認するため、/order/api/room/csv/uploadのレスポンスBodyには正常終了時には"SUCCESS"、CSVファイルエラー時にはエラーメッセージを設定するように実装している
