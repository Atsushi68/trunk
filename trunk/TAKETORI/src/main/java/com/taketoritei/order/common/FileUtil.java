package com.taketoritei.order.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;


public class FileUtil {

	/**
	 * フォルダが無い場合作成する
	 * @throws IOException
	 */
	public static void createFolder(String filePath) throws IOException {

		// フォルダが無い場合
		Path folder = Paths.get(filePath);
		if (!Files.exists(folder)) {
			// 親フォルダを含めて作成
			Files.createDirectories(folder);
		}
	}


	/**
	 * ファイルを読み込んでバイト文字列に変換
	 * @param filepath
	 * @return
	 * @throws Throwable
	 */
	public static byte[] convertToByteArray(String filePath) throws Exception {

		int nRead;
		InputStream is = null;
		byte[] fileContent = new byte[16384];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		//ファイルをバイト形式に変換
		is = new FileInputStream(filePath);

		while ((nRead = is.read(fileContent, 0, fileContent.length)) != -1) {
			buffer.write(fileContent, 0, nRead);
		}

		buffer.flush();
		is.close();

		return buffer.toByteArray();
	}

	/**
     * ファイルに書き込んでダウンロード
     * @param response
     * @param fileContent
	 * @throws IOException
     */
    public static void outputSreamWrite(HttpServletResponse response, byte[] fileContent) throws IOException {
        OutputStream os = null;
        os = response.getOutputStream();
        os.write(fileContent);
        os.flush();
    }


	/**
	 * ファイル読込
	 * @param uploadFile
	 * @return
	 */
	public static List<String> fileContents(MultipartFile uploadFile) {
		return fileContents(uploadFile, "UTF-8");
	}

	public static List<String> fileContents(MultipartFile uploadFile, String charsetName) {

		List<String> lines = new ArrayList<String>();
		try {
			InputStream stream = uploadFile.getInputStream();
			Reader reader = new InputStreamReader(stream, charsetName);
			BufferedReader buf = new BufferedReader(reader);

			String line = buf.readLine();
			while (line != null) {
				lines.add(line);
				line = buf.readLine();
			}

		} catch (IOException e) {
			lines = null;
			e.printStackTrace();
		}
		return lines;
	}
}
