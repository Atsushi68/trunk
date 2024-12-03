package com.taketoritei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

public class RoomCSVUpload {

	public static void main(String[] args) {

		log("アップロード処理開始");

		String mailSubject, mailBody = null;
		// バッチパラメータが不正
		// ------------------------------------------------
		if (args.length < 3) {
			mailSubject = "エラー";
			mailBody = "アップロード処理のパラメータが不正です";
			log("バッチパラメータ不正");
			return;
		}

		String url = args[0];
		String path = args[1];
		String to = args[2];

		// ファイルが存在しない場合は処理しない
		// ------------------------------------------------
		File file = new File(path);
		if (!file.exists()) {
			log("アップロード対象ファイルなし 処理終了");
			return;
		}

		// ファイル送信
		// ------------------------------------------------
		String errMessage = sendServer(url, file);
		if (StringUtils.isEmpty(errMessage)) {
			// 処理成功
			mailSubject = "正常終了";
			mailBody = "";
			// ファイル削除
			file.delete();
		} else {
			mailSubject = "エラー";
			mailBody = errMessage;
		}

		// メール送信
		// ------------------------------------------------
		sendMail(to, //
				"[チェックインデータCSV登録] " + mailSubject, //
				"実行時刻 : " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "\n" + //
				"サーバ : " + url + "\n" + //
				"CSV : " + path + "\n" + //
				"メッセージ : " + mailBody);

	}

	/**
	 * ファイルアップロード処理
	 */
	private static String sendServer(String url, File f) {

		final String EOL = "\r\n";

		log("通信処理開始 : " + url);
		try (FileInputStream file = new FileInputStream(f)) {

			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			final String boundary = UUID.randomUUID().toString();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			InputStream stream = null;

			try (OutputStream out = con.getOutputStream()) {
				out.write(("--" + boundary + EOL //
						+ "Content-Disposition: form-data; name=\"file\"; " //
						+ "filename=\"" + f.getName() + "\"" + EOL +
						"Content-Type: application/octet-stream" + EOL + EOL)
								.getBytes(StandardCharsets.UTF_8));
				byte[] buffer = new byte[2048];
				int size = -1;
				while (-1 != (size = file.read(buffer))) {
					out.write(buffer, 0, size);
				}
				out.write((EOL + "--" + boundary + "--" + EOL).getBytes(StandardCharsets.UTF_8));
				out.flush();

				log("通信処理終了");
				log("http response code - " + con.getResponseCode());
				if (HttpURLConnection.HTTP_OK == con.getResponseCode()) {

					String line;
					stream = con.getInputStream();
					StringBuffer sb = new StringBuffer();
					BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
					while ((line = br.readLine()) != null)
					    sb.append(line);

					log("http response body - " + sb.toString());
					if ("SUCCESS".equals(sb.toString())) {
						return null;
					} else if (!StringUtils.isEmpty(sb.toString())) {
						return sb.toString();
					} else {
						return "サーバからの応答がありませんでした";
					}
				} else {
					return con.getResponseMessage();
				}
			} finally {
				if (stream != null)
					stream.close();
				con.disconnect();
			}
		} catch (Exception e) {
			log("通信処理失敗");
			log(e);
			return "通信処理で予期しないエラーが発生しました";
		}
	}

	/**
	 * メール送信処理
	 */
	private static boolean sendMail(String to, String subject, String body) {

		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "weedplanning.co.jp"); // TODO
		props.setProperty("mail.smtp.port", "587"); // TODO
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.connectiontimeout", "5000");
		props.setProperty("mail.smtp.timeout", "5000");
		props.setProperty("mail.mime.address.strict", "false");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						// 認証情報
						return new PasswordAuthentication("toshikazufujiwara@weedplanning.co.jp", "eSr5N7wc"); // TODO
					}
				});

		try {
			log("メール送信");
			log("Subject : " + subject);
			log("Body : " + body);

			MimeMessage message = new MimeMessage(session);
			// From
			message.setFrom(new InternetAddress("toshikazufujiwara@weedplanning.co.jp", "藤原")); // TODO
			// To
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// Cc
			Address[] cc = { new InternetAddress("m-tamoto@bravetact.com") }; // TODO
			message.setRecipients(Message.RecipientType.CC, cc);
			// Subject
			message.setSubject(subject, "UTF-8");
			// Body
			message.setText(body, "UTF-8");
			// Encoding
			message.setHeader("Content-Transfer-Encoding", "base64");
			// 送信
			Transport.send(message);

			log("メール送信正常終了");
			return true;
		} catch (Exception e) {
			log("メール送信失敗");
			log(e);

			return false;
		}
	}

	private static void log(String str) {
		try {
			System.out.println(str);
			String path = RoomCSVUpload.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");

			File file = new File(decodedPath + "_" + new SimpleDateFormat("yyyyMM").format(new Date()) + ".log");
			FileWriter writer = new FileWriter(file, true);
			writer.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " - " + str + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void log(Exception e) {

		StringBuilder bld = new StringBuilder();
		StackTraceElement[] stacks = e.getStackTrace();
		bld.append(e.getClass().toString() + " - " + e.getMessage());
		for (StackTraceElement element : stacks) {
			bld.append("\n");
			bld.append(element);
		}

		log(bld.toString());
	}

}
