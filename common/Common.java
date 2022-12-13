package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

public class Common {
	public static void main(String[] args) {
		File fin = new File("C:\\Users\\wang.yuting\\OneDrive - 株式会社クリーク･アンド･リバー社\\デスクトップ\\hello.txt");
		File fout = new File("C:\\Users\\wang.yuting\\OneDrive - 株式会社クリーク･アンド･リバー社\\デスクトップ\\test.txt");
		byte[] res = file2Byte(fin);
		byte2File(res, fout);
	}

	private static final int DEFAULT_BUFFER_SIZE = 10 * 1024 * 1024; // バッファサイズ

	/**
	 * バイトを指定ファイルに書き込む
	 * 
	 * @param in
	 *            バイト
	 * @param out
	 *            指定ファイル
	 * @return 
	 */
	public static boolean byte2File(byte[] in, File out) {
		try {
			FileOutputStream fout = new FileOutputStream(out);
			fout.write(in);
			fout.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " �ֽ�ת��ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * 指定ファイルをバイトに変換
	 * 
	 * @param file
	 *            指定ファイル
	 * @return 
	 */
	public static byte[] file2Byte(File file) {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int len = 0;
		try {
			FileInputStream fin = new FileInputStream(file);
			len = fin.read(buffer);
			fin.close();
		} catch (Exception e) {
			System.out.println(e.toString() + "ファイル変換失敗");
		}
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++)
			ret[i] = buffer[i];
		return ret;
	}

	/**
	 * 指定ファイルを目標ファイルにコピー
	 * 
	 * @param in
	 *            元ファイル
	 * @param out
	 *            目標ファイル
	 * @return 
	 */
	public static boolean file2File(File in, File out) {
		try {
			FileInputStream fin = new FileInputStream(in);
			FileOutputStream fout = new FileOutputStream(out);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int len = 0;
			while ((len = fin.read(buffer)) != -1) {
				fout.write(buffer, 0, len);
			}
			fin.close();
			fout.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " ファイルコピー失敗");
			return false;
		}
		return true;
	}

	/**
	 * 文字列を指定ファイルに書き込む
	 * 
	 * @param res
	 *            文字列
	 * @param filePath
	 *            ファイルパス
	 * @return 
	 */
	public static boolean string2File(String res, String filePath) {
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists())
				distFile.getParentFile().mkdirs();
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[DEFAULT_BUFFER_SIZE]; // 文字列一時的に保存する
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println(e.toString() + " ファイル存在しないあるいは開けない");
			return false;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * テキストファイルを指定コードに変換
	 * 
	 * @param file
	 *            テキストファイル
	 * @param encoding
	 *            指定コード
	 * @return 変換した文字列
	 * @throws IOException
	 */
	public static String file2String(File file, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file));
			} else {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			}
			// 入力を出力に書き込む
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return writer.toString();
	}
}
